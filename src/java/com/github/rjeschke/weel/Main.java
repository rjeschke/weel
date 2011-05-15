/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

import java.io.FileOutputStream;

public class Main
{
    public static void main(String[] args)
    {
        try
        {
            final Weel weel = new Weel();
            weel.setDebugMode(false);

            weel.compileResource("com.github.rjeschke.weel.test.mandel");
            //weel.compileResource("com.github.rjeschke.weel.test.test");
            //weel.compileResource("com.github.rjeschke.weel.test.wunitArith");

            for(WeelLoader.ClassData cd : weel.classLoader.classData)
            {
                FileOutputStream fos = new FileOutputStream(
                        "/home/rjeschke/" + cd.name.substring(cd.name.lastIndexOf('.') + 1) + ".class");
                fos.write(cd.code);
                fos.close();
            }

//            for(final WeelFunction f : weel.functions)
//            {
//                System.out.println(f.index + " : " + f.toFullString());
//            }
            
            weel.runStatic();
            //weel.getRuntime().wipeStack();
            //WeelUnit.runTests(weel);
            
            if(weel.getRuntime().getStackPointer() != -1)
                System.err.println("Doh! You messed it up! (" + weel.getRuntime().getStackPointer() + ")");
        }
        catch (Exception e)
        {
            throw (e instanceof WeelException) ? (WeelException) e
                    : new WeelException(e);
        }
    }
}
