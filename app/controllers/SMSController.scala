package controllers

import models.{Message, Subscription}
import play.api.libs.json.Json
import play.api.mvc._
import services.{FilterService, SubscriberService}
import utils.JsParser

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

class SMSController @Inject()(cc: ControllerComponents,
                              messageFilterService: FilterService,
                              subscriberService: SubscriberService
                             )(
                               implicit val executionContext: ExecutionContext
                             ) extends AbstractController(cc) {

  /**
   * Sprawdza, czy wiadomość jest bezpieczna
   */
  def check() = Action.async { request =>
    JsParser.parse[Message](request.body) match {
      case Some(msg) =>
        processTheMessage(msg)

      case None =>
        val errorObj = Json.obj(
          "source" -> request.body.asJson,
          "title" -> "Invalid request body"
        )

        Future.successful(
          BadRequest(Json.obj("error" -> errorObj)))
    }
  }

  private def processTheMessage(msg: Message) = {
    subscriberService.update(msg).flatMap {
      case Right(subscriber) =>
        Future.successful(Ok(Json.obj("data" -> Subscription.toJson(subscriber))))

      case Left(error) =>
        filter(msg)
    }
  }

  private def filter(msg: Message) = {
    messageFilterService.filter(msg).map {
      case Some(safeMsg) =>
        Ok(Json.obj("data" -> Json.toJson(safeMsg)))

      case None =>
        NoContent
    }
  }
}
