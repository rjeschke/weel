/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

    /**
     * Whether this is an OOP class or a collection of array functions.
     */
    boolean usesOop() default false;

    /**
     * Whether this class is private and no global variable should be created or
     * not. If you set isPrivate() to <code>true</code>, you will need a
     * <code>public static ValueMap ME;</code> variable in your class.
     */
    boolean isPrivate() default false;
}
