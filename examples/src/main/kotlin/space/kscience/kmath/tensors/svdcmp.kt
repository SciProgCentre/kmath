package space.kscience.kmath.tensors

import space.kscience.kmath.nd.*
import space.kscience.kmath.tensors.core.BroadcastDoubleTensorAlgebra
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */


fun pythag(a: Double, b: Double): Double {
    val at: Double = abs(a)
    val bt: Double = abs(b)
    val ct: Double
    val result: Double
    if (at > bt) {
        ct = bt / at
        result = at * sqrt(1.0 + ct * ct)
    } else if (bt > 0.0) {
        ct = at / bt
        result = bt * sqrt(1.0 + ct * ct)
    } else result = 0.0
    return result
}

fun SIGN(a: Double, b: Double): Double {
    if (b >= 0.0)
        return abs(a)
    else
        return -abs(a)
}

internal fun MutableStructure2D<Double>.svdcmp(v: MutableStructure2D<Double>) {
    val shape = this.shape
    val n = shape.component2()
    val m = shape.component1()
    var f = 0.0
    val rv1 = DoubleArray(n)
    var s = 0.0
    var scale = 0.0
    var anorm = 0.0
    var g = 0.0
    var l = 0
    val w_shape = intArrayOf(m, 1)
    var w_buffer = doubleArrayOf(0.000000)
    for (i in 0 until m - 1) {
        w_buffer += doubleArrayOf(0.000000)
    }
    val w = BroadcastDoubleTensorAlgebra.fromArray(w_shape, w_buffer).as2D()
    for (i in 0 until n) {
        /* left-hand reduction */
        l = i + 1
        rv1[i] = scale * g
        g = 0.0
        s = 0.0
        scale = 0.0
        if (i < m) {
            for (k in i until m) {
                scale += abs(this[k, i]);
            }
            if (scale != 0.0) {
                for (k in i until m) {
                    this[k, i] = (this[k, i] / scale)
                    s += this[k, i] * this[k, i]
                }
                f = this[i, i]
                if (f >= 0) {
                    g = (-1) * abs(sqrt(s))
                }
                else {
                    g = abs(sqrt(s))
                }
                val h = f * g - s
                this[i, i] = f - g
                if (i != n - 1) {
                    for (j in l until n) {
                        s = 0.0
                        for (k in i until m) {
                            s += this[k, i] * this[k, j]
                        }
                        f = s / h
                        for (k in i until m) {
                            this[k, j] += f * this[k, i]
                        }
                    }
                }
                for (k in i until m) {
                    this[k, i] = this[k, i] * scale
                }
            }
        }

        w[i, 0] = scale * g
        /* right-hand reduction */
        g = 0.0
        s = 0.0
        scale = 0.0
        if (i < m && i != n - 1) {
            for (k in l until n) {
                scale += abs(this[i, k])
            }
            if (scale != 0.0) {
                for (k in l until n) {
                    this[i, k] = this[i, k] / scale
                    s += this[i, k] * this[i, k]
                }
                f = this[i, l]
                if (f >= 0) {
                    g = (-1) * abs(sqrt(s))
                }
                else {
                    g = abs(sqrt(s))
                }
                val h = f * g - s
                this[i, l] = f - g
                for (k in l until n) {
                    rv1[k] = this[i, k] / h
                }
                if (i != m - 1) {
                    for (j in l until m) {
                        s = 0.0
                        for (k in l until n) {
                            s += this[j, k] * this[i, k]
                        }
                        for (k in l until n) {
                            this[j, k] += s * rv1[k]
                        }
                    }
                }
                for (k in l until n) {
                    this[i, k] = this[i, k] * scale
                }
            }
        }
        anorm = max(anorm, (abs(w[i, 0]) + abs(rv1[i])));
    }

    for (i in n - 1 downTo 0) {
        if (i < n - 1) {
            if (g != 0.0) {
                for (j in l until n) {
                    v[j, i] = (this[i, j] / this[i, l]) / g
                }
                for (j in l until  n) {
                    s = 0.0
                    for (k in l until n)
                        s += this[i, k] * v[k, j]
                    for (k in l until n)
                        v[k, j] += s * v[k, i]
                }
            }
            for (j in l until n) {
                v[i, j] = 0.0
                v[j, i] = 0.0
            }
        }
        v[i, i] = 1.0
        g = rv1[i]
        l = i
    }

    // тут все правильно считается


//    println("w")
//    w.print()
//
    val eps = 0.000000001
//    println("1.0 / w[2, 0] " + 1.0 / w[2, 0])
//    println("w[2, 0] " + w[2, 0])

    for (i in min(n, m) - 1 downTo 0) {
        l = i + 1
        g = w[i, 0]
//        println("w[i, 0] " + w[i, 0])
        for (j in l  until n) {
            this[i, j] = 0.0
        }
        if (g != 0.0) {
            g = 1.0 / g
//            println("g " + g)
            for (j in l until n) {
                s = 0.0
                for (k in l until m) {
                    s += this[k, i] * this[k, j]
                }
                f = (s / this[i, i]) * g
                for (k in i until m) {
                    this[k, j] += f * this[k, i]
                }
            }
            for (j in i until m) {
                this[j, i] *= g
            }
        }
        else {
            for (j in i until m) {
                this[j, i] = 0.0
            }
        }
        this[i, i] += 1.0
//        println("matrix")
//        this.print()
    }

    println("matrix")
    this.print()

    // тут матрица должна выглядеть так:

//    0.134840   -0.762770    0.522117
//    -0.269680   -0.476731   -0.245388
//    -0.404520   -0.190693   -0.527383
//    -0.539360    0.095346   -0.297540
//    -0.674200    0.381385    0.548193



//    var flag = 0
//    var nm = 0
//    var c = 0.0
//    var h = 0.0
//    var y = 0.0
//    var z = 0.0
//    for (k in n - 1 downTo  0) {
//        for (its in 0 until 30) {
//            flag = 0
//            for (l in k downTo 0) {
//                nm = l - 1
//                if (abs(rv1[l]) < eps) {
//                    flag = 0
////                    println("break1")
//                    break
//                }
//                if (abs(w[nm, 0]) < eps) {
//                    println("break2")
//                    break
//                }
//            }
//
//            // l = 1 тут
//
//            if (flag != 0) {
//                c = 0.0
//                s = 0.0
//                for (i in l until k) { // а точно ли такие границы? там немного отличается
//                    f=s*rv1[i]
//                    rv1[i]=c*rv1[i]
//                    if (abs(f) < eps) {
//                        println("break3")
//                        break
//                    }
//                    g=w[i, 0]
//                    h=pythag(f,g)
//                    w[i, 0]=h
//                    h=1.0/h
//                    c=g*h
//                    s = -f*h
//                    for (j in 0 until m) { // точно ли такие границы?
//                        y=this[j, nm]
//                        z=this[j, i]
//                        this[j, nm]=y*c+z*s
//                        this[j, i]=z*c-y*s
//                    }
//                }
//            }
//
//
//        }
//    }
}