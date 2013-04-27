package models

/**
 *  superclass for CSs and TFs (coding sequences and transcription factors)
 */ 
abstract class Part

/**
 * class for coding sequences
 * k2 and the d tuple govern the speed at which this CS is translated into protein
 * and the speed at which its mRNA and the protein decays, respectively
 * (the constants for the generation of the mRNA are stored in the TFs, see below)
 * concentration is the current contentration of this CS as ([mRNA], [Protein])
 * linksTo is the gate this sequence links to; it is optional to enable the chain to end
 * finally, read is a boolean that tells the solver whether the concentration of this
 * coding sequence is already updated to this round's concentration
 */
case class CodingSeq(k2:Double, d: (Double,Double), var concentration: (Double, Double), linksTo: Option[Part], var ready: Boolean = false) extends Part {
    // the next link cannot be a CS
    // unfortunately this check is impossible due to type erasure (any hints would be appreciated)
    //require(!linksTo.isInstanceOf[Option[CodingSeq]])
}

/**
 *  class for NOT gates
 *  input is the CS that produces the protein that activates this gate and causes
 *  the output to be repressed; output is the output CS that will be repressed by
 *  this TF
 *  the other parameters determine the transcription rate of the output
 */
case class NotGate(input: CodingSeq, output: CodingSeq, k1: Double, Km: Double, n: Int) extends Part

/**
 *  class for AND gates
 *  the difference with NOT gates is what kind of ODE function will be generated
 *  for this class and the number of inputs
 */
case class AndGate(input: (CodingSeq, CodingSeq), output: CodingSeq, k1: Double, Km: Double, n: Int) extends Part
