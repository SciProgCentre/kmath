/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.geometry

import space.kscience.kmath.operations.Algebra
import kotlin.math.*

public class GeodeticCoordinates private constructor(public val latitude: Double, public val longitude: Double) {
    init {
        require(longitude in (-PI)..(PI)) { "Longitude $longitude is not in (-PI)..(PI)" }
        require(latitude in (-PI / 2)..(PI / 2)) { "Latitude $latitude is not in (-PI/2)..(PI/2)" }
    }

    public companion object {
        public fun ofRadians(longitude: Double, latitude: Double): GeodeticCoordinates =
            GeodeticCoordinates(latitude, longitude)

        public fun ofDegrees(longitude: Double, latitude: Double): GeodeticCoordinates =
            GeodeticCoordinates(latitude * PI / 180, longitude * PI / 180)
    }
}

public data class WebMercatorCoordinates(val zoom: Double, val x: Double, val y: Double)

public object WebMercatorAlgebra : Algebra<WebMercatorCoordinates> {
    fun WebMercatorCoordinates.toGeodetic(): GeodeticCoordinates = TODO()

    /**
     * https://en.wikipedia.org/wiki/Web_Mercator_projection#Formulas
     */
    public fun GeodeticCoordinates.toMercator(zoom: Double): WebMercatorCoordinates {
        require(abs(latitude) <= 2 * atan(E.pow(PI)) - PI / 2) { "Latitude exceeds the maximum latitude for mercator coordinates" }

        val scaleFactor = 256.0 / 2 / PI * 2.0.pow(zoom)
        return WebMercatorCoordinates(
            zoom = zoom,
            x = scaleFactor * (longitude + PI),
            y = scaleFactor * (PI - ln(tan(PI / 4 + latitude / 2)))
        )
    }

    /**
     * Compute and offset of [target] coordinate relative to [base] coordinate. If [zoom] is null, then optimal zoom
     * will be computed to put the resulting x and y coordinates between -127.0 and 128.0
     */
    public fun computeOffset(
        base: GeodeticCoordinates,
        target: GeodeticCoordinates,
        zoom: Double? = null,
    ): WebMercatorCoordinates {
        val xOffsetUnscaled = target.longitude - base.longitude
        val yOffsetUnscaled = ln(
            tan(PI / 4 + target.latitude / 2) / tan(PI / 4 + base.latitude / 2)
        )

        val computedZoom = zoom ?: floor( log2(PI / max(abs(xOffsetUnscaled), abs(yOffsetUnscaled))))
        val scaleFactor = 256.0 / 2 / PI * 2.0.pow(computedZoom)
        return WebMercatorCoordinates(
            computedZoom,
            x = scaleFactor * xOffsetUnscaled,
            y = scaleFactor * yOffsetUnscaled
        )
    }
}