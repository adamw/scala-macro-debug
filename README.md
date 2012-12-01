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