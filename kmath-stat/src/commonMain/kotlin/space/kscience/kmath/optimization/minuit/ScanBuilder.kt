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
import ru.inr.mass.minuit.*

/**
 * Performs a minimization using the simplex method of Nelder and Mead (ref.
 * Comp. J. 7, 308 (1965)).
 *
 * @version $Id$
 */
internal class ScanBuilder : MinimumBuilder {
    /** {@inheritDoc}  */
    fun minimum(
        mfcn: MnFcn,
        gc: GradientCalculator?,
        seed: MinimumSeed,
        stra: MnStrategy?,
        maxfcn: Int,
        toler: Double
    ): FunctionMinimum {
        val x: RealVector = seed.parameters().vec().copy()
        val upst = MnUserParameterState(seed.state(), mfcn.errorDef(), seed.trafo())
        val scan = MnParameterScan(mfcn.fcn(), upst.parameters(), seed.fval())
        var amin: Double = scan.fval()
        val n: Int = seed.trafo().variableParameters()
        val dirin: RealVector = ArrayRealVector(n)
        for (i in 0 until n) {
            val ext: Int = seed.trafo().extOfInt(i)
            scan.scan(ext)
            if (scan.fval() < amin) {
                amin = scan.fval()
                x.setEntry(i, seed.trafo().ext2int(ext, scan.parameters().value(ext)))
            }
            dirin.setEntry(i, sqrt(2.0 * mfcn.errorDef() * seed.error().invHessian()[i, i]))
        }
        val mp = MinimumParameters(x, dirin, amin)
        val st = MinimumState(mp, 0.0, mfcn.numOfCalls())
        val states: MutableList<MinimumState> = java.util.ArrayList<MinimumState>(1)
        states.add(st)
        return FunctionMinimum(seed, states, mfcn.errorDef())
    }
}