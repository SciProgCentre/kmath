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

import space.kscience.kmath.optimization.minuit.MINUITPlugin
import ru.inr.mass.minuit.*
import space.kscience.kmath.optimization.minuit.MinimumSeed

/**
 *
 * @version $Id$
 */
internal class SimplexBuilder : MinimumBuilder {
    /** {@inheritDoc}  */
    fun minimum(
        mfcn: MnFcn,
        gc: GradientCalculator?,
        seed: MinimumSeed,
        strategy: MnStrategy?,
        maxfcn: Int,
        minedm: Double,
    ): FunctionMinimum {
        val prec: MnMachinePrecision = seed.precision()
        val x: RealVector = seed.parameters().vec().copy()
        val step: RealVector = MnUtils.mul(seed.gradient().getStep(), 10.0)
        val n: Int = x.getDimension()
        val wg = 1.0 / n
        val alpha = 1.0
        val beta = 0.5
        val gamma = 2.0
        val rhomin = 4.0
        val rhomax = 8.0
        val rho1 = 1.0 + alpha
        val rho2 = 1.0 + alpha * gamma
        val simpl: MutableList<Pair<Double?, RealVector?>> = java.util.ArrayList<Pair<Double, RealVector>>(n + 1)
        simpl.add(Pair(seed.fval(), x.copy()))
        var jl = 0
        var jh = 0
        var amin: Double = seed.fval()
        var aming: Double = seed.fval()
        for (i in 0 until n) {
            val dmin: Double = 8.0 * prec.eps2() * (abs(x.getEntry(i)) + prec.eps2())
            if (step.getEntry(i) < dmin) {
                step.setEntry(i, dmin)
            }
            x.setEntry(i, x.getEntry(i) + step.getEntry(i))
            val tmp: Double = mfcn.value(x)
            if (tmp < amin) {
                amin = tmp
                jl = i + 1
            }
            if (tmp > aming) {
                aming = tmp
                jh = i + 1
            }
            simpl.add(Pair(tmp, x.copy()))
            x.setEntry(i, x.getEntry(i) - step.getEntry(i))
        }
        val simplex = SimplexParameters(simpl, jh, jl)
        do {
            amin = simplex[jl].getFirst()
            jl = simplex.jl()
            jh = simplex.jh()
            var pbar: RealVector = ArrayRealVector(n)
            for (i in 0 until n + 1) {
                if (i == jh) {
                    continue
                }
                pbar = MnUtils.add(pbar, MnUtils.mul(simplex[i].getSecond(), wg))
            }
            val pstar: RealVector =
                MnUtils.sub(MnUtils.mul(pbar, 1.0 + alpha), MnUtils.mul(simplex[jh].getSecond(), alpha))
            val ystar: Double = mfcn.value(pstar)
            if (ystar > amin) {
                if (ystar < simplex[jh].getFirst()) {
                    simplex.update(ystar, pstar)
                    if (jh != simplex.jh()) {
                        continue
                    }
                }
                val pstst: RealVector =
                    MnUtils.add(MnUtils.mul(simplex[jh].getSecond(), beta), MnUtils.mul(pbar, 1.0 - beta))
                val ystst: Double = mfcn.value(pstst)
                if (ystst > simplex[jh].getFirst()) {
                    break
                }
                simplex.update(ystst, pstst)
                continue
            }
            var pstst: RealVector = MnUtils.add(MnUtils.mul(pstar, gamma), MnUtils.mul(pbar, 1.0 - gamma))
            var ystst: Double = mfcn.value(pstst)
            val y1: Double = (ystar - simplex[jh].getFirst()) * rho2
            val y2: Double = (ystst - simplex[jh].getFirst()) * rho1
            var rho = 0.5 * (rho2 * y1 - rho1 * y2) / (y1 - y2)
            if (rho < rhomin) {
                if (ystst < simplex[jl].getFirst()) {
                    simplex.update(ystst, pstst)
                } else {
                    simplex.update(ystar, pstar)
                }
                continue
            }
            if (rho > rhomax) {
                rho = rhomax
            }
            val prho: RealVector =
                MnUtils.add(MnUtils.mul(pbar, rho), MnUtils.mul(simplex[jh].getSecond(), 1.0 - rho))
            val yrho: Double = mfcn.value(prho)
            if (yrho < simplex[jl].getFirst() && yrho < ystst) {
                simplex.update(yrho, prho)
                continue
            }
            if (ystst < simplex[jl].getFirst()) {
                simplex.update(ystst, pstst)
                continue
            }
            if (yrho > simplex[jl].getFirst()) {
                if (ystst < simplex[jl].getFirst()) {
                    simplex.update(ystst, pstst)
                } else {
                    simplex.update(ystar, pstar)
                }
                continue
            }
            if (ystar > simplex[jh].getFirst()) {
                pstst = MnUtils.add(MnUtils.mul(simplex[jh].getSecond(), beta), MnUtils.mul(pbar, 1 - beta))
                ystst = mfcn.value(pstst)
                if (ystst > simplex[jh].getFirst()) {
                    break
                }
                simplex.update(ystst, pstst)
            }
        } while (simplex.edm() > minedm && mfcn.numOfCalls() < maxfcn)
        amin = simplex[jl].getFirst()
        jl = simplex.jl()
        jh = simplex.jh()
        var pbar: RealVector = ArrayRealVector(n)
        for (i in 0 until n + 1) {
            if (i == jh) {
                continue
            }
            pbar = MnUtils.add(pbar, MnUtils.mul(simplex[i].getSecond(), wg))
        }
        var ybar: Double = mfcn.value(pbar)
        if (ybar < amin) {
            simplex.update(ybar, pbar)
        } else {
            pbar = simplex[jl].getSecond()
            ybar = simplex[jl].getFirst()
        }
        var dirin: RealVector = simplex.dirin()
        //   scale to sigmas on parameters werr^2 = dirin^2 * (up/edm)
        dirin = MnUtils.mul(dirin, sqrt(mfcn.errorDef() / simplex.edm()))
        val st = MinimumState(MinimumParameters(pbar, dirin, ybar), simplex.edm(), mfcn.numOfCalls())
        val states: MutableList<MinimumState> = java.util.ArrayList<MinimumState>(1)
        states.add(st)
        if (mfcn.numOfCalls() > maxfcn) {
            MINUITPlugin.logStatic("Simplex did not converge, #fcn calls exhausted.")
            return FunctionMinimum(seed, states, mfcn.errorDef(), MnReachedCallLimit())
        }
        if (simplex.edm() > minedm) {
            MINUITPlugin.logStatic("Simplex did not converge, edm > minedm.")
            return FunctionMinimum(seed, states, mfcn.errorDef(), MnAboveMaxEdm())
        }
        return FunctionMinimum(seed, states, mfcn.errorDef())
    }
}