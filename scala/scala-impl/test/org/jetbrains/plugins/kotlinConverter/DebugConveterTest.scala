package org.jetbrains.plugins.kotlinConverter

class DebugConveterTest extends ConverterTestBase {
  def testDebug(): Unit =
    doTest(
      """ case class A(x: Int)
      """.stripMargin,"", true)

}
