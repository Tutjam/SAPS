package dao

import models.Subscription
import utils.Logger

import javax.inject.{Inject, Singleton}
import scala.collection.mutable.ListBuffer

@Singleton
class SubscriptionDAOImpl @Inject() extends SubscriptionDAO with Logger {

  /**
   * 'Baza danych' Subskrybentów
   */
  val list: ListBuffer[Subscription] = ListBuffer[Subscription]()

  (0L until 10L).toList.map { subId =>
    val subscription = new Subscription(subId, math.random() < 0.5)
    logger.info(s"Subscription added to database : ${subscription}")
    list.addOne(subscription)
  }

  /**
   * Wyszukuje subskrypcji użytkownika
   *
   * @param userId identyfikator użytkownika
   * @return subskrypcja lub None
   */
  override def find(userId: Long): Option[Subscription] = list.find(_.getUserId == userId)

  /**
   * Aktualizuje subskrypcję
   *
   * @param subscription subskrypcja do aktualizacji
   * @return subskrypcja jeżeli udało się zaktualizować
   */
  override def update(subscription: Subscription): Option[Subscription] = Some(subscription)
}
