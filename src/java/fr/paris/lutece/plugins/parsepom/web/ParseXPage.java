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
    private static int _nMaxDepth = 5;
    private Collection<Site> _globaleSites;
    private Collection<Dependency> _globalDep;
    private List<String> _conflict;
    private String path;
    int maxIdSite;
    int maxIdDep;

    
    public List<String> debug ;
    
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
    public XPage doParse( HttpServletRequest request )
    {		
    	_globaleSites = new ArrayList<Site>();
    	_globalDep = new ArrayList<Dependency>();
    	_conflict = new ArrayList<String>();
		maxIdSite = SiteHome.getMaxId( );
		maxIdDep = DependencyHome.getMaxId( );
		
		path = request.getParameter( "path" );
		
		FileFilter filter = new DirFilter ( );
    	File dirs = new File( path );
    	if ( !dirs.isDirectory( ) )
    	{
    		addError( ERROR_PATH_NOT_FOUND, getLocale( request ) );
    		return redirectView( request, VIEW_PARSE );
    	}
    	parsePom(dirs.getName( ), dirs);
    	
		openDir( dirs, filter, 0 );
		return redirectView( request, VIEW_VALIDATE );
    }
    
    /*
     * open all directory in recurcive mode 
     * stop recursive at value of _nMaxDepth 
     */
	private void openDir( File dirs, FileFilter filter, int depth )
    {
		int i = depth;
    	File[] site = dirs.listFiles(filter);
    	if ( i > _nMaxDepth )
    		return ;
    	for ( File d : site )
    	{
    		String name = d.getName();
    		parsePom(name, d );
			openDir( d, filter, i++ );
    	}
    }
    
	/*
	 * @return Boolean with status 
	 */
	private Boolean parsePom( String name, File fDir ) 
    {
    	 FileFilter _pomFilter = new PomFilter(  );
         File[] pom = fDir.listFiles( _pomFilter );
         int i = 0;
         for (File p : pom)
         {
        	extratInfoPom( p );
        	maxIdSite++;
        	i++;
         }
         if ( i == 1 )
         {
        	 return true;
         }

         return false;
	}
   
	private void extratInfoPom( File pom )
	{
		Site site;
		PomHandler handler = new PomHandler(  );
        handler.parse( pom );
        List<Dependency> lDep = handler.getDependencies(  );
        
        site = new Site();
        site = handler.getSite();
        StringBuffer strIdPlugins = new StringBuffer();
        
    	Site _dbSite = new Site( );
    	site.setIdPlugins( "" );
    	site.setId( maxIdSite );
    	_dbSite = SiteHome.getSiteByArtifactId( site.getArtifactId( ) );
    	
        for ( Dependency d : lDep )
        {
        	d.setSiteId( maxIdSite );
        	d.setId(maxIdDep);
        	maxIdDep++;
      
        	if (d.getType( ) == null)
        	{
        		d.setType( "NULL" );
        	}
        	_globalDep.add( d );
        	strIdPlugins.append(d.getId());
        	strIdPlugins.append(";");
        }
        site.setIdPlugins(strIdPlugins.toString());
        if ( site.getName() == null ) 
		{
        	site.setName( "null");
		}
        if ( site.getVersion() == null )
        {
        	site.setVersion( "null" );
        }
        if ( site.getArtifactId( ) == null )
        {
        	site.setArtifactId( "null" );
        }
        if ( site.getLastUpdate() == null)
        {
        	site.setLastUpdate( "null " );
        }
        
    	if ( _dbSite != null )
    	{
    		_conflict.add( site.getArtifactId( ) );
    	}
    	_globaleSites.add( site );
    	

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
    	Iterator<String> itConflict;
    	Collection<Site> listSiteConflict = new ArrayList<>( );
    	
    	Map<String, Object> model = getModel(  );
    	model.put( MARK_PARSE, path);
    	if ( !_conflict.isEmpty( ) )
    	{
    		itConflict = _conflict.iterator( );
    		while ( itConflict.hasNext( ) )
			{      		
        		listSiteConflict.add( SiteHome.getSiteByArtifactId( itConflict.next( ) ) ) ;
			}
    	}
    	model.put( "conflict", listSiteConflict );
    	model.put( "all", _globaleSites );
    	addInfo( "path to ", getLocale( request ) );

        return getXPage( TEMPLATE_VALIDATE,request.getLocale(  ), model );
    }
    
    @Action( ACTION_VALIDATE )
    public XPage doValidate( HttpServletRequest request )
    {		
    	
    	debug = new ArrayList<String>();
    	Iterator<Site> itSite;
    	Iterator<String> itConflict;
    	debug.add( "dovalidate begin");
    	if ( !_conflict.isEmpty( ) )
    	{
    		debug.add( "not emptuy");
    		itConflict = _conflict.iterator( );
    		while ( itConflict.hasNext( ) )
			{
    			String strConflict = itConflict.next( );
    			debug.add( "strCnfilct = " + strConflict );
        		conflictSite( SiteHome.getSiteByArtifactId( strConflict) ) ;
        		
        		itConflict.remove( );
        		
			} 
    	}
    	debug.add( "Size list site = " + _globaleSites.size( ) );
    	itSite =_globaleSites.iterator( );
		while ( itSite.hasNext( ) )
		{
			String timeStamp = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance( ).getTime( ) );
			Site currentSite = itSite.next( );
			currentSite.setLastUpdate( timeStamp );
			createSite( currentSite );
			debug.add("create Site : " + currentSite.getId( ) + " Name = " + currentSite.getArtifactId());
			itSite.remove( );
		}
        Map<String, Object> model = getModel(  );
        model.put( MARK_SITE_LIST , SiteHome.getSitesList(  ) );
        model.put("debug", debug) ;
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
    	strIdPluginNew = conflicDependency( currentSite.getId( ), conflict.getId( ), strIdPluginNew );
		updateSite( currentSite, conflict.getId( ), conflict.getArtifactId( ), conflict.getName( ), conflict.getVersion( ), strIdPluginNew.toString( ), conflict.getLastUpdate( ) );
		debug.add( "confilct befor remove size = " + _globaleSites.size( ) );
		itSite.remove( );
		debug.add( "confilct after remove size = " + _globaleSites.size( ) );
    }
    
    /*
     * When Site is already in data base override Site
     * Create new Dependency if pom as more dependencies than previous file 
     * @return StringBuilder with update value of id dependency
     */
    private StringBuilder conflicDependency( int siteTmp, int siteDb, StringBuilder strIdPluginNew )
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
        	debug.add("size dep = [" + _globalDep.size() + "] ");
        	debug.add("size col = [" + coldepDB.size() + "] ");
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
	    			debug.add("bfind == true et itdep remove");
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
    	debug.add( "ceate Site :begin");
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
		upSite.setLastUpdate( strLastUpdate );
		
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
