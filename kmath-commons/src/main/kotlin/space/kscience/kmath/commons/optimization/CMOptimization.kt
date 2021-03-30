package space.kscience.kmath.commons.optimization

import org.apache.commons.math3.optim.*
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType
import org.apache.commons.math3.optim.nonlinear.scalar.MultivariateOptimizer
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunctionGradient
import org.apache.commons.math3.optim.nonlinear.scalar.gradient.NonLinearConjugateGradientOptimizer
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.AbstractSimplex
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.NelderMeadSimplex
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.SimplexOptimizer
import space.kscience.kmath.expressions.DifferentiableExpression
import space.kscience.kmath.expressions.Expression
import space.kscience.kmath.expressions.SymbolIndexer
import space.kscience.kmath.expressions.derivative
import space.kscience.kmath.misc.Symbol
import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.optimization.*
import kotlin.reflect.KClass

public operator fun PointValuePair.component1(): DoubleArray = point
public operator fun PointValuePair.component2(): Double = value

@OptIn(UnstableKMathAPI::class)
public class CMOptimization(
    override val symbols: List<Symbol>,
) : FunctionOptimization<Double>, NoDerivFunctionOptimization<Double>, SymbolIndexer, OptimizationFeature {

    private val optimizationData: HashMap<KClass<out OptimizationData>, OptimizationData> = HashMap()
    private var optimizerBuilder: (() -> MultivariateOptimizer)? = null
    public var convergenceChecker: ConvergenceChecker<PointValuePair> = SimpleValueChecker(
        DEFAULT_RELATIVE_TOLERANCE,
        DEFAULT_ABSOLUTE_TOLERANCE,
        DEFAULT_MAX_ITER
    )

    override var maximize: Boolean
        get() = optimizationData[GoalType::class] == GoalType.MAXIMIZE
        set(value) {
            optimizationData[GoalType::class] = if (value) GoalType.MAXIMIZE else GoalType.MINIMIZE
        }

    public fun addOptimizationData(data: OptimizationData) {
        optimizationData[data::class] = data
    }

    init {
        addOptimizationData(MaxEval.unlimited())
    }

    public fun exportOptimizationData(): List<OptimizationData> = optimizationData.values.toList()

    public override fun initialGuess(map: Map<Symbol, Double>): Unit {
        addOptimizationData(InitialGuess(map.toDoubleArray()))
    }

    public override fun function(expression: Expression<Double>): Unit {
        val objectiveFunction = ObjectiveFunction {
            val args = it.toMap()
            expression(args)
        }
        addOptimizationData(objectiveFunction)
    }

    public override fun diffFunction(expression: DifferentiableExpression<Double, Expression<Double>>) {
        function(expression)
        val gradientFunction = ObjectiveFunctionGradient {
            val args = it.toMap()
            DoubleArray(symbols.size) { index ->
                expression.derivative(symbols[index])(args)
            }
        }
        addOptimizationData(gradientFunction)
        if (optimizerBuilder == null) {
            optimizerBuilder = {
                NonLinearConjugateGradientOptimizer(
                    NonLinearConjugateGradientOptimizer.Formula.FLETCHER_REEVES,
                    convergenceChecker
                )
            }
        }
    }

    public fun simplex(simplex: AbstractSimplex) {
        addOptimizationData(simplex)
        //Set optimization builder to simplex if it is not present
        if (optimizerBuilder == null) {
            optimizerBuilder = { SimplexOptimizer(convergenceChecker) }
        }
    }

    public fun simplexSteps(steps: Map<Symbol, Double>) {
        simplex(NelderMeadSimplex(steps.toDoubleArray()))
    }

    public fun goal(goalType: GoalType) {
        addOptimizationData(goalType)
    }

    public fun optimizer(block: () -> MultivariateOptimizer) {
        optimizerBuilder = block
    }

    override fun update(result: OptimizationResult<Double>) {
        initialGuess(result.point)
    }

    override fun optimize(): OptimizationResult<Double> {
        val optimizer = optimizerBuilder?.invoke() ?: error("Optimizer not defined")
        val (point, value) = optimizer.optimize(*optimizationData.values.toTypedArray())
        return OptimizationResult(point.toMap(), value, setOf(this))
    }

    public companion object : OptimizationProblemFactory<Double, CMOptimization> {
        public const val DEFAULT_RELATIVE_TOLERANCE: Double = 1e-4
        public const val DEFAULT_ABSOLUTE_TOLERANCE: Double = 1e-4
        public const val DEFAULT_MAX_ITER: Int = 1000

        override fun build(symbols: List<Symbol>): CMOptimization = CMOptimization(symbols)
    }
}

public fun CMOptimization.initialGuess(vararg pairs: Pair<Symbol, Double>): Unit = initialGuess(pairs.toMap())
public fun CMOptimization.simplexSteps(vararg pairs: Pair<Symbol, Double>): Unit = simplexSteps(pairs.toMap())
