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

import fr.paris.lutece.plugins.parsepom.business.Dependency;
import fr.paris.lutece.plugins.parsepom.business.DependencyHome;
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
 
@Path( RestConstants.BASE_PATH + Constants.PLUGIN_PATH + Constants.DEPENDENCY_PATH )
public class DependencyRest
{
    private static final String KEY_DEPENDENCYS = "dependencys";
    private static final String KEY_DEPENDENCY = "dependency";
    
    private static final String KEY_ID = "id";
    private static final String KEY_GROUP_ID = "group_id";
    private static final String KEY_ARTIFACT_ID = "artifact_id";
    private static final String KEY_VERSION = "version";
    private static final String KEY_TYPE = "type";
    private static final String KEY_SITE_ID = "site_id";
    
    @GET
    @Path( Constants.ALL_PATH )
    public Response getDependencys( @HeaderParam(HttpHeaders.ACCEPT) String accept , @QueryParam( Constants.FORMAT_QUERY ) String format ) throws IOException
    {
        String entity;
        String mediaType;
        
        if ( (accept != null && accept.contains(MediaType.APPLICATION_JSON)) || (format != null && format.equals(Constants.MEDIA_TYPE_JSON)) )
        {
            entity = getDependencysJson();
            mediaType = MediaType.APPLICATION_JSON;
        }
        else
        {
            entity = getDependencysXml();
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
    public String getDependencysXml( )
    {
        StringBuffer sbXML = new StringBuffer( XmlUtil.getXmlHeader() );
        Collection<Dependency> list = DependencyHome.getDependencysList();
        
        XmlUtil.beginElement( sbXML , KEY_DEPENDENCYS );

        for ( Dependency dependency : list )
        {
            addDependencyXml( sbXML, dependency );
        }
        
        XmlUtil.endElement( sbXML , KEY_DEPENDENCYS );

        return sbXML.toString(  );
    }
    
    /**
     * Gets all resources list in JSON format
     * @return The list
     */
    public String getDependencysJson( )
    {
        JSONObject jsonDependency = new JSONObject(  );
        JSONObject json = new JSONObject(  );
        
        Collection<Dependency> list = DependencyHome.getDependencysList();
        
        for ( Dependency dependency : list )
        {
            addDependencyJson( jsonDependency, dependency );
        }
        
        json.accumulate( KEY_DEPENDENCYS, jsonDependency );
        
        return json.toString( );
    }
    
    @GET
    @Path( "{" + Constants.ID_PATH + "}" )
    public Response getDependency( @PathParam( Constants.ID_PATH ) String strId, @HeaderParam(HttpHeaders.ACCEPT) String accept , @QueryParam( Constants.FORMAT_QUERY ) String format ) throws IOException
    {
        String entity;
        String mediaType;
        
        if ( (accept != null && accept.contains(MediaType.APPLICATION_JSON)) || (format != null && format.equals(Constants.MEDIA_TYPE_JSON)) )
        {
            entity = getDependencyJson( strId );
            mediaType = MediaType.APPLICATION_JSON;
        }
        else
        {
            entity = getDependencyXml( strId );
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
    public String getDependencyXml( String strId )
    {
        StringBuffer sbXML = new StringBuffer(  );
        
        try
        {
            int nId = Integer.parseInt( strId );
            Dependency dependency = DependencyHome.findByPrimaryKey( nId );

            if ( dependency != null )
            {
                sbXML.append( "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" );
                addDependencyXml( sbXML, dependency );
            }
        }
        catch ( NumberFormatException e )
        {
            sbXML.append( XMLUtil.formatError( "Invalid dependency number", 3 ) );
        }
        catch ( Exception e )
        {
            sbXML.append( XMLUtil.formatError( "Dependency not found", 1 ) );
        }

        return sbXML.toString(  );
    }
    
    /**
     * Gets a resource in JSON format
     * @param strId The resource ID
     * @return The JSON output
     */
    public String getDependencyJson( String strId )
    {
        JSONObject json = new JSONObject(  );
        String strJson = "";

        try
        {
            int nId = Integer.parseInt( strId );
            Dependency dependency = DependencyHome.findByPrimaryKey( nId );

            if ( dependency != null )
            {
                addDependencyJson( json, dependency );
                strJson = json.toString( );
            }
        }
        catch ( NumberFormatException e )
        {
            strJson = JSONUtil.formatError( "Invalid dependency number", 3 );
        }
        catch ( Exception e )
        {
            strJson = JSONUtil.formatError( "Dependency not found", 1 );
        }

        return strJson;
    }
    
    @DELETE
    @Path( "{" + Constants.ID_PATH + "}" )
    public Response deleteDependency( @PathParam( Constants.ID_PATH ) String strId, @HeaderParam(HttpHeaders.ACCEPT) String accept, @QueryParam( Constants.FORMAT_QUERY ) String format ) throws IOException
    {
        try
        {
            int nId = Integer.parseInt( strId );
            
            if ( DependencyHome.findByPrimaryKey( nId ) != null )
            {
                DependencyHome.remove( nId );
            }
        }
        catch ( NumberFormatException e )
        {
            AppLogService.error( "Invalid dependency number" );
        }
        return getDependencys(accept, format);
    }
    
    @POST
    public Response createDependency(
    @FormParam( KEY_ID ) String id,
    @FormParam( "group_id" ) String group_id, 
    @FormParam( "artifact_id" ) String artifact_id, 
    @FormParam( "version" ) String version, 
    @FormParam( "type" ) String type, 
    @FormParam( "site_id" ) String site_id, 
    @HeaderParam(HttpHeaders.ACCEPT) String accept, @QueryParam( Constants.FORMAT_QUERY ) String format) throws IOException
    {
        if( id != null )
        {
            int nId = Integer.parseInt( KEY_ID );

            Dependency dependency = DependencyHome.findByPrimaryKey( nId );

            if ( dependency != null )
            {
                dependency.setGroupId( group_id );
                dependency.setArtifactId( artifact_id );
                dependency.setVersion( version );
                dependency.setType( type );
                dependency.setSiteId( Integer.parseInt( site_id ) );
                DependencyHome.update( dependency );
            }
        }
        else
        {
            Dependency dependency = new Dependency( );
            
            dependency.setGroupId( group_id );
            dependency.setArtifactId( artifact_id );
            dependency.setVersion( version );
            dependency.setType( type );
            dependency.setSiteId( Integer.parseInt( site_id ) );
            DependencyHome.create( dependency );
        }
        return getDependencys(accept, format);
    }
    
    /**
     * Write a dependency into a buffer
     * @param sbXML The buffer
     * @param dependency The dependency
     */
    private void addDependencyXml( StringBuffer sbXML, Dependency dependency )
    {
        XmlUtil.beginElement( sbXML, KEY_DEPENDENCY );
        XmlUtil.addElement( sbXML, KEY_ID , dependency.getId( ) );
        XmlUtil.addElement( sbXML, KEY_GROUP_ID , dependency.getGroupId( ) );
        XmlUtil.addElement( sbXML, KEY_ARTIFACT_ID , dependency.getArtifactId( ) );
        XmlUtil.addElement( sbXML, KEY_VERSION , dependency.getVersion( ) );
        XmlUtil.addElement( sbXML, KEY_TYPE , dependency.getType( ) );
        XmlUtil.addElement( sbXML, KEY_SITE_ID , dependency.getSiteId( ) );
        XmlUtil.endElement( sbXML, KEY_DEPENDENCY );
    }
    
    /**
     * Write a dependency into a JSON Object
     * @param json The JSON Object
     * @param dependency The dependency
     */
    private void addDependencyJson( JSONObject json, Dependency dependency )
    {
        JSONObject jsonDependency = new JSONObject(  );
        jsonDependency.accumulate( KEY_ID , dependency.getId( ) );
        jsonDependency.accumulate( KEY_GROUP_ID, dependency.getGroupId( ) );
        jsonDependency.accumulate( KEY_ARTIFACT_ID, dependency.getArtifactId( ) );
        jsonDependency.accumulate( KEY_VERSION, dependency.getVersion( ) );
        jsonDependency.accumulate( KEY_TYPE, dependency.getType( ) );
        jsonDependency.accumulate( KEY_SITE_ID, dependency.getSiteId( ) );
        json.accumulate( KEY_DEPENDENCY, jsonDependency );
    }
}