package io.data2viz.geo

import io.data2viz.geojson.*
import io.data2viz.math.toRadians
import io.data2viz.path.epsilon

fun contains(geo: GeoJsonObject, point: Position): Boolean {
    // TODO add containsObjectType for features...
    return containsGeometry(geo, point)
}

private fun containsGeometry(geo: GeoJsonObject, point: Position): Boolean {
    when (geo) {
        is Point -> return containsPoint(geo.coordinates, point)
        is MultiPoint -> {
            geo.coordinates.forEach { if (containsPoint(it, point)) return true }
            return false
        }
        is Polygon -> return containsPolygon(geo.coordinates, point)
        is MultiPolygon -> {
            geo.coordinates.forEach { if (containsPolygon(it, point)) return true }
            return false
        }
        is LineString -> return containsLine(geo.coordinates, point)
        is MultiLineString -> {
            geo.coordinates.forEach { if (containsLine(it, point)) return true }
            return false
        }
        is Sphere -> return true
        is GeometryCollection -> {
            geo.geometries.forEach { if (containsGeometry(it, point)) return true }
            return false
        }
        is FeatureCollection -> {
            geo.features.forEach { if (containsGeometry(it, point)) return true }
            return false
        }
        is Feature -> return containsGeometry(geo.geometry, point)
        else -> return false
    }
}

private fun containsPolygon(coordinates: Lines, point: Position): Boolean {
    val coords = coordinates.map { it.map { toRadians(it) } }.toMutableList()
    coords.removeAt(coords.lastIndex)
    return polygonContains(coords, toRadians(point))
}

fun toRadians(array: Position): DoubleArray {
    return array.map { it.toRadians() }.toDoubleArray()
}

private fun containsLine(coordinates: Positions, point: Position): Boolean {
    val ab = geoDistance(coordinates[0], coordinates[1])
    val ao = geoDistance(coordinates[0], point)
    val ob = geoDistance(point, coordinates[1])
    return ao + ob <= ab + epsilon
}

private fun containsPoint(coordinates: Position, point: Position): Boolean {
    return geoDistance(coordinates, point) == .0
}