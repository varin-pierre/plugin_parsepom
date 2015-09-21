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
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.util.url.UrlItem;

import java.util.List;
import java.util.Map;


import javax.servlet.http.HttpServletRequest;


/**
 * This class provides the user interface to manage Dependency features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageDependencys.jsp", controllerPath = "jsp/admin/plugins/parsepom/", right = "PARSEPOM_MANAGEMENT" )
public class DependencyJspBean extends ManageParsepomJspBean
{

    ////////////////////////////////////////////////////////////////////////////
    // Constants

    // templates
    private static final String TEMPLATE_MANAGE_DEPENDENCYS = "/admin/plugins/parsepom/manage_dependencys.html";
    private static final String TEMPLATE_CREATE_DEPENDENCY = "/admin/plugins/parsepom/create_dependency.html";
    private static final String TEMPLATE_MODIFY_DEPENDENCY = "/admin/plugins/parsepom/modify_dependency.html";


    // Parameters
    private static final String PARAMETER_ID_DEPENDENCY = "id";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_DEPENDENCYS = "parsepom.manage_dependencys.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_DEPENDENCY = "parsepom.modify_dependency.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_DEPENDENCY = "parsepom.create_dependency.pageTitle";

    // Markers
    private static final String MARK_DEPENDENCY_LIST = "dependency_list";
    private static final String MARK_DEPENDENCY = "dependency";

    private static final String JSP_MANAGE_DEPENDENCYS = "jsp/admin/plugins/parsepom/ManageDependencys.jsp";

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_DEPENDENCY = "parsepom.message.confirmRemoveDependency";
    private static final String PROPERTY_DEFAULT_LIST_DEPENDENCY_PER_PAGE = "parsepom.listDependencys.itemsPerPage";
 
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "parsepom.model.entity.dependency.attribute.";

    // Views
    private static final String VIEW_MANAGE_DEPENDENCYS = "manageDependencys";
    private static final String VIEW_CREATE_DEPENDENCY = "createDependency";
    private static final String VIEW_MODIFY_DEPENDENCY = "modifyDependency";

    // Actions
    private static final String ACTION_CREATE_DEPENDENCY = "createDependency";
    private static final String ACTION_MODIFY_DEPENDENCY = "modifyDependency";
    private static final String ACTION_REMOVE_DEPENDENCY = "removeDependency";
    private static final String ACTION_CONFIRM_REMOVE_DEPENDENCY = "confirmRemoveDependency";

    // Infos
    private static final String INFO_DEPENDENCY_CREATED = "parsepom.info.dependency.created";
    private static final String INFO_DEPENDENCY_UPDATED = "parsepom.info.dependency.updated";
    private static final String INFO_DEPENDENCY_REMOVED = "parsepom.info.dependency.removed";
    
    // Session variable to store working values
    private Dependency _dependency;
    
    
    @View( value = VIEW_MANAGE_DEPENDENCYS, defaultView = true )
    public String getManageDependencys( HttpServletRequest request )
    {
        _dependency = null;
        List<Dependency> listDependencys = (List<Dependency>) DependencyHome.getDependencysList(  );
        Map<String, Object> model = getPaginatedListModel( request, MARK_DEPENDENCY_LIST, listDependencys, JSP_MANAGE_DEPENDENCYS );

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_DEPENDENCYS, TEMPLATE_MANAGE_DEPENDENCYS, model );
    }

    /**
     * Returns the form to create a dependency
     *
     * @param request The Http request
     * @return the html code of the dependency form
     */
    @View( VIEW_CREATE_DEPENDENCY )
    public String getCreateDependency( HttpServletRequest request )
    {
        _dependency = ( _dependency != null ) ? _dependency : new Dependency(  );

        Map<String, Object> model = getModel(  );
        model.put( MARK_DEPENDENCY, _dependency );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_DEPENDENCY, TEMPLATE_CREATE_DEPENDENCY, model );
    }

    /**
     * Process the data capture form of a new dependency
     *
     * @param request The Http Request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_CREATE_DEPENDENCY )
    public String doCreateDependency( HttpServletRequest request )
    {
        populate( _dependency, request );

        // Check constraints
        if ( !validateBean( _dependency, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirectView( request, VIEW_CREATE_DEPENDENCY );
        }

        DependencyHome.create( _dependency );
        addInfo( INFO_DEPENDENCY_CREATED, getLocale(  ) );

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
    public String getConfirmRemoveDependency( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_DEPENDENCY ) );
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_DEPENDENCY ) );
        url.addParameter( PARAMETER_ID_DEPENDENCY, nId );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_DEPENDENCY,
                url.getUrl(  ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Handles the removal form of a dependency
     *
     * @param request The Http request
     * @return the jsp URL to display the form to manage dependencys
     */
    @Action( ACTION_REMOVE_DEPENDENCY )
    public String doRemoveDependency( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_DEPENDENCY ) );
        DependencyHome.remove( nId );
        addInfo( INFO_DEPENDENCY_REMOVED, getLocale(  ) );

        return redirectView( request, VIEW_MANAGE_DEPENDENCYS );
    }

    /**
     * Returns the form to update info about a dependency
     *
     * @param request The Http request
     * @return The HTML form to update info
     */
    @View( VIEW_MODIFY_DEPENDENCY )
    public String getModifyDependency( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_DEPENDENCY ) );

        if ( _dependency == null || ( _dependency.getId(  ) != nId ))
        {
            _dependency = DependencyHome.findByPrimaryKey( nId );
        }

        Map<String, Object> model = getModel(  );
        model.put( MARK_DEPENDENCY, _dependency );

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_DEPENDENCY, TEMPLATE_MODIFY_DEPENDENCY, model );
    }

    /**
     * Process the change form of a dependency
     *
     * @param request The Http request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_MODIFY_DEPENDENCY )
    public String doModifyDependency( HttpServletRequest request )
    {
        populate( _dependency, request );

        // Check constraints
        if ( !validateBean( _dependency, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirect( request, VIEW_MODIFY_DEPENDENCY, PARAMETER_ID_DEPENDENCY, _dependency.getId( ) );
        }

        DependencyHome.update( _dependency );
        addInfo( INFO_DEPENDENCY_UPDATED, getLocale(  ) );

        return redirectView( request, VIEW_MANAGE_DEPENDENCYS );
    }
}
