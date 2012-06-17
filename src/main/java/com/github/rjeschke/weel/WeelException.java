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
package com.github.rjeschke.weel;

/**
 * Weel exception.
 * 
 * @author René Jeschke <rene_jeschke@yahoo.de>
 */
public class WeelException extends RuntimeException
{
    /** serialVersionUID */
    private static final long serialVersionUID = 7187502537191362694L;
    private final Value value;
    
    /**
     * @param value The Weel value.
     */
    public WeelException(final Value value)
    {
        super("Error: " + value);
        this.value = value.clone();
    }

    /** @see java.lang.RuntimeException#RuntimeException(String) */
    public WeelException(final String message)
    {
        super(message);
        this.value = null;
    }
    
    /** @see java.lang.RuntimeException#RuntimeException(String, Throwable) */
    public WeelException(final String message, final Throwable throwable)
    {
        super(message, throwable);
        this.value = null;
    }

    /** @see java.lang.RuntimeException#RuntimeException(Throwable) */
    public WeelException(final Throwable throwable)
    {
        super(throwable);
        this.value = null;
    }
    
    /**
     * Gets the Weel value of this Exception.
     * @return The value or null.
     */
    public Value getValue()
    {
        return this.value != null ? this.value.clone() : null;
    }
}
