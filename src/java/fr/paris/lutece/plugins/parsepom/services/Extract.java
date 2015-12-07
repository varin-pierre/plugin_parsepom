package fr.paris.lutece.plugins.parsepom.services;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import fr.paris.lutece.plugins.parsepom.business.Dependency;
import fr.paris.lutece.plugins.parsepom.business.DependencyHome;
import fr.paris.lutece.plugins.parsepom.business.Site;
import fr.paris.lutece.plugins.parsepom.business.SiteHome;
import fr.paris.lutece.portal.service.util.AppLogService;

public class Extract
{
	
	// Markers
    private static final String MARK_TAGS = "tags";
    private static final String MARK_BRANCHES = "branches";
    
    // Session variable to store working values
    private Collection<Site> _globaleSites = new ArrayList<Site>( );
    private Collection<Dependency> _globalDep = new ArrayList<Dependency>( );
    private Collection<Site> _conflict = new ArrayList<Site>( );
    private int maxIdSite = 0;
    private int maxIdDep = 0;
	
	/*
	 * Getter
	 */
    public Collection<Site> getGlobaleSite( )
	{
		return _globaleSites;
	}
    
    public Collection<Dependency> getGlobaleDep( )
    {
    	return _globalDep;
    }
    
    public Collection<Site> getConflict( )
    {
    	return _conflict;
    }
    
    /*
     * Setter clean COllection
     */
    public void setGlobaleSiteClear( )
  	{
  		 _globaleSites.clear( );
  	}

	public void setGlobaleDepClear( )
	{
	 	_globalDep.clear( );
	}

	public void setConflictClear( )
	{
	 	_conflict.clear( );
	}
    
	/**
	 * open recursive directory
	 * stop recursive at value of _nMaxDepth 
	 * @throws ParserConfigurationException 
	 * @throws SAXException 
    */
	public void openDir( File dirs, FileFilter filter )
    {
		
		FileFilter _pomFilter = new PomFilter( );
		File[] pom = dirs.listFiles( _pomFilter );
		
		if ( ( pom.length ) == 1 )
		{
			try
			{
				extractInfoPom( pom[0] );
			}
			catch ( IOException e )
			{
				AppLogService.error( e.getMessage( ) );
			}
			maxIdSite++;
		}
		else
		{
			File[] site = dirs.listFiles(filter);
	    	for ( File d : site )
	    	{
	    		String name = d.getName( );
	    		if ( !name.equals( MARK_TAGS ) && !name.equals( MARK_BRANCHES ) )
	    			openDir( d, filter );
	    	}
		}
    }

	/**
	 * Format Date in string like date picker format
	 * @return String 
	 */
	private String formatDate( String date )
	{
		date = date.replace('T', ' ');
		date = date.replace('Z', ' ');
		
		return date;
	}
	
	/**
	 * Extract Date of metadata in File pom
	 * and modify with formatDate
	 * @return String date 
	 */
	private String extractdate( File pom ) throws IOException
	{
		Path p = pom.toPath( );
	    BasicFileAttributes view = Files.getFileAttributeView( p, BasicFileAttributeView.class ).readAttributes( );
	    String date =  view.lastAccessTime( ).toString( );
	    String OS = System.getProperty("os.name").toLowerCase();
	    
        date = formatDate( date );
        
        if ( OS.contains( "win" ) == true )
        	date = date.substring( 0, date.indexOf( "." ) );
        
        return date;
	}
	
	/**
	 * Find conflict if current pom is already in data base 
	 * check if file has been modify recently 
	 * @params Site site
	 */
	private void findConflict( Site site )
	{		
		Collection <Site> listSiteDb = SiteHome.getSitesListByArtifactId( site.getArtifactId( ) );
		Iterator<Site> it;
        it = listSiteDb.iterator( );
        if ( it.hasNext( ) )
        {
        	Site tmp = it.next( );
            Site _dbSite = SiteHome.getOneSite( site.getArtifactId( ), site.getLastUpdate( ) );
          
        	if ( _dbSite != null )
        	{
        		return ;
        	}
        	else if ( tmp.getArtifactId( ).equals( site.getArtifactId( ) ) ) 
    		{
				_conflict.add( tmp );
				_globaleSites.add( site );
    		}
    	}
        else if ( !site.getIdPlugins( ).isEmpty( ) )
        {
        	_globaleSites.add( site );
        }
        else
        {
        	AppLogService.error( "Encoding error : the pom located in \"" + site.getPath( ) + "\" is not valid and will not be registered." );
        }
	}
	
	/**
	 * Extract Data of pom.xml
	 */
	private void extractInfoPom( File pom ) throws IOException
	{
	
        PomHandlerDom p = new PomHandlerDom( );
       
		p.parse( pom );
	
        Site site = new Site( );

        if ( ( site = p.getSite( ) ) == null )
        	return ;
        List<Dependency> lDep = p.getDependencies();
        StringBuffer strIdPlugins = new StringBuffer( );

        
        AppLogService.debug("Parse File : " + site.getName( ) );
        for ( Dependency d : lDep )
        {
        	d.setSiteId( maxIdSite );
        	d.setId(maxIdDep++);
        	depFiledNotNull( d );
        	strIdPlugins.append( d.getId( ) + ";" );
        	_globalDep.add( d );
        }
        site.setId( maxIdSite );
        site.setIdPlugins( strIdPlugins.toString( ) );
        site.setLastUpdate( extractdate( pom ) );
        site.setPath(pom.getAbsolutePath( ) );
        siteFiledNotNull( site );
      
        findConflict( site );
	}

	/**
     * Directory filter
     */
    public static class DirFilter implements FileFilter
    {
        @Override
        public boolean accept( File file )
        {
            return file.isDirectory(  );
        }
    }
    
    /**
     * A class that implements the Java FileFilter interface.
     */
    public class PomFilter implements FileFilter
    {
    	@Override
    	public boolean accept( File file )
    	{
    		return file.getName( ).equals( "pom.xml" );
    	}
    }
    
 
    /*
     * Call when conflict is find 
     * Name of Site in current read and database is the same
     */
    public void conflictSite( Site conflict )
    {
    	Iterator<Site> itSite = _globaleSites.iterator( );
    	StringBuilder strIdPluginNew = new StringBuilder( );
    	Site currentSite = new Site( );
    	Boolean bFind = false;

    	while ( itSite.hasNext( ) && !bFind )
    	{
    		currentSite = itSite.next( );
    		if ( conflict.getName( ).equals( currentSite.getName( ) ) )
    		{
    			bFind = true;
    		}
    	}
    	strIdPluginNew = conflictDependency( currentSite.getId( ), conflict.getId( ), strIdPluginNew );
		updateSite( currentSite, conflict.getId( ), conflict.getArtifactId( ), conflict.getName( ), conflict.getVersion( ), strIdPluginNew.toString( ), conflict.getLastUpdate( ) );
		itSite.remove( );
    }
    
    /*
     * When Site is already in data base override Site
     * Create new Dependency if pom as more dependencies than previous file 
     * @return StringBuilder with update value of id dependency
     */
    private StringBuilder conflictDependency( int siteTmp, int siteDb, StringBuilder strIdPluginNew )
    {
    	Iterator<Dependency> itDep = _globalDep.iterator( );
    	Iterator<Dependency> itColDepDB;
    	Dependency currentDep = new Dependency( );
    	Collection<Dependency> coldepDB = DependencyHome.getDependencysListBySiteId( siteDb );
    	strIdPluginNew = new StringBuilder( );
    	Boolean bFind = false;

    	while ( itDep.hasNext( ) )
    	{
    		currentDep = itDep.next( );
        	itColDepDB = coldepDB.iterator( );
        	if ( currentDep.getSiteId( ) == siteTmp )
        	{
        		while ( itColDepDB.hasNext( ) && !bFind )
	    		{
	    			Dependency currentDB = itColDepDB.next( );
	    			
	    			if ( currentDB.getArtifactId( ).equals( currentDep.getArtifactId( ) ) )
	    			{
	    				strIdPluginNew.append( currentDB.getId( ) + ";" );
	    				itColDepDB.remove( );
	    				bFind = true;
	    			}
	    		}
	    		if ( bFind )
	    		{
	    			itDep.remove( );
	    			bFind = false;
	    		}
        	}
    	}
    	itDep = _globalDep.iterator( );
    	while ( itDep.hasNext( ) )
    	{
    		currentDep = itDep.next( );
    		if ( currentDep.getSiteId( ) == siteTmp )
    		{
    			createDependency( currentDep , siteDb );
    			strIdPluginNew.append( currentDep.getId( ) + ";" );
    			itDep.remove( );
    		}
    	}
		return strIdPluginNew;
	}

    /*
     * Create new Site
     */
    public void createSite( Site currentSite )
    {
    	Iterator<Dependency> itDep = _globalDep.iterator( );
    	String stridPlugin;
		
		int nidSiteOrigin = currentSite.getId( );
		SiteHome.create( currentSite );
		itDep = _globalDep.iterator( );
		while ( itDep.hasNext( ) )
		{
			Dependency currentDep = itDep.next( );
			if ( currentDep.getSiteId( ) == nidSiteOrigin )
			{
				Dependency tmp = new Dependency ( ); 
				tmp = currentDep;
				createDependency( currentDep,  currentSite.getId( ) );
				itDep.remove( );
				stridPlugin = replaceDepInIdPlugins( tmp, currentDep, currentSite );
				currentSite.setIdPlugins( stridPlugin );
				SiteHome.update( currentSite );
			}
		}
    }
    
    /*
     * Update Site
     */
    private void updateSite( Site upSite, int nSiteId, String strArtifactId, String strName, String strVersion, String strIdPlugins, String strLastUpdate )
	{
		upSite.setId( nSiteId );
		upSite.setArtifactId( strArtifactId );
		upSite.setName( strName );
		upSite.setVersion( strVersion );
		upSite.setIdPlugins( strIdPlugins );
		
		SiteHome.update( upSite );
	}
    
    /*
     * Create new Dependency
     */
	private void createDependency( Dependency dependency, int siteId )
    {
		dependency.setSiteId( siteId );
		DependencyHome.create( dependency );
	}

	
	/*
	 * Switch tow Id of Dependency is strIdPlugin of Site
	 * @return String idPlugin update
	 */
	private String replaceDepInIdPlugins(  Dependency old, Dependency next, Site site )
    {
    	String strOld =  String.valueOf(old.getId());
		String strNew = String.valueOf( next.getId());
		int len = strOld.length();
		StringBuilder updateIdPlugin = new StringBuilder( site.getIdPlugins( ) );
		int start = updateIdPlugin.indexOf( strOld ); 
		
		if ( start != -1 )
			updateIdPlugin.replace(start, start + len, strNew);
		
		return updateIdPlugin.toString( );
    }
	
	/*
	 * Check if Dependency object have not field at null
	 */
	private void depFiledNotNull( Dependency d )
	{
		if ( d.getArtifactId( ) == null)
			d.setArtifactId( "-" );
		if ( d.getGroupId( ) == null )
			d.setGroupId( "-" );
		if ( d.getType( ) == null )
			d.setType( "-" );
		if ( d.getVersion( ) == null )
			d.setVersion( "-" );
	}
	

	/*
	 * Check if Site object have not field at null
	 */
	private void siteFiledNotNull( Site s )
	{
		if ( s.getArtifactId( ) == null)
			s.setArtifactId( "-" );
		if ( s.getVersion( ) == null )
			s.setVersion( "-" );
		if ( s.getName( ) == null )
			s.setName( "-" );
		if ( s.getIdPlugins( ) == null )
			s.setIdPlugins( "-" );
		if (s.getLastUpdate( ) == null )
			s.setLastUpdate( "-" );
	}
	
	/*
	 * initiation of variable at each call of doParse
	 */
	public void initMaxInt( )
	{
		maxIdSite = SiteHome.getMaxId( );
		maxIdDep = DependencyHome.getMaxId( );
	}
	
}