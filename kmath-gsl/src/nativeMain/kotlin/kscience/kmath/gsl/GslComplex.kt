package kscience.kmath.gsl

import kotlinx.cinterop.*
import kscience.kmath.operations.Complex
import org.gnu.gsl.gsl_complex

internal fun CValue<gsl_complex>.toKMath(): Complex = useContents { Complex(dat[0], dat[1]) }

internal fun Complex.toGsl(): CValue<gsl_complex> = cValue {
    dat[0] = re
    dat[1] = im
}
