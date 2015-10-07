/**
 * 
 */
$(document).ready(function() {
	$( '#myTableSite th' ).append( " <i class=\"fa fa-fw fa-sort\">" );
	$( '#myTableDependency th' ).append( " <i class=\"fa fa-fw fa-sort\">" );
	
	$( "#myTableSite" ).tablesorter( {debug: true} );
	$( "#myTableDependency" ).tablesorter( {debug: true} );
	$( "" ).toggle( );
	
	$("#myTableSite th").css('cursor', 'pointer');
	$("#myTableDependency th").css('cursor', 'pointer');
	
	$( '#first' ).click( function( ) {	
	    handleHeaderClick( '#first' );
	})
	$( '#second' ).click( function( ) {	
		handleHeaderClick( '#second' );
	})
	$( '#third' ).click( function( ) {	
		handleHeaderClick( '#third' );
	})
	$( '#fourth' ).click( function( ) {
		handleHeaderClick( '#fourth' );
	})
	
	switchDebug( );
	autoComplete( );
})

function handleHeaderClick( hdr ) {
	
    var concat = hdr.concat(' i');
    if ($(hdr).hasClass('sortUp') == true) {
        $(hdr).removeClass('sortUp');
        $(hdr).addClass('sortDown');
        $(concat).removeClass('fa-sort fa-sort-asc').addClass('fa-sort-desc');
    } else if ($(hdr).hasClass('sortDown') == true) {
        $(hdr).removeClass('sortDown');
        $(hdr).addClass('sortUp');
        $(concat).removeClass('fa-sort fa-sort-desc').addClass('fa-sort-asc');
    } else {
    	$(hdr).removeClass('sortUp sortDown');
        $(hdr).addClass('sortUp');
        $(concat).removeClass('fa-sort').addClass('fa-sort-asc');
    }
};

function switchDebug( ) {
	$("#debug").css('cursor', 'pointer');
	$("#debug").next().hide();
	$("#debug").children('.switch').text('[ + ] ');
	$("#debug").click(function() {
		if ($(this).next().is(":hidden")) {
			$(this).next().slideDown("slow");
			$(this).children('.switch').text('[ - ] ');
		} else {
			$(this).next().slideUp("slow");
			$(this).children('.switch').text('[ + ] ');
		}
	})
};

function autoComplete(  ) {
	var listSite = [];
	var listDependency = [];
	function availableTags ( ) {
		$.getJSON("rest/parsepom/site/s?format=json", function(data) {
		    $.map(data.sites.site, function (value) {
		    	listSite.push(value.name);
	        });
		});
	};
	function availableTags2 ( ) {
	$.getJSON("rest/parsepom/dependency/s?format=json", function(data) {
	    $.map(data.dependencys.dependency, function (value) {
	    	listDependency.push(value.artifact_id);
        });
	});
	};
	availableTags();
	availableTags2();
	$( "#siteArtifactId" ).autocomplete({
	  source: listSite,
	  minLength: 2,
	});
	$( "#dependencyArtifactId" ).autocomplete({
	  source: listDependency,
	  minLength: 2,
	});
}