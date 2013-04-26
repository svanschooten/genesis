

$(document).ready(function(){
    setupTestCanvas();
});

function setupTestCanvas(){
    jsRoutes.controllers.Application.jsontest().ajax({
        success: function(response) {
            drawRK(response)
        },
        error: function(response) { alertError(response)}
    })
}

function alertError(error) {
    alert(error.responseText);
}

function drawRK(jsvalue) {
    var canvas = $("#plotCanvas")[0];
    var data = $.parseJSON(jsvalue);
    var time = data["t"];
    var vectors = data["vectors"];
    var max_conc=-1;
    var min_conc=1E10;
    vectors.forEach(function(v){
        v.forEach(function(w){
            if(max_conc<=w) max_conc=w;
            if(min_conc>=w) min_conc=w;
        });
    });
    c=canvas.getContext("2d");
    var xrange=time[time.length-1]-time[0];
    var yrange=max_conc-min_conc;
    for(var i=0;i<vectors[0].length;i++){
        var j;
        c.beginPath();
        for(j=0;j<time.length-1;j++){
            var current_x = time[j]/xrange*canvas.width;
            var current_y = canvas.height-vectors[j][i]/yrange*canvas.heigh;
            c.moveTo(current_x,current_y);
            c.lineTo(time[j+1]/xrange*canvas.width,canvas.height-vectors[j+1][i]/yrange*canvas.height);
        }
        color = toRGB(i * (360/ (vectors[0].length + 1)), 1 - (Math.random() * 0.4), 0.3 + ((Math.random() * 0.2) - (0.2 * 0.5)));
        c.strokeStyle = rgbToHex(color[0], color[1], color[2]);
        c.stroke();
        c.closePath();
    }
}

function colswitch(i) {
    switch(i){
    case 0: return "#000";
    case 1: return "#A00";
    case 2: return "#00A";
    case 3: return "#0A0";
    case 4: return "#AA0";
    case 5: return "#A0A";
    case 6: return "#0AA";
    case 7: return "#FFF";
    default: return "#000";
    }
}
function componentToHex(c) {
    var hex = c.toString(16);
    return hex.length == 1 ? "0" + hex : hex;
}

function rgbToHex(r, g, b) {
    return "#" + componentToHex(r) + componentToHex(g) + componentToHex(b);
}

/*
function draw(vecs) {
    var time = vecs["t"];
    var vectors = vecs["vectors"];
    var max_conc=-1;
    var min_conc=1E10;
    vectors.forEach(function(v){
        v.forEach(function(w){
            if(max_conc<=w) max_conc=w;
            if(min_conc>=w) min_conc=w;
        });
    });
    cv=document.getElementById("gdraw");
	cv.width=cv.width;
	c=cv.getContext("2d");
    var xrange=time[time.length-1]-time[0];
    var yrange=max_conc-min_conc;
	for(var i=0;i<vectors[0].length;i++){
        var j;
        c.beginPath();
		for(j=0;j<time.length-1;j++){
            var current_x = time[j]/xrange*cv.width;
            var current_y = cv.height-vectors[j][i]/yrange*cv.heigh;
			c.moveTo(current_x,current_y);
			c.lineTo(time[j+1]/xrange*cv.width,cv.height-vectors[j+1][i]/yrange*cv.height);
		}
        c.strokeStyle=colswitch(i);
        c.stroke();
        c.closePath();
	}
} */