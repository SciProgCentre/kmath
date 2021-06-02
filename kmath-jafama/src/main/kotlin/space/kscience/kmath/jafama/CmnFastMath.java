/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.jafama;

/**
 * Stuffs for FastMath and StrictFastMath.
 */
abstract class CmnFastMath {

    /*
     * For trigonometric functions, use of look-up tables and Taylor-Lagrange formula
     * with 4 derivatives (more take longer to compute and don't add much accuracy,
     * less require larger tables (which use more memory, take more time to initialize,
     * and are slower to access (at least on the machine they were developed on))).
     *
     * For angles reduction of cos/sin/tan functions:
     * - for small values, instead of reducing angles, and then computing the best index
     *   for look-up tables, we compute this index right away, and use it for reduction,
     * - for large values, treatments derived from fdlibm package are used, as done in
     *   java.lang.Math. They are faster but still "slow", so if you work with
     *   large numbers and need speed over accuracy for them, you might want to use
     *   normalizeXXXFast treatments before your function, or modify cos/sin/tan
     *   so that they call the fast normalization treatments instead of the accurate ones.
     *   NB: If an angle is huge (like PI*1e20), in double precision format its last digits
     *       are zeros, which most likely is not the case for the intended value, and doing
     *       an accurate reduction on a very inaccurate value is most likely pointless.
     *       But it gives some sort of coherence that could be needed in some cases.
     *
     * Multiplication on double appears to be about as fast (or not much slower) than call
     * to <double_array>[<index>], and regrouping some doubles in a private class, to use
     * index only once, does not seem to speed things up, so:
     * - for uniformly tabulated values, to retrieve the parameter corresponding to
     *   an index, we recompute it rather than using an array to store it,
     * - for cos/sin, we recompute derivatives divided by (multiplied by inverse of)
     *   factorial each time, rather than storing them in arrays.
     *
     * Lengths of look-up tables are usually of the form 2^n+1, for their values to be
     * of the form (<a_constant> * k/2^n, k in 0 .. 2^n), so that particular values
     * (PI/2, etc.) are "exactly" computed, as well as for other reasons.
     *
     * Tables are put in specific inner classes, to be lazily initialized.
     * Always doing strict tables initialization, even if StrictFastMath delegates
     * to StrictMath and doesn't use tables, which makes tables initialization a bit
     * slower but code simpler.
     * Using redefined pure Java treatments during tables initialization,
     * instead of Math or StrictMath ones (even asin(double)), can be very slow,
     * because class loading is likely not to be optimized.
     *
     * Most math treatments I could find on the web, including "fast" ones,
     * usually take care of special cases (NaN, etc.) at the beginning, and
     * then deal with the general case, which adds a useless overhead for the
     * general (and common) case. In this class, special cases are only dealt
     * with when needed, and if the general case does not already handle them.
     */

    /*
     * Regarding strictfp-ness:
     *
     * Switching from/to strictfp has some overhead, so we try to only
     * strictfp-ize when needed (or when clueless).
     * Compile-time constants are computed in a FP-strict way, so no need
     * to make this whole class strictfp.
     */

    //--------------------------------------------------------------------------
    // CONFIGURATION
    //--------------------------------------------------------------------------

    /*
     * FastMath
     */

    static final boolean FM_USE_JDK_MATH = getBooleanProperty("jafama.usejdk", false);

    /**
     * Used for both FastMath.log(double) and FastMath.log10(double).
     */
    static final boolean FM_USE_REDEFINED_LOG = getBooleanProperty("jafama.fastlog", false);

    static final boolean FM_USE_REDEFINED_SQRT = getBooleanProperty("jafama.fastsqrt", false);

    /**
     * Set it to true if FastMath.sqrt(double) is slow
     * (more tables, but less calls to FastMath.sqrt(double)).
     */
    static final boolean FM_USE_POWTABS_FOR_ASIN = false;

    /*
     * StrictFastMath
     */

    static final boolean SFM_USE_JDK_MATH = getBooleanProperty("jafama.strict.usejdk", false);

    /**
     * Used for both StrictFastMath.log(double) and StrictFastMath.log10(double).
     * True by default because the StrictMath implementations can be slow.
     */
    static final boolean SFM_USE_REDEFINED_LOG = getBooleanProperty("jafama.strict.fastlog", true);

    static final boolean SFM_USE_REDEFINED_SQRT = getBooleanProperty("jafama.strict.fastsqrt", false);

    /**
     * Set it to true if StrictFastMath.sqrt(double) is slow
     * (more tables, but less calls to StrictFastMath.sqrt(double)).
     */
    static final boolean SFM_USE_POWTABS_FOR_ASIN = false;

    /*
     * Common to FastMath and StrictFastMath.
     */

    /**
     * Using two pow tab can just make things barely faster,
     * and could relatively hurt in case of cache-misses,
     * especially for methods that otherwise wouldn't rely
     * on any tab, so we don't use it.
     */
    static final boolean USE_TWO_POW_TAB = false;

    /**
     * Because on some architectures, some casts can be slow,
     * especially for large values.
     * Might make things a bit slower for latest architectures,
     * but not as much as it makes them faster for older ones.
     */
    static final boolean ANTI_SLOW_CASTS = true;

    /**
     * If some methods get JIT-optimized, they might crash
     * if they contain "(var == xxx)" with var being NaN
     * (can happen with Java 6u29).
     *
     * The crash does not happen if we replace "==" with "<" or ">".
     *
     * Only the code that has been observed to trigger the bug
     * has been modified.
     */
    static final boolean ANTI_JIT_OPTIM_CRASH_ON_NAN = true;

    //--------------------------------------------------------------------------
    // GENERAL CONSTANTS
    //--------------------------------------------------------------------------

    /**
     * Closest double approximation of e.
     */
    public static final double E = Math.E;

    /**
     * Closest double approximation of pi, which is inferior to mathematical pi:
     * pi ~= 3.14159265358979323846...
     * PI ~= 3.141592653589793
     */
    public static final double PI = Math.PI;

    /**
     * High double approximation of pi, which is further from pi
     * than the low approximation PI:
     *     pi ~= 3.14159265358979323846...
     *     PI ~= 3.141592653589793
     * PI_SUP ~= 3.1415926535897936
     */
    public static final double PI_SUP = Double.longBitsToDouble(Double.doubleToRawLongBits(Math.PI)+1);

    static final double ONE_DIV_F2 = 1/2.0;
    static final double ONE_DIV_F3 = 1/6.0;
    static final double ONE_DIV_F4 = 1/24.0;

    static final float TWO_POW_23_F = (float)NumbersUtils.twoPow(23);

    static final double TWO_POW_24 = NumbersUtils.twoPow(24);
    private static final double TWO_POW_N24 = NumbersUtils.twoPow(-24);

    static final double TWO_POW_26 = NumbersUtils.twoPow(26);
    static final double TWO_POW_N26 = NumbersUtils.twoPow(-26);

    // First double value (from zero) such as (value+-1/value == value).
    static final double TWO_POW_27 = NumbersUtils.twoPow(27);
    static final double TWO_POW_N27 = NumbersUtils.twoPow(-27);

    static final double TWO_POW_N28 = NumbersUtils.twoPow(-28);

    static final double TWO_POW_52 = NumbersUtils.twoPow(52);

    static final double TWO_POW_N55 = NumbersUtils.twoPow(-55);

    static final double TWO_POW_66 = NumbersUtils.twoPow(66);

    static final double TWO_POW_512 = NumbersUtils.twoPow(512);
    static final double TWO_POW_N512 = NumbersUtils.twoPow(-512);

    /**
     * Double.MIN_NORMAL since Java 6.
     */
    static final double DOUBLE_MIN_NORMAL = Double.longBitsToDouble(0x0010000000000000L); // 2.2250738585072014E-308

    // Not storing float/double mantissa size in constants,
    // for 23 and 52 are shorter to read and more
    // bitwise-explicit than some constant's name.

    static final int MIN_DOUBLE_EXPONENT = -1074;
    static final int MIN_DOUBLE_NORMAL_EXPONENT = -1022;
    static final int MAX_DOUBLE_EXPONENT = 1023;

    static final int MIN_FLOAT_NORMAL_EXPONENT = -126;
    static final int MAX_FLOAT_EXPONENT = 127;

    private static final double SQRT_2 = StrictMath.sqrt(2.0);

    static final double LOG_2 = StrictMath.log(2.0);
    static final double LOG_TWO_POW_27 = StrictMath.log(TWO_POW_27);
    static final double LOG_DOUBLE_MAX_VALUE = StrictMath.log(Double.MAX_VALUE);

    static final double INV_LOG_10 = 1.0/StrictMath.log(10.0);

    static final double DOUBLE_BEFORE_60 = Double.longBitsToDouble(Double.doubleToRawLongBits(60.0)-1);

    //--------------------------------------------------------------------------
    // CONSTANTS FOR NORMALIZATIONS
    //--------------------------------------------------------------------------

    /**
     * Table of constants for 1/(PI/2), 282 Hex digits (enough for normalizing doubles).
     * 1/(PI/2) approximation = sum of TWO_OVER_PI_TAB[i]*2^(-24*(i+1)).
     *
     * double and not int, to avoid int-to-double cast during computations.
     */
    private static final double TWO_OVER_PI_TAB[] = {
        0xA2F983, 0x6E4E44, 0x1529FC, 0x2757D1, 0xF534DD, 0xC0DB62,
        0x95993C, 0x439041, 0xFE5163, 0xABDEBB, 0xC561B7, 0x246E3A,
        0x424DD2, 0xe00649, 0x2EEA09, 0xD1921C, 0xFE1DEB, 0x1CB129,
        0xA73EE8, 0x8235F5, 0x2EBB44, 0x84E99C, 0x7026B4, 0x5F7E41,
        0x3991d6, 0x398353, 0x39F49C, 0x845F8B, 0xBDF928, 0x3B1FF8,
        0x97FFDE, 0x05980F, 0xEF2F11, 0x8B5A0A, 0x6D1F6D, 0x367ECF,
        0x27CB09, 0xB74F46, 0x3F669E, 0x5FEA2D, 0x7527BA, 0xC7EBE5,
        0xF17B3D, 0x0739F7, 0x8A5292, 0xEA6BFB, 0x5FB11F, 0x8D5D08,
        0x560330, 0x46FC7B, 0x6BABF0, 0xCFBC20, 0x9AF436, 0x1DA9E3,
        0x91615E, 0xE61B08, 0x659985, 0x5F14A0, 0x68408D, 0xFFD880,
        0x4D7327, 0x310606, 0x1556CA, 0x73A8C9, 0x60E27B, 0xC08C6B};

    /*
     * Constants for PI/2. Only the 23 most significant bits of each mantissa are used.
     * 2*PI approximation = sum of TWOPI_TAB<i>.
     */
    private static final double PIO2_TAB0 = Double.longBitsToDouble(0x3FF921FB40000000L);
    private static final double PIO2_TAB1 = Double.longBitsToDouble(0x3E74442D00000000L);
    private static final double PIO2_TAB2 = Double.longBitsToDouble(0x3CF8469880000000L);
    private static final double PIO2_TAB3 = Double.longBitsToDouble(0x3B78CC5160000000L);
    private static final double PIO2_TAB4 = Double.longBitsToDouble(0x39F01B8380000000L);
    private static final double PIO2_TAB5 = Double.longBitsToDouble(0x387A252040000000L);

    static final double PIO2_INV = Double.longBitsToDouble(0x3FE45F306DC9C883L); // 6.36619772367581382433e-01 53 bits of 2/pi
    static final double PIO2_HI = Double.longBitsToDouble(0x3FF921FB54400000L); // 1.57079632673412561417e+00 first 33 bits of pi/2
    static final double PIO2_LO = Double.longBitsToDouble(0x3DD0B4611A626331L); // 6.07710050650619224932e-11 pi/2 - PIO2_HI
    static final double PI_INV = PIO2_INV/2;
    static final double PI_HI = 2*PIO2_HI;
    static final double PI_LO = 2*PIO2_LO;
    static final double TWOPI_INV = PIO2_INV/4;
    static final double TWOPI_HI = 4*PIO2_HI;
    static final double TWOPI_LO = 4*PIO2_LO;

    /**
     * Bit = 0 where quadrant is encoded in remainder bits.
     */
    private static final long QUADRANT_BITS_0_MASK = 0xCFFFFFFFFFFFFFFFL;

    /**
     * Remainder bits where quadrant is encoded, 0 elsewhere.
     */
    private static final long QUADRANT_PLACE_BITS = 0x3000000000000000L;

    /**
     * fdlibm uses 2^19*PI/2 here.
     * With 2^18*PI/2 we would be more accurate, for example when normalizing
     * 822245.903631403, which is close to 2^19*PI/2, but we are still in
     * our accuracy tolerance with fdlibm's value (but not 2^20*PI/2) so we
     * stick to it, to help being faster than (Strict)Math for values in
     * [2^18*PI/2,2^19*PI/2].
     *
     * For tests, can use a smaller value, for heavy remainder
     * not to only be used with huge values.
     */
    static final double NORMALIZE_ANGLE_MAX_MEDIUM_DOUBLE_PIO2 = StrictMath.pow(2.0,19.0)*(Math.PI/2);

    /**
     * 2*Math.PI, normalized into [-PI,PI], as returned by
     * StrictMath.asin(StrictMath.sin(2*Math.PI))
     * (asin behaves as identity for this).
     *
     * NB: NumbersUtils.minus2PI(2*Math.PI) returns  -2.449293598153844E-16,
     * which is different due to not using an accurate enough definition of PI.
     */
    static final double TWO_MATH_PI_IN_MINUS_PI_PI = -2.4492935982947064E-16;

    //--------------------------------------------------------------------------
    // CONSTANTS AND TABLES FOR SIN AND COS
    //--------------------------------------------------------------------------

    static final int SIN_COS_TABS_SIZE = (1<<getTabSizePower(11)) + 1;
    static final double SIN_COS_DELTA_HI = TWOPI_HI/(SIN_COS_TABS_SIZE-1);
    static final double SIN_COS_DELTA_LO = TWOPI_LO/(SIN_COS_TABS_SIZE-1);
    static final double SIN_COS_INDEXER = 1/(SIN_COS_DELTA_HI+SIN_COS_DELTA_LO);

    static final class MyTSinCos {
        static final double[] sinTab = new double[SIN_COS_TABS_SIZE];
        static final double[] cosTab = new double[SIN_COS_TABS_SIZE];
        static {
            init();
        }
        private static strictfp void init() {
            final int SIN_COS_PI_INDEX = (SIN_COS_TABS_SIZE-1)/2;
            final int SIN_COS_PI_MUL_2_INDEX = 2*SIN_COS_PI_INDEX;
            final int SIN_COS_PI_MUL_0_5_INDEX = SIN_COS_PI_INDEX/2;
            final int SIN_COS_PI_MUL_1_5_INDEX = 3*SIN_COS_PI_INDEX/2;
            for (int i=0;i<SIN_COS_TABS_SIZE;i++) {
                // angle: in [0,2*PI] (doesn't seem to help to have it in [-PI,PI]).
                double angle = i * SIN_COS_DELTA_HI + i * SIN_COS_DELTA_LO;
                double sinAngle = StrictMath.sin(angle);
                double cosAngle = StrictMath.cos(angle);
                // For indexes corresponding to zero cosine or sine, we make sure
                // the value is zero and not an epsilon, since each value
                // corresponds to sin-or-cos(i*PI/n), where PI is a more accurate
                // definition of PI than Math.PI.
                // This allows for a much better accuracy for results close to zero.
                if (i == SIN_COS_PI_INDEX) {
                    sinAngle = 0.0;
                } else if (i == SIN_COS_PI_MUL_2_INDEX) {
                    sinAngle = 0.0;
                } else if (i == SIN_COS_PI_MUL_0_5_INDEX) {
                    cosAngle = 0.0;
                } else if (i == SIN_COS_PI_MUL_1_5_INDEX) {
                    cosAngle = 0.0;
                }
                sinTab[i] = sinAngle;
                cosTab[i] = cosAngle;
            }
        }
    }

    /**
     * Max abs value for index-based reduction, above which we use regular angle normalization.
     * This value must be < (Integer.MAX_VALUE / SIN_COS_INDEXER), to stay in range of int type.
     * If too high, error gets larger because index-based reduction doesn't use an accurate
     * enough definition of PI.
     * If too low, and if we would be using remainder into [-PI,PI] instead of into [-PI/4,PI/4],
     * error would get larger as well, because remainder would just provide a double, while
     * index-based reduction is more accurate, using delta from index values and HI/LO values.
     */
    static final double SIN_COS_MAX_VALUE_FOR_INT_MODULO = ((Integer.MAX_VALUE>>9) / SIN_COS_INDEXER) * 0.99;

    //--------------------------------------------------------------------------
    // CONSTANTS AND TABLES FOR TAN
    //--------------------------------------------------------------------------

    // We use the following formula:
    // 1) tan(-x) = -tan(x)
    // 2) tan(x) = 1/tan(PI/2-x)
    // ---> we only have to compute tan(x) on [0,A] with PI/4<=A<PI/2.

    /**
     * We use indexing past look-up tables, so that indexing information
     * allows for fast recomputation of angle in [0,PI/2] range.
     */
    static final int TAN_VIRTUAL_TABS_SIZE = (1<<getTabSizePower(12)) + 1;

    /**
     * Must be >= 45deg, and supposed to be >= 51.4deg, as fdlibm code is not
     * supposed to work with values inferior to that (51.4deg is about
     * (PI/2-Double.longBitsToDouble(0x3FE5942800000000L))).
     */
    static final double TAN_MAX_VALUE_FOR_TABS = StrictMath.toRadians(77.0);

    static final int TAN_TABS_SIZE = (int)((TAN_MAX_VALUE_FOR_TABS/(Math.PI/2)) * (TAN_VIRTUAL_TABS_SIZE-1)) + 1;
    static final double TAN_DELTA_HI = PIO2_HI/(TAN_VIRTUAL_TABS_SIZE-1);
    static final double TAN_DELTA_LO = PIO2_LO/(TAN_VIRTUAL_TABS_SIZE-1);
    static final double TAN_INDEXER = 1/(TAN_DELTA_HI+TAN_DELTA_LO);

    static final class MyTTan {
        static final double[] tanTab = new double[TAN_TABS_SIZE];
        static final double[] tanDer1DivF1Tab = new double[TAN_TABS_SIZE];
        static final double[] tanDer2DivF2Tab = new double[TAN_TABS_SIZE];
        static final double[] tanDer3DivF3Tab = new double[TAN_TABS_SIZE];
        static final double[] tanDer4DivF4Tab = new double[TAN_TABS_SIZE];
        static {
            init();
        }
        private static strictfp void init() {
            for (int i=0;i<TAN_TABS_SIZE;i++) {
                // angle: in [0,TAN_MAX_VALUE_FOR_TABS].
                double angle = i * TAN_DELTA_HI + i * TAN_DELTA_LO;
                double sinAngle = StrictMath.sin(angle);
                double cosAngle = StrictMath.cos(angle);
                double cosAngleInv = 1/cosAngle;
                double cosAngleInv2 = cosAngleInv*cosAngleInv;
                double cosAngleInv3 = cosAngleInv2*cosAngleInv;
                double cosAngleInv4 = cosAngleInv2*cosAngleInv2;
                double cosAngleInv5 = cosAngleInv3*cosAngleInv2;
                tanTab[i] = sinAngle * cosAngleInv;
                tanDer1DivF1Tab[i] = cosAngleInv2;
                tanDer2DivF2Tab[i] = ((2*sinAngle)*cosAngleInv3) * ONE_DIV_F2;
                tanDer3DivF3Tab[i] = ((2*(1+2*sinAngle*sinAngle))*cosAngleInv4) * ONE_DIV_F3;
                tanDer4DivF4Tab[i] = ((8*sinAngle*(2+sinAngle*sinAngle))*cosAngleInv5) * ONE_DIV_F4;
            }
        }
    }

    /**
     * Max abs value for fast modulo, above which we use regular angle normalization.
     * This value must be < (Integer.MAX_VALUE / TAN_INDEXER), to stay in range of int type.
     * If too high, error gets larger because index-based reduction doesn't use an accurate
     * enough definition of PI.
     * If too low, error gets larger as well, because we use remainder into [-PI/2,PI/2],
     * just provides a double, while index-based reduction is more accurate, using delta
     * from index values and HI/LO values.
     */
    static final double TAN_MAX_VALUE_FOR_INT_MODULO = (((Integer.MAX_VALUE>>9) / TAN_INDEXER) * 0.99);

    //--------------------------------------------------------------------------
    // CONSTANTS AND TABLES FOR ACOS, ASIN
    //--------------------------------------------------------------------------

    // We use the following formula:
    // 1) acos(x) = PI/2 - asin(x)
    // 2) asin(-x) = -asin(x)
    // ---> we only have to compute asin(x) on [0,1].
    // For values not close to +-1, we use look-up tables;
    // for values near +-1, we use code derived from fdlibm.

    /**
     * Supposed to be >= sin(77.2deg), as fdlibm code is supposed to work with values > 0.975,
     * but seems to work well enough as long as value >= sin(25deg).
     */
    static final double ASIN_MAX_VALUE_FOR_TABS = StrictMath.sin(StrictMath.toRadians(73.0));

    static final int ASIN_TABS_SIZE = (1<<getTabSizePower(13)) + 1;
    static final double ASIN_DELTA = ASIN_MAX_VALUE_FOR_TABS/(ASIN_TABS_SIZE - 1);
    static final double ASIN_INDEXER = 1/ASIN_DELTA;

    static final class MyTAsin {
        static final double[] asinTab = new double[ASIN_TABS_SIZE];
        static final double[] asinDer1DivF1Tab = new double[ASIN_TABS_SIZE];
        static final double[] asinDer2DivF2Tab = new double[ASIN_TABS_SIZE];
        static final double[] asinDer3DivF3Tab = new double[ASIN_TABS_SIZE];
        static final double[] asinDer4DivF4Tab = new double[ASIN_TABS_SIZE];
        static {
            init();
        }
        private static strictfp void init() {
            for (int i=0;i<ASIN_TABS_SIZE;i++) {
                // x: in [0,ASIN_MAX_VALUE_FOR_TABS].
                double x = i * ASIN_DELTA;
                double oneMinusXSqInv = 1/(1-x*x);
                double oneMinusXSqInv0_5 = StrictMath.sqrt(oneMinusXSqInv);
                double oneMinusXSqInv1_5 = oneMinusXSqInv0_5*oneMinusXSqInv;
                double oneMinusXSqInv2_5 = oneMinusXSqInv1_5*oneMinusXSqInv;
                double oneMinusXSqInv3_5 = oneMinusXSqInv2_5*oneMinusXSqInv;
                asinTab[i] = StrictMath.asin(x);
                asinDer1DivF1Tab[i] = oneMinusXSqInv0_5;
                asinDer2DivF2Tab[i] = (x*oneMinusXSqInv1_5) * ONE_DIV_F2;
                asinDer3DivF3Tab[i] = ((1+2*x*x)*oneMinusXSqInv2_5) * ONE_DIV_F3;
                asinDer4DivF4Tab[i] = ((5+2*x*(2+x*(5-2*x)))*oneMinusXSqInv3_5) * ONE_DIV_F4;
            }
        }
    }

    static final double ASIN_MAX_VALUE_FOR_POWTABS = StrictMath.sin(StrictMath.toRadians(88.6));
    static final int ASIN_POWTABS_POWER = 84;

    static final double ASIN_POWTABS_ONE_DIV_MAX_VALUE = 1/ASIN_MAX_VALUE_FOR_POWTABS;
    static final int ASIN_POWTABS_SIZE = (FM_USE_POWTABS_FOR_ASIN || SFM_USE_POWTABS_FOR_ASIN) ? (1<<getTabSizePower(12)) + 1 : 0;
    static final int ASIN_POWTABS_SIZE_MINUS_ONE = ASIN_POWTABS_SIZE - 1;

    static final class MyTAsinPow {
        static final double[] asinParamPowTab = new double[ASIN_POWTABS_SIZE];
        static final double[] asinPowTab = new double[ASIN_POWTABS_SIZE];
        static final double[] asinDer1DivF1PowTab = new double[ASIN_POWTABS_SIZE];
        static final double[] asinDer2DivF2PowTab = new double[ASIN_POWTABS_SIZE];
        static final double[] asinDer3DivF3PowTab = new double[ASIN_POWTABS_SIZE];
        static final double[] asinDer4DivF4PowTab = new double[ASIN_POWTABS_SIZE];
        static {
            init();
        }
        private static strictfp void init() {
            if (FM_USE_POWTABS_FOR_ASIN || SFM_USE_POWTABS_FOR_ASIN) {
                for (int i=0;i<ASIN_POWTABS_SIZE;i++) {
                    // x: in [0,ASIN_MAX_VALUE_FOR_POWTABS].
                    double x = StrictMath.pow(i*(1.0/ASIN_POWTABS_SIZE_MINUS_ONE), 1.0/ASIN_POWTABS_POWER) * ASIN_MAX_VALUE_FOR_POWTABS;
                    double oneMinusXSqInv = 1/(1-x*x);
                    double oneMinusXSqInv0_5 = StrictMath.sqrt(oneMinusXSqInv);
                    double oneMinusXSqInv1_5 = oneMinusXSqInv0_5*oneMinusXSqInv;
                    double oneMinusXSqInv2_5 = oneMinusXSqInv1_5*oneMinusXSqInv;
                    double oneMinusXSqInv3_5 = oneMinusXSqInv2_5*oneMinusXSqInv;
                    asinParamPowTab[i] = x;
                    asinPowTab[i] = StrictMath.asin(x);
                    asinDer1DivF1PowTab[i] = oneMinusXSqInv0_5;
                    asinDer2DivF2PowTab[i] = (x*oneMinusXSqInv1_5) * ONE_DIV_F2;
                    asinDer3DivF3PowTab[i] = ((1+2*x*x)*oneMinusXSqInv2_5) * ONE_DIV_F3;
                    asinDer4DivF4PowTab[i] = ((5+2*x*(2+x*(5-2*x)))*oneMinusXSqInv3_5) * ONE_DIV_F4;
                }
            }
        }
    }

    static final double ASIN_PIO2_HI = Double.longBitsToDouble(0x3FF921FB54442D18L); // 1.57079632679489655800e+00
    static final double ASIN_PIO2_LO = Double.longBitsToDouble(0x3C91A62633145C07L); // 6.12323399573676603587e-17
    static final double ASIN_PS0 = Double.longBitsToDouble(0x3fc5555555555555L); //  1.66666666666666657415e-01
    static final double ASIN_PS1 = Double.longBitsToDouble(0xbfd4d61203eb6f7dL); // -3.25565818622400915405e-01
    static final double ASIN_PS2 = Double.longBitsToDouble(0x3fc9c1550e884455L); //  2.01212532134862925881e-01
    static final double ASIN_PS3 = Double.longBitsToDouble(0xbfa48228b5688f3bL); // -4.00555345006794114027e-02
    static final double ASIN_PS4 = Double.longBitsToDouble(0x3f49efe07501b288L); //  7.91534994289814532176e-04
    static final double ASIN_PS5 = Double.longBitsToDouble(0x3f023de10dfdf709L); //  3.47933107596021167570e-05
    static final double ASIN_QS1 = Double.longBitsToDouble(0xc0033a271c8a2d4bL); // -2.40339491173441421878e+00
    static final double ASIN_QS2 = Double.longBitsToDouble(0x40002ae59c598ac8L); //  2.02094576023350569471e+00
    static final double ASIN_QS3 = Double.longBitsToDouble(0xbfe6066c1b8d0159L); // -6.88283971605453293030e-01
    static final double ASIN_QS4 = Double.longBitsToDouble(0x3fb3b8c5b12e9282L); //  7.70381505559019352791e-02

    //--------------------------------------------------------------------------
    // CONSTANTS AND TABLES FOR ATAN
    //--------------------------------------------------------------------------

    // We use the formula atan(-x) = -atan(x)
    // ---> we only have to compute atan(x) on [0,+Infinity[.
    // For values corresponding to angles not close to +-PI/2, we use look-up tables;
    // for values corresponding to angles near +-PI/2, we use code derived from fdlibm.

    /**
     * Supposed to be >= tan(67.7deg), as fdlibm code is supposed to work with values > 2.4375.
     */
    static final double ATAN_MAX_VALUE_FOR_TABS = StrictMath.tan(StrictMath.toRadians(74.0));

    static final int ATAN_TABS_SIZE = (1<<getTabSizePower(12)) + 1;
    static final double ATAN_DELTA = ATAN_MAX_VALUE_FOR_TABS/(ATAN_TABS_SIZE - 1);
    static final double ATAN_INDEXER = 1/ATAN_DELTA;

    static final class MyTAtan {
        static final double[] atanTab = new double[ATAN_TABS_SIZE];
        static final double[] atanDer1DivF1Tab = new double[ATAN_TABS_SIZE];
        static final double[] atanDer2DivF2Tab = new double[ATAN_TABS_SIZE];
        static final double[] atanDer3DivF3Tab = new double[ATAN_TABS_SIZE];
        static final double[] atanDer4DivF4Tab = new double[ATAN_TABS_SIZE];
        static {
            init();
        }
        private static strictfp void init() {
            for (int i=0;i<ATAN_TABS_SIZE;i++) {
                // x: in [0,ATAN_MAX_VALUE_FOR_TABS].
                double x = i * ATAN_DELTA;
                double onePlusXSqInv = 1/(1+x*x);
                double onePlusXSqInv2 = onePlusXSqInv*onePlusXSqInv;
                double onePlusXSqInv3 = onePlusXSqInv2*onePlusXSqInv;
                double onePlusXSqInv4 = onePlusXSqInv2*onePlusXSqInv2;
                atanTab[i] = StrictMath.atan(x);
                atanDer1DivF1Tab[i] = onePlusXSqInv;
                atanDer2DivF2Tab[i] = (-2*x*onePlusXSqInv2) * ONE_DIV_F2;
                atanDer3DivF3Tab[i] = ((-2+6*x*x)*onePlusXSqInv3) * ONE_DIV_F3;
                atanDer4DivF4Tab[i] = ((24*x*(1-x*x))*onePlusXSqInv4) * ONE_DIV_F4;
            }
        }
    }

    static final double ATAN_HI3 = Double.longBitsToDouble(0x3ff921fb54442d18L); // 1.57079632679489655800e+00 atan(inf)hi
    static final double ATAN_LO3 = Double.longBitsToDouble(0x3c91a62633145c07L); // 6.12323399573676603587e-17 atan(inf)lo
    static final double ATAN_AT0 = Double.longBitsToDouble(0x3fd555555555550dL); //  3.33333333333329318027e-01
    static final double ATAN_AT1 = Double.longBitsToDouble(0xbfc999999998ebc4L); // -1.99999999998764832476e-01
    static final double ATAN_AT2 = Double.longBitsToDouble(0x3fc24924920083ffL); //  1.42857142725034663711e-01
    static final double ATAN_AT3 = Double.longBitsToDouble(0xbfbc71c6fe231671L); // -1.11111104054623557880e-01
    static final double ATAN_AT4 = Double.longBitsToDouble(0x3fb745cdc54c206eL); //  9.09088713343650656196e-02
    static final double ATAN_AT5 = Double.longBitsToDouble(0xbfb3b0f2af749a6dL); // -7.69187620504482999495e-02
    static final double ATAN_AT6 = Double.longBitsToDouble(0x3fb10d66a0d03d51L); //  6.66107313738753120669e-02
    static final double ATAN_AT7 = Double.longBitsToDouble(0xbfadde2d52defd9aL); // -5.83357013379057348645e-02
    static final double ATAN_AT8 = Double.longBitsToDouble(0x3fa97b4b24760debL); //  4.97687799461593236017e-02
    static final double ATAN_AT9 = Double.longBitsToDouble(0xbfa2b4442c6a6c2fL); // -3.65315727442169155270e-02
    static final double ATAN_AT10 = Double.longBitsToDouble(0x3f90ad3ae322da11L); // 1.62858201153657823623e-02

    //--------------------------------------------------------------------------
    // CONSTANTS AND TABLES FOR TANH
    //--------------------------------------------------------------------------

    /**
     * Constant found experimentally:
     * StrictMath.tanh(TANH_1_THRESHOLD) = 1,
     * StrictMath.tanh(nextDown(TANH_1_THRESHOLD)) = FastMath.tanh(nextDown(TANH_1_THRESHOLD)) < 1.
     */
    static final double TANH_1_THRESHOLD = 19.061547465398498;

    //--------------------------------------------------------------------------
    // CONSTANTS AND TABLES FOR ASINH AND ACOSH
    //--------------------------------------------------------------------------

    static final double ASINH_LOG1P_THRESHOLD = 0.04;

    /**
     * sqrt(x*x+-1) should yield higher threshold, but it's enough due to
     * subsequent log.
     */
    static final double ASINH_ACOSH_SQRT_ELISION_THRESHOLD = (1<<24);

    //--------------------------------------------------------------------------
    // CONSTANTS AND TABLES FOR EXP AND EXPM1
    //--------------------------------------------------------------------------

    static final double EXP_OVERFLOW_LIMIT = Double.longBitsToDouble(0x40862E42FEFA39EFL); // 7.09782712893383973096e+02
    static final double EXP_UNDERFLOW_LIMIT = Double.longBitsToDouble(0xC0874910D52D3051L); // -7.45133219101941108420e+02
    static final int EXP_LO_DISTANCE_TO_ZERO_POT = 0;
    static final int EXP_LO_DISTANCE_TO_ZERO = (1<<EXP_LO_DISTANCE_TO_ZERO_POT);
    static final int EXP_LO_TAB_SIZE_POT = getTabSizePower(11);
    static final int EXP_LO_TAB_SIZE = (1<<EXP_LO_TAB_SIZE_POT)+1;
    static final int EXP_LO_TAB_MID_INDEX = ((EXP_LO_TAB_SIZE-1)/2);
    static final int EXP_LO_INDEXING = EXP_LO_TAB_MID_INDEX/EXP_LO_DISTANCE_TO_ZERO;
    static final int EXP_LO_INDEXING_DIV_SHIFT = EXP_LO_TAB_SIZE_POT-1-EXP_LO_DISTANCE_TO_ZERO_POT;

    static final class MyTExp {
        static final double[] expHiTab = new double[1+(int)EXP_OVERFLOW_LIMIT-(int)EXP_UNDERFLOW_LIMIT];
        static final double[] expLoPosTab = new double[EXP_LO_TAB_SIZE];
        static final double[] expLoNegTab = new double[EXP_LO_TAB_SIZE];
        static {
            init();
        }
        private static strictfp void init() {
            for (int i=(int)EXP_UNDERFLOW_LIMIT;i<=(int)EXP_OVERFLOW_LIMIT;i++) {
                expHiTab[i-(int)EXP_UNDERFLOW_LIMIT] = StrictMath.exp(i);
            }
            for (int i=0;i<EXP_LO_TAB_SIZE;i++) {
                // x: in [-EXPM1_DISTANCE_TO_ZERO,EXPM1_DISTANCE_TO_ZERO].
                double x = -EXP_LO_DISTANCE_TO_ZERO + i/(double)EXP_LO_INDEXING;
                // exp(x)
                expLoPosTab[i] = StrictMath.exp(x);
                // 1-exp(-x), accurately computed
                expLoNegTab[i] = -StrictMath.expm1(-x);
            }
        }
    }

    //--------------------------------------------------------------------------
    // CONSTANTS AND TABLES FOR LOG AND LOG1P
    //--------------------------------------------------------------------------

    static final int LOG_BITS = getTabSizePower(12);
    static final int LOG_TAB_SIZE = (1<<LOG_BITS);

    static final class MyTLog {
        static final double[] logXLogTab = new double[LOG_TAB_SIZE];
        static final double[] logXTab = new double[LOG_TAB_SIZE];
        static final double[] logXInvTab = new double[LOG_TAB_SIZE];
        static {
            init();
        }
        private static strictfp void init() {
            for (int i=0;i<LOG_TAB_SIZE;i++) {
                // Exact to use inverse of tab size, since it is a power of two.
                double x = 1+i*(1.0/LOG_TAB_SIZE);
                logXLogTab[i] = StrictMath.log(x);
                logXTab[i] = x;
                logXInvTab[i] = 1/x;
            }
        }
    }

    //--------------------------------------------------------------------------
    // TABLE FOR POWERS OF TWO
    //--------------------------------------------------------------------------

    static final int TWO_POW_TAB_SIZE = USE_TWO_POW_TAB ? (MAX_DOUBLE_EXPONENT-MIN_DOUBLE_EXPONENT)+1 : 0;

    static final class MyTTwoPow {
        static final double[] twoPowTab = new double[TWO_POW_TAB_SIZE];
        static {
            init();
        }
        private static strictfp void init() {
            if (USE_TWO_POW_TAB) {
                for (int i=MIN_DOUBLE_EXPONENT;i<=MAX_DOUBLE_EXPONENT;i++) {
                    twoPowTab[i-MIN_DOUBLE_EXPONENT] = NumbersUtils.twoPow(i);
                }
            }
        }
    }

    //--------------------------------------------------------------------------
    // CONSTANTS AND TABLES FOR SQRT
    //--------------------------------------------------------------------------

    static final int SQRT_LO_BITS = getTabSizePower(12);
    static final int SQRT_LO_TAB_SIZE = (1<<SQRT_LO_BITS);

    static final class MyTSqrt {
        static final double[] sqrtXSqrtHiTab = new double[MAX_DOUBLE_EXPONENT-MIN_DOUBLE_EXPONENT+1];
        static final double[] sqrtXSqrtLoTab = new double[SQRT_LO_TAB_SIZE];
        static final double[] sqrtSlopeHiTab = new double[MAX_DOUBLE_EXPONENT-MIN_DOUBLE_EXPONENT+1];
        static final double[] sqrtSlopeLoTab = new double[SQRT_LO_TAB_SIZE];
        static {
            init();
        }
        private static strictfp void init() {
            for (int i=MIN_DOUBLE_EXPONENT;i<=MAX_DOUBLE_EXPONENT;i++) {
                double twoPowExpDiv2 = StrictMath.pow(2.0,i*0.5);
                sqrtXSqrtHiTab[i-MIN_DOUBLE_EXPONENT] = twoPowExpDiv2 * 0.5; // Half sqrt, to avoid overflows.
                sqrtSlopeHiTab[i-MIN_DOUBLE_EXPONENT] = 1/twoPowExpDiv2;
            }
            sqrtXSqrtLoTab[0] = 1.0;
            sqrtSlopeLoTab[0] = 1.0;
            final long SQRT_LO_MASK = (0x3FF0000000000000L | (0x000FFFFFFFFFFFFFL>>SQRT_LO_BITS));
            for (int i=1;i<SQRT_LO_TAB_SIZE;i++) {
                long xBits = SQRT_LO_MASK | (((long)(i-1))<<(52-SQRT_LO_BITS));
                double sqrtX = StrictMath.sqrt(Double.longBitsToDouble(xBits));
                sqrtXSqrtLoTab[i] = sqrtX;
                sqrtSlopeLoTab[i] = 1/sqrtX;
            }
        }
    }

    //--------------------------------------------------------------------------
    // CONSTANTS AND TABLES FOR CBRT
    //--------------------------------------------------------------------------

    static final int CBRT_LO_BITS = getTabSizePower(12);
    static final int CBRT_LO_TAB_SIZE = (1<<CBRT_LO_BITS);

    // For CBRT_LO_BITS = 12:
    // cbrtXCbrtLoTab[0] = 1.0.
    // cbrtXCbrtLoTab[1] = cbrt(1. 000000000000 1111111111111111111111111111111111111111b)
    // cbrtXCbrtLoTab[2] = cbrt(1. 000000000001 1111111111111111111111111111111111111111b)
    // cbrtXCbrtLoTab[3] = cbrt(1. 000000000010 1111111111111111111111111111111111111111b)
    // cbrtXCbrtLoTab[4] = cbrt(1. 000000000011 1111111111111111111111111111111111111111b)
    // etc.
    static final class MyTCbrt {
        static final double[] cbrtXCbrtHiTab = new double[MAX_DOUBLE_EXPONENT-MIN_DOUBLE_EXPONENT+1];
        static final double[] cbrtXCbrtLoTab = new double[CBRT_LO_TAB_SIZE];
        static final double[] cbrtSlopeHiTab = new double[MAX_DOUBLE_EXPONENT-MIN_DOUBLE_EXPONENT+1];
        static final double[] cbrtSlopeLoTab = new double[CBRT_LO_TAB_SIZE];
        static {
            init();
        }
        private static strictfp void init() {
            for (int i=MIN_DOUBLE_EXPONENT;i<=MAX_DOUBLE_EXPONENT;i++) {
                double twoPowExpDiv3 = StrictMath.pow(2.0,i*(1.0/3));
                cbrtXCbrtHiTab[i-MIN_DOUBLE_EXPONENT] = twoPowExpDiv3 * 0.5; // Half cbrt, to avoid overflows.
                cbrtSlopeHiTab[i-MIN_DOUBLE_EXPONENT] = (4.0/3)/(twoPowExpDiv3*twoPowExpDiv3);
            }
            cbrtXCbrtLoTab[0] = 1.0;
            cbrtSlopeLoTab[0] = 1.0;
            final long CBRT_LO_MASK = (0x3FF0000000000000L | (0x000FFFFFFFFFFFFFL>>CBRT_LO_BITS));
            for (int i=1;i<CBRT_LO_TAB_SIZE;i++) {
                long xBits = CBRT_LO_MASK | (((long)(i-1))<<(52-CBRT_LO_BITS));
                double cbrtX = StrictMath.cbrt(Double.longBitsToDouble(xBits));
                cbrtXCbrtLoTab[i] = cbrtX;
                cbrtSlopeLoTab[i] = 1/(cbrtX*cbrtX);
            }
        }
    }

    //--------------------------------------------------------------------------
    // CONSTANTS FOR HYPOT
    //--------------------------------------------------------------------------

    /**
     * For using sqrt, to avoid overflow/underflow, we want values magnitude in
     * [1/sqrt(Double.MAX_VALUE/n),sqrt(Double.MAX_VALUE/n)],
     * n being the number of arguments.
     *
     * sqrt(Double.MAX_VALUE/2) = 9.480751908109176E153
     * and
     * sqrt(Double.MAX_VALUE/3) = 7.741001517595157E153
     * so
     * 2^511 = 6.7039039649712985E153
     * works for both.
     */
    static final double HYPOT_MAX_MAG = NumbersUtils.twoPow(511);

    /**
     * Large enough to get a value's magnitude back into [2^-511,2^511]
     * from Double.MIN_VALUE or Double.MAX_VALUE, and small enough
     * not to get it across that range (considering a 2*53 tolerance
     * due to only checking magnitude of min/max value, and scaling
     * all values together).
     */
    static final double HYPOT_FACTOR = NumbersUtils.twoPow(750);

    //--------------------------------------------------------------------------
    // PUBLIC METHODS
    //--------------------------------------------------------------------------

    /**
     * Ensures that all look-up tables are initialized - otherwise they are
     * initialized lazily.
     */
    public static void initTables() {
        /*
         * Taking care not to call init methods here, which would
         * recompute tables each time (even though the computations
         * should be identical, since done with strictfp and StrictMath).
         */
        int antiOptim = 0;
        antiOptim += MyTSinCos.sinTab.length;
        antiOptim += MyTTan.tanTab.length;
        antiOptim += MyTAsin.asinTab.length;
        antiOptim += MyTAsinPow.asinPowTab.length;
        antiOptim += MyTAtan.atanTab.length;
        antiOptim += MyTExp.expHiTab.length;
        antiOptim += MyTLog.logXTab.length;
        antiOptim += MyTTwoPow.twoPowTab.length;
        antiOptim += MyTSqrt.sqrtXSqrtHiTab.length;
        antiOptim += MyTCbrt.cbrtXCbrtHiTab.length;
        if (StrictMath.cos((double)antiOptim) == 0.0) {
            // Can't happen, cos is never +-0.0.
            throw new AssertionError();
        }
    }

    /*
     * logarithms
     */

    /**
     * @param value An integer value in [1,Integer.MAX_VALUE].
     * @return The integer part of the logarithm, in base 2, of the specified value,
     *         i.e. a result in [0,30]
     * @throws IllegalArgumentException if the specified value is <= 0.
     */
    public static int log2(int value) {
        return NumbersUtils.log2(value);
    }

    /**
     * @param value An integer value in [1,Long.MAX_VALUE].
     * @return The integer part of the logarithm, in base 2, of the specified value,
     *         i.e. a result in [0,62]
     * @throws IllegalArgumentException if the specified value is <= 0.
     */
    public static int log2(long value) {
        return NumbersUtils.log2(value);
    }

    /*
     * powers
     */

    /**
     * Returns the exact result, provided it's in double range,
     * i.e. if power is in [-1074,1023].
     *
     * @param power An int power.
     * @return 2^power as a double, or +-Infinity in case of overflow.
     */
    public static double twoPow(int power) {
        /*
         * OK to have this method factored here even though it returns
         * a floating point value, because it only does integer operations
         * and only takes integer arguments, so should behave the same
         * even if inlined into FP-wide context.
         */
        if (USE_TWO_POW_TAB) {
            if (power >= MIN_DOUBLE_EXPONENT) {
                if (power <= MAX_DOUBLE_EXPONENT) { // Normal or subnormal.
                    return MyTTwoPow.twoPowTab[power-MIN_DOUBLE_EXPONENT];
                } else { // Overflow.
                    return Double.POSITIVE_INFINITY;
                }
            } else { // Underflow.
                return 0.0;
            }
        } else {
            return NumbersUtils.twoPow(power);
        }
    }

    /**
     * @param value An int value.
     * @return value*value.
     */
    public static int pow2(int value) {
        return value*value;
    }

    /**
     * @param value A long value.
     * @return value*value.
     */
    public static long pow2(long value) {
        return value*value;
    }

    /**
     * @param value An int value.
     * @return value*value*value.
     */
    public static int pow3(int value) {
        return value*value*value;
    }

    /**
     * @param value A long value.
     * @return value*value*value.
     */
    public static long pow3(long value) {
        return value*value*value;
    }

    /*
     * absolute values
     */

    /**
     * @param value An int value.
     * @return The absolute value, except if value is Integer.MIN_VALUE, for which it returns Integer.MIN_VALUE.
     */
    public static int abs(int value) {
        if (FM_USE_JDK_MATH || SFM_USE_JDK_MATH) {
            return Math.abs(value);
        }
        return NumbersUtils.abs(value);
    }

    /**
     * @param value A long value.
     * @return The absolute value, except if value is Long.MIN_VALUE, for which it returns Long.MIN_VALUE.
     */
    public static long abs(long value) {
        if (FM_USE_JDK_MATH || SFM_USE_JDK_MATH) {
            return Math.abs(value);
        }
        return NumbersUtils.abs(value);
    }

    /*
     * close values
     */

    /**
     * @param value A long value.
     * @return The specified value as int.
     * @throws ArithmeticException if the specified value is not in [Integer.MIN_VALUE,Integer.MAX_VALUE] range.
     */
    public static int toIntExact(long value) {
        return NumbersUtils.asInt(value);
    }

    /**
     * @param value A long value.
     * @return The closest int value in [Integer.MIN_VALUE,Integer.MAX_VALUE] range.
     */
    public static int toInt(long value) {
        return NumbersUtils.toInt(value);
    }

    /*
     * ranges
     */

    /**
     * @param min An int value.
     * @param max An int value.
     * @param value An int value.
     * @return minValue if value < minValue, maxValue if value > maxValue, value otherwise.
     */
    public static int toRange(int min, int max, int value) {
        return NumbersUtils.toRange(min, max, value);
    }

    /**
     * @param min A long value.
     * @param max A long value.
     * @param value A long value.
     * @return min if value < min, max if value > max, value otherwise.
     */
    public static long toRange(long min, long max, long value) {
        return NumbersUtils.toRange(min, max, value);
    }

    /*
     * unary operators (increment,decrement,negate)
     */

    /**
     * @param value An int value.
     * @return The argument incremented by one.
     * @throws ArithmeticException if the mathematical result
     *         is not in int range.
     */
    public static int incrementExact(int value) {
        if (value == Integer.MAX_VALUE) {
            throw new ArithmeticException("integer overflow");
        }
        return value + 1;
    }

    /**
     * @param value A long value.
     * @return The argument incremented by one.
     * @throws ArithmeticException if the mathematical result
     *         is not in long range.
     */
    public static long incrementExact(long value) {
        if (value == Long.MAX_VALUE) {
            throw new ArithmeticException("long overflow");
        }
        return value + 1L;
    }

    /**
     * @param value An int value.
     * @return The argument incremented by one, or the argument
     *         if the mathematical result is not in int range.
     */
    public static int incrementBounded(int value) {
        if (value == Integer.MAX_VALUE) {
            return value;
        }
        return value + 1;
    }

    /**
     * @param value A long value.
     * @return The argument incremented by one, or the argument
     *         if the mathematical result is not in long range.
     */
    public static long incrementBounded(long value) {
        if (value == Long.MAX_VALUE) {
            return value;
        }
        return value + 1L;
    }

    /**
     * @param value An int value.
     * @return The argument decremented by one.
     * @throws ArithmeticException if the mathematical result
     *         is not in int range.
     */
    public static int decrementExact(int value) {
        if (value == Integer.MIN_VALUE) {
            throw new ArithmeticException("integer overflow");
        }
        return value - 1;
    }

    /**
     * @param value A long value.
     * @return The argument decremented by one.
     * @throws ArithmeticException if the mathematical result
     *         is not in long range.
     */
    public static long decrementExact(long value) {
        if (value == Long.MIN_VALUE) {
            throw new ArithmeticException("long overflow");
        }
        return value - 1L;
    }

    /**
     * @param value An int value.
     * @return The argument decremented by one, or the argument
     *         if the mathematical result is not in int range.
     */
    public static int decrementBounded(int value) {
        if (value == Integer.MIN_VALUE) {
            return value;
        }
        return value - 1;
    }

    /**
     * @param value A long value.
     * @return The argument decremented by one, or the argument
     *         if the mathematical result is not in long range.
     */
    public static long decrementBounded(long value) {
        if (value == Long.MIN_VALUE) {
            return value;
        }
        return value - 1L;
    }

    /**
     * @param value An int value.
     * @return The argument negated.
     * @throws ArithmeticException if the mathematical result
     *         is not in int range.
     */
    public static int negateExact(int value) {
        if (value == Integer.MIN_VALUE) {
            throw new ArithmeticException("integer overflow");
        }
        return -value;
    }

    /**
     * @param value A long value.
     * @return The argument negated.
     * @throws ArithmeticException if the mathematical result
     *         is not in long range.
     */
    public static long negateExact(long value) {
        if (value == Long.MIN_VALUE) {
            throw new ArithmeticException("long overflow");
        }
        return -value;
    }

    /**
     * @param value An int value.
     * @return The argument negated, or Integer.MAX_VALUE
     *         if the argument is Integer.MIN_VALUE.
     */
    public static int negateBounded(int value) {
        if (value == Integer.MIN_VALUE) {
            return Integer.MAX_VALUE;
        }
        return -value;
    }

    /**
     * @param value A long value.
     * @return The argument negated, or Long.MAX_VALUE
     *         if the argument is Long.MIN_VALUE.
     */
    public static long negateBounded(long value) {
        if (value == Long.MIN_VALUE) {
            return Long.MAX_VALUE;
        }
        return -value;
    }

    /*
     * binary operators (+,-,*)
     */

    /**
     * @param a An int value.
     * @param b An int value.
     * @return The mathematical result of a+b.
     * @throws ArithmeticException if the mathematical result of a+b is not in [Integer.MIN_VALUE,Integer.MAX_VALUE] range.
     */
    public static int addExact(int a, int b) {
        return NumbersUtils.plusExact(a, b);
    }

    /**
     * @param a A long value.
     * @param b A long value.
     * @return The mathematical result of a+b.
     * @throws ArithmeticException if the mathematical result of a+b is not in [Long.MIN_VALUE,Long.MAX_VALUE] range.
     */
    public static long addExact(long a, long b) {
        return NumbersUtils.plusExact(a, b);
    }

    /**
     * @param a An int value.
     * @param b An int value.
     * @return The int value of [Integer.MIN_VALUE,Integer.MAX_VALUE] range which is the closest to mathematical result of a+b.
     */
    public static int addBounded(int a, int b) {
        return NumbersUtils.plusBounded(a, b);
    }

    /**
     * @param a A long value.
     * @param b A long value.
     * @return The long value of [Long.MIN_VALUE,Long.MAX_VALUE] range which is the closest to mathematical result of a+b.
     */
    public static long addBounded(long a, long b) {
        return NumbersUtils.plusBounded(a, b);
    }

    /**
     * @param a An int value.
     * @param b An int value.
     * @return The mathematical result of a-b.
     * @throws ArithmeticException if the mathematical result of a-b is not in [Integer.MIN_VALUE,Integer.MAX_VALUE] range.
     */
    public static int subtractExact(int a, int b) {
        return NumbersUtils.minusExact(a, b);
    }

    /**
     * @param a A long value.
     * @param b A long value.
     * @return The mathematical result of a-b.
     * @throws ArithmeticException if the mathematical result of a-b is not in [Long.MIN_VALUE,Long.MAX_VALUE] range.
     */
    public static long subtractExact(long a, long b) {
        return NumbersUtils.minusExact(a, b);
    }

    /**
     * @param a An int value.
     * @param b An int value.
     * @return The int value of [Integer.MIN_VALUE,Integer.MAX_VALUE] range which is the closest to mathematical result of a-b.
     */
    public static int subtractBounded(int a, int b) {
        return NumbersUtils.minusBounded(a, b);
    }

    /**
     * @param a A long value.
     * @param b A long value.
     * @return The long value of [Long.MIN_VALUE,Long.MAX_VALUE] range which is the closest to mathematical result of a-b.
     */
    public static long subtractBounded(long a, long b) {
        return NumbersUtils.minusBounded(a, b);
    }

    /**
     * @param a An int value.
     * @param b An int value.
     * @return The mathematical result of a*b.
     * @throws ArithmeticException if the mathematical result of a*b is not in [Integer.MIN_VALUE,Integer.MAX_VALUE] range.
     */
    public static int multiplyExact(int a, int b) {
        return NumbersUtils.timesExact(a, b);
    }

    /**
     * @param a A long value.
     * @param b An int value.
     * @return The mathematical result of a*b.
     * @throws ArithmeticException if the mathematical result of a*b is not in [Long.MIN_VALUE,Long.MAX_VALUE] range.
     */
    public static long multiplyExact(long a, int b) {
        return NumbersUtils.timesExact(a, (long) b);
    }

    /**
     * @param a A long value.
     * @param b A long value.
     * @return The mathematical result of a*b.
     * @throws ArithmeticException if the mathematical result of a*b is not in [Long.MIN_VALUE,Long.MAX_VALUE] range.
     */
    public static long multiplyExact(long a, long b) {
        return NumbersUtils.timesExact(a, b);
    }

    /**
     * @param a An int value.
     * @param b An int value.
     * @return The int value of [Integer.MIN_VALUE,Integer.MAX_VALUE] range which is the closest to mathematical result of a*b.
     */
    public static int multiplyBounded(int a, int b) {
        return NumbersUtils.timesBounded(a, b);
    }

    /**
     * @param a A long value.
     * @param b An int value.
     * @return The long value of [Long.MIN_VALUE,Long.MAX_VALUE] range which is the closest to mathematical result of a*b.
     */
    public static long multiplyBounded(long a, int b) {
        return NumbersUtils.timesBounded(a, (long) b);
    }

    /**
     * @param a A long value.
     * @param b A long value.
     * @return The long value of [Long.MIN_VALUE,Long.MAX_VALUE] range which is the closest to mathematical result of a*b.
     */
    public static long multiplyBounded(long a, long b) {
        return NumbersUtils.timesBounded(a, b);
    }

    /**
     * @param x An int value.
     * @param y An int value.
     * @return The mathematical product as a long.
     */
    public static long multiplyFull(int x, int y) {
        return ((long) x) * ((long) y);
    }

    /**
     * @param x A long value.
     * @param y A long value.
     * @return The most significant 64 bits of the 128-bit product of two 64-bit factors.
     */
    public static long multiplyHigh(long x, long y) {
        if ((x|y) < 0) {
            // Use technique from section 8-2 of Henry S. Warren, Jr.,
            // Hacker's Delight (2nd ed.) (Addison Wesley, 2013), 173-174.
            long x1 = (x >> 32);
            long y1 = (y >> 32);
            long x2 = (x & 0xFFFFFFFFL);
            long y2 = (y & 0xFFFFFFFFL);
            long z2 = x2 * y2;
            long t = x1 * y2 + (z2 >>> 32);
            long z1 = (t & 0xFFFFFFFFL) + x2 * y1;
            long z0 = (t >> 32);
            return x1 * y1 + z0 + (z1 >> 32);
        } else {
            // Use Karatsuba technique with two base 2^32 digits.
            long x1 = (x >>> 32);
            long y1 = (y >>> 32);
            long x2 = (x & 0xFFFFFFFFL);
            long y2 = (y & 0xFFFFFFFFL);
            long A = x1 * y1;
            long B = x2 * y2;
            long C = (x1 + x2) * (y1 + y2);
            long K = C - A - B;
            return (((B >>> 32) + K) >>> 32) + A;
        }
    }

    /*
     * binary operators (/,%)
     */

    /**
     * Returns the largest int <= dividend/divisor.
     *
     * Unlike "/" operator, which rounds towards 0, this division
     * rounds towards -Infinity (which give different result
     * when the exact result is negative).
     *
     * @param x The dividend.
     * @param y The divisor.
     * @return The largest int <= dividend/divisor, unless dividend is
     *         Integer.MIN_VALUE and divisor is -1, in which case
     *         Integer.MIN_VALUE is returned.
     * @throws ArithmeticException if the divisor is zero.
     */
    public static int floorDiv(int x, int y) {
        int r = x / y;
        // If the signs are different and modulo not zero, rounding down.
        if (((x ^ y) < 0) && ((r * y) != x)) {
            r--;
        }
        return r;
    }

    /**
     * Returns the largest long <= dividend/divisor.
     *
     * Unlike "/" operator, which rounds towards 0, this division
     * rounds towards -Infinity (which give different result
     * when the exact result is negative).
     *
     * @param x The dividend.
     * @param y The divisor.
     * @return The largest long <= dividend/divisor, unless dividend is
     *         Long.MIN_VALUE and divisor is -1, in which case
     *         Long.MIN_VALUE is returned.
     * @throws ArithmeticException if the divisor is zero.
     */
    public static long floorDiv(long x, int y) {
        return floorDiv(x, (long) y);
    }

    /**
     * Returns the largest long <= dividend/divisor.
     *
     * Unlike "/" operator, which rounds towards 0, this division
     * rounds towards -Infinity (which give different result
     * when the exact result is negative).
     *
     * @param x The dividend.
     * @param y The divisor.
     * @return The largest long <= dividend/divisor, unless dividend is
     *         Long.MIN_VALUE and divisor is -1, in which case
     *         Long.MIN_VALUE is returned.
     * @throws ArithmeticException if the divisor is zero.
     */
    public static long floorDiv(long x, long y) {
        long r = x / y;
        // If the signs are different and modulo not zero, rounding down.
        if (((x ^ y) < 0) && ((r * y) != x)) {
            r--;
        }
        return r;
    }

    /**
     * Returns the floor modulus, which is "x - floorDiv(x,y) * y",
     * has the same sign as y, and is in ]-abs(y),abs(y)[.
     *
     * The relationship between floorMod and floorDiv is the same
     * than between "%" and "/".
     *
     * @param x The dividend.
     * @param y The divisor.
     * @return The floor modulus, i.e. "x - (floorDiv(x, y) * y)".
     * @throws ArithmeticException if the divisor is zero.
     */
    public static int floorMod(int x, int y) {
        return x - floorDiv(x, y) * y;
    }

    /**
     * Returns the floor modulus, which is "x - floorDiv(x,y) * y",
     * has the same sign as y, and is in ]-abs(y),abs(y)[.
     *
     * The relationship between floorMod and floorDiv is the same
     * than between "%" and "/".
     *
     * @param x The dividend.
     * @param y The divisor.
     * @return The floor modulus, i.e. "x - (floorDiv(x, y) * y)".
     * @throws ArithmeticException if the divisor is zero.
     */
    public static int floorMod(long x, int y) {
        // No overflow so can cast.
        return (int) (x - floorDiv(x,y) * y);
    }

    /**
     * Returns the floor modulus, which is "x - floorDiv(x,y) * y",
     * has the same sign as y, and is in ]-abs(y),abs(y)[.
     *
     * The relationship between floorMod and floorDiv is the same
     * than between "%" and "/".
     *
     * @param x The dividend.
     * @param y The divisor.
     * @return The floor modulus, i.e. "x - (floorDiv(x, y) * y)".
     * @throws ArithmeticException if the divisor is zero.
     */
    public static long floorMod(long x, long y) {
        return x - floorDiv(x, y) * y;
    }

    /*
     * Non-redefined Math public values and treatments.
     */

    public static int min(int a, int b) {
        return Math.min(a,b);
    }

    public static long min(long a, long b) {
        return Math.min(a,b);
    }

    public static int max(int a, int b) {
        return Math.max(a,b);
    }

    public static long max(long a, long b) {
        return Math.max(a,b);
    }

    //--------------------------------------------------------------------------
    // PACKAGE-PRIVATE METHODS
    //--------------------------------------------------------------------------

    /**
     * @param power Must be in normal values range.
     */
    static double twoPowNormal(int power) {
        if (USE_TWO_POW_TAB) {
            return MyTTwoPow.twoPowTab[power-MIN_DOUBLE_EXPONENT];
        } else {
            return Double.longBitsToDouble(((long)(power+MAX_DOUBLE_EXPONENT))<<52);
        }
    }

    /**
     * @param power Must be in normal or subnormal values range.
     */
    static double twoPowNormalOrSubnormal(int power) {
        if (USE_TWO_POW_TAB) {
            return MyTTwoPow.twoPowTab[power-MIN_DOUBLE_EXPONENT];
        } else {
            if (power <= -MAX_DOUBLE_EXPONENT) { // Not normal.
                return Double.longBitsToDouble(0x0008000000000000L>>(-(power+MAX_DOUBLE_EXPONENT)));
            } else { // Normal.
                return Double.longBitsToDouble(((long)(power+MAX_DOUBLE_EXPONENT))<<52);
            }
        }
    }

    static double atan2_pinf_yyy(double y) {
        if (y == Double.POSITIVE_INFINITY) {
            return Math.PI/4;
        } else if (y == Double.NEGATIVE_INFINITY) {
            return -Math.PI/4;
        } else if (y > 0.0) {
            return 0.0;
        } else if (y < 0.0) {
            return -0.0;
        } else {
            return Double.NaN;
        }
    }

    static double atan2_ninf_yyy(double y) {
        if (y == Double.POSITIVE_INFINITY) {
            return 3*Math.PI/4;
        } else if (y == Double.NEGATIVE_INFINITY) {
            return -3*Math.PI/4;
        } else if (y > 0.0) {
            return Math.PI;
        } else if (y < 0.0) {
            return -Math.PI;
        } else {
            return Double.NaN;
        }
    }

    static double atan2_yyy_zeroOrNaN(double y, double x) {
        if (x == 0.0) {
            if (y == 0.0) {
                if (signFromBit_antiCyclic(x) < 0) {
                    // x is -0.0
                    return signFromBit_antiCyclic(y) * Math.PI;
                } else {
                    // +-0.0
                    return y;
                }
            }
            if (y > 0.0) {
                return Math.PI/2;
            } else if (y < 0.0) {
                return -Math.PI/2;
            } else {
                return Double.NaN;
            }
        } else {
            return Double.NaN;
        }
    }

    /**
     * At least one of the arguments must be NaN.
     */
    static double hypot_NaN(double xAbs, double yAbs) {
        if ((xAbs == Double.POSITIVE_INFINITY) || (yAbs == Double.POSITIVE_INFINITY)) {
            return Double.POSITIVE_INFINITY;
        } else {
            return Double.NaN;
        }
    }

    /**
     * At least one of the arguments must be NaN.
     */
    static double hypot_NaN(double xAbs, double yAbs, double zAbs) {
        if ((xAbs == Double.POSITIVE_INFINITY) || (yAbs == Double.POSITIVE_INFINITY) || (zAbs == Double.POSITIVE_INFINITY)) {
            return Double.POSITIVE_INFINITY;
        } else {
            return Double.NaN;
        }
    }

    /*
     *
     */

    /**
     * @param remainder Must have 1 for 2nd and 3rd exponent bits, which is the
     *        case for heavyRemPiO2 remainders (their absolute values are >=
     *        Double.longBitsToDouble(0x3000000000000000L)
     *        = 1.727233711018889E-77, and even if they were not, turning these
     *        bits from 0 to 1 on decoding would not change the absolute error
     *        much), and also works for +-Infinity or NaN encoding.
     * @param quadrant Must be in [0,3].
     * @return Bits holding remainder, and quadrant instead of
     *         reamainder's 2nd and 3rd exponent bits.
     */
    static long encodeRemainderAndQuadrant(double remainder, int quadrant) {
        final long bits = Double.doubleToRawLongBits(remainder);
        return (bits&QUADRANT_BITS_0_MASK)|(((long)quadrant)<<60);
    }

    static double decodeRemainder(long bits) {
        return Double.longBitsToDouble((bits&QUADRANT_BITS_0_MASK)|QUADRANT_PLACE_BITS);
    }

    static int decodeQuadrant(long bits) {
        return ((int)(bits>>60))&3;
    }

    /*
     * JDK-based remainders.
     * Since a strict one for (% (PI/2)) is needed for heavyRemainderPiO2,
     * we need it in this class.
     * Then, for homogeneity, we put them all in this class.
     * Then, to avoid code duplication for these slow-anyway methods,
     * we just stick with strict versions, for both FastMath and StrictFastMath.
     */

    /**
     * @param angle Angle, in radians.
     * @return Remainder of (angle % (2*PI)), in [-PI,PI].
     */
    static strictfp double jdkRemainderTwoPi(double angle) {
        final double sin = StrictMath.sin(angle);
        final double cos = StrictMath.cos(angle);
        return StrictMath.atan2(sin, cos);
    }

    /**
     * @param angle Angle, in radians.
     * @return Remainder of (angle % PI), in [-PI/2,PI/2].
     */
    static strictfp double jdkRemainderPi(double angle) {
        final double sin = StrictMath.sin(angle);
        final double cos = StrictMath.cos(angle);
        /*
         * Making sure atan2's result ends up in [-PI/2,PI/2],
         * i.e. has maximum accuracy.
         */
        return StrictMath.atan2(sin, Math.abs(cos));
    }

    /**
     * @param angle Angle, in radians.
     * @return Bits of double corresponding to remainder of (angle % (PI/2)),
     *         in [-PI/4,PI/4], with quadrant encoded in exponent bits.
     */
    static strictfp long jdkRemainderPiO2(double angle, boolean negateRem) {
        final double sin = StrictMath.sin(angle);
        final double cos = StrictMath.cos(angle);

        /*
         * Computing quadrant first, and then computing
         * atan2, to make sure its result ends up in [-PI/4,PI/4],
         * i.e. has maximum accuracy.
         */

        final int q;
        final double sinForAtan2;
        final double cosForAtan2;
        if (cos >= (SQRT_2/2)) {
            // [-PI/4,PI/4]
            q = 0;
            sinForAtan2 = sin;
            cosForAtan2 = cos;
        } else if (cos <= -(SQRT_2/2)) {
            // [3*PI/4,5*PI/4]
            q = 2;
            sinForAtan2 = -sin;
            cosForAtan2 = -cos;
        } else if (sin > 0.0) {
            // [PI/4,3*PI/4]
            q = 1;
            sinForAtan2 = -cos;
            cosForAtan2 = sin;
        } else {
            // [5*PI/4,7*PI/4]
            q = 3;
            sinForAtan2 = cos;
            cosForAtan2 = -sin;
        }

        double fw = StrictMath.atan2(sinForAtan2, cosForAtan2);

        return encodeRemainderAndQuadrant(negateRem ? -fw : fw, q);
    }

    /*
     * Our remainders implementations.
     */

    /**
     * @param angle Angle, in radians. Must not be NaN nor +-Infinity.
     * @return Remainder of (angle % (2*PI)), in [-PI,PI].
     */
    static strictfp double heavyRemainderTwoPi(double angle) {
        final long remAndQuad = heavyRemainderPiO2(angle, false);
        final double rem = decodeRemainder(remAndQuad);
        final int q = decodeQuadrant(remAndQuad);
        if (q == 0) {
            return rem;
        } else if (q == 1) {
            return (rem + PIO2_LO) + PIO2_HI;
        } else if (q == 2) {
            if (rem < 0.0) {
                return (rem + PI_LO) + PI_HI;
            } else {
                return (rem - PI_LO) - PI_HI;
            }
        } else {
            return (rem - PIO2_LO) - PIO2_HI;
        }
    }

    /**
     * @param angle Angle, in radians. Must not be NaN nor +-Infinity.
     * @return Remainder of (angle % PI), in [-PI/2,PI/2].
     */
    static strictfp double heavyRemainderPi(double angle) {
        final long remAndQuad = heavyRemainderPiO2(angle, false);
        final double rem = decodeRemainder(remAndQuad);
        final int q = decodeQuadrant(remAndQuad);
        if ((q&1) != 0) {
            // q is 1 or 3
            if (rem < 0.0) {
                return (rem + PIO2_LO) + PIO2_HI;
            } else {
                return (rem - PIO2_LO) - PIO2_HI;
            }
        }
        return rem;
    }

    /**
     * Remainder using an accurate definition of PI.
     * Derived from a fdlibm treatment called __kernel_rem_pio2.
     *
     * Not defining a non-strictfp version for FastMath, to avoid duplicating
     * its long and messy code, and because it's slow anyway, and should be
     * rarely used when speed matters.
     *
     * @param angle Angle, in radians. Must not be NaN nor +-Infinity.
     * @param negateRem True if remainder must be negated before encoded into returned long.
     * @return Bits of double corresponding to remainder of (angle % (PI/2)),
     *         in [-PI/4,PI/4], with quadrant encoded in exponent bits.
     */
    static strictfp long heavyRemainderPiO2(double angle, boolean negateRem) {

        /*
         * fdlibm treatments unrolled, to avoid garbage and be OOME-free,
         * corresponding to:
         * 1) initial jk = 4 (precision = 3 = 64 bits (extended)),
         *    which is more accurate than using precision = 2
         *    (53 bits, double), even though we work with doubles
         *    and use strictfp!
         * 2) max lengths of 8 for f[], 6 for q[], fq[] and iq[].
         * 3) at most one recomputation (one goto).
         * These limitations were experimentally found to
         * be sufficient for billions of random doubles
         * of random magnitudes.
         * For the rare cases that our unrolled treatments can't handle,
         * we fall back to a JDK-based implementation.
         */

        int n,i,j,ih;
        double fw;

        /*
         * Turning angle into 24-bits integer chunks.
         * Done outside __kernel_rem_pio2, but we factor it inside our method.
         */

        // Reworking exponent to have a value < 2^24.
        final long lx = Double.doubleToRawLongBits(angle);
        final long exp = ((lx>>52)&0x7FF) - (1023+23);
        double z = Double.longBitsToDouble(lx - (exp<<52));

        double x0 = (double)(int)z;
        z = (z-x0)*TWO_POW_24;
        double x1 = (double)(int)z;
        z = (z-x1)*TWO_POW_24;
        double x2 = (double)(int)z;

        final int e0 = (int)exp;
        // in [1,3]
        final int nx = (x2 == 0.0) ? ((x1 == 0.0) ? 1 : 2) : 3;

        /*
         *
         */

        double f0,f1,f2,f3,f4,f5,f6,f7;
        double q0,q1,q2,q3,q4,q5;
        int iq0,iq1,iq2,iq3,iq4,iq5;

        int jk = 4;

        int jx = nx-1;
        int jv = Math.max(0,(e0-3)/24);
        // In fdlibm, this is q0, but we prefer to use q0 for q[0].
        int qZero = e0-24*(jv+1);

        j = jv-jx;
        if (jx == 0) {
            f6 = 0.0;
            f5 = 0.0;
            f4 = (j >= -4) ? TWO_OVER_PI_TAB[j+4] : 0.0;
            f3 = (j >= -3) ? TWO_OVER_PI_TAB[j+3] : 0.0;
            f2 = (j >= -2) ? TWO_OVER_PI_TAB[j+2] : 0.0;
            f1 = (j >= -1) ? TWO_OVER_PI_TAB[j+1] : 0.0;
            f0 = (j >= 0) ? TWO_OVER_PI_TAB[j] : 0.0;

            q0 = x0*f0;
            q1 = x0*f1;
            q2 = x0*f2;
            q3 = x0*f3;
            q4 = x0*f4;
        } else if (jx == 1) {
            f6 = 0.0;
            f5 = (j >= -5) ? TWO_OVER_PI_TAB[j+5] : 0.0;
            f4 = (j >= -4) ? TWO_OVER_PI_TAB[j+4] : 0.0;
            f3 = (j >= -3) ? TWO_OVER_PI_TAB[j+3] : 0.0;
            f2 = (j >= -2) ? TWO_OVER_PI_TAB[j+2] : 0.0;
            f1 = (j >= -1) ? TWO_OVER_PI_TAB[j+1] : 0.0;
            f0 = (j >= 0) ? TWO_OVER_PI_TAB[j] : 0.0;

            q0 = x0*f1 + x1*f0;
            q1 = x0*f2 + x1*f1;
            q2 = x0*f3 + x1*f2;
            q3 = x0*f4 + x1*f3;
            q4 = x0*f5 + x1*f4;
        } else { // jx == 2
            f6 = (j >= -6) ? TWO_OVER_PI_TAB[j+6] : 0.0;
            f5 = (j >= -5) ? TWO_OVER_PI_TAB[j+5] : 0.0;
            f4 = (j >= -4) ? TWO_OVER_PI_TAB[j+4] : 0.0;
            f3 = (j >= -3) ? TWO_OVER_PI_TAB[j+3] : 0.0;
            f2 = (j >= -2) ? TWO_OVER_PI_TAB[j+2] : 0.0;
            f1 = (j >= -1) ? TWO_OVER_PI_TAB[j+1] : 0.0;
            f0 = (j >= 0) ? TWO_OVER_PI_TAB[j] : 0.0;

            q0 = x0*f2 + x1*f1 + x2*f0;
            q1 = x0*f3 + x1*f2 + x2*f1;
            q2 = x0*f4 + x1*f3 + x2*f2;
            q3 = x0*f5 + x1*f4 + x2*f3;
            q4 = x0*f6 + x1*f5 + x2*f4;
        }

        double twoPowQZero = twoPowNormal(qZero);

        int jz = jk;

        /*
         * Unrolling of first round.
         */

        z = q4;
        fw = (double)(int)(TWO_POW_N24*z);
        iq0 = (int)(z-TWO_POW_24*fw);
        z = q3+fw;
        fw = (double)(int)(TWO_POW_N24*z);
        iq1 = (int)(z-TWO_POW_24*fw);
        z = q2+fw;
        fw = (double)(int)(TWO_POW_N24*z);
        iq2 = (int)(z-TWO_POW_24*fw);
        z = q1+fw;
        fw = (double)(int)(TWO_POW_N24*z);
        iq3 = (int)(z-TWO_POW_24*fw);
        z = q0+fw;
        iq4 = 0;
        iq5 = 0;

        z = (z*twoPowQZero) % 8.0;
        n = (int)z;
        z -= (double)n;

        ih = 0;
        if (qZero > 0) {
            // Parentheses against code formatter bug.
            i = (iq3>>(24-qZero));
            n += i;
            iq3 -= i<<(24-qZero);
            ih = iq3>>(23-qZero);
        } else if (qZero == 0) {
            ih = iq3>>23;
        } else if (z >= 0.5) {
            ih = 2;
        }

        if (ih > 0) {
            n += 1;
            // carry = 1 is common case,
            // so using it as initial value.
            int carry = 1;
            if (iq0 != 0) {
                iq0 = 0x1000000 - iq0;
                iq1 = 0xFFFFFF - iq1;
                iq2 = 0xFFFFFF - iq2;
                iq3 = 0xFFFFFF - iq3;
            } else if (iq1 != 0) {
                iq1 = 0x1000000 - iq1;
                iq2 = 0xFFFFFF - iq2;
                iq3 = 0xFFFFFF - iq3;
            } else if (iq2 != 0) {
                iq2 = 0x1000000 - iq2;
                iq3 = 0xFFFFFF - iq3;
            } else if (iq3 != 0) {
                iq3 = 0x1000000 - iq3;
            } else {
                carry = 0;
            }
            if (qZero > 0) {
                if (qZero == 1) {
                    iq3 &= 0x7FFFFF;
                } else if (qZero == 2) {
                    iq3 &= 0x3FFFFF;
                }
            }
            if (ih == 2) {
                z = 1.0 - z;
                if (carry != 0) {
                    z -= twoPowQZero;
                }
            }
        }

        if (z == 0.0) {
            if (iq3 == 0) {
                // With random values of random magnitude,
                // probability for this to happen seems lower than 1e-6.
                // jz would be more than just incremented by one,
                // which our unrolling doesn't support.
                return jdkRemainderPiO2(angle, negateRem);
            }
            if (jx == 0) {
                f5 = TWO_OVER_PI_TAB[jv+5];
                q5 = x0*f5;
            } else if (jx == 1) {
                f6 = TWO_OVER_PI_TAB[jv+5];
                q5 = x0*f6 + x1*f5;
            } else { // jx == 2
                f7 = TWO_OVER_PI_TAB[jv+5];
                q5 = x0*f7 + x1*f6 + x2*f5;
            }

            jz++;

            /*
             * Unrolling of second round.
             */

            z = q5;
            fw = (double)(int)(TWO_POW_N24*z);
            iq0 = (int)(z-TWO_POW_24*fw);
            z = q4+fw;
            fw = (double)(int)(TWO_POW_N24*z);
            iq1 = (int)(z-TWO_POW_24*fw);
            z = q3+fw;
            fw = (double)(int)(TWO_POW_N24*z);
            iq2 = (int)(z-TWO_POW_24*fw);
            z = q2+fw;
            fw = (double)(int)(TWO_POW_N24*z);
            iq3 = (int)(z-TWO_POW_24*fw);
            z = q1+fw;
            fw = (double)(int)(TWO_POW_N24*z);
            iq4 = (int)(z-TWO_POW_24*fw);
            z = q0+fw;
            iq5 = 0;

            z = (z*twoPowQZero) % 8.0;
            n = (int)z;
            z -= (double)n;

            ih = 0;
            if (qZero > 0) {
                // Parentheses against code formatter bug.
                i = (iq4>>(24-qZero));
                n += i;
                iq4 -= i<<(24-qZero);
                ih = iq4>>(23-qZero);
            } else if (qZero == 0) {
                ih = iq4>>23;
            } else if (z >= 0.5) {
                ih = 2;
            }

            if (ih > 0) {
                n += 1;
                // carry = 1 is common case,
                // so using it as initial value.
                int carry = 1;
                if (iq0 != 0) {
                    iq0 = 0x1000000 - iq0;
                    iq1 = 0xFFFFFF - iq1;
                    iq2 = 0xFFFFFF - iq2;
                    iq3 = 0xFFFFFF - iq3;
                    iq4 = 0xFFFFFF - iq4;
                } else if (iq1 != 0) {
                    iq1 = 0x1000000 - iq1;
                    iq2 = 0xFFFFFF - iq2;
                    iq3 = 0xFFFFFF - iq3;
                    iq4 = 0xFFFFFF - iq4;
                } else if (iq2 != 0) {
                    iq2 = 0x1000000 - iq2;
                    iq3 = 0xFFFFFF - iq3;
                    iq4 = 0xFFFFFF - iq4;
                } else if (iq3 != 0) {
                    iq3 = 0x1000000 - iq3;
                    iq4 = 0xFFFFFF - iq4;
                } else if (iq4 != 0) {
                    iq4 = 0x1000000 - iq4;
                } else {
                    carry = 0;
                }
                if (qZero > 0) {
                    if (qZero == 1) {
                        iq4 &= 0x7FFFFF;
                    } else if (qZero == 2) {
                        iq4 &= 0x3FFFFF;
                    }
                }
                if (ih == 2) {
                    z = 1.0 - z;
                    if (carry != 0) {
                        z -= twoPowQZero;
                    }
                }
            }

            if (z == 0.0) {
                if (iq4 == 0) {
                    // Case not encountered in tests, but still handling it.
                    // Would require a third loop unrolling.
                    return jdkRemainderPiO2(angle, negateRem);
                } else {
                    // z == 0.0, and iq4 != 0,
                    // so we remove 24 from qZero only once,
                    // but since we no longer use qZero,
                    // we just bother to multiply its 2-power
                    // by 2^-24.
                    jz--;
                    twoPowQZero *= TWO_POW_N24;
                }
            } else {
                // z != 0.0 at end of second round.
            }
        } else {
            // z != 0.0 at end of first round.
        }

        /*
         * After loop.
         */

        if (z != 0.0) {
            z /= twoPowQZero;
            if (z >= TWO_POW_24) {
                fw = (double)(int)(TWO_POW_N24*z);
                if (jz == jk) {
                    iq4 = (int)(z-TWO_POW_24*fw);
                    jz++; // jz to 5
                    // Not using qZero anymore so not updating it.
                    twoPowQZero *= TWO_POW_24;
                    iq5 = (int)fw;
                } else { // jz == jk+1 == 5
                    // Case not encountered in tests, but still handling it.
                    // Would require use of iq6, with jz = 6.
                    return jdkRemainderPiO2(angle, negateRem);
                }
            } else {
                if (jz == jk) {
                    iq4 = (int)z;
                } else { // jz == jk+1 == 5
                    // Case not encountered in tests, but still handling it.
                    iq5 = (int)z;
                }
            }
        }

        fw = twoPowQZero;

        if (jz == 5) {
            q5 = fw*(double)iq5;
            fw *= TWO_POW_N24;
        } else {
            q5 = 0.0;
        }
        q4 = fw*(double)iq4;
        fw *= TWO_POW_N24;
        q3 = fw*(double)iq3;
        fw *= TWO_POW_N24;
        q2 = fw*(double)iq2;
        fw *= TWO_POW_N24;
        q1 = fw*(double)iq1;
        fw *= TWO_POW_N24;
        q0 = fw*(double)iq0;

        /*
         * We just use HI part of the result.
         */

        fw = PIO2_TAB0*q5;
        fw += PIO2_TAB0*q4 + PIO2_TAB1*q5;
        fw += PIO2_TAB0*q3 + PIO2_TAB1*q4 + PIO2_TAB2*q5;
        fw += PIO2_TAB0*q2 + PIO2_TAB1*q3 + PIO2_TAB2*q4 + PIO2_TAB3*q5;
        fw += PIO2_TAB0*q1 + PIO2_TAB1*q2 + PIO2_TAB2*q3 + PIO2_TAB3*q4 + PIO2_TAB4*q5;
        fw += PIO2_TAB0*q0 + PIO2_TAB1*q1 + PIO2_TAB2*q2 + PIO2_TAB3*q3 + PIO2_TAB4*q4 + PIO2_TAB5*q5;

        if ((ih != 0) ^ negateRem) {
            fw = -fw;
        }

        return encodeRemainderAndQuadrant(fw, n&3);
    }

    //--------------------------------------------------------------------------
    // PRIVATE METHODS
    //--------------------------------------------------------------------------

    /**
     * Redefined here, to avoid cyclic dependency with (Strict)FastMath.
     *
     * @param value A double value.
     * @return -1 if sign bit is 1, 1 if sign bit is 0.
     */
    private static long signFromBit_antiCyclic(double value) {
        // Returning a long, to avoid useless cast into int.
        return ((Double.doubleToRawLongBits(value)>>62)|1);
    }

    private static boolean getBooleanProperty(
            final String key,
            boolean defaultValue) {
        final String tmp = System.getProperty(key);
        if (tmp != null) {
            return Boolean.parseBoolean(tmp);
        } else {
            return defaultValue;
        }
    }

    /**
     * Use look-up tables size power through this method,
     * to make sure is it small in case java.lang.Math
     * is directly used.
     */
    private static int getTabSizePower(int tabSizePower) {
        return (FM_USE_JDK_MATH && SFM_USE_JDK_MATH) ? Math.min(2, tabSizePower) : tabSizePower;
    }
}
