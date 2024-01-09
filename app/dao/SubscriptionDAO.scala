package dao

import models.Subscription
import utils.Logger

import scala.collection.mutable.ListBuffer

object SubscriptionDAO extends Logger {

  /**
   * 'Baza danych' Subskrybentów
   */
  val list: ListBuffer[Subscription] = ListBuffer[Subscription]()

  (0L until 10L).toList.map { subId =>
    val subscription = new Subscription(subId, math.random() < 0.5)
    logger.info(s"Subscription added to database : ${subscription}")
    list.addOne(subscription)
  }
}
