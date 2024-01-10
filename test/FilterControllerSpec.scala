import akka.stream.Materializer
import controllers.FilterController
import models.{Message, Subscription}
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar.mock
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import org.scalatestplus.play.{PlaySpec, PortNumber}
import play.api.libs.json.{JsNull, JsValue, Json}
import play.api.libs.ws.WSClient
import play.api.mvc.Headers
import play.api.test.Helpers.baseApplicationBuilder.injector
import play.api.test.Helpers.{CONTENT_TYPE, POST, await, contentAsJson, defaultAwaitTimeout}
import play.api.test.{FakeRequest, Helpers}
import services.{FilterService, SubscriberService}

import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class FilterControllerSpec @Inject()() extends PlaySpec with GuiceOneAppPerSuite {
  private implicit val portNumber = PortNumber(9000)
  implicit lazy val materializer: Materializer = app.materializer

  val mockMessageFilterService = mock[FilterService]
  val mockSubscriberService = mock[SubscriberService]
  val message = Message(1, 2, "Some text")

  val controller = new FilterController(Helpers.stubControllerComponents(), mockMessageFilterService, mockSubscriberService)
  val requestBody = Json.toJson(message)
  val req = FakeRequest[JsValue](POST, "/messages", Headers((CONTENT_TYPE, "application/json")), body = requestBody)
  "A FilterController #filter" must {
    "returns safe message" in {
      when(mockSubscriberService.update(message)).thenReturn(Future.successful(Left("Test error")))
      when(mockMessageFilterService.filter(message)).thenReturn(Future.successful(Some(message)))

      val result = contentAsJson(Helpers.call(controller.filter(), req))
      val subcriber = (result \ "message").getOrElse(JsNull)

      subcriber mustBe requestBody
    }

    "filters out dangerous message" in {
      when(mockSubscriberService.update(message)).thenReturn(Future.successful(Left("Test error")))
      when(mockMessageFilterService.filter(message)).thenReturn(Future.successful(None))

      val result = await(Helpers.call(controller.filter(), req))

      result.header.status mustBe 204
    }

    "activates subscription" in {
      val exampleSubscription = new Subscription(1L, true)
      when(mockSubscriberService.update(message)).thenReturn(Future.successful(Right(exampleSubscription)))

      val result = contentAsJson(Helpers.call(controller.filter(), req))
      val subscriptionResult = (result \ "subscription").getOrElse(JsNull)

      subscriptionResult mustBe Subscription.toJson(exampleSubscription)
    }

    "stops subscription" in {
      val exampleSubscription = new Subscription(1L, false)
      when(mockSubscriberService.update(message)).thenReturn(Future.successful(Right(exampleSubscription)))

      val result = contentAsJson(Helpers.call(controller.filter(), req))
      val subscriptionResult = (result \ "subscription").getOrElse(JsNull)
      subscriptionResult mustBe Subscription.toJson(exampleSubscription)
    }
  }

}
