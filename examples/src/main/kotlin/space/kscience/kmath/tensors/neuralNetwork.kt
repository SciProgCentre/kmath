/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.tensors

import space.kscience.kmath.nd.ShapeND
import space.kscience.kmath.nd.contentEquals
import space.kscience.kmath.operations.asIterable
import space.kscience.kmath.operations.invoke
import space.kscience.kmath.tensors.core.*
import kotlin.math.sqrt

const val seed = 100500L

// Simple feedforward neural network with backpropagation training

// interface of network layer
interface Layer {
    fun forward(input: DoubleTensor): DoubleTensor
    fun backward(input: DoubleTensor, outputError: DoubleTensor): DoubleTensor
}

// activation layer
open class Activation(
    val activation: (DoubleTensor) -> DoubleTensor,
    val activationDer: (DoubleTensor) -> DoubleTensor,
) : Layer {
    override fun forward(input: DoubleTensor): DoubleTensor {
        return activation(input)
    }

    override fun backward(input: DoubleTensor, outputError: DoubleTensor): DoubleTensor {
        return DoubleTensorAlgebra { outputError * activationDer(input) }
    }
}

fun relu(x: DoubleTensor): DoubleTensor = DoubleTensorAlgebra {
    x.map { if (it > 0) it else 0.0 }
}

fun reluDer(x: DoubleTensor): DoubleTensor = DoubleTensorAlgebra {
    x.map { if (it > 0) 1.0 else 0.0 }
}

// activation layer with relu activator
class ReLU : Activation(::relu, ::reluDer)

fun sigmoid(x: DoubleTensor): DoubleTensor = DoubleTensorAlgebra {
    1.0 / (1.0 + exp((-x)))
}

fun sigmoidDer(x: DoubleTensor): DoubleTensor = DoubleTensorAlgebra {
    sigmoid(x) * (1.0 - sigmoid(x))
}

// activation layer with sigmoid activator
class Sigmoid : Activation(::sigmoid, ::sigmoidDer)

// dense layer
class Dense(
    private val inputUnits: Int,
    private val outputUnits: Int,
    private val learningRate: Double = 0.1,
) : Layer {

    private val weights: DoubleTensor = DoubleTensorAlgebra {
        randomNormal(
            ShapeND(inputUnits, outputUnits),
            seed
        ) * sqrt(2.0 / (inputUnits + outputUnits))
    }

    private val bias: DoubleTensor = DoubleTensorAlgebra { zeros(ShapeND(outputUnits)) }

    override fun forward(input: DoubleTensor): DoubleTensor = BroadcastDoubleTensorAlgebra {
        (input dot weights) + bias
    }

    override fun backward(input: DoubleTensor, outputError: DoubleTensor): DoubleTensor = DoubleTensorAlgebra {
        val gradInput = outputError dot weights.transposed()

        val gradW = input.transposed() dot outputError
        val gradBias = mean(structureND = outputError, dim = 0, keepDim = false) * input.shape[0].toDouble()

        weights -= learningRate * gradW
        bias -= learningRate * gradBias

        gradInput
    }

}

// simple accuracy equal to the proportion of correct answers
fun accuracy(yPred: DoubleTensor, yTrue: DoubleTensor): Double {
    check(yPred.shape contentEquals yTrue.shape)
    val n = yPred.shape[0]
    var correctCnt = 0
    for (i in 0 until n) {
        if (yPred[intArrayOf(i, 0)] == yTrue[intArrayOf(i, 0)]) {
            correctCnt += 1
        }
    }
    return correctCnt.toDouble() / n.toDouble()
}

// neural network class
class NeuralNetwork(private val layers: List<Layer>) {
    private fun softMaxLoss(yPred: DoubleTensor, yTrue: DoubleTensor): DoubleTensor = BroadcastDoubleTensorAlgebra {

        val onesForAnswers = zeroesLike(yPred)
        yTrue.source.asIterable().forEachIndexed { index, labelDouble ->
            val label = labelDouble.toInt()
            onesForAnswers[intArrayOf(index, label)] = 1.0
        }

        val softmaxValue = exp(yPred) / exp(yPred).sum(dim = 1, keepDim = true)

        (-onesForAnswers + softmaxValue) / (yPred.shape[0].toDouble())
    }


    private fun forward(x: DoubleTensor): List<DoubleTensor> {
        var input = x

        return buildList {
            layers.forEach { layer ->
                val output = layer.forward(input)
                add(output)
                input = output
            }
        }
    }

    private fun train(xTrain: DoubleTensor, yTrain: DoubleTensor) {
        val layerInputs = buildList {
            add(xTrain)
            addAll(forward(xTrain))
        }

        var lossGrad = softMaxLoss(layerInputs.last(), yTrain)

        layers.zip(layerInputs).reversed().forEach { (layer, input) ->
            lossGrad = layer.backward(input, lossGrad)
        }
    }

    fun fit(xTrain: DoubleTensor, yTrain: DoubleTensor, batchSize: Int, epochs: Int) = DoubleTensorAlgebra {
        fun iterBatch(x: DoubleTensor, y: DoubleTensor): Sequence<Pair<DoubleTensor, DoubleTensor>> = sequence {
            val n = x.shape[0]
            val shuffledIndices = (0 until n).shuffled()
            for (i in 0 until n step batchSize) {
                val excerptIndices = shuffledIndices.drop(i).take(batchSize).toIntArray()
                val batch = x.rowsByIndices(excerptIndices) to y.rowsByIndices(excerptIndices)
                yield(batch)
            }
        }

        for (epoch in 0 until epochs) {
            println("Epoch ${epoch + 1}/$epochs")
            for ((xBatch, yBatch) in iterBatch(xTrain, yTrain)) {
                train(xBatch, yBatch)
            }
            println("Accuracy:${accuracy(yTrain, predict(xTrain).argMax(1, true).toDoubleTensor())}")
        }
    }

    fun predict(x: DoubleTensor): DoubleTensor {
        return forward(x).last()
    }

}


fun main() = BroadcastDoubleTensorAlgebra {
    val features = 5
    val sampleSize = 250
    val trainSize = 180
    //val testSize = sampleSize - trainSize

    // take sample of features from normal distribution
    val x = randomNormal(ShapeND(sampleSize, features), seed) * 2.5

    x += fromArray(
        ShapeND(5),
        doubleArrayOf(0.0, -1.0, -2.5, -3.0, 5.5) // row means
    )


    // define class like '1' if the sum of features > 0 and '0' otherwise
    val y = fromArray(
        ShapeND(sampleSize, 1),
        DoubleArray(sampleSize) { i ->
            if (x.getTensor(i).sum() > 0.0) {
                1.0
            } else {
                0.0
            }
        }
    )

    // split train ans test
    val trainIndices = (0 until trainSize).toList().toIntArray()
    val testIndices = (trainSize until sampleSize).toList().toIntArray()

    val xTrain = x.rowsByIndices(trainIndices)
    val yTrain = y.rowsByIndices(trainIndices)

    val xTest = x.rowsByIndices(testIndices)
    val yTest = y.rowsByIndices(testIndices)

    // build model
    val layers = buildList {
        add(Dense(features, 64))
        add(ReLU())
        add(Dense(64, 16))
        add(ReLU())
        add(Dense(16, 2))
        add(Sigmoid())
    }
    val model = NeuralNetwork(layers)

    // fit it with train data
    model.fit(xTrain, yTrain, batchSize = 20, epochs = 10)

    // make prediction
    val prediction = model.predict(xTest)

    // process raw prediction via argMax
    val predictionLabels = prediction.argMax(1, true).toDoubleTensor()

    // find out accuracy
    val acc = accuracy(yTest, predictionLabels)
    println("Test accuracy:$acc")

}
