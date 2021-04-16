/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.ejml

import org.ejml.simple.SimpleMatrix
import space.kscience.kmath.linear.Matrix
import space.kscience.kmath.nd.NDStructure

/**
 * Represents featured matrix over EJML [SimpleMatrix].
 *
 * @property origin the underlying [SimpleMatrix].
 * @author Iaroslav Postovalov
 */
public class EjmlMatrix(public val origin: SimpleMatrix) : Matrix<Double> {
    public override val rowNum: Int get() = origin.numRows()
    public override val colNum: Int get() = origin.numCols()

    public override operator fun get(i: Int, j: Int): Double = origin[i, j]

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is NDStructure<*>) return false
        return NDStructure.contentEquals(this, other)
    }

    override fun hashCode(): Int = origin.hashCode()


}
