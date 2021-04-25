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

import ru.inr.mass.minuit.*

/**
 * Result of the minimization.
 *
 *
 * The FunctionMinimum is the output of the minimizers and contains the
 * minimization result. The methods
 *
 *  * userState(),
 *  * userParameters() and
 *  * userCovariance()
 *
 * are provided. These can be used as new input to a new minimization after some
 * manipulation. The parameters and/or the FunctionMinimum can be printed using
 * the toString() method or the MnPrint class.
 *
 * @author Darksnake
 */
class FunctionMinimum {
    private var theAboveMaxEdm = false
    private var theErrorDef: Double
    private var theReachedCallLimit = false
    private var theSeed: MinimumSeed
    private var theStates: MutableList<MinimumState>
    private var theUserState: MnUserParameterState

    internal constructor(seed: MinimumSeed, up: Double) {
        theSeed = seed
        theStates = ArrayList()
        theStates.add(MinimumState(seed.parameters(),
            seed.error(),
            seed.gradient(),
            seed.parameters().fval(),
            seed.nfcn()))
        theErrorDef = up
        theUserState = MnUserParameterState()
    }

    internal constructor(seed: MinimumSeed, states: MutableList<MinimumState>, up: Double) {
        theSeed = seed
        theStates = states
        theErrorDef = up
        theUserState = MnUserParameterState()
    }

    internal constructor(seed: MinimumSeed, states: MutableList<MinimumState>, up: Double, x: MnReachedCallLimit?) {
        theSeed = seed
        theStates = states
        theErrorDef = up
        theReachedCallLimit = true
        theUserState = MnUserParameterState()
    }

    internal constructor(seed: MinimumSeed, states: MutableList<MinimumState>, up: Double, x: MnAboveMaxEdm?) {
        theSeed = seed
        theStates = states
        theErrorDef = up
        theAboveMaxEdm = true
        theReachedCallLimit = false
        theUserState = MnUserParameterState()
    }

    // why not
    fun add(state: MinimumState) {
        theStates.add(state)
    }

    /**
     * returns the expected vertical distance to the minimum (EDM)
     *
     * @return a double.
     */
    fun edm(): Double {
        return lastState().edm()
    }

    fun error(): MinimumError {
        return lastState().error()
    }

    /**
     *
     *
     * errorDef.
     *
     * @return a double.
     */
    fun errorDef(): Double {
        return theErrorDef
    }

    /**
     * Returns the function value at the minimum.
     *
     * @return a double.
     */
    fun fval(): Double {
        return lastState().fval()
    }

    fun grad(): FunctionGradient {
        return lastState().gradient()
    }

    fun hasAccurateCovar(): Boolean {
        return state().error().isAccurate()
    }

    fun hasCovariance(): Boolean {
        return state().error().isAvailable()
    }

    fun hasMadePosDefCovar(): Boolean {
        return state().error().isMadePosDef()
    }

    fun hasPosDefCovar(): Boolean {
        return state().error().isPosDef()
    }

    fun hasReachedCallLimit(): Boolean {
        return theReachedCallLimit
    }

    fun hasValidCovariance(): Boolean {
        return state().error().isValid()
    }

    fun hasValidParameters(): Boolean {
        return state().parameters().isValid()
    }

    fun hesseFailed(): Boolean {
        return state().error().hesseFailed()
    }

    fun isAboveMaxEdm(): Boolean {
        return theAboveMaxEdm
    }

    /**
     * In general, if this returns <CODE>true</CODE>, the minimizer did find a
     * minimum without running into troubles. However, in some cases a minimum
     * cannot be found, then the return value will be <CODE>false</CODE>.
     * Reasons for the minimization to fail are
     *
     *  * the number of allowed function calls has been exhausted
     *  * the minimizer could not improve the values of the parameters (and
     * knowing that it has not converged yet)
     *  * a problem with the calculation of the covariance matrix
     *
     * Additional methods for the analysis of the state at the minimum are
     * provided.
     *
     * @return a boolean.
     */
    fun isValid(): Boolean {
        return state().isValid() && !isAboveMaxEdm() && !hasReachedCallLimit()
    }

    private fun lastState(): MinimumState {
        return theStates[theStates.size - 1]
    }
    // forward interface of last state
    /**
     * returns the total number of function calls during the minimization.
     *
     * @return a int.
     */
    fun nfcn(): Int {
        return lastState().nfcn()
    }

    fun parameters(): MinimumParameters {
        return lastState().parameters()
    }

    fun seed(): MinimumSeed {
        return theSeed
    }

    fun state(): MinimumState {
        return lastState()
    }

    fun states(): List<MinimumState> {
        return theStates
    }

    /**
     * {@inheritDoc}
     *
     * @return
     */
    override fun toString(): String {
        return MnPrint.toString(this)
    }

    /**
     *
     *
     * userCovariance.
     *
     * @return a [hep.dataforge.MINUIT.MnUserCovariance] object.
     */
    fun userCovariance(): MnUserCovariance {
        if (!theUserState.isValid()) {
            theUserState = MnUserParameterState(state(), errorDef(), seed().trafo())
        }
        return theUserState.covariance()
    }

    /**
     *
     *
     * userParameters.
     *
     * @return a [hep.dataforge.MINUIT.MnUserParameters] object.
     */
    fun userParameters(): MnUserParameters {
        if (!theUserState.isValid()) {
            theUserState = MnUserParameterState(state(), errorDef(), seed().trafo())
        }
        return theUserState.parameters()
    }

    /**
     * user representation of state at minimum
     *
     * @return a [hep.dataforge.MINUIT.MnUserParameterState] object.
     */
    fun userState(): MnUserParameterState {
        if (!theUserState.isValid()) {
            theUserState = MnUserParameterState(state(), errorDef(), seed().trafo())
        }
        return theUserState
    }

    internal class MnAboveMaxEdm
    internal class MnReachedCallLimit
}