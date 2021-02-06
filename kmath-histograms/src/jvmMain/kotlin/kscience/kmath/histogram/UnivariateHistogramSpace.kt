package kscience.kmath.histogram

import kscience.kmath.operations.Space

public class UnivariateHistogramSpace(public val binFactory: (Double) -> UnivariateBin) : Space<UnivariateHistogram> {

    public fun builder(): UnivariateHistogramBuilder = UnivariateHistogramBuilder(this)

    public fun produce(builder: UnivariateHistogramBuilder.() -> Unit): UnivariateHistogram = builder().apply(builder)

    override fun add(
        a: UnivariateHistogram,
        b: UnivariateHistogram,
    ): UnivariateHistogram {
        require(a.context == this){"Histogram $a does not belong to this context"}
        require(b.context == this){"Histogram $b does not belong to this context"}
        TODO()
    }

    override fun multiply(a: UnivariateHistogram, k: Number): UnivariateHistogram {
        TODO("Not yet implemented")
    }

    override val zero: UnivariateHistogram = produce {  }
}