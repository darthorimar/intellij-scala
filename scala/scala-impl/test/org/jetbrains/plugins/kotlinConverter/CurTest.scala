package org.jetbrains.plugins.kotlinConverter

import org.jetbrains.plugins.scala.base.ScalaLightPlatformCodeInsightTestCaseAdapter
import org.jetbrains.plugins.scala.lang.psi.api.ScalaFile

class CurTest extends ScalaLightPlatformCodeInsightTestCaseAdapter {

  def eval(scala: String): Unit = {
    configureFromFileTextAdapter("dummy.scala", scala)
    val psiFile = getFileAdapter
    val res = Converter.convert(psiFile.asInstanceOf[ScalaFile])
    println(res)
  }

  def test(): Unit = {
    //  case q if 1 == 1 => q + 1
    //  case y if 1 != 1 => y
    eval(
      """
        |trait A
        |case class B(a: A, b: A) extends A
        |case class C(c: Int) extends A
        |
        |def a(x: Any) = x match {
        |  case B(a, B(C(e), C(d))) if e > 2 => e
        |  case q: Int if x > 5 => q
        |  case _ =>
        | }
      """.stripMargin)
  }

}

