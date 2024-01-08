package models

import play.api.libs.json.{Json, Reads}

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
