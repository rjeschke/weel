/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
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
     * <code>Usage: weel &lt;script> [&lt;script> ...] [-- args]</code>
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
                    .println("Usage: weel <script> [<script> ...] [--debug] [--dump] [-- args]");
            return;
        }

        final Weel weel = new Weel();
        ArrayList<String> input = new ArrayList<String>();
        try
        {
            int as = -1;
            for (int i = 0; i < args.length; i++)
            {
                final String a = args[i];
                if(a.startsWith("--"))
                {
                    if(a.length() == 2)
                    {
                        as = i + 1;
                        break;
                    }
                    final String o = a.substring(2).toLowerCase();
                    if(o.equals("debug"))
                    {
                        weel.setDebugMode(true);
                    }
                    else if(o.equals("dump"))
                    {
                        weel.enableCodeDump(true);
                    }
                    else
                    {
                        System.out.println("Unknown option '" + a + "'.");
                        System.exit(-1);
                    }
                }
                else
                {
                    File f = new File(a);
                    if (!f.exists())
                    {
                        f = new File(a + ".weel");
                        if (!f.exists())
                        {
                            System.out.println("Can't open input file '" + a + "'.");
                            System.exit(-1);
                        }
                    }
                    input.add(f.toString());
                }
                if (args[i].equals("--"))
                {
                    as = i + 1;
                    break;
                }
            }

            if(input.size() == 0)
            {
                System.out.println("No input file specified.");
                System.exit(-1);
            }
            
            for(final String filename : input)
            {
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
//            if (e instanceof WeelException)
//                System.err.println(e.toString());
//            else
            e.printStackTrace();
        }
    }
}
