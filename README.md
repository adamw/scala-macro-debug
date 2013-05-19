scala-macro-debug
=================

Scala macros to make debugging easier.

Comes in two flavors, `DebugMacros`, as described on the blog (see introduction below),
and an enhanced version `DebugConsole`.

`DebugMacros` example:

    class Test {
        import com.softwaremill.debug.DebugMacros._

        val v1 = 10

        def test() {
            val v2 = 20
            debug("Values in test", v1, v2)
        }
    }

Should print:

    Values in test, Test.this.v1 = 10, v2 = 20

`DebugConsole` example:

    class Test {
        import com.softwaremill.debug.DebugConsole._

        val v1 = 10

        def test() {
            val v2 = 20
            debug("Values in test", v1, v2)
        }
    }

Should print:

    |D| Values in test, Test.this.v1 = 10, v2 = 20

And:

    class Test {
        import com.softwaremill.debug.DebugConsole._

        val v1 = 10

        def test() {
            val v2 = 20
            debugReport("Values in test", v1, v2)
        }
    }

Should print:

    |D| Values in test
    |D|     Test.this.v1 = 10
    |D|     v2           = 20

Features
--------

- Two Modes: debug (dynamic single line debugging message) and debugReport (title and variables report)
- Can be disabled at compile time (the debugging code is removed from the final `.class` files)
- Can be used to print the current source code file name and line
- Really easy to use (there are only two methods)

Introduction
------------

We all use `println` messages to debug our code and check the execution flow. And we quickly end up with things like this:

    println("Values in test, Test.this.v1 = " + v1 + ", v2 = " + v2)

And this is only for two variables. It can quickly grow ugly. And also, hunting down all the lost `println` lines lost in the middle of the code ends up being nightmarish.

This project is the brainchild of a tutorial to learn to code Scala Macros. See the blog: ["Starting with Scala Macros: a short tutorial"](http://www.warski.org/blog/2012/12/starting-with-scala-macros-a-short-tutorial/).

Getting the Project: SBT
------------------------

To use in your project, add the following dependency:

    "com.softwaremill.scalamacrodebug" %% "macros" % "0.2"

Getting the Project: Maven
--------------------------

To use in your project, add the following dependency:

    <dependency>
        <groupId>com.softwaremill.scalamacrodebug</groupId>
        <artifactId>macros_2.10</artifactId>
        <version>0.2</version>
    </dependency>

Usage
-----

Always include:

    import com.softwaremill.debug.DebugConsole._

The great strength of the debug methods is the ability to create a label for an expression to be debugged:

    debug(a + b)

prints:

    |D| a.+(b) = 30

You can combine as many as you want:

    debug(a + b, c, 7 + 3)

prints:

    |D| a.+(b) = 30, c = 14, 7.+(3) = 10

You can also mix as many constant literals (typically a String) as you want. They would be left untouched:

    debug(a + b, "which should be different from", 7 + 3)

prints:

    |D| a.+(b) = 30, which should be different from, 7.+(3)

The `debugReport` method prints the expressions debug with one expression per line:

    debugReport(a + b, c, 7 + 3)

prints:

    |D| a.+(b) = 30
    |D| c      = 14
    |D| 7.+(3) = 10

With an optional title as the first parameter:

    debugReport("And the set of vars is:", a + b, c, 7 + 3)

prints:

    |D| And the set of vars is:
    |D|     a.+(b) = 30
    |D|     c      = 14
    |D|     7.+(3) = 10


And finally, if you do:

    debug()

or

    debugReport()

You will get a debug message that reports the Source File and Line this call is placed in.


Disabling
---------

One of the strengths of the library is that it can be disabled on compile time. And if you disable it, all the `debug` and `debugReport` calls are literally removed from the generated code. You will no longer need to hunt down all the `println` sentences lost in the middle of the code.

To do this, you can set the environment variable `enable_debug_messages` to false (it's considered to be true by default). You can also send it as a system property (the system  property takes precedence over the environment variable).




