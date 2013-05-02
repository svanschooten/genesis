/**
Authors:
-Jeroen Peperkamp
-Stijn van Schooten
*/

//Global variables.
var data = null;


/**
Method that fires when the document is loaded.
Containing all the setup methods and listener setups.
*/
$(document).ready(function(){
    //setupTestCanvas();
    getPlotData();

});

/**
Standard error message for AJAX requests and alerts.
*/
function alertError(error) {
    alert(error.responseText);
}

/**
Makes a request for the JSON test method calculating a standard ODE and sending the results in JSON back.
When received, the results are plotted on the canvas.
*/
function getPlotData(){


    jsRoutes.controllers.Application.jsontest().ajax({
        success: function(response) {
            drawGraph(response)
        },
        error: function(response) { alertError(response)}
    })
}

function drawGraph(response) {

    if(data == null) {
        data = $.parseJSON(response);
        scale = 1;
    }
    var time = data["t"];
    var vectors = data["vectors"];
    var names = data["names"];
    var series = new Array();
    var palette = new Rickshaw.Color.Palette( { interpolatedStopsCount: vectors[0].length } );

    for (var i=0;i<vectors[0].length;i++){
        var serie = new Object();
        serie.name = names[i];
        var sData = [];
        for (j=0;j<time.length-1;j++){
            sData.push({x: time[j], y: vectors[j][i]});
        }
        serie.data = sData;
        serie.color = palette.color();
        series.push(serie);
    }

    alert(JSON.stringify(series));

    var graph = new Rickshaw.Graph( {
            element: document.querySelector("#chart"),
            width: 800,
            height: 250,
            renderer: 'line',
            series: series,
    } );

    var x_axis = new Rickshaw.Graph.Axis.X( {
        graph: graph,
        orientation: 'top',
        tickFormat: Rickshaw.Fixtures.Number.formatKMBT,
    } );

    var y_axis = new Rickshaw.Graph.Axis.Y( {
            graph: graph,
            orientation: 'left',
            tickFormat: Rickshaw.Fixtures.Number.formatKMBT,
            element: document.getElementById('y_axis'),
    } );

    var legend = new Rickshaw.Graph.Legend( {
            element: document.querySelector('#legend'),
            graph: graph
    } );

    graph.render();

    var hoverDetail = new Rickshaw.Graph.HoverDetail( {
    	graph: graph,
    	formatter: function(series, x, y) {
    		return series.name + ": " + y ;
    	}
    } );
}
