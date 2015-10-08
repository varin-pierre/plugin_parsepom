
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;



/**
 * IDependencyDAO Interface
 */
public interface IDependencyDAO
{
    /**
     * Insert a new record in the table.
     * @param dependency instance of the Dependency object to insert
     * @param plugin the Plugin
     */
    void insert( Dependency dependency, Plugin plugin );

    /**
     * Update the record in the table
     * @param dependency the reference of the Dependency
     * @param plugin the Plugin
     */
    void store( Dependency dependency, Plugin plugin );

    /**
     * Delete a record from the table
     * @param nKey The identifier of the Dependency to delete
     * @param plugin the Plugin
     */
    void delete( int nKey, Plugin plugin );

    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Load the data from the table
     * @param nKey The identifier of the dependency
     * @param plugin the Plugin
     * @return The instance of the dependency
     */
    Dependency load( int nKey, Plugin plugin );

    /**
     * Load the data of all the dependency objects and returns them as a collection
     * @param plugin the Plugin
     * @return The collection which contains the data of all the dependency objects
     */
    Collection<Dependency> selectDependencysList( Plugin plugin );
    
    /**
     * Load the id of all the dependency objects and returns them as a collection
     * @param plugin the Plugin
     * @return The collection which contains the id of all the dependency objects
     */
    Collection<Integer> selectIdDependencysList( Plugin plugin );
    
    /**
     * Load the data of all the dependency objects by site and returns them as a collection
     * @param nSId The identifier of the site
     * @param plugin the Plugin
     * @return The collection which contains the data of all the dependency objects by site
     */
    Collection<Dependency> selectDependencysListBySiteId( int nSId, Plugin plugin );
    
    /**
     * Load the artifact Id and the name of all the site objects by dependency and load the versions of this dependency by site and returns them as a map
     * @param strArtifactId The Artifact Id of the site
     * @param idSitesList The double list of the Dependency ids by site
     * @param plugin the Plugin
     * @return The map which contains the artifactId and the name of all the site objects by dependency and the versions of this dependency by site
     */
    Map<String, ArrayList<String>> selectSitesListByDependencyId( String strArtifactId, List<List<Integer>> idSitesList, Plugin plugin );

    /**
     * Load the data of all the dependency objects by artifactId and returns them as a collection
     * @param strArtifactId The identifier of the dependency
     * @param plugin the Plugin
     * @return The collection which contains the data of all the dependency objects by site
     */
	Collection<Dependency> selectDependencyListByArtifactId(String strArtifactId, Plugin plugin);
	
	/**
     * Load the data of all all the dependency objects without duplicates Artifact Id and returns them as a collection 
     * @param plugin the Plugin
     * @return The collection which contains the data of all the dependency object without duplicates Artifact Id
     */
	Collection<Dependency> selectDependencysListWithoutDuplicates( Plugin plugin );
	
	int newPrimaryKey( Plugin plugin );
}

