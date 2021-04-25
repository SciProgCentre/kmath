/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */
package space.kscience.kmath.optimization.minuit

import hep.dataforge.MINUIT.FunctionMinimum

internal object MINUITUtils {
    fun getFcn(source: FitState, allPar: ParamSet, fitPars: Array<String>): MultiFunction {
        return MnFunc(source, allPar, fitPars)
    }

    fun getFitParameters(set: ParamSet, fitPars: Array<String>): MnUserParameters {
        val pars = MnUserParameters()
        var i: Int
        var par: Param
        i = 0
        while (i < fitPars.size) {
            par = set.getByName(fitPars[i])
            pars.add(fitPars[i], par.getValue(), par.getErr())
            if (par.getLowerBound() > Double.NEGATIVE_INFINITY && par.getUpperBound() < Double.POSITIVE_INFINITY) {
                pars.setLimits(i, par.getLowerBound(), par.getUpperBound())
            } else if (par.getLowerBound() > Double.NEGATIVE_INFINITY) {
                pars.setLowerLimit(i, par.getLowerBound())
            } else if (par.getUpperBound() < Double.POSITIVE_INFINITY) {
                pars.setUpperLimit(i, par.getUpperBound())
            }
            i++
        }
        return pars
    }

    fun getValueSet(allPar: ParamSet, names: Array<String>, values: DoubleArray): ParamSet {
        assert(values.size == names.size)
        assert(allPar.getNames().contains(names))
        val vector: ParamSet = allPar.copy()
        for (i in values.indices) {
            vector.setParValue(names[i], values[i])
        }
        return vector
    }

    fun isValidArray(ar: DoubleArray): Boolean {
        for (i in ar.indices) {
            if (java.lang.Double.isNaN(ar[i])) {
                return false
            }
        }
        return true
    }

    /**
     *
     *
     * printMINUITResult.
     *
     * @param out a [PrintWriter] object.
     * @param minimum a [hep.dataforge.MINUIT.FunctionMinimum] object.
     */
    fun printMINUITResult(out: PrintWriter, minimum: FunctionMinimum?) {
        out.println()
        out.println("***MINUIT INTERNAL FIT INFORMATION***")
        out.println()
        MnPrint.print(out, minimum)
        out.println()
        out.println("***END OF MINUIT INTERNAL FIT INFORMATION***")
        out.println()
    }

    internal class MnFunc(source: FitState, allPar: ParamSet, fitPars: Array<String>) : MultiFunction {
        var source: FitState
        var allPar: ParamSet
        var fitPars: Array<String>
        fun value(doubles: DoubleArray): Double {
            assert(isValidArray(doubles))
            assert(doubles.size == fitPars.size)
            return -2 * source.getLogProb(getValueSet(allPar, fitPars, doubles))
            //                    source.getChi2(getValueSet(allPar, fitPars, doubles));
        }

        @Throws(NotDefinedException::class)
        fun derivValue(n: Int, doubles: DoubleArray): Double {
            assert(isValidArray(doubles))
            assert(doubles.size == getDimension())
            val set: ParamSet = getValueSet(allPar, fitPars, doubles)

//            double res;
//            double d, s, deriv;
//
//            res = 0;
//            for (int i = 0; i < source.getDataNum(); i++) {
//                d = source.getDis(i, set);
//                s = source.getDispersion(i, set);
//                if (source.modelProvidesDerivs(fitPars[n])) {
//                    deriv = source.getDisDeriv(fitPars[n], i, set);
//                } else {
//                    throw new NotDefinedException();
//                    // Такого не должно быть, поскольку мы где-то наверху должы были проверить, что производные все есть.
//                }
//                res += 2 * d * deriv / s;
//            }
            return -2 * source.getLogProbDeriv(fitPars[n], set)
        }

        fun getDimension(): Int {
            return fitPars.size
        }

        fun providesDeriv(n: Int): Boolean {
            return source.modelProvidesDerivs(fitPars[n])
        }

        init {
            this.source = source
            this.allPar = allPar
            this.fitPars = fitPars
            assert(source.getModel().getNames().contains(fitPars))
        }
    }
}