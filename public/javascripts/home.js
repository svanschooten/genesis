function addTab() {
	var id = prompt("Please enter a name for your circuit");
					
	$('.tab-content').append(
		$('<div>').attr({
			id: 'content-'+id,
		}).addClass('tab-pane').text(id)
	);

	var close_button = "<button class=\"close\" type=\"button\" onclick=\"closeTab(this)\">&times;</button>";
	
	var tab = $('<li>').attr({
				id: id
			}).html(
				$('<a>').attr({
					href: '#'+id,
					'data-toggle': 'tab'
				}).html(id +" "+ close_button)
	);
	
	tab.insertBefore($('#add-tab-button'));
	
	$('#myTab a:last').tab('show');
}

function closeTab(e) {
alert($(e).parent().id);
//	var id = e.id;
//	$('#'+id).remove();
//	$('#content-'+id).remove();
}
