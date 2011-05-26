/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * Weel map implementation.
 * 
 * @author René Jeschke <rene_jeschke@yahoo.de>
 */
public final class ValueMap
{
    /** Integer keys. */
    private HashMap<Integer, Integer> intKeys = new HashMap<Integer, Integer>();
    /** String keys. */
    private HashMap<String, Integer> strKeys = new HashMap<String, Integer>();
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
            final Integer idx2 = this.intKeys.get(index);
            if (idx2 != null)
                this.data.get(idx2).copyTo(out);
            else
                out.setNull();
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
        return this.get(index, new Value());
    }

    /**
     * Get the value at the given index.
     * 
     * @param index
     *            The index.
     * @param out
     *            The output Value.
     * @return out.
     * @throws WeelException
     *             If the index is invalid.
     */
    public Value get(final String index, final Value out)
    {
        if (this.ordered)
        {
            out.setNull();
        }
        else
        {
            final Integer idx2 = this.strKeys.get(index);
            if (idx2 != null)
                this.data.get(idx2).copyTo(out);
            else
                out.setNull();
        }
        return out;
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
            final Integer idx = this.strKeys.get(index.object);
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
            return this.strKeys.containsKey(key.object);
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
            this.keys.add(new Value(i));
            this.intKeys.put(i, i);
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
        if (this.ordered)
        {
            this.unorder();
        }
        final Integer idx = this.strKeys.get(index);
        if (idx != null)
        {
            value.copyTo(this.data.get(idx));
        }
        else
        {
            this.strKeys.put(index, this.size);
            this.keys.add(new Value(index));
            this.data.add(value.clone());
            this.size++;
        }
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
        if (this.ordered && index >= 0 && index <= this.size)
        {
            if (index == this.size)
            {
                this.data.add(value.clone());
                this.size++;
            }
            else
            {
                value.copyTo(this.data.get(index));
            }
        }
        else
        {
            if (this.ordered)
            {
                this.unorder();
            }
            final Integer index2 = this.intKeys.get(index);
            if (index2 != null)
            {
                value.copyTo(this.data.get(index2));
            }
            else
            {
                this.intKeys.put(index, this.size);
                this.keys.add(new Value(index));
                this.data.add(value.clone());
                this.highestIntKey = index;
                this.size++;
            }
        }
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
            final Integer idx = this.strKeys.get(index.object);
            if (idx != null)
            {
                value.copyTo(this.data.get(idx));
            }
            else
            {
                this.strKeys.put((String)index.object, this.size);
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
        final Value k = new Value();
        final Value v = new Value();
        for (final ValueMapIterator i = new ValueMapIterator(this); i
                .next(k, v);)
        {
            ret.set(k, v.isMap() ? new Value(v.getMap().clone()) : v);
        }
        return ret;
    }

    private void remap(final int r)
    {
        for(final Entry<Integer, Integer> e : this.intKeys.entrySet())
        {
            final int i = e.getValue();
            if(i > r)
            {
                this.intKeys.put(e.getKey(), i - 1);
            }
        }
        for(final Entry<String, Integer> e : this.strKeys.entrySet())
        {
            final int i = e.getValue();
            if(i > r)
            {
                this.strKeys.put(e.getKey(), i - 1);
            }
        }
    }
    
    /**
     * Removes the last entry in this map.
     */
    public Value removeLast()
    {
        if(this.size < 1)
            return new Value();
        
        this.size--;
        final Value rem = this.data.remove(this.size);
        if(!this.ordered)
        {
            final Value k = this.keys.remove(this.size);
            this.data.remove(this.size);
            if(k.type == ValueType.NUMBER)
            {
                this.intKeys.remove((int)k.number);
            }
            else
            {
                this.strKeys.remove(k.object);
            }
        }
        return rem;
    }
    
    public void remove(final Value index)
    {
        if(index.type == ValueType.NUMBER)
        {
            final int idx = (int) index.number;
            if(this.ordered)
            {
                if(idx < 0 || idx >= this.size)
                    return;
                if(idx + 1 == this.size)
                {
                    this.size--;
                    this.data.remove(this.size);
                    return;
                }
                this.unorder();
            }
            final Integer idx2 = this.intKeys.get(idx);
            if(idx2 != null)
            {
                final int r = idx2;
                this.data.remove(r);
                this.keys.remove(r);
                this.intKeys.remove(idx);
                this.remap(r);
            }
        }
        else if(index.type == ValueType.STRING)
        {
            if(this.ordered)
                return;
            
            final Integer idx2 = this.strKeys.get(index.object);
            if(idx2 != null)
            {
                final int r = idx2;
                this.data.remove(r);
                this.keys.remove(r);
                this.strKeys.remove(index.object);
                this.remap(r);
            }
        }
        else
        {
            throw new WeelException("Illegal map index type: " + index.type);
        }
    }
    
    /**
     * Reverses this map.
     * 
     * @return This map.
     */
    public ValueMap reverse()
    {
        // FIXME ... hä?
        Collections.reverse(this.data);
        Collections.reverse(this.keys);

        return this;
    }

    /**
     * Creates an iterator.
     * 
     * @return The iterator.
     */
    public ValueMapIterator createIterator()
    {
        return new ValueMapIterator(this);
    }

    /**
     * Iterator implementation.
     * 
     * @author René Jeschke <rene_jeschke@yahoo.de>
     */

    public final static class ValueMapIterator
    {
        /** The ValueMap. */
        private final ValueMap map;
        /** Current cursor. */
        private int cursor = 0;

        /**
         * Constructor.
         * 
         * @param map
         *            The ValueMap to iterate over.
         */
        ValueMapIterator(final ValueMap map)
        {
            this.map = map;
        }

        /**
         * Gets the next key-value pair.
         * 
         * @param key
         *            The key.
         * @param value
         *            The value.
         * @return <code>false</code> if there are no more elements.
         */
        public boolean next(final Value key, final Value value)
        {
            if (this.cursor < this.map.size)
            {
                if (this.map.ordered)
                {
                    key.type = ValueType.NUMBER;
                    key.number = this.cursor;
                }
                else
                {
                    this.map.keys.get(this.cursor).copyTo(key);
                }
                this.map.data.get(this.cursor++).copyTo(value);
                return true;
            }
            return false;
        }
    }

    /** @see java.lang.Object#toString() */
    @Override
    public String toString()
    {
        return "map(" + this.size + ")";
    }
}
