/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;

final class JvmClassWriter
{
    private final static int CLASS_VERSION = 0x00320000;
    private ArrayList<JvmConstant> constants = new ArrayList<JvmConstant>();
    private HashMap<JvmConstant, Integer> mapConstants = new HashMap<JvmConstant, Integer>();
    private ArrayList<JvmMethodWriter> methods = new ArrayList<JvmMethodWriter>();
    private String className;

    public JvmClassWriter(final String className)
    {
        this.constants.add(null);
        this.className = className;
        this.addConstant(new JvmConstant(JvmConstant.CONSTANT_Class, this
                .addConstant(new JvmConstant(className.replace('.', '/')))));
        this.addConstant(new JvmConstant("Code"));
        this.addConstant(new JvmConstant(JvmConstant.CONSTANT_Class, this
                .addConstant(new JvmConstant("java/lang/Object"))));

        final JvmMethodWriter mw = this.createMethod("<init>", "()V");
        mw.maxLocals = mw.maxStack = 1;
        mw.access = Modifier.PRIVATE;
        mw.code.add(JvmOp.ALOAD_0);
        mw.code.add(JvmOp.INVOKESPECIAL);
        mw.code.addShort(this.addMethodRefConstant("java.lang.Object",
                "<init>", "()V"));
        mw.code.add(JvmOp.RETURN);
    }

    public JvmMethodWriter createMethod(final String methodName,
            final String signature)
    {
        final JvmMethodWriter mw = new JvmMethodWriter(this, methodName,
                signature);
        mw.nameIndex = this.addConstant(new JvmConstant(methodName));
        mw.descriptorIndex = this.addConstant(new JvmConstant(signature));
        this.methods.add(mw);
        return mw;
    }

    public int addConstant(final JvmConstant c)
    {
        final Integer t = this.mapConstants.get(c);
        if (t != null)
            return t;
        final int idx = this.constants.size();
        this.mapConstants.put(c, idx);
        this.constants.add(c);
        if (c.type == JvmConstant.CONSTANT_Double
                || c.type == JvmConstant.CONSTANT_Long)
            this.constants.add(null);
        return idx;
    }

    public int addMethodRefConstant(final String className, final String name,
            final String type)
    {
        final int c = this.addConstant(new JvmConstant(
                JvmConstant.CONSTANT_Class, this.addConstant(new JvmConstant(
                        className.replace('.', '/')))));
        return this.addConstant(new JvmConstant(JvmConstant.CONSTANT_Methodref,
                c, this.addConstant(new JvmConstant(
                        JvmConstant.CONSTANT_NameAndType, this
                                .addConstant(new JvmConstant(name)), this
                                .addConstant(new JvmConstant(type))))));
    }

    public byte[] build()
    {
        final ByteList bytes = new ByteList();

        try
        {
            bytes.addInteger(0xcafebabe);
            bytes.addShort(CLASS_VERSION);
            bytes.addShort(CLASS_VERSION >> 16);

            bytes.addShort(this.constants.size());

            for (int i = 1; i < this.constants.size(); i++)
            {
                final JvmConstant c = this.constants.get(i);
                bytes.add(c.type);
                switch (c.type)
                {
                case JvmConstant.CONSTANT_Utf8:
                {
                    final byte[] str = c.stringValue.getBytes("UTF-8");
                    bytes.addShort(str.length);
                    for (int n = 0; n < str.length; n++)
                        bytes.add(str[n]);
                    break;
                }
                case JvmConstant.CONSTANT_Long:
                    bytes.addInteger((int) (c.longValue >> 32));
                    bytes.addInteger((int) c.longValue);
                    i++;
                    break;
                case JvmConstant.CONSTANT_Double:
                {
                    final long d = Double.doubleToLongBits(c.doubleValue);
                    bytes.addInteger((int) (d >> 32));
                    bytes.addInteger((int) d);
                    i++;
                    break;
                }
                case JvmConstant.CONSTANT_Integer:
                    bytes.addInteger(c.intValue);
                    break;
                case JvmConstant.CONSTANT_Float:
                    bytes.addInteger(Float.floatToIntBits(c.floatValue));
                    break;
                case JvmConstant.CONSTANT_Class:
                case JvmConstant.CONSTANT_String:
                    bytes.addShort(c.index0);
                    break;
                case JvmConstant.CONSTANT_Fieldref:
                case JvmConstant.CONSTANT_InterfaceMethodref:
                case JvmConstant.CONSTANT_Methodref:
                case JvmConstant.CONSTANT_NameAndType:
                    bytes.addShort(c.index0);
                    bytes.addShort(c.index1);
                    break;
                }
            }

            bytes.addShort(Modifier.PUBLIC | Modifier.FINAL);
            bytes.addShort(2); // this
            bytes.addShort(5); // super class
            bytes.addShort(0); // interfaces
            bytes.addShort(0); // fields

            bytes.addShort(this.methods.size());
            for (int i = 0; i < this.methods.size(); i++)
            {
                final JvmMethodWriter mw = this.methods.get(i);
                bytes.addShort(mw.access);
                bytes.addShort(mw.nameIndex);
                bytes.addShort(mw.descriptorIndex);
                bytes.addShort(1); // attributes
                bytes.addShort(3); // "Code"
                final byte[] code = mw.code.toArray();
                bytes.addInteger(12 + code.length); // size
                bytes.addShort(mw.maxStack);
                bytes.addShort(mw.maxLocals);
                bytes.addInteger(code.length);
                for (int n = 0; n < code.length; n++)
                    bytes.add(code[n]);
                bytes.addShort(0); // exception table
                bytes.addShort(0); // attributes
            }

            bytes.addShort(0); // attributes
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
            return null;
        }

        return bytes.toArray();
    }

    public static String buildDescriptor(Class<?>... args)
    {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i++)
        {
            final Class<?> c = args[i];
            if (c == void.class)
                sb.append('V');
            else if (c == byte.class)
                sb.append('B');
            else if (c == char.class)
                sb.append('C');
            else if (c == short.class)
                sb.append('S');
            else if (c == int.class)
                sb.append('I');
            else if (c == long.class)
                sb.append('J');
            else if (c == float.class)
                sb.append('F');
            else if (c == double.class)
                sb.append('D');
            else if (c == boolean.class)
                sb.append('Z');
            else
            {
                sb.append('L');
                sb.append(c.getCanonicalName().replace('.', '/'));
                sb.append(';');
            }
        }
        return sb.toString();
    }
}
