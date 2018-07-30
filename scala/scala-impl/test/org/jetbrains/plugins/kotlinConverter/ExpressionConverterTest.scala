package org.jetbrains.plugins.kotlinConverter

class ExpressionConverterTest extends ConverterTestBase {
  def testForComprehension(): Unit =
    doExprTest(
      """for {
        |  i <- Seq(1,2)
        |  j <- Seq(2,3)
        |  a = i
        |  if a > 2
        |} {
        |   println(i + j)
        |}""".stripMargin,
      """
        |for (i in listOf(1, 2)) {
        |    for (j in listOf(2, 3)) {
        |      val a = i
        |      if (a > 2){
        |        println(i + j)
        |      }
        |    }
        |  }
      """.stripMargin)

  def testCasts(): Unit =
    doExprTest(
      """1.asInstanceOf[Long]
        |1.isInstanceOf[Long]
      """.stripMargin,
      """(1 as Long)
        |(1 is Long)
      """.stripMargin)

  def testTryFinally(): Unit =
    doExprTest(
      """try 1 finally 5""".stripMargin,
      """try {
        |  1
        |} finally {
        |  5
        |}""".stripMargin)


  def testStringInterpolation(): Unit =
    doExprTest(
      """ s"${1} + $None" """.stripMargin,
      """ "${1} + $null"""".stripMargin)

  def testSomeInWhen(): Unit =
    doExprTest(
      """Some(1) match {
        |   case Some(x) => x + 1
        |   case _ => 0
        |}"""".stripMargin,
      """
        |  val match1 = Some()(1)
        |  data class `Some(x)_data`(public val x: Any)
        |  val `Some(x)` by lazy {
        |    if (match1 != null){
        |      val x = match1
        |      return@lazy `Some(x)_data`(x)
        |    }
        |    return@lazy null
        |  }
        |  return when {
        |    `Some(x)` != null -> {
        |      val x = `Some(x)`
        |      x + 1
        |    }
        |    else -> {
        |      0
        |    }}        |
        |"""".stripMargin, true)



}
