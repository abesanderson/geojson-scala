package com.github.abesanderson.geojson

case class Position(longitude: Double, latitude: Double, altitude: Option[Double] = None)
// todo: LinearRing?

sealed trait GeoJsonObject
trait UserData

// todo: BoundingBox
final case class FeatureCollection[T <: UserData](
  features: List[Feature[T]]) 
    extends GeoJsonObject
final case class Feature[T <: UserData](
  geometry: Geometry, 
  properties: Option[T] = None) 
    extends GeoJsonObject

sealed abstract class Geometry extends GeoJsonObject
final case class Point(coordinates: Position)                   extends Geometry
final case class LineString(coordinates: List[Position])        extends Geometry
final case class Polygon(coordinates: List[List[Position]])     extends Geometry
final case class MultiPoint(points: List[Point])                extends Geometry
final case class MultiLineString(lines: List[LineString])       extends Geometry 
final case class MultiPolygon(polygons: List[Polygon])          extends Geometry
final case class GeometryCollection(geometries: List[Geometry]) extends Geometry
