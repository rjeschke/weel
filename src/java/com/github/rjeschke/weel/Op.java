/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

enum Op
{
    ALU2(-1), CMPPOP(-2),
    
    NEG(0), NOT(0), LNOT(0),
    
    CMPEQUAL(-2),
    
    TESTPOPT(0), TESTPOPF(0), 
    
    LOAD(1), LOADFUNC(1), POP(0), SDUP(1), SDUP2(2), SDUPS(1), POPBOOL(-1),

    VARLOAD(1), VARSTORE(-1),
    
    CREATEMAP(1), GETMAP(-1), GETMAPOOP(0), SETMAP(-3), APPENDMAP(-2),
    
    CALL(0), STACKCALL(0), SPECIALCALL(0), CREATECLOSURE(1),
    
    ASSERT(-1), BEGASSERT(0), ENDASSERT(0),
    
    BEGINFOR(0), ENDFOR(0),
    
    PREPARFOREACH(0), DOFOREACH(2),
    
    OFRAME(0), CFRAME(0), CFRAMERET(0),
    
    IFEQ(0), IFNE(0), GOTO(0), LABEL(0);
    
    private int delta;
    
    private Op(final int delta)
    {
        this.delta = delta;
    }
    
    public int getDelta()
    {
        return this.delta;
    }
}
