package bvo.scala1.controllers

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._

class ApplicationFuncTest extends Specification {

  "email question" in {
    val Some(result) = routeAndCall(FakeRequest(GET, "/?q=Quelle+est+ton+adresse+email"))

    status(result) must equalTo(OK)
    contentAsString(result) must equalTo("bvo123@123mail.org")
  }

  "scalaskel question" in {
    val Some(result) = routeAndCall(FakeRequest(GET, "/scalaskel/change/7"))

    status(result) must equalTo(OK)
    contentAsString(result) must equalTo("[{\"foo\":7},{\"bar\":1}]")
  }
  "/?q=1+1" in {
    val Some(result) = routeAndCall(FakeRequest(GET, "/?q=1+1"))

    status(result) must equalTo(OK)
    contentAsString(result) must equalTo("2")
  }

  "/?q=2+2" in {
    val Some(result) = routeAndCall(FakeRequest(GET, "/?q=2+2"))

    status(result) must equalTo(OK)
    contentAsString(result) must equalTo("4")
  }

  "fractionnal output" in {
    val Some(result) = routeAndCall(FakeRequest(GET, "/?q=(1+2)/2"))
    status(result) must equalTo(OK)
    contentAsString(result) must equalTo("1,5")
  }

  "fractionnal input" in {
    val Some(result) = routeAndCall(FakeRequest(GET, "/?q=(1,5)*2"))
    status(result) must equalTo(OK)
    contentAsString(result) must equalTo("3")
  }

  "big numbers" in {
    val Some(result) = routeAndCall(FakeRequest(GET, "/?q=553344300034334349999000"))
    status(result) must equalTo(OK)
    contentAsString(result) must equalTo("553344300034334349999000")
  }

  "((1+2)+3+4+(5+6+7)+(8+9+10)*3)/2*5" in {
    val Some(result) = routeAndCall(FakeRequest(GET, "/?q=((1+2)+3+4+(5+6+7)+(8+9+10)*3)/2*5"))
    status(result) must equalTo(OK)
    contentAsString(result) must equalTo("272,5")
  }

  "flightOptimizer" in {
    val json = 
    "[" +
      "                { \"VOL\": \"MONAD42\", \"DEPART\": 0, \"DUREE\": 5, \"PRIX\": 10 }," +
      "                { \"VOL\": \"META18\", \"DEPART\": 3, \"DUREE\": 7, \"PRIX\": 14 }," +
      "                { \"VOL\": \"LEGACY01\", \"DEPART\": 5, \"DUREE\": 9, \"PRIX\": 8 }," +
      "                { \"VOL\": \"YAGNI17\", \"DEPART\": 5, \"DUREE\": 9, \"PRIX\": 7 }" +
      "        ]"
      
    println("################################### flightOptimizer ##################")
    val Some(result)= routeAndCall(FakeRequest(
      POST, 
      "/jajascript/optimize",
      FakeHeaders(Map("Content-Type" -> Seq("application/json"))), json))
      
      status(result) must equalTo(CREATED)
    contentAsString(result) must equalTo("{\"gain\":18,\"path\":[\"MONAD42\",\"LEGACY01\"]}")
  }

}