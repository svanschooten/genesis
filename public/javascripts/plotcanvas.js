/**
Authors:
-Jeroen Peperkamp
-Stijn van Schooten
*/


//Global variables
var width;
var height;
var axisWidth;
var xrange;
var yrange

/**
Makes a request for the JSON test method calculating a standard ODE and sending the results in JSON back.
When received, the results are plotted on the canvas.
*/
function setupTestCanvas(){
    jsRoutes.controllers.Application.jsontest().ajax({
        success: function(response) {
            drawRK(response)
        },
        error: function(response) { alertError(response)}
    })
}

/**
Method to plot the results of the JSON request on a canvas element using dynamic scaling.
*/
function drawRK(jsvalue) {
    var canvas = $("#plotCanvas")[0];
    var data = $.parseJSON(jsvalue);
    var time = data["t"];
    var vectors = data["vectors"];
    var names = data["names"];
    var max_conc=-1;
    var min_conc=1E10;
    vectors.forEach(function(v){
        v.forEach(function(w){
            if(max_conc<=w) max_conc=w;
            if(min_conc>=w) min_conc=w;
        });
    });
    //Start drawing on the canvas.
    //First set all the correct elements for re-use
    //and compensate for axiswidth
    width = canvas.width;
    height = canvas.height;
    var heightParts = 4;
    var widthParts = 8;
    axisWidth = 30;
    canvas.width = width + 200 + axisWidth;
    canvas.height = height + axisWidth;
    xrange=time[time.length-1]-time[0];
    yrange=max_conc-min_conc;
    c=canvas.getContext("2d");
    //draw the horizontal grid lines and axis legends
    drawGrid(c, heightParts, widthParts);
    c.lineWidth = 3;
    for(var i=0;i<vectors[0].length;i++){
        //Begin drawing the plotline
        var j;
        c.beginPath();
        for(j=0;j<time.length-1;j++){
            var current_x = time[j]/xrange*width;
            var current_y = height-vectors[j][i]/yrange*height;
            c.moveTo(current_x + axisWidth,current_y);
            c.lineTo(time[j+1]/xrange*width + axisWidth,height-vectors[j+1][i]/yrange*height);
        }
        //Set stroke color to semi-random using the HSV colorcircle
        c.strokeStyle = randomRGB(i, (360/ (vectors[0].length + 1)));
        c.stroke();
        c.closePath();
        //Begin drawing the legenda
        c.beginPath();
        var legendHeight = height / vectors[0].length + 1;
        var elemHeight = (i*legendHeight) + (legendHeight / 2);
        c.textBaseline="middle";
        c.moveTo(width + 5 + axisWidth, elemHeight);
        c.lineTo(width + 25 + axisWidth, elemHeight);
        c.stroke();
        c.fillText(names[i],width + 30 + axisWidth, elemHeight);
    }
}

/**
Method to get a random RGB color according to the part in the HSV domain the counter is in.
The counter multiplied with a step size to get a different color for every iteration.
*/
function randomRGB(i, stepSize) {
    var color = toRGB(i * stepSize, 1 - (Math.random() * 0.2), 0.3 + ((Math.random() * 0.2) - (0.2 * 0.5)));
    return rgbToHex(color[0], color[1], color[2]);
}

/**
Extracted method to draw a grid on the canvas using an equal distribution
and adding the axis according to the height and width parts.
*/
function drawGrid(context, heightParts, widthParts) {
    context.lineWidth = 1;
    context.font="10px Arial";
    context.textBaseline="top";
    context.strokeStyle = "#d0d0d0"
    //draw the horizontal grid lines and axis legends
    for(i = 0; i < heightParts + 1; i++) {
        context.beginPath();
        context.moveTo(axisWidth, (height / heightParts) * i);
        context.lineTo(width + axisWidth, (height / heightParts) * i);
        context.stroke();
        context.fillText((yrange - ((yrange / heightParts) * i)).toFixed(3), 1, (height / heightParts) * i);
    }
    //draw the vertical grid lines and axis legends
    for(i = 0; i < widthParts + 1; i++) {
        context.beginPath();
        context.moveTo((width / widthParts) * i + axisWidth, 0);
        context.lineTo((width / widthParts) * i + axisWidth, height);
        context.stroke();
        context.fillText(((xrange / widthParts) * i).toFixed(3), ((width / widthParts) * i) + axisWidth, height + 1);
    }
}

/**
Method to clear the mouseposition div.
*/
function clearMouseOverCanvas() {
    $("#canvasMouse")[0].innerHTML = " ";
}

/**
Method to set the mouseposition div with the time and concentration for that moment.
*/
function mouseOverCanvas(event) {
    var x = event.offsetX;
    var y = event.offsetY;
    if((x > axisWidth && x < axisWidth+width) && (y < height)) {
        $("#canvasMouse")[0].innerHTML = "t:" + (((x - axisWidth) / width)* xrange).toFixed(5) + " c:" + (yrange - ((y / height) * yrange)).toFixed(5);
    }
}

/**
Method to write RGB component to hexadecimal
*/
function componentToHex(c) {
    var hex = c.toString(16);
    return hex.length == 1 ? "0" + hex : hex;
}

/**
Method to write RGB to HEX color format
*/
function rgbToHex(r, g, b) {
    return "#" + componentToHex(r) + componentToHex(g) + componentToHex(b);
}