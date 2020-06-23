/* 
 * Copyright 2015 Alexander Nozik.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package scientifik.kmath.domains

import scientifik.kmath.linear.Point

/**
 * n-dimensional volume
 *
 * @author Alexander Nozik
 */
interface RealDomain: Domain<Double> {

    fun nearestInDomain(point: Point<Double>): Point<Double>

    /**
     * The lower edge for the domain going down from point
     * @param num
     * @param point
     * @return
     */
    fun getLowerBound(num: Int, point: Point<Double>): Double?

    /**
     * The upper edge of the domain going up from point
     * @param num
     * @param point
     * @return
     */
    fun getUpperBound(num: Int, point: Point<Double>): Double?

    /**
     * Global lower edge
     * @param num
     * @return
     */
    fun getLowerBound(num: Int): Double?

    /**
     * Global upper edge
     * @param num
     * @return
     */
    fun getUpperBound(num: Int): Double?

    /**
     * Hyper volume
     * @return
     */
    fun volume(): Double

}