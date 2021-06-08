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
 * API class for defining three levels of strategies: low (0), medium (1), high
 * (2).
 *
 *
 * At many places in the analysis of the FCN (the user provided function),
 * MINUIT must decide whether to be <I>safe</I> and waste a few function calls
 * in order to know where it is, or to be <I>fast</I> and attempt to get the
 * requested results with the fewest possible calls at a certain risk of not
 * obtaining the precision desired by the user. In order to allow the user to
 * infuence these decisions, the MnStrategy class allows the user to control
 * different settings. MnStrategy can be instantiated with three different
 * minimization quality levels for low (0), medium (1) and high (2) quality.
 * Default settings for iteration cycles and tolerances are initialized then.
 *
 *
 * The default setting is set for medium quality. Value 0 (low) indicates to
 * MINUIT that it should economize function calls; it is intended for cases
 * where there are many variable parameters and/or the function takes a long
 * time to calculate and/or the user is not interested in very precise values
 * for parameter errors. On the other hand, value 2 (high) indicates that MINUIT
 * is allowed to waste function calls in order to be sure that all values are
 * precise; it is it is intended for cases where the function is evaluated in a
 * relatively short time and/or where the parameter errors must be calculated
 * reliably.
 *
 * In addition all constants set in MnStrategy can be changed individually by
 * the user, e.g. the number of iteration cycles in the numerical gradient.
 *
 *
 *
 *
 * Acts on: Migrad (behavioural), Minos (lowers strategy by 1 for Minos-own
 * minimization), Hesse (iterations), Numerical2PDerivative (iterations)
 *
 * @author Darksnake
 * @version $Id$
 */
class MnStrategy {
    private var theGradNCyc = 0
    private var theGradTlr = 0.0
    private var theGradTlrStp = 0.0
    private var theHessGradNCyc = 0

    //default strategy
    private var theHessNCyc = 0
    private var theHessTlrG2 = 0.0
    private var theHessTlrStp = 0.0
    private var theStrategy = 0

    /**
     * Creates a MnStrategy object with the default strategy (medium)
     */
    constructor() {
        setMediumStrategy()
    }
    //user defined strategy (0, 1, >=2)
    /**
     * Creates a MnStrategy object with the user specified strategy.
     *
     * @param stra The use defined strategy, 0=low, 1 medium, 2=high.
     */
    constructor(stra: Int) {
        if (stra == 0) {
            setLowStrategy()
        } else if (stra == 1) {
            setMediumStrategy()
        } else {
            setHighStrategy()
        }
    }

    /**
     *
     * gradientNCycles.
     *
     * @return a int.
     */
    fun gradientNCycles(): Int {
        return theGradNCyc
    }

    /**
     *
     * gradientStepTolerance.
     *
     * @return a double.
     */
    fun gradientStepTolerance(): Double {
        return theGradTlrStp
    }

    /**
     *
     * gradientTolerance.
     *
     * @return a double.
     */
    fun gradientTolerance(): Double {
        return theGradTlr
    }

    /**
     *
     * hessianG2Tolerance.
     *
     * @return a double.
     */
    fun hessianG2Tolerance(): Double {
        return theHessTlrG2
    }

    /**
     *
     * hessianGradientNCycles.
     *
     * @return a int.
     */
    fun hessianGradientNCycles(): Int {
        return theHessGradNCyc
    }

    /**
     *
     * hessianNCycles.
     *
     * @return a int.
     */
    fun hessianNCycles(): Int {
        return theHessNCyc
    }

    /**
     *
     * hessianStepTolerance.
     *
     * @return a double.
     */
    fun hessianStepTolerance(): Double {
        return theHessTlrStp
    }

    /**
     *
     * isHigh.
     *
     * @return a boolean.
     */
    fun isHigh(): Boolean {
        return theStrategy >= 2
    }

    /**
     *
     * isLow.
     *
     * @return a boolean.
     */
    fun isLow(): Boolean {
        return theStrategy <= 0
    }

    /**
     *
     * isMedium.
     *
     * @return a boolean.
     */
    fun isMedium(): Boolean {
        return theStrategy == 1
    }

    /**
     *
     * setGradientNCycles.
     *
     * @param n a int.
     */
    fun setGradientNCycles(n: Int) {
        theGradNCyc = n
    }

    /**
     *
     * setGradientStepTolerance.
     *
     * @param stp a double.
     */
    fun setGradientStepTolerance(stp: Double) {
        theGradTlrStp = stp
    }

    /**
     *
     * setGradientTolerance.
     *
     * @param toler a double.
     */
    fun setGradientTolerance(toler: Double) {
        theGradTlr = toler
    }

    /**
     *
     * setHessianG2Tolerance.
     *
     * @param toler a double.
     */
    fun setHessianG2Tolerance(toler: Double) {
        theHessTlrG2 = toler
    }

    /**
     *
     * setHessianGradientNCycles.
     *
     * @param n a int.
     */
    fun setHessianGradientNCycles(n: Int) {
        theHessGradNCyc = n
    }

    /**
     *
     * setHessianNCycles.
     *
     * @param n a int.
     */
    fun setHessianNCycles(n: Int) {
        theHessNCyc = n
    }

    /**
     *
     * setHessianStepTolerance.
     *
     * @param stp a double.
     */
    fun setHessianStepTolerance(stp: Double) {
        theHessTlrStp = stp
    }

    fun setHighStrategy() {
        theStrategy = 2
        setGradientNCycles(5)
        setGradientStepTolerance(0.1)
        setGradientTolerance(0.02)
        setHessianNCycles(7)
        setHessianStepTolerance(0.1)
        setHessianG2Tolerance(0.02)
        setHessianGradientNCycles(6)
    }

    /**
     *
     * setLowStrategy.
     */
    fun setLowStrategy() {
        theStrategy = 0
        setGradientNCycles(2)
        setGradientStepTolerance(0.5)
        setGradientTolerance(0.1)
        setHessianNCycles(3)
        setHessianStepTolerance(0.5)
        setHessianG2Tolerance(0.1)
        setHessianGradientNCycles(1)
    }

    /**
     *
     * setMediumStrategy.
     */
    fun setMediumStrategy() {
        theStrategy = 1
        setGradientNCycles(3)
        setGradientStepTolerance(0.3)
        setGradientTolerance(0.05)
        setHessianNCycles(5)
        setHessianStepTolerance(0.3)
        setHessianG2Tolerance(0.05)
        setHessianGradientNCycles(2)
    }

    /**
     *
     * strategy.
     *
     * @return a int.
     */
    fun strategy(): Int {
        return theStrategy
    }
}