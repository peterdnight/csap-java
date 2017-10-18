

// peter7
$(document).ready(
		function() {

			$('#currentTimeButton').click(getTime);

			CsapCommon.configureCsapAlertify();
			CsapCommon.labelTableRows( $("table") ) 

			$('#themeTablesButton').click(
					function() {

						$("table").tablesorter({
							theme : 'metro-dark'
						});

						$("table").removeClass("simple").css("width", "80em")
								.css("margin", "2em");
					});

		});

function getTime() {

	$('body').css('cursor', 'wait');
	$.get("currentTime",
			function(data) {
				// alertify.alert(data) ;
				alertify.dismissAll();
				alertify.csapWarning('<pre style="font-size: 0.8em">' + data
						+ "</pre>")
				$('body').css('cursor', 'default');
			}, 'text' // I expect a JSON response
	);

}