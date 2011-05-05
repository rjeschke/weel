# Weel - An extensible scripting language
Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>  
See LICENSE.txt for licensing information.

***

### Weel (aka Yjasl5) is a scripting language for Java

The first Yjasl (Yet just another scripting language) version
was created somewhere around 2005 (in C#). A lot of minor and
major changes followed. Versions 2 and 3 where also done in
C#, 2.5 was an attempt to translate Yjasl to C.

In 2008 I rediscovered Java (the last Java versions I worked with
were 1.3.x and 1.4.x) and finally started to say goodbye to Microsoft-only
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

Weel is a stack based, dynamically typed language with a base syntax 
similar to [Lua] and using elements from various other languages. (At 
the time I wrote Yjasl1 I worked heavily with [Lua] for creating [Enigma] 
levels. That's why there's so many syntax similarities.)

All previous versions compiled to a very simple byte code (less
than 40 opcodes) and got interpreted by a *VirtualMachine* which was
in fact a `for`-loop and a `switch`.

This version compiles directly to Java byte code using only a simple 
runtime class for its operation. *(Which decreases the execution time
by a factor of 6 to 10 compared to Yjasl4, and now Weel doesn't need
to hide away from other dynamically typed languages.)*

### Features

*   Fully written in Java without any external dependencies
*   Hand-written compiler and tokenizer
*   Easily extendible by Java methods 
*   Multithreading support with a simple locking mechanism to protect
    global variable accessing 
*   Basic OOP functionality (by using maps)
*   Closures and anonymous functions
*   Possibility to export the compiled scripts to avoid making
    your script sources publicly viewable  
*   ... and much more

### Current development status

*alpha*

Most of the stuff is working, still a lot missing though.

### How would Weel source code look like?

	func fold(list, fc)
		if size(list) == 0 then
			return null
		elseif size(list) == 1 then
			return list[0]
		else
			ret = fc(list[0], list[1])
			for i = 2, size(list) - 1 do
				ret = fc(ret, list[i])
		    end
		    return ret
		end
	end

	ilist = {1, 2, 3, 4}
	slist = {"Hello", "world!"}
	
	println("Result: "..fold(ilist, func(a, b) return a + b; end));
	// OUTPUT: Result: 10
	
	println("Result: "..fold(slist, 
		func(a, b) 
			return a.." "..b
		end));
	// OUTPUT: Result: Hello world!
	
	func create(var)
		return 
			sub()
				print(var)
			end
	end

	f0 = create("Foo");
	f1 = create("Bar");
	f0() print(" ")	f1() println()
	// OUTPUT: Foo Bar
	
	clazz = {}
	clazz.var = "Hello world!"
	
	sub clazz:println()
		println(this->var)
	end
	
	my = new(clazz)
	my->var = "Hello John!"
	my->println()
	// OUTPUT: "Hello John!"
	
	clazz->println()
	// OUTPUT: Hello world!

	config = {
		user = {
			name = "John Doe"
		},
		prefs = {
			nocolors = false,
			verbose = true,
			shell = "/bin/bash"
		},
		flags = { 0, 3, -2, 1 }
	}
	
	println(config.user.name)
	// OUTPUT: John Doe
	println(config["prefs"]["verbose"])
	// OUTPUT: -1
	
	myFunc = func()
		// 'outer' explicitly declares a closure variable
		// closure variables can be modified and used to
		// store the state between function calls
		outer counter = 0
		ret = counter
		counter += 1
		return ret
	end
	
	println(myFunc())
	// OUTPUT: 0
	println(myFunc())
	// OUTPUT: 1
	
***

### Current compiler state

##### Implemented

*   expressions (assignments, calls, ...)
*	if-elseif-else-end, switch-case-default-end
*   for-end, foreach-end, do-end, do-until, while-end
*	break, continue
*	func/sub, exit & return
*	anonymous functions & closures
*	local, global
*	OOP (base) and array functions
*   changed alternate syntax for anonymous functions: e.g. `@{println("Hello world!")}`
    or `@{(a, b) return a + b}` or `@{ (a, b) println(a.." + "..b.." = "..(a + b))}`
*   ternary operator: `cond ? expr : expr`
*	type bound functions: e.g. `map::size()`

##### Missing / TODO / planned

*	outer
*	OOP (constructors, new, ...)
*	lock/end
*	interop nice methods
*	most of the Weel library
*	unit test framework (started but not finished yet)
*	refactor error messages
*	expression optimization (will be one of the last things)

*REMARK:* The compiler and runtime still need full testing so everything
might be a bit unstable at the moment (Especially the compiler is very
fragile right now). 

***

[Lua]: http://www.lua.org/ "The Programming Language Lua"
[Enigma]: http://www.nongnu.org/enigma/ "Enigma is a puzzle game inspired by Oxyd on the Atari ST and Rock'n'Roll on the Amiga"

[$PROFILE$]: extended "Txtmark processing information."

Project link: <https://github.com/rjeschke/weel>
