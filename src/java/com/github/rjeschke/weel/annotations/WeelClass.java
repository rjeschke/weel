/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Weel class annotation.
 * 
 * @author René Jeschke <rene_jeschke@yahoo.de>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface WeelClass
{
    /**
     * The name of the Weel class. If none is given, the Java class name will be
     * used.
     */
    String name() default "";
}
