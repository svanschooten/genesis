package models

import play.api.db._
import play.api.Play.current
import anorm._
import anorm.SqlParser._

/**
 *  superclass for CSs and TFs (coding sequences and transcription factors)
 */ 
abstract class Part
abstract class Gate extends Part

/**
 * class for coding sequences
 * k2 and the d tuple govern the speed at which this CS is translated into protein
 * and the speed at which its mRNA and the protein decays, respectively
 * (the constants for the generation of the mRNA are stored in the TFs, see below)
 * concentration is the current contentration of this CS as ([mRNA], [Protein])
 * linksTo is the gate this sequence links to; it is optional to enable the chain to end
 */
case class CodingSeq(val name:String, var concentration: (Double, Double)) extends Part{
    private val params = getParams
    val k2 = params[Double]("K2")
    val d1 = params[Double]("D1")
    val d2 = params[Double]("D2")
    var linksTo: Option[Gate] = None
    
    /**
     * Retrieve the k2, d1 and d2 parameters for this CS from the database
     */
	def getParams = {
        DB.withConnection { implicit connection =>
          SQL("select * from cds where name = {name}"
          ).on(
            'name -> name
          ).apply().head
        }
    }
}

/**
 *  class for NOT gates
 *  input is the CS that produces the protein that activates this gate and causes
 *  the output to be repressed; output is the output CS that will be repressed by
 *  this TF
 *  the other parameters determine the transcription rate of the output
 */
case class NotGate(input: CodingSeq, output: CodingSeq) extends Gate{
  input.linksTo = Some(this)
  private val params = getParams
  val k1 = params[Double]("K1")
  val km = params[Double]("KM")
  val n = params[Int]("N")
    
  /**
   * Retrieve the k1, km and n parameters from the database
   */
  def getParams = {
    DB.withConnection { implicit connection =>
      SQL("select * from notgates where input = {input}"
      ).on(
        'input -> input.name
      ).apply().head
    }
  }
}

/**
 *  class for AND gates
 *  the difference with NOT gates is what kind of ODE function will be generated
 *  for this class and the number of inputs
 */
case class AndGate(input: (CodingSeq, CodingSeq), output: CodingSeq) extends Gate{
  input._1.linksTo = Some(this)
  input._2.linksTo = Some(this)
  
  private val params = getParams
  val k1 = params[Double]("K1")
  val km = params[Double]("KM")
  val n = params[Int]("N")
  
  /**
   * Retrieve the k1, km and n parameters from the database
   */
  def getParams = {
	    DB.withConnection { implicit connection =>
	      SQL(
	        """
	         select * from andgates where 
	         (input1 = {input1} AND input2 = {input2})
	         OR (input2 = {input1} AND input1 = {input2})
	        """
	      ).on(
	        'input1 -> input._1.name,
	        'input2 -> input._2.name
	      ).apply().head
	    }
	    
	  }
}
