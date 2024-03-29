import com.google.inject.AbstractModule
import dao.{SubscriptionDAO, SubscriptionDAOImpl}

import java.time.Clock
import services.{ApplicationTimer, AtomicCounter, Counter, FilterService, FilterServiceImpl,SubscriberService, SubscriberServiceImpl, WSService, WSServiceImpl}

/**
 * This class is a Guice module that tells Guice how to bind several
 * different types. This Guice module is created when the Play
 * application starts.

 * Play will automatically use any class called `Module` that is in
 * the root package. You can create modules in other locations by
 * adding `play.modules.enabled` settings to the `application.conf`
 * configuration file.
 */
class Module extends AbstractModule {

  override def configure() = {
    // Use the system clock as the default implementation of Clock
    bind(classOf[Clock]).toInstance(Clock.systemDefaultZone)
    // Ask Guice to create an instance of ApplicationTimer when the
    // application starts.
    bind(classOf[ApplicationTimer]).asEagerSingleton()
    // Set AtomicCounter as the implementation for Counter.
    bind(classOf[Counter]).to(classOf[AtomicCounter])

    bind(classOf[SubscriptionDAO]).to(classOf[SubscriptionDAOImpl])
    bind(classOf[SubscriberService]).to(classOf[SubscriberServiceImpl])
    bind(classOf[FilterService]).to(classOf[FilterServiceImpl])
    bind(classOf[WSService]).to(classOf[WSServiceImpl])
  }

}
