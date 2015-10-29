 /*
 * Copyright (c) 2002-2012, Mairie de Paris
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
package fr.paris.lutece.plugins.parsepom.web.rs;

import fr.paris.lutece.plugins.parsepom.business.Site;
import fr.paris.lutece.plugins.parsepom.business.SiteHome;
import fr.paris.lutece.plugins.rest.service.RestConstants;
import fr.paris.lutece.plugins.rest.util.json.JSONUtil;
import fr.paris.lutece.plugins.rest.util.xml.XMLUtil;
import fr.paris.lutece.util.xml.XmlUtil;
import fr.paris.lutece.portal.service.util.AppLogService;
import java.io.IOException;

import net.sf.json.JSONObject;

import java.util.Collection;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Page resource
 */
 
@Path( RestConstants.BASE_PATH + Constants.PLUGIN_PATH + Constants.SITE_PATH )
public class SiteRest
{
    private static final String KEY_SITES = "sites";
    private static final String KEY_SITE = "site";
    
    private static final String KEY_ID = "id";
    private static final String KEY_ARTIFACT_ID = "artifact_id";
    private static final String KEY_NAME = "name";
    private static final String KEY_VERSION = "version";
    private static final String KEY_ID_PLUGINS = "id_plugins";
    private static final String KEY_LAST_UPDATE = "last_update";
    private static final String KEY_PATH = "path";
    
    @GET
    @Path( Constants.ALL_PATH )
    public Response getSites( @HeaderParam(HttpHeaders.ACCEPT) String accept , @QueryParam( Constants.FORMAT_QUERY ) String format ) throws IOException
    {
        String entity;
        String mediaType;
        
        if ( (accept != null && accept.contains(MediaType.APPLICATION_JSON)) || (format != null && format.equals(Constants.MEDIA_TYPE_JSON)) )
        {
            entity = getSitesJson();
            mediaType = MediaType.APPLICATION_JSON;
        }
        else
        {
            entity = getSitesXml();
            mediaType = MediaType.APPLICATION_XML;
        }
        return Response
            .ok(entity, mediaType)
            .build();
    }
    
    /**
     * Gets all resources list in XML format
     * @return The list
     */
    public String getSitesXml( )
    {
        StringBuffer sbXML = new StringBuffer( XmlUtil.getXmlHeader() );
        Collection<Site> list = SiteHome.getSitesList();
        
        XmlUtil.beginElement( sbXML , KEY_SITES );

        for ( Site site : list )
        {
            addSiteXml( sbXML, site );
        }
        
        XmlUtil.endElement( sbXML , KEY_SITES );

        return sbXML.toString(  );
    }
    
    /**
     * Gets all resources list in JSON format
     * @return The list
     */
    public String getSitesJson( )
    {
        JSONObject jsonSite = new JSONObject(  );
        JSONObject json = new JSONObject(  );
        
        Collection<Site> list = SiteHome.getSitesList();
        
        for ( Site site : list )
        {
            addSiteJson( jsonSite, site );
        }
        
        json.accumulate( KEY_SITES, jsonSite );
        
        return json.toString( );
    }
    
    @GET
    @Path( "{" + Constants.ID_PATH + "}" )
    public Response getSite( @PathParam( Constants.ID_PATH ) String strId, @HeaderParam(HttpHeaders.ACCEPT) String accept , @QueryParam( Constants.FORMAT_QUERY ) String format ) throws IOException
    {
        String entity;
        String mediaType;
        
        if ( (accept != null && accept.contains(MediaType.APPLICATION_JSON)) || (format != null && format.equals(Constants.MEDIA_TYPE_JSON)) )
        {
            entity = getSiteJson( strId );
            mediaType = MediaType.APPLICATION_JSON;
        }
        else
        {
            entity = getSiteXml( strId );
            mediaType = MediaType.APPLICATION_XML;
        }
        return Response
            .ok(entity, mediaType)
            .build();
    }
    
    /**
     * Gets a resource in XML format
     * @param strId The resource ID
     * @return The XML output
     */
    public String getSiteXml( String strId )
    {
        StringBuffer sbXML = new StringBuffer(  );
        
        try
        {
            int nId = Integer.parseInt( strId );
            Site site = SiteHome.findByPrimaryKey( nId );

            if ( site != null )
            {
                sbXML.append( "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" );
                addSiteXml( sbXML, site );
            }
        }
        catch ( NumberFormatException e )
        {
            sbXML.append( XMLUtil.formatError( "Invalid site number", 3 ) );
        }
        catch ( Exception e )
        {
            sbXML.append( XMLUtil.formatError( "Site not found", 1 ) );
        }

        return sbXML.toString(  );
    }
    
    /**
     * Gets a resource in JSON format
     * @param strId The resource ID
     * @return The JSON output
     */
    public String getSiteJson( String strId )
    {
        JSONObject json = new JSONObject(  );
        String strJson = "";

        try
        {
            int nId = Integer.parseInt( strId );
            Site site = SiteHome.findByPrimaryKey( nId );

            if ( site != null )
            {
                addSiteJson( json, site );
                strJson = json.toString( );
            }
        }
        catch ( NumberFormatException e )
        {
            strJson = JSONUtil.formatError( "Invalid site number", 3 );
        }
        catch ( Exception e )
        {
            strJson = JSONUtil.formatError( "Site not found", 1 );
        }

        return strJson;
    }
    
    @DELETE
    @Path( "{" + Constants.ID_PATH + "}" )
    public Response deleteSite( @PathParam( Constants.ID_PATH ) String strId, @HeaderParam(HttpHeaders.ACCEPT) String accept, @QueryParam( Constants.FORMAT_QUERY ) String format ) throws IOException
    {
        try
        {
            int nId = Integer.parseInt( strId );
            
            if ( SiteHome.findByPrimaryKey( nId ) != null )
            {
                SiteHome.remove( nId );
            }
        }
        catch ( NumberFormatException e )
        {
            AppLogService.error( "Invalid site number" );
        }
        return getSites(accept, format);
    }
    
    @POST
    public Response createSite(
    @FormParam( KEY_ID ) String id,
    @FormParam( KEY_ARTIFACT_ID ) String artifact_id,
    @FormParam( KEY_NAME ) String name,
    @FormParam( KEY_VERSION ) String version,
    @FormParam( KEY_ID_PLUGINS ) String id_plugins,
    @FormParam( KEY_LAST_UPDATE ) String last_update,
    @FormParam( KEY_PATH ) String path,
    @HeaderParam(HttpHeaders.ACCEPT) String accept, @QueryParam( Constants.FORMAT_QUERY ) String format) throws IOException
    {
        if( id != null )
        {
            int nId = Integer.parseInt( KEY_ID );

            Site site = SiteHome.findByPrimaryKey( nId );

            if ( site != null )
            {
            	site.setArtifactId( artifact_id );
                site.setName( name );
                site.setVersion( version );
                site.setIdPlugins( id_plugins );
                site.setLastUpdate( last_update );
                site.setPath( path );
                SiteHome.update( site );
            }
        }
        else
        {
            Site site = new Site( );
            
            site.setArtifactId( artifact_id );
            site.setName( name );
            site.setVersion( version );
            site.setIdPlugins( id_plugins );
            site.setLastUpdate( last_update );
            site.setPath( path );
            SiteHome.create( site );
        }
        return getSites(accept, format);
    }
    
    /**
     * Write a site into a buffer
     * @param sbXML The buffer
     * @param site The site
     */
    private void addSiteXml( StringBuffer sbXML, Site site )
    {
        XmlUtil.beginElement( sbXML, KEY_SITE );
        XmlUtil.addElement( sbXML, KEY_ID , site.getId( ) );
        XmlUtil.addElement( sbXML, KEY_ARTIFACT_ID , site.getArtifactId( ) );
        XmlUtil.addElement( sbXML, KEY_NAME , site.getName( ) );
        XmlUtil.addElement( sbXML, KEY_VERSION , site.getVersion( ) );
        XmlUtil.addElement( sbXML, KEY_ID_PLUGINS , site.getIdPlugins( ) );
        XmlUtil.addElement( sbXML, KEY_LAST_UPDATE , site.getLastUpdate( ) );
        XmlUtil.addElement( sbXML, KEY_PATH , site.getPath( ) );
        XmlUtil.endElement( sbXML, KEY_SITE );
    }
    
    /**
     * Write a site into a JSON Object
     * @param json The JSON Object
     * @param site The site
     */
    private void addSiteJson( JSONObject json, Site site )
    {
        JSONObject jsonSite = new JSONObject(  );
        jsonSite.accumulate( KEY_ID , site.getId( ) );
        jsonSite.accumulate( KEY_ARTIFACT_ID, site.getArtifactId( ) );
        jsonSite.accumulate( KEY_NAME, site.getName( ) );
        jsonSite.accumulate( KEY_VERSION, site.getVersion( ) );
        jsonSite.accumulate( KEY_ID_PLUGINS, site.getIdPlugins( ) );
        jsonSite.accumulate( KEY_LAST_UPDATE, site.getLastUpdate( ) );
        jsonSite.accumulate( KEY_PATH, site.getPath( ) );
        json.accumulate( KEY_SITE, jsonSite );
    }
}