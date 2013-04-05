var tabView = new YAHOO.widget.TabView('tab');
var removeTab = function() {
       tabView.removeTab(tabView.get('activeTab'));
};
var addTab = function() { 
	   var labelText = window.prompt('enter circuit name'); 
	   if (labelText && content) { 
	        tabView.addTab( new YAHOO.widget.Tab({ label: labelText, content: '<canvas id="canvas" style="border:solid 1px #000000;"></canvas>' }) ); 
	    } 
};    

var changeTabName = function() { 
	tab.get('activeTab').setText('Text');
}