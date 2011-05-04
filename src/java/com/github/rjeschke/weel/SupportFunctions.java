package com.github.rjeschke.weel;

import java.util.ArrayList;
import java.util.HashMap;

class SupportFunctions
{
    /** Function list. */
    ArrayList<WeelFunction> functions = new ArrayList<WeelFunction>();
    /** Name to function index mapping. */
    HashMap<String, Integer> mapFunctions = new HashMap<String, Integer>();
    /** Name to exact function index mapping. */
    HashMap<String, Integer> mapFunctionsExact = new HashMap<String, Integer>();

    /**
     * Finds the given function.
     * 
     * @param name
     *            The function's name.
     * @return The WeelFunction or <code>null</code> if none was found.
     */
    public WeelFunction findFunction(final String name)
    {
        final Integer index = this.mapFunctions.get(name.toLowerCase());
        return index != null ? this.functions.get(index) : null;
    }

    /**
     * Finds the given function.
     * 
     * @param name
     *            The function's name.
     * @param args
     *            The number of arguments.
     * @return The WeelFunction or <code>null</code> if none was found.
     */
    public WeelFunction findFunction(final String name, final int args)
    {
        final Integer index = this.mapFunctionsExact.get(name.toLowerCase()
                + "#" + args);
        return index != null ? this.functions.get(index) : null;
    }

    /**
     * Adds the given function to this function list.
     * 
     * @param iname
     *            The internal name.
     * @param func
     *            The function.
     */
    void addFunction(final String iname, final WeelFunction func)
    {
        this.addFunction(iname, func, true);
    }

    /**
     * Adds the given function to this function list.
     * 
     * @param iname
     *            The internal name.
     * @param func
     *            The function.
     * @param addToHash
     *            Add it to the function name hash map?
     */
    void addFunction(final String iname, final WeelFunction func,
            final boolean addToHash)
    {
        final int index = this.functions.size();
        func.index = index;
        this.functions.add(func);
        if (addToHash)
        {
            this.mapFunctions.put(func.name, index);
            this.mapFunctionsExact.put(iname, index);
        }
    }
}
