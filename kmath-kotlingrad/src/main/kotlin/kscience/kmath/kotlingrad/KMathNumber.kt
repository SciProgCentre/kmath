package kscience.kmath.kotlingrad

import edu.umontreal.kotlingrad.experimental.RealNumber
import edu.umontreal.kotlingrad.experimental.SConst
import kscience.kmath.operations.NumericAlgebra

/**
 * Implements [RealNumber] by delegating its functionality to [NumericAlgebra].
 *
 * @param T the type of number.
 * @param A the [NumericAlgebra] of [T].
 * @property algebra the algebra.
 * @param value the value of this number.
 */
public class KMathNumber<T, A>(public val algebra: A, value: T) :
    RealNumber<KMathNumber<T, A>, T>(value) where T : Number, A : NumericAlgebra<T> {
    public override fun wrap(number: Number): SConst<KMathNumber<T, A>> = SConst(algebra.number(number))
}
