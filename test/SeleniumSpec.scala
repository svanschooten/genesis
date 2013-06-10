package test

import org.specs2.mutable._
import org.openqa.selenium.WebDriver
import org.openqa.selenium._
import org.openqa.selenium.firefox._
import java.util.concurrent.TimeUnit
import junit.framework.Assert._
import org.openqa.selenium.interactions._

/*
 * Testing the actual website with Selenium
 * This test only works when Genesis is deployed
 */
class SeleniumSpec  extends Specification {

  implicit val webDriver : WebDriver = new FirefoxDriver

  "The blog app home page" should {
      "have the correct title" in {
      /* 
       * Testing url with wrong information
       */
      //Use this for testing the website : webDriver.get("http://80.112.151.137:9001")
      webDriver.get("http://127.0.0.1:9000")
      webDriver.findElement(By.id("email")).sendKeys("wrong@email.com")
      webDriver.findElement(By.id("password")).sendKeys("helloworld")
      webDriver.findElement(By.id("loginbutton")).click()
      assertEquals(webDriver.getPageSource().contains("Please sign in"), true)
      
      webDriver.findElement(By.id("email")).sendKeys("hello@world.com")
      webDriver.findElement(By.id("password")).sendKeys("wrongpassword")
      webDriver.findElement(By.id("loginbutton")).click()
      assertEquals(webDriver.getPageSource().contains("Please sign in"), true)
      
      /*
       * Testing url with correct information
       */
      webDriver.findElement(By.id("email")).sendKeys("hello@world.com")
      webDriver.findElement(By.id("password")).sendKeys("helloworld")
      webDriver.findElement(By.id("loginbutton")).click()
      
      /*
       * Testing modal (library selection)
       */
      webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS)
      assertEquals(webDriver.getPageSource().contains("Setup the circuit"), true)
      val okButton = webDriver.findElement(By.className("btn-primary"))
      okButton.click()
      assertEquals(webDriver.getPageSource().contains("Choose a library first!"), true)
      webDriver.findElement(By.id("setupLibrarySelector")).sendKeys("default")
      okButton.click()
      assertEquals(webDriver.getPageSource().contains("You must specify a name!"), true)
      webDriver.findElement(By.id("circuitName")).sendKeys("testCircuit")
      okButton.click()
      assertEquals(webDriver.findElement(By.className("alert-success")).findElement(By.tagName("Strong")).getText(),"SUCCESS!")
      webDriver.findElement(By.className("alert-success")).findElement(By.className("close")).click()
      
      /*
       * Testing workspace (canvas)
       */
      assertTrue(webDriver.findElements(By.id("input")).size() > 0)
      assertTrue(webDriver.findElements(By.id("output")).size() > 0)
      val builder : Actions = new Actions(webDriver)
      val gate : WebElement = webDriver.findElement(By.id("ng")) 
      val input : WebElement = webDriver.findElement(By.id("input"))
      val output : WebElement = webDriver.findElement(By.id("output"))
      val dragNdrop : Action = builder.clickAndHold(gate).moveToElement(output).click().build()
      dragNdrop.perform()
      assertTrue(webDriver.findElements(By.id("not2")).size() > 0)
      val makeConnection : Action = builder.clickAndHold(input).moveToElement(output).click().build()
      makeConnection.perform()
      val svg : WebElement = webDriver.findElement(By.className("_jsPlumb_connector"))
      assertTrue(webDriver.findElements(By.className("_jsPlumb_connector")).size() > 0)
      
      /*
       * Testing new (clean workspace)
       */
      webDriver.findElement(By.id("circuit")).click()
      webDriver.findElement(By.id("new")).click()
      val alert : Alert = webDriver.switchTo().alert()
      alert.accept()
      webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS)
      webDriver.findElement(By.className("btn-primary")).click()
      webDriver.findElement(By.className("alert-success")).findElement(By.className("close")).click()
      assertTrue(webDriver.findElements(By.id("input")).size() > 0)
      assertTrue(webDriver.findElements(By.id("output")).size() > 0)
      
      
      /*
       * Testing load 
       */
      webDriver.findElement(By.id("circuit")).click()
      webDriver.findElement(By.id("load")).click()
      Thread.sleep(1000)
      webDriver.findElement(By.id("loadNetworkSelector")).sendKeys("Not")
      webDriver.findElement(By.id("loadcircuit")).click()
      assertTrue(webDriver.findElements(By.id("not2")).size() > 0)
      assertTrue(webDriver.findElements(By.className("_jsPlumb_connector")).size() > 0)
      
      /*
       * Testing save
       */
      webDriver.findElement(By.id("circuit")).click()
      webDriver.findElement(By.id("save")).click()
      assertEquals(webDriver.findElement(By.className("alert-success")).findElement(By.tagName("Strong")).getText(),"SUCCESS!")
      webDriver.findElement(By.className("alert-success")).findElement(By.className("close")).click()
      Thread.sleep(1000)
      
      /*
       * Testing simulate
       */
      webDriver.findElement(By.id("simulation")).click()
      webDriver.findElement(By.id("runcircuit")).click()
      val signalArea = webDriver.findElement(By.id("signalArea"))
      signalArea.sendKeys("t,A")
      signalArea.sendKeys(Keys.ENTER)
      signalArea.sendKeys("0,0")
      signalArea.sendKeys(Keys.ENTER)
      signalArea.sendKeys("30,1")
      Thread.sleep(1000)
      webDriver.findElement(By.id("completesimulation")).click()
      Thread.sleep(5000)
      assertTrue(webDriver.findElements(By.id("resultModal")).size() > 0)
      webDriver.findElement(By.id("closeSim")).click()
      
      /*
       * Testing setup
       */
      Thread.sleep(1000)
      webDriver.findElement(By.id("simulation")).click()
      webDriver.findElement(By.id("setup")).click()
      webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS)
      webDriver.findElement(By.className("btn-primary")).click()
      assertEquals(webDriver.findElement(By.className("alert-success")).findElement(By.tagName("Strong")).getText(),"SUCCESS!")
      webDriver.findElement(By.className("alert-success")).findElement(By.className("close")).click()
      
      /*
       * Testing logout
       */
      webDriver.get("http://80.112.151.137:9001/logout")
      assertEquals(webDriver.getPageSource().contains("You've been logged out"), true)
      
      webDriver.close()
    }
  }
  

}

