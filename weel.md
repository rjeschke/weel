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
*   [Technical details](#technical)
    +   [Performance](#perft)
    +   [Weel stacks](#stacks)
    +   [Function calling](#funccall)
    +   [Anonymous functions](#anonfuncst)
    +   [Internal function naming](#intnames)
    +   [Compilation](#compilation)
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
    the third map in the code example above is a 'real' map, the others
    are lists. 

*   Functions: (`function`)
        a = println
        
*   Objects: (`object`)
    
    A special type representing a Java Object. This type can't be
    directly manipulated from Weel code. 
    
*	Booleans:

	Weel does not have a real boolean type. `true` evaluates to `-1` and
	`false` evaluates to `0`. See below to see how different types and
	values map to `true` and `false`:
	
	+	Null: is always `false`
	+	Numbers: `0.0` is `false`, everything else is `true`
	+	Strings: "" is `false`, everything else is `true`
	+	Map: A map with a size of `0` is `false`, everything else is `true`
	+	Function: is always `true`
	+	Object: a Java(TM) `null` value is `false`, everything else is `true`

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
*   `++` : Map concatenation
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
        ...
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
        ...
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

#### Static global functions

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

Weel supports function overloading for static, array and OOP functions. 
The correct function is determined by name and number of arguments. So it
is possible to have an overloaded function taking a differing number of
arguments that returns a value, even if all others don't.
 
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

### Technical details               {#technical}

#### Performance                    {#perft}

The reason why I rewrote Yjasl4 was **speed**. After some days of thinking
about the new Weel VM I decided to give direct JVM bytecode generation a try.
The first test, compiling Weel source code by 'hand' as calls to the runtime
in a Java(TM) method showed the potential this approach offered. The speed
increase compared to Yjasl4 is about a factor of 3 to 6, depending on the
code used.

Another benefit of the new compilation method is that I have more freedom to
develop new language features. In Yjasl4 adding a new bytecode could result in
large decrease in performance, so I had to choose wisely which bytecode I *really*
needed.

In Weel, I can add as many 'bytecodes' as I want, without sacrificing speed (which
already resulted in lots of new features and possibilities).

The main design goals for Weel were/are:
*   Lots of small runtime methods
*   Minimized number constants storage in class files (not everything gets
    really stored as a `double`)
*   Avoid object creations wherever possible
*   Having an API for Java(TM) Weel libraries which also makes it possible
    to retrieve the needed value in raw methods without object creations
*   Multithreading support

If you have a look at the runtime class, you won't see many `new` operators.
Values on the stack get 'copied', not cloned, except for the case when the user
really needs a `Value`, then I have to clone it.

#### Weel stacks                    {#stacks}

1.  The `Value` stack:

    The Value stack is used for all Weel operations. The Runtime avoids costly 
    Object creations by copying the values inside the stack and only cloning 
    values if they need to leave the safe space inside the Runtime (e.g. when 
    calling `Runtime.pop()`). The Runtime offers also various methods for
    library programmers to avoid value cloning by querying a direct value (e.g.
    `double Runtime.popNumber()` or `String Runtime.popString()`.

2.  The *'Frame'* stack:

    This is used for function frames to prepare function arguments and reserve
    space for local variables. In fact it only holds the position of the first
    argument or local variable and a frame size.
    
3.  The *'Virtual Function'* stack:

    This one is used by anonymous functions containing closure variables and
    holds a reference to the currently executed function and its closure
    variables.

#### Function calling               {#funccall}

There are four different types of function calls:

1.  Static calls:

    Every call to a static Weel function or a Java function is a static call
    which gets directly compiled into bytecode.
    
2.  Dynamic calls (aka stack call):

    The function to call resides on the stack and has to be called using an 
    *invoker* which at the moment uses Reflection to perform its task. As a
    dynamic call has every information about the call that it need (name, 
    number of arguments, and returns-value flag) it can resolve overloaded
    functions and prepares the stack to match the situation (e.g. if a 
    function returns a value and the context doesn't need one the return 
    value gets discarded).
    
    A stack call only tries to resolve an overloaded function if the expected
    argument count does not match the amount defined by the function on the
    stack.
    
3.  Anonymous closure function calls:

    These work exactly like stack calls (because they are stack calls) but a
    different invoker method is used which registes the function on the 
    virtual function stack.
    
4.  Type bound functions:

    This is a special type of a stack call, which is always dynamic. The 
    runtime examines the stack to determine the type to call the function on.
    Then it resolves the correct function to call by looking it up using the
    type, the name (which is supplied as a String on the JVM stack) and the 
    number of arguments.
 
#### Anonymous functions            {#anonfuncst}

Anonymous functions are nameless static functions which don't get registered
in the Weel (that's why you can't uses overloading on anonymous functions).

Declaring an anonymous function without closure variables only results in a
Runtime.load(...) operation, loading the anonymous function (which is already
compiled) by its function index.

This behavious changes when closure variables are needed. Here every expression
which creates such a function also creates a new object with a snapshot of its
used outer variables.

#### Internal function naming       {#intnames}

To differentiate between different kinds of functions, three types of function
names are used:

1.  Static functions are named as you specified it, so `func test(a, b)` will
    be named `test`.
2.  Array functions use the name of the array and the function's name 
    concatenated by `$`, so `func arr.test(a, b)` will be named `arr$test`.
3.  OOP functions use the name of the array and the function's name 
    concatenated by `$$`, so `func arr:test(a, b)` will be named `arr$$test`.

Anonymous functions all use the same static name `ANON`.
    
#### Compilation                    {#compilation}

The Weel compiler does not do any Voodoo, it mostly chains calls to runtime
methods, generates method calls and some IFEQs, IFNEs and GOTOs. It seems
that weel performes so well because of the JIT compiler loving small methods. 

So it's all a little bit cheating ... but it works ... and it's fast. 

*****************************************************************************

### Performance hints               {#performance}

*   At the moment static expressions won't result in a static value, so
    `a = 1 + 2` evaluates to `a = 1 + 2` and not `a = 3`.
*   local and closure variables are faster than global variables.
*   `a = 2 * b` is faster than `a = b + b`.
*   `a += 1` is exactly the same as writing `a = a + 1`, it just is less 
    characters to type.
*   a `switch` is not faster than doing the same with `if`, `elseif` and `else`,
    it just looks better.
*   A Java implementation of a library function might not always be faster than
    a pure Weel function when called from Weel code.
*   If you use overloading with array or OOP functions try to define the 
    overloaded type you will probably use the most after all other overloading
    variations. This will save you some costly runtime overload resolving 
    operations.
*   Function calling speed (decreasing from top to bottom):
    
    +   Static Weel/Java function calls
    +   Static Java nice function calls
    +   Dynamic calls
    +   Type bound functions
    +   Dynamic calls with overload resolving
    +   Dynamic calls to closure functions
    
    This is currently a good guess, I'll benchmark this as soon as the
    rest is stable.
    
*****************************************************************************

[Lua]: http://www.lua.org/ "The Programming Language Lua"
Git repository: <https://github.com/rjeschke/weel>
