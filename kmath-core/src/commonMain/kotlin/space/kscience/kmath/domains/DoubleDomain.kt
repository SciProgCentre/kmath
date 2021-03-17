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
package space.kscience.kmath.domains

import space.kscience.kmath.misc.UnstableKMathAPI

/**
 * n-dimensional volume
 *
 * @author Alexander Nozik
 */
@UnstableKMathAPI
public interface DoubleDomain : Domain<Double> {

    /**
     * Global lower edge
     * @param num axis number
     */
    public fun getLowerBound(num: Int): Double

    /**
     * Global upper edge
     * @param num axis number
     */
    public fun getUpperBound(num: Int): Double

    /**
     * Hyper volume
     * @return
     */
    public fun volume(): Double
}
