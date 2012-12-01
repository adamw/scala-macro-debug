package com.softwaremill.debug

object DebugExample extends App {
  import DebugMacros._

  hello()

  val x = 12
  printparam(x)
  printparam(x*2)
  printparam(f(x))

  val z = 10
  debug1(z)
  debug1(z*2)
  debug1(f(z))

  def test() {
    val p = 11
    debug1(p)
    debug1(p + z)
  }

  test()

  def f(p: Int) = p + 1
}
