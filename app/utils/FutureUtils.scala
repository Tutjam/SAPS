package utils

import scala.concurrent.{ExecutionContext, Future}

object FutureUtils {
  def flatten[B](x: Option[Future[B]])(implicit ec: ExecutionContext): Future[Option[B]] =
    x match {
      case Some(f) => f.map(Some(_))
      case None => Future.successful(None)
    }
  def flattenOption[B](x: Option[Future[Option[B]]])(implicit ec: ExecutionContext): Future[Option[B]] = flatten(x).map(_.flatten)
}
