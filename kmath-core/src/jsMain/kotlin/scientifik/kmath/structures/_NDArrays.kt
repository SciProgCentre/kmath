package scientifik.kmath.structures

import scientifik.kmath.operations.DoubleField

/**
 * Using boxing implementation for js
 */
actual val realNDFieldFactory: NDFieldFactory<Double> = NDArrays.createSimpleNDFieldFactory(DoubleField)