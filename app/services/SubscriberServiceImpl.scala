package services

import dao.SubscriptionDAO
import models.{Message, Subscription}
import utils.Logger

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

/**
 * Serwis filtrujący wiadomości
 */
class SubscriberServiceImpl @Inject()(
                                       subscriptionDAO: SubscriptionDAO
                                     )
                                     (
                                       implicit val executionContext: ExecutionContext
                                     ) extends SubscriberService with Logger {

  /**
   * Aktualizuje subskrypcją subskrybentów
   *
   * @param message wiadomość
   * @return zwraca subskrybenta, jeżeli zaktualizowano stan jego subskrypcji
   */
  def update(message: Message): Future[Either[String, Subscription]] = Future {
    val text = message.message
    val subscription = subscriptionDAO.find(message.recipient)
    val active = text == "START"
    (text match {
      case "START" | "STOP" =>
        updateActivity(isActive = active, subscription, message)
      case _ =>
        Left("Text of the message should be equal to 'START' or 'STOP'.")
    }) match {
      case Right(r) =>
        logger.info(s"Subscription of recipient with id '${message.recipient}' set to ${r.isActive.toString.toUpperCase}")
        Right(r)

      case Left(error) =>
        logger.info(error)
        Left(error)
    }
  }

  /**
   * Aktualizuje flagę posiadania subskrypcji
   *
   * @param isActive     czy jest aktywna
   * @param subscription subskrypcja
   * @param message      wiadomość
   * @return na lewo błąd, na prawo zaktualizowana subskrypcja
   */
  def updateActivity(isActive: Boolean, subscription: Option[Subscription], message: Message): Either[String, Subscription] = {
    subscription.flatMap(s => subscriptionDAO.update(s.setActivity(isActive))).toRight(s"Recipient with id '${message.recipient}' not found in database.")
  }
}
