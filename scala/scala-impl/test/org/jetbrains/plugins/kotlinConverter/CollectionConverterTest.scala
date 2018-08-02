package org.jetbrains.plugins.kotlinConverter

class CollectionConverterTest extends ConverterTestBase {
  def testOptionConverters(): Unit =
    doExprTest(" Some(1).map(x => x + 1).get",
      "1?.let { x -> x + 1}!!")

  def testListCon(): Unit =
    doExprTest(
      "1 :: Nil",
      "listOf(1) + emptyList()")

  def testSeqMmpty(): Unit =
    doExprTest(
      """Seq.empty[Int]""".stripMargin,
      """emptyList<Int>()""".stripMargin)

  def testMkString(): Unit =
    doExprTest(
      """Seq.empty.mkString("(", ",", ")" )""".stripMargin,
      """emptyList().joinToString(",", "(", ")")""".stripMargin)

  def testSeqTail(): Unit =
    doExprTest(
      """Seq.empty.tail""".stripMargin,
      """emptyList().drop(1)""".stripMargin)

  def testSeqInit(): Unit =
    doExprTest(
      """Seq.empty.init""".stripMargin,
      """emptyList().dropLast(1)""".stripMargin)

  def testSeqHead(): Unit =
    doExprTest(
      """Seq.empty.head""".stripMargin,
      """emptyList().first()""".stripMargin)

  def testSeqApply(): Unit =
    doExprTest(
      """val s = Seq(1,2)
        |s(0)
      """.stripMargin,
      """val s: List<Int> = listOf(1, 2)
        |s[0]""".stripMargin)

  def testSeqConcat(): Unit =
    doExprTest(
      """val s1 = Seq(1,2)
        |val s2 = Seq(2,3)
        |s1 ++ s2
      """.stripMargin,
      """val s1: List<Int> = listOf(1, 2)
        |val s2: List<Int> = listOf(2, 3)
        |s1 + s2""".stripMargin)

  def testSeqNotEmpty(): Unit =
    doExprTest(
      """val s1 = Seq(1,2)
        |val s2 = Seq(2,3)
        |s1 ++ s2
      """.stripMargin,
      """val s1: List<Int> = listOf(1, 2)
        |val s2: List<Int> = listOf(2, 3)
        |s1 + s2""".stripMargin)


  def testStringRepeat(): Unit =
    doExprTest(
      """ "nya" * 4""".stripMargin,
      """ "nya".repeat(4)""".stripMargin)

}
