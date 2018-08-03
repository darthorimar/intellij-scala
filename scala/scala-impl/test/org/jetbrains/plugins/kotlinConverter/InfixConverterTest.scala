package org.jetbrains.plugins.kotlinConverter

class InfixConverterTest extends ConverterTestBase {
  def testSeqOfOptionFlatten(): Unit =
    doTest(
      """class A {
        |}
        |object A {def a = 5}
        |object B
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
