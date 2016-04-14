package com.mars.domain

/**
  * Created by Gennady on 4/13/2016.
  */


case class User( id: String )
case class Geo( country: String, city: String )
case class Device( dnt: Integer, connectiontype: Integer, carrier: String, dpidsha1: String, dpidmd5: String, didmd5: String, didsha1: String, ifa: String, osv: String, os: String, ua: String, ip: String, devicetype: Integer, geo: Geo )
case class Publisher( id: String )
case class App( id: String, name: String, bundle: String, cat: List[String], publisher: Publisher)
case class Banner( w: Integer, h: Integer, battr: List[Integer], api: List[Object])
case class Imp(id: String, banner: Banner, bidfloor: Double )
case class RtbRequest(id: String, tmax: Long, at: Integer, bcat: List[String], imp: List[Imp], app: App, device: Device, user: User, cur: List[String] )
