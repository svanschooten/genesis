
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
/** @author  John Miller
 *  @version 1.0
 *  @date    Wed Aug 26 18:41:26 EDT 2009
 *  @see     LICENSE (MIT style license file).
 */

package scalation.math

import Numeric._
import math.{ceil, sqrt}

import scalation.util.Error

//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
/** Convenience definitions for commonly used types of vectors.
 */
object Vectors
{
    type VectorI = VectorN [Int]                    // Vector of Integers
    type VectorL = VectorN [Long]                   // Vector of Long Integers
    type VectorF = VectorN [Float]                  // Vector of Floating Point Numbers
    type VectorD = VectorN [Double]                 // Vector of Double Precision Float
    type VectorC = VectorN [Complex]                // Vector of Complex Numbers

    type FunctionS2S [T] = T => T                       // Function mapping scalars to scalars
    type FunctionV2S [T] = VectorN [T] => T             // Function mapping vectors to scalars
    type FunctionV2V [T] = VectorN [T] => VectorN [T]   // Function mapping vectors to vectors

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Indicator function, returning 1 if i == j, 0 otherwise.
     *  @param i  the first integer value (e.g., index)
     *  @param j  the second integer value (e.g., index)
     */
    def ind (i: Int, j: Int): Int = if (i == j) 1 else 0

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Convert type IndexedSeq [B] to type VectorN [B].
     */
    implicit def seqToVectorN [B <% Ordered [B]: ClassManifest: Numeric]
                              (b: IndexedSeq [B]): VectorN [B] =
    {
       val c = new VectorN [B] (b.length)
       for (i <- 0 until b.length) c(i) = b(i)
       c
    } // seqToVectorN

} // Vectors object


//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
/** The VectorN class stores and operates on Numeric Vectors of various sizes
 *  and types.  The element type may be any subtype of Numeric.
 *  @param dim  the dimension/size of the vector
 *  @param v    the 1D array used to store vector elements
 */
class VectorN [T <% Ordered [T]: ClassManifest: Numeric]
      (val dim: Int, private var v: Array [T] = null)
      extends PartiallyOrdered [VectorN [T]] with Error
{
    import Vectors._

    {
        if (v == null) {
            v = new Array [T] (dim)
        } else if (dim != v.length) {
            flaw ("constructor", "dimension is wrong")
        } // if
    } // primary constructor

    /** Range for the storage array
     */
    private val range = 0 until dim

    /** Create and import Numeric evidence
     */
    private val nu = implicitly [Numeric [T]]
    import nu._

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Construct a vector from an array of values.
     *  @param u  the array of values
     */
    def this (u: Array [T]) { this (u.length, u) }

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Construct a vector from two or more values (repeated values T*).
     *  @param u0  the first value
     *  @param u1  the second value
     *  @param u   the rest of the values (zero or more additional values)
     */
    def this (u0: T, u1: T, u: T*)
    {
        this (u.length + 2)                        // invoke primary constructor
        v(0) = u0; v(1) = u1
        for (i <- 2 until dim) v(i) = u(i - 2)
    } // constructor

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Construct a vector and assign values from vector u.
     *  @param u  the other vector
     */
    def this (u: VectorN [T])
    {
        this (u.dim)                               // invoke primary constructor
        for (i <- range) v(i) = u(i)
    } // constructor

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Expand the size (dim) of this vector by 'more' elements.
     *  @param factor  the expansion factor
     */
    def expand (more: Int = dim): VectorN [T] =
    {
        if (more < 1) this       // no change
        else          new VectorN [T] (dim + more, Array.concat (v, new Array [T] (more)))
    } // expand

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Create a vector of the form (0, ... 1, ... 0) where the 1 is at position j.
     *  @param j     the position to place the 1
     *  @param size  the size of the vector (upper bound = size - 1)
     */
    def oneAt (j: Int, size: Int = dim): VectorN [T] =
    {
        for (i <- 0 until size) yield if (i == j) one else zero
    } // oneAt

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Create a vector of the form (0, ... -1, ... 0) where the -1 is at position j.
     *  @param j     the position to place the 1
     *  @param size  the size of the vector (upper bound = size - 1)
     */
    def _oneAt (j: Int, size: Int = dim): VectorN [T] =
    {
        for (i <- 0 until size) yield if (i == j) -one else zero
    } // _oneAt

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Create a ramp-up vector of increasing values: 0, 1, 2, ..., size - 1.
     *  @param size  the size of the vector (upper bound = size - 1)
     */
    def ramp (size: Int = dim): VectorN [T] = for (i <- 0 until size) yield nu.fromInt (i)

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Convert a VectorN [T] into a VectorN [Int] (VectorI).
     *  @param u  the vector to convert an integer vector
     */
    def toInt: VectorI = for (i <- range) yield nu.toInt (v(i))

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Convert a VectorN [T] into a VectorN [Double] (VectorD).
     *  @param u  the vector to convert a double vector
     */
    def toDouble: VectorD = for (i <- range) yield nu.toDouble (v(i))

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Get this vector's element at the i-th index position. 
     *  @param i  the given index
     */
    def apply (i: Int): T = v(i)

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Get this vector's elements within the given range (vector slicing).
     *  @param r  the given range
     */
    def apply (r: Range): VectorN [T] = slice (r.start, r.end)

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Get this vector's entire array.
     */
    def apply (): Array [T] = v

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Set this vector's element at the i-th index position. 
     *  @param i  the given index
     *  @param x  the value to assign
     */
    def update (i: Int, x: T) { v(i) = x }

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Set this vector's elements over the given range (vector slicing).
     *  @param r  the given range
     *  @param x  the value to assign
     */
    def update (r: Range, x: T) { for (i <- r) v(i) = x }

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Set this vector's elements over the given range (vector slicing).
     *  @param r  the given range
     *  @param u  the vector to assign
     */
    def update (r: Range, u: VectorN [T]) { for (i <- r) v(i) = u(i) }

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Set each value in this vector to x.
     *  @param x  the value to be assigned
     */
    def set (x: T) { for (i <- range) v(i) = x }

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Set the values in this vector to the values in array u.
     *  @param u  the array of values to be assigned
     */
    def setAll (u: Array [T]) { for (i <- range) v(i) = u(i) }

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Iterate over the vector element by element.
     *  @param f  the function to apply
     */
    def foreach [U] (f: T => U)
    {
        var i = 0    
        while (i < dim) { f (v(i)); i += 1 }
    } // foreach

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Slice this vector from to end.
     *  @param from  the start of the slice (included)
     *  @param till  the end of the slice (excluded)
     */
    def slice (from: Int, till: Int): VectorN [T] = new VectorN [T] (till - from, v.slice (from, till))

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Select a subset of elements of this vector corresponding to a basis.
     *  @param basis  the set of index positions (e.g., 0, 2, 5)
     */
    def select (basis: VectorI): VectorN [T] = for (i <- 0 until basis.dim) yield v(basis(i))

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Concatenate this vector and scalar b.
     *  @param b  the vector to be concatenated
     */
    def ++ (b: T): VectorN [T] = for (i <- 0 to dim) yield if (i < dim) v(i) else b

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Concatenate this vector and vector b.
     *  @param b  the vector to be concatenated
     */
    def ++ (b: VectorN [T]): VectorN [T] =
    {
        for (i <- 0 until dim + b.dim) yield if (i < dim) v(i) else b.v(i - dim)
    } // ++

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Add this vector and vector b.
     *  @param b  the vector to add
     */
    def + (b: VectorN [T]): VectorN [T] = for (i <- range) yield v(i) + b.v(i)

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Add this vector and scalar s.
     *  @param s  the scalar to add
     */
    def + (s: T): VectorN [T] = for (i <- range) yield v(i) + s
 
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Add this vector and scalar s._1 only at position s._2.
     *  @param s  the (scalar, position) to add
     */
    def + (s: Tuple2 [T, Int]): VectorN [T] =
    {
        for (i <- range) yield if (i == s._2) v(i) + s._1 else v(i)
    } // +
 
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Return the negative of this vector (unary minus).
     */
    def unary_-(): VectorN [T] = for (i <- range) yield -v(i)
 
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** From this vector substract vector b.
     *  @param b  the vector to subtract
     */
    def - (b: VectorN [T]): VectorN [T] = for (i <- range) yield v(i) - b.v(i)
 
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** From this vector subtract scalar s.
     *  @param s  the scalar to subtract
     */
    def - (s: T): VectorN [T] = for (i <- range) yield v(i) - s
 
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** From this vector subtract scalar s._1 only at position s._2.
     *  @param s  the (scalar, position) to subtract
     */
    def - (s: Tuple2 [T, Int]): VectorN [T] =
    {
        for (i <- range) yield if (i == s._2) v(i) - s._1 else v(i)
    } // -
 
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Multiply this vector by scalar s.
     *  @param s  the scalar to multiply by
     */
    def * (s: T): VectorN [T] = for (i <- range) yield v(i) * s

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Multiply this vector by vector b.
     *  @param b  the vector to multiply by
     */
    def * (b: VectorN [T]): VectorN [T] = for (i <- range) yield v(i) * b.v(i)

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Multiply this 'row' vector by matrix m.
     *  @param m  the matrix to multiply by
     */
    def * (m: MatrixN [T]): VectorN [T] = m.t * this

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Square each element of this vector.
     */
    def sq: VectorN [T] = this * this

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Divide this vector by vector b (element-by-element).
     *  @param b  the vector to divide by
     */
    def / (b: VectorN [T]) (implicit fr: Fractional [T]): VectorN [T] =
    {
        import fr._
        for (i <- range) yield v(i) / b.v(i)
    } // /

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Divide this vector by scalar s.
     *  @param s  the scalar to divide by
     */
    def / (s: T) (implicit fr: Fractional [T]): VectorN [T] =
    {
        import fr._
        for (i <- range) yield v(i) / s
    } // /

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Return the vector is absolute values.
     */
    def abs: VectorN [T] = for (i <- range) yield v(i).abs

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Sum the elements of this vector.
     */
    def sum: T = v.foldLeft (zero) (_ + _)

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Sum the elements of this vector skipping the ith element.
     *  @param i  the index of the element to skip
     */
    def sum_ne (i: Int): T = sum - v(i)

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Sum the positive (> 0) elements of this vector.
     */
    def sum_pos: T =
    {
        var sum = zero
        for (i <- range if v(i) > zero) sum += v(i)
        sum
    } // sum_pos

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Cumulate the values of this vector from left to right (e.g., create a
     *  cdf from a pmf). Example: (4, 2, 3, 1) --> (4, 6, 9, 10)
     */
    def cumulate: VectorN [T] =
    {
        var sum = zero
        for (i <- range) yield { sum += v(i); sum }
    } // cumulate

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Normalize this vector so that it sums to one (like a probability vector).
     */
    def normalize (implicit fr: Fractional [T]): VectorN [T] =
    {
        import fr._
        this * (one / sum)
    } // normalize

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Compute the dot product (or inner product) of this vector with vector b.
     *  @param b  the other vector
     */
    def dot (b: VectorN [T]): T =
    {
        var s = zero
        for (i <- range) s += v(i) * b.v(i)
        s
    } // dot

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Compute the outer product of this vector with vector b.
     *  @param b  the other vector
     */
    def outer (b: VectorN [T]): MatrixN [T] =
    {
        val c = new MatrixN [T] (dim, b.dim)
        for (i <- range; j <- b.range) c(i, j) = v(i) * b.v(j)
        c
    } // outer

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Compute the Euclidean norm (2-norm) squared of this vector.
     */
    def normSq: T = this dot this

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Compute the Euclidean norm (2-norm) of this vector (requires Fractional type).
     */
    def norm (implicit fr: Fractional [T]): Double = sqrt (normSq.asInstanceOf [Double])

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Find the maximum element in this vector.
     *  @param e  the ending index (exclusive) for the search
     */
    def max (e: Int = dim): T = v.slice (0, e).max

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Take the maximum of this vector with vector b (element-by element).
     *  @param b  the other vector
     */
    def max (b: VectorN [T]): VectorN [T] =
    {
        for (i <- range) yield if (b.v(i) > v(i)) b.v(i) else v(i)
    } // max

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Find the minimum element in this vector.
     *  @param e  the ending index (exclusive) for the search
     */
    def min (e: Int = dim): T = v.slice (0, e).min

    /**:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
     * Take the minimum of this vector with vector b (element-by element).
     * @param b  the other vector
     */
    def min (b: VectorN [T]): VectorN [T] =
    {
        for (i <- range) yield if (b.v(i) < v(i)) b.v(i) else v(i)
    } // min

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Find the argument maximum of this vector (index of maximum element).
     *  @param e  the ending index (exclusive) for the search
     */
    def argmax (e: Int = dim): Int =
    {
        var j = 0
        for (i <- 0 until e if v(i) > v(j)) j = i
        j
    } // argmax

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Find the argument minimum of this vector (index of minimum element).
     *  @param e  the ending index (exclusive) for the search
     */
    def argmin (e: Int = dim): Int =
    {
        var j = 0
        for (i <- 0 until e if v(i) < v(j)) j = i
        j
    } // argmin

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Return the argument minimum of this vector (-1 if its not negative).
     *  @param e  the ending index (exclusive) for the search
     */
    def argminNeg (e: Int = dim): Int =
    {
        val j = argmin (e); if (v(j) < zero) j else -1
    } // argmaxNeg

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Return the argument maximum of this vector (-1 if its not positive).
     *  @param e  the ending index (exclusive) for the search
     */
    def argmaxPos (e: Int = dim): Int =
    {
        val j = argmax (e); if (v(j) > zero) j else -1
    } // argmaxPos

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Return the index of the first negative element in this vector (-1 otherwise).
     *  @param e  the ending index (exclusive) for the search
     */
    def firstNeg (e: Int = dim): Int =
    {
        for (i <- 0 until e if v(i) < zero) return i; -1
    } // firstNeg

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Return the index of the first positive element in this vector (-1 otherwise).
     *  @param e  the ending index (exclusive) for the search
     */
    def firstPos (e: Int = dim): Int =
    {
        for (i <- 0 until e if v(i) > zero) return i; -1
    } // firstPos

   //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Count the number of strictly negative entries in this vector.
     */
    def countNeg: Int =
    {
        var count = 0
        for (i <- 0 until dim if v(i) < zero) count += 1
        count
    } // countNeg

   //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Count the number of strictly positive entries in this vector.
     */
    def countPos: Int =
    {
        var count = 0
        for (i <- 0 until dim if v(i) > zero) count += 1
        count
    } // countPos

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Determine whether x is contained in this vector.
     *  @param x  the element to be checked
     */
    def contains (x: T): Boolean = v.contains (x)

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Check whether the other vector is at least as long as this vector.
     *  @param b  the other vector
     */
    def sameDimensions (b: VectorN [T]): Boolean = dim <= b.dim

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Check whether this vector is nonnegative (has no negative elements).
     */
    def isNonnegative: Boolean =
    {
        for (i <- range if v(i) < zero) return false
        true
    } // isNonnegative

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Compare this vector with vector b.
     *  @param b  the other vector
     */
    def tryCompareTo [B >: VectorN[T]] (b: B)
        (implicit view$1: (B) => PartiallyOrdered [B]): Option [Int] =
    {
        var le = true
        var ge = true

        for (i <- range) {
            val b_i = b.asInstanceOf [VectorN[T]](i)
            if      (ge && (v(i) compare b_i) < 0) ge = false
            else if (le && (v(i) compare b_i) > 0) le = false
        } // for
        if (ge && le) Some (0) else if (le) Some (-1) else if (ge) Some (1) else None
    } // tryCompareTo

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Determine whether vector this equals vector b.
     *  @param b  the vector to compare with this
     *
    override def equals (b: VectorN [T]): Boolean =
    {
        if (dim != b.dim) return false
        for (i <- range if v(i) != b.v(i)) return false
        true
    } // equals
     */

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Hash a vector into an integer.  Serves as the default hash function for
     *  vectors.  Warning, collisions may be unavoidable.
     *  @param x  the vector of type T to hash
     */
    override def hashCode (): Int =
    {
        import Primes.prime
        if (dim > prime.length) flaw ("hash", "not enough primes for computing hash function")
        var accum = 0
        for (i <- range) accum ^= (ceil (v(i).toDouble * prime(i))).toInt
        accum
    } // hashCode

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Convert this vector to a string.
     */
    override def toString: String = "VectorN" + v.deep.toString.substring (5)
  
} // VectorN class


//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
/** The VectorNTest object tests the operations provided by VectorN.
 */
object VectorNTest extends App
{
    import Vectors.{VectorI, VectorD}

    var a: VectorI = null
    var b: VectorI = null
    var c: VectorI = null
    var x: VectorD = null
    var y: VectorD = null

    for (l <- 1 to 4) {
        println ("\n\tTest VectorN on integer vectors of dim " + l)
        a = new VectorI (l)
        b = new VectorI (l)
        a.set (2)
        b.set (3)
        println ("a + b    = " + (a + b))
        println ("a - b    = " + (a - b))
        println ("a * b    = " + (a * b))
        println ("a * 4    = " + (a * 4))
        println ("a.max    = " + a.max ())
        println ("a.min    = " + a.min ())
        println ("a.sum    = " + a.sum)
        println ("a.sum_ne = " + a.sum_ne (0))
        println ("a dot b  = " + (a dot b))
        println ("a.normSq = " + a.normSq)
        println ("a < b    = " + (a < b))
        for (x <- a) print (" " + x)
        println

        println ("\n\tTest VectorN on real vectors of dim " + l)
        x = new VectorD (l)
        y = new VectorD (l)
        x.set (2)
        y.set (3)
        println ("x + y    = " + (x + y))
        println ("x - y    = " + (x - y))
        println ("x * y    = " + (x * y))
        println ("x * 4.0  = " + (x * 4.0))
        println ("x.min    = " + x.min ())
        println ("x.max    = " + x.max ())
        println ("x.sum    = " + x.sum)
        println ("x.sum_ne = " + x.sum_ne (0))
        println ("x dot y  = " + (x dot y))
        println ("x.normSq = " + x.normSq)
        println ("x.norm   = " + x.norm)
        println ("x < y    = " + (x < y))
    } // for

    c = new VectorI (4, 2, 3, 1)
    println ("c          = " + c) 
    println ("c.cumulate = " + c.cumulate)
    println ("c.ramp     = " + c.ramp ())

    println ("hashCode (" + a + ") = " + a.hashCode ())
    println ("hashCode (" + b + ") = " + b.hashCode ())
    println ("hashCode (" + c + ") = " + c.hashCode ())
    println ("hashCode (" + x + ") = " + x.hashCode ())
    println ("hashCode (" + y + ") = " + y.hashCode ())

} // VectorNTest

