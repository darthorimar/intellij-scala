package org.jetbrains.plugins.kotlinConverter

class DebugConveterTest extends ConverterTestBase {
  def testDebug(): Unit =
    doTest(
      """class RefCollector  {
        |  protected def action(ast: AST): Option[AST] =
        |    ast match {
        |      case ClassType(name) =>
        |        val className = addImport(name)
        |        Some(ClassType(className))
        |      case JavaType(name) =>
        |        val className = addImport(name)
        |        Some(JavaType(className))
        |      case r@RefExpr(_, None, name, _, _) =>
        |        val className = addImport(name)
        |        Some(copy(r).asInstanceOf[RefExpr].copy(referenceName = className))
        |      case _ => None
        |    }
        |
        |  private def addImport(name: String) = {
        |    val importPath = name.stripSuffix("$")
        |    if (name.contains(".") && !name.startsWith("scala."))
        |      imports = imports + Import(importPath)
        |    val className = name.split('.').last
        |    className
        |  }
        |}
        |
        |
        |""".stripMargin,"", doPrint = true)


}
