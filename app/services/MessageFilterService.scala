package services

import models.{Message, PhishingDetectorResponse}
import org.nibor.autolink.{LinkExtractor, LinkSpan, LinkType}
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import utils.Logger

import java.util
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.CollectionConverters


/**
 * Serwis filtrujący wiadomości
 */
class MessageFilterService @Inject()(
                                      ws: WSClient
                                    )
                                    (
                                      implicit val executionContext: ExecutionContext
                                    ) extends Logger {

  /**
   * Odfiltrowuje niebezpieczne wiadomości
   *
   * @param msg wiadomość do sprawdzenia
   * @return wiadomość, jeżeli jest bezpieczna
   */
  def filter(msg: Message): Future[Option[Message]] = {
    val text = msg.message
    val results = for {
      link <- extractLinks(msg)
    } yield {
      val url = text.substring(link.getBeginIndex, link.getEndIndex)
      externalServiceRequest(url)
    }

    Future.sequence(results).map { urls =>
      if (urls.forall(_.isEmpty)) {
        None
      } else {
        Some(msg)
      }
    }
  }

  /**
   * Wyciąga linki z danej wiadomości
   *
   * @param msg wiadomość
   * @return lista linków
   */
  def extractLinks(msg: Message): List[LinkSpan] = {
    val text = msg.message
    val linkExtractor = LinkExtractor.builder.linkTypes(util.EnumSet.of(LinkType.URL, LinkType.WWW, LinkType.EMAIL)).build
    CollectionConverters.IterableHasAsScala(linkExtractor.extractLinks(text)).asScala.toList
  }

  def externalServiceRequest(url: String): Future[Option[String]] = {
    executeRequest(url).map {
      case Some(response) =>
        // w przypadku poprawnej odpowiedzi zewnętrznego serwisu sprawdzam, czy url jest bezpieczny i zwracam go, jeżeli jest
        if (response.isSafe) {
          Some(url)
        } else {
          None
        }

      case None =>
        // coś poszło nie tak, w zależności od decyzji klienta możemy zrobić różne podejścia
        // albo z góry zakładamy, że sprawdzany URL jest błędny w przypadku, gdy odpowiedź z zewnętrznego serwera zwróciła failure - ryzyko utraty ważnych dla odbiorcy informacji, ale większe bezpieczeństwo
        // albo ponawiamy request za jakiś czas, maxymalnie określoną ilość razy i ewentualnie wtedy przy braku odpowiedzi podejmujemy decyzję
        // albo z góry zakładamy, że sprawdzany URL jest poprawny, gdy odpowiedź z zewnętrznego serwera zwróciła failure - wiadomość dotrze, nie ma ryzyka utraty ważnych dla odbiorcy informacji
        logger.error(s"Nie udało się sparsować odpowiedzi z zewnętrznego serwisu. Zakładam, że url '$url' jest bezpieczny.")
        Some(url) // zakładam, że w takim wypadku jest bezpieczny
    }
  }

  /**
   * Wysyła request do zewnętrznego serwisu
   *
   * @param url url do sprawdzenia
   * @return odpowiedź
   */
  private def executeRequest(url: String): Future[Option[PhishingDetectorResponse]] = {
    val uri = Json.toJson("uri" -> url)
    val host = "web-risk-api-host.com"
    val contentTypeHeader = ("Content-Type", "application/json")
    val authHeader = ("Authorization", "Bearer test-1234567890")
    val hostHeader = ("Host", host)

    ws
      .url(s"$host/v1/$url")
      .withHttpHeaders(contentTypeHeader, authHeader, hostHeader)
      .post(uri).map { response =>
      // w przypadku poprawnej odpowiedzi zewnętrznego serwisu sprawdzam, czy url jest bezpieczny i zwracam tą wiadomość
      Json.fromJson[PhishingDetectorResponse](response.json).asOpt
    }
  }
}
