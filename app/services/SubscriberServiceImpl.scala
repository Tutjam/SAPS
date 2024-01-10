package services

import dao.SubscriptionDAO
import models.{Message, Subscription}
import utils.{Logger, SubscriptionManager}

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
   * @return zaktualizowana subskrypcja na prawo, błąd na lewo
   */
  def update(message: Message): Future[Either[String, Subscription]] = Future {
    if(message.recipient == SubscriptionManager.ID) {
      val text = message.message
      val subscription = subscriptionDAO.find(message.sender)
      val active = text == "START"
      (text match {
        case "START" | "STOP" =>
          updateActivity(isActive = active, subscription, message)
        case _ =>
          Left("Text of the message should be equal to 'START' or 'STOP'.")
      }) match {
        case Right(r) =>
          logger.info(s"Subscription of sender with id '${message.sender}' set to ${r.isActive.toString.toUpperCase}")
          Right(r)

        case Left(error) =>
          logger.info(error)
          Left(error)
      }
    } else {
      logger.info("Recipient is not a SubscriptionManager.")
      Left("Recipient is not a SubscriptionManager.")
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
    subscription.flatMap(s => subscriptionDAO.update(s.setActivity(isActive))).toRight(s"Sender with id '${message.sender}' not found in database.")
  }
}
