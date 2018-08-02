package org.jetbrains.plugins.kotlinConverter

class InfixConverterTest extends ConverterTestBase {
  def testInfix(): Unit =
    doExprTest(""" "a" * 3 """ ,
      "1?.let { x -> x + 1}!!", true)
}
