import models.Message
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar.mock
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import services.{FilterServiceImpl, WSService}
import utils.UrlCache

import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class FilterServiceSpec @Inject()() extends PlaySpec with GuiceOneAppPerSuite {
  val wsService = mock[WSService]
  val message = Message(1, 2, "Some text")

  val filterService = new FilterServiceImpl(wsService)
  "A FilterService #filter" must {
    "returns Message becouse externalServiceRequest returned URL" in {
      val testUrl = "https://www.testurl.pl"
      val testMessage = Message(1, 2, testUrl)

      when(wsService.externalServiceRequest(testUrl)).thenReturn(Future.successful(Some(testUrl)))

      await(filterService.filter(testMessage)).isEmpty mustBe false
      await(filterService.filter(testMessage)).map(m => m mustBe testMessage)
    }

    "returns Message becouse there are no links inside" in {
      val text = "No links message"
      val testMessage = Message(1, 2, text)

      await(filterService.filter(testMessage)).isEmpty mustBe false
      await(filterService.filter(testMessage)).map(m => m mustBe testMessage)
    }


    "returns Message when all of extracted links returned Links" in {
      val firstLink = "www.test.pl"
      val secondLink = "www.google.pl"
      val text = s"First url : ${firstLink}, second url: ${secondLink}"
      val testMessage = Message(1, 2, text)

      when(wsService.externalServiceRequest(firstLink)).thenReturn(Future.successful(Some(firstLink)))
      when(wsService.externalServiceRequest(secondLink)).thenReturn(Future.successful(Some(secondLink)))

      await(filterService.filter(testMessage)).isEmpty mustBe false
    }

    "returns None becouse URL is already in cache" in {
      val testUrl = "https://www.testurl.pl"
      val testMessage = Message(1, 2, testUrl)

      UrlCache.list.addOne(testUrl)

      await(filterService.filter(testMessage)).isEmpty mustBe true
    }

    "returns None becouse externalServiceRequest returned None" in {
      val testUrl = "https://www.testurl.pl"
      val testMessage = Message(1, 2, testUrl)
      when(wsService.externalServiceRequest(testUrl)).thenReturn(Future.successful(None))

      await(filterService.filter(testMessage)).isEmpty mustBe true
    }

    "returns None becouse one of extracted links returned None" in {
      val unSafeLink = "www.test.pl"
      val safeLink = "www.google.pl"
      val text = s"First url : ${unSafeLink}, second url: ${safeLink}"
      val testMessage = Message(1, 2, text)

      when(wsService.externalServiceRequest(safeLink)).thenReturn(Future.successful(Some(safeLink)))
      UrlCache.list.addOne(unSafeLink)

      await(filterService.filter(testMessage)).isEmpty mustBe true
    }

  }

}
