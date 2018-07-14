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
    eval(
      """
        |trait A
        |case class B(a: A, b: A) extends A
        |case class C(c: Int) extends A
        |
        |def a(x: Any) = x match {
        |  case B(a, hello@B(C(e: Int), C(d))) if e > 3 => e
        |  case B(a, b) => 42
        |  case q: Int if q == 2 => q
        |  case 2 if 1 == 1 => q
        |  case _ if 1 == 1 => q
        | }
      """.stripMargin)
  }
}

