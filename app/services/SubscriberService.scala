package services

import models.{Message, Subscription}
import scala.concurrent.Future

/**
 * Serwis filtrujący wiadomości
 */
trait SubscriberService {

  /**
   * Aktualizuje subskrypcją subskrybentów
   *
   * @param message wiadomość
   * @return zwraca subskrybenta, jeżeli zaktualizowano stan jego subskrypcji
   */
  def update(message: Message): Future[Either[String, Subscription]]

  /**
   * Aktualizuje flagę posiadania subskrypcji
   *
   * @param isActive     czy jest aktywna
   * @param subscription subskrypcja
   * @param message      wiadomość
   * @return na lewo błąd, na prawo zaktualizowana subskrypcja
   */
  def updateActivity(isActive: Boolean, subscription: Option[Subscription], message: Message): Either[String, Subscription]
}
