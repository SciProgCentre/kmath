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

/**
 *
 * @version $Id$
 */
class MinuitParameter {
    private var theConst = false
    private var theError = 0.0
    private var theFix = false
    private var theLoLimValid = false
    private var theLoLimit = 0.0
    private var theName: String
    private var theNum: Int
    private var theUpLimValid = false
    private var theUpLimit = 0.0
    private var theValue: Double

    /**
     * constructor for constant parameter
     *
     * @param num a int.
     * @param name a [String] object.
     * @param val a double.
     */
    constructor(num: Int, name: String, `val`: Double) {
        theNum = num
        theValue = `val`
        theConst = true
        theName = name
    }

    /**
     * constructor for standard parameter
     *
     * @param num a int.
     * @param name a [String] object.
     * @param val a double.
     * @param err a double.
     */
    constructor(num: Int, name: String, `val`: Double, err: Double) {
        theNum = num
        theValue = `val`
        theError = err
        theName = name
    }

    /**
     * constructor for limited parameter
     *
     * @param num a int.
     * @param name a [String] object.
     * @param val a double.
     * @param err a double.
     * @param min a double.
     * @param max a double.
     */
    constructor(num: Int, name: String, `val`: Double, err: Double, min: Double, max: Double) {
        theNum = num
        theValue = `val`
        theError = err
        theLoLimit = min
        theUpLimit = max
        theLoLimValid = true
        theUpLimValid = true
        require(min != max) { "min == max" }
        if (min > max) {
            theLoLimit = max
            theUpLimit = min
        }
        theName = name
    }

    private constructor(other: MinuitParameter) {
        theNum = other.theNum
        theName = other.theName
        theValue = other.theValue
        theError = other.theError
        theConst = other.theConst
        theFix = other.theFix
        theLoLimit = other.theLoLimit
        theUpLimit = other.theUpLimit
        theLoLimValid = other.theLoLimValid
        theUpLimValid = other.theUpLimValid
    }

    /**
     *
     * copy.
     *
     * @return a [hep.dataforge.MINUIT.MinuitParameter] object.
     */
    fun copy(): MinuitParameter {
        return MinuitParameter(this)
    }

    /**
     *
     * error.
     *
     * @return a double.
     */
    fun error(): Double {
        return theError
    }

    /**
     *
     * fix.
     */
    fun fix() {
        theFix = true
    }

    /**
     *
     * hasLimits.
     *
     * @return a boolean.
     */
    fun hasLimits(): Boolean {
        return theLoLimValid || theUpLimValid
    }

    /**
     *
     * hasLowerLimit.
     *
     * @return a boolean.
     */
    fun hasLowerLimit(): Boolean {
        return theLoLimValid
    }

    /**
     *
     * hasUpperLimit.
     *
     * @return a boolean.
     */
    fun hasUpperLimit(): Boolean {
        return theUpLimValid
    }
    //state of parameter (fixed/const/limited)
    /**
     *
     * isConst.
     *
     * @return a boolean.
     */
    fun isConst(): Boolean {
        return theConst
    }

    /**
     *
     * isFixed.
     *
     * @return a boolean.
     */
    fun isFixed(): Boolean {
        return theFix
    }

    /**
     *
     * lowerLimit.
     *
     * @return a double.
     */
    fun lowerLimit(): Double {
        return theLoLimit
    }

    /**
     *
     * name.
     *
     * @return a [String] object.
     */
    fun name(): String {
        return theName
    }
    //access methods
    /**
     *
     * number.
     *
     * @return a int.
     */
    fun number(): Int {
        return theNum
    }

    /**
     *
     * release.
     */
    fun release() {
        theFix = false
    }

    /**
     *
     * removeLimits.
     */
    fun removeLimits() {
        theLoLimit = 0.0
        theUpLimit = 0.0
        theLoLimValid = false
        theUpLimValid = false
    }

    /**
     *
     * setError.
     *
     * @param err a double.
     */
    fun setError(err: Double) {
        theError = err
        theConst = false
    }

    /**
     *
     * setLimits.
     *
     * @param low a double.
     * @param up a double.
     */
    fun setLimits(low: Double, up: Double) {
        require(low != up) { "min == max" }
        theLoLimit = low
        theUpLimit = up
        theLoLimValid = true
        theUpLimValid = true
        if (low > up) {
            theLoLimit = up
            theUpLimit = low
        }
    }

    /**
     *
     * setLowerLimit.
     *
     * @param low a double.
     */
    fun setLowerLimit(low: Double) {
        theLoLimit = low
        theUpLimit = 0.0
        theLoLimValid = true
        theUpLimValid = false
    }

    /**
     *
     * setUpperLimit.
     *
     * @param up a double.
     */
    fun setUpperLimit(up: Double) {
        theLoLimit = 0.0
        theUpLimit = up
        theLoLimValid = false
        theUpLimValid = true
    }
    //interaction
    /**
     *
     * setValue.
     *
     * @param val a double.
     */
    fun setValue(`val`: Double) {
        theValue = `val`
    }

    /**
     *
     * upperLimit.
     *
     * @return a double.
     */
    fun upperLimit(): Double {
        return theUpLimit
    }

    /**
     *
     * value.
     *
     * @return a double.
     */
    fun value(): Double {
        return theValue
    }
}