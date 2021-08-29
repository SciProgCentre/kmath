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
package space.kscience.kmath.optimization.minuit


/**
 * Контейнер для несимметричных оценок и доверительных интервалов
 *
 * @author Darksnake
 * @version $Id: $Id
 */
class MINOSResult
/**
 *
 * Constructor for MINOSResult.
 *
 * @param list an array of [String] objects.
 */(private val names: Array<String>, private val errl: DoubleArray?, private val errp: DoubleArray?) :
    IntervalEstimate {
    fun getNames(): NameList {
        return NameList(names)
    }

    fun getInterval(parName: String?): Pair<Value, Value> {
        val index: Int = getNames().getNumberByName(parName)
        return Pair(ValueFactory.of(errl!![index]), ValueFactory.of(errp!![index]))
    }

    val cL: Double
        get() = 0.68

    /** {@inheritDoc}  */
    fun print(out: PrintWriter) {
        if (errl != null || errp != null) {
            out.println()
            out.println("Assymetrical errors:")
            out.println()
            out.println("Name\tLower\tUpper")
            for (i in 0 until getNames().size()) {
                out.print(getNames().get(i))
                out.print("\t")
                if (errl != null) {
                    out.print(errl[i])
                } else {
                    out.print("---")
                }
                out.print("\t")
                if (errp != null) {
                    out.print(errp[i])
                } else {
                    out.print("---")
                }
                out.println()
            }
        }
    }
}