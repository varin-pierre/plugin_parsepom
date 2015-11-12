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
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import fr.paris.lutece.plugins.parsepom.business.Site;
import fr.paris.lutece.plugins.parsepom.services.Extract;
import fr.paris.lutece.plugins.parsepom.services.FileChooser;
import fr.paris.lutece.plugins.parsepom.services.Global;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.xpage.MVCApplication;
import fr.paris.lutece.portal.util.mvc.xpage.annotations.Controller;
import fr.paris.lutece.portal.web.xpages.XPage;


/**
 * This class provides the user interface to manage Site xpages ( manage, create, modify, remove, etc. )
 */
 
@Controller( xpageName = "parse" , pageTitleI18nKey = "parsepom.xpage.parse.pageTitle" , pagePathI18nKey = "parsepom.xpage.parse.pagePathLabel" )
public class ParseXPage extends MVCApplication
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// Templates
	private static final String TEMPLATE_PARSEPOM="/skin/plugins/parsepom/manage_parsepom.html";
    private static final String TEMPLATE_PARSE="/skin/plugins/parsepom/manage_parse.html";
    private static final String TEMPLATE_VALIDATE="/skin/plugins/parsepom/validate_parse.html";
    private static final String TEMPLATE_CHOOSE="/skin/plugins/parsepom/choose.html";
 
    // Markers
    private static final String MARK_PARSE = "parse";
    private static final String MARK_PATH = "path";
    private static final String MARK_CONFLICT = "conflict";
    private static final String MARK_ALLSITE = "all";
    private static final String MARK_DATA_EXIST="exist";

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
    private static final String INFO_VALIDATE = "parsepom.info.validate";
    private static final String INFO_VALIDATE_UPTODATE = "parsepom.info.validate.uptodate";
    private static final String INFO_CANCEL = "parsepom.info.cancel";
 
    // Session variable to store working values
    private String path = "";
    private Extract ext = new Extract( );
    
    
    /**
     * Returns the page home.
     * @param request The HTTP request
     * @return The view
     */
    @View( value = VIEW_PARSE, defaultView = true )
    public XPage getParse( HttpServletRequest request )
    {
    	Map<String, Object> model = getModel(  );
    	
    	model.put( MARK_PATH, path);

    	return getXPage( TEMPLATE_PARSE, request.getLocale(  ), model );
    }

    /**
     * Returns the page to choose your depot.
     * @param request
     * @return The view
     */
    @View( value = VIEW_CHOOSE )
    public XPage getChoose( HttpServletRequest request )
    {
    	return getXPage( TEMPLATE_CHOOSE, request.getLocale( ) );
    }
    
    /**
     * 
     * @param request
     * @return XPage
     */
    @Action( ACTION_CHOOSE )
    public XPage doChoose( HttpServletRequest request )
    {		
    	path = FileChooser.chooserDir( );
    	
    	return redirectView( request, VIEW_PARSE );
    }
    
    @Action( ACTION_PARSE )
    public XPage doParse( HttpServletRequest request ) throws IOException, SAXException, ParserConfigurationException
    {		
    	path = request.getParameter( "path" );
		
    	FileFilter filter = new Extract.DirFilter( );
    	File dirs = new File( path );
    	ext.initMaxInt( );
    	if ( !dirs.isDirectory( ) )
	    {
    		addError( ERROR_PATH_NOT_FOUND, getLocale( request ) );
    		path = "";
    		return redirectView( request, VIEW_PARSE );
	    }

    	ext.openDir( dirs, filter );
    	
    	if ( ext.getConflict( ).isEmpty( ) && ext.getGlobaleSite().isEmpty( ) )
 	    {
    		addInfo( INFO_VALIDATE_UPTODATE, getLocale( request ) );
    		path = "";
    		return redirectView( request, VIEW_PARSE );
 	    }
		
    	return redirectView( request, VIEW_VALIDATE );
    }
    
    @View( value = VIEW_VALIDATE)
    public XPage getValidate( HttpServletRequest request )
    {
    	Map<String, Object> model = getModel(  );

    	model.put( MARK_PARSE, path );
    	model.put( MARK_CONFLICT, ext.getConflict( ) );
    	model.put( MARK_ALLSITE, ext.getGlobaleSite( ) );

        return getXPage( TEMPLATE_VALIDATE, request.getLocale( ), model );
    }
    
    @Action( ACTION_VALIDATE )
    public XPage doValidate( HttpServletRequest request )
    {
		Collection<Site> _globaleSites  = ext.getGlobaleSite( );
		Collection<Site> _conflict =  ext.getConflict( );
	        
		Iterator<Site> itSite;
		Iterator<Site> itConflict;
		if ( !_conflict.isEmpty( ) )
	    {
			itConflict = _conflict.iterator( );
			while ( itConflict.hasNext( ) )
			{
			    Site siteConflict = itConflict.next( );
			    ext.conflictSite(  siteConflict ) ;
			    
			    itConflict.remove( );
			} 
	    }
		itSite = _globaleSites.iterator( );
		while ( itSite.hasNext( ) )
	    {
			Site currentSite = itSite.next( );
			ext.createSite( currentSite );
			itSite.remove( );
	    }

		path = "";
		Global._boolNotEmptyDB = true;
		
		Map<String, Object> model = getModel(  );
        model.put( MARK_DATA_EXIST, Global._boolNotEmptyDB );
		
    	addInfo( INFO_VALIDATE, getLocale( request ) );
    	
    	return getXPage( TEMPLATE_PARSEPOM, request.getLocale( ), model );
    }
   
    /*
     * Cancel parsing of all pom.xml file
     */
    @Action( ACTION_CLEAN )
    public XPage doClean( HttpServletRequest request )
    {
		ext.setConflictClear( );
		ext.setGlobaleDepClear( );
		ext.setGlobaleSiteClear( );
		
		path = "";
		
		addInfo( INFO_CANCEL, getLocale( request ) );
		
		return redirectView( request, VIEW_PARSE );
	}
		
}