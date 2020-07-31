# Local coding conventions

Kmath and other `scientifik` projects use general [kotlin code conventions](https://kotlinlang.org/docs/reference/coding-conventions.html), but with a number of small changes and clarifications.

## Utility class names
File name should coincide with a name of one of the classes contained in the file or start with small letter and describe its contents.

The code convention [here](https://kotlinlang.org/docs/reference/coding-conventions.html#source-file-names) says that file names should start with capital letter even if file does not contain classes. Yet starting utility classes and aggregators with a small letter seems to be a good way to clearly visually separate those files.

This convention could be changed in future in a non-breaking way.

## Private variable names
Private variable names could start with underscore `_` in case the private mutable variable is shadowed by the public read-only value with the same meaning.

Code convention do not permit underscores in names, but is is sometimes useful to "underscore" the fact that public and private versions define the same entity. It is allowed only for private variables.

This convention could be changed in future in a non-breaking way.

## Functions and properties one-liners
Use one-liners when they occupy single code window line both for functions and properties with getters like `val b: String get() = "fff"`. The same should be done with multiline expressions when they could be cleanly separated.

There is not general consensus whenever use `fun a() = {}` or `fun a(){return}`. Yet from reader perspective one-lines seem to better show that the property or function is easily calculated.