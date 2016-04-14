/**
  * Created by Lital on 4/13/2016.
  */
package com.sysgears.example.rest

import java.text.{ParseException, SimpleDateFormat}
import java.util.Date

import akka.actor.Actor
import akka.event.slf4j.SLF4JLogging
import com.mars.dao.RtbServiceServiceDAO
import com.mars.domain._
import net.liftweb.json.Serialization._
import net.liftweb.json.{DateFormat, Formats}
import spray.http._
import spray.httpx.unmarshalling._
import spray.routing._

/**
  * REST Service actor.
  */
class RestServiceActor extends Actor with RestService with SLF4JLogging {

  implicit def actorRefFactory = context

  def receive = runRoute(rest)
}

/**
  * REST Service
  */
trait RestService extends HttpService with SLF4JLogging {

  val rtbServiceService = new RtbServiceServiceDAO

  implicit val executionContext = actorRefFactory.dispatcher

  implicit val liftJsonFormats = new Formats {
    val dateFormat = new DateFormat {
      val sdf = new SimpleDateFormat("yyyy-MM-dd")

      def parse(s: String): Option[Date] = try {
        Some(sdf.parse(s))
      } catch {
        case e: Exception => None
      }

      def format(d: Date): String = sdf.format(d)
    }
  }

  implicit val string2Date = new FromStringDeserializer[Date] {
    def apply(value: String) = {
      val sdf = new SimpleDateFormat("yyyy-MM-dd")
      try Right(sdf.parse(value))
      catch {
        case e: ParseException => {
          Left(MalformedContent("'%s' is not a valid Date value" format (value), e))
        }
      }
    }
  }

  implicit val customRejectionHandler = RejectionHandler {
    case rejections => mapHttpResponse {
      response =>
        response.withEntity(HttpEntity(ContentType(MediaTypes.`application/json`),
          write(Map("error" -> response.entity.asString))))
    } {
      RejectionHandler.Default(rejections)
    }
  }

  val rest = respondWithMediaType(MediaTypes.`application/json`) {
    path("rtb") {
      post {
        entity(Unmarshaller(MediaTypes.`application/json`) {
          case httpEntity: HttpEntity =>

            read[RtbRequest](httpEntity.asString(HttpCharsets.`UTF-8`))
        }) {
          rtbRequest: RtbRequest =>
            ctx: RequestContext =>
              handleRequest(ctx, StatusCodes.Created) {
                log.debug("Got request id [%s] user agent [%s]".format( rtbRequest.id, rtbRequest.device.ua ))
                rtbServiceService.processRtbRequest( rtbRequest )
              }
        }
      }
    }
  }

  /**
    * Handles an incoming request and create valid response for it.
    *
    * @param ctx         request context
    * @param successCode HTTP Status code for success
    * @param action      action to perform
    */
  protected def handleRequest(ctx: RequestContext, successCode: StatusCode = StatusCodes.OK)(action: => Either[Failure, _]) {

    ctx.request.headers.foreach( header => {
      println( header.toString() )
    })
    action match {
      case Right(result: Object) =>
        ctx.complete(successCode, write(result))
      case Left(error: Failure) =>
        ctx.complete(error.getStatusCode, net.liftweb.json.Serialization.write(Map("error" -> error.message)))
      case _ =>
        ctx.complete(StatusCodes.InternalServerError)
    }
  }
}
