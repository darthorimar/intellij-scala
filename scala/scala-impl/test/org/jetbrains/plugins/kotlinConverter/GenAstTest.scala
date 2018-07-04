package org.jetbrains.plugins.kotlinConverter

import java.io.File

import com.intellij.openapi.command.impl.DummyProject
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.fileTypes.ex.FakeFileType
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.{LocalFileSystem, VirtualFile}
import com.intellij.psi.{PsiFile, PsiManager}
import org.jetbrains.plugins.kotlinConverter.ast.Expr.Lit
import org.jetbrains.plugins.kotlinConverter.ast.Stmt.{ClassDef, DefnDef, FileDef, MultiBlock}
import org.jetbrains.plugins.kotlinConverter.ast.{AST, Expr, Type}
import org.jetbrains.plugins.scala.ScalaFileType
import org.jetbrains.plugins.scala.base.{ScalaLightCodeInsightFixtureTestAdapter, ScalaLightPlatformCodeInsightTestCaseAdapter}
import org.jetbrains.plugins.scala.decompiler.ScClassFileViewProvider
import org.jetbrains.plugins.scala.lang.formatting.settings.ScalaCodeStyleSettings
import org.jetbrains.plugins.scala.lang.psi.api.ScalaFile
import org.jetbrains.plugins.scala.lang.psi.impl.{ScalaFileImpl, ScalaPsiElementFactory}
import org.jetbrains.plugins.scala.project.ProjectContext
import org.jetbrains.plugins.scala.util.TypeAnnotationSettings
import org.junit.Test

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

class GenAstTest extends ScalaLightPlatformCodeInsightTestCaseAdapter {

  def doTest(code: String, expectedAst: AST) = {
    configureFromFileTextAdapter("dummy.scala", code)
    val psiFile = getFileAdapter
    println(ASTConverter.convert(psiFile))
    assert(expectedAst == ASTConverter.convert(psiFile))
  }

  def testClass = {
    val code =
      """class A {}""".stripMargin
    val expected = FileDef("dummy.scala",Seq(),Seq(ClassDef("A",Seq())))

    doTest(code, expected)
  }

  def testDef = {
    val code =
      """class A {
        |  def foo = {
        |    1
        |  }
        |}
      """.stripMargin
    val expected = FileDef("dummy.scala",Seq(),Seq(ClassDef("A",List(DefnDef("foo",Type("Int"),Seq(),MultiBlock(ArrayBuffer(Lit(Type("Int"),"1"))))))))

    doTest(code, expected)
  }
}

