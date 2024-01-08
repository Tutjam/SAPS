package utils

import play.api
import play.api.Logger

trait Logger {
  implicit val logger: api.Logger = Logger(s"application.${this.getClass.getName}")
}
