package io.data2viz.geo.projection

import io.data2viz.core.Extent
import io.data2viz.geo.clip.clipExtent
import io.data2viz.geo.clip.clipRectangle
import io.data2viz.geo.path.geoPath
import io.data2viz.geojson.MultiPolygon
import io.data2viz.path.SvgPath
import io.data2viz.path.svgPath
import io.data2viz.test.TestBase
import kotlin.test.Test

class RectangleClippingTests : TestBase() {

    fun getProjection() = equirectangularProjection {
        translate = doubleArrayOf(480.0, 350.0)
        scale = 2000.0
        precision = .0
        center = doubleArrayOf(10.0, 5.0)
    }

    val polygon = MultiPolygon(
        arrayOf(
            arrayOf(
                arrayOf(
                    arrayOf(5.0, 5.0),
                    arrayOf(2.50, 7.5),
                    arrayOf(5.0, 10.0),
                    arrayOf(10.0, 10.0),
                    arrayOf(12.5, 7.5),
                    arrayOf(10.0, 5.0),
                    arrayOf(5.0, 5.0)
                )
            )
        )
    )

    @Test
    fun no_clipping() {
        val geoPath = geoPath(getProjection(), svgPath())
        val path: SvgPath = geoPath.path(polygon) as SvgPath

        path.path.round() shouldBe "M305.46707480056705,350L218.20061220085057,262.7335374002835L305.46707480056705,175.46707480056705L480,175.46707480056705L567.2664625997165,262.7335374002835L480,350Z".round()
    }

    @Test
    fun rectangle_clipping_east() {
        val projection = getProjection()
        projection.postClip = clipRectangle(48.0, 50.0, 498.0, 500.0)

        val geoPath = geoPath(projection, svgPath())
        val path: SvgPath = geoPath.path(polygon) as SvgPath

        path.path.round() shouldBe "M498,332L480,350L305.46707480056705,350L305.46707480056705,350L218.20061220085057,262.7335374002835L305.46707480056705,175.46707480056705L480,175.46707480056705L498,193.46707480056705L498,332Z".round()
    }

    @Test
    fun rectangle_clipping_north() {
        val projection = getProjection()
        projection.postClip = clipRectangle(200.0, 200.0, 700.0, 500.0)

        val geoPath = geoPath(projection, svgPath())
        val path: SvgPath = geoPath.path(polygon) as SvgPath

        path.path.round() shouldBe "M504.53292519943295,200L567.2664625997165,262.7335374002835L480,350L305.46707480056705,350L305.46707480056705,350L218.20061220085057,262.7335374002835L280.9341496011341,200L504.53292519943295,200Z".round()
    }

    @Test
    fun rectangle_clipping_south() {
        val projection = getProjection()
        projection.postClip = clipRectangle(48.0, 50.0, 700.0, 300.0)

        val geoPath = geoPath(projection, svgPath())
        val path: SvgPath = geoPath.path(polygon) as SvgPath

        path.path.round() shouldBe "M255.46707480056705,300L218.20061220085057,262.7335374002835L305.46707480056705,175.46707480056705L480,175.46707480056705L567.2664625997165,262.7335374002835L530,300L255.46707480056705,300Z".round()
    }

    @Test
    fun rectangle_clipping_west() {
        val projection = getProjection()
        projection.postClip = clipRectangle(250.0, 50.0, 700.0, 500.0)

        val geoPath = geoPath(projection, svgPath())
        val path: SvgPath = geoPath.path(polygon) as SvgPath

        path.path.round() shouldBe "M250,230.9341496011341L305.46707480056705,175.46707480056705L480,175.46707480056705L567.2664625997165,262.7335374002835L480,350L305.46707480056705,350L305.46707480056705,350L250,294.53292519943295L250,230.9341496011341Z".round()
    }

    @Test
    fun rectangle_vs_extent() {
        val projection = getProjection()
        projection.postClip = clipRectangle(250.0, 50.0, 700.0, 500.0)
        val geoPath1 = geoPath(projection, svgPath())
        val path1: SvgPath = geoPath1.path(polygon) as SvgPath

        projection.postClip = clipExtent(Extent(250.0, 50.0, 700.0, 500.0))
        val geoPath2 = geoPath(projection, svgPath())
        val path2: SvgPath = geoPath2.path(polygon) as SvgPath

        path1.path shouldBe path2.path
    }
}