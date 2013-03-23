package scalation

import math.{abs, max}

import scalation.Basic.oneIf

/** The MatrixD class stores and operates on Numeric Matrices of type Double.
 *  This class follows the MatrixN framework and is provided for efficieny.
 *  @param d1  the first/row dimension
 *  @param d2  the second/column dimension
 *  @param v   the 2D array used to store matrix elements
 */
class MatrixD (val d1: Int, val d2: Int, private var v:  Array [Array [Double]] = null)
    extends Matrix with Error with Serializable {

    lazy val dim1 = d1
    lazy val dim2 = d2

    if(v == null) {
        v = Array.ofDim[Double](dim1, dim2)
    } else if(dim1 != v.length || dim2 != v(0).length) {
        flaw("constructor", "dimensions are wrong")
    }

    /** Construct a dim1 by dim1 square matrix.
     *  @param dim1  the row and column dimension
     */
    def this(dim1: Int) {
        this(dim1, dim1)
    }

    /** Construct a dim1 by dim2 matrix and assign each element the value x.
     *  @param dim1  the row dimension
     *  @param dim2  the column dimesion
     *  @param x     the scalar value to assign
     */
    def this(dim1: Int, dim2: Int, x: Double) {
        this(dim1, dim2)
        for(
            i <- range1;
            j <- range2
        ) v(i)(j) = x
    }

    /** Construct a dim1 by dim1 square matrix with x assigned on the diagonal
     *  and y assigned off the diagonal.  To obtain an identity matrix, let x = 1
     *  and y = 0.
     *  @param dim1  the row and column dimension
     *  @param x     the scalar value to assign on the diagonal
     *  @param y     the scalar value to assign off the diagonal
     */
    def this(dim1: Int, x: Double, y: Double) {
        this(dim1, dim1)
        for(
            i <- range1;
            j <- range1
        ) v(i)(j) = if(i == j) x else y
    }
    
    /** Construct a matrix and assign values from array of arrays u.
     *  @param u  the 2D array of values to assign
     */
    def this(u: Array[Array[Double]]) {
        this(u.length, u(0).length, u)
    }
    
    /** Construct a matrix from repeated values.
     *  @param dim  the (row, column) dimensions
     *  @param u    the repeated values
     */
    def this(dim: (Int, Int), u: Double*) {
        this(dim._1, dim._2)
        for(
            i <- range1;
            j <- range2
        ) v(i)(j) = u(i * dim2 + j)
    }

    /** Construct a matrix and assign values from array of vectors u.
     *  @param u  the 2D array of values to assign
     */
    def this(u: Array [VectorD]) {
        this(u.length, u(0).dim)
        for(
            i <- range1;
            j <- range2
        ) v(i)(j) = u(i)(j)
    }
    
    /** Construct a matrix and assign values from matrix u.
     *  @param b  the matrix of values to assign
     */
    def this(b: MatrixD) {
        this(b.d1, b.d2)
        for(
            i <- range1;
            j <- range2
        ) v(i)(j) = b.v(i)(j)
    }
    
    /** Get this matrix's element at the i,j-th index position. 
     *  @param i  the row index
     *  @param j  the column index
     */
    def apply(i: Int, j: Int): Double = v(i)(j)
    
    /** Get this matrix's vector at the i-th index position (i-th row).
     *  @param i  the row index
     */
    def apply(i: Int): VectorD = new VectorD(v(i))
    
    /** Get a slice this matrix row-wise on range ir and column-wise on range jr.
     *  Ex: b = a(2..4, 3..5)
     *  @param ir  the row range
     *  @param jr  the column range
     */
    def apply(ir: Range, jr: Range): MatrixD = slice(ir.start, ir.end, jr.start, jr.end)

    /** Get a slice this matrix row-wise on range ir and column-wise at index j.
     *  Ex: u = a(2..4, 3)
     *  @param ir  the row range
     *  @param j   the column index
     */
    def apply(ir: Range, j: Int): VectorD = col(j)(ir)

    /** Get a slice this matrix row-wise at index i and column-wise on range jr.
     *  Ex: u = a(2, 3..5)
     *  @param i   the row index
     *  @param jr  the column range
     */
    def apply(i: Int, jr: Range): VectorD = this(i)(jr)

    /** Set this matrix's element at the i,j-th index position to the scalar x.
     *  @param i  the row index
     *  @param j  the column index
     *  @param x  the scalar value to assign
     */
    def update(i: Int, j: Int, x: Double) {
        v(i)(j) = x
    }

    /** Set this matrix's row at the i-th index position to the vector u.
     *  @param i  the row index
     *  @param u  the vector value to assign
     */
    def update(i: Int, u: VectorD) {
        v(i) = u()
    }

    /** Set a slice this matrix row-wise on range ir and column-wise on range jr.
     *  Ex: a(2..4, 3..5) = b
     *  @param ir  the row range
     *  @param jr  the column range
     *  @param b   the matrix to assign
     */
    def update(ir: Range, jr: Range, b: MatrixD) {
        for(
            i <- ir;
            j <- jr
        ) v(i)(j) = b.v(i - ir.start)(j - jr.start)
    }
    
    /** Set a slice this matrix row-wise on range ir and column-wise at index j.
     *  Ex: a(2..4, 3) = u
     *  @param ir  the row range
     *  @param j   the column index
     *  @param u   the vector to assign
     */
    def update(ir: Range, j: Int, u: VectorD) {
        col(j)(ir) = u
    }

    /** Set a slice this matrix row-wise at index i and column-wise on range jr.
     *  Ex: a(2, 3..5) = u
     *  @param i   the row index
     *  @param jr  the column range
     *  @param u   the vector to assign
     */
    def update(i: Int, jr: Range, u: VectorD) {
        this(i)(jr) = u
    }
    
    /** Set all the elements in this matrix to the scalar x.
     *  @param x  the scalar value to assign
     */
    def set(x: Double) {
        for(
            i <- range1;
            j <- range2
        ) v(i)(j) = x
    }

    /** Set all the values in this matrix as copies of the values in 2D array u.
     *  @param u  the 2D array of values to assign
     */
    def set(u: Array[Array[Double]]) {
        for (
            i <- range1;
            j <- range2
        ) v(i)(j) = u(i)(j)
    }
    
    /** Set this matrix's ith row starting at column j to the vector u.
     *  @param i  the row index
     *  @param u  the vector value to assign
     *  @param j  the starting column index
     */
    def set(i: Int, u: VectorD, j: Int = 0) {
        for(k <- 0 until u.dim)
            v(i)(k+j) = u(k)
    }

    /** Slice this matrix row-wise from to end.
     *  @param from  the start row of the slice (inclusive)
     *  @param end   the end row of the slice (exclusive)
     */
    def slice(from: Int, end: Int): MatrixD = {
        new MatrixD(end - from, dim2, v.slice (from, end))
    }
    
    /** Slice this matrix row-wise r_from to r_end and column-wise c_from to c_end.
     *  @param r_from  the start of the row slice
     *  @param r_end   the end of the row slice
     *  @param c_from  the start of the column slice
     *  @param c_end   the end of the column slice
     */
    def slice(r_from: Int, r_end: Int, c_from: Int, c_end: Int): MatrixD = {
        val c = new MatrixD(r_end - r_from, c_end - c_from)
        for (
            i <- c.range1;
            j <- c.range2
        ) c.v(i)(j) = v(i + r_from)(j + c_from)
        c
    }
    
    /** Slice this matrix excluding the given row and/or column.
     *  @param row  the row to exclude (0 until dim1, set to dim1 to keep all rows)
     *  @param col  the column to exclude (0 until dim2, set to dim2 to keep all columns)
     */
    def sliceExclude(row: Int, col: Int): MatrixD = {
        val c = new MatrixD(dim1 - oneIf (row < dim1), dim2 - oneIf (col < dim2))
        for (
            i <- range1
            if i != row;
            j <- range2
            if j != col
        ) c.v(i - oneIf (i > row))(j - oneIf (j > col)) = v(i)(j)
        c
    }
    
    /** Select rows from this matrix according a basis. 
     *  @param basis  the row index positions (e.g., (0, 2, 5))
     */
    def selectRows(basis: Array[Int]): MatrixD = {
        val c = new MatrixD(basis.length)
        for(i <- c.range1)
            c(i) = col(basis(i))
        c
    }
    
    /** Get column 'c' from the matrix, returning it as a vector.
     *  @param c     the column to extract from the matrix
     *  @param from  the position to start extracting from
     */
    def col(c: Int, from: Int = 0): VectorD = {
        val u = new VectorD(dim1 - from)
        for(i <- from until dim1)
            u(i-from) = v(i)(c)
        u
    }
    
    /** Set column 'c' of the matrix to a vector.
     *  @param c  the column to set
     *  @param u  the vector to assign to the column
     */
    def setCol(c: Int, u: VectorD) {
        for(i <- range1)
            v(i)(c) = u(i)
    }
    
    /** Select columns from this matrix according a basis. 
     *  @param basis  the column index positions (e.g., (0, 2, 5))
     */
    def selectCols(basis: Array[Int]): MatrixD = {
        val c = new MatrixD(basis.length)
        for(j <- c.range1)
            c.setCol (j, col(basis(j)))
        c
    }
    
    /** Transpose this matrix (rows => columns).
     */
    def t: MatrixD = {
        val b = new MatrixD(dim2, dim1)
        for(
            i <- b.range1;
            j <- b.range2
        ) b.v(i)(j) = v(j)(i)
        b
    }
    
    /** Concatenate this matrix and vector u.
     *  @param u  the vector to be concatenated as the new last row in matrix
     */
    def ++ (u: VectorD): MatrixD = {
        if (u.dim != dim2)
            flaw("++", "vector does not match row dimension")

        val c = new MatrixD(dim1 + 1, dim2)
        for(i <- c.range1)
            c(i) = if (i < dim1) this(i) else u
        c
    }
    
    /** Add this matrix and matrix b.
     *  @param b  the matrix to add (requires leDimensions)
     */
    def + (b: MatrixD): MatrixD = {
        val c = new MatrixD(dim1, dim2)
        for(
            i <- range1;
            j <- range2
        ) c.v(i)(j) = v(i)(j) + b.v(i)(j)
        c
    }
    
    /** Add this matrix and scalar x.
     *  @param x  the scalar to add
     */
    def + (x: Double): MatrixD = {
        val c = new MatrixD(dim1, dim2)
        for(
            i <- range1;
            j <- range2
        ) c.v(i)(j) = v(i)(j) + x
        c
    }
    
    /** Add in-place this matrix and matrix b.
     *  @param b  the matrix to add (requires leDimensions)
     */
    def += (b: MatrixD) {
        for(
            i <- range1;
            j <- range2
        ) v(i)(j) = v(i)(j) + b.v(i)(j)
    }
    
    /** Add in-place this matrix and scalar x.
     *  @param x  the scalar to add
     */
    def += (x: Double) {
        for(
            i <- range1;
            j <- range2
        ) v(i)(j) = v(i)(j) + x
    }
    
    /** From this matrix subtract matrix b.
     *  @param b  the matrix to subtract (requires leDimensions)
     */
    def - (b: MatrixD): MatrixD = {
        val c = new MatrixD(dim1, dim2)
        for(
            i <- range1;
            j <- range2
        ) c.v(i)(j) = v(i)(j) - b.v(i)(j)
        c
    }
    
    /** From this matrix subtract scalar x.
     *  @param x  the scalar to subtract
     */
    def - (x: Double): MatrixD = {
        val c = new MatrixD(dim1, dim2)
        for(
            i <- c.range1;
            j <- c.range2
        ) c.v(i)(j) = v(i)(j) - x
        c
    }
    
    /** From this matrix subtract in-place matrix b.
     *  @param b  the matrix to subtract (requires leDimensions)
     */
    def -= (b: MatrixD) {
        for(
            i <- range1;
            j <- range2
        ) v(i)(j) = v(i)(j) - b.v(i)(j)
    }
    
    /** From this matrix subtract in-place scalar x.
     *  @param x  the scalar to subtract
     */
    def -= (x: Double) {
        for(
            i <- range1;
            j <- range2
        ) v(i)(j) = v(i)(j) - x
    }
    
    /** Multiply this matrix by matrix b, transposing b to improve efficiency.
     *  Use 'times' method to skip the transpose step.
     *  @param b  the matrix to multiply by (requires sameCrossDimensions)
     */
    def * (b: MatrixD): MatrixD = {
        if(dim2 != b.dim1)
            flaw("*", "matrix * matrix - incompatible cross dimensions")

        val c = new MatrixD(dim1, b.dim2)
        // transpose the b matrix
        val bt = b.t
        for(
            i <- range1;
            j <- c.range2
        ) {
            var sum = 0.0
            for(k <- range2)
                sum += v(i)(k) * bt.v(j)(k)
            c.v(i)(j) = sum
        }
        c
    }
    
    /** Multiply this matrix by vector u.
     *  @param u  the vector to multiply by
     */
    def * (u: VectorD): VectorD = {
        if(dim2 != u.dim)
            flaw("*", "matrix * vector - incompatible cross dimensions")

        val c = new VectorD(dim1)
        for(i <- range1) {
            var sum = 0.0
            for(k <- range2)
                sum += v(i)(k) * u(k)
            c(i) = sum
        }
        c
    }
    
    /** Multiply this matrix by scalar x.
     *  @param x  the scalar to multiply by
     */
    def * (x: Double): MatrixD = {
        val c = new MatrixD(dim1, dim2)
        for(
            i <- range1;
            j <- range2
        ) c.v(i)(j) = v(i)(j) * x
        c
    }
    
    /** Multiply in-place this matrix by matrix b, transposing b to improve
     *  efficiency.  Use 'times_ip' method to skip the transpose step.
     *  @param b  the matrix to multiply by (requires square and sameCrossDimensions)
     */
    def *= (b: MatrixD) {
        if(! b.isSquare)
            flaw("*=", "matrix b must be square")
        if(dim2 != b.dim1)
            flaw("*=", "matrix *= matrix - incompatible cross dimensions")

        // use the transpose of b
        val bt = b.t
        for(i <- range1) {
            // save ith row so not overwritten
            val row_i = new VectorD(dim2)
            // copy values from ith row of this matrix
            for(j <- range2)
                row_i(j) = v(i)(j)
            for(j <- range2) {
                var sum = 0.0
                for(k <- range2)
                    sum += row_i(k) * bt.v(j)(k)
                v(i)(j) = sum
            }
        }
    }
    
    /** Multiply in-place this matrix by scalar x.
     *  @param x  the scalar to multiply by
     */
    def *= (x: Double) {
        for(
            i <- range1;
            j <- range2
        ) v(i)(j) = v(i)(j) * x
    }
    
    /** Multiply this matrix by matrix b without first transposing b.
     *  @param b  the matrix to multiply by (requires sameCrossDimensions)
     */
    def times(b: MatrixD): MatrixD = {
        if (dim2 != b.dim1)
            flaw("times", "matrix * matrix - incompatible cross dimensions")

        val c = new MatrixD(dim1, b.dim2)
        for(
            i <- range1;
            j <- c.range2
        ) {
            var sum = 0.0
            for(k <- range2)
                sum += v(i)(k) * b.v(k)(j)
            c.v(i)(j) = sum
        }
        c
    }
    
    /** Multiply in-place this matrix by matrix b without first transposing b.
     *  If b and this reference the same matrix (b == this), a copy of the this
     *  matrix is made.
     *  @param b  the matrix to multiply by (requires square and sameCrossDimensions)
     */
    def times_ip(b: MatrixD) {
        if(!b.isSquare)
            flaw("times_ip", "matrix b must be square")
        if(dim2 != b.dim1)
            flaw("times_ip", "matrix * matrix - incompatible cross dimensions")

        val bb = if(b == this) new MatrixD(this) else b
        for(i <- range1) {
            // save ith row so not overwritten
            val row_i = new VectorD(dim2)
            // copy values from ith row of this matrix
            for(j <- range2)
                row_i(j) = v(i)(j)
            for(j <- range2) {
                var sum = 0.0
                for(k <- range2)
                    sum += row_i(k) * bb.v(k)(j)
                v(i)(j) = sum
            }
        }
    }
    
    /** Multiply this matrix by matrix b using dot product (concise solution).
     *  @param b  the matrix to multiply by (requires sameCrossDimensions)
     */
    def times_d(b: Matrix): MatrixD = {
        if(dim2 != b.dim1)
            flaw("*", "matrix * matrix - incompatible cross dimensions")

        val c = new MatrixD(dim1, b.dim2)
        for(
            i <- range1;
            j <- c.range2
        ) c.v(i)(j) = this(i) dot b.col(j)
        c
    }
    
    /** Multiply this matrix by matrix b using the Strassen matrix multiplication
     *  algorithm.  Both matrices (this and b) must be square.  Although the
     *  algorithm is faster than the traditional cubic algorithm, its requires
     *  more memory and is often less stable (due to round-off errors).
     *  FIX:  could be made more efficient using a virtual slice (vslice) method.
     *  @see http://en.wikipedia.org/wiki/Strassen_algorithm
     *  @param b  the matrix to multiply by (it has to be a square matrix)
     */
    def times_s(b: MatrixD): MatrixD = {
        if(dim2 != b.dim1)
            flaw("*", "matrix * matrix - incompatible cross dimensions")

        // allocate result matrix
        val c = new MatrixD(dim1, dim1)
        // half dim1
        var d = dim1 / 2
        // if not even, increment by 1
        if (d + d < dim1) d += 1
        // equals dim1 if even, else dim1 + 1
        val evenDim = d + d
        
        // decompose to blocks (use vslice method if available)
        val a11 = slice(0, d, 0, d)
        val a12 = slice(0, d, d, evenDim)
        val a21 = slice(d, evenDim, 0, d)
        val a22 = slice(d, evenDim, d, evenDim)
        val b11 = b.slice(0, d, 0, d)
        val b12 = b.slice(0, d, d, evenDim)
        val b21 = b.slice(d, evenDim, 0, d)
        val b22 = b.slice(d, evenDim, d, evenDim)
        
        // compute intermediate sub-matrices
        val p1 = (a11 + a22) * (b11 + b22)
        val p2 = (a21 + a22) * b11
        val p3 = a11 * (b12 - b22)
        val p4 = a22 * (b21 - b11)
        val p5 = (a11 + a12) * b22
        val p6 = (a21 - a11) * (b11 + b12)
        val p7 = (a12 - a22) * (b21 + b22)
        
        for(
            i <- c.range1;
            j <- c.range2
        ) {
            c.v(i)(j) = if (i < d && j < d)  p1.v(i)(j) + p4.v(i)(j)- p5.v(i)(j) + p7.v(i)(j)
                   else if (i < d)           p3.v(i)(j-d) + p5.v(i)(j-d)
                   else if (i >= d && j < d) p2.v(i-d)(j) + p4.v(i-d)(j)
                   else                      p1.v(i-d)(j-d) - p2.v(i-d)(j-d) + p3.v(i-d)(j-d) + p6.v(i-d)(j-d)
        }
        c
    }
    
    /** Multiply this matrix by vector u to produce another matrix (a_ij * u_j)
     *  @param u  the vector to multiply by
     */
    def ** (u: VectorD): MatrixD = {
        val c = new MatrixD(dim1, dim2)
        for(
            i <- range1;
            j <- range2
        ) c.v(i)(j) = v(i)(j) * u(j)
        c
    }
    
    /** Multiply in-place this matrix by vector u to produce another matrix (a_ij * u_j)
     *  @param u  the vector to multiply by
     */
    def **= (u: VectorD) {
        for(
            i <- range1;
            j <- range2
        ) v(i)(j) = v(i)(j) * u(j)
    }
    
    /** Divide this matrix by scalar x.
     *  @param x  the scalar to divide by
     */
    def / (x: Double): MatrixD = {
        val c = new MatrixD(dim1, dim2)
        for(
            i <- range1;
            j <- range2
        ) c.v(i)(j) = v(i)(j) / x
        c
    }
    
    /** Divide in-place this matrix by scalar x.
     *  @param x  the scalar to divide by
     */
    def /= (x: Double) {
        for(
            i <- range1;
            j <- range2
        ) v(i)(j) = v(i)(j) / x
    }
    
    /** Raise this matrix to the pth power (for some integer p >= 2).
     *  Caveat: should be replaced by a divide and conquer algorithm.
     *  @param p  the power to raise this matrix to
     */
    def ~^ (p: Int): MatrixD = {
        if(p < 2)
            flaw("~^", "p must be an integer >= 2")
        if(!isSquare)
            flaw ("~^", "only defined on square matrices")

        val c = new MatrixD(dim1, dim1)
        for(
            i <- range1;
            j <- range1
        ) {
            var sum = 0.0
            for(k <- range1)
                sum += v(i)(k) * v(k)(j)
            c.v(i)(j) = sum
        }
        if(p > 2)
            c ~^ (p-1)
        else
            c
    }
    
    /** Find the maximum element in this matrix.
     *  @param e  the ending row index (exclusive) for the search
     */
    def max(e: Int = dim1): Double = {
        var x = v(0).max
        for(
            i <- 1 until e
            if v(i).max > x
        ) x = v(i).max
        x
    }
    
    /** Find the minimum element in this matrix.
     *  @param e  the ending row index (exclusive) for the search
     */
    def min(e: Int = dim1): Double = {
        var x = v(0).min
        for(
            i <- 1 until e
            if v(i).min < x
        ) x = v(i).min
        x
    }

    /** Decompose this matrix into the product of upper and lower triangular
     *  matrices (l, u) using the LU Decomposition algorithm.  This version uses
     *  no partial pivoting.
     */
    def lud_npp: (MatrixD, MatrixD) = {
        // lower triangular matrix
        val l = new MatrixD(dim1, dim2)
        // upper triangular matrix (a copy of this)
        val u = new MatrixD(this)

        for(i <- u.range1) {
            val pivot = u.v(i)(i)
            if(pivot == 0.0)
                flaw("lud_npp", "use lud since you have a zero pivot")
            l.v(i)(i) = 1.0
            for(j <- i + 1 until u.dim2)
                l.v(i)(j) = 0.0
            for(k <- i + 1 until u.dim1) {
                val mul = u.v(k)(i) / pivot
                l.v(k)(i) = mul
                for(j <- u.range2)
                    u.v(k)(j) = u.v(k)(j) - mul * u.v(i)(j)
            }
        }
        (l, u)
    }
    
    /** Decompose this matrix into the product of lower and upper triangular
     *  matrices (l, u) using the LU Decomposition algorithm.  This version uses
     *  partial pivoting.
     */
    def lud: (MatrixD, MatrixD) = {
        // lower triangular matrix
        val l = new MatrixD(dim1, dim2)
        // upper triangular matrix (a copy of this)
        val u = new MatrixD(this)

        for(i <- u.range1) {
            var pivot = u.v(i)(i)
            if(pivot == 0.0) {
                // find the maxiumum element below pivot
                val k = partialPivoting(u, i)
                // swap rows i and k from column k
                swap(u, i, k, i)
                // reset the pivot
                pivot = u.v(i)(i)
            }
            l.v(i)(i) = 1.0
            for(j <- i + 1 until u.dim2)
                l.v(i)(j) = 0.0
            for(k <- i + 1 until u.dim1) {
                val mul = u.v(k)(i) / pivot
                l.v(k)(i) = mul
                for(j <- u.range2)
                    u.v(k)(j) = u.v(k)(j) - mul * u.v(i)(j)
            }
        }
        (l, u)
    }
    
    /** Decompose in-place this matrix into the product of lower and upper triangular
     *  matrices (l, u) using the LU Decomposition algorithm.  This version uses
     *  partial pivoting.
     */
    def lud_ip: (MatrixD, MatrixD) = {
        // lower triangular matrix
        val l = new MatrixD (dim1, dim2)
        // upper triangular matrix (this)
        val u = this

        for(i <- u.range1) {
            var pivot = u.v(i)(i)
            if(pivot == 0.0) {
                // find the maxiumum element below pivot
                val k = partialPivoting(u, i)
                // swap rows i and k from column k
                swap(u, i, k, i)
                // reset the pivot
                pivot = u.v(i)(i)
            }
            l.v(i)(i) = 1.0
            for(j <- i + 1 until u.dim2)
                l.v(i)(j) = 0.0
            for(k <- i + 1 until u.dim1) {
                val mul = u.v(k)(i) / pivot
                l.v(k)(i) = mul
                for(j <- u.range2)
                    u.v(k)(j) = u.v(k)(j) - mul * u.v(i)(j)
            }
        }
        (l, u)
    }
    
    /** Use partial pivoting to find a maximal non-zero pivot and return its row
     *  index, i.e., find the maximum element (k, i) below the pivot (i, i).
     *  @param a  the matrix to perform partial pivoting on
     *  @param i  the row and column index for the current pivot
     */
    private def partialPivoting(a: MatrixD, i: Int): Int = {
        // initially set to the pivot
        var max  = a.v(i)(i)
        // initially the pivot row
        var kMax = i

        for(
            k <- i + 1 until a.dim1
            if abs (a.v(k)(i)) > max
        ) {
            max  = abs (a.v(k)(i))
            kMax = k
        }

        if(kMax == i)
            flaw("partialPivoting", "unable to find a non-zero pivot for row " + i)

        kMax
    }
    
    /** Swap the elements in rows i and k starting from column col.
     *  @param a    the matrix containing the rows to swap
     *  @param i    the higher row  (e.g., contains a zero pivot)
     *  @param k    the lower row (e.g., contains max element below pivot)
     *  @param col  the starting column for the swap
     */
    private def swap(a: MatrixD, i: Int, k: Int, col: Int) {
        for(j <- col until a.dim2) {
            val tmp = a.v(k)(j)
            a.v(k)(j) = a.v(i)(j)
            a.v(i)(j) = tmp
        }
    }
    
    /** Solve for x in the equation l*u*x = b (see lud above).
     *  @param l  the lower triangular matrix
     *  @param u  the upper triangular matrix
     *  @param b  the constant vector
     */
    def solve(l: Matrix, u: Matrix, b: VectorD): VectorD = {
        val y  = new VectorD(l.dim2)
        // solve for y in l*y = b
        for (k <- 0 until y.dim) {
            y(k) = b(k) - (l(k) dot y)
        }

        val x = new VectorD(u.dim2)
        // solve for x in u*x = y
        for(k <- x.dim - 1 to 0 by -1)
            x(k) = (y(k) - (u(k) dot x)) / u(k, k)
        x
    }
    
    /** Solve for x in the equation l*u*x = b (see lud above).
     *  @param lu  the lower and upper triangular matrices
     *  @param b   the constant vector
     */
    def solve(lu: (Matrix, Matrix), b: VectorD): VectorD = solve(lu._1, lu._2, b)
    
    /** Solve for x in the equation a*x = b where a is this matrix (see lud above).
     *  @param b  the constant vector.
     */
    def solve(b: VectorD): VectorD = solve (lud, b)
    
    /** Combine this matrix with matrix b, placing them along the diagonal and
     *  filling in the bottom left and top right regions with zeroes; [this, b].
     *  @param b  the matrix to combine with this matrix
     */
    def diag(b: MatrixD): MatrixD = {
        val m = dim1 + b.dim1
        val n = dim2 + b.dim2
        val c = new MatrixD(m, n)

        for(
            i <- 0 until m;
            j <- 0 until n
        ) {
            c.v(i)(j) = if (i <  dim1 && j <  dim2) v(i)(j)
                   else if (i >= dim1 && j >= dim2) b.v(i-dim1)(j-dim2)
                   else 0.0
        }
        c
    }
    
    /** Form a matrix [Ip, this] where Ip is a p by p identity matrix, by
     *  positioning the two matrices Ip and this along the diagonal.
     *  Fill the rest of matrix with zeroes.
     *  @param p  the size of identity matrix Ip
     */
    def diag(p: Int): MatrixD = {
        // new number of rows
        val m = dim1 + p
        // new number of columns
        val n = dim1 + p
        val c = new MatrixD(m, n)
        for(i <- 0 until p)
            c.v(i)(i) = 1.0
        c(p until m, p until n) = this
        c
    }
    
    /** Form a matrix [Ip, this, Iq] where Ir is a r by r identity matrix, by
     *  positioning the three matrices Ip, this and Iq along the diagonal.
     *  Fill the rest of matrix with zeroes.
     *  @param p  the size of identity matrix Ip
     *  @param q  the size of identity matrix Iq
     */
    def diag(p: Int, q: Int): MatrixD = {
        if(!isSymmetric)
            flaw("diag", "this matrix must be symmetric")

        val n = dim1 + p + q 
        val c = new MatrixD(n, n)

        for(
            i <- 0 until n;
            j <- 0 until n
        ) {
            c.v(i)(j) = {
                if (i < p || i > p + dim1)
                    if (i == j)
                        1.0
                    else
                        0.0
                else
                    v(i-p)(j-p)
            }
        }
        c
    }
    
    /** Get the kth diagonal of this matrix.  Assumes dim2 >= dim1.
     *  @param k  how far above the main diagonal, e.g., (-1, 0, 1) for (sub, main, super)
     */
    def getDiag(k: Int = 0): VectorD = {
        val mm = dim1 - abs(k)
        val c = new VectorD(mm)
        for(i <- 0 until mm)
            c(i) = v(i)(i+k)
        c
    }

    /** Set the kth diagonal of this matrix to the vector u.  Assumes dim2 >= dim1.
     *  @param u  the vector to set the diagonal to
     *  @param k  how far above the main diagonal, e.g., (-1, 0, 1) for (sub, main, super)
     */
    def setDiag(u: VectorD, k: Int = 0) {
        val mm = dim1 - abs(k)
        for(i <- 0 until mm)
            v(i)(i+k) = u(i)
    }
    
    /** Set the main diagonal of this matrix to the scalar x.  Assumes dim2 >= dim1.
     *  @param x  the scalar to set the diagonal to
     */
    def setDiag(x: Double) {
        for (i <- range1)
            v(i)(i) = x
    }
    
    /** Invert this matrix (requires a squareMatrix) and does not use partial pivoting.
     */
    def inverse_npp: MatrixD = {
        // copy this matrix into b
        val b = new MatrixD(this)
        // let c represent the augmentation
        val c = new MatrixD(dim1, 1.0, 0.0)

        for(i <- b.range1) {
            val pivot = b.v(i)(i)
            if(pivot == 0.0)
                flaw("inverse_npp", "use inverse since you have a zero pivot")
            for(j <- b.range2) {
                b.v(i)(j) /= pivot
                c.v(i)(j) /= pivot
            }
            for(
                k <- 0 until b.dim1
                if k != i
            ) {
                val mul = b.v(k)(i)
                for(j <- b.range2) {
                    b.v(k)(j) -= mul * b.v(i)(j)
                    c.v(k)(j) -= mul * c.v(i)(j)
                }
            }
        }
        c
    }
    
    /** Invert this matrix (requires a squareMatrix) and use partial pivoting.
     */
    def inverse: MatrixD = {
        // copy this matrix into b
        val b = new MatrixD(this)
        // let c represent the augmentation
        val c = new MatrixD(dim1, 1.0, 0.0)

        for(i <- b.range1) {
            var pivot = b.v(i)(i)
            if(pivot == 0.0) {
                // find the maxiumum element below pivot
                val k = partialPivoting (b, i)
                // in b, swap rows i and k from column i
                swap (b, i, k, i)
                // in c, swap rows i and k from column 0
                swap (c, i, k, 0)
                // reset the pivot
                pivot = b.v(i)(i)
            }
            for(j <- b.range2) {
                b.v(i)(j) /= pivot
                c.v(i)(j) /= pivot
            }
            for(
                k <- 0 until dim1
                if k != i
            ) {
                val mul = b.v(k)(i)
                for(j <- b.range2) {
                    b.v(k)(j) -= mul * b.v(i)(j)
                    c.v(k)(j) -= mul * c.v(i)(j)
                }
            }
        }
        c
    }

    /** Invert in-place this matrix (requires a squareMatrix) and uses partial pivoting.
     */
    def inverse_ip: MatrixD = {
        // use this matrix for b
        val b = this
        // let c represent the augmentation
        val c = new MatrixD(dim1, 1.0, 0.0)

        for (i <- b.range1) {
            var pivot = b.v(i)(i)
            if (pivot == 0.0) {
                // find the maxiumum element below pivot
                val k = partialPivoting (b, i)
                // in b, swap rows i and k from column i
                swap (b, i, k, i)
                // in c, swap rows i and k from column 0
                swap (c, i, k, 0)
                // reset the pivot
                pivot = b.v(i)(i)
            }
            for(j <- b.range2) {
                b.v(i)(j) /= pivot
                c.v(i)(j) /= pivot
            }
            for(
                k <- 0 until dim1
                if k != i
            ) {
                val mul = b.v(k)(i)
                for(j <- b.range2) {
                    b.v(k)(j) -= mul * b.v(i)(j)
                    c.v(k)(j) -= mul * c.v(i)(j)
                }
            }
        }
        c
    }

    
    /** Use Gauss-Jordan reduction on this matrix to make the left part embed an
     *  identity matrix.  A constraint on this m by n matrix is that n >= m.
     */
    def reduce: MatrixD = {
        if(dim2 < dim1)
            flaw("reduce", "requires n (columns) >= m (rows)")

        // copy this matrix into b
        val b = new MatrixD(this)

        for(i <- b.range1) {
            var pivot = b.v(i)(i)
            if(pivot == 0.0) {
                // find the maxiumum element below pivot
                val k = partialPivoting(b, i)
                // in b, swap rows i and k from column i
                swap (b, i, k, i)
                // reset the pivot
                pivot = b.v(i)(i)
            }
            for(j <- b.range2)
                b.v(i)(j) /= pivot
            for(
                k <- 0 until dim1
                if k != i
            ) {
                val mul = b.v(k)(i)
                for(j <- b.range2)
                    b.v(k)(j) -= mul * b.v(i)(j)
            }
        }
        b
    }
    
    /** Use Gauss-Jordan reduction in-place on this matrix to make the left part
     *  embed an identity matrix.  A constraint on this m by n matrix is that n >= m.
     */
    def reduce_ip {
        if(dim2 < dim1)
            flaw("reduce", "requires n (columns) >= m (rows)")

        // use this matrix for b
        val b = this

        for(i <- b.range1) {
            var pivot = b.v(i)(i)
            if(pivot == 0.0) {
                // find the maxiumum element below pivot
                val k = partialPivoting(b, i)
                // in b, swap rows i and k from column i
                swap (b, i, k, i)
                // reset the pivot
                pivot = b.v(i)(i)
            }
            for(j <- b.range2)
                b.v(i)(j) /= pivot
            for(
                k <- 0 until dim1
                if k != i
            ) {
                val mul = b.v(k)(i)
                for(j <- b.range2)
                    b.v(k)(j) -= mul * b.v(i)(j)
            }
        }
    }
    
    /** Clean values in matrix at or below the threshold by setting them to zero.
     *  Iterative algorithms give approximate values and if very close to zero,
     *  may throw off other calculations, e.g., in computing eigenvectors.
     *  @param thres     the cutoff threshold (a small value)
     *  @param relative  whether to use relative or absolute cutoff
     */
    def clean(thres: Double, relative: Boolean = true): MatrixD = {
        // use matrix magnitude or 1
        val s = if(relative) mag else 1.0
        for(
            i <- range1;
            j <- range2
        ) if (abs(v(i)(j)) <= thres * s) v(i)(j) = 0.0
        this
    }

    
    /** Compute the (right) nullspace of this m by n matrix (requires n = m + 1)
     *  by performing Gauss-Jordan reduction and extracting the negation of the
     *  last column augmented by 1.  The nullspace of matrix a is "this vector v
     *  times any scalar s", i.e., a*(v*s) = 0.  The left nullspace of matrix a is
     *  the same as the right nullspace of a.t (a transpose).
     *  FIX: need a more robust algorithm for computing nullspace (@see QRDecomp.scala)
     *  @see http://ocw.mit.edu/courses/mathematics/18-06sc-linear-algebra-fall-2011/ax-b-and-the-four-subspaces
     *  /solving-ax-0-pivot-variables-special-solutions/MIT18_06SCF11_Ses1.7sum.pdf
     */
    def nullspace: VectorD = {
        if(dim2 != dim1 + 1)
            flaw("nullspace", "requires n (columns) = m (rows) + 1")

        reduce.col(dim2 - 1) * -1.0 ++ 1.0
    }
    
    /** Compute the (right) nullspace in-place of this m by n matrix (requires n = m + 1)
     *  by performing Gauss-Jordan reduction and extracting the negation of the
     *  last column augmented by 1.  The nullspace of matrix a is "this vector v
     *  times any scalar s", i.e., a*(v*s) = 0.  The left nullspace of matrix a is
     *  the same as the right nullspace of a.t (a transpose).
     */
    def nullspace_ip: VectorD = {
        if(dim2 != dim1 + 1)
            flaw("nullspace", "requires n (columns) = m (rows) + 1")

        reduce_ip
        col(dim2 - 1) * -1.0 ++ 1.0
    }
    
    /** Compute the trace of this matrix, i.e., the sum of the elements on the
     *  main diagonal.  Should also equal the sum of the eigenvalues.
     *  @see Eigen.scala
     */
    def trace: Double = {
        if(!isSquare)
            flaw("trace", "trace only works on square matrices")

        var sum = 0.0
        for(i <- range1)
            sum += v(i)(i)
        sum
    }
    
    /** Compute the sum of this matrix, i.e., the sum of its elements.
     */
    def sum: Double = {
        var sum = 0.0
        for(
            i <- range1;
            j <- range2
        ) sum += v(i)(j)
        sum
    }
    
    /** Compute the sum of the lower triangular region of this matrix.
     */
    def sumLower: Double = {
        var sum = 0.0
        for(
            i <- range1;
            j <- 0 until i
        ) sum += v(i)(j)
        sum
    }
    
    /** Compute the determinant of this matrix.  The value of the determinant
     *  indicates, among other things, whether there is a unique solution to a
     *  system of linear equations (a nonzero determinant).
     */
    def det: Double = {
        if(!isSquare)
            flaw("det", "determinant only works on square matrices")

        var sum = 0.0
        var b: MatrixD = null
        for(j <- range2) {
            // the submatrix that excludes row 0 and column j
            b = sliceExclude(0, j)
            sum += (if (j % 2 == 0) v(0)(j) * (if (b.dim1 == 1) b.v(0)(0) else b.det)
                    else           -v(0)(j) * (if (b.dim1 == 1) b.v(0)(0) else b.det))
        }
        sum
    }
    
    /** Check whether this matrix is rectangular (all rows have the same number
     *  of columns).
     */
    def isRectangular: Boolean = {
        for(
            i <- range1
            if v(i).length != dim2
        ) return false
        true
    }
    
    /** Check whether this matrix is nonnegative (has no negative elements).
     */
    def isNonnegative: Boolean = {
        for(
            i <- range1;
            j <- range2
            if v(i)(j) < 0.0
        ) return false
        true
    }
    
    /** Convert this matrix to a string.
     */
    override def toString: String =  {
        var sb = new StringBuilder("\nMatrixD(")
        for(i <- range1) {
            sb.append(this(i).toString)
            sb.append(if (i < dim1 - 1) ",\n\t" else ")")
        }
        sb.mkString
    }
  
}


/** The MatrixD companion object provides operations for MatrixD that don't require
 *  'this' (like static methods in Java).  Typically used to form matrices from
 *  vectors.
 */
object MatrixD extends Error
{
    
    /** Create an n-by-n identity matrix (ones on main diagonal, zeroes elsewhere).
     */
    def eye(n: Int): MatrixD = new MatrixD(n, 1.0, 0.0)

    /** Multiply vector u by matrix a.  Treat u as a row vector.
     *  @param u  the vector to multiply by
     *  @param a  the matrix to multiply by (requires sameCrossDimensions)
     */
    def times(u: VectorD, a: MatrixD): VectorD = {
        if(u.dim != a.dim1)
            flaw("times", "vector * matrix - incompatible cross dimensions")

        val c = new VectorD(a.dim2)
        for(j <- a.range2) {
            var sum = 0.0
            for(k <- a.range1)
                sum += u(k) * a.v(k)(j)
            c(j) = sum
        }
        c
    }
    
    /** Compute the outer product of vector x and vector y.  The result of the
     *  outer product is a matrix where c(i, j) is the product of i-th element
     *  of x with the j-th element of y.
     *  @param x  the first vector
     *  @param y  the second vector
     */
    def outer(x: VectorD, y: VectorD): MatrixD = {
        val c = new MatrixD(x.dim, y.dim)
        for(
            i <- 0 until x.dim;
            j <- 0 until y.dim
        ) c(i, j) = x(i) * y(j)
        c
    }
    
    /** Form a matrix from two vectors, row-wise.
     *  @param x  the first vector -> row 0
     *  @param y  the second vector -> row 1
     */
    def form_rw(x: VectorD, y: VectorD): MatrixD = {
        if(x.dim != y.dim)
            flaw("form_rw", "dimensions of x and y must be the same")

        val cols = x.dim
        val c = new MatrixD(2, cols)
        c(0) = x
        c(1) = y
        c
    }
    
    /** Form a matrix from scalar and a vector, row-wise.
     *  @param x  the first scalar -> row 0 (repeat scalar)
     *  @param y  the second vector -> row 1
     */
    def form_rw(x: Double, y: VectorD): MatrixD = {
        val cols = y.dim
        val c = new MatrixD(2, cols)
        for(j <- 0 until cols)
            c(0, j) = x
        c(1) = y
        c
    }
    
    /** Form a matrix from a vector and a scalar, row-wise.
     *  @param x  the first vector -> row 0
     *  @param y  the second scalar -> row 1 (repeat scalar)
     */
    def form_rw(x: VectorD, y: Double): MatrixD = {
        val cols = x.dim
        val c = new MatrixD(2, cols)
        c(0) = x
        for(j <- 0 until cols)
            c(1, j) = y
        c
    }
    
    /** Form a matrix from two vectors, column-wise.
     *  @param x  the first vector -> column 0
     *  @param y  the second vector -> column 1
     */
    def form_cw(x: VectorD, y: VectorD): MatrixD = {
        if(x.dim != y.dim)
            flaw("form_cw", "dimensions of x and y must be the same")

        val rows = x.dim
        val c = new MatrixD(rows, 2)
        c.setCol(0, x)
        c.setCol(1, y)
        c
    }
    
    /** Form a matrix from a scalar and a vector, column-wise.
     *  @param x  the first scalar -> column 0 (repeat scalar)
     *  @param y  the second vector -> column 1
     */
    def form_cw(x: Double, y: VectorD): MatrixD = {
        val rows = y.dim
        val c = new MatrixD(rows, 2)
        for(i <- 0 until rows)
            c(i, 0) = x
        c.setCol(1, y)
        c
    }
    
    /** Form a matrix from a vector and a scalar, column-wise.
     *  @param x  the first vector -> column 0
     *  @param y  the second scalar -> column 1 (repeat scalar)
     */
    def form_cw(x: VectorD, y: Double): MatrixD = {
        val rows = x.dim
        val c = new MatrixD(rows, 2)
        c.setCol(0, x)
        for(i <- 0 until rows)
            c(i, 1) = y
        c
    }

}
