package fr.paris.lutece.plugins.parsepom.services;

import java.util.Collection;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import fr.paris.lutece.plugins.parsepom.business.Dependency;
import fr.paris.lutece.plugins.parsepom.business.Tools;
import fr.paris.lutece.plugins.parsepom.business.ToolsHome;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.httpaccess.HttpAccess;
import fr.paris.lutece.util.httpaccess.HttpAccessException;

public class HttpProcess
{
	// URL
    private static final String PROPERTY_LUTECETOOLS_REST_URL = "parsepom.lutecetools.rest.url";
    private static final String URL_LUTECETOOLS_REST = AppPropertiesService.getProperty( PROPERTY_LUTECETOOLS_REST_URL );
    private static final String PROPERTY_LUTECETOOLS_JSON_PARAM = "parsepom.lutecetools.json.param";
    private static final String PARAM_LUTECETOOLS_JSON = AppPropertiesService.getProperty( PROPERTY_LUTECETOOLS_JSON_PARAM );
    private static final String TAG_COMPONENT = "component";
    private static final String ELEMENT_VERSION = "version";
    private static final String RELEASE_NOT_FOUND = "Release not found";
    private static final String HTTP_ERROR = "HTTP error";
    
	
	public static void getLastReleases( Collection<Dependency> dependencyList )
	{
		HttpAccess httpAccess = new HttpAccess(  );
    	
    	for ( Dependency list : dependencyList )
    	{
    		AppLogService.debug( "Find last version of : " + list.getArtifactId( ) );

    		Tools base  = ToolsHome.findByArtifactId( list.getArtifactId( ) );
    		String JSONPath = URL_LUTECETOOLS_REST.concat( list.getArtifactId( ) ).concat( PARAM_LUTECETOOLS_JSON );
    		
	    	try
	    	{
	    		String strHtml = httpAccess.doGet( JSONPath );
	    		JSONObject json = new JSONObject( strHtml );
	    		String version = json.getJSONObject( TAG_COMPONENT ).getString( ELEMENT_VERSION );
	    		 	
		    	if ( base == null )
		    	{
		    		base = new Tools( );
		    		base.setArtifactId( list.getArtifactId( ) );
		    		base.setLastRelease( version );
		    		ToolsHome.create( base );
		    	}
		    	else
		    	{
		    		base.setLastRelease( version );
		    		ToolsHome.update( base );
		    	}
	    	}
	    	catch (HttpAccessException e1)
	    	{
	    		setErrorMessage( base, list.getArtifactId(), HTTP_ERROR );
	    		AppLogService.error( e1.getMessage( ) );
	    	}
	    	catch ( JSONException e2 )
	    	{
	    		setErrorMessage( base, list.getArtifactId(), RELEASE_NOT_FOUND );
	    		AppLogService.error( e2.getMessage( ) );
	    	}
    	}
	}
	
	private static void setErrorMessage( Tools base, String strArtifactId, String strErrorMessage )
	{
		if ( base == null )
    	{
    		base = new Tools( );
    		base.setArtifactId( strArtifactId );
    		base.setLastRelease( strErrorMessage );
    		ToolsHome.create( base );
    	}
    	else
    	{
    		base.setLastRelease( strErrorMessage );
    		ToolsHome.update( base );
    	}
	}
}
