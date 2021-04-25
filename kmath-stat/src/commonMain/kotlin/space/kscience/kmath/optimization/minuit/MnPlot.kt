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

import java.lang.StringBuffer
import kotlin.jvm.JvmOverloads

/**
 * MnPlot produces a text-screen graphical output of (x,y) points. E.g. from
 * Scan or Contours.
 *
 * @version $Id$
 * @author Darksnake
 */
class MnPlot @JvmOverloads constructor(private val thePageWidth: Int = 80, private val thePageLength: Int = 30) {
    private var bh = 0.0
    private var bl = 0.0
    private var bwid = 0.0
    private var nb = 0
    fun length(): Int {
        return thePageLength
    }

    private fun mnbins(a1: Double, a2: Double, naa: Int) {

        //*-*-*-*-*-*-*-*-*-*-*Compute reasonable histogram intervals*-*-*-*-*-*-*-*-*
        //*-*                  ======================================
        //*-*        Function TO DETERMINE REASONABLE HISTOGRAM INTERVALS
        //*-*        GIVEN ABSOLUTE UPPER AND LOWER BOUNDS  A1 AND A2
        //*-*        AND DESIRED MAXIMUM NUMBER OF BINS NAA
        //*-*        PROGRAM MAKES REASONABLE BINNING FROM BL TO BH OF WIDTH BWID
        //*-*        F. JAMES,   AUGUST, 1974 , stolen for Minuit, 1988
        //*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*

        /* Local variables */
        var awid: Double
        var ah: Double
        var sigfig: Double
        var sigrnd: Double
        var alb: Double
        var kwid: Int
        var lwid: Int
        var na = 0
        var log_: Int
        val al: Double = if (a1 < a2) a1 else a2
        ah = if (a1 > a2) a1 else a2
        if (al == ah) {
            ah = al + 1
        }

        //*-*-       IF NAA .EQ. -1 , PROGRAM USES BWID INPUT FROM CALLING ROUTINE
        var skip = naa == -1 && bwid > 0
        if (!skip) {
            na = naa - 1
            if (na < 1) {
                na = 1
            }
        }
        while (true) {
            if (!skip) {
                //*-*-        GET NOMINAL BIN WIDTH IN EXPON FORM
                awid = (ah - al) / na.toDouble()
                log_ = log10(awid)
                if (awid <= 1) {
                    --log_
                }
                sigfig = awid * pow(10.0, -log_.toDouble())
                //*-*-       ROUND MANTISSA UP TO 2, 2.5, 5, OR 10
                if (sigfig <= 2) {
                    sigrnd = 2.0
                } else if (sigfig <= 2.5) {
                    sigrnd = 2.5
                } else if (sigfig <= 5) {
                    sigrnd = 5.0
                } else {
                    sigrnd = 1.0
                    ++log_
                }
                bwid = sigrnd * pow(10.0, log_.toDouble())
            }
            alb = al / bwid
            lwid = alb.toInt()
            if (alb < 0) {
                --lwid
            }
            bl = bwid * lwid.toDouble()
            alb = ah / bwid + 1
            kwid = alb.toInt()
            if (alb < 0) {
                --kwid
            }
            bh = bwid * kwid.toDouble()
            nb = kwid - lwid
            if (naa <= 5) {
                if (naa == -1) {
                    return
                }
                //*-*-        REQUEST FOR ONE BIN IS DIFFICULT CASE
                if (naa > 1 || nb == 1) {
                    return
                }
                bwid *= 2.0
                nb = 1
                return
            }
            if (nb shl 1 != naa) {
                return
            }
            ++na
            skip = false
            continue
        }
    }

    private fun mnplot(xpt: DoubleArray, ypt: DoubleArray, chpt: StringBuffer, nxypt: Int, npagwd: Int, npagln: Int) {
        //*-*-*-*Plots points in array xypt onto one page with labelled axes*-*-*-*-*
        //*-*    ===========================================================
        //*-*        NXYPT is the number of points to be plotted
        //*-*        XPT(I) = x-coord. of ith point
        //*-*        YPT(I) = y-coord. of ith point
        //*-*        CHPT(I) = character to be plotted at this position
        //*-*        the input point arrays XPT, YPT, CHPT are destroyed.
        //*-*
        //*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*

        /* Local variables */
        var xmin: Double
        var xmax: Double
        var ymax: Double
        var savx: Double
        var savy: Double
        var yprt: Double
        var xbest: Double
        var ybest: Double
        val xvalus = DoubleArray(12)
        val any: Double
        val iten: Int
        var j: Int
        var k: Int
        var maxnx: Int
        var maxny: Int
        var iquit: Int
        var ni: Int
        var linodd: Int
        var ibk: Int
        var isp1: Int
        var ks: Int
        var ix: Int
        var overpr: Boolean
        val cline = StringBuffer(npagwd)
        for (ii in 0 until npagwd) {
            cline.append(' ')
        }
        var chsav: Char
        val chbest: Char

        /* Function Body */
        //*-*  Computing MIN
        maxnx = if (npagwd - 20 < 100) npagwd - 20 else 100
        if (maxnx < 10) {
            maxnx = 10
        }
        maxny = npagln
        if (maxny < 10) {
            maxny = 10
        }
        if (nxypt <= 1) {
            return
        }
        xbest = xpt[0]
        ybest = ypt[0]
        chbest = chpt.get(0)
        //*-*-        order the points by decreasing y
        val km1: Int = nxypt - 1
        var i: Int = 1
        while (i <= km1) {
            iquit = 0
            ni = nxypt - i
            j = 1
            while (j <= ni) {
                if (ypt[j - 1] > ypt[j]) {
                    ++j
                    continue
                }
                savx = xpt[j - 1]
                xpt[j - 1] = xpt[j]
                xpt[j] = savx
                savy = ypt[j - 1]
                ypt[j - 1] = ypt[j]
                ypt[j] = savy
                chsav = chpt.get(j - 1)
                chpt.setCharAt(j - 1, chpt.get(j))
                chpt.setCharAt(j, chsav)
                iquit = 1
                ++j
            }
            if (iquit == 0) {
                break
            }
            ++i
        }
        //*-*-        find extreme values
        xmax = xpt[0]
        xmin = xmax
        i = 1
        while (i <= nxypt) {
            if (xpt[i - 1] > xmax) {
                xmax = xpt[i - 1]
            }
            if (xpt[i - 1] < xmin) {
                xmin = xpt[i - 1]
            }
            ++i
        }
        val dxx: Double = (xmax - xmin) * .001
        xmax += dxx
        xmin -= dxx
        mnbins(xmin, xmax, maxnx)
        xmin = bl
        xmax = bh
        var nx: Int = nb
        val bwidx: Double = bwid
        ymax = ypt[0]
        var ymin: Double = ypt[nxypt - 1]
        if (ymax == ymin) {
            ymax = ymin + 1
        }
        val dyy: Double = (ymax - ymin) * .001
        ymax += dyy
        ymin -= dyy
        mnbins(ymin, ymax, maxny)
        ymin = bl
        ymax = bh
        var ny: Int = nb
        val bwidy: Double = bwid
        any = ny.toDouble()
        //*-*-        if first point is blank, it is an 'origin'
        if (chbest != ' ') {
            xbest = (xmax + xmin) * .5
            ybest = (ymax + ymin) * .5
        }
        //*-*-        find scale constants
        val ax: Double = 1 / bwidx
        val ay: Double = 1 / bwidy
        val bx: Double = -ax * xmin + 2
        val by: Double = -ay * ymin - 2
        //*-*-        convert points to grid positions
        i = 1
        while (i <= nxypt) {
            xpt[i - 1] = ax * xpt[i - 1] + bx
            ypt[i - 1] = any - ay * ypt[i - 1] - by
            ++i
        }
        val nxbest: Int = (ax * xbest + bx).toInt()
        val nybest: Int = (any - ay * ybest - by).toInt()
        //*-*-        print the points
        ny += 2
        nx += 2
        isp1 = 1
        linodd = 1
        overpr = false
        i = 1
        while (i <= ny) {
            ibk = 1
            while (ibk <= nx) {
                cline.setCharAt(ibk - 1, ' ')
                ++ibk
            }
            //         cline.setCharAt(nx,'\0');
            //         cline.setCharAt(nx+1,'\0');
            cline.setCharAt(0, '.')
            cline.setCharAt(nx - 1, '.')
            cline.setCharAt(nxbest - 1, '.')
            if (i == 1 || i == nybest || i == ny) {
                j = 1
                while (j <= nx) {
                    cline.setCharAt(j - 1, '.')
                    ++j
                }
            }
            yprt = ymax - (i - 1.0) * bwidy
            var isplset = false
            if (isp1 <= nxypt) {
                //*-*-        find the points to be plotted on this line
                k = isp1
                while (k <= nxypt) {
                    ks = ypt[k - 1].toInt()
                    if (ks > i) {
                        isp1 = k
                        isplset = true
                        break
                    }
                    ix = xpt[k - 1].toInt()
                    if (cline.get(ix - 1) != '.' && cline.get(ix - 1) != ' ') {
                        if (cline.get(ix - 1) == chpt.get(k - 1)) {
                            ++k
                            continue
                        }
                        overpr = true
                        //*-*-        OVERPR is true if one or more positions contains more than
                        //*-*-           one point
                        cline.setCharAt(ix - 1, '&')
                        ++k
                        continue
                    }
                    cline.setCharAt(ix - 1, chpt.get(k - 1))
                    ++k
                }
                if (!isplset) {
                    isp1 = nxypt + 1
                }
            }
            if (linodd != 1 && i != ny) {
                linodd = 1
                java.lang.System.out.printf("                  %s", cline.substring(0, 60))
            } else {
                java.lang.System.out.printf(" %14.7g ..%s", yprt, cline.substring(0, 60))
                linodd = 0
            }
            println()
            ++i
        }
        //*-*-        print labels on x-axis every ten columns
        ibk = 1
        while (ibk <= nx) {
            cline.setCharAt(ibk - 1, ' ')
            if (ibk % 10 == 1) {
                cline.setCharAt(ibk - 1, '/')
            }
            ++ibk
        }
        java.lang.System.out.printf("                  %s", cline)
        java.lang.System.out.printf("\n")
        ibk = 1
        while (ibk <= 12) {
            xvalus[ibk - 1] = xmin + (ibk - 1.0) * 10 * bwidx
            ++ibk
        }
        java.lang.System.out.printf("           ")
        iten = (nx + 9) / 10
        ibk = 1
        while (ibk <= iten) {
            java.lang.System.out.printf(" %9.4g", xvalus[ibk - 1])
            ++ibk
        }
        java.lang.System.out.printf("\n")
        if (overpr) {
            val chmess = "   Overprint character is &"
            java.lang.System.out.printf("                         ONE COLUMN=%13.7g%s", bwidx, chmess)
        } else {
            val chmess = " "
            java.lang.System.out.printf("                         ONE COLUMN=%13.7g%s", bwidx, chmess)
        }
        println()
    }

    /**
     *
     * plot.
     *
     * @param points a [List] object.
     */
    fun plot(points: List<Range>) {
        val x = DoubleArray(points.size)
        val y = DoubleArray(points.size)
        val chpt = StringBuffer(points.size)
        for ((i, ipoint) in points.withIndex()) {
            x[i] = ipoint.getFirst()
            y[i] = ipoint.getSecond()
            chpt.append('*')
        }
        mnplot(x, y, chpt, points.size, width(), length())
    }

    /**
     *
     * plot.
     *
     * @param xmin a double.
     * @param ymin a double.
     * @param points a [List] object.
     */
    fun plot(xmin: Double, ymin: Double, points: List<Range>) {
        val x = DoubleArray(points.size + 2)
        x[0] = xmin
        x[1] = xmin
        val y = DoubleArray(points.size + 2)
        y[0] = ymin
        y[1] = ymin
        val chpt = StringBuffer(points.size + 2)
        chpt.append(' ')
        chpt.append('X')
        var i = 2
        for (ipoint in points) {
            x[i] = ipoint.getFirst()
            y[i] = ipoint.getSecond()
            chpt.append('*')
            i++
        }
        mnplot(x, y, chpt, points.size + 2, width(), length())
    }

    fun width(): Int {
        return thePageWidth
    }
    /**
     *
     * Constructor for MnPlot.
     *
     * @param thePageWidth a int.
     * @param thePageLength a int.
     */
    /**
     *
     * Constructor for MnPlot.
     */
    init {
        if (thePageWidth > 120) {
            thePageWidth = 120
        }
        if (thePageLength > 56) {
            thePageLength = 56
        }
    }
}