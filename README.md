# Weel - A fast and extensible programming language for Java
Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>  
See LICENSE.txt for licensing information.

***

[Weel] is a dynamically typed, case insensitive, imperative and slightly 
functional and object oriented programming language running on the Java(TM)
virtual machine.

It's main goal is to provide a simple and powerful scripting language
for Java with mechanisms for easy interaction with Java code
and without having to bind to any native libraries.

Weel's syntax is heavily based on [Lua], though Weel is not Lua. There
are minor and major differences between Lua and Weel, but if you know
Lua, it won't be hard to get started with Weel. The syntax also contains
elements from C, C++, Java and functional programming languages.

As usual the term 'fast' is relative. Weel is of course much slower than
doing the same things in pure Java, but it beats most other existing 
dynamically typed languages running on the JVM.

### Development state

*still-beta*

### Features

*   Can easily be extended with Java functions (using annotations)
*   Easy mechanisms for mapping Java classes to Weel 'classes'
*   Multithreading support
*   Fully written in Java, no external dependencies
*   Unit test framework

For more informations have a look [here](http://rjeschke.github.com/weel/).

### TODO

*   Var-args
*   Call anonymous functions without assigning them (e.g. @{println "Doh!"}(); )
*   Finish standard library (Map/IO functions)
*   Add a security mechanism to control which libraries/functions get loaded
*   Add mechanism for user library registering and loading on demand
*   Map some more useful Java classes to Weel
*   Library documentation
*   Maybe create a 'weel-library' project on github to give users a chance
    to extend Weel in a moderated way?

### Build instructions

As usual a

    ant release
    
creates the jars in the 'release' folder.

### Usage

1.  Usage from a terminal

    You can invoke Weel from a terminal using the following line:
    
        java -cp <path-to-weel-jar-or-build-classes> com.github.rjeschke.weel.Run <args>
        
        <args>:  <script> [<script> ...] [--debug] [--dump] [-- args]

    Run searches for a sub/func called main, with 0 or 1 arguments, which gets called (if
    exists) after all static code was run. The arguments (all after `--`) will be supplied
    as a list.
    
    *   `--debug` enables asserts (which would otherwise be stripped away)
    *   `--dump` dumps the generated intermediate code in human readable form to stdout
    
2.  Usage from Java

        Weel weel = new Weel();

        // Compile also takes InputStreams and compileResource accepts a 'resource name',
        // e.g. if you have a script inside you code in 'my.scripts' called 'Scripts.weel'
        // you can simply use: weel.compileResource("my.scripts.Scripts");
        weel.compile("println('Hello world!')");

        weel.runStatic(); 

### Examples

#### Type bound and anonymous functions

    // Semicolons aren't needed, but allowed
    ms = {};
    
    // Generic list fold function
    func ms:fold(fc)
        // Asserts get stripped when not compiled in debug mode
        assert(isMap(this),
            "Value is not a map");
        
        if size(this) == 0 then
            return null;
        elseif size(this) == 1 then
            return this[0];
        else
            ret = fc(this[0], this[1]);
            for i = 2, size(this) - 1 do
                ret = fc(ret, this[i]);
            end
            return ret;
        end
    end 
    
    // Generic populate function
    func ms:populate(sz, fc)
        assert(sz > 0,
            "Size is zero or less");
        assert(isMap(this),
            "Value is not a map");
        assert(funcCheck(fc, 1, true) || funcCheck(fc, 2, true), 
            "Illegal populator function signature");
         
        if funcArgs(fc) == 2 then
            for i = 0, sz - 1 do
                this[] = fc(this, i);
            end
        else
            for i = 0, sz - 1 do
                this[] = fc(i);
            end
        end
        return this;
    end
    
    // Generic filter function    
    func ms:filter(fc)
        assert(funcCheck(fc, 1, true),
            "Illegal filter function.");
        assert(isMap(this),
            "Value is not a map");
        
        ret = {};
        foreach v in this do
            if fc(v) then
                ret[] = v;
            end
        end
        return ret;
    end
    
    funcReg("map", ms);
    
    println(
        {}
            ::populate(9, @{(i) return i + 1})
            ::filter(
                @{ (v) 
                    return (v % 3) && (v % 5) ? false : true;
                 })
            ::fold(
                @{ (a, b) 
                    return a + b;
                 })
           );


#### Wrapping 'java.lang.StringBuilder'

##### Java code

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
    
        @WeelMethod
        public final static void clear(final ValueMap thiz)
        {
            final StringBuilder sb = WeelOop.getInstance(thiz, StringBuilder.class);
            sb.setLength(0);
        }
    
        @WeelMethod(name = "toString")
        public final static String sbToString(final ValueMap thiz)
        {
            final StringBuilder sb = WeelOop.getInstance(thiz, StringBuilder.class);
            return sb.toString();
        }
    }

##### Import the functions into Weel

    Weel weel = new Weel();

    // ...

    weel.importFunctions(MyStringBuilder.class);
    
    // ...

##### Usage from Weel

    sb = new(java.lang.StringBuilder);
    
    sb->append("Hello");
    sb->append(" world!");
    
    println(sb->toString());


***

[Lua]: http://www.lua.org/ "The Programming Language Lua"
[Weel]: http://rjeschke.github.com/weel/ "Weel at rjeschke.github.com"

[$PROFILE$]: extended "Txtmark processing information."

Project link: <https://github.com/rjeschke/weel>
