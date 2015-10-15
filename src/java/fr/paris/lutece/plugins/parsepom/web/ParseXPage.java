/*
 * Copyright (c) 2002-2015, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */

package fr.paris.lutece.plugins.parsepom.web;
 

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.parsepom.business.Dependency;
import fr.paris.lutece.plugins.parsepom.business.DependencyHome;
import fr.paris.lutece.plugins.parsepom.business.Site;
import fr.paris.lutece.plugins.parsepom.business.SiteHome;
import fr.paris.lutece.plugins.parsepom.services.FileChooser;
import fr.paris.lutece.plugins.parsepom.services.PomHandler;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.xpage.MVCApplication;
import fr.paris.lutece.portal.util.mvc.xpage.annotations.Controller;
import fr.paris.lutece.portal.web.xpages.XPage;


/**
 * This class provides the user interface to manage Site xpages ( manage, create, modify, remove )
 */
 
@Controller( xpageName = "parse" , pageTitleI18nKey = "parsepom.xpage.parse.pageTitle" , pagePathI18nKey = "parsepom.xpage.parse.pagePathLabel" )
public class ParseXPage extends MVCApplication
{
    // Templates
    private static final String TEMPLATE_PARSE="/skin/plugins/parsepom/manage_parse.html";
    private static final String TEMPLATE_VALIDATE="/skin/plugins/parsepom/validate_parse.html";
    private static final String TEMPLATE_SITE="/skin/plugins/parsepom/manage_sites.html";
    private static final String TEMPLATE_CHOOSE="/skin/plugins/parsepom/choose.html";
 
    
    // Markers
    private static final String MARK_PARSE = "parse";
    private static final String MARK_SITE_LIST = "site_list";

    // Views
    private static final String VIEW_PARSE = "parse";
    private static final String VIEW_VALIDATE = "validate";
    private static final String VIEW_CHOOSE = "choose";



    // Actions
    private static final String ACTION_PARSE = "parse";
    private static final String ACTION_VALIDATE = "validate";
    private static final String ACTION_CLEAN = "clean";
    private static final String ACTION_CHOOSE = "choose";
    
    
    // Infos
    private static final String ERROR_PATH_NOT_FOUND = "parsepom.error.path.notFound";
    
    // Session variable to store working values
    private  int _nMaxDepth = 0;
    private Collection<Site> _globaleSites;
    private Collection<Dependency> _globalDep;
    private List<Site> _conflict;
    private String path;
    int maxIdSite;
    int maxIdDep;

    
    public List<String> debug = new ArrayList<String>();
    
    @View( value = VIEW_PARSE, defaultView = true )
    public XPage getParse( HttpServletRequest request )
    {
    	
        return getXPage( TEMPLATE_PARSE, request.getLocale(  ) );
    }
   
    @View( value = VIEW_CHOOSE)
    public XPage getChoose( HttpServletRequest request )
    {
    	
    	return getXPage( TEMPLATE_CHOOSE, request.getLocale() );
    }
    
    @Action( ACTION_CHOOSE )
    public XPage doChoose( HttpServletRequest request )
    {		
    	
    	path = FileChooser.chooserDir();
    	Map<String, Object> model = getModel(  );
    	model.put( "path", path);
    	    	
    	return getXPage( TEMPLATE_PARSE, request.getLocale(), model );

    }
    
    @Action( ACTION_PARSE )
    public XPage doParse( HttpServletRequest request ) throws IOException
    {		
    	_globaleSites = new ArrayList<Site>();
    	_globalDep = new ArrayList<Dependency>();
    	_conflict = new ArrayList<Site>();
    	debug = new ArrayList<String>();
		maxIdSite = SiteHome.getMaxId( );
		maxIdDep = DependencyHome.getMaxId( );
		_nMaxDepth = 0;
		path = request.getParameter( "path" );
		
		FileFilter filter = new DirFilter ( );
    	File dirs = new File( path );
    	if ( !dirs.isDirectory( ) )
    	{
    		addError( ERROR_PATH_NOT_FOUND, getLocale( request ) );
    		return redirectView( request, VIEW_PARSE );
    	}
    	
		openDir( dirs, filter );
		return redirectView( request, VIEW_VALIDATE );
    }
    
    /*
     * open all directory in recurcive mode 
     * stop recursive at value of _nMaxDepth 
     */
	private void openDir( File dirs, FileFilter filter ) throws IOException
    {
		FileFilter _pomFilter = new PomFilter(  );
		File[] pom = dirs.listFiles( _pomFilter );
		if ( ( pom.length ) == 1 )
		{
			extratInfoPom( pom[0]);
        	maxIdSite++;
		}
		else
		{
			File[] site = dirs.listFiles(filter);
	    	for ( File d : site )
    			openDir( d, filter );
		}
    }

	/*
	 * Format Date in string like date picker format
	 * @return String 
	 */
	private String formatDate( String date )
	{
		date = date.replace('T', ' ');
		date = date.replace('Z', ' ');
		
		return date;
	}
	
	/*
	 * Extract Date of metadata in File pom
	 * and modify with formatDate
	 * @return String date 
	 */
	private String extractdate(File pom) throws IOException
	{
		Path p = pom.toPath();
	    BasicFileAttributes view = Files.getFileAttributeView(p, BasicFileAttributeView.class).readAttributes();
	    String date =  view.lastAccessTime().toString();
        date = formatDate(date);
        
        return date;
	}
	
	/*
	 * Find conflict if current pom is already in data base 
	 * check if file has been modify recently 
	 */
	private void findConfilct( Site site )
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
        	else if ( tmp.getArtifactId( ).equals(site.getArtifactId( ) ) ) 
    		{
				_conflict.add( tmp );
				_globaleSites.add( site );
    		}
    	}
        else
        {
        	_globaleSites.add( site );
        }
	}
	
	/*
	 * Extract Data of pom.xml
	 */
	private void extratInfoPom( File pom ) throws IOException
	{
		PomHandler handler = new PomHandler(  );
        handler.parse( pom );
        List<Dependency> lDep = handler.getDependencies(  );
        StringBuffer strIdPlugins = new StringBuffer( );
        Site site = new Site( );
        
        if ( ( site = handler.getSite( ) ) == null )
        	return ;

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
        site.setLastUpdate( extractdate(pom) );
        siteFiledNotNull( site );
      
        findConfilct(site);
	}

	/**
     * Directory filter
     */
    static class DirFilter implements FileFilter
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
    		return file.getName( ).equals("pom.xml");
    	}
    }
    
    @View( value = VIEW_VALIDATE)
    public XPage getValidate( HttpServletRequest request )
    {
    	Map<String, Object> model = getModel(  );
    	model.put( MARK_PARSE, path);
    	model.put( "conflict", _conflict );
    	model.put( "all", _globaleSites );
    	addInfo( "path to ", getLocale( request ) );

        return getXPage( TEMPLATE_VALIDATE,request.getLocale(  ), model );
    }
    
    @Action( ACTION_VALIDATE )
    public XPage doValidate( HttpServletRequest request )
    {
    	Iterator<Site> itSite;
    	Iterator<Site> itConflict;
    	if ( !_conflict.isEmpty( ) )
    	{
    		itConflict = _conflict.iterator( );
    		while ( itConflict.hasNext( ) )
			{
    			Site siteConflict = itConflict.next( );
        		conflictSite(  siteConflict ) ;
        		
        		itConflict.remove( );
			} 
    	}
    	itSite =_globaleSites.iterator( );
		while ( itSite.hasNext( ) )
		{
			Site currentSite = itSite.next( );
			createSite( currentSite );
			itSite.remove( );
		}
        Map<String, Object> model = getModel(  );
        model.put( MARK_SITE_LIST , SiteHome.getSitesList(  ) );
    	model.put( "depth", _nMaxDepth );

    	return getXPage( TEMPLATE_SITE,request.getLocale(  ), model );
    }

    /*
     * Call when conflict is find 
     * Name of Site in current read and database is the same
     */
    private void conflictSite( Site conflict )
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
    	Dependency currentDep = new Dependency();
    	Collection<Dependency> coldepDB = DependencyHome.getDependencysListBySiteId( siteDb );
    	strIdPluginNew = new StringBuilder();
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
    		currentDep = itDep.next();
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
    private void createSite( Site currentSite )
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
    private void updateSite( Site upSite, int siteId, String strArtifactId, String strName, String version, String strIdPlugins, String strLastUpdate )
	{
		upSite.setId( siteId );
		upSite.setArtifactId( strArtifactId );
		upSite.setName( strName );
		upSite.setVersion( version );
		upSite.setIdPlugins( strIdPlugins );
//		upSite.setLastUpdate( strLastUpdate );
		
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
	 * Cancel parsing of all pom.xml file
	 */
	@Action( ACTION_CLEAN )
    public XPage doClean( HttpServletRequest request )
    {
    	_globaleSites = null;
    	_globalDep = null;
        return getXPage( TEMPLATE_PARSE );
    }
	

	/*
	 * Check if Dependency object have not field at null
	 */
	private void depFiledNotNull( Dependency d )
	{
		if ( d.getArtifactId( ) == null)
			d.setArtifactId( "null" );
		if ( d.getGroupId( ) == null )
			d.setGroupId( "null" );
		if ( d.getType( ) == null )
			d.setType( "null" );
		if ( d.getVersion( ) == null )
			d.setVersion( "null" );
	}
	

	/*
	 * Check if Site object have not field at null
	 */
	private void siteFiledNotNull( Site s )
	{
		if ( s.getArtifactId( ) == null)
			s.setArtifactId( "null" );
		if ( s.getVersion( ) == null )
			s.setVersion( "null" );
		if ( s.getName( ) == null )
			s.setName( "null" );
		if ( s.getIdPlugins( ) == null )
			s.setIdPlugins( "null" );
		if (s.getLastUpdate( ) == null )
			s.setLastUpdate( "null" );
	}
	
   
 /*
  * DEPRECIATE
  */
	
	private Collection<Dependency> listTmpDepBySiteId(int siteTmp) 
	{
    	Iterator<Dependency> itDep = _globalDep.iterator( );

        Collection<Dependency> dependencyList = new ArrayList<Dependency>(  );
        while ( itDep.hasNext( ) )
        {
        	Dependency currentDep = itDep.next( );
        	if ( currentDep.getSiteId() == siteTmp )
        	{
        		dependencyList.add( currentDep );
        		itDep.remove();
        	}
        }
		return null;
	}
	
	private void updateDependency( Dependency current, int dbId, int siteId  )
    {
    	current.setId( dbId );
    	current.setSiteId( siteId );
    	
    	DependencyHome.update( current );
    }
	
	List<String> con;

    private void cleanDependency( Site site ) 
    {
		con.add("==> siteTmp = " + site.getName());
		con.add("==> idplugin = " + site.getIdPlugins());
    	if (site.getIdPlugins().isEmpty())
		{
			con.add("idplugin is empty");
			return ;
		}
    	String[] table ;
    	String strSiteId = site.getIdPlugins( );
    	table = strSiteId.split(";");
    	int i = 0;
    	int len = table.length;
    	con.add("len  = " + len );
    	con.add("table [i]  = " + table[i] );
    	while ( i < len )
		{
    		DependencyHome.remove( Integer.parseInt( table[i] ) );    
			SiteHome.removeDependencyFromSite(Integer.parseInt( table[i] ), site.getId( ));
			con.add("table [i]  = " + table[i] + " | site id = " + site.getId( ) );
			con.add("apres remove = " + site.getIdPlugins());
    		i++;
		}
    
	}
	
}
