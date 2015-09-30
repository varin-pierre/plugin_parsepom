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
 

import fr.paris.lutece.portal.web.xpages.XPage;
import ucar.nc2.util.xml.Parse;
import fr.paris.lutece.portal.util.mvc.xpage.MVCApplication;
import fr.paris.lutece.plugins.parsepom.business.Dependency;
import fr.paris.lutece.plugins.parsepom.business.DependencyHome;
import fr.paris.lutece.plugins.parsepom.business.Site;
import fr.paris.lutece.plugins.parsepom.business.SiteHome;
import fr.paris.lutece.plugins.parsepom.services.PomHandler;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.xpage.annotations.Controller;

import java.util.List;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.sound.midi.MidiDevice.Info;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.xpath.operations.Bool;
import org.hibernate.validator.constraints.Length;
import org.springframework.ui.Model;
import org.xml.sax.InputSource;


/**
 * This class provides the user interface to manage Site xpages ( manage, create, modify, remove )
 */
 
@Controller( xpageName = "parse" , pageTitleI18nKey = "parsepom.xpage.parse.pageTitle" , pagePathI18nKey = "parsepom.xpage.parse.pagePathLabel" )
public class ParseXPage extends MVCApplication
{
    // Templates
    private static final String TEMPLATE_PARSE="/skin/plugins/parsepom/parse.html";
    private static final String TEMPLATE_TMP="/skin/plugins/parsepom/tmp.html";
    private static final String TEMPLATE_VALIDATE="/skin/plugins/parsepom/validate.html";
    private static final String TEMPLATE_SITE="/skin/plugins/parsepom/manage_sites.html";

   
    // JSP
    private static final String JSP_PAGE_PORTAL = "jsp/site/Portal.jsp";
    
    // Parameters
//    private static final String PARAMETER_ID_SITE="id";
    private static final String PARAM_ACTION = "action";
    private static final String PARAM_PAGE = "page";
    
    // Markers
    // private static final String MARK_SITE_LIST = "site_list";
    private static final String MARK_PARSE = "parse";
    private static final String MARK_SITE = "site";
    private static final String MARK_DEP = "dep";
    private static final String MARK_SITE_LIST = "site_list";

    
    // Message
    // private static final String MESSAGE_CONFIRM_REMOVE_SITE = "parsepom.message.confirmRemoveSite";
    
    // Views
    private static final String VIEW_PARSE = "parse";
    private static final String VIEW_TMP = "tmp";
    private static final String VIEW_VALIDATE = "validate";


    // Actions
    private static final String ACTION_PARSE = "parse";
    private static final String ACTION_VALIDATE = "validate";
    private static final String ACTION_CLEAN = "clean";
    
    // Infos
    // private static final String INFO_SITE_REMOVED = "parsepom.info.site.removed";
    
    // Session variable to store working values
    // private Site _site;
    
    @View( value = VIEW_PARSE, defaultView = true )
    public XPage getParse( HttpServletRequest request )
    {

        return getXPage( TEMPLATE_PARSE );
    }
    
    // Dev zone
    // variables
    private static int _nMaxDepth = 3;
    private Dependency _dependency;
    private String path;
    private Site _site;
    private Collection<Site> _gobalSites;
    private Collection<Dependency> _globalDep;
    private List<String> _conflict;
    
    int maxIdSite;
    int maxIdDep;
    
    @View( value = VIEW_TMP )
    public XPage getTmp( HttpServletRequest request )
    {
    	
    	Map<String, Object> model = getModel(  );
    	model.put( MARK_PARSE, path);
    	model.put( "list", listFiles );
    	model.put( "conflict", _conflict );
    	addInfo( "path to ", getLocale( request ) );

        return getXPage( TEMPLATE_TMP,request.getLocale(  ), model );
    }
    
    @Action( ACTION_PARSE )
    public XPage doParse( HttpServletRequest request )
    {		
    	listFiles = new ArrayList<String>();
    	
    	//TOOD Global for prevent error
    	_gobalSites = new ArrayList<Site>();
    	_globalDep = new ArrayList<Dependency>();
    	_conflict = new ArrayList<String>();
		int len;
		
		maxIdSite = SiteHome.getMaxId();
		maxIdDep = DependencyHome.getMaxId();
// TODO clear len
		path = request.getParameter( "path" );
		StringBuilder tmp = new StringBuilder( path );
		len = path.length( ) - 1;
		if ( len > 0 && path.charAt( len ) != '/' )
		{
			tmp.append( "/" );
		}
		path = tmp.toString( );
		
		FileFilter filter = new DirFilter ();
    	File dirs = new File( path );
    	if ( !dirs.isDirectory( ) )
    		return redirectView( request, VIEW_PARSE );
    	parsePom(dirs.getName(), dirs);
    	
    	listFiles.add("@action dirs name = " + dirs.getName());
    	listFiles.add("@action dirs path = " + dirs.getPath());
    	listFiles.add("@action dirs  = " + dirs);
		openDir( dirs, filter, 0 );
		
		return redirectView( request, VIEW_VALIDATE );
    }
    
    List<String>listFiles;
    
    private void openDir( File dirs, FileFilter filter, int depth )
    {
		listFiles.add("=== openDir ===");
		int i = depth;
    	listFiles.add("Dir Name = " + dirs.getName( ));
    	File[] site = dirs.listFiles(filter);
    	if ( i > _nMaxDepth )
    		return ;
    	for ( File d : site )
    	{
    		String name = d.getName();
    		parsePom(name, d );
			openDir( d, filter, i++ );
    	}
		listFiles.add("=== /openDir ===");

    }
    
 
    private Boolean parsePom( String name, File fDir ) 
    {
		// TODO Auto-generated method stub
    	listFiles.add("=== parsePom ===");
    	listFiles.add("file name = " + fDir.getName( ) );
    	 FileFilter _pomFilter = new PomFilter(  );
         File[] pom = fDir.listFiles( _pomFilter );
         if ( pom == null)
        	 listFiles.add("parsePom pom == null");
         int i = 0;
         for (File p : pom)
         {
        	extratInfoPom( p );
        	maxIdSite++;
        	i++;
         }
         listFiles.add("i = " + i );
         if ( i == 1 )
         {
         	listFiles.add("=== /parsePom ===");

        	 return true;
         }
     	listFiles.add("=== /parsePom ===");

         return false;
	}
//TODO delete after working
   
	private void extratInfoPom( File pom )
	{
		listFiles.add("=== extratInfoPom ===");
		listFiles.add("extractInfoPom " + pom.getName());
		PomHandler handler = new PomHandler(  );
        handler.parse( pom );
        List<Dependency> lDep = handler.getDependencies(  );
        
        
        
        _site = new Site();
        _site = handler.getSite();
        StringBuffer strIdPlugins = new StringBuffer();
        listFiles.add( "Site name : " +  _site.getName());
        
    	Site _dbSite = new Site( );
    	_site.setIdPlugins("");
    	_site.setId( maxIdSite );
    	_dbSite = SiteHome.getSiteByName( _site.getName( ) );
    	
        for ( Dependency d : lDep )
        {
        	listFiles.add("===> Site Id " + _site.getId());
        	listFiles.add("ArtifactId " + d.getArtifactId());
        	listFiles.add("Type : " + d.getType());
        	listFiles.add("Groupeid : " + d.getGroupId());
        	listFiles.add("Version : " + d.getVersion());
        	listFiles.add("MaxId = " + maxIdSite );
        	d.setSiteId( maxIdSite );
        	d.setId(maxIdDep);
        	maxIdDep++;

      
        	if (d.getType( ) == null)
        	{
        		d.setType( "NULL" );
        		listFiles.add("type null = " + "artifactID = "+ d.getArtifactId() + "site = " + _site.getName() + "id site = " + _site.getId());
        	}
        	_globalDep.add( d );
        	strIdPlugins.append(d.getId());
        	strIdPlugins.append(";");
        }
        _site.setIdPlugins(strIdPlugins.toString());
    	if (_dbSite != null)
    	{
    		_conflict.add( _site.getName( ) );
    	}
    	_gobalSites.add( _site );
    	
		listFiles.add("=== /extratInfoPom ===");

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
    		return file.getName().equals("pom.xml");
    	}
    }
    
    @View( value = VIEW_VALIDATE)
    public XPage getValidate( HttpServletRequest request )
    {
    	
    	Map<String, Object> model = getModel(  );
    	model.put( MARK_PARSE, path);
    	model.put( "list", listFiles );
    	model.put( "conflict", _conflict );
    	addInfo( "path to ", getLocale( request ) );

        return getXPage( TEMPLATE_VALIDATE,request.getLocale(  ), model );
    }
    
	List<String> con;
    @Action( ACTION_VALIDATE )
    public XPage doValidate( HttpServletRequest request )
    {		
    	Iterator<Site> itSite=_gobalSites.iterator();
    	Iterator<Dependency> itDep;
    	Iterator<String> itConflict;
    	con = new ArrayList<String>();
    	String strTmp;
    	Dependency depTmp ;
    	
    	int len2 = _globalDep.size();
    	con.add( "avant  len2 = " + len2);

    		// read list Site
	    	while ( itSite.hasNext( ) )
	    	{
	    		Boolean update = false;
	    		Site _dbSite;
	    		Site currentSite = itSite.next();
	        	
	        	if ( !_conflict.isEmpty( ) )
	        	{
	        		// reset iterator conflict
	        		itConflict = _conflict.iterator( );
		        	
		        	// Read list conflict
		    		while (itConflict.hasNext( ) )
		    		{
		    		 	// 	Reset Iterator for dependency
		    			itDep = _globalDep.iterator();
		        		strTmp = itConflict.next();
		        		con.add("==> currentSite name = {" + currentSite.getName() + "} | strTmp = {"+ strTmp + "} |");
		        		if ( currentSite.getName( ) == strTmp )
		        		{
		        			// find in base the previous Site
		        			_dbSite = SiteHome.getSiteByName( strTmp );
        					Collection <Dependency> cDep = DependencyHome.getDependencysListBySiteId( _dbSite.getId());
        					
        					// read list Dependency
		        			while ( itDep.hasNext( ) )
		        			{
		        				depTmp = itDep.next( );

		        				con.add("==> depTmp id = {" + depTmp.getSiteId( ) + "} | currentSite = {"+ currentSite.getId( ) + "} |");
		        				// if dep link to current Site
		        				if ( depTmp.getSiteId( ) == currentSite.getId( ) )
		        				{
		        					Boolean updateDep = false;
		        					int sizeOfcDep = cDep.size();
		        					con.add( " size of cDep = " + 		  sizeOfcDep); 
		        					for ( Dependency elem : cDep)
		        					{
		        						con.add("elem = " + elem.getArtifactId() + " | dep = " + depTmp.getArtifactId());
		        						if ( elem.getArtifactId( ).equals(depTmp.getArtifactId( ) ) )
		        						{
		        							con.add("For if ok ==> depTmp id = {" + depTmp.getSiteId( ) +"} |");
		        							String strOld =  String.valueOf(depTmp.getId());
		        							String strNew = String.valueOf( elem.getId());
		        							int len = strOld.length();
		        							StringBuilder updateIdPlugin = new StringBuilder( currentSite.getIdPlugins( ) );
		        							int start = updateIdPlugin.indexOf( strOld ); 
		        							if ( start != -1 )
		        								updateIdPlugin.replace(start, start + len, strNew);

		        							updateDep = true;
		        							depTmp.setId(elem.getId());
		        							depTmp.setSiteId(_dbSite.getId()); // may be useless 


		        							currentSite.setIdPlugins( updateIdPlugin.toString() );

		        							//currentSite.setIdPlugins( tmp );
		        							con.add(  "strplugin = " + currentSite.getIdPlugins() + " old = " + strOld + " new = " + strNew);
		        							DependencyHome.update(depTmp);
		        							con.add("!=> depTmp site id = {" + depTmp.getSiteId( ) +"} |");
		        								con.add(" remove");
		        						}
		        					}
		        					if ( !updateDep )
		        					{
		        						depTmp.setSiteId( _dbSite.getId( ) );
		        						con.add("!update ==> depTmp site id = {" + depTmp.getSiteId( ) +"} |");
		        						
		        					}
		        					else
		        						itDep.remove();
		        				}
		        			}
		        			currentSite.setId( _dbSite.getId( ) );
//		        			cleanDependency( _dbSite );
		        			
		        			update = true ;
		        			SiteHome.update( currentSite );
		        		}
		    		}
	        	}
	    		if ( !update )
				{
	    			SiteHome.create( currentSite );
				}
	    		update = false;
	    		
	   
	    	}
	    	
//	    	Reset Iterator for dependency
	    	itDep = _globalDep.iterator();
	    	 len2 = _globalDep.size();
	    	con.add( " len2 = " + len2);
    	while ( itDep.hasNext( ) )
    	{
    		DependencyHome.create( itDep.next( ) );
    	}
    	_site = null;
        Map<String, Object> model = getModel(  );
        model.put( MARK_SITE_LIST , SiteHome.getSitesList(  ) );
        model.put( "con", con );

    	return getXPage( TEMPLATE_SITE,request.getLocale(  ), model );
    }
    
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

	@Action( ACTION_CLEAN )
    public XPage doClean( HttpServletRequest request )
    {
    	_gobalSites = null;
    	_globalDep = null;
    	//TODO change to redirectView
        return getXPage( TEMPLATE_PARSE );
    }
   
}
