/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.jafama;

/**
 * Strict versions of FastMath methods.
 * Cf. README.txt for more info.
 */
public final strictfp class StrictFastMath extends CmnFastMath {

    /*
     * We use strictfp for the whole class:
     * - for simplicity,
     * - to reduce strictfp/non-strictfp switching, which can add overhead,
     *   when these treatments are used from within strictfp code,
     * - to make sure that we only use and return non-extended values,
     *   else if strictfp gets added later to some treatments they might then
     *   behave differently due to no longer being inlinable into FP-wide
     *   expressions,
     * - to make sure we don't mistakenly not use it.
     */

    //--------------------------------------------------------------------------
    // CONFIGURATION
    //--------------------------------------------------------------------------

    private static final boolean USE_JDK_MATH = SFM_USE_JDK_MATH;

    private static final boolean USE_REDEFINED_LOG = SFM_USE_REDEFINED_LOG;

    private static final boolean USE_REDEFINED_SQRT = SFM_USE_REDEFINED_SQRT;

    private static final boolean USE_POWTABS_FOR_ASIN = SFM_USE_POWTABS_FOR_ASIN;

    //--------------------------------------------------------------------------
    // PUBLIC METHODS
    //--------------------------------------------------------------------------

    /*
     * trigonometry
     */

    /**
     * @param angle Angle in radians.
     * @return Angle sine.
     */
    public static double sin(double angle) {
        if (USE_JDK_MATH) {
            return StrictMath.sin(angle);
        }
        boolean negateResult = false;
        if (angle < 0.0) {
            angle = -angle;
            negateResult = true;
        }
        if (angle > SIN_COS_MAX_VALUE_FOR_INT_MODULO) {
            if (false) {
                // Can give very bad relative error near PI (mod 2*PI).
                angle = remainderTwoPi(angle);
                if (angle < 0.0) {
                    angle = -angle;
                    negateResult = !negateResult;
                }
            } else {
                final long remAndQuad = remainderPiO2(angle);
                angle = decodeRemainder(remAndQuad);
                final double sin;
                final int q = decodeQuadrant(remAndQuad);
                if (q == 0) {
                    sin = sin(angle);
                } else if (q == 1) {
                    sin = cos(angle);
                } else if (q == 2) {
                    sin = -sin(angle);
                } else {
                    sin = -cos(angle);
                }
                return (negateResult ? -sin : sin);
            }
        }
        // index: possibly outside tables range.
        int index = (int)(angle * SIN_COS_INDEXER + 0.5);
        double delta = (angle - index * SIN_COS_DELTA_HI) - index * SIN_COS_DELTA_LO;
        // Making sure index is within tables range.
        // Last value of each table is the same than first,
        // so we ignore it (tabs size minus one) for modulo.
        index &= (SIN_COS_TABS_SIZE-2); // index % (SIN_COS_TABS_SIZE-1)
        double indexSin = MyTSinCos.sinTab[index];
        double indexCos = MyTSinCos.cosTab[index];
        double result = indexSin + delta * (indexCos + delta * (-indexSin * ONE_DIV_F2 + delta * (-indexCos * ONE_DIV_F3 + delta * indexSin * ONE_DIV_F4)));
        return negateResult ? -result : result;
    }

    /**
     * Quick sin, with accuracy of about 1.6e-3 (PI/<look-up tabs size>)
     * for |angle| < 6588395.0 (Integer.MAX_VALUE * (2*PI/<look-up tabs size>) - 2)
     * (- 2 due to removing PI/2 before using cosine tab),
     * and no accuracy at all for larger values.
     *
     * @param angle Angle in radians.
     * @return Angle sine.
     */
    public static double sinQuick(double angle) {
        if (USE_JDK_MATH) {
            return StrictMath.sin(angle);
        }
        return MyTSinCos.cosTab[((int)(Math.abs(angle-Math.PI/2) * SIN_COS_INDEXER + 0.5)) & (SIN_COS_TABS_SIZE-2)];
    }

    /**
     * @param angle Angle in radians.
     * @return Angle cosine.
     */
    public static double cos(double angle) {
        if (USE_JDK_MATH) {
            return StrictMath.cos(angle);
        }
        angle = Math.abs(angle);
        if (angle > SIN_COS_MAX_VALUE_FOR_INT_MODULO) {
            if (false) {
                // Can give very bad relative error near PI (mod 2*PI).
                angle = remainderTwoPi(angle);
                if (angle < 0.0) {
                    angle = -angle;
                }
            } else {
                final long remAndQuad = remainderPiO2(angle);
                angle = decodeRemainder(remAndQuad);
                final double cos;
                final int q = decodeQuadrant(remAndQuad);
                if (q == 0) {
                    cos = cos(angle);
                } else if (q == 1) {
                    cos = -sin(angle);
                } else if (q == 2) {
                    cos = -cos(angle);
                } else {
                    cos = sin(angle);
                }
                return cos;
            }
        }
        // index: possibly outside tables range.
        int index = (int)(angle * SIN_COS_INDEXER + 0.5);
        double delta = (angle - index * SIN_COS_DELTA_HI) - index * SIN_COS_DELTA_LO;
        // Making sure index is within tables range.
        // Last value of each table is the same than first,
        // so we ignore it (tabs size minus one) for modulo.
        index &= (SIN_COS_TABS_SIZE-2); // index % (SIN_COS_TABS_SIZE-1)
        double indexCos = MyTSinCos.cosTab[index];
        double indexSin = MyTSinCos.sinTab[index];
        return indexCos + delta * (-indexSin + delta * (-indexCos * ONE_DIV_F2 + delta * (indexSin * ONE_DIV_F3 + delta * indexCos * ONE_DIV_F4)));
    }

    /**
     * Quick cos, with accuracy of about 1.6e-3 (PI/<look-up tabs size>)
     * for |angle| < 6588397.0 (Integer.MAX_VALUE * (2*PI/<look-up tabs size>)),
     * and no accuracy at all for larger values.
     *
     * @param angle Angle in radians.
     * @return Angle cosine.
     */
    public static double cosQuick(double angle) {
        if (USE_JDK_MATH) {
            return StrictMath.cos(angle);
        }
        return MyTSinCos.cosTab[((int)(Math.abs(angle) * SIN_COS_INDEXER + 0.5)) & (SIN_COS_TABS_SIZE-2)];
    }

    /**
     * Computes sine and cosine together.
     *
     * @param angle Angle in radians.
     * @param cosine (out) Angle cosine.
     * @return Angle sine.
     */
    public static double sinAndCos(double angle, DoubleWrapper cosine) {
        if (USE_JDK_MATH) {
            cosine.value = StrictMath.cos(angle);
            return StrictMath.sin(angle);
        }
        // Using the same algorithm than sin(double) method,
        // and computing also cosine at the end.
        boolean negateResult = false;
        if (angle < 0.0) {
            angle = -angle;
            negateResult = true;
        }
        if (angle > SIN_COS_MAX_VALUE_FOR_INT_MODULO) {
            if (false) {
                // Can give very bad relative error near PI (mod 2*PI).
                angle = remainderTwoPi(angle);
                if (angle < 0.0) {
                    angle = -angle;
                    negateResult = !negateResult;
                }
            } else {
                final long remAndQuad = remainderPiO2(angle);
                angle = decodeRemainder(remAndQuad);
                final double sin;
                final int q = decodeQuadrant(remAndQuad);
                if (q == 0) {
                    sin = sin(angle);
                    cosine.value = cos(angle);
                } else if (q == 1) {
                    sin = cos(angle);
                    cosine.value = -sin(angle);
                } else if (q == 2) {
                    sin = -sin(angle);
                    cosine.value = -cos(angle);
                } else {
                    sin = -cos(angle);
                    cosine.value = sin(angle);
                }
                return (negateResult ? -sin : sin);
            }
        }
        int index = (int)(angle * SIN_COS_INDEXER + 0.5);
        double delta = (angle - index * SIN_COS_DELTA_HI) - index * SIN_COS_DELTA_LO;
        index &= (SIN_COS_TABS_SIZE-2); // index % (SIN_COS_TABS_SIZE-1)
        double indexSin = MyTSinCos.sinTab[index];
        double indexCos = MyTSinCos.cosTab[index];
        // Could factor some multiplications (delta * factorials), but then is less accurate.
        cosine.value = indexCos + delta * (-indexSin + delta * (-indexCos * ONE_DIV_F2 + delta * (indexSin * ONE_DIV_F3 + delta * indexCos * ONE_DIV_F4)));
        double result = indexSin + delta * (indexCos + delta * (-indexSin * ONE_DIV_F2 + delta * (-indexCos * ONE_DIV_F3 + delta * indexSin * ONE_DIV_F4)));
        return negateResult ? -result : result;
    }

    /**
     * Can have very bad relative error near +-PI/2,
     * but of the same magnitude than the relative delta between
     * StrictMath.tan(PI/2) and StrictMath.tan(nextDown(PI/2)).
     *
     * @param angle Angle in radians.
     * @return Angle tangent.
     */
    public static double tan(double angle) {
        if (USE_JDK_MATH) {
            return StrictMath.tan(angle);
        }
        boolean negateResult = false;
        if (angle < 0.0) {
            angle = -angle;
            negateResult = true;
        }
        if (angle > TAN_MAX_VALUE_FOR_INT_MODULO) {
            angle = remainderPi(angle);
            if (angle < 0.0) {
                angle = -angle;
                negateResult = !negateResult;
            }
        }
        // index: possibly outside tables range.
        int index = (int)(angle * TAN_INDEXER + 0.5);
        double delta = (angle - index * TAN_DELTA_HI) - index * TAN_DELTA_LO;
        // Making sure index is within tables range.
        // index modulo PI, i.e. 2*(virtual tab size minus one).
        index &= (2*(TAN_VIRTUAL_TABS_SIZE-1)-1); // index % (2*(TAN_VIRTUAL_TABS_SIZE-1))
        // Here, index is in [0,2*(TAN_VIRTUAL_TABS_SIZE-1)-1], i.e. indicates an angle in [0,PI[.
        if (index > (TAN_VIRTUAL_TABS_SIZE-1)) {
            index = (2*(TAN_VIRTUAL_TABS_SIZE-1)) - index;
            delta = -delta;
            negateResult = !negateResult;
        }
        double result;
        if (index < TAN_TABS_SIZE) {
            result = MyTTan.tanTab[index]
                    + delta * (MyTTan.tanDer1DivF1Tab[index]
                            + delta * (MyTTan.tanDer2DivF2Tab[index]
                                    + delta * (MyTTan.tanDer3DivF3Tab[index]
                                            + delta * MyTTan.tanDer4DivF4Tab[index])));
        } else { // angle in ]TAN_MAX_VALUE_FOR_TABS,TAN_MAX_VALUE_FOR_INT_MODULO], or angle is NaN
            // Using tan(angle) == 1/tan(PI/2-angle) formula: changing angle (index and delta), and inverting.
            index = (TAN_VIRTUAL_TABS_SIZE-1) - index;
            result = 1/(MyTTan.tanTab[index]
                    - delta * (MyTTan.tanDer1DivF1Tab[index]
                            - delta * (MyTTan.tanDer2DivF2Tab[index]
                                    - delta * (MyTTan.tanDer3DivF3Tab[index]
                                            - delta * MyTTan.tanDer4DivF4Tab[index]))));
        }
        return negateResult ? -result : result;
    }

    /**
     * @param value Value in [-1,1].
     * @return Value arcsine, in radians, in [-PI/2,PI/2].
     */
    public static double asin(double value) {
        if (USE_JDK_MATH) {
            return StrictMath.asin(value);
        }
        boolean negateResult = false;
        if (value < 0.0) {
            value = -value;
            negateResult = true;
        }
        if (value <= ASIN_MAX_VALUE_FOR_TABS) {
            int index = (int)(value * ASIN_INDEXER + 0.5);
            double delta = value - index * ASIN_DELTA;
            double result = MyTAsin.asinTab[index]
                    + delta * (MyTAsin.asinDer1DivF1Tab[index]
                            + delta * (MyTAsin.asinDer2DivF2Tab[index]
                                    + delta * (MyTAsin.asinDer3DivF3Tab[index]
                                            + delta * MyTAsin.asinDer4DivF4Tab[index])));
            return negateResult ? -result : result;
        } else if (USE_POWTABS_FOR_ASIN && (value <= ASIN_MAX_VALUE_FOR_POWTABS)) {
            int index = (int)(powFast(value * ASIN_POWTABS_ONE_DIV_MAX_VALUE, ASIN_POWTABS_POWER) * ASIN_POWTABS_SIZE_MINUS_ONE + 0.5);
            double delta = value - MyTAsinPow.asinParamPowTab[index];
            double result = MyTAsinPow.asinPowTab[index]
                    + delta * (MyTAsinPow.asinDer1DivF1PowTab[index]
                            + delta * (MyTAsinPow.asinDer2DivF2PowTab[index]
                                    + delta * (MyTAsinPow.asinDer3DivF3PowTab[index]
                                            + delta * MyTAsinPow.asinDer4DivF4PowTab[index])));
            return negateResult ? -result : result;
        } else { // value > ASIN_MAX_VALUE_FOR_TABS, or value is NaN
            // This part is derived from fdlibm.
            if (value < 1.0) {
                double t = (1.0 - value)*0.5;
                double p = t*(ASIN_PS0+t*(ASIN_PS1+t*(ASIN_PS2+t*(ASIN_PS3+t*(ASIN_PS4+t*ASIN_PS5)))));
                double q = 1.0+t*(ASIN_QS1+t*(ASIN_QS2+t*(ASIN_QS3+t*ASIN_QS4)));
                double s = sqrt(t);
                double z = s+s*(p/q);
                double result = ASIN_PIO2_HI-((z+z)-ASIN_PIO2_LO);
                return negateResult ? -result : result;
            } else { // value >= 1.0, or value is NaN
                if (value == 1.0) {
                    return negateResult ? -Math.PI/2 : Math.PI/2;
                } else {
                    return Double.NaN;
                }
            }
        }
    }

    /**
     * If value is not NaN and is outside [-1,1] range, closest value in this range is used.
     *
     * @param value Value in [-1,1].
     * @return Value arcsine, in radians, in [-PI/2,PI/2].
     */
    public static double asinInRange(double value) {
        if (value <= -1.0) {
            return -Math.PI/2;
        } else if (value >= 1.0) {
            return Math.PI/2;
        } else {
            return asin(value);
        }
    }

    /**
     * @param value Value in [-1,1].
     * @return Value arccosine, in radians, in [0,PI].
     */
    public static double acos(double value) {
        if (USE_JDK_MATH) {
            return StrictMath.acos(value);
        }
        return Math.PI/2 - asin(value);
    }

    /**
     * If value is not NaN and is outside [-1,1] range,
     * closest value in this range is used.
     *
     * @param value Value in [-1,1].
     * @return Value arccosine, in radians, in [0,PI].
     */
    public static double acosInRange(double value) {
        if (value <= -1.0) {
            return Math.PI;
        } else if (value >= 1.0) {
            return 0.0;
        } else {
            return acos(value);
        }
    }

    /**
     * @param value A double value.
     * @return Value arctangent, in radians, in [-PI/2,PI/2].
     */
    public static double atan(double value) {
        if (USE_JDK_MATH) {
            return StrictMath.atan(value);
        }
        boolean negateResult = false;
        if (value < 0.0) {
            value = -value;
            negateResult = true;
        }
        if (value == 1.0) {
            // We want "exact" result for 1.0.
            return negateResult ? -Math.PI/4 : Math.PI/4;
        } else if (value <= ATAN_MAX_VALUE_FOR_TABS) {
            int index = (int)(value * ATAN_INDEXER + 0.5);
            double delta = value - index * ATAN_DELTA;
            double result = MyTAtan.atanTab[index]
                    + delta * (MyTAtan.atanDer1DivF1Tab[index]
                            + delta * (MyTAtan.atanDer2DivF2Tab[index]
                                    + delta * (MyTAtan.atanDer3DivF3Tab[index]
                                            + delta * MyTAtan.atanDer4DivF4Tab[index])));
            return negateResult ? -result : result;
        } else { // value > ATAN_MAX_VALUE_FOR_TABS, or value is NaN
            // This part is derived from fdlibm.
            if (value < TWO_POW_66) {
                double x = -1/value;
                double x2 = x*x;
                double x4 = x2*x2;
                double s1 = x2*(ATAN_AT0+x4*(ATAN_AT2+x4*(ATAN_AT4+x4*(ATAN_AT6+x4*(ATAN_AT8+x4*ATAN_AT10)))));
                double s2 = x4*(ATAN_AT1+x4*(ATAN_AT3+x4*(ATAN_AT5+x4*(ATAN_AT7+x4*ATAN_AT9))));
                double result = ATAN_HI3-((x*(s1+s2)-ATAN_LO3)-x);
                return negateResult ? -result : result;
            } else { // value >= 2^66, or value is NaN
                if (value != value) {
                    return Double.NaN;
                } else {
                    return negateResult ? -Math.PI/2 : Math.PI/2;
                }
            }
        }
    }

    /**
     * For special values for which multiple conventions could be adopted,
     * behaves like StrictMath.atan2(double,double).
     *
     * @param y Coordinate on y axis.
     * @param x Coordinate on x axis.
     * @return Angle from x axis positive side to (x,y) position, in radians, in [-PI,PI].
     *         Angle measure is positive when going from x axis to y axis (positive sides).
     */
    public static double atan2(double y, double x) {
        if (USE_JDK_MATH) {
            return StrictMath.atan2(y,x);
        }
        /*
         * Using sub-methods, to make method lighter for general case,
         * and to avoid JIT-optimization crash on NaN.
         */
        if (x > 0.0) {
            if (y == 0.0) {
                // +-0.0
                return y;
            }
            if (x == Double.POSITIVE_INFINITY) {
                return atan2_pinf_yyy(y);
            } else {
                return atan(y/x);
            }
        } else if (x < 0.0) {
            if (y == 0.0) {
                return signFromBit(y) * Math.PI;
            }
            if (x == Double.NEGATIVE_INFINITY) {
                return atan2_ninf_yyy(y);
            } else if (y > 0.0) {
                return Math.PI/2 - atan(x/y);
            } else if (y < 0.0) {
                return -Math.PI/2 - atan(x/y);
            } else {
                return Double.NaN;
            }
        } else {
            return atan2_yyy_zeroOrNaN(y, x);
        }
    }

    /**
     * Gives same result as StrictMath.toRadians for some particular values
     * like 90.0, 180.0 or 360.0, but is faster (no division).
     *
     * @param angdeg Angle value in degrees.
     * @return Angle value in radians.
     */
    public static double toRadians(double angdeg) {
        if (USE_JDK_MATH) {
            return StrictMath.toRadians(angdeg);
        }
        return angdeg * (Math.PI/180);
    }

    /**
     * Gives same result as StrictMath.toDegrees for some particular values
     * like Math.PI/2, Math.PI or 2*Math.PI, but is faster (no division).
     *
     * @param angrad Angle value in radians.
     * @return Angle value in degrees.
     */
    public static double toDegrees(double angrad) {
        if (USE_JDK_MATH) {
            return StrictMath.toDegrees(angrad);
        }
        return angrad * (180/Math.PI);
    }

    /**
     * @param sign Sign of the angle: true for positive, false for negative.
     * @param degrees Degrees, in [0,180].
     * @param minutes Minutes, in [0,59].
     * @param seconds Seconds, in [0.0,60.0[.
     * @return Angle in radians.
     */
    public static double toRadians(boolean sign, int degrees, int minutes, double seconds) {
        return toRadians(toDegrees(sign, degrees, minutes, seconds));
    }

    /**
     * @param sign Sign of the angle: true for positive, false for negative.
     * @param degrees Degrees, in [0,180].
     * @param minutes Minutes, in [0,59].
     * @param seconds Seconds, in [0.0,60.0[.
     * @return Angle in degrees.
     */
    public static double toDegrees(boolean sign, int degrees, int minutes, double seconds) {
        double signFactor = sign ? 1.0 : -1.0;
        return signFactor * (degrees + (1.0/60)*(minutes + (1.0/60)*seconds));
    }

    /**
     * @param angrad Angle in radians.
     * @param degrees (out) Degrees, in [0,180].
     * @param minutes (out) Minutes, in [0,59].
     * @param seconds (out) Seconds, in [0.0,60.0[.
     * @return true if the resulting angle in [-180deg,180deg] is positive, false if it is negative.
     */
    public static boolean toDMS(double angrad, IntWrapper degrees, IntWrapper minutes, DoubleWrapper seconds) {
        // Computing longitude DMS.
        double tmp = toDegrees(normalizeMinusPiPi(angrad));
        boolean isNeg = (tmp < 0.0);
        if (isNeg) {
            tmp = -tmp;
        }
        degrees.value = (int)tmp;
        tmp = (tmp-degrees.value)*60.0;
        minutes.value = (int)tmp;
        seconds.value = Math.min((tmp-minutes.value)*60.0,DOUBLE_BEFORE_60);
        return !isNeg;
    }

    /**
     * NB: Since 2*Math.PI < 2*PI, a span of 2*Math.PI does not mean full angular range.
     * ex.: isInClockwiseDomain(0.0, 2*Math.PI, -1e-20) returns false.
     * ---> For full angular range, use a span > 2*Math.PI, like 2*PI_SUP constant of this class.
     *
     * @param startAngRad An angle, in radians.
     * @param angSpanRad An angular span, >= 0.0, in radians.
     * @param angRad An angle, in radians.
     * @return true if angRad is in the clockwise angular domain going from startAngRad, over angSpanRad,
     *         extremities included, false otherwise.
     */
    public static boolean isInClockwiseDomain(double startAngRad, double angSpanRad, double angRad) {
        if (Math.abs(angRad) < -TWO_MATH_PI_IN_MINUS_PI_PI) {
            // special case for angular values of small magnitude
            if (angSpanRad <= 2*Math.PI) {
                if (angSpanRad < 0.0) {
                    // empty domain
                    return false;
                }
                // angSpanRad is in [0,2*PI]
                startAngRad = normalizeMinusPiPi(startAngRad);
                double endAngRad = normalizeMinusPiPi(startAngRad + angSpanRad);
                if (startAngRad <= endAngRad) {
                    return (angRad >= startAngRad) && (angRad <= endAngRad);
                } else {
                    return (angRad >= startAngRad) || (angRad <= endAngRad);
                }
            } else { // angSpanRad > 2*Math.PI, or is NaN
                return (angSpanRad == angSpanRad);
            }
        } else {
            // general case
            return (normalizeZeroTwoPi(angRad - startAngRad) <= angSpanRad);
        }
    }

    /*
     * hyperbolic trigonometry
     */

    /**
     * Some properties of sinh(x) = (exp(x)-exp(-x))/2:
     * 1) defined on ]-Infinity,+Infinity[
     * 2) result in ]-Infinity,+Infinity[
     * 3) sinh(x) = -sinh(-x) (implies sinh(0) = 0)
     * 4) sinh(epsilon) ~= epsilon
     * 5) lim(sinh(x),x->+Infinity) = +Infinity
     *    (y increasing exponentially faster than x)
     * 6) reaches +Infinity (double overflow) for x >= 710.475860073944,
     *    i.e. a bit further than exp(x)
     *
     * @param value A double value.
     * @return Value hyperbolic sine.
     */
    public static double sinh(double value) {
        if (USE_JDK_MATH) {
            return StrictMath.sinh(value);
        }
        // sinh(x) = (exp(x)-exp(-x))/2
        double h;
        if (value < 0.0) {
            value = -value;
            h = -0.5;
        } else {
            h = 0.5;
        }
        if (value < 22.0) {
            if (value < TWO_POW_N28) {
                return (h < 0.0) ? -value : value;
            } else {
                // sinh(x)
                // = (exp(x)-exp(-x))/2
                // = (exp(x)-1/exp(x))/2
                // = (expm1(x) + 1 - 1/(expm1(x)+1))/2
                // = (expm1(x) + (expm1(x)+1)/(expm1(x)+1) - 1/(expm1(x)+1))/2
                // = (expm1(x) + expm1(x)/(expm1(x)+1))/2
                double t = expm1(value);
                // Might be more accurate, if value < 1: return h*((t+t)-t*t/(t+1.0)).
                return h * (t + t/(t+1.0));
            }
        } else if (value < LOG_DOUBLE_MAX_VALUE) {
            return h * exp(value);
        } else {
            double t = exp(value*0.5);
            return (h*t)*t;
        }
    }

    /**
     * Some properties of cosh(x) = (exp(x)+exp(-x))/2:
     * 1) defined on ]-Infinity,+Infinity[
     * 2) result in [1,+Infinity[
     * 3) cosh(0) = 1
     * 4) cosh(x) = cosh(-x)
     * 5) lim(cosh(x),x->+Infinity) = +Infinity
     *    (y increasing exponentially faster than x)
     * 6) reaches +Infinity (double overflow) for x >= 710.475860073944,
     *    i.e. a bit further than exp(x)
     *
     * @param value A double value.
     * @return Value hyperbolic cosine.
     */
    public static double cosh(double value) {
        if (USE_JDK_MATH) {
            return StrictMath.cosh(value);
        }
        // cosh(x) = (exp(x)+exp(-x))/2
        if (value < 0.0) {
            value = -value;
        }
        if (value < LOG_TWO_POW_27) {
            if (value < TWO_POW_N27) {
                // cosh(x)
                // = (exp(x)+exp(-x))/2
                // = ((1+x+x^2/2!+...) + (1-x+x^2/2!-...))/2
                // = 1+x^2/2!+x^4/4!+...
                // For value of x small in magnitude, the sum of the terms does not add to 1.
                return 1;
            } else {
                // cosh(x)
                // = (exp(x)+exp(-x))/2
                // = (exp(x)+1/exp(x))/2
                double t = exp(value);
                return 0.5 * (t+1/t);
            }
        } else if (value < LOG_DOUBLE_MAX_VALUE) {
            return 0.5 * exp(value);
        } else {
            double t = exp(value*0.5);
            return (0.5*t)*t;
        }
    }

    /**
     * Much more accurate than cosh(value)-1,
     * for arguments (and results) close to zero.
     *
     * coshm1(-0.0) = -0.0, for homogeneity with
     * acosh1p(-0.0) = -0.0.
     *
     * @param value A double value.
     * @return Value hyperbolic cosine, minus 1.
     */
    public static double coshm1(double value) {
        // cosh(x)-1 = (exp(x)+exp(-x))/2 - 1
        if (value < 0.0) {
            value = -value;
        }
        if (value < LOG_TWO_POW_27) {
            if (value < TWO_POW_N27) {
                if (value == 0.0) {
                    // +-0.0
                    return value;
                }
                // Using (expm1(x)+expm1(-x))/2
                // is not accurate for tiny values,
                // for expm1 results are of higher
                // magnitude than the result and
                // of different signs, such as their
                // sum is not accurate.
                // cosh(x) - 1
                // = (exp(x)+exp(-x))/2 - 1
                // = ((1+x+x^2/2!+...) + (1-x+x^2/2!-...))/2 - 1
                // = x^2/2!+x^4/4!+...
                // ~= x^2 * (1/2 + x^2 * 1/24)
                //  = x^2 * 0.5 (since x < 2^-27)
                return 0.5 * value*value;
            } else {
                // cosh(x) - 1
                // = (exp(x)+exp(-x))/2 - 1
                // = (exp(x)-1+exp(-x)-1)/2
                // = (expm1(x)+expm1(-x))/2
                return 0.5 * (expm1(value)+expm1(-value));
            }
        } else if (value < LOG_DOUBLE_MAX_VALUE) {
            return 0.5 * exp(value) - 1.0;
        } else {
            // No need to subtract 1 from result.
            double t = exp(value*0.5);
            return (0.5*t)*t;
        }
    }

    /**
     * Computes hyperbolic sine and hyperbolic cosine together.
     *
     * @param value A double value.
     * @param hcosine (out) Value hyperbolic cosine.
     * @return Value hyperbolic sine.
     */
    public static double sinhAndCosh(double value, DoubleWrapper hcosine) {
        if (USE_JDK_MATH) {
            hcosine.value = StrictMath.cosh(value);
            return StrictMath.sinh(value);
        }
        // Mixup of sinh and cosh treatments: if you modify them,
        // you might want to also modify this.
        double h;
        if (value < 0.0) {
            value = -value;
            h = -0.5;
        } else {
            h = 0.5;
        }
        final double hsine;
        // LOG_TWO_POW_27 = 18.714973875118524
        if (value < LOG_TWO_POW_27) { // test from cosh
            // sinh
            if (value < TWO_POW_N28) {
                hsine = (h < 0.0) ? -value : value;
            } else {
                double t = expm1(value);
                hsine = h * (t + t/(t+1.0));
            }
            // cosh
            if (value < TWO_POW_N27) {
                hcosine.value = 1;
            } else {
                double t = exp(value);
                hcosine.value = 0.5 * (t+1/t);
            }
        } else if (value < 22.0) { // test from sinh
            // Here, value is in [18.714973875118524,22.0[.
            double t = expm1(value);
            hsine = h * (t + t/(t+1.0));
            hcosine.value = 0.5 * (t+1.0);
        } else {
            if (value < LOG_DOUBLE_MAX_VALUE) {
                hsine = h * exp(value);
            } else {
                double t = exp(value*0.5);
                hsine = (h*t)*t;
            }
            hcosine.value = Math.abs(hsine);
        }
        return hsine;
    }

    /**
     * Some properties of tanh(x) = sinh(x)/cosh(x) = (exp(2*x)-1)/(exp(2*x)+1):
     * 1) defined on ]-Infinity,+Infinity[
     * 2) result in ]-1,1[
     * 3) tanh(x) = -tanh(-x) (implies tanh(0) = 0)
     * 4) tanh(epsilon) ~= epsilon
     * 5) lim(tanh(x),x->+Infinity) = 1
     * 6) reaches 1 (double loss of precision) for x = 19.061547465398498
     *
     * @param value A double value.
     * @return Value hyperbolic tangent.
     */
    public static double tanh(double value) {
        if (USE_JDK_MATH) {
            return StrictMath.tanh(value);
        }
        // tanh(x) = sinh(x)/cosh(x)
        //         = (exp(x)-exp(-x))/(exp(x)+exp(-x))
        //         = (exp(2*x)-1)/(exp(2*x)+1)
        boolean negateResult = false;
        if (value < 0.0) {
            value = -value;
            negateResult = true;
        }
        double z;
        if (value < TANH_1_THRESHOLD) {
            if (value < TWO_POW_N55) {
                return negateResult ? -value*(1.0-value) : value*(1.0+value);
            } else if (value >= 1) {
                z = 1.0-2.0/(expm1(value+value)+2.0);
            } else {
                double t = expm1(-(value+value));
                z = -t/(t+2.0);
            }
        } else {
            z = (value != value) ? Double.NaN : 1.0;
        }
        return negateResult ? -z : z;
    }

    /**
     * Some properties of asinh(x) = log(x + sqrt(x^2 + 1))
     * 1) defined on ]-Infinity,+Infinity[
     * 2) result in ]-Infinity,+Infinity[
     * 3) asinh(x) = -asinh(-x) (implies asinh(0) = 0)
     * 4) asinh(epsilon) ~= epsilon
     * 5) lim(asinh(x),x->+Infinity) = +Infinity
     *    (y increasing logarithmically slower than x)
     *
     * @param value A double value.
     * @return Value hyperbolic arcsine.
     */
    public static double asinh(double value) {
        // asinh(x) = log(x + sqrt(x^2 + 1))
        boolean negateResult = false;
        if (value < 0.0) {
            value = -value;
            negateResult = true;
        }
        double result;
        // (about) smallest possible for
        // non-log1p case to be accurate.
        if (value < ASINH_LOG1P_THRESHOLD) {
            // Around this range, FDLIBM uses
            // log1p(value+value*value/(1+sqrt(value*value+1))),
            // but it's slower, so we don't use it.
            /*
             * If x is close to zero, log argument is close to 1,
             * so to avoid precision loss we use log1p(double),
             * with
             * (1+x)^p = 1 + p * x + (p*(p-1))/2! * x^2 + (p*(p-1)*(p-2))/3! * x^3 + ...
             * (1+x)^p = 1 + p * x * (1 + (p-1)/2 * x * (1 + (p-2)/3 * x + ...)
             * (1+x)^0.5 = 1 + 0.5 * x * (1 + (0.5-1)/2 * x * (1 + (0.5-2)/3 * x + ...)
             * (1+x^2)^0.5 = 1 + 0.5 * x^2 * (1 + (0.5-1)/2 * x^2 * (1 + (0.5-2)/3 * x^2 + ...)
             * x + (1+x^2)^0.5 = 1 + x * (1 + 0.5 * x * (1 + (0.5-1)/2 * x^2 * (1 + (0.5-2)/3 * x^2 + ...))
             * so
             * asinh(x) = log1p(x * (1 + 0.5 * x * (1 + (0.5-1)/2 * x^2 * (1 + (0.5-2)/3 * x^2 + ...)))
             */
            final double x = value;
            final double x2 = x*x;
            // Enough terms for good accuracy,
            // given our threshold.
            final double argLog1p = (x *
                    (1 + 0.5 * x
                            * (1 + (0.5-1)/2 * x2
                                    * (1 + (0.5-2)/3 * x2
                                            * (1 + (0.5-3)/4 * x2
                                                    * (1 + (0.5-4)/5 * x2
                                                    ))))));
            result = log1p(argLog1p);
        } else if (value < ASINH_ACOSH_SQRT_ELISION_THRESHOLD) {
            // Around this range, FDLIBM uses
            // log(2*value+1/(value+sqrt(value*value+1))),
            // but it involves an additional division
            // so we don't use it.
            result = log(value + sqrt(value*value + 1.0));
        } else {
            // log(2*value) would overflow for value > Double.MAX_VALUE/2,
            // so we compute otherwise.
            result = LOG_2 + log(value);
        }
        return negateResult ? -result : result;
    }

    /**
     * Some properties of acosh(x) = log(x + sqrt(x^2 - 1)):
     * 1) defined on [1,+Infinity[
     * 2) result in ]0,+Infinity[ (by convention, since cosh(x) = cosh(-x))
     * 3) acosh(1) = 0
     * 4) acosh(1+epsilon) ~= log(1 + sqrt(2*epsilon)) ~= sqrt(2*epsilon)
     * 5) lim(acosh(x),x->+Infinity) = +Infinity
     *    (y increasing logarithmically slower than x)
     *
     * @param value A double value.
     * @return Value hyperbolic arccosine.
     */
    public static double acosh(double value) {
        if (!(value > 1.0)) {
            // NaN, or value <= 1
            if (ANTI_JIT_OPTIM_CRASH_ON_NAN) {
                return (value < 1.0) ? Double.NaN : value - 1.0;
            } else {
                return (value == 1.0) ? 0.0 : Double.NaN;
            }
        }
        double result;
        if (value < ASINH_ACOSH_SQRT_ELISION_THRESHOLD) {
            // Around this range, FDLIBM uses
            // log(2*value-1/(value+sqrt(value*value-1))),
            // but it involves an additional division
            // so we don't use it.
            result = log(value + sqrt(value*value - 1.0));
        } else {
            // log(2*value) would overflow for value > Double.MAX_VALUE/2,
            // so we compute otherwise.
            result = LOG_2 + log(value);
        }
        return result;
    }

    /**
     * Much more accurate than acosh(1+value),
     * for arguments (and results) close to zero.
     *
     * acosh1p(-0.0) = -0.0, for homogeneity with
     * sqrt(-0.0) = -0.0, which looks about the same
     * near 0.
     *
     * @param value A double value.
     * @return Hyperbolic arccosine of (1+value).
     */
    public static double acosh1p(double value) {
        if (!(value > 0.0)) {
            // NaN, or value <= 0.
            // If value is -0.0, returning -0.0.
            if (ANTI_JIT_OPTIM_CRASH_ON_NAN) {
                return (value < 0.0) ? Double.NaN : value;
            } else {
                return (value == 0.0) ? value : Double.NaN;
            }
        }
        double result;
        if (value < (ASINH_ACOSH_SQRT_ELISION_THRESHOLD-1)) {
            // acosh(1+x)
            // = log((1+x) + sqrt((1+x)^2 - 1))
            // = log(1 + x + sqrt(1 + 2*x + x^2 - 1))
            // = log1p(x + sqrt(2*x + x^2))
            // = log1p(x + sqrt(x * (2 + x))
            result = log1p(value + sqrt(value * (2 + value)));
        } else {
            result = LOG_2 + log(1+value);
        }
        return result;
    }

    /**
     * Some properties of atanh(x) = log((1+x)/(1-x))/2:
     * 1) defined on ]-1,1[
     * 2) result in ]-Infinity,+Infinity[
     * 3) atanh(-1) = -Infinity (by continuity)
     * 4) atanh(1) = +Infinity (by continuity)
     * 5) atanh(epsilon) ~= epsilon
     * 6) lim(atanh(x),x->1) = +Infinity
     *
     * @param value A double value.
     * @return Value hyperbolic arctangent.
     */
    public static double atanh(double value) {
        boolean negateResult = false;
        if (value < 0.0) {
            value = -value;
            negateResult = true;
        }
        double result;
        if (!(value < 1.0)) {
            // NaN, or value >= 1
            if (ANTI_JIT_OPTIM_CRASH_ON_NAN) {
                result = (value > 1.0) ? Double.NaN : Double.POSITIVE_INFINITY + value;
            } else {
                result = (value == 1.0) ? Double.POSITIVE_INFINITY : Double.NaN;
            }
        } else {
            // For value < 0.5, FDLIBM uses
            // 0.5 * log1p((value+value) + (value+value)*value/(1-value)),
            // instead, but this is good enough for us.
            // atanh(x)
            // = log((1+x)/(1-x))/2
            // = log((1-x+2x)/(1-x))/2
            // = log1p(2x/(1-x))/2
            result = 0.5 * log1p((value+value)/(1.0-value));
        }
        return negateResult ? -result : result;
    }

    /*
     * exponentials
     */

    /**
     * @param value A double value.
     * @return e^value.
     */
    public static double exp(double value) {
        if (USE_JDK_MATH) {
            return StrictMath.exp(value);
        }
        // exp(x) = exp([x])*exp(y)
        // with [x] the integer part of x, and y = x-[x]
        // ===>
        // We find an approximation of y, called z.
        // ===>
        // exp(x) = exp([x])*(exp(z)*exp(epsilon))
        // with epsilon = y - z
        // ===>
        // We have exp([x]) and exp(z) pre-computed in tables, we "just" have to compute exp(epsilon).
        //
        // We use the same indexing (cast to int) to compute x integer part and the
        // table index corresponding to z, to avoid two int casts.
        // Also, to optimize index multiplication and division, we use powers of two,
        // so that we can do it with bits shifts.

        if (value > EXP_OVERFLOW_LIMIT) {
            return Double.POSITIVE_INFINITY;
        } else if (!(value >= EXP_UNDERFLOW_LIMIT)) {
            return (value != value) ? Double.NaN : 0.0;
        }

        final int indexes = (int)(value*EXP_LO_INDEXING);

        final int valueInt;
        if (indexes >= 0) {
            valueInt = (indexes>>EXP_LO_INDEXING_DIV_SHIFT);
        } else {
            valueInt = -((-indexes)>>EXP_LO_INDEXING_DIV_SHIFT);
        }
        final double hiTerm = MyTExp.expHiTab[valueInt-(int)EXP_UNDERFLOW_LIMIT];

        final int zIndex = indexes - (valueInt<<EXP_LO_INDEXING_DIV_SHIFT);
        final double y = (value-valueInt);
        final double z = zIndex*(1.0/EXP_LO_INDEXING);
        final double eps = y-z;
        final double expZ = MyTExp.expLoPosTab[zIndex+EXP_LO_TAB_MID_INDEX];
        final double expEps = (1+eps*(1+eps*(1.0/2+eps*(1.0/6+eps*(1.0/24)))));
        final double loTerm = expZ * expEps;

        return hiTerm * loTerm;
    }

    /**
     * Quick exp, with a max relative error of about 2.94e-2 for |value| < 700.0 or so,
     * and no accuracy at all outside this range.
     * Derived from a note by Nicol N. Schraudolph, IDSIA, 1998.
     *
     * @param value A double value.
     * @return e^value.
     */
    public static double expQuick(double value) {
        if (USE_JDK_MATH) {
            return StrictMath.exp(value);
        }
        /*
         * Cast of double values, even in long range, into long, is slower than
         * from double to int for values in int range, and then from int to long.
         * For that reason, we only work with integer values in int range
         * (corresponding to the 32 first bits of the long, containing sign,
         * exponent, and highest significant bits of double's mantissa),
         * and cast twice.
         *
         * Constants determined empirically, using a random-based metaheuristic.
         * Should be possible to find better ones.
         */
        return Double.longBitsToDouble(((long)(int)(1512775.3952 * value + 1.0726481222E9))<<32);
    }

    /**
     * Much more accurate than exp(value)-1,
     * for arguments (and results) close to zero.
     *
     * @param value A double value.
     * @return e^value-1.
     */
    public static double expm1(double value) {
        if (USE_JDK_MATH) {
            return StrictMath.expm1(value);
        }
        // If value is far from zero, we use exp(value)-1.
        //
        // If value is close to zero, we use the following formula:
        // exp(value)-1
        // = exp(valueApprox)*exp(epsilon)-1
        // = exp(valueApprox)*(exp(epsilon)-exp(-valueApprox))
        // = exp(valueApprox)*(1+epsilon+epsilon^2/2!+...-exp(-valueApprox))
        // = exp(valueApprox)*((1-exp(-valueApprox))+epsilon+epsilon^2/2!+...)
        // exp(valueApprox) and exp(-valueApprox) being stored in tables.

        if (Math.abs(value) < EXP_LO_DISTANCE_TO_ZERO) {
            // Taking int part instead of rounding, which takes too long.
            int i = (int)(value*EXP_LO_INDEXING);
            double delta = value-i*(1.0/EXP_LO_INDEXING);
            return MyTExp.expLoPosTab[i+EXP_LO_TAB_MID_INDEX]*(MyTExp.expLoNegTab[i+EXP_LO_TAB_MID_INDEX]+delta*(1+delta*(1.0/2+delta*(1.0/6+delta*(1.0/24+delta*(1.0/120))))));
        } else {
            return exp(value)-1;
        }
    }

    /*
     * logarithms
     */

    /**
     * @param value A double value.
     * @return Value logarithm (base e).
     */
    public static double log(double value) {
        if (USE_JDK_MATH || (!USE_REDEFINED_LOG)) {
            return StrictMath.log(value);
        }
        if (value > 0.0) {
            if (value == Double.POSITIVE_INFINITY) {
                return Double.POSITIVE_INFINITY;
            }

            // For normal values not close to 1.0, we use the following formula:
            // log(value)
            // = log(2^exponent*1.mantissa)
            // = log(2^exponent) + log(1.mantissa)
            // = exponent * log(2) + log(1.mantissa)
            // = exponent * log(2) + log(1.mantissaApprox) + log(1.mantissa/1.mantissaApprox)
            // = exponent * log(2) + log(1.mantissaApprox) + log(1+epsilon)
            // = exponent * log(2) + log(1.mantissaApprox) + epsilon-epsilon^2/2+epsilon^3/3-epsilon^4/4+...
            // with:
            // 1.mantissaApprox <= 1.mantissa,
            // log(1.mantissaApprox) in table,
            // epsilon = (1.mantissa/1.mantissaApprox)-1
            //
            // To avoid bad relative error for small results,
            // values close to 1.0 are treated aside, with the formula:
            // log(x) = z*(2+z^2*((2.0/3)+z^2*((2.0/5))+z^2*((2.0/7))+...)))
            // with z=(x-1)/(x+1)

            double h;
            if (value > 0.95) {
                if (value < 1.14) {
                    double z = (value-1.0)/(value+1.0);
                    double z2 = z*z;
                    return z*(2+z2*((2.0/3)+z2*((2.0/5)+z2*((2.0/7)+z2*((2.0/9)+z2*((2.0/11)))))));
                }
                h = 0.0;
            } else if (value < DOUBLE_MIN_NORMAL) {
                // Ensuring value is normal.
                value *= TWO_POW_52;
                // log(x*2^52)
                // = log(x)-ln(2^52)
                // = log(x)-52*ln(2)
                h = -52*LOG_2;
            } else {
                h = 0.0;
            }

            int valueBitsHi = (int)(Double.doubleToRawLongBits(value)>>32);
            int valueExp = (valueBitsHi>>20)-MAX_DOUBLE_EXPONENT;
            // Getting the first LOG_BITS bits of the mantissa.
            int xIndex = ((valueBitsHi<<12)>>>(32-LOG_BITS));

            // 1.mantissa/1.mantissaApprox - 1
            double z = (value * twoPowNormalOrSubnormal(-valueExp)) * MyTLog.logXInvTab[xIndex] - 1;

            z *= (1-z*((1.0/2)-z*((1.0/3))));

            return h + valueExp * LOG_2 + (MyTLog.logXLogTab[xIndex] + z);

        } else if (value == 0.0) {
            return Double.NEGATIVE_INFINITY;
        } else { // value < 0.0, or value is NaN
            return Double.NaN;
        }
    }

    /**
     * Quick log, with a max relative error of about 1.9e-3
     * for values in ]Double.MIN_NORMAL,+Infinity[, and
     * worse accuracy outside this range.
     *
     * @param value A double value, in ]0,+Infinity[ (strictly positive and finite).
     * @return Value logarithm (base e).
     */
    public static double logQuick(double value) {
        if (USE_JDK_MATH) {
            return StrictMath.log(value);
        }
        /*
         * Inverse of Schraudolph's method for exp, is very inaccurate near 1,
         * and not that fast (even using floats), especially with added if's
         * to deal with values near 1, so we don't use it, and use a simplified
         * version of our log's redefined algorithm.
         */

        // Simplified version of log's redefined algorithm:
        // log(value) ~= exponent * log(2) + log(1.mantissaApprox)

        double h;
        if (value > 0.87) {
            if (value < 1.16) {
                return 2.0 * (value-1.0)/(value+1.0);
            }
            h = 0.0;
        } else if (value < DOUBLE_MIN_NORMAL) {
            value *= TWO_POW_52;
            h = -52*LOG_2;
        } else {
            h = 0.0;
        }

        int valueBitsHi = (int)(Double.doubleToRawLongBits(value)>>32);
        int valueExp = (valueBitsHi>>20)-MAX_DOUBLE_EXPONENT;
        int xIndex = ((valueBitsHi<<12)>>>(32-LOG_BITS));

        return h + valueExp * LOG_2 + MyTLog.logXLogTab[xIndex];
    }

    /**
     * @param value A double value.
     * @return Value logarithm (base 10).
     */
    public static double log10(double value) {
        if (USE_JDK_MATH || (!USE_REDEFINED_LOG)) {
            return StrictMath.log10(value);
        }
        // INV_LOG_10 is < 1, but there is no risk of log(double)
        // overflow (positive or negative) while the end result shouldn't,
        // since log(Double.MIN_VALUE) and log(Double.MAX_VALUE) have
        // magnitudes of just a few hundreds.
        return log(value) * INV_LOG_10;
    }

    /**
     * Much more accurate than log(1+value),
     * for arguments (and results) close to zero.
     *
     * @param value A double value.
     * @return Logarithm (base e) of (1+value).
     */
    public static double log1p(double value) {
        if (USE_JDK_MATH) {
            return StrictMath.log1p(value);
        }
        if (false) {
            // This also works. Simpler but a bit slower.
            if (value == Double.POSITIVE_INFINITY) {
                return Double.POSITIVE_INFINITY;
            }
            double valuePlusOne = 1+value;
            if (valuePlusOne == 1.0) {
                return value;
            } else {
                return log(valuePlusOne)*(value/(valuePlusOne-1.0));
            }
        }
        if (value > -1.0) {
            if (value == Double.POSITIVE_INFINITY) {
                return Double.POSITIVE_INFINITY;
            }

            // ln'(x) = 1/x
            // so
            // log(x+epsilon) ~= log(x) + epsilon/x
            //
            // Let u be 1+value rounded:
            // 1+value = u+epsilon
            //
            // log(1+value)
            // = log(u+epsilon)
            // ~= log(u) + epsilon/value
            // We compute log(u) as done in log(double), and then add the corrective term.

            double valuePlusOne = 1.0+value;
            if (valuePlusOne == 1.0) {
                return value;
            } else if (Math.abs(value) < 0.15) {
                double z = value/(value+2.0);
                double z2 = z*z;
                return z*(2+z2*((2.0/3)+z2*((2.0/5)+z2*((2.0/7)+z2*((2.0/9)+z2*((2.0/11)))))));
            }

            int valuePlusOneBitsHi = (int)(Double.doubleToRawLongBits(valuePlusOne)>>32) & 0x7FFFFFFF;
            int valuePlusOneExp = (valuePlusOneBitsHi>>20)-MAX_DOUBLE_EXPONENT;
            // Getting the first LOG_BITS bits of the mantissa.
            int xIndex = ((valuePlusOneBitsHi<<12)>>>(32-LOG_BITS));

            // 1.mantissa/1.mantissaApprox - 1
            double z = (valuePlusOne * twoPowNormalOrSubnormal(-valuePlusOneExp)) * MyTLog.logXInvTab[xIndex] - 1;

            z *= (1-z*((1.0/2)-z*(1.0/3)));

            // Adding epsilon/valuePlusOne to z,
            // with
            // epsilon = value - (valuePlusOne-1)
            // (valuePlusOne + epsilon ~= 1+value (not rounded))

            return valuePlusOneExp * LOG_2 + MyTLog.logXLogTab[xIndex] + (z + (value - (valuePlusOne-1))/valuePlusOne);
        } else if (value == -1.0) {
            return Double.NEGATIVE_INFINITY;
        } else { // value < -1.0, or value is NaN
            return Double.NaN;
        }
    }

    /*
     * powers
     */

    /**
     * 1e-13ish accuracy or better on whole double range.
     *
     * @param value A double value.
     * @param power A power.
     * @return value^power.
     */
    public static double pow(double value, double power) {
        if (USE_JDK_MATH) {
            return StrictMath.pow(value,power);
        }
        if (power == 0.0) {
            return 1.0;
        } else if (power == 1.0) {
            return value;
        }
        if (value <= 0.0) {
            // powerInfo: 0 if not integer, 1 if even integer, -1 if odd integer
            int powerInfo;
            if (Math.abs(power) >= (TWO_POW_52*2)) {
                // The binary digit just before comma is outside mantissa,
                // thus it is always 0: power is an even integer.
                powerInfo = 1;
            } else {
                // If power's magnitude permits, we cast into int instead of into long,
                // as it is faster.
                if (Math.abs(power) <= (double)Integer.MAX_VALUE) {
                    int powerAsInt = (int)power;
                    if (power == (double)powerAsInt) {
                        powerInfo = ((powerAsInt & 1) == 0) ? 1 : -1;
                    } else { // power is not an integer (and not NaN, due to test against Integer.MAX_VALUE)
                        powerInfo = 0;
                    }
                } else {
                    long powerAsLong = (long)power;
                    if (power == (double)powerAsLong) {
                        powerInfo = ((powerAsLong & 1) == 0) ? 1 : -1;
                    } else { // power is not an integer, or is NaN
                        if (power != power) {
                            return Double.NaN;
                        }
                        powerInfo = 0;
                    }
                }
            }

            if (value == 0.0) {
                if (power < 0.0) {
                    return (powerInfo < 0) ? 1/value : Double.POSITIVE_INFINITY;
                } else { // power > 0.0 (0 and NaN cases already treated)
                    return (powerInfo < 0) ? value : 0.0;
                }
            } else { // value < 0.0
                if (value == Double.NEGATIVE_INFINITY) {
                    if (powerInfo < 0) { // power odd integer
                        return (power < 0.0) ? -0.0 : Double.NEGATIVE_INFINITY;
                    } else { // power even integer, or not an integer
                        return (power < 0.0) ? 0.0 : Double.POSITIVE_INFINITY;
                    }
                } else {
                    return (powerInfo == 0) ? Double.NaN : powerInfo * exp(power*log(-value));
                }
            }
        } else { // value > 0.0, or value is NaN
            return exp(power*log(value));
        }
    }

    /**
     * Quick pow, with a max relative error of about 1e-2
     * for value >= Double.MIN_NORMAL and 1e-10 < |value^power| < 1e10,
     * of about 6e-2 for value >= Double.MIN_NORMAL and 1e-40 < |value^power| < 1e40,
     * and worse accuracy otherwise.
     *
     * @param value A double value, in ]0,+Infinity[ (strictly positive and finite).
     * @param power A double value.
     * @return value^power.
     */
    public static double powQuick(double value, double power) {
        if (USE_JDK_MATH) {
            return StrictMath.pow(value,power);
        }
        return exp(power*logQuick(value));
    }

    /**
     * This treatment is somehow accurate for low values of |power|,
     * and for |power*getExponent(value)| < 1023 or so (to stay away
     * from double extreme magnitudes (large and small)).
     *
     * @param value A double value.
     * @param power A power.
     * @return value^power.
     */
    public static double powFast(double value, int power) {
        if (USE_JDK_MATH) {
            return StrictMath.pow(value,power);
        }
        if (power < 3) {
            if (power < 0) {
                // Opposite of Integer.MIN_VALUE does not exist as int.
                if (power == Integer.MIN_VALUE) {
                    // Integer.MAX_VALUE = -(power+1)
                    return 1.0/(powFast(value,Integer.MAX_VALUE) * value);
                } else {
                    return 1.0/powFast(value,-power);
                }
            } else {
                // Here, power is in [0,2].
                if (power == 2) { // Most common case first.
                    return value * value;
                } else if (power == 0) {
                    return 1.0;
                } else { // power == 1
                    return value;
                }
            }
        } else { // power >= 4
            double oddRemains = 1.0;
            // If power <= 5, faster to finish outside the loop.
            while (power > 5) {
                // Test if power is odd.
                if ((power & 1) != 0) {
                    oddRemains *= value;
                }
                value *= value;
                power >>= 1; // power = power / 2
            }
            // Here, power is in [3,5].
            if (power == 3) {
                return oddRemains * value * value * value;
            } else { // power in [4,5].
                double v2 = value * value;
                if (power == 4) {
                    return oddRemains * v2 * v2;
                } else { // power == 5
                    return oddRemains * v2 * v2 * value;
                }
            }
        }
    }

    /**
     * @param value A float value.
     * @return value*value.
     */
    public static float pow2(float value) {
        return value*value;
    }

    /**
     * @param value A double value.
     * @return value*value.
     */
    public static double pow2(double value) {
        return value*value;
    }

    /**
     * @param value A float value.
     * @return value*value*value.
     */
    public static float pow3(float value) {
        return value*value*value;
    }

    /**
     * @param value A double value.
     * @return value*value*value.
     */
    public static double pow3(double value) {
        return value*value*value;
    }

    /*
     * roots
     */

    /**
     * @param value A double value.
     * @return Value square root.
     */
    public static double sqrt(double value) {
        if (USE_JDK_MATH || (!USE_REDEFINED_SQRT)) {
            return StrictMath.sqrt(value);
        }
        // See cbrt for comments, sqrt uses the same ideas.

        if (!(value > 0.0)) { // value <= 0.0, or value is NaN
            if (ANTI_JIT_OPTIM_CRASH_ON_NAN) {
                return (value < 0.0) ? Double.NaN : value;
            } else {
                return (value == 0.0) ? value : Double.NaN;
            }
        } else if (value == Double.POSITIVE_INFINITY) {
            return Double.POSITIVE_INFINITY;
        }

        double h;
        if (value < DOUBLE_MIN_NORMAL) {
            value *= TWO_POW_52;
            h = 2*TWO_POW_N26;
        } else {
            h = 2.0;
        }

        int valueBitsHi = (int)(Double.doubleToRawLongBits(value)>>32);
        int valueExponentIndex = (valueBitsHi>>20)+(-MAX_DOUBLE_EXPONENT-MIN_DOUBLE_EXPONENT);
        int xIndex = ((valueBitsHi<<12)>>>(32-SQRT_LO_BITS));

        double result = MyTSqrt.sqrtXSqrtHiTab[valueExponentIndex] * MyTSqrt.sqrtXSqrtLoTab[xIndex];
        double slope = MyTSqrt.sqrtSlopeHiTab[valueExponentIndex] * MyTSqrt.sqrtSlopeLoTab[xIndex];
        value *= 0.25;

        result += (value - result * result) * slope;
        result += (value - result * result) * slope;
        return h*(result + (value - result * result) * slope);
    }

    /**
     * Quick sqrt, with with a max relative error of about 3.41e-2
     * for values in [Double.MIN_NORMAL,Double.MAX_VALUE], and worse
     * accuracy outside this range.
     *
     * @param value A double value.
     * @return Value square root.
     */
    public static double sqrtQuick(double value) {
        if (USE_JDK_MATH) {
            return StrictMath.sqrt(value);
        }
        final long bits = Double.doubleToRawLongBits(value);
        /*
         * Constant determined empirically, using a random-based metaheuristic.
         * Should be possible to find a better one.
         */
        return Double.longBitsToDouble((bits+4606859074900000000L)>>>1);
    }

    /**
     * Quick inverse of square root, with a max relative error of about 3.44e-2
     * for values in [Double.MIN_NORMAL,Double.MAX_VALUE], and worse accuracy
     * outside this range.
     *
     * This implementation uses zero step of Newton's method.
     * Here are the max relative errors on [Double.MIN_NORMAL,Double.MAX_VALUE]
     * depending on number of steps, if you want to copy-paste this code
     * and use your own number:
     * n=0: about 3.44e-2
     * n=1: about 1.75e-3
     * n=2: about 4.6e-6
     * n=3: about 3.17e-11
     * n=4: about 3.92e-16
     * n=5: about 3.03e-16
     *
     * @param value A double value.
     * @return Inverse of value square root.
     */
    public static double invSqrtQuick(double value) {
        if (USE_JDK_MATH) {
            return 1/StrictMath.sqrt(value);
        }
        /*
         * http://en.wikipedia.org/wiki/Fast_inverse_square_root
         */
        if (false) {
            // With one Newton step (much slower than
            // 1/Math.sqrt(double) if not optimized).
            final double halfInitial = value * 0.5;
            long bits = Double.doubleToRawLongBits(value);
            // If n=0, 6910474759270000000L might be better (3.38e-2 max relative error).
            bits = 0x5FE6EB50C7B537A9L - (bits>>1);
            value = Double.longBitsToDouble(bits);
            value = value * (1.5 - halfInitial * value * value); // Newton step, can repeat.
            return value;
        } else {
            return Double.longBitsToDouble(0x5FE6EB50C7B537A9L - (Double.doubleToRawLongBits(value)>>1));
        }
    }

    /**
     * @param value A double value.
     * @return Value cubic root.
     */
    public static double cbrt(double value) {
        if (USE_JDK_MATH) {
            return StrictMath.cbrt(value);
        }
        double h;
        if (value < 0.0) {
            if (value == Double.NEGATIVE_INFINITY) {
                return Double.NEGATIVE_INFINITY;
            }
            value = -value;
            // Making sure value is normal.
            if (value < DOUBLE_MIN_NORMAL) {
                value *= (TWO_POW_52*TWO_POW_26);
                // h = <result_sign> * <result_multiplicator_to_avoid_overflow> / <cbrt(value_multiplicator_to_avoid_subnormal)>
                h = -2*TWO_POW_N26;
            } else {
                h = -2.0;
            }
        } else {
            if (!(value < Double.POSITIVE_INFINITY)) { // value is +Infinity, or value is NaN
                return value;
            }
            // Making sure value is normal.
            if (value < DOUBLE_MIN_NORMAL) {
                if (value == 0.0) {
                    // cbrt(0.0) = 0.0, cbrt(-0.0) = -0.0
                    return value;
                }
                value *= (TWO_POW_52*TWO_POW_26);
                h = 2*TWO_POW_N26;
            } else {
                h = 2.0;
            }
        }

        // Normal value is (2^<value exponent> * <a value in [1,2[>).
        // First member cubic root is computed, and multiplied with an approximation
        // of the cubic root of the second member, to end up with a good guess of
        // the result before using Newton's (or Archimedes's) method.
        // To compute the cubic root approximation, we use the formula "cbrt(value) = cbrt(x) * cbrt(value/x)",
        // choosing x as close to value as possible but inferior to it, so that cbrt(value/x) is close to 1
        // (we could iterate on this method, using value/x as new value for each iteration,
        // but finishing with Newton's method is faster).

        // Shift and cast into an int, which overall is faster than working with a long.
        int valueBitsHi = (int)(Double.doubleToRawLongBits(value)>>32);
        int valueExponentIndex = (valueBitsHi>>20)+(-MAX_DOUBLE_EXPONENT-MIN_DOUBLE_EXPONENT);
        // Getting the first CBRT_LO_BITS bits of the mantissa.
        int xIndex = ((valueBitsHi<<12)>>>(32-CBRT_LO_BITS));
        double result = MyTCbrt.cbrtXCbrtHiTab[valueExponentIndex] * MyTCbrt.cbrtXCbrtLoTab[xIndex];
        double slope = MyTCbrt.cbrtSlopeHiTab[valueExponentIndex] * MyTCbrt.cbrtSlopeLoTab[xIndex];

        // Lowering values to avoid overflows when using Newton's method
        // (we will then just have to return twice the result).
        // result^3 = value
        // (result/2)^3 = value/8
        value *= 0.125;
        // No need to divide result here, as division is factorized in result computation tables.
        // result *= 0.5;

        // Newton's method, looking for y = x^(1/p):
        // y(n) = y(n-1) + (x-y(n-1)^p) * slope(y(n-1))
        // y(n) = y(n-1) + (x-y(n-1)^p) * (1/p)*(x(n-1)^(1/p-1))
        // y(n) = y(n-1) + (x-y(n-1)^p) * (1/p)*(x(n-1)^((1-p)/p))
        // with x(n-1)=y(n-1)^p, i.e.:
        // y(n) = y(n-1) + (x-y(n-1)^p) * (1/p)*(y(n-1)^(1-p))
        //
        // For p=3:
        // y(n) = y(n-1) + (x-y(n-1)^3) * (1/(3*y(n-1)^2))

        // To save time, we don't recompute the slope between Newton's method steps,
        // as initial slope is good enough for a few iterations.
        //
        // NB: slope = 1/(3*trueResult*trueResult)
        //     As we have result = trueResult/2 (to avoid overflows), we have:
        //     slope = 4/(3*result*result)
        //           = (4/3)*resultInv*resultInv
        //     with newResultInv = 1/newResult
        //                       = 1/(oldResult+resultDelta)
        //                       = (oldResultInv)*1/(1+resultDelta/oldResult)
        //                       = (oldResultInv)*1/(1+resultDelta*oldResultInv)
        //                      ~= (oldResultInv)*(1-resultDelta*oldResultInv)
        //     ===> Successive slopes could be computed without division, if needed,
        //          by computing resultInv (instead of slope right away) and retrieving
        //          slopes from it.

        result += (value - result * result * result) * slope;
        result += (value - result * result * result) * slope;
        return h*(result + (value - result * result * result) * slope);
    }

    /**
     * @return sqrt(x^2+y^2) without intermediate overflow or underflow.
     */
    public static double hypot(double x, double y) {
        if (USE_JDK_MATH) {
            return StrictMath.hypot(x,y);
        }
        x = Math.abs(x);
        y = Math.abs(y);
        // Ensuring x <= y.
        if (y < x) {
            double a = x;
            x = y;
            y = a;
        } else if (!(y >= x)) { // Testing if we have some NaN.
            return hypot_NaN(x, y);
        }

        if (y-x == y) {
            // x too small to subtract from y.
            return y;
        } else {
            double factor;
            if (y > HYPOT_MAX_MAG) {
                // y is too large: scaling down.
                x *= (1/HYPOT_FACTOR);
                y *= (1/HYPOT_FACTOR);
                factor = HYPOT_FACTOR;
            } else if (x < (1/HYPOT_MAX_MAG)) {
                // x is too small: scaling up.
                x *= HYPOT_FACTOR;
                y *= HYPOT_FACTOR;
                factor = (1/HYPOT_FACTOR);
            } else {
                factor = 1.0;
            }
            return factor * sqrt(x*x+y*y);
        }
    }

    /**
     * @return sqrt(x^2+y^2+z^2) without intermediate overflow or underflow.
     */
    public static double hypot(double x, double y, double z) {
        if (USE_JDK_MATH) {
            // No simple JDK equivalent.
        }
        x = Math.abs(x);
        y = Math.abs(y);
        z = Math.abs(z);
        /*
         * Considering that z magnitude is the most likely to be the smaller,
         * hence ensuring z <= y <= x, and not x <= y <= z, for less swaps.
         */
        // Ensuring z <= y.
        if (z > y) {
            // y < z: swapping y and z
            double a = z;
            z = y;
            y = a;
        } else if (!(z <= y)) { // Testing if y or z is NaN.
            return hypot_NaN(x, y, z);
        }
        // Ensuring y <= x.
        if (z > x) {
            // x < z <= y: moving x
            double oldZ = z;
            z = x;
            double oldY = y;
            y = oldZ;
            x = oldY;
        } else if (y > x) {
            // z <= x < y: swapping x and y
            double a = y;
            y = x;
            x = a;
        } else if (x != x) { // Testing if x is NaN.
            return hypot_NaN(x, y, z);
        }

        if (x-y == x) {
            // y, hence z, too small to subtract from x.
            return x;
        } else if (y-z == y) {
            // z too small to subtract from y, hence x.
            double factor;
            if (x > HYPOT_MAX_MAG) {
                // x is too large: scaling down.
                x *= (1/HYPOT_FACTOR);
                y *= (1/HYPOT_FACTOR);
                factor = HYPOT_FACTOR;
            } else if (y < (1/HYPOT_MAX_MAG)) {
                // y is too small: scaling up.
                x *= HYPOT_FACTOR;
                y *= HYPOT_FACTOR;
                factor = (1/HYPOT_FACTOR);
            } else {
                factor = 1.0;
            }
            return factor * sqrt(x*x+y*y);
        } else {
            double factor;
            if (x > HYPOT_MAX_MAG) {
                // x is too large: scaling down.
                x *= (1/HYPOT_FACTOR);
                y *= (1/HYPOT_FACTOR);
                z *= (1/HYPOT_FACTOR);
                factor = HYPOT_FACTOR;
            } else if (z < (1/HYPOT_MAX_MAG)) {
                // z is too small: scaling up.
                x *= HYPOT_FACTOR;
                y *= HYPOT_FACTOR;
                z *= HYPOT_FACTOR;
                factor = (1/HYPOT_FACTOR);
            } else {
                factor = 1.0;
            }
            // Adding smaller magnitudes together first.
            return factor * sqrt(x*x+(y*y+z*z));
        }
    }

    /*
     * close values
     */

    /**
     * @param value A float value.
     * @return Floor of value.
     */
    public static float floor(float value) {
        final int exponent = getExponent(value);
        if (exponent < 0) {
            // abs(value) < 1.
            if (value < 0.0f) {
                return -1.0f;
            } else {
                // 0.0f, or -0.0f if value is -0.0f
                return 0.0f * value;
            }
        } else if (exponent < 23) {
            // A bit faster than using casts.
            final int bits = Float.floatToRawIntBits(value);
            final int anteCommaBits = bits & (0xFF800000>>exponent);
            if ((value < 0.0f) && (anteCommaBits != bits)) {
                return Float.intBitsToFloat(anteCommaBits) - 1.0f;
            } else {
                return Float.intBitsToFloat(anteCommaBits);
            }
        } else {
            // +-Infinity, NaN, or a mathematical integer.
            return value;
        }
    }

    /**
     * @param value A double value.
     * @return Floor of value.
     */
    public static double floor(double value) {
        if (USE_JDK_MATH) {
            return StrictMath.floor(value);
        }
        if (ANTI_SLOW_CASTS) {
            double valueAbs = Math.abs(value);
            if (valueAbs <= (double)Integer.MAX_VALUE) {
                if (value > 0.0) {
                    return (double)(int)value;
                } else if (value < 0.0) {
                    double anteCommaDigits = (double)(int)value;
                    if (value != anteCommaDigits) {
                        return anteCommaDigits - 1.0;
                    } else {
                        return anteCommaDigits;
                    }
                } else { // value is +-0.0 (not NaN due to test against Integer.MAX_VALUE)
                    return value;
                }
            } else if (valueAbs < TWO_POW_52) {
                // We split the value in two:
                // high part, which is a mathematical integer,
                // and the rest, for which we can get rid of the
                // post comma digits by casting into an int.
                double highPart = ((int)(value * TWO_POW_N26)) * TWO_POW_26;
                if (value > 0.0) {
                    return highPart + (double)((int)(value - highPart));
                } else {
                    double anteCommaDigits = highPart + (double)((int)(value - highPart));
                    if (value != anteCommaDigits) {
                        return anteCommaDigits - 1.0;
                    } else {
                        return anteCommaDigits;
                    }
                }
            } else { // abs(value) >= 2^52, or value is NaN
                return value;
            }
        } else {
            final int exponent = getExponent(value);
            if (exponent < 0) {
                // abs(value) < 1.
                if (value < 0.0) {
                    return -1.0;
                } else {
                    // 0.0, or -0.0 if value is -0.0
                    return 0.0 * value;
                }
            } else if (exponent < 52) {
                // A bit faster than working on bits.
                final long matIntPart = (long)value;
                final double matIntToValue = value-(double)matIntPart;
                if (matIntToValue >= 0.0) {
                    return (double)matIntPart;
                } else {
                    return (double)(matIntPart - 1);
                }
            } else {
                // +-Infinity, NaN, or a mathematical integer.
                return value;
            }
        }
    }

    /**
     * @param value A float value.
     * @return Ceiling of value.
     */
    public static float ceil(float value) {
        return -floor(-value);
    }

    /**
     * @param value A double value.
     * @return Ceiling of value.
     */
    public static double ceil(double value) {
        if (USE_JDK_MATH) {
            return StrictMath.ceil(value);
        }
        return -floor(-value);
    }

    /**
     * Might have different semantics than StrictMath.round(float),
     * see bugs 6430675 and 8010430.
     *
     * @param value A double value.
     * @return Value rounded to nearest int, choosing superior int in case two
     *         are equally close (i.e. rounding-up).
     */
    public static int round(float value) {
        /*
         * Not delegating to JDK, because we want delegation to provide
         * at least as good results, and some supported JDK versions
         * have bugged round() methods.
         */
        // Algorithm by Dmitry Nadezhin (but replaced an if by a multiply)
        // (http://mail.openjdk.java.net/pipermail/core-libs-dev/2013-August/020247.html).
        final int bits = Float.floatToRawIntBits(value);
        final int biasedExp = ((bits>>23)&0xFF);
        // Shift to get rid of bits past comma except first one: will need to
        // 1-shift to the right to end up with correct magnitude.
        final int shift = (23 - 1 + MAX_FLOAT_EXPONENT) - biasedExp;
        if ((shift & -32) == 0) {
            int bitsSignum = (((bits >> 31) << 1) + 1);
            // shift in [0,31], so unbiased exp in [-9,22].
            int extendedMantissa = (0x00800000 | (bits & 0x007FFFFF)) * bitsSignum;
            // If value is positive and first bit past comma is 0, rounding
            // to lower integer, else to upper one, which is what "+1" and
            // then ">>1" do.
            return ((extendedMantissa >> shift) + 1) >> 1;
        } else {
            // +-Infinity, NaN, or a mathematical integer, or tiny.
            if (false && ANTI_SLOW_CASTS) { // not worth it
                if (Math.abs(value) >= -(float)Integer.MIN_VALUE) {
                    // +-Infinity or a mathematical integer (mostly) out of int range.
                    return (value < 0.0) ? Integer.MIN_VALUE : Integer.MAX_VALUE;
                }
                // NaN or a mathematical integer (mostly) in int range.
            }
            return (int)value;
        }
    }

    /**
     * Might have different semantics than StrictMath.round(double),
     * see bugs 6430675 and 8010430.
     *
     * @param value A double value.
     * @return Value rounded to nearest long, choosing superior long in case two
     *         are equally close (i.e. rounding-up).
     */
    public static long round(double value) {
        /*
         * Not delegating to JDK, because we want delegation to provide
         * at least as good results, and some supported JDK versions
         * have bugged round() methods.
         */
        final long bits = Double.doubleToRawLongBits(value);
        final int biasedExp = (((int)(bits>>52))&0x7FF);
        // Shift to get rid of bits past comma except first one: will need to
        // 1-shift to the right to end up with correct magnitude.
        final int shift = (52 - 1 + MAX_DOUBLE_EXPONENT) - biasedExp;
        if ((shift & -64) == 0) {
            long bitsSignum = (((bits >> 63) << 1) + 1);
            // shift in [0,63], so unbiased exp in [-12,51].
            long extendedMantissa = (0x0010000000000000L | (bits & 0x000FFFFFFFFFFFFFL)) * bitsSignum;
            // If value is positive and first bit past comma is 0, rounding
            // to lower integer, else to upper one, which is what "+1" and
            // then ">>1" do.
            return ((extendedMantissa >> shift) + 1L) >> 1;
        } else {
            // +-Infinity, NaN, or a mathematical integer, or tiny.
            if (ANTI_SLOW_CASTS) {
                if (Math.abs(value) >= -(double)Long.MIN_VALUE) {
                    // +-Infinity or a mathematical integer (mostly) out of long range.
                    return (value < 0.0) ? Long.MIN_VALUE : Long.MAX_VALUE;
                }
                // NaN or a mathematical integer (mostly) in long range.
            }
            return (long)value;
        }
    }

    /**
     * @param value A float value.
     * @return Value rounded to nearest int, choosing even int in case two
     *         are equally close.
     */
    public static int roundEven(float value) {
        final int sign = signFromBit(value);
        value = Math.abs(value);
        if (ANTI_SLOW_CASTS) {
            if (value < TWO_POW_23_F) {
                // Getting rid of post-comma bits.
                value = ((value + TWO_POW_23_F) - TWO_POW_23_F);
                return sign * (int)value;
            } else if (value < (float)Integer.MAX_VALUE) { // "<=" doesn't work, because of float precision
                // value is in [-Integer.MAX_VALUE,Integer.MAX_VALUE]
                return sign * (int)value;
            }
        } else {
            if (value < TWO_POW_23_F) {
                // Getting rid of post-comma bits.
                value = ((value + TWO_POW_23_F) - TWO_POW_23_F);
            }
        }
        return (int)(sign * value);
    }

    /**
     * @param value A double value.
     * @return Value rounded to nearest long, choosing even long in case two
     *         are equally close.
     */
    public static long roundEven(double value) {
        final int sign = (int)signFromBit(value);
        value = Math.abs(value);
        if (value < TWO_POW_52) {
            // Getting rid of post-comma bits.
            value = ((value + TWO_POW_52) - TWO_POW_52);
        }
        if (ANTI_SLOW_CASTS) {
            if (value <= (double)Integer.MAX_VALUE) {
                // value is in [-Integer.MAX_VALUE,Integer.MAX_VALUE]
                return sign * (int)value;
            }
        }
        return (long)(sign * value);
    }

    /**
     * @param value A float value.
     * @return The float mathematical integer closest to the specified value,
     *         choosing even one if two are equally close, or respectively
     *         NaN, +-Infinity or +-0.0f if the value is any of these.
     */
    public static float rint(float value) {
        final int sign = signFromBit(value);
        value = Math.abs(value);
        if (value < TWO_POW_23_F) {
            // Getting rid of post-comma bits.
            value = ((TWO_POW_23_F + value ) - TWO_POW_23_F);
        }
        // Restoring original sign.
        return sign * value;
    }

    /**
     * @param value A double value.
     * @return The double mathematical integer closest to the specified value,
     *         choosing even one if two are equally close, or respectively
     *         NaN, +-Infinity or +-0.0 if the value is any of these.
     */
    public static double rint(double value) {
        if (USE_JDK_MATH) {
            return StrictMath.rint(value);
        }
        final int sign = (int)signFromBit(value);
        value = Math.abs(value);
        if (value < TWO_POW_52) {
            // Getting rid of post-comma bits.
            value = ((TWO_POW_52 + value ) - TWO_POW_52);
        }
        // Restoring original sign.
        return sign * value;
    }

    /*
     * close int values
     *
     * Never delegating to JDK for these methods, for we should always
     * be faster and exact, and JDK doesn't exactly have such methods.
     */

    /**
     * @param value A double value.
     * @return Floor of value as int, or closest int if floor is out
     *         of int range, or 0 if value is NaN.
     */
    public static int floorToInt(double value) {
        int valueInt = (int) value;
        if (value < 0.0) {
            if (value == (double) valueInt) {
                return valueInt;
            } else {
                if (valueInt == Integer.MIN_VALUE) {
                    return valueInt;
                } else {
                    return valueInt - 1;
                }
            }
        } else { // >= 0 or NaN.
            return valueInt;
        }
    }

    /**
     * @param value A double value.
     * @return Ceiling of value as int, or closest int if ceiling is out
     *         of int range, or 0 if value is NaN.
     */
    public static int ceilToInt(double value) {
        int valueInt = (int) value;
        if (value > 0.0) {
            if (value == (double) valueInt) {
                return valueInt;
            } else {
                if (valueInt == Integer.MAX_VALUE) {
                    return valueInt;
                } else {
                    return valueInt + 1;
                }
            }
        } else { // <= 0 or NaN.
            return valueInt;
        }
    }

    /**
     * @param value A double value.
     * @return Value rounded to nearest int, choosing superior int in case two
     *         are equally close (i.e. rounding-up).
     */
    public static int roundToInt(double value) {
        /*
         * We don't gain much by reimplementing rounding, except for
         * pathologically large values, which should not be a common case
         * when dealing with ints, so we just use round(double).
         */
        return NumbersUtils.toInt(round(value));
    }

    /**
     * @param value A double value.
     * @return Value rounded to nearest int, choosing even int in case two
     *         are equally close.
     */
    public static int roundEvenToInt(double value) {
        final int sign = (int)signFromBit(value);
        value = Math.abs(value);
        /*
         * Applying the post-comma bits removal logic even if value is out
         * of int range, to avoid a test, for it doesn't mess up the result,
         * and we want to optimize for the case of values in int range.
         */
        value = ((value + TWO_POW_52) - TWO_POW_52);
        return (int)(sign * value);
    }

    /*
     * ranges
     */

    /**
     * @param min A float value.
     * @param max A float value.
     * @param value A float value.
     * @return min if value < min, max if value > max, value otherwise.
     */
    public static float toRange(float min, float max, float value) {
        return NumbersUtils.toRange(min, max, value);
    }

    /**
     * @param min A double value.
     * @param max A double value.
     * @param value A double value.
     * @return min if value < min, max if value > max, value otherwise.
     */
    public static double toRange(double min, double max, double value) {
        return NumbersUtils.toRange(min, max, value);
    }

    /*
     * binary operators (/,%)
     */

    /**
     * Returns dividend - divisor * n, where n is the mathematical integer
     * closest to dividend/divisor.
     * If dividend/divisor is equally close to surrounding integers,
     * we choose n to be the integer of smallest magnitude, which makes
     * this treatment differ from StrictMath.IEEEremainder(double,double),
     * where n is chosen to be the even integer.
     * Note that the choice of n is not done considering the double
     * approximation of dividend/divisor, because it could cause
     * result to be outside [-|divisor|/2,|divisor|/2] range.
     * The practical effect is that if multiple results would be possible,
     * we always choose the result that is the closest to (and has the same
     * sign as) the dividend.
     * Ex. :
     * - for (-3.0,2.0), this method returns -1.0,
     *   whereas StrictMath.IEEEremainder returns 1.0.
     * - for (-5.0,2.0), both this method and StrictMath.IEEEremainder return -1.0.
     *
     * If the remainder is zero, its sign is the same as the sign of the first argument.
     * If either argument is NaN, or the first argument is infinite,
     * or the second argument is positive zero or negative zero,
     * then the result is NaN.
     * If the first argument is finite and the second argument is
     * infinite, then the result is the same as the first argument.
     *
     * NB:
     * - Modulo operator (%) returns a value in ]-|divisor|,|divisor|[,
     *   which sign is the same as dividend.
     * - As for modulo operator, the sign of the divisor has no effect on the result.
     * - On some architecture, % operator has been observed to return NaN
     *   for some subnormal values of divisor, when dividend exponent is 1023,
     *   which impacts the correctness of this method.
     *
     * @param dividend Dividend.
     * @param divisor Divisor.
     * @return Remainder of dividend/divisor, i.e. a value in [-|divisor|/2,|divisor|/2].
     */
    public static double remainder(double dividend, double divisor) {
        if (Double.isInfinite(divisor)) {
            if (Double.isInfinite(dividend)) {
                return Double.NaN;
            } else {
                return dividend;
            }
        }
        double value = dividend % divisor;
        if (Math.abs(value+value) > Math.abs(divisor)) {
            return value + ((value > 0.0) ? -Math.abs(divisor) : Math.abs(divisor));
        } else {
            return value;
        }
    }

    /**
     * @param angle Angle in radians.
     * @return The same angle, in radians, but in [-PI,PI].
     */
    public static double normalizeMinusPiPi(double angle) {
        // Not modifying values in output range.
        if ((angle >= -Math.PI) && (angle <= Math.PI)) {
            return angle;
        }
        return remainderTwoPi(angle);
    }

    /**
     * Not accurate for large values.
     *
     * @param angle Angle in radians.
     * @return The same angle, in radians, but in [-PI,PI].
     */
    public static double normalizeMinusPiPiFast(double angle) {
        // Not modifying values in output range.
        if ((angle >= -Math.PI) && (angle <= Math.PI)) {
            return angle;
        }
        return remainderTwoPiFast(angle);
    }

    /**
     * @param angle Angle in radians.
     * @return The same angle, in radians, but in [0,2*PI].
     */
    public static double normalizeZeroTwoPi(double angle) {
        // Not modifying values in output range.
        if ((angle >= 0.0) && (angle <= 2*Math.PI)) {
            return angle;
        }
        angle = remainderTwoPi(angle);
        if (angle < 0.0) {
            // LO then HI is theoretically better (when starting near 0).
            return (angle + TWOPI_LO) + TWOPI_HI;
        } else {
            return angle;
        }
    }

    /**
     * Not accurate for large values.
     *
     * @param angle Angle in radians.
     * @return The same angle, in radians, but in [0,2*PI].
     */
    public static double normalizeZeroTwoPiFast(double angle) {
        // Not modifying values in output range.
        if ((angle >= 0.0) && (angle <= 2*Math.PI)) {
            return angle;
        }
        angle = remainderTwoPiFast(angle);
        if (angle < 0.0) {
            // LO then HI is theoretically better (when starting near 0).
            return (angle + TWOPI_LO) + TWOPI_HI;
        } else {
            return angle;
        }
    }

    /**
     * @param angle Angle in radians.
     * @return Angle value modulo PI, in radians, in [-PI/2,PI/2].
     */
    public static double normalizeMinusHalfPiHalfPi(double angle) {
        // Not modifying values in output range.
        if ((angle >= -Math.PI/2) && (angle <= Math.PI/2)) {
            return angle;
        }
        return remainderPi(angle);
    }

    /**
     * Not accurate for large values.
     *
     * @param angle Angle in radians.
     * @return Angle value modulo PI, in radians, in [-PI/2,PI/2].
     */
    public static double normalizeMinusHalfPiHalfPiFast(double angle) {
        // Not modifying values in output range.
        if ((angle >= -Math.PI/2) && (angle <= Math.PI/2)) {
            return angle;
        }
        return remainderPiFast(angle);
    }

    /*
     * floating points utils
     */

    /**
     * @param value A float value.
     * @return true if the specified value is NaN or +-Infinity, false otherwise.
     */
    public static boolean isNaNOrInfinite(float value) {
        return NumbersUtils.isNaNOrInfinite(value);
    }

    /**
     * @param value A double value.
     * @return true if the specified value is NaN or +-Infinity, false otherwise.
     */
    public static boolean isNaNOrInfinite(double value) {
        return NumbersUtils.isNaNOrInfinite(value);
    }

    /**
     * @param value A float value.
     * @return Value unbiased exponent.
     */
    public static int getExponent(float value) {
        return ((Float.floatToRawIntBits(value)>>23)&0xFF)-MAX_FLOAT_EXPONENT;
    }

    /**
     * @param value A double value.
     * @return Value unbiased exponent.
     */
    public static int getExponent(double value) {
        return (((int)(Double.doubleToRawLongBits(value)>>52))&0x7FF)-MAX_DOUBLE_EXPONENT;
    }

    /**
     * @param value A float value.
     * @return -1.0f if the specified value is < 0, 1.0f if it is > 0,
     *         and the value itself if it is NaN or +-0.0f.
     */
    public static float signum(float value) {
        if (USE_JDK_MATH) {
            return StrictMath.signum(value);
        }
        if ((value == 0.0f) || (value != value)) {
            return value;
        }
        return (float)signFromBit(value);
    }

    /**
     * @param value A double value.
     * @return -1.0 if the specified value is < 0, 1.0 if it is > 0,
     *         and the value itself if it is NaN or +-0.0.
     */
    public static double signum(double value) {
        if (USE_JDK_MATH) {
            return StrictMath.signum(value);
        }
        if ((value == 0.0) || (value != value)) {
            return value;
        }
        if (ANTI_SLOW_CASTS) {
            return (double)(int)signFromBit(value);
        } else {
            return (double)signFromBit(value);
        }
    }

    /**
     * @param value A float value.
     * @return -1 if sign bit is 1, 1 if sign bit is 0.
     */
    public static int signFromBit(float value) {
        return ((Float.floatToRawIntBits(value)>>30)|1);
    }

    /**
     * @param value A double value.
     * @return -1 if sign bit is 1, 1 if sign bit is 0.
     */
    public static long signFromBit(double value) {
        // Returning a long, to avoid useless cast into int.
        return ((Double.doubleToRawLongBits(value)>>62)|1);
    }

    /**
     * A sign of NaN is interpreted as positive.
     *
     * @param magnitude A float value.
     * @param sign A float value.
     * @return A value with the magnitude of the first argument, and the sign
     *         of the second argument.
     */
    public static float copySign(float magnitude, float sign) {
        return Float.intBitsToFloat(
                (Float.floatToRawIntBits((sign != sign) ? 1.0f : sign) & Integer.MIN_VALUE)
                | (Float.floatToRawIntBits(magnitude) & Integer.MAX_VALUE));
    }

    /**
     * A sign of NaN is interpreted as positive.
     *
     * @param magnitude A double value.
     * @param sign A double value.
     * @return A value with the magnitude of the first argument, and the sign
     *         of the second argument.
     */
    public static double copySign(double magnitude, double sign) {
        return Double.longBitsToDouble(
                (Double.doubleToRawLongBits((sign != sign) ? 1.0 : sign) & Long.MIN_VALUE)
                | (Double.doubleToRawLongBits(magnitude) & Long.MAX_VALUE));
    }

    /**
     * The ULP (Unit in the Last Place) is the distance to the next value larger
     * in magnitude.
     *
     * @param value A float value.
     * @return The size of an ulp of the specified value, or Float.MIN_VALUE
     *         if it is +-0.0f, or +Infinity if it is +-Infinity, or NaN
     *         if it is NaN.
     */
    public static float ulp(float value) {
        if (USE_JDK_MATH) {
            return StrictMath.ulp(value);
        }
        /*
         * Look-up table not really worth it in micro-benchmark,
         * so should be worse with cache-misses.
         */
        final int exponent = getExponent(value);
        if (exponent >= (MIN_FLOAT_NORMAL_EXPONENT+23)) {
            if (exponent == MAX_FLOAT_EXPONENT+1) {
                // NaN or +-Infinity
                return Math.abs(value);
            }
            // normal: returning 2^(exponent-23)
            return Float.intBitsToFloat((exponent+(MAX_FLOAT_EXPONENT-23))<<23);
        } else {
            if (exponent == MIN_FLOAT_NORMAL_EXPONENT-1) {
                // +-0.0f or subnormal
                return Float.MIN_VALUE;
            }
            // subnormal result
            return Float.intBitsToFloat(1<<(exponent-MIN_FLOAT_NORMAL_EXPONENT));
        }
    }

    /**
     * The ULP (Unit in the Last Place) is the distance to the next value larger
     * in magnitude.
     *
     * @param value A double value.
     * @return The size of an ulp of the specified value, or Double.MIN_VALUE
     *         if it is +-0.0, or +Infinity if it is +-Infinity, or NaN
     *         if it is NaN.
     */
    public static double ulp(double value) {
        if (USE_JDK_MATH) {
            return StrictMath.ulp(value);
        }
        /*
         * Look-up table not really worth it in micro-benchmark,
         * so should be worse with cache-misses.
         */
        final int exponent = getExponent(value);
        if (exponent >= (MIN_DOUBLE_NORMAL_EXPONENT+52)) {
            if (exponent == MAX_DOUBLE_EXPONENT+1) {
                // NaN or +-Infinity
                return Math.abs(value);
            }
            // normal: returning 2^(exponent-52)
            return Double.longBitsToDouble((exponent+(MAX_DOUBLE_EXPONENT-52L))<<52);
        } else {
            if (exponent == MIN_DOUBLE_NORMAL_EXPONENT-1) {
                // +-0.0f or subnormal
                return Double.MIN_VALUE;
            }
            // subnormal result
            return Double.longBitsToDouble(1L<<(exponent-MIN_DOUBLE_NORMAL_EXPONENT));
        }
    }

    /**
     * If both arguments are +-0.0(f), (float)direction is returned.
     *
     * If both arguments are +Infinity or -Infinity,
     * respectively +Infinity or -Infinity is returned.
     *
     * @param start A float value.
     * @param direction A double value.
     * @return The float adjacent to start towards direction, considering that
     *         +(-)Float.MIN_VALUE is adjacent to +(-)0.0f, and that
     *         +(-)Float.MAX_VALUE is adjacent to +(-)Infinity,
     *         or NaN if any argument is NaN.
     */
    public static float nextAfter(float start, double direction) {
        if (direction < start) {
            // Going towards -Infinity.
            if (start == 0.0f) {
                // +-0.0f
                return -Float.MIN_VALUE;
            }
            final int bits = Float.floatToRawIntBits(start);
            return Float.intBitsToFloat(bits + ((bits > 0) ? -1 : 1));
        } else if (direction > start) {
            // Going towards +Infinity.
            // +0.0f to get rid of eventual -0.0f
            final int bits = Float.floatToRawIntBits(start + 0.0f);
            return Float.intBitsToFloat(bits + (bits >= 0 ? 1 : -1));
        } else if (start == direction) {
            return (float)direction;
        } else {
            // Returning a NaN derived from the input NaN(s).
            return start + (float)direction;
        }
    }

    /**
     * If both arguments are +-0.0, direction is returned.
     *
     * If both arguments are +Infinity or -Infinity,
     * respectively +Infinity or -Infinity is returned.
     *
     * @param start A double value.
     * @param direction A double value.
     * @return The double adjacent to start towards direction, considering that
     *         +(-)Double.MIN_VALUE is adjacent to +(-)0.0, and that
     *         +(-)Double.MAX_VALUE is adjacent to +(-)Infinity,
     *         or NaN if any argument is NaN.
     */
    public static double nextAfter(double start, double direction) {
        if (direction < start) {
            // Going towards -Infinity.
            if (start == 0.0) {
                // +-0.0
                return -Double.MIN_VALUE;
            }
            final long bits = Double.doubleToRawLongBits(start);
            return Double.longBitsToDouble(bits + ((bits > 0) ? -1 : 1));
        } else if (direction > start) {
            // Going towards +Infinity.
            // +0.0 to get rid of eventual -0.0
            final long bits = Double.doubleToRawLongBits(start + 0.0f);
            return Double.longBitsToDouble(bits + (bits >= 0 ? 1 : -1));
        } else if (start == direction) {
            return direction;
        } else {
            // Returning a NaN derived from the input NaN(s).
            return start + direction;
        }
    }

    /**
     * Semantically equivalent to nextAfter(start,Double.NEGATIVE_INFINITY).
     */
    public static float nextDown(float start) {
        if (start > Float.NEGATIVE_INFINITY) {
            if (start == 0.0f) {
                // +-0.0f
                return -Float.MIN_VALUE;
            }
            final int bits = Float.floatToRawIntBits(start);
            return Float.intBitsToFloat(bits + ((bits > 0) ? -1 : 1));
        } else if (start == Float.NEGATIVE_INFINITY) {
            return Float.NEGATIVE_INFINITY;
        } else {
            // NaN
            return start;
        }
    }

    /**
     * Semantically equivalent to nextAfter(start,Double.NEGATIVE_INFINITY).
     */
    public static double nextDown(double start) {
        if (start > Double.NEGATIVE_INFINITY) {
            if (start == 0.0) {
                // +-0.0
                return -Double.MIN_VALUE;
            }
            final long bits = Double.doubleToRawLongBits(start);
            return Double.longBitsToDouble(bits + ((bits > 0) ? -1 : 1));
        } else if (start == Double.NEGATIVE_INFINITY) {
            return Double.NEGATIVE_INFINITY;
        } else {
            // NaN
            return start;
        }
    }

    /**
     * Semantically equivalent to nextAfter(start,Double.POSITIVE_INFINITY).
     */
    public static float nextUp(float start) {
        if (start < Float.POSITIVE_INFINITY) {
            // +0.0f to get rid of eventual -0.0f
            final int bits = Float.floatToRawIntBits(start + 0.0f);
            return Float.intBitsToFloat(bits + (bits >= 0 ? 1 : -1));
        } else if (start == Float.POSITIVE_INFINITY) {
            return Float.POSITIVE_INFINITY;
        } else {
            // NaN
            return start;
        }
    }

    /**
     * Semantically equivalent to nextAfter(start,Double.POSITIVE_INFINITY).
     */
    public static double nextUp(double start) {
        if (start < Double.POSITIVE_INFINITY) {
            // +0.0 to get rid of eventual -0.0
            final long bits = Double.doubleToRawLongBits(start + 0.0);
            return Double.longBitsToDouble(bits + (bits >= 0 ? 1 : -1));
        } else if (start == Double.POSITIVE_INFINITY) {
            return Double.POSITIVE_INFINITY;
        } else {
            // NaN
            return start;
        }
    }

    /**
     * Precision may be lost if the result is subnormal.
     *
     * @param value A float value.
     * @param scaleFactor An int value.
     * @return value * 2^scaleFactor, or a value equivalent to the specified
     *         one if it is NaN, +-Infinity or +-0.0f.
     */
    public static float scalb(float value, int scaleFactor) {
        // Large enough to imply overflow or underflow for
        // a finite non-zero value.
        final int MAX_SCALE = 2*MAX_FLOAT_EXPONENT+23+1;

        // Making sure scaling factor is in a reasonable range.
        scaleFactor = Math.max(Math.min(scaleFactor, MAX_SCALE), -MAX_SCALE);

        return (float)(((double)value) * twoPowNormal(scaleFactor));
    }

    /**
     * Precision may be lost if the result is subnormal.
     *
     * @param value A double value.
     * @param scaleFactor An int value.
     * @return value * 2^scaleFactor, or a value equivalent to the specified
     *         one if it is NaN, +-Infinity or +-0.0.
     */
    public static double scalb(double value, int scaleFactor) {
        if ((scaleFactor > -MAX_DOUBLE_EXPONENT) && (scaleFactor <= MAX_DOUBLE_EXPONENT)) {
            // Quick case (as done in apache FastMath).
            return value * twoPowNormal(scaleFactor);
        }

        // Large enough to imply overflow or underflow for
        // a finite non-zero value.
        final int MAX_SCALE = 2*MAX_DOUBLE_EXPONENT+52+1;

        // Making sure scaling factor is in a reasonable range.
        final int exponentAdjust;
        final int scaleIncrement;
        final double exponentDelta;
        if (scaleFactor < 0) {
            scaleFactor = Math.max(scaleFactor, -MAX_SCALE);
            scaleIncrement = -512;
            exponentDelta = TWO_POW_N512;
        } else {
            scaleFactor = Math.min(scaleFactor, MAX_SCALE);
            scaleIncrement = 512;
            exponentDelta = TWO_POW_512;
        }

        // Calculating (scaleFactor % +-512), 512 = 2^9, using
        // technique from "Hacker's Delight" section 10-2.
        final int t = ((scaleFactor >> (9-1)) >>> (32-9));
        exponentAdjust = ((scaleFactor + t) & (512-1)) - t;

        value *= twoPowNormal(exponentAdjust);
        scaleFactor -= exponentAdjust;

        while (scaleFactor != 0) {
            value *= exponentDelta;
            scaleFactor -= scaleIncrement;
        }

        return value;
    }

    /*
     * Non-redefined StrictMath public values and treatments.
     */

    public static float abs(float a) {
        return StrictMath.abs(a);
    }

    public static double abs(double a) {
        return StrictMath.abs(a);
    }

    public static float min(float a, float b) {
        return StrictMath.min(a,b);
    }

    public static double min(double a, double b) {
        return StrictMath.min(a,b);
    }

    public static float max(float a, float b) {
        return StrictMath.max(a,b);
    }

    public static double max(double a, double b) {
        return StrictMath.max(a,b);
    }

    public static double IEEEremainder(double f1, double f2) {
        return StrictMath.IEEEremainder(f1,f2);
    }

    public static double random() {
        return StrictMath.random();
    }

    //--------------------------------------------------------------------------
    //  PRIVATE METHODS
    //--------------------------------------------------------------------------

    /**
     * Non-instantiable.
     */
    private StrictFastMath() {
    }

    /*
     * Remainders (accurate).
     */

    /**
     * @param angle Angle in radians.
     * @return Remainder of (angle % (2*PI)), in [-PI,PI].
     */
    private static double remainderTwoPi(double angle) {
        if (USE_JDK_MATH) {
            return jdkRemainderTwoPi(angle);
        }
        boolean negateResult = false;
        if (angle < 0.0) {
            angle = -angle;
            negateResult = true;
        }
        if (angle <= (4*NORMALIZE_ANGLE_MAX_MEDIUM_DOUBLE_PIO2)) {
            double fn = (double)(int)(angle*TWOPI_INV+0.5);
            angle = (angle - fn*TWOPI_HI) - fn*TWOPI_LO;
            // Ensuring range.
            // HI/LO can help a bit, even though we are always far from 0.
            if (angle < -Math.PI) {
                angle = (angle + TWOPI_HI) + TWOPI_LO;
            } else if (angle > Math.PI) {
                angle = (angle - TWOPI_HI) - TWOPI_LO;
            }
            return negateResult ? -angle : angle;
        } else if (angle < Double.POSITIVE_INFINITY) {
            angle = heavyRemainderTwoPi(angle);
            return negateResult ? -angle : angle;
        } else { // angle is +Infinity or NaN
            return Double.NaN;
        }
    }

    /**
     * @param angle Angle in radians.
     * @return Remainder of (angle % PI), in [-PI/2,PI/2].
     */
    private static double remainderPi(double angle) {
        if (USE_JDK_MATH) {
            return jdkRemainderPi(angle);
        }
        boolean negateResult = false;
        if (angle < 0.0) {
            angle = -angle;
            negateResult = true;
        }
        if (angle <= (2*NORMALIZE_ANGLE_MAX_MEDIUM_DOUBLE_PIO2)) {
            double fn = (double)(int)(angle*PI_INV+0.5);
            angle = (angle - fn*PI_HI) - fn*PI_LO;
            // Ensuring range.
            // HI/LO can help a bit, even though we are always far from 0.
            if (angle < -Math.PI/2) {
                angle = (angle + PI_HI) + PI_LO;
            } else if (angle > Math.PI/2) {
                angle = (angle - PI_HI) - PI_LO;
            }
            return negateResult ? -angle : angle;
        } else if (angle < Double.POSITIVE_INFINITY) {
            angle = heavyRemainderPi(angle);
            return negateResult ? -angle : angle;
        } else { // angle is +Infinity or NaN
            return Double.NaN;
        }
    }

    /**
     * @param angle Angle in radians.
     * @return Bits of double corresponding to remainder of (angle % (PI/2)),
     *         in [-PI/4,PI/4], with quadrant encoded in exponent bits.
     */
    private static long remainderPiO2(double angle) {
        if (USE_JDK_MATH) {
            return jdkRemainderPiO2(angle, false);
        }
        boolean negateResult = false;
        if (angle < 0.0) {
            angle = -angle;
            negateResult = true;
        }
        if (angle <= NORMALIZE_ANGLE_MAX_MEDIUM_DOUBLE_PIO2) {
            int n = (int)(angle*PIO2_INV+0.5);
            double fn = (double)n;
            angle = (angle - fn*PIO2_HI) - fn*PIO2_LO;
            // Ensuring range.
            // HI/LO can help a bit, even though we are always far from 0.
            if (angle < -Math.PI/4) {
                angle = (angle + PIO2_HI) + PIO2_LO;
                n--;
            } else if (angle > Math.PI/4) {
                angle = (angle - PIO2_HI) - PIO2_LO;
                n++;
            }
            if (negateResult) {
                angle = -angle;
            }
            return encodeRemainderAndQuadrant(angle, n&3);
        } else if (angle < Double.POSITIVE_INFINITY) {
            return heavyRemainderPiO2(angle, negateResult);
        } else { // angle is +Infinity or NaN
            return encodeRemainderAndQuadrant(Double.NaN, 0);
        }
    }

    /*
     * Remainders (fast).
     */

    /**
     * Not accurate for large values.
     *
     * @param angle Angle in radians.
     * @return Remainder of (angle % (2*PI)), in [-PI,PI].
     */
    private static double remainderTwoPiFast(double angle) {
        if (USE_JDK_MATH) {
            return jdkRemainderTwoPi(angle);
        }
        boolean negateResult = false;
        if (angle < 0.0) {
            angle = -angle;
            negateResult = true;
        }
        // - We don't bother with values higher than (2*PI*(2^52)),
        //   since they are spaced by 2*PI or more from each other.
        // - For large values, we don't use % because it might be very slow,
        //   and we split computation in two, because cast from double to int
        //   with large numbers might be very slow also.
        if (angle <= TWO_POW_26*(2*Math.PI)) {
            // ok
        } else if (angle <= TWO_POW_52*(2*Math.PI)) {
            // Computing remainder of angle modulo TWO_POW_26*(2*PI).
            double fn = (double)(int)(angle*(TWOPI_INV/TWO_POW_26)+0.5);
            angle = (angle - fn*(TWOPI_HI*TWO_POW_26)) - fn*(TWOPI_LO*TWO_POW_26);
            // Here, angle is in [-TWO_POW_26*PI,TWO_POW_26*PI], or so.
            if (angle < 0.0) {
                angle = -angle;
                negateResult = !negateResult;
            }
        } else if (angle < Double.POSITIVE_INFINITY) {
            return 0.0;
        } else { // angle is +Infinity or NaN
            return Double.NaN;
        }

        // Computing remainder of angle modulo 2*PI.
        double fn = (double)(int)(angle*TWOPI_INV+0.5);
        angle = (angle - fn*TWOPI_HI) - fn*TWOPI_LO;

        // Ensuring range.
        // HI/LO can help a bit, even though we are always far from 0.
        if (angle < -Math.PI) {
            angle = (angle + TWOPI_HI) + TWOPI_LO;
        } else if (angle > Math.PI) {
            angle = (angle - TWOPI_HI) - TWOPI_LO;
        }
        return negateResult ? -angle : angle;
    }

    /**
     * Not accurate for large values.
     *
     * @param angle Angle in radians.
     * @return Remainder of (angle % PI), in [-PI/2,PI/2].
     */
    private static double remainderPiFast(double angle) {
        if (USE_JDK_MATH) {
            return jdkRemainderPi(angle);
        }
        boolean negateResult = false;
        if (angle < 0.0) {
            angle = -angle;
            negateResult = true;
        }
        // - We don't bother with values higher than (PI*(2^52)),
        //   since they are spaced by PI or more from each other.
        // - For large values, we don't use % because it might be very slow,
        //   and we split computation in two, because cast from double to int
        //   with large numbers might be very slow also.
        if (angle <= TWO_POW_26*Math.PI) {
            // ok
        } else if (angle <= TWO_POW_52*Math.PI) {
            // Computing remainder of angle modulo TWO_POW_26*PI.
            double fn = (double)(int)(angle*(PI_INV/TWO_POW_26)+0.5);
            angle = (angle - fn*(PI_HI*TWO_POW_26)) - fn*(PI_LO*TWO_POW_26);
            // Here, angle is in [-TWO_POW_26*PI/2,TWO_POW_26*PI/2], or so.
            if (angle < 0.0) {
                angle = -angle;
                negateResult = !negateResult;
            }
        } else if (angle < Double.POSITIVE_INFINITY) {
            return 0.0;
        } else { // angle is +Infinity or NaN
            return Double.NaN;
        }

        // Computing remainder of angle modulo PI.
        double fn = (double)(int)(angle*PI_INV+0.5);
        angle = (angle - fn*PI_HI) - fn*PI_LO;

        // Ensuring range.
        // HI/LO can help a bit, even though we are always far from 0.
        if (angle < -Math.PI/2) {
            angle = (angle + PI_HI) + PI_LO;
        } else if (angle > Math.PI/2) {
            angle = (angle - PI_HI) - PI_LO;
        }
        return negateResult ? -angle : angle;
    }
}
