package models

/**
 *  superclass for coding sequences and TFs
 */ 
sealed abstract class Part

/**
 * class for coding sequences
 * the k2 and d tuple govern the speed at which this CS is transcribed into mRNA
 * and the speed at which its mRNA decays
 */
case class Gene(val k2:Double, val d: (Double,Double)) extends Part

/**
 *  class for NOT gates
 *  only include output gene; input is not relevant: only input concentrations will be fed to the ode function
 *  the other parameters determine the specific type of not gate (i.e. the specific TF)
 */
case class NotGate(val output: Gene, val k1: Double, val Km: Double, val n: Int) extends Part

/**
 *  class for AND gates
 *  the only difference with NOT gates is what kind of ODE function will be generated for this class
 */
case class AndGate(val output: Gene, val k1: Double, val Km: Double, val n: Int) extends Part

object Part {

    def parseProteinChain(chain: ProteinChain): List[Part] =
        (chain.startNodes :\ List[Part]())((protein: Protein, curlist:List[Part]) => curlist match {
            case Nil => protein match {// first element is copied directly
                case ProteinActivator(_,_,_,ks,ds,km,n) => Gene(ks(1), (ds(0),ds(1))) :: Nil
                case ProteinRepressor(_,_,_,ks,ds,km,n) => Gene(ks(1), (ds(0),ds(1))) :: Nil
                case _ => Nil
            }
            case h::t => h match { //
                case _ => Nil
            }
        }
     )

}

/*
case class ProteinActivator(val name:String, val id: Int, val parents: List[Protein],
    val ks: List[Double], val ds: List[Double], val km: Double = 2.0, val n: Int = 4) extends Protein
case class ProteinRepressor(val name:String, val id: Int, val parents: List[Protein],
    val ks: List[Double], val ds: List[Double], val km: Double = 2.0, val n: Int = 4) extends 
*/