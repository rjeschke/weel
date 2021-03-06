/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.rjeschke.weel;

/**
 * A Weel value.
 * 
 * @author René Jeschke <rene_jeschke@yahoo.de>
 */
public final class Value
{
    /** This Value's type. */
    ValueType type;
    /** This Value's double value. */
    double number;
    /** This Value's object. */
    Object object;

    /**
     * Creates a Value of type NULL.
     */
    public Value()
    {
        this.type = ValueType.NULL;
    }

    /**
     * Creates a Value of type NUMBER.
     * 
     * @param value
     *            The value.
     */
    public Value(final int value)
    {
        this.type = ValueType.NUMBER;
        this.number = value;
    }

    /**
     * Creates a Value of type NUMBER.
     * 
     * @param value
     *            The value.
     */
    public Value(final double value)
    {
        this.type = ValueType.NUMBER;
        this.number = value;
    }

    /**
     * Creates a Value of type STRING.
     * 
     * @param value
     *            The value.
     */
    public Value(final String value)
    {
        this.type = ValueType.STRING;
        this.object = value;
    }

    /**
     * Creates a Value of type MAP.
     * 
     * @param value
     *            The value.
     */
    public Value(final ValueMap value)
    {
        this.type = ValueType.MAP;
        this.object = value;
    }

    /**
     * Creates a Value of type FUNCTION.
     * 
     * @param value
     *            The value.
     */
    public Value(final WeelFunction value)
    {
        this.type = ValueType.FUNCTION;
        this.object = value;
    }

    /**
     * Creates a Value of type OBJECT.
     * 
     * @param value
     *            The value.
     */
    public Value(final Object value)
    {
        this.type = ValueType.OBJECT;
        this.object = value;
    }

    /**
     * Changes the type of this Value to <code>NULL</code>, clears references.
     */
    public void setNull()
    {
        this.type = ValueType.NULL;
        this.number = 0;
        this.object = null;
    }

    /**
     * Returns a boolean interpretation of this value.
     * 
     * <ul>
     * <li><code>NULL</code> returns <code>false</code></li>
     * <li>A <code>NUMBER</code> equal to 0 returns <code>false</code></li>
     * <li>A <code>STRING</code> with a length of 0 returns <code>false</code></li>
     * <li>A <code>MAP</code> with a length of 0 returns <code>false</code></li>
     * <li>A <code>OBJECT</code> with a value of null returns <code>false</code>
     * </li>
     * <li>Everything else returns <code>true</code></li>
     * </ul>
     * 
     * @return A boolean interpretation of this value.
     */
    public boolean toBoolean()
    {
        switch (this.type)
        {
        case NULL:
            return false;
        case STRING:
            return ((String)this.object).length() > 0;
        case NUMBER:
            return this.number != 0;
        case MAP:
            return ((ValueMap)this.object).size != 0;
        case OBJECT:
            return this.object != null;
        default:
            return true;
        }
    }

    /**
     * Copies this value into another.
     * 
     * @param other
     *            Value to copy to.
     */
    public void copyTo(final Value other)
    {
        other.type = this.type;
        other.number = this.number;
        other.object = this.object;
    }

    /** @see java.lang.Object#clone() */
    @Override
    public Value clone()
    {
        final Value v = new Value();
        this.copyTo(v);
        return v;
    }

    /**
     * Returns an internal String representation of this value.
     * 
     * @return The String.
     */
    public String toIntString()
    {
        switch (this.type)
        {
        case NULL:
            return "null";
        case NUMBER:
        {
            // Hack
            final long temp = (long)this.number;
            if(temp == this.number)
                return Long.toString(temp);
            return Double.toString(this.number);
        }
        case STRING:
            return "\"" + InstrLoad.escape((String)this.object) + "\"";
        case MAP:
            return ((ValueMap)this.object).toString();
        case FUNCTION:
            return ((WeelFunction)this.object).toString();
        case OBJECT:
            return this.object.toString();
        }
        return "null";
    }
    
    /** @see java.lang.Object#toString() */
    @Override
    public String toString()
    {
        switch (this.type)
        {
        case NULL:
            return "null";
        case NUMBER:
        {
            // Hack
            final long temp = (long)this.number;
            if(temp == this.number)
                return Long.toString(temp);
            return Double.toString(this.number);
        }
        case STRING:
            return (String)this.object;
        case MAP:
            return ((ValueMap)this.object).toString();
        case FUNCTION:
            return ((WeelFunction)this.object).toString();
        case OBJECT:
            return this.object.toString();
        }
        return "null";
    }

    /**
     * Gets this Value's type.
     * 
     * @return The type of this value.
     */
    public ValueType getType()
    {
        return this.type;
    }

    /**
     * Check if this Value is NULL.
     * 
     * @return <code>true</code> if this Value is NULL.
     */
    public boolean isNull()
    {
        return this.type == ValueType.NULL;
    }

    /**
     * Check if this Value is a NUMBER.
     * 
     * @return <code>true</code> if this Value is a NUMBER.
     */
    public boolean isNumber()
    {
        return this.type == ValueType.NUMBER;
    }

    /**
     * Check if this Value is a STRING.
     * 
     * @return <code>true</code> if this Value is a STRING.
     */
    public boolean isString()
    {
        return this.type == ValueType.STRING;
    }

    /**
     * Check if this Value is a MAP.
     * 
     * @return <code>true</code> if this Value is a MAP.
     */
    public boolean isMap()
    {
        return this.type == ValueType.MAP;
    }

    /**
     * Check if this Value is a FUNCTION.
     * 
     * @return <code>true</code> if this Value is a FUNCTION.
     */
    public boolean isFunction()
    {
        return this.type == ValueType.FUNCTION;
    }

    /**
     * Check if this Value is an OBJECT.
     * 
     * @return <code>true</code> if this Value is an OBJECT.
     */
    public boolean isObject()
    {
        return this.type == ValueType.OBJECT;
    }

    /**
     * Gets this Value's NUMBER.
     * 
     * @return The NUMBER of this Value.
     */
    public double getNumber()
    {
        if (this.type != ValueType.NUMBER)
            throw new WeelException("Value is not a NUMBER");
        return this.number;
    }

    /**
     * Gets this Value's STRING.
     * 
     * @return The STRING of this Value.
     */
    public String getString()
    {
        if (this.type != ValueType.STRING)
            throw new WeelException("Value is not a STRING");
        return (String)this.object;
    }

    /**
     * Gets this Value's MAP.
     * 
     * @return The MAP of this Value.
     */
    public ValueMap getMap()
    {
        if (this.type != ValueType.MAP)
            throw new WeelException("Value is not a MAP");
        return (ValueMap)this.object;
    }

    /**
     * Gets this Value's FUNCTION.
     * 
     * @return The FUNCTION of this Value.
     */
    public WeelFunction getFunction()
    {
        if (this.type != ValueType.FUNCTION)
            throw new WeelException("Value is not a FUNCTION");
        return (WeelFunction)this.object;
    }

    /**
     * Gets this Value's OBJECT.
     * 
     * @return The OBJECT of this Value.
     */
    public Object getObject()
    {
        if (this.type != ValueType.OBJECT)
            throw new WeelException("Value is not an OBJECT");
        return this.object;
    }

    /**
     * Returns the size of this value.
     * 
     * @return The size as a double.
     */
    public double size()
    {
        switch (this.type)
        {
        case NUMBER:
            return Math.abs(this.number);
        case STRING:
            return ((String)this.object).length();
        case MAP:
            return ((ValueMap)this.object).size();
        case FUNCTION:
            return ((WeelFunction)this.object).arguments;
        default:
            return 0;
        }
    }
}
