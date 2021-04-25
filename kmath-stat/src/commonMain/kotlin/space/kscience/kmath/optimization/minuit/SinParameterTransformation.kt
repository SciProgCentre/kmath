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
package ru.inr.mass.minuit

/**
 *
 * @version $Id$
 */
internal class SinParameterTransformation {
    fun dInt2Ext(value: Double, upper: Double, lower: Double): Double {
        return 0.5 * abs((upper - lower) * cos(value))
    }

    fun ext2int(value: Double, upper: Double, lower: Double, prec: MnMachinePrecision): Double {
        val piby2: Double = 2.0 * atan(1.0)
        val distnn: Double = 8.0 * sqrt(prec.eps2())
        val vlimhi = piby2 - distnn
        val vlimlo = -piby2 + distnn
        val yy = 2.0 * (value - lower) / (upper - lower) - 1.0
        val yy2 = yy * yy
        return if (yy2 > 1.0 - prec.eps2()) {
            if (yy < 0.0) {
                vlimlo
            } else {
                vlimhi
            }
        } else {
            asin(yy)
        }
    }

    fun int2ext(value: Double, upper: Double, lower: Double): Double {
        return lower + 0.5 * (upper - lower) * (sin(value) + 1.0)
    }
}