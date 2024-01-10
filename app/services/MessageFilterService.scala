package services

import models.Message


import scala.concurrent.Future


/**
 * Serwis filtrujący wiadomości
 */
trait MessageFilterService {

  /**
   * Odfiltrowuje niebezpieczne wiadomości
   *
   * @param msg wiadomość do sprawdzenia
   * @return wiadomość, jeżeli jest bezpieczna
   */
  def filter(msg: Message): Future[Option[Message]]

  /**
   * Wyciąga linki z danej wiadomości
   *
   * @param msg wiadomość
   * @return lista linków
   */
  def extractUrls(msg: Message): List[String]
}
