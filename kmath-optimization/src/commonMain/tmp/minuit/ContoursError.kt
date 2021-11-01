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
 * ContoursError class.
 *
 * @author Darksnake
 * @version $Id$
 */
class ContoursError internal constructor(
    private val theParX: Int,
    private val theParY: Int,
    points: List<Range>,
    xmnos: MinosError,
    ymnos: MinosError,
    nfcn: Int
) {
    private val theNFcn: Int
    private val thePoints: List<Range> = points
    private val theXMinos: MinosError
    private val theYMinos: MinosError

    /**
     *
     * nfcn.
     *
     * @return a int.
     */
    fun nfcn(): Int {
        return theNFcn
    }

    /**
     *
     * points.
     *
     * @return a [List] object.
     */
    fun points(): List<Range> {
        return thePoints
    }

    /**
     * {@inheritDoc}
     */
    override fun toString(): String {
        return MnPrint.toString(this)
    }

    /**
     *
     * xMinosError.
     *
     * @return a [hep.dataforge.MINUIT.MinosError] object.
     */
    fun xMinosError(): MinosError {
        return theXMinos
    }

    /**
     *
     * xRange.
     *
     * @return
     */
    fun xRange(): Range {
        return theXMinos.range()
    }

    /**
     *
     * xmin.
     *
     * @return a double.
     */
    fun xmin(): Double {
        return theXMinos.min()
    }

    /**
     *
     * xpar.
     *
     * @return a int.
     */
    fun xpar(): Int {
        return theParX
    }

    /**
     *
     * yMinosError.
     *
     * @return a [hep.dataforge.MINUIT.MinosError] object.
     */
    fun yMinosError(): MinosError {
        return theYMinos
    }

    /**
     *
     * yRange.
     *
     * @return
     */
    fun yRange(): Range {
        return theYMinos.range()
    }

    /**
     *
     * ymin.
     *
     * @return a double.
     */
    fun ymin(): Double {
        return theYMinos.min()
    }

    /**
     *
     * ypar.
     *
     * @return a int.
     */
    fun ypar(): Int {
        return theParY
    }

    init {
        theXMinos = xmnos
        theYMinos = ymnos
        theNFcn = nfcn
    }
}