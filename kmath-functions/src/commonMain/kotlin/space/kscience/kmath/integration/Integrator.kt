package space.kscience.kmath.integration

/**
 * A general interface for all integrators.
 */
public interface Integrator<I : Integrand> {
    /**
     * Runs one integration pass and return a new [Integrand] with a new set of features.
     */
    public fun integrate(integrand: I): I
}
