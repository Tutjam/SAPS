package services

import scala.concurrent.Future


/**
 * Serwis filtrujący wiadomości
 */
trait WSService {

  /**
   * Wykonuje request do zewnętrznego serwisu
   * @param data dane do przesłania
   * @return
   */
  def externalServiceRequest(data: String): Future[Option[String]]
}
