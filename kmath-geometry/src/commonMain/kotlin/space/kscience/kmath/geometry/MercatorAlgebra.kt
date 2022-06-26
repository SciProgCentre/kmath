/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.geometry

import space.kscience.kmath.operations.Algebra
import kotlin.math.*

/**
 * Geodetic coordinated
 */
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

public data class MercatorCoordinates(val x: Double, val y: Double)

/**
 * @param baseLongitude the longitude offset in radians
 * @param radius the average radius of the Earth
 * @param correctedRadius optional radius correction to account for ellipsoid model
 */
public open class MercatorAlgebra(
    public val baseLongitude: Double = 0.0,
    protected val radius: Double = DEFAULT_EARTH_RADIUS,
    private val correctedRadius: ((GeodeticCoordinates) -> Double)? = null,
) : Algebra<TileWebMercatorCoordinates> {

    public fun MercatorCoordinates.toGeodetic(): GeodeticCoordinates {
        val res = GeodeticCoordinates.ofRadians(
            atan(sinh(y / radius)),
            baseLongitude + x / radius,
        )
        return if (correctedRadius != null) {
            val r = correctedRadius.invoke(res)
            GeodeticCoordinates.ofRadians(
                atan(sinh(y / r)),
                baseLongitude + x / r,
            )
        } else {
            res
        }
    }

    /**
     * https://en.wikipedia.org/wiki/Web_Mercator_projection#Formulas
     */
    public fun GeodeticCoordinates.toMercator(): MercatorCoordinates {
        require(abs(latitude) <= MAXIMUM_LATITUDE) { "Latitude exceeds the maximum latitude for mercator coordinates" }
        val r = correctedRadius?.invoke(this) ?: radius
        return MercatorCoordinates(
            x = r * (longitude - baseLongitude),
            y = r * ln(tan(PI / 4 + latitude / 2))
        )
    }

    public companion object : MercatorAlgebra(0.0, 6378137.0) {
        public const val MAXIMUM_LATITUDE: Double = 85.05113
        public val DEFAULT_EARTH_RADIUS: Double = radius
    }
}