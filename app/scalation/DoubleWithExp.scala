
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
/** @author  John Miller
 *  @version 1.0
 *  @date    Mon Nov 14 2:34:38 EST 2011
 *  @see     LICENSE (MIT style license file).
 *  @see     http://www.scala-lang.org/node/724
 */

package scalation

//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
/** This class defines an expontiation operator '~^' for Doubles.
 *  @param x  the base
 */
case class DoubleWithExp (x: Double)
{
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Exponentiation operator for scala Doubles (x ~^ y).
     *  @param y  the exponent
     */
    def ~^ (y: Double) = Math.pow (x, y)

} // DoubleWithExp class


//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
/** Implicit conversion from Double to DoubleWithExp allowing '~^' to be applied
 *  to Doubles.
 */
object DoubleWithExp
{
    implicit def doubleWithExp (d: Double) = DoubleWithExp (d)

} // DoubleWithExp object