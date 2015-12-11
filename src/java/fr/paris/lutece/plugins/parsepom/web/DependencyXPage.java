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
import fr.paris.lutece.plugins.parsepom.business.Tools;
import fr.paris.lutece.plugins.parsepom.business.ToolsHome;
import fr.paris.lutece.plugins.parsepom.services.HttpProcess;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.web.xpages.XPage;
import fr.paris.lutece.portal.util.mvc.xpage.MVCApplication;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.xpage.annotations.Controller;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

/**
 * This class provides the user interface to manage Dependency xpages ( manage, create, modify, remove )
 */
 
@Controller( xpageName = "dependency" , pageTitleI18nKey = "parsepom.xpage.dependency.pageTitle" , pagePathI18nKey = "parsepom.xpage.dependency.pagePathLabel" )
public class DependencyXPage extends MVCApplication
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// Templates
    private static final String TEMPLATE_MANAGE_DEPENDENCIES="/skin/plugins/parsepom/manage_dependencies.html";
    private static final String TEMPLATE_DETAILS_DEPENDENCY="/skin/plugins/parsepom/details_dependency.html";
    
    // Parameters
    private static final String PARAMETER_ID_DEPENDENCY = "id";
    private static final String PARAMETER_ARTIFACT_ID_DEPENDENCY = "dependencyArtifactId";
    
    // Markers
    private static final String MARK_DEPENDENCY = "dependency";
    private static final String MARK_SITES_LIST_BY_DEPENDENCY = "sites_list_by_dependency";
    private static final String MARK_DEPENDENCY_LIST_WITHOUT_DUPLICATES = "dependency_list_without_duplicates";
    private static final String MARK_LAST_RELEASE_LIST = "last_release_list";
    private static final String MARK_LAST_RELEASE_STRING = "last_release";
    private static final String MARK_SONAR_DATA = "sonar_data";
    
    // Views
    private static final String VIEW_MANAGE_DEPENDENCYS = "manageDependencys";
    private static final String VIEW_DETAILS_DEPENDENCY = "detailsDependency";

    // Actions
    private static final String ACTION_SEARCH_DEPENDENCY_BY_ARTIFACT_ID = "searchDependencyByArtifactId";
    
    // Errors
    private static final String ERROR_NOT_FOUND = "parsepom.error.dependency.notFound";
    
    // Session variable to store working values
    private Dependency _dependency;
    
    @View( value = VIEW_MANAGE_DEPENDENCYS, defaultView = true )
    public XPage getManageDependencys( HttpServletRequest request )
    {
        _dependency = null;
        
        Collection<Dependency> dependencyList = DependencyHome.getDependencysListWithoutDuplicates(  );

        if ( !dependencyList.isEmpty( ) )
        {
        	Map<String, Object> model = getModel(  );
            Map<String, String> listRelease = new HashMap<>( );
            
            for ( Dependency list : dependencyList)
            {
            	Tools tools = ToolsHome.findByArtifactId( list.getArtifactId( ) );
            	if ( tools != null )
            		listRelease.put( tools.getArtifactId( ) , tools.getLastRelease( ));
            }
        	model.put( MARK_LAST_RELEASE_LIST, listRelease );
        	model.put( MARK_DEPENDENCY_LIST_WITHOUT_DUPLICATES, dependencyList );
        	
        	return getXPage( TEMPLATE_MANAGE_DEPENDENCIES, request.getLocale(  ), model );
        }
        addError( ERROR_NOT_FOUND, getLocale( request ) );

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
        String strRelease = "";
        
        Tools tools = ToolsHome.findByArtifactId( strArtifactId );
    	if ( tools != null )
    	{
    		strRelease = tools.getLastRelease( );
    	}
    	
    	HashMap<String, String> sonarData = new HashMap<String, String>( );
    	sonarData = HttpProcess.getSonarMetricsFromArtifactId( strArtifactId );
    	
        Map<String, Object> model = getModel(  );
        model.put( MARK_DEPENDENCY, _dependency );
        model.put( MARK_LAST_RELEASE_STRING, strRelease );
        model.put( MARK_SITES_LIST_BY_DEPENDENCY, DependencyHome.getSitesListByDependencyId( strArtifactId, idSitesList ) );
        model.put( MARK_SONAR_DATA, sonarData );
        
        return getXPage( TEMPLATE_DETAILS_DEPENDENCY, request.getLocale(  ), model );
    }
    
    
    /**
     * Return Infos about dependency
     *
     * @param request The Http request
     * @return the HTML page to display infos
     */
    @Action( ACTION_SEARCH_DEPENDENCY_BY_ARTIFACT_ID )
    public XPage doSearchDependencyByArtifactId( HttpServletRequest request )
    {
    	String strName = request.getParameter( PARAMETER_ARTIFACT_ID_DEPENDENCY ).toLowerCase( );        
        Collection<Dependency> list = DependencyHome.getDependencysListWithoutDuplicates(  );
        
        for (Dependency _dependency : list)
        {
        	String strArtifactId = _dependency.getArtifactId( );
        	
        	if ( strArtifactId.equals( strName ) )
        	{
        		String strRelease = "";
        		
        		Tools tools = ToolsHome.findByArtifactId( strArtifactId );
            	if ( tools != null )
            	{
            		strRelease = tools.getLastRelease( );
            	}
        		
                List<List<Integer>> idSitesList = SiteHome.getIdSitesListByDependency( );
                
                HashMap<String, String> sonarData = new HashMap<String, String>( );
            	sonarData = HttpProcess.getSonarMetricsFromArtifactId( strArtifactId );
                
                Map<String, Object> model = getModel(  );
                model.put( MARK_DEPENDENCY, _dependency );
                model.put( MARK_LAST_RELEASE_STRING, strRelease );
                model.put( MARK_SITES_LIST_BY_DEPENDENCY,  DependencyHome.getSitesListByDependencyId( strArtifactId, idSitesList ) );
                model.put( MARK_SONAR_DATA, sonarData );
                
                return getXPage( TEMPLATE_DETAILS_DEPENDENCY, request.getLocale(  ), model );
        	}
        }
        addError( ERROR_NOT_FOUND, getLocale( request ) );

        return redirectView( request, VIEW_MANAGE_DEPENDENCYS );
    }
}
