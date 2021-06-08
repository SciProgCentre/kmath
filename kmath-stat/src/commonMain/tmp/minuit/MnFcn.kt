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

import ru.inr.mass.maths.MultiFunction

/**
 * Функция, которая помнит количество вызовов себя и ErrorDef
 * @version $Id$
 */
class MnFcn(fcn: MultiFunction?, errorDef: Double) {
    private val theErrorDef: Double
    private val theFCN: MultiFunction?
    protected var theNumCall: Int
    fun errorDef(): Double {
        return theErrorDef
    }

    fun fcn(): MultiFunction? {
        return theFCN
    }

    fun numOfCalls(): Int {
        return theNumCall
    }

    fun value(v: RealVector): Double {
        theNumCall++
        return theFCN.value(v.toArray())
    }

    init {
        theFCN = fcn
        theNumCall = 0
        theErrorDef = errorDef
    }
}