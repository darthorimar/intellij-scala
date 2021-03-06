package org.jetbrains.plugins.scala.settings.annotations

import com.intellij.psi.{PsiElement, PsiEnumConstant}
import org.jetbrains.plugins.scala.extensions._
import org.jetbrains.plugins.scala.lang.lexer.ScalaTokenTypes
import org.jetbrains.plugins.scala.lang.psi.api.base.ScLiteral
import org.jetbrains.plugins.scala.lang.psi.api.expr._
import org.jetbrains.plugins.scala.lang.psi.api.statements.{ScFunction, ScFunctionDefinition, ScPatternDefinition, ScVariableDefinition}

/**
  * @author Pavel Fatin
  */
trait Implementation {
  def containsReturn: Boolean

  def isTypeObvious: Boolean
}

object Implementation {
  private val TraversableClassNames =
    Set("Seq", "Array", "List", "Vector", "Set", "HashSet", "Map", "HashMap", "Iterator", "Option")

  def apply(definition: PsiElement): Implementation = new Definition(definition)

  object Expression {
    def apply(expression: PsiElement): Implementation = new Expression(expression)
  }

  private class Definition(element: PsiElement) extends Implementation {
    override def containsReturn: Boolean = element match {
      case f: ScFunctionDefinition => f.returnUsages().exists {
        case _: ScReturnStmt => true
        case _ => false
      }
      case _ => false
    }

    override def isTypeObvious: Boolean = rightHandSideOf(element).exists(isSimple)
  }

  private class Expression(element: PsiElement) extends Implementation {
    override def containsReturn: Boolean = element.depthFirst().exists {
      case _: ScReturnStmt => true
      case _ => false
    }

    override def isTypeObvious: Boolean = isSimple(element)
  }

  private def isSimple(expression: PsiElement): Boolean = expression match {
    case literal: ScLiteral => literal.getFirstChild.getNode.getElementType != ScalaTokenTypes.kNULL
    case _: ScThrowStmt => true
    case it: ScNewTemplateDefinition if it.extendsBlock.templateBody.isEmpty => true
    case ref: ScReferenceExpression if isApplyCall(ref) => true
    case ScReferenceExpression(_: PsiEnumConstant) => true
    case ScGenericCall(referenced, _) if isEmptyCollectionFactory(referenced) => true
    case ScMethodCall(invoked: ScReferenceExpression, _) if isApplyCall(invoked) => true
    case _ => false
  }

  private def rightHandSideOf(element: PsiElement) = element match {
    case value: ScPatternDefinition if value.isSimple => value.expr
    case variable: ScVariableDefinition if variable.isSimple => variable.expr
    case method: ScFunctionDefinition if method.hasAssign && !method.isSecondaryConstructor => method.body
    case _ => None //support isSimple for JavaPsi
  }

  private def isApplyCall(reference: ScReferenceExpression): Boolean = {
    reference.bind().map(result => result.innerResolveResult.getOrElse(result).element).exists {
      case function: ScFunction => function.isApplyMethod
      case _ => false
    }
  }

  // TODO Restore encapsulation
  def isEmptyCollectionFactory(referenced: ScReferenceExpression): Boolean = referenced match {
    case ScReferenceExpression.withQualifier(qualifier: ScReferenceExpression) =>
      TraversableClassNames.contains(qualifier.refName) && referenced.refName == "empty"
    case _ => false
  }
}
