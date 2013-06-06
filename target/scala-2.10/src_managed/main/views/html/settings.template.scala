
package views.html

import play.templates._
import play.templates.TemplateMagic._

import play.api.templates._
import play.api.templates.PlayMagic._
import models._
import controllers._
import play.api.i18n._
import play.api.mvc._
import play.api.data._
import views.html._
/**/
object settings extends BaseScalaTemplate[play.api.templates.Html,Format[play.api.templates.Html]](play.api.templates.HtmlFormat) with play.api.templates.Template3[String,Html,Form[User],play.api.templates.Html] {

    /**/
    def apply/*1.2*/(title: String)(content: Html)(form: Form[User]):play.api.templates.Html = {
        _display_ {
def /*3.2*/head/*3.6*/:play.api.templates.Html = {_display_(

Seq[Any]())};
Seq[Any](format.raw/*1.50*/("""

"""),format.raw/*3.11*/("""

"""),_display_(Seq[Any](/*5.2*/main(title)/*5.13*/(head)/*5.19*/ {_display_(Seq[Any](format.raw/*5.21*/("""
	
	<div>
		"""),_display_(Seq[Any](/*8.4*/content)),format.raw/*8.11*/("""
		
		"""),_display_(Seq[Any](/*10.4*/helper/*10.10*/.form(action = routes.Settings.saveSettings)/*10.54*/ {_display_(Seq[Any](format.raw/*10.56*/("""
			"""),_display_(Seq[Any](/*11.5*/helper/*11.11*/.inputText(form("Email")))),format.raw/*11.36*/("""
			"""),_display_(Seq[Any](/*12.5*/helper/*12.11*/.inputPassword(form("Password")))),format.raw/*12.43*/("""
			"""),_display_(Seq[Any](/*13.5*/helper/*13.11*/.inputText(form("First name")))),format.raw/*13.41*/("""
			"""),_display_(Seq[Any](/*14.5*/helper/*14.11*/.inputText(form("Last name")))),format.raw/*14.40*/("""
			<button class='btn btn-primary' type='submit'>Save</button>
		""")))})),format.raw/*16.4*/("""
	</div>
	
""")))})))}
    }
    
    def render(title:String,content:Html,form:Form[User]): play.api.templates.Html = apply(title)(content)(form)
    
    def f:((String) => (Html) => (Form[User]) => play.api.templates.Html) = (title) => (content) => (form) => apply(title)(content)(form)
    
    def ref: this.type = this

}
                /*
                    -- GENERATED --
                    DATE: Fri May 31 11:41:39 CEST 2013
                    SOURCE: D:/workspace/play/genesis/app/views/settings.scala.html
                    HASH: 6605d33e3f222eab09ad297b58dd787e55f232c0
                    MATRIX: 524->1|632->54|643->58|724->49|755->63|794->68|813->79|827->85|866->87|916->103|944->110|988->119|1003->125|1056->169|1096->171|1137->177|1152->183|1199->208|1240->214|1255->220|1309->252|1350->258|1365->264|1417->294|1458->300|1473->306|1524->335|1624->404
                    LINES: 19->1|21->3|21->3|24->1|26->3|28->5|28->5|28->5|28->5|31->8|31->8|33->10|33->10|33->10|33->10|34->11|34->11|34->11|35->12|35->12|35->12|36->13|36->13|36->13|37->14|37->14|37->14|39->16
                    -- GENERATED --
                */
            