/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

import java.lang.reflect.Modifier;
import java.util.ArrayList;

/**
 * Method writer specialized for Weel.
 * 
 * @author René Jeschke <rene_jeschke@yahoo.de>
 */
final class JvmMethodWriter
{
    /** The class writer. */
    final JvmClassWriter classWriter;
    /** This methods's name. */
    String methodName;
    /** This method's descriptor. */
    final String descriptor;
    /** This method's code. */
    ByteList code = new ByteList();
    /** Maximum stack used. */
    int maxStack = 0;
    /** Current stack used. */
    int curStack = 0;
    /** Maximum locals used. */
    int maxLocals = 0;
    /** The name constant pool index. */
    int nameIndex;
    /** The descriptor constant pool index. */
    int descriptorIndex;
    /** The access mode. */
    int access = Modifier.PUBLIC | Modifier.FINAL | Modifier.STATIC;
    /** Encountered jumps. */
    ArrayList<Integer> jumps = new ArrayList<Integer>();
    /** Registered labels. */
    ArrayList<Integer> labels = new ArrayList<Integer>();
    
    /**
     * Constructor.
     * 
     * @param classWriter
     *            The class writer.
     * @param methodName
     *            The method name.
     * @param descriptor
     *            The descriptor.
     */
    JvmMethodWriter(final JvmClassWriter classWriter, final String methodName,
            final String descriptor, final int access)
    {
        this.classWriter = classWriter;
        this.methodName = methodName;
        this.descriptor = descriptor;
        this.access = access;

        int i, ps = (access & Modifier.STATIC) != 0 ? 0 : 1;

        for(i = 1; i < descriptor.length(); i++)
        {
            if(descriptor.charAt(i) == ')')
                break;
            switch(descriptor.charAt(i))
            {
            case 'L':
                while(descriptor.charAt(i) != ';')
                {
                    i++;
                }
                //$FALL-THROUGH$
            default:
                ps++;
                break;
            }
        }

        this.maxLocals = ps;
    }

    /**
     * Loads a constant.
     * 
     * @param index
     *            The constant pool index.
     */
    private void ldcIndex(final int index)
    {
        if(index > 255)
        {
            this.code.add(JvmOp.LDC_W);
            this.code.addShort(index);
        }
        else
        {
            this.code.add(JvmOp.LDC);
            this.code.add(index);
        }
    }

    /**
     * Writes a invoke instruction.
     * 
     * @param op
     *            Opcode.
     * @param clazz
     *            Class name.
     * @param method
     *            Method name.
     * @param descriptor
     *            Descriptor.
     */
    private void invoke(final int op, final String clazz, final String method,
            final String descriptor)
    {
        final int c0 = this.classWriter
                .addConstant(new JvmConstant(JvmConstant.CONSTANT_Class,
                        this.classWriter.addConstant(new JvmConstant(clazz
                                .replace('.', '/')))));
        final int c1 = this.classWriter.addConstant(new JvmConstant(
                JvmConstant.CONSTANT_Methodref, c0, this.classWriter
                        .addConstant(new JvmConstant(
                                JvmConstant.CONSTANT_NameAndType,
                                this.classWriter.addConstant(new JvmConstant(
                                        method)),
                                this.classWriter.addConstant(new JvmConstant(
                                        descriptor))))));
        this.code.add(op);
        this.code.addShort(c1);

        int spp = op != JvmOp.INVOKESTATIC ? 1 : 0, spm = 0, i;

        for(i = 1; i < descriptor.length(); i++)
        {
            if(descriptor.charAt(i) == ')')
                break;
            switch(descriptor.charAt(i))
            {
            case 'J':
            case 'D':
                spp += 2;
                break;
            case 'L':
                while(descriptor.charAt(i) != ';')
                {
                    i++;
                }
                //$FALL-THROUGH$
            default:
                spp++;
                break;
            }
        }

        switch(descriptor.charAt(i + 1))
        {
        case 'V':
            break;
        case 'J':
        case 'D':
            spm += 2;
            break;
        default:
            spm++;
            break;
        }

        this.curStack -= spp;
        this.add(spm);
    }

    /**
     * Writes a invokevirtual instruction.
     * 
     * @param clazz
     *            Class name.
     * @param method
     *            Method name.
     * @param descriptor
     *            Descriptor.
     */
    public void invokeVirtual(final String clazz, final String method,
            final String descriptor)
    {
        this.invoke(JvmOp.INVOKEVIRTUAL, clazz, method, descriptor);
    }

    /**
     * Writes a invokestatic instruction.
     * 
     * @param clazz
     *            Class name.
     * @param method
     *            Method name.
     * @param descriptor
     *            Descriptor.
     */
    public void invokeStatic(final String clazz, final String method,
            final String descriptor)
    {
        this.invoke(JvmOp.INVOKESTATIC, clazz, method, descriptor);
    }

    /**
     * Writes a invokespecial instruction.
     * 
     * @param clazz
     *            Class name.
     * @param method
     *            Method name.
     * @param descriptor
     *            Descriptor.
     */
    public void invokeSpecial(final String clazz, final String method,
            final String descriptor)
    {
        this.invoke(JvmOp.INVOKESPECIAL, clazz, method, descriptor);
    }

    /**
     * Writes a invokeinterface instruction.
     * 
     * @param clazz
     *            Class name.
     * @param method
     *            Method name.
     * @param descriptor
     *            Descriptor.
     */
    public void invokeInterface(final String clazz, final String method,
            final String descriptor)
    {
        this.invoke(JvmOp.INVOKEINTERFACE, clazz, method, descriptor);
    }

    /**
     * Loads a String constant.
     * 
     * @param value
     *            The value.
     */
    public void ldc(final String value)
    {
        this.ldcIndex(this.classWriter.addConstant(new JvmConstant(
                JvmConstant.CONSTANT_String, this.classWriter
                        .addConstant(new JvmConstant(value)))));
        this.add(1);
    }

    /**
     * Loads a float constant.
     * 
     * @param value
     *            The value.
     */
    public void ldc(final float value)
    {
        this.ldcIndex(this.classWriter.addConstant(new JvmConstant(value)));
        this.add(1);
    }

    /**
     * Loads a double constant.
     * 
     * @param value
     *            The value.
     */
    public void ldc(final double value)
    {
        this.code.add(JvmOp.LDC2_W);
        this.code.addShort(this.classWriter.addConstant(new JvmConstant(value)));
        this.add(2);
    }

    /**
     * Loads a boolean constant.
     * 
     * @param value
     *            The value.
     */
    public void ldc(final boolean value)
    {
        if(value)
        {
            this.code.add(JvmOp.ICONST_1);
        }
        else
        {
            this.code.add(JvmOp.ICONST_0);
        }
        this.add(1);
    }

    /**
     * Loads an integer constant.
     * 
     * @param value
     *            The value.
     */
    public void ldc(final int value)
    {
        switch(value)
        {
        case -1:
            this.code.add(JvmOp.ICONST_M1);
            break;
        case 0:
            this.code.add(JvmOp.ICONST_0);
            break;
        case 1:
            this.code.add(JvmOp.ICONST_1);
            break;
        case 2:
            this.code.add(JvmOp.ICONST_2);
            break;
        case 3:
            this.code.add(JvmOp.ICONST_3);
            break;
        case 4:
            this.code.add(JvmOp.ICONST_4);
            break;
        case 5:
            this.code.add(JvmOp.ICONST_5);
            break;
        default:
            this.ldcIndex(this.classWriter.addConstant(new JvmConstant(value)));
            break;
        }

        this.add(1);
    }

    /**
     * Adds an opcode without any checking to this code.
     * 
     * @param op
     *            The opcode.
     */
    public void addOp(final int op)
    {
        this.code.add(op);
    }

    /**
     * Writes an aload.
     * 
     * @param i
     *            Index.
     */
    public void aload(final int i)
    {
        switch(i)
        {
        case 0:
            this.code.add(JvmOp.ALOAD_0);
            break;
        case 1:
            this.code.add(JvmOp.ALOAD_1);
            break;
        case 2:
            this.code.add(JvmOp.ALOAD_2);
            break;
        case 3:
            this.code.add(JvmOp.ALOAD_3);
            break;
        default:
            this.code.add(JvmOp.ALOAD);
            this.code.add(i);
            break;
        }
        this.maxLocals = Math.max(this.maxLocals, i + 1);
        this.add(1);
    }

    /**
     * Writes a jump instruction.
     * 
     * @param op
     *            The opcode.
     * @param label
     *            The label.
     */
    void writeJmp(final int op, final int label)
    {
        switch(op)
        {
        case JvmOp.IFNE:
        case JvmOp.IFGE:
            this.curStack--;
            break;
        default:
            break;
        }
        this.code.add(op);
        this.jumps.add(this.code.size());
        this.code.addShort(label);
    }

    /**
     * Adds the value to the current used stack value. Checks maxStack.
     * 
     * @param plus
     *            Value to add.
     */
    void add(int plus)
    {
        this.maxStack = Math.max(this.maxStack, this.curStack += plus);
    }
    
    public void resolveLabels()
    {
        for(int i : this.jumps)
        {
            final int l = this.labels.get(this.code.getShort(i));
            this.code.setShort(i, l - i + 1);
        }
    }
    
    public void addLabel(final int index)
    {
        while(this.labels.size() <= index)
        {
            this.labels.add(0);
        }
        this.labels.set(index, this.code.size());
    }
}
