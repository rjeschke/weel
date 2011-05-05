# Weel - An extensible scripting language
Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>  
See LICENSE.txt for licensing information.

[$PROFILE$]: extended "Will look horrible when processed as standard Markdown"

*****************************************************************************
*   [Overview](#overview)
*   [Weel and Runtime](#weelruntime)
*   [Types, names and others](#typesandstuff)
    +   [Types](#types)
    +   [Names](#names)
    +   [Reserved words](#reservedwords)
    +   [Operators](#operators)
*   [Program flow control](#flow)
    +   [if, elseif, else, end](#ifelse)
    +   [switch, end](#switch)
    +   [for, end](#for)
    +   [foreach, end](#foreach)
    +   [do, end](#doend)
    +   [do, until](#dountil)
    +   [while, end](#while)
*   [Functions](#functions)
*   [Performance hints](#performance)

*****************************************************************************

### Overview                        {#overview}

Weel is a dynamically typed, stack based programming language running on the
Java(TM) Virtual Machine. Weel source code gets compiled to Java(TM) classes
using a *Runtime* for its dynamic nature.

Weel ...
*   ... is a simple to learn but powerful language
*   ... has anonymous functions and closures
*   ... has type bound dynamic functions *(something like mix-ins/traits for
    Weel's base types)*
*   ... supports multi-threading
*   ... can export pre-compiled binaries
*   ... does not depend on any libraries except the Java(TM) runtime
*   ... can easily be extended by Java(TM) methods
*   ... has built-in OOP functionality
*   ... has a built in unit test framework
*   ... uses UTF-8 for everything string/text related
*   ... is in some situations even faster than [Lua]

*****************************************************************************

### Weel and Runtime                {#weelruntime}

Weel is separated into two main parts:

1.  `Weel` -- The mother class of all runtimes, giving access to a compiler
    and holding all global variables and registered functions.
2.  `Runtime` -- The execution environment unique to each thread and mother
    `Weel` instance.
    
With this model it is possible to run Weel functions safely in a 
multithreading environment *(as long as you don't access global variables)*
and to have different 'sets' of Weel instances each having their unique set 
of globals and functions.

Weel source code gets compiled 'into' a `Weel` instance and can then be used
from a `Runtime` retrieved by `Weel.getRuntime()`.

Each source file gets internally compiled into its own Java(TM) class.

It is possible to compile new sources into a `Weel` at any time. This makes
library loading on demand possible *(if you only use array or OOP functions
in the library)*.

*****************************************************************************

### Types, names and others {#typesandstuff}

#### Types                          {#types}

*   Null: (`null`)
        null

*   Strings: (`string`)
        'Hello world'
        "Hello world"
        'With " inside'
        "With ' inside"
        "With
        line break"
        
    Allowed escape characters:
        \n  \r  \t  \'  \"  \\

*   Numbers: (`number`)
    *   Decimal:
            -10.3
            42
            1.23e-6
    *   Octal:
            0o777
            0O1723
    *   Binary:
            0b11001010
            0B111001010101
    *   Hexadecimal:
            0xdeadbeaf
            0XCafeBabe
            
*   Maps: (`map`)
        {1, 2, 3, 4};
        {"Hello", null, 1, true, println};
        {
            [0] = "Test",
            ["key"] = 42,
            name = "Hund",
            arr = {1, 2, 3}
        };
        { [0] = "Hello", [1] = "test", [2] = null };
    
    Internally Weel differentiates between 'list' and 'map', lists are
    generally much faster than maps. A list is an ordered map which 
    contains only consecutive integer keys starting from `0 `. So only 
    the second map in the code example above is a 'real' map, the others
    are lists. 

*   Functions: (`function`)
        a = println
        
*   Objects: (`object`)
    
    A special type representing a Java Object. This type can't be
    directly manipulated from Weel code. 
    

#### Names                          {#names}

*   Names are not case sensitive, start with a letter and may contain 
    letters, digits and `_`
 
#### Operators                      {#operators}

*   `+` : Addition
*   `-` : Subtraction
*   `*` : Multiplication
*   `/` : Division
*   `%` : Modulo
*   `..` : String concatenation
*   `=` : Assign
*   `==` : Equal
*   `!=` : Not equal
*   `>` : Greater than
*   `>=` : Greater or equal
*   `<` : Less than
*   `<=` : Less or equal
*   `!` : Logical not
*   `||` : Logical or
*   `&&` : Logical and
*   `~` : Binary not
*   `&` : Binary and
*   `|` : Binary or
*   `^` : Binary xor 
*   `.` : Dot (named array indices)
*   `:` : Colon (declaring OOP functions)
*   `::` : Double colon (calling type bound functions)
*   `->` : Arrow (calling OOP functions)
*   `?` : Ternary (`<cond> ? <expr> : <expr>`)
*   `+=, -=, *=, /=, %=, &=, |=, ^=, ..=`

#### Reserved words                 {#reservedwords}

    break,
    case, continue,
    default, do,
    else, elseif, end, exit,
    false, for, foreach, func,
    global,
    if, in,
    local,
    null,
    outer,
    return,
    sub, switch,
    then, this, true,
    until,
    while    

*****************************************************************************

### Program flow control            {#flow}

#### if, elseif, else, end          {#ifelse}

    if <expr> then
        ...
    [elseif <expr> then]
        ...
    [else]
        ...
    end 

An `if` block may contain zero or more `elseif`s and zero or one `else`. The
`else` clause must always be the last.

#### switch, end                    {#switch}
 
    switch(<expr>)
        [case <expr>:]
            ...
            [break]
        [default:]
            ...
            [break]
    end
    
A `switch` block may contain zero or more `case` statements and zero or one
`default` statements. `default` must always be the last statement in a `switch`
block.

#### for, end                       {#for}

    for <var> = <start-expr>, <end-expr>[, <step-expr>] do
        ...
        [break]
        ...
        [continue]
    end
    
`<var>` must be a local variable, if you try to use anything else, a new local
variable with the given name will be created and used.  
The loop goes from `<start-expr>` to (including) `<end-expr>` with an optional
step (`<step-expr>`).

#### foreach, end                   {#foreach}

    foreach [<key-var>,]<value-var> in <map-var> do
        ...
        [break]
        ...
        [continue]
    end

Iterates over the given map.  

#### do, end                        {#doend}

    do
        ...
        [break]
        ...
    end

A one-shot loop. Practical as a structured `goto` replacement and a scope
for local variables.  

#### do, until                      {#dountil}

    do
        ...
        [break]
        ...
        [continue]
        ...
    until <expr>

Loops until `<expr>` evaluates to `true`.  

#### while, end                     {#while}

    while <expr> do
        ...
        [break]
        ...
        [continue]
        ...
    end

Loops while `<expr>` evaluates to `true`.  

*****************************************************************************

### Functions                       {#functions}

Weel differentiates between `functions` *(which return a value)* and `subs`
*(which don't return a value)*:

    func <name>([<arg-name>[,<arg-name>...]])
        ...
        return <expr>
        ...
    end
    
    sub <name>([<arg-name>[,<arg-name>...]])
        ...
        [exit]  // this 'exits' the sub
        ...
    end

#### Anonymous functions

    <expr> = func([<arg-name>[,<arg-name>...]])
        ...
        return <expr>
        ...
    end
    
    <expr> = sub([<arg-name>[,<arg-name>...]])
        ...
        [exit]  // this 'exits' the sub
        ...
    end
      
There's also a shorter syntax available:

    <expr> = @{ [(<arg-name>[,<arg-name>...])]
        ...
        return <expr>
        ...
    }
    
    <expr> = @{ [(<arg-name>[,<arg-name>...])]
        ...
        [exit]  // this 'exits' the sub
        ...
    }

As you can see you can leave out the argument declaration if you don't use
any arguments, and the syntax for functions and subs is identically.

#### Array function examples

    arr = {}
    
    func arr.f()
        return true;
    end
    
    arr.g = @{
        println("Hello world!");
    }
    
    arr2 = {
        test = 
        @{
            println("Another silly test ...");
        },
        add = 
        @{ (a, b)
            return a + b;
        }
    }
    
    arr.g();
    println(arr2.add(21, 21));
    
    
#### OOP function examples

*OOP functions are array functions with some syntactic sugar.*

    clazz = {
        var = "Hello Jim!"
    };
    
    sub clazz:print()
        println(this->var);
    end
    
    clazz->print();
    
    // The above could also be written as:
    clazz.print(clazz);
    // or:
    funcFind("clazz$$print", 1)(clazz);
    // or:
    createCaller = 
        @{(var)
            return 
                @{
                    var->print();
                    // of course you could also use:
                    // var.print(var);
                    // here.
                };
        };
    caller = createCaller(clazz);
    caller();
    // *cough*

#### A more complex example

*This demonstrates the use of type bound functions.*

    // Define our functions (anonymous functions inside a map)
    ms = {
        // size()
        size = 
        @{ (m)
            return size(m);
        },
        // fold(f)
        // a generic list fold function
        // for maps we need to use a 'foreach' loop which
        // would make the fold function more complicated
        fold = 
        @{ (list, fc)
            if list == null || size(list) == 0 then
                return null;
            elseif size(list) == 1 then
                return list[0];
            else
                ret = fc(list[0], list[1]);
                for i = 2, size(list) - 1 do
                    ret = fc(ret, list[i]);
                end
                return ret;
            end
        }
    }
    
    strs = {
        // length()
        length =
        @{ (s)
            return size(s);
        } ,
        // size()
        size =
        @{ (s)
            return size(s);
        } 
    }
    
    // Register functions for type 'map'
    funcReg("map", ms);
    // Register functions for type 'string'
    funcReg("string", strs);
    
    // Create a map (well, it's a list)
    map = {1, 2, 3, 4};

    println(map::size());
    // Outputs: 4

    println("Result: "..map::fold(@{(a, b) return a + b}));
    // Outputs: Result: 10

    println({1, 2, 3}::fold(@{(a, b) return a..b}));
    // Outputs: 123

    println({1, 2, 3}::size());
    // Outputs: 3
    
    println("Hello"::size());
    // Outputs: 5
    
*This demonstrates 'patching' a library.*

    // Say you've go a console library which defines:
    
    console->charAt(x, y, ch, col);
    
    // And you want to simplify your life because most of the
    // time you use the color `0xff00ff` and don't want to type
    // the color every time you call `charAt`.
    // Well, simply 'inject' your 'curried' function by putting
    // the new function somewhere in your code:
    
    sub concole:charAt(x, y, ch)
        console->charAt(x, y, ch, 0xff00ff);
    end
    
    // and you're done
    // A 'dangerous' but very handy possibility to adapt libraries
    // to your needs.
    

*****************************************************************************

### Performance hints               {#performance}

*   At the moment static expressions won't result in a static value,
    so `a = 1 + 2` evaluates to `a = 1 + 2` and not `a = 3`.
*   local and closure variables are faster than global variables.
*   `a = 2 * b` is faster than `a = b + b`.
*   `a += 1` is exactly the same as writing `a = a + 1`, it just is
    less characters to type.
*   a `switch` is not faster than doing the same with `if`, `elseif` 
    and `else`. It just looks better.
*   A Java implementation of a library function might not always be
    faster than a pure Weel function when called from Weel code.
*   Function calling speed (decreasing from top to bottom):
    
    +   Static Weel/Java function calls
    +   Static Java nice function calls
    +   Dynamic calls
    +   Dynamic calls with overload resolving
    +   Type bound functions
    +   Dynamic calls to closure functions
    +   Dynamic calls to closure functions with overload resolving
    
    This is currently a good guess, I'll benchmark this as soon as the
    rest is stable.
    
*****************************************************************************

[Lua]: http://www.lua.org/ "The Programming Language Lua"
Project link: <https://github.com/rjeschke/weel>
