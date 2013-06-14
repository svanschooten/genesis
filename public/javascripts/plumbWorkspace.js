/*jshint -W099, smarttabs: true, forin:true, noarg:true, noempty:true, eqeqeq:true, unused:true, curly:true, browser:true, jquery:true, indent:4, maxerr:50 */
/**
Authors:
-Stijn van Schooten
*/

//Globals
var circuit = [];
var customGates = [];
var connectorPaintStyle = {
    lineWidth: 4,
    strokeStyle: "#deea18",
    joinstyle: "round"
};
var connectorHoverStyle = {
    lineWidth: 4,
    strokeStyle: "#2e2aF8"
};
var endpointHoverStyle = {fillStyle: "#2e2aF8"};
var dropOptions = {
    tolerance: "touch",
    hoverClass: "dropHover",
    activeClass: "dragActive"
};

var jsp, gin, gout;
var currentConnection = null;
var gateHeight = 80, gateWith = 80;


/**
Ready call for jsPlumb library
*/
jsPlumb.ready(function() {
    $(window).resize(function(){jsPlumb.repaintEverything();});
    $("#plumbArea").bind("contextmenu", function(e){return false;});
    jsp = jsPlumb.getInstance({
        connector: ["Flowchart", { stub: [40, 60], gap: 10, cornerRadius: 5 }],
        Endpoints : [[ "Dot", {radius: 5} ], [ "Dot", { radius: 7 } ]],
        EndpointStyles : [{ fillStyle: '#225588' }, { fillStyle: '#558822' }],
        hoverPaintStyle: endpointHoverStyle
    });
    jsp.Defaults.Container = "plumbArea";
    jsp.importDefaults({
        DragOptions : {cursor: "pointer", zIndex: -2},
        HoverClass: connectorHoverStyle,
        ConnectionOverlays : [[ "Arrow", {width: 15, location: 0.6, height: 10, id: "arrow"}]]
    });
});

function findElement(array, elementId) {
    for (var i = 0; i < array.length; i++) {
        if(array[i] !== null && array[i].id === elementId) {
            return array[i];
        }
    }
    return null;
}

function openProteinModal(connection){
    currentConnection = connection;
    usedProteins = {};
    var cons = jsPlumb.getConnections();
    for(var i=0;i<cons.length;i++) usedProteins[cons[i].protein] = cons[i].sourceId;
    proteinModal.modal("show");
    makeProteinList(connection);
}

function setProtein() {
    if(selectedProtein === ""){
        alertError("Invalid protein selection!");
        return;
    }
    currentConnection.protein = selectedProtein.input1;
    setLabel(currentConnection);
    var other = connSourceOther(currentConnection);
    for(var i=0;i<other.length;i++){
    	other[i].protein = selectedProtein.input1;
    	setLabel(other[i]);
    }
    proteinModal.modal("hide");
}

function setLabel(con) {
    con.removeOverlay("label");
    var location = (con.targetId === "output")? 0.4 : 0.7;
    con.addOverlay([ "Label", {label: con.protein, location: location, cssClass: "aLabel", id:"label"}]);
}

function makeConnection(params) {
    if(params.sourceId === params.targetId) {
        notify("Cannot connect to self.", "Warning");
        return false;
    }
    var element = findElement(circuit, params.sourceId.replace("#",""));
    if(element === null) {
        notify("Invalid element: " + params.sourceId, "Warning");
        return false;
    }
    currentConnection = params.connection;
    var other = connSourceOther(params.connection);
    if(other.length > 0){
    	var otherTarget = connTargetHasOther(params.connection);
    	if(otherTarget !== "" && andMap[other[0].protein][otherTarget] === undefined){
    		notify("Proteins "+other[0].protein+" and "+otherTarget+" can not lead to the same AND-gate with this library.", "Warning")
    		return false;
    	}
    	for(var i=0;i<other.length;i++){
    		if(other[0].protein !== ""){
	    		params.connection.protein = other[0].protein;
	    		setLabel(params.connection);
	    		break;
	    	}
    	}
    }
    else params.connection.protein = "";
     
    params.connection.addOverlay([ "Arrow", { width:15, location: 0.65,height:10, id:"arrow" }]);
    params.connection.bind("click", function(connection){ openProteinModal(connection); });
    params.connection.bind("contextmenu", function(connection){ 
        if (confirm("Delete connection from " + connection.sourceId + " to " + connection.targetId + "?")) {
            jsPlumb.detach(connection);
        }
        return false;
    });
    
    return true;
}

/**
Wrapper for adding multiple endPoints
*/
function addEndPoints(inputs, outputs, element) {
    for(var i = 0; i < inputs; i++) {
        jsPlumb.addEndpoint(
            element.id,
            {
                endpoint:"Dot",
                paintStyle:{ fillStyle:"#558822",radius:9 },
                hoverPaintStyle: endpointHoverStyle,
                isTarget:true,
                maxConnections: 1,
                anchor: [0, (1 / (inputs+1)) * (i + 1), -1, 0],
                beforeDrop: makeConnection,
                dropOptions: dropOptions
            }
        );
    }

    for(var j = 0; j < outputs; j++) {
        jsPlumb.addEndpoint(
            element.id,
            {
                endpoint:"Dot",
                paintStyle:{ fillStyle:"#225588",radius:7 },
                isSource: true,
                connector:[ "Flowchart", { cornerRadius:5 } ],
                connectorStyle: connectorPaintStyle,
                hoverPaintStyle: endpointHoverStyle,
                connectorHoverStyle: connectorHoverStyle,
                maxConnections: -1,
                anchor: [1, (1 / (outputs+1)) * (j + 1), 1, 0],
                ConnectionOverlays : [ [ "Label", {label:" ", location: 0.25, cssClass: "aLabel", id:"label"}]],
            }
        );
    }
}

function repaintElement(elementId) {
    jsPlumb.selectEndpoints({element:elementId}).repaint();
    jsPlumb.selectEndpoints({element:elementId}).each(function(endpoint){
        var conns = endpoint.getAttachedElements();
        for(var i = 0; i < conns.length; i++) {
            conns[i].repaint();
        }
    });
}

function parseJsPlumb() {
    var network = {};
    var plumb = jsPlumb.getConnections();
    network.vertices = [];
    network.edges = [];
    for(var i = 0; i < circuit.length; i++) {
        if(circuit[i] !== null){
            network.vertices.push({
                id: circuit[i].id,
                type: circuit[i].type,
                x: circuit[i].x,
                y: circuit[i].y
            });
        }
    }
    for(var j = 0; j < plumb.length; j++) {
    	var trg = plumb[j].target.selector.replace("#","");
        network.edges.push({
            source: plumb[j].source.selector.replace("#",""),
            target: (trg == "" ? "output" : trg),
            protein: plumb[j].protein
        });
    }
    return network;
}

function makeDraggable(div, gate) {
    jsPlumb.draggable(div, {
        containment: "parent",
        grid: [20, 20],
        stop: function() {
            gate.x = div.position().left;
            gate.y = div.position().top;
        }
    });
}

function InputGate() {
	this.id = "input";
	this.type = "input";

	var gate = $('<div>', {
		id: this.id
	});
	$('#plumbArea').append(gate);

	var text = $('<p>').appendTo(gate);
	text.css('margin', "15px 30px");
	gate.css({
        border: "2px dashed black",
        position: "absolute",
        left: 0,
        height: "50%",
        width: "80px"
	});
	text.text(this.id);

	this.x = gate.position().left;
	this.y = gate.position().top;
	circuit.push(this);

	return gate;
}

function OutputGate() {
	this.id = "output";
	this.type = "output";

	var gate = $('<div>', {
		id: this.id
	});
	$('#plumbArea').append(gate);

	var text = $('<p>').appendTo(gate);
	text.css('margin', "15px 30px");
	gate.css({
		border: '2px dashed brown',
		position: "absolute",
        right: 0,
        height: "50%",
        width: "80px"
	});
	text.text(this.id);

	this.x = gate.position().left;
	this.y = gate.position().top;
	circuit.push(this);
	
	makeDraggable(gate, this);

	return gate;
}
/**
Gate constructor
*/
function Gate(name, inputs, outputs, image,px,py) {
    this.id = name + circuit.length;
    this.type = name;
    var gate = $('<div/>', {
        id: this.id,
        class: "gateElement",
    });
    gate.offset({ top: py, left: px });
    $("#plumbArea").append(gate);

    if(image === null) {
        var text = $("<p>").appendTo(gate);
        text.css("padding", "15px 30px");
        gate.css("border", "1px solid black");
        text.text(this.id);
    } else {
        $("<img>",{
            src: image,
            height: gateHeight,
            width: gateWith
        }).appendTo(gate);
    }

    this.x = gate.position().left;
    this.y = gate.position().top;

    addEndPoints(inputs, outputs, this);
    makeDraggable(gate, this);
    circuit.push(this);

    makeDeletable(this);
}

function makeDeletable(gate){
    $('#' +	gate.id).bind('contextmenu', {gate: gate}, function(event){
		if(confirm("Delete this gate?")){
			gate = event.data.gate;
			var i = circuit.indexOf(gate);
			if(~i){
				circuit[i] = null; // Replace instead of remove because the ids depend on circuit.length...
			}

			jsPlumb.detachAllConnections(gate);
			jsPlumb.removeAllEndpoints(gate);
			$("#" + gate.id).remove();
		}
		return false;
	});
}

/**
Protein constructor
*/
function Protein(id, data) {
    if(!(id < 0)){
        this.id = id;
        this.data = data;
    }
}

/**
Wrapper for simple creation of AND gates
*/
function andGate(posx,posy) {
    return new Gate("and", 2, 1, "assets/images/AND_gate.png",posx,posy);
}

/**
Wrapper for simple creation of NOT gates
*/
function notGate(posx,posy) {
    return new Gate("not", 1, 1, "assets/images/NOT_gate.png",posx,posy);
}

/**
Provided a connection this method will return the protein
linked to the other input if it exists, or "" otherwise
*/
function connTargetHasOther(connection){
	if(connection === undefined){
		return "";
    }
    
    var targetId = connection.target.selector.replace("#","");
    var sourceId = connection.source.selector.replace("#","");
    var other = jsPlumb.getConnections({
        target: targetId
    });
    
    for(var i = 0; i < other.length; i++){
        if(other[i] !== connection && other[i].protein !== undefined)
            return other[i].protein;
    }
    return "";
}

/**
 * Returns an array of connections that share the same source gate as the input connection.
 */
function connSourceOther(connection){
	var res = Array();
	if(connection === undefined || connection.sourceId == "input") return res;
    var sourceId = connection.source.selector.replace("#","");
    var other = jsPlumb.getConnections({
            source: sourceId
        });    
    for(var i = 0; i < other.length; i++){
        if(other[i] !== connection) res.push(other[i]);
    }
    return res;
}

/**
 *  make a custom gate by exploding the network it represents into the workspace
 */
function makeCustomGate(id,posx,posy) {
    var data;
    for(var i = 0; i < customGates.length; i++) {
        if(customGates[i].name === id)
            data = customGates[i];
    }
    if(data === undefined)
        return false;
    
    var nodes = {};
    
    function reduceFn(toFind) {
        return function(prev, next, idx, arr){
            if(next.id === toFind || (next.id === undefined && nodes[next] !== undefined && next === toFind))
                return next;
            return prev;
        };
    }
    
    function filterFn(testProp) {
        return function(el, idx, arr){
            if(el[testProp])
                return true;
            return false;
        };
    }
    // generate edges one at a time, making nodes as needed
    for(var i = 0; i < data.edges.length; i++) {
        // firstTimeTarget is used to determine which endpoint of an and gate to connect to
        var firstTimeTarget = false;
        var source = nodes[Object.getOwnPropertyNames(nodes).reduce(reduceFn(data.edges[i].source), undefined)];
        if(!source) {
            var src = data.nodes.reduce(reduceFn(data.edges[i].source),undefined);
            source = src.type === "not" ? notGate(posx+src.x, posy+src.y) : andGate(posx+src.x, posy+src.y);
            nodes[data.edges[i].source] = source;
        }
        var target = nodes[Object.getOwnPropertyNames(nodes).reduce(reduceFn(data.edges[i].target),undefined)];
        if(!target){
            firstTimeTarget = true;
            var tgt = data.nodes.reduce(reduceFn(data.edges[i].target), undefined);
            target = tgt.type === "not" ? notGate(posx+(i+1)*100, posy+(i+1)*100) : andGate(posx+(i+1)*100, posy+(i+1)*100);
            nodes[data.edges[i].target] = target;
        }
        else
            firstTimeTarget = false;
        // thanks to Anton for figuring out it had to be the EPs, not just the DIVs
        var srcEPs = jsPlumb.getEndpoints(source.id);
        var tgtEPs = jsPlumb.getEndpoints(target.id);
        var conn = jsPlumb.connect({
            "source": srcEPs.filter(filterFn("isSource"))[0],
            "target": tgtEPs.filter(filterFn("isTarget"))[firstTimeTarget ? 0 : 1]
        });
        makeConnection({
            "sourceId": source.id,
			"targetId": target.id,
            "scope": jsPlumb.Defaults.Scope,
            "connection": conn
        });
        conn.protein = data.edges[i].protein;
        conn.removeOverlay("Label");
        conn.addOverlay([ "Label", {label: data.edges[i].protein, location: 0.7, cssClass: "aLabel", id:"label"}]);
    }
}

/**
Testing drag and drop
*/
$(function() {
    $('.product').draggable({
        revert: "invalid",
		helper: "clone",
    });

    $('#plumbArea').droppable({
		accept: '.product',
        drop: function(event, ui) {
            var posx = event.pageX + $('#plumbArea').scrollLeft() - $('#plumbArea').offset().left - 30;
            var posy = event.pageY + $('#plumbArea').scrollTop() - $('#plumbArea').offset().top - 30;
            var id = ui.draggable.attr("id");
            
            if(id === "ng") {
                notGate(posx,posy);
            } else if(id === "ag") {
                andGate(posx,posy);
            } else {
                makeCustomGate(id,posx,posy);
            }
        }
    });
});

function makeInput(){
    if(gin !== null){
        $("#input").remove();
    }
    gin = new InputGate();
    jsPlumb.makeSource(gin, {
        anchor:[ "Perimeter", { shape:"Rectangle"} ],
        connector:[ "Flowchart", { cornerRadius:5 } ],
        connectorStyle: connectorPaintStyle,
        connectorHoverStyle: connectorHoverStyle
    });
}

function makeOutput(){
    if(gout !== null){
        $("#output").remove();
    }
    gout = new OutputGate();
    jsPlumb.makeTarget(gout, {
        deleteEndpointsOnDetach: false,
        anchor:[ "Perimeter", { shape:"Rectangle"} ],
        dropOptions: $.extend(dropOptions, 
            {drop: function(event, ui){
	            connections = jsPlumb.getConnections({target: 'output'});
                connections.forEach(function(con){
                	makeConnection({connection:con, targetId:con.targetId, sourceId:con.sourceId});
				});
	        }}
        )
    });
}

function resetWorkspace(confirmed){
    var confirmedReset = confirmed || confirm("Are you sure you want to reset?\nMake sure you saved first.");
    if(confirmedReset){
    	hardReset();
    	setupModal.modal("show");	
    }
}

function hardReset(){
	circuit = [];
    currentConnection = null;
    resetInputs();
    jsPlumb.detachEveryConnection();
    jsPlumb.deleteEveryEndpoint();
    $("#plumbArea").empty();
    $("#circuitNameTag").text("");
    circuitName = "";
    makeInput();
    makeOutput();
    disableResults();
}

function getAllCircuits(){
    jsRoutes.controllers.Application.getallcircuits().ajax({
        success: function(response) {
            displayCircuits(response);
        },
        error: function(response) { alertError("Error while getting circuits."); }
    });
}

function displayCircuits(json){
    var circuits = $.parseJSON(json);
}
