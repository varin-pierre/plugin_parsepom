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
import fr.paris.lutece.plugins.parsepom.business.SiteHome;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.web.xpages.XPage;
import fr.paris.lutece.portal.util.mvc.xpage.MVCApplication;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.xpage.annotations.Controller;
import fr.paris.lutece.util.url.UrlItem;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import fr.paris.lutece.portal.service.message.SiteMessageService;
import fr.paris.lutece.portal.service.message.SiteMessage;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import javax.servlet.http.HttpServletRequest;

/**
 * This class provides the user interface to manage Dependency xpages ( manage, create, modify, remove )
 */
 
@Controller( xpageName = "dependency" , pageTitleI18nKey = "parsepom.xpage.dependency.pageTitle" , pagePathI18nKey = "parsepom.xpage.dependency.pagePathLabel" )
public class DependencyXPage extends MVCApplication
{
    // Templates
    private static final String TEMPLATE_MANAGE_DEPENDENCYS="/skin/plugins/parsepom/manage_dependencys.html";
    private static final String TEMPLATE_CREATE_DEPENDENCY="/skin/plugins/parsepom/create_dependency.html";
    private static final String TEMPLATE_MODIFY_DEPENDENCY="/skin/plugins/parsepom/modify_dependency.html";
    private static final String TEMPLATE_DETAILS_DEPENDENCY="/skin/plugins/parsepom/details_dependency.html";
    
    // JSP
    private static final String JSP_PAGE_PORTAL = "jsp/site/Portal.jsp";
    
    // Parameters
    private static final String PARAMETER_ID_DEPENDENCY="id";
    private static final String PARAM_ACTION = "action";
    private static final String PARAM_PAGE = "page";
    private static final String PARAMETER_ARTIFACT_ID_DEPENDENCY = "dependencyArtifactId";
    
    // Markers
    private static final String MARK_DEPENDENCY_LIST = "dependency_list";
    private static final String MARK_DEPENDENCY = "dependency";
    private static final String MARK_SITES_LIST_BY_DEPENDENCY = "sites_list_by_dependency";
    private static final String MARK_DEPENDENCY_LIST_WITHOUT_DUPLICATES = "dependency_list_without_duplicates";
    
    // Message
    private static final String MESSAGE_CONFIRM_REMOVE_DEPENDENCY = "parsepom.message.confirmRemoveDependency";
    
    // Views
    private static final String VIEW_MANAGE_DEPENDENCYS = "manageDependencys";
    private static final String VIEW_CREATE_DEPENDENCY = "createDependency";
    private static final String VIEW_MODIFY_DEPENDENCY = "modifyDependency";
    private static final String VIEW_DETAILS_DEPENDENCY = "detailsDependency";

    // Actions
    private static final String ACTION_CREATE_DEPENDENCY = "createDependency";
    private static final String ACTION_MODIFY_DEPENDENCY= "modifyDependency";
    private static final String ACTION_REMOVE_DEPENDENCY = "removeDependency";
    private static final String ACTION_CONFIRM_REMOVE_DEPENDENCY = "confirmRemoveDependency";
    private static final String ACTION_SEARCH_DEPENDENCY = "searchDependency";

    // Infos
    private static final String INFO_DEPENDENCY_CREATED = "parsepom.info.dependency.created";
    private static final String INFO_DEPENDENCY_UPDATED = "parsepom.info.dependency.updated";
    private static final String INFO_DEPENDENCY_REMOVED = "parsepom.info.dependency.removed";
    
    // Errors
    private static final String ERROR_DEPENDENCY_NOT_FOUND = "parsepom.error.dependency.notFound";
    
    // Session variable to store working values
    private Dependency _dependency;
    
    @View( value = VIEW_MANAGE_DEPENDENCYS, defaultView = true )
    public XPage getManageDependencys( HttpServletRequest request )
    {
        _dependency = null;
        Map<String, Object> model = getModel(  );
        model.put( MARK_DEPENDENCY_LIST, DependencyHome.getDependencysList(  ) );
        model.put( MARK_DEPENDENCY_LIST_WITHOUT_DUPLICATES, DependencyHome.getDependencysListWithoutDuplicates(  ) );

        return getXPage( TEMPLATE_MANAGE_DEPENDENCYS, request.getLocale(  ), model );
    }

    /**
     * Returns the form to create a dependency
     *
     * @param request The Http request
     * @return the html code of the dependency form
     */
    @View( VIEW_CREATE_DEPENDENCY )
    public XPage getCreateDependency( HttpServletRequest request )
    {
        _dependency = ( _dependency != null ) ? _dependency : new Dependency(  );

        Map<String, Object> model = getModel(  );
        model.put( MARK_DEPENDENCY, _dependency );
           
        return getXPage( TEMPLATE_CREATE_DEPENDENCY, request.getLocale(  ), model );
    }

    /**
     * Process the data capture form of a new dependency
     *
     * @param request The Http Request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_CREATE_DEPENDENCY )
    public XPage doCreateDependency( HttpServletRequest request )
    {
        populate( _dependency, request );

        // Check constraints
        if ( !validateBean( _dependency, getLocale( request ) ) )
        {
            return redirectView( request, VIEW_CREATE_DEPENDENCY );
        }

        DependencyHome.create( _dependency );
        
        int nDId = _dependency.getId( );
        int nDSiteId = _dependency.getSiteId( );
        SiteHome.updateDependencyInSite( nDId, nDSiteId );
        
        addInfo( INFO_DEPENDENCY_CREATED, getLocale( request ) );

        return redirectView( request, VIEW_MANAGE_DEPENDENCYS );
    }

    /**
     * Manages the removal form of a dependency whose identifier is in the http
     * request
     *
     * @param request The Http request
     * @return the html code to confirm
     */
    @Action( ACTION_CONFIRM_REMOVE_DEPENDENCY )
    public XPage getConfirmRemoveDependency( HttpServletRequest request ) throws SiteMessageException
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_DEPENDENCY ) );
        UrlItem url = new UrlItem( JSP_PAGE_PORTAL );
        url.addParameter( PARAM_PAGE, MARK_DEPENDENCY );
        url.addParameter( PARAM_ACTION, ACTION_REMOVE_DEPENDENCY );
        url.addParameter( PARAMETER_ID_DEPENDENCY, nId );
        
        SiteMessageService.setMessage(request, MESSAGE_CONFIRM_REMOVE_DEPENDENCY, SiteMessage.TYPE_CONFIRMATION, url.getUrl(  ));
        return null;
    }

    /**
     * Handles the removal form of a dependency
     *
     * @param request The Http request
     * @return the jsp URL to display the form to manage dependencys
     */
    @Action( ACTION_REMOVE_DEPENDENCY )
    public XPage doRemoveDependency( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_DEPENDENCY ) );
        
        if ( _dependency == null  || ( _dependency.getId( ) != nId ))
        {
            _dependency = DependencyHome.findByPrimaryKey( nId );
        }
        
        int nDSiteId = _dependency.getSiteId( );
        
        DependencyHome.remove( nId );      
        SiteHome.removeDependencyFromSite( nId, nDSiteId );
        
        addInfo( INFO_DEPENDENCY_REMOVED, getLocale( request ) );

        return redirectView( request, VIEW_MANAGE_DEPENDENCYS );
    }

    /**
     * Returns the form to update info about a dependency
     *
     * @param request The Http request
     * @return The HTML form to update info
     */
    @View( VIEW_MODIFY_DEPENDENCY )
    public XPage getModifyDependency( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_DEPENDENCY ) );

        if ( _dependency == null  || ( _dependency.getId( ) != nId ))
        {
            _dependency = DependencyHome.findByPrimaryKey( nId );
        }

        Map<String, Object> model = getModel(  );
        model.put( MARK_DEPENDENCY, _dependency );
        
        return getXPage( TEMPLATE_MODIFY_DEPENDENCY, request.getLocale(  ), model );
    }

    /**
     * Process the change form of a dependency
     *
     * @param request The Http request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_MODIFY_DEPENDENCY )
    public XPage doModifyDependency( HttpServletRequest request )
    {
        populate( _dependency, request );

        // Check constraints
        if ( !validateBean( _dependency, getLocale( request ) ) )
        {
            return redirect( request, VIEW_MODIFY_DEPENDENCY, PARAMETER_ID_DEPENDENCY, _dependency.getId( ) );
        }

        DependencyHome.update( _dependency );
        addInfo( INFO_DEPENDENCY_UPDATED, getLocale( request ) );

        return redirectView( request, VIEW_MANAGE_DEPENDENCYS );
    }
    
    /**
     * Returns infos about a dependency
     *
     * @param request The Http request
     * @return The HTML page to display infos
     */
    @View( VIEW_DETAILS_DEPENDENCY )
    public XPage getDetailsDependency( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_DEPENDENCY ) );

        if ( _dependency == null  || ( _dependency.getId( ) != nId ))
        {
            _dependency = DependencyHome.findByPrimaryKey( nId );
        }
        
        String strArtifactId = _dependency.getArtifactId( );
        List<List<Integer>> idSitesList = SiteHome.getIdSitesListByDependency( );
        
        Map<String, Object> model = getModel(  );
        model.put( MARK_DEPENDENCY, _dependency );
        model.put( MARK_SITES_LIST_BY_DEPENDENCY,  DependencyHome.getSitesListByDependencyId( strArtifactId, idSitesList ) );
        
        return getXPage( TEMPLATE_DETAILS_DEPENDENCY, request.getLocale(  ), model );
    }
    
    /**
     * Return Infos about dependency
     *
     * @param request The Http request
     * @return the HTML page to display infos
     */
    @Action( ACTION_SEARCH_DEPENDENCY )
    public XPage doSearchDependency( HttpServletRequest request )
    {
    	String strName = request.getParameter( PARAMETER_ARTIFACT_ID_DEPENDENCY ).toLowerCase( );        
        Collection<Dependency> list = DependencyHome.getDependencysListWithoutDuplicates(  );
        
        for (Dependency depend : list)
        {
        	if ( depend.getArtifactId( ).equals( strName ) )
        	{
        		_dependency = depend;
        		
        		String strArtifactId = _dependency.getArtifactId( );
                List<List<Integer>> idSitesList = SiteHome.getIdSitesListByDependency( );
                 
                Map<String, Object> model = getModel(  );
                model.put( MARK_DEPENDENCY, _dependency );
                model.put( MARK_SITES_LIST_BY_DEPENDENCY,  DependencyHome.getSitesListByDependencyId( strArtifactId, idSitesList ) );
                
                return getXPage( TEMPLATE_DETAILS_DEPENDENCY, request.getLocale(  ), model );
        	}
        }
        addError( ERROR_DEPENDENCY_NOT_FOUND, getLocale( request ) );

        return redirectView( request, VIEW_MANAGE_DEPENDENCYS );
    }
}
