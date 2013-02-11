package bvo.scala1.codestory.jajascript.iterative

import com.codahale.jerkson.Json._
import java.io.ByteArrayInputStream
import scala.collection.mutable.ArrayBuffer
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
import scala.collection.mutable.Stack
import scala.io.Source
import bvo.scala1.codestory.jajascript._

trait FlightOptimizer extends FlightOptimizerBase {
  
  override def optimize(requests: IndexedSeq[FlightRequest]): FlightOptimizerResponse = {

    val resp: RequestChain = findBestChain(requests)

    val vols = new ArrayBuffer[String](resp.propCount-1)

    var respIter = resp
    while (respIter != null) {
      {
        val request = respIter.request
        if (request.price != 0) {
          vols.append(request.flight)
        }
        respIter = respIter.previous
      }
    }

    return FlightOptimizerResponse(resp.gain, vols.reverse)
  }

  private def findBestChain(sortedList: IndexedSeq[FlightRequest]): RequestChain = {
    //Logger.info("findBestProposition(" + index + " " + time + ") ....")
    //list of index to look, with the best previous chain
    val latestDeparture = sortedList.last.departure
    val bestChainBeforeTime = new BestChainsByArrival(latestDeparture + 2)

    //now, analyze from each possible departure
    for (index <- 0 until sortedList.size) {
      val departure = sortedList(index).departure
      val bestChainToIndex = bestChainBeforeTime.getBestChainAt(departure)

      val lastRequest = bestChainToIndex.request
      val previousArrival = lastRequest.arrival

      var firstNextArrival = Int.MaxValue

      //maintenant on regarde les vols qui pourraient suivre, 
      var i = index
      while (i < sortedList.size && sortedList(i).departure < firstNextArrival) { //les vols qui partiraient après ne sont pas intéressant comme vol suivant
        val possibleFollowingFlight = sortedList(i)
        var arrival = possibleFollowingFlight.arrival

        if (arrival < firstNextArrival) {
          firstNextArrival = possibleFollowingFlight.arrival
        }

        bestChainBeforeTime.registerChain(RequestChain(bestChainToIndex, possibleFollowingFlight))
        i += 1
      }
    }
    return bestChainBeforeTime.getLast()
  }
}

private class BestChainsByArrival(val maxTime:Int) {
  val arr = new Array[RequestChain](maxTime+1)
  
  var maxUsedTime = 0
  
  {
    arr(0)= RequestChain(null, FlightRequest("", 0, 0, 0))
  }
  
  def registerChain(chain:RequestChain) = {
    var arrival = chain.request.arrival
    if (arrival>maxTime) {
      arrival=maxTime
    }
    val newChainGain = chain.gain
    
    val notEmptyTimeBeforeArrival = getFirstNotEmptyIndexBefore(arrival)

    val previousChainBeforeArrival = arr(notEmptyTimeBeforeArrival)

    if (previousChainBeforeArrival.gain < newChainGain) {
      //this chain is the best for this arrival time
        arr(arrival) = chain
	
	    if (arrival > maxUsedTime) {
	      maxUsedTime = arrival
	    } else {
	      var j = arrival + 1
	      while (j <= maxUsedTime) {
	        val futureChain = arr(j)
	        if (futureChain != null) {
	          if (futureChain.gain < newChainGain) {
	            arr(j) = null
	          } else if (futureChain.gain >= newChainGain) {
	            j = maxUsedTime //break
	          }
	        }
	        j += 1
	      }
	    }    
    }
  }
  def getBestChainAt(time:Int) = arr(getFirstNotEmptyIndexBefore(time))
   
  def getLast() = arr(getFirstNotEmptyIndexBefore(maxUsedTime))
  
  private def getFirstNotEmptyIndexBefore(time: Int): Int = {
    var t = time
    while (arr(t) == null) { t -= 1 }
    t
  }
}

private case class RequestChain(previous: RequestChain, request: FlightRequest) {
  val propCount: Int = if (previous == null) 1 else previous.propCount + 1
  val gain: Int = if (previous == null) request.price else request.price + previous.gain
}
