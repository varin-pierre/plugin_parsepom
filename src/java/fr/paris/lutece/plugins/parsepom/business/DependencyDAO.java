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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This class provides Data Access methods for Dependency objects
 */

public final class DependencyDAO implements IDependencyDAO
{
    // Constants
    private static final String SQL_QUERY_NEW_PK = "SELECT max( id_dependency ) FROM parsepom_dependency";
    private static final String SQL_QUERY_SELECT = "SELECT id_dependency, group_id, artifact_id, version, type, site_id FROM parsepom_dependency WHERE id_dependency = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO parsepom_dependency ( id_dependency, group_id, artifact_id, version, type, site_id ) VALUES ( ?, ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM parsepom_dependency WHERE id_dependency = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE parsepom_dependency SET id_dependency = ?, group_id = ?, artifact_id = ?, version = ?, type = ?, site_id = ? WHERE id_dependency = ?";
    private static final String SQL_QUERY_SELECTALL = "SELECT id_dependency, group_id, artifact_id, version, type, site_id FROM parsepom_dependency";
    private static final String SQL_QUERY_SELECTALL_ID = "SELECT id_dependency FROM parsepom_dependency";
    private static final String SQL_QUERY_SELECTALL_BY_SITE_ID = "SELECT id_dependency, group_id, artifact_id, version, type, site_id FROM parsepom_dependency WHERE site_id = ?";
    private static final String SQL_QUERY_SELECT_BY_DEPENDENCY_ID = "SELECT pd.artifact_id, pd.version, ps.id_site, ps.artifact_id, ps.name FROM parsepom_dependency as pd JOIN parsepom_site as ps ON pd.site_id = ps.id_site WHERE id_dependency = ?";
    private static final String SQL_QUERY_SELECTALL_BY_ARTIFACT_ID = "SELECT id_dependency, group_id, artifact_id, version, type, site_id FROM parsepom_dependency WHERE artifact_id = ?";
    private static final String SQL_QUERY_SELECTALL_BY_UNIQUE_ARTIFACT_ID = "SELECT id_dependency, group_id, artifact_id, version, type, site_id FROM parsepom_dependency GROUP BY artifact_id";

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
    public void insert( Dependency dependency, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );

        dependency.setId( newPrimaryKey( plugin ) );

        daoUtil.setInt( 1, dependency.getId( ) );
        daoUtil.setString( 2, dependency.getGroupId( ) );
        daoUtil.setString( 3, dependency.getArtifactId( ) );
        daoUtil.setString( 4, dependency.getVersion( ) );
        daoUtil.setString( 5, dependency.getType( ) );
        daoUtil.setInt( 6, dependency.getSiteId( ) );

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Dependency load( int nKey, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.setInt( 1 , nKey );
        daoUtil.executeQuery( );

        Dependency dependency = null;

        if ( daoUtil.next( ) )
        {
            dependency = new Dependency();
            dependency.setId( daoUtil.getInt( 1 ) );
            dependency.setGroupId( daoUtil.getString( 2 ) );
            dependency.setArtifactId( daoUtil.getString( 3 ) );
            dependency.setVersion( daoUtil.getString( 4 ) );
            dependency.setType( daoUtil.getString( 5 ) );
            dependency.setSiteId( daoUtil.getInt( 6 ) );
        }

        daoUtil.free( );
        return dependency;
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
    public void store( Dependency dependency, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );
        
        daoUtil.setInt( 1, dependency.getId( ) );
        daoUtil.setString( 2, dependency.getGroupId( ) );
        daoUtil.setString( 3, dependency.getArtifactId( ) );
        daoUtil.setString( 4, dependency.getVersion( ) );
        daoUtil.setString( 5, dependency.getType( ) );
        daoUtil.setInt( 6, dependency.getSiteId( ) );
        daoUtil.setInt( 7, dependency.getId( ) );

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Collection<Dependency> selectDependencysList( Plugin plugin )
    {
        Collection<Dependency> dependencyList = new ArrayList<Dependency>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            Dependency dependency = new Dependency(  );
            
            dependency.setId( daoUtil.getInt( 1 ) );
            dependency.setGroupId( daoUtil.getString( 2 ) );
            dependency.setArtifactId( daoUtil.getString( 3 ) );
            dependency.setVersion( daoUtil.getString( 4 ) );
            dependency.setType( daoUtil.getString( 5 ) );
            dependency.setSiteId( daoUtil.getInt( 6 ) );

            dependencyList.add( dependency );
        }

        daoUtil.free( );
        return dependencyList;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public Collection<Integer> selectIdDependencysList( Plugin plugin )
    {
            Collection<Integer> dependencyList = new ArrayList<Integer>( );
            DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_ID, plugin );
            daoUtil.executeQuery(  );

            while ( daoUtil.next(  ) )
            {
                dependencyList.add( daoUtil.getInt( 1 ) );
            }

            daoUtil.free( );
            return dependencyList;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public Collection<Dependency> selectDependencysListBySiteId( int nSId, Plugin plugin )
    {
        Collection<Dependency> dependencyList = new ArrayList<Dependency>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_BY_SITE_ID, plugin );
        daoUtil.setInt( 1, nSId );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            Dependency dependency = new Dependency(  );
            
            dependency.setId( daoUtil.getInt( 1 ) );
            dependency.setGroupId( daoUtil.getString( 2 ) );
            dependency.setArtifactId( daoUtil.getString( 3 ) );
            dependency.setVersion( daoUtil.getString( 4 ) );
            dependency.setType( daoUtil.getString( 5 ) );
            dependency.setSiteId( daoUtil.getInt( 6 ) );

            dependencyList.add( dependency );
        }

        daoUtil.free( );
        return dependencyList;
    }
    
    public Map<String, ArrayList<String>> selectSitesListByDependencyId( String strArtifactId, List<List<Integer>> idSitesList, Plugin plugin )
    {  	
    	Iterator<List<Integer>> itr = idSitesList.iterator( );	
    	Map<String, ArrayList<String>> listSite = new HashMap<>( );
    	DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_DEPENDENCY_ID, plugin );
    	while ( itr.hasNext( ) )
    	{
    		
    		List<Integer> id = itr.next( );
    		Iterator<Integer> itr2 = id.iterator( );
   	     	
    		while ( itr2.hasNext( ) )
    		{
    			daoUtil.setInt( 1, itr2.next( ) );
    	        daoUtil.executeQuery(  );  	        
    	        while ( daoUtil.next( ) )
    	        {
    	        	if (daoUtil.getString( 1 ).equals( strArtifactId ) )
    	        	{
    	        		String strDependencyVersion = daoUtil.getString( 2 );
    	        		String strSiteId = Integer.toString( daoUtil.getInt( 3 ) );
    	        		String strArtifactIdSite = daoUtil.getString( 4 );
    	        		String strNameSite = daoUtil.getString( 5 );
    	        		ArrayList<String> site = new ArrayList<String>( );
    	        		
    	        		site.add( strSiteId );
    	        		site.add( strArtifactIdSite );
    	        		site.add( strNameSite );
    	        		listSite.put( strDependencyVersion, site );
    	        	}
    	        }
    		}
    		
    	}
    	daoUtil.free( );
    	
    	return listSite;
    }
    
    public Collection<Dependency> selectDependencyListByArtifactId( String strArtifactId, Plugin plugin )
    {
    	Collection<Dependency> dependencyList = new ArrayList<Dependency>(  );
    	DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_BY_ARTIFACT_ID, plugin );
        daoUtil.setString( 1, strArtifactId );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            Dependency dependency = new Dependency(  );
             
            dependency.setId( daoUtil.getInt( 1 ) );
            dependency.setGroupId( daoUtil.getString( 2 ) );
            dependency.setArtifactId( daoUtil.getString( 3 ) );
            dependency.setVersion( daoUtil.getString( 4 ) );
            dependency.setType( daoUtil.getString( 5 ) );
            dependency.setSiteId( daoUtil.getInt( 6 ) );

            dependencyList.add( dependency );
        }

         daoUtil.free( );
         return dependencyList;
    }
    
    public Collection<Dependency> selectDependencysListWithoutDuplicates( Plugin plugin )
    {
    	Collection<Dependency> dependencyList = new ArrayList<Dependency>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_BY_UNIQUE_ARTIFACT_ID, plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            Dependency dependency = new Dependency(  );
            
            dependency.setId( daoUtil.getInt( 1 ) );
            dependency.setGroupId( daoUtil.getString( 2 ) );
            dependency.setArtifactId( daoUtil.getString( 3 ) );
            dependency.setVersion( daoUtil.getString( 4 ) );
            dependency.setType( daoUtil.getString( 5 ) );
            dependency.setSiteId( daoUtil.getInt( 6 ) );

            dependencyList.add( dependency );
        }

        daoUtil.free( );
        return dependencyList;
    }
}