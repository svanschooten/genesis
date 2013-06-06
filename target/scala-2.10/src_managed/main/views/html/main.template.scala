
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
object main extends BaseScalaTemplate[play.api.templates.Html,Format[play.api.templates.Html]](play.api.templates.HtmlFormat) with play.api.templates.Template3[String,Html,Html,play.api.templates.Html] {

    /**/
    def apply/*1.2*/(title: String)(head_extra: Html)(content: Html):play.api.templates.Html = {
        _display_ {
def /*4.2*/active/*4.8*/(t : String):play.api.templates.Html = {_display_(

Seq[Any](format.raw/*4.24*/("""
	"""),_display_(Seq[Any](/*5.3*/if(title equals t)/*5.21*/{_display_(Seq[Any](format.raw/*5.22*/("""class="active"""")))})),format.raw/*5.37*/("""
""")))};def /*8.2*/head/*8.6*/:play.api.templates.Html = {_display_(

Seq[Any](format.raw/*8.10*/("""
    <script type="text/javascript" src=""""),_display_(Seq[Any](/*9.42*/routes/*9.48*/.Application.javascriptRoutes)),format.raw/*9.77*/(""""></script>
	
    """),_display_(Seq[Any](/*11.6*/head_extra)),format.raw/*11.16*/("""

""")))};
Seq[Any](format.raw/*1.50*/("""

"""),format.raw/*3.90*/("""
"""),format.raw/*6.2*/("""

"""),format.raw/*13.2*/("""

"""),_display_(Seq[Any](/*15.2*/base(title)/*15.13*/(head)/*15.19*/ {_display_(Seq[Any](format.raw/*15.21*/("""	
	<div class="mainpage">
		<header>
	        <div class="navbar navbar-static-top">
                <div class="navbar-inner">
                    <div class="container">
                        <ul class="nav pull-left">
                            <li><img src=""""),_display_(Seq[Any](/*22.44*/routes/*22.50*/.Assets.at("images/genesis-logo-h40.png"))),format.raw/*22.91*/(""""></li>
                            <li>&nbsp;</li>
                            <li """),_display_(Seq[Any](/*24.34*/active("Home"))),format.raw/*24.48*/("""><a href="/">Home</a></li>
                        </ul>
                        <ul class="nav pull-right">
                            <li """),_display_(Seq[Any](/*27.34*/active("Help"))),format.raw/*27.48*/("""><a href="/help">Help</a></li>
                            <li """),_display_(Seq[Any](/*28.34*/active("Settings"))),format.raw/*28.52*/("""><a href="/settings">Settings</a></li>
                            <li><a href="/logout">Logout</a></li>
                        </ul>
                    </div>
                </div>
	        </div>  
		</header>

		<section class="content">"""),_display_(Seq[Any](/*36.29*/content)),format.raw/*36.36*/("""</section>
		
		<footer>
			<div class="pull-left">
				<p>&copy; Team JAMES</p>
			</div>
			
			<div class="pull-right">		
				<a href="/about">About</a>
			</div>
		</footer>
		
	</div>
	<script src="""),_display_(Seq[Any](/*49.15*/routes/*49.21*/.Assets.at("javascripts/script.js"))),format.raw/*49.56*/(""" ></script>
    """)))})),format.raw/*50.6*/("""
"""))}
    }
    
    def render(title:String,head_extra:Html,content:Html): play.api.templates.Html = apply(title)(head_extra)(content)
    
    def f:((String) => (Html) => (Html) => play.api.templates.Html) = (title) => (head_extra) => (content) => apply(title)(head_extra)(content)
    
    def ref: this.type = this

}
                /*
                    -- GENERATED --
                    DATE: Thu Jun 06 12:24:13 CEST 2013
                    SOURCE: D:/workspace/play/genesis/app/views/main.scala.html
                    HASH: 78db284e8786bed4ad1b3b8e0e7a8cc2b6268fad
                    MATRIX: 514->1|622->145|635->151|714->167|752->171|778->189|816->190|862->205|887->213|898->217|965->221|1043->264|1057->270|1107->299|1163->320|1195->330|1239->49|1270->142|1298->208|1329->335|1369->340|1389->351|1404->357|1444->359|1753->632|1768->638|1831->679|1954->766|1990->780|2171->925|2207->939|2308->1004|2348->1022|2636->1274|2665->1281|2917->1497|2932->1503|2989->1538|3038->1556
                    LINES: 19->1|21->4|21->4|23->4|24->5|24->5|24->5|24->5|25->8|25->8|27->8|28->9|28->9|28->9|30->11|30->11|33->1|35->3|36->6|38->13|40->15|40->15|40->15|40->15|47->22|47->22|47->22|49->24|49->24|52->27|52->27|53->28|53->28|61->36|61->36|74->49|74->49|74->49|75->50
                    -- GENERATED --
                */
            