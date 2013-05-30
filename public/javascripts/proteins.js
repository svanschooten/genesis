/**
Script to integrate the protein library and selection into the front end.
authors:
- Stijn van Schooten
*/

var inputs = "";//TODO hier het inputsignaal zetten

var proteinLibrary = [
    {name:"henk", pk1:0.145, pkm:2.14, pn:0.0124},
    {name:"klaas", pk1:1.5421, pkm:1.544, pn:0.0476},
    {name:"piet", pk1:0.846, pkm:0.9954, pn:0.1457},
    {name:"kees", pk1:1.024, pkm:2.476, pn:0.0864}];//new Array();
var selectedProtein = null;

function makeProteinList(){
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
        selectedProtein = findProtein($(this)[0].value);
        updateParameters();
    })
}

function parseLibrary(json) {
    return true;
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
    $("#pk1").text(selectedProtein.pk1);
    $("#pkm").text(selectedProtein.pkm);
    $("#pn").text(selectedProtein.pn);
}

function getLibrary(libraryId){
    jsRoutes.controllers.Application.getlibrary().ajax({
        data: libraryId,
        success: function(response) {
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
            console.log(response);
            setupLibrarySelector($.parseJSON(response));
        },
        error: function(response) { alertError(response)}
    });
}

function setupLibrarySelector(libraries) {
    var selector = $("#setupLibrarySelector");
    for(i = 0; i < libraries.length; i++) {
        $("<option></option>",{
            value: libraries[i].libraryid
        })
        .text(libraries[i].libraryname)
        .appendTo(selector);
    }
    showSetup();
}