/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
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

    /** @see java.lang.RuntimeException#RuntimeException(String) */
    public WeelException(final String message)
    {
        super(message);
    }

    /** @see java.lang.RuntimeException#RuntimeException(String, Throwable) */
    public WeelException(final String message, final Throwable throwable)
    {
        super(message, throwable);
    }

    /** @see java.lang.RuntimeException#RuntimeException(Throwable) */
    public WeelException(final Throwable throwable)
    {
        super(throwable);
    }
}
