/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

/**
 * Weel function type enumeration.
 * 
 * @author René Jeschke <rene_jeschke@yahoo.de>
 */
enum WeelFunctionType
{
    /** This is only used during compile time. */
    PROTOTYPE,
    /** Weel compiled or WeelRawMethod. */
    RAW,
    /** Every other Java method. */
    JAVA
}
