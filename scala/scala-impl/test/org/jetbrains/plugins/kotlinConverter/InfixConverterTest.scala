package org.jetbrains.plugins.kotlinConverter

class InfixConverterTest extends ConverterTestBase {
  def testSeqOfOptionFlatten(): Unit =
    doExprTest(
      """Seq(Some(1),None).flatten""".stripMargin,
      """listOf(1, null).filterNotNull()""".stripMargin, true)
}
