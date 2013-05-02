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
            drawGraph(parseJSONdata(response))
        },
        error: function(response) { alertError(response)}
    })
}

/**
Parses the standard JSON ouput to a usable format for plotting.
*/
function parseJSONdata(response){

    //Check if data in memory is empty
    if(data == null) {
       data = $.parseJSON(response);
    }

    //Parse the different vectors from the JSON object
    var time = data["t"];
    var vectors = data["vectors"];
    var names = data["names"];

    //Instantiate a new color pallette
    var palette = new Rickshaw.Color.Palette( { interpolatedStopsCount: vectors[0].length } );

    //Make the data array, now still empty
    var series = new Array();

    //Fill the data array
    for (var i=0;i<vectors[0].length;i++){
        //Make new series object
        var serie = new Object();
        //Give it a name
        serie.name = names[i];
        //Instantiate the data object in memory
        var sData = [];
        //Fill the data object
        for (j=0;j<time.length-1;j++){
            sData.push({x: time[j], y: vectors[j][i]});
        }
        //Add data an color
        serie.data = sData;
        serie.color = palette.color();
        //Push data into the data array
        series.push(serie);
    }

    return series;
}

/**
Plots the received data in a interactive plot.
*/
function drawGraph(series) {

    //Creating the graph to plot in
    var graph = new Rickshaw.Graph( {
            element: document.querySelector("#chart"),
            width: 800,
            height: 250,
            renderer: 'line',
            series: series,
    } );

    //Defining the x-axis
    var x_axis = new Rickshaw.Graph.Axis.X( {
        graph: graph,
        orientation: 'top',
        tickFormat: Rickshaw.Fixtures.Number.formatKMBT,
    } );

    //Defining the y-axis
    var y_axis = new Rickshaw.Graph.Axis.Y( {
            graph: graph,
            orientation: 'left',
            tickFormat: Rickshaw.Fixtures.Number.formatKMBT,
            element: document.getElementById('y_axis'),
    } );

    //Building the legend
    var legend = new Rickshaw.Graph.Legend( {
            element: document.querySelector('#legend'),
            graph: graph
    } );

    //Setting up the hover detail
    var hoverDetail = new Rickshaw.Graph.HoverDetail( {
    	graph: graph,
    	formatter: function(series, x, y) {
    		var swatch = '<span class="detail_swatch" style="background-color: ' + series.color + '"></span>';
    		var content = swatch + series.name + '<br>' + "t: " + x + "<br> c: " + y;  //.toFixed(6) for rounding to decimals
    		return content;
    	}
    } );

    //Add toggle functionality
    var shelving = new Rickshaw.Graph.Behavior.Series.Toggle( {
    	graph: graph,
    	legend: legend
    } );

    //Add legend hover highlight
    var highlight = new Rickshaw.Graph.Behavior.Series.Highlight( {
    	graph: graph,
    	legend: legend
    } );

    //Render the constructed graph
    graph.render();
}
