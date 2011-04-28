# Weel - An extensible scripting language
Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>  
See LICENSE.txt for licensing information.

***

### Weel (aka Yjasl5) is a scripting language for Java

The first Yjasl (Yet just another scripting language) version
was created somewhere around 2005 (in C#). A lot of minor and
major changes followed. Versions 2 and 3 where also done in
C#, 2.5 was an attempt to translate Yjasl to C.

In 2009 I rediscovered Java (the last Java version I worked on
was 1.4.x) and finally started to say goodbye to Microsoft only
solutions. Some time later Yjasl4 was born. 

The current version might be called Yjasl5 because it is again
a major revision, but as it is said that I'm always reinventing
the wheel, I decided to name this version *Weel*. (I left out the
'h' to make it a bit less obvious ;) ).

Yjasl versions 1, 3 and 4 have not been published anywhere, Yjasl2
may be viewed [here] (http://yjasl.sourceforge.net/ "This sf.net repo is dead."). 

Large parts of the language changed until today, so the facts and manual
given on the SourceForge page are only valid for Yjasl2. Still you will
get an overview of Weel's features.

### Architecture

Weel is a stack based, weakly-typed language with a syntax similar 
to [Lua] and Basic. (At the time I wrote Yjasl1 I worked heavily 
with [Lua] for creating [Enigma] levels. That's why there's so many
syntax similarities.)

All previous versions compiled to a very simple byte code (less
than 40 opcodes) and got interpreted by a *VirtualMachine* which was
in fact a `for`-loop and a `switch`.

This version compiles directly to Java byte code using only a simple 
runtime class for its operation. 

### Features

*   Fully written in Java without any external dependencies
*   Hand-written compiler and tokenizer
*   Easily extendible by Java methods 
*   Multithreading support with simple locking mechanisms
*   Basic OOP functionality (by using maps)
*   Possibility to export the compiled scripts to avoid making
    your script sources publicly viewable  

### Current development status

*pre-alpha*

This is just a rough outline of the framework.


***

[Lua]: http://www.lua.org/ "The Programming Language Lua"
[Enigma]: http://www.nongnu.org/enigma/ "Enigma is a puzzle game inspired by Oxyd on the Atari ST and Rock'n'Roll on the Amiga"

[$PROFILE$]: extended "Txtmark processing information."

Project link: <https://github.com/rjeschke/weel>
