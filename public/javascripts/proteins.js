/**
Script to integrate the protein library and selection into the front end.
authors:
- Stijn van Schooten
*/

var inputs = "";

var proteinLibrary = new Array();
var selectedProtein = "";
var selectedLibrary = -1;

function makeProteinList(){
    $("#proteinListDiv").empty();
    for(i = 0; i < proteinLibrary.length; i++) {
        var oddClass = (i % 2) == 0 ? "" : " odd";
        var span = $("<span></span>", {
            class: oddClass + " spanProtein"
        })
        .text(proteinLibrary[i].name)
        .appendTo($("#proteinListDiv"));
        $("<input>", {
            type: "radio",
            name: "proteinSelector",
            class: "proteinSelector",
            value: proteinLibrary[i].name
        })
        .prependTo(span);
        $("<br>").appendTo($("#proteinListDiv"));
    }
    $(".proteinSelector").click(function(){
        selectedProtein = this.value;
        updateParameters();
    })
}

function parseLibrary(json) {
	var obj = $.parseJSON(json);
	var cds = obj.cds;
	proteinLibrary = [];
	var cdsMap = {};
	var andMap = {};
	for(i = 0; i < cds.length; i++){
		for(var key in cds[i]){
			cdsMap[key] = {name:key, pd1:cds[i][key]["d1"], pd2:cds[i][key]["d2"], pk2:cds[i][key]["k2"]}
		}
	}
	if(true){ //if this connection is input of a NOT gate
		var nots = obj.not;
		for(i = 0; i < nots.length; i++){
			for(var key in nots[i]){
				cdsMap[key]["pk1"] = nots[i][key]["k1"];
				cdsMap[key]["pkm"] = nots[i][key]["km"];
				cdsMap[key]["pn"] = nots[i][key]["n"];
				proteinLibrary.push(cdsMap[key]);
			}
		}		
	}
	else if(true){ //if this connection is input of an AND gate
		var ands = obj.and;
		for(i = 0; i < ands.length; i++){
			var andObj = {in1:ands[i]["input1"], in2:ands[i]["input2"],
						  pk1:ands[i]["k1"], pkm:ands[i]["km"], pn:ands[i]["n"]}
			if(!andMap.hasOwnProperty(ands[i]["input1"])) andMap[ands[i]["input1"]] = {};
			//if(!andMap.hasOwnProperty(ands[i]["input2"])) andMap[ands[i]["input2"]] = {};
			andMap[ands[i]["input1"]][ands[i]["input2"]] = andObj;
			//andMap[ands[i]["input2"]][ands[i]["input1"]] = andObj;
			
			//proteinLibrary.push(andMap[ands[i]["input1"]][ands[i]["input2"]]);
		}
	}
}

function findProtein(name) {
    var res = null;
    $.each(proteinLibrary, function(index, protein) {
        if(name == protein.name){
            res = protein;
        }
    });
    return res;
}

function updateParameters(){
    var selection = proteinLibrary.filter(function(x){return x.name==selectedProtein})[0];
    $("#pk1").text(selection.pk1);
    $("#pkm").text(selection.pkm);
    $("#pn").text(selection.pn);
    $("#pd1").text(selection.pd1);
    $("#pd2").text(selection.pd2);
    $("#pk2").text(selection.pk2);
}

function getLibrary(libraryId){
    jsRoutes.controllers.Application.getlibrary().ajax({
        method: "POST",
        contentType: "application/json",
        data: JSON.stringify({id: libraryId}),
        success: function(response) {
            selectedLibrary = libraryId;
            parseLibrary(response);
            makeProteinList();
            notify("Protein library successfully loaded!", "success");
        },
        error: function(response) { alertError(response)}
    });
}

function getAvailableLibraries(){
    jsRoutes.controllers.Application.getalllibraries().ajax({
        success: function(response) {
            setupLibrarySelector($.parseJSON(response));
        },
        error: function(response) { alertError(response)}
    });
}

function setupLibrarySelector(libraries) {
    var selector = $("#setupLibrarySelector");
    for(i = 0; i < libraries.length; i++) {
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