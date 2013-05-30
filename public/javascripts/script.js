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

var proteinModal, resultModal, signalModal;

/**
Method that fires when the document is loaded.
Containing all the setup methods and listener setups.
*/
$(document).ready(function(){

    loadArrayScripts("", scripts,
        loadArrayScripts("lib/", libraries,
            loadPageScript()));
    jsRoutes.controllers.Application.getlibrary().ajax({
        success: function(response) {
            parseLibrary(response);
            makeProteinList();
            notify("Protein library successfully loaded!", "success");
        },
        error: function(response) { alertError(response)}
    });
    proteinModal = $("#proteinModal");
    resultModal = $("#resultModal");
    signalModal = $("#signalModal");

});


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
        'proteins.js',
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
    signalModal.modal("hide");
    var simulateData = {circuit: parseJsPlumb(), inputs: inputs};
    // jsRoutes.controllers.Application.simulate().ajax({
    jsRoutes.controllers.Application.jsontest().ajax({
        success: function(response) {
            drawGraph(parseJSONdataRickShaw(response));
            resultModal.modal("show");
        },
        error: function(response) { alertError(response)}
    });
}
