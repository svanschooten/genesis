package models

import play.api._

abstract class Gate
case class AndGate(in1: Protein, in2: Protein, out: Protein) extends Gate{
	val k1, km, n, k2, d1, d2 = -1
	//val resIn = parseInputData(in1.name, in2.name)
	//k1 = resIn[0]
	//km = resIn[1]
	//n = resIn[2]
	//val resOut = parseOutputData(out.name)
	//k2 = resOut[0]
	//d1 = resOut[1]
	//d2 = resOut[2]
}
case class NotGate(in: Protein, out: Protein) extends Gate{
	val k1, km, n, k2, d1, d2 = -1
	//val resIn = parseInputData(in.name)
	//k1 = resIn[0]
	//km = resIn[1]
	//n = resIn[2]
	//val resOut = parseOutputData(out.name)
	//k2 = resOut[0]
	//d1 = resOut[1]
	//d2 = resOut[2]
}