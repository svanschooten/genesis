
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
object base extends BaseScalaTemplate[play.api.templates.Html,Format[play.api.templates.Html]](play.api.templates.HtmlFormat) with play.api.templates.Template3[String,Html,Html,play.api.templates.Html] {

    /**/
    def apply/*1.2*/(title : String)(head : Html)(body : Html):play.api.templates.Html = {
        _display_ {

Seq[Any](format.raw/*1.44*/("""

<!DOCTYPE html>
<html lang="en">
	
	<head>
		<title>"""),_display_(Seq[Any](/*7.11*/title)),format.raw/*7.16*/("""</title>
		
		<!-- Here be humans -->
		<link type="text/plain" rel="author" href=""""),_display_(Seq[Any](/*10.47*/routes/*10.53*/.Assets.at("humans.txt"))),format.raw/*10.77*/(""""/>

		<!-- bootstrap -->
		<link rel="stylesheet" media="screen" href=""""),_display_(Seq[Any](/*13.48*/routes/*13.54*/.Assets.at("stylesheets/bootstrap.min.css"))),format.raw/*13.97*/("""">
        <link rel="shortcut icon" type="image/png" href=""""),_display_(Seq[Any](/*14.59*/routes/*14.65*/.Assets.at("images/genesis-logo64x64.png"))),format.raw/*14.107*/("""">
		<!-- jQuery -->
		<script src=""""),_display_(Seq[Any](/*16.17*/routes/*16.23*/.Assets.at("javascripts/lib/jquery-1.9.1.min.js"))),format.raw/*16.72*/("""" type="text/javascript"></script>

        <link rel="stylesheet" href=""""),_display_(Seq[Any](/*18.39*/routes/*18.45*/.Assets.at("stylesheets/jquery-ui-1.10.2.min.css"))),format.raw/*18.95*/("""">
		
		<!-- CSS -->
		<link rel="stylesheet" href=""""),_display_(Seq[Any](/*21.33*/routes/*21.39*/.Assets.at("stylesheets/base.css"))),format.raw/*21.73*/("""">
		
	
		"""),_display_(Seq[Any](/*24.4*/head)),format.raw/*24.8*/("""
		
	</head>
	
	<body>
		"""),_display_(Seq[Any](/*29.4*/body)),format.raw/*29.8*/("""

	</body>
	
</html>"""))}
    }
    
    def render(title:String,head:Html,body:Html): play.api.templates.Html = apply(title)(head)(body)
    
    def f:((String) => (Html) => (Html) => play.api.templates.Html) = (title) => (head) => (body) => apply(title)(head)(body)
    
    def ref: this.type = this

}
                /*
                    -- GENERATED --
                    DATE: Fri May 31 11:41:39 CEST 2013
                    SOURCE: D:/workspace/play/genesis/app/views/base.scala.html
                    HASH: a92aa851d4dec672c771e6c0ff15421734be3a7e
                    MATRIX: 514->1|633->43|729->104|755->109|878->196|893->202|939->226|1051->302|1066->308|1131->351|1229->413|1244->419|1309->461|1384->500|1399->506|1470->555|1582->631|1597->637|1669->687|1761->743|1776->749|1832->783|1881->797|1906->801|1972->832|1997->836
                    LINES: 19->1|22->1|28->7|28->7|31->10|31->10|31->10|34->13|34->13|34->13|35->14|35->14|35->14|37->16|37->16|37->16|39->18|39->18|39->18|42->21|42->21|42->21|45->24|45->24|50->29|50->29
                    -- GENERATED --
                */
            