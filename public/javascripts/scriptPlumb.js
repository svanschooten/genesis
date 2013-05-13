var andGates = 0;
var notGates = 0;
var circuit = new Array();
var endpointOptions = { isTarget:true, isSource:true };

var connectorPaintStyle = {
    lineWidth:4,
    strokeStyle:"#deea18",
    joinstyle:"round",
    outlineColor:"#EAEDEF",
    outlineWidth:7
};

var connectorHoverStyle = {
    lineWidth:4,
    strokeStyle:"#2e2aF8"
};

var endpointHoverStyle = {fillStyle:"#2e2aF8"};

var sourceEndpoint = {
    endpoint:"Dot",
    paintStyle:{ fillStyle:"#225588",radius:7 },
    isSource:true,
    connector:[ "Flowchart", { stub:[40, 60], gap:10, cornerRadius:5 } ],
    connectorStyle: connectorPaintStyle,
    hoverPaintStyle: endpointHoverStyle,
    connectorHoverStyle: connectorHoverStyle,
    dragOptions:{},
};

var targetEndpoint = {
    endpoint:"Dot",
    paintStyle:{ fillStyle:"#558822",radius:11 },
    hoverPaintStyle: endpointHoverStyle,
    maxConnections:-1,
    dropOptions:{ hoverClass:"hover", activeClass:"active" },
    isTarget:true,
};

jsPlumb.ready(function(){

    jsPlumb.Defaults.Container = "plumbArea";
    jsPlumb.importDefaults({
        DragOptions : { cursor: "pointer", zIndex:2000 },
        HoverClass: connectorHoverStyle,
        EndpointStyles : [{ fillStyle:'#225588' }, { fillStyle:'#558822' }],
        Endpoints : [ [ "Dot", {radius:7} ], [ "Dot", { radius:11 } ]],
    });
    var jsp = jsPlumb.getInstance({
      PaintStyle:{
        lineWidth:6,
        strokeStyle:"#567567",
        outlineColor:"black",
        outlineWidth:1
      },
      Connector:[ "Flowchart", { stub:[40, 60], gap:10, cornerRadius:5 } ],
      Endpoint:[ "Dot", { radius:5 } ],
      EndpointStyle : { fillStyle: "#567567"  },
    });


    jsPlumb.bind("click", function(conn, originalEvent) {
        console.log(objToString(conn));
        console.log(objToString(conn.source));
        console.log(objToString(conn.target));
    });
});

function notify(message, type) {
    alert(type + "! " + message);//TODO hier een mooi bootstrap element voor gebruiken.
}

function removeElem(array, elem) {
    if(array.indexOf(elem) != -1)
        array.splice(array.indexOf(elem), 1);
}

function objToString (obj) {
    var str = '';
    for (var p in obj) {
        if (obj.hasOwnProperty(p)) {
            str += p + '::' + obj[p] + '\n';
        }
    }
    return str;
}


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
        removeElem(this.targets, target);
        target.removeIn(this);
    }
}

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

    $('<div/>', {
        id: this.id,
        style: 'height: 50px; width: 50px; border: 1px solid black;',
        background: image
    })
    .appendTo($('#plumbArea'))
    .draggable({ containment: "parent",
        drag: jsPlumb.repaintEverything
    });
    for(i = 0; i < inputs.length; i++) {
        jsPlumb.addEndpoint(this.id, sourceEndpoint, { isTarget: true, anchor: [0, (1 / (inputs.length+1)) * (i + 1), -1, 0] });
    }
    for(i = 0; i < outputs.length; i++) {
        jsPlumb.addEndpoint(this.id, targetEndpoint, { isSource: true, anchor: [1, (1 / (outputs.length+1)) * (i + 1), 1, 0] });
    }
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
        removeElem(this.inputs, source);
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
                removeElem(outputs[i].targets, target);
            }
        }
    }
}

function Protein(id, data) {
    this.id = id;
    this.data = data;
}

function Position(x, y) {
    this.x = x;
    this.y = y;
}

function andGate() {
    var inputs = new Array();
    var outputs = new Array();
    inputs.length = 2;
    outputs.length = 1;
    var gate = new Gate("and" + andGates.length, inputs, outputs, new Protein("protein" + (Math.random() * 1000).toFixed(3)), "", new Position(0,0), {});
    andGates = andGates + 1;
};

function notGate() {
    var inputs = new Array();
    var outputs = new Array();
    inputs.length = 1;
    outputs.length = 1;
    var gate = new Gate("not" + notGates.length, inputs, outputs, new Protein("protein" + (Math.random() * 1000).toFixed(3)), "", new Position(0,0), {});
    notGates = notGates + 1;
};