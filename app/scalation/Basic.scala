package scalation

// natural logarithm
import math.log

/** This object provides additional methods for computing logarithms and a
 *  method for transforming Booleans into Ints.
 */
object Basic {

    /** Return 1 if the condition is true else 0.
     *  @param cond  the condition to evaluate
     */
    def oneIf(cond: Boolean): Int = if(cond) 1 else 0

    /** The natural log of 2
     */
    val log_of_2  = log(2.0)

    /** The natural log of 10
     */
    val log_of_10 = log(10.0)

    /** Compute the log of x base 2
     *  @param x  the value whose log is sought
     */
    def log2(x: Double): Double = log(x) / log_of_2

    /** Compute the log of x base 2
     *  @param x  the value whose log is sought
     */
    def log10(x: Double): Double = log(x) / log_of_10

    /** Compute the log of x base b
     *  @param b  the base of the logarithm
     *  @param x  the value whose log is sought
     */
    def logb(b: Double, x: Double): Double = log(x) / log (b)

}
