
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
object login extends BaseScalaTemplate[play.api.templates.Html,Format[play.api.templates.Html]](play.api.templates.HtmlFormat) with play.api.templates.Template2[Form[scala.Tuple2[String, String]],Flash,play.api.templates.Html] {

    /**/
    def apply/*1.2*/(form: Form[(String,String)])(implicit flash: Flash):play.api.templates.Html = {
        _display_ {
def /*3.2*/head/*3.6*/:play.api.templates.Html = {_display_(

Seq[Any](format.raw/*3.10*/("""
	<link rel="stylesheet" href=""""),_display_(Seq[Any](/*4.32*/routes/*4.38*/.Assets.at("stylesheets/login.css"))),format.raw/*4.73*/("""">
""")))};
Seq[Any](format.raw/*1.54*/("""

"""),format.raw/*5.2*/("""

"""),_display_(Seq[Any](/*7.2*/base("Welcome to GENESIS")/*7.28*/(head)/*7.34*/ {_display_(Seq[Any](format.raw/*7.36*/("""	
	<div class="container">
	
		<div class="row-fluid">
			<div id="container-left" class="span6">
				"""),_display_(Seq[Any](/*12.6*/helper/*12.12*/.form(routes.Application.authenticate, 'class -> "form-signin")/*12.75*/ {_display_(Seq[Any](format.raw/*12.77*/("""
					"""),_display_(Seq[Any](/*13.7*/form/*13.11*/.globalError.map/*13.27*/ { error =>_display_(Seq[Any](format.raw/*13.38*/("""
						<p class="error">"""),_display_(Seq[Any](/*14.25*/error/*14.30*/.message)),format.raw/*14.38*/("""</p>
					""")))})),format.raw/*15.7*/(""" 
					
					"""),_display_(Seq[Any](/*17.7*/flash/*17.12*/.get("success").map/*17.31*/ { message =>_display_(Seq[Any](format.raw/*17.44*/("""
						<p class="success">"""),_display_(Seq[Any](/*18.27*/message)),format.raw/*18.34*/("""</p>
					""")))})),format.raw/*19.7*/("""				
					
					<h2 id="title" class="form-signin-heading">Please sign in</h2>
					<input class="input-block-level" type="email" id="email" name="email" placeholder="Username">
					<input class="input-block-level" type="password" id="password" name="password" placeholder="Password">
					<label class="checkbox"> 
						<input type="checkbox" value="remember-me"> Remember me (doesn't work yet)
					</label>
					<button id ="loginbutton" class="btn btn-large btn-primary" type="submit">Sign in</button>
				""")))})),format.raw/*28.6*/("""
			</div>
			
			<div id="container-right" class="span6">
				A little bit of lorem ipsum
			</div>
		</div>
		
		<footer>
			<p>&copy; Team James</p>
		</footer>   
	</div>
		
""")))})))}
    }
    
    def render(form:Form[scala.Tuple2[String, String]],flash:Flash): play.api.templates.Html = apply(form)(flash)
    
    def f:((Form[scala.Tuple2[String, String]]) => (Flash) => play.api.templates.Html) = (form) => (flash) => apply(form)(flash)
    
    def ref: this.type = this

}
                /*
                    -- GENERATED --
                    DATE: Wed Jun 05 20:59:14 CEST 2013
                    SOURCE: D:/workspace/play/genesis/app/views/login.scala.html
                    HASH: f1839ee337747f1eed02286f8303d57575fbba7d
                    MATRIX: 539->1|651->58|662->62|729->66|797->99|811->105|867->140|911->53|941->145|980->150|1014->176|1028->182|1067->184|1210->292|1225->298|1297->361|1337->363|1380->371|1393->375|1418->391|1467->402|1529->428|1543->433|1573->441|1616->453|1667->469|1681->474|1709->493|1760->506|1824->534|1853->541|1896->553|2449->1075
                    LINES: 19->1|21->3|21->3|23->3|24->4|24->4|24->4|26->1|28->5|30->7|30->7|30->7|30->7|35->12|35->12|35->12|35->12|36->13|36->13|36->13|36->13|37->14|37->14|37->14|38->15|40->17|40->17|40->17|40->17|41->18|41->18|42->19|51->28
                    -- GENERATED --
                */
            