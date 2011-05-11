/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Weel map implementation.
 * 
 * @author René Jeschke <rene_jeschke@yahoo.de>
 */
// FIXME this class needs improvement!
public final class ValueMap implements Iterable<Entry<Value, Value>>
{
    /** Integer keys. */
    private Map<Integer, Integer> intKeys = new HashMap<Integer, Integer>();
    /** String keys. */
    private Map<String, Integer> strKeys = new HashMap<String, Integer>();
    /** The size. */
    int size;
    /** Is this map ordered? */
    boolean ordered = true;
    /** The data. */
    ArrayList<Value> data;
    /** The keys. */
    ArrayList<Value> keys;
    /** The highest integer key value for unordered maps. */
    int highestIntKey;

    /**
     * Creates a new ValueMap.
     */
    public ValueMap()
    {
        this.data = new ArrayList<Value>();
        this.keys = new ArrayList<Value>();
    }

    /**
     * Gets the size of this map.
     * 
     * @return The size.
     */
    public int size()
    {
        return this.size;
    }

    /**
     * Get the value at the given index.
     * 
     * @param index
     *            The index.
     * @return The value or a Value of type NULL.
     * @throws WeelException
     *             If the index is invalid.
     */
    public Value get(final Value index)
    {
        final Value out = new Value();
        this.get(index, out);
        return out;
    }

    /**
     * Get the value at the given index.
     * 
     * @param index
     *            The index.
     * @return The value or a Value of type NULL.
     * @throws WeelException
     *             If the index is invalid.
     */
    public Value get(final int index)
    {
        if (this.ordered)
        {
            return index >= 0 && index < this.size ? this.data.get(index)
                    .clone() : new Value();
        }
        return this.get(new Value(index));
    }

    /**
     * Get the value at the given index.
     * 
     * @param index
     *            The index.
     * @param out
     *            The output Value.
     * @throws WeelException
     *             If the index is invalid.
     * @return out.
     */
    public Value get(final int index, final Value out)
    {
        if (this.ordered)
        {
            if (index >= 0 && index < this.size)
                this.data.get(index).copyTo(out);
            else
                out.setNull();
        }
        else
        {
            this.get(new Value(index), out);
        }
        return out;
    }

    /**
     * Get the value at the given index.
     * 
     * @param index
     *            The index.
     * @return The value or a Value of type NULL.
     * @throws WeelException
     *             If the index is invalid.
     */
    public Value get(final String index)
    {
        return this.get(new Value(index));
    }

    /**
     * Gets the value at the given index.
     * 
     * @param index
     *            The index.
     * @param out
     *            The output Value.
     * @throws WeelException
     *             If the index is invalid.
     */
    public void get(final Value index, final Value out)
    {
        if (index.type == ValueType.NUMBER)
        {
            final int idx = (int) index.number;
            if (this.ordered)
            {
                if (idx < 0 || idx >= this.size)
                    out.setNull();
                else
                    this.data.get(idx).copyTo(out);

            }
            else
            {
                final Integer idx2 = this.intKeys.get(idx);
                if (idx2 != null)
                    this.data.get(idx2).copyTo(out);
                else
                    out.setNull();
            }
        }
        else if (index.type == ValueType.STRING)
        {
            final Integer idx = this.strKeys.get(index.string);
            if (idx != null)
                this.data.get(idx).copyTo(out);
            else
                out.setNull();
        }
        else
        {
            throw new WeelException("Illegal map index type: " + index.type);
        }
    }

    /**
     * Check if this map contains the given key.
     * 
     * @param key
     *            The key
     * @return <code>true</code> if it contains the given key.
     */
    public boolean hasKey(final String key)
    {
        return this.hasKey(new Value(key));
    }

    /**
     * Check if this map contains the given key.
     * 
     * @param key
     *            The key
     * @return <code>true</code> if it contains the given key.
     */
    public boolean hasKey(final int key)
    {
        return this.hasKey(new Value(key));
    }

    /**
     * Check if this map contains the given key.
     * 
     * @param key
     *            The key
     * @return <code>true</code> if it contains the given key.
     */
    public boolean hasKey(final Value key)
    {
        if (key.type == ValueType.NUMBER)
        {
            final int idx = (int) key.number;
            if (this.ordered)
            {
                return idx >= 0 && idx < this.size;
            }
            return this.intKeys.containsKey(idx);
        }
        if (key.type == ValueType.STRING)
        {
            if (this.ordered)
                return false;
            return this.strKeys.containsKey(key.string);
        }
        throw new WeelException("Illegal map index type: " + key.type);
    }

    /**
     * Creates integer key mappings for ordered to unordered transition.
     */
    private void unorder()
    {
        this.ordered = false;
        // There can only be integer keys inside the map right now
        for (int i = 0; i < this.size; i++)
        {
            this.intKeys.put((int) this.keys.get(i).number, i);
        }
        this.highestIntKey = this.size - 1;
    }

    /**
     * Sets the value at the given index. Maps grow automatically.
     * 
     * @param index
     *            The index.
     * @param value
     *            The value.
     * @throws WeelException
     *             If the index is invalid.
     */
    public void set(final String index, final Value value)
    {
        this.set(new Value(index), value);
    }

    /**
     * Sets the value at the given index. Maps grow automatically.
     * 
     * @param index
     *            The index.
     * @param value
     *            The value.
     * @throws WeelException
     *             If the index is invalid.
     */
    public void set(final int index, final Value value)
    {
        this.set(new Value(index), value);
    }

    /**
     * Sets the value at the given index. Maps grow automatically.
     * 
     * @param index
     *            The index.
     * @param value
     *            The value.
     * @throws WeelException
     *             If the index is invalid.
     */
    public void set(final Value index, final Value value)
    {
        if (index.type == ValueType.NUMBER)
        {
            final int idx = (int) index.number;
            if (this.ordered && idx >= 0 && idx <= this.size)
            {
                if (idx == this.size)
                {
                    this.keys.add(new Value(idx));
                    this.data.add(value.clone());
                    this.size++;
                }
                else
                {
                    value.copyTo(this.data.get(idx));
                }
            }
            else
            {
                if (this.ordered)
                {
                    this.unorder();
                }
                final Integer idx2 = this.intKeys.get(idx);
                if (idx2 != null)
                {
                    value.copyTo(this.data.get(idx2));
                }
                else
                {
                    this.intKeys.put(idx, this.size);
                    this.keys.add(new Value(idx));
                    this.data.add(value.clone());
                    this.highestIntKey = idx;
                    this.size++;
                }
            }
        }
        else if (index.type == ValueType.STRING)
        {
            if (this.ordered)
            {
                this.unorder();
            }
            final Integer idx = this.strKeys.get(index.string);
            if (idx != null)
            {
                value.copyTo(this.data.get(idx));
            }
            else
            {
                this.strKeys.put(index.string, this.size);
                this.keys.add(index.clone());
                this.data.add(value.clone());
                this.size++;
            }
        }
        else
        {
            throw new WeelException("Illegal map index type: " + index.type);
        }
    }

    /**
     * Appends the given value to this map using an auto generated integer key.
     * 
     * @param value
     *            The value to append.
     */
    public void append(final Value value)
    {
        if (this.ordered)
        {
            this.keys.add(new Value(this.size));
            this.data.add(value.clone());
        }
        else
        {
            this.keys.add(new Value(++this.highestIntKey));
            this.intKeys.put(this.highestIntKey, this.size);
            this.data.add(value.clone());
        }
        this.size++;
    }

    /** @see java.lang.Object#clone() */
    @Override
    public ValueMap clone()
    {
        final ValueMap ret = new ValueMap();
        for (final Entry<Value, Value> e : this)
        {
            ret.set(e.getKey(), e.getValue().isMap() ? new Value(e.getValue()
                    .getMap().clone()) : e.getValue());
        }
        return ret;
    }

    /** @see java.lang.Iterable#iterator() */
    @Override
    public Iterator<Entry<Value, Value>> iterator()
    {
        return new ValueMapIterator(this);
    }

    /**
     * Entry implementation.
     * 
     * @author René Jeschke <rene_jeschke@yahoo.de>
     */
    private final static class ValueMapEntry implements Entry<Value, Value>
    {
        /** This Entry's key. */
        private final Value key;
        /** This Entry's value. */
        private Value value;

        /**
         * Constructor.
         * 
         * @param key
         *            The key.
         * @param value
         *            The value.
         */
        public ValueMapEntry(final Value key, final Value value)
        {
            this.key = key.clone();
            this.value = value.clone();
        }

        /** @see java.util.Map.Entry#getKey() */
        @Override
        public Value getKey()
        {
            return this.key;
        }

        /** @see java.util.Map.Entry#getValue() */
        @Override
        public Value getValue()
        {
            return this.value;
        }

        /** @see java.util.Map.Entry#setValue(Object) */
        @Override
        public Value setValue(Value value)
        {
            final Value old = this.value;
            this.value = value.clone();
            return old;
        }

    }

    /**
     * Iterator implementation.
     * 
     * @author René Jeschke <rene_jeschke@yahoo.de>
     */
    private final static class ValueMapIterator implements
            Iterator<Entry<Value, Value>>
    {
        /** The ValueMap. */
        private final ValueMap map;
        /** The current cursor position. */
        private int cursor = 0;

        /**
         * Constructor.
         * 
         * @param map
         *            The ValueMap.
         */
        public ValueMapIterator(final ValueMap map)
        {
            this.map = map;
        }

        /** @see java.util.Iterator#hasNext() */
        @Override
        public boolean hasNext()
        {
            return this.cursor != this.map.size;
        }

        /** @see java.util.Iterator#next() */
        @Override
        public Entry<Value, Value> next()
        {
            Entry<Value, Value> ret = new ValueMapEntry(this.map.keys
                    .get(this.cursor), this.map.data.get(this.cursor));
            this.cursor++;
            return ret;
        }

        /**
         * @see java.util.Iterator#remove()
         * @throws IllegalStateException
         *             ValueMaps can not be modified using iterators.
         */
        @Override
        public void remove()
        {
            throw new IllegalStateException("Can't modify ValueMaps.");
        }
    }

    /** @see java.lang.Object#toString() */
    @Override
    public String toString()
    {
        return "map(" + this.size + ")";
    }
}
