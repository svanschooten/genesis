
    var t_counter = 1;  // iterator for new tab labels
    
    var myTabs = new YAHOO.widget.TabView('demo');
    
    myTabs.on('contentReady', function() { // ensure Tabs exist before accessing
        YAHOO.util.Dom.batch(myTabs.get('tabs'), function(tab) {
            YAHOO.util.Event.on(tab.getElementsByClassName('close')[0], 'click', handleClose, tab);
        });
        YAHOO.util.Event.on('add-tab', 'click', addTab, myTabs, true);
    });
    
    var handleClose = function(e, tab) {
        YAHOO.util.Event.preventDefault(e);
        myTabs.removeTab(tab);
        t_counter--;
    };
    
    function addTab() {
    	if (t_counter < 6){
    	t_counter++;

        var labelText = window.prompt('enter circuit name');
        var newTab = new YAHOO.widget.Tab({
            label: labelText + '<span class="close"> X </span>',
            content: '<canvas id="canvas" style="border:solid 1px #000000;"></canvas>'
        });

        this.addTab(newTab);
        YAHOO.util.Event.on(newTab.getElementsByClassName('close')[0], 'click', handleClose, newTab);
        this.set('activeTab', newTab, true);
        }
        else {confirm("Cannot create more than five circuits");}
    };
    

        