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

function logCircuit() {
    for(i = 0; i < circuit.length; i++) {
        var elem = circuit[i];
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

    jsPlumb.Defaults.Container = "plumbArea";
    jsPlumb.importDefaults({
        DragOptions : { cursor: "pointer", zIndex:2000 },
        HoverClass: connectorHoverStyle,
        ConnectionOverlays : [[ "Arrow", { location:-40 } ]],
    });
    var jsp = jsPlumb.getInstance({
        connector:[ "Flowchart", { stub:[40, 60], gap:10, cornerRadius:5 } ],
        Endpoints : [ [ "Dot", {radius: 5} ], [ "Dot", { radius: 7 } ]],
        EndpointStyles : [{ fillStyle:'#225588' }, { fillStyle:'#558822' }],
        hoverPaintStyle: endpointHoverStyle,
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

function makeConnection(params) {

    var confirmed = confirm("Connect " + params.sourceId + " to " + params.targetId + "?");
    if(confirmed) {
        var element = findElement(circuit, params.sourceId.replace("#",""));
        if(element == null) {
            notify("Invalid element: " + params.sourceId, "Warning");
        } else {
        //TODO hier misschien protein kiezen.
            element.connectOut(params.targetId.replace("#",""), new Protein(Math.random().toFixed(2)*100,  {}), {})
        }
    }
    return confirmed;
}

/**
Wrapper for adding multiple endPoints
*/
function addEndPoints(inputs, outputs, element) {
    for(i = 0; i < inputs.length; i++) {
        jsPlumb.addEndpoint(
            element.id,
            {
                endpoint:"Dot",
                paintStyle:{ fillStyle:"#558822",radius:7 },
                hoverPaintStyle: endpointHoverStyle,
                isTarget:true,
                anchor: [0, (1 / (inputs.length+1)) * (i + 1), -1, 0],
                beforeDrop: makeConnection,
                dropOptions: dropOptions
            }
        );
    }

    for(i = 0; i < outputs.length; i++) {
        jsPlumb.addEndpoint(
            element.id,
            {
                endpoint:"Dot",
                paintStyle:{ fillStyle:"#225588",radius:5 },
                isSource: true,
                connector:[ "Flowchart", { cornerRadius:5 } ],
                connectorStyle: connectorPaintStyle,
                hoverPaintStyle: endpointHoverStyle,
                connectorHoverStyle: connectorHoverStyle,
                maxConnections:-1,
                anchor: [1, (1 / (outputs.length+1)) * (i + 1), 1, 0],
                ConnectionOverlays : [ "Arrow" ],
            }
        );
    }
}

/**
Gate constructor
*/
function Gate(id, inputs, outputs, image, position, data) {
    this.id = id;
    this.inputs = inputs;
    this.outputs = outputs;
    this.position = position;
    this.image = image;
    this.position = position;
    this.data = data;

    var gate = $('<div/>', {
        id: this.id,
        style: 'height: 50px; width: 50px; border: 1px solid black;',
        background: image
    })
    .appendTo($('#plumbArea'));

    addEndPoints(inputs, outputs, this);

    gate.draggable({ containment: "parent",
        drag: function(){jsPlumb.selectEndpoints({element:this.id}).repaint()},
        stop: function(){
        position.x = $("#" + this.id).position().left;
        position.y = $("#" + this.id).position().top;
        jsPlumb.selectEndpoints({element:this.id}).repaint();}
    });
    jsPlumb.draggable(gate, {
        containment: "#plumbArea",
        grid: [20, 20]
    });

    circuit.push(this);

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
*/
function Position(x, y) {
    this.x = x;
    this.y = y;
}

/**
Wrapper for simple creation of AND gates
*/
function andGate() {
    var inputs = new Array();
    var outputs = new Array();
    inputs.length = 2;
    outputs.length = 1;
    var gate = new Gate("and" + circuit.length, inputs, outputs, "", new Position(0,0), {});
};

/**
Wrapper for simple creation of NOT gates
*/
function notGate() {
    var inputs = new Array();
    var outputs = new Array();
    inputs.length = 1;
    outputs.length = 1;
    var gate = new Gate("not" + circuit.length, inputs, outputs, "", new Position(0,0), {});
};