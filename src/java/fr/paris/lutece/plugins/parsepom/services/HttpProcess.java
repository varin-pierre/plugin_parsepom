package fr.paris.lutece.plugins.parsepom.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Document;

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
	private static final String PROPERTY_MAVEN_LUTECECORE_URL = "parsepom.maven.lutececore.url";
	private static final String URL_MAVEN_LUTECORE = AppPropertiesService.getProperty( PROPERTY_MAVEN_LUTECECORE_URL );
	private static final String PROPERTY_MAVEN_PLUGINS_URL = "parsepom.maven.plugins.url";
	private static final String URL_MAVEN_PLUGINS = AppPropertiesService.getProperty( PROPERTY_MAVEN_PLUGINS_URL );
	//private static final String PROPERTY_LUTECETOOLS_REST_URL = "parsepom.lutecetools.rest.url";
	//private static final String URL_LUTECETOOLS_REST = AppPropertiesService.getProperty( PROPERTY_LUTECETOOLS_REST_URL );
	//private static final String PROPERTY_LUTECETOOLS_JSON_PARAM = "parsepom.lutecetools.json.param";
	//private static final String PARAM_LUTECETOOLS_JSON = AppPropertiesService.getProperty( PROPERTY_LUTECETOOLS_JSON_PARAM );
	private static final String PATTERN_VERSION = "[0-9]+.[0-9]+.[0-9]+";
	private static final String TAG_HTML_SELECT = "td a";
	private static final String LUTECE_CORE = "lutece-core";
	//private static final String TAG_COMPONENT = "component";
	//private static final String ELEMENT_VERSION = "version";
	private static final String RELEASE_NOT_FOUND = "Release not found";
	private static final String HTTP_ERROR = "HTTP error";
    
	
	public static void getLastReleases( Collection<Dependency> dependencyList )
	{
		HttpAccess httpAccess = new HttpAccess( );
		
    	for ( Dependency list : dependencyList )
    	{
    		AppLogService.debug( "Find last version of : " + list.getArtifactId( ) );

    		Tools base  = ToolsHome.findByArtifactId( list.getArtifactId( ) );
    		//String JSONPath = URL_LUTECETOOLS_REST.concat( list.getArtifactId( ) ).concat( PARAM_LUTECETOOLS_JSON );
    		
    		String version = "";
    		if ( list.getArtifactId( ).equals( LUTECE_CORE ))
    		{
    			try
    			{
    				version = getLuteceCoreFromMavenRepo( httpAccess );
    			}
    			catch ( HttpAccessException e1 )
    	    	{
    	    		setErrorMessage( base, list.getArtifactId( ), HTTP_ERROR );
    	    		AppLogService.error( e1.getMessage( ) );
    	    	}
    		}
    		
	    	try
	    	{
	    		//String strHtml = httpAccess.doGet( JSONPath );
	    		//JSONObject json = new JSONObject( strHtml );
	    		if ( version.isEmpty( ) )
	    		{
	    			version = getPluginsFromMavenRepo( list.getArtifactId( ), httpAccess );
	    			//json.getJSONObject( TAG_COMPONENT ).getString( ELEMENT_VERSION );
	    		}

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
	    	catch ( HttpAccessException e2 )
	    	{
	    		setErrorMessage( base, list.getArtifactId( ), RELEASE_NOT_FOUND );
	    		AppLogService.error( e2.getMessage( ) );
	    	}
	    	
    	}
	}
	
	public static String getLuteceCoreFromMavenRepo( HttpAccess httpAccess ) throws HttpAccessException
	{
		String strHtml = httpAccess.doGet( URL_MAVEN_LUTECORE );
		Document doc = Jsoup.parse( strHtml );
		Elements links = doc.select( TAG_HTML_SELECT );
		
		ArrayList<String> strArr = new ArrayList<>( );
		String linkText = "";
		Pattern p = Pattern.compile( PATTERN_VERSION );

		for ( Element link : links )
		{
			linkText = link.text( );
			Matcher m = p.matcher( linkText );
			if( m.find( ) )
			{
				strArr.add( linkText );
			}
		}
		String strVersion = ( strArr.get( strArr.size( ) - 1 ) ).replace( "/", "" );

		return ( strVersion );
	}
	
	public static String getPluginsFromMavenRepo( String strArtifactId, HttpAccess httpAccess ) throws HttpAccessException
	{
		String strHtml = httpAccess.doGet( URL_MAVEN_PLUGINS.concat( strArtifactId ) );
		Document doc = Jsoup.parse( strHtml );
		Elements links = doc.select( TAG_HTML_SELECT );
		
		ArrayList<String> strArr = new ArrayList<>( );
		String linkText = "";
		Pattern p = Pattern.compile( PATTERN_VERSION );

		for ( Element link : links )
		{
			linkText = link.text( );
			Matcher m = p.matcher( linkText );
			if( m.find( ) )
			{
				strArr.add( linkText );
			}
		}
		String strVersion = ( strArr.get( strArr.size( ) - 1 ) ).replace( "/", "" );

		return ( strVersion );
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
