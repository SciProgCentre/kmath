package kscience.kmath.gsl

import kotlinx.cinterop.staticCFunction
import kotlinx.cinterop.toKString
import org.gnu.gsl.gsl_set_error_handler
import org.gnu.gsl.gsl_set_error_handler_off
import kotlin.native.concurrent.AtomicInt

private object Container {
    val isKmathHandlerRegistered = AtomicInt(0)
}

internal enum class GslErrnoValue(val code: Int, val text: String) {
    GSL_SUCCESS(org.gnu.gsl.GSL_SUCCESS, ""),
    GSL_FAILURE(org.gnu.gsl.GSL_FAILURE, ""),
    GSL_CONTINUE(org.gnu.gsl.GSL_CONTINUE, "iteration has not converged"),
    GSL_EDOM(org.gnu.gsl.GSL_EDOM, "input domain error, e.g sqrt(-1)"),
    GSL_ERANGE(org.gnu.gsl.GSL_ERANGE, "output range error, e.g. exp(1e100)"),
    GSL_EFAULT(org.gnu.gsl.GSL_EFAULT, "invalid pointer"),
    GSL_EINVAL(org.gnu.gsl.GSL_EINVAL, "invalid argument supplied by user"),
    GSL_EFAILED(org.gnu.gsl.GSL_EFAILED, "generic failure"),
    GSL_EFACTOR(org.gnu.gsl.GSL_EFACTOR, "factorization failed"),
    GSL_ESANITY(org.gnu.gsl.GSL_ESANITY, "sanity check failed - shouldn't happen"),
    GSL_ENOMEM(org.gnu.gsl.GSL_ENOMEM, "malloc failed"),
    GSL_EBADFUNC(org.gnu.gsl.GSL_EBADFUNC, "problem with user-supplied function"),
    GSL_ERUNAWAY(org.gnu.gsl.GSL_ERUNAWAY, "iterative process is out of control"),
    GSL_EMAXITER(org.gnu.gsl.GSL_EMAXITER, "exceeded max number of iterations"),
    GSL_EZERODIV(org.gnu.gsl.GSL_EZERODIV, "tried to divide by zero"),
    GSL_EBADTOL(org.gnu.gsl.GSL_EBADTOL, "user specified an invalid tolerance"),
    GSL_ETOL(org.gnu.gsl.GSL_ETOL, "failed to reach the specified tolerance"),
    GSL_EUNDRFLW(org.gnu.gsl.GSL_EUNDRFLW, "underflow"),
    GSL_EOVRFLW(org.gnu.gsl.GSL_EOVRFLW, "overflow"),
    GSL_ELOSS(org.gnu.gsl.GSL_ELOSS, "loss of accuracy"),
    GSL_EROUND(org.gnu.gsl.GSL_EROUND, "failed because of roundoff error"),
    GSL_EBADLEN(org.gnu.gsl.GSL_EBADLEN, "matrix, vector lengths are not conformant"),
    GSL_ENOTSQR(org.gnu.gsl.GSL_ENOTSQR, "matrix not square"),
    GSL_ESING(org.gnu.gsl.GSL_ESING, "apparent singularity detected"),
    GSL_EDIVERGE(org.gnu.gsl.GSL_EDIVERGE, "integral or series is divergent"),
    GSL_EUNSUP(org.gnu.gsl.GSL_EUNSUP, "requested feature is not supported by the hardware"),
    GSL_EUNIMPL(org.gnu.gsl.GSL_EUNIMPL, "requested feature not (yet) implemented"),
    GSL_ECACHE(org.gnu.gsl.GSL_ECACHE, "cache limit exceeded"),
    GSL_ETABLE(org.gnu.gsl.GSL_ETABLE, "table limit exceeded"),
    GSL_ENOPROG(org.gnu.gsl.GSL_ENOPROG, "iteration is not making progress towards solution"),
    GSL_ENOPROGJ(org.gnu.gsl.GSL_ENOPROGJ, "jacobian evaluations are not improving the solution"),
    GSL_ETOLF(org.gnu.gsl.GSL_ETOLF, "cannot reach the specified tolerance in F"),
    GSL_ETOLX(org.gnu.gsl.GSL_ETOLX, "cannot reach the specified tolerance in X"),
    GSL_ETOLG(org.gnu.gsl.GSL_ETOLG, "cannot reach the specified tolerance in gradient"),
    GSL_EOF(org.gnu.gsl.GSL_EOF, "end of file");

    override fun toString(): String = "${name}('$text')"

    companion object {
        fun valueOf(code: Int): GslErrnoValue? = values().find { it.code == code }
    }
}

/**
 * Wraps all the errors reported by GSL.
 */
public class GslException internal constructor(file: String, line: Int, reason: String, errno: Int) :
    RuntimeException("$file:$line: $reason. errno - $errno, ${GslErrnoValue.valueOf(errno)}") {
}

internal fun ensureHasGslErrorHandler() {
    if (Container.isKmathHandlerRegistered.value == 1) return
    gsl_set_error_handler_off()

    gsl_set_error_handler(staticCFunction { reason, file, line, errno ->
        throw GslException(checkNotNull(file).toKString(), line, checkNotNull(reason).toKString(), errno)
    })

    Container.isKmathHandlerRegistered.value = 1
}
