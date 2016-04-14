/**
  * Created by Lital on 4/13/2016.
  */

package com.sysgears.example.boot

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import com.mars.config.Configuration
import com.sysgears.example.rest.RestServiceActor
import spray.can.Http


// Main class to run

object Boot extends App with Configuration {

  // create an actor system for application
  implicit val system = ActorSystem("rest-service-example")

  // create and start rest service actor
  val restService = system.actorOf(Props[RestServiceActor], "rest-endpoint")

  // start HTTP server with rest service actor as a handler
  IO(Http) ! Http.Bind(restService, serviceHost, servicePort)
}

