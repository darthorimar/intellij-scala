package org.jetbrains.plugins.kotlinConverter

import org.jetbrains.plugins.scala.base.ScalaLightPlatformCodeInsightTestCaseAdapter
import org.jetbrains.plugins.scala.lang.psi.api.ScalaFile
import org.junit.Assert.assertEquals

abstract class ConverterTestBase extends ScalaLightPlatformCodeInsightTestCaseAdapter {
  def doTest(scala: String, kotlin: String, doPrint: Boolean = false): Unit = {
    configureFromFileTextAdapter("dummy.scala", scala)
    val psiFile = getFileAdapter
    val res = Converter.convert(psiFile.asInstanceOf[ScalaFile], doPrint)
    if (doPrint) {
      println(res)
    }
    else {
      assertEquals(s"$res\n is not equals to\n $kotlin",
        unformat(kotlin),
        unformat(res))
    }
  }
  private def unformat(text: String) =
    text.replaceAllLiterally(" ", "").replaceAllLiterally("\n", "")
}
