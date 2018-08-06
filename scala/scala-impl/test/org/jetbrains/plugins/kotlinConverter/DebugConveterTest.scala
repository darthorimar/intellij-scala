package org.jetbrains.plugins.kotlinConverter

class DebugConveterTest extends ConverterTestBase {
  def testDebug(): Unit =
    doExprTest(
      """Seq(1).map(_+ 1)""".stripMargin,"", true)

}
