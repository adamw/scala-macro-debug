package com.softwaremill.debug

object DebugExample extends App {
  import DebugMacros._

  hello()

  val x = 12
  printparam(x)
  printparam(x*2)
  printparam(f(x))

  val z = 10
  debug(z)
  debug(z*2)
  debug(f(z))

  def test() {
    val p = 11
    debug(p)
    debug(p + z)
  }

  test()

  def f(p: Int) = p + 1
}
