var andGates = new Array();
var notGates = new Array();
var endpointOptions = { isTarget:true, isSource:true };

jsPlumb.ready(function(){
    jsPlumb.Defaults.Container = "plumbArea";
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
    jsPlumb.connect({
        source:"el0",
        target:"el1",
        endpoint:"Dot"
    });
});

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

    andGates.push(this);
    var tmpId = "and" + andGates.length;

    $('<div/>', {
        id: tmpId,
        style: 'height: 50px; width: 50px; border: 1px solid black;'
    })
    .appendTo($('#plumbArea'))
    .draggable({ containment: "parent",
        stop: jsPlumb.repaintEverything()
    });
    jsPlumb.addEndpoint(tmpId, { isTarget: true, anchor: "TopLeft" });
    jsPlumb.addEndpoint(tmpId, { isTarget: true, anchor: "BottomLeft" });
    jsPlumb.addEndpoint(tmpId, { isSource: true, anchor: "Right" });
    jsPlumb.draggable($("#" + tmpId), {
        containment: "parent"
    });
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

    notGates.push(this);
    var tmpId = "not" + andGates.length;

    $('<div/>', {
        id: tmpId,
        style: 'height: 50px; width: 50px; border: 1px solid black;'
    })
    .appendTo($('#plumbArea'))
    .draggable({ containment: "parent",
        stop: jsPlumb.repaintEverything()
    });
    jsPlumb.addEndpoint(tmpId, { isTarget: true, anchor: "Left" });
    jsPlumb.addEndpoint(tmpId, { isSource: true, anchor: "Right" });
    jsPlumb.draggable($("#" + tmpId), {
        containment: "parent"
    });
};