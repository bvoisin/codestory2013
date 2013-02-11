package bvo.scala1.codestory

import org.scalatest._
import org.scalatest.matchers.ShouldMatchers
import java.math.BigDecimal

class EvaluatorTest extends FunSuite with BeforeAndAfter with ShouldMatchers with Evaluator {

  test("1+1") {
    assert("2" === evaluate("1+1"))
  }

  test("8*9") {
    assert("72" === evaluate("8*9"))
  }

  test("1 1") {
    assert("2" === evaluate("1 1"))
  }
  
  test("fractionnal output") {
    assert("1,5" === evaluate("(1+2)/2"))
  }
  
  test("fractionnal input") {
    assert("3" === evaluate("1,5*2"))
  }
  
  test("big numbers") {
    assert("553344300034334349999000" === evaluate("553344300034334349999000"))
  }
  
  //test("big numbers2") {
  //  assert(-1===evaluate("((1.1+2)+3.14+4+(5+6+7)+(8+9+10)*4267387833344334647677634)/2*553344300034334349999000"))
  //}
  
  test("((1+2)+3+4+(5+6+7)+(8+9+10)*3)/2*5") {
    assert("272,5" === evaluate("((1+2)+3+4+(5+6+7)+(8+9+10)*3)/2*5"))
  }
  
  test("big decimal parsing") {
    val txtVal = "553344300034334349999000"
    val bd = new BigDecimal(txtVal)
    assert(txtVal === numberFormat.format(bd))
  }
  
  test("big decimal parsing2") {
    val txtVal = "1.0000000000000000000000000000000000000000000000001"
    val bd = new BigDecimal(txtVal)
    assert(txtVal.replace(".",",") === numberFormat.format(bd))
  }
  
  test("huge precision") {
    assert("1,00000000000000000000000000000000000000000000000020000000000000000000000000000000000000000000000001" === evaluate("1.0000000000000000000000000000000000000000000000001*1.0000000000000000000000000000000000000000000000001"))
  }
}