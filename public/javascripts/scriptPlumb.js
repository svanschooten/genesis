var andGates = new Array();
var notGates = new Array();
var endpointOptions = { isTarget:true, isSource:true };

jsPlumb.ready(function(){

    jsPlumb.Defaults.Container = "plumbArea";
    jsPlumb.importDefaults({
        DragOptions : { cursor: "pointer", zIndex:2000 },
        HoverClass:"connector-hover"
    });
    var jsp = jsPlumb.getInstance({
      PaintStyle:{
        lineWidth:6,
        strokeStyle:"#567567",
        outlineColor:"black",
        outlineWidth:1
      },
      Connector:[ "Bezier", { curviness: 30 } ],
      Endpoint:[ "Dot", { radius:5 } ],
      EndpointStyle : { fillStyle: "#567567"  },
      Anchor : [ 0.5, 0.5, 1, 1 ]
    });

    var elms = new Array();
    for(i = 0; i < 3; i++){
        var tmpel = jsPlumb.addEndpoint("el" + i, endpointOptions);
        $("#el" + i).draggable({ containment: "parent" });
        jsPlumb.draggable($("#el" + i), {
            containment: "parent"
        });
        elms.push(tmpel);
    }

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

function andGate() {
    this.input1 = null;
    this.input2 = null;
    this.output = null;

    function receive(source) {
        if(input1 == null) {
            receive1(source);
        } else if(input2 == null) {
            receive2(source);
        } else {
            alert("No free connections.");
        }
    }

    function connect(target) {
        this.output = target;
        target.receive(this);
    }

    function receive1(source) {
        this.input1 = source;
    }

    function receive2(source) {
        this.input2 = source;
    }


    var tmpId = "and" + andGates.length;

    $('<div/>', {
        id: tmpId,
        style: 'height: 50px; width: 50px; border: 1px solid black;'
    })
    .appendTo($('#plumbArea'))
    .draggable({ containment: "parent",
        drag: jsPlumb.repaintEverything
    });
    jsPlumb.addEndpoint(tmpId, { isTarget: true, anchor: "TopLeft" });
    jsPlumb.addEndpoint(tmpId, { isTarget: true, anchor: "BottomLeft" });
    jsPlumb.addEndpoint(tmpId, { isSource: true, anchor: "Right", maxConnections:-1 });
    jsPlumb.draggable(jsPlumb.getSelector("#" + tmpId), {
        containment: "#plumbArea"
    });
    jsPlumb.bind("dblclick",
        function(connection, originalEvent) {
            alert(JSON.stringify(connection));
        }
    );
    andGates.push(this);
};

function notGate() {
    this.input = null;
    this.output = null;

    function receive(source) {
        if(input == null) {
            this.input = source
        } else {
            alert("No free connections.");
        }
    }

    function connect(target) {
        this.output = target;
        target.receive(this);
    }


    var tmpId = "not" + notGates.length;

    $('<div/>', {
        id: tmpId,
        style: 'height: 50px; width: 50px; border: 1px solid black;'
    })
    .appendTo($('#plumbArea'))
    .draggable({ containment: "parent",
        drag: jsPlumb.repaintEverything
    });
    jsPlumb.addEndpoint(tmpId, { isTarget: true, anchor: "Left" });
    jsPlumb.addEndpoint(tmpId, { isSource: true, anchor: "Right" });
    jsPlumb.draggable(jsPlumb.getSelector("#" + tmpId), {
        containment: "#plumbArea"
    });

     notGates.push(this);
};

function objToString (obj) {
    var str = '';
    for (var p in obj) {
        if (obj.hasOwnProperty(p)) {
            str += p + '::' + obj[p] + '\n';
        }
    }
    return str;
}