package org.jetbrains.plugins.kotlinConverter

class DebugConveterTest extends ConverterTestBase {
  def testSeqOfOptionFlatten(): Unit =
    doExprTest(
      """Seq(1).foldLeft(0)(_+_)""".stripMargin,"", true)

}
