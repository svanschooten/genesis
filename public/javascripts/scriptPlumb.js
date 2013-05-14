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
var outputStyle =  {
    endpoint:"Dot",
    paintStyle:{ fillStyle:"#225588",radius:7 },
    isSource: true,
    connector:[ "Flowchart", { cornerRadius:5 } ],
    connectorStyle: connectorPaintStyle,
    hoverPaintStyle: endpointHoverStyle,
    connectorHoverStyle: connectorHoverStyle,
    maxConnections:-1,
    anchor: [1, (1 / (outputs.length+1)) * (i + 1), 1, 0]
};
var inputStyle = {
    endpoint:"Dot",
    paintStyle:{ fillStyle:"#558822",radius:11 },
    hoverPaintStyle: endpointHoverStyle,
    isTarget:true,
    anchor: [0, (1 / (inputs.length+1)) * (i + 1), -1, 0]
};
var endpointHoverStyle = {fillStyle:"#2e2aF8"};


/**
Ready call for jsPlumb library
*/
jsPlumb.ready(function(){

    jsPlumb.Defaults.Container = "plumbArea";
    jsPlumb.importDefaults({
        DragOptions : { cursor: "pointer", zIndex:2000 },
        HoverClass: connectorHoverStyle,
        ConnectionOverlays : [[ "Arrow", { location:0.9 } ]],
    });
    var jsp = jsPlumb.getInstance({
        connector:[ "Flowchart", { stub:[40, 60], gap:10, cornerRadius:5 } ],
        endpoint:[ "Dot", { radius:5 } ],
        endpointStyle : { fillStyle: "#567567"  },
        hoverPaintStyle: endpointHoverStyle,
    });


    jsPlumb.bind("click", function(conn, originalEvent) {
        console.log(objToString(conn));
        console.log(objToString(conn.source));
        console.log(objToString(conn.target));
    });
});

/**
Connection constructor
*/
function Connection(source, targets, protein, data) {
    this.source = source;
    this.targets = targets;
    this.protein = protein;
    this.data = data;

    function addTarget(target) {
        this.targets.push(target);
        target.connectIn(this);
    }

    function removeTarget(target) {
        this.targets.removeElem(target);
        target.removeIn(this);
    }
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
                paintStyle:{ fillStyle:"#558822",radius:11 },
                hoverPaintStyle: endpointHoverStyle,
                isTarget:true,
                anchor: [0, (1 / (inputs.length+1)) * (i + 1), -1, 0]
            }
        );
    }

    for(i = 0; i < outputs.length; i++) {
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
                anchor: [1, (1 / (outputs.length+1)) * (i + 1), 1, 0]
            }
        );
    }
}

/**
Gate constructor
*/
function Gate(id, inputs, outputs, proteins, image, position, data) {
    this.id = id;
    this.inputs = inputs;
    this.outputs = outputs;
    this.proteins = proteins;
    this.position = position;
    this.image = image;
    this.x = position.x;
    this.y = position.y;
    this.data = data;

    var gate = $('<div/>', {
        id: this.id,
        style: 'height: 50px; width: 50px; border: 1px solid black;',
        background: image
    })
    .appendTo($('#plumbArea'));

    addEndPoints(inputs, outputs, this);

    gate.draggable({ containment: "parent",
        drag: jsPlumb.repaintEverything
    });
    jsPlumb.draggable(jsPlumb.getSelector("#" + this.id), {
        containment: "#plumbArea",
        grid: [20, 20]
    });

    circuit.push(this);

    function connectIn(source) {
        for(i = 0; i < inputs.length; i++) {
            if(inputs[i] == null) {
                inputs[i] = source;
                source.connectOut(this);
                break;
            }
        }
        notify("No more inputs available", "warning");
    }

    function removeIn(source) {
        this.inputs.removeElem(source);
    }

    function connectOut(target, protein, data) {
        for(i = 0; i < outputs.length; i++) {
            if(outputs[i].protein == protein) {
                outputs[i].addTarget(target);
                break;
            }
        }
        outputs.push(new Connection(this, target, protein, data));
    }

    function removeOut(target) {
        for(i = 0; i < outputs.length; i++) {
            if(outputs[i].targets.indexOf(target) != -1) {
                outputs[i].targets.removeElem(target);
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
    var gate = new Gate("and" + circuit.length, inputs, outputs, new Protein("protein" + (Math.random() * 1000).toFixed(3)), "", new Position(0,0), {});
};

/**
Wrapper for simple creation of NOT gates
*/
function notGate() {
    var inputs = new Array();
    var outputs = new Array();
    inputs.length = 1;
    outputs.length = 1;
    var gate = new Gate("not" + circuit.length, inputs, outputs, new Protein("protein" + (Math.random() * 1000).toFixed(3)), "", new Position(0,0), {});
};