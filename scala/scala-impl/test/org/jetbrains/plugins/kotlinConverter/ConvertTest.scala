package org.jetbrains.plugins.kotlinConverter

import org.jetbrains.plugins.kotlinConverter.ast._
import org.jetbrains.plugins.scala.base.ScalaLightPlatformCodeInsightTestCaseAdapter
import org.jetbrains.plugins.scala.lang.psi.api.ScalaFile

class ConvertTest extends ScalaLightPlatformCodeInsightTestCaseAdapter {

  def doTest(scala: String, kotlin: String): Unit = {
    configureFromFileTextAdapter("dummy.scala", scala)
    val psiFile = getFileAdapter
    val res = Converter.convert(psiFile.asInstanceOf[ScalaFile])
    assert(kotlin.replaceAllLiterally(" ", "").replaceAllLiterally("\n", "") ==
      res.replaceAllLiterally(" ", "").replaceAllLiterally("\n", ""))
  }

  def testFuncCall(): Unit = {
    doTest(
      """def a = "ny".substring(1,2)""",
      """fun a(): String = "ny".substring(1,2)""")
  }

  def testOptionConverters(): Unit = {
    doTest("def a = Some(1).map(x => x + 1).get",
      "fun a(): Int =1?.let { x -> x + 1}!!")
  }


  def testUncarry(): Unit = {
    doTest(
      """def a(x: Int, b: String)(c: Char) = 1
        |def b = a(1,"2")('3')
      """.stripMargin,
      """fun a(x: Int, b: String, c: Char): Int =1
        |fun b(): Int =a(1, "2", '3')""".stripMargin)
  }
  """def a = (1 + 1) match {
  case 1 => 2
  case x => x + 1
  case x: Int => 42
  case _ => 4
 }
      """
  def testSimplMatch(): Unit = {
    doTest(
      """def a = (1 + 1) match {
         | case 1 => 2
         | case x => x + 1
         | case x: Int => 42
         | case _ => 4
         | }
      """.stripMargin,
      """fun a(): Int ={
        |  val match$: Int = (1 + 1)
        |  when(match$) {
        |    1 -> 2
        |    true -> match$ + 1
        |     is Int -> 42
        |    else -> 4}
        |
        |}""".stripMargin)
  }


}

