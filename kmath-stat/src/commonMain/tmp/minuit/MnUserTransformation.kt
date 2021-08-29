/* 
 * Copyright 2015 Alexander Nozik.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.inr.mass.minuit

import org.apache.commons.math3.linear.ArrayRealVector

/**
 * knows how to andThen between user specified parameters (external) and
 * internal parameters used for minimization
 *
 * Жуткий октопус, который занимается преобразованием внешних параметров во внутренние
 * TODO по возможности отказаться от использования этого монстра
 * @version $Id$
 */
class MnUserTransformation {
    private val nameMap: MutableMap<String?, Int> = HashMap()
    private var theCache: MutableList<Double>
    private var theExtOfInt: MutableList<Int>
    private var theParameters: MutableList<MinuitParameter>
    private var thePrecision: MnMachinePrecision

    constructor() {
        thePrecision = MnMachinePrecision()
        theParameters = java.util.ArrayList<MinuitParameter>()
        theExtOfInt = java.util.ArrayList<Int>()
        theCache = java.util.ArrayList<Double>(0)
    }

    private constructor(other: MnUserTransformation) {
        thePrecision = other.thePrecision
        theParameters = java.util.ArrayList<MinuitParameter>(other.theParameters.size)
        for (par in other.theParameters) {
            theParameters.add(par.copy())
        }
        theExtOfInt = java.util.ArrayList<Int>(other.theExtOfInt)
        theCache = java.util.ArrayList<Double>(other.theCache)
    }

    constructor(par: DoubleArray, err: DoubleArray) {
        thePrecision = MnMachinePrecision()
        theParameters = java.util.ArrayList<MinuitParameter>(par.size)
        theExtOfInt = java.util.ArrayList<Int>(par.size)
        theCache = java.util.ArrayList<Double>(par.size)
        for (i in par.indices) {
            add("p$i", par[i], err[i])
        }
    }

    /**
     * add free parameter
     * @param err
     * @param val
     */
    fun add(name: String, `val`: Double, err: Double) {
        require(!nameMap.containsKey(name)) { "duplicate name: $name" }
        nameMap[name] = theParameters.size
        theExtOfInt.add(theParameters.size)
        theCache.add(`val`)
        theParameters.add(MinuitParameter(theParameters.size, name, `val`, err))
    }

    /**
     * add limited parameter
     * @param up
     * @param low
     */
    fun add(name: String, `val`: Double, err: Double, low: Double, up: Double) {
        require(!nameMap.containsKey(name)) { "duplicate name: $name" }
        nameMap[name] = theParameters.size
        theExtOfInt.add(theParameters.size)
        theCache.add(`val`)
        theParameters.add(MinuitParameter(theParameters.size, name, `val`, err, low, up))
    }

    /**
     * add parameter
     * @param name
     * @param val
     */
    fun add(name: String, `val`: Double) {
        require(!nameMap.containsKey(name)) { "duplicate name: $name" }
        nameMap[name] = theParameters.size
        theCache.add(`val`)
        theParameters.add(MinuitParameter(theParameters.size, name, `val`))
    }

    /**
     *
     * copy.
     *
     * @return a [hep.dataforge.MINUIT.MnUserTransformation] object.
     */
    fun copy(): MnUserTransformation {
        return MnUserTransformation(this)
    }

    fun dInt2Ext(i: Int, `val`: Double): Double {
        var dd = 1.0
        val parm: MinuitParameter = theParameters[theExtOfInt[i]]
        if (parm.hasLimits()) {
            dd = if (parm.hasUpperLimit() && parm.hasLowerLimit()) {
                theDoubleLimTrafo.dInt2Ext(`val`,
                    parm.upperLimit(),
                    parm.lowerLimit())
            } else if (parm.hasUpperLimit() && !parm.hasLowerLimit()) {
                theUpperLimTrafo.dInt2Ext(`val`, parm.upperLimit())
            } else {
                theLowerLimTrafo.dInt2Ext(`val`, parm.lowerLimit())
            }
        }
        return dd
    }

    fun error(index: Int): Double {
        return theParameters[index].error()
    }

    fun error(name: String?): Double {
        return error(index(name))
    }

    fun errors(): DoubleArray {
        val result = DoubleArray(theParameters.size)
        var i = 0
        for (parameter in theParameters) {
            result[i++] = parameter.error()
        }
        return result
    }

    fun ext2int(i: Int, `val`: Double): Double {
        val parm: MinuitParameter = theParameters[i]
        return if (parm.hasLimits()) {
            if (parm.hasUpperLimit() && parm.hasLowerLimit()) {
                theDoubleLimTrafo.ext2int(`val`,
                    parm.upperLimit(),
                    parm.lowerLimit(),
                    precision())
            } else if (parm.hasUpperLimit() && !parm.hasLowerLimit()) {
                theUpperLimTrafo.ext2int(`val`,
                    parm.upperLimit(),
                    precision())
            } else {
                theLowerLimTrafo.ext2int(`val`,
                    parm.lowerLimit(),
                    precision())
            }
        } else `val`
    }

    fun extOfInt(internal: Int): Int {
        return theExtOfInt[internal]
    }

    /**
     * interaction via external number of parameter
     * @param index
     */
    fun fix(index: Int) {
        val iind = intOfExt(index)
        theExtOfInt.removeAt(iind)
        theParameters[index].fix()
    }

    /**
     * interaction via name of parameter
     * @param name
     */
    fun fix(name: String?) {
        fix(index(name))
    }

    /**
     * convert name into external number of parameter
     * @param name
     * @return
     */
    fun index(name: String?): Int {
        return nameMap[name]!!
    }

    fun int2ext(i: Int, `val`: Double): Double {
        val parm: MinuitParameter = theParameters[theExtOfInt[i]]
        return if (parm.hasLimits()) {
            if (parm.hasUpperLimit() && parm.hasLowerLimit()) {
                theDoubleLimTrafo.int2ext(`val`,
                    parm.upperLimit(),
                    parm.lowerLimit())
            } else if (parm.hasUpperLimit() && !parm.hasLowerLimit()) {
                theUpperLimTrafo.int2ext(`val`, parm.upperLimit())
            } else {
                theLowerLimTrafo.int2ext(`val`, parm.lowerLimit())
            }
        } else `val`
    }

    fun int2extCovariance(vec: RealVector, cov: MnAlgebraicSymMatrix): MnUserCovariance {
        val result = MnUserCovariance(cov.nrow())
        for (i in 0 until vec.getDimension()) {
            var dxdi = 1.0
            if (theParameters[theExtOfInt[i]].hasLimits()) {
                dxdi = dInt2Ext(i, vec.getEntry(i))
            }
            for (j in i until vec.getDimension()) {
                var dxdj = 1.0
                if (theParameters[theExtOfInt[j]].hasLimits()) {
                    dxdj = dInt2Ext(j, vec.getEntry(j))
                }
                result[i, j] = dxdi * cov[i, j] * dxdj
            }
        }
        return result
    }

    fun int2extError(i: Int, `val`: Double, err: Double): Double {
        var dx = err
        val parm: MinuitParameter = theParameters[theExtOfInt[i]]
        if (parm.hasLimits()) {
            val ui = int2ext(i, `val`)
            var du1 = int2ext(i, `val` + dx) - ui
            val du2 = int2ext(i, `val` - dx) - ui
            if (parm.hasUpperLimit() && parm.hasLowerLimit()) {
                if (dx > 1.0) {
                    du1 = parm.upperLimit() - parm.lowerLimit()
                }
                dx = 0.5 * (abs(du1) + abs(du2))
            } else {
                dx = 0.5 * (abs(du1) + abs(du2))
            }
        }
        return dx
    }

    fun intOfExt(ext: Int): Int {
        for (iind in theExtOfInt.indices) {
            if (ext == theExtOfInt[iind]) {
                return iind
            }
        }
        throw IllegalArgumentException("ext=$ext")
    }

    /**
     * convert external number into name of parameter
     * @param index
     * @return
     */
    fun name(index: Int): String {
        return theParameters[index].name()
    }

    /**
     * access to single parameter
     * @param index
     * @return
     */
    fun parameter(index: Int): MinuitParameter {
        return theParameters[index]
    }

    fun parameters(): List<MinuitParameter> {
        return theParameters
    }

    //access to parameters and errors in column-wise representation
    fun params(): DoubleArray {
        val result = DoubleArray(theParameters.size)
        var i = 0
        for (parameter in theParameters) {
            result[i++] = parameter.value()
        }
        return result
    }

    fun precision(): MnMachinePrecision {
        return thePrecision
    }

    fun release(index: Int) {
        require(!theExtOfInt.contains(index)) { "index=$index" }
        theExtOfInt.add(index)
        Collections.sort<Int>(theExtOfInt)
        theParameters[index].release()
    }

    fun release(name: String?) {
        release(index(name))
    }

    fun removeLimits(index: Int) {
        theParameters[index].removeLimits()
    }

    fun removeLimits(name: String?) {
        removeLimits(index(name))
    }

    fun setError(index: Int, err: Double) {
        theParameters[index].setError(err)
    }

    fun setError(name: String?, err: Double) {
        setError(index(name), err)
    }

    fun setLimits(index: Int, low: Double, up: Double) {
        theParameters[index].setLimits(low, up)
    }

    fun setLimits(name: String?, low: Double, up: Double) {
        setLimits(index(name), low, up)
    }

    fun setLowerLimit(index: Int, low: Double) {
        theParameters[index].setLowerLimit(low)
    }

    fun setLowerLimit(name: String?, low: Double) {
        setLowerLimit(index(name), low)
    }

    fun setPrecision(eps: Double) {
        thePrecision.setPrecision(eps)
    }

    fun setUpperLimit(index: Int, up: Double) {
        theParameters[index].setUpperLimit(up)
    }

    fun setUpperLimit(name: String?, up: Double) {
        setUpperLimit(index(name), up)
    }

    fun setValue(index: Int, `val`: Double) {
        theParameters[index].setValue(`val`)
        theCache[index] = `val`
    }

    fun setValue(name: String?, `val`: Double) {
        setValue(index(name), `val`)
    }

    fun transform(pstates: RealVector): ArrayRealVector {
        // FixMe: Worry about efficiency here
        val result = ArrayRealVector(theCache.size)
        for (i in 0 until result.getDimension()) {
            result.setEntry(i, theCache[i])
        }
        for (i in 0 until pstates.getDimension()) {
            if (theParameters[theExtOfInt[i]].hasLimits()) {
                result.setEntry(theExtOfInt[i], int2ext(i, pstates.getEntry(i)))
            } else {
                result.setEntry(theExtOfInt[i], pstates.getEntry(i))
            }
        }
        return result
    }

    //forwarded interface
    fun value(index: Int): Double {
        return theParameters[index].value()
    }

    fun value(name: String?): Double {
        return value(index(name))
    }

    fun variableParameters(): Int {
        return theExtOfInt.size
    }

    companion object {
        private val theDoubleLimTrafo: SinParameterTransformation = SinParameterTransformation()
        private val theLowerLimTrafo: SqrtLowParameterTransformation = SqrtLowParameterTransformation()
        private val theUpperLimTrafo: SqrtUpParameterTransformation = SqrtUpParameterTransformation()
    }
}