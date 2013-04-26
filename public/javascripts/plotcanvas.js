var width;
var height;
var axisWidth;
var xrange;
var yrange


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
    //Start drawing the grid on the canvas.
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
    c.lineWidth = 1;
    c.font="10px Arial";
    c.textBaseline="top";
    c.strokeStyle = "#d0d0d0"
    //c.strokeRect(20,0, width, height)
    for(i = 0; i < heightParts + 1; i++) {
        c.beginPath();
        c.moveTo(axisWidth, (height / heightParts) * i);
        c.lineTo(width + axisWidth, (height / heightParts) * i);
        c.stroke();
        c.fillText((yrange - ((yrange / heightParts) * i)).toFixed(3), 1, (height / heightParts) * i);
    }
    for(i = 0; i < widthParts + 1; i++) {
        c.beginPath();
        c.moveTo((width / widthParts) * i + axisWidth, 0);
        c.lineTo((width / widthParts) * i + axisWidth, height);
        c.stroke();
        c.fillText(((xrange / widthParts) * i).toFixed(3), ((width / widthParts) * i) + axisWidth, height + 1);
    }
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
        color = toRGB(i * (360/ (vectors[0].length + 1)), 1 - (Math.random() * 0.2), 0.3 + ((Math.random() * 0.2) - (0.2 * 0.5)));
        c.strokeStyle = rgbToHex(color[0], color[1], color[2]);
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

function clearMouseOverCanvas() {

    $("#canvasMouse")[0].innerHTML = " ";
}

function mouseOverCanvas(event) {
    var x = event.offsetX;
    var y = event.offsetY;
    if((x > axisWidth && x < axisWidth+width) && (y < height)) {
        $("#canvasMouse")[0].innerHTML = (((x - axisWidth) / width)* xrange).toFixed(5) + " " + (yrange - ((y / height) * yrange)).toFixed(5);
    }
}

function componentToHex(c) {
    var hex = c.toString(16);
    return hex.length == 1 ? "0" + hex : hex;
}

function rgbToHex(r, g, b) {
    return "#" + componentToHex(r) + componentToHex(g) + componentToHex(b);
}