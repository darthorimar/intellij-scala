package org.jetbrains.plugins.kotlinConverter


import org.jetbrains.plugins.scala.base.ScalaLightPlatformCodeInsightTestCaseAdapter
import org.jetbrains.plugins.scala.lang.psi.api.ScalaFile
class CurTest extends ScalaLightPlatformCodeInsightTestCaseAdapter {

  def eval(scala: String): Unit = {
    configureFromFileTextAdapter("dummy.scala", scala)
    val psiFile = getFileAdapter.asInstanceOf[ScalaFile]
    val res = Converter.convert(psiFile)
    println(res)
  }

  def test(): Unit = {
    eval(
      """def a = Some(1) match {
        |   case Some(x) => x + 1
        |   case None => 0
        |}
      """.stripMargin)
  }

}
