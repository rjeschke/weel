/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

import java.io.FileInputStream;
import java.util.Arrays;

/**
 * Weel command line invoker prototype.
 * 
 * @author René Jeschke <rene_jeschke@yahoo.de>
 */
public final class Run
{
    /**
     * Main.
     * <p>
     * <code>Usage: weel &lt;script> [&lt;script> ...] [-args args]</code>
     * </p>
     * 
     * @param args
     *            Arguments
     */
    public static void main(String[] args)
    {
        if (args.length == 0)
        {
            System.out
                    .println("Usage: weel <script> [<script> ...] [-args args]");
            return;
        }
//        System.out.println("Weel @(" + System.getProperty("java.vm.name")
//                + " v" + System.getProperty("java.vm.version") + ")");
        final Weel weel = new Weel();
        try
        {
            int as = -1;
            for (int i = 0; i < args.length; i++)
            {
                if (args[i].toLowerCase().equals("-args"))
                {
                    as = i + 1;
                    break;
                }
                String filename = args[i];
                if (filename.indexOf('.') < 0)
                {
                    filename += ".weel";
                }
                final FileInputStream fis = new FileInputStream(filename);
                weel.compile(fis, filename);
                fis.close();
            }

            weel.runStatic();

            final Value ret = as != -1 ? weel.runMain(Arrays.copyOfRange(args,
                    as, args.length)) : weel.runMain();

            System.out.flush();
            
            if (ret != null)
                System.out.println(ret);
        }
        catch (final Exception e)
        {
            if (e instanceof WeelException)
                System.err.println(e.toString());
            else
                e.printStackTrace();
        }
    }
}
