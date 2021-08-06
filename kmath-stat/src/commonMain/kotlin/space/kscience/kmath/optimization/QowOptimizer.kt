/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.optimization

import space.kscience.kmath.expressions.DifferentiableExpression
import space.kscience.kmath.expressions.Symbol
import space.kscience.kmath.expressions.SymbolIndexer
import space.kscience.kmath.expressions.derivative
import space.kscience.kmath.linear.*
import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.structures.DoubleBuffer
import space.kscience.kmath.structures.DoubleL2Norm


/**
 * An optimizer based onf Fyodor Tkachev's quasi-optimal weights method.
 * See [the article](http://arxiv.org/abs/physics/0604127).
 */
@UnstableKMathAPI
public class QowOptimizer : Optimizer<Double, XYOptimization> {

    private val linearSpace: LinearSpace<Double, DoubleField> = LinearSpace.double
    private val solver: LinearSolver<Double> = linearSpace.lupSolver()

    @OptIn(UnstableKMathAPI::class)
    private inner class QoWeight(
        val problem: XYOptimization,
        val parameters: Map<Symbol, Double>,
    ) : Map<Symbol, Double> by parameters, SymbolIndexer {
        override val symbols: List<Symbol> = parameters.keys.toList()

        val data get() = problem.data

        /**
         * Derivatives of the spectrum over parameters. First index in the point number, second one - index of parameter
         */
        val derivs: Matrix<Double> by lazy {
            linearSpace.buildMatrix(problem.data.size, symbols.size) { i, k ->
                problem.distance(i).derivative(symbols[k])(parameters)
            }
        }

        /**
         * Array of dispersions in each point
         */
        val dispersion: Point<Double> by lazy {
            DoubleBuffer(problem.data.size) { i ->
                problem.weight(i).invoke(parameters)
            }
        }

        val prior: DifferentiableExpression<Double>? get() = problem.getFeature<OptimizationPrior<Double>>()
    }

    /**
     * The signed distance from the model to the [i]-th point of data.
     */
    private fun QoWeight.distance(i: Int, parameters: Map<Symbol, Double>): Double = problem.distance(i)(parameters)


    /**
     * The derivative of [distance]
     */
    private fun QoWeight.distanceDerivative(symbol: Symbol, i: Int, parameters: Map<Symbol, Double>): Double =
        problem.distance(i).derivative(symbol)(parameters)

    /**
     * Теоретическая ковариация весовых функций.
     *
     * D(\phi)=E(\phi_k(\theta_0) \phi_l(\theta_0))= disDeriv_k * disDeriv_l /sigma^2
     */
    private fun QoWeight.covarF(): Matrix<Double> =
        linearSpace.matrix(size, size).symmetric { k, l ->
            (0 until data.size).sumOf { i -> derivs[k, i] * derivs[l, i] / dispersion[i] }
        }

    /**
     * Экспериментальная ковариация весов. Формула (22) из
     * http://arxiv.org/abs/physics/0604127
     */
    private fun QoWeight.covarFExp(theta: Map<Symbol, Double>): Matrix<Double> =
        with(linearSpace) {
            /*
             * Важно! Если не делать предварителього вычисления этих производных, то
             * количество вызывов функции будет dim^2 вместо dim Первый индекс -
             * номер точки, второй - номер переменной, по которой берется производная
             */
            val eqvalues = linearSpace.buildMatrix(data.size, size) { i, l ->
                distance(i, theta) * derivs[l, i] / dispersion[i]
            }

            buildMatrix(size, size) { k, l ->
                (0 until data.size).sumOf { i -> eqvalues[i, l] * eqvalues[i, k] }
            }
        }

    /**
     * Equation derivatives for Newton run
     */
    private fun QoWeight.getEqDerivValues(
        theta: Map<Symbol, Double> = parameters,
    ): Matrix<Double> = with(linearSpace) {
        //Возвращает производную k-того Eq по l-тому параметру
        //val res = Array(fitDim) { DoubleArray(fitDim) }
        val sderiv = buildMatrix(data.size, size) { i, l ->
            distanceDerivative(symbols[l], i, theta)
        }

        buildMatrix(size, size) { k, l ->
            val base = (0 until data.size).sumOf { i ->
                require(dispersion[i] > 0)
                sderiv[i, l] * derivs[k, i] / dispersion[i]
            }
            prior?.let { prior ->
                //Check if this one is correct
                val pi = prior(theta)
                val deriv1 = prior.derivative(symbols[k])(theta)
                val deriv2 = prior.derivative(symbols[l])(theta)
                base + deriv1 * deriv2 / pi / pi
            } ?: base
        }
    }


    /**
     * Значения уравнений метода квазиоптимальных весов
     */
    private fun QoWeight.getEqValues(theta: Map<Symbol, Double> = this): Point<Double> {
        val distances = DoubleBuffer(data.size) { i -> distance(i, theta) }

        return DoubleBuffer(size) { k ->
            val base = (0 until data.size).sumOf { i -> distances[i] * derivs[k, i] / dispersion[i] }
            //Поправка на априорную вероятность
            prior?.let { prior ->
                base - prior.derivative(symbols[k])(theta) / prior(theta)
            } ?: base
        }
    }


    private fun QoWeight.newtonianStep(
        theta: Map<Symbol, Double>,
        eqvalues: Point<Double>,
    ): QoWeight = linearSpace {
        with(this@newtonianStep) {
            val start = theta.toPoint()
            val invJacob = solver.inverse(this@newtonianStep.getEqDerivValues(theta))

            val step = invJacob.dot(eqvalues)
            return QoWeight(problem, theta + (start - step).toMap())
        }
    }

    private fun QoWeight.newtonianRun(
        maxSteps: Int = 100,
        tolerance: Double = 0.0,
        fast: Boolean = false,
    ): QoWeight {

        val logger = problem.getFeature<OptimizationLog>()

        var dis: Double//норма невязки
        // Для удобства работаем всегда с полным набором параметров
        var par = problem.startPoint

        logger?.log { "Starting newtonian iteration from: \n\t$par" }

        var eqvalues = getEqValues(par)//значения функций

        dis = DoubleL2Norm.norm(eqvalues)// невязка
        logger?.log { "Starting discrepancy is $dis" }
        var i = 0
        var flag = false
        while (!flag) {
            i++
            logger?.log { "Starting step number $i" }

            val currentSolution = if (fast) {
                //Берет значения матрицы в той точке, где считается вес
                newtonianStep(this, eqvalues)
            } else {
                //Берет значения матрицы в точке par
                newtonianStep(par, eqvalues)
            }
            // здесь должен стоять учет границ параметров
            logger?.log { "Parameter values after step are: \n\t$currentSolution" }

            eqvalues = getEqValues(currentSolution)
            val currentDis = DoubleL2Norm.norm(eqvalues)// невязка после шага

            logger?.log { "The discrepancy after step is: $currentDis." }

            if (currentDis >= dis && i > 1) {
                //дополнительно проверяем, чтобы был сделан хотя бы один шаг
                flag = true
                logger?.log { "The discrepancy does not decrease. Stopping iteration." }
            } else {
                par = currentSolution
                dis = currentDis
            }
            if (i >= maxSteps) {
                flag = true
                logger?.log { "Maximum number of iterations reached. Stopping iteration." }
            }
            if (dis <= tolerance) {
                flag = true
                logger?.log { "Tolerance threshold is reached. Stopping iteration." }
            }
        }

        return QoWeight(problem, par)
    }

    private fun QoWeight.covariance(): Matrix<Double> {
        val logger = problem.getFeature<OptimizationLog>()

        logger?.log {
            """
            Starting errors estimation using quasioptimal weights method. The starting weight is:
                ${problem.startPoint}
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
        return covar
    }

    override suspend fun optimize(problem: XYOptimization): XYOptimization {
        val initialWeight = QoWeight(problem, problem.startPoint)
        val res = initialWeight.newtonianRun()
        return res.problem.withFeature(OptimizationResult(res.parameters))
    }
}