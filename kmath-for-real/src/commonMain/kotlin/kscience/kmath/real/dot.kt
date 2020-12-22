package kscience.kmath.real

import kscience.kmath.linear.BufferMatrix
import kscience.kmath.structures.Buffer
import kscience.kmath.structures.RealBuffer


/**
 * Optimized dot product for real matrices
 */
public infix fun BufferMatrix<Double>.dot(other: BufferMatrix<Double>): BufferMatrix<Double> {
    require(colNum == other.rowNum) { "Matrix dot operation dimension mismatch: ($rowNum, $colNum) x (${other.rowNum}, ${other.colNum})" }
    val resultArray = DoubleArray(this.rowNum * other.colNum)

    //convert to array to insure there is no memory indirection
    fun Buffer<out Double>.unsafeArray() = if (this is RealBuffer)
        this.array
    else
        DoubleArray(size) { get(it) }

    val a = this.buffer.unsafeArray()
    val b = other.buffer.unsafeArray()

    for (i in (0 until rowNum))
        for (j in (0 until other.colNum))
            for (k in (0 until colNum))
                resultArray[i * other.colNum + j] += a[i * colNum + k] * b[k * other.colNum + j]

    val buffer = RealBuffer(resultArray)
    return BufferMatrix(rowNum, other.colNum, buffer)
}