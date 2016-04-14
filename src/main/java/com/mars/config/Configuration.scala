package com.mars.config

/**
  * Created by Lital on 4/13/2016.
  */
/**
  * Created by Lital on 4/13/2016.
  */

import com.typesafe.config.ConfigFactory

import scala.util.Try

/**
  * Holds service configuration settings.
  */
trait Configuration {

  /**
    * Application config object.
    */
  val config = ConfigFactory.load()

  /** Host name/address to start service on. */
  lazy val serviceHost = Try(config.getString("service.host")).getOrElse("localhost")

  /** Port to start service on. */
  lazy val servicePort = Try(config.getInt("service.port")).getOrElse(8080)

}
