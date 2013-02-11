package controllers

<<<<<<< HEAD
import bvo.scala1.codestory.CouldNotEvaluateException
import bvo.scala1.codestory.Evaluator
import bvo.scala1.codestory.scalaskel.ScalaskelChanger
import play.api.mvc.Action
import play.api.mvc.Controller
import play.api.mvc.Result
import play.api.Logger
import java.util.Date
import bvo.scala1.codestory.jajascript.recursive.FlightOptimizer
import bvo.scala1.codestory.scalaskel.ScalaskelChanger

object Application extends Controller with ScalaskelChanger with Evaluator with FlightOptimizer {

  def index = Action {
    request =>
      {
        if (request.queryString.contains("q")) {
          val q = request.queryString("q")
          if (q != null) {
            answerQuestion(q(0))
          } else {
            BadRequest("Did not understand request q='" + q + "'")
          }
        } else {
          Ok(<h1>Test app for <a href='http://code-story.net'>code-story</a></h1>).as("text/html")
        }
      }
  }

  private val directAnswers = Map(
    "Quelle est ton adresse email" -> "bvo123@123mail.org",
    "Es tu heureux de participer(OUI/NON)" -> "OUI",
    "Es tu abonne a la mailing list(OUI/NON)" -> "OUI",
    "Es tu pret a recevoir une enonce au format markdown par http post(OUI/NON)" -> "OUI",
    "Est ce que tu reponds toujours oui(OUI/NON)" -> "NON",
    "As tu passe une bonne nuit malgre les bugs de l etape precedente(PAS_TOP/BOF/QUELS_BUGS)" -> "QUELS_BUGS",
    "As tu bien recu le second enonce(OUI/NON)" -> "OUI",
    "As tu copie le code de ndeloof(OUI/NON/JE_SUIS_NICOLAS)" -> "NON",
    "Souhaites-tu-participer-a-la-suite-de-Code-Story(OUI/NON)" -> "OUI",
    "As tu bien recu le premier enonce(OUI/NON)" -> "OUI")

  private def answerQuestion(question: String): Result = {
    if (directAnswers.contains(question)) {
      val answer = directAnswers(question)
      Logger.info("=> directAnswer " + answer)
      return Ok(answer)
    } else {
      try {
      val answer = evaluate(question)
      Logger.info("=> evaluation " + answer)
      return Ok(answer)
      }
      catch {
        case e:CouldNotEvaluateException => Logger.info("Evaluation miss", e)
      }
    }

    Logger.warn("Did not understand your question '" + question + "'")
    BadRequest("Did not understand your question '" + question + "'")
  }

  def enonce(id: Long) = Action(parse.tolerantText) {
    request =>
      {
        val body: String = request.body
        Logger.info("For enonce " + id + ", retrieved bodyText:\n" + body)
        Created("")
      }
  }

  def scalaskelChange(sum: Int) = Action {
    request =>
      {
        Ok(findPossibleChangesAsJSon(sum))
      }
  }
  
  def jajascriptOptimize() = Action(parse.tolerantText(Int.MaxValue)) {
    request => {
      try {
    	Logger.info("content type:" + request.contentType);
        val body: String = request.body
        
        if (body.length()<500) {
          Logger.info("Received jajascript Optimize2:\n" + body)
        }
        else {
        	Logger.info("Received jajascript Optimize2:\n" + body.substring(0,500) + "...")
        }
        val json = optimize(body)
        Logger.info("Replied:\n" + json)
        Created(json)
      }
      catch {
        case t:Throwable => {
          Logger.error("Exception:", t)
          throw t
        }
      }
    }
  }
  
  def viewLog() = Action {
    request =>
      {
        val logs=scala.io.Source.fromFile("logs/stdout.log").mkString
        Ok("Logs at " +  (new Date) + ":\n" + logs)
      }
=======
import play.api._
import play.api.mvc._

object Application extends Controller {
  
  def index = Action { 
	request => {
		if (request.queryString.contains("q")) {
			val q=request.queryString("q")
			if (q!=null && q(0)=="Quelle est ton adresse email") {
				Ok("bvo123@123mail.org")
			}
			else {
				BadRequest("Did not understand request q='" + q + ")")
			}
		}
		else {
			Ok(<h1>Test app for <a href='http://code-story.net'>code-story</a></h1>).as("text/html")
		}
	}
  }
  
  def toto = Action {
    Ok(<a href='http://2.bp.blogspot.com/-guhquZHsHPU/UIFzBQ-RYJI/AAAAAAAAABg/rkfCynXR_2c/s1600/titi%2B-%2BCopie.jpg'>Titi</a>).as("text/html")
>>>>>>> With scala
  }
}