package bvo.scala1.codestory.jajascript

case class FlightRequest(VOL: String, DEPART: Int, DUREE: Int, PRIX: Int) {
  val flight = VOL
  val departure = DEPART
  val arrival = DEPART + DUREE
  val duration = DUREE
  val price = PRIX
  def completelyOverlaps(otherFlight: FlightRequest) = departure <= otherFlight.departure && arrival >= otherFlight.arrival
}