//Global variables.
var data = null;
var max_c_x = 0.0;

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

    //Instantiate a new color pallette
    var palette = new Rickshaw.Color.Palette( { interpolatedStopsCount: data.length } );

    //Fill the data array
    for (var i=0;i<data.length;i++){
        //Add an color
        data[i].color = palette.color();
    }

    return data;
}

/**
Plots the received data in a interactive plot.
*/
function drawGraph(series) {

    var width = 800;
    var height = 250;

    //Creating the graph to plot in
    var graph = new Rickshaw.Graph( {
            element: $('#chart')[0], // Graph's element must be an element, not an array of elements
            width: width,
            height: height,
            renderer: 'line',
            series: series,
    } );

    //Defining the x-axis
    var x_axis = new Rickshaw.Graph.Axis.X( {
        graph: graph,
        orientation: 'top',
        //tickFormat: Rickshaw.Fixtures.Number.formatKMBT,
    } );

    //Defining the y-axis
    var y_axis = new Rickshaw.Graph.Axis.Y( {
            graph: graph,
            orientation: 'left',
            tickFormat: Rickshaw.Fixtures.Number.formatKMBT,
            element: document.getElementById('y_axis'),
    } );

    //Render the constructed graph
    graph.render();

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
            var content = swatch + series.name + '<br>' + "t: " + (x / 1000) + "<br> c: " + y ;  //.toFixed(6) for rounding to decimals
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

    //TODO werkt soort van, maar nog niet helemaal lekker, moest de timestamp *1000 doen.
    //Add the range slider for zooming in
    var slider = new Rickshaw.Graph.RangeSlider( {
        graph: graph,
        element: new Array(document.getElementById('slider')) // RangeSlider's element is apparently supposed to be an Array
    } );

}