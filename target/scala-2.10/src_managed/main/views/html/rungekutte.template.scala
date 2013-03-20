
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
object rungekutte extends BaseScalaTemplate[play.api.templates.Html,Format[play.api.templates.Html]](play.api.templates.HtmlFormat) with play.api.templates.Template2[String,rungekuttatest,play.api.templates.Html] {

    /**/
    def apply/*1.2*/(title: String, rk: rungekuttatest):play.api.templates.Html = {
        _display_ {

Seq[Any](format.raw/*1.37*/("""

<!DOCTYPE html>

<html>
    <head>
        <title>"""),_display_(Seq[Any](/*7.17*/title)),format.raw/*7.22*/("""</title>
    </head>
    <body>
        """),_display_(Seq[Any](/*10.10*/rk/*10.12*/.test())),format.raw/*10.19*/("""
    </body>
</html>"""))}
    }
    
    def render(title:String,rk:rungekuttatest): play.api.templates.Html = apply(title,rk)
    
    def f:((String,rungekuttatest) => play.api.templates.Html) = (title,rk) => apply(title,rk)
    
    def ref: this.type = this

}
                /*
                    -- GENERATED --
                    DATE: Wed Mar 20 17:19:23 CET 2013
                    SOURCE: D:/workspace/play/genesis/app/views/rungekutte.scala.html
                    HASH: 48edc44a848a02008946e4f9749e5e7d364133f8
                    MATRIX: 525->1|637->36|731->95|757->100|837->144|848->146|877->153
                    LINES: 19->1|22->1|28->7|28->7|31->10|31->10|31->10
                    -- GENERATED --
                */
            