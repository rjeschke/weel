/*
* Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
* See LICENSE.txt for licensing information.
*/
package com.github.rjeschke.weel;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map.Entry;

import com.github.rjeschke.weel.annotations.WeelMethod;


public class Main
{

    @WeelMethod(name = "pow")
    public static double wPow(double a, double b)
    {
        return Math.pow(a, b);
    }
    
    @WeelMethod(name = "println")
    public static void wPow(Value v)
    {
        System.out.println(v);
    }

    public static double calc_weel(final Runtime w, double a, double b, double c)
    {
        w.load(a);
        w.load(b);
        w.add();
        w.load(c);
        w.mul();
        return w.pop().number;
    }
    
    public static void mapTest()
    {
        ValueMap map = new ValueMap();
        for(Entry<Value, Value> e : map)
        {
            System.out.println(e.getKey() + ", " + e.getValue());
        }
    }
    
    /**
     * This is:
     * <pre><code>local i
     *for i = 0, 16777215 do
     *    x = (a + b) * c
     *end
     *println(i)</code></pre> 
     * 
     * @param w
     * @param a
     * @param b
     * @param c
     */
    public static void weel_run(final Runtime w, float a, float b, float c)
    {
        w.openFrame(0, 2);
        
        w.load(0);
        w.sloc(0);
        
        w.load(16777215);
        w.load(1);
        
        if(w.beginForloop(0))
        {
            do
            {
                w.load(a);
                w.load(b);
                w.add();
                w.load(c);
                w.mul();
                w.sloc(1);
            } while(w.endForLoop(0));
        }
        w.pop(2);
        
        w.closeFrame();
    }

    public static double calc_java(double a, double b, double c)
    {
        return (a + b) * c;
    }

    /**
     * @param args
     * @throws IOException 
     * @throws InvocationTargetException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws NoSuchMethodException 
     * @throws SecurityException 
     */
    public static void main(String[] args) throws IOException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException
    {
        StringReader reader = new StringReader(
                "'test' fOr Do IF eLSeif hUNd 00.06"
                );
        Tokenizer tokenizer = new Tokenizer(reader);
        for(;;)
        {
            Token t = tokenizer.next();
            System.out.print(t);
            switch(t)
            {
            case NUMBER:
                System.out.println(": " + tokenizer.getNumber());
                break;
            case RESERVED:
                System.out.println(": " + tokenizer.getReservedWord());
                break;
            case STRING:
            case NAME:
                System.out.println(": '" + tokenizer.getString() + "'");
                break;
            default:
                System.out.println();
                break;
            }
            if(t == Token.EOF)
                break;
        }
        
        JvmClassWriter cw = new JvmClassWriter("com.github.rjeschke.weelscripts.Clazz0");
        
        JvmMethodWriter mw = cw.createMethod("test", "(Lcom/github/rjeschke/weel/Runtime;)V");
        
        ByteList code = mw.getCodeList();
        
        mw.dLoad(1);
        mw.dLoad(2);
        mw.getCodeList().add(JvmOp.ALOAD_0);
        mw.getCodeList().add(JvmOp.INVOKEVIRTUAL);
        mw.getCodeList().addShort(cw.addMethodRefConstant("com.github.rjeschke.weel.Runtime", "add", "()V"));
        mw.dLoad(42);
        mw.getCodeList().add(JvmOp.ALOAD_0);
        mw.getCodeList().add(JvmOp.INVOKEVIRTUAL);
        mw.getCodeList().addShort(cw.addMethodRefConstant("com.github.rjeschke.weel.Runtime", "mul", "()V"));
        code.add(JvmOp.RETURN);
        
        final byte[] test = cw.build();
        
        final FileOutputStream fos = new FileOutputStream("/home/rjeschke/Clazz0.class");
        fos.write(test);
        fos.close();

        Runtime weel = new Weel().getRuntime();
        Class<?> cl = Weel.classLoader.addClass("com.github.rjeschke.weelscripts.Clazz0", test);
        Method m = cl.getMethod("test", Runtime.class);
        long t0;

        WeelFunction func = new WeelFunction();
        func.name = "test";
        func.arguments = 0;
        func.returnsValue = false;
        
        func.clazz = "com.github.rjeschke.weelscripts.Clazz0";
        func.javaName = "test";
        
        func.invoker = WeelInvokerFactory.create();
        
        func.initialize();
        
        t0 = System.nanoTime();
        for(double d = 0; d < 16777216.0; d++)
        {
            //m.invoke(null, weel);
            func.invoke(weel);
            weel.pop(1);
        }
        t0 = System.nanoTime() - t0;
        System.out.println(t0 * 1e-6);
        
        t0 = System.nanoTime();
        for(double d = 0; d < 16777216.0; d++)
        {
            calc_weel(weel, 1, 2, 42);
        }
        t0 = System.nanoTime() - t0;
        System.out.println(t0 * 1e-6);

        for(int n = 0; n < 10; n++)
        {
            t0 = System.nanoTime();
            weel_run(weel, 1, 2, 42);
            t0 = System.nanoTime() - t0;
        }
        System.out.println(t0 * 1e-6);

        t0 = System.nanoTime();
        for(double d = 0; d < 16777216.0; d++)
        {
            calc_java(1, 2, 42);
        }
        t0 = System.nanoTime() - t0;
        System.out.println(t0 * 1e-6);
        
        weel.load("Hello world!");
        weel.mother.findFunction("println", 1).invoke(weel);
        
        System.out.println(double.class.getSuperclass());
    }

}
