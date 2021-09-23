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
 * @version $Id$
 */
internal object MnParabolaFactory {
    fun create(p1: MnParabolaPoint, p2: MnParabolaPoint, p3: MnParabolaPoint): MnParabola {
        var x1: Double = p1.x()
        var x2: Double = p2.x()
        var x3: Double = p3.x()
        val dx12 = x1 - x2
        val dx13 = x1 - x3
        val dx23 = x2 - x3
        val xm = (x1 + x2 + x3) / 3.0
        x1 -= xm
        x2 -= xm
        x3 -= xm
        val y1: Double = p1.y()
        val y2: Double = p2.y()
        val y3: Double = p3.y()
        val a = y1 / (dx12 * dx13) - y2 / (dx12 * dx23) + y3 / (dx13 * dx23)
        var b = -y1 * (x2 + x3) / (dx12 * dx13) + y2 * (x1 + x3) / (dx12 * dx23) - y3 * (x1 + x2) / (dx13 * dx23)
        var c = y1 - a * x1 * x1 - b * x1
        c += xm * (xm * a - b)
        b -= 2.0 * xm * a
        return MnParabola(a, b, c)
    }

    fun create(p1: MnParabolaPoint, dxdy1: Double, p2: MnParabolaPoint): MnParabola {
        val x1: Double = p1.x()
        val xx1 = x1 * x1
        val x2: Double = p2.x()
        val xx2 = x2 * x2
        val y1: Double = p1.y()
        val y12: Double = p1.y() - p2.y()
        val det = xx1 - xx2 - 2.0 * x1 * (x1 - x2)
        val a = -(y12 + (x2 - x1) * dxdy1) / det
        val b = -(-2.0 * x1 * y12 + (xx1 - xx2) * dxdy1) / det
        val c = y1 - a * xx1 - b * x1
        return MnParabola(a, b, c)
    }
}