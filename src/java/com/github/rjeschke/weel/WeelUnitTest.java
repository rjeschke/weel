/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

import java.util.Map.Entry;

/**
 * Weel unit test.
 * 
 * @author René Jeschke <rene_jeschke@yahoo.de>
 */
public class WeelUnitTest
{
    /** The Weel class. */
    final ValueMap me;
    /** Testcase title. */
    final String title;
    /** Counters. */
    private volatile int passed = 0, failed = 0, error = 0;

    /**
     * Constructor.
     * 
     * @param me
     *            The Weel class.
     * @param title
     *            The title.
     */
    public WeelUnitTest(final ValueMap me, final String title)
    {
        this.me = me;
        this.title = title;
    }

    /**
     * Runs all tests inside this testcase.
     * 
     * @param weel
     *            The Weel.
     * @return <code>true</code> if all tests passed.
     */
    public boolean runTests(final Weel weel)
    {
        final Runtime rt = weel.getRuntime();
        boolean ranTest = false;
        System.out.println("[TestCase]: " + this.title);
        System.out.println("----------");
        final Value before = this.me.get("before");
        final Value after = this.me.get("after");
        
        if(before.isFunction())
        {
            System.out.println("  [before]");
            this.invoke(before.getFunction(), rt);
        }
        
        for (Entry<Value, Value> e : this.me)
        {
            final String key = e.getKey().toString();
            final Value val = e.getValue();
            if (val.isFunction())
            {
                final WeelFunction func = val.getFunction();
                if (!func.name.startsWith("weelunit$$")
                        && func.name.contains("$$") && key.startsWith("test")
                        && key.length() > 4)
                {
                    if (ranTest)
                        System.out.println();
                    System.out.println("    [test]: " + key.substring(4));
                    this.invoke(func, rt);
                    ranTest = true;
                }
            }
        }
        
        if(after.isFunction())
        {
            System.out.println("   [after]");
            this.invoke(after.getFunction(), rt);
        }

        System.out.println("----------");
        if(ranTest)
        {
            System.out.println(String.format(
                    "  [result]: %d passed, %d failed, %d error(s)", this.passed,
                    this.failed, this.error));
        }
        else
        {
            System.out.println("[TestCase]: Nothing to do.");
        }
        System.out.println();
        return this.failed == 0 && this.error == 0;
    }

    /**
     * Invokes a test, takes care of stack cleanup.
     * 
     * @param func
     *            The function.
     * @param runtime
     *            The runtime.
     */
    private void invoke(final WeelFunction func, final Runtime runtime)
    {
        final int sp = runtime.getStackPointer();
        runtime.load(this.me);
        func.invoke(runtime);
        runtime.pop(runtime.getStackPointer() - sp);
    }

    /**
     * This test passes if the value is <code>true</code>.
     * 
     * @param value
     *            The value.
     * @param title
     *            The title.
     */
    public void wuAssert(final boolean value, final String title)
    {
        System.out.print("  [assert]");
        if (title != null)
            System.out.print(": " + title);
        String err = null;
        boolean result;
        try
        {
            result = value;
            if (result)
                this.passed++;
            else
                this.failed++;
        }
        catch (Exception e)
        {
            err = e.toString();
            this.error++;
            result = false;
        }
        System.out.print(" => " + (result ? "PASS" : "FAIL"));
        if (err != null)
            System.out.print(" (" + err + ")");
        System.out.println();
    }

    /**
     * This test passes if the functions throws an Exception.
     * 
     * @param runtime
     *            The runtime.
     * @param function
     *            The function.
     * @param title
     *            The title.
     */
    public void wuThrows(final Runtime runtime, final WeelFunction function,
            final String title)
    {
        System.out.print("  [throws]");
        if (title != null)
            System.out.print(": " + title);
        boolean result;
        try
        {
            function.invoke(runtime);
            this.failed++;
            result = false;
        }
        catch (Exception e)
        {
            runtime.closeFrame(0);
            this.passed++;
            result = true;
        }
        System.out.println(" => " + (result ? "PASS" : "FAIL"));
    }

    /**
     * This test passes if the function does not throw an Exception.
     * 
     * @param runtime
     *            The runtime.
     * @param function
     *            The function.
     * @param title
     *            The title.
     */
    public void wuNoThrow(final Runtime runtime, final WeelFunction function,
            final String title)
    {
        System.out.print(" [nothrow]");
        if (title != null)
            System.out.print(": " + title);
        boolean result;
        try
        {
            function.invoke(runtime);
            this.passed++;
            result = true;
        }
        catch (Exception e)
        {
            runtime.closeFrame(0);
            this.failed++;
            result = false;
        }
        System.out.println(" => " + (result ? "PASS" : "FAIL"));
    }
}
