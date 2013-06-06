$(document).ready(function(){
    getPlotData();
});

/**
Makes a request for the JSON test method calculating a standard ODE and sending the results in JSON back.
When received, the results are plotted on the canvas.
*/
function getPlotData(){
    jsRoutes.controllers.Application.jsontest().ajax({
        success: function(response) {
            drawGraph(parseJSONdataRickShaw(response))
        },
        error: function(response) { alertError(response)}
    })
}