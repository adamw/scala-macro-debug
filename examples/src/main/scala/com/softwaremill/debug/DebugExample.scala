package com.softwaremill.debug

object DebugExample extends App {
  import DebugMacros._

  header("Hello World macro")
  hello()

  header("Printparam macro")
  val x = 12
  printparam(x)
  printparam(x*2)
  printparam(f(x))

  header("Debug 1 macro")
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

  // Helper methods

  def f(p: Int) = p + 1

  def header(name: String) {
    println()
    println("------------")
    println(name)
    println("------------")
  }
}
