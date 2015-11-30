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

import java.util.Collection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.parsepom.business.Dependency;
import fr.paris.lutece.plugins.parsepom.business.DependencyHome;
import fr.paris.lutece.plugins.parsepom.services.Global;
import fr.paris.lutece.plugins.parsepom.services.HttpProcess;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.xpage.MVCApplication;
import fr.paris.lutece.portal.util.mvc.xpage.annotations.Controller;
import fr.paris.lutece.portal.web.xpages.XPage;


/**
 * This class provides the user interface to manage Tools xpages.
 */

@Controller( xpageName = "tools" , pageTitleI18nKey = "parsepom.xpage.tools.pageTitle" , pagePathI18nKey = "parsepom.xpage.tools.pagePathLabel" )
public class ToolsXPage extends MVCApplication
{
	
	   /**
	    * 
	    */
		private static final long serialVersionUID = 1L;

		// Templates
		private static final String TEMPLATE_PARSEPOM="/skin/plugins/parsepom/manage_parsepom.html";
	    private static final String TEMPLATE_TOOLS="/skin/plugins/parsepom/manage_tools.html";
	 
	    // Markers
	    private static final String MARK_DATA_EXIST="exist";
	    
	    // Views
	    private static final String VIEW_TOOLS = "tools";

	    // Actions
	    private static final String ACTION_TOOLS = "tools";
	    
	    // Infos
	    private static final String INFO_TOOLS_UPDATED = "parsepom.info.tools.updated";
	  
	    
	    /**
	     * Returns the page home.
	     * @param request The HTTP request
	     * @return The view
	     */
	    @View( value = VIEW_TOOLS, defaultView = true )
	    public XPage getParse( HttpServletRequest request )
	    {  	
	    	return getXPage( TEMPLATE_TOOLS, request.getLocale(  ) );
	    }
	    
	    /**
	     * 
	     * @param request
	     * @return XPage
	     */
	    @Action( ACTION_TOOLS )
	    public XPage doUpdate( HttpServletRequest request )
	    {		
	    	Collection<Dependency> dependencyList = DependencyHome.getDependencysListWithoutDuplicates( );
	    	
	    	HttpProcess.getLastReleases( dependencyList );
	    	
	    	Map<String, Object> model = getModel(  );
	        model.put( MARK_DATA_EXIST, Global._boolNotEmptyDB );
	    	
	    	addInfo( INFO_TOOLS_UPDATED, getLocale( request ) );
	    	
	    	return getXPage( TEMPLATE_PARSEPOM, request.getLocale( ), model );
	    }
}
