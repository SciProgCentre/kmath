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
 * Determines the relative floating point arithmetic precision. The
 * setPrecision() method can be used to override Minuit's own determination,
 * when the user knows that the {FCN} function value is not calculated to the
 * nominal machine accuracy.
 *
 * @version $Id$
 * @author Darksnake
 */
class MnMachinePrecision internal constructor() {
    private var theEpsMa2 = 0.0
    private var theEpsMac = 0.0

    /**
     * eps returns the smallest possible number so that 1.+eps > 1.
     * @return
     */
    fun eps(): Double {
        return theEpsMac
    }

    /**
     * eps2 returns 2*sqrt(eps)
     * @return
     */
    fun eps2(): Double {
        return theEpsMa2
    }

    /**
     * override Minuit's own determination
     *
     * @param prec a double.
     */
    fun setPrecision(prec: Double) {
        theEpsMac = prec
        theEpsMa2 = 2.0 * sqrt(theEpsMac)
    }

    init {
        setPrecision(4.0E-7)
        var epstry = 0.5
        val one = 1.0
        for (i in 0..99) {
            epstry *= 0.5
            val epsp1 = one + epstry
            val epsbak = epsp1 - one
            if (epsbak < epstry) {
                setPrecision(8.0 * epstry)
                break
            }
        }
    }
}