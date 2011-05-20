/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

import java.util.Locale;

import com.github.rjeschke.weel.annotations.WeelRawMethod;

/**
 * Weel string library.
 * 
 * @author René Jeschke <rene_jeschke@yahoo.de>
 */
public final class WeelLibString
{
    private WeelLibString()
    {
        // empty
    }

    /**
     * <code>strUpper(s)</code>
     * <p>
     * Returns the string converted to upper case.
     * </p>
     * 
     * @param runtime
     *            The runtime.
     * @see java.lang.String#toUpperCase()
     */
    @WeelRawMethod(args = 1, returnsValue = true)
    public final static void strUpper(final WeelRuntime runtime)
    {
        runtime.load(runtime.popString().toUpperCase());
    }

    /**
     * <code>strLower(s)</code>
     * <p>
     * Returns the string converted to lower case.
     * </p>
     * 
     * @param runtime
     *            The runtime.
     * @see java.lang.String#toLowerCase()
     */
    @WeelRawMethod(args = 1, returnsValue = true)
    public final static void strLower(final WeelRuntime runtime)
    {
        runtime.load(runtime.popString().toLowerCase());
    }

    /**
     * <code>strIndexOf(str, val)</code>
     * <p>
     * Returns the index of the first occurence of 'val' in 'str', -1 if none
     * was found.
     * </p>
     * 
     * @param runtime
     *            The runtime.
     * @see java.lang.String#indexOf(String)
     */
    @WeelRawMethod(name = "strindexof", args = 2, returnsValue = true)
    public final static void strIndex2(final WeelRuntime runtime)
    {
        final String b = runtime.popString();
        final String a = runtime.popString();
        runtime.load(a.indexOf(b));
    }

    /**
     * <code>strIndexOf(str, val, i)</code>
     * <p>
     * Returns the index of the first occurence of 'val' in 'str' starting from
     * 'i', -1 if none was found.
     * </p>
     * 
     * @param runtime
     *            The runtime.
     * @see java.lang.String#indexOf(String, int)
     */
    @WeelRawMethod(name = "strindexof", args = 3, returnsValue = true)
    public final static void strIndex3(final WeelRuntime runtime)
    {
        final int i = (int) runtime.popNumber();
        final String b = runtime.popString();
        final String a = runtime.popString();
        runtime.load(a.indexOf(b, i));
    }

    /**
     * <code>strLastIndexOf(str, val)</code>
     * <p>
     * Returns the index of the last occurence of 'val' in 'str', -1 if none was
     * found.
     * </p>
     * 
     * @param runtime
     *            The runtime.
     * @see java.lang.String#lastIndexOf(String)
     */
    @WeelRawMethod(name = "strlastindexof", args = 2, returnsValue = true)
    public final static void strLastIndex2(final WeelRuntime runtime)
    {
        final String b = runtime.popString();
        final String a = runtime.popString();
        runtime.load(a.lastIndexOf(b));
    }

    /**
     * <code>strLastIndexOf(str, val, i)</code>
     * <p>
     * Returns the index of the last occurence of 'val' in 'str' starting from
     * 'i', -1 if none was found.
     * </p>
     * 
     * @param runtime
     *            The runtime.
     * @see java.lang.String#lastIndexOf(String, int)
     */
    @WeelRawMethod(name = "strlastindexof", args = 3, returnsValue = true)
    public final static void strLastIndex3(final WeelRuntime runtime)
    {
        final int i = (int) runtime.popNumber();
        final String b = runtime.popString();
        final String a = runtime.popString();
        runtime.load(a.lastIndexOf(b, i));
    }

    /**
     * <code>strSplit(str, val)</code>
     * <p>
     * Splits this string around matches of the given regular expression.
     * </p>
     * 
     * @param runtime
     *            The runtime.
     * @see java.lang.String#split(String)
     */
    @WeelRawMethod(args = 2, returnsValue = true)
    public final static void strSplit(final WeelRuntime runtime)
    {
        final String b = runtime.popString();
        final String a = runtime.popString();
        final String[] t = a.split(b);
        final ValueMap m = new ValueMap();
        for (int i = 0; i < t.length; i++)
        {
            m.append(new Value(t[i]));
        }
        runtime.load(m);
    }

    /**
     * <code>strSub(str, start)</code>
     * <p>
     * Returns a sub string of 'str' starting at 'start'.
     * </p>
     * 
     * @param runtime
     *            The runtime.
     * @see java.lang.String#substring(int)
     */
    @WeelRawMethod(args = 2, returnsValue = true)
    public final static void strSub(final WeelRuntime runtime)
    {
        final int b = (int) runtime.popNumber();
        final String a = runtime.popString();
        runtime.load(b <= 0 ? a : b >= a.length() ? "" : a.substring(b));
    }

    /**
     * <code>strSub(str, start, en)</code>
     * <p>
     * Returns a sub string of 'str' starting at 'start' up to 'end'
     * (exclusive).
     * </p>
     * 
     * @param runtime
     *            The runtime.
     * @see java.lang.String#substring(int, int)
     */
    @WeelRawMethod(name = "strsub", args = 3, returnsValue = true)
    public final static void strSub3(final WeelRuntime runtime)
    {
        final int c = (int) runtime.popNumber();
        final int b = (int) runtime.popNumber();
        final String a = runtime.popString();

        final int start = b < 0 ? 0 : b >= a.length() ? a.length() - 1 : b;
        final int end = c <= b ? b : c > a.length() ? a.length() : c;

        runtime.load(start != end ? a.substring(start, end) : "");
    }

    /**
     * <code>toChar(v)</code>
     * <p>
     * Returns the number 'v' as a char (string).
     * </p>
     * 
     * @param runtime
     *            The runtime.
     */
    @WeelRawMethod(args = 1, returnsValue = true)
    public final static void toChar(final WeelRuntime runtime)
    {
        runtime.load(Character.toString((char) runtime.popNumber()));
    }

    /**
     * <code>strTrim(s)</code>
     * <p>
     * Returns 's' with all leading and trailing white spaces removed.
     * </p>
     * 
     * @param runtime
     *            The runtime.
     * @see java.lang.String#trim()
     */
    @WeelRawMethod(args = 1, returnsValue = true)
    public final static void strTrim(final WeelRuntime runtime)
    {
        runtime.load(runtime.popString().trim());
    }

    /**
     * <code>strCharAt(s, i)</code>
     * <p>
     * Returns the chat at position 'i' in string 's'.
     * </p>
     * 
     * @param runtime
     *            The runtime.
     * @see java.lang.String#charAt(int)
     */
    @WeelRawMethod(args = 2, returnsValue = true)
    public final static void strCharAt(final WeelRuntime runtime)
    {
        final int index = (int) runtime.popNumber();
        final String str = runtime.popString();
        runtime.load(index < 0 || index >= str.length() ? 0 : (int) str
                .charAt(index));
    }

    /**
     * <code>strContains(s, v)</code>
     * <p>
     * Returns true is 's' contains 'v'.
     * </p>
     * 
     * @param runtime
     *            The runtime.
     * @see java.lang.String#contains(CharSequence)
     */
    @WeelRawMethod(args = 2, returnsValue = true)
    public final static void strContains(final WeelRuntime runtime)
    {
        final String v = runtime.popString();
        final String str = runtime.popString();
        runtime.load(str.contains(v));
    }

    /**
     * <code>strStartsWith(s, v)</code>
     * <p>
     * Returns true is 's' starts with 'v'.
     * </p>
     * 
     * @param runtime
     *            The runtime.
     * @see java.lang.String#startsWith(String)
     */
    @WeelRawMethod(args = 2, returnsValue = true)
    public final static void strStartsWith(final WeelRuntime runtime)
    {
        final String v = runtime.popString();
        final String str = runtime.popString();
        runtime.load(str.startsWith(v));
    }

    /**
     * <code>strEndsWith(s, v)</code>
     * <p>
     * Returns true is 's' ends with 'v'.
     * </p>
     * 
     * @param runtime
     *            The runtime.
     * @see java.lang.String#endsWith(String)
     */
    @WeelRawMethod(args = 2, returnsValue = true)
    public final static void strEndsWith(final WeelRuntime runtime)
    {
        final String v = runtime.popString();
        final String str = runtime.popString();
        runtime.load(str.endsWith(v));
    }

    /**
     * <code>strMatches(s, v)</code>
     * <p>
     * Returns true is 's' matches the regular expression 'v'.
     * </p>
     * 
     * @param runtime
     *            The runtime.
     * @see java.lang.String#matches(String)
     */
    @WeelRawMethod(args = 2, returnsValue = true)
    public final static void strMatches(final WeelRuntime runtime)
    {
        final String v = runtime.popString();
        final String str = runtime.popString();
        runtime.load(str.matches(v));
    }

    /**
     * <code>strReplace(s, a, b)</code>
     * <p>
     * Replaces all occurrences of 'a' in 's' with 'b'.
     * </p>
     * 
     * @param runtime
     *            The runtime.
     * @see java.lang.String#replace(CharSequence, CharSequence)
     */
    @WeelRawMethod(args = 3, returnsValue = true)
    public final static void strReplace(final WeelRuntime runtime)
    {
        final String b = runtime.popString();
        final String a = runtime.popString();
        final String str = runtime.popString();
        runtime.load(str.replace(a, b));
    }

    /**
     * <code>strReplaceAll(s, a, b)</code>
     * <p>
     * Replaces all strings that match the regular expression 'a' in 's' with
     * 'b'.
     * </p>
     * 
     * @param runtime
     *            The runtime.
     * @see java.lang.String#replaceAll(String, String)
     */
    @WeelRawMethod(args = 3, returnsValue = true)
    public final static void strReplaceAll(final WeelRuntime runtime)
    {
        final String b = runtime.popString();
        final String a = runtime.popString();
        final String str = runtime.popString();
        runtime.load(str.replaceAll(a, b));
    }

    /**
     * <code>strReplaceFirst(s, a, b)</code>
     * <p>
     * Replaces the first match of the regular expression 'a' in 's' with 'b'.
     * </p>
     * 
     * @param runtime
     *            The runtime.
     * @see java.lang.String#replaceFirst(String, String)
     */
    @WeelRawMethod(args = 3, returnsValue = true)
    public final static void strReplaceFirst(final WeelRuntime runtime)
    {
        final String b = runtime.popString();
        final String a = runtime.popString();
        final String str = runtime.popString();
        runtime.load(str.replaceFirst(a, b));
    }

    /**
     * <code>strRepeat(s, n)</code>
     * <p>
     * Returns a string consisting of 'n' times 's'.
     * </p>
     * 
     * @param runtime
     *            The runtime.
     */
    @WeelRawMethod(args = 2, returnsValue = true)
    public final static void strRepeat(final WeelRuntime runtime)
    {
        final int n = (int)runtime.popNumber();
        final String s = runtime.popToString();
        if(n < 1)
        {
            runtime.load("");
        }
        else 
        {
            final StringBuilder sb = new StringBuilder(n * s.length());
            for(int i = 0; i < n; i++)
                sb.append(s);
            runtime.load(sb.toString());
        }
    }
    
    /**
     * <code>strPadLeft(s, a, l)</code>
     * <p>
     * Returns 's' padded left with 'a' up to a total length of 'l'.
     * </p>
     * 
     * @param runtime
     *            The runtime.
     */
    @WeelRawMethod(args = 3, returnsValue = true)
    public final static void strPadLeft(final WeelRuntime runtime)
    {
        final int n = (int)runtime.popNumber();
        final String a = runtime.popToString();
        final String s = runtime.popToString();
        if(s.length() >= n || n < 1)
        {
            runtime.load(s);
        }
        else 
        {
            final StringBuilder sb = new StringBuilder(n);
            int left = n - s.length();
            while(left >= a.length())
            {
                sb.append(a);
                left -= a.length();
            }
            if(left > 0)
            {
                sb.append(a.substring(0, left));
            }
            sb.append(s);
            runtime.load(sb.toString());
        }
    }

    /**
     * <code>strPadRight(s, a, l)</code>
     * <p>
     * Returns 's' padded right with 'a' up to a total length of 'l'.
     * </p>
     * 
     * @param runtime
     *            The runtime.
     */
    @WeelRawMethod(args = 3, returnsValue = true)
    public final static void strPadRight(final WeelRuntime runtime)
    {
        final int n = (int)runtime.popNumber();
        final String a = runtime.popToString();
        final String s = runtime.popToString();
        if(s.length() >= n || n < 1)
        {
            runtime.load(s);
        }
        else 
        {
            final StringBuilder sb = new StringBuilder(n);
            sb.append(s);
            int left = n - s.length();
            while(left >= a.length())
            {
                sb.append(a);
                left -= a.length();
            }
            if(left > 0)
            {
                sb.append(a.substring(0, left));
            }
            runtime.load(sb.toString());
        }
    }
    
    /**
     * <code>strChars(s)</code>
     * <p>
     * Returns the string as a char array.
     * </p>
     * 
     * @param runtime
     *            The runtime.
     * @see java.lang.String#toCharArray()
     */
    @WeelRawMethod(args = 1, returnsValue = true)
    public final static void strChars(final WeelRuntime runtime)
    {
        final char[] chars = runtime.popToString().toCharArray();
        final ValueMap map = new ValueMap();
        for(int i = 0; i < chars.length; i++)
        {
            map.append(new Value(chars[i]));
        }
        runtime.load(map);
    }

    /**
     * <code>strReverse(s)</code>
     * <p>
     * Returns the string in reverse order.
     * </p>
     * 
     * @param runtime
     *            The runtime.
     */
    @WeelRawMethod(args = 1, returnsValue = true)
    public final static void strReverse(final WeelRuntime runtime)
    {
        final String str = runtime.popToString();
        final StringBuilder sb = new StringBuilder(str.length());
        for(int i = str.length() - 1; i >= 0; i--)
        {
            sb.append(str.charAt(i));
        }
        runtime.load(sb.toString());
    }

    /**
     * <code>strFormat(s, list)</code>
     * <p>
     * Returns a formatted String.
     * </p>
     * 
     * @param runtime
     *            The runtime.
     * @see java.lang.String#format(String, Object...)
     */
    @WeelRawMethod(args = 2, returnsValue = true)
    public final static void strFormat(final WeelRuntime runtime)
    {
        final ValueMap l = runtime.popMap();
        final String fmt = runtime.popString();
        runtime.load(format(Locale.getDefault(), fmt, l));
    }

    /**
     * <code>strFormat(locale, s, list)</code>
     * <p>
     * Returns a formatted String.
     * </p>
     * 
     * @param runtime
     *            The runtime.
     * @see java.lang.String#format(String, Object...)
     */
    @WeelRawMethod(name = "strFormat", args = 3, returnsValue = true)
    public final static void strFormat3(final WeelRuntime runtime)
    {
        final ValueMap l = runtime.popMap();
        final String fmt = runtime.popString();
        final String locale = runtime.popString();
        runtime.load(format(new Locale(locale), fmt, l));
    }
    
    /**
     * Formats a string.
     * 
     * @param locale The Locale.
     * @param fmt The format string.
     * @param l The values.
     * @return The formatted string.
     */
    final static String format(final Locale locale, final String fmt, final ValueMap l)
    {
        if (l.size == 0)
        {
            return fmt;
        }

        try
        {
            final Object[] objs = new Object[l.size];
            for (int i = 0, p = 0; i < fmt.length(); i++)
            {
                final char c = fmt.charAt(i);
                if (c == '%')
                {
                    int n = i + 1;
                    boolean done = false;
                    while (n < fmt.length() && !done)
                    {
                        switch (Character.toLowerCase(fmt.charAt(n)))
                        {
                        case 'a':
                        case 'e':
                        case 'f':
                        case 'g':
                            objs[p] = l.data.get(p++).getNumber();
                            done = true;
                            break;
                        case 'd':
                        case 'o':
                        case 'x':
                            objs[p] = (int) l.data.get(p++).getNumber();
                            done = true;
                            break;
                        case 'c':
                            objs[p] = (char) l.data.get(p++).getNumber();
                            done = true;
                            break;
                        case 's':
                            objs[p] = l.data.get(p++).toString();
                            done = true;
                            break;
                        case '%':
                            done = true;
                            break;
                        default:
                            n++;
                            break;
                        }
                    }
                    i = n;
                }
            }
            return String.format(locale, fmt, objs);
        }
        catch (Exception e)
        {
            return "***ERR***" + fmt;
        }
    }
}
