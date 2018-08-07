package org.jetbrains.plugins.kotlinConverter

class DebugConveterTest extends ConverterTestBase {
  def testDebug(): Unit =
    doTest(
      """def a(x: => Int) = x
        |def q = a(1)
      """.stripMargin,"", true)

}
