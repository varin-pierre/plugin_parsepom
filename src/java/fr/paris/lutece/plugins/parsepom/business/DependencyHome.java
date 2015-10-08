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
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * This class provides instances management methods (create, find, ...) for Dependency objects
 */

public final class DependencyHome
{
    // Static variable pointed at the DAO instance

    private static IDependencyDAO _dao = SpringContextService.getBean( "parsepom.dependencyDAO" );
    private static Plugin _plugin = PluginService.getPlugin( "parsepom" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private DependencyHome(  )
    {
    }

    /**
     * Create an instance of the dependency class
     * @param dependency The instance of the Dependency which contains the informations to store
     * @return The  instance of dependency which has been created with its primary key.
     */
    public static Dependency create( Dependency dependency )
    {
        _dao.insert( dependency, _plugin );

        return dependency;
    }

    /**
     * Update of the dependency which is specified in parameter
     * @param dependency The instance of the Dependency which contains the data to store
     * @return The instance of the  dependency which has been updated
     */
    public static Dependency update( Dependency dependency )
    {
        _dao.store( dependency, _plugin );

        return dependency;
    }

    /**
     * Remove the dependency whose identifier is specified in parameter
     * @param nKey The dependency Id
     */
    public static void remove( int nKey )
    {
        _dao.delete( nKey, _plugin );
    }

    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Returns an instance of a dependency whose identifier is specified in parameter
     * @param nKey The dependency primary key
     * @return an instance of Dependency
     */
    public static Dependency findByPrimaryKey( int nKey )
    {
        return _dao.load( nKey, _plugin);
    }

    /**
     * Load the data of all the dependency objects and returns them in form of a collection
     * @return the collection which contains the data of all the dependency objects
     */
    public static Collection<Dependency> getDependencysList( )
    {
        return _dao.selectDependencysList( _plugin );
    }
    
    /**
     * Load the id of all the dependency objects and returns them in form of a collection
     * @return the collection which contains the id of all the dependency objects
     */
    public static Collection<Integer> getIdDependencysList( )
    {
        return _dao.selectIdDependencysList( _plugin );
    }
    
    /**
     * Load the data of all the dependency objects by site and returns them in form of a collection
     * @param nSId The identifier of the site 
     * @return the collection which contains the data of all the dependency objects
     */
    public static Collection<Dependency> getDependencysListBySiteId( int nSId )
    {
        return _dao.selectDependencysListBySiteId( nSId, _plugin);
    }
    
    /**
     * Load the artifactId and the name of all the site objects by dependency and load the versions of this dependency by site and returns them as a map
     * @param strArtifactId The Artifact Id of the site
     * @param idSitesList The double list of the Dependency ids by site
     * @return The map which contains the artifactId and the name of all the site objects by dependency and the versions of this dependency by site
     */
    public static Map<String, ArrayList<String>> getSitesListByDependencyId( String strArtifactId, List<List<Integer>> idSitesList )
    {
    	return _dao.selectSitesListByDependencyId( strArtifactId, idSitesList, _plugin );
    }
    
    /**
     * Load the list of dependency with ArtifactId
     * @param strArtifactId The Artifact Id of the dependency
     * @return the collection which contains the data of all the dependencies matching with this Artifact Id
     */
    public static Collection<Dependency> getDependencysByArtifactId( String strArtifactId )
    {
    	return _dao.selectDependencyListByArtifactId(strArtifactId, _plugin);
    }
    
    /**
     * Load the list of dependency without duplicates Artifact Id
     * @return the collection which contains the data of all the dependencies without duplicates Artifact Id
     */
    public static Collection<Dependency> getDependencysListWithoutDuplicates( )
    {
    	return _dao.selectDependencysListWithoutDuplicates( _plugin );
    }
    
    public static int getMaxId( )
    {
    	return _dao.newPrimaryKey(  _plugin );
    }
}

