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
	$( '#fifth' ).click( function( ) {
		handleHeaderClick( '#fifth' );
	})
	
	autoComplete( );
	datePicker( );
	updateLoader( );
})

function handleHeaderClick( hdr ) {
	
    var concat = hdr.concat(' i');
    
    if ($( hdr ).hasClass( 'sortUp' ) == true) {
        $( hdr ).removeClass( 'sortUp' );
        $( hdr ).addClass( 'sortDown' );
        $( concat ).removeClass( 'fa-sort fa-sort-asc' ).addClass( 'fa-sort-desc' );
    } else if ($( hdr ).hasClass( 'sortDown' ) == true) {
        $( hdr ).removeClass( 'sortDown' );
        $( hdr ).addClass( 'sortUp' );
        $( concat ).removeClass( 'fa-sort fa-sort-desc' ).addClass( 'fa-sort-asc' );
    } else {
    	$( hdr ).removeClass( 'sortUp sortDown' );
        $( hdr ).addClass( 'sortUp' );
        $( concat ).removeClass( 'fa-sort' ).addClass( 'fa-sort-asc' );
    }
}

function autoComplete(  ) {
	var listSiteArtifactId = [];	
	var listSiteName = [];
	var listSiteVersion = [];
	var listSiteLastUpdate = [];	
	var listDependencyArtifactId = [];
	
	function isArray( object ) {
	    return Object.prototype.toString.call( object ) === '[object Array]';
	}
	
	function removeDuplicates( list )
	{
		var tmp = [];
		
		$.each( list, function( index, value ) {
	        if( $.inArray( value, tmp) === -1 )
	        	tmp.push( value );
	    });
		
		return tmp;
	}
	
	function availableTags ( ) {
		$.getJSON( "rest/parsepom/site/s?format=json", function( data ) {			
			if ( isArray( data.sites.site ) )
				data = data.sites.site;
			else
				data = data.sites;
			$.map(data, function ( value, index ) {				
			    listSiteArtifactId.push( value.artifact_id );
			    listSiteName.push( value.name );
			    listSiteVersion.push( value.version );
			    listSiteLastUpdate.push( value.last_update );
		    });
			listSiteArtifactId = removeDuplicates( listSiteArtifactId );
			listSiteName = removeDuplicates( listSiteName );
			listSiteVersion = removeDuplicates( listSiteVersion );
			listSiteLastUpdate = removeDuplicates( listSiteLastUpdate );
			
			$( "#siteArtifactId" ).autocomplete( {		
				source: listSiteArtifactId,
			});
			$( "#siteName" ).autocomplete( {
				source: listSiteName,
			});
			$( "#siteVersion" ).autocomplete( {
				source: listSiteVersion,
			});
			$( "#datepicker" ).autocomplete( {
				source: listSiteLastUpdate,
			});
		});
		$.getJSON("rest/parsepom/dependency/s?format=json", function( data ) {
			if ( isArray( data.dependencys.dependency ) )
				data = data.dependencys.dependency;
			else
				data = data.dependencys;
		    $.map(data, function ( value, index ) {
		    	listDependencyArtifactId.push( value.artifact_id );
		    });
		    listDependencyArtifactId = removeDuplicates( listDependencyArtifactId );
		    
		    $( "#dependencyArtifactId" ).autocomplete( {
				source: listDependencyArtifactId,
			});
		});
	};
	
	availableTags( );
}

function datePicker( )
{
	$( '#submit' ).attr( 'disabled', true );
	$( "#datepicker" ).datepicker( {
		dateFormat: 'yy-mm-dd',
	    onClose: function( dateText ) {
	        if( !dateText ) {
	        	$( '#submit' ).attr( 'disabled', true );
	        }
	        else
		        $( '#submit' ).attr( 'disabled', false );
	    }
	});
	$( '#buttonpicker' ).click( function ( ) {
	    $( '#datepicker' ).datepicker( 'show' );
	});
}

function updateLoader( )
{
	$( '#buttonLoader' ).click( function( ) {
		$( '#imgLoader' ).show( );
		$( '#buttonLoader' ).hide( );
	})
	$( '#buttonLoader2' ).click( function( ) {
		$( '#imgLoader2' ).show( );
		$( '#buttonLoader2' ).hide( );
	})
}