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
  def testMatch(): Unit = {
    doTest(
      """
        |trait A
        |case class B(a: A, b: A) extends A
        |case class C(c: Int) extends A
        |
        |def a(x: Any) = x match {
        |  case B(a, B(C(e: Int), C(d))) if e > 3 => e
        |  case B(a, b) => 42
        |  case q: Int if q == 2 => q
        |  case 2 if 1 == 1 => q
        |  case _ if 1 == 1 => q
        | }
      """.stripMargin,
      """fun a(x: Any): Any ={
        |  val match: Any = x
        |  data class `B(a, B(C(e: Int), C(d)))_data`(public val a: Any, public val e: Int, public val d: Any)
        |  data class `B(a, b)_data`(public val a: Any, public val b: Any)
        |  val `B(a, B(C(e: Int), C(d)))` by lazy {
        |    if (match is B){
        |      val (a, l1) = match
        |      if (l1 is B){
        |        val (l2, l3) = l1
        |        if (l2 is C && l3 is C){
        |          val e = l2
        |          val d = l3
        |          if (e is Int)if (e > 3)return@lazy `B(a, B(C(e: Int), C(d)))_data`(a, e, d)
        |        }
        |      }
        |    }
        |    return@lazy null
        |  }
        |  val `B(a, b)` by lazy {
        |    if (match is B){
        |      val (a, b) = match
        |      return@lazy `B(a, b)_data`(a, b)
        |    }
        |    return@lazy null
        |  }
        |  when {
        |    `B(a, B(C(e: Int), C(d)))` != null -> {
        |      val (a, e, d) = `B(a, B(C(e: Int), C(d)))`
        |      e
        |    }
        |    `B(a, b)` != null -> {
        |      val (a, b) = `B(a, b)`
        |      42
        |    }
        |    match is Int && match == 2 -> match
        |    match == 2 && 1 == 1 -> q
        |    1 == 1 -> q}
        |
        |}
        |public interface A
        |public data class B(public val a: A, public val b: A) : A
        |public data class C(public val c: Int) : A """.stripMargin)
  }


}

