package utils

import play.api.libs.json.{Json, Reads}
import play.api.mvc.AnyContent

object JsParser {

  def parse[T](anyContent: AnyContent)(implicit reads: Reads[T]): Option[T] = {
    anyContent.asJson match {
      case Some(message) =>
        Json.fromJson[T](message).asOpt
      case None =>
        None
    }
  }
}
