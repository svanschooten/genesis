package models

case class Protein( Name : String, Valk_2 : Double, Vald_1 : Double, Vald_2 : Double ) {
  def getName = Name
  def getValk = Valk_2
  def getVald1 = Vald_1
  def getVald2 = Vald_2
}

import sorm._
object Db extends Instance(
  entities = Set(Entity[Protein]()),
  url = "jdbc:h2:genesis",
  user = "sa"
)
