package org.jetbrains.plugins.kotlinConverter

class DebugConveterTest extends ConverterTestBase {
  def testDebug(): Unit =
    doTest(
      """val a =  Seq.empty
        |
        |
        |""".stripMargin,"", doPrint = true)


}
