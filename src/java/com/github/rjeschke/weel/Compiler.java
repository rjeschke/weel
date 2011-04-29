/*
* Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
* See LICENSE.txt for licensing information.
*/
package com.github.rjeschke.weel;

/**
 * Weel compiler.
 * 
 * @author René Jeschke <rene_jeschke@yahoo.de>
 */
final class Compiler
{
    /*
     *  Maybe it's time to re-think the whole compiler thingy ... dunno if I
     *  really should do it the same way as I always did ... well, we'll see^^
     * 
     *  Planned new features:
     *  - closures and anonymous functions
     *  Should I rename 'function' to 'func' ? Does any language use 'func' as a keyword?
     *  Google revealed that at least 'Go' uses 'func' as a keyword ... so if it works for
     *  'Go' it should work for Weel.
     *  
     *  Anonymous functions:
     *  
     *  list = {1, 2, 3, 4}
     *  list2 = {5, 6, 7, 8}
     *  
     *  result = fold(list, func(a, b) return a + b; end)
     *  forAll(list, sub(a) println(a); end)
     *  convolve(list, list2, func(a, b) return a * b; end)
     *  
     *  or:
     *  result = fold(list, 
     *      func(a, b) 
     *          return a + b
     *      end)
     *  
     *  that means that you could do things like:
     *  
     *  func myFunc()
     *      return sub() println("Hello world!"); end
     *  end
     *  
     *  and with closures you could do:
     *  
     *  func myFunc(target)
     *      return sub() println("Hello "..target.."!"); end
     *  end
     *  
     *  and myFunc("world")() would produce:
     *      Hello world!
     *  
     *  I think that I'll have to implement some sort of 'virtual functions'
     *  to support closures ... and some fancy startup code to initialize
     *  the inherited state.
     *  
     *  So every call to myFunc in the above example will create a new virtual 
     *  function with its own state ... yep, this should work ... closures ftw^^ 
     * 
     *  Do I want to support currying ? Not now^^
     * 
     *  Changes to the runtime to support anonymous functions:
     *  - createVirtual : creates a virtual function from a static function
     *                    and prepare closures if needed
     *  Changes to the runtime to support closures:
     *  - linhenv, sinhenv : load/store inherited environment value
     *  - openClosure, closeClosure, closeClosureRet
     *  - a virtual function stack
     *  
     */
}
