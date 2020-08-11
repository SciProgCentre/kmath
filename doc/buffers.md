# Buffers

Buffer is one of main building blocks of kmath. It is a basic interface allowing random-access read and write (with `MutableBuffer`).
There are different types of buffers:

* Primitive buffers wrapping like `RealBuffer` which are wrapping primitive arrays.
* Boxing `ListBuffer` wrapping a list
* Functionally defined `VirtualBuffer` which does not hold a state itself, but provides a function to calculate value
* `MemoryBuffer` allows direct allocation of objects in continuous memory block.

Some kmath features require a `BufferFactory` class to operate properly. A general convention is to use functions defined in
`Buffer` and `MutableBuffer` companion classes. For example factory `Buffer.Companion::auto` in most cases creates the most suitable
buffer for given reified type (for types with custom memory buffer it still better to use their own `MemoryBuffer.create()` factory).

## Buffer performance

One should avoid using default boxing buffer wherever it is possible. Try to use primitive buffers or memory buffers instead  
