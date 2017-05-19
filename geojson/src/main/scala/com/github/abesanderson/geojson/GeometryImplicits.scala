package com.github.abesanderson.geojson

object GeometryImplicits {
  implicit def pointToPosition(point: Point)                              = point.coordinates
  implicit def lineToPositions(line: LineString)                          = line.coordinates
  implicit def polygonToPositions(polygon: Polygon)                       = polygon.coordinates

  implicit def positionToPoint(position: Position)                        = Point(position)
  implicit def positionsToLine(positions: List[Position])                 = LineString(positions)
  implicit def positionsToPolygon(positions: List[List[Position]])        = Polygon(positions)

  implicit def positionsToPoints(positions: List[Position])               = positions.map(Point(_))
  implicit def positionsToLines(positions: List[List[Position]])          = positions.map(LineString(_))
  implicit def positionsToPolygons(positions: List[List[List[Position]]]) = positions.map(Polygon(_))
}
