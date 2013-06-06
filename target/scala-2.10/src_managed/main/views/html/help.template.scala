
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
object help extends BaseScalaTemplate[play.api.templates.Html,Format[play.api.templates.Html]](play.api.templates.HtmlFormat) with play.api.templates.Template2[String,Html,play.api.templates.Html] {

    /**/
    def apply/*1.2*/(title: String)(content: Html):play.api.templates.Html = {
        _display_ {
def /*3.2*/head/*3.6*/:play.api.templates.Html = {_display_(

Seq[Any]())};
Seq[Any](format.raw/*1.32*/("""

"""),format.raw/*3.11*/("""

"""),_display_(Seq[Any](/*5.2*/main(title)/*5.13*/(head)/*5.19*/ {_display_(Seq[Any](format.raw/*5.21*/("""

<div>
	"""),_display_(Seq[Any](/*8.3*/content)),format.raw/*8.10*/("""
</div>

""")))})))}
    }
    
    def render(title:String,content:Html): play.api.templates.Html = apply(title)(content)
    
    def f:((String) => (Html) => play.api.templates.Html) = (title) => (content) => apply(title)(content)
    
    def ref: this.type = this

}
                /*
                    -- GENERATED --
                    DATE: Fri May 31 11:41:39 CEST 2013
                    SOURCE: D:/workspace/play/genesis/app/views/help.scala.html
                    HASH: 784f75bfe14f71de38465e06d8b3514e1e3d7ba4
                    MATRIX: 509->1|599->36|610->40|691->31|722->45|761->50|780->61|794->67|833->69|880->82|908->89
                    LINES: 19->1|21->3|21->3|24->1|26->3|28->5|28->5|28->5|28->5|31->8|31->8
                    -- GENERATED --
                */
            