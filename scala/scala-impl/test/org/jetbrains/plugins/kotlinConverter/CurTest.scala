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
      """case class A(v: Int)
        |def a = A(1) match {
        |  case A(1 | 2) => 3
        |}"""
      .stripMargin)
  }

//  case class A(v: B)
//  case class B(v: Int)
//  def a = A(B(3)) match {
//    case A(B(1) | B(4)) => 3
//  }

  }

