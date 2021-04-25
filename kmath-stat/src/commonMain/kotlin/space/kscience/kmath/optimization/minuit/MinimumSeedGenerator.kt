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
 * base class for seed generators (starting values); the seed generator prepares
 * initial starting values from the input (MnUserParameterState) for the
 * minimization;
 *
 * @version $Id$
 */
interface MinimumSeedGenerator {
    /**
     *
     * generate.
     *
     * @param fcn a [hep.dataforge.MINUIT.MnFcn] object.
     * @param calc a [hep.dataforge.MINUIT.GradientCalculator] object.
     * @param user a [hep.dataforge.MINUIT.MnUserParameterState] object.
     * @param stra a [hep.dataforge.MINUIT.MnStrategy] object.
     * @return a [hep.dataforge.MINUIT.MinimumSeed] object.
     */
    fun generate(fcn: MnFcn?, calc: GradientCalculator?, user: MnUserParameterState?, stra: MnStrategy?): MinimumSeed
}