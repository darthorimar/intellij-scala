package org.jetbrains.plugins.kotlinConverter

class InfixConverterTest extends ConverterTestBase {
  def testSeqOfOptionFlatten(): Unit =
    doTest(
      """val a : List[_] = List.empty
      """.stripMargin,
      """open class A() {
        |  companion object {
        |    public fun a(): Int =5
        |  }
        |}
        |object B
        |
        |}""".stripMargin, true)
}
