import dao.SubscriptionDAO
import models.{Message, Subscription}
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar.mock
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import services.SubscriberServiceImpl

import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global

class SubscriberServiceSpec @Inject()() extends PlaySpec with GuiceOneAppPerSuite {
  val subscriptionDAO = mock[SubscriptionDAO]
  val message = Message("1", "100", "START")
  val wrongMessage = Message("1", "100", "XXX")
  val wrongSenderIDMessage = Message("-1", "100", "START")
  val wrongSubscriptionManagerIdMessage = Message("1", "99", "STOP")
  val activeSubscription = new Subscription("1", true)
  val inActiveSubscription = new Subscription("1", false)

  val subscriberService = new SubscriberServiceImpl(subscriptionDAO)
  "A SubscriberService #update" must {
    "updates subscription to active" in {
      when(subscriptionDAO.update(activeSubscription)).thenReturn(Some(activeSubscription))
      when(subscriptionDAO.find("1")).thenReturn(Some(activeSubscription))

      await(subscriberService.update(message)) mustBe Right(activeSubscription)
    }

    "updates subscription to inactive" in {
      when(subscriptionDAO.update(inActiveSubscription)).thenReturn(Some(inActiveSubscription))
      when(subscriptionDAO.find("1")).thenReturn(Some(inActiveSubscription))

      await(subscriberService.update(message)) mustBe Right(inActiveSubscription)
    }

    "returns error becouse message is other than START or STOP" in {
      await(subscriberService.update(wrongMessage)) mustBe Left("Text of the message should be equal to 'START' or 'STOP'.")
    }

    "returns error becouse recipient ID is different that SubscriberManager's ID" in {
      await(subscriberService.update(wrongSubscriptionManagerIdMessage)) mustBe Left("Recipient is not a SubscriptionManager.")
    }

    "returns error becouse there is no subscription with such sender ID" in {
      when(subscriptionDAO.find("-1")).thenReturn(None)
      await(subscriberService.update(wrongSenderIDMessage)) mustBe Left("Sender with id '-1' not found in database.")
    }
  }

}
