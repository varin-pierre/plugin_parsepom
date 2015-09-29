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