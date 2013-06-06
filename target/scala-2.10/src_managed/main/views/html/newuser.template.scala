
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
object newuser extends BaseScalaTemplate[play.api.templates.Html,Format[play.api.templates.Html]](play.api.templates.HtmlFormat) with play.api.templates.Template2[String,Form[User],play.api.templates.Html] {

    /**/
    def apply/*1.2*/(title: String)(form: Form[User]):play.api.templates.Html = {
        _display_ {

Seq[Any](format.raw/*1.35*/("""

"""),_display_(Seq[Any](/*3.2*/main(title)/*3.13*/(Html.apply("Placeholder"))/*3.40*/{_display_(Seq[Any](format.raw/*3.41*/("""
	
	<div>
		
		"""),_display_(Seq[Any](/*7.4*/helper/*7.10*/.form(action = routes.Admin.saveUser)/*7.47*/ {_display_(Seq[Any](format.raw/*7.49*/("""
			"""),_display_(Seq[Any](/*8.5*/helper/*8.11*/.inputText(form("Email")))),format.raw/*8.36*/("""
			"""),_display_(Seq[Any](/*9.5*/helper/*9.11*/.inputPassword(form("Password")))),format.raw/*9.43*/("""
			"""),_display_(Seq[Any](/*10.5*/helper/*10.11*/.inputText(form("First name")))),format.raw/*10.41*/("""
			"""),_display_(Seq[Any](/*11.5*/helper/*11.11*/.inputText(form("Last name")))),format.raw/*11.40*/("""
			<button class='btn btn-primary' type='submit'>Save</button>
		""")))})),format.raw/*13.4*/("""
	</div>
	
""")))})))}
    }
    
    def render(title:String,form:Form[User]): play.api.templates.Html = apply(title)(form)
    
    def f:((String) => (Form[User]) => play.api.templates.Html) = (title) => (form) => apply(title)(form)
    
    def ref: this.type = this

}
                /*
                    -- GENERATED --
                    DATE: Fri May 31 11:41:39 CEST 2013
                    SOURCE: D:/workspace/play/genesis/app/views/newuser.scala.html
                    HASH: 8d38756a9e4624bf16e42afe783b5d5fb3edd1a4
                    MATRIX: 518->1|628->34|667->39|686->50|721->77|759->78|813->98|827->104|872->141|911->143|951->149|965->155|1011->180|1051->186|1065->192|1118->224|1159->230|1174->236|1226->266|1267->272|1282->278|1333->307|1433->376
                    LINES: 19->1|22->1|24->3|24->3|24->3|24->3|28->7|28->7|28->7|28->7|29->8|29->8|29->8|30->9|30->9|30->9|31->10|31->10|31->10|32->11|32->11|32->11|34->13
                    -- GENERATED --
                */
            