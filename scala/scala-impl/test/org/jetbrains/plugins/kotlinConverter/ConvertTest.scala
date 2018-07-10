package org.jetbrains.plugins.kotlinConverter

import org.jetbrains.plugins.kotlinConverter.ast._
import org.jetbrains.plugins.scala.base.ScalaLightPlatformCodeInsightTestCaseAdapter
import org.jetbrains.plugins.scala.lang.psi.api.ScalaFile

class ConvertTest extends ScalaLightPlatformCodeInsightTestCaseAdapter {

  def doTest(code: String) = {
    configureFromFileTextAdapter("dummy.scala", code)
    val psiFile = getFileAdapter
    println(Converter.convert(psiFile.asInstanceOf[ScalaFile]))
//    assert(expectedAst == ASTConverter.gen(psiFile))
  }

 def test = {
   doTest(
     "def a = Some(1).map(x => x + 1)".stripMargin)
 }
}

