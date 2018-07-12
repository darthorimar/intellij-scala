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
      """def a = (1 + 1) match {
        |  case 1 => 2
        |  case x => x + 1
        |  case x: Int => 42
        |  case _ => 4
        | }
      """.stripMargin)
  }

}

