/*
 * Copyright (c) 2002-2015, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *	 and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *	 and the following disclaimer in the documentation and/or other materials
 *	 provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *	 contributors may be used to endorse or promote products derived from
 *	 this software without specific prior written permission.
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


package fr.paris.lutece.plugins.parsepom.business;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This class provides Data Access methods for Site objects
 */

public final class SiteDAO implements ISiteDAO
{
    // Constants
    private static final String SQL_QUERY_NEW_PK = "SELECT max( id_site ) FROM parsepom_site";
    private static final String SQL_QUERY_SELECT = "SELECT id_site, artifact_id, name, version, id_plugins, last_update, path FROM parsepom_site WHERE id_site = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO parsepom_site ( id_site, artifact_id, name, version, id_plugins, last_update, path ) VALUES ( ?, ?, ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM parsepom_site WHERE id_site = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE parsepom_site SET id_site = ?, artifact_id = ?, name = ?, version = ?, id_plugins = ?, last_update = ?, path = ? WHERE id_site = ?";
    private static final String SQL_QUERY_SELECTALL = "SELECT id_site, artifact_id, name, version, id_plugins, last_update, path FROM parsepom_site";
    private static final String SQL_QUERY_SELECTALL_ID = "SELECT id_site FROM parsepom_site";
    private static final String SQL_QUERY_UPDATE_PLUGIN_FIELD = "UPDATE parsepom_site SET id_plugins = ? WHERE id_site = ?";
    private static final String SQL_QUERY_SELECT_BY_ARTIFACT_ID = "SELECT id_site, artifact_id, name, version, id_plugins, last_update, path FROM parsepom_site WHERE artifact_id = ?";
    private static final String SQL_QUERY_SELECT_BY_NAME = "SELECT id_site, artifact_id, name, version, id_plugins, last_update, path FROM parsepom_site WHERE name = ?";
    private static final String SQL_QUERY_SELECT_BY_VERSION = "SELECT id_site, artifact_id, name, version, id_plugins, last_update, path FROM parsepom_site WHERE version = ?";
    private static final String SQL_QUERY_SELECT_BY_LAST_UPDATE = "SELECT id_site, artifact_id, name, version, id_plugins, last_update, path FROM parsepom_site WHERE cast(last_update as date) = ?";
    private static final String SQL_QUERY_SELECT_BY_ONE = "SELECT id_site, artifact_id, name, version, id_plugins, last_update, path FROM parsepom_site WHERE artifact_id = ? AND last_update = ?";

    /**
     * Generates a new primary key
     * @param plugin The Plugin
     * @return The new primary key
     */
    public int newPrimaryKey( Plugin plugin)
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_PK , plugin  );
        daoUtil.executeQuery( );

        int nKey = 1;

        if( daoUtil.next( ) )
        {
                nKey = daoUtil.getInt( 1 ) + 1;
        }

        daoUtil.free();

        return nKey;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void insert( Site site, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );

        site.setId( newPrimaryKey( plugin ) );

        daoUtil.setInt( 1, site.getId( ) );
        daoUtil.setString( 2, site.getArtifactId( ) );
        daoUtil.setString( 3, site.getName( ) );
        daoUtil.setString( 4, site.getVersion( ) );
        daoUtil.setString( 5, site.getIdPlugins( ) );
        daoUtil.setString(6, site.getLastUpdate( ));
        daoUtil.setString(7, site.getPath( ));

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Site load( int nKey, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.setInt( 1 , nKey );
        daoUtil.executeQuery( );

        Site site = null;

        if ( daoUtil.next( ) )
        {
            site = new Site();
            site.setId( daoUtil.getInt( 1 ) );
            site.setArtifactId( daoUtil.getString( 2 ) );
            site.setName( daoUtil.getString( 3 ) );
            site.setVersion( daoUtil.getString( 4 ) );
            site.setIdPlugins( daoUtil.getString( 5 ) );
            site.setLastUpdate( daoUtil.getString( 6 ) );
            site.setPath( daoUtil.getString( 7 ) );
        }

        daoUtil.free( );
        return site;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void delete( int nKey, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1 , nKey );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void store( Site site, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );
        
        daoUtil.setInt( 1, site.getId( ) );
        daoUtil.setString( 2, site.getArtifactId( ) );
        daoUtil.setString( 3, site.getName( ) );
        daoUtil.setString( 4, site.getVersion( ) );
        daoUtil.setString( 5, site.getIdPlugins( ) );
        daoUtil.setString( 6, site.getLastUpdate( ) );
        daoUtil.setString( 7, site.getPath( ) );
        daoUtil.setInt( 8, site.getId( ) );

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Collection<Site> selectSitesList( Plugin plugin )
    {
        Collection<Site> siteList = new ArrayList<Site>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            Site site = new Site(  );
            
            site.setId( daoUtil.getInt( 1 ) );
            site.setArtifactId( daoUtil.getString( 2 ) );
            site.setName( daoUtil.getString( 3 ) );
            site.setVersion( daoUtil.getString( 4 ) );
            site.setIdPlugins( daoUtil.getString( 5 ) );
            site.setLastUpdate( daoUtil.getString( 6 ) );
            site.setPath( daoUtil.getString( 7 ) );

            siteList.add( site );
        }

        daoUtil.free( );
        return siteList;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public Collection<Integer> selectIdSitesList( Plugin plugin )
    {
            Collection<Integer> siteList = new ArrayList<Integer>( );
            DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_ID, plugin );
            daoUtil.executeQuery(  );

            while ( daoUtil.next(  ) )
            {
                siteList.add( daoUtil.getInt( 1 ) );
            }

            daoUtil.free( );
            return siteList;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public void addDependencyIdToSite( int nDId, int nDSiteId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.setInt( 1, nDSiteId );	
        daoUtil.executeQuery(  );
        
        String StringToModify = "";
        
        if ( daoUtil.next( ) )
        	StringToModify = daoUtil.getString( 5 );
        StringBuilder addDependencyId = new StringBuilder( StringToModify );
        addDependencyId.append( nDId );
        addDependencyId.append( ";" );
           
        DAOUtil daoUtil2 = new DAOUtil( SQL_QUERY_UPDATE_PLUGIN_FIELD, plugin );
        
        daoUtil2.setString( 1, addDependencyId.toString( ) );
        daoUtil2.setInt( 2, nDSiteId );
        daoUtil2.executeUpdate(  );

        daoUtil.free( );
        daoUtil2.free( );
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public void deleteDependencyIdFromSite( int nDId, int nDSiteId, Plugin plugin )
    {
    	DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.setInt( 1, nDSiteId );	
        daoUtil.executeQuery(  );
        
        String stringToModify = "";
        String value = String.valueOf(nDId) + ";";
        int len = value.length( );
        
        if ( daoUtil.next( ) )
        {
        	stringToModify = daoUtil.getString( 5 );
        }
        StringBuilder deleteDependencyId = new StringBuilder( stringToModify );
        int start = deleteDependencyId.indexOf( value );
        if ( start != -1 )
        {
        	deleteDependencyId.delete( start, start + len );
        
        	DAOUtil daoUtil2 = new DAOUtil( SQL_QUERY_UPDATE_PLUGIN_FIELD, plugin );
        
        	daoUtil2.setString( 1, deleteDependencyId.toString( ) );
        	daoUtil2.setInt( 2, nDSiteId );
        	daoUtil2.executeUpdate(  );
        	
        	daoUtil2.free( );
        }
        daoUtil.free( );
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public List<List<Integer>> selectIdSitesListByDependency( Plugin plugin )
    {
    	List<List<Integer>> siteIdList = new ArrayList<List<Integer>>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            String strIdPlugins = daoUtil.getString( 5 );
            List<Integer> pluginsIdList = new ArrayList<Integer>( );
            
            for ( String id : strIdPlugins.split( ";" ) )
            {
            	pluginsIdList.add( Integer.valueOf( id ) );
            }
            siteIdList.add( pluginsIdList );
        }

        daoUtil.free( );
        return siteIdList;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public Collection<Site> selectSitesListByArtifactId( String strArtifactId, Plugin plugin )
    {
    	Collection<Site> siteList = new ArrayList<Site>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ARTIFACT_ID, plugin );
        daoUtil.setString( 1 , strArtifactId );
        daoUtil.executeQuery(  );

        while ( daoUtil.next( ) )
        {
            Site site = new Site(  );
            
            site.setId( daoUtil.getInt( 1 ) );
            site.setArtifactId( daoUtil.getString( 2 ) );
            site.setName( daoUtil.getString( 3 ) );
            site.setVersion( daoUtil.getString( 4 ) );
            site.setIdPlugins( daoUtil.getString( 5 ) );
            site.setLastUpdate( daoUtil.getString( 6 ) );
            site.setPath( daoUtil.getString( 7 ) );

            siteList.add(site);
        }

        daoUtil.free( );
        return siteList;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public Collection<Site> selectSitesListByName( String strName, Plugin plugin )
    {
    	Collection<Site> siteList = new ArrayList<Site>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_NAME, plugin );
        daoUtil.setString( 1 , strName );
        daoUtil.executeQuery(  );

        while ( daoUtil.next( ) )
        {
            Site site = new Site(  );
            
            site.setId( daoUtil.getInt( 1 ) );
            site.setArtifactId( daoUtil.getString( 2 ) );
            site.setName( daoUtil.getString( 3 ) );
            site.setVersion( daoUtil.getString( 4 ) );
            site.setIdPlugins( daoUtil.getString( 5 ) );
            site.setLastUpdate( daoUtil.getString( 6 ) );
            site.setPath( daoUtil.getString( 7 ) );

            siteList.add(site);
        }

        daoUtil.free( );
        return siteList;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public Collection<Site> selectSitesListByVersion( String strVersion, Plugin plugin )
    {
    	Collection<Site> siteList = new ArrayList<Site>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_VERSION, plugin );
        daoUtil.setString( 1 , strVersion );
        daoUtil.executeQuery(  );

        while ( daoUtil.next( ) )
        {
            Site site = new Site(  );
            
            site.setId( daoUtil.getInt( 1 ) );
            site.setArtifactId( daoUtil.getString( 2 ) );
            site.setName( daoUtil.getString( 3 ) );
            site.setVersion( daoUtil.getString( 4 ) );
            site.setIdPlugins( daoUtil.getString( 5 ) );
            site.setLastUpdate( daoUtil.getString( 6 ) );
            site.setPath( daoUtil.getString( 7 ) );

            siteList.add(site);
        }

        daoUtil.free( );
        return siteList;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public Collection<Site> selectSitesListByLastUpdate( String strLastUpdate, Plugin plugin)
    {
    	Collection<Site> siteList = new ArrayList<Site>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_LAST_UPDATE, plugin );
        daoUtil.setString( 1 , strLastUpdate );
        daoUtil.executeQuery(  );

        while ( daoUtil.next( ) )
        {
        	Site site = new Site(  );
        	
            site.setId( daoUtil.getInt( 1 ) );
            site.setArtifactId( daoUtil.getString( 2 ) );
            site.setName( daoUtil.getString( 3 ) );
            site.setVersion( daoUtil.getString( 4 ) );
            site.setIdPlugins( daoUtil.getString( 5 ) );
            site.setLastUpdate( daoUtil.getString( 6 ) );
            site.setPath( daoUtil.getString( 7 ) );
            
            siteList.add(site);
        }

        daoUtil.free( );
        return siteList;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public Site selectSitesByFilter( String strArtifactId, String strLastUpDate, Plugin plugin )
    {
    	Site site = null;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ONE, plugin );
        daoUtil.setString( 1 , strArtifactId );
        daoUtil.setString( 2 , strLastUpDate );
        daoUtil.executeQuery(  );

        while ( daoUtil.next( ) )
        {
        	site = new Site(  );
        	
            site.setId( daoUtil.getInt( 1 ) );
            site.setArtifactId( daoUtil.getString( 2 ) );
            site.setName( daoUtil.getString( 3 ) );
            site.setVersion( daoUtil.getString( 4 ) );
            site.setIdPlugins( daoUtil.getString( 5 ) );
            site.setLastUpdate( daoUtil.getString( 6 ) );
            site.setPath( daoUtil.getString( 7 ) );
        }

        daoUtil.free( );
        return site;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public Site selectSitesByArtifactId( String strArtifactId, Plugin plugin )
    {
    	Site site = null;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ONE, plugin );
        daoUtil.setString( 1 , strArtifactId );
        daoUtil.executeQuery(  );

        while ( daoUtil.next( ) )
        {
        	site = new Site(  );
        	
            site.setId( daoUtil.getInt( 1 ) );
            site.setArtifactId( daoUtil.getString( 2 ) );
            site.setName( daoUtil.getString( 3 ) );
            site.setVersion( daoUtil.getString( 4 ) );
            site.setIdPlugins( daoUtil.getString( 5 ) );
            site.setLastUpdate( daoUtil.getString( 6 ) );
            site.setPath( daoUtil.getString( 7 ) );
        }

        daoUtil.free( );
        return site;
    }
}