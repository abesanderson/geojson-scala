package com.github.abesanderson.geojson

import cats.syntax.either._
import io.circe.parser.parse
import io.circe.{Json, Encoder, Decoder}
import java.io.File
import org.scalatest._
import org.scalatest.matchers._

trait JsonMatchers {
  private def parseOrNull(json: String) = parse(json) getOrElse Json.Null

  def encodeTo[T](right: Json)(implicit encoder: Encoder[T])     = new EncodeToMatcher[T](right)(encoder)
  def encodeTo[T](right: String)(implicit encoder: Encoder[T])   = new EncodeToMatcher[T](parseOrNull(right))(encoder)
  def decodeFrom[T](right: Json)(implicit decoder: Decoder[T])   = new DecodeFromMatcher[T](right)(decoder)
  def decodeFrom[T](right: String)(implicit decoder: Decoder[T]) = new DecodeFromMatcher[T](parseOrNull(right))(decoder)

  class EncodeToMatcher[T](json: Json)(implicit encoder: Encoder[T]) extends Matcher[T] {
    def apply(left: T) = {
      val encoded = encoder(left)

      val failureMessageSuffix        = "object " + left + " does not encode to " + json + ", was " + encoded
      val negatedFailureMessageSuffix = "object " + left + " does encode to " + json

      MatchResult(
        json.equals(encoded),
        "The " + failureMessageSuffix,
        "The " + negatedFailureMessageSuffix,
        "the " + failureMessageSuffix,
        "the " + negatedFailureMessageSuffix
      )
    }
  }

  class DecodeFromMatcher[T](json: Json)(implicit decoder: Decoder[T]) extends Matcher[T] {
    def apply(left: T) = {
      val decoded =
        json.as[T] match {
          case Left(ex)     => println(ex); null.asInstanceOf[T]
          case Right(value) => value
        }

      val failureMessageSuffix        = "object " + left + " does not decode from " + json + ", was " + decoded
      val negatedFailureMessageSuffix = "object " + left + " does decode from " + json

      MatchResult(
        decoded.equals(left),
        "The " + failureMessageSuffix,
        "The " + negatedFailureMessageSuffix,
        "the " + failureMessageSuffix,
        "the " + negatedFailureMessageSuffix
      )
    }
  }
}

object JsonMatchers extends JsonMatchers
