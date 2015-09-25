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
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.sound.midi.MidiDevice.Info;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

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

    
    // Message
    // private static final String MESSAGE_CONFIRM_REMOVE_SITE = "parsepom.message.confirmRemoveSite";
    
    // Views
    private static final String VIEW_PARSE = "parse";
    private static final String VIEW_TMP = "tmp";

    // Actions
    private static final String ACTION_PARSE = "parse";
    
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
    private Dependency _dependency;
    private String path;
    private Site _site;
    
    @View( value = VIEW_TMP )
    public XPage getTmp( HttpServletRequest request )
    {
    	
    	Map<String, Object> model = getModel(  );
    	model.put( MARK_PARSE, path);
    	model.put( "list", listFiles );
    	addInfo( "path to ", getLocale( request ) );

        return getXPage( TEMPLATE_TMP,request.getLocale(  ), model );
    }
    
    @Action( ACTION_PARSE )
    public XPage doParse( HttpServletRequest request )
    {		
    	listFiles = new ArrayList<String>();
		int len;
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
    	listFiles.add("@action dirs name = " + dirs.getName());
    	listFiles.add("@action dirs path = " + dirs.getPath());
    	listFiles.add("@action dirs  = " + dirs);
		openDir( dirs, filter );
		
		return redirectView( request, VIEW_TMP );
    }
    
    List<String>listFiles;
    
    private void openDir( File dirs, FileFilter filter )
    {
    	listFiles.add("Dir Name = " + dirs.getName( ));
    	File[] site = dirs.listFiles(filter);
    	for ( File d : site )
    	{
    		String name = d.getName();
    		parsePom(name, d );
    		openDir( d, filter );
    	}
    }
    
 
    private void parsePom(String name, File fDir) {
		// TODO Auto-generated method stub
    	 FileFilter _pomFilter = new PomFilter(  );
         File[] pom = fDir.listFiles( _pomFilter );
         for (File p : pom)
         {
        	 extratInfoPom( p );
         }
	}

   
	private void extratInfoPom( File pom )
	{
		// TODO Auto-generated method stub
		listFiles.add("extractInfoPom " + pom.getName());
		PomHandler handler = new PomHandler(  );
        handler.parse( pom );
        List<Dependency> lDep = handler.getDependencies(  );
        _site = new Site();
        _site = handler.getSite();
        StringBuffer strIdPlugins = new StringBuffer();
        listFiles.add( "Site name : " +  _site.getName());
        
        for ( Dependency d : lDep )
        {
        	listFiles.add("Type : " + d.getType());
        	listFiles.add("Groupeid : " + d.getGroupId());
        	listFiles.add("Version : " + d.getVersion());
        	listFiles.add("ArtifactId " + d.getArtifactId());
        	
        	DependencyHome.create( d );
        	strIdPlugins.append(d.getId());
        	strIdPlugins.append(";");
        }
        _site.setIdPlugins(strIdPlugins.toString());
        SiteHome.create( _site );
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
    
   
}

