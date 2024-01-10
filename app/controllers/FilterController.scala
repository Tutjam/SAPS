package controllers

import models.{Message, Subscription}
import play.api.libs.json.Json
import play.api.mvc._
import services.{FilterService, SubscriberService}
import utils.JsParser

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

class FilterController @Inject()(cc: ControllerComponents,
                                 filterService: FilterService,
                                 subscriberService: SubscriberService
                             )(
                               implicit val executionContext: ExecutionContext
                             ) extends AbstractController(cc) {

  /**
   * Sprawdza, czy wiadomość jest bezpieczna
   */
  def filter() = Action.async(parse.json) { request =>
    JsParser.parse[Message](request.body) match {
      case Some(msg) =>
        processTheMessage(msg)

      case None =>
        val errorObj = Json.obj(
          "source" -> request.body,
          "title" -> "Invalid request body"
        )

        Future.successful(
          BadRequest(Json.obj("error" -> errorObj)))
    }
  }

  private def processTheMessage(msg: Message) = {
    subscriberService.update(msg).flatMap {
      case Right(subscription) =>
        Future.successful(Ok(Json.obj("subscription" -> Subscription.toJson(subscription))))

      case Left(error) =>
        filterService.filter(msg).map {
          case Some(safeMsg) =>
            Ok(Json.obj("message" -> Json.toJson(safeMsg)))

          case None =>
            NoContent
        }
    }
  }
}
