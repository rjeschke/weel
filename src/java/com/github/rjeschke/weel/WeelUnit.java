/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

import java.util.ArrayList;

import com.github.rjeschke.weel.annotations.WeelRawMethod;

/**
 * Simple Weel unit testing framework.
 * <p>
 * Usage:
 * </p>
 * 
 * <pre>
 * <code>myTests = {}
 * wUnitTestcase(myTests, "The tests I wish to get executed.")
 * sub myTests:testThis()
 *    this-&gt;assert(1 + 1 == 2, "1 + 1 == 2")
 *    this-&gt;throws(sub()
 *        var = null
 *        println(var[0])
 *    end, "Using indexing on a non-map value should throw")
 * end</code>
 * </pre>
 * <p>
 * You may use as many testcases and subs in a testcase as you want.
 * </p>
 * 
 * @author René Jeschke <rene_jeschke@yahoo.de>
 */
// TODO maybe thread-safe this?
public class WeelUnit
{
    /** Unit testing methods. */
    private final static String[] FUNCTION_NAMES =
    { "assert", "throws", "nothrow" };
    /** Registered unit tests. */
    final static ArrayList<WeelUnitTest> unitTests = new ArrayList<WeelUnitTest>();

    /**
     * Runs all testcases registered with the given Weel instance.
     * 
     * @param weel
     *            The Weel.
     * @return <code>true</code> if all testcases passed.
     */
    public static boolean runTests(final Weel weel)
    {
        final int sp = weel.getRuntime().getStackPointer();
        int passed = 0, failed = 0, error = 0;
        System.out.println();
        System.out.println("[WeelUnit]");
        System.out.println("==========");

        if(unitTests.size() > 0)
        {
            for (final WeelUnitTest wut : unitTests)
            {
                try
                {
                    final boolean result = wut.runTests(weel);
                    if (result)
                        passed++;
                    else
                        failed++;
                }
                catch (Exception e)
                {
                    error++;
                    System.out.println(String.format("[WeelUnit]: ERROR '%s'", e
                            .toString()));
                    e.printStackTrace();
                }
            }
        
            System.out.println("==========");
            System.out
                    .println(String
                            .format(
                                    "[WeelUnit]: Performed %d testcase(s), %d passed, %d failed, %d total failure(s)",
                                    unitTests.size(), passed, failed, error));
        }
        else
        {
            System.out.println("[WeelUnit]: Nothing to do.");
        }
        
        weel.getRuntime().pop(weel.getRuntime().getStackPointer() - sp);

        return failed == 0 && error == 0;
    }

    /**
     * <code>wUnitTestcase(map, title)</code>
     * 
     * @param runtime
     *            The runtime.
     */
    @WeelRawMethod(args = 2)
    public static void wUnitTestcase(final Runtime runtime)
    {
        final String title = runtime.pop().toString();
        final ValueMap me = runtime.pop().getMap();
        final WeelUnitTest test = new WeelUnitTest(me, title);
        unitTests.add(test);
        initTestCase(runtime.getMother(), test);
    }

    /**
     * Initializes the unit test Weel class.
     * 
     * @param weel
     *            The Weel.
     * @param test
     *            The test.
     */
    private static void initTestCase(final Weel weel, final WeelUnitTest test)
    {
        final ValueMap me = test.me;
        me.set("$me$", new Value(test));
        for (final String f : FUNCTION_NAMES)
        {
            me.set(f, new Value(weel.findFunction("weelunit$$" + f)));
        }
    }

    /**
     * <code>this-&gt;assert(value)</code>
     * 
     * @param runtime
     *            The runtime.
     * @see WeelUnitTest#wuAssert(boolean, String)
     */
    @WeelRawMethod(name = "weelunit$$assert", args = 2)
    public static void wuAssert2(final Runtime runtime)
    {
        final boolean flag = runtime.popBoolean();
        final ValueMap me = runtime.pop().getMap();
        final WeelUnitTest test = (WeelUnitTest) me.get("$me$").getObject();
        test.wuAssert(flag, null);
    }

    /**
     * <code>this-&gt;assert(value, title)</code>
     * 
     * @param runtime
     *            The runtime.
     * @see WeelUnitTest#wuAssert(boolean, String)
     */
    @WeelRawMethod(name = "weelunit$$assert", args = 3)
    public static void wuAssert3(final Runtime runtime)
    {
        final String title = runtime.pop().getString();
        final boolean flag = runtime.popBoolean();
        final ValueMap me = runtime.pop().getMap();
        final WeelUnitTest test = (WeelUnitTest) me.get("$me$").getObject();
        test.wuAssert(flag, title);
    }

    /**
     * <code>this-&gt;throws(sub)</code>
     * 
     * @param runtime
     *            The runtime.
     * @see WeelUnitTest#wuThrows(Runtime, WeelFunction, String)
     */
    @WeelRawMethod(name = "weelunit$$throws", args = 2)
    public static void wuThrows2(final Runtime runtime)
    {
        final WeelFunction function = runtime.pop().getFunction();
        final ValueMap me = runtime.pop().getMap();
        final WeelUnitTest test = (WeelUnitTest) me.get("$me$").getObject();
        test.wuThrows(runtime, function, null);
    }

    /**
     * <code>this-&gt;throws(sub, title)</code>
     * 
     * @param runtime
     *            The runtime.
     * @see WeelUnitTest#wuThrows(Runtime, WeelFunction, String)
     */
    @WeelRawMethod(name = "weelunit$$throws", args = 3)
    public static void wuThrows3(final Runtime runtime)
    {
        final String title = runtime.pop().getString();
        final WeelFunction function = runtime.pop().getFunction();
        final ValueMap me = runtime.pop().getMap();
        final WeelUnitTest test = (WeelUnitTest) me.get("$me$").getObject();
        test.wuThrows(runtime, function, title);
    }

    /**
     * <code>this-&gt;nothrow(sub)</code>
     * 
     * @param runtime
     *            The runtime.
     * @see WeelUnitTest#wuNoThrow(Runtime, WeelFunction, String)
     */
    @WeelRawMethod(name = "weelunit$$nothrow", args = 2)
    public static void wuNoThrows2(final Runtime runtime)
    {
        final WeelFunction function = runtime.pop().getFunction();
        final ValueMap me = runtime.pop().getMap();
        final WeelUnitTest test = (WeelUnitTest) me.get("$me$").getObject();
        test.wuNoThrow(runtime, function, null);
    }

    /**
     * <code>this-&gt;nothrow(sub, title)</code>
     * 
     * @param runtime
     *            The runtime.
     * @see WeelUnitTest#wuNoThrow(Runtime, WeelFunction, String)
     */
    @WeelRawMethod(name = "weelunit$$nothrow", args = 3)
    public static void wuNoThrow3(final Runtime runtime)
    {
        final String title = runtime.pop().getString();
        final WeelFunction function = runtime.pop().getFunction();
        final ValueMap me = runtime.pop().getMap();
        final WeelUnitTest test = (WeelUnitTest) me.get("$me$").getObject();
        test.wuNoThrow(runtime, function, title);
    }

    // public static void main(String[] args)
    // {
    // final Weel weel = new Weel();
    //        
    // }
}
