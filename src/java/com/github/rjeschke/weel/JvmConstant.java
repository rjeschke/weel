/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

/**
 * A class file constant.
 * 
 * @author René Jeschke <rene_jeschke@yahoo.de>
 */
final class JvmConstant
{
    public final static int CONSTANT_Class = 7;
    public final static int CONSTANT_Fieldref = 9;
    public final static int CONSTANT_Methodref = 10;
    public final static int CONSTANT_InterfaceMethodref = 11;
    public final static int CONSTANT_String = 8;
    public final static int CONSTANT_Integer = 3;
    public final static int CONSTANT_Float = 4;
    public final static int CONSTANT_Long = 5;
    public final static int CONSTANT_Double = 6;
    public final static int CONSTANT_NameAndType = 12;
    public final static int CONSTANT_Utf8 = 1;

    /** This constant's type. */
    public final int type;
    /** A String. */
    public final String stringValue;
    /** A double. */
    public final double doubleValue;
    /** A float. */
    public final float floatValue;
    /** An integer. */
    public final int intValue;
    /** A long. */
    public final long longValue;
    /** Indices. */
    public final int index0, index1;

    public JvmConstant(final String value)
    {
        this.type = CONSTANT_Utf8;
        this.stringValue = value;
        this.doubleValue = 0;
        this.floatValue = 0;
        this.intValue = 0;
        this.longValue = 0;
        this.index0 = this.index1 = 0;
    }

    public JvmConstant(final int value)
    {
        this.type = CONSTANT_Integer;
        this.stringValue = null;
        this.doubleValue = 0;
        this.floatValue = 0;
        this.intValue = value;
        this.longValue = 0;
        this.index0 = this.index1 = 0;
    }

    public JvmConstant(final long value)
    {
        this.type = CONSTANT_Long;
        this.stringValue = null;
        this.doubleValue = 0;
        this.floatValue = 0;
        this.intValue = 0;
        this.longValue = value;
        this.index0 = this.index1 = 0;
    }

    public JvmConstant(final double value)
    {
        this.type = CONSTANT_Double;
        this.stringValue = null;
        this.doubleValue = value;
        this.floatValue = 0;
        this.intValue = 0;
        this.longValue = 0;
        this.index0 = this.index1 = 0;
    }

    public JvmConstant(final float value)
    {
        this.type = CONSTANT_Float;
        this.stringValue = null;
        this.doubleValue = 0;
        this.floatValue = value;
        this.intValue = 0;
        this.longValue = 0;
        this.index0 = this.index1 = 0;
    }

    public JvmConstant(final int type, final int index0)
    {
        this.type = type;
        this.stringValue = null;
        this.doubleValue = 0;
        this.floatValue = 0;
        this.intValue = 0;
        this.longValue = 0;
        this.index0 = index0;
        this.index1 = 0;
    }

    public JvmConstant(final int type, final int index0, final int index1)
    {
        this.type = type;
        this.stringValue = null;
        this.doubleValue = 0;
        this.floatValue = 0;
        this.intValue = 0;
        this.longValue = 0;
        this.index0 = index0;
        this.index1 = index1;
    }

    /** @see java.lang.Object#hashCode() */
    @Override
    public int hashCode()
    {
        int hash = this.type;
        switch (this.type)
        {
        case CONSTANT_Utf8:
            hash = (hash * 31) + this.stringValue.hashCode();
            break;
        case CONSTANT_Double:
            hash = (hash * 31) + ((Double) this.doubleValue).hashCode();
            break;
        case CONSTANT_Float:
            hash = (hash * 31) + ((Float) this.floatValue).hashCode();
            break;
        case CONSTANT_Integer:
            hash = (hash * 31) + ((Integer) this.intValue).hashCode();
            break;
        case CONSTANT_Long:
            hash = (hash * 31) + ((Long) this.longValue).hashCode();
            break;
        case CONSTANT_Class:
        case CONSTANT_String:
            hash = (hash * 31) + this.index0;
            break;
        case CONSTANT_Fieldref:
        case CONSTANT_InterfaceMethodref:
        case CONSTANT_Methodref:
        case CONSTANT_NameAndType:
            hash = ((hash * 31) + this.index0) * 31 + this.index1;
            break;
        }
        return hash;
    }

    /** @see java.lang.Object#equals(Object) */
    @Override
    public boolean equals(final Object other)
    {
        if (other == null || !(other instanceof JvmConstant))
            return false;
        final JvmConstant c = (JvmConstant) other;
        if (this.type != c.type)
            return false;
        switch (this.type)
        {
        case CONSTANT_Utf8:
            return this.stringValue.equals(c.stringValue);
        case CONSTANT_Double:
            return Double.compare(this.doubleValue, c.doubleValue) == 0;
        case CONSTANT_Float:
            return Float.compare(this.floatValue, c.floatValue) == 0;
        case CONSTANT_Integer:
            return this.intValue == c.intValue;
        case CONSTANT_Long:
            return this.longValue == c.longValue;
        case CONSTANT_Class:
        case CONSTANT_String:
            return this.index0 == c.index0;
        case CONSTANT_Fieldref:
        case CONSTANT_InterfaceMethodref:
        case CONSTANT_Methodref:
        case CONSTANT_NameAndType:
            return this.index0 == c.index0 && this.index1 == c.index1;
        }
        return false;
    }
}
