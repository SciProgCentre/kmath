/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */
package space.kscience.kmath.optimization.minuit

import ru.inr.mass.minuit.*

/**
 *
 *
 * MINUITFitter class.
 *
 * @author Darksnake
 * @version $Id: $Id
 */
class MINUITFitter : Fitter {
    fun run(state: FitState, parentLog: History?, meta: Meta): FitResult {
        val log = Chronicle("MINUIT", parentLog)
        val action: String = meta.getString("action", TASK_RUN)
        log.report("MINUIT fit engine started action '{}'", action)
        return when (action) {
            TASK_COVARIANCE -> runHesse(state, log, meta)
            TASK_SINGLE, TASK_RUN -> runFit(state, log, meta)
            else -> throw IllegalArgumentException("Unknown task")
        }
    }

    @NotNull
    fun getName(): String {
        return MINUIT_ENGINE_NAME
    }

    /**
     *
     *
     * runHesse.
     *
     * @param state a [hep.dataforge.stat.fit.FitState] object.
     * @param log
     * @return a [FitResult] object.
     */
    fun runHesse(state: FitState, log: History, meta: Meta?): FitResult {
        val strategy: Int
        strategy = Global.INSTANCE.getInt("MINUIT_STRATEGY", 2)
        log.report("Generating errors using MnHesse 2-nd order gradient calculator.")
        val fcn: MultiFunction
        val fitPars: Array<String> = Fitter.Companion.getFitPars(state, meta)
        val pars: ParamSet = state.getParameters()
        fcn = MINUITUtils.getFcn(state, pars, fitPars)
        val hesse = MnHesse(strategy)
        val mnState: MnUserParameterState = hesse.calculate(fcn, MINUITUtils.getFitParameters(pars, fitPars))
        val allPars: ParamSet = pars.copy()
        for (fitPar in fitPars) {
            allPars.setParValue(fitPar, mnState.value(fitPar))
            allPars.setParError(fitPar, mnState.error(fitPar))
        }
        val newState: FitState.Builder = state.edit()
        newState.setPars(allPars)
        if (mnState.hasCovariance()) {
            val mnCov: MnUserCovariance = mnState.covariance()
            var j: Int
            val cov = Array(mnState.variableParameters()) { DoubleArray(mnState.variableParameters()) }
            for (i in 0 until mnState.variableParameters()) {
                j = 0
                while (j < mnState.variableParameters()) {
                    cov[i][j] = mnCov.get(i, j)
                    j++
                }
            }
            newState.setCovariance(NamedMatrix(fitPars, cov), true)
        }
        return FitResult.build(newState.build(), fitPars)
    }

    fun runFit(state: FitState, log: History, meta: Meta): FitResult {
        val minuit: MnApplication
        log.report("Starting fit using Minuit.")
        val strategy: Int
        strategy = Global.INSTANCE.getInt("MINUIT_STRATEGY", 2)
        var force: Boolean
        force = Global.INSTANCE.getBoolean("FORCE_DERIVS", false)
        val fitPars: Array<String> = Fitter.Companion.getFitPars(state, meta)
        for (fitPar in fitPars) {
            if (!state.modelProvidesDerivs(fitPar)) {
                force = true
                log.reportError("Model does not provide derivatives for parameter '{}'", fitPar)
            }
        }
        if (force) {
            log.report("Using MINUIT gradient calculator.")
        }
        val fcn: MultiFunction
        val pars: ParamSet = state.getParameters().copy()
        fcn = MINUITUtils.getFcn(state, pars, fitPars)
        val method: String = meta.getString("method", MINUIT_MIGRAD)
        when (method) {
            MINUIT_MINOS, MINUIT_MINIMIZE -> minuit =
                MnMinimize(fcn, MINUITUtils.getFitParameters(pars, fitPars), strategy)
            MINUIT_SIMPLEX -> minuit = MnSimplex(fcn, MINUITUtils.getFitParameters(pars, fitPars), strategy)
            else -> minuit = MnMigrad(fcn, MINUITUtils.getFitParameters(pars, fitPars), strategy)
        }
        if (force) {
            minuit.setUseAnalyticalDerivatives(false)
            log.report("Forced to use MINUIT internal derivative calculator!")
        }

//        minuit.setUseAnalyticalDerivatives(true);
        val minimum: FunctionMinimum
        val maxSteps: Int = meta.getInt("iterations", -1)
        val tolerance: Double = meta.getDouble("tolerance", -1)
        minimum = if (maxSteps > 0) {
            if (tolerance > 0) {
                minuit.minimize(maxSteps, tolerance)
            } else {
                minuit.minimize(maxSteps)
            }
        } else {
            minuit.minimize()
        }
        if (!minimum.isValid()) {
            log.report("Minimization failed!")
        }
        log.report("MINUIT run completed in {} function calls.", minimum.nfcn())

        /*
         * Генерация результата
         */
        val allPars: ParamSet = pars.copy()
        for (fitPar in fitPars) {
            allPars.setParValue(fitPar, minimum.userParameters().value(fitPar))
            allPars.setParError(fitPar, minimum.userParameters().error(fitPar))
        }
        val newState: FitState.Builder = state.edit()
        newState.setPars(allPars)
        var valid: Boolean = minimum.isValid()
        if (minimum.userCovariance().nrow() > 0) {
            var j: Int
            val cov = Array(minuit.variableParameters()) { DoubleArray(minuit.variableParameters()) }
            if (cov[0].length == 1) {
                cov[0][0] = minimum.userParameters().error(0) * minimum.userParameters().error(0)
            } else {
                for (i in 0 until minuit.variableParameters()) {
                    j = 0
                    while (j < minuit.variableParameters()) {
                        cov[i][j] = minimum.userCovariance().get(i, j)
                        j++
                    }
                }
            }
            newState.setCovariance(NamedMatrix(fitPars, cov), true)
        }
        if (method == MINUIT_MINOS) {
            log.report("Starting MINOS procedure for precise error estimation.")
            val minos = MnMinos(fcn, minimum, strategy)
            var mnError: MinosError
            val errl = DoubleArray(fitPars.size)
            val errp = DoubleArray(fitPars.size)
            for (i in fitPars.indices) {
                mnError = minos.minos(i)
                if (mnError.isValid()) {
                    errl[i] = mnError.lower()
                    errp[i] = mnError.upper()
                } else {
                    valid = false
                }
            }
            val minosErrors = MINOSResult(fitPars, errl, errp)
            newState.setInterval(minosErrors)
        }
        return FitResult.build(newState.build(), valid, fitPars)
    }

    companion object {
        /**
         * Constant `MINUIT_MIGRAD="MIGRAD"`
         */
        const val MINUIT_MIGRAD = "MIGRAD"

        /**
         * Constant `MINUIT_MINIMIZE="MINIMIZE"`
         */
        const val MINUIT_MINIMIZE = "MINIMIZE"

        /**
         * Constant `MINUIT_SIMPLEX="SIMPLEX"`
         */
        const val MINUIT_SIMPLEX = "SIMPLEX"

        /**
         * Constant `MINUIT_MINOS="MINOS"`
         */
        const val MINUIT_MINOS = "MINOS" //MINOS errors

        /**
         * Constant `MINUIT_HESSE="HESSE"`
         */
        const val MINUIT_HESSE = "HESSE" //HESSE errors

        /**
         * Constant `MINUIT_ENGINE_NAME="MINUIT"`
         */
        const val MINUIT_ENGINE_NAME = "MINUIT"
    }
}