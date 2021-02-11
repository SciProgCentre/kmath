package kscience.kmath.histogram


/**
 * Univariate histogram with log(n) bin search speed
 */
//private abstract class AbstractUnivariateHistogram<B: UnivariateBin>{
//
//    public abstract val bins: TreeMap<Double, B>
//
//    public open operator fun get(value: Double): B? {
//        // check ceiling entry and return it if it is what needed
//        val ceil = bins.ceilingEntry(value)?.value
//        if (ceil != null && value in ceil) return ceil
//        //check floor entry
//        val floor = bins.floorEntry(value)?.value
//        if (floor != null && value in floor) return floor
//        //neither is valid, not found
//        return null
//    }

//    public override operator fun get(point: Buffer<out Double>): B? = get(point[0])
//
//    public override val dimension: Int get() = 1
//
//    public override operator fun iterator(): Iterator<B> = bins.values.iterator()
//
//    public companion object {

//    }
//}


