/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

interface Instr
{
    /**
     * Returns the type of this instruction.
     * 
     * @return The type.
     */
    public Op getType();

    /**
     * Writes this instruction into the method writer.
     * 
     * @param mw
     *            The method writer.
     */
    public void write(final JvmMethodWriter mw);
}
