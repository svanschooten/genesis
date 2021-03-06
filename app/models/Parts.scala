package models

import play.api.db.DB
import play.api.Play.current
import anorm._
import anorm.SqlParser._

/**
 *  superclass for CSs and TFs (coding sequences and transcription factors)
 */ 
abstract class Part
abstract class Gate extends Part {
    val output: CodingSeq
	var x : Double = 0
	var y : Double = 0
}
case class Output() extends Gate {
  val output = null
}

/**
 * class for coding sequences
 * k2 and the d tuple govern the speed at which this CS is translated into protein
 * and the speed at which its mRNA and the protein decays, respectively
 * (the constants for the generation of the mRNA are stored in the TFs, see below)
 * concentration is the current contentration of this CS as ([mRNA], [Protein])
 * linksTo is the gate this sequence links to; it is optional to enable the chain to end
 */
object CodingSeq {
	def apply(name: String) = new CodingSeq(name,0,List((0,0)),false)
    def apply(name: String, isInput: Boolean) = new CodingSeq(name,0,List((0,0)),isInput)
    def apply(name: String, libID: Int) = new CodingSeq(name,libID,List((0,0)),false)
    def apply(name: String, libID: Int, isInput: Boolean) = new CodingSeq(name,libID,List((0,0)),isInput)
    def apply(name: String, conc:List[(Double,Double)], isInput: Boolean) = new CodingSeq(name,0,conc,isInput)
    
}

case class CodingSeq(val name: String, val libID: Int, var concentration: List[(Double, Double)], var isInput: Boolean) extends Part{
    // we need at least one concentration
    require(concentration.length > 0)

    private val params = getParams(libID)
    val k2 = params[Double]("K2")
    val d1 = params[Double]("D1")
    val d2 = params[Double]("D2")
    var linkedBy: Option[Gate] = None
    var linksTo: List[Gate] = Nil
    var ready: Boolean = false
    var currentStep: Int = 0

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
    def save(id: Int, isInput: Boolean) {
      //To prevent infinite loops when a cycle is present
      if(ready) return;
      ready = true;
      DB.withConnection { implicit connection =>   
          for(l <- linksTo){
        	  val out = l match {
        	    case _: Output => "output"
        	    case _ => l.output.name
        	  }
		      SQL("insert into cds values({id},{name},{next},{isInput});")
		      .on(
		        'id -> id,
		        'name -> name,
		        'next -> out,
		        'isInput -> isInput
		      ).executeUpdate()
          }
		  
		  linkedBy match {
			case Some(gate) => {
			  val exists = SQL("select * from gates where networkid={id} and output={output}")
					  		.on('id -> id,
					  		    'output -> name)
					  		.apply.size > 0
			  if(!exists){
				  SQL("insert into gates values({id},{output},{x},{y})")
					  .on(
						'id -> id,
						'output -> name,
						'x -> gate.x,
						'y -> gate.y)
					  .executeUpdate()
			  }
			}
			case _ =>
		  }
      }
      for(l <- linksTo){
		  l match{
		    case _: Output =>
	        case x: Gate => x.output.save(id,x.output.isInput)
	        case _ =>
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
case class NotGate(input: CodingSeq, output: CodingSeq, val libID: Int=0) extends Gate{
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
case class AndGate(input: (CodingSeq, CodingSeq), output: CodingSeq, val libID: Int=0) extends Gate{
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
