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

/**
 *
 * @version $Id$
 */
internal object MnPosDef {
    fun test(st: MinimumState, prec: MnMachinePrecision): MinimumState {
        val err: MinimumError = test(st.error(), prec)
        return MinimumState(st.parameters(), err, st.gradient(), st.edm(), st.nfcn())
    }

    fun test(e: MinimumError, prec: MnMachinePrecision): MinimumError {
        val err: MnAlgebraicSymMatrix = e.invHessian().copy()
        if (err.size() === 1 && err[0, 0] < prec.eps()) {
            err[0, 0] = 1.0
            return MinimumError(err, MnMadePosDef())
        }
        if (err.size() === 1 && err[0, 0] > prec.eps()) {
            return e
        }
        //   standardDiviation::cout<<"MnPosDef init matrix= "<<err<<standardDiviation::endl;
        val epspdf: Double = max(1e-6, prec.eps2())
        var dgmin: Double = err[0, 0]
        for (i in 0 until err.nrow()) {
            if (err[i, i] < prec.eps2()) {
                MINUITPlugin.logStatic("negative or zero diagonal element $i in covariance matrix")
            }
            if (err[i, i] < dgmin) {
                dgmin = err[i, i]
            }
        }
        var dg = 0.0
        if (dgmin < prec.eps2()) {
            dg = 1.0 + epspdf - dgmin
            //     dg = 0.5*(1. + epspdf - dgmin);
            MINUITPlugin.logStatic("added $dg to diagonal of error matrix")
        }
        val s: RealVector = ArrayRealVector(err.nrow())
        val p = MnAlgebraicSymMatrix(err.nrow())
        for (i in 0 until err.nrow()) {
            err[i, i] = err[i, i] + dg
            if (err[i, i] < 0.0) {
                err[i, i] = 1.0
            }
            s.setEntry(i, 1.0 / sqrt(err[i, i]))
            for (j in 0..i) {
                p[i, j] = err[i, j] * s.getEntry(i) * s.getEntry(j)
            }
        }

        //   standardDiviation::cout<<"MnPosDef p: "<<p<<standardDiviation::endl;
        val eval: RealVector = p.eigenvalues()
        val pmin: Double = eval.getEntry(0)
        var pmax: Double = eval.getEntry(eval.getDimension() - 1)
        //   standardDiviation::cout<<"pmin= "<<pmin<<" pmax= "<<pmax<<standardDiviation::endl;
        pmax = max(abs(pmax), 1.0)
        if (pmin > epspdf * pmax) {
            return e
        }
        val padd = 0.001 * pmax - pmin
        MINUITPlugin.logStatic("eigenvalues: ")
        for (i in 0 until err.nrow()) {
            err[i, i] = err[i, i] * (1.0 + padd)
            MINUITPlugin.logStatic(java.lang.Double.toString(eval.getEntry(i)))
        }
        //   standardDiviation::cout<<"MnPosDef final matrix: "<<err<<standardDiviation::endl;
        MINUITPlugin.logStatic("matrix forced pos-def by adding $padd to diagonal")
        //   standardDiviation::cout<<"eigenvalues: "<<eval<<standardDiviation::endl;
        return MinimumError(err, MnMadePosDef())
    }
}