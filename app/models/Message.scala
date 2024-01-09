package models

import play.api.libs.json.{Json, OWrites, Reads}

/**
 * Reprezentuje wiadomość SMS
 *
 * @param sender    identyfikator osoby wysyłającej
 * @param recipient identyfikator osoby otrzymującej
 * @param message   wiadomość
 */
case class Message(
                    sender: Long,
                    recipient: Long,
                    message: String
                  )

object Message {
  implicit val reads: Reads[Message] = Json.reads[Message]
  implicit val writes: OWrites[Message] = Json.writes[Message]
}
