package org.jetbrains.plugins.kotlinConverter

class InfixConverterTest extends ConverterTestBase {
  def testSeqOfOptionFlatten(): Unit =
    doTest(
      """def foo(xs: String*) = xs
        |def bar = foo(Seq(1,2) :_*)
      """.stripMargin,
      """fun foo(vararg xs: String): List<String> =xs""".stripMargin, true)
}
