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
            printUsage();
            return;
        }

        ArrayList<String> input = new ArrayList<String>();
        
        int vstack = Weel.DEFAULT_VALUE_STACK_SIZE;
        int fstack = Weel.DEFAULT_FRAME_STACK_SIZE;
        int cstack = Weel.DEFAULT_CLOSURE_STACK_SIZE;
        boolean debugMode = false;
        boolean dumpCode = false;
        int as = -1;
        
        try
        {
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
                        debugMode = true;
                    }
                    else if(o.equals("dump"))
                    {
                        dumpCode = true;
                    }
                    else if(o.equals("vstack"))
                    {
                        vstack = parseSize(args[++i]);
                    }
                    else if(o.equals("fstack"))
                    {
                        fstack = parseSize(args[++i]);
                    }
                    else if(o.equals("cstack"))
                    {
                        cstack = parseSize(args[++i]);
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
            }

            if(input.size() == 0)
            {
                System.out.println("No input file specified.");
                System.exit(-1);
            }
        }
        catch(Exception e)
        {
            printUsage();
            System.exit(-1);
        }
        
        try
        {
            final Weel weel = new Weel(vstack, fstack, cstack);
            weel.setDebugMode(debugMode);
            weel.enableCodeDump(dumpCode);
            
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
    
    private static void printUsage()
    {
        System.out.println("Usage: weel <script> [<script> ...] [options] [-- args]");
        System.out.println("---");
        System.out.println("Options:");
        System.out.println("--debug    : Enabled debug mode (asserts)");
        System.out.println("--dump     : Dump generated intermediate code");
        System.out.println("--vstack n : Sets the value stack size to 'n' slots");
        System.out.println("--fstack n : Sets the frame stack size to 'n' slots");
        System.out.println("--cstack n : Sets the closure function stack size to 'n' slots");
    }
    
    private static int parseSize(final String sz)
    {
        final String s = sz.toLowerCase();
        if(s.endsWith("m"))
            return Integer.parseInt(sz.substring(0, sz.length() - 1)) << 20;
        if(s.endsWith("k"))
            return Integer.parseInt(sz.substring(0, sz.length() - 1)) << 10;
        return Integer.parseInt(sz);
    }
}
