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
 * MnCross class.
 *
 * @version $Id$
 * @author Darksnake
 */
class MnCross {
    private var theLimset = false
    private var theMaxFcn = false
    private var theNFcn = 0
    private var theNewMin = false
    private var theState: MnUserParameterState
    private var theValid = false
    private var theValue = 0.0

    internal constructor() {
        theState = MnUserParameterState()
    }

    internal constructor(nfcn: Int) {
        theState = MnUserParameterState()
        theNFcn = nfcn
    }

    internal constructor(value: Double, state: MnUserParameterState, nfcn: Int) {
        theValue = value
        theState = state
        theNFcn = nfcn
        theValid = true
    }

    internal constructor(state: MnUserParameterState, nfcn: Int, x: CrossParLimit?) {
        theState = state
        theNFcn = nfcn
        theLimset = true
    }

    internal constructor(state: MnUserParameterState, nfcn: Int, x: CrossFcnLimit?) {
        theState = state
        theNFcn = nfcn
        theMaxFcn = true
    }

    internal constructor(state: MnUserParameterState, nfcn: Int, x: CrossNewMin?) {
        theState = state
        theNFcn = nfcn
        theNewMin = true
    }

    fun atLimit(): Boolean {
        return theLimset
    }

    fun atMaxFcn(): Boolean {
        return theMaxFcn
    }

    fun isValid(): Boolean {
        return theValid
    }

    fun newMinimum(): Boolean {
        return theNewMin
    }

    fun nfcn(): Int {
        return theNFcn
    }

    fun state(): MnUserParameterState {
        return theState
    }

    fun value(): Double {
        return theValue
    }

    internal class CrossFcnLimit
    internal class CrossNewMin
    internal class CrossParLimit
}