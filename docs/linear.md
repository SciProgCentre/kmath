## Basic linear algebra layout

KMath support for linear algebra organized in a context-oriented way. Meaning that operations are in most cases declared 
in context classes, and are not the members of classes that store data. This allows more flexible approach to maintain multiple 
back-ends. The new operations added as extensions to contexts instead of being member functions of data structures.

Two major contexts used for linear algebra and hyper-geometry:

* `VectorSpace` forms a mathematical space on top of array-like structure (`Buffer` and its typealias `Point` used for geometry). 

* `MatrixContext` forms a space-like context for 2d-structures. It does not store matrix size and therefore does not implement
`Space` interface (it is not possible to create zero element without knowing the matrix size). 

## Vector spaces


## Matrix operations

## Back-end overview