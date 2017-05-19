package com.github.abesanderson.geojson

import org.scalatest._
import io.circe._
import io.circe.syntax._
import io.circe.parser._
import com.github.abesanderson.geojson.GeoJsonCodecs._
import com.github.abesanderson.geojson.TestUserDataCodecs._
import com.github.abesanderson.geojson.GeometryImplicits._

class GeometrySpec extends WordSpecLike
    with Matchers
    with JsonMatchers
{
  "Position" should {
    "encode correctly" in {
      Position(0.0, 0.0)            should encodeTo[Position]("""[0.0, 0.0]""")
      Position(0.0, 0.0, Some(0.0)) should encodeTo[Position]("""[0.0, 0.0, 0.0]""")
    }

    "decode correctly" in {
      Position(0.0, 0.0)            should decodeFrom[Position]("""[0.0, 0.0]""")
      Position(0.0, 0.0, Some(0.0)) should decodeFrom[Position]("""[0.0, 0.0, 0.0]""")
    }
  }

  val point1   = Point(Position(100.0, 0.0))
  val point2   = Point(Position(101.0, 1.0))
  val line1    = LineString(List(Position(100.0, 0.0), Position(101.0, 1.0)))
  val line2    = LineString(List(Position(102.0, 2.0), Position(103.0, 3.0)))
  val line3    = LineString(List(Position(101.0, 0.0), Position(102.0, 1.0)))

  val polygon1 =
    Polygon(
      List(
        List(
          Position(100.0, 0.0),
          Position(101.0, 0.0),
          Position(101.0, 1.0),
          Position(100.0, 1.0),
          Position(100.0, 0.0)
        )
      )
    )
  val polygonWithHole1 =
    Polygon(
      List(
        List(
          Position(100.0, 0.0),
          Position(101.0, 0.0),
          Position(101.0, 1.0),
          Position(100.0, 1.0),
          Position(100.0, 0.0)
        ),
        List(
          Position(100.8, 0.8),
          Position(100.8, 0.2),
          Position(100.2, 0.2),
          Position(100.2, 0.8),
          Position(100.8, 0.8)
        )
      )
    )

  val polygon2 =
    Polygon(
      List(
        List(
          Position(102.0,2.0),
          Position(103.0,2.0),
          Position(103.0,3.0),
          Position(102.0,3.0),
          Position(102.0,2.0))
      )
    )
  val polygonWithHole2 =
    Polygon(
      List(
        List(
          Position(100.0, 0.0),
          Position(101.0, 0.0),
          Position(101.0, 1.0),
          Position(100.0, 1.0),
          Position(100.0, 0.0)
        ),
        List(
          Position(100.2, 0.2),
          Position(100.2, 0.8),
          Position(100.8, 0.8),
          Position(100.8, 0.2),
          Position(100.2, 0.2)
        )
      )
    )

  "Point" should {
    val json =
      """{
        |  "type": "Point",
        |  "coordinates": [100.0, 0.0]
        |}""".stripMargin

    "encode correctly" in { point1 should encodeTo[Point](json) }
    "decode correctly" in { point1 should decodeFrom[Point](json) }
  }

  "LineString" should {
    val json =
      """{
        |  "type": "LineString",
        |  "coordinates": [
        |      [100.0, 0.0],
        |      [101.0, 1.0]
        |  ]
        |}""".stripMargin

    "encode correctly" in { line1 should encodeTo[LineString](json) }
    "decode correctly" in { line1 should decodeFrom[LineString](json) }
  }

  "Polygon" should {
    val json1 =
      """{
        |  "type": "Polygon",
        |  "coordinates": [
        |    [
        |      [100.0, 0.0],
        |      [101.0, 0.0],
        |      [101.0, 1.0],
        |      [100.0, 1.0],
        |      [100.0, 0.0]
        |    ]
        |  ]
        |}""".stripMargin

    val json2 =
      """{
        |  "type": "Polygon",
        |  "coordinates": [
        |    [
        |      [100.0, 0.0],
        |      [101.0, 0.0],
        |      [101.0, 1.0],
        |      [100.0, 1.0],
        |      [100.0, 0.0]
        |    ],
        |    [
        |      [100.8, 0.8],
        |      [100.8, 0.2],
        |      [100.2, 0.2],
        |      [100.2, 0.8],
        |      [100.8, 0.8]
        |    ]
        |  ]
        |}""".stripMargin

    "encode correctly" in {
      polygon1         should encodeTo[Polygon](json1)
      polygonWithHole1 should encodeTo[Polygon](json2)
    }
    "decode correctly" in {
      polygon1         should decodeFrom[Polygon](json1)
      polygonWithHole1 should decodeFrom[Polygon](json2)
    }
  }

  val multiPoint   = MultiPoint(List(point1, point2))
  val multiLine    = MultiLineString(List(line1, line2))
  val multiPolygon = MultiPolygon(List(polygon2, polygonWithHole2))
  val geometries   = GeometryCollection(List(point1, line3))

  "MultiPoint" should {
    val json =
      """{
        |  "type": "MultiPoint",
        |  "coordinates": [
        |    [100.0, 0.0],
        |    [101.0, 1.0]
        |  ]
        |}""".stripMargin

    "encode correctly" in { multiPoint should encodeTo[MultiPoint](json) }
    "decode correctly" in { multiPoint should decodeFrom[MultiPoint](json) }
  }

  "MultiLineString" should {
    val json =
      """{
        |  "type": "MultiLineString",
        |  "coordinates": [
        |    [
        |      [100.0, 0.0],
        |      [101.0, 1.0]
        |    ],
        |    [
        |      [102.0, 2.0],
        |      [103.0, 3.0]
        |    ]
        |  ]
        |}""".stripMargin

    "encode correctly" in { multiLine should encodeTo[MultiLineString](json) }
    "decode correctly" in { multiLine should decodeFrom[MultiLineString](json) }
  }

  "MultiPolygon" should {
    val json =
      """{
        |  "type": "MultiPolygon",
        |  "coordinates": [
        |    [
        |      [
        |        [102.0, 2.0],
        |        [103.0, 2.0],
        |        [103.0, 3.0],
        |        [102.0, 3.0],
        |        [102.0, 2.0]
        |      ]
        |    ],
        |    [
        |      [
        |        [100.0, 0.0],
        |        [101.0, 0.0],
        |        [101.0, 1.0],
        |        [100.0, 1.0],
        |        [100.0, 0.0]
        |      ],
        |      [
        |        [100.2, 0.2],
        |        [100.2, 0.8],
        |        [100.8, 0.8],
        |        [100.8, 0.2],
        |        [100.2, 0.2]
        |      ]
        |    ]
        |  ]
        |}""".stripMargin

    "encode correctly" in { multiPolygon should encodeTo[MultiPolygon](json) }
    "decode correctly" in { multiPolygon should decodeFrom[MultiPolygon](json) }
  } 

  "GeometryCollection" should {
    val json =
      """{
        |  "type": "GeometryCollection",
        |  "geometries": [{
        |    "type": "Point",
        |    "coordinates": [100.0, 0.0]
        |  }, {
        |    "type": "LineString",
        |    "coordinates": [
        |      [101.0, 0.0],
        |      [102.0, 1.0]
        |    ]
        |  }]
        |}""".stripMargin

    "encode correctly" in { geometries should encodeTo[GeometryCollection](json) }
    "decode correctly" in { geometries should decodeFrom[GeometryCollection](json) }
  }

  "Geometry" should {
    val json1 =
      """{
        |  "type": "Point",
        |  "coordinates": [100.0, 0.0]
        |}""".stripMargin
    val json2 =
      """{
        |  "type": "LineString",
        |  "coordinates": [
        |      [100.0, 0.0],
        |      [101.0, 1.0]
        |  ]
        |}""".stripMargin
    val json3 =
      """{
        |  "type": "Polygon",
        |  "coordinates": [
        |    [
        |      [100.0, 0.0],
        |      [101.0, 0.0],
        |      [101.0, 1.0],
        |      [100.0, 1.0],
        |      [100.0, 0.0]
        |    ]
        |  ]
        |}""".stripMargin
    val json4 =
      """{
        |  "type": "MultiPoint",
        |  "coordinates": [
        |    [100.0, 0.0],
        |    [101.0, 1.0]
        |  ]
        |}""".stripMargin
    val json5 =
      """{
        |  "type": "MultiLineString",
        |  "coordinates": [
        |    [
        |      [100.0, 0.0],
        |      [101.0, 1.0]
        |    ],
        |    [
        |      [102.0, 2.0],
        |      [103.0, 3.0]
        |    ]
        |  ]
        |}""".stripMargin
    val json6 =
      """{
        |  "type": "GeometryCollection",
        |  "geometries": [{
        |    "type": "Point",
        |    "coordinates": [100.0, 0.0]
        |  }, {
        |    "type": "LineString",
        |    "coordinates": [
        |      [101.0, 0.0],
        |      [102.0, 1.0]
        |    ]
        |  }]
        |}""".stripMargin

    "encode correctly" in { 
      point1     should encodeTo[Geometry](json1) 
      line1      should encodeTo[Geometry](json2)
      polygon1   should encodeTo[Geometry](json3)
      multiPoint should encodeTo[Geometry](json4)
      multiLine  should encodeTo[Geometry](json5)
      geometries should encodeTo[Geometry](json6)
    }
    "decode correctly" in { 
      point1     should decodeFrom[Geometry](json1) 
      line1      should decodeFrom[Geometry](json2)
      polygon1   should decodeFrom[Geometry](json3)
      multiPoint should decodeFrom[Geometry](json4)
      multiLine  should decodeFrom[Geometry](json5)
      geometries should decodeFrom[Geometry](json6)
    }
  }

  "Feature" should {
    val props   = Foo(prop0 = Some("value0"))
    val point   = Point(Position(102.0, 0.5))
    val feature = Feature[Foo](point, Some(props))

    val json =
      """{
        |  "type": "Feature",
        |  "geometry": {
        |    "type": "Point",
        |    "coordinates": [102.0, 0.5]
        |  },
        |  "properties": {
        |    "prop0": "value0",
        |    "prop1": null
        |  }
        |}""".stripMargin

    "encode correctly" in { feature should encodeTo[Feature[Foo]](json) }
    "decode correctly" in { feature should decodeFrom[Feature[Foo]](json) }
  }

  "FeatureCollection" should {
    val point   = Point(Position(102.0, 0.5))
    val line    =
      LineString(
        List(
          Position(102.0, 0.0),
          Position(103.0, 1.0),
          Position(104.0, 0.0),
          Position(105.0, 1.0)
        )
      )
    val polygon =
      Polygon(
        List(
          List(
            Position(100.0, 0.0),
            Position(101.0, 0.0),
            Position(101.0, 1.0),
            Position(100.0, 1.0),
            Position(100.0, 0.0)
          )
        )
      )

    val props1 = Foo(Some("value0"))
    val props2 = Foo(Some("value0"), Some(Left(0.0)))
    val props3 = Foo(Some("value0"), Some(Right(Bar("that"))))

    val feature1 = Feature[Foo](point, Some(props1))
    val feature2 = Feature[Foo](line, Some(props2))
    val feature3 = Feature[Foo](polygon, Some(props3))
    val features = FeatureCollection[Foo](List(feature1, feature2, feature3))

    val json =
      """{
        |  "type": "FeatureCollection",
        |  "features": [{
        |    "type": "Feature",
        |    "geometry": {
        |      "type": "Point",
        |      "coordinates": [102.0, 0.5]
        |    },
        |    "properties": {
        |      "prop0": "value0",
        |      "prop1": null
        |    }
        |  }, {
        |    "type": "Feature",
        |    "geometry": {
        |      "type": "LineString",
        |      "coordinates": [
        |        [102.0, 0.0],
        |        [103.0, 1.0],
        |        [104.0, 0.0],
        |        [105.0, 1.0]
        |      ]
        |    },
        |    "properties": {
        |      "prop0": "value0",
        |      "prop1": 0.0
        |    }
        |  }, {
        |    "type": "Feature",
        |    "geometry": {
        |      "type": "Polygon",
        |      "coordinates": [
        |        [
        |          [100.0, 0.0],
        |          [101.0, 0.0],
        |          [101.0, 1.0],
        |          [100.0, 1.0],
        |          [100.0, 0.0]
        |        ]
        |      ]
        |    },
        |    "properties": {
        |      "prop0": "value0",
        |      "prop1": {
        |        "this": "that"
        |      }
        |    }
        |  }]
        |}""".stripMargin

    "encode correctly" in { features should encodeTo[FeatureCollection[Foo]](json) }
    "decode correctly" in { features should decodeFrom[FeatureCollection[Foo]](json) }
  }
}
