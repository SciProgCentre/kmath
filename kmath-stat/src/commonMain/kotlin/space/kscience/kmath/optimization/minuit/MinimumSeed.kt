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
class MinimumSeed(state: MinimumState, trafo: MnUserTransformation) {
    private val theState: MinimumState = state
    private val theTrafo: MnUserTransformation = trafo
    private val theValid: Boolean = true
    val edm: Double get() = state().edm()

    fun error(): MinimumError {
        return state().error()
    }

    fun fval(): Double {
        return state().fval()
    }

    fun gradient(): FunctionGradient {
        return state().gradient()
    }

    fun isValid(): Boolean {
        return theValid
    }

    fun nfcn(): Int {
        return state().nfcn()
    }

    fun parameters(): MinimumParameters {
        return state().parameters()
    }

    fun precision(): MnMachinePrecision {
        return theTrafo.precision()
    }

    fun state(): MinimumState {
        return theState
    }

    fun trafo(): MnUserTransformation {
        return theTrafo
    }

}