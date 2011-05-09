/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

import java.io.FileInputStream;

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
        final Weel weel = new Weel();
        ValueMap vargs = null;
        try
        {
            for (int i = 0; i < args.length; i++)
            {
                if (args[i].toLowerCase().equals("-args"))
                {
                    vargs = new ValueMap();
                    for (int n = i + 1, t = 0; n < args.length; n++, t++)
                    {
                        vargs.append(new Value(args[n]));
                    }
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

            final WeelFunction main = weel.findFunction("main");
            if (main != null)
            {
                if (main.arguments > 1)
                    throw new WeelException(
                            "main() should only take one argument");
                final Value ret = main.arguments == 1 ? weel
                        .invoke(main, vargs) : weel.invoke(main);
                if (ret != null)
                    System.out.println(ret);
            }
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
