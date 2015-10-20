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

/**
 * This class provides Data Access methods for tools objects
 */

public final class ToolsDAO implements IToolsDAO
{
    // Constants
    private static final String SQL_QUERY_NEW_PK = "SELECT max( id_tools ) FROM parsepom_tools";
    private static final String SQL_QUERY_SELECT = "SELECT id_tools, artifact_id, last_release FROM parsepom_tools WHERE id_tools = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO parsepom_tools ( id_tools, artifact_id, last_release ) VALUES ( ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM parsepom_tools WHERE id_tools = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE parsepom_tools SET id_tools = ?, artifact_id = ?, last_release = ? WHERE id_tools = ?";
    private static final String SQL_QUERY_SELECTALL = "SELECT id_tools, artifact_id, last_release FROM parsepom_tools";
    private static final String SQL_QUERY_SELECTALL_ID = "SELECT id_tools FROM parsepom_tools";
    private static final String SQL_QUERY_SELECT_BY_ARTIFACTID = "SELECT id_tools, artifact_id, last_release FROM parsepom_tools WHERE artifact_id = ?";

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
    public void insert( Tools tools, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );

        tools.setId( newPrimaryKey( plugin ) );

        daoUtil.setInt( 1, tools.getId( ) );
        daoUtil.setString( 2, tools.getArtifactId( ) );
        daoUtil.setString( 3, tools.getLastRelease( ) );

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Tools load( int nKey, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.setInt( 1 , nKey );
        daoUtil.executeQuery( );

        Tools tools = null;

        if ( daoUtil.next( ) )
        {
            tools = new Tools( );
            tools.setId( daoUtil.getInt( 1 ) );
            tools.setArtifactId( daoUtil.getString( 2 ) );
            tools.setLastRelease( daoUtil.getString( 3 ) );
        }

        daoUtil.free( );
        return tools;
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
    public void store( Tools tools, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );
        
        daoUtil.setInt( 1, tools.getId( ) );
        daoUtil.setString( 2, tools.getArtifactId( ) );
        daoUtil.setString( 3, tools.getLastRelease( ) );
        daoUtil.setInt( 4, tools.getId( ) );

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Collection<Tools> selectToolsList( Plugin plugin )
    {
        Collection<Tools> toolsList = new ArrayList<Tools>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            Tools tools = new Tools(  );
            
            tools.setId( daoUtil.getInt( 1 ) );
            tools.setArtifactId( daoUtil.getString( 2 ) );
            tools.setLastRelease( daoUtil.getString( 3 ) );

            toolsList.add( tools );
        }

        daoUtil.free( );
        return toolsList;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public Collection<Integer> selectIdToolsList( Plugin plugin )
    {
            Collection<Integer> toolsList = new ArrayList<Integer>( );
            DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_ID, plugin );
            daoUtil.executeQuery(  );

            while ( daoUtil.next(  ) )
            {
                toolsList.add( daoUtil.getInt( 1 ) );
            }

            daoUtil.free( );
            return toolsList;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public Tools selectByArtifactId( String strArtifactId, Plugin plugin )
    {
    	DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ARTIFACTID, plugin );
        daoUtil.setString( 1, strArtifactId );
        daoUtil.executeQuery(  );
        
    	Tools tools = null;            
        
        if ( daoUtil.next( ) )
        {
            tools = new Tools( );
            
            tools.setId( daoUtil.getInt( 1 ) );
            tools.setArtifactId( daoUtil.getString( 2 ) );
            tools.setLastRelease( daoUtil.getString( 3 ) );
        }

        daoUtil.free( );
        return tools;
    }
      
}