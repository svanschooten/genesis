package scalation

import math.sqrt
import scalation.DoubleWithExp._

/** The VectorD class stores and operates on Numeric Vectors of base type Double.
 *  It follows the framework of VectorN [T] and is provided for performance.
 *  @param dim  the dimension/size of the vector
 *  @param v    the 1D array used to store vector elements
 */
class VectorD (val dim: Int, protected var v: Array[Double] = null)
      extends PartiallyOrdered[VectorD] with Serializable {


  if(v == null)
        v = Array.ofDim[Double](dim)
    else if (dim != v.length)
        throw new IllegalArgumentException("Dim doesn't match input's dimension.")

    /** zip two arrays into one, using f to combine them
     * ClassTag is needed to enable the compiler to tell
     * the type of the result properly
     * @param xs  the first array
     * @param ys  the second array
     * @param f  the function that combines the elements
     */
    def zipWith[A,B,C : scala.reflect.ClassTag](xs: Array[A], ys: Array[B], f: (A,B)=> C): Array[C] = (xs,ys).zipped.map(f).toArray

    def update(x: Int,y: Double) { v(x) = y }

    def getConts: List[Double] = v.clone().toList

    override def clone() = new VectorD(dim, v.clone)

    def length: Int = v.length

    def toStringBare: String = v.mkString(" ")

    /** Construct a vector from two or more values (repeated values Double*).
     *  @param u0  the first value
     *  @param u1  the second value
     *  @param u   the rest of the values (zero or more additional values)
     */
    def this(u: Array[Double]) {
        this(u.length, u)
    }

    /** Construct a vector and assign values from vector u.
     *  @param u  the other vector
     */
    def this(u: VectorD) {
        this(u.dim, u.v.clone())
    }

    /** Get this vector's element at the i-th index position. 
     *  @param i  the given index
     */
    def apply(i: Int): Double = v(i)

    /** Get this vector's entire array.
     */
    def apply(): Array[Double] = v

    /** Iterate over the vector element by element.
     *  @param f  the function to apply
     */
    def foreach[U](f: Double => U) {
        v.foreach(f)
    }

    /** Concatenate this vector and vector b.
     *  @param b  the vector to be concatenated
     */
    def ++ (b: VectorD): VectorD = new VectorD(v ++ b.v)

    /** Concatenate this vector and scalar b.
     *  @param b  the scalar to be concatenated
     */
    def ++ (b: Double): VectorD = new VectorD(v ++ Array(b))

    /** Add this vector and vector b.
     *  @param b  the vector to add
     */
    def + (b: VectorD): VectorD =  new VectorD(zipWith(v,b.v,(x:Double, y:Double)=>x+y))

    /** Add this vector and scalar s.
     *  @param s  the scalar to add
     */
    def + (s: Double): VectorD = new VectorD(v.map(_ + s))

    /** Add in-place this vector and vector b.
     *  @param b  the vector to add
     */
    def += (b: VectorD): VectorD = {
        v = zipWith(v,b.v,(x:Double, y:Double)=>x+y)
        this
    }

    /** Add in-place this vector and scalar s.
     *  @param s  the scalar to add
     */
    def += (s: Double): VectorD = {
        v = v.map(_ + s)
        this
    }
 
    /** Return the negative of this vector (unary minus).
     */
    def unary_-(): VectorD = new VectorD(v.map(x => -x))

    /** From this vector subtract vector b.
     *  @param b  the vector to subtract
     */
    def - (b: VectorD): VectorD = new VectorD(zipWith(v,b.v,(x:Double, y:Double)=>x-y))
 
    /** From this vector subtract scalar s.
     *  @param s  the scalar to subtract
     */
    def - (s: Double): VectorD = new VectorD(v.map(_ - s))

    /** From this vector subtract in-place vector b.
     *  @param b  the vector to add
     */
    def -= (b: VectorD): VectorD = {
        v = zipWith(v,b.v,(x:Double, y:Double)=>x-y)
        this
    }

    /** From this vector subtract in-place scalar s.
     *  @param s  the scalar to add
     */
    def -= (s: Double): VectorD = {
        v = v.map(_ - s)
        this
    }
 
    /** Multiply this vector by vector b.
     *  @param b  the vector to multiply by
     */
    def * (b: VectorD): VectorD = new VectorD(zipWith(v,b.v,(x:Double,y:Double)=>x*y))

    /** Multiply this vector by scalar s.
     *  @param s  the scalar to multiply by
     */
    def * (s: Double): VectorD = new VectorD(v.map(_ * s))

    /** Multiply in-place this vector and vector b.
     *  @param b  the vector to add
     */
    def *= (b: VectorD): VectorD = {
        v = zipWith(v,b.v,(x:Double,y:Double)=>x*y)
        this
    }

    /** Multiply in-place this vector and scalar s.
     *  @param s  the scalar to add
     */
    def *= (s: Double): VectorD = {
        v = v.map(_ * s)
        this
    }

    /** Divide this vector by vector b (element-by-element).
     *  @param b  the vector to divide by
     */
    def / (b: VectorD): VectorD = new VectorD(zipWith(v,b.v,(x:Double,y:Double)=>x/y))

    /** Divide this vector by scalar s.
     *  @param s  the scalar to divide by
     */
    def / (s: Double): VectorD = new VectorD(v.map(_ / s))

    /** Divide in-place this vector and vector b.
     *  @param b  the vector to add
     */
    def /= (b: VectorD): VectorD = {
        v = zipWith(v,b.v,(x:Double,y:Double)=>x/y)
        this
    }

    /** Divide in-place this vector and scalar s.
     *  @param s  the scalar to add
     */
    def /= (s: Double): VectorD = {
        v = v.map(_ / s)
        this
    }

    /** Return the vector containing each element of this vector raised to the
     *  s-th power.
     *  @param s  the scalar exponent
     */
    def ~^ (s: Double): VectorD = new VectorD(v.map(_ ~^ s))

    /** Raise each element of this vector to the s-th power.
     *  @param s  the scalar exponent
     */
    def ~^= (s: Double) {
        v = v.map(_ ~^ s)
    }

    /** Return the vector containing the square each element of this vector.
     */
    def sq: VectorD = this * this

    /** Return the vector that is the element-wise absolute value of this matrix.
     */
    def abs: VectorD = new VectorD(v.map(_.abs))

    /** Sum the elements of this vector.
     */
    def sum: Double = v.foldLeft(0.0)(_ + _)

    /** Sum the positive (> 0) elements of this vector.
     */
    def sum_pos: Double = v.filter(_ > 0.0).sum

    /** Normalize this vector so that it sums to one (like a probability vector).
     */
    def normalize: VectorD = this * (1.0/sum)

    /** Compute the dot product (or inner product) of this vector with vector b.
     *  @param b  the other vector
     */
    def dot(b: VectorD): Double = zipWith(v,b.v,(x:Double,y:Double)=>x*y).sum

    /** Compute the Euclidean norm (2-norm) squared of this vector.
     */
    def normSq: Double = this dot this

    /** Compute the Euclidean norm (2-norm) of this vector.
     */
    def norm: Double = sqrt(normSq)

    /** Find the maximum element in this vector.
     *  @param e  the ending index (exclusive) for the search
     */
    def max(e: Int = dim): Double = v.max

    /** Take the maximum of this vector with vector b (element-by element).
     *  @param b  the other vector
     */
    def max(b: VectorD): VectorD = new VectorD(zipWith(v,b.v,(x:Double,y:Double)=>if(x>y) x else y))

    /** Find the minimum element in this vector.
     *  @param e  the ending index (exclusive) for the search
     */
    def min(e: Int = dim): Double = v.min

    /** Take the minimum of this vector with vector b (element-by element).
     *  @param b  the other vector
     */
    def min(b: VectorD): VectorD = new VectorD(zipWith(v,b.v,(x:Double,y:Double)=>if(x<y) x else y))

    /** Find the element with the greatest magnitude in this vector.
     */
    def mag: Double = math.abs(max()) max math.abs(min())

    /** Find the argument maximum of this vector (index of maximum element).
     *  @param e  the ending index (exclusive) for the search
     */
    def argmax(e: Int = dim): Int = {
        var j = 0
        for(
            i <- 0 until e
            if v(i) > v(j)
        ) j = i
        j
    }

    /** Find the argument minimum of this vector (index of minimum element).
     *  @param e  the ending index (exclusive) for the search
     */
    def argmin(e: Int = dim): Int = {
        var j = 0
        for(
            i <- 0 until e
            if v(i) < v(j)
        ) j = i
        j
    }

    /** Determine whether the predicate pred holds for some element in this vector.
     *  @param pred  the predicate to test (e.g., "_ == 5.")
     */
    def exists(pred: (Double) => Boolean): Boolean = v.exists(pred)

    /** Determine whether x is contained in this vector.
     *  @param x  the element to be checked
     */
    def contains(x: Double): Boolean = v contains x

    /** Check whether the other vector is at least as long as this vector.
     *  @param b  the other vector
     */
    def sameDimensions(b: VectorD): Boolean = dim <= b.dim

    /** Check whether this vector is nonnegative (has no negative elements).
     */
    def isNonnegative: Boolean = v.foldLeft(true)((x,y)=>{
        if(!x) false
        else if(y>=0.0) true
        else false})

    /** Compare this vector with vector b.
     *  @param b  the other vector
     */
    def tryCompareTo[B >: VectorD](b: B)
        (implicit view$1: (B) => PartiallyOrdered[B]): Option[Int] = {
        val range = 0 until dim
        var le = true
        var ge = true

        for(i <- range) {
            val b_i = b.asInstanceOf[VectorD](i)
            if(ge && (v(i) compare b_i) < 0)
                ge = false
            else if(le && (v(i) compare b_i) > 0)
                le = false
        }
        if(ge && le)
            Some(0)
        else if(le)
            Some(-1)
        else if(ge)
            Some(1)
        else
            None
    }

    /** Override equals to determine whether vector this equals vector b.
     *  @param b  the vector to compare with this
     */
    override def equals(b: Any): Boolean =
        b.isInstanceOf[VectorD] && (v.deep equals b.asInstanceOf[VectorD].v.deep)

    /** Must also override hashCode to be be compatible with equals.
     */
    override def hashCode: Int = v.deep.hashCode

    /** Convert this vector to a string.
     */
    override def toString: String = "VectorD(" + v.mkString(" ") + ")"
  
}