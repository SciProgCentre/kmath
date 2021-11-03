package space.kscience.kmath.operations

/**
 * Check if number is an integer
 */
public actual fun Number.isInteger(): Boolean = (this is Int) || (this is Long) || (this is Short) || (this.toDouble() % 1 == 0.0)