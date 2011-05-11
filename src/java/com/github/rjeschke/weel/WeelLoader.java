/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Simple ClassLoader implementation for loading compiled Weel classes.
 * 
 * @author René Jeschke <rene_jeschke@yahoo.de>
 */
final class WeelLoader extends ClassLoader
{
    /** HashMap containing class name to class mapping. */
    private HashMap<String, Class<?>> classes = new HashMap<String, Class<?>>();
    /** Class data. */
    ArrayList<ClassData> classData = new ArrayList<ClassData>();

    /**
     * Constructor.
     */
    public WeelLoader()
    {
        super(WeelLoader.class.getClassLoader());
    }

    /**
     * Adds the given class to this ClassLoader.
     * 
     * @param name
     *            The name of the class.
     * @param code
     *            The bytecode.
     * @return The ready-to-use class.
     */
    public Class<?> addClass(final String name, final byte[] code)
    {
        if (this.classes.containsKey(name))
        {
            throw new WeelException("Duplicate class in WeelLoader: " + name);
        }
        this.classData.add(new ClassData(name, code));
        final Class<?> clazz = this.defineClass(name, code, 0, code.length);
        this.classes.put(name, clazz);
        return clazz;
    }

    /**
     * Adds the given class to this ClassLoader.
     * 
     * @param writer
     *            The class writer.
     * @return The ready-to-use class.
     */
    public Class<?> addClass(final JvmClassWriter writer)
    {
        return this.addClass(writer.className, writer.build());
    }

    /** @see java.lang.ClassLoader#findClass(String) */
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException
    {
        final Class<?> clazz = this.classes.get(name);
        if (clazz == null)
            throw new ClassNotFoundException(name);
        return clazz;
    }

    /**
     * Class data.
     * 
     * @author René Jeschke <rene_jeschke@yahoo.de>
     */
    static class ClassData
    {
        /** The class name. */
        final String name;
        /** The code. */
        final byte[] code;

        /**
         * Constructor.
         * 
         * @param name
         *            The name.
         * @param code
         *            The code.
         */
        public ClassData(final String name, final byte[] code)
        {
            this.name = name;
            this.code = code;
        }
    }
}
