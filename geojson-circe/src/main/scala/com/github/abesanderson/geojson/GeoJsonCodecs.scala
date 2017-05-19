package com.github.abesanderson.geojson

import io.circe._
import io.circe.generic._
import io.circe.generic.semiauto._
import io.circe.syntax._
import cats.syntax.either._
import com.github.abesanderson.geojson.GeometryImplicits._

object GeoJsonCodecs {
  implicit val encodePosition = 
    Encoder.instance[Position] {
      case Position(lng, lat, None)      => (lng, lat).asJson
      case Position(lng, lat, Some(alt)) => (lng, lat, alt).asJson
    }

  private val decodePosition2 = Decoder[(Double,Double)].map[Position](c => Position(c._1, c._2))
  private val decodePosition3 = Decoder[(Double, Double,Double)].map[Position](c => Position(c._1, c._2, Some(c._3)))
  implicit val decodePosition = decodePosition2 or decodePosition3

  implicit val encodePoint      = 
    Encoder.instance[Point](d =>
      Json.obj("type"        -> Json.fromString("Point"),
               "coordinates" -> d.coordinates.asJson)
    )
  implicit val encodeLineString = 
    Encoder.instance[LineString](d =>
      Json.obj("type"        -> Json.fromString("LineString"),
               "coordinates" -> d.coordinates.asJson)
    )
  implicit val encodePolygon    = 
    Encoder.instance[Polygon](d =>
      Json.obj("type"        -> Json.fromString("Polygon"),
               "coordinates" -> d.coordinates.asJson)
    )

  implicit val encodeMultiPoint =
    Encoder.instance[MultiPoint](d =>
      Json.obj("type"        -> Json.fromString("MultiPoint"),
               "coordinates" -> d.points.map(pointToPosition(_)).asJson)
    )
  implicit val encodeMultiLineString =
    Encoder.instance[MultiLineString](d =>
      Json.obj("type"        -> Json.fromString("MultiLineString"),
               "coordinates" -> d.lines.map(lineToPositions(_)).asJson)
    )
  implicit val encodeMultiPolygon =
    Encoder.instance[MultiPolygon](d =>
      Json.obj("type"        -> Json.fromString("MultiPolygon"),
               "coordinates" -> d.polygons.map(polygonToPositions(_)).asJson)
    )
  
  implicit val decodePoint              = Decoder[Position].prepare(_.downField("coordinates")).map(Point(_))
  implicit val decodeLineString         = Decoder[List[Position]].prepare(_.downField("coordinates")).map(LineString(_))
  implicit val decodePolygon            = Decoder[List[List[Position]]].prepare(_.downField("coordinates")).map(Polygon(_))
  implicit val decodeMultiPoint         = Decoder[List[Position]].prepare(_.downField("coordinates")).map(MultiPoint(_))
  implicit val decodeMultiLineString    = Decoder[List[List[Position]]].prepare(_.downField("coordinates")).map(MultiLineString(_))
  implicit val decodeMultiPolygon       = Decoder[List[List[List[Position]]]].prepare(_.downField("coordinates")).map(MultiPolygon(_))

  implicit val encodeGeometryCollection: Encoder[GeometryCollection] =
    Encoder.instance[GeometryCollection](d =>
      Json.obj("type"        -> Json.fromString("GeometryCollection"),
               "geometries"  -> d.geometries.asJson)
    )
  implicit val decodeGeometryCollection: Decoder[GeometryCollection] =
    Decoder[List[Geometry]].prepare(_.downField("geometries")).map(GeometryCollection(_))

  implicit lazy val encodeGeometry =
    Encoder.instance[Geometry] {
      case d: Point              => d.asJson
      case d: LineString         => d.asJson
      case d: Polygon            => d.asJson
      case d: MultiPoint         => d.asJson
      case d: MultiLineString    => d.asJson
      case d: MultiPolygon       => d.asJson
      case d: GeometryCollection => d.asJson
    }
  implicit lazy val decodeGeometry =
    Decoder.instance[Geometry](c =>
      c.downField("type").as[String].flatMap {
        case "Point"              => c.as[Point]
        case "LineString"         => c.as[LineString]
        case "Polygon"            => c.as[Polygon]
        case "MultiPoint"         => c.as[MultiPoint]
        case "MultiLineString"    => c.as[MultiLineString]
        case "MultiPolygon"       => c.as[MultiPolygon]
        case "GeometryCollection" => c.as[GeometryCollection]
      }
    )

  implicit def encodeFeature[T <: UserData](implicit encoder: Encoder[T]): Encoder[Feature[T]] =
    Encoder.instance(d =>
      Json.obj("type"       -> Json.fromString("Feature"),
               "geometry"   -> d.geometry.asJson,
               "properties" -> d.properties.asJson)
    )
  implicit def decodeFeature[T <: UserData](implicit decoder: Decoder[T]): Decoder[Feature[T]] =
    new Decoder[Feature[T]] {
      final def apply(c: HCursor): Decoder.Result[Feature[T]] =
        for {
          geometry   <- c.downField("geometry").as[Geometry]
          properties <- c.downField("properties").as[Option[T]]
        } yield {
          Feature(geometry, properties)
        }
    }

  implicit def encodeFeatureCollection[T <: UserData](implicit encoder: Encoder[T]): Encoder[FeatureCollection[T]] =
    Encoder.instance[FeatureCollection[T]](d =>
      Json.obj("type"     -> Json.fromString("FeatureCollection"),
               "features" -> d.features.asJson)
    )
  implicit def decodeFeatureCollection[T <: UserData](implicit decoder: Decoder[T]): Decoder[FeatureCollection[T]] =
    Decoder[List[Feature[T]]].prepare(_.downField("features")).map(FeatureCollection[T](_))
}
