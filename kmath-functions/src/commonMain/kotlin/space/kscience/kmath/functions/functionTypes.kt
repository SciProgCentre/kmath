package space.kscience.kmath.functions

import space.kscience.kmath.structures.Buffer

public typealias UnivariateFunction<T> = (T) -> T

public typealias MultivariateFunction<T> = (Buffer<T>) -> T