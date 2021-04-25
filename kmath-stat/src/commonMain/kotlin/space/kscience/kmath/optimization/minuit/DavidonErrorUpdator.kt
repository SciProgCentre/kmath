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

import org.apache.commons.math3.linear.RealVector
import ru.inr.mass.minuit.*

/**
 *
 * @version $Id$
 */
internal class DavidonErrorUpdator : MinimumErrorUpdator {
    /** {@inheritDoc}  */
    fun update(s0: MinimumState, p1: MinimumParameters, g1: FunctionGradient): MinimumError {
        val V0: MnAlgebraicSymMatrix = s0.error().invHessian()
        val dx: RealVector = MnUtils.sub(p1.vec(), s0.vec())
        val dg: RealVector = MnUtils.sub(g1.getGradient(), s0.gradient().getGradient())
        val delgam: Double = MnUtils.innerProduct(dx, dg)
        val gvg: Double = MnUtils.similarity(dg, V0)
        val vg: RealVector = MnUtils.mul(V0, dg)
        var Vupd: MnAlgebraicSymMatrix =
            MnUtils.sub(MnUtils.div(MnUtils.outerProduct(dx), delgam), MnUtils.div(MnUtils.outerProduct(vg), gvg))
        if (delgam > gvg) {
            Vupd = MnUtils.add(Vupd,
                MnUtils.mul(MnUtils.outerProduct(MnUtils.sub(MnUtils.div(dx, delgam), MnUtils.div(vg, gvg))), gvg))
        }
        val sum_upd: Double = MnUtils.absoluteSumOfElements(Vupd)
        Vupd = MnUtils.add(Vupd, V0)
        val dcov: Double = 0.5 * (s0.error().dcovar() + sum_upd / MnUtils.absoluteSumOfElements(Vupd))
        return MinimumError(Vupd, dcov)
    }
}