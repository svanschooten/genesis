
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
object modals extends BaseScalaTemplate[play.api.templates.Html,Format[play.api.templates.Html]](play.api.templates.HtmlFormat) with play.api.templates.Template0[play.api.templates.Html] {

    /**/
    def apply():play.api.templates.Html = {
        _display_ {

Seq[Any](format.raw/*1.1*/("""<div id="setupModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="setupModalHeader">
    <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">x</button>
        <h3 id="setupModalHeader" accesskey="">Setup the circuit.</h3>
    </div>
    <div class="modal-body" id="setupModalBody">
        <div id="setupErrorDiv" style="color: red;"></div>
        <table class="noborders">
            <tr>
                <td>Library: </td>
                <td><select name="setupLibrarySelector" id="setupLibrarySelector"><option value="-1">Select a library.</option></select></td>
            </tr>
            <tr>
                <td>Circuit name: </td>
                <td><input type="text" name="circuitName" id="circuitName" placeholder="circuitName"></td>
            </tr>
            <tr>
                <td>Simulation timespan: </td>
                <td><input type="number" name="simTimeSpan" id="simTimeSpan" value="5" min="1" max="100"></td>
            </tr>
            <tr>
                <td>Simulation steps: </td>
                <td><input type="number" name="simSteps" id="simSteps" value="100" min="1" max="10000"></td>
            </tr>
        </table>
    </div>
    <div id="setupModalFooter" class="modal-footer">
        <button id="applylib" class="btn btn-primary" onclick="applySetup()">Apply setup</button>
    </div>
</div>
<div id="proteinModal" class="modal hide fade bigModal" tabindex="-1" role="dialog" aria-labelledby="proteinModalHeader">
    <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">x</button>
        <h3 id="proteinModalHeader" accesskey="">Choose your protein.</h3>
    </div>
    <div class="modal-body" id="proteinModalBody">
        <div id="proteinListDiv" class="well"></div>
        <div id="proteinParameters" class="well">
            <span><b>Values that influence the reaction of the <u>input</u> signal:</span><br><br>
            <span>k<sub>1</sub> = <p id="pk1" class="inline"></p></span><br><br>
            <span>k<sub>m</sub> = <p id="pkm" class="inline"></p></span><br><br>
            <span>n = <p id="pn" class="inline"></p></span><br><br>
        	<span><b>Values that influence the reaction of the <u>output</u> signal:</span><br><br>
        	<span>k<sub>2</sub> = <p id="pk2" class="inline"></p></span><br><br>
        	<span>d<sub>1</sub> = <p id="pd1" class="inline"></p></span><br><br>
        	<span>d<sub>2</sub> = <p id="pd2" class="inline"></p></span>
        </div>
    </div>
    <div id="proteinModalFooter" class="modal-footer">
        <button class="btn" data-dismiss="modal" aria-hidden="true">Close</button>
        <button class="btn btn-primary" onclick="setProtein()">Select protein</button>
    </div>
</div>
<div id="resultModal" class="modal hide fade bigModal" tabindex="-1" role="dialog" aria-labelledby="resultModalHeader">
    <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">x</button>
        <h3 id="resultModalHeader" accesskey="">Simulation results.</h3>
    </div>
    <div class="modal-body" id="resultModalBody">
        <div id="chart_container" data-width="565" data-height="250">
            <div id="y_axis"></div>
            <div id="chart"></div>
            <div id="legend"></div><br>
            <div id="slider"></div>
        </div>
    </div>
    <div id="resultModalFooter" class="modal-footer">
        <button class="btn" data-dismiss="modal" aria-hidden="true">Close</button>
        <button class="btn btn-primary">Download csv.</button> <!-- \\TODO create csv in backend and push to front.      -->
    </div>
</div>
<div id="signalModal" class="modal hide fade bigModal" tabindex="-1" role="dialog" aria-labelledby="signalModalHeader">
    <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">x</button>
        <h3 id="signalModalHeader" accesskey="">Create your input signal.</h3>
    </div>
    <div class="modal-body" id="signalModalBody">
        <div id="signalErrorDiv" style="color: red;"></div>
        <textarea rows="25" cols="70" id="signalArea"></textarea>
    </div>
    <div id="signalModalFooter" class="modal-footer">
        <button class="btn" data-dismiss="modal" aria-hidden="true">Close</button>
        <button class="btn btn-primary" onclick="completeSimulation()">Apply</button>
    </div>
</div>"""))}
    }
    
    def render(): play.api.templates.Html = apply()
    
    def f:(() => play.api.templates.Html) = () => apply()
    
    def ref: this.type = this

}
                /*
                    -- GENERATED --
                    DATE: Thu Jun 06 12:24:13 CEST 2013
                    SOURCE: D:/workspace/play/genesis/app/views/modals.scala.html
                    HASH: 8ca9ea3aa48a75c55f337413a4285ee7717c1c3a
                    MATRIX: 570->0
                    LINES: 22->1
                    -- GENERATED --
                */
            