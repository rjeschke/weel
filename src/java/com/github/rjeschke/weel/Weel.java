/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;

import com.github.rjeschke.weel.annotations.WeelClass;
import com.github.rjeschke.weel.annotations.WeelRawMethod;
import com.github.rjeschke.weel.annotations.WeelMethod;

/**
 * Main Weel class.
 * <p>
 * Example usage:
 * </p>
 * 
 * <pre>
 * <code>final Weel weel = new Weel();
 * weel.compile("println('Hello world!')");
 * weel.runStatic();</code>
 * </pre>
 * 
 * @author René Jeschke <rene_jeschke@yahoo.de>
 */
public final class Weel
{
    /** Global variables. */
    ArrayList<Value> globals = new ArrayList<Value>();
    /** Function list. */
    ArrayList<WeelFunction> functions = new ArrayList<WeelFunction>();
    /** Name to global variable index mapping. */
    HashMap<String, Integer> mapGlobals = new HashMap<String, Integer>();
    /** Name to function index mapping. */
    HashMap<String, Integer> mapFunctions = new HashMap<String, Integer>();
    /** Name to exact function index mapping. */
    HashMap<String, Integer> mapFunctionsExact = new HashMap<String, Integer>();
    /** Weel class loader. */
    final WeelLoader classLoader = new WeelLoader();
    /** Compiled script classes. */
    final ArrayList<String> scriptClasses = new ArrayList<String>();
    /** Type bound support functions. */
    final TypeFunctions[] typeFunctions = new TypeFunctions[5];

    /** ThreadLocal variable for Weel Runtimes associated with this Weel class. */
    private final ThreadLocal<Runtime> runtime = new ThreadLocal<Runtime>()
    {
        @Override
        protected Runtime initialValue()
        {
            return new Runtime(Weel.this);
        }
    };

    /**
     * Constructor.
     * 
     * <p>
     * Imports standard library functions.
     * </p>
     */
    public Weel()
    {
        this.importFunctions(WeelLibCon.class);
        this.importFunctions(WeelLibMath.class);
        this.importFunctions(WeelLibSys.class);
        this.importFunctions(WeelLibMap.class);
        this.importFunctions(WeelLibString.class);
        this.importFunctions(WeelUnit.class);

        this.typeFunctions[ValueType.STRING.ordinal()] = new TypeFunctions();
        this.typeFunctions[ValueType.MAP.ordinal()] = new TypeFunctions();
        this.typeFunctions[ValueType.FUNCTION.ordinal()] = new TypeFunctions();
    }

    /**
     * Invokes the Weel function with the given name and arguments.
     * 
     * @param functionName
     *            The function name.
     * @param args
     *            The arguments.
     * @return A Value if the functions returns a value, <code>null</code>
     *         otherwise.
     * @throws WeelException
     *             if the function does not exist.
     */
    public Value invoke(final String functionName, Object... args)
    {
        final WeelFunction function = this.findFunction(functionName,
                args.length);
        if (function == null)
        {
            throw new WeelException("Unknown function '" + functionName + "'("
                    + args.length + ")");
        }
        return this.invoke(function, args);
    }

    /**
     * Invokes the Weel function with the given name and arguments.
     * 
     * @param function
     *            The function.
     * @param args
     *            The arguments.
     * @return A Value if the functions returns a value, <code>null</code>
     *         otherwise.
     * @throws WeelException
     *             if the function argument count does not match the supplied
     *             argument count.
     */
    public Value invoke(final WeelFunction function, Object... args)
    {
        final Runtime rt = this.getRuntime();
        if (args.length != function.arguments)
        {
            throw new WeelException("Argument count mismatch");
        }

        for (final Object o : args)
        {
            if (o == null)
            {
                rt.load();
                continue;
            }
            final Class<?> oc = o.getClass();
            if (oc == Double.class)
            {
                rt.load((double) ((Double) o));
            }
            else if (oc == Float.class)
            {
                rt.load((float) ((Float) o));
            }
            else if (oc == Integer.class)
            {
                rt.load((int) ((Integer) o));
            }
            else if (oc == Short.class)
            {
                rt.load((int) ((Short) o));
            }
            else if (oc == Byte.class)
            {
                rt.load((int) ((Byte) o));
            }
            else if (oc == Character.class)
            {
                rt.load((int) ((Character) o));
            }
            else if (oc == String.class)
            {
                rt.load((String) o);
            }
            else if (oc == ValueMap.class)
            {
                rt.load((ValueMap) o);
            }
            else
            {
                rt.load(o);
            }
        }

        function.invoke(rt);

        if (function.returnsValue)
            return rt.pop();

        return null;
    }

    /**
     * Compiles the given input String.
     * 
     * @param input
     *            The input String.
     */
    public void compile(final String input)
    {
        final Compiler compiler = new Compiler(this);
        compiler.compile(input, null);
    }

    /**
     * Compiles the given input String.
     * 
     * @param input
     *            The input String.
     * @param filename
     *            The filename used in error messages.
     */
    public void compile(final String input, final String filename)
    {
        final Compiler compiler = new Compiler(this);
        compiler.compile(input, filename);
    }

    /**
     * Compiles the given input stream.
     * 
     * @param input
     *            The input stream.
     * @return DEBUG purpose only. Will get removed.
     */
    public byte[] compile(final InputStream input)
    {
        final Compiler compiler = new Compiler(this);
        compiler.compile(input, null);
        return compiler.classWriter.build();
    }

    /**
     * Compiles the given input stream.
     * 
     * @param input
     *            The input stream.
     * @param filename
     *            The filename used in error messages.
     * @return DEBUG purpose only. Will get removed.
     */
    public byte[] compile(final InputStream input, final String filename)
    {
        final Compiler compiler = new Compiler(this);
        compiler.compile(input, filename);
        return compiler.classWriter.build();
    }

    /**
     * Runs the static part of all compiled scripts.
     */
    public void runStatic()
    {
        final Runtime runtime = this.getRuntime();

        for (final String name : this.scriptClasses)
        {
            try
            {
                final Class<?> clazz = this.classLoader.findClass(name);
                clazz.getMethod("STATIC", Runtime.class).invoke(null, runtime);
            }
            catch (ClassNotFoundException e)
            {
                throw new WeelException(e);
            }
            catch (IllegalArgumentException e)
            {
                throw new WeelException(e);
            }
            catch (SecurityException e)
            {
                throw new WeelException(e);
            }
            catch (IllegalAccessException e)
            {
                throw new WeelException(e);
            }
            catch (InvocationTargetException e)
            {
                if (e.getCause() instanceof WeelException)
                    throw (WeelException) e.getCause();
                throw new WeelException(e);
            }
            catch (NoSuchMethodException e)
            {
                throw new WeelException(e);
            }
        }
    }

    /**
     * Gets a Runtime object local to the current Thread.
     * 
     * @return A Runtime.
     * @see com.github.rjeschke.weel.Runtime
     * @see java.lang.ThreadLocal
     */
    public Runtime getRuntime()
    {
        return this.runtime.get();
    }

    /**
     * Gets a global variable.
     * 
     * @param name
     *            The name.
     * @return The Value.
     * @throws WeelException
     *             If no global variable with the given name exists.
     */
    public Value getGlobal(final String name)
    {
        final Integer index = this.mapGlobals.get(name.toLowerCase());
        if (index == null)
            throw new WeelException("Unknown global variable '" + name + "'");
        return this.globals.get(index).clone();
    }

    /**
     * Sets a global variable.
     * 
     * @param name
     *            The name.
     * @param value
     *            The value.
     * @throws WeelException
     *             If no global variable with the given name exists.
     */
    public void setGlobal(final String name, final Value value)
    {
        final Integer index = this.mapGlobals.get(name.toLowerCase());
        if (index == null)
            throw new WeelException("Unknown global variable '" + name + "'");
        value.copyTo(this.globals.get(index));
    }

    /**
     * Creates a new global variable.
     * 
     * @param name
     *            The name.
     * @throws WeelException
     *             If a global variable with the given name already exists.
     */
    public void createGlobal(final String name)
    {
        if (this.mapGlobals.containsKey(name.toLowerCase()))
            throw new WeelException("Duplicate global variable '" + name + "'");
        this.mapGlobals.put(name.toLowerCase(), this.globals.size());
        this.globals.add(new Value());
    }

    /**
     * Adds a global variable to this Weel.
     * 
     * @param name
     *            The name.
     * @return The index.
     */
    int addGlobal(final String name)
    {
        final int index = this.globals.size();
        this.mapGlobals.put(name.toLowerCase(), index);
        this.globals.add(new Value());
        return index;
    }

    /**
     * Checks if a global variable with the given name exists.
     * 
     * @param name
     *            The name.
     * @return <code>true</code> if the global variable exists.
     */
    public boolean hasGlobal(final String name)
    {
        return this.mapGlobals.containsKey(name.toLowerCase());
    }

    /**
     * Finds the given function.
     * 
     * @param name
     *            The function's name.
     * @return The WeelFunction or <code>null</code> if none was found.
     */
    public WeelFunction findFunction(final String name)
    {
        final Integer index = this.mapFunctions.get(name.toLowerCase());
        return index != null ? this.functions.get(index) : null;
    }

    /**
     * Finds the given function.
     * 
     * @param name
     *            The function's name.
     * @param args
     *            The number of arguments.
     * @return The WeelFunction or <code>null</code> if none was found.
     */
    public WeelFunction findFunction(final String name, final int args)
    {
        final Integer index = this.mapFunctionsExact.get(name.toLowerCase()
                + "#" + args);
        return index != null ? this.functions.get(index) : null;
    }

    /**
     * Adds the given function to this Weel's function list.
     * 
     * @param iname
     *            The internal name.
     * @param func
     *            The function.
     */
    void addFunction(final String iname, final WeelFunction func)
    {
        this.addFunction(iname, func, true);
    }

    /**
     * Adds the given function to this Weel's function list.
     * 
     * @param iname
     *            The internal name.
     * @param func
     *            The function.
     * @param addToHash
     *            Add it to the function name hash map?
     */
    void addFunction(final String iname, final WeelFunction func,
            final boolean addToHash)
    {
        final int index = this.functions.size();
        func.index = index;
        this.functions.add(func);
        if (addToHash)
        {
            this.mapFunctions.put(func.name, index);
            this.mapFunctionsExact.put(iname, index);
        }
    }

    /**
     * Imports a class with static Weel functions.
     * 
     * @param clazz
     *            The class to import.
     */
    public void importFunctions(Class<?> clazz)
    {
        final Method[] methods = clazz.getDeclaredMethods();
        final WeelClass wclass = clazz.getAnnotation(WeelClass.class);
        final ValueMap map;
        final String prefix;
        
        if(wclass != null)
        {
            final String clazzName = wclass.name().length() > 0 ? wclass.name().toLowerCase() : clazz.getSimpleName().toLowerCase();
            if(this.hasGlobal(clazzName))
            {
                throw new WeelException("Duplicate global variable for Weel clazz: " + clazzName);
            }
            final int g = this.addGlobal(clazzName);
            map = new ValueMap();
            this.globals.set(g, new Value(map));
            prefix = clazzName + (wclass.usesOop() ? "$$" : "$");
        }
        else
        {
            map = null;
            prefix = "";
        }
        
        for (int i = 0; i < methods.length; i++)
        {
            final Method m = methods[i];
            final WeelRawMethod raw = m.getAnnotation(WeelRawMethod.class);
            final WeelMethod nice = m.getAnnotation(WeelMethod.class);
            if (raw != null)
            {
                if (m.getParameterTypes().length != 1
                        || m.getReturnType() != void.class
                        || m.getParameterTypes()[0] != Runtime.class)
                    throw new WeelException("Illegal raw Weel function: "
                            + m.toGenericString());

                final WeelFunction func = new WeelFunction();
                final String fname = (raw.name().length() > 0 ? raw.name() : m.getName()).toLowerCase();
                func.name = prefix + fname;
                
                func.arguments = raw.args();
                func.returnsValue = raw.returnsValue();

                func.clazz = clazz.getCanonicalName();
                func.javaName = m.getName();

                if ((m.getModifiers() & Modifier.STATIC) == 0)
                {
                    throw new WeelException("Weel only supports static functions: " + m);
                }

                final String iname = func.name + "#" + func.arguments;
                if (this.mapFunctionsExact.containsKey(iname))
                    throw new WeelException("Duplicate function: " + func.name
                            + "(" + func.arguments + ") ["
                            + m.toGenericString() + "]");

                this.addFunction(iname, func);
                func.invoker = WeelInvokerFactory.create();
                func.initialize(this);
                
                if(map != null)
                {
                    map.set(fname, new Value(func));
                }
            }
            else if (nice != null)
            {
                /*
                 * Supported types: - Primitives: - double - Objects - String -
                 * ValueMap - Objects (with checkcast)
                 */
                // TODO nice
            }
        }
    }

    /**
     * Initializes all invokers.
     */
    void initAllInvokers()
    {
        for (final WeelFunction func : this.functions)
        {
            if (func.invoker == null)
                func.invoker = WeelInvokerFactory.create();
            func.initialize(this);
        }
    }
}
