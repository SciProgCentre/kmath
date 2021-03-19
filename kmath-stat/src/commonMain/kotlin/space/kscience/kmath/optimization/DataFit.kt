package space.kscience.kmath.optimization

import space.kscience.kmath.expressions.DifferentiableExpression
import space.kscience.kmath.expressions.StringSymbol
import space.kscience.kmath.expressions.Symbol
import space.kscience.kmath.structures.Buffer

public interface DataFit<T : Any> : Optimization<T> {

    public fun modelAndData(
        x: Buffer<T>,
        y: Buffer<T>,
        yErr: Buffer<T>,
        model: DifferentiableExpression<T, *>,
        xSymbol: Symbol = StringSymbol("x"),
    )
}