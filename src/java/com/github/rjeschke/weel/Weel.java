/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.github.rjeschke.weel.annotations.WeelClass;
import com.github.rjeschke.weel.annotations.WeelRawMethod;
import com.github.rjeschke.weel.annotations.WeelMethod;
import com.github.rjeschke.weel.jclass.WeelImage;
import com.github.rjeschke.weel.jclass.WeelIo;
import com.github.rjeschke.weel.jclass.WeelLock;
import com.github.rjeschke.weel.jclass.WeelReader;
import com.github.rjeschke.weel.jclass.WeelSemaphore;
import com.github.rjeschke.weel.jclass.WeelStringBuilder;
import com.github.rjeschke.weel.jclass.WeelBlockingQueue;
import com.github.rjeschke.weel.jclass.WeelSyncVar;
import com.github.rjeschke.weel.jclass.WeelThread;
import com.github.rjeschke.weel.jclass.WeelWriter;

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
    /** Default size of the operand stack. */
    public final static int DEFAULT_VALUE_STACK_SIZE = 8192;
    /** Default size of the function frame stack. */
    public final static int DEFAULT_FRAME_STACK_SIZE = 4096;
    /** Default size of the closure function stack. */
    public final static int DEFAULT_CLOSURE_STACK_SIZE = 256;
    /** Global variables. */
    final ArrayList<Value> globals = new ArrayList<Value>();
    /** Private variables. */
    final ArrayList<Value> privates = new ArrayList<Value>();
    /** Function list. */
    final ArrayList<WeelFunction> functions = new ArrayList<WeelFunction>();
    /** Name to global variable index mapping. */
    final HashMap<String, Integer> mapGlobals = new HashMap<String, Integer>();
    /** Name to function index mapping. */
    final HashMap<String, Integer> mapFunctions = new HashMap<String, Integer>();
    /** Name to exact function index mapping. */
    final HashMap<String, Integer> mapFunctionsExact = new HashMap<String, Integer>();
    /** Weel class loader. */
    final WeelLoader classLoader = new WeelLoader();
    /** Compiled script classes. */
    final ArrayList<String> scriptClasses = new ArrayList<String>();
    /** Type bound support functions. */
    final TypeFunctions[] typeFunctions = new TypeFunctions[6];
    /** Counter for wrapper classes. */
    final static AtomicLong wrapperCounter = new AtomicLong();
    /** Counter for script classes. */
    final static AtomicLong scriptCounter = new AtomicLong();
    /** Debug mode flag. */
    boolean debugMode = false;
    /** Debug mode flag. */
    boolean dumpCode = false;
    /** Default size of the operand stack. */
    final int valueStackSize;
    /** Default size of the function frame stack. */
    final int frameStackSize;
    /** Default size of the closure function stack. */
    final int closureStackSize;

    private final static Class<?>[] STDLIB =
    { WeelLibMath.class, WeelLibString.class, WeelLibCon.class,
            WeelLibMap.class, WeelLibOop.class, WeelLibSys.class,
            WeelUnit.class };

    private final static Class<?>[] JCLASSES =
    { WeelStringBuilder.class, WeelImage.class, WeelThread.class,
            WeelLock.class, WeelSemaphore.class, WeelBlockingQueue.class,
            WeelSyncVar.class, WeelIo.class, WeelReader.class, WeelWriter.class };

    /** ThreadLocal variable for Weel Runtimes associated with this Weel class. */
    private final ThreadLocal<WeelRuntime> runtime = new ThreadLocal<WeelRuntime>()
    {
        /** @see java.lang.ThreadLocal#initialValue() */
        @Override
        protected WeelRuntime initialValue()
        {
            return new WeelRuntime(Weel.this);
        }
    };

    /**
     * Constructor.
     */
    public Weel()
    {
        this(DEFAULT_VALUE_STACK_SIZE, DEFAULT_FRAME_STACK_SIZE,
                DEFAULT_CLOSURE_STACK_SIZE);
    }

    /**
     * Constructor.
     * 
     * @param stackSize
     *            Size of the operand stack in slots.
     * @param fStackSize
     *            Size of the frame stack in slots.
     * @param cStackSize
     *            Size of the closure function stack in slots.
     */
    public Weel(final int stackSize, final int fStackSize, final int cStackSize)
    {
        this.valueStackSize = stackSize;
        this.frameStackSize = fStackSize;
        this.closureStackSize = cStackSize;

        // Import standard library
        for (final Class<?> c : STDLIB)
        {
            this.importFunctions(c);
        }
        // Import classes
        for (final Class<?> c : JCLASSES)
        {
            this.importFunctions(c);
        }
        // Initialize type functions
        for (int i = 0; i < this.typeFunctions.length; i++)
        {
            this.typeFunctions[i] = new TypeFunctions();
        }
    }

    /**
     * Enables or disables debug mode. When set to <code>true</code> asserts get
     * compiled, otherwise they get skipped.
     * 
     * @param enable
     *            On or off?
     */
    public void setDebugMode(final boolean enable)
    {
        this.debugMode = enable;
    }

    /**
     * Enables or disables dumping of generated intermediate byte code.
     * 
     * @param enable
     *            On or off?
     */
    public void enableCodeDump(final boolean enable)
    {
        this.dumpCode = enable;
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

        return this.getRuntime().invoke(function, args);
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
        return this.getRuntime().invoke(function, args);
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
     */
    public void compile(final InputStream input)
    {
        final Compiler compiler = new Compiler(this);
        compiler.compile(input, null);
    }

    /**
     * Compiles the given input stream.
     * 
     * @param input
     *            The input stream.
     * @param filename
     *            The filename used in error messages.
     */
    public void compile(final InputStream input, final String filename)
    {
        final Compiler compiler = new Compiler(this);
        compiler.compile(input, filename);
    }

    /**
     * Compiles a file given as a Java resource.
     * 
     * @param resource
     *            The resource (e.g. <code>my.scripts.Script</code>)
     */
    public void compileResource(final String resource)
    {
        final Compiler compiler = new Compiler(this);
        final InputStream in = Weel.class.getResourceAsStream("/"
                + resource.replace('.', '/') + ".weel");
        compiler.compile(in, resource);
        try
        {
            in.close();
        }
        catch (IOException e)
        {
            throw new WeelException(e);
        }
    }

    /**
     * Runs the static part of all compiled scripts.
     */
    public void runStatic()
    {
        final WeelRuntime runtime = this.getRuntime();

        for (final String name : this.scriptClasses)
        {
            try
            {
                final Class<?> clazz = this.classLoader.findClass(name);
                clazz.getMethod("STATIC", WeelRuntime.class).invoke(null,
                        runtime);
            }
            catch (Exception e)
            {
                if (e instanceof WeelException)
                    throw (WeelException) e;
                if (e.getCause() != null
                        && e.getCause() instanceof WeelException)
                    throw (WeelException) e.getCause();
                throw new WeelException(e);
            }
        }
    }

    /**
     * Runs the main function/sub (if exists).
     * 
     * @return The return value of 'main' or null.
     */
    public Value runMain()
    {
        return this.runMain((String[]) null);
    }

    /**
     * Runs the main function/sub (if exists).
     * 
     * @param args
     *            Program arguments.
     * @return The return value of 'main' or null.
     */
    public Value runMain(final String... args)
    {
        final WeelFunction main = this.findFunction("main");
        final ValueMap vargs = new ValueMap();
        if (args != null)
        {
            for (final String s : args)
            {
                vargs.append(new Value(s));
            }
        }
        if (main != null)
        {
            if (main.arguments > 1)
            {
                throw new WeelException("main() should only take one argument");
            }
            try
            {
                return main.arguments == 1 ? this.invoke(main, vargs) : this
                        .invoke(main);
            }
            catch (Exception e)
            {
                if (e instanceof WeelException)
                    throw (WeelException) e;
                if (e.getCause() != null
                        && e.getCause() instanceof WeelException)
                    throw (WeelException) e.getCause();
                throw new WeelException(e);
            }
        }
        return null;
    }

    /**
     * Gets a Runtime object local to the current Thread.
     * 
     * @return A Runtime.
     * @see com.github.rjeschke.weel.WeelRuntime
     * @see java.lang.ThreadLocal
     */
    public WeelRuntime getRuntime()
    {
        return this.runtime.get();
    }

    /**
     * Gets a temporary Runtime object for optimization purposes.
     * 
     * @return A Runtime.
     * @see com.github.rjeschke.weel.WeelRuntime
     */
    WeelRuntime getTempRuntime()
    {
        return new WeelRuntime(this);
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
        ValueMap map;
        final String prefix;
        MethodWrapper mw = null;

        if (wclass != null)
        {
            map = new ValueMap();
            final String clazzName = wclass.name().length() > 0 ? wclass.name()
                    .toLowerCase() : clazz.getSimpleName().toLowerCase();
            prefix = clazzName + (wclass.usesOop() ? "$$" : "$");
            if (wclass.isPrivate() && clazzName.indexOf('.') == -1)
            {
                //
                try
                {
                    final Field me = clazz.getDeclaredField("ME");
                    me.set(null, map);
                }
                catch (Exception e)
                {
                    throw new WeelException("Can't access 'ME'", e);
                }
            }
            else
            {
                if (clazzName.indexOf('.') != -1)
                {
                    final String[] toks = clazzName.split("[.]");
                    final int g = this.hasGlobal(toks[0]) ? this.mapGlobals
                            .get(toks[0]) : this.addGlobal(toks[0]);

                    ValueMap cur;

                    if (this.globals.get(g).type != ValueType.MAP)
                    {
                        this.globals.set(g, new Value(cur = new ValueMap()));
                    }
                    else
                    {
                        cur = this.globals.get(g).getMap();
                    }

                    for (int i = 1; i < toks.length; i++)
                    {
                        ValueMap next;
                        if (!cur.hasKey(toks[i]))
                        {
                            cur.set(toks[i], new Value(next = new ValueMap()));
                        }
                        else
                        {
                            next = cur.get(toks[i]).getMap();
                        }
                        cur = next;
                    }
                    map = cur;
                }
                else
                {
                    if (this.hasGlobal(clazzName))
                    {
                        throw new WeelException(
                                "Duplicate global variable for Weel clazz: "
                                        + clazzName);
                    }
                    final int g = this.addGlobal(clazzName);
                    this.globals.set(g, new Value(map));
                }
            }
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

            if ((m.getModifiers() & Modifier.STATIC) == 0)
            {
                throw new WeelException("Weel only supports static functions: "
                        + m);
            }

            final WeelFunction func = new WeelFunction();
            if (raw != null)
            {
                if (m.getParameterTypes().length != 1
                        || m.getReturnType() != void.class
                        || m.getParameterTypes()[0] != WeelRuntime.class)
                    throw new WeelException("Illegal raw Weel function: "
                            + m.toGenericString());

                final String fname = (raw.name().length() > 0 ? raw.name() : m
                        .getName()).toLowerCase();
                func.name = prefix + fname;

                func.arguments = raw.args();
                func.returnsValue = raw.returnsValue();

                func.clazz = clazz.getCanonicalName();
                func.javaName = m.getName();

                final String iname = func.name + "#" + func.arguments;
                if (this.mapFunctionsExact.containsKey(iname))
                    throw new WeelException("Duplicate function: " + func.name
                            + "(" + func.arguments + ") ["
                            + m.toGenericString() + "]");

                this.addFunction(iname, func);

                if (map != null)
                {
                    map.set(fname, new Value(func));
                }
            }
            else if (nice != null)
            {
                if (mw == null)
                {
                    mw = new MethodWrapper("Wrap$" + clazz.getSimpleName()
                            + "$" + wrapperCounter.getAndIncrement());
                }

                final String fname = (nice.name().length() > 0 ? nice.name()
                        : m.getName()).toLowerCase();
                func.name = prefix + fname;

                func.arguments = m.getParameterTypes().length;
                func.returnsValue = m.getReturnType() != void.class;

                func.clazz = mw.getClassName();
                func.javaName = mw.wrap(m, func);

                final String iname = func.name + "#" + func.arguments;
                if (this.mapFunctionsExact.containsKey(iname))
                    throw new WeelException("Duplicate function: " + func.name
                            + "(" + func.arguments + ") ["
                            + m.toGenericString() + "]");

                this.addFunction(iname, func);

                if (map != null)
                {
                    map.set(fname, new Value(func));
                }
            }
        }

        if (mw != null && mw.classWriter.hasMethods())
        {
            this.classLoader.addClass(mw.classWriter);
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

    /**
     * Registeres a private variable.
     * 
     * @return The index.
     */
    int registerPrivate()
    {
        final int i = this.privates.size();
        this.privates.add(new Value());
        return i;
    }
}
