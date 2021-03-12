package space.kscience.kmath.integration

import kotlin.reflect.KClass

public interface IntegrandFeature

public interface Integrand {
    public fun <T : IntegrandFeature> getFeature(type: KClass<T>): T? = null
}