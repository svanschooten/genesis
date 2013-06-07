package test

import org.specs2.mutable._
import org.openqa.selenium.WebDriver
import org.openqa.selenium._
import org.openqa.selenium.firefox._
import java.util.concurrent.TimeUnit
import org.fest.assertions.Assertions._
import junit.framework.Assert._
import org.openqa.selenium.interactions._
import org.openqa.selenium._
import org.openqa.selenium.internal.Locatable
import org.openqa.selenium.interactions.internal.Coordinates
import org.openqa.selenium.JavascriptExecutor

/*
 * Testing the actual website with Selenium
 * This test only works when Genesis is deployed
 */
class SeleniumSpec  extends Specification {
/*
  case class param(sourceId : String, targetId:String)
  implicit val webDriver : WebDriver = new FirefoxDriver

  "The blog app home page" should {
    	"have the correct title" in {
    	webDriver.get("http://80.112.151.137:9001")
      webDriver.findElement(By.id("email")).sendKeys("hello@world.com")
      webDriver.findElement(By.id("password")).sendKeys("helloworld")
      webDriver.findElement(By.id("loginbutton")).click()
      webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS)
      webDriver.findElement(By.id("setupLibrarySelector")).sendKeys("default")
      webDriver.findElement(By.id("circuitName")).sendKeys("testCircuit")
      webDriver.findElement(By.className("btn-primary")).click()
      //assertEquals(2,webDriver.findElement(By.className("gateElement")).getSize())
      val builder : Actions = new Actions(webDriver)
      val gate : WebElement = webDriver.findElement(By.id("ng"))
      val canvas : WebElement = webDriver.findElement(By.id("plumbArea"))
      val output : WebElement = webDriver.findElement(By.id("Output"))
      //val output1 : WebElement = output.findElement(By.className("_jsPlumb_endpoint  _jsPlumb_endpoint_anchor_ ui-draggable ui-droppable"))
      val input = webDriver.findElement(By.id("Input"))
      //val input1 : WebElement = input.findElement(By.className("_jsPlumb_endpoint  _jsPlumb_endpoint_anchor_ ui-draggable ui-droppable"))
      val dragAndDrop : Action = builder.clickAndHold(gate).moveToElement(canvas).click().build()
      dragAndDrop.perform()
      val moveOutput: Action = builder.clickAndHold(output).moveByOffset(50, 50).click().build()
      moveOutput.perform()
      
      
     // webDriver.switchTo().frame(input)
      val test : Action = builder.clickAndHold().moveByOffset(97, 139).click().build()
      test.perform()
      
		//JavascriptExecutor jse=(JavascriptExecutor)driver;
		//String str=jse.executeScript(jsQuery).toString();

    
      val test2 = param("Input","Output")
      val jse : JavascriptExecutor = webDriver.asInstanceOf[JavascriptExecutor]
     val str = jse.executeScript("makeConnection(test2)")
      //val moveFromInputToOutput: Action = builder.clickAndHold(input1).moveToElement(output1).click().build()
      //moveFromInputToOutput.perform()
      //val hoverItem : Locatable  = webDriver.findElement(By.className("ui-droppable")).asInstanceOf[Locatable]
// _jsPlumb_endpoint _jsPlumb_endpoint_anchor_ ui-draggable ui-droppable
		// http://selenium.googlecode.com/svn/trunk/docs/api/java/org/openqa/selenium/interactions/internal/Coordinates.html
	  //val MyTestCoordinates : Coordinates = hoverItem.getCoordinates()
	  //val testing = canvas.findElement(By.className("ui-droppable")).getLocation()
      //println( "test: "+ testing)

    }
  }
  */

}

