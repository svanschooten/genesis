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

  private def makeContent(input: List[List[(String, Double, Double)]]): String = {
    input.foldLeft(
      input.head.foldRight("")(
        (tup: (String, Double, Double), rest: String) => tup._1 + "," + rest
      )
    )(
      (rest: String, list: List[(String, Double, Double)]) => rest + "\n" + list.foldRight("")(
        (tup: (String, Double, Double), rest: String) => tup._2 + "," + tup._3 + "," + rest
      )
    )
  }

  def makeFile(results: List[List[(String, Double, Double)]]): String = {
    val fileName: String = getSessionHelper() + ".csv"
    val content = makeContent(results)
    writeFile(fileName, content)
  }
}
