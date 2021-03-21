package space.kscience.kmath.integration

import space.kscience.kmath.linear.Point
import kotlin.reflect.KClass

public class MultivariateIntegrand<T : Any> internal constructor(
    private val features: Map<KClass<*>, IntegrandFeature>,
    public val function: (Point<T>) -> T,
) : Integrand {

    @Suppress("UNCHECKED_CAST")
    override fun <T : IntegrandFeature> getFeature(type: KClass<T>): T? = features[type] as? T

    public operator fun <F : IntegrandFeature> plus(pair: Pair<KClass<out F>, F>): MultivariateIntegrand<T> =
        MultivariateIntegrand(features + pair, function)

    public operator fun <F : IntegrandFeature> plus(feature: F): MultivariateIntegrand<T> =
        plus(feature::class to feature)
}

@Suppress("FunctionName")
public fun <T : Any> MultivariateIntegrand(
    vararg features: IntegrandFeature,
    function: (Point<T>) -> T,
): MultivariateIntegrand<T> = MultivariateIntegrand(features.associateBy { it::class }, function)

public val <T : Any> MultivariateIntegrand<T>.value: T? get() = getFeature<IntegrandValue<T>>()?.value
