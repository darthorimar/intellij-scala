package org.jetbrains.plugins.kotlinConverter

class DebugConveterTest extends ConverterTestBase {
  def testSeqOfOptionFlatten(): Unit =
    doExprTest("None",
      "1?.let { x -> x + 1}!!", true)
}
