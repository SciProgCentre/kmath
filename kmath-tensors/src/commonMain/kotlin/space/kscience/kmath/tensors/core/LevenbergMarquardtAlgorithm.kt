/*
 * Copyright 2018-2023 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.tensors.core

import space.kscience.kmath.linear.transpose
import space.kscience.kmath.nd.*
import space.kscience.kmath.tensors.core.BroadcastDoubleTensorAlgebra.div
import space.kscience.kmath.tensors.core.BroadcastDoubleTensorAlgebra.dot
import space.kscience.kmath.tensors.core.BroadcastDoubleTensorAlgebra.minus
import space.kscience.kmath.tensors.core.BroadcastDoubleTensorAlgebra.times
import space.kscience.kmath.tensors.core.BroadcastDoubleTensorAlgebra.transposed
import space.kscience.kmath.tensors.core.DoubleTensorAlgebra.Companion.plus
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.reflect.KFunction3

/**
 * Type of convergence achieved as a result of executing the Levenberg-Marquardt algorithm.
 *
 * InGradient: gradient convergence achieved
 *            (max(J^T W dy) < epsilon1,
 *            where J - Jacobi matrix (dy^/dp) for the current approximation y^,
 *            W - weight matrix from input, dy = (y - y^(p))).
 * InParameters: convergence in parameters achieved
 *            (max(h_i / p_i) < epsilon2,
 *            where h_i - offset for parameter p_i on the current iteration).
 * InReducedChiSquare: chi-squared convergence achieved
 *            (chi squared value divided by (m - n + 1) < epsilon2,
 *            where n - number of parameters, m - amount of points).
 * NoConvergence: the maximum number of iterations has been reached without reaching any convergence.
 */
public enum class TypeOfConvergence {
    InGradient,
    InParameters,
    InReducedChiSquare,
    NoConvergence
}

/**
 * The data obtained as a result of the execution of the Levenberg-Marquardt algorithm.
 *
 *  iterations: number of completed iterations.
 *  funcCalls: the number of evaluations of the input function during execution.
 *  resultChiSq: chi squared value on final parameters.
 *  resultLambda: final lambda parameter used to calculate the offset.
 *  resultParameters: final parameters.
 *  typeOfConvergence: type of convergence.
 */
public data class LMResultInfo (
    var iterations:Int,
    var funcCalls: Int,
    var resultChiSq: Double,
    var resultLambda: Double,
    var resultParameters: MutableStructure2D<Double>,
    var typeOfConvergence: TypeOfConvergence,
)

/**
 * Input data for the Levenberg-Marquardt function.
 *
 *  func: function of n independent variables x, m parameters an example number,
 *        rotating a vector of n values y, in which each of the y_i is calculated at its x_i with the given parameters.
 *  startParameters: starting parameters.
 *  independentVariables: independent variables, for each of which the real value is known.
 *  realValues: real values obtained with given independent variables but unknown parameters.
 *  weight: measurement error for realValues (denominator in each term of sum of weighted squared errors).
 *  pDelta: delta when calculating the derivative with respect to parameters.
 *  minParameters: the lower bound of parameter values.
 *  maxParameters: upper limit of parameter values.
 *  maxIterations: maximum allowable number of iterations.
 *  epsilons: epsilon1 - convergence tolerance for gradient,
 *            epsilon2 - convergence tolerance for parameters,
 *            epsilon3 - convergence tolerance for reduced chi-square,
 *            epsilon4 - determines acceptance of a step.
 *  lambdas: lambda0 - starting lambda value for parameter offset count,
 *           lambdaUp - factor for increasing lambda,
 *           lambdaDown - factor for decreasing lambda.
 *  updateType: 1: Levenberg-Marquardt lambda update,
 *              2: Quadratic update,
 *              3: Nielsen's lambda update equations.
 *  nargin: a value that determines which options to use by default
 *         (<5 - use weight by default, <6 - use pDelta by default, <7 - use minParameters by default,
 *          <8 - use maxParameters by default, <9 - use updateType by default).
 *  exampleNumber: a parameter for a function with which you can choose its behavior.
 */
public data class LMInput (
    var func: KFunction3<MutableStructure2D<Double>, MutableStructure2D<Double>, Int, MutableStructure2D<Double>>,
    var startParameters: MutableStructure2D<Double>,
    var independentVariables: MutableStructure2D<Double>,
    var realValues: MutableStructure2D<Double>,
    var weight: Double,
    var pDelta: MutableStructure2D<Double>,
    var minParameters: MutableStructure2D<Double>,
    var maxParameters: MutableStructure2D<Double>,
    var maxIterations: Int,
    var epsilons: DoubleArray,
    var lambdas: DoubleArray,
    var updateType: Int,
    var nargin: Int,
    var exampleNumber: Int
)


/**
 * Levenberg-Marquardt optimization.
 *
 * An optimization method that iteratively searches for the optimal function parameters
 * that best describe the dataset. The 'input' is the function being optimized, a set of real data
 * (calculated with independent variables, but with an unknown set of parameters), a set of
 * independent variables, and variables for adjusting the algorithm, described in the documentation for the LMInput class.
 * The function returns number of completed iterations, the number of evaluations of the input function during execution,
 * chi squared value on final parameters, final lambda parameter used to calculate the offset, final parameters
 * and type of convergence in the 'output'.
 *
 * @receiver the `input`.
 * @return the 'output'.
 */
public fun DoubleTensorAlgebra.levenbergMarquardt(inputData: LMInput): LMResultInfo {
    val resultInfo = LMResultInfo(0, 0, 0.0,
        0.0, inputData.startParameters, TypeOfConvergence.NoConvergence)

    val eps = 2.2204e-16

    val settings = LMSettings(0, 0, inputData.exampleNumber)
    settings.funcCalls = 0 // running count of function evaluations

    var p = inputData.startParameters
    val t = inputData.independentVariables

    val Npar   = length(p) // number of parameters
    val Npnt   = length(inputData.realValues) // number of data points
    var pOld = zeros(ShapeND(intArrayOf(Npar, 1))).as2D() // previous set of parameters
    var yOld = zeros(ShapeND(intArrayOf(Npnt, 1))).as2D() // previous model, y_old = y_hat(t;p_old)
    var X2 = 1e-3 / eps  // a really big initial Chi-sq value
    var X2Old = 1e-3 / eps // a really big initial Chi-sq value
    var J = zeros(ShapeND(intArrayOf(Npnt, Npar))).as2D() // Jacobian matrix
    val DoF = Npnt - Npar // statistical degrees of freedom

    var weight = fromArray(ShapeND(intArrayOf(1, 1)), doubleArrayOf(inputData.weight)).as2D()
    if (inputData.nargin <  5) {
        weight = fromArray(ShapeND(intArrayOf(1, 1)), doubleArrayOf((inputData.realValues.transpose().dot(inputData.realValues)).as1D()[0])).as2D()
    }

    var dp = inputData.pDelta
    if (inputData.nargin < 6) {
        dp = fromArray(ShapeND(intArrayOf(1, 1)), doubleArrayOf(0.001)).as2D()
    }

    var minParameters = inputData.minParameters
    if (inputData.nargin < 7) {
        minParameters = p
        minParameters.abs()
        minParameters = minParameters.div(-100.0).as2D()
    }

    var maxParameters = inputData.maxParameters
    if (inputData.nargin < 8) {
        maxParameters = p
        maxParameters.abs()
        maxParameters = maxParameters.div(100.0).as2D()
    }

    var maxIterations = inputData.maxIterations
    var epsilon1 = inputData.epsilons[0]
    var epsilon2 = inputData.epsilons[1]
    var epsilon3 = inputData.epsilons[2]
    var epsilon4 = inputData.epsilons[3]
    var lambda0  = inputData.lambdas[0]
    var lambdaUpFac = inputData.lambdas[1]
    var lambdaDnFac = inputData.lambdas[2]
    var updateType = inputData.updateType

    if (inputData.nargin < 9) {
        maxIterations = 10 * Npar
        epsilon1 = 1e-3
        epsilon2 = 1e-3
        epsilon3 = 1e-1
        epsilon4 = 1e-1
        lambda0 = 1e-2
        lambdaUpFac = 11.0
        lambdaDnFac = 9.0
        updateType = 1
    }

    minParameters = makeColumn(minParameters)
    maxParameters = makeColumn(maxParameters)

    if (length(makeColumn(dp)) == 1) {
        dp = ones(ShapeND(intArrayOf(Npar, 1))).div(1 / dp[0, 0]).as2D()
    }

    var stop = false // termination flag

    if (weight.shape.component1() == 1 || variance(weight) == 0.0) { // identical weights vector
        weight = ones(ShapeND(intArrayOf(Npnt, 1))).div(1 / kotlin.math.abs(weight[0, 0])).as2D()
    }
    else {
        weight = makeColumn(weight)
        weight.abs()
    }

    // initialize Jacobian with finite difference calculation
    var lmMatxAns = lmMatx(inputData.func, t, pOld, yOld, 1, J, p, inputData.realValues, weight, dp, settings)
    var JtWJ = lmMatxAns[0]
    var JtWdy = lmMatxAns[1]
    X2 = lmMatxAns[2][0, 0]
    var yHat = lmMatxAns[3]
    J = lmMatxAns[4]

    if ( abs(JtWdy).max() < epsilon1 ) {
        stop = true
    }

    var lambda = 1.0
    var nu = 1

    if (updateType == 1) {
        lambda  = lambda0 // Marquardt: init'l lambda
    }
    else {
        lambda  = lambda0 * (makeColumnFromDiagonal(JtWJ)).max()
        nu = 2
    }

    X2Old = X2 // previous value of X2

    var h: DoubleTensor

    while (!stop && settings.iteration <= maxIterations) {
        settings.iteration += 1

        // incremental change in parameters
        h = if (updateType == 1) { // Marquardt
            val solve = solve(JtWJ.plus(makeMatrixWithDiagonal(makeColumnFromDiagonal(JtWJ)).div(1 / lambda)).as2D(), JtWdy)
            solve.asDoubleTensor()
        } else { // Quadratic and Nielsen
            val solve = solve(JtWJ.plus(lmEye(Npar).div(1 / lambda)).as2D(), JtWdy)
            solve.asDoubleTensor()
        }

        var pTry = (p + h).as2D()  // update the [idx] elements
        pTry = smallestElementComparison(largestElementComparison(minParameters, pTry.as2D()), maxParameters) // apply constraints

        var deltaY = inputData.realValues.minus(evaluateFunction(inputData.func, t, pTry, inputData.exampleNumber)) // residual error using p_try

        for (i in 0 until deltaY.shape.component1()) {  // floating point error; break
            for (j in 0 until deltaY.shape.component2()) {
                if (deltaY[i, j] == Double.POSITIVE_INFINITY || deltaY[i, j] == Double.NEGATIVE_INFINITY) {
                    stop = true
                    break
                }
            }
        }

        settings.funcCalls += 1

        val tmp = deltaY.times(weight)
        var X2Try = deltaY.as2D().transpose().dot(tmp) // Chi-squared error criteria

        val alpha = 1.0
        if (updateType == 2) { // Quadratic
            // One step of quadratic line update in the h direction for minimum X2
            val alpha = JtWdy.transpose().dot(h) / ((X2Try.minus(X2)).div(2.0).plus(2 * JtWdy.transpose().dot(h)))
            h = h.dot(alpha)
            pTry = p.plus(h).as2D() // update only [idx] elements
            pTry = smallestElementComparison(largestElementComparison(minParameters, pTry), maxParameters) // apply constraints

            deltaY = inputData.realValues.minus(evaluateFunction(inputData.func, t, pTry, inputData.exampleNumber)) // residual error using p_try
            settings.funcCalls += 1

            X2Try = deltaY.as2D().transpose().dot(deltaY.times(weight)) // Chi-squared error criteria
        }

        val rho = when (updateType) { // Nielsen
            1 -> {
                val tmp = h.transposed()
                    .dot(makeMatrixWithDiagonal(makeColumnFromDiagonal(JtWJ)).div(1 / lambda).dot(h).plus(JtWdy))
                X2.minus(X2Try).as2D()[0, 0] / abs(tmp.as2D()).as2D()[0, 0]
            }
            else -> {
                val tmp = h.transposed().dot(h.div(1 / lambda).plus(JtWdy))
                X2.minus(X2Try).as2D()[0, 0] / abs(tmp.as2D()).as2D()[0, 0]
            }
        }

        if (rho > epsilon4) { // it IS significantly better
            val dX2 = X2.minus(X2Old)
            X2Old = X2
            pOld = p.copyToTensor().as2D()
            yOld = yHat.copyToTensor().as2D()
            p = makeColumn(pTry) // accept p_try

            lmMatxAns = lmMatx(inputData.func, t, pOld, yOld, dX2.toInt(), J, p, inputData.realValues, weight, dp, settings)
            // decrease lambda ==> Gauss-Newton method
            JtWJ = lmMatxAns[0]
            JtWdy = lmMatxAns[1]
            X2 = lmMatxAns[2][0, 0]
            yHat = lmMatxAns[3]
            J = lmMatxAns[4]

            lambda = when (updateType) {
                1 -> { // Levenberg
                    max(lambda / lambdaDnFac, 1e-7);
                }

                2 -> { // Quadratic
                    max(lambda / (1 + alpha), 1e-7);
                }

                else -> { // Nielsen
                    nu = 2
                    lambda * max(1.0 / 3, 1 - (2 * rho - 1).pow(3))
                }
            }
        } else { // it IS NOT better
            X2 = X2Old // do not accept p_try
            if (settings.iteration % (2 * Npar) == 0) { // rank-1 update of Jacobian
                lmMatxAns = lmMatx(inputData.func, t, pOld, yOld, -1, J, p, inputData.realValues, weight, dp, settings)
                JtWJ = lmMatxAns[0]
                JtWdy = lmMatxAns[1]
                yHat = lmMatxAns[3]
                J = lmMatxAns[4]
            }

            // increase lambda  ==> gradient descent method
            lambda = when (updateType) {
                1 -> { // Levenberg
                    min(lambda * lambdaUpFac, 1e7)
                }

                2 -> { // Quadratic
                    lambda + kotlin.math.abs(((X2Try.as2D()[0, 0] - X2) / 2) / alpha)
                }

                else -> { // Nielsen
                    nu *= 2
                    lambda * (nu / 2)
                }
            }
        }

        val chiSq = X2 / DoF
        resultInfo.iterations = settings.iteration
        resultInfo.funcCalls = settings.funcCalls
        resultInfo.resultChiSq = chiSq
        resultInfo.resultLambda = lambda
        resultInfo.resultParameters = p


        if (abs(JtWdy).max() < epsilon1 && settings.iteration > 2) {
            resultInfo.typeOfConvergence = TypeOfConvergence.InGradient
            stop = true
        }
        if ((abs(h.as2D()).div(abs(p) + 1e-12)).max() < epsilon2 && settings.iteration > 2) {
            resultInfo.typeOfConvergence = TypeOfConvergence.InParameters
            stop = true
        }
        if (X2 / DoF < epsilon3 && settings.iteration > 2) {
            resultInfo.typeOfConvergence = TypeOfConvergence.InReducedChiSquare
            stop = true
        }
        if (settings.iteration == maxIterations) {
            resultInfo.typeOfConvergence = TypeOfConvergence.NoConvergence
            stop = true
        }
    }
    return resultInfo
}

private data class LMSettings (
    var iteration:Int,
    var funcCalls: Int,
    var exampleNumber:Int
)

/* matrix -> column of all elements */
private fun makeColumn(tensor: MutableStructure2D<Double>): MutableStructure2D<Double> {
    val shape = intArrayOf(tensor.shape.component1() * tensor.shape.component2(), 1)
    val buffer = DoubleArray(tensor.shape.component1() * tensor.shape.component2())
    for (i in 0 until tensor.shape.component1()) {
        for (j in 0 until tensor.shape.component2()) {
            buffer[i * tensor.shape.component2() + j] = tensor[i, j]
        }
    }
    return BroadcastDoubleTensorAlgebra.fromArray(ShapeND(shape), buffer).as2D()
}

/* column length */
private fun length(column: MutableStructure2D<Double>) : Int {
    return column.shape.component1()
}

private fun MutableStructure2D<Double>.abs() {
    for (i in 0 until this.shape.component1()) {
        for (j in 0 until this.shape.component2()) {
            this[i, j] = kotlin.math.abs(this[i, j])
        }
    }
}

private fun abs(input: MutableStructure2D<Double>): MutableStructure2D<Double> {
    val tensor = BroadcastDoubleTensorAlgebra.ones(
        ShapeND(
            intArrayOf(
                input.shape.component1(),
                input.shape.component2()
            )
        )
    ).as2D()
    for (i in 0 until tensor.shape.component1()) {
        for (j in 0 until tensor.shape.component2()) {
            tensor[i, j] = kotlin.math.abs(input[i, j])
        }
    }
    return tensor
}

private fun makeColumnFromDiagonal(input: MutableStructure2D<Double>): MutableStructure2D<Double> {
    val tensor = BroadcastDoubleTensorAlgebra.ones(ShapeND(intArrayOf(input.shape.component1(), 1))).as2D()
    for (i in 0 until tensor.shape.component1()) {
        tensor[i, 0] = input[i, i]
    }
    return tensor
}

private fun makeMatrixWithDiagonal(column: MutableStructure2D<Double>): MutableStructure2D<Double> {
    val size = column.shape.component1()
    val tensor = BroadcastDoubleTensorAlgebra.zeros(ShapeND(intArrayOf(size, size))).as2D()
    for (i in 0 until size) {
        tensor[i, i] = column[i, 0]
    }
    return tensor
}

private fun lmEye(size: Int): MutableStructure2D<Double> {
    val column = BroadcastDoubleTensorAlgebra.ones(ShapeND(intArrayOf(size, 1))).as2D()
    return makeMatrixWithDiagonal(column)
}

private fun largestElementComparison(a: MutableStructure2D<Double>, b: MutableStructure2D<Double>): MutableStructure2D<Double> {
    val aSizeX = a.shape.component1()
    val aSizeY = a.shape.component2()
    val bSizeX = b.shape.component1()
    val bSizeY = b.shape.component2()
    val tensor = BroadcastDoubleTensorAlgebra.zeros(ShapeND(intArrayOf(max(aSizeX, bSizeX), max(aSizeY, bSizeY)))).as2D()
    for (i in 0 until tensor.shape.component1()) {
        for (j in 0 until tensor.shape.component2()) {
            if (i < aSizeX && i < bSizeX && j < aSizeY && j < bSizeY) {
                tensor[i, j] = max(a[i, j], b[i, j])
            }
            else if (i < aSizeX && j < aSizeY) {
                tensor[i, j] = a[i, j]
            }
            else {
                tensor[i, j] = b[i, j]
            }
        }
    }
    return tensor
}

private fun smallestElementComparison(a: MutableStructure2D<Double>, b: MutableStructure2D<Double>): MutableStructure2D<Double> {
    val aSizeX = a.shape.component1()
    val aSizeY = a.shape.component2()
    val bSizeX = b.shape.component1()
    val bSizeY = b.shape.component2()
    val tensor = BroadcastDoubleTensorAlgebra.zeros(ShapeND(intArrayOf(max(aSizeX, bSizeX), max(aSizeY, bSizeY)))).as2D()
    for (i in 0 until tensor.shape.component1()) {
        for (j in 0 until tensor.shape.component2()) {
            if (i < aSizeX && i < bSizeX && j < aSizeY && j < bSizeY) {
                tensor[i, j] = min(a[i, j], b[i, j])
            }
            else if (i < aSizeX && j < aSizeY) {
                tensor[i, j] = a[i, j]
            }
            else {
                tensor[i, j] = b[i, j]
            }
        }
    }
    return tensor
}

private fun getZeroIndices(column: MutableStructure2D<Double>, epsilon: Double = 0.000001): MutableStructure2D<Double>? {
    var idx = emptyArray<Double>()
    for (i in 0 until column.shape.component1()) {
        if (kotlin.math.abs(column[i, 0]) > epsilon) {
            idx += (i + 1.0)
        }
    }
    if (idx.isNotEmpty()) {
        return BroadcastDoubleTensorAlgebra.fromArray(ShapeND(intArrayOf(idx.size, 1)), idx.toDoubleArray()).as2D()
    }
    return null
}

private fun evaluateFunction(func: (MutableStructure2D<Double>, MutableStructure2D<Double>, Int) ->  MutableStructure2D<Double>,
                             t: MutableStructure2D<Double>, p: MutableStructure2D<Double>, exampleNumber: Int)
        : MutableStructure2D<Double>
{
    return func(t, p, exampleNumber)
}

private fun lmMatx(func: (MutableStructure2D<Double>, MutableStructure2D<Double>, Int) -> MutableStructure2D<Double>,
                   t: MutableStructure2D<Double>, pOld: MutableStructure2D<Double>, yOld: MutableStructure2D<Double>,
                   dX2: Int, JInput: MutableStructure2D<Double>, p: MutableStructure2D<Double>,
                   yDat: MutableStructure2D<Double>, weight: MutableStructure2D<Double>, dp:MutableStructure2D<Double>, settings:LMSettings) : Array<MutableStructure2D<Double>>
{
    // default: dp = 0.001
    val Npar = length(p) // number of parameters

    val yHat = evaluateFunction(func, t, p, settings.exampleNumber) // evaluate model using parameters 'p'
    settings.funcCalls += 1

    var J = JInput

    J = if (settings.iteration % (2 * Npar) == 0 || dX2 > 0) {
        lmFdJ(func, t, p, yHat, dp, settings).as2D() // finite difference
    }
    else {
        lmBroydenJ(pOld, yOld, J, p, yHat).as2D() // rank-1 update
    }

    val deltaY = yDat.minus(yHat)

    val chiSq = deltaY.transposed().dot( deltaY.times(weight) ).as2D()
    val JtWJ = J.transposed().dot ( J.times( weight.dot(BroadcastDoubleTensorAlgebra.ones(ShapeND(intArrayOf(1, Npar)))) ) ).as2D()
    val JtWdy = J.transposed().dot( weight.times(deltaY) ).as2D()

    return arrayOf(JtWJ,JtWdy,chiSq,yHat,J)
}

private fun lmBroydenJ(pOld: MutableStructure2D<Double>, yOld: MutableStructure2D<Double>, JInput: MutableStructure2D<Double>,
                       p: MutableStructure2D<Double>, y: MutableStructure2D<Double>): MutableStructure2D<Double> {
    var J = JInput.copyToTensor()

    val h = p.minus(pOld)
    val increase = y.minus(yOld).minus( J.dot(h) ).dot(h.transposed()).div( (h.transposed().dot(h)).as2D()[0, 0] )
    J = J.plus(increase)

    return J.as2D()
}

private fun lmFdJ(func: (MutableStructure2D<Double>, MutableStructure2D<Double>, exampleNumber: Int) -> MutableStructure2D<Double>,
                  t: MutableStructure2D<Double>, p: MutableStructure2D<Double>, y: MutableStructure2D<Double>,
                  dp: MutableStructure2D<Double>, settings: LMSettings): MutableStructure2D<Double> {
    // default: dp = 0.001 * ones(1,n)

    val m = length(y) // number of data points
    val n = length(p) // number of parameters

    val ps = p.copyToTensor().as2D()
    val J = BroadcastDoubleTensorAlgebra.zeros(ShapeND(intArrayOf(m, n))).as2D()  // initialize Jacobian to Zero
    val del = BroadcastDoubleTensorAlgebra.zeros(ShapeND(intArrayOf(n, 1))).as2D()

    for (j in 0 until n) {

        del[j, 0] = dp[j, 0] * (1 + kotlin.math.abs(p[j, 0])) // parameter perturbation
        p[j, 0] = ps[j, 0] + del[j, 0] // perturb parameter p(j)

        val epsilon = 0.0000001
        if (kotlin.math.abs(del[j, 0]) > epsilon) {
            val y1 = evaluateFunction(func, t, p, settings.exampleNumber)
            settings.funcCalls += 1

            if (dp[j, 0] < 0) { // backwards difference
                for (i in 0 until J.shape.component1()) {
                    J[i, j] = (y1.as2D().minus(y).as2D())[i, 0] / del[j, 0]
                }
            }
            else {
                // Do tests for it
                p[j, 0] = ps[j, 0] - del[j, 0] // central difference, additional func call
                for (i in 0 until J.shape.component1()) {
                    J[i, j] = (y1.as2D().minus(evaluateFunction(func, t, p, settings.exampleNumber)).as2D())[i, 0] / (2 * del[j, 0])
                }
                settings.funcCalls += 1
            }
        }

        p[j, 0] = ps[j, 0]
    }

    return J.as2D()
}
