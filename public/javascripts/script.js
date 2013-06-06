/**
Authors:
-Jeroen Peperkamp
-Stijn van Schooten
*/


//Load all the javascript libraries except for jQuery
var libraries = [ 'bootstrap.min.js',
                'element-min.js',
                //'jquery-ui-1.10.2.min.js',
                'jquery.jsPlumb-1.4.0-all-min.js',
                'jquery.ui.touch-punch.min.js'];

//Load all the standard scripts. If page specific, extend loadPageScript()
var scripts = [  ];

var proteinModal, resultModal, signalModal, setupModal, loadModal;
var circuitName, timeSpan, numSteps;
var circuitList;

/**
Method that fires when the document is loaded.
Containing all the setup methods and listener setups.
*/
$(document).ready(function(){

    loadArrayScripts("", scripts,
        loadArrayScripts("lib/", libraries,
            loadPageScript()));

    getCustomGates();
    setTimeout(wrapModals, 1000);
});

function wrapModals(){
    proteinModal = $("#proteinModal");
    resultModal = $("#resultModal");
    signalModal = $("#signalModal");
    setupModal = $("#setupModal");
    loadModal = $("#loadModal");
    getAvailableLibraries();
}


/**
Load page specific scripts if needed.
If needed on all pages: Put in scripts array.
*/
function loadPageScript() {
    switch(document.URL.split("/").pop()) {
        case "rk":
            loadScript("test/rkPlot.js");
            break;
        case "plumbtest":
            loadScript("test/plumbTest.js");
            break;
        default:
            break;
    }
    var mainLibs = [
        'rickShawPlot.js',
        'lib/rickshaw.min.js',
        'lib/d3.v3.min.js',
        'plumbWorkspace.js']
    loadArrayScripts("", mainLibs);
}

/**
Standard error message for AJAX requests and alerts.
*/
function alertError(error) {
    notify(error.responseText, "error");
}

/**
Loads an array of .js files with a prefix to simplify importing scripts and libraries.
*/
function loadArrayScripts(prefix, files, callback) {
    if(files.length != 0) {
        loadScript(prefix + files.shift(), loadArrayScripts(prefix, files, callback));
    } else {
        callback;
    }
}

/**
Loader wrapper for script loading
*/
function loadScript(script, callback) {
    $.getScript("assets/javascripts/" + script).done(callback);
}

/**
Spiffy stackable notifying method
Standard yellow alert of supply with second parameter
for different types of alerts use:
- error (red)
- success (green)
- info (blue)
*/
function notify(message, type) {
    if(type == null) {
        type = "warning";
    }
    var notificationID = "notification" + Math.floor((Math.random()*100)+1);
    var notification = $('<div/>', {
        class: "alert fade in alert-" + type.toLowerCase(),
        id: notificationID
    })
    .text(message)
    .appendTo($('#alertBox'));

    $('<strong></strong>')
    .text(type.toUpperCase() + "! ")
    .prependTo(notification);

    $('<button></button>', {
        type: "button",
        class: "close",
        'data-dismiss': "alert"
    })
    .text("×")
    .prependTo(notification);
    setTimeout(function(){disposeNotification(notificationID);}, 10000);
}

function disposeNotification(notificationID){
    $("#" + notificationID).alert('close');
}

/**
Enhancing the methods of an array
*/
Array.prototype.removeElem = function(elem)   {
    var idx = this.indexOf(elem);
    if(idx != -1)
        this.splice(idx, 1);
}

/**
Generalised object toString method. JSON.stringify does not work with cyclomatic objects.
*/
function objToString (obj) {
    var str = '';
    for (var p in obj) {
        if (obj.hasOwnProperty(p)) {
            str += p + '::' + obj[p] + '\n';
        }
    }
    return str;
}

/**
Wrapper for simpler data attribute retrieval.
The id is the id of the element and the data is the name of the attribute.
No # and no data- prefixes needed.
*/
function getData(id, data) {
    return $("#" + id.replace("#", ""))[0].getAttribute("data-" + data.replace("data-", ""))
}

function beginSimulation(){
//TODO Checken van verbindingen enzo
    signalModal.modal("show");
}

function completeSimulation(){
    //TODO Checken van inputsignalen
    inputs = $("#signalArea")[0].value;
    if(inputs == ""){
        $("#signalErrorDiv").text("No input signal given.")
    } else {
        signalModal.modal("hide");
        var simulateData = {name: circuitName, circuit: parseJsPlumb(), inputs: inputs, time: timeSpan, steps: numSteps, library: selectedLibrary};
        jsRoutes.controllers.Application.getCooking().ajax({
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
}

function showSetup(){
    setupModal.modal("show");
}

function showLoad(){
    loadModal.modal("show");
    getallCircuits();
}

function showNetworkList(){
	
}

function getallCircuits() {
	jsRoutes.controllers.Application.getallcircuits().ajax({
        method: "POST",
        success: function(response) { parseCircuits(response) },
        error: function(response) { "Unable to load circuits." }
    });
}

function parseCircuits(json) {
	var data = JSON.parse(json);
	var results = {};
	for(var i=0; i<data.length; i++){
		var cur = data[i];
		var name = Object.keys(cur)[0];
		cur = cur[name];
		var inputs = {};
		var outputs = {};
		var gateID = {};
		var allCDS = {};
		for(var j=0;j<cur["CDS"].length;j++){
			var cs = cur["CDS"][j];
			if(!(cs["next"] in inputs)) inputs[cs["next"]] = Array();
			inputs[cs["next"]].push(cs["name"]);
			if(!(cs["name"] in outputs)) outputs[cs["name"]] = Array();
			outputs[cs["name"]].push(cs["next"])
			allCDS[cs["next"]] = true; allCDS[cs["name"]] = true;
		}
		console.log(inputs);
		
		var network = new Object();
	    network.vertices = new Array();
	    network.edges = new Array();
	    network.name = name;
	    for(var j=0;j<cur["gates"].length;j++){
	    	var g = cur["gates"][j];
	    	var gate = {
	            x: g.x,
	        	y: g.y
	        }
	        if(inputs[g.name].length==2){
	        	gate.type = "and";
	        	gate.id = "and"+(j+2);
	        }
	        else if(inputs[g.name].length==1){
	        	gate.type = "not";
	        	gate.id = "not"+(j+2);
	        }
	        else{
	        	gate.type = "custom";
	        	gate.id = "custom"+(j+2);
	        }
	        gateID[g.name] = gate.id;
		    network.vertices.push(gate);
	    }
	    for(key in allCDS){
	    	if(outputs[key]==undefined){
	    		network.edges.push({
		            source: gateID[key],
		            target: "output",
		            protein: key
		        });
	    	}
	    }
	    for(key in inputs){
	    	for(var j=0;j<inputs[key].length;j++){
	    		network.edges.push({
		            source: (gateID[inputs[key][j]]==undefined ? "input" : gateID[inputs[key][j]]),
		            target: gateID[key],
		            protein: inputs[key][j]
		        });
	    	}
	    }
	    results[name] = network;
	}
	console.log(results);
	circuitList = results;
	for(key in results){
	    $("<option></option>").text(results[key].name).appendTo($("#loadNetworkSelector"));
	}
}

function saveCircuit() {
    jsRoutes.controllers.Application.savecircuit().ajax({
        data: Json.stringify(parseJsPlumb()),
        method: "POST",
        contentType: "application/json",
        success: function(response) { notify(response,"success") },
        error: function(response) { alertError(response)}
    });
}

function loadCircuit() {
	hardReset();
	var selected = $("#loadNetworkSelector").find('option:selected').text();
	var network = circuitList[selected];
	for(var i=0;i<network.vertices.length;i++){
		var cur = network.vertices[i]
		if(cur.type=="and") andGate(cur.x, cur.y);
		if(cur.type=="not") notGate(cur.x, cur.y);
	}
	
	for(var i=0;i<network.edges.length;i++){
		var cur = network.edges[i];
		//console.log(jsPlumb.getEndpoints(cur.target)[0]);
		//console.log(jsPlumb.getEndpoints(cur.source)[0]);
		var connection = jsPlumb.connect({source : cur.source, 
			target : cur.target, paintStyle: connectorPaintStyle,
			hoverPaintStyle: endpointHoverStyle, connectorHoverStyle: connectorHoverStyle
            //endpoints: [jsPlumb.getEndpoints(cur.source)[0],jsPlumb.getEndpoints(cur.target)[0]]
        });
        //jsPlumb.getEndpoints(cur.source)[0].addConnection(connection);
		
		var location = (cur.target == "output")? 0.4 : 0.7;
    	connection.addOverlay([ "Label", {label: cur.protein, location: location, cssClass: "aLabel", id:"label"}]);
		//
	}
	loadModal.modal("hide");
}

function applySetup(){
    var lib = $("#setupLibrarySelector option:selected")[0].value;
    var name = $("#circuitName")[0].value;
    if(lib == -1){
        $("#setupErrorDiv").text("Choose a library first!");
    } else if(name == ""){
        $("#setupErrorDiv").text("You must specify a name!");
    } else {
        $("#setupErrorDiv").text("");
        getLibrary($("#setupLibrarySelector option:selected")[0].value);
        circuitName = name;
        timeSpan = $("#simTimeSpan")[0].value;
        numSteps = $("#simSteps")[0].value;
        makeInput();
        makeOutput();
        setupModal.modal("hide");
    }
}

function getCustomGates(){
    jsRoutes.controllers.Application.getallcircuits().ajax({
        success: function(response) {
            showGates(parseGates(response));
        },
        error: function(response) { alertError(response)}
    });
}

/* store each custom gate as an object with edges and vertices into the customGates array
 also return a copy needed by showGates */
function parseGates(json){
    var data = $.parseJSON(json);
    // copy for showGates
    data_parsed = new Array();
    // get it sorted out
    data.forEach(function(gate) {
        var nodes = Array();
        var edges = Array();
        var inputs = gate.CDS.filter(function(cs){ if(cs.isInput) return true; else return false; });
        var outputs = gate.CDS.filter(function(cs){ if(cs.next === undefined) return true; else return false; });
        for(var i = 0; i < gate.gates.length; i++) {
            nodes.push({"id": gate.name+i, "type": null, "x":0, "y":0, "next": gate.gates[i]});
        }
        for(var i = 0; i < nodes.length; i++) {
            var idx = -1;
            for(var j = 0; j < gate.CDS.length; j++ ){
                if(gate.CDS[j].name == nodes[i].next) {
                    if(idx < 0){
                        idx1 = j;
                        break;
                    }
                }
            }
            for(var j = 0; j < nodes.length; j++){
                if(nodes[j].next == gate.CDS[idx].name)
                    edges.push({"source": nodes[i].id, "target": nodes[j].id, "protein": nodes[i].next});
            }
        }
        data_parsed.push({
            "name": gate.name,
            "inputs": inputs.length,
            "outputs": ouputs.length
            //image: ...
            // "posx": <what
            // "posy": <is this?
        });
        customGates.push({
            "name": gate.name,
            "inputs": inputs,
            "outputs": outputs,
            "edges": edges,
            "nodes": nodes
        });
    });
    return data_parsed;
}

function showGates(data) {
    var customDiv = $("#customGates");
    for(i = 0; i < data.length; i++){
        var gate = $("<div></div>",{
            id: data.name + circuit.length,
            class: "product",
            'data-inputs': data.inputs,
            'data-outputs': data.outputs,
            'data-image': data.image,
            'data-posx': data.posx,
            'data-posy': data.posy
        })
        if(data.image == null){
            $("<p>no img</p>")
            .text(data.name)
            .appendTo(gate);
        } else {
            var img = $("<img></img>",{
                height: "80px;",
                width: "80px",
                src: data.image
            })
            .appendTo(gate);
        }
        gate.appendTo(customDiv);
    }
}
