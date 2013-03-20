
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
object rungekutte extends BaseScalaTemplate[play.api.templates.Html,Format[play.api.templates.Html]](play.api.templates.HtmlFormat) with play.api.templates.Template2[String,Rungekuttatest,play.api.templates.Html] {

    /**/
    def apply/*1.2*/(title: String, rk: Rungekuttatest):play.api.templates.Html = {
        _display_ {

Seq[Any](format.raw/*1.37*/("""

<!DOCTYPE html>

<html>
    <head>
        <title>"""),_display_(Seq[Any](/*7.17*/title)),format.raw/*7.22*/("""</title>
    </head>
    <body>
    	"""),_display_(Seq[Any](/*10.7*/for(el <- rk.test()) yield /*10.27*/ {_display_(Seq[Any](format.raw/*10.29*/("""
    		"""),_display_(Seq[Any](/*11.8*/el)),format.raw/*11.10*/(""" <br>
    	""")))})),format.raw/*12.7*/("""
    </body>
</html>"""))}
    }
    
    def render(title:String,rk:Rungekuttatest): play.api.templates.Html = apply(title,rk)
    
    def f:((String,Rungekuttatest) => play.api.templates.Html) = (title,rk) => apply(title,rk)
    
    def ref: this.type = this

}
                /*
                    -- GENERATED --
                    DATE: Wed Mar 20 17:50:10 CET 2013
                    SOURCE: D:/workspace/play/genesis/app/views/rungekutte.scala.html
                    HASH: e5dfb814fcc032402e6a5845eb1043673e2e7148
                    MATRIX: 525->1|637->36|731->95|757->100|833->141|869->161|909->163|953->172|977->174|1021->187
                    LINES: 19->1|22->1|28->7|28->7|31->10|31->10|31->10|32->11|32->11|33->12
                    -- GENERATED --
                */
            