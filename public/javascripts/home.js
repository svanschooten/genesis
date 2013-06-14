$(document).ready(function(){
    $("body").prepend('<div id="loader">' +
        '<div class="circle"></div>' +
        '<div class="circle1"></div>' +
        '<div class="circle2"></div></div>' +
        '<div id="mask"></div>');
    setLoader();
});

function loaderReady(){
	$("#loader").fadeOut().zIndex(-9999);
	$("#mask").fadeOut().zIndex(-999);
}

function setLoader(){
    $("#loader").zIndex(9999).fadeTo(1);
    $("#mask").zIndex(999).fadeTo(0.2);
}