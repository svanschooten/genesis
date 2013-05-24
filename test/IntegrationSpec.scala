package test

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._
import org.openqa.selenium._

/*
 * Selenium test
 * Login, Home, Help, Settings pages and logout tested
 */
class IntegrationSpec extends Specification {
  "Application" should {

    "works from within a browser" in new WithBrowser(webDriver = Helpers.FIREFOX) {

      running(FakeApplication()) {
        browser.goTo("/login")
        browser.$("#email").text("test@t.com")
        browser.$("#password").text("testerr")
        browser.$("#loginbutton").click()
        browser.pageSource must contain("Wrong email or password")
        
        browser.$("#email").text("test@t.com")
        browser.$("#password").text("tester")
        browser.$("#loginbutton").click()
        browser.pageSource must not contain("Pleases sign in")
        browser.pageSource must contain("Home")
        
        browser.goTo("/help")
        browser.pageSource must contain("Help")
        
        browser.goTo("/settings")
        browser.pageSource must contain("Settings")
        
        browser.goTo("/logout")
        browser.pageSource must contain("You've been logged out")
      }
    }
  }
}
