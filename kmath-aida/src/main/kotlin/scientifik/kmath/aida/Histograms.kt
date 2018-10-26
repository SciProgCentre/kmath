package scientifik.kmath.aida

import hep.aida.IHistogram1D
import hep.aida.IHistogram2D

fun Iterable<Double>.histogram(range: ClosedFloatingPointRange<Double>, bins: Int = 100, path: String = ""): IHistogram1D{
    val h1d = Histograms.create1D(range,bins,path)
    forEach{
        h1d.fill(it)
    }
    return h1d
}


object Histograms {
    fun create1D(range: ClosedFloatingPointRange<Double>, bins: Int = 100, path: String = ""): IHistogram1D {
        return histogramFactory.createHistogram1D(path, bins, range.start, range.endInclusive)
    }

    fun create2D(xRange: ClosedFloatingPointRange<Double>, yRange: ClosedFloatingPointRange<Double>, xBins: Int = 100, yBins: Int = 100, path: String = ""): IHistogram2D {
        return histogramFactory.createHistogram2D(path, xBins, xRange.start, xRange.endInclusive, yBins, yRange.start, yRange.endInclusive)
    }
}