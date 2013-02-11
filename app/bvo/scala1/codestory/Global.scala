package bvo.scala1.codestory

import play.api._
import play.api.mvc._
import play.api.mvc.Results._

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    Logger.info("Application has started")
  }

  override def onStop(app: Application) {
    Logger.info("Application shutdown...")
  }

  override def onError(request: RequestHeader, ex: Throwable) = {
    Logger.error("onError", ex)
    InternalServerError(ex.getMessage())
  }

  
  override def onHandlerNotFound(request: RequestHeader): Result = {
    Logger.warn("onHandlerNotFound " + request)
    NotFound("Page not found:" + request.path)
  }

  override def onBadRequest(request: RequestHeader, error: String) = {
    Logger.warn("onBadRequest " + request.path + ":" + error)
    BadRequest("Bad Request: " + error)
  }

  override def onRouteRequest(request: RequestHeader): Option[Handler] = {
    Logger.info("request:" + request.toString + " from " + request.remoteAddress + "/" + request.path + " [" + request.contentType + "]:" + request.rawQueryString)
    
    super.onRouteRequest(request)
  }

}