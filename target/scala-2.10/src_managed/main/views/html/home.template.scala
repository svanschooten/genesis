
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
object home extends BaseScalaTemplate[play.api.templates.Html,Format[play.api.templates.Html]](play.api.templates.HtmlFormat) with play.api.templates.Template2[String,Html,play.api.templates.Html] {

    /**/
    def apply/*1.2*/(title: String)(content: Html):play.api.templates.Html = {
        _display_ {
def /*3.2*/head/*3.6*/:play.api.templates.Html = {_display_(

Seq[Any](format.raw/*3.10*/("""
	<link rel="stylesheet" href=""""),_display_(Seq[Any](/*4.32*/routes/*4.38*/.Assets.at("stylesheets/home.css"))),format.raw/*4.72*/("""">
    <link href="""),_display_(Seq[Any](/*5.17*/routes/*5.23*/.Assets.at("stylesheets/rickshaw.min.css"))),format.raw/*5.65*/(""" rel='stylesheet' />
    <link href="""),_display_(Seq[Any](/*6.17*/routes/*6.23*/.Assets.at("stylesheets/main.css"))),format.raw/*6.57*/(""" rel='stylesheet' />
    <script src="""),_display_(Seq[Any](/*7.18*/routes/*7.24*/.Assets.at("javascripts/lib/jquery-ui-1.9.2.js"))),format.raw/*7.72*/(""" ></script> 
	<script src="""),_display_(Seq[Any](/*8.15*/routes/*8.21*/.Assets.at("javascripts/lib/jquery.jsPlumb-1.4.0-all-min.js"))),format.raw/*8.82*/(""" ></script>
	<script src="""),_display_(Seq[Any](/*9.15*/routes/*9.21*/.Assets.at("javascripts/plumbWorkspace.js"))),format.raw/*9.64*/(""" ></script>
	<script src="""),_display_(Seq[Any](/*10.15*/routes/*10.21*/.Assets.at("javascripts/proteins.js"))),format.raw/*10.58*/(""" ></script>
	
    """)))};
Seq[Any](format.raw/*1.32*/("""

"""),format.raw/*12.6*/("""


"""),_display_(Seq[Any](/*15.2*/main(title)/*15.13*/(head)/*15.19*/ {_display_(Seq[Any](format.raw/*15.21*/("""
    """),_display_(Seq[Any](/*16.6*/modals())),format.raw/*16.14*/("""
	<div id="application-area" class="container-fluid">
        <div id="alertBox"></div>
		<div id="application-header-row" class="row-fluid">
			<header class="span12">
				<div class="btn-toolbar">
					<div class="btn-group">
						<a class="btn dropdown-toggle" data-toggle="dropdown" href="#">
					   	 Circuit
						</a>
						<ul class="dropdown-menu">
							<li>
								<a href="#">This doesn't do anything (yet)</a>
							</li>
							
							<li class="divider" />
							
							<li>
								<a onclick="resetWorkspace()"> New </a>
							</li>
							<li>
								<a onclick="saveCircuit()"> Save </a>
							</li>
							<li>
								<a onclick="getAllCircuits()"> Load </a>
							</li>
							<!-- <li>  //TODO Pas invoegen als we het daadwerkelijk gaan doen. Opschonen voor SIG!
								<a href="#">Import...</a>
							</li>
							<li>
								<a href="#">Export...</a>
							</li>  _-->
						</ul>
					</div>
					<div class="btn-group">
						<a class="btn dropdown-toggle" data-toggle="dropdown" href="#">
					   	 Simulation
						</a>
						<ul class="dropdown-menu">
							<li>
								<a onclick="beginSimulation()">Run circuit</a>
							</li>
                            <li>
                                <a onclick="showSetup()">Setup</a>
                            </li>
						</ul>
					</div>
				</div>
			</header>
		</div>
		<div id="application-main-row" class="row-fluid">	
			<div id="plumbArea" class="application-canvas span9">
			</div>
			<div class="application-sidebar span3">
					<br></br>
					<div>
					<div id="ag" class="product"><img height="80px" width="80px" src="""),_display_(Seq[Any](/*72.72*/routes/*72.78*/.Assets.at("images/AND_gate.png"))),format.raw/*72.111*/("""></div>
					<div id="ng" class="product"><img height="80px" width="80px" src="""),_display_(Seq[Any](/*73.72*/routes/*73.78*/.Assets.at("images/NOT_gate.png"))),format.raw/*73.111*/("""></div>
					</div>
					<br></br>
					<div>Modified gates :</div>
					<div>
					</div>
			</div>
		</div>
    </div>
""")))})))}
    }
    
    def render(title:String,content:Html): play.api.templates.Html = apply(title)(content)
    
    def f:((String) => (Html) => play.api.templates.Html) = (title) => (content) => apply(title)(content)
    
    def ref: this.type = this

}
                /*
                    -- GENERATED --
                    DATE: Thu Jun 06 12:24:13 CEST 2013
                    SOURCE: D:/workspace/play/genesis/app/views/home.scala.html
                    HASH: 406f11d072893257de446e41a383fa8c7749badb
                    MATRIX: 509->1|599->36|610->40|677->44|745->77|759->83|814->117|869->137|883->143|946->185|1019->223|1033->229|1088->263|1162->302|1176->308|1245->356|1308->384|1322->390|1404->451|1466->478|1480->484|1544->527|1607->554|1622->560|1681->597|1741->31|1772->618|1814->625|1834->636|1849->642|1889->644|1931->651|1961->659|3682->2344|3697->2350|3753->2383|3869->2463|3884->2469|3940->2502
                    LINES: 19->1|21->3|21->3|23->3|24->4|24->4|24->4|25->5|25->5|25->5|26->6|26->6|26->6|27->7|27->7|27->7|28->8|28->8|28->8|29->9|29->9|29->9|30->10|30->10|30->10|33->1|35->12|38->15|38->15|38->15|38->15|39->16|39->16|95->72|95->72|95->72|96->73|96->73|96->73
                    -- GENERATED --
                */
            