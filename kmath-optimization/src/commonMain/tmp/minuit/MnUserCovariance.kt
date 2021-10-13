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
 * MnUserCovariance is the external covariance matrix designed for the
 * interaction of the user. The result of the minimization (internal covariance
 * matrix) is converted into the user representable format. It can also be used
 * as input prior to the minimization. The size of the covariance matrix is
 * according to the number of variable parameters (free and limited).
 *
 * @version $Id$
 * @author Darksnake
 */
class MnUserCovariance {
    private var theData: DoubleArray
    private var theNRow: Int

    private constructor(other: MnUserCovariance) {
        theData = other.theData.clone()
        theNRow = other.theNRow
    }

    internal constructor() {
        theData = DoubleArray(0)
        theNRow = 0
    }

    /*
     * covariance matrix is stored in upper triangular packed storage format,
     * e.g. the elements in the array are arranged like
     * {a(0,0), a(0,1), a(1,1), a(0,2), a(1,2), a(2,2), ...},
     * the size is nrow*(nrow+1)/2.
     */
    internal constructor(data: DoubleArray, nrow: Int) {
        require(data.size == nrow * (nrow + 1) / 2) { "Inconsistent arguments" }
        theData = data
        theNRow = nrow
    }

    /**
     *
     * Constructor for MnUserCovariance.
     *
     * @param nrow a int.
     */
    constructor(nrow: Int) {
        theData = DoubleArray(nrow * (nrow + 1) / 2)
        theNRow = nrow
    }

    /**
     *
     * copy.
     *
     * @return a [hep.dataforge.MINUIT.MnUserCovariance] object.
     */
    fun copy(): MnUserCovariance {
        return MnUserCovariance(this)
    }

    fun data(): DoubleArray {
        return theData
    }

    /**
     *
     * get.
     *
     * @param row a int.
     * @param col a int.
     * @return a double.
     */
    operator fun get(row: Int, col: Int): Double {
        require(!(row >= theNRow || col >= theNRow))
        return if (row > col) {
            theData[col + row * (row + 1) / 2]
        } else {
            theData[row + col * (col + 1) / 2]
        }
    }

    /**
     *
     * ncol.
     *
     * @return a int.
     */
    fun ncol(): Int {
        return theNRow
    }

    /**
     *
     * nrow.
     *
     * @return a int.
     */
    fun nrow(): Int {
        return theNRow
    }

    fun scale(f: Double) {
        for (i in theData.indices) {
            theData[i] *= f
        }
    }

    /**
     *
     * set.
     *
     * @param row a int.
     * @param col a int.
     * @param value a double.
     */
    operator fun set(row: Int, col: Int, value: Double) {
        require(!(row >= theNRow || col >= theNRow))
        if (row > col) {
            theData[col + row * (row + 1) / 2] = value
        } else {
            theData[row + col * (col + 1) / 2] = value
        }
    }

    fun size(): Int {
        return theData.size
    }

    /** {@inheritDoc}  */
    override fun toString(): String {
        return MnPrint.toString(this)
    }
}