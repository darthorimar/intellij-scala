package org.jetbrains.plugins.kotlinConverter

class DebugConveterTest extends ConverterTestBase {
  def testDebug(): Unit =
    doTest(
      """val a = Seq(1,2)
        |
        |
        |""".stripMargin,"", doPrint = true)


}
