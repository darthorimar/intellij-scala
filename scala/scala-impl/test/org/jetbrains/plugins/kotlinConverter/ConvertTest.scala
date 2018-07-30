package org.jetbrains.plugins.kotlinConverter

import org.jetbrains.plugins.kotlinConverter.ast._
import org.jetbrains.plugins.scala.base.ScalaLightPlatformCodeInsightTestCaseAdapter
import org.jetbrains.plugins.scala.lang.psi.api.ScalaFile
import org.junit.Assert._

class ConvertTest extends ConverterTestBase {

  def testFuncCall(): Unit = {
    doTest(
      """def a = "ny".substring(1,2)""",
      """public fun a(): String = "ny".substring(1,2)""")
  }

  def testOptionConverters(): Unit = {
    doTest("def a = Some(1).map(x => x + 1).get",
      "public fun a(): Int =1?.let { x -> x + 1}!!")
  }


  def testUncarry(): Unit = {
    doTest(
      """def a(x: Int, b: String)(c: Char) = 1
        |public def b = a(1,"2")('3')
      """.stripMargin,
      """fun a(x: Int, b: String, c: Char): Int =1
        |public fun b(): Int =a(1, "2", '3')""".stripMargin)
  }
  def testMatch(): Unit = {
    doTest(
      """
        |trait A
        |case class B(a: A, b: A) extends A
        |case class C(c: Int) extends A
        |
        |def a(x: Any) = x match {
        |  case B(a, B(C(e: Int), C(d))) if e > 3 => e
        |  case B(a, b) => 42
        |  case q: Int if q == 2 => q
        |  case 2 if 1 == 1 => q
        |  case _ if 1 == 1 => q
        | }
      """.stripMargin,
      """fun a(x: Any): Any ={
        |  val match: Any = x
        |  data class `B(a, B(C(e: Int), C(d)))_data`(public val a: Any, public val e: Int, public val d: Any)
        |  data class `B(a, b)_data`(public val a: Any, public val b: Any)
        |  val `B(a, B(C(e: Int), C(d)))` by lazy {
        |    if (match is B){
        |      val (a, l1) = match
        |      if (l1 is B){
        |        val (l2, l3) = l1
        |        if (l2 is C && l3 is C){
        |          val e = l2
        |          val d = l3
        |          if (e is Int)if (e > 3)return@lazy `B(a, B(C(e: Int), C(d)))_data`(a, e, d)
        |        }
        |      }
        |    }
        |    return@lazy null
        |  }
        |  val `B(a, b)` by lazy {
        |    if (match is B){
        |      val (a, b) = match
        |      return@lazy `B(a, b)_data`(a, b)
        |    }
        |    return@lazy null
        |  }
        |  return when {
        |    `B(a, B(C(e: Int), C(d)))` != null -> {
        |      val (a, e, d) = `B(a, B(C(e: Int), C(d)))`
        |      e
        |    }
        |    `B(a, b)` != null -> {
        |      val (a, b) = `B(a, b)`
        |      42
        |    }
        |    match is Int && match == 2 -> match
        |    match == 2 && 1 == 1 -> q
        |    1 == 1 -> q}
        |
        |}
        |public interface A
        |public data class B(public val a: A, public val b: A) : A
        |public data class C(public val c: Int) : A """.stripMargin)
  }

  def testOverride(): Unit =
    doTest(
      """
        | class A {
        |      def a: Int = 5
        |    }
        |    class B extends A {
        |      def a: Int = 42
        |    }
      """.stripMargin,
      """
        |public open class A() {
        |  public fun a(): Int =5
        |}
        |public open class B() : A {
        |  public override fun a(): Int =42
        |}""")

  def testImplicitLambda(): Unit =
    doTest(
      """
        | def a = Seq(1,2,3).map {
        |    case x if x >= 3 => x - 3
        |  case _ => 0
        | }
      """.stripMargin,
      """public fun a(): List<Int> =listOf(1, 2, 3).map { val match = it
        |when {
        |  match >= 3 -> {
        |    match - 3
        |  }
        |  else -> {
        |    0
        |  }}
        | }""")

  def testCasts(): Unit =
    doTest(
      """def a = 1.asInstanceOf[Long]
        |def a = 1.isInstanceOf[Long]
      """.stripMargin,
      """public fun a(): Long =(1 as Long)
        |public fun a(): Long =(1 is Long)
      """.stripMargin)

  def testSeq_empty(): Unit =
    doTest(
      """def a = Seq.empty[Int]""".stripMargin,
      """public fun a(): List<Int> =emmptyList<Int>()""".stripMargin)

  def testListCon(): Unit =
    doTest(
      """def a = 1 :: Nil""".stripMargin,
      """public fun a(): List<Int> =listOf(1) + emptyList()""".stripMargin)

  def testMkString(): Unit =
    doTest(
      """def a = Seq.empty.mkString("(", ",", ")" )""".stripMargin,
      """public fun a(): String =emptyList().joinToString(",", "(", ")")""".stripMargin)

def testSeqTail(): Unit =
    doTest(
      """def a = Seq.empty.tail""".stripMargin,
      """public fun a(): List<Nothing> =emptyList().drop(1)""".stripMargin)

  def testSeqInit(): Unit =
    doTest(
      """def a = Seq.empty.init""".stripMargin,
      """public fun a(): List<Nothing> =emptyList().dropLast(1)""".stripMargin)

def testTryFinally(): Unit =
    doTest(
      """def a = try 1 finally 5""".stripMargin,
      """public fun a(): Int =try {
        |  1
        |} finally {
        |  5
        |}""".stripMargin)

  def testStringRepeat(): Unit =
    doTest(
      """def a = "nya" * 4""".stripMargin,
      """public fun a(): String ="nya".repeat(4)""".stripMargin)

  def testClassTypeParams(): Unit =
    doTest(
      """class A[T]""".stripMargin,
      """public open class A<T>()""".stripMargin)

  def testFunctionTypeParams(): Unit =
    doTest(
      """def a[T] = Seq.empty[T]""".stripMargin,
      """public fun<T> a(): List<T> =emptyList<T>()""".stripMargin)

  def testStringInterpolation(): Unit =
    doTest(
      """def a = s"${1} + $a"""".stripMargin,
      """public fun a(): String ="${1} + $a()"""".stripMargin)

 def testSomeInWhen(): Unit =
    doTest(
      """def a = Some(1) match {
        |   case Some(x) => x + 1
        |   case None => 0
        |}"""".stripMargin,
      """public fun a(): Int {
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
        |    true -> {
        |      0
        |    }}
        |
        |}"""".stripMargin)

 def testRefInLitPattern(): Unit =
    doTest(
      """public fun a(): Int {
        |  val match1 = A
        |  return when {
        |    match1 == A -> {
        |      1
        |    }}        |
        |}
        |public object A """".stripMargin,
      """"""".stripMargin)
}

