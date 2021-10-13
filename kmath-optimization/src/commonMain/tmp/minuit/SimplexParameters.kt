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

import org.apache.commons.math3.linear.ArrayRealVector

/**
 *
 * @version $Id$
 */
internal class SimplexParameters(simpl: MutableList<Pair<Double?, RealVector?>>, jh: Int, jl: Int) {
    private var theJHigh: Int
    private var theJLow: Int
    private val theSimplexParameters: MutableList<Pair<Double?, RealVector?>>
    fun dirin(): ArrayRealVector {
        val dirin = ArrayRealVector(theSimplexParameters.size - 1)
        for (i in 0 until theSimplexParameters.size - 1) {
            var pbig: Double = theSimplexParameters[0].getSecond().getEntry(i)
            var plit = pbig
            for (theSimplexParameter in theSimplexParameters) {
                if (theSimplexParameter.getSecond().getEntry(i) < plit) {
                    plit = theSimplexParameter.getSecond().getEntry(i)
                }
                if (theSimplexParameter.getSecond().getEntry(i) > pbig) {
                    pbig = theSimplexParameter.getSecond().getEntry(i)
                }
            }
            dirin.setEntry(i, pbig - plit)
        }
        return dirin
    }

    fun edm(): Double {
        return theSimplexParameters[jh()].getFirst() - theSimplexParameters[jl()].getFirst()
    }

    operator fun get(i: Int): Pair<Double?, RealVector?> {
        return theSimplexParameters[i]
    }

    fun jh(): Int {
        return theJHigh
    }

    fun jl(): Int {
        return theJLow
    }

    fun simplex(): List<Pair<Double?, RealVector?>> {
        return theSimplexParameters
    }

    fun update(y: Double, p: RealVector?) {
        theSimplexParameters.set(jh(), Pair(y, p))
        if (y < theSimplexParameters[jl()].getFirst()) {
            theJLow = jh()
        }
        var jh = 0
        for (i in 1 until theSimplexParameters.size) {
            if (theSimplexParameters[i].getFirst() > theSimplexParameters[jh].getFirst()) {
                jh = i
            }
        }
        theJHigh = jh
    }

    init {
        theSimplexParameters = simpl
        theJHigh = jh
        theJLow = jl
    }
}