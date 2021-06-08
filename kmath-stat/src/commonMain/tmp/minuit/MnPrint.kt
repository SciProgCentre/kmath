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

import org.apache.commons.math3.linear.RealVector
import ru.inr.mass.minuit.*

/**
 * Utilities for printing various minuit results.
 *
 * @version $Id$
 * @author Darksnake
 */
object MnPrint {
    /**
     *
     * print.
     *
     * @param os a [PrintWriter] object.
     * @param vec a [org.apache.commons.math3.linear.RealVector] object.
     */
    fun print(os: PrintWriter, vec: RealVector) {
        os.println("LAVector parameters:")
        run {
            os.println()
            val nrow: Int = vec.getDimension()
            for (i in 0 until nrow) {
                os.printf("%g ", vec.getEntry(i))
            }
            os.println()
        }
    }

    /**
     *
     * print.
     *
     * @param os a [PrintWriter] object.
     * @param matrix a [hep.dataforge.MINUIT.MnAlgebraicSymMatrix] object.
     */
    fun print(os: PrintWriter, matrix: MnAlgebraicSymMatrix) {
        os.println("LASymMatrix parameters:")
        run {
            os.println()
            val n: Int = matrix.nrow()
            for (i in 0 until n) {
                for (j in 0 until n) {
                    os.printf("%10g ", matrix[i, j])
                }
                os.println()
            }
        }
    }

    /**
     *
     * print.
     *
     * @param os a [PrintWriter] object.
     * @param min a [hep.dataforge.MINUIT.FunctionMinimum] object.
     */
    fun print(os: PrintWriter, min: FunctionMinimum) {
        os.println()
        if (!min.isValid()) {
            os.println()
            os.println("WARNING: Minuit did not converge.")
            os.println()
        } else {
            os.println()
            os.println("Minuit did successfully converge.")
            os.println()
        }
        os.printf("# of function calls: %d\n", min.nfcn())
        os.printf("minimum function value: %g\n", min.fval())
        os.printf("minimum edm: %g\n", min.edm())
        os.println("minimum internal state vector: " + min.parameters().vec())
        if (min.hasValidCovariance()) {
            os.println("minimum internal covariance matrix: " + min.error().matrix())
        }
        os.println(min.userParameters())
        os.println(min.userCovariance())
        os.println(min.userState().globalCC())
        if (!min.isValid()) {
            os.println("WARNING: FunctionMinimum is invalid.")
        }
        os.println()
    }

    /**
     *
     * print.
     *
     * @param os a [PrintWriter] object.
     * @param min a [hep.dataforge.MINUIT.MinimumState] object.
     */
    fun print(os: PrintWriter, min: MinimumState) {
        os.println()
        os.printf("minimum function value: %g\n", min.fval())
        os.printf("minimum edm: %g\n", min.edm())
        os.println("minimum internal state vector: " + min.vec())
        os.println("minimum internal gradient vector: " + min.gradient().getGradient())
        if (min.hasCovariance()) {
            os.println("minimum internal covariance matrix: " + min.error().matrix())
        }
        os.println()
    }

    /**
     *
     * print.
     *
     * @param os a [PrintWriter] object.
     * @param par a [hep.dataforge.MINUIT.MnUserParameters] object.
     */
    fun print(os: PrintWriter, par: MnUserParameters) {
        os.println()
        os.println("# ext. |" + "|   name    |" + "|   type  |" + "|   value   |" + "|  error +/- ")
        os.println()
        var atLoLim = false
        var atHiLim = false
        for (ipar in par.parameters()) {
            os.printf(" %5d || %9s || ", ipar.number(), ipar.name())
            if (ipar.isConst()) {
                os.printf("         || %10g   ||", ipar.value())
            } else if (ipar.isFixed()) {
                os.printf("  fixed  || %10g   ||\n", ipar.value())
            } else if (ipar.hasLimits()) {
                if (ipar.error() > 0.0) {
                    os.printf(" limited || %10g", ipar.value())
                    if (abs(ipar.value() - ipar.lowerLimit()) < par.precision().eps2()) {
                        os.print("* ")
                        atLoLim = true
                    }
                    if (abs(ipar.value() - ipar.upperLimit()) < par.precision().eps2()) {
                        os.print("**")
                        atHiLim = true
                    }
                    os.printf(" || %10g\n", ipar.error())
                } else {
                    os.printf("  free   || %10g || no\n", ipar.value())
                }
            } else {
                if (ipar.error() > 0.0) {
                    os.printf("  free   || %10g || %10g\n", ipar.value(), ipar.error())
                } else {
                    os.printf("  free   || %10g || no\n", ipar.value())
                }
            }
        }
        os.println()
        if (atLoLim) {
            os.print("* parameter is at lower limit")
        }
        if (atHiLim) {
            os.print("** parameter is at upper limit")
        }
        os.println()
    }

    /**
     *
     * print.
     *
     * @param os a [PrintWriter] object.
     * @param matrix a [hep.dataforge.MINUIT.MnUserCovariance] object.
     */
    fun print(os: PrintWriter, matrix: MnUserCovariance) {
        os.println()
        os.println("MnUserCovariance: ")
        run {
            os.println()
            val n: Int = matrix.nrow()
            for (i in 0 until n) {
                for (j in 0 until n) {
                    os.printf("%10g ", matrix[i, j])
                }
                os.println()
            }
        }
        os.println()
        os.println("MnUserCovariance parameter correlations: ")
        run {
            os.println()
            val n: Int = matrix.nrow()
            for (i in 0 until n) {
                val di: Double = matrix[i, i]
                for (j in 0 until n) {
                    val dj: Double = matrix[j, j]
                    os.printf("%g ", matrix[i, j] / sqrt(abs(di * dj)))
                }
                os.println()
            }
        }
    }

    /**
     *
     * print.
     *
     * @param os a [PrintWriter] object.
     * @param coeff a [hep.dataforge.MINUIT.MnGlobalCorrelationCoeff] object.
     */
    fun print(os: PrintWriter, coeff: MnGlobalCorrelationCoeff) {
        os.println()
        os.println("MnGlobalCorrelationCoeff: ")
        run {
            os.println()
            for (i in 0 until coeff.globalCC().length) {
                os.printf("%g\n", coeff.globalCC()[i])
            }
        }
    }

    /**
     *
     * print.
     *
     * @param os a [PrintWriter] object.
     * @param state a [hep.dataforge.MINUIT.MnUserParameterState] object.
     */
    fun print(os: PrintWriter, state: MnUserParameterState) {
        os.println()
        if (!state.isValid()) {
            os.println()
            os.println("WARNING: MnUserParameterState is not valid.")
            os.println()
        }
        os.println("# of function calls: " + state.nfcn())
        os.println("function value: " + state.fval())
        os.println("expected distance to the minimum (edm): " + state.edm())
        os.println("external parameters: " + state.parameters())
        if (state.hasCovariance()) {
            os.println("covariance matrix: " + state.covariance())
        }
        if (state.hasGlobalCC()) {
            os.println("global correlation coefficients : " + state.globalCC())
        }
        if (!state.isValid()) {
            os.println("WARNING: MnUserParameterState is not valid.")
        }
        os.println()
    }

    /**
     *
     * print.
     *
     * @param os a [PrintWriter] object.
     * @param me a [hep.dataforge.MINUIT.MinosError] object.
     */
    fun print(os: PrintWriter, me: MinosError) {
        os.println()
        os.printf("Minos # of function calls: %d\n", me.nfcn())
        if (!me.isValid()) {
            os.println("Minos error is not valid.")
        }
        if (!me.lowerValid()) {
            os.println("lower Minos error is not valid.")
        }
        if (!me.upperValid()) {
            os.println("upper Minos error is not valid.")
        }
        if (me.atLowerLimit()) {
            os.println("Minos error is lower limit of parameter " + me.parameter())
        }
        if (me.atUpperLimit()) {
            os.println("Minos error is upper limit of parameter " + me.parameter())
        }
        if (me.atLowerMaxFcn()) {
            os.println("Minos number of function calls for lower error exhausted.")
        }
        if (me.atUpperMaxFcn()) {
            os.println("Minos number of function calls for upper error exhausted.")
        }
        if (me.lowerNewMin()) {
            os.println("Minos found a new minimum in negative direction.")
            os.println(me.lowerState())
        }
        if (me.upperNewMin()) {
            os.println("Minos found a new minimum in positive direction.")
            os.println(me.upperState())
        }
        os.println("# ext. ||   name    || value@min ||  negative || positive  ")
        os.printf("%4d||%10s||%10g||%10g||%10g\n",
            me.parameter(),
            me.lowerState().name(me.parameter()),
            me.min(),
            me.lower(),
            me.upper())
        os.println()
    }

    /**
     *
     * print.
     *
     * @param os a [PrintWriter] object.
     * @param ce a [hep.dataforge.MINUIT.ContoursError] object.
     */
    fun print(os: PrintWriter, ce: ContoursError) {
        os.println()
        os.println("Contours # of function calls: " + ce.nfcn())
        os.println("MinosError in x: ")
        os.println(ce.xMinosError())
        os.println("MinosError in y: ")
        os.println(ce.yMinosError())
        val plot = MnPlot()
        plot.plot(ce.xmin(), ce.ymin(), ce.points())
        for ((i, ipoint) in ce.points().withIndex()) {
            os.printf("%d %10g %10g\n", i, ipoint.getFirst(), ipoint.getSecond())
        }
        os.println()
    }

    fun toString(x: RealVector): String {
        val writer: java.io.StringWriter = java.io.StringWriter()
        PrintWriter(writer).use { pw -> print(pw, x) }
        return writer.toString()
    }

    fun toString(x: MnAlgebraicSymMatrix?): String {
        val writer: java.io.StringWriter = java.io.StringWriter()
        PrintWriter(writer).use { pw -> print(pw, x) }
        return writer.toString()
    }

    fun toString(min: FunctionMinimum?): String {
        val writer: java.io.StringWriter = java.io.StringWriter()
        PrintWriter(writer).use { pw -> print(pw, min) }
        return writer.toString()
    }

    fun toString(x: MinimumState?): String {
        val writer: java.io.StringWriter = java.io.StringWriter()
        PrintWriter(writer).use { pw -> print(pw, x) }
        return writer.toString()
    }

    fun toString(x: MnUserParameters?): String {
        val writer: java.io.StringWriter = java.io.StringWriter()
        PrintWriter(writer).use { pw -> print(pw, x) }
        return writer.toString()
    }

    fun toString(x: MnUserCovariance?): String {
        val writer: java.io.StringWriter = java.io.StringWriter()
        PrintWriter(writer).use { pw -> print(pw, x) }
        return writer.toString()
    }

    fun toString(x: MnGlobalCorrelationCoeff?): String {
        val writer: java.io.StringWriter = java.io.StringWriter()
        PrintWriter(writer).use { pw -> print(pw, x) }
        return writer.toString()
    }

    fun toString(x: MnUserParameterState?): String {
        val writer: java.io.StringWriter = java.io.StringWriter()
        PrintWriter(writer).use { pw -> print(pw, x) }
        return writer.toString()
    }

    fun toString(x: MinosError?): String {
        val writer: java.io.StringWriter = java.io.StringWriter()
        PrintWriter(writer).use { pw -> print(pw, x) }
        return writer.toString()
    }

    fun toString(x: ContoursError?): String {
        val writer: java.io.StringWriter = java.io.StringWriter()
        PrintWriter(writer).use { pw -> print(pw, x) }
        return writer.toString()
    }
}