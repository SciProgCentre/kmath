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
class MnAlgebraicSymMatrix(n: Int) {
    private val theData: DoubleArray
    private val theNRow: Int
    private val theSize: Int

    /**
     *
     * copy.
     *
     * @return a [hep.dataforge.MINUIT.MnAlgebraicSymMatrix] object.
     */
    fun copy(): MnAlgebraicSymMatrix {
        val copy = MnAlgebraicSymMatrix(theNRow)
        java.lang.System.arraycopy(theData, 0, copy.theData, 0, theSize)
        return copy
    }

    fun data(): DoubleArray {
        return theData
    }

    fun eigenvalues(): ArrayRealVector {
        val nrow = theNRow
        val tmp = DoubleArray((nrow + 1) * (nrow + 1))
        val work = DoubleArray(1 + 2 * nrow)
        for (i in 0 until nrow) {
            for (j in 0..i) {
                tmp[1 + i + (1 + j) * nrow] = get(i, j)
                tmp[(1 + i) * nrow + (1 + j)] = get(i, j)
            }
        }
        val info = mneigen(tmp, nrow, nrow, work.size, work, 1e-6)
        if (info != 0) {
            throw EigenvaluesException()
        }
        val result = ArrayRealVector(nrow)
        for (i in 0 until nrow) {
            result.setEntry(i, work[1 + i])
        }
        return result
    }

    operator fun get(row: Int, col: Int): Double {
        if (row >= theNRow || col >= theNRow) {
            throw ArrayIndexOutOfBoundsException()
        }
        return theData[theIndex(row, col)]
    }

    @Throws(SingularMatrixException::class)
    fun invert() {
        if (theSize == 1) {
            val tmp = theData[0]
            if (tmp <= 0.0) {
                throw SingularMatrixException()
            }
            theData[0] = 1.0 / tmp
        } else {
            val nrow = theNRow
            val s = DoubleArray(nrow)
            val q = DoubleArray(nrow)
            val pp = DoubleArray(nrow)
            for (i in 0 until nrow) {
                val si = theData[theIndex(i, i)]
                if (si < 0.0) {
                    throw SingularMatrixException()
                }
                s[i] = 1.0 / sqrt(si)
            }
            for (i in 0 until nrow) {
                for (j in i until nrow) {
                    theData[theIndex(i, j)] *= s[i] * s[j]
                }
            }
            for (i in 0 until nrow) {
                var k = i
                if (theData[theIndex(k, k)] == 0.0) {
                    throw SingularMatrixException()
                }
                q[k] = 1.0 / theData[theIndex(k, k)]
                pp[k] = 1.0
                theData[theIndex(k, k)] = 0.0
                val kp1 = k + 1
                if (k != 0) {
                    for (j in 0 until k) {
                        val index = theIndex(j, k)
                        pp[j] = theData[index]
                        q[j] = theData[index] * q[k]
                        theData[index] = 0.0
                    }
                }
                if (k != nrow - 1) {
                    for (j in kp1 until nrow) {
                        val index = theIndex(k, j)
                        pp[j] = theData[index]
                        q[j] = -theData[index] * q[k]
                        theData[index] = 0.0
                    }
                }
                for (j in 0 until nrow) {
                    k = j
                    while (k < nrow) {
                        theData[theIndex(j, k)] += pp[j] * q[k]
                        k++
                    }
                }
            }
            for (j in 0 until nrow) {
                for (k in j until nrow) {
                    theData[theIndex(j, k)] *= s[j] * s[k]
                }
            }
        }
    }

    fun ncol(): Int {
        return nrow()
    }

    fun nrow(): Int {
        return theNRow
    }

    operator fun set(row: Int, col: Int, value: Double) {
        if (row >= theNRow || col >= theNRow) {
            throw ArrayIndexOutOfBoundsException()
        }
        theData[theIndex(row, col)] = value
    }

    fun size(): Int {
        return theSize
    }

    private fun theIndex(row: Int, col: Int): Int {
        return if (row > col) {
            col + row * (row + 1) / 2
        } else {
            row + col * (col + 1) / 2
        }
    }

    /** {@inheritDoc}  */
    override fun toString(): String {
        return MnPrint.toString(this)
    } /* mneig_ */

    private inner class EigenvaluesException : RuntimeException()
    companion object {
        private fun mneigen(a: DoubleArray, ndima: Int, n: Int, mits: Int, work: DoubleArray, precis: Double): Int {

            /* System generated locals */
            var i__2: Int
            var i__3: Int

            /* Local variables */
            var b: Double
            var c__: Double
            var f: Double
            var h__: Double
            var i__: Int
            var j: Int
            var k: Int
            var l: Int
            var m = 0
            var r__: Double
            var s: Double
            var i0: Int
            var i1: Int
            var j1: Int
            var m1: Int
            var hh: Double
            var gl: Double
            var pr: Double
            var pt: Double

            /* PRECIS is the machine precision EPSMAC */
            /* Parameter adjustments */
            val a_dim1: Int = ndima
            val a_offset: Int = 1 + a_dim1 * 1

            /* Function Body */
            var ifault = 1
            i__ = n
            var i__1: Int = n
            i1 = 2
            while (i1 <= i__1) {
                l = i__ - 2
                f = a[i__ + (i__ - 1) * a_dim1]
                gl = 0.0
                if (l >= 1) {
                    i__2 = l
                    k = 1
                    while (k <= i__2) {

                        /* Computing 2nd power */
                        val r__1 = a[i__ + k * a_dim1]
                        gl += r__1 * r__1
                        ++k
                    }
                }
                /* Computing 2nd power */h__ = gl + f * f
                if (gl <= 1e-35) {
                    work[i__] = 0.0
                    work[n + i__] = f
                } else {
                    ++l
                    gl = sqrt(h__)
                    if (f >= 0.0) {
                        gl = -gl
                    }
                    work[n + i__] = gl
                    h__ -= f * gl
                    a[i__ + (i__ - 1) * a_dim1] = f - gl
                    f = 0.0
                    i__2 = l
                    j = 1
                    while (j <= i__2) {
                        a[j + i__ * a_dim1] = a[i__ + j * a_dim1] / h__
                        gl = 0.0
                        i__3 = j
                        k = 1
                        while (k <= i__3) {
                            gl += a[j + k * a_dim1] * a[i__ + k * a_dim1]
                            ++k
                        }
                        if (j < l) {
                            j1 = j + 1
                            i__3 = l
                            k = j1
                            while (k <= i__3) {
                                gl += a[k + j * a_dim1] * a[i__ + k * a_dim1]
                                ++k
                            }
                        }
                        work[n + j] = gl / h__
                        f += gl * a[j + i__ * a_dim1]
                        ++j
                    }
                    hh = f / (h__ + h__)
                    i__2 = l
                    j = 1
                    while (j <= i__2) {
                        f = a[i__ + j * a_dim1]
                        gl = work[n + j] - hh * f
                        work[n + j] = gl
                        i__3 = j
                        k = 1
                        while (k <= i__3) {
                            a[j + k * a_dim1] = a[j + k * a_dim1] - f * work[n + k] - (gl
                                    * a[i__ + k * a_dim1])
                            ++k
                        }
                        ++j
                    }
                    work[i__] = h__
                }
                --i__
                ++i1
            }
            work[1] = 0.0
            work[n + 1] = 0.0
            i__1 = n
            i__ = 1
            while (i__ <= i__1) {
                l = i__ - 1
                if (work[i__] != 0.0 && l != 0) {
                    i__3 = l
                    j = 1
                    while (j <= i__3) {
                        gl = 0.0
                        i__2 = l
                        k = 1
                        while (k <= i__2) {
                            gl += a[i__ + k * a_dim1] * a[k + j * a_dim1]
                            ++k
                        }
                        i__2 = l
                        k = 1
                        while (k <= i__2) {
                            a[k + j * a_dim1] -= gl * a[k + i__ * a_dim1]
                            ++k
                        }
                        ++j
                    }
                }
                work[i__] = a[i__ + i__ * a_dim1]
                a[i__ + i__ * a_dim1] = 1.0
                if (l != 0) {
                    i__2 = l
                    j = 1
                    while (j <= i__2) {
                        a[i__ + j * a_dim1] = 0.0
                        a[j + i__ * a_dim1] = 0.0
                        ++j
                    }
                }
                ++i__
            }
            val n1: Int = n - 1
            i__1 = n
            i__ = 2
            while (i__ <= i__1) {
                i0 = n + i__ - 1
                work[i0] = work[i0 + 1]
                ++i__
            }
            work[n + n] = 0.0
            b = 0.0
            f = 0.0
            i__1 = n
            l = 1
            while (l <= i__1) {
                j = 0
                h__ = precis * (abs(work[l]) + abs(work[n + l]))
                if (b < h__) {
                    b = h__
                }
                i__2 = n
                m1 = l
                while (m1 <= i__2) {
                    m = m1
                    if (abs(work[n + m]) <= b) {
                        break
                    }
                    ++m1
                }
                if (m != l) {
                    while (true) {
                        if (j == mits) {
                            return ifault
                        }
                        ++j
                        pt = (work[l + 1] - work[l]) / (work[n + l] * 2.0)
                        r__ = sqrt(pt * pt + 1.0)
                        pr = pt + r__
                        if (pt < 0.0) {
                            pr = pt - r__
                        }
                        h__ = work[l] - work[n + l] / pr
                        i__2 = n
                        i__ = l
                        while (i__ <= i__2) {
                            work[i__] -= h__
                            ++i__
                        }
                        f += h__
                        pt = work[m]
                        c__ = 1.0
                        s = 0.0
                        m1 = m - 1
                        i__ = m
                        i__2 = m1
                        i1 = l
                        while (i1 <= i__2) {
                            j = i__
                            --i__
                            gl = c__ * work[n + i__]
                            h__ = c__ * pt
                            if (abs(pt) < abs(work[n + i__])) {
                                c__ = pt / work[n + i__]
                                r__ = sqrt(c__ * c__ + 1.0)
                                work[n + j] = s * work[n + i__] * r__
                                s = 1.0 / r__
                                c__ /= r__
                            } else {
                                c__ = work[n + i__] / pt
                                r__ = sqrt(c__ * c__ + 1.0)
                                work[n + j] = s * pt * r__
                                s = c__ / r__
                                c__ = 1.0 / r__
                            }
                            pt = c__ * work[i__] - s * gl
                            work[j] = h__ + s * (c__ * gl + s * work[i__])
                            i__3 = n
                            k = 1
                            while (k <= i__3) {
                                h__ = a[k + j * a_dim1]
                                a[k + j * a_dim1] = s * a[k + i__ * a_dim1] + c__ * h__
                                a[k + i__ * a_dim1] = c__ * a[k + i__ * a_dim1] - s * h__
                                ++k
                            }
                            ++i1
                        }
                        work[n + l] = s * pt
                        work[l] = c__ * pt
                        if (abs(work[n + l]) <= b) {
                            break
                        }
                    }
                }
                work[l] += f
                ++l
            }
            i__1 = n1
            i__ = 1
            while (i__ <= i__1) {
                k = i__
                pt = work[i__]
                i1 = i__ + 1
                i__3 = n
                j = i1
                while (j <= i__3) {
                    if (work[j] < pt) {
                        k = j
                        pt = work[j]
                    }
                    ++j
                }
                if (k != i__) {
                    work[k] = work[i__]
                    work[i__] = pt
                    i__3 = n
                    j = 1
                    while (j <= i__3) {
                        pt = a[j + i__ * a_dim1]
                        a[j + i__ * a_dim1] = a[j + k * a_dim1]
                        a[j + k * a_dim1] = pt
                        ++j
                    }
                }
                ++i__
            }
            ifault = 0
            return ifault
        } /* mneig_ */
    }

    init {
        require(n >= 0) { "Invalid matrix size: $n" }
        theSize = n * (n + 1) / 2
        theNRow = n
        theData = DoubleArray(theSize)
    }
}