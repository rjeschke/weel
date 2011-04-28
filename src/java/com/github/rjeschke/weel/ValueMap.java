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
public final class ValueMap implements Iterable<Entry<Value, Value>>
{
    private Map<Integer, Integer> intKeys = new HashMap<Integer, Integer>();
    private Map<String, Integer> strKeys = new HashMap<String, Integer>();
    int size;
    private boolean ordered = true;
    ArrayList<Value> data;
    ArrayList<Value> keys;

    public ValueMap()
    {
        this.data = new ArrayList<Value>();
        this.keys = new ArrayList<Value>();
    }

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
}
