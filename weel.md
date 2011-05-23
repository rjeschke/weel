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
*   [Storage and lifetime](#storage)
*   [Program flow control](#flow)
    +   [if, elseif, else, end](#ifelse)
    +   [switch, end](#switch)
    +   [for, end](#for)
    +   [foreach, end](#foreach)
    +   [do, end](#doend)
    +   [do, until](#dountil)
    +   [while, end](#while)
*   [Functions](#functions)
*   [Extending Weel](#extending)
    +   [Static functions](#extstatic)
    +   [Array and OOP functions](#arroop)
*   [Technical details](#technical)
    +   [Performance](#perft)
    +   [Weel stacks](#stacks)
    +   [Function calling](#funccall)
    +   [Anonymous functions](#anonfuncst)
    +   [Internal function naming](#intnames)
    +   [Compilation](#compilation)
*   [Performance hints](#performance)
*   [Benchmarks](#bench)

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
*   ... can export pre-compiled binaries (not now, but it will)
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
2.  `WeelRuntime` -- The execution environment unique to each thread and mother
    `Weel` instance.
    
With this model it is possible to run Weel functions safely in a 
multithreading environment *(as long as you don't access global variables)*
and to have different 'sets' of Weel instances each having their unique set 
of globals and functions.

Weel source code gets compiled 'into' a `Weel` instance and can then be used
from a `WeelRuntime` retrieved by `Weel.getRuntime()`.

Each source file gets internally compiled into its own Java(TM) class.

It is possible to compile new sources into a `Weel` at any time. This makes
library loading on demand possible *(if you only use array or OOP functions
in the library)*.

*****************************************************************************

### Types, names and others         {#typesandstuff}

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
        
    As functions may be assigned to e.g. variables, the following code
    fragment *really* works:
    
        // Don't try this at home^^
        
        f = 
        @{ (a)
          return
           {
            [5] = 
            @{ (b)
              local z = {};
              z[b] = 
              @{
                return 
                 {
                  [2] = 
                   {
                    fin = 
                    @{ (v)
                      return v * 2;
                     }
                   }
                 };
               };
              return z;
             }
           };
         };
        
        println(f(10)[5]("hund").hund()[2].fin(21));
        // Outputs: 42
        
*   Objects: (`object`)
    
    A special type representing a Java(TM) Object. This type can't be
    directly manipulated from Weel code. 
    
*   Booleans:

    Weel does not have a real boolean type. `true` evaluates to `-1` and
    `false` evaluates to `0`. See below to see how different types and
    values map to `true` and `false`:
    
    +   Null: is always `false`
    +   Numbers: `0.0` is `false`, everything else is `true`
    +   Strings: "" is `false`, everything else is `true`
    +   Map: A map with a size of `0` is `false`, everything else is `true`
    +   Function: is always `true`
    +   Object: a Java(TM) `null` value is `false`, everything else is `true`

#### Names                          {#names}

*   Names are not case sensitive, start with a letter and may contain 
    letters, digits and `_` (underscore)
 
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
*   `>>` : Binary signed right shift 
*   `>>>` : Binary unsigned right shift 
*   `<<` : Binary left shift 
*   `.` : Dot (named array indices)
*   `:` : Colon (declaring OOP functions)
*   `::` : Double colon (calling type bound functions)
*   `->` : Arrow (calling OOP functions)
*   `?` : Ternary (`<cond> ? <expr> : <expr>`)
*   `+=, -=, *=, /=, %=, &=, |=, ^=, ..=, ++=, >>=, <<=, <<<=`

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

### Storage and lifetime            {#storage}

By default all variables are local and every static function is global. If you 
want to override the default storage for variables use one of the following 
keywords:
 
*   `local <name>[ = expr[,<name>...]]`

    The 'local' keyword forces a variable to be local:
    
        local i;
        i = 1;
        do
            local i = 2;
            i += 1;
            println(i);
            // Outputs: 3
        end
        
        println(i);
        // Outputs: 1

*   `global <name>[ = expr[,<name>...]]`

    The 'global' keyword registers a global variable inside the Weel. This
    variable is accessible from every script compiled into this Weel instance.
    
        sub init()
            global my = "global";
        end
        
        println(my);
        // Outputs: null
        init();
        println(my);
        // Outputs: global

*   `private <name>[ = expr[,<name>...]]`

    The 'private' keyword acts like 'global', but instead of making the
    variable visible for the whole Weel instance, it is only visible to
    the current script.

*   `outer <name> = expr[,<name> = expr, ...]`

    The keyword 'outer' declares an initialized closure variable inside
    an anonymous function:
    
        f = 
            @{
                outer counter = -1;
                return counter += 1;
             };
        
        println(f());
        // Outputs: 0
        println(f());
        // Outputs: 1
        
    The following code demonstrates the way 'outer' works by showing how
    to achieve the above function definition without using 'outer':
    
    
        f = null;
        do
            local counter = -1;
            f = 
                @{
                    return counter += 1;
                 };
        end
        
        
Variable search order:

1.  Locals and closure variables
2.  Private variables
3.  Global variables

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
 
    switch <expr> do
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
    
    arr.g = 
        @{
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
             },
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
    // Well, it's not that dangerous, because you can not override
    // any function, i.e. you can't 
    

*****************************************************************************

### Extending Weel                  {#extending}

#### Static functions               {#extstatic}

Weel uses a simple mechanism to import Java(TM) methods for usage from Weel
code:

    public void Weel.importFunctions(Class<?> clazz)
    
Weel only supports `static` methods, so you just have to supply the class
you want to import.

You can use two different ways to write a function for Weel:
     
1.  Using the `WeelRawMethod` annotation:

    Example:
    
        // strindex(a, b, i)
        @WeelRawMethod(name = "strindex", args = 3, returnsValue = true)
        public final static void strIndex3(final WeelRuntime runtime)
        {
            final int i = (int) runtime.popNumber();
            final String b = runtime.popString();
            final String a = runtime.popString();
            runtime.load(a.indexOf(b, i));
        }

    If you don't specify `name`, the name of the method will be used,
    `args` defaults to `0` and `returnsValue` defaults to `false`.
    
    As you can see, you have to 'pop' the arguments from the Weel stack (in
    reverse order). When you use the `pop<Type>` runtime functions Weel
    will throw an Exception if the value is not of the requested type.
    
    The `load` methods are used to push a return value onto the Weel stack.
    
    Make sure you don't mess around with the stack: Only pop as many arguments
    as you need, and don't push a return value if non is needed/declared.

2.  Using the `WeelMethod` annotation ('nice' methods):

    Example using WeelRawMethod:
    
        // pow(a, b)
        @WeelRawMethod(name = "pow", args = 2, returnsValue = true)
        public final static double pow(final WeelRuntime runtime)
        {
            final double b = runtime.popNumber();
            final double a = runtime.popNumber();
            runtime.load(Math.pow(a, b));
        }

    The same using WeelMethod:

        // pow(a, b)
        @WeelMethod(name = "pow")
        public final static double pow(double a, double b)
        {
            return Math.pow(a, b);
        }

    If you don't specify `name`, the name of the method will be used.
    
    As you can see, this is much simpler than doing it using the 'raw'
    approach. Weel takes care of your values, does type checks and
    casts things around.
    
    Auto-mapped types:
    
        Java(TM)        <=> Weel
        ----------------------------------
        double          <=> NUMBER
        int             <=> NUMBER (casted to/from double)
        boolean         <=> Value.toBoolean()
        String          <=> STRING/NULL
        Value           <=> Value
        ValueMap        <=> MAP/NULL
        WeelFunction    <=> FUNCTION/NULL
        
    All other types are treated as OBJECT and casted to the desired type.
    
    If you need access to the runtime in a 'nice' method, just specify
    a parameter of type `WeelRuntime`. This parameter will receive the current
    runtime instance and won't appear as an argument in the Weel function:

        // funcFind(name, args)
        @WeelMethod
        public final static WeelFunction funcFind(WeelRuntime rt, String name, int args)
        {
            return rt.getMother().findFunction(name, args);
        }

    The `load` methods in the runtime will automatically load `NULL` if you
    try to load a String, Value, ValueMap or WeelFunction which is `null`.
    
    **Technical information:** When registering 'nice' methods, a wrapper
    Java(TM) method is created, which takes care of parameter mappings, so this
    is not resolved dynamically during runtime. Calling 'nice' methods is about
    10% slower than calling 'raw' methods.
    
#### Array and OOP functions        {#arroop}

To create array and OOP functions you only have to tag the enclosing Java(TM)
class with `WeelClass`:

    @WeelClass(name = "myclazz", usesOop = true, isPrivate = false)
    public class MyClass
    {
        ...
    }

If you don't specify `name`, the simple name of the class will be used,
`usesOop` and `isPrivate` both default to `false`.

Members are declared like 'static' weel functions with `WeelRawMethod` or
`WeelMethod`.

The `usesOop` parameter is (right now) nothing more than a hint to indicate
which naming schema should be used (see [Internal function naming](#intnames)).

When `isPrivate` is `false`, a global variable named `name` will be created
and initialized with an empty array, which then receives all defined functions. 

If `isPrivate` is `true`, then no global variable will be created. The ValueMap
which holds all the functions gets instead assigned to a static variable
inside the class:

    public static ValueMap ME;
    
This allows for easy 'invisible' class definition, which may get 'instantiated'
using other methods/functions.

**Remember:** When you use Oop functions (called using `->`) you will always
get the ValueMap (`this`) as the first parameter.

Here's a simple example, wrapping a StringBuilder in a Weel class:

    import com.github.rjeschke.weel.*;
    import com.github.rjeschke.weel.annotations.*;
    
    @WeelClass(name = "java.lang.StringBuilder", usesOop = true)
    public class MyStringBuilder
    {
        @WeelMethod
        public final static void ctor(final ValueMap thiz)
        {
            WeelOop.setInstance(thiz, new StringBuilder());
        }
        
        @WeelMethod
        public final static void append(final ValueMap thiz, final Value value)
        {
            final StringBuilder sb = WeelOop.getInstance(thiz, StringBuilder.class);
            sb.append(value.toString());
        }
    
        @WeelMethod(name = "toString")
        public final static String sbToString(final ValueMap thiz)
        {
            final StringBuilder sb = WeelOop.getInstance(thiz, StringBuilder.class);
            return sb.toString();
        }
    }
    
And here's how you would use it from Weel:

    sb = new(java.lang.StringBuilder);
    
    sb->append("Hello");
    sb->append(" world!");
    
    println(sb->toString());


Using `name = "java.lang.StringBuilder"` in the above example as the class
name results roughly in the following expression generated by Weel (well, it
does not really get generated, but it explains how it works):

    global java = 
    {
        lang = 
        {
            stringbuilder = {...}
        }
    };

If you now would also import a class which uses e.g. 'java.lang.String' as its
name, Weel would act like this:

    java.lang.string = {...}


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
    calling `WeelRuntime.pop()`). The runtime offers also various methods for
    library programmers to avoid value cloning by querying a direct value (e.g.
    `double WeelRuntime.popNumber()` or `String WeelRuntime.popString()`.

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

    Every call to a static Weel function or a Java(TM) function is a static call
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
    different invoker method is used which registers the function on the 
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
WeelRuntime.load(...) operation, loading the anonymous function (which is already
compiled) by its function index.

This behaviour changes when closure variables are needed. Here every expression
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
that weel performs so well because of the JIT compiler loving small methods. 

So it's all a little bit cheating ... but it works ... and it's fast. 

*****************************************************************************

### Performance hints               {#performance}

*   Static expressions:
    +   `a = 1 + 2 * 3` gets compiled as `a = 7`
    +   `a = b + 2 * 3` gets compiled as `a = b + 6`
    +   `a = 1 + b + 2` gets compiled as `a = 1 + b + 2`
    
    No term reordering is performed, so be careful which expressions you use
*   Reorder your expressions: `a = b * 2` is faster than `a = 2 * b`. Weel
    generates special calls for these cases (currently for `+`, `-`, `*`, `/`
    and `%`)
*   local and closure variables are faster than global variables.
*   `a = 2 * b` is faster than `a = b + b` (and `a = b * 2` is even faster).
*   `a += 1` is exactly the same as writing `a = a + 1` --- it just is less 
    characters to type.
*   a `switch` is not faster than doing the same with `if`, `elseif` and `else`
    --- it just looks better.
*   If you use overloading with array or OOP functions try to define the 
    overloaded type you will probably use the most after all other overloading
    variations. This will save you some costly runtime overload resolving 
    operations.
*   Function calling speed (decreasing from top to bottom):
    
    +   Static Weel/Java(TM) function calls
    +   Static Java(TM) nice function calls
    +   Dynamic calls
    +   Dynamic calls with overload resolving
    +   Type bound functions
    
    This is currently a good guess, I'll benchmark this as soon as the
    rest is stable.

*****************************************************************************

### Benchmarks                      {#bench}

Here are some benchmarks based on <http://shootout.alioth.debian.org/>, JRuby 
and Weel both run on Java(TM) SE Runtime Environment (build 1.6.0_24-b07) and 
the Java HotSpot(TM) Server VM (build 19.1-b02, mixed mode). JRuby is started 
with `--server` and Weel with `-Xmx500M` to allow for fair comparison to JRuby
which uses the same memory settings per default.

Benchmarked on an Athlon X2 2500MHz, Ubuntu x86 10.04, 2GiB RAM. Result is
running time in seconds (full, i.e. creating the process, compiling and executing).
The second column shows how much faster (`+`) or slower (`-`) the benchmarked
version is compared to Weel (so Weel is always `~ 1.00`). 

*Info:* alioth.debian.org is no more, I'll provide a new link to the 'Language
Benchmark Game' if one is available. Meanwhile you can have a look at the 
benchmarks at <https://github.com/kragen/shootout>.

<table style="width: 100%; font-family: Monospace;">
 <tr>
   <th style="text-align: left;">Benchmark</th>
   <th colspan="2">Weel</th>
   <th colspan="2">JRuby 1.6.1</th>
   <th colspan="2">Lua 5.1.4</th>
   <th colspan="2">V8 3.3.8.1</th>
   <th colspan="2">Jaeger 1.8.5+</th>
 </tr>
 <tr>
  <td>mandel (500)</td>
  <td style="text-align: right;"> 1.11</td><td>~ 1.00</td>
  <td style="text-align: right;"> 4.96</td><td>- 4.46</td>
  <td style="text-align: right;"> 1.10</td><td>+ 1.01</td>
  <td style="text-align: right;"> 0.99</td><td>+ 1.12</td>
  <td style="text-align: right;"> 7.89</td><td>- 7.10</td>
 </tr>
 <tr>
  <td>mandel (2000)</td>
  <td style="text-align: right;">10.48</td><td>~ 1.00</td>
  <td style="text-align: right;">57.21</td><td>- 5.46</td>
  <td style="text-align: right;">17.46</td><td>- 1.67</td>
  <td style="text-align: right;">14.83</td><td>- 1.42</td>
  <td style="text-align: right;">126.33</td><td>- 12.06</td>
 </tr>
 <tr>
  <td>mandel (4000)</td>
  <td style="text-align: right;">42.24</td><td>~ 1.00</td>
  <td style="text-align: right;">218.05</td><td>- 5.16</td>
  <td style="text-align: right;">71.35</td><td>- 1.69</td>
  <td style="text-align: right;">55.62</td><td>- 1.32</td>
  <td style="text-align: right;">499.05</td><td>- 11.81</td>
 </tr>
 <tr>
  <td>spectral-norm (500)</td>
  <td style="text-align: right;"> 2.21</td><td>~ 1.00</td>
  <td style="text-align: right;">12.50</td><td>- 5.67</td>
  <td style="text-align: right;"> 3.37</td><td>- 1.53</td>
  <td style="text-align: right;"> 0.42</td><td>+ 5.20</td>
  <td style="text-align: right;"> 7.94</td><td>- 3.60</td>
 </tr>
 <tr>
  <td>spectral-norm (1000)</td>
  <td style="text-align: right;"> 7.23</td><td>~ 1.00</td>
  <td style="text-align: right;">43.19</td><td>- 5.97</td>
  <td style="text-align: right;">13.54</td><td>- 1.87</td>
  <td style="text-align: right;"> 1.29</td><td>+ 5.60</td>
  <td style="text-align: right;">31.80</td><td>- 4.40</td>
 </tr>
 <tr>
  <td>spectral-norm (2000)</td>
  <td style="text-align: right;">26.98</td><td>~ 1.00</td>
  <td style="text-align: right;">167.90</td><td>- 6.22</td>
  <td style="text-align: right;">53.74</td><td>- 1.99</td>
  <td style="text-align: right;"> 4.94</td><td>+ 5.46</td>
  <td style="text-align: right;">126.57</td><td>- 4.69</td>
 </tr>
 <tr>
  <td>n-body (50000)</td>
  <td style="text-align: right;"> 1.41</td><td>~ 1.00</td>
  <td style="text-align: right;"> 3.39</td><td>- 2.41</td>
  <td style="text-align: right;"> 0.70</td><td>+ 2.02</td>
  <td style="text-align: right;"> 0.15</td><td>+ 9.25</td>
  <td style="text-align: right;"> 1.98</td><td>- 1.41</td>
 </tr>
 <tr>
  <td>n-body (500000)</td>
  <td style="text-align: right;"> 8.06</td><td>~ 1.00</td>
  <td style="text-align: right;">16.89</td><td>- 2.09</td>
  <td style="text-align: right;"> 6.75</td><td>+ 1.19</td>
  <td style="text-align: right;"> 0.76</td><td>+ 10.65</td>
  <td style="text-align: right;">19.13</td><td>- 2.37</td>
 </tr>
 <tr>
  <td>n-body (5000000)</td>
  <td style="text-align: right;">74.01</td><td>~ 1.00</td>
  <td style="text-align: right;">154.34</td><td>- 2.09</td>
  <td style="text-align: right;">67.52</td><td>+ 1.10</td>
  <td style="text-align: right;"> 6.95</td><td>+ 10.64</td>
  <td style="text-align: right;">215.97</td><td>- 2.92</td>
 </tr>
 <tr>
  <td>binary-trees (12)</td>
  <td style="text-align: right;"> 2.34</td><td>~ 1.00</td>
  <td style="text-align: right;"> 2.35</td><td>- 1.00</td>
  <td style="text-align: right;"> 1.27</td><td>+ 1.84</td>
  <td style="text-align: right;"> 0.13</td><td>+ 17.76</td>
  <td style="text-align: right;"> 1.90</td><td>+ 1.23</td>
 </tr>
 <tr>
  <td>binary-trees (14)</td>
  <td style="text-align: right;"> 6.19</td><td>~ 1.00</td>
  <td style="text-align: right;"> 4.76</td><td>+ 1.30</td>
  <td style="text-align: right;"> 6.47</td><td>- 1.05</td>
  <td style="text-align: right;"> 0.34</td><td>+ 18.00</td>
  <td style="text-align: right;"> 8.79</td><td>- 1.42</td>
 </tr>
 <tr>
  <td>binary-trees (16)</td>
  <td style="text-align: right;">29.32</td><td>~ 1.00</td>
  <td style="text-align: right;">15.42</td><td>+ 1.90</td>
  <td style="text-align: right;">31.82</td><td>- 1.09</td>
  <td style="text-align: right;"> 1.46</td><td>+ 20.03</td>
  <td style="text-align: right;">41.02</td><td>- 1.40</td>
 </tr>
 <tr>
  <td>thread-ring (5000)</td>
  <td style="text-align: right;"> 2.84</td><td>~ 1.00</td>
  <td style="text-align: right;"> 1.14</td><td>+ 2.48</td>
  <td style="text-align: right;">---</td><td>---</td>
  <td style="text-align: right;">---</td><td>---</td>
  <td style="text-align: right;">---</td><td>---</td>
 </tr>
 <tr>
  <td>thread-ring (50000)</td>
  <td style="text-align: right;"> 3.60</td><td>~ 1.00</td>
  <td style="text-align: right;"> 1.80</td><td>+ 2.00</td>
  <td style="text-align: right;">---</td><td>---</td>
  <td style="text-align: right;">---</td><td>---</td>
  <td style="text-align: right;">---</td><td>---</td>
 </tr>
 <tr>
  <td>thread-ring (500000)</td>
  <td style="text-align: right;"> 9.96</td><td>~ 1.00</td>
  <td style="text-align: right;"> 8.19</td><td>+ 1.22</td>
  <td style="text-align: right;">---</td><td>---</td>
  <td style="text-align: right;">---</td><td>---</td>
  <td style="text-align: right;">---</td><td>---</td>
 </tr>
</table>

*****************************************************************************

[Lua]: http://www.lua.org/ "The Programming Language Lua"
Git repository: <https://github.com/rjeschke/weel>
