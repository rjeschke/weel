/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

import java.util.ArrayList;
import java.util.Collections;
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
    private HashMap<Integer, Integer> cvarMap = new HashMap<Integer, Integer>();
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
    /** Maximum stack depth. */
    private int maxStack;
    /** Label counter */
    private int labels = 0;
    /** Label lines. */
    private int[] lines;
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
     * @param dumpCode
     *            Flag indicating that we should dump the generated code to
     *            stdout.
     */
    public void closeBlock(final boolean debugMode, final boolean dumpCode)
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

        this.refactorTailCalls();

        // Create frame
        this.insertInstr(0, this.function != null ? new InstrOframe(
                this.function.getNumArguments(), this.locals.size()
                        - this.function.getNumArguments()) : new InstrOframe(0,
                this.locals.size()));
        this.instrs.add(new InstrCframe(this.maxStack, this.function != null
                && this.function.returnsValue));

        // Finally resolve labels again
        this.resolveLabels();

        if (dumpCode)
        {
            this.dump();
        }
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
            if (i.getType() == Op.KEY)
                continue;
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
     * Refactors ALU2 instructions.
     */
    private void refactorAlu()
    {
        final WeelRuntime rt = this.weel.getTempRuntime();
        boolean redo = true;
        while (redo)
        {
            redo = false;
            for (int i = 0; i < this.instrs.size() - 1; i++)
            {
                final Instr a = this.instrs.get(i);
                if (a.getType() == Op.ALU2)
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
                                i -= 2; 
                                redo = true;
                            }
                            break;
                        }
                        case mapcat:
                        case mapcat2:
                            break;
                        default:
                        {
                            final Instr l1 = this.instrs.get(i - 1);
                            if (l1.getType() == Op.LOAD
                                    && ((InstrLoad) l1).value.isNumber())
                            {
                                final Instr l0 = this.instrs.get(i - 2);

                                if (l0.getType() != Op.LOAD
                                        && alu.value == null
                                        && InstrAlu2.OVERLOADED
                                                .contains(alu.type))
                                {
                                    alu.value = ((InstrLoad) l1).value;
                                    this.instrs.remove(i - 1);
                                    i--;
                                    redo = true;
                                }
                                else if (l0.getType() == Op.LOAD
                                        && ((InstrLoad) l0).value.isNumber())
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
                                    case pow:
                                        rt.pow();
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
                                    i -= 2;
                                    redo = true;
                                }
                                break;
                            }
                        }
                        }
                    }
                }
            }
        }
    }

    /**
     * Refactors map accesses and jumps.
     */
    private void refactorMapsAndJumps()
    {
        boolean redo = true;
        while (redo)
        {
            redo = false;
            for (int i = 0; i < this.instrs.size() - 1; i++)
            {
                final Instr y = i > 1 ? this.instrs.get(i - 2) : null;
                final Instr z = i > 0 ? this.instrs.get(i - 1) : null;
                final Instr a = this.instrs.get(i);
                final Instr b = this.instrs.get(i + 1);
                switch (a.getType())
                {
                case SETMAP:
                {
                    final InstrSetMap sm = (InstrSetMap) a;
                    if (sm.key == null)
                    {
                        int p = i;
                        while (p >= 0 && this.instrs.get(p).getType() != Op.KEY)
                        {
                            p--;
                        }
                        if (p > 0
                                && this.instrs.get(p - 1).getType() == Op.LOAD)
                        {
                            final Value v = ((InstrLoad) this.instrs.get(p - 1)).value
                                    .clone();
                            if (v.type == ValueType.NUMBER
                                    || v.type == ValueType.STRING)
                            {
                                sm.key = v;
                                this.instrs.remove(p - 1);
                                i--;
                                redo = true;
                            }
                        }
                    }
                    break;
                }
                case GETMAP:
                    if (z != null && z.getType() == Op.KEY && y != null
                            && y.getType() == Op.LOAD)
                    {
                        final Value v = ((InstrLoad) y).value.clone();
                        final InstrGetMap gm = (InstrGetMap) a;
                        if (gm.key == null
                                && (v.type == ValueType.STRING || v.type == ValueType.NUMBER))
                        {
                            gm.key = v;
                            this.instrs.remove(i - 2);
                            i--;
                            redo = true;
                        }
                    }
                    break;
                case GETMAPOOP:
                    if (z != null && z.getType() == Op.KEY && y != null
                            && y.getType() == Op.LOAD)
                    {
                        final Value v = ((InstrLoad) y).value.clone();
                        final InstrGetMapOop gm = (InstrGetMapOop) a;
                        if (gm.key == null && v.type == ValueType.STRING)
                        {
                            gm.key = v;
                            this.instrs.remove(i - 2);
                            i--;
                            redo = true;
                        }
                    }
                    break;
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
                            i--;
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
                default:
                    break;
                }
            }
        }
    }

    /**
     * Refactors CMP.
     */
    private void refactorCmp()
    {
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
                case ALU2:
                {
                    final InstrAlu2 alu = (InstrAlu2) a;
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
                            final InstrCmpPop icp = new InstrCmpPop(
                                    alu.type);
                            icp.value = alu.value;
                            this.instrs.set(i, icp);
                            this.instrs.remove(i + 1);
                            redo = true;
                        }
                        break;
                    default:
                        break;
                    }
                    break;
                }
                default:
                    break;
                }
            }
        }
    }

    /**
     * Inserts an instruction.
     * 
     * @param index The index.
     * @param ins The instruction.
     */
    private void insertInstr(final int index, final Instr ins)
    {
        if(index == this.instrs.size())
        {
            this.instrs.add(ins);
        }
        else
        {
            this.instrs.add(null);
            for(int i = this.instrs.size() - 2; i >= index; i--)
            {
                this.instrs.set(i + 1, this.instrs.get(i));
            }
            this.instrs.set(index, ins);
        }
    }
    
    /**
     * Refactors tail calls.
     */
    private void refactorTailCalls()
    {
        if(this.instrs.size() < 1 || this.function == null)
        {
            return;
        }
        
        final int fidx = this.function.index;
        boolean changed = false;
        final int l = this.registerLabel();
        if(this.instrs.get(this.instrs.size() - 1).getType() == Op.LABEL)
        {
            int n = this.instrs.size() - 1;
            final ArrayList<Integer> labels = new ArrayList<Integer>();
            while(n > 0 && this.instrs.get(n).getType() == Op.LABEL)
            {
                labels.add(((InstrLabel)this.instrs.get(n--)).index);
            }
            n++;
            
            for(int i = 1; i < this.instrs.size() - 1; i++)
            {
                final Instr a = this.instrs.get(i);
                final Instr b = this.instrs.get(i + 1);
                if(a.getType() == Op.CALL && (b.getType() == Op.GOTO || (i + 1) == n))
                {
                    final InstrCall call = (InstrCall)a;
                    if((call.func.index != fidx) || (b.getType() == Op.GOTO && !labels.contains(((InstrGoto)b).index)))
                    {
                        continue;
                    }

                    // Remove return/exit GOTO
                    if(b.getType() == Op.GOTO)
                    {
                        this.instrs.remove(i + 1);
                    }

                    // Place new GOTO
                    this.instrs.set(i, new InstrGoto(l));
                    for(int p = 0; p < this.function.arguments; p++)
                    {
                        this.insertInstr(i, new InstrVarStore(VarInstrType.LOCAL, p));
                    }
                    i += this.function.arguments;
                    changed = true;
                }
            }
        }
        else if(this.instrs.get(this.instrs.size() - 1).getType() == Op.CALL)
        {
            final InstrCall call = (InstrCall)this.instrs.get(this.instrs.size() - 1);
            if(call.func.index == fidx)
            {
                final int i = this.instrs.size() - 1;
                this.instrs.set(i, new InstrGoto(l));
                for(int p = 0; p < this.function.arguments; p++)
                {
                    this.insertInstr(i, new InstrVarStore(VarInstrType.LOCAL, p));
                }
                changed = true;
            }
        }
        
        if(changed)
        {
            this.insertInstr(0, new InstrLabel(l));
        }
    }
    
    /**
     * Refactors this block's code, by replacing/reordering/removing common
     * compilation 'artifacts'.
     */
    // TODO check if my redesign works ;)
    private void refactor()
    {
        this.refactorAlu();
        // I think I could join these two
        this.refactorMapsAndJumps();
        this.refactorCmp();
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
            case ALU2:
                if (((InstrAlu2) in).value == null)
                    cur--;
                break;
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
            case GETMAP:
                if (((InstrGetMap) in).key == null)
                {
                    cur--;
                }
                break;
            case SETMAP:
                if (((InstrSetMap) in).key == null)
                {
                    cur -= 3;
                }
                else
                {
                    cur -= 2;
                }
                break;
            case GETMAPOOP:
                if (((InstrGetMapOop) in).key != null)
                {
                    cur++;
                }
                break;
            case CMPPOP:
                cur -= ((InstrCmpPop) in).value == null ? 2 : 1;
                break;
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
                throw new WeelException("What a terrible failure: recurse reached " + cur + ", contact the author.");
            }
            returns |= cur == 0;
            return returns ? 0 : 1;
        }
        if (cur < 0 || cur > 1)
        {
            throw new WeelException("What a terrible failure: recurse reached " + cur + ", contact the author.");
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
