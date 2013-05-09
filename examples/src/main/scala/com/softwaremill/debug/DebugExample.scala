package com.softwaremill.debug

object DebugExample extends App {
  def tutorialExamples() {
	  import DebugMacros._
	
	  h1("Tutorial Examples")

	  h2("Hello World macro")
	  hello()
	
	  h2("Printparam macro")
	  val x = 12
	  printparam(x)
	  printparam(x*2)
	  printparam(f(x))
	
	  h2("Debug 1 macro")
	  val y = 10
	  debug1(y)
	  debug1(y*2)
	  debug1(f(y))
	
	  def test() {
	    val p = 11
	    debug1(p)
	    debug1(p + y)
	  }
	
	  test()
	
	  h2("Debug macro")
	  val a = 10
	
	  def test2() {
	    val b = 20
	    val c = 30
	    debug(b, c)
	    debug("Got as far as here", a, b , c)
	    debug("Adding", a+b, "should be", c)
	  }
	
	  test2()
  }

	def debugConsoleExamples() {
		import DebugConsole._

		h1("Debug Console Examples")
		h2("Debug macro")
		val a = 10

		def test2() {
			val b = 20
			val c = 30
			debug(b, c)
			debug("Got as far as here", a, b , c)
			debug("Adding", a+b, "should be", c)
		}

		test2()

		h2("DebugReport macro")
		val i = 12
		val anotherParameter = "Something entered"

		println("\nEmpty")
		debugReport()
	
		println("\nSingle Message")
		debugReport("It's happening")
	
		println("\nSingle Parameter")
		debugReport(i)
	
		println("\nSingle Message + Single Parameter")
		debugReport("Here is the output", i)
	
		println("\nMultiple Parameters")
		debugReport(i, Math.pow(123.45, 2) * 2, anotherParameter)
	
		println("\nMessage + Multiple Parameters")
		debugReport("And here they are valued?", i, Math.pow(123.45 , 2) * 2, anotherParameter)
	}

  tutorialExamples()
  debugConsoleExamples()

  // Helper methods

  def f(p: Int) = p + 1

  def h2(name: String) {
    println()
    println("------------")
    println(name)
    println("------------")
  }

  def h1(name: String) {
    println()
    println("." * 80)
    println()
    println()
    println()

    println(name.toUpperCase())
    println("-" * name.length())
    println()
  }
}
