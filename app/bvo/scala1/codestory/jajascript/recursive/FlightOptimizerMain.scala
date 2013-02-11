package bvo.scala1.codestory.jajascript.recursive
import scala.io.Source

object FlightOptimizerMain extends FlightOptimizer {
  def main(args: Array[String]) {
    println("?")
    readLine()
    val json = Source.fromFile("test/bvo/scala1/codestory/jajascript-50000.txt").mkString
    optimize(json)
  }
}