package services

import models.Message
import org.nibor.autolink.{LinkExtractor, LinkSpan, LinkType}
import utils.{Logger, UrlCache}

import java.util
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.CollectionConverters



/**
 * Serwis filtrujący wiadomości
 */
class FilterServiceImpl @Inject()(
                                      ws: WSService
                                    )
                                 (
                                      implicit val executionContext: ExecutionContext
                                    ) extends FilterService with Logger {

  /**
   * Odfiltrowuje niebezpieczne wiadomości
   *
   * @param msg wiadomość do sprawdzenia
   * @return wiadomość, jeżeli jest bezpieczna
   */
  def filter(msg: Message): Future[Option[Message]] = {
    val results = for {
      url <- extractUrls(msg)
    } yield {
      UrlCache.list.find(_ == url) match {
        case Some(url) =>
          Future.successful(None)
        case None =>
          ws.externalServiceRequest(url)
      }
    }

    Future.sequence(results).map { urls =>
      if (urls.exists(_.isEmpty)) {
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
  def extractUrls(msg: Message): List[String] = {
    val text = msg.message
    val linkExtractor = LinkExtractor.builder.linkTypes(util.EnumSet.of(LinkType.URL, LinkType.WWW, LinkType.EMAIL)).build
    CollectionConverters.IterableHasAsScala(linkExtractor.extractLinks(text)).asScala.toList.map(link => text.substring(link.getBeginIndex, link.getEndIndex))
  }
}
