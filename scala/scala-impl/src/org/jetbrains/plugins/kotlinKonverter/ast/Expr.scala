package org.jetbrains.plugins.kotlinKonverter.ast

trait Expr extends AST{
  def ty: Type
}

object Expr {
  case class BinExpr(ty: Type, op: BinOp, left: Expr, right: Expr) extends Expr
  case class Call(ty: Type, obj: Expr, method: String, params: Seq[Expr]) extends Expr
}


