package bvo.scala1.codestory.scalaskel

import org.scalatest._
import org.scalatest.matchers.ShouldMatchers
import bvo.scala1.codestory.scalaskel._

class ScalaskelTest extends FunSuite with BeforeAndAfter with ShouldMatchers with ScalaskelChanger {

  test("all changes contains at least one Foo(s) solution") {
    for (i <- 7 until 50) {
      val actual: Seq[Change] = findPossibleChanges(i).toSeq
      println("i=" + i)
      checkChangeContainsAtLeast(i, Change(Foo -> i))
    }
  }

  private def checkChangeContainsAtLeast(sum: Int, aSolution: Change) {
    val solutions: Seq[Change] = findPossibleChanges(sum).toSeq
    assertAllSolutionsAreValid(sum, solutions)

    solutions should contain(aSolution)
  }

  private def assertAllSolutionsAreValid(expectedSum: Int, solutions: Seq[Change]) {
    for (solution <- solutions) {
      var solutionSum = 0
      for ((coin, count) <- solution) {
        solutionSum += coin.value * count
      }

      assert(solutionSum === expectedSum, "Sum of solution " + solution + " does not match expected sum:")
    }
  }

  test("all changes of 7*N least a Bar(s) only solution") {
    for (i <- 1 until 10) {
      checkChangeContainsAtLeast(i * 7, Change(Bar -> i))
    }
  }
  test("all changes above 8 contains at least one Bar + Foo(s) solution") {
    for (i <- 8 until 50) {
      checkChangeContainsAtLeast(i, Change(Foo -> (i - 7), Bar -> 1))
    }
  }

  test("all changes above 40 contains at least one Baz + Qix + Bar + Foo(s) solution") {
    for (i <- 40 until 50) {
      checkChangeContainsAtLeast(i, Change(Foo -> (i - 39), Bar -> 1, Qix -> 1, Baz -> 1))
    }
  }

  test("all changes of 21*N least a Baz(s) only solution") {
    for (i <- 1 until 5) {
      checkChangeContainsAtLeast(i * 21, Change(Baz -> i))
    }
  }

  test("with bson output 1") {
    checkJSon("[{\"foo\":1}]", 1)
  } 

  test("with bson output 7") {
    checkJSon("[ {\"foo\": 7}, {\"bar\": 1} ]", 7)
  }

  private def checkJSon(expectedJSon: String, sum: Int) = {
    val ret = findPossibleChangesAsJSon(sum).replaceAll("[ ,\n]", "");
    assert(ret === expectedJSon.replaceAll("[ ,\n]", ""))
  }
}