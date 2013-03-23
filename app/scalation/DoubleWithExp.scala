package scalation

/** This class defines an expontiation operator '~^' for Doubles.
 *  @param x  the base
 */
case class DoubleWithExp(x: Double) {

    /** Exponentiation operator for scala Doubles (x ~^ y).
     *  @param y  the exponent
     */
    def ~^ (y: Double) = Math.pow(x, y)

}

/** Implicit conversion from Double to DoubleWithExp allowing '~^' to be applied
 *  to Doubles.
 */
object DoubleWithExp {

    implicit def doubleWithExp(d: Double) = DoubleWithExp(d)

}
