package bvo.scala1.codestory.jajascript

import com.codahale.jerkson.Json._
import scala.collection.mutable.ArrayBuffer
import java.io.StringWriter
import java.util.HashMap
import scala.collection.mutable.HashMap
import play.api.Logger
import java.io.ByteArrayInputStream
import java.io.Writer

object DepartureOrdering extends Ordering[FlightRequest] {
  def compare(a: FlightRequest, b: FlightRequest) = {
    var ret = a.departure compare b.departure
    if (ret == 0) {
      ret = a.duration compare b.duration
    }
    ret
  }
}

case class FlightOptimizerResponse(gain: Int, path: Seq[String])

abstract trait FlightOptimizerBase {
  def optimize(jsonString: String): String = {
    Logger.info("Received jsonString")
    val fullRequestList = stream[FlightRequest](new ByteArrayInputStream(jsonString.getBytes)).toBuffer
    Logger.info("Parsed into " + fullRequestList.size + " FlightRequests")

    val start = System.currentTimeMillis()
    
    val requests = prepareRequestSeq(fullRequestList.asInstanceOf[IndexedSeq[FlightRequest]])
    
    val response:FlightOptimizerResponse = optimize(requests)
    
    Logger.info({ "Found best proposition in " + (System.currentTimeMillis() - start) + " ms" })
    
    val ret = generate(response)
    
    Logger.debug({ "sortedFilteredFlights:\n" + dumpFlightGraph(requests, Set()) });//TODO selectedVols
    return ret
  }
  
  def optimize(requests: IndexedSeq[FlightRequest]): FlightOptimizerResponse

  private def dumpFlightGraph(requests: Seq[FlightRequest], selectedVols: Set[String]): String = {
    val writer = new StringWriter()
    dumpFlightGraph(requests, selectedVols, writer)
    writer.toString()
  }

  private def dumpFlightGraph(requests: Seq[FlightRequest], selectedVols: Set[String], writer: Writer): Unit = {
    for (f <- requests) {
      writer.append(" " * (f.DEPART % 50))
      writer.append((if (selectedVols.contains(f.VOL)) "#" else "-") * f.DUREE)
      writer.append(" ")
      writer.append(f.PRIX.toString())
      writer.append(" ")
      writer.append(f.VOL)
      writer.append("\n")
    }
  }

  private def filterFlightRequestsStep1(sortedRequests: Seq[FlightRequest]): IndexedSeq[FlightRequest] = {

    val sortedFilteredList = ArrayBuffer[FlightRequest]()
    var bestFlightForDepartureAndDuration: FlightRequest = null
    var bestFlightAlreadyMarked = true

    for (flight <- sortedRequests) {
      if (bestFlightForDepartureAndDuration == null || flight.departure > bestFlightForDepartureAndDuration.departure) {
        if (!bestFlightAlreadyMarked) {
          //mark this best one, it can always be useful for shorter durations
          //but it might stay the bestFlight because of it's high price
          sortedFilteredList += bestFlightForDepartureAndDuration
          bestFlightAlreadyMarked = true
        }
        //on change d'horaire de départ, il faut recommencer
        bestFlightForDepartureAndDuration = flight
        bestFlightAlreadyMarked = false
      } else {
        if (flight.duration > bestFlightForDepartureAndDuration.duration && !bestFlightAlreadyMarked) {
          //mark this best one, it can always be useful for shorter durations
          //but it might stay the bestFlight because of it's high price
          sortedFilteredList += bestFlightForDepartureAndDuration
          bestFlightAlreadyMarked = true
        }

        if (flight.price > bestFlightForDepartureAndDuration.price) {
          if (!bestFlightAlreadyMarked) {
            //mark this best one, it can always be useful for shorter durations
            //but it might stay the bestFlight because of it's high price
            sortedFilteredList += bestFlightForDepartureAndDuration
            bestFlightAlreadyMarked = true
          }
          bestFlightForDepartureAndDuration = flight
          bestFlightAlreadyMarked = false
        }
        //        else {
        //          ogger.debug("Ignoring flight " + flight + ", overriden by " + bestFlightForDepartureAndDuration)
        //        }
      }
    }
    if (!bestFlightAlreadyMarked) {
      //mark this best one, it can always be useful for shorter durations
      //but it might stay the bestFlight because of it's high price
      sortedFilteredList += bestFlightForDepartureAndDuration
      bestFlightAlreadyMarked = true
    }

    sortedFilteredList
  }

  private def filterFlightRequestsStep2(sortedList: IndexedSeq[FlightRequest]): IndexedSeq[FlightRequest] = {
    val sortedFilteredList = ArrayBuffer[FlightRequest]()

    for (i <- 0 until sortedList.size) {
      val checkedFlight = sortedList(i)
      val betterFlight = findBetterFlight(checkedFlight, sortedList.view(i + 1, sortedList.size))
      if (betterFlight == null) {
        sortedFilteredList += checkedFlight
      } //else ignoring the checkedFlight
    }
    sortedFilteredList
  }

  private def findBetterFlight(flightRequest: FlightRequest, followingFlights: Iterable[FlightRequest]): FlightRequest = {
    for (otherRequest <- followingFlights) {
      if (otherRequest.price >= flightRequest.price && flightRequest.completelyOverlaps(otherRequest)) {
        return otherRequest
      }
      if (otherRequest.departure > flightRequest.arrival) {
        return null
      }
    }
    return null
  }

  protected def prepareRequestSeq(list: IndexedSeq[FlightRequest]): IndexedSeq[FlightRequest] = {
    var sortedList: IndexedSeq[FlightRequest] = list.sorted(DepartureOrdering)
    Logger.debug({ "sorted:" + sortedList.size + "\n" + dumpFlightGraph(sortedList, Set()) });

    sortedList = filterFlightRequestsStep1(sortedList)
    Logger.info("Filtered1 => " + sortedList.size + " requests")
    Logger.debug({ "filtered: \n" + dumpFlightGraph(sortedList, Set()) });

    sortedList = filterFlightRequestsStep2(sortedList)
    Logger.info("Filtered2 => " + sortedList.size + " requests")
    Logger.debug({ "filtered2:\n" + dumpFlightGraph(sortedList, Set()) });

    sortedList
  }
}