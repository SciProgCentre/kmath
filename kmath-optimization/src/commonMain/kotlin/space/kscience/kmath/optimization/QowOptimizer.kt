/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.optimization

import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.expressions.*
import space.kscience.kmath.linear.*
import space.kscience.kmath.misc.log
import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.operations.DoubleL2Norm
import space.kscience.kmath.operations.algebra
import space.kscience.kmath.structures.DoubleBuffer
import kotlin.math.abs


public class QowRuns(public val runs: Int) : OptimizationFeature {
    init {
        require(runs >= 1) { "Number of runs must be more than zero" }
    }

    override fun toString(): String = "QowRuns(runs=$runs)"
}


/**
 * An optimizer based onf Fyodor Tkachev's quasi-optimal weights method.
 * See [the article](http://arxiv.org/abs/physics/0604127).
 */
@UnstableKMathAPI
public object QowOptimizer : Optimizer<Double, XYFit> {

    private val linearSpace: LinearSpace<Double, DoubleField> = Double.algebra.linearSpace
    private val solver: LinearSolver<Double> = linearSpace.lupSolver()

    @OptIn(UnstableKMathAPI::class)
    private class QoWeight(
        val problem: XYFit,
        val freeParameters: Map<Symbol, Double>,
    ) : SymbolIndexer {
        val size get() = freeParameters.size

        override val symbols: List<Symbol> = freeParameters.keys.toList()

        val data get() = problem.data

        val allParameters by lazy {
            problem.startPoint + freeParameters
        }

        /**
         * Derivatives of the spectrum over parameters. First index in the point number, second one - index of parameter
         */
        val derivs: Matrix<Double> by lazy {
            linearSpace.buildMatrix(problem.data.size, symbols.size) { d, s ->
                problem.distance(d).derivative(symbols[s]).invoke(allParameters)
            }
        }

        /**
         * Array of dispersions in each point
         */
        val dispersion: Point<Double> by lazy {
            DoubleBuffer(problem.data.size) { d ->
                1.0 / problem.weight(d).invoke(allParameters)
            }
        }

        val prior: DifferentiableExpression<Double>?
            get() = problem.getFeature<OptimizationPrior<Double>>()?.withDefaultArgs(allParameters)

        override fun toString(): String = freeParameters.toString()
    }

    /**
     * The signed distance from the model to the [d]-th point of data.
     */
    private fun QoWeight.distance(d: Int, parameters: Map<Symbol, Double>): Double =
        problem.distance(d)(allParameters + parameters)


    /**
     * The derivative of [distance]
     */
    private fun QoWeight.distanceDerivative(symbol: Symbol, d: Int, parameters: Map<Symbol, Double>): Double =
        problem.distance(d).derivative(symbol).invoke(allParameters + parameters)

    /**
     * Theoretical covariance of weight functions
     *
     * D(\phi)=E(\phi_k(\theta_0) \phi_l(\theta_0))= disDeriv_k * disDeriv_l /sigma^2
     */
    private fun QoWeight.covarF(): Matrix<Double> =
        linearSpace.matrix(size, size).symmetric { s1, s2 ->
            (0 until data.size).sumOf { d -> derivs[d, s1] * derivs[d, s2] / dispersion[d] }
        }

    /**
     * Experimental covariance Eq (22) from
     * http://arxiv.org/abs/physics/0604127
     */
    private fun QoWeight.covarFExp(theta: Map<Symbol, Double>): Matrix<Double> =
        with(linearSpace) {
            /*
             * Важно! Если не делать предварителього вычисления этих производных, то
             * количество вызывов функции будет dim^2 вместо dim Первый индекс -
             * номер точки, второй - номер переменной, по которой берется производная
             */
            val eqvalues = linearSpace.buildMatrix(data.size, size) { d, s ->
                distance(d, theta) * derivs[d, s] / dispersion[d]
            }

            buildMatrix(size, size) { s1, s2 ->
                (0 until data.size).sumOf { d -> eqvalues[d, s2] * eqvalues[d, s1] }
            }
        }

    /**
     * Equation derivatives for Newton run
     */
    private fun QoWeight.getEqDerivValues(
        theta: Map<Symbol, Double> = freeParameters,
    ): Matrix<Double> = with(linearSpace) {
        //Derivative of k Eq over l parameter
        val sderiv = buildMatrix(data.size, size) { d, s ->
            distanceDerivative(symbols[s], d, theta)
        }

        buildMatrix(size, size) { s1, s2 ->
            val base = (0 until data.size).sumOf { d ->
                require(dispersion[d] > 0)
                sderiv[d, s2] * derivs[d, s1] / dispersion[d]
            }
            prior?.let { prior ->
                //Check if this one is correct
                val pi = prior(theta)
                val deriv1 = prior.derivative(symbols[s1])(theta)
                val deriv2 = prior.derivative(symbols[s2])(theta)
                base + deriv1 * deriv2 / pi / pi
            } ?: base
        }
    }


    /**
     * Quasi optimal weights equations values
     */
    private fun QoWeight.getEqValues(theta: Map<Symbol, Double>): Point<Double> {
        val distances = DoubleBuffer(data.size) { d -> distance(d, theta) }
        return DoubleBuffer(size) { s ->
            val base = (0 until data.size).sumOf { d -> distances[d] * derivs[d, s] / dispersion[d] }
            //Prior probability correction
            prior?.let { prior ->
                base - prior.derivative(symbols[s]).invoke(theta) / prior(theta)
            } ?: base
        }
    }


    private fun QoWeight.newtonianStep(
        theta: Map<Symbol, Double>,
        eqValues: Point<Double>,
    ): QoWeight = linearSpace {
        val start = theta.toPoint()
        val invJacob = solver.inverse(getEqDerivValues(theta))

        val step = invJacob.dot(eqValues)
        return QoWeight(problem, theta + (start - step).toMap())
    }

    private fun QoWeight.newtonianRun(
        maxSteps: Int = 100,
        tolerance: Double = 0.0,
        fast: Boolean = false,
    ): QoWeight {

        val logger = problem.getFeature<OptimizationLog>()

        var dis: Double //discrepancy value

        var par = freeParameters

        logger?.log { "Starting newtonian iteration from: \n\t$allParameters" }

        var eqvalues = getEqValues(par) //Values of the weight functions

        dis = DoubleL2Norm.norm(eqvalues) // discrepancy
        logger?.log { "Starting discrepancy is $dis" }
        var i = 0
        var flag = false
        while (!flag) {
            i++
            logger?.log { "Starting step number $i" }

            val currentSolution = if (fast) {
                //Matrix values in the point of weight computation
                newtonianStep(freeParameters, eqvalues)
            } else {
                //Matrix values in a current point
                newtonianStep(par, eqvalues)
            }
            // здесь должен стоять учет границ параметров
            logger?.log { "Parameter values after step are: \n\t$currentSolution" }

            eqvalues = getEqValues(currentSolution.freeParameters)
            val currentDis = DoubleL2Norm.norm(eqvalues)// discrepancy after the step

            logger?.log { "The discrepancy after step is: $currentDis." }

            if (currentDis >= dis && i > 1) {
                //Check if one step is made
                flag = true
                logger?.log { "The discrepancy does not decrease. Stopping iteration." }
            } else if (abs(dis - currentDis) <= tolerance) {
                flag = true
                par = currentSolution.freeParameters
                logger?.log { "Relative discrepancy tolerance threshold is reached. Stopping iteration." }
            } else {
                par = currentSolution.freeParameters
                dis = currentDis
            }
            if (i >= maxSteps) {
                flag = true
                logger?.log { "Maximum number of iterations reached. Stopping iteration." }
            }
        }

        return QoWeight(problem, par)
    }

    private fun QoWeight.covariance(): NamedMatrix<Double> {
        val logger = problem.getFeature<OptimizationLog>()

        logger?.log {
            """
            Starting errors estimation using quasi-optimal weights method. The starting weight is:
                $allParameters
             """.trimIndent()
        }

        val covar = solver.inverse(getEqDerivValues())
        //TODO fix eigenvalues check
//        val decomposition = EigenDecomposition(covar.matrix)
//        var valid = true
//        for (lambda in decomposition.realEigenvalues) {
//            if (lambda <= 0) {
//                logger?.log { "The covariance matrix is not positive defined. Error estimation is not valid" }
//                valid = false
//            }
//        }
        logger?.log {
            "Covariance matrix:" + "\n" + NamedMatrix.toStringWithSymbols(covar, this)
        }
        return covar.named(symbols)
    }

    override suspend fun optimize(problem: XYFit): XYFit {
        val qowRuns = problem.getFeature<QowRuns>()?.runs ?: 2
        val iterations = problem.getFeature<OptimizationIterations>()?.maxIterations ?: 50

        val freeParameters: Map<Symbol, Double> = problem.getFeature<OptimizationParameters>()?.let { op ->
            problem.startPoint.filterKeys { it in op.symbols }
        } ?: problem.startPoint

        var qow = QoWeight(problem, freeParameters)
        var res = qow.newtonianRun(maxSteps = iterations)
        repeat(qowRuns - 1) {
            qow = QoWeight(problem, res.freeParameters)
            res = qow.newtonianRun(maxSteps = iterations)
        }
        val covariance = res.covariance()
        return res.problem.withFeature(OptimizationResult(res.freeParameters), OptimizationCovariance(covariance))
    }
}