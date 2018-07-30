package org.jetbrains.plugins.kotlinConverter

class ExpressionConverterTest extends ConverterTestBase {
  def doExprTest(scala: String, kotlin: String, doPrint: Boolean = false): Unit =
    doTest(s"def a = {$scala \n 42}", s"public fun a(): Int {$kotlin \n return 42}", doPrint)

  def testForComprehension(): Unit =
    doExprTest(
      """for {
        |  i <- Seq(1,2)
        |  j <- Seq(2,3)
        |  a = i
        |  if a > 2
        |} {
        |   println(i + j)
        |}""".stripMargin,
      """
        |for (i in listOf(1, 2)) {
        |    for (j in listOf(2, 3)) {
        |      val a = i
        |      if (a > 2){
        |        println(i + j)
        |      }
        |    }
        |  }
      """.stripMargin)
}
