package models

import play.api.libs.json.{JsObject, Json}

/**
 * Model reprezentujący subskrybenta serwisu anty-phishingowego
 *
 * @param userId       identyfikator
 * @param isActive true jeżeli subskrybuje
 */
class Subscription(userId: Long,
                 var isActive: Boolean) {
  //getter
  def getUserId: Long = userId

  //setter
  def setActivity(isActive: Boolean): Unit =
    this.isActive = isActive

  //getter
  def getIsActive: Boolean =
    this.isActive

  override def toString: String =
    s"Subscription(userId=${userId}, isActive=${isActive})"
}

object Subscription {
  def toJson(subscription: Subscription): JsObject = {
    Json.obj("userId" -> subscription.getUserId,
      "isActive" -> subscription.isActive)
  }
}
