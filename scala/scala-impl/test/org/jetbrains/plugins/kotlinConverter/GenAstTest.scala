package org.jetbrains.plugins.kotlinConverter

import org.jetbrains.plugins.kotlinConverter.ast.Expr.Lit
import org.jetbrains.plugins.kotlinConverter.ast.Stmt.{ClassDef, DefnDef, FileDef, MultiBlock}
import org.jetbrains.plugins.kotlinConverter.ast.{AST, DefParam, Expr, Type}
import org.jetbrains.plugins.scala.base.ScalaLightPlatformCodeInsightTestCaseAdapter

class GenAstTest extends ScalaLightPlatformCodeInsightTestCaseAdapter {

  def doTest(code: String, expectedAst: AST) = {
    configureFromFileTextAdapter("dummy.scala", code)
    val psiFile = getFileAdapter
    println(ASTConverter.gen(psiFile))
    assert(expectedAst == ASTConverter.gen(psiFile))
  }

  def doExprTest(expr: String, expectedExpr: Expr) = {
    val code =
      s"""
        |def foo(x: Int) = {
        |  $expr
        |}
      """.stripMargin
    val expected = FileDef("dummy.scala", Seq(),Seq(DefnDef("foo",Type("Int"),Seq.empty, MultiBlock(Seq(expectedExpr)))))
    doTest(code, expected)
  }

//  def testClass() = {
//    val code =
//      """class A {}""".stripMargin
//    val expected = FileDef("dummy.scala",Seq(),Seq(ClassDef("A",Seq())))
//
//    doTest(code, expected)
//  }
//
//  def testDef() = {
//    val code =
//      """def foo(x: Int) = {
//        |    1
//        |}
//      """.stripMargin
//    val expected =
//      FileDef("dummy.scala", Seq(),Seq(DefnDef("foo",Type("Int"),Seq(DefParam(Type("Int"),"x")),MultiBlock(Seq(Lit(Type("Int"),"1"))))))
//    doTest(code, expected)
//  }

  def testExpr = {
    doExprTest(
      """
        |  x match {
        |    case 1 => "d"
        |    case 2 => "c"
        |    case _ => "e"
        |  }
      """.stripMargin, Lit(Type("Int"), "1"))
  }
}

