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
    for(i = 0; i < circuit.length; i++) {
        var elem = circuit[i];
        console.log(elem.outputs.length);
        for(j = 0; j < elem.outputs.length; j++) {
            var out = elem.outputs[j];
            console.log(elem.id + " - " + out.protein.id);
            console.log("x:" + elem.position.x + " y:" + elem.position.y);
            for(k = 0; k < out.targets.length; k++) {
                console.log("    -> " + out.targets[k].id);
            }
        }
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

/**
Connection constructor
*/
function Connection(source, target, protein, data) {
    this.source = source;
    this.targets = [target];
    this.protein = protein;
    this.data = data;

    target.connectIn(this);

    function addTarget(target) {
        this.targets.push(target);
        target.connectIn(this);
    }

    function removeTarget(target) {
        this.targets.removeElem(target);
        target.removeIn(this);
    }
}

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
        var e = jsPlumb.addEndpoint(
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
        e.bind("mouseup", function(endpoint) { });
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
        style: 'height: 80px; width: 80px; border: 1px solid black;',
        background: image
    })
    .appendTo($('#plumbArea'));

    this.x = gate.position().left;
    this.y = gate.position().top;

    addEndPoints(inputs, outputs, this);
    makeDraggable(gate, this);
    circuit.push(this);

/*
    function connectIn(source) {
        for(i = 0; i < inputs.length; i++) {
            if(inputs[i] == null) {
                inputs[i] = source;
                break;
            }
        }
        notify("No more inputs available", "Warning");
    }

    function removeIn(source) {
        this.inputs.removeElem(source);
    }

    function connectOut(target, protein, data) {
        for(i = 0; i < outputs.length; i++) {
            if(outputs[i].protein == protein) {
                outputs[i].addTarget(target);
                break;
            } else if(outputs[i] == null) {
                outputs.push(new Connection(this, target, protein, data));
            }
        }
        notify("No more outgoing connections possible from " + this.id, "Warning");
    }

    function removeOut(target) {
        for(i = 0; i < outputs.length; i++) {
            if(outputs[i].targets.indexOf(target) != -1) {
                outputs[i].targets.removeElem(target);
                break;
            }
        }
    }
    */
}

/**
Protein constructor
*/
function Protein(id, data) {
    this.id = id;
    this.data = data;
}

/**
Position constructor

function Position(x, y) {
    this.x = x;
    this.y = y;
}*/

/**
Wrapper for simple creation of AND gates
*/
function andGate() {
    var gate = new Gate("and", 2, 1, "");
};

/**
Wrapper for simple creation of NOT gates
*/
function notGate() {
    var gate = new Gate("not", 2, 1, "");
};