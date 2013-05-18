/**
Authors:
-Stijn van Schooten
*/

//Globals
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

function logCircuit() {
    var div = $("#circuitDiv");
    div.html("");
    var network = jsPlumb.getConnections();
    for(i = 0; i < network.length; i++) {
        var elem = network[i];
        div.html(div.html() + "</br> " + elem.source.selector + " - " + elem.protein + " -> " + elem.target.selector)
        console.log(elem.source.selector + " - " + elem.protein + " -> " + elem.target.selector);
    }
}

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

function setProtein(connection) {
    //TODO Protein selectie en controle
    var protein = prompt("What protein do you want to bind?");
    connection.protein = protein;
    connection.removeOverlay("label");
    connection.addOverlay([ "Label", {label:protein, location: 0.25, cssClass: "aLabel", id:"label"}]);
}

function makeConnection(params) {

    var confirmed = confirm("Connect " + params.sourceId + " to " + params.targetId + "?");
    if(confirmed) {
        var element = findElement(circuit, params.sourceId.replace("#",""));
        if(element == null) {
            notify("Invalid element: " + params.sourceId, "Warning");
        } else {
            params.connection.addOverlay([ "Arrow", { width:15, location: 0.5,height:10, id:"arrow" }]);
            params.connection.bind("click", function(connection){ setProtein(connection) });
        }
    }
    return confirmed;
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
                maxConnections:-1,
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
        network.vertices.push({ id: circuit[i].id, x: circuit[i].x, y: circuit[i].y});
    }
    for(i = 0; i < plumb.length; i++) {
        network.edges.push({source: plumb[i].source.selector.replace("#",""),
            target: plumb[i].target.selector.replace("#",""),
            protein: plumb[i].protein
        });
    }
}

function makeDraggable(div, gate) {
    div.draggable({ containment: "parent",
        drag: function(){repaintElement(gate.id)},
        stop: function(){
            gate.x = div.position().left;
            gate.y = div.position().top;
            repaintElement(gate.id)
        }
    });
    jsPlumb.draggable(div, {
        containment: "#plumbArea",
        grid: [20, 20]
    });
}

/**
Gate constructor
*/
function Gate(name, inputs, outputs, image) {
    this.id = name + circuit.length;
    this.image = image;

    var gate = $('<div/>', {
        id: this.id,
        style: 'height: 80px; width: 80px;',
        background: image
    })
    .appendTo($('#plumbArea'));
    if(image == null) {
        var text = $("<p>").appendTo(gate);
        text.css("padding", "15px 30px");
        gate.css("border", "1px solid black");
        text.text(this.id);
    }


    this.x = gate.position().left;
    this.y = gate.position().top;

    addEndPoints(inputs, outputs, this);
    makeDraggable(gate, this);
    circuit.push(this);
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
function andGate() {
    var gate = new Gate("and", 2, 1, null);
};

/**
Wrapper for simple creation of NOT gates
*/
function notGate() {
    var gate = new Gate("not", 1, 1, null);
};