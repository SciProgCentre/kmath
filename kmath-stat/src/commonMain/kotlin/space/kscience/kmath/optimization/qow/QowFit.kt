/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.optimization.qow

import space.kscience.kmath.data.ColumnarData
import space.kscience.kmath.data.XYErrorColumnarData
import space.kscience.kmath.expressions.*
import space.kscience.kmath.linear.*
import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.operations.Field
import space.kscience.kmath.optimization.OptimizationFeature
import space.kscience.kmath.optimization.OptimizationProblemFactory
import space.kscience.kmath.optimization.OptimizationResult
import space.kscience.kmath.optimization.XYFit
import space.kscience.kmath.structures.DoubleBuffer
import space.kscience.kmath.structures.DoubleL2Norm
import kotlin.math.pow


private typealias ParamSet = Map<Symbol, Double>

@OptIn(UnstableKMathAPI::class)
public class QowFit(
    override val symbols: List<Symbol>,
    private val space: LinearSpace<Double, DoubleField>,
    private val solver: LinearSolver<Double>,
) : XYFit<Double>, SymbolIndexer {

    private var logger: FitLogger? = null

    private var startingPoint: Map<Symbol, Double> = TODO()
    private var covariance: Matrix<Double>? = TODO()
    private val prior: DifferentiableExpression<Double, Expression<Double>>? = TODO()
    private var data: XYErrorColumnarData<Double, Double, Double> = TODO()
    private var model: DifferentiableExpression<Double, Expression<Double>> = TODO()

    private val features = HashSet<OptimizationFeature>()

    override fun update(result: OptimizationResult<Double>) {
        TODO("Not yet implemented")
    }

    override val algebra: Field<Double>
        get() = TODO("Not yet implemented")

    override fun data(
        dataSet: ColumnarData<Double>,
        xSymbol: Symbol,
        ySymbol: Symbol,
        xErrSymbol: Symbol?,
        yErrSymbol: Symbol?,
    ) {
        TODO("Not yet implemented")
    }

    override fun model(model: (Double) -> DifferentiableExpression<Double, *>) {
        TODO("Not yet implemented")
    }

    private var x: Symbol = Symbol.x

    /**
     * The signed distance from the model to the [i]-th point of data.
     */
    private fun distance(i: Int, parameters: Map<Symbol, Double>): Double =
        model(parameters + (x to data.x[i])) - data.y[i]


    /**
     * The derivative of [distance]
     * TODO use expressions instead
     */
    private fun distanceDerivative(symbol: Symbol, i: Int, parameters: Map<Symbol, Double>): Double =
        model.derivative(symbol)(parameters + (x to data.x[i]))

    /**
     * The dispersion of [i]-th data point
     */
    private fun getDispersion(i: Int, parameters: Map<Symbol, Double>): Double = data.yErr[i].pow(2)

    private fun getCovariance(weight: QoWeight): Matrix<Double> = solver.inverse(getEqDerivValues(weight))

    /**
     * Теоретическая ковариация весовых функций.
     *
     * D(\phi)=E(\phi_k(\theta_0) \phi_l(\theta_0))= disDeriv_k * disDeriv_l /sigma^2
     */
    private fun covarF(weight: QoWeight): Matrix<Double> = space.buildSymmetricMatrix(symbols.size) { k, l ->
        (0 until data.size).sumOf { i -> weight.derivs[k, i] * weight.derivs[l, i] / weight.dispersion[i] }
    }

    /**
     * Экспериментальная ковариация весов. Формула (22) из
     * http://arxiv.org/abs/physics/0604127
     *
     * @param source
     * @param set
     * @param fitPars
     * @param weight
     * @return
     */
    private fun covarFExp(weight: QoWeight, theta: Map<Symbol, Double>): Matrix<Double> = space.run {
        /*
         * Важно! Если не делать предварителього вычисления этих производных, то
         * количество вызывов функции будет dim^2 вместо dim Первый индекс -
         * номер точки, второй - номер переменной, по которой берется производная
         */
        val eqvalues = buildMatrix(data.size, symbols.size) { i, l ->
            distance(i, theta) * weight.derivs[l, i] / weight.dispersion[i]
        }

        buildMatrix(symbols.size, symbols.size) { k, l ->
            (0 until data.size).sumOf { i -> eqvalues[i, l] * eqvalues[i, k] }
        }
    }

    /**
     * производные уравнений для метода Ньютона
     *
     * @param source
     * @param set
     * @param fitPars
     * @param weight
     * @return
     */
    private fun getEqDerivValues(
        weight: QoWeight, theta: Map<Symbol, Double> = weight.theta,
    ): Matrix<Double> = space.run {
        val fitDim = symbols.size
        //Возвращает производную k-того Eq по l-тому параметру
        val res = Array(fitDim) { DoubleArray(fitDim) }
        val sderiv = buildMatrix(data.size, symbols.size) { i, l ->
            distanceDerivative(symbols[l], i, theta)
        }

        buildMatrix(symbols.size, symbols.size) { k, l ->
            val base = (0 until data.size).sumOf { i ->
                require(weight.dispersion[i] > 0)
                sderiv[i, l] * weight.derivs[k, i] / weight.dispersion[i]
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
     *
     * @param source
     * @param set
     * @param fitPars
     * @param weight
     * @return
     */
    private fun getEqValues(weight: QoWeight, theta: Map<Symbol, Double> = weight.theta): Point<Double> {
        val distances = DoubleBuffer(data.size) { i -> distance(i, theta) }

        return DoubleBuffer(symbols.size) { k ->
            val base = (0 until data.size).sumOf { i -> distances[i] * weight.derivs[k, i] / weight.dispersion[i] }
            //Поправка на априорную вероятность
            prior?.let { prior ->
                base - prior.derivative(symbols[k])(theta) / prior(theta)
            } ?: base
        }
    }


    /**
     * The state of QOW fitter
     * Created by Alexander Nozik on 17-Oct-16.
     */
    private inner class QoWeight(
        val theta: Map<Symbol, Double>,
    ) {

        init {
            require(data.size > 0) { "The state does not contain data" }
        }

        /**
         * Derivatives of the spectrum over parameters. First index in the point number, second one - index of parameter
         */
        val derivs: Matrix<Double> by lazy {
            space.buildMatrix(data.size, symbols.size) { i, k ->
                distanceDerivative(symbols[k], i, theta)
            }
        }

        /**
         * Array of dispersions in each point
         */
        val dispersion: Point<Double> by lazy {
            DoubleBuffer(data.size) { i -> getDispersion(i, theta) }
        }

    }

    private fun newtonianStep(
        weight: QoWeight,
        par: Map<Symbol, Double>,
        eqvalues: Point<Double>,
    ): Map<Symbol, Double> = space.run {
        val start = par.toPoint()
        val invJacob = solver.inverse(getEqDerivValues(weight, par))

        val step = invJacob.dot(eqvalues)
        return par + (start - step).toMap()
    }

    private fun newtonianRun(
        weight: QoWeight,
        maxSteps: Int = 100,
        tolerance: Double = 0.0,
        fast: Boolean = false,
    ): ParamSet {

        var dis: Double//норма невязки
        // Для удобства работаем всегда с полным набором параметров
        var par = startingPoint

        logger?.log { "Starting newtonian iteration from: \n\t$par" }

        var eqvalues = getEqValues(weight, par)//значения функций

        dis = DoubleL2Norm.norm(eqvalues)// невязка
        logger?.log { "Starting discrepancy is $dis" }
        var i = 0
        var flag = false
        while (!flag) {
            i++
            logger?.log { "Starting step number $i" }

            val currentSolution = if (fast) {
                //Берет значения матрицы в той точке, где считается вес
                newtonianStep(weight, weight.theta, eqvalues)
            } else {
                //Берет значения матрицы в точке par
                newtonianStep(weight, par, eqvalues)
            }
            // здесь должен стоять учет границ параметров
            logger?.log { "Parameter values after step are: \n\t$currentSolution" }

            eqvalues = getEqValues(weight, currentSolution)
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

        return par
    }


//
//    override fun run(state: FitState, parentLog: History?, meta: Meta): FitResult {
//        val log = Chronicle("QOW", parentLog)
//        val action = meta.getString(FIT_STAGE_TYPE, TASK_RUN)
//        log.report("QOW fit engine started task '{}'", action)
//        return when (action) {
//            TASK_SINGLE -> makeRun(state, log, meta)
//            TASK_COVARIANCE -> generateErrors(state, log, meta)
//            TASK_RUN -> {
//                var res = makeRun(state, log, meta)
//                res = makeRun(res.optState().get(), log, meta)
//                generateErrors(res.optState().get(), log, meta)
//            }
//            else -> throw IllegalArgumentException("Unknown task")
//        }
//    }

//    private fun makeRun(state: FitState, log: History, meta: Meta): FitResult {
//        /*Инициализация объектов, задание исходных значений*/
//        log.report("Starting fit using quasioptimal weights method.")
//
//        val fitPars = getFitPars(state, meta)
//
//        val curWeight = QoWeight(state, fitPars, state.parameters)
//
//        // вычисляем вес в allPar. Потом можно будет попробовать ручное задание веса
//        log.report("The starting weight is: \n\t{}",
//            MathUtils.toString(curWeight.theta))
//
//        //Стартовая точка такая же как и параметр веса
//        /*Фитирование*/
//        val res = newtonianRun(state, curWeight, log, meta)
//
//        /*Генерация результата*/
//
//        return FitResult.build(state.edit().setPars(res).build(), *fitPars)
//    }

    /**
     * generateErrors.
     */
    private fun generateErrors(): Matrix<Double> {
        logger?.log { """
            Starting errors estimation using quasioptimal weights method. The starting weight is:
                ${curWeight.theta}
             """.trimIndent()}
        val curWeight = QoWeight(startingPoint)

        val covar = getCovariance(curWeight)

        val decomposition = EigenDecomposition(covar.matrix)
        var valid = true
        for (lambda in decomposition.realEigenvalues) {
            if (lambda <= 0) {
                log.report("The covariance matrix is not positive defined. Error estimation is not valid")
                valid = false
            }
        }
    }


    override suspend fun optimize(): OptimizationResult<Double> {
        val curWeight = QoWeight(startingPoint)
        logger?.log {
            """
            Starting fit using quasioptimal weights method. The starting weight is: 
	            ${curWeight.theta}
            """.trimIndent()
        }
        val res = newtonianRun(curWeight)
    }


    companion object : OptimizationProblemFactory<Double, QowFit> {
        override fun build(symbols: List<Symbol>): QowFit {
            TODO("Not yet implemented")
        }


        /**
         * Constant `QOW_ENGINE_NAME="QOW"`
         */
        const val QOW_ENGINE_NAME = "QOW"

        /**
         * Constant `QOW_METHOD_FAST="fast"`
         */
        const val QOW_METHOD_FAST = "fast"


    }
}

