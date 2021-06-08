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
internal class SqrtLowParameterTransformation {
    // derivative of transformation from internal to external
    fun dInt2Ext(value: Double, lower: Double): Double {
        return value / sqrt(value * value + 1.0)
    }

    // transformation from external to internal
    fun ext2int(value: Double, lower: Double, prec: MnMachinePrecision): Double {
        val yy = value - lower + 1.0
        val yy2 = yy * yy
        return if (yy2 < 1.0 + prec.eps2()) {
            8 * sqrt(prec.eps2())
        } else {
            sqrt(yy2 - 1)
        }
    }

    // transformation from internal to external
    fun int2ext(value: Double, lower: Double): Double {
        return lower - 1.0 + sqrt(value * value + 1.0)
    }
}