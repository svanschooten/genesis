package factories

import scalax.io._
import controllers.Application.getSessionHelper
import java.io.File

/**
 * Created with IntelliJ IDEA.
 * User: Stijn
 * Date: 6-6-13
 * Time: 10:09
 * To change this template use File | Settings | File Templates.
 */
object FileFactory {

  private def writeFile(fileName: String, content: String): String = {
    val fileLocation = "public/files/" + fileName
    (new File(fileLocation)).delete()
    val output: Output = Resource.fromFile(fileLocation)
    output.write(content)(Codec.UTF8)
    fileName
  }

  private def makeContent(input: List[(List[(String, Double, Double)], Double)]): String = {
    input.foldLeft(
      input.head._1.foldLeft("t,")(
        (rest: String, tup: (String, Double, Double)) => rest + "mRNA_" + tup._1 + "," + "protein_" + tup._1 + ", "
      )
    )(
      (rest: String, list: (List[(String, Double, Double)], Double)) => rest + "\n" + list._2 + "," + list._1.foldRight("")(
        (tup: (String, Double, Double), rest: String) => tup._2 + "," + tup._3 + "," + rest
      )
    )
  }

  def makeFile(results: List[List[(String, Double, Double)]], finish: Double): String = {
    val fileName: String = getSessionHelper() + ".csv"
    val content = makeContent(results.zip((0.0 to finish by (finish/results.length))))
    writeFile(fileName, content)
  }
}
