package org.jetbrains.plugins.kotlinConverter

class DebugConveterTest extends ConverterTestBase {
  def testDebug(): Unit =
    doTest(
      """trait I
        |  case class A(i: I) extends I
        |  case class B(x: Int) extends I
        |  object O {
        |    def unapply(arg: Int): Option[A] = Some(A(B(1)))
        |  }
        |  val q = 1 match {
        |    case O(A(B(x))) => x
        |  }
        |""".stripMargin,"", doPrint = true)


}
