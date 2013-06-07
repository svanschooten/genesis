package test

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._
import org.openqa.selenium._
import org.fest.assertions.Assertions._
//import org.fluentlenium.core.filter.FilterConstructor._
import java.util.concurrent.TimeUnit
import org.fluentlenium.core.filter.FilterConstructor._
/*
 * Testing the website from localhost
 * This means that this test works even when the website is not deployed
 * Login, Home, Help, Settings pages and logout tested
 */
class IntegrationSpec extends Specification {
/*
  "Application" should {

    "works from within a browser" in new WithBrowser(webDriver = Helpers.FIREFOX) {

      running(FakeApplication()) {
        /* 
         * Testing redirects when not logged in
         */
        browser.goTo("/")
        browser.pageSource must contain("Please sign in")
        browser.goTo("/help")
        browser.pageSource must contain("Please sign in")
        browser.goTo("/settings")
        browser.pageSource must contain("Please sign in")
        browser.$("#email").text("fail@fail.com")
        browser.$("#password").text("fail")
        browser.$("#loginbutton").click()
        browser.pageSource must contain("Wrong email or password")
        
        /*
         * Testing successfully log in
         */
        browser.$("#email").text("hello@world.com")
        browser.$("#password").text("helloworld")
        browser.$("#loginbutton").click()
        browser.pageSource must not contain("Please sign in")
        
        /*
         * Testing modal (library selection)
         */
        browser.withDefaultSearchWait(10, TimeUnit.SECONDS);
        browser.pageSource must contain("Setup the circuit")
        browser.$("#applylib").click()
        browser.pageSource must contain("Choose a library first!")
        browser.$("#lib0").click()
        browser.$("#applylib").click()
        browser.pageSource must contain("You must specify a name!") 
        browser.$("#circuitName").text("testCircuit")
        browser.$("#applylib").click()
        browser.executeScript("applySetup()")
        //browser.$(".alert fade in alert-success").getText must contain("SUCCESS! Protein library successfully loaded!") 
        
        /*
         * Testing workbox 
         */
        //In the start, there are source and sink created
        browser.$(".gateElement").size must equalTo(2)
        browser.executeScript("andGate(0,0)")
        browser.$(".gateElement").size must equalTo(3)      
        browser.executeScript("notGate(0,0)")
        browser.$(".gateElement").size must equalTo(4)
        
        /*
         * Redirects and other functions when logged in
         */
        //browser.goTo("/help")
        //browser.pageSource must contain("Help")
        
        //browser.goTo("/settings")
        //browser.pageSource must contain("Settings")
        
        //browser.goTo("/logout")
        //browser.pageSource must contain("You've been logged out")
      }
    }
  }
  * 
  */
}
