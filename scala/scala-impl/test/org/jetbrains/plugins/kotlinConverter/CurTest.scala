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
    eval("""
           | def foo = Seq(1,2,3).map {
           |    case x if x >= 3 => x - 3
           |    case _ => 0
           |  }
        |""".stripMargin)
  }
}

