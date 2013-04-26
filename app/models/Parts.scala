package models

/**
 *  superclass for CSs and TFs (coding sequences and transcription factors)
 */ 
abstract class Part

/**
 * class for coding sequences
 * the k2 and d tuple govern the speed at which this CS is transcribed into mRNA
 * and the speed at which its mRNA decays
 */
case class Gene(k2:Double, d: (Double,Double), initialConc: Double) extends Part

/**
 *  class for NOT gates
 *  input is the CS that produces the protein that activates this gate and causes
 *  the output to be repressed; output is the output CS that will be repressed by
 *  this TF
 *  the other parameters determine the specific type of TF
 */
case class NotGate(input: Gene, output: Gene, k1: Double, Km: Double, n: Int) extends Part

/**
 *  class for AND gates
 *  the difference with NOT gates is what kind of ODE function will be generated
 *  for this class and the number of inputs
 */
case class AndGate(input: (Gene, Gene), output: Gene, k1: Double, Km: Double, n: Int) extends Part
