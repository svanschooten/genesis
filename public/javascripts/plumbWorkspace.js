/**
Authors:
-Stijn van Schooten
*/

//Globals
var gateid = 0
var circuit = new Array();
var endpointOptions = { isTarget:true, isSource:true };
var connectorPaintStyle = {
    lineWidth:4,
    strokeStyle:"#deea18",
    joinstyle:"round",
};
var connectorHoverStyle = {
    lineWidth:4,
    strokeStyle:"#2e2aF8"
};
var endpointHoverStyle = {fillStyle:"#2e2aF8"};
var dropOptions = {
    tolerance:"touch",
    hoverClass:"dropHover",
    activeClass:"dragActive"
};

var jsp, gin, gout;
var currentConnection = null;
var gateHeight = 80, gateWith = 80;


/**
Ready call for jsPlumb library
*/
jsPlumb.ready(function(){
    jsp = jsPlumb.getInstance({
        connector:[ "Flowchart", { stub:[40, 60], gap:10, cornerRadius:5 } ],
        Endpoints : [ [ "Dot", {radius: 5} ], [ "Dot", { radius: 7 } ]],
        EndpointStyles : [{ fillStyle:'#225588' }, { fillStyle:'#558822' }],
        hoverPaintStyle: endpointHoverStyle,
    });
    jsp.Defaults.Container = "plumbArea";
    jsp.importDefaults({
        DragOptions : { cursor: "pointer", zIndex:-2 },
        HoverClass: connectorHoverStyle,
        ConnectionOverlays : [[ "Arrow", { width:15, location: 0.6,height:10, id:"arrow" }]],
    });
});

function findElement(array, elementId) {
    for(i = 0; i < array.length; i++) {
        if(array[i].id == elementId) {
            return array[i];
        }
    }
    return null;
}

function openProteinModal(connection){
    currentConnection = connection;
    proteinModal.modal("show");
    makeProteinList(connection);
}

function setProtein() {
    //TODO Protein selectie en controle
    if(selectedProtein == ""){
        alertError("Invalid protein selection!");
        return;
    }
    currentConnection.protein = selectedProtein;
    currentConnection.removeOverlay("label");
    currentConnection.addOverlay([ "Label", {label: selectedProtein, location: 0.7, cssClass: "aLabel", id:"label"}]);
    proteinModal.modal("hide");
}

function makeConnection(params) {
    if(params.sourceId == params.targetId) {
        notify("Cannot connect to self.", "Warning");
        return false;
    }
    var element = findElement(circuit, params.sourceId.replace("#",""));
    if(element == null) {
        notify("Invalid element: " + params.sourceId, "Warning");
        return false;
    }
    params.connection.protein = "";
    params.connection.addOverlay([ "Arrow", { width:15, location: 0.65,height:10, id:"arrow" }]);
    params.connection.bind("click", function(connection){ openProteinModal(connection) });
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
    for(i = 0; i < inputs; i++) {
        jsPlumb.addEndpoint(
            element.id,
            {
                endpoint:"Dot",
                paintStyle:{ fillStyle:"#558822",radius:9 },
                hoverPaintStyle: endpointHoverStyle,
                isTarget:true,
                maxConnections: element.id.search("Output")==0 ? -1 : 1,
                anchor: [0, (1 / (inputs+1)) * (i + 1), -1, 0],
                beforeDrop: makeConnection,
                dropOptions: dropOptions
            }
        );
    }

    for(i = 0; i < outputs; i++) {
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
                maxConnections: element.id.search("Input")==0 ? -1 : 1,
                anchor: [1, (1 / (outputs+1)) * (i + 1), 1, 0],
                ConnectionOverlays : [ [ "Label", {label:" ", location: 0.25, cssClass: "aLabel", id:"label"}]],
            }
        );
    }
}

function repaintElement(elementId) {
    jsPlumb.selectEndpoints({element:elementId}).repaint();
    jsPlumb.selectEndpoints({element:elementId}).each(function(endpoint){
        var conns = endpoint.getAttachedElements();
        for(i = 0; i < conns.length; i++) {
            conns[i].repaint();
        }
    });
}

function parseJsPlumb() {
    var network = new Object();
    var plumb = jsPlumb.getConnections();
    network.vertices = new Array();
    network.edges = new Array();
    for(i = 0; i < circuit.length; i++) {
        if(circuit[i] != null){
            network.vertices.push({
                id: circuit[i].id,
                type: circuit[i].type,
                x: circuit[i].x,
                y: circuit[i].y
            });
        }
    }
    for(i = 0; i < plumb.length; i++) {
        network.edges.push({
            source: plumb[i].source.selector.replace("#",""),
            target: plumb[i].target.selector.replace("#",""),
            protein: plumb[i].protein
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
		//class: "gateElement",
	});
	$('#plumbArea').append(gate);
	
	var text = $('<p>').appendTo(gate);
	text.css('margin', "15px 30px");
	gate.css({
	    border: "2px dashed black",
	    position: "absolute",
	    left: 0,
	    height: "100%",
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
		//class: "gateElement",
	});
	$('#plumbArea').append(gate);
	
	var text = $('<p>').appendTo(gate);
	text.css('margin', "15px 30px");
	gate.css({
		border: '2px dashed brown',
		position: "absolute",
        right: 0,
        height: "100%",
        width: "80px"
	});
	text.text(this.id);
	
	this.x = gate.position().left;
	this.y = gate.position().top;
	circuit.push(this);
	
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
    })
    gate.offset({ top: py, left: px });
    $("#plumbArea").append(gate);
    
    if(image == null) {
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

    //TODO size bij groot aantal inputs vergroten
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
			i = circuit.indexOf(gate);
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
    this.id = id;
    this.data = data;
}

/**
Wrapper for simple creation of AND gates
*/
function andGate(posx,posy) {
    var gate = new Gate("and", 2, 1, "assets/images/AND_gate.png",posx,posy);
};

/**
Wrapper for simple creation of NOT gates
*/
function notGate(posx,posy) {
    var gate = new Gate("not", 1, 1, "assets/images/NOT_gate.png",posx,posy);
};

/**
Provided a connection this method will return the protein
linked to the other input if it exists, or "" otherwise
*/
function connTargetHasOther(connection){
	if(connection == undefined) return "";
    var targetId = connection.target.selector.replace("#","");
    var sourceId = connection.source.selector.replace("#","");
    var other = jsPlumb.getConnections({
            target: targetId
    });
    for(i = 0; i <  other.length; i++){
		if(other[i] != connection) return other[i].protein;
    }
    return "";
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
	                var posx = ui.offset.left - $(this).offset().left;
	        		var posy = ui.offset.top - $(this).offset().top;
	        		var id = ui.draggable.attr("id");
	        		if(id == "ng") { 
	        			notGate(posx,posy);
	        		}
	        		if(id == "ag") {
	        			andGate(posx,posy);
	        		}
	            }
        });         
});

function makeInput(){
    if(gin != null){
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
    if(gout != null){
        $("#output").remove();
    }
    gout = new OutputGate();
    jsPlumb.makeTarget(gout, {
        deleteEndpointsOnDetach: false,
        anchor:[ "Perimeter", { shape:"Rectangle"} ],
        beforeDrop: makeConnection,
        dropOptions: dropOptions
    });
}

function resetWorkspace(){
    var confirmedReset = confirm("Are you sure you want to reset?\nMake sure you saved first.");
    if(confirmedReset) {
        circuit = new Array();
        currentConnection = null;
        resetInputs();
        jsPlumb.detachEveryConnection();
        jsPlumb.deleteEveryEndpoint();
        $("#plumbArea").empty();
        makeInput();
        makeOutput();
    }
}

function saveCircuit(){
    var simulateData = {name: circuitName, circuit: parseJsPlumb(), inputs: inputs, time: timeSpan, steps: numSteps, library: selectedLibrary};
    jsRoutes.controllers.Application.savecircuit().ajax({
        data: JSON.stringify(simulateData),
        method: "POST",
        contentType: "application/json",
        success: function(response) {
            drawGraph(parseJSONdataRickShaw(response));
            signalModal.modal("hide");
            resultModal.modal("show");
        },
        error: function(response) { alertError(response)}
    });
}

function getAllCircuits(){
    jsRoutes.controllers.Application.getallcircuits().ajax({
        success: function(response) {
            displayCircuits(response);
        },
        error: function(response) { alertError(response)}
    });
}

function displayCircuits(json){
    var circuits = $.parseJSON(json);

}

function loadCircuit(circuitId){

}



