/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

enum Alu2InstrType
{
    add, sub, mul, div, mod, pow,
    
    and, or, xor,
    
    cmpNe, cmpEq, cmpLe, cmpLt, cmpGe, cmpGt,
    
    strcat, mapcat, mapcat2,
    
    shr, ushr, shl
}
