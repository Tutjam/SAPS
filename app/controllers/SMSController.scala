package controllers

import models.Message
import play.api.libs.json.Json

import javax.inject._
import play.api.mvc._
import services.MessageFilterService
import utils.JsParser

import scala.concurrent.{ExecutionContext, Future}

class SMSController @Inject()(cc: ControllerComponents,
                              messageFilterService: MessageFilterService
                             )(
                               implicit val executionContext: ExecutionContext
                             ) extends AbstractController(cc) {

  /**
   * Sprawdza, czy wiadomość jest bezpieczna
   */
  def check() = Action.async { request =>
    JsParser.parse[Message](request.body) match {
      case Some(msg) =>
        messageFilterService.filter(msg).map {
          case Some(safeMsg) =>
            Ok(Json.obj("data" -> Json.toJson(safeMsg)))

          case None =>
            NoContent
        }

      case None =>
        val errorObj = Json.obj(
          "source" -> request.body.asJson,
          "title" -> "Invalid request body"
        )

        Future.successful(
          BadRequest(Json.obj("error" -> errorObj)))
    }
  }
}
