package dao

import models.Subscription

trait SubscriptionDAO {

  /**
   * Wyszukuje subskrypcji użytkownika
   *
   * @param userId identyfikator użytkownika
   * @return subskrypcja lub None
   */
  def find(userId: String): Option[Subscription]

  /**
   * Aktualizuje subskrypcję
   *
   * @param subscription subskrypcja do aktualizacji
   * @return subskrypcja jeżeli udało się zaktualizować
   */
  def update(subscription: Subscription): Option[Subscription]
}
