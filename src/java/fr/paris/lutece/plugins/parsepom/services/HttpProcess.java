package fr.paris.lutece.plugins.parsepom.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codehaus.jettison.json.JSONArray;
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
	private static final String PROPERTY_SONAR_JSON_URL = "parsepom.sonar.json.url";
	private static final String URL_SONAR_JSON = AppPropertiesService.getProperty( PROPERTY_SONAR_JSON_URL );
	private static final String PROPERTY_SONAR_JSON_METRICS = "parsepom.sonar.json.metrics";
	private static final String METRICS_SONAR_JSON = AppPropertiesService.getProperty( PROPERTY_SONAR_JSON_METRICS );
	private static final String PROPERTY_SONAR_JSON_LC_RESOURCE = "parsepom.sonar.json.lutececore.resource";
	private static final String RESOURCE_LC_SONAR_JSON = AppPropertiesService.getProperty( PROPERTY_SONAR_JSON_LC_RESOURCE );
	private static final String PROPERTY_SONAR_JSON_PLUGINS_RESOURCE = "parsepom.sonar.json.plugins.resource";
	private static final String RESOURCE_PLUGINS_SONAR_JSON = AppPropertiesService.getProperty( PROPERTY_SONAR_JSON_PLUGINS_RESOURCE );
	
	// Tags
	private static final String TAG_HTML_SELECT = "td a";
	private static final String TAG_LUTECE_CORE = "lutece-core";
	
	// Regex
	private static final String PATTERN_VERSION = "[0-9]+.[0-9]+.[0-9]+";
	
	// Keys
	private static final String KEY_MSR = "msr";
	private static final String KEY_KEY = "key";
	private static final String KEY_FRMT_VAL = "frmt_val";
	private static final String KEY_NCLOC = "ncloc";
	private static final String KEY_FILES = "files";
	private static final String KEY_TESTS_COVERAGE = "coverage";
	private static final String KEY_TESTS_SUCCESS = "test_success_density";
	private static final String KEY_VIOLATIONS = "violations";
	
	// Errors
	private static final String RELEASE_NOT_FOUND = "Release not found";
	
	private static HttpAccess httpAccess = new HttpAccess( );
    
	public static HashMap<String, String> getSonarMetricsFromArtifactId( String artifactId )
	{
		HashMap<String, String> metrics = new HashMap<String, String>( );
		String JSONUrl;
		
		if ( artifactId.equals( TAG_LUTECE_CORE ) )
		{
			JSONUrl = URL_SONAR_JSON.concat( RESOURCE_LC_SONAR_JSON ).concat( artifactId ).concat( METRICS_SONAR_JSON );
		}
		else
		{
			JSONUrl = URL_SONAR_JSON.concat( RESOURCE_PLUGINS_SONAR_JSON ).concat( artifactId ).concat( METRICS_SONAR_JSON );
		}
		
		try 
		{
			String strHtml = httpAccess.doGet( JSONUrl );
			JSONObject json = new JSONObject( strHtml.substring( 1, strHtml.lastIndexOf( "]" ) ) );
			JSONArray msr = json.getJSONArray( KEY_MSR );
			
			for ( int i = 0; i < msr.length( ); i++ )
			{
			    JSONObject key = msr.getJSONObject( i );
			    
			    if (key.getString( KEY_KEY ).equals( KEY_NCLOC ) )
			    {
			    	metrics.put( KEY_NCLOC, key.getString( KEY_FRMT_VAL ) );
			    }
			    if (key.getString( KEY_KEY ).equals( KEY_FILES ) )
			    {
			    	metrics.put( KEY_FILES, key.getString( KEY_FRMT_VAL ) );
			    }
			    if (key.getString( KEY_KEY ).equals( KEY_TESTS_COVERAGE ) )
			    {
			    	metrics.put( KEY_TESTS_COVERAGE, key.getString( KEY_FRMT_VAL ) );
			    }
			    if (key.getString( KEY_KEY ).equals( KEY_TESTS_SUCCESS ) )
			    {
			    	metrics.put( KEY_TESTS_SUCCESS, key.getString( KEY_FRMT_VAL ) );
			    }
			    if (key.getString( KEY_KEY ).equals( KEY_VIOLATIONS ) )
			    {
			    	metrics.put( KEY_VIOLATIONS, key.getString( KEY_FRMT_VAL ) );
			    }
			}
		}
		catch ( HttpAccessException | JSONException e )
		{
			AppLogService.error( e.getMessage( ) );
		}
		
		return metrics;
	}
	
	public static void getLastReleases( Collection<Dependency> dependencyList )
	{
    	for ( Dependency list : dependencyList )
    	{
    		AppLogService.debug( "Find last version of : " + list.getArtifactId( ) );

    		Tools base  = ToolsHome.findByArtifactId( list.getArtifactId( ) );
    		
    		String version = "";
    		if ( list.getArtifactId( ).equals( TAG_LUTECE_CORE ) )
    		{
    			try
    			{
    				version = getLastVersionFromCore( httpAccess );
    			}
    			catch ( HttpAccessException e )
    	    	{
    	    		setErrorMessage( base, list.getArtifactId( ), RELEASE_NOT_FOUND );
    	    		AppLogService.error( e.getMessage( ) );
    	    	}
    		}
    		
	    	try
	    	{
	    		if ( version.isEmpty( ) )
	    		{
	    			version = getLastVersionFromPlugin( list.getArtifactId( ), httpAccess );
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
	    	catch ( HttpAccessException e )
	    	{
	    		setErrorMessage( base, list.getArtifactId( ), RELEASE_NOT_FOUND );
	    		AppLogService.error( e.getMessage( ) );
	    	}
    	}
	}
	
	public static String getLastVersionFromCore( HttpAccess httpAccess ) throws HttpAccessException
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
		
		String strLastVersion = RELEASE_NOT_FOUND;	
		if ( strArr.size( ) != 0 )
		{
			strLastVersion = ( strArr.get( strArr.size( ) - 1 ) ).replace( "/", "" );
		}

		return ( strLastVersion );
	}
	
	public static String getLastVersionFromPlugin( String strArtifactId, HttpAccess httpAccess ) throws HttpAccessException
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
		
		String strLastVersion = RELEASE_NOT_FOUND;	
		if ( strArr.size( ) != 0 )
		{
			strLastVersion = ( strArr.get( strArr.size( ) - 1 ) ).replace( "/", "" );
		}
		
		return ( strLastVersion );
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
