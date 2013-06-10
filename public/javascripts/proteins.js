/*jshint -W099, smarttabs: true, forin:true, noarg:true, noempty:true, eqeqeq:true, unused:true, curly:false, browser:true, jquery:true, indent:4, maxerr:50 */
/**
Script to integrate the protein library and selection into the front end.
authors:
- Stijn van Schooten
*/

var inputs = "";

var cdsMap = {};
var andMap = {};
var selectedProtein = "";
var selectedLibrary = -1;

function makeProteinList(connection){
	$("#proteinListDiv").empty();
	clearParameters();
	var fromSource = connection.sourceId.toLowerCase().indexOf("input") !== -1;
	var toSink = connection.targetId.toLowerCase().indexOf("output") !== -1;
	var toAndGate = connection.targetId.toLowerCase().indexOf("and") !== -1;
	var o = connTargetHasOther(connection);
	if(o !== ""){
		var ind = 0;
		for(var key in andMap[o]) {
	        var oddClass = (ind % 2) === 0 ? "" : " odd";
	        ind += 1;
	        var span = $("<span></span>")
	        .addClass(oddClass + " spanProtein")
	        .text(key)
	        .appendTo($("#proteinListDiv"));
	        $("<input>", {
	            type: "radio",
	            name: "proteinSelector",
	            class: "proteinSelector",
	            input1: key,
	            input2: o
	        })
	        .prependTo(span);
	        $("<br>").appendTo($("#proteinListDiv"));
	    }
	}
	else{
		var ind = 0;
	    for(var key in cdsMap) {
	        var oddClass = (ind % 2) === 0 ? "" : " odd";
	        ind += 1;
	        var span = $("<span></span>", {
	            class: oddClass + " spanProtein"
	        })
	        .text(key)
	        .appendTo($("#proteinListDiv"));
	        $("<input>", {
	            type: "radio",
	            name: "proteinSelector",
	            class: "proteinSelector",
	            input1: key,
	            input2: -1
	        })
	        .prependTo(span);
	        $("<br>").appendTo($("#proteinListDiv"));
	    }
	}
    $(".proteinSelector").click(function(){
        selectedProtein = {input1:$(this)[0].getAttribute("input1"), input2:$(this)[0].getAttribute("input2")};
        updateParameters(fromSource, toSink, toAndGate && o === "");
    });
}

function parseLibrary(json) {
	var obj = $.parseJSON(json);
	var cds = obj.cds;
	for(var i = 0; i < cds.length; i++){
		for(var key in cds[i]){
			cdsMap[key] = {name:key, pd1:cds[i][key].d1, pd2:cds[i][key].d2, pk2:cds[i][key].k2};
		}
	}
	var nots = obj.not;
	for(i = 0; i < nots.length; i++){
		for(var key in nots[i]){
			cdsMap[key].pk1 = nots[i][key].k1;
			cdsMap[key].pkm = nots[i][key].km;
			cdsMap[key].pn = nots[i][key].n;
		}
	}
	var ands = obj.and;
	for(i = 0; i < ands.length; i++){
		var obj1 = {name:ands[i].input2, pk1:ands[i].k1, pkm:ands[i].km, pn:ands[i].n};
		if(!andMap.hasOwnProperty(ands[i].input1)) andMap[ands[i].input1] = {};
		andMap[ands[i].input1][ands[i].input2] = obj1;
		
		var obj2 = {name: ands[i].input1, pk1: ands[i].k1, pkm: ands[i].km, pn: ands[i].n};
		if(!andMap.hasOwnProperty(ands[i].input2)) andMap[ands[i].input2] = {};
		andMap[ands[i].input2][ands[i].input1] = obj2;
	}
}

function findProtein(name) {
    var res = null;
    $.each(proteinLibrary, function(index, protein) {
        if(name === protein.name){
            res = protein;
        }
    });
    return res;
}

function updateParameters(fromSource, toSink, toAndGate){
	var prot = {};
	if(selectedProtein.input2 !== -1){
		prot = andMap[selectedProtein.input1][selectedProtein.input2];
		prot.pd1 = cdsMap[selectedProtein.input1].pd1;
		prot.pd2 = cdsMap[selectedProtein.input1].pd2;
		prot.pk2 = cdsMap[selectedProtein.input1].pk2;
	}
	else prot = cdsMap[selectedProtein.input1];
	if(fromSource){
		$("#pd1").text("n/a");
	    $("#pd2").text("n/a");
	    $("#pk2").text("n/a");
	}
	else{
		$("#pd1").text(prot.pd1);
	    $("#pd2").text(prot.pd2);
	    $("#pk2").text(prot.pk2);
    }
    if(toSink){
	    $("#pk1").text("n/a");
	    $("#pkm").text("n/a");
	    $("#pn").text("n/a");
    }
    else if(toAndGate){
    	var txt = "Please connect the gate's other input.";
    	$("#pk1").text(txt);
    	$("#pkm").text(txt);
    	$("#pn").text(txt);
    }
    else{
        $("#pk1").text(prot.pk1);
    	$("#pkm").text(prot.pkm);
    	$("#pn").text(prot.pn);
    }
}

function clearParameters(){
	$("#pd1").text("");
	$("#pd2").text("");
    $("#pk2").text("");
    $("#pk1").text("");
    $("#pkm").text("");
    $("#pn").text("");
}

function getLibrary(libraryId){
    jsRoutes.controllers.Application.getlibrary().ajax({
        method: "POST",
        contentType: "application/json",
        data: JSON.stringify({id: libraryId}),
        success: function(response) {
            selectedLibrary = libraryId;
            parseLibrary(response);
            notify("Protein library successfully loaded!", "success");
        },
        error: function(response) { alertError(response); }
    });
}

function getAvailableLibraries(){
    jsRoutes.controllers.Application.getalllibraries().ajax({
        success: function(response) {
            setupLibrarySelector($.parseJSON(response));
        },
        error: function(response) { alertError(response); }
    });
}

function setupLibrarySelector(libraries) {
    var selector = $("#setupLibrarySelector");
    for(var i = 0; i < libraries.length; i++) {
        $("<option></option>",{
            value: libraries[i].libraryId
        })
        .text(libraries[i].libraryname)
        .appendTo(selector);
    }
    showSetup();
}

function resetInputs() {
    inputs = "";
}