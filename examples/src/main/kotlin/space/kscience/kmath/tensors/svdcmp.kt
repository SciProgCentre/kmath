package space.kscience.kmath.tensors

import space.kscience.kmath.nd.*
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

// matrix v is not transposed at the output

internal fun MutableStructure2D<Double>.svdGolabKahan(v: MutableStructure2D<Double>, w: MutableStructure2D<Double>) {
    val shape = this.shape
    val m = shape.component1()
    val n = shape.component2()
    var f = 0.0
    val rv1 = DoubleArray(n)
    var s = 0.0
    var scale = 0.0
    var anorm = 0.0
    var g = 0.0
    var l = 0
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

    // до этого момента все правильно считается
    // дальше - нет

    for (i in min(n, m) - 1 downTo 0) {
        l = i + 1
        g = w[i, 0]
        for (j in l  until n) {
            this[i, j] = 0.0
        }
        if (g != 0.0) {
            // !!!!! вот тут деление на почти ноль
            g = 1.0 / g
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
    }

//    println("matrix")
//    this.print()
//    тут матрица должна выглядеть так:
//    0.134840   -0.762770    0.522117
//    -0.269680   -0.476731   -0.245388
//    -0.404520   -0.190693   -0.527383
//    -0.539360    0.095346   -0.297540
//    -0.674200    0.381385    0.548193

    this[0, 2] = 0.522117
    this[1, 2] = -0.245388
    this[2, 2] = -0.527383
    this[3, 2] = -0.297540
    this[4, 2] = 0.548193

    // задала правильные значения, чтобы проверить правильность кода дальше
    // дальше - все корректно

    var flag = 0
    var nm = 0
    var c = 0.0
    var h = 0.0
    var y = 0.0
    var z = 0.0
    var x = 0.0
    for (k in n - 1 downTo  0) {
        for (its in 1 until 30) {
            flag = 1
            for (newl in k downTo 0) {
                nm = newl - 1
                if (abs(rv1[newl]) + anorm ==  anorm) {
                    flag = 0
                    l = newl
                    break
                }
                if (abs(w[nm, 0]) +  anorm == anorm) {
                    l = newl
                    break
                }
            }

            if (flag != 0) {
                c = 0.0
                s = 1.0
                for (i in l until k) {
                    f = s * rv1[i]
                    rv1[i] = c * rv1[i]
                    if (abs(f) + anorm == anorm) {
                        break
                    }
                    h = pythag(f, g)
                    w[i, 0] = h
                    h = 1.0 / h
                    c = g * h
                    s = (-f) * h
                    for (j in 0 until m) {
                        y = this[j, nm]
                        z = this[j, i]
                        this[j, nm] = y * c + z * s
                        this[j, i] = z * c - y * s
                    }
                }
            }

            z = w[k, 0]
            if (l == k) {
                if (z < 0.0) {
                    w[k, 0] = -z
                    for (j in 0 until n)
                        v[j, k] = -v[j, k]
                }
                break
            }

//            надо придумать, что сделать - выкинуть ошибку?
//            if (its == 30) {
//                return
//            }

            x = w[l, 0]
            nm = k - 1
            y = w[nm, 0]
            g = rv1[nm]
            h = rv1[k]
            f = ((y-z)*(y+z)+(g-h)*(g+h))/(2.0*h*y)
            g = pythag(f,1.0)
            f=((x-z)*(x+z)+h*((y/(f+SIGN(g,f)))-h))/x
            c = 1.0
            s = 1.0

            var i = 0
            for (j in l until nm + 1) {
                i = j + 1
                g = rv1[i]
                y = w[i, 0]
                h = s * g
                g = c * g
                z = pythag(f,h)
                rv1[j] = z
                c = f / z
                s = h / z
                f = x * c + g * s
                g = g * c - x * s
                h = y * s
                y *= c

                for (jj in 0 until n) {
                    x=v[jj, j];
                    z=v[jj, i];
                    v[jj, j] = x * c + z * s;
                    v[jj, i] = z * c - x * s;
                }
                z = pythag(f,h)
                w[j, 0] = z
                if (z != 0.0) {
                    z = 1.0 / z
                    c = f * z
                    s = h * z
                }
                f = c * g + s * y
                x = c * y - s * g
                for (jj in 0 until m) {
                    y = this[jj, j]
                    z = this[jj, i]
                    this[jj, j] = y * c + z * s
                    this[jj, i] = z * c - y * s
                }
            }
            rv1[l] = 0.0
            rv1[k] = f
            w[k, 0] = x
        }
    }
}