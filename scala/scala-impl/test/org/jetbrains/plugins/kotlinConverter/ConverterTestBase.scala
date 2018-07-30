package org.jetbrains.plugins.kotlinConverter

import org.jetbrains.plugins.scala.base.ScalaLightPlatformCodeInsightTestCaseAdapter
import org.jetbrains.plugins.scala.lang.psi.api.ScalaFile
import org.junit.Assert.assertEquals

abstract class ConverterTestBase extends ScalaLightPlatformCodeInsightTestCaseAdapter {
  def doTest(scala: String, kotlin: String, doPrint: Boolean = false): Unit = {
    configureFromFileTextAdapter("dummy.scala", scala)
    val psiFile = getFileAdapter
    val res = Converter.convert(psiFile.asInstanceOf[ScalaFile])
    if (doPrint) {
      println(res)
    }
    else {
      assertEquals(kotlin.replaceAllLiterally(" ", "").replaceAllLiterally("\n", ""),
        res.replaceAllLiterally(" ", "").replaceAllLiterally("\n", ""))
    }
  }
}
