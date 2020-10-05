package kscience.kmath.gsl.codegen

import java.util.regex.Pattern

private val EOL_SPLIT_DONT_TRIM_PATTERN: Pattern = Pattern.compile("(\r|\n|\r\n)+")

internal fun splitByLinesDontTrim(string: String): Array<String> {
    return EOL_SPLIT_DONT_TRIM_PATTERN.split(string)
}
