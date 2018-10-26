package scientifik.kmath.aida

import hep.aida.IAnalysisFactory
import hep.aida.IHistogramFactory
import hep.aida.ITreeFactory

val analysisFactory: IAnalysisFactory by lazy { IAnalysisFactory.create() }
val treeFactory: ITreeFactory by lazy { analysisFactory.createTreeFactory() }
val histogramFactory: IHistogramFactory by lazy { analysisFactory.createHistogramFactory(treeFactory.create()) }