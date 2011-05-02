/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

import java.lang.reflect.Modifier;

/**
 * Method writer specialized for Weel.
 * 
 * @author René Jeschke <rene_jeschke@yahoo.de>
 */
final class JvmMethodWriter
{
    final JvmClassWriter classWriter;
    final String methodName;
    final String descriptor;
    ByteList code = new ByteList();
    int maxStack = 4, maxLocals = 1;
    int nameIndex, descriptorIndex;
    int access = Modifier.PUBLIC | Modifier.FINAL | Modifier.STATIC;
    
    JvmMethodWriter(final JvmClassWriter classWriter, final String methodName,
            final String descriptor)
    {
        this.classWriter = classWriter;
        this.methodName = methodName;
        this.descriptor = descriptor;
    }
}
