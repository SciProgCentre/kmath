// Instead of defining runtime properties and use them dynamically
// define version in buildSrc and have autocompletion and compile-time check
// Also dependencies itself can be moved here
object Ver {
    val ioVersion = "0.1.5"
    val coroutinesVersion = "1.1.1"
    val atomicfuVersion = "0.12.1"
    // This version is not used and IDEA shows this property as unused
    val dokkaVersion = "0.9.17"
}
