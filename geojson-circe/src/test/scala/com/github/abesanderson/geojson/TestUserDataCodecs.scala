package com.github.abesanderson.geojson

import io.circe._
import io.circe.syntax._
import io.circe.parser._
import cats.syntax.either._

case class Foo(
  prop0: Option[String]              = None,
  prop1: Option[Either[Double, Bar]] = None)
    extends UserData
case class Bar(`this`: String)

object TestUserDataCodecs {
  implicit val encodeFoo: Encoder[Foo] =
    Encoder.instance(d =>
      Json.obj("prop0" -> d.prop0.asJson,
               "prop1" -> d.prop1.asJson)
    )
  implicit val decodeFoo: Decoder[Foo] =
    new Decoder[Foo] {
      final def apply(c: HCursor): Decoder.Result[Foo] =
        for {
          prop0 <- c.downField("prop0").as[Option[String]]
          prop1 <- c.downField("prop1").as[Option[Either[Double, Bar]]]
        } yield {
          Foo(prop0, prop1)
        }
    }

  implicit val encodeBar: Encoder[Bar] = Encoder.instance(d => Json.obj("this" -> d.`this`.asJson))
  implicit val decodeBar: Decoder[Bar] = Decoder[String].prepare(_.downField("this")).map(Bar(_))

  implicit val encodeProp1: Encoder[Either[Double,Bar]] = 
    Encoder.instance[Either[Double,Bar]](_.fold(_.asJson, _.asJson))
  implicit val decodeProp1: Decoder[Either[Double,Bar]] =
    Decoder[Double].map[Either[Double,Bar]](Left(_)) or Decoder[Bar].map[Either[Double,Bar]](Right(_))
}
