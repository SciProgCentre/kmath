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
 * parabola = a*xx + b*x + c
 *
 * @version $Id$
 */
internal class MnParabola(private val theA: Double, private val theB: Double, private val theC: Double) {
    fun a(): Double {
        return theA
    }

    fun b(): Double {
        return theB
    }

    fun c(): Double {
        return theC
    }

    fun min(): Double {
        return -theB / (2.0 * theA)
    }

    fun x_neg(y: Double): Double {
        return -sqrt(y / theA + min() * min() - theC / theA) + min()
    }

    fun x_pos(y: Double): Double {
        return sqrt(y / theA + min() * min() - theC / theA) + min()
    }

    fun y(x: Double): Double {
        return theA * x * x + theB * x + theC
    }

    fun ymin(): Double {
        return -theB * theB / (4.0 * theA) + theC
    }
}