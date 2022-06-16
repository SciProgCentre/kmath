/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.geometry

import space.kscience.kmath.operations.Algebra
import kotlin.math.*

public class GeodeticCoordinates private constructor(public val latitude: Double, public val longitude: Double) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as GeodeticCoordinates

        if (latitude != other.latitude) return false
        if (longitude != other.longitude) return false

        return true
    }

    override fun hashCode(): Int {
        var result = latitude.hashCode()
        result = 31 * result + longitude.hashCode()
        return result
    }

    override fun toString(): String {
        return "GeodeticCoordinates(latitude=$latitude, longitude=$longitude)"
    }


    public companion object {
        public fun ofRadians(latitude: Double, longitude: Double): GeodeticCoordinates {
            require(longitude in (-PI)..(PI)) { "Longitude $longitude is not in (-PI)..(PI)" }
            return GeodeticCoordinates(latitude, longitude.rem(PI / 2))
        }

        public fun ofDegrees(latitude: Double, longitude: Double): GeodeticCoordinates {
            require(latitude in (-90.0)..(90.0)) { "Latitude $latitude is not in -90..90" }
            return GeodeticCoordinates(latitude * PI / 180, (longitude.rem(180) * PI / 180))
        }
    }
}

public data class WebMercatorCoordinates(val zoom: Double, val x: Double, val y: Double)

public object WebMercatorAlgebra : Algebra<WebMercatorCoordinates> {

    private fun scaleFactor(zoom: Double) = 256.0 / 2 / PI * 2.0.pow(zoom)

    public fun WebMercatorCoordinates.toGeodetic(): GeodeticCoordinates {
        val scaleFactor = scaleFactor(zoom)
        val longitude = x / scaleFactor - PI
        val latitude = (atan(exp(PI - y / scaleFactor)) - PI / 4) * 2
        return GeodeticCoordinates.ofRadians(latitude, longitude)
    }

    /**
     * https://en.wikipedia.org/wiki/Web_Mercator_projection#Formulas
     */
    public fun GeodeticCoordinates.toMercator(zoom: Double): WebMercatorCoordinates {
        require(abs(latitude) <= 2 * atan(E.pow(PI)) - PI / 2) { "Latitude exceeds the maximum latitude for mercator coordinates" }

        val scaleFactor = scaleFactor(zoom)
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

        val computedZoom = zoom ?: floor(log2(PI / max(abs(xOffsetUnscaled), abs(yOffsetUnscaled))))
        val scaleFactor = scaleFactor(computedZoom)
        return WebMercatorCoordinates(
            computedZoom,
            x = scaleFactor * xOffsetUnscaled,
            y = scaleFactor * yOffsetUnscaled
        )
    }
}