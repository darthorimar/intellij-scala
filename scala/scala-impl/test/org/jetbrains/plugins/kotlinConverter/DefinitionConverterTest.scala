package org.jetbrains.plugins.kotlinConverter

class DefinitionConverterTest extends ConverterTestBase {
  def testTraitDef(): Unit =
    doTest(
      """
        |class A(a: Int)
        |trait B
        |class C extends A(1) with B
      """.stripMargin,
      """
        |open class A(a: Int)
        |interface B
        |open class C() : A(1), B
      """.stripMargin)

  def testMultipleConstctorParams(): Unit =
    doTest(
      """
        |class A(a: Int, b: String)
        |class C extends A(1, "nya")
      """.stripMargin,
      """open class A(a: Int,  b: String)
        |open class C() : A(1, "nya")
      """.stripMargin)

  def testClassModifiers(): Unit =
    doTest(
      """
        |final class A
        |class B
        |abstract class C
      """.stripMargin,
      """class A()
        |open class B()
        |abstract class C()
      """.stripMargin)

  class A(a: Int)
}
