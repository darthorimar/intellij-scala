package org.jetbrains.plugins.kotlinKonverter.ast

trait Stmt

object Stmt {
  case class If(cond: Expr, trueB: Block, falseB: Block) extends Stmt
  case class For(range: Expr, body: Block) extends Stmt
  case class While(cond: Expr, body: Block) extends Stmt


  trait Block extends Stmt {
    def stmts: Seq[Stmt]
  }
  case class SingleBlock(stmt: Stmt) extends Block {
    override def stmts: Seq[Stmt] = Seq(stmt)
  }
  case class MultiBlock(stmts: Seq[Stmt]) extends Block


  trait Defn extends Stmt {
    def name: String
  }

  case class ClassDefn(name: String, members: Seq[Defn]) extends Defn
  case class ValDefn(name: String, ty: Type) extends Defn
  case class DefDefn(name: String, ty: Type, args: Seq[(String, String)]) extends Defn
  case class ObjDefn(name: String, members: Seq[Defn]) extends Defn


  trait Top extends Stmt
  case class FileDef(pckg: String, imports: Seq[ImportDef], defns: Seq[Defn]) extends Top
  case class ImportDef(name: String) extends Top

}