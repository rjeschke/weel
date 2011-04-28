/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

import java.util.Arrays;

/**
 * A specialized ByteList for bytecode writing.
 * 
 * @author René Jeschke <rene_jeschke@yahoo.de>
 */
final class ByteList
{
    /** Initial size. */
    private final static int INITIAL_SIZE = 128;
    /** Growth factor. */
    private final static int GROWTH_FACTOR = 125;
    /** The array. */
    private byte[] array;
    /** The size. */
    private int size = 0;

    /**
     * Constructor.
     */
    public ByteList()
    {
        this.array = new byte[INITIAL_SIZE];
    }

    /**
     * Constructor.
     * 
     * @param initialSize
     *            Initial size.
     */
    public ByteList(final int initialSize)
    {
        this.array = new byte[initialSize];
    }

    /**
     * Gets the size of this ByteList.
     * 
     * @return The size in bytes.
     */
    public int size()
    {
        return this.size;
    }

    /**
     * Clears this ByteList by setting its size to <code>0</code>.
     */
    public void clear()
    {
        this.size = 0;
    }

    /**
     * Grows this ByteList's backing array.
     */
    private void grow()
    {
        this.array = Arrays.copyOf(this.array,
                (this.array.length * GROWTH_FACTOR) / 100);
    }

    /**
     * Adds a byte value to this ByteList.
     * 
     * @param value
     *            The byte value to add.
     */
    public void add(final int value)
    {
        if (this.array.length == this.size)
            this.grow();
        this.array[this.size++] = (byte) value;
    }

    /**
     * Adds a short (big endian) value to this ByteList.
     * 
     * @param value
     *            The short value to add.
     */
    public void addShort(final int value)
    {
        while (this.size + 2 >= this.array.length)
            this.grow();
        this.array[this.size + 0] = (byte) (value >> 8);
        this.array[this.size + 1] = (byte) value;
        this.size += 2;
    }

    /**
     * Adds an integer (big endian) value to this ByteList.
     * 
     * @param value
     *            The integer value to add.
     */
    public void addInteger(final int value)
    {
        while (this.size + 4 >= this.array.length)
            this.grow();
        this.array[this.size + 0] = (byte) (value >> 24);
        this.array[this.size + 1] = (byte) (value >> 16);
        this.array[this.size + 2] = (byte) (value >> 8);
        this.array[this.size + 3] = (byte) value;
        this.size += 4;
    }

    /**
     * Adds a long (big endian) value to this ByteList.
     * 
     * @param value
     *            The long value to add.
     */
    public void addLong(final long value)
    {
        while (this.size + 8 >= this.array.length)
            this.grow();
        this.array[this.size + 0] = (byte) (value >> 56L);
        this.array[this.size + 1] = (byte) (value >> 48L);
        this.array[this.size + 2] = (byte) (value >> 40L);
        this.array[this.size + 3] = (byte) (value >> 32L);
        this.array[this.size + 4] = (byte) (value >> 24L);
        this.array[this.size + 5] = (byte) (value >> 16L);
        this.array[this.size + 6] = (byte) (value >> 8L);
        this.array[this.size + 7] = (byte) value;
        this.size += 8;
    }

    /**
     * Sets a byte a the given index.
     * 
     * @param index
     *            The index.
     * @param value
     *            The byte value.
     */
    public void set(final int index, final int value)
    {
        this.array[index] = (byte) value;
    }

    /**
     * Sets a short a the given index.
     * 
     * @param index
     *            The index.
     * @param value
     *            The short value.
     */
    public void setShort(final int index, final int value)
    {
        this.array[index + 0] = (byte) (value >> 8);
        this.array[index + 1] = (byte) value;
    }

    /**
     * Sets an integer a the given index.
     * 
     * @param index
     *            The index.
     * @param value
     *            The integer value.
     */
    public void setInteger(final int index, final int value)
    {
        this.array[index + 0] = (byte) (value >> 24);
        this.array[index + 1] = (byte) (value >> 16);
        this.array[index + 2] = (byte) (value >> 8);
        this.array[index + 3] = (byte) value;
    }

    /**
     * Sets a long a the given index.
     * 
     * @param index
     *            The index.
     * @param value
     *            The long value.
     */
    public void setLong(final int index, final long value)
    {
        this.array[index + 0] = (byte) (value >> 56L);
        this.array[index + 1] = (byte) (value >> 48L);
        this.array[index + 2] = (byte) (value >> 40L);
        this.array[index + 3] = (byte) (value >> 32L);
        this.array[index + 4] = (byte) (value >> 24L);
        this.array[index + 5] = (byte) (value >> 16L);
        this.array[index + 6] = (byte) (value >> 8L);
        this.array[index + 7] = (byte) value;
    }

    /**
     * Gets the byte at the given index.
     * 
     * @param index
     *            The index.
     * @return The byte at the given position.
     */
    public byte get(final int index)
    {
        return this.array[index];
    }

    /**
     * Gets the byte at the given index as an unsigned value.
     * 
     * @param index
     *            The index.
     * @return The byte at the given position.
     */
    public int getUnsigned(final int index)
    {
        return this.array[index] & 255;
    }

    /**
     * Returns a copy of this ByteLists's backing array truncated to the correct
     * size.
     * 
     * @return The array.
     */
    public byte[] toArray()
    {
        return Arrays.copyOf(this.array, this.size);
    }
}
