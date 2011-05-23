/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

import java.util.ArrayList;

import com.github.rjeschke.weel.ValueMap.ValueMapIterator;

/**
 * Weel runtime.
 * 
 * <p>
 * As you can see most of the methods declared in Runtime are public. This is by
 * design to support the Weel compiling method.
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
public final class WeelRuntime
{
    /** The creating Weel instance. */
    final Weel mother;
    /** The Weel stack. */
    private final Value[] stack;
    /** Weel function frame start. */
    private final int[] frameStart;
    /** Weel function frame size. */
    private final int[] frameSize;
    /** Weel closure function stack. */
    private final WeelFunction[] closureFunctions;
    /** The Weel stack pointer. */
    private int sp = -1;
    /** The Weel frame pointer. */
    private int fp = -1;
    /** The Weel virtual function pointer. */
    private int vp = -1;
    /** Global variables. */
    private final ArrayList<Value> globals;
    /** Private variables. */
    private final ArrayList<Value> privates;
    /** Type bound support functions. */
    private final TypeFunctions[] typeFunctions;

    /**
     * Constructor.
     * 
     * @param weel
     *            The mother Weel.
     */
    WeelRuntime(final Weel weel)
    {
        this.mother = weel;
        this.globals = weel.globals;
        this.privates = weel.privates;
        this.typeFunctions = weel.typeFunctions;

        this.stack = new Value[weel.valueStackSize];
        this.frameStart = new int[weel.frameStackSize];
        this.frameSize = new int[weel.frameStackSize];
        this.closureFunctions = new WeelFunction[weel.closureStackSize];
        
        for (int i = 0; i < this.stack.length; i++)
        {
            this.stack[i] = new Value();
        }
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
     * Pops <code>1</code> Value from the Weel stack.
     */
    public void pop1()
    {
        this.sp--;
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
     * Stores a Value into a private variable.
     * 
     * <p>
     * <code>..., value &rArr; ... </code>
     * </p>
     * 
     * @param index
     *            The index of the private variable.
     */
    public void spriv(final int index)
    {
        this.stack[this.sp--].copyTo(this.privates.get(index));
    }

    /**
     * Loads a Value from a private variable.
     * 
     * <p>
     * <code>... &rArr; ..., value </code>
     * </p>
     * 
     * @param index
     *            The index of the private variable.
     */
    public void lpriv(final int index)
    {
        this.privates.get(index).copyTo(this.stack[++this.sp]);
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
        this.closureFunctions[this.vp].environment[index]
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
                .copyTo(this.closureFunctions[this.vp].environment[index]);
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
        final Value a = this.stack[this.sp];
        a.copyTo(this.stack[++this.sp]);
    }

    /**
     * Duplicates two values on top of the Weel stack.
     * 
     * <p>
     * <code>..., value1, value2 &rArr; ..., value1, value2, value1, value2 </code>
     * </p>
     */
    public void sdup2()
    {
        this.stack[this.sp - 1].copyTo(this.stack[this.sp + 1]);
        this.stack[this.sp].copyTo(this.stack[this.sp + 2]);
        this.sp += 2;
    }

    /**
     * Duplicates the value on top of the Weel stack and moves it three places
     * up.
     * 
     * <p>
     * <code>..., value1, value2 value3, &rArr; ..., value3, value1, value2, value3 </code>
     * </p>
     */
    public void sdups()
    {
        this.stack[this.sp].copyTo(this.stack[this.sp + 1]);
        this.stack[this.sp - 1].copyTo(this.stack[this.sp]);
        this.stack[this.sp - 2].copyTo(this.stack[this.sp - 1]);
        this.stack[this.sp + 1].copyTo(this.stack[this.sp - 2]);
        this.sp++;
    }

    /**
     * Test the stack top as a boolean. Pops the stack if the boolean is false.
     * 
     * @return <code>true</code> if the value is true.
     */
    public boolean testPopTrue()
    {
        if (this.stack[this.sp].toBoolean())
            return true;
        this.sp--;
        return false;
    }

    /**
     * Test the stack top as a boolean. Pops the stack if the boolean is true.
     * 
     * @return <code>true</code> if the value is false.
     */
    public boolean testPopFalse()
    {
        if (!this.stack[this.sp].toBoolean())
            return true;
        this.sp--;
        return false;
    }

    /**
     * Pops the top of the stack and returns the popped value as a boolean;
     * 
     * <p>
     * <code>..., value &rArr; ... </code>
     * </p>
     * 
     * @return The boolean representation of the popped value.
     */
    public boolean popBoolean()
    {
        return this.stack[this.sp--].toBoolean();
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
    int cmp()
    {
        final Value a = this.stack[this.sp - 1];
        final Value b = this.stack[this.sp];
        this.sp -= 2;
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
            return ((String) a.object).compareTo((String) b.object);
        default:
            throw new WeelException("Can't compare values of type: " + a.type);
        }
    }

    /**
     * Compares for equality.
     * 
     * <p>
     * <code>..., value1, value2 &rArr; ..., result </code>
     * </p>
     */
    public void cmpEq()
    {
        final boolean res = this.cmpEqual();
        this.stack[++this.sp].type = ValueType.NUMBER;
        this.stack[this.sp].number = res ? -1 : 0;
    }

    /**
     * Compares for equality.
     * 
     * <p>
     * <code>..., value &rArr; ..., result </code>
     * </p>
     * 
     * @param operand
     *            Number to compare to.
     */
    public void cmpEq(final double operand)
    {
        final Value a = this.stack[this.sp];
        a.number = Double.compare(a.number, operand) == 0 ? -1 : 0;
        a.type = ValueType.NUMBER;
    }

    /**
     * Compares for inequality.
     * 
     * <p>
     * <code>..., value1, value2 &rArr; ..., result </code>
     * </p>
     */
    public void cmpNe()
    {
        final boolean res = this.cmpEqual();
        this.stack[++this.sp].type = ValueType.NUMBER;
        this.stack[this.sp].number = res ? 0 : -1;
    }

    /**
     * Compares for inequality.
     * 
     * <p>
     * <code>..., value &rArr; ..., result </code>
     * </p>
     * 
     * @param operand
     *            Number to compare to.
     */
    public void cmpNe(final double operand)
    {
        final Value a = this.stack[this.sp];
        a.number = Double.compare(a.number, operand) == 0 ? 0 : -1;
        a.type = ValueType.NUMBER;
    }

    /**
     * Compares for greater than.
     * 
     * <p>
     * <code>..., value1, value2 &rArr; ..., result </code>
     * </p>
     */
    public void cmpGt()
    {
        final int res = this.cmp();
        this.stack[++this.sp].type = ValueType.NUMBER;
        this.stack[this.sp].number = res > 0 ? -1 : 0;
    }

    /**
     * Compares for greater than.
     * 
     * <p>
     * <code>..., value &rArr; ..., result </code>
     * </p>
     * 
     * @param operand
     *            Number to compare to.
     */
    public void cmpGt(final double operand)
    {
        final Value a = this.stack[this.sp];
        a.number = Double.compare(a.number, operand) > 0 ? -1 : 0;
        a.type = ValueType.NUMBER;
    }

    /**
     * Compares for greater or equal.
     * 
     * <p>
     * <code>..., value1, value2 &rArr; ..., result </code>
     * </p>
     */
    public void cmpGe()
    {
        final int res = this.cmp();
        this.stack[++this.sp].type = ValueType.NUMBER;
        this.stack[this.sp].number = res >= 0 ? -1 : 0;
    }

    /**
     * Compares for greater or equal.
     * 
     * <p>
     * <code>..., value &rArr; ..., result </code>
     * </p>
     * 
     * @param operand
     *            Number to compare to.
     */
    public void cmpGe(final double operand)
    {
        final Value a = this.stack[this.sp];
        a.number = Double.compare(a.number, operand) >= 0 ? -1 : 0;
        a.type = ValueType.NUMBER;
    }

    /**
     * Compares for less than.
     * 
     * <p>
     * <code>..., value1, value2 &rArr; ..., result </code>
     * </p>
     */
    public void cmpLt()
    {
        final int res = this.cmp();
        this.stack[++this.sp].type = ValueType.NUMBER;
        this.stack[this.sp].number = res < 0 ? -1 : 0;
    }

    /**
     * Compares for less than.
     * 
     * <p>
     * <code>..., value &rArr; ..., result </code>
     * </p>
     * 
     * @param operand
     *            Number to compare to.
     */
    public void cmpLt(final double operand)
    {
        final Value a = this.stack[this.sp];
        a.number = Double.compare(a.number, operand) < 0 ? -1 : 0;
        a.type = ValueType.NUMBER;
    }

    /**
     * Compares for less or equal.
     * 
     * <p>
     * <code>..., value1, value2 &rArr; ..., result </code>
     * </p>
     */
    public void cmpLe()
    {
        final int res = this.cmp();
        this.stack[++this.sp].type = ValueType.NUMBER;
        this.stack[this.sp].number = res <= 0 ? -1 : 0;
    }

    /**
     * Compares for less or equal.
     * 
     * <p>
     * <code>..., value &rArr; ..., result </code>
     * </p>
     * 
     * @param operand
     *            Number to compare to.
     */
    public void cmpLe(final double operand)
    {
        final Value a = this.stack[this.sp];
        a.number = Double.compare(a.number, operand) <= 0 ? -1 : 0;
        a.type = ValueType.NUMBER;
    }

    /**
     * Compares for equality.
     * 
     * <p>
     * <code>..., value1, value2 &rArr; ... </code>
     * </p>
     * 
     * @return <code>true</code> if condition is met.
     */
    public boolean cmpEqPop()
    {
        return this.cmpEqual();
    }

    /**
     * Compares for equality.
     * 
     * <p>
     * <code>..., value &rArr; ... </code>
     * </p>
     * 
     * @param operand
     *            Value to compare to.
     * @return <code>true</code> if condition is met.
     */
    public boolean cmpEqPop(final double operand)
    {
        return Double.compare(this.stack[this.sp--].number, operand) == 0;
    }

    /**
     * Compares for inequality.
     * 
     * <p>
     * <code>..., value1, value2 &rArr; ... </code>
     * </p>
     * 
     * @return <code>true</code> if condition is met.
     */
    public boolean cmpNePop()
    {
        return !this.cmpEqual();
    }

    /**
     * Compares for inequality.
     * 
     * <p>
     * <code>..., value &rArr; ... </code>
     * </p>
     * 
     * @param operand
     *            Value to compare to.
     * @return <code>true</code> if condition is met.
     */
    public boolean cmpNePop(final double operand)
    {
        return Double.compare(this.stack[this.sp--].number, operand) != 0;
    }

    /**
     * Compares for greater than.
     * 
     * <p>
     * <code>..., value1, value2 &rArr; ... </code>
     * </p>
     * 
     * @return <code>true</code> if condition is met.
     */
    public boolean cmpGtPop()
    {
        return this.cmp() > 0;
    }

    /**
     * Compares for greater than.
     * 
     * <p>
     * <code>..., value &rArr; ... </code>
     * </p>
     * 
     * @param operand
     *            Value to compare to.
     * @return <code>true</code> if condition is met.
     */
    public boolean cmpGtPop(final double operand)
    {
        return Double.compare(this.stack[this.sp--].number, operand) > 0;
    }

    /**
     * Compares for greater or equal.
     * 
     * <p>
     * <code>..., value1, value2 &rArr; ... </code>
     * </p>
     * 
     * @return <code>true</code> if condition is met.
     */
    public boolean cmpGePop()
    {
        return this.cmp() >= 0;
    }

    /**
     * Compares for greater or equal.
     * 
     * <p>
     * <code>..., value &rArr; ... </code>
     * </p>
     * 
     * @param operand
     *            Value to compare to.
     * @return <code>true</code> if condition is met.
     */
    public boolean cmpGePop(final double operand)
    {
        return Double.compare(this.stack[this.sp--].number, operand) >= 0;
    }

    /**
     * Compares for less than.
     * 
     * <p>
     * <code>..., value1, value2 &rArr; ... </code>
     * </p>
     * 
     * @return <code>true</code> if condition is met.
     */
    public boolean cmpLtPop()
    {
        return this.cmp() < 0;
    }

    /**
     * Compares for less than.
     * 
     * <p>
     * <code>..., value &rArr; ... </code>
     * </p>
     * 
     * @param operand
     *            Value to compare to.
     * @return <code>true</code> if condition is met.
     */
    public boolean cmpLtPop(final double operand)
    {
        return Double.compare(this.stack[this.sp--].number, operand) < 0;
    }

    /**
     * Compares for less or equal.
     * 
     * <p>
     * <code>..., value1, value2 &rArr; ... </code>
     * </p>
     * 
     * @return <code>true</code> if condition is met.
     */
    public boolean cmpLePop()
    {
        return this.cmp() <= 0;
    }

    /**
     * Compares for less or equal.
     * 
     * <p>
     * <code>..., value &rArr; ... </code>
     * </p>
     * 
     * @param operand
     *            Value to compare to.
     * @return <code>true</code> if condition is met.
     */
    public boolean cmpLePop(final double operand)
    {
        return Double.compare(this.stack[this.sp--].number, operand) <= 0;
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
        this.sp -= 2;
        if (a.type != b.type)
            return false;
        switch (a.type)
        {
        case NULL:
            return true;
        case NUMBER:
            return a.number == b.number;
        case STRING:
            return ((String) a.object).equals(b.object);
        case MAP:
            return ((ValueMap) a.object).equals(b.object);
        case FUNCTION:
            return ((WeelFunction) a.object).equals(b.object);
        case OBJECT:
            return a.object.equals(b.object);
        }
        return false;
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
    public boolean beginForLoop(final int var)
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
     * Ends a for loop.
     * 
     * @param var
     *            For loop variable index.
     * @return <code>true</code> if the loop must be continued.
     */
    public boolean endForLoop(final int var)
    {
        final double step = this.stack[this.sp].number;
        final double value = this.stack[var + this.frameStart[this.fp]].number += step;
        return step < 0 ? value >= this.stack[this.sp - 1].number
                : value <= this.stack[this.sp - 1].number;
    }

    /**
     * Prepares a foreach loop.
     * 
     * <p>
     * <code>..., map &rArr; ..., iterator </code>
     * </p>
     */
    public void prepareForEach()
    {
        final ValueMap map = this.stack[this.sp].getMap();
        this.stack[this.sp].type = ValueType.OBJECT;
        this.stack[this.sp].object = map.createIterator();
    }

    /**
     * Performs a foreach loop interation.
     * 
     * <p>
     * <code>..., iterator &rArr; ..., iterator, key, value </code>
     * </p>
     * 
     * @return <code>false</code> if there are no more elements.
     */
    public boolean doForEach()
    {
        final ValueMapIterator iterator = (ValueMapIterator) this.stack[this.sp]
                .getObject();
        if (!iterator.next(this.stack[this.sp + 1], this.stack[this.sp + 2]))
            return false;
        this.sp += 2;
        return true;
    }

    /**
     * String concatenation.
     * 
     * <p>
     * <code>..., value1, value2 &rArr; ..., value1 + value2 </code>
     * </p>
     */
    public void strcat()
    {
        final Value b = this.stack[this.sp--];
        final Value a = this.stack[this.sp];
        a.object = a.toString() + b.toString();
        a.type = ValueType.STRING;
    }

    /**
     * Map concatenation.
     * <p>
     * When both maps are 'lists' then the result is a 'real' concatenation,
     * else a merge will be performed.
     * </p>
     * 
     * <p>
     * <code>..., map1, map2 &rArr; ..., map1 ++ map2 </code>
     * </p>
     */
    public void mapcat()
    {
        final ValueMap b = this.popMap();
        final ValueMap a = this.popMap();
        final ValueMap c = new ValueMap();

        if (a.ordered && b.ordered)
        {
            for (int i = 0; i < a.size; i++)
            {
                c.append(a.data.get(i));
            }
            for (int i = 0; i < b.size; i++)
            {
                c.append(b.data.get(i));
            }
        }
        else
        {
            ValueMapIterator it;
            final Value k = new Value(), v = new Value();
            for (it = a.createIterator(); it.next(k, v);)
            {
                c.set(k, v);
            }
            for (it = b.createIterator(); it.next(k, v);)
            {
                c.set(k, v);
            }
        }
        this.stack[++this.sp].type = ValueType.MAP;
        this.stack[this.sp].object = c;
    }

    /**
     * Map concatenation.
     * <p>
     * When both maps are 'lists' then the result is a 'real' concatenation,
     * else a merge will be performed. This instruction modifies 'map1' instead
     * of returning a new map.
     * </p>
     * 
     * <p>
     * <code>..., map1, map2 &rArr; ..., map1 ++= map2 </code>
     * </p>
     */
    public void mapcat2()
    {
        final ValueMap b = this.popMap();
        final ValueMap a = this.stack[this.sp].getMap();

        if (a.ordered && b.ordered)
        {
            final Value v = new Value();
            for (int i = 0; i < b.size; i++)
            {
                a.append(b.get(i, v));
            }
        }
        else
        {

            final Value k = new Value(), v = new Value();
            for (final ValueMapIterator it = b.createIterator(); it.next(k, v);)
            {
                a.set(k, v);
            }
        }
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
     * Addition.
     * 
     * <p>
     * <code>..., value &rArr; ..., value + operand</code>
     * </p>
     * 
     * @param operand
     *            Value to add.
     */
    public void add(final double operand)
    {
        this.stack[this.sp].number += operand;
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
     * Subtraction.
     * 
     * <p>
     * <code>..., value &rArr; ..., value - operand</code>
     * </p>
     * 
     * @param operand
     *            Value to subtract.
     */
    public void sub(final double operand)
    {
        this.stack[this.sp].number -= operand;
    }

    /**
     * Multiplication.
     * 
     * <p>
     * <code>..., value1, value2 &rArr; ..., value1 * value2 </code>
     * </p>
     * 
     * @param operand
     *            Value to multiply with.
     */
    public void mul()
    {
        final Value b = this.stack[this.sp--];
        final Value a = this.stack[this.sp];
        a.number *= b.number;
    }

    /**
     * Multiplication.
     * 
     * <p>
     * <code>..., value &rArr; ..., value * operand</code>
     * </p>
     */
    public void mul(final double operand)
    {
        this.stack[this.sp].number *= operand;
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
     * Division.
     * 
     * <p>
     * <code>..., value &rArr; ..., value / operand</code>
     * </p>
     * 
     * @param operand
     *            Value to divide by.
     */
    public void div(final double operand)
    {
        this.stack[this.sp].number /= operand;
    }

    /**
     * Modulo.
     * 
     * <p>
     * <code>..., value1, value2 &rArr; ..., value1 % value2 </code>
     * </p>
     */
    public void mod()
    {
        final Value b = this.stack[this.sp--];
        final Value a = this.stack[this.sp];
        a.number %= b.number;
    }

    /**
     * Modulo.
     * 
     * <p>
     * <code>..., value &rArr; ..., value % operand</code>
     * </p>
     * 
     * @param operand
     *            Value to divide by.
     */
    public void mod(final double operand)
    {
        this.stack[this.sp].number %= operand;
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
     * uses (like most other dynamically types languages) double as its number
     * type, and doubles only have 52(+1) Bits mantissa, only 32 Bit binary
     * integer arithmetics makes sense.
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
     * Binary signed right shift.
     * 
     * <p>
     * <code>..., value1, value2 &rArr; ..., value1 >> value2 </code>
     * </p>
     */
    public void shr()
    {
        final Value b = this.stack[this.sp--];
        final Value a = this.stack[this.sp];
        a.number = (int) a.number >> (int) b.number;
    }

    /**
     * Binary unsigned right shift.
     * 
     * <p>
     * <code>..., value1, value2 &rArr; ..., value1 >>> value2 </code>
     * </p>
     */
    public void ushr()
    {
        final Value b = this.stack[this.sp--];
        final Value a = this.stack[this.sp];
        a.number = (int) a.number >>> (int) b.number;
    }

    /**
     * Binary left shift.
     * 
     * <p>
     * <code>..., value1, value2 &rArr; ..., value1 &lt;&lt; value2 </code>
     * </p>
     */
    public void shl()
    {
        final Value b = this.stack[this.sp--];
        final Value a = this.stack[this.sp];
        a.number = (int) a.number << (int) b.number;
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
     * Arithmetic not.
     * 
     * <p>
     * <code>..., value &rArr; ..., -value</code>
     * </p>
     */
    public void neg()
    {
        final Value a = this.stack[this.sp];
        a.number = -a.number;
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
     * Loads NULL onto the stack.
     * 
     * <p>
     * <code>... &rArr; ..., NULL</code>
     * </p>
     */
    public void load()
    {
        this.stack[++this.sp].type = ValueType.NULL;
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
     * Loads a boolean value onto the Weel stack.
     * 
     * <p>
     * <code>... &rArr; ..., value</code>
     * </p>
     * 
     * @param value
     *            Boolean value to load.
     */
    public void load(final boolean value)
    {
        this.stack[++this.sp].type = ValueType.NUMBER;
        this.stack[this.sp].number = value ? -1 : 0;
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
        if (value != null)
        {
            this.stack[++this.sp].type = ValueType.STRING;
            this.stack[this.sp].object = value;
        }
        else
        {
            this.stack[++this.sp].type = ValueType.NULL;
        }
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
        if (value != null)
        {
            this.stack[++this.sp].type = ValueType.MAP;
            this.stack[this.sp].object = value;
        }
        else
        {
            this.stack[++this.sp].type = ValueType.NULL;
        }
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
        if (value != null)
        {
            this.stack[++this.sp].type = ValueType.FUNCTION;
            this.stack[this.sp].object = value;
        }
        else
        {
            this.stack[++this.sp].type = ValueType.NULL;
        }
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
     * Loads a value onto the Weel stack.
     * 
     * <p>
     * <code>... &rArr; ..., value</code>
     * </p>
     * 
     * @param value
     *            Value to load.
     */
    public void load(final Value value)
    {
        if (value != null)
        {
            value.copyTo(this.stack[++this.sp]);
        }
        else
        {
            this.stack[++this.sp].type = ValueType.NULL;
        }
    }

    /**
     * Loads a function.
     * 
     * <p>
     * <code>... &rArr; ..., value</code>
     * </p>
     * 
     * @param index
     *            The function index.
     */
    public void loadFunc(final int index)
    {
        this.stack[++this.sp].type = ValueType.FUNCTION;
        this.stack[this.sp].object = this.mother.functions.get(index);
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
     * 
     * @param depth
     *            Maximum stack depth the function used.
     */
    public void closeFrame(final int depth)
    {
        final int pops = this.frameSize[this.fp--];
        for (int i = -depth; i < pops; i++)
        {
            this.stack[this.sp - i].setNull();
        }
        this.sp -= pops;
    }

    /**
     * Closes a function frame taking care of the return value.
     * 
     * @param depth
     *            Maximum stack depth the function used.
     */
    public void closeFrameRet(final int depth)
    {
        this.stack[this.sp].copyTo(this.stack[this.frameStart[this.fp]]);
        final int pops = this.frameSize[this.fp--];
        for (int i = -depth; i < pops; i++)
        {
            this.stack[this.sp - i].setNull();
        }
        this.sp -= pops;
    }

    /**
     * Calls a Weel function defined by a value on the stack. Stack calls are
     * significantly slower than 'normal' calls.
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
        WeelFunction func = this.stack[this.sp - args].getFunction();

        if (func.arguments != args)
        {
            // Is there a valid overloaded function?
            final WeelFunction overloaded = this.mother.findFunction(func.name,
                    args);
            if (overloaded != null)
            {
                // Yes, use it
                func = (WeelFunction) (this.stack[this.sp - args].object = overloaded);
            }
            else
            {
                // Nope, throw it
                throw new WeelException(
                        "Stackcall argument count mismatch, expected " + args
                                + " got " + func.arguments);
            }
        }

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
     * Performs a special call.
     * 
     * <p>
     * <code>..., value, [arguments] &rArr; ..., [return value]</code>
     * </p>
     * 
     * @param name
     *            The name of the function.
     * @param args
     *            The number of arguments.
     * @param shouldReturn
     *            Flags indicating that we need a return value.
     */
    public void specialCall(final String name, final int args,
            final boolean shouldReturn)
    {
        final TypeFunctions funcs = this.typeFunctions[this.stack[this.sp
                - args].type.ordinal()];
        final WeelFunction func = funcs.findFunction(name);
        if (func == null)
        {
            throw new WeelException("Unknown support function '"
                    + name.substring(0, name.lastIndexOf('#')) + "(" + args
                    + ")'");
        }

        func.invoke(this);
        // Check return value
        if (func.returnsValue && !shouldReturn)
        {
            this.sp--;
        }
        else if (!func.returnsValue && shouldReturn)
        {
            this.stack[++this.sp].setNull();
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
        this.stack[this.sp].object = new ValueMap();
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
     * Gets a value from a map.
     * 
     * <p>
     * <code>..., map &rArr; ..., value</code>
     * </p>
     * 
     * @param index
     *            The index.
     * @throws WeelException
     *             If the 'map' is not a ValueMap.
     */
    public void getMap(final int index)
    {
        final ValueMap map = this.stack[this.sp].getMap();
        map.get(index, this.stack[this.sp]);
    }

    /**
     * Gets a value from a map.
     * 
     * <p>
     * <code>..., map &rArr; ..., value</code>
     * </p>
     * 
     * @param index
     *            The index.
     * @throws WeelException
     *             If the 'map' is not a ValueMap.
     */
    public void getMap(final String index)
    {
        final ValueMap map = this.stack[this.sp].getMap();
        map.get(index, this.stack[this.sp]);
    }

    /**
     * Gets a value from a map prepared for OOP calls.
     * 
     * <p>
     * <code>..., map, index &rArr; ..., value, map</code>
     * </p>
     * 
     * @throws WeelException
     *             If the 'map' is not a ValueMap.
     */
    public void getMapOop()
    {
        final ValueMap map = this.stack[this.sp - 1].getMap();
        map.get(this.stack[this.sp], this.stack[this.sp - 1]);
        this.stack[this.sp].type = ValueType.MAP;
        this.stack[this.sp].object = map;
    }

    /**
     * Gets a value from a map prepared for OOP calls.
     * 
     * <p>
     * <code>..., map &rArr; ..., value, map</code>
     * </p>
     * 
     * @param index
     *            The index.
     * @throws WeelException
     *             If the 'map' is not a ValueMap.
     */
    public void getMapOop(final String index)
    {
        final ValueMap map = this.stack[this.sp].getMap();
        map.get(index, this.stack[this.sp]);
        this.stack[++this.sp].type = ValueType.MAP;
        this.stack[this.sp].object = map;
    }

    /**
     * Gets a value from a map prepared for OOP calls.
     * 
     * <p>
     * <code>..., map &rArr; ..., value, map</code>
     * </p>
     * 
     * @param index
     *            The index.
     * @throws WeelException
     *             If the 'map' is not a ValueMap.
     */
    public void getMapOop(final int index)
    {
        final ValueMap map = this.stack[this.sp].getMap();
        map.get(index, this.stack[this.sp]);
        this.stack[++this.sp].type = ValueType.MAP;
        this.stack[this.sp].object = map;
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
     * Sets a value in a map.
     * 
     * <p>
     * <code>..., map, value &rArr; ...</code>
     * </p>
     * 
     * @param index
     *            The index.
     * @throws WeelException
     *             If the 'map' is not a ValueMap.
     */
    public void setMap(final int index)
    {
        final ValueMap map = this.stack[this.sp - 1].getMap();
        map.set(index, this.stack[this.sp]);
        this.sp -= 2;
    }

    /**
     * Sets a value in a map.
     * 
     * <p>
     * <code>..., map, value &rArr; ...</code>
     * </p>
     * 
     * @param index
     *            The index.
     * @throws WeelException
     *             If the 'map' is not a ValueMap.
     */
    public void setMap(final String index)
    {
        final ValueMap map = this.stack[this.sp - 1].getMap();
        map.set(index, this.stack[this.sp]);
        this.sp -= 2;
    }

    /**
     * Appends a value to a map.
     * 
     * <p>
     * <code>..., map, value &rArr; ...</code>
     * </p>
     * 
     * @throws WeelException
     *             If the 'map' is not a ValueMap.
     */
    public void appendMap()
    {
        this.stack[this.sp - 1].getMap().append(this.stack[this.sp]);
        this.sp -= 2;
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
     * Pops a number from the stack.
     * 
     * <p>
     * <code>..., value &rArr; ...</code>
     * </p>
     * 
     * @return The number.
     * @throws WeelException
     *             if the type of the value is incorrect.
     */
    public double popNumber()
    {
        return this.stack[this.sp--].getNumber();
    }

    /**
     * Pops a string from the stack.
     * 
     * <p>
     * <code>..., value &rArr; ...</code>
     * </p>
     * 
     * @return The string.
     * @throws WeelException
     *             if the type of the value is incorrect.
     */
    public String popString()
    {
        return this.stack[this.sp--].getString();
    }

    /**
     * Pops a value from the stack.
     * 
     * <p>
     * <code>..., value &rArr; ...</code>
     * </p>
     * 
     * @return Value.toString.
     */
    public String popToString()
    {
        return this.stack[this.sp--].toString();
    }

    /**
     * Pops a value from the stack and returns its type.
     * 
     * <p>
     * <code>..., value &rArr; ...</code>
     * </p>
     * 
     * @return The type.
     */
    public ValueType popType()
    {
        return this.stack[this.sp--].type;
    }

    /**
     * Pops a value from the stack and returns its size.
     * 
     * <p>
     * <code>..., value &rArr; ...</code>
     * </p>
     * 
     * @return The size.
     */
    public double popSize()
    {
        return this.stack[this.sp--].size();
    }

    /**
     * Pops a map from the stack.
     * 
     * <p>
     * <code>..., value &rArr; ...</code>
     * </p>
     * 
     * @return The ValueMap.
     * @throws WeelException
     *             if the type of the value is incorrect.
     */
    public ValueMap popMap()
    {
        return this.stack[this.sp--].getMap();
    }

    /**
     * Pops a function from the stack.
     * 
     * <p>
     * <code>..., value &rArr; ...</code>
     * </p>
     * 
     * @return The function.
     * @throws WeelException
     *             if the type of the value is incorrect.
     */
    public WeelFunction popFunction()
    {
        return this.stack[this.sp--].getFunction();
    }

    /**
     * Pops an object from the stack.
     * 
     * <p>
     * <code>..., value &rArr; ...</code>
     * </p>
     * 
     * @return The object.
     * @throws WeelException
     *             if the type of the value is incorrect.
     */
    public Object popObject()
    {
        return this.stack[this.sp--].getObject();
    }

    /**
     * Gets a number value from the stack.
     * <p>
     * Only used in wrapper methods.
     * </p>
     * 
     * @param var
     *            The local variable index.
     * @return The number.
     */
    public double getNumberLocal(final int var)
    {
        return this.stack[var + this.frameStart[this.fp]].getNumber();
    }

    /**
     * Gets a boolean value from the stack.
     * <p>
     * Only used in wrapper methods.
     * </p>
     * 
     * @param var
     *            The local variable index.
     * @return The boolean.
     */
    public boolean getBooleanLocal(final int var)
    {
        return this.stack[var + this.frameStart[this.fp]].toBoolean();
    }

    /**
     * Gets a String value from the stack.
     * <p>
     * Only used in wrapper methods.
     * </p>
     * 
     * @param var
     *            The local variable index.
     * @return The String or <code>null</code> if Value is of type NULL.
     */
    public String getStringLocal(final int var)
    {
        final Value value = this.stack[var + this.frameStart[this.fp]];
        return value.type == ValueType.NULL ? null : value.getString();
    }

    /**
     * Gets a ValueMap value from the stack.
     * <p>
     * Only used in wrapper methods.
     * </p>
     * 
     * @param var
     *            The local variable index.
     * @return The ValueMap or <code>null</code> if Value is of type NULL.
     */
    public ValueMap getMapLocal(final int var)
    {
        final Value value = this.stack[var + this.frameStart[this.fp]];
        return value.type == ValueType.NULL ? null : value.getMap();
    }

    /**
     * Gets a function value from the stack.
     * <p>
     * Only used in wrapper methods.
     * </p>
     * 
     * @param var
     *            The local variable index.
     * @return The WeelFunction or <code>null</code> if Value is of type NULL.
     */
    public WeelFunction getFunctionLocal(final int var)
    {
        final Value value = this.stack[var + this.frameStart[this.fp]];
        return value.type == ValueType.NULL ? null : value.getFunction();
    }

    /**
     * Gets an object value from the stack.
     * <p>
     * Only used in wrapper methods.
     * </p>
     * 
     * @param var
     *            The local variable index.
     * @return The Object or <code>null</code> if Value is of type NULL.
     */
    public Object getObjectLocal(final int var)
    {
        final Value value = this.stack[var + this.frameStart[this.fp]];
        return value.type == ValueType.NULL ? null : value.getObject();
    }

    /**
     * Gets a value from the stack.
     * <p>
     * Only used in wrapper methods.
     * </p>
     * 
     * @param var
     *            The local variable index.
     * @return The Value.
     */
    public Value getValueLocal(final int var)
    {
        return this.stack[var + this.frameStart[this.fp]].clone();
    }

    /**
     * Creates a closure function from a static anonymous function.
     * 
     * @param index
     *            The function index.
     */
    public void createClosure(final int index)
    {
        this.stack[++this.sp].type = ValueType.FUNCTION;
        this.stack[this.sp].object = this.mother.functions.get(index)
                .cloneClosure(this);
    }

    /**
     * Cleans the stack up to the current stack pointer.
     * 
     * <p>
     * You may use this function to clean up unused references on the Weel
     * stack.
     * </p>
     */
    public void wipeStack()
    {
        for (int i = this.stack.length - 1; i > this.sp; i--)
        {
            this.stack[i].setNull();
        }
    }

    /**
     * <code>assert(expr)</code>
     * <p>
     * Weel assert.
     * </p>
     * 
     * @param err
     *            The error message if the assert fails.
     */
    public void weelAssert(final String err)
    {
        if (!this.popBoolean())
        {
            throw new WeelException(err);
        }
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
        if (args.length != function.arguments)
        {
            throw new WeelException("Argument count mismatch");
        }

        for (final Object o : args)
        {
            if (o == null)
            {
                this.load();
                continue;
            }
            final Class<?> oc = o.getClass();
            if (oc == Double.class)
            {
                this.load((double) ((Double) o));
            }
            else if (oc == Float.class)
            {
                this.load((float) ((Float) o));
            }
            else if (oc == Integer.class)
            {
                this.load((int) ((Integer) o));
            }
            else if (oc == Short.class)
            {
                this.load((int) ((Short) o));
            }
            else if (oc == Byte.class)
            {
                this.load((int) ((Byte) o));
            }
            else if (oc == Character.class)
            {
                this.load((int) ((Character) o));
            }
            else if (oc == String.class)
            {
                this.load((String) o);
            }
            else if (oc == ValueMap.class)
            {
                this.load((ValueMap) o);
            }
            else
            {
                this.load(o);
            }
        }

        function.invoke(this);

        if (function.returnsValue)
            return this.pop();

        return null;
    }

    /**
     * Initializes a virtual function. Called from invokers only.
     * 
     * @param function
     *            The virtual function.
     */
    void initVirtual(final WeelFunction function)
    {
        this.closureFunctions[++this.vp] = function;
    }

    /**
     * Ends a virtual function. Called from invokers only.
     */
    void exitVirtual()
    {
        this.closureFunctions[this.vp--] = null;
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
        return this.closureFunctions[this.vp].environment[var].clone();
    }
}
