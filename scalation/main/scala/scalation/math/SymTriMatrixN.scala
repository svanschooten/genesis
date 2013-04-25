
/**:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
 * @author  Robert Davis, John Miller
 * @version 1.0
 * @date    Thu Sep 16 22:17:43 EDT 2010
 * @see     LICENSE (MIT style license file).
 */

package scalation.math

import math.abs

import scalation.math.Vectors._
import scalation.util.Error

/**::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
 * Convenience definitions for commonly used types of symmetric tridiagonal matrices.
 */
object SymTriMatrices
{
    type SymTriMatrixI = SymTriMatrixN [Int]
    type SymTriMatrixL = SymTriMatrixN [Long]
    type SymTriMatrixF = SymTriMatrixN [Float]
    type SymTriMatrixD = SymTriMatrixN [Double]
    type SymTriMatrixC = SymTriMatrixN [Complex]

} // SymTriMatrices object


/**:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
 * The SymTriMatrixN class stores and operates on symmetric tridiagonal matrices.
 * The element may be of any subtype of Numeric.  A matrix is stored as two vectors:
 * the diagonal vector and the sub-diagonal vector.
 * @param d1  the first/row dimension (symmetric => d2 = d1)
 */
class SymTriMatrixN [T <% Ordered [T]: ClassManifest: Numeric] (val d1: Int)
      extends Matrix [T] (d1, d1) with Error
{
    /** Size of the sub-diagonal
     */
    private val d1_1 = d1 - 1

    /** Range for the diagonal
     */
    private val range_d = 0 until d1

    /** Range for the sub-diagonal
     */
    private val range_s = 0 until d1_1

    /** Diagonal of the matrix
     */
    private var _dg: VectorN [T] = new VectorN [T] (d1)

    /** Sub-diagonal (also same for sup-diagonal) of the matrix
     */
    private var _sd: VectorN [T] = new VectorN [T] (d1_1)

    /** Import Numeric evidence (gets nu val from superclass)
     */
    import nu._

    /**:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
     * Construct a symmetric tridiagonal matrix with the given diagonal and sub-diagonal.
     * @param v1  the diagonal vector
     * @param v2  the sub-diagonal vector
     */
    def this (v1: VectorN [T], v2: VectorN [T])
    {
        this (v1.dim)
        for (i <- range_d) _dg(i) = v1(i)
        for (i <- range_s) _sd(i) = v2(i)
    } // constructor

    /**:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
     * Construct a symmetric tridiagonal matrix from the given matrix.
     * @param a  the matrix of values to assign
     */
    def this (a: MatrixN [T])
    {
        this (a.dim1)
        for (i <- range_d) _dg(i) = a(i, i)
        for (i <- range_s) _sd(i) = a(i, i + 1)
    } // constructor

    /**:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
     * Get the diagonal of the matrix.
     */
    def dg: VectorN [T] = _dg

    /**:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
     * Set the diagonal of the matrix.
     * @param v  the vector to assign to the diagonal
     */
    def dg_ (v: VectorN [T]) { _dg = v }

    /**:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
     * Get the sub-diagonal of the matrix.
     */
    def sd: VectorN [T] = _sd

    /**:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
     * Set the sub-diagonal of the matrix.
     * @param v  the vector to assign to the sub-diagonal
     */
    def sd_ (v: VectorN [T]) { _sd = v }

    /**:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
     * Get this matrix's element at the i,j-th index position.
     * @param i  the row index
     * @param j  the column index
     */
    def apply (i: Int, j: Int): T = 
    {
        if      (i == j)     _dg(i)       // on diagonal
        else if (i == j + 1) _sd(j)       // on sub-diagonal (below diagonal)
        else if (i + 1 == j) _sd(i)       // on sup-diagonal (above diagonal)
        else throw new Exception ("apply: element not on tridiagonal")
    } // apply

    /**:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
     * Get this matrix's vector at the i-th index position (i-th row).
     * @param i  the row index
     */
    def apply (i: Int): VectorN [T] =
    {
        val v = new VectorN [T] (d1)
        v(i)  = _dg(i)
        if (i > 0)    v(i - 1) = _sd(i - 1)
        if (i < d1_1) v(i + 1) = _sd(i)
        v
    } // apply

    /**:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
     * Set this matrix's element at the i,j-th index position to the scalar x.
     * Only store x if it is non-zero.
     * @param i  the row index
     * @param j  the column index
     * @param x  the scalar value to assign
     */
    def update (i: Int, j: Int, x: T)
    {
        if      (i == j)     _dg(i) = x
        else if (i == j + 1) _sd(j) = x
        else if (i + 1 == j) _sd(i) = x
        else flaw ("update", "element not on tridiagonal")
    } // update

    /**:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
     * Set this matrix's row at the i-th index position to the vector u.
     * @param i  the row index
     * @param u  the vector value to assign
     */
    def update (i: Int, u: VectorN [T])
    {
        _dg(i) = u(i)
        if (i > 0)    _sd(i - 1) = u(i - 1)
        if (i < d1_1) _sd(i)     = u(i + 1)
    } // update

    /**:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
     * Transpose this matrix (rows => columns).  Note, since the matrix is
     * symmetric, it returns itself.
     */
    def t: SymTriMatrixN [T] = this

   /**:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
     * Add this matrix and matrix b.
     * @param b  the matrix to add (requires sameCrossDimensions)
     */
    def + (b: Matrix [T]): Matrix [T] = 
    {
        val trid = b.asInstanceOf [SymTriMatrixN [T]]
	if (d1 == trid.d1) {
            new SymTriMatrixN [T] (_dg + trid.dg, _sd + trid.sd)
        } else {
            flaw ("+", "matrix b has the wrong dimensions")
            null
        } // if
    } // +

   /**:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
     * Add inplace this matrix and matrix b.
     * @param b  the matrix to add (requires sameCrossDimensions)
     */
    def += (b: Matrix [T]) = 
    {
        val trid = b.asInstanceOf[SymTriMatrixN [T]]
        if (d1 == trid.d1) {
            _dg += trid.dg
            _sd += trid.sd
        } else {
            flaw ("+=", "matrix b has the wrong dimensions")
        } // if
    } // +=

   /**:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
     * Add this matrix and scalar s.
     * @param s  the scalar to add
     */
    def + (s: T): Matrix [T] = 
    {
        new SymTriMatrixN [T] (_dg + s, _sd + s)
    } // +

   /**:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
     * Add inplace this matrix and scalar s.
     * @param s  the scalar to add
     */
    def += (s: T) = 
    {
        _dg += s; _sd += s
    } // +=

    /**:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
     * From this matrix substract matrix b.
     * @param b  the matrix to subtract (requires sameCrossDimensions)
     */
    def - (b: Matrix [T]): Matrix [T] = 
    {
        val trid = b.asInstanceOf [SymTriMatrixN [T]]
        if (d1 == trid.d1) {
            return new SymTriMatrixN [T] (_dg - trid.dg, _sd - trid.sd)
        } else {
            flaw ("-", "matrix b has the wrong dimensions")
        } // if
        null
    } // -

    /**:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
     * From this matrix substract inplace matrix b.
     * @param b  the matrix to subtract (requires sameCrossDimensions)
     */
    def -= (b: Matrix [T]) = 
    {
        val trid = b.asInstanceOf [SymTriMatrixN [T]]
        if (d1 == trid.d1) {
            _dg -= trid.dg
            _sd -= trid.sd
        } else {
            flaw ("-=", "matrix b has the wrong dimensions")
        } // if
    } // -=

    /**:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
     * From this matrix subtract scalar s.
     * @param s  the scalar to subtract
     */
    def - (s: T): Matrix [T] = 
    {
        new SymTriMatrixN [T] (_dg - s, _sd - s)
    } // -

    /**:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
     * From this matrix subtract inplace scalar s.
     * @param s  the scalar to subtract
     */
    def -= (s: T) = 
    {
        _dg -= s; _sd -= s
    } // -=

    /**:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
     * Multiply this matrix by vector b.
     * @param b  the vector to multiply by
     */
    def * (b: VectorN [T]): VectorN [T] = 
    {
        import nu._
        val c = new VectorN [T] (d1)
        c(0)  = b(0) * _dg(0) + _sd(0) * b(1)
        for (i <- 1 until d1_1) {
            c(i) = _sd(i - 1) * b(i - 1)
            c(i) = c(i) + _dg(i) * b(i)
            c(i) = c(i) + _sd(i + 1) * b(i + 1)
        } // for
        c(d1 - 1) = _sd(d1 - 2) * b(d1 - 2) + _dg(d1 - 1) * b(d1 - 1)
        c
    } // *

    /**:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
     * Multiply this matrix by scalar s.
     * @param s  the scalar to multiply by
     */
    def * (s: T): Matrix [T] = 
    {
        new SymTriMatrixN [T] (_dg * s, _sd * s)
    } // *

    /**:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
     * Multiply inplace this matrix by scalar s.
     * @param s  the scalar to multiply by
     */
    def *= (s: T) = 
    {
        _dg *= s; _sd *= s
    } // *=

    /**:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
     * Check whether this matrix is rectangular (all rows have the same number
     * of columns).
     */
    def isRectangular: Boolean = true

    /**:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
     * Compute the determinant of this matrix.
     */
    def det: T = 
    {
        detHelper (d1 - 1)
    } // det

    /**:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
     * Solve for x in the equation a*x = d where a is this matrix
     * @param d  the constant vector.
     */
    def solve (d: VectorN [T]) (implicit fr: Fractional [T]): VectorN [T] =
    {
        import fr._
        val x = new VectorN [T] (d1)
        val c = new VectorN [T] (d1)
        val a = new VectorN [T] (d1)
        val b = new VectorN [T] (d1)
        for (i <- range_s) { c(i) = _sd(i); a(i) = _sd(i) }
        for (i <- range_d) b(i) = _dg(i)

        c(0) = c(0) / b(0)
        d(0) = d(0) / b(0)
        for (i <- 1 until d1) {
            val id = one / (b(i) - c(i - 1) * a(i))
            c(i)   = c(i) * id
            d(i)   = (d(i) - d(i - 1) * a(i)) * id
        } // for
        x(d1 - 1) = d(d1 - 1)
        for (i <- d1 - 2 to 0 by -1) x(i) = d(i) - c(i) * x(i + 1)
        x
    } // solve

    /**:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
     * Convert this symmetric tridiagonal matrix to a string.
     */
    override def toString: String = 
    {
        "\nSymTriMatrixN(\t" + _dg + ", \n\t\t\t" + _sd + ")"
    } // toString

    /**:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
     * Helper method for computing the determinant of this matrix.
     * @param n  the current dimension
     */
    private def detHelper (n: Int): T =
    {
        import nu._
        if (n == 0)      _dg(0)
        else if (n == 1) _dg(0) * _dg(1) - _sd(0) * _sd(0)
	else             _dg(n) * detHelper (n - 1) - _sd(n - 1) * _sd(n - 1) * detHelper (n - 2)
    } // detHelper

    //--------------------------------------------------------------------------
    // The following methods are not useful for Symmetric Tridiagonal matrices

    def slice (from: Int, end: Int): Matrix [T] = 
    {
        throw new NoSuchMethodException ("convert to other matrix type first")
    } // slice

    def slice (r_from: Int, r_end: Int, c_from: Int, c_end: Int): Matrix [T] = 
    {
        throw new NoSuchMethodException ("convert to other matrix type first")
    } // slice

    def sliceExclude (row: Int, col: Int): Matrix [T] =
    {
        throw new NoSuchMethodException ("convert to other matrix type first")
    } // sliceExclude

    def * (b: Matrix [T]): Matrix [T] = 
    {
        throw new Exception("convert to other matrix type first")
    } // *

    def *= (b: Matrix [T]) = 
    {
        throw new Exception("convert to other matrix type first")
    } // *=

    def ** (b: VectorN [T]): Matrix [T] = 
    {
        throw new NoSuchMethodException ("convert to other matrix type first")
    } // **

    def **= (b: VectorN [T]) = 
    {
        throw new NoSuchMethodException ("convert to other matrix type first")
    } // **=

    def lud (implicit fr: Fractional [T]): Tuple2 [MatrixN [T], MatrixN [T]] =
    {
        throw new NoSuchMethodException ("not implemented")
    } // lud

    def lud_ip (implicit fr: Fractional [T]): Tuple2 [Matrix [T], Matrix [T]] = 
    {
        throw new NoSuchMethodException ("not implemented")
    } // lud_ip

    def solve (l: Matrix [T], u: Matrix [T], b: VectorN [T])
              (implicit fr: Fractional [T]): VectorN [T] = 
    {
        throw new NoSuchMethodException ("not implemented")
    } // solve

    def solve (lu: Tuple2 [Matrix [T], Matrix [T]], b: VectorN [T])
              (implicit fr: Fractional [T]): VectorN [T] = 
    {
        throw new NoSuchMethodException ("not implemented")
    } // solve

    def diag (b: Matrix [T]): Matrix [T] = 
    {
        throw new NoSuchMethodException ("not implemented")
    } // diag

    def diag (p: Int, q: Int): Matrix [T] = 
    {
        throw new NoSuchMethodException("not implemented")
    } // diag

    def inverse_npp (implicit fr: Fractional [T]): Matrix [T] = 
    {
        throw new NoSuchMethodException("not implemented")
    } // inverse_npp

    def inverse (implicit fr: Fractional [T]): Matrix [T] = 
    {
        throw new NoSuchMethodException("not implemented")
    } // inverse

    def inverse_ip (implicit fr: Fractional [T]): Matrix [T] = 
    {
        throw new NoSuchMethodException("not implemented")
    } // inverse_ip

    def reduce (implicit fr: Fractional [T]): Matrix [T] = 
    {
        throw new NoSuchMethodException("not implemented")
    } // reduce

    def reduce_ip (implicit fr: Fractional [T])
    {
        throw new NoSuchMethodException("not implemented")
    } // reduce_ip

} // SymTriMatrixN class


/**:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
 * This object is used to test the SymTriMatrixN class.
 */
object SymTriMatrixNtest extends App
{
    val a = new SymTriMatrixN [Double] (new VectorD (1., 2., 3.),
                                               new VectorD (4., 5.))

    val b = new SymTriMatrixN [Double] (new VectorD (2., 3., 4.),
                                               new VectorD (5., 6.))

    println ("a     = " + a)
    println ("b     = " + b)
    println ("a.det = " + a.det)	
    println ("a + b = " + (a + b))	

} // SymTriMatrixNTest object

