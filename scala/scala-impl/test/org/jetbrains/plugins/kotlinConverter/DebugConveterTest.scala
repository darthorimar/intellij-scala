package org.jetbrains.plugins.kotlinConverter

class DebugConveterTest extends ConverterTestBase {
  def testDebug(): Unit =
    doTest(
      """import scala.util.Try
        |val a: Try[Int] = Try(1)
      """.stripMargin,"", doPrint = true)


}
