package models

import play.api.db._
import play.api.Play.current
import anorm._
import anorm.SqlParser._

/**
 *  superclass for CSs and TFs (coding sequences and transcription factors)
 */ 
abstract class Part
abstract class Gate extends Part {
    val output: CodingSeq
}

/**
 * class for coding sequences
 * k2 and the d tuple govern the speed at which this CS is translated into protein
 * and the speed at which its mRNA and the protein decays, respectively
 * (the constants for the generation of the mRNA are stored in the TFs, see below)
 * concentration is the current contentration of this CS as ([mRNA], [Protein])
 * linksTo is the gate this sequence links to; it is optional to enable the chain to end
 */
case class CodingSeq(val name: String, val libID: Int, var concentration: List[(Double, Double)] = List((0,0)), var isInput: Boolean) extends Part{
    private val params = getParams(libID)
    val k2 = params[Double]("K2")
    val d1 = params[Double]("D1")
    val d2 = params[Double]("D2")
    var linkedBy: Option[Gate] = None
    var linksTo: List[Gate] = Nil
    var ready: Boolean = false
    var currentStep: Int = 1

    def curConc: (Double,Double) = {
        if(isInput){
            concentration(currentStep-1)}
        else
            concentration.head
    }
    
    /**
     * Retrieve the k2, d1 and d2 parameters for this CS from the database
     */
	  def getParams(libraryID: Int) = {
	    DB.withConnection { implicit connection =>
	      SQL("select * from cdsparams where name = {name} and libraryid = {libraryID}"
	      ).on(
	        'name -> name,
	        'libraryID -> libraryID
	      ).apply().head
	    }
	  }
    
    /**
     * Save this codingSequence to the database.
     */
    def save(id: Int, isInput: Boolean, canBeInput: Boolean) {
      //To prevent infinite loops when a cycle is present
      if(isInput && !canBeInput) return
      DB.withConnection { implicit connection =>   
          for(l <- linksTo){
        	  val exists = SQL(
		            """
		            select * from cds
		            where networkid={id} and name={name} and next={next}
		            """
		            ).on(
		            	'id -> id,
		            	'name -> name,
		            	'next -> l.output.name
		            ).apply.size > 0
		      if(exists) return
		      SQL("insert into cds values({id},{name},{next},{isInput});")
		      .on(
		        'id -> id,
		        'name -> name,
		        'next -> l.output.name,
		        'isInput -> isInput
		      ).executeUpdate()
          }
	      
	      val concs = concentration.toArray
	      for(i <- 0 to concs.length-1){
		      SQL("insert into concentrations values({id},{name},{time},{c1},{c2});")
		      .on(
		        'id -> id,
		        'name -> name,
		        'time -> i,
		        'c1 -> concs(i)._1,
		        'c2 -> concs(i)._2
		      ).executeUpdate()
	      }
	      
	      for(l <- linksTo){
		      l match{
		        case x: Gate => x.output.save(id,x.output.isInput,false)
		        case _ =>
		      }
	      }
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
case class NotGate(input: CodingSeq, output: CodingSeq, val libID: Int) extends Gate{
  input.linksTo ::= this
  output.linkedBy = Some(this)
  private val params = getParams(libID)
  val k1 = params[Double]("K1")
  val km = params[Double]("KM")
  val n = params[Int]("N")
    
  /**
   * Retrieve the k1, km and n parameters from the database
   */
  def getParams(libraryid: Int) = {
    DB.withConnection { implicit connection =>
      SQL("select * from notparams where input = {input} and libraryid={libraryid}"
      ).on(
        'input -> input.name,
        'libraryid -> libraryid
      ).apply().head
    }
  }
}

/**
 *  class for AND gates
 *  the difference with NOT gates is what kind of ODE function will be generated
 *  for this class and the number of inputs
 */
case class AndGate(input: (CodingSeq, CodingSeq), output: CodingSeq, val libID: Int) extends Gate{
  input._1.linksTo ::= this
  input._2.linksTo ::= this
  output.linkedBy = Some(this)
  
  private val params = getParams(libID)
  val k1 = params[Double]("K1")
  val km = params[Double]("KM")
  val n = params[Int]("N")
  
  /**
   * Retrieve the k1, km and n parameters from the database
   */
  def getParams(libraryid: Int) = {
	    DB.withConnection { implicit connection =>
	      SQL(
	        """
	         select * from andparams where
	         libraryid = {libraryid} AND
	         ((input1 = {input1} AND input2 = {input2})
	         OR (input2 = {input1} AND input1 = {input2}))
	        """
	      ).on(
	        'libraryid -> libraryid,
	        'input1 -> input._1.name,
	        'input2 -> input._2.name
	      ).apply().head
	    }
  }
}
