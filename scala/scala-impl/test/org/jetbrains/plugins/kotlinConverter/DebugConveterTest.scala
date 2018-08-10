package org.jetbrains.plugins.kotlinConverter

class DebugConveterTest extends ConverterTestBase {
  def testDebug(): Unit =
    doExprTest(
      """Seq.empty[Int].collect {
         |  case 1 => 4
         |}
         |""".stripMargin,"", doPrint = true)


}
