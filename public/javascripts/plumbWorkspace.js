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

var jsp;
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
        DragOptions : { cursor: "pointer", zIndex:2000 },
        HoverClass: connectorHoverStyle,
        ConnectionOverlays : [[ "Arrow", { location:-40 } ]],
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
}

function setProtein() {
    //TODO Protein selectie en controle
    if(selectedProtein == ""){
        alertError("Invalid protein selection!");
        return;
    }
    currentConnection.protein = selectedProtein["input1"];
    currentConnection.removeOverlay("label");
    currentConnection.addOverlay([ "Label", {label: selectedProtein["input1"], location: 0.3, cssClass: "aLabel", id:"label"}]);
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
    params.connection.addOverlay([ "Arrow", { width:15, location: 0.7,height:10, id:"arrow" }]);
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
                //maxConnections:-1,
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

/**
Gate constructor
*/
function Gate(name, inputs, outputs, image,px,py) {
    this.id = name + circuit.length;
    this.type = name;
	/*
	var gate = $('<div style="left:' + positionx + ';top:'+ positiony +'"></div>', {
            id: this.id,
        class: "gateElement",
    })
    .appendTo($('#plumbArea'));
    );
    
    */
    
    var gate = $('<div/>', {
        id: this.id,
        class: "gateElement",
    })
    gate.offset({ top: py, left: px });
    //var gate = $("<div class = 'gateElement', id='this.id'></div>");
    $("#plumbArea").append(gate);
    //.appendTo($('#plumbArea'));
    
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


    this.x = gate.position().left;
    this.y = gate.position().top;

	
    addEndPoints(inputs, outputs, this);
    makeDraggable(gate, this);
    circuit.push(this);
    
    $('#'+	this.id).bind('contextmenu', {gate: this}, function(event){
		if(confirm("Delete this gate?")){
			
			gate = event.data.gate
			i = circuit.indexOf(gate);
			if(~i){
				circuit[i] = null; // Replace instead of remove because the ids depend on circuit.length...
			}
			
			jsPlumb.detachAllConnections(this);
			jsPlumb.removeAllEndpoints(this);
			$(this).remove();
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


function connSourceHasOther(connection){
    return jsPlumb.getConnections({
            target: connection.target.selector.replace("#","")
    });
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
