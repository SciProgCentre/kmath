package space.kscience.kmath.operations

/**
 * Check if number is an integer
 */
public actual fun Number.isInteger(): Boolean = js("Number").isInteger(this) as Boolean