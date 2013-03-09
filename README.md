scala-macro-debug
=================

Scala macros for making debugging easier.

Example:

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

See the blog: ["Starting with Scala Macros: a short tutorial"](http://www.warski.org/blog/2012/12/starting-with-scala-macros-a-short-tutorial/).

To use in your project, add the following dependency:

    "com.softwaremill.scalamacrodebug" %% "macros" % "0.1"