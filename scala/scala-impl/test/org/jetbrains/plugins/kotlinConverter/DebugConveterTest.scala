package org.jetbrains.plugins.kotlinConverter

class DebugConveterTest extends ConverterTestBase {
  def testDebug(): Unit =
    doTest(
      """def a =
        |""".stripMargin,"", doPrint = true)


}
