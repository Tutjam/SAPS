package controllers

import models.{Message, Subscription}
import play.api.libs.json.Json

import javax.inject._
import play.api.mvc._
import services.SubscriberService
import utils.JsParser

import scala.concurrent.{ExecutionContext, Future}

class SubscriptionController @Inject()(cc: ControllerComponents,
                                       subscriberService: SubscriberService
                             )(
                               implicit val executionContext: ExecutionContext
                             ) extends AbstractController(cc) {

  /**
   * Aktualizuje subskrypcję dla systemu anty phishingowego
   */
  def update() = Action.async { request =>
    JsParser.parse[Message](request.body) match {
      case Some(msg) =>
        subscriberService.update(msg).map {
          case Right(subscriber) =>
            Ok(Json.obj("subscription" -> Subscription.toJson(subscriber)))

          case Left(error) =>
            BadRequest(Json.obj("error" -> error))
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
