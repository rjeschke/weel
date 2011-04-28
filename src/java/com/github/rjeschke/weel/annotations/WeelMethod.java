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
 * Weel nice method annotation.
 * 
 * @author René Jeschke <rene_jeschke@yahoo.de>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface WeelMethod
{
    /**
     * The name of the Weel function. If none is given, the Java method name
     * will be used.
     */
    String name() default "";

    /**
     * If set to <code>true</code> the compiler will use different Object type
     * checking to allow for <code>null</code> values.
     * <p>
     * E.g.: if you define a method taking a String as its argument and need to
     * allow the user to pass a Weel NULL Value you would set this flag to
     * <code>true</code>, otherwise the JVM will throw a runtime exception
     * telling you that the Value is not a STRING.
     * </p>
     */
    boolean allowNull() default false;
}
