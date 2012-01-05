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
 * Weel invocation interface to support both JNI and reflection invocation.
 * 
 * @author René Jeschke <rene_jeschke@yahoo.de>
 */
interface WeelInvoker
{
    /**
     * Invokes a Weel function.
     * 
     * @param runtime
     *            The Weel Runtime.
     */
    public void invoke(final WeelRuntime runtime);

    /**
     * Invokes a Weel anonymous function with closure.
     * 
     * @param runtime
     *            The Weel Runtime.
     * @param function
     *            The virtual WeelFunction.
     * 
     */
    public void invoke(final WeelRuntime runtime, final WeelFunction function);

    /**
     * Initializes this invoker.
     * 
     * @param weel
     *            The Weel.
     * @param function
     *            The Weel function.
     */
    public void initialize(final Weel weel, final WeelFunction function);
}
