function addTab() {
  var id = prompt("Please enter a name for your circuit");
				
  $('.tab-content').append(
    $('<div>')
      .attr('id', 'content-'+id)
      .addClass('tab-pane')
      .text('placeholder text')
  );

  var close_button = $('<button>')
    .addClass('close')
    .attr({
      type: 'button',
      onclick: 'closeTab(this)'
    })
    .html('&times;')[0].outerHTML;

  var tab_link = $('<a>')
        .attr({
          href: '#content-'+id,
          'data-toggle': 'tab'
        })
        .html(id +" "+ close_button);

  var tab = $('<li>')
    .attr('id', id)
    .html(tab_link);
	
  tab.insertBefore($('#add-tab-button'));	
  $('#myTab a:last').tab('show');
}

function closeTab(e) {
	var id = $(e).parent().parent()[0].id;
	$(e).parent().parent().remove();
	$('#content-'+id).remove();
}
