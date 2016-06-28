package org.jetbrains.plugins.scala.lang.breadcrumbs

import com.intellij.lang.Language
import com.intellij.psi.PsiElement
import com.intellij.xml.breadcrumbs.BreadcrumbsInfoProvider
import org.jetbrains.plugins.scala.ScalaLanguage
import org.jetbrains.plugins.scala.lang.psi.api.base.ScPrimaryConstructor
import org.jetbrains.plugins.scala.lang.psi.api.base.patterns.ScCaseClause
import org.jetbrains.plugins.scala.lang.psi.api.expr._
import org.jetbrains.plugins.scala.lang.psi.api.statements.params.ScParameter
import org.jetbrains.plugins.scala.lang.psi.api.statements.{ScFunction, ScPatternDefinition}
import org.jetbrains.plugins.scala.lang.psi.api.toplevel.ScNamedElement
import org.jetbrains.plugins.scala.lang.psi.api.toplevel.typedef._
import org.jetbrains.plugins.scala.lang.psi.types.ScType
import org.jetbrains.plugins.scala.lang.psi.types.result.TypingContext
import org.jetbrains.plugins.scala.settings.ScalaProjectSettings

/**
  * User: Dmitry.Naydanov
  * Date: 23.06.16.
  */
class ScalaBreadcrumbsInfoProvider extends BreadcrumbsInfoProvider {
  override def getElementTooltip(e: PsiElement): String = {
    import ScalaBreadcrumbsInfoProvider.MyTextRepresentationUtil._
    
    e match {
      case templ: ScTemplateDefinition => getTemplateDefTooltip(templ)
      case fun: ScFunction => getFunctionTooltip(fun)
      case funExpr: ScFunctionExpr => getFunctionTooltip(funExpr)
      case member: ScMember => getMemberTooltip(member)
      case caseClause: ScCaseClause => getCaseClauseTooltip(caseClause)
      case _ => "?"
    }
  }

  override def getElementInfo(e: PsiElement): String = {
    import ScalaBreadcrumbsInfoProvider.MyTextRepresentationUtil._
    
    e match {
      case clazz: ScTemplateDefinition => describeTemplateDef(clazz)
      case funExpr: ScFunctionExpr => describeFunction(funExpr)
      case fun: ScFunction => describeFunction(fun)
      case member: ScMember => describeMember(member)
      case caseClause: ScCaseClause => describeCaseClause(caseClause)
      case expr: ScExpression => describeExpression(expr)
      case named: ScNamedElement => named.name
      case other =>
        println("Error: " + other.getClass + "   " + other.getText)
        ""
    }
  }

  override def acceptElement(e: PsiElement): Boolean = {
    val settings = ScalaProjectSettings.getInstance(e.getProject)
    
    e match {
      case _: ScTemplateDefinition => settings.isBreadcrumbsClassEnabled
      case _: ScFunctionExpr => settings.isBreadcrumbsLambdaEnabled
      case _: ScFunction => settings.isBreadcrumbsFunctionEnabled
      case _: ScCaseClause | _: ScMatchStmt => settings.isBreadcrumbsMatchEnabled
      case _: ScMember => settings.isBreadcrumbsValDefEnabled
      case _: ScIfStmt | _: ScWhileStmt | _: ScDoStmt => settings.isBreadcrumbsIfDoWhileEnabled
      case _ => false
    }
  }

  override def getLanguages: Array[Language] = ScalaBreadcrumbsInfoProvider.SCALA_LANG
}

object ScalaBreadcrumbsInfoProvider {
  val SCALA_LANG = Array[Language](ScalaLanguage.Instance)
  
  val MAX_TEXT_LENGTH = 150
  val MAX_STRING_LENGTH = 25
  
  object MyTextRepresentationUtil {
    private def limitString(s: String, stub: String = "(...)") = if (s == null) "" else if (s.length < MAX_STRING_LENGTH) s else stub
    private def limitText(txt: String, stub: String = "...") = {
      if (txt == null) "" else if (txt.length < MAX_TEXT_LENGTH) txt else txt.substring(0, MAX_TEXT_LENGTH - 1 - stub.length) + stub
    }
    
    def getSignature(el: Option[ScNamedElement], parameters: Seq[ScParameter], tpe: Option[ScType], needTpe: Boolean = false): String =
      el.map(_.name).getOrElse("") + 
        limitString(parameters.map(p => p.name + ": " + p.getType.getPresentableText).mkString("(", ", ", ")")) + 
        (if (needTpe) ": " + tpe.map(_.presentableText).getOrElse("") else "")

    def getSignature(fun: ScFunction): String = getSignature(Option(fun), fun.parameters, fun.returnType.toOption)

    def getSignature(fun: ScFunctionExpr): String = getSignature(None, fun.parameters, fun.getType(TypingContext.empty).toOption)
    
    def getConstructorSignature(constr: ScFunction): String = {
      if (!constr.isConstructor) return ""
      
      val signature = getSignature(None, constr.parameters, None)
      "this" + limitString(signature)
    }

    def getPrimaryConstructorSignature(constr: ScPrimaryConstructor): String = { 
      val signature = getSignature(None, constr.parameters, None)
      "this" + limitString(signature)
    }
    
    def describeFunction(fun: ScFunction) = if (fun.isConstructor) getConstructorSignature(fun) else getSignature(fun)
    
    def describeFunction(fun: ScFunctionExpr) = "λ" + getSignature(fun)
    
    def describeTemplateDef(td: ScTemplateDefinition) = td.name
    
    def describeMember(mb: ScMember) = {
      mb match {
        case pattern: ScPatternDefinition =>
          val vs = pattern.bindings.map(b => b.name)
          "val " + limitString(if (vs.length == 1) vs.head else vs.mkString("(", ", ", ""))
        case other => other.getName
      }
    }
    
    def describeExpression(expr: ScExpression) = {
      expr match {
        case ifSt: ScIfStmt => s"if (${limitString(ifSt.condition.map(_.text).getOrElse(""))}) {...}"
        case whileSt: ScWhileStmt => s"while(${limitString(whileSt.condition.map(_.text).getOrElse(""), "...")})"
        case doWhileSt: ScDoStmt => s"do ... while(${limitString(doWhileSt.condition.map(_.text).getOrElse(""), "...")})"
        case matchSt: ScMatchStmt => limitString(matchSt.expr.map(_.getText).getOrElse("(...)")) + " match {...}"
        case _ => "Expr"
      }
    }
    
    def describeCaseClause(clause: ScCaseClause) = {
      s"case ${limitString(clause.pattern.map(_.text).getOrElse(""))} =>"
    }
    
    def getTemplateDefTooltip(td: ScTemplateDefinition) = {
      (td match {
        case clazz: ScClass => "class"
        case obj: ScObject => "object"
        case tr: ScTrait => "trait"
        case _ => ""
      }) + " " + describeTemplateDef(td)
    }
    
    def getFunctionTooltip(fun: ScFunction) = {
      describeFunction(fun) + (if (fun.getBody != null) limitText(fun.getBody.getText) else "")
    }
    
    def getFunctionTooltip(funExpr: ScFunctionExpr) = {
      limitText(funExpr.getText)
    }
    
    def getMemberTooltip(member: ScMember) = limitText(member.getText)
    
    def getCaseClauseTooltip(clause: ScCaseClause) = limitText(clause.getText)
  }
}
