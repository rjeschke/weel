/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

import java.util.ArrayList;

/**
 * Weel runtime.
 * 
 * <p>
 * As you can see that most of the methods declared in Runtime are public. This
 * is by design to support the Weel compiling method.
 * </p>
 * <p>
 * You might say that this can cause serious trouble when someone doesn't know
 * what he or she is doing. Then I might answer: Your coders should know what
 * they are doing when writing Java methods for Weel, and the user should
 * normally only see the scripting API. So everything's shiny.
 * </p>
 * 
 * @author René Jeschke <rene_jeschke@yahoo.de>
 */
public final class Runtime
{
    /** Default size of the operand stack. */
    public final static int DEFAULT_VALUE_STACK_SIZE = 8192;
    /** Default size of the function frame stack. */
    public final static int DEFAULT_FRAME_STACK_SIZE = 4096;
    /** Default size of the virtual function stack. */
    public final static int DEFAULT_VIRTUAL_STACK_SIZE = 256;
    /** The creating Weel instance. */
    final Weel mother;
    /** The Weel stack. */
    private final Value[] stack = new Value[DEFAULT_VALUE_STACK_SIZE];
    /** Weel function frame start. */
    private final int[] frameStart = new int[DEFAULT_FRAME_STACK_SIZE];
    /** Weel function frame size. */
    private final int[] frameSize = new int[DEFAULT_FRAME_STACK_SIZE];
    /** Weel virtual function stack. */
    private final WeelFunction[] virtualFunctions = new WeelFunction[DEFAULT_VIRTUAL_STACK_SIZE];
    /** The Weel stack pointer. */
    private int sp = -1;
    /** The Weel frame pointer. */
    private int fp = -1;
    /** The Weel virtual function pointer. */
    private int vp = -1;
    /** Global variables. */
    private final ArrayList<Value> globals;
    /** Function list. */
    private final ArrayList<WeelFunction> functions;

    /**
     * Constructor.
     * 
     * @param weel
     *            The mother Weel.
     */
    Runtime(final Weel weel)
    {
        this.mother = weel;
        this.globals = weel.globals;
        this.functions = weel.functions;
        for (int i = 0; i < this.stack.length; i++)
            this.stack[i] = new Value();
    }

    /**
     * Gets the current stack pointer value.
     * 
     * @return The current stack pointer.
     */
    public int getStackPointer()
    {
        return this.sp;
    }

    /**
     * Pops <code>count</code> Values from the Weel stack.
     * 
     * @param count
     *            Number of values to pop.
     */
    public void pop(final int count)
    {
        this.sp -= count;
    }

    /**
     * Pops a Value from the Weel stack.
     * 
     * @return The popped Value.
     */
    public Value pop()
    {
        return this.stack[this.sp--].clone();
    }

    /**
     * Stores a Value into a local variable.
     * 
     * <p>
     * <code>..., value &rArr; ... </code>
     * </p>
     * 
     * @param var
     *            The index of the local variable.
     */
    public void sloc(final int var)
    {
        this.stack[this.sp--]
                .copyTo(this.stack[var + this.frameStart[this.fp]]);
    }

    /**
     * Loads a Value from a local variable.
     * 
     * <p>
     * <code>... &rArr; ..., value </code>
     * </p>
     * 
     * @param var
     *            The index of the local variable.
     */
    public void lloc(final int var)
    {
        this.stack[var + this.frameStart[this.fp]]
                .copyTo(this.stack[++this.sp]);
    }

    /**
     * Stores a Value into a global variable.
     * 
     * <p>
     * <code>..., value &rArr; ... </code>
     * </p>
     * 
     * @param index
     *            The index of the global variable.
     */
    public void sglob(final int index)
    {
        this.stack[this.sp--].copyTo(this.globals.get(index));
    }

    /**
     * Loads a Value from a global variable.
     * 
     * <p>
     * <code>... &rArr; ..., value </code>
     * </p>
     * 
     * @param index
     *            The index of the global variable.
     */
    public void lglob(final int index)
    {
        this.globals.get(index).copyTo(this.stack[++this.sp]);
    }

    /**
     * Loads a Value from an inherited environment.
     * 
     * <p>
     * <code>... &rArr; ..., value </code>
     * </p>
     * 
     * @param index
     *            The index of the variable.
     */
    public void linenv(final int index)
    {
        this.virtualFunctions[this.vp].environment[index]
                .copyTo(this.stack[++this.sp]);
    }

    /**
     * Stores a Value into a inherited environment.
     * 
     * <p>
     * <code>..., value &rArr; ... </code>
     * </p>
     * 
     * @param index
     *            The index of the variable.
     */
    public void sinenv(final int index)
    {
        this.stack[this.sp--]
                .copyTo(this.virtualFunctions[this.vp].environment[index]);
    }

    /**
     * Duplicates the value on top of the Weel stack.
     * 
     * <p>
     * <code>..., value &rArr; ..., value, value </code>
     * </p>
     */
    public void sdup()
    {
        this.stack[this.sp].copyTo(this.stack[this.sp + 1]);
        this.sp++;
    }

    /**
     * Compares two values.
     * 
     * <p>
     * <code>..., value1, value2 &rArr; ... </code>
     * </p>
     * 
     * @return An integer describing the compare result.
     * @throws WeelException
     *             If the values differ in type or type is not of NULL, NUMBER
     *             or STRING.
     */
    public int cmp()
    {
        final Value a = this.stack[this.sp - 1];
        final Value b = this.stack[this.sp];
        if (a.type != b.type)
            throw new WeelException("Incompatible values for comparison: "
                    + a.type + " <-> " + b.type);
        switch (a.type)
        {
        case NULL:
            return 0;
        case NUMBER:
            return Double.compare(a.number, b.number);
        case STRING:
            return a.string.compareTo(b.string);
        default:
            throw new WeelException("Can't compare values of type: " + a.type);
        }
    }

    /**
     * Compares two values for equality.
     * 
     * <p>
     * This is used for <code>==</code> and <code>!=</code>
     * <p>
     * <code>..., value1, value2 &rArr; ... </code>
     * </p>
     * 
     * @return <code>true</code> if the values are equal.
     */
    public boolean cmpEqual()
    {
        final Value a = this.stack[this.sp - 1];
        final Value b = this.stack[this.sp];
        if (a.type != b.type)
            return false;
        switch (a.type)
        {
        case NULL:
            return true;
        case NUMBER:
            return a.number == b.number;
        case STRING:
            return a.string.equals(b.string);
        case MAP:
            return a.map.equals(b.map);
        case FUNCTION:
            return a.function.equals(b.function);
        case OBJECT:
            return a.object.equals(b.object);
        }
        return false;
    }

    /**
     * Ends a for loop.
     * 
     * @param var
     *            For loop variable index.
     * @return <code>true</code> if the loop must be continued.
     * @throws WeelException
     *             If the for variable is not a number.
     */
    public boolean endForLoop(final int var)
    {
        final double step = this.stack[this.sp].number;
        final double value = this.stack[var + this.frameStart[this.fp]].number += step;
        return step < 0 ? value >= this.stack[this.sp - 1].number
                : value <= this.stack[this.sp - 1].number;
    }

    /**
     * Begins a for loop.
     * 
     * @param var
     *            For loop variable index.
     * @return <code>true</code> if the loop must be executed.
     * @throws WeelException
     *             If the for variable is not a number.
     */
    public boolean beginForloop(final int var)
    {
        final int index = var + this.frameStart[this.fp];
        final double step = this.stack[this.sp].number;
        final double lim = this.stack[this.sp - 1].number;
        final Value value = this.stack[index];
        if (value.type != ValueType.NUMBER)
            throw new WeelException("FOR variable must be a number.");
        return step < 0 ? value.number >= lim : value.number <= lim;
    }

    /**
     * Addition.
     * 
     * <p>
     * <code>..., value1, value2 &rArr; ..., value1 + value2 </code>
     * </p>
     */
    public void add()
    {
        final Value b = this.stack[this.sp--];
        final Value a = this.stack[this.sp];
        a.number += b.number;
    }

    /**
     * Subtraction.
     * 
     * <p>
     * <code>..., value1, value2 &rArr; ..., value1 - value2 </code>
     * </p>
     */
    public void sub()
    {
        final Value b = this.stack[this.sp--];
        final Value a = this.stack[this.sp];
        a.number -= b.number;
    }

    /**
     * Multiplication.
     * 
     * <p>
     * <code>..., value1, value2 &rArr; ..., value1 * value2 </code>
     * </p>
     */
    public void mul()
    {
        final Value b = this.stack[this.sp--];
        final Value a = this.stack[this.sp];
        a.number *= b.number;
    }

    /**
     * Division.
     * 
     * <p>
     * <code>..., value1, value2 &rArr; ..., value1 / value2 </code>
     * </p>
     */
    public void div()
    {
        final Value b = this.stack[this.sp--];
        final Value a = this.stack[this.sp];
        a.number /= b.number;
    }

    /**
     * Binary and.
     * 
     * <p>
     * <code>..., value1, value2 &rArr; ..., value1 &amp; value2 </code>
     * </p>
     * 
     * <p>
     * All binary operations in Weel are performed on 32 Bit integer. As Weel
     * uses (like most other weakly-types languages) double as its number type,
     * and doubles only have 52(+1) Bits mantissa, only 32 Bit binary integer
     * arithmetics makes sense.
     * </p>
     */
    public void and()
    {
        final Value b = this.stack[this.sp--];
        final Value a = this.stack[this.sp];
        a.number = (int) a.number & (int) b.number;
    }

    /**
     * Binary or.
     * 
     * <p>
     * <code>..., value1, value2 &rArr; ..., value1 | value2 </code>
     * </p>
     * 
     * @see #and()
     */
    public void or()
    {
        final Value b = this.stack[this.sp--];
        final Value a = this.stack[this.sp];
        a.number = (int) a.number | (int) b.number;
    }

    /**
     * Binary exclusive or.
     * 
     * <p>
     * <code>..., value1, value2 &rArr; ..., value1 ^ value2 </code>
     * </p>
     * 
     * @see #and()
     */
    public void xor()
    {
        final Value b = this.stack[this.sp--];
        final Value a = this.stack[this.sp];
        a.number = (int) a.number ^ (int) b.number;
    }

    /**
     * Binary not.
     * 
     * <p>
     * <code>..., value &rArr; ..., ~value</code>
     * </p>
     * 
     * @see #and()
     */
    public void not()
    {
        final Value a = this.stack[this.sp];
        a.number = ~(int) a.number;
    }

    /**
     * Logical not.
     * 
     * <p>
     * <code>..., value &rArr; ..., !toBoolean(value)</code>
     * </p>
     */
    public void lnot()
    {
        this.stack[this.sp].number = this.stack[this.sp].toBoolean() ? 0 : -1;
        this.stack[this.sp].type = ValueType.NUMBER;
    }

    /**
     * Loads a float value onto the Weel stack.
     * 
     * <p>
     * <code>... &rArr; ..., value</code>
     * </p>
     * 
     * @param value
     *            Float value to load.
     */
    public void load(final float value)
    {
        this.stack[++this.sp].type = ValueType.NUMBER;
        this.stack[this.sp].number = value;
    }

    /**
     * Loads a double value onto the Weel stack.
     * 
     * <p>
     * <code>... &rArr; ..., value</code>
     * </p>
     * 
     * @param value
     *            Double value to load.
     */
    public void load(final double value)
    {
        this.stack[++this.sp].type = ValueType.NUMBER;
        this.stack[this.sp].number = value;
    }

    /**
     * Loads an integer value onto the Weel stack.
     * 
     * <p>
     * <code>... &rArr; ..., value</code>
     * </p>
     * 
     * @param value
     *            Integer value to load.
     */
    public void load(final int value)
    {
        this.stack[++this.sp].type = ValueType.NUMBER;
        this.stack[this.sp].number = value;
    }

    /**
     * Loads a String value onto the Weel stack.
     * 
     * <p>
     * <code>... &rArr; ..., value</code>
     * </p>
     * 
     * @param value
     *            String value to load.
     */
    public void load(final String value)
    {
        this.stack[++this.sp].type = ValueType.STRING;
        this.stack[this.sp].string = value;
    }

    /**
     * Loads a ValueMap value onto the Weel stack.
     * 
     * <p>
     * <code>... &rArr; ..., value</code>
     * </p>
     * 
     * @param value
     *            ValueMap value to load.
     */
    public void load(final ValueMap value)
    {
        this.stack[++this.sp].type = ValueType.MAP;
        this.stack[this.sp].map = value;
    }

    /**
     * Loads a function value onto the Weel stack.
     * 
     * <p>
     * <code>... &rArr; ..., value</code>
     * </p>
     * 
     * @param value
     *            Function value to load.
     */
    public void load(final WeelFunction value)
    {
        this.stack[++this.sp].type = ValueType.FUNCTION;
        this.stack[this.sp].function = value;
    }

    /**
     * Loads an object value onto the Weel stack.
     * 
     * <p>
     * <code>... &rArr; ..., value</code>
     * </p>
     * 
     * @param value
     *            Object value to load.
     */
    public void load(final Object value)
    {
        this.stack[++this.sp].type = ValueType.OBJECT;
        this.stack[this.sp].object = value;
    }

    /**
     * Opens a function frame.
     * 
     * @param args
     *            Number of arguments.
     * @param locals
     *            Number of locals.
     */
    public void openFrame(final int args, final int locals)
    {
        this.frameStart[++this.fp] = this.sp - args + 1;
        this.frameSize[this.fp] = args + locals;
        this.sp += locals;
    }

    /**
     * Closes a function frame.
     */
    public void closeFrame()
    {
        // TODO maybe nullify stack values?
        this.sp = this.frameStart[this.fp--];
    }

    /**
     * Closes a function frame taking care of the return value.
     */
    public void closeFrameRet()
    {
        // TODO maybe nullify stack values?
        // If no return value is given, NULL gets returned
        if (this.sp - this.frameStart[this.fp] == this.frameSize[this.fp])
            this.stack[++this.sp].setNull();
        this.stack[this.sp].copyTo(this.stack[this.frameStart[this.fp]]);
        this.sp -= this.frameSize[this.fp--];
    }

    /**
     * Gets an instance of a non-static Java method call.
     * 
     * @param index
     *            The function index.
     * @return The instance.
     */
    public Object getFunctionInstance(final int index)
    {
        return this.functions.get(index).instance;
    }

    /**
     * Calls a Weel function defined by a value on the stack.
     * 
     * <p>
     * <code>..., function, [arguments] &rArr; ..., [return value]</code>
     * </p>
     * 
     * @param args
     *            Number of arguments.
     * @param shouldReturn
     *            Flag indicating that the context expects a return value.
     * @throws WeelException
     *             If the target Value is not a function or the number of
     *             arguments doesn't match.
     */
    public void stackCall(final int args, final boolean shouldReturn)
    {
        final WeelFunction func = this.stack[this.sp - args].getFunction();

        if (func.arguments != args)
            throw new WeelException("Argument count mismatch, expected " + args
                    + " got " + func.arguments);

        func.invoke(this);
        // Check return value
        if (func.returnsValue)
        {
            if (shouldReturn)
            {
                this.stack[this.sp].copyTo(this.stack[this.sp - 1]);
                --this.sp;
            }
            else
            {
                this.sp -= 2;
            }
        }
        else if (shouldReturn)
        {
            this.stack[this.sp].setNull();
        }
        else
        {
            --this.sp;
        }
    }

    /**
     * Creates a map.
     * 
     * <p>
     * <code>... &rArr; ..., map</code>
     * </p>
     */
    public void createMap()
    {
        this.stack[++this.sp].type = ValueType.MAP;
        this.stack[this.sp].map = new ValueMap();
    }

    /**
     * Creates an ordered map of the given size.
     * 
     * <p>
     * <code>... &rArr; ..., map</code>
     * </p>
     */
    public void createMap(final int size)
    {
        this.stack[++this.sp].type = ValueType.MAP;
        this.stack[this.sp].map = new ValueMap(size);
    }

    /**
     * Gets a value from a map.
     * 
     * <p>
     * <code>..., map, index &rArr; ..., value</code>
     * </p>
     * 
     * @throws WeelException
     *             If the 'map' is not a ValueMap.
     */
    public void getMap()
    {
        final ValueMap map = this.stack[this.sp - 1].getMap();
        map.get(this.stack[this.sp], this.stack[this.sp - 1]);
        --this.sp;
    }

    /**
     * Sets a value in a map.
     * 
     * <p>
     * <code>..., map, index, value &rArr; ...</code>
     * </p>
     * 
     * @throws WeelException
     *             If the 'map' is not a ValueMap.
     */
    public void setMap()
    {
        final ValueMap map = this.stack[this.sp - 2].getMap();
        map.set(this.stack[this.sp - 1], this.stack[this.sp]);
        this.sp -= 3;
    }

    /**
     * Returns the Weel instance which created this Runtime.
     * 
     * @return The Weel instance.
     */
    // Are you my mummy?
    public Weel getMother()
    {
        return this.mother;
    }

    /**
     * Gets a number value from the stack.
     * <p>
     * Only used in wrapper methods.
     * </p>
     * 
     * @param offset
     *            Stack offset.
     * @return The number.
     */
    public double getNumberRelative(final int offset)
    {
        return this.stack[this.sp + offset].getNumber();
    }

    /**
     * Gets a String value from the stack.
     * <p>
     * Only used in wrapper methods.
     * </p>
     * 
     * @param offset
     *            Stack offset.
     * @return The String.
     */
    public String getStringRelative(final int offset)
    {
        return this.stack[this.sp + offset].getString();
    }

    /**
     * Gets a String value from the stack.
     * <p>
     * Only used in wrapper methods.
     * </p>
     * 
     * @param offset
     *            Stack offset.
     * @return The String or <code>null</code> if Value is of type NULL.
     */
    public String getStringOrNullRelative(final int offset)
    {
        final Value value = this.stack[this.sp + offset];
        return value.type == ValueType.NULL ? null : this.stack[this.sp
                + offset].getString();
    }

    /**
     * Gets a ValueMap value from the stack.
     * <p>
     * Only used in wrapper methods.
     * </p>
     * 
     * @param offset
     *            Stack offset.
     * @return The ValueMap.
     */
    public ValueMap getMapRelative(final int offset)
    {
        return this.stack[this.sp + offset].getMap();
    }

    /**
     * Gets a ValueMap value from the stack.
     * <p>
     * Only used in wrapper methods.
     * </p>
     * 
     * @param offset
     *            Stack offset.
     * @return The ValueMap or <code>null</code> if Value is of type NULL.
     */
    public ValueMap getMapOrNullRelative(final int offset)
    {
        final Value value = this.stack[this.sp + offset];
        return value.type == ValueType.NULL ? null : this.stack[this.sp
                + offset].getMap();
    }

    /**
     * Gets a function value from the stack.
     * <p>
     * Only used in wrapper methods.
     * </p>
     * 
     * @param offset
     *            Stack offset.
     * @return The WeelFunction.
     */
    public WeelFunction getFunctionRelative(final int offset)
    {
        return this.stack[this.sp + offset].getFunction();
    }

    /**
     * Gets a function value from the stack.
     * <p>
     * Only used in wrapper methods.
     * </p>
     * 
     * @param offset
     *            Stack offset.
     * @return The WeelFunction or <code>null</code> if Value is of type NULL.
     */
    public WeelFunction getFunctionOrNullRelative(final int offset)
    {
        final Value value = this.stack[this.sp + offset];
        return value.type == ValueType.NULL ? null : this.stack[this.sp
                + offset].getFunction();
    }

    /**
     * Gets an object value from the stack.
     * <p>
     * Only used in wrapper methods.
     * </p>
     * 
     * @param offset
     *            Stack offset.
     * @return The Object.
     */
    public Object getObjectRelative(final int offset)
    {
        return this.stack[this.sp + offset].getObject();
    }

    /**
     * Gets an object value from the stack.
     * <p>
     * Only used in wrapper methods.
     * </p>
     * 
     * @param offset
     *            Stack offset.
     * @return The Object or <code>null</code> if Value is of type NULL.
     */
    public Object getObjectOrNullRelative(final int offset)
    {
        final Value value = this.stack[this.sp + offset];
        return value.type == ValueType.NULL ? null : this.stack[this.sp
                + offset].getObject();
    }

    /**
     * Creates a virtual function from a static anonymous function.
     * 
     * @param index
     *            The function index.
     */
    public void createVirtual(final int index)
    {
        this.stack[++this.sp].type = ValueType.FUNCTION;
        this.stack[this.sp].function = this.mother.functions.get(index)
                .cloneVirtual(this);
    }

    /**
     * Initializes a virtual function. Called from invokers only.
     * 
     * @param function
     *            The virtual function.
     */
    void initVirtual(final WeelFunction function)
    {
        this.virtualFunctions[++this.vp] = function;
    }

    /**
     * Ends a virtual function. Called from invokers only.
     */
    void exitVirtual()
    {
        // We nullify to give the GC a chance to collect the virtual function
        this.virtualFunctions[this.vp--] = null;
    }

    /**
     * Gets the clone of a local variable. Used in closures only.
     * 
     * @param var
     *            The index of the local variable.
     * @return The clone of the local variable.
     */
    Value gloc(final int var)
    {
        return this.stack[var + this.frameStart[this.fp]].clone();
    }

    /**
     * Gets the clone of a closure variable. Used in closures only.
     * 
     * @param var
     *            The index of the closure variable.
     * @return The clone of the closure variable.
     */
    Value ginenv(final int var)
    {
        return this.virtualFunctions[this.vp].environment[var];
    }
}
