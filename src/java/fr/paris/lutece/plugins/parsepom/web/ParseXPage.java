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
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.xpage.annotations.Controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.Model;

import com.sun.xml.bind.v2.schemagen.xmlschema.List; 

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
    Site s;
    Dependency d;
    String path;
    @View( value = VIEW_TMP )
    public XPage getTmp( HttpServletRequest request )
    {
    	
    	Map<String, Object> model = getModel(  );
    	model.put( MARK_PARSE, path);
		addInfo( "path to ", getLocale( request ) );

        return getXPage( TEMPLATE_TMP,request.getLocale(  ), model );
    }
    
    @Action( ACTION_PARSE )
    public XPage doParse( HttpServletRequest request )
    {    
		
		int len;

		path= request.getParameter("path");
		StringBuilder tmp = new StringBuilder(path);
		len = path.length() - 1;
		if (len > 0 && path.charAt( len ) != '/')
		{
			tmp.append("/");
		}
		path = tmp.toString();

		
        return redirectView( request, VIEW_TMP );
    }
    
}
