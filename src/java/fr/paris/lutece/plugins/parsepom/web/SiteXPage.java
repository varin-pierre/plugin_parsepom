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

import fr.paris.lutece.plugins.parsepom.business.Dependency;
import fr.paris.lutece.plugins.parsepom.business.DependencyHome;
import fr.paris.lutece.plugins.parsepom.business.Site;
import fr.paris.lutece.plugins.parsepom.business.SiteHome;
import fr.paris.lutece.plugins.parsepom.business.Tools;
import fr.paris.lutece.plugins.parsepom.business.ToolsHome;
import fr.paris.lutece.plugins.parsepom.services.FileDownloader;
import fr.paris.lutece.plugins.parsepom.services.TimeInterval;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.web.xpages.XPage;
import fr.paris.lutece.portal.util.mvc.xpage.MVCApplication;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.xpage.annotations.Controller;
import fr.paris.lutece.util.url.UrlItem;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import fr.paris.lutece.portal.service.message.SiteMessageService;
import fr.paris.lutece.portal.service.message.SiteMessage;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import javax.servlet.http.HttpServletRequest;

import org.joda.time.DateTime;
import org.joda.time.Hours;
import org.joda.time.Instant;
import org.joda.time.Interval;
import org.joda.time.Months;


/**
 * This class provides the user interface to manage Site xpages ( manage, create, modify, remove, etc. )
 */
 
@Controller( xpageName = "site" , pageTitleI18nKey = "parsepom.xpage.site.pageTitle" , pagePathI18nKey = "parsepom.xpage.site.pagePathLabel" )
public class SiteXPage extends MVCApplication
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// Templates
    private static final String TEMPLATE_MANAGE_SITES="/skin/plugins/parsepom/manage_sites.html";
    private static final String TEMPLATE_CREATE_SITE="/skin/plugins/parsepom/create_site.html";
    private static final String TEMPLATE_MODIFY_SITE="/skin/plugins/parsepom/modify_site.html";
    private static final String TEMPLATE_DETAILS_SITE="/skin/plugins/parsepom/details_site.html";
    private static final String TEMPLATE_LIST_SITES="/skin/plugins/parsepom/list_sites.html";
    
    // JSP
    private static final String JSP_PAGE_PORTAL = "jsp/site/Portal.jsp";
    
    // Parameters
    private static final String PARAMETER_ID_SITE="id";
    private static final String PARAM_ACTION = "action";
    private static final String PARAM_PAGE = "page";
    private static final String PARAMETER_ARTIFACT_ID_SITE = "siteArtifactId";
    private static final String PARAMETER_NAME_SITE = "siteName";
    private static final String PARAMETER_VERSION_SITE = "siteVersion";
    private static final String PARAMETER_VERSION_LAST_UPDATE = "siteLastUpdate";
    
    // Markers
    private static final String MARK_SITE_LIST = "site_list";
    private static final String MARK_SITE = "site";
    private static final String MARK_DEPENDENCY_LIST_BY_SITE = "dependency_list_by_site";
    private static final String MARK_LAST_RELEASE_LIST = "last_release_list";
    private static final String MARK_SITE_LIST_BY_NAME = "site_list_by_name";
    private static final String MARK_SITE_LIST_BY_VERSION = "site_list_by_version";
    private static final String MARK_SITE_LIST_BY_ARTIFACT_ID = "site_list_by_artifact_id";
    private static final String MARK_SITE_LIST_BY_LAST_UPDATE = "site_list_by_last_update";
    private static final String MARK_LAST_UPDATE_TIME_INTERVAL = "last_update_time_interval";
    
    // Message
    private static final String MESSAGE_CONFIRM_REMOVE_SITE = "parsepom.message.confirmRemoveSite";
    
    // Views
    private static final String VIEW_MANAGE_SITES = "manageSites";
    private static final String VIEW_CREATE_SITE = "createSite";
    private static final String VIEW_MODIFY_SITE = "modifySite";
    private static final String VIEW_DETAILS_SITE = "detailsSite";

    // Actions
    private static final String ACTION_CREATE_SITE = "createSite";
    private static final String ACTION_MODIFY_SITE= "modifySite";
    private static final String ACTION_REMOVE_SITE = "removeSite";
    private static final String ACTION_CONFIRM_REMOVE_SITE = "confirmRemoveSite";
    private static final String ACTION_SEARCH_ALL_SITES = "searchAllSites";
    private static final String ACTION_SEARCH_SITES_BY_ARTIFACT_ID = "searchSiteByArtifactId";
    private static final String ACTION_SEARCH_SITES_BY_NAME = "searchSiteByName";
    private static final String ACTION_SEARCH_SITES_BY_VERSION = "searchSiteByVersion";
    private static final String ACTION_SEARCH_SITES_BY_LAST_UPDATE = "searchSiteByLastUpdate";
    private static final String ACTION_DOWNLOAD_POM = "downloadPom";

    // Infos
    private static final String INFO_SITE_CREATED = "parsepom.info.site.created";
    private static final String INFO_SITE_UPDATED = "parsepom.info.site.updated";
    private static final String INFO_SITE_REMOVED = "parsepom.info.site.removed";
    private static final String INFO_FILE_DOWNLOADED = "parsepom.info.site.fileDownloaded";
    
    // Errors
    private static final String ERROR_NOT_FOUND = "parsepom.error.site.notFound";
    private static final String ERROR_FILE_NOT_FOUND = "parsepom.error.site.fileNotFound";
    private static final String ERROR_FILE_EXISTS = "parsepom.error.site.fileExists";
    private static final int VALUE_INPUT_FILE_NOT_FOUND = -1;
    private static final int VALUE_OUTPUT_FILE_EXISTS = 0;
    private static final int VALUE_SUCCESS = 1;
    
    // Session variable to store working values
    private Site _site;
    
    
    @View( value = VIEW_MANAGE_SITES, defaultView = true )
    public XPage getManageSites( HttpServletRequest request )
    {
        _site = null;
        Map<String, Object> model = getModel(  );
        model.put( MARK_SITE_LIST, SiteHome.getSitesList(  ) );

        return getXPage( TEMPLATE_MANAGE_SITES, request.getLocale(  ), model );
    }

    /**
     * Returns the form to create a site
     *
     * @param request The Http request
     * @return the html code of the site form
     */
    @View( VIEW_CREATE_SITE )
    public XPage getCreateSite( HttpServletRequest request )
    {
        _site = ( _site != null ) ? _site : new Site(  );

        Map<String, Object> model = getModel(  );
        model.put( MARK_SITE, _site );
           
        return getXPage( TEMPLATE_CREATE_SITE, request.getLocale(  ), model );
    }

    /**
     * Process the data capture form of a new site
     *
     * @param request The Http Request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_CREATE_SITE )
    public XPage doCreateSite( HttpServletRequest request )
    {
        populate( _site, request );

        // Check constraints
        if ( !validateBean( _site, getLocale( request ) ) )
        {
            return redirectView( request, VIEW_CREATE_SITE );
        }

        SiteHome.create( _site );
        addInfo( INFO_SITE_CREATED, getLocale( request ) );

        return redirectView( request, VIEW_MANAGE_SITES );
    }

    /**
     * Manages the removal form of a site whose identifier is in the http
     * request
     *
     * @param request The Http request
     * @return the html code to confirm
     */
    @Action( ACTION_CONFIRM_REMOVE_SITE )
    public XPage getConfirmRemoveSite( HttpServletRequest request ) throws SiteMessageException
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_SITE ) );
        UrlItem url = new UrlItem( JSP_PAGE_PORTAL );
        url.addParameter( PARAM_PAGE, MARK_SITE );
        url.addParameter( PARAM_ACTION, ACTION_REMOVE_SITE );
        url.addParameter( PARAMETER_ID_SITE, nId );
        
        SiteMessageService.setMessage(request, MESSAGE_CONFIRM_REMOVE_SITE, SiteMessage.TYPE_CONFIRMATION, url.getUrl(  ));
        return null;
    }

    /**
     * Handles the removal form of a site
     *
     * @param request The Http request
     * @return the jsp URL to display the form to manage sites
     */
    @Action( ACTION_REMOVE_SITE )
    public XPage doRemoveSite( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_SITE ) );
        SiteHome.remove( nId );
        addInfo( INFO_SITE_REMOVED, getLocale( request ) );

        return redirectView( request, VIEW_MANAGE_SITES );
    }

    /**
     * Returns the form to update info about a site
     *
     * @param request The Http request
     * @return The HTML form to update info
     */
    @View( VIEW_MODIFY_SITE )
    public XPage getModifySite( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_SITE ) );

        if ( _site == null  || ( _site.getId( ) != nId ))
        {
            _site = SiteHome.findByPrimaryKey( nId );
        }

        Map<String, Object> model = getModel(  );
        model.put( MARK_SITE, _site );
        
        return getXPage( TEMPLATE_MODIFY_SITE, request.getLocale(  ), model );
    }

    /**
     * Process the change form of a site
     *
     * @param request The Http request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_MODIFY_SITE )
    public XPage doModifySite( HttpServletRequest request )
    {
        populate( _site, request );

        // Check constraints
        if ( !validateBean( _site, getLocale( request ) ) )
        {
            return redirect( request, VIEW_MODIFY_SITE, PARAMETER_ID_SITE, _site.getId( ) );
        }

        SiteHome.update( _site );
        addInfo( INFO_SITE_UPDATED, getLocale( request ) );

        return redirectView( request, VIEW_MANAGE_SITES );
    }
    
    /**
     * Returns the infos about a site
     *
     * @param request The Http request
     * @return The HTML page to display infos
     */
    @View( VIEW_DETAILS_SITE )
    public XPage getDetailsSite( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_SITE ) );

        Collection<Dependency> dependencyList = DependencyHome.getDependencysListBySiteId( nId );
        Map<String, String> listRelease = new HashMap<>( );
        
        for ( Dependency list : dependencyList)
        {
        	Tools tools = ToolsHome.findByArtifactId( list.getArtifactId( ) );
        	if ( tools != null )
        		listRelease.put( tools.getArtifactId( ) , tools.getLastRelease( ));
        }
        
        if ( _site == null  || ( _site.getId( ) != nId ) )
        {
            _site = SiteHome.findByPrimaryKey( nId );
        }
        
        int nMonths = TimeInterval.getMonthDiff( _site.getLastUpdate( ) );
        
        /*System.out.println("==================");
        System.out.println(monthsResult);
        System.out.println(hoursResult);
        System.out.println("==================");*/
        
        Map<String, Object> model = getModel(  );
        model.put( MARK_SITE, _site );
        model.put( MARK_DEPENDENCY_LIST_BY_SITE, dependencyList );
        model.put( MARK_LAST_RELEASE_LIST, listRelease );
        model.put( MARK_LAST_UPDATE_TIME_INTERVAL, nMonths );
        
        return getXPage( TEMPLATE_DETAILS_SITE, request.getLocale(  ), model );
    }
    
    /**
     * Returns the infos about all sites
     *
     * @param request The Http request
     * @return The HTML page to display infos
     */
    @Action( ACTION_SEARCH_ALL_SITES )
    public XPage doSearchAllSites( HttpServletRequest request )
    {
        Collection<Site> siteList = SiteHome.getSitesList( );

        if ( !siteList.isEmpty( ) )
        {
        	Map<String, Object> model = getModel(  );
        	model.put( MARK_SITE_LIST, siteList );
        
        	return getXPage( TEMPLATE_LIST_SITES, request.getLocale(  ), model );
        }
        addError( ERROR_NOT_FOUND, getLocale( request ) );

        return redirectView( request, VIEW_MANAGE_SITES );
    }
    
    /**
     * Returns the infos about all sites ranked by artifactId
     *
     * @param request The Http request
     * @return The HTML page to display infos
     */
    @Action( ACTION_SEARCH_SITES_BY_ARTIFACT_ID )
    public XPage doSearchArtifactId( HttpServletRequest request )
    {
        String strArtifactId = request.getParameter( PARAMETER_ARTIFACT_ID_SITE ).toLowerCase( ) ;
        Collection<Site> siteList = SiteHome.getSitesListByArtifactId( strArtifactId );

        if ( !siteList.isEmpty( ) )
        {
        	Map<String, Object> model = getModel(  );
        	model.put( MARK_SITE_LIST_BY_ARTIFACT_ID, siteList );
        
        	return getXPage( TEMPLATE_LIST_SITES, request.getLocale(  ), model );
        }
        addError( ERROR_NOT_FOUND, getLocale( request ) );

        return redirectView( request, VIEW_MANAGE_SITES );
    }
    
    /**
     * Returns the infos about all sites ranked by name
     *
     * @param request The Http request
     * @return The HTML page to display infos
     */
    @Action( ACTION_SEARCH_SITES_BY_NAME )
    public XPage doSearchName( HttpServletRequest request )
    {
        String strName = request.getParameter( PARAMETER_NAME_SITE ).toLowerCase( ) ;
        Collection<Site> siteList = SiteHome.getSitesListByName( strName );

        if ( !siteList.isEmpty( ) )
        {
        	Map<String, Object> model = getModel(  );
        	model.put( MARK_SITE_LIST_BY_NAME, siteList );
        
        	return getXPage( TEMPLATE_LIST_SITES, request.getLocale(  ), model );
        }
        addError( ERROR_NOT_FOUND, getLocale( request ) );

        return redirectView( request, VIEW_MANAGE_SITES );
    }
    
    /**
     * Returns the infos about all sites ranked by version
     *
     * @param request The Http request
     * @return The HTML page to display infos
     */
    @Action( ACTION_SEARCH_SITES_BY_VERSION )
    public XPage doSearchVersion( HttpServletRequest request )
    {
        String strVersion = request.getParameter( PARAMETER_VERSION_SITE ).toLowerCase( );
        Collection<Site> siteList = SiteHome.getSitesListByVersion( strVersion );

        if ( !siteList.isEmpty( ) )
        { 
        	Map<String, Object> model = getModel(  );
        	model.put( MARK_SITE_LIST_BY_VERSION, siteList );
        
        	return getXPage( TEMPLATE_LIST_SITES, request.getLocale(  ), model );
        }
        addError( ERROR_NOT_FOUND, getLocale( request ) );

        return redirectView( request, VIEW_MANAGE_SITES );
    }
    
    /**
     * Returns the infos about all sites ranked by last update
     *
     * @param request The Http request
     * @return The HTML page to display infos
     */
    @Action( ACTION_SEARCH_SITES_BY_LAST_UPDATE )
    public XPage doSearchLastUpdate( HttpServletRequest request )
    {
        String strLastUpdate = request.getParameter( PARAMETER_VERSION_LAST_UPDATE ).toLowerCase( );
        Collection<Site> siteList = SiteHome.getSitesListByLastUpdate( strLastUpdate );

        if ( !siteList.isEmpty( ) )
        { 
        	Map<String, Object> model = getModel(  );
        	model.put( MARK_SITE_LIST_BY_LAST_UPDATE, siteList );
        
        	return getXPage( TEMPLATE_LIST_SITES, request.getLocale(  ), model );
        }
        addError( ERROR_NOT_FOUND, getLocale( request ) );

        return redirectView( request, VIEW_MANAGE_SITES );
    }
    
    /**
     * Download a pom
     *
     * @param request The Http request
     * @return The HTML page to display infos
     */
    @Action( ACTION_DOWNLOAD_POM )
    public XPage doDownloadPom( HttpServletRequest request )
    {
    	String strId = request.getParameter( PARAMETER_ID_SITE );
     	Site site = SiteHome.findByPrimaryKey( Integer.parseInt( strId ) );
    	String fileInputPath = site.getPath( );
    	Integer nReturn = FileDownloader.download( fileInputPath );
    	
    	if ( nReturn == VALUE_INPUT_FILE_NOT_FOUND )
    	{
    		addError( ERROR_FILE_NOT_FOUND, getLocale( request ) );
    	}
    	else if ( nReturn == VALUE_OUTPUT_FILE_EXISTS )
    	{
    		addError( ERROR_FILE_EXISTS, getLocale( request ) );
    	}
    	else if ( nReturn == VALUE_SUCCESS )
    	{
    		addInfo( INFO_FILE_DOWNLOADED, getLocale( request ) );
    	}
        
        return redirectView( request, VIEW_DETAILS_SITE.concat( "&" ).concat( PARAMETER_ID_SITE ).concat( "=" ).concat( strId ) );
    }
}