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

