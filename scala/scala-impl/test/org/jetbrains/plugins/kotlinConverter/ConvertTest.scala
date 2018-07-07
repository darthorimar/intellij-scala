package org.jetbrains.plugins.kotlinConverter

import org.jetbrains.plugins.kotlinConverter.ast.Expr.Lit
import org.jetbrains.plugins.kotlinConverter.ast.Stmt.{DefnDef, FileDef, MultiBlock}
import org.jetbrains.plugins.kotlinConverter.ast.{AST, Expr, TypeCont}
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
     """
       |trait KotlinBuilderBase {
       |  def a(x: Int)(y: Int) =  x + Y
       |  def rep(a: Int, b: Boolean)(c: String, d: Long)(e: Char): Unit =
       |    rep(a,b)(c,a(1)(2))(e)
       |}
       |
     """.stripMargin)

 }
  case class A()
}

