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
import space.kscience.kmath.expressions.*
import space.kscience.kmath.stat.OptimizationFeature
import space.kscience.kmath.stat.OptimizationProblem
import space.kscience.kmath.stat.OptimizationProblemFactory
import space.kscience.kmath.stat.OptimizationResult
import kotlin.reflect.KClass

public operator fun PointValuePair.component1(): DoubleArray = point
public operator fun PointValuePair.component2(): Double = value

public class CMOptimizationProblem(override val symbols: List<Symbol>, ) :
    OptimizationProblem<Double>, SymbolIndexer, OptimizationFeature {
    private val optimizationData: HashMap<KClass<out OptimizationData>, OptimizationData> = HashMap()
    private var optimizatorBuilder: (() -> MultivariateOptimizer)? = null
    public var convergenceChecker: ConvergenceChecker<PointValuePair> = SimpleValueChecker(DEFAULT_RELATIVE_TOLERANCE,
        DEFAULT_ABSOLUTE_TOLERANCE, DEFAULT_MAX_ITER)

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

    public override fun expression(expression: Expression<Double>): Unit {
        val objectiveFunction = ObjectiveFunction {
            val args = it.toMap()
            expression(args)
        }
        addOptimizationData(objectiveFunction)
    }

    public override fun diffExpression(expression: DifferentiableExpression<Double, Expression<Double>>) {
        expression(expression)
        val gradientFunction = ObjectiveFunctionGradient {
            val args = it.toMap()
            DoubleArray(symbols.size) { index ->
                expression.derivative(symbols[index])(args)
            }
        }
        addOptimizationData(gradientFunction)
        if (optimizatorBuilder == null) {
            optimizatorBuilder = {
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
        if (optimizatorBuilder == null) {
            optimizatorBuilder = { SimplexOptimizer(convergenceChecker) }
        }
    }

    public fun simplexSteps(steps: Map<Symbol, Double>) {
        simplex(NelderMeadSimplex(steps.toDoubleArray()))
    }

    public fun goal(goalType: GoalType) {
        addOptimizationData(goalType)
    }

    public fun optimizer(block: () -> MultivariateOptimizer) {
        optimizatorBuilder = block
    }

    override fun update(result: OptimizationResult<Double>) {
        initialGuess(result.point)
    }

    override fun optimize(): OptimizationResult<Double> {
        val optimizer = optimizatorBuilder?.invoke() ?: error("Optimizer not defined")
        val (point, value) = optimizer.optimize(*optimizationData.values.toTypedArray())
        return OptimizationResult(point.toMap(), value, setOf(this))
    }

    public companion object : OptimizationProblemFactory<Double, CMOptimizationProblem> {
        public const val DEFAULT_RELATIVE_TOLERANCE: Double = 1e-4
        public const val DEFAULT_ABSOLUTE_TOLERANCE: Double = 1e-4
        public const val DEFAULT_MAX_ITER: Int = 1000

        override fun build(symbols: List<Symbol>): CMOptimizationProblem = CMOptimizationProblem(symbols)
    }
}

public fun CMOptimizationProblem.initialGuess(vararg pairs: Pair<Symbol, Double>): Unit = initialGuess(pairs.toMap())
public fun CMOptimizationProblem.simplexSteps(vararg pairs: Pair<Symbol, Double>): Unit = simplexSteps(pairs.toMap())
