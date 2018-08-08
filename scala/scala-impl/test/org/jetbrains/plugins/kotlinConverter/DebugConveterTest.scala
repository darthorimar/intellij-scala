package org.jetbrains.plugins.kotlinConverter

class DebugConveterTest extends ConverterTestBase {
  def testDebug(): Unit =
    doTest(
      """ val a = (1, 2, 3) match {
        |    case (x, 2, 3) => x
        |  }
        |
        |
      """.stripMargin,"", true)

}
