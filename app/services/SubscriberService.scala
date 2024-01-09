package services

import dao.SubscriptionDAO
import models.{Message, Subscription}
import utils.Logger

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

/**
 * Serwis filtrujący wiadomości
 */
class SubscriberService @Inject()(
                                 subscriptionDAO: SubscriptionDAO
                                 )
                                 (
                                   implicit val executionContext: ExecutionContext
                                 ) extends Logger {

  /**
   * Aktualizuje subskrypcją subskrybentów
   *
   * @param message wiadomość
   * @return zwraca subskrybenta, jeżeli zaktualizowano stan jego subskrypcji
   */
  def update(message: Message): Future[Either[String, Subscription]] = Future {
    val subscription = subscriptionDAO.find(message.recipient)
    (message.message match {
      case "START" =>
        setActivity(isActive = true, subscription, message)

      case "STOP" =>
        setActivity(isActive = false, subscription, message)

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

  def setActivity(isActive: Boolean, subscription: Option[Subscription], message: Message) = {
    subscription match {
      case Some(r) =>
        subscriptionDAO.update(r.setActivity(isActive)).toRight("")

      case None =>
        Left(s"Recipient with id '${message.recipient}' not found in database.")
    }
  }
}
