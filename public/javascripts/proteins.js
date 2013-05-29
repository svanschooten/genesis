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
        var oddClass = (1 % 2) == 0 ? "" : " odd";
        $("<option></option>", {
            class: "proteinSelector" + oddClass,
            value: proteinLibrary[i].name
        })
        .text(proteinLibrary[i].name)
        .appendTo($("#proteinList"));
        $("<br>").appendTo($("#proteinList"));
    }
    $("#proteinList").change(function(){
        selectedProtein = findProtein($("#proteinList option:selected")[0].value);
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