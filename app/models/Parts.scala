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
case class CodingSeq(var k2:Double, var d: (Double,Double), var concentration: (Double, Double), var linksTo: Option[Gate]) extends Part{
    
	  def setParams(name: String) =  {
	    val params = DB.withConnection { implicit connection =>
	      SQL(
	        """
	         select * from cds where 
	         name = {name};
	        """
	      ).on(
	        'name -> name
	      ).apply().head
	    }
	    k2 = params[Double]("K2");
	    d = (params[Double]("D1"),params[Double]("D2"))
	  }
}

/**
 *  class for NOT gates
 *  input is the CS that produces the protein that activates this gate and causes
 *  the output to be repressed; output is the output CS that will be repressed by
 *  this TF
 *  the other parameters determine the transcription rate of the output
 */
case class NotGate(input: CodingSeq, output: CodingSeq, k1: Double, Km: Double, n: Int) extends Gate{
  input.linksTo = Some(this)
}

/**
 *  class for AND gates
 *  the difference with NOT gates is what kind of ODE function will be generated
 *  for this class and the number of inputs
 */
case class AndGate(input: (CodingSeq, CodingSeq), output: CodingSeq, k1: Double, Km: Double, n: Int) extends Gate{
  input._1.linksTo = Some(this)
  input._2.linksTo = Some(this)
}
