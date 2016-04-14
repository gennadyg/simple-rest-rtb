package com.mars.dao

/**
  * Created by Lital on 4/13/2016.
  */

import akka.event.slf4j.SLF4JLogging
import com.mars.config.Configuration
import com.mars.domain._
import net.liftweb.json.DefaultFormats
import net.liftweb.json.Serialization.write

/**
  * Provides DAL for Customer entities for MySQL database.
  */
class RtbServiceServiceDAO extends Configuration with SLF4JLogging{
  /**
    * Reseives RtbRequest and printing it.
    *
    * @param rtbRequest instance of type RtbRequest
    *
    * @return parsed RTB request
    */
  def processRtbRequest( rtbRequest: RtbRequest): Either[Failure, RtbRequest] = {

    try {

      implicit val formats = DefaultFormats
      val jsonString = write( rtbRequest )
      log.debug( jsonString )
      Right( rtbRequest )

    } catch {
      case e: Exception =>
        Left( serviceError(e) )
    }

  }

  /**
    * Produce database error description.
    *
    * @param e SQL Exception
    * @return database error description
    */
  protected def serviceError(e: Exception) =
    Failure("%s".format( e.getMessage), FailureType.InternalError)

  /**
    * Produce customer not found error description.
    *
    * @param customerId id of the customer
    * @return not found error description
    */
  protected def notFoundError(customerId: Long) =
    Failure("Customer with id=%d does not exist".format(customerId), FailureType.NotFound)
}
