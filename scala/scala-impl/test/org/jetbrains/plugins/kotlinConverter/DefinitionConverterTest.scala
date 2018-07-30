package org.jetbrains.plugins.kotlinConverter

class DefinitionConverterTest extends ConverterTestBase {
  def testTraitDef(): Unit =
    doTest(
      """
        |class A(val a: Int)
        |trait B
        |class C extends A(1) with B
      """.stripMargin,
      """public open class A(public val a: Int)
        |public interface B
        |public open class C() : A(1), B
      """.stripMargin)

  def testMultipleConstctorParams(): Unit =
    doTest(
      """
        |class A(val a: Int, b: String)
        |class C extends A(1, "nya")
      """.stripMargin,
      """public open class A(public val a: Int, public  b: String)
        |public open class C() : A(1, "nya")
      """.stripMargin)
}
