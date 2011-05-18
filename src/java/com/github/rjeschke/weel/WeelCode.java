/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

import java.util.ArrayList;
import java.util.HashMap;

import com.github.rjeschke.weel.Variable.Type;

/**
 * Weel intermediate language code container.
 * 
 * @author René Jeschke <rene_jeschke@yahoo.de>
 */
class WeelCode
{
    /** The Weel. */
    private final Weel weel;
    /** List of used locals. */
    ArrayList<Boolean> locals = new ArrayList<Boolean>();
    /** Closure variables indices. */
    ArrayList<Integer> cvarIndex = new ArrayList<Integer>();
    /** Variable index to closure index mapping. */
    HashMap<Integer, Integer> cvarMap = new HashMap<Integer, Integer>();
    /** Closure variable name to index mapping. */
    final HashMap<String, Integer> cvars = new HashMap<String, Integer>();
    /** Instructions. */
    ArrayList<Instr> instrs = new ArrayList<Instr>();
    /** Enclosing WeelFunction (if not STATIC). */
    WeelFunction function;
    /** Flag indicating that we're an anonymous function. */
    boolean isAnonymousFunction;
    /** Flag indicating that we're an anonymous function using alternate syntax. */
    boolean isAlternateSyntax;
    /** Flag indicating that our function has at least one return statement. */
    boolean hasReturn;
    /**
     * Flag indicating that our function has at least one exit statement.
     * Relevant only for automatically typed anonymous functions.
     */
    boolean hasExit;
    /** Current stack position. */
    int currentStack;
    /** Maximum stack depth. */
    int maxStack;
    /** Label counter */
    int labels = 0;
    /** Label lines. */
    int[] lines;
    /** Starting position in source code. */
    String source = null;

    /**
     * Constructor.
     * 
     * @param weel
     *            The Weel.
     */
    public WeelCode(final Weel weel)
    {
        this.weel = weel;
    }

    /**
     * Adds an instruction to this code block.
     * 
     * @param i
     *            The instruction.
     */
    void add(final Instr i)
    {
        this.instrs.add(i);
    }

    /**
     * Registers a local variable.
     * 
     * @return The local variable index.
     */
    int registerLocal()
    {
        for (int i = 0; i < this.locals.size(); i++)
        {
            if (!this.locals.get(i).booleanValue())
            {
                this.locals.set(i, true);
                return i;
            }
        }
        final int ret = this.locals.size();
        this.locals.add(true);
        return ret;
    }

    /**
     * Unregisters a local variable.
     * 
     * @param index
     *            The variable index.
     */
    void unregisterLocal(final int index)
    {
        this.locals.set(index, false);
    }

    /**
     * Registers or gets a closure variable.
     * 
     * @param var
     *            The variable.
     * @return The index or <code>-1</code>
     */
    int getCvar(final Variable var)
    {
        if (var.type == Type.NONE || var.type == Type.GLOBAL)
            return -1;
        final int vi = var.type == Type.LOCAL ? var.index : -var.index - 1;
        final Integer idx = this.cvarMap.get(vi);
        if (idx != null)
            return idx;
        final int num = this.cvarIndex.size();
        this.cvarIndex.add(vi);
        this.cvarMap.put(vi, num);
        return num;
    }

    /**
     * Registers a label.
     * 
     * @return The label index.
     */
    int registerLabel()
    {
        return this.labels++;
    }

    /**
     * Closes this block.
     * 
     * @param debugMode
     *            Flag indicating that we're in debug mode.
     */
    public void closeBlock(final boolean debugMode)
    {
        // Remove asserts if !debugMode
        if (!debugMode)
        {
            int i = 0;
            while (i < this.instrs.size())
            {
                if (this.instrs.get(i).getType() == Op.BEGASSERT)
                {
                    while (this.instrs.get(i).getType() != Op.ENDASSERT)
                    {
                        this.instrs.remove(i);
                    }
                    this.instrs.remove(i);
                }
                else
                {
                    i++;
                }
            }
        }
        // Nothing left? Return
        if (this.instrs.size() == 0)
        {
            return;
        }
        // Resolve labels 
        this.resolveLabels();
        // Refactor
        this.refactor();
        // Resolve labels again 
        this.resolveLabels();

        // Calculate maximum stack depth, check for return value
        this.maxStack = 0;
        final boolean allReturn = this.recurse(0, 0) == 1;
        if (this.function != null && this.function.returnsValue && !allReturn)
        {
            throw new WeelException("Not all code paths of '" + this.function
                    + "' return a value" + this.source);
        }

        // Create frame
        this.instrs.add(null);
        for (int i = this.instrs.size() - 2; i >= 0; i--)
        {
            this.instrs.set(i + 1, this.instrs.get(i));
        }
        this.instrs.set(0, this.function != null ? new InstrOframe(
                this.function.getNumArguments(), this.locals.size()
                        - this.function.getNumArguments()) : new InstrOframe(0,
                this.locals.size()));
        this.instrs.add(new InstrCframe(this.maxStack, this.function != null
                && this.function.returnsValue));

        // Finally resolve labels again
        this.resolveLabels();
    }

    /**
     * Dumps the contents of this block to stdout.
     */
    void dump()
    {
        System.out.println();
        if (this.function != null)
            System.out.println(this.function);
        else
            System.out.println("STATIC");
        for (Instr i : this.instrs)
        {
            if (i.getType() != Op.LABEL)
            {
                if (i.getType() == Op.BEGASSERT || i.getType() == Op.ENDASSERT)
                    System.out.print("  ");
                else
                    System.out.print("    ");
            }
            System.out.println(i);
        }
    }

    /**
     * Refactors this block's code, by replacing/reordering/removing common
     * compilation 'artifacts'.
     */
    private void refactor()
    {
        final WeelRuntime rt = this.weel.getTempRuntime();
        boolean redo = true;
        while (redo)
        {
            redo = false;
            for (int i = 0; i < this.instrs.size() - 1; i++)
            {
                final Instr a = this.instrs.get(i);
                final Instr b = this.instrs.get(i + 1);
                switch (a.getType())
                {
                case GOTO:
                    if (b.getType() == Op.GOTO)
                    {
                        // Remove duplicate GOTOs
                        this.instrs.remove(i + 1);
                        redo = true;
                    }
                    else if (b.getType() == Op.LABEL)
                    {
                        if (((InstrGoto) a).index == ((InstrLabel) b).index)
                        {
                            // Remove GOTO to next line
                            this.instrs.remove(i);
                            redo = true;
                        }
                    }
                    break;
                case IFEQ:
                    if (b.getType() == Op.GOTO)
                    {
                        // Replace IFEQ followed by GOTO with IFNE
                        this.instrs
                                .set(i, new InstrIfNe(((InstrGoto) b).index));
                        this.instrs.remove(i + 1);
                        redo = true;
                    }
                    break;
                case IFNE:
                    if (b.getType() == Op.GOTO)
                    {
                        // Replace IFNE followed by GOTO with IFEQ
                        this.instrs
                                .set(i, new InstrIfEq(((InstrGoto) b).index));
                        this.instrs.remove(i + 1);
                        redo = true;
                    }
                    break;
                case ALU2:
                {
                    final InstrAlu2 alu = (InstrAlu2) a;
                    if (i > 1)
                    {
                        switch (alu.type)
                        {
                        case strcat:
                        {
                            final Instr l0 = this.instrs.get(i - 2);
                            final Instr l1 = this.instrs.get(i - 1);
                            if (l0.getType() == Op.LOAD
                                    && l1.getType() == Op.LOAD)
                            {
                                rt.load(((InstrLoad) l0).value);
                                rt.load(((InstrLoad) l1).value);
                                rt.strcat();
                                this.instrs.set(i - 2, new InstrLoad(rt
                                        .popString()));
                                this.instrs.remove(i - 1);
                                this.instrs.remove(i - 1);
                                redo = true;
                            }
                            break;
                        }
                        case mapcat:
                        case mapcat2:
                            break;
                        default:
                        {
                            final Instr l0 = this.instrs.get(i - 2);
                            final Instr l1 = this.instrs.get(i - 1);
                            if (l0.getType() == Op.LOAD
                                    && ((InstrLoad) l0).value.isNumber()
                                    && l1.getType() == Op.LOAD
                                    && ((InstrLoad) l1).value.isNumber())
                            {
                                rt.load(((InstrLoad) l0).value.getNumber());
                                rt.load(((InstrLoad) l1).value.getNumber());
                                switch (alu.type)
                                {
                                case strcat:
                                case mapcat:
                                case mapcat2:
                                    break;
                                case add:
                                    rt.add();
                                    break;
                                case sub:
                                    rt.sub();
                                    break;
                                case mul:
                                    rt.mul();
                                    break;
                                case div:
                                    rt.mul();
                                    break;
                                case mod:
                                    rt.mod();
                                    break;
                                case and:
                                    rt.and();
                                    break;
                                case or:
                                    rt.or();
                                    break;
                                case xor:
                                    rt.xor();
                                    break;
                                case shl:
                                    rt.shl();
                                    break;
                                case shr:
                                    rt.shr();
                                    break;
                                case ushr:
                                    rt.ushr();
                                    break;
                                case cmpEq:
                                    rt.cmpEq();
                                    break;
                                case cmpNe:
                                    rt.cmpNe();
                                    break;
                                case cmpGt:
                                    rt.cmpGt();
                                    break;
                                case cmpGe:
                                    rt.cmpGe();
                                    break;
                                case cmpLt:
                                    rt.cmpLt();
                                    break;
                                case cmpLe:
                                    rt.cmpLe();
                                    break;
                                }
                                this.instrs.set(i - 2, new InstrLoad(rt
                                        .popNumber()));
                                this.instrs.remove(i - 1);
                                this.instrs.remove(i - 1);
                                redo = true;
                            }
                            break;
                        }
                        }
                    }
                    if (!redo)
                    {
                        switch (alu.type)
                        {
                        case cmpEq:
                        case cmpGe:
                        case cmpGt:
                        case cmpLe:
                        case cmpLt:
                        case cmpNe:
                            if (b.getType() == Op.POPBOOL)
                            {
                                this.instrs.set(i, new InstrCmpPop(alu.type));
                                this.instrs.remove(i + 1);
                                redo = true;
                            }
                            break;
                        default:
                            break;
                        }
                    }
                    break;
                }
                default:
                    break;
                }
                if (redo)
                {
                    // Size has changed, restart
                    break;
                }
            }
        }
    }

    /**
     * Scans all code paths in this block, checking for return values and
     * maximum stack depth.
     * 
     * @param line
     *            Line number to start.
     * @param stack
     *            Stack value to start with.
     * @return true/false for return value, the stack depth
     */
    private int recurse(final int line, final int stack)
    {
        int cur = stack;
        boolean returns = false;
        for (int i = line; i < this.instrs.size(); i++)
        {
            final Instr in = this.instrs.get(i);
            switch (in.getType())
            {
            case POP:
                cur -= ((InstrPop) in).pops;
                break;
            case STACKCALL:
            {
                final InstrStackCall call = (InstrStackCall) in;
                cur -= call.paramc + 1;
                if (call.needsReturn)
                    cur++;
                break;
            }
            case SPECIALCALL:
            {
                final InstrSpecialCall call = (InstrSpecialCall) in;
                cur -= call.paramc + 1;
                if (call.needsReturn)
                    cur++;
                break;
            }
            case CALL:
            {
                final InstrCall call = (InstrCall) in;
                cur -= call.func.arguments;
                if (call.func.returnsValue)
                    cur++;
                break;
            }
            case IFEQ:
            {
                int to = this.lines[((InstrIfEq) in).index];
                if (to > i)
                {
                    if (i > 0
                            && (this.instrs.get(i - 1).getType() == Op.DOFOREACH))
                        returns |= this.recurse(to, cur - 2) == 0;
                    else
                        returns |= this.recurse(to, cur) == 0;
                }
                break;
            }
            case IFNE:
            {
                int to = this.lines[((InstrIfNe) in).index];
                if (to > i)
                {
                    returns |= this.recurse(to, cur) == 0;
                    if (i > 0
                            && (this.instrs.get(i - 1).getType() == Op.TESTPOPF || this.instrs
                                    .get(i - 1).getType() == Op.TESTPOPT))
                        cur--;
                }
                break;
            }
            case GOTO:
            {
                int to = this.lines[((InstrGoto) in).index];
                if (to > i)
                {
                    i = to;
                }
                break;
            }
            default:
                cur += in.getType().getDelta();
                break;
            }
            this.maxStack = Math.max(cur, this.maxStack);
        }
        if (line == 0 && stack == 0)
        {
            if (cur < 0 || cur > 1)
            {
                System.err.println("Check this: " + cur);
            }
            returns |= cur == 0;
            return returns ? 0 : 1;
        }
        if (cur < 0 || cur > 1)
        {
            System.err.println("Check this: " + cur);
        }
        return cur;
    }

    /**
     * Maps label indices to line numbers.
     */
    private void resolveLabels()
    {
        this.lines = new int[this.labels];
        for (int i = 0; i < this.instrs.size(); i++)
        {
            final Instr in = this.instrs.get(i);
            if (in.getType() == Op.LABEL)
            {
                this.lines[((InstrLabel) in).index] = i;
                ((InstrLabel) in).line = i;
            }
        }
    }
}
