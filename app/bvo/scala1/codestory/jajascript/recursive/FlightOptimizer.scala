package bvo.scala1.codestory.jajascript.recursive

import com.codahale.jerkson.Json._
import java.io.ByteArrayInputStream
import scala.collection.mutable.ArrayBuffer
import scala.Int
import scala.Int
import play.api.Logger
import scala.collection.immutable.StringOps
import scala.collection.mutable.Map
import scala.collection.mutable.HashMap
import java.io.StringWriter
import java.io.Writer
import java.io.FileWriter
import java.io.BufferedInputStream
import java.io.FileInputStream
import java.io.File
import java.io.OutputStreamWriter
import java.io.BufferedOutputStream
import java.io.FileOutputStream
import scala.io.Source
import bvo.scala1.codestory.jajascript.FlightOptimizerBase
import bvo.scala1.codestory.jajascript.FlightRequest
import bvo.scala1.codestory.jajascript.FlightOptimizerResponse

private case class RequestChain(request: FlightRequest, next: RequestChain) {
  val propCount: Int = if (next == null) 1 else next.propCount + 1
  val gain: Int = if (next == null) request.price else request.price + next.gain
}

trait FlightOptimizer extends FlightOptimizerBase {

  override def optimize(requests: IndexedSeq[FlightRequest]): FlightOptimizerResponse = {
    val bestRouteMapFromIndex = HashMap[Int, RequestChain]()

    preFillBestRoutMap(requests, bestRouteMapFromIndex)
    Logger.info("Prefilled map with " + bestRouteMapFromIndex.size + " entries")

    val resp: RequestChain = findBestFollowingChain(requests, 0, requests(0).departure, bestRouteMapFromIndex)

    var respIter = resp
    val vols = new ArrayBuffer[String](resp.propCount)

    while (respIter != null) {
      vols.append(respIter.request.flight)
      respIter = respIter.next
    }

    return FlightOptimizerResponse(resp.gain, vols)
  }

  private def findBestFollowingChain(requests: IndexedSeq[FlightRequest], index: Int, time: Int, bestRouteMapFromIndex: Map[Int, RequestChain]): RequestChain = {
    var i = index
    //avancer jusqu'au prochain vol prennable (a partir de time)
    while (i < requests.size && requests(i).DEPART < time) {
      i += 1
    }
    if (i == requests.size) {
      return null
    }

    var currentBestChain = bestRouteMapFromIndex.getOrElseUpdate(i, { selectBestFlight(requests, i, bestRouteMapFromIndex) })

    // Logger.info("findBestProposition(" + index + " " + time + ") END")
    return currentBestChain
  }

  private def selectBestFlight(requests: IndexedSeq[FlightRequest], fromIndex: Int, bestRouteMapFromIndex: Map[Int, RequestChain]): RequestChain = {
    //voir le prochain vol possible
    var earliestArrival = Int.MaxValue
    var tooLate: Boolean = false
    var currentBestChain: RequestChain = null
    var i = fromIndex
    while (i < requests.size && !tooLate) {
      val request = requests(i)
      if (request.departure >= earliestArrival) { //no need to check now for flights departing after another possible flight arrived back 
        tooLate = true
      } else {
        //ce vol est envisageable
        val flightArrival = request.arrival
        if (earliestArrival > flightArrival) {
          earliestArrival = flightArrival
        }

        val bestChainWithThisFlight = RequestChain(request, findBestFollowingChain(requests, i + 1, flightArrival, bestRouteMapFromIndex))
        if (currentBestChain == null || currentBestChain.gain < bestChainWithThisFlight.gain) {
          currentBestChain = bestChainWithThisFlight
        }
        i = i + 1
      }
    }

    assert(currentBestChain != null)
    currentBestChain
  }

  private def preFillBestRoutMap(sortedFilteredList: IndexedSeq[FlightRequest], bestRouteMapFromIndex: scala.collection.mutable.HashMap[Int, RequestChain]): Unit = {
    for (i <- 0 until sortedFilteredList.size) {
      val index = sortedFilteredList.size - i - 1
      val request = sortedFilteredList(index)
      findBestFollowingChain(sortedFilteredList, index, request.departure, bestRouteMapFromIndex)
    }
  }
}