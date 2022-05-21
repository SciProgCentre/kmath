/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.noa

import java.io.File
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertTrue

class TestJitModules {

    private val resources = File("").resolve("src/test/resources")
    private val dataPath = resources.resolve("data.pt").absolutePath
    private val netPath = resources.resolve("net.pt").absolutePath
    private val lossPath = resources.resolve("loss.pt").absolutePath

    @Test
    fun testOptimisationAdam() = NoaFloat {

        setSeed(SEED)

        val dataModule = loadJitModule(dataPath)
        val netModule = loadJitModule(netPath)
        val lossModule = loadJitModule(lossPath)

        val xTrain = dataModule.getBuffer("x_train")
        val yTrain = dataModule.getBuffer("y_train")
        val xVal = dataModule.getBuffer("x_val")
        val yVal = dataModule.getBuffer("y_val")

        netModule.train(true)
        lossModule.setBuffer("target", yTrain)

        val yPred = netModule.forward(xTrain)
        val loss = lossModule.forward(yPred)
        val optimiser = netModule.adamOptimiser(0.005)

        repeat(250){
            optimiser.zeroGrad()
            netModule.forwardAssign(xTrain, yPred)
            lossModule.forwardAssign(yPred, loss)
            loss.backward()
            optimiser.step()
        }

        netModule.forwardAssign(xVal, yPred)
        lossModule.setBuffer("target", yVal)
        lossModule.forwardAssign(yPred, loss)

        assertTrue(loss.value() < 0.1)
    }!!

    @Test
    fun testOptimisationRms() = NoaFloat {

        setSeed(SEED)

        val dataModule = loadJitModule(dataPath)
        val netModule = loadJitModule(netPath)
        val lossModule = loadJitModule(lossPath)

        val xTrain = dataModule.getBuffer("x_train")
        val yTrain = dataModule.getBuffer("y_train")
        val xVal = dataModule.getBuffer("x_val")
        val yVal = dataModule.getBuffer("y_val")

        netModule.train(true)
        lossModule.setBuffer("target", yTrain)

        val yPred = netModule.forward(xTrain)
        val loss = lossModule.forward(yPred)
        val optimiser = netModule.rmsOptimiser(0.005, 0.99, 1e-08, 0.0, 0.0, false)

        repeat(250){
            optimiser.zeroGrad()
            netModule.forwardAssign(xTrain, yPred)
            lossModule.forwardAssign(yPred, loss)
            loss.backward()
            optimiser.step()
        }

        netModule.forwardAssign(xVal, yPred)
        lossModule.setBuffer("target", yVal)
        lossModule.forwardAssign(yPred, loss)

        assertTrue(loss.value() < 0.1)
    }!!

    @Test
    fun testOptimisationAdamW() = NoaFloat {

        setSeed(SEED)

        val dataModule = loadJitModule(dataPath)
        val netModule = loadJitModule(netPath)
        val lossModule = loadJitModule(lossPath)

        val xTrain = dataModule.getBuffer("x_train")
        val yTrain = dataModule.getBuffer("y_train")
        val xVal = dataModule.getBuffer("x_val")
        val yVal = dataModule.getBuffer("y_val")

        netModule.train(true)
        lossModule.setBuffer("target", yTrain)

        val yPred = netModule.forward(xTrain)
        val loss = lossModule.forward(yPred)
        val optimiser = netModule.adamWOptimiser(0.005, 0.9, 0.999, 1e-08, 0.01, false)

        repeat(250){
            optimiser.zeroGrad()
            netModule.forwardAssign(xTrain, yPred)
            lossModule.forwardAssign(yPred, loss)
            loss.backward()
            optimiser.step()
        }

        netModule.forwardAssign(xVal, yPred)
        lossModule.setBuffer("target", yVal)
        lossModule.forwardAssign(yPred, loss)

        assertTrue(loss.value() < 0.1)
    }!!

    @Test
    fun testOptimisationAdagrad() = NoaFloat {

        setSeed(SEED)

        val dataModule = loadJitModule(dataPath)
        val netModule = loadJitModule(netPath)
        val lossModule = loadJitModule(lossPath)

        val xTrain = dataModule.getBuffer("x_train")
        val yTrain = dataModule.getBuffer("y_train")
        val xVal = dataModule.getBuffer("x_val")
        val yVal = dataModule.getBuffer("y_val")

        netModule.train(true)
        lossModule.setBuffer("target", yTrain)

        val yPred = netModule.forward(xTrain)
        val loss = lossModule.forward(yPred)
        val optimiser = netModule.adagradOptimiser(0.05, 0.0, 0.0, 0.0, 1e-10)

        repeat(250){
            optimiser.zeroGrad()
            netModule.forwardAssign(xTrain, yPred)
            lossModule.forwardAssign(yPred, loss)
            loss.backward()
            optimiser.step()
        }

        netModule.forwardAssign(xVal, yPred)
        lossModule.setBuffer("target", yVal)
        lossModule.forwardAssign(yPred, loss)

        assertTrue(loss.value() < 0.1)
    }!!

    @Test
    fun testOptimisationSgd() = NoaFloat {

        setSeed(SEED)

        val dataModule = loadJitModule(dataPath)
        val netModule = loadJitModule(netPath)
        val lossModule = loadJitModule(lossPath)

        val xTrain = dataModule.getBuffer("x_train")
        val yTrain = dataModule.getBuffer("y_train")
        val xVal = dataModule.getBuffer("x_val")
        val yVal = dataModule.getBuffer("y_val")

        netModule.train(true)
        lossModule.setBuffer("target", yTrain)

        val yPred = netModule.forward(xTrain)
        val loss = lossModule.forward(yPred)
        val optimiser = netModule.sgdOptimiser(0.01, 0.9, 0.0, 0.0, false)

        repeat(400){
            optimiser.zeroGrad()
            netModule.forwardAssign(xTrain, yPred)
            lossModule.forwardAssign(yPred, loss)
            loss.backward()
            optimiser.step()
        }

        netModule.forwardAssign(xVal, yPred)
        lossModule.setBuffer("target", yVal)
        lossModule.forwardAssign(yPred, loss)

        assertTrue(loss.value() < 0.1)
    }!!
}