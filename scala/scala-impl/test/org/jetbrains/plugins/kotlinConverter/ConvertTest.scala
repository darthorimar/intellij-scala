package org.jetbrains.plugins.kotlinConverter

import org.jetbrains.plugins.kotlinConverter.ast._
import org.jetbrains.plugins.scala.base.ScalaLightPlatformCodeInsightTestCaseAdapter
import org.jetbrains.plugins.scala.lang.psi.api.ScalaFile

class ConvertTest extends ScalaLightPlatformCodeInsightTestCaseAdapter {

  def doTest(scala: String, kotlin: String): Unit = {
    configureFromFileTextAdapter("dummy.scala", scala)
    val psiFile = getFileAdapter
    val res = Converter.convert(psiFile.asInstanceOf[ScalaFile])
    println(res)
    assert(kotlin.replaceAllLiterally(" ", "").replaceAllLiterally("\n", "") ==
      res.replaceAllLiterally(" ", "").replaceAllLiterally("\n", ""))
  }

  def testFuncCall() = {
    doTest("def a = Some(1)", "fun a(): Int? = Some(1)")
    doTest("""def a = "ny".substring(1,2) """, """fun a(): String = "ny".substring(1,2)""")
  }

}

