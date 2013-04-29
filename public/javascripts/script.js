/**
Authors:
-Jeroen Peperkamp
-Stijn van Schooten
*/


/**
Method that fires when the document is loaded.
Containing all the setup methods and listener setups.
*/
$(document).ready(function(){
    setupTestCanvas();
});

/**
Standard error message for AJAX requests and alerts.
*/
function alertError(error) {
    alert(error.responseText);
}

