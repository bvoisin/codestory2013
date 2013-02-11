package bvo.scala1.codestory
import javax.script.ScriptEngineManager
import javax.script.ScriptEngine
import play.api.Logger
import java.text.NumberFormat
import java.util.Locale
import net.java.dev.eval.Expression

class CouldNotEvaluateException(val expression: String, exception: Exception) extends RuntimeException("Could not evaluate " + expression, exception)

trait Evaluator {
  //val scriptEngineManager = new ScriptEngineManager()
  //val engine = scriptEngineManager.getEngineByName("js");
  
  val numberFormat = NumberFormat.getInstance(Locale.FRENCH)
  
  numberFormat.setMinimumIntegerDigits(1)
  numberFormat.setMinimumFractionDigits(0)
  numberFormat.setMaximumIntegerDigits(Int.MaxValue)
  numberFormat.setMaximumFractionDigits(Int.MaxValue)
  numberFormat.setGroupingUsed(false)
  
  def evaluate(expr: String): String = {
    val expr2 = expr.replace(' ', '+').replace(",", ".");
    var result: java.math.BigDecimal=null

    try {
      Logger.info("Evaluating '" + expr2 + "'")
      //result = engine.eval(expr2).asInstanceOf[Double]
      val exp=new Expression(expr2) 
      result = exp.eval()
    } catch {
      case e: RuntimeException => throw new CouldNotEvaluateException(expr2, e)
    }

    return numberFormat.format(result)
  }

  private def isInteger(result: Double): Boolean = {
    math.abs(result - result.intValue()) < 0.001
  }
}