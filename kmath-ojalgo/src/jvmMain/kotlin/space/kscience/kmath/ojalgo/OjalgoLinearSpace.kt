/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.ojalgo

import org.ojalgo.matrix.store.MatrixStore
import org.ojalgo.matrix.store.PhysicalStore
import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.linear.*
import space.kscience.kmath.nd.StructureAttribute
import space.kscience.kmath.operations.Ring
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.IntBuffer
import space.kscience.kmath.structures.asBuffer
import space.kscience.kmath.structures.asList

@JvmInline
public value class OjalgoBuffer<T : Comparable<T>>(public val ojalgoMatrix: MatrixStore<T>) : Buffer<T> {
    override val size: Int get() = ojalgoMatrix.size()

    override fun get(index: Int): T = ojalgoMatrix.get(index.toLong())

    override fun toString(): String = ojalgoMatrix.toString()
}

@JvmInline
public value class OjalgoMatrix<T : Comparable<T>>(public val ojalgoVector: MatrixStore<T>) : Matrix<T> {
    override val rowNum: Int get() = ojalgoVector.rowDim

    override val colNum: Int get() = ojalgoVector.colDim

    override fun get(i: Int, j: Int): T = ojalgoVector.get(i.toLong(), j.toLong())
}


public class OjalgoLinearSpace<T : Comparable<T>, A : Ring<T>>(
    public val ojalgo: Ojalgo<T, A>
) : LinearSpace<T, A> {

    override val elementAlgebra: A
        get() = ojalgo.elementAlgebra

    public fun MatrixStore<T>.asMatrix(): OjalgoMatrix<T> = OjalgoMatrix(this)

    public fun MatrixStore<T>.asVector(): OjalgoBuffer<T> = OjalgoBuffer(this)

    /**
     * If this matrix is [OjalgoMatrix] return it without conversion, otherwise create new [PhysicalStore]
     */
    @OptIn(UnstableKMathAPI::class)
    public fun Matrix<T>.toOjalgo(): MatrixStore<T> = when (val matrix = origin) {
        is OjalgoMatrix<T> -> matrix.ojalgoVector
        else -> ojalgo.storeFactory.make(rowNum.toLong(), colNum.toLong()).apply {
            for (row in 0 until rowNum) {
                for (column in 0 until colNum) {
                    set(row.toLong(), column.toLong(), get(row, column))
                }
            }
        }

    }

    /**
     * If this vector is [OjalgoBuffer] return it without conversion, otherwise create new [PhysicalStore]
     */
    public fun Point<T>.toOjalgo(): MatrixStore<T> =
        (this as? OjalgoBuffer<T>)?.ojalgoMatrix ?: ojalgo.storeFactory.column(asList())

    override fun buildMatrix(
        rows: Int,
        columns: Int,
        initializer: A.(Int, Int) -> T
    ): Matrix<T> {
        val structure: MatrixStore<T> = ojalgo.storeFactory.make(rows.toLong(), columns.toLong()).apply {
            for (row in 0 until rows) {
                for (column in 0 until columns) {
                    set(row.toLong(), column.toLong(), elementAlgebra.initializer(row, column))
                }
            }
        }
        return OjalgoMatrix(structure)
    }

    override fun buildVector(size: Int, initializer: A.(Int) -> T): Point<T> {
        val structure: MatrixStore<T> = ojalgo.storeFactory.column(List(size) { elementAlgebra.initializer(it) })
        return OjalgoBuffer(structure)
    }

    @OptIn(UnstableKMathAPI::class)
    override fun <V, A : StructureAttribute<V>> computeAttribute(
        structure: Matrix<T>,
        attribute: A
    ): V? {

        val origin = structure.toOjalgo()

        val raw: Any? = when (attribute) {
            Determinant -> ojalgo.lu.make(origin).apply { decompose(origin) }.determinant

            Inverted -> ojalgo.lu.make().apply { decompose(origin) }.inverse.asMatrix()

            LUP -> object : LupDecomposition<T> {
                val lup by lazy {
                    ojalgo.lu.make(origin).apply { decompose(origin) }
                }
                override val pivot: IntBuffer get() = lup.pivotOrder.asBuffer()
                override val l: Matrix<T> get() = lup.l.asMatrix().withAttribute(LowerTriangular)
                override val u: Matrix<T> get() = lup.u.asMatrix().withAttribute(UpperTriangular)
            }

            Cholesky -> object : CholeskyDecomposition<T> {
                val cholesky by lazy {
                    ojalgo.cholesky.make(origin).apply { decompose(origin) }
                }
                override val l: Matrix<T> get() = cholesky.l.asMatrix()
            }

            QR -> object : QRDecomposition<T> {
                val qr by lazy {
                    ojalgo.qr.make(origin).apply { decompose(origin) }
                }
                override val q: Matrix<T> get() = qr.q.asMatrix().withAttribute(OrthogonalAttribute)
                override val r: Matrix<T> get() = qr.r.asMatrix().withAttribute(UpperTriangular)
            }

            SVD -> object : SingularValueDecomposition<T> {
                val svd by lazy {
                    ojalgo.svd.make(origin).apply { decompose(origin) }
                }

                override val u: Matrix<T> get() = svd.u.asMatrix()
                override val s: Matrix<T> get() = ojalgo.storeFactory.makeDiagonal(svd.singularValues).get().asMatrix()
                override val v: Matrix<T> get() = svd.v.asMatrix()

                override val singularValues: Point<T>
                    get() = ojalgo.storeFactory.asFactory1D().make(svd.singularValues).asList().asBuffer()

            }

            EIG -> object : EigenDecomposition<T> {
                val eigen by lazy {
                    ojalgo.eigen.make(origin).apply { decompose(origin) }
                }

                override val v: Matrix<T> get() = eigen.v.asMatrix()
                override val d: Matrix<T> get() = eigen.d.asMatrix()
            }

            else -> null
        }
        @Suppress("UNCHECKED_CAST")
        return raw as V?
    }

    override fun Matrix<T>.times(value: T): OjalgoMatrix<T> = toOjalgo().multiply(value).asMatrix()

    override fun Matrix<T>.dot(vector: Point<T>): OjalgoBuffer<T> = toOjalgo().multiply(vector.toOjalgo()).asVector()

    override fun Matrix<T>.dot(other: Matrix<T>): OjalgoMatrix<T> = toOjalgo().multiply(other.toOjalgo()).asMatrix()

    override fun Point<T>.times(value: T): OjalgoBuffer<T> = toOjalgo().multiply(value).asVector()

    override fun Point<T>.minus(other: Point<T>): OjalgoBuffer<T> = toOjalgo().subtract(other.toOjalgo()).asVector()

    override fun Matrix<T>.minus(other: Matrix<T>): OjalgoMatrix<T> = toOjalgo().subtract(other.toOjalgo()).asMatrix()

    override fun Point<T>.plus(other: Point<T>): OjalgoBuffer<T> = toOjalgo().subtract(other.toOjalgo()).asVector()

    override fun Matrix<T>.plus(other: Matrix<T>): OjalgoMatrix<T> = toOjalgo().add(other.toOjalgo()).asMatrix()

    override fun Point<T>.unaryMinus(): OjalgoBuffer<T> = toOjalgo().negate().asVector()

    override fun Matrix<T>.unaryMinus(): OjalgoMatrix<T> = toOjalgo().negate().asMatrix()

}

public val <T : Comparable<T>, A : Ring<T>> Ojalgo<T, A>.linearSpace: OjalgoLinearSpace<T, A>
    get() = OjalgoLinearSpace(this)