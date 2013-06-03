//Global variables.
var data = null;
var graph = null;

/**
Parses the standard JSON ouput to a usable format for plotting.
*/
function parseJSONdataRickShaw(response){

   // Refresh data with the latest results
   data = $.parseJSON(response);

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

    if(!$('#chart').is(":empty")){
        $('#chart').empty();
        $('#y_axis').empty();
        $('#legend').empty();
        $('#slider').empty();
    }

    var width = getData("chart_container", "width");
    var height = getData("chart_container", "heigth");

    //Creating the graph to plot in
    graph = new Rickshaw.Graph( {
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

    //Add the range slider for zooming in
    var slider = new Rickshaw.Graph.RangeSlider( {
        graph: graph,
        element: $('#slider') // RangeSlider's element is apparently supposed to be an Array
    } );

}