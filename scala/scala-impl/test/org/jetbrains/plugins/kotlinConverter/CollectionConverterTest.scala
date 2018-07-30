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


  def testStringRepeat(): Unit =
    doExprTest(
      """ "nya" * 4""".stripMargin,
      """ "nya".repeat(4)""".stripMargin)

}
