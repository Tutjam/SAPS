package services

import scala.concurrent.Future


/**
 * Serwis filtrujący wiadomości
 */
trait WSService {

  /**
   * Sprawdza bezpieczeństwo przekazanego urla
   *
   * @param url url do sprawdzenia
   * @return url, jeżeli bezpieczny
   */
  def externalServiceRequest(url: String): Future[Option[String]]
}
