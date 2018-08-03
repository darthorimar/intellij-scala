package org.jetbrains.plugins.kotlinConverter

import org.jetbrains.plugins.kotlinConverter.ast._
import org.jetbrains.plugins.scala.base.ScalaLightPlatformCodeInsightTestCaseAdapter
import org.jetbrains.plugins.scala.lang.psi.api.ScalaFile
import org.junit.Assert._
import org.junit.Ignore

class ConvertTest extends ConverterTestBase {

  def testFuncCall(): Unit = {
    doExprTest(
      """ "ny".substring(1,2)""",
      """  "ny".substring(1,2)""")
  }

  def testAutoImport(): Unit = {
    doExprTest(
      """ "ny".substring(1,2)""",
      """  "ny".substring(1,2)""")
  }

  def testUncarry(): Unit = {
    doTest(
      """def a(x: Int, b: String)(c: Char) = 1
        |def b = a(1,"2")('3')
      """.stripMargin,
      """fun a(x: Int, b: String, c: Char): Int =1
        |fun b(): Int =a(1, "2", '3')""".stripMargin)
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
        |  case 2 if 1 == 1 => 1
        |  case _ if 1 == 1 => 2
        | }
      """.stripMargin,
      """fun a(x: Any): Int {
        |  val match = x
        |  case class `B(a, B(C(e: Int), C(d)))_data`(public val a: Any, public val e: Int, public val d: Any)
        |  case class `B(a, b)_data`(public val a: Any, public val b: Any)
        |  val `B(a, B(C(e: Int), C(d)))` by lazy {
        |    if (match is B){
        |       val (a, l) = match
        |      if (l is B){
        |         val (l1, l2) = l
        |        if (l1 is C && l2 is C){
        |           val (e) = l1
        |           val (d) = l2
        |          if (e is Int)if (e > 3)return@lazy `B(a, B(C(e: Int), C(d)))_data`(a, e, d)
        |        }
        |      }
        |    }
        |    return@lazy null
        |  }
        |  val `B(a, b)` by lazy {
        |    if (match is B){
        |       val (a, b) = match
        |      return@lazy `B(a, b)_data`(a, b)
        |    }
        |    return@lazy null
        |  }
        |  return when {
        |    `B(a, B(C(e: Int), C(d)))` != null -> {
        |       val (a, e, d) = `B(a, B(C(e: Int), C(d)))`
        |      e
        |    }
        |    `B(a, b)` != null -> {
        |       val (a, b) = `B(a, b)`
        |      42
        |    }
        |    match is Int && match == 2 -> {
        |      match
        |    }
        |    match == 2 && 1 == 1 -> {
        |      1
        |    }
        |    1 == 1 -> {
        |      2
        |    }}
        |
        |}
        |interface A
        |data class B(public val a: A, public val b: A) : A()
        |data class C(public val c: Int) : A() """.stripMargin)
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
        |open class A() {
        |  fun a(): Int =5
        |}
        |open class B() : A() {
        |  override fun a(): Int =42
        |}""".stripMargin)

  def testImplicitLambda(): Unit =
    doExprTest(
      """
        |Seq(1,2,3).map {
        |    case x if x >= 3 => x - 3
        |  case _ => 0
        | }
      """.stripMargin,
      """listOf(1, 2, 3).map { val match = it
        |when {
        |  match >= 3 -> {
        |    match - 3
        |  }
        |  else -> {
        |    0
        |  }}
        | }""".stripMargin)


  def testSimpleValDef(): Unit =
    doTest(
      """val a = 5""".stripMargin,
      """val a: Int = 5""".stripMargin)

  def testValInClass(): Unit =
    doTest(
      """class A {
        |  val a: Int
        |  val b = 32
        |  var c: Int
        |  var d = 1
        |}
      """.stripMargin,
      """open class A() {
        |   val a: Int
        |   val b: Int = 32
        |   var c: Int
        |   var d: Int = 1
        |}
      """.stripMargin)


  def testClassTypeParams(): Unit =
    doTest(
      """class A[T]""".stripMargin,
      """open class A<T>()""".stripMargin)

  def testImplicits(): Unit =
    doExprTest(
      """ implicit def toStr(a: Int) = a.toString
        |  def q(s: String) = s
        |  println(q(1))""".stripMargin,
      """fun toStr(a: Int): String =a.toString
        |  fun q(s: String): String =s
        |  println(q(toStr(1)))""".stripMargin)

  def testFunctionTypeParams(): Unit =
    doTest(
      """def a[T] = Seq.empty[T]""".stripMargin,
      """fun<T> a(): List<T> =emptyList<T>()""".stripMargin)

  def testForYeild(): Unit =
    doTest(
      """val a = for {
        |  i <- Seq(1,2)
        |} yield i
        |""".stripMargin,
      """import kotlin.coroutines.experimental.buildSequence
        |
        |val a: List<Int> = buildSequence {
        |  for (i in listOf(1, 2)) {
        |      yield(i)
        |   }
        |}
      """.stripMargin)



}

