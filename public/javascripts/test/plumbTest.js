
function logCircuit() {
    var div = $("#circuitDiv");
    div.html("");
    var network = jsPlumb.getConnections();
    for(i = 0; i < network.length; i++) {
        var elem = network[i];
        div.html(div.html() + "</br> " + elem.source.selector + " - " + elem.protein + " -> " + elem.target.selector)
    }
}

function parseJsPlumbTest() {
    console.log(JSON.stringify(parseJsPlumb()));
}