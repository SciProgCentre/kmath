package space.kscience.kmath.ojalgo

import org.ojalgo.algebra.ScalarOperation
import space.kscience.kmath.operations.Field
import space.kscience.kmath.operations.Group
import space.kscience.kmath.operations.Ring
import space.kscience.kmath.operations.invoke

public open class OjalgoGroup<T>(override val zero: T) : Group<T>
        where T : org.ojalgo.algebra.Group.Additive<T> {
    public override fun add(a: T, b: T): T = a.add(b)
    public override fun T.unaryMinus(): T = negate()
}

public open class OjalgoRing<T>(zero: T, override val one: T) : OjalgoGroup<T>(zero),
    Ring<T> where T : org.ojalgo.algebra.Ring<T> {
    public override fun multiply(a: T, b: T): T = a.multiply(b)
}

public open class OjalgoField<T>(zero: T, one: T) : OjalgoRing<T>(zero, one),
    Field<T> where T : org.ojalgo.algebra.Field<T>, T : ScalarOperation.Multiplication<T, *> {
    public override fun divide(a: T, b: T): T = a.divide(b)
    public override fun scale(a: T, value: Double): T = a.multiply(value)
}

internal inline fun <T, R> Field<T>.convert(crossinline tToR: (T) -> R, crossinline rToT: (R) -> T): Field<R> =
    object : Field<R> {
        override val zero: R
            get() = tToR(this@convert.zero)

        override val one: R
            get() = tToR(this@convert.one)

        override fun add(a: R, b: R): R = tToR(this@convert.add(rToT(a), rToT(b)))
        override fun multiply(a: R, b: R): R = tToR(this@convert.multiply(rToT(a), rToT(b)))
        override fun divide(a: R, b: R): R = tToR(this@convert.divide(rToT(a), rToT(b)))
        override fun R.unaryMinus(): R = tToR(this@convert { -rToT(this@unaryMinus) })

        override fun scale(a: R, value: Double): R {
            return tToR(this@convert.scale(rToT(a), value))
        }
    }
