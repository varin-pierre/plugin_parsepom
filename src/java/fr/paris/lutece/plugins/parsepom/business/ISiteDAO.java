
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
import java.util.Collection;
import java.util.List;



/**
 * ISiteDAO Interface
 */
public interface ISiteDAO
{
    /**
     * Insert a new record in the table.
     * @param site instance of the Site object to insert
     * @param plugin the Plugin
     */
    void insert( Site site, Plugin plugin );

    /**
     * Update the record in the table
     * @param site the reference of the Site
     * @param plugin the Plugin
     */
    void store( Site site, Plugin plugin );

    /**
     * Delete a record from the table
     * @param nKey The identifier of the Site to delete
     * @param plugin the Plugin
     */
    void delete( int nKey, Plugin plugin );
    
    /**
     * Update the id_plugin field in the table Site
     * @param nDId The identifier of the Dependency to add
     * @param nDSiteId The identifier of the site registered in the dependency
     * @param plugin the Plugin
     */
    void addDependencyIdToSite( int nDId, int nDSiteId, Plugin plugin );

    /**
     * Update the id_plugin field in the table Site
     * @param nDId The identifier of the Dependency to delete
     * @param nDSiteId The identifier of the site registered in the dependency
     * @param plugin the Plugin
     */
    void deleteDependencyIdFromSite( int nDId, int nDSiteId, Plugin plugin );
    
    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Load the data from the table
     * @param nKey The identifier of the site
     * @param plugin the Plugin
     * @return The instance of the site
     */
    Site load( int nKey, Plugin plugin );

    /**
     * Load the data of all the site objects and returns them as a collection
     * @param plugin the Plugin
     * @return The collection which contains the data of all the site objects
     */
    Collection<Site> selectSitesList( Plugin plugin );
    
    /**
     * Load the id of all the site objects and returns them as a collection
     * @param plugin the Plugin
     * @return The collection which contains the id of all the site objects
     */
    Collection<Integer> selectIdSitesList( Plugin plugin );
    
    /**
     * Load the id of all the dependency objects and returns them as a double list of integers
     * @param plugin the Plugin
     * @return The bidimensional List of integers which contains the ids of all the dependency objects by site
     */
    public List<List<Integer>> selectIdSitesListByDependency( Plugin plugin );

    /**
     * Load the data of all the sites object matching the strArtifactId and return them as an collection
     * @param plugin the Plugin
     * @return The site which contains the data of the site matching the strArtifactId 
     */
    Collection<Site> selectSitesListByArtifactId( String strArtifactId, Plugin plugin );
	
    /**
     * Load the data of all the sites object matching the strName and return them as an collection
     * @param plugin the Plugin
     * @return The site which contains the data of the site matching the strName 
     */
	Collection<Site> selectSitesListByName( String strName, Plugin plugin );
    
	/**
     * Load the data of all the sites object matching the strVersion and return them as an collection
     * @param plugin the Plugin
     * @return The site which contains the data of the site matching the strVersion 
     */
	Collection<Site> selectSitesListByVersion( String strVersion, Plugin plugin );
	
	/**
     * Load the data of all the sites object matching the strLastUpdate and return them as an collection
     * @param plugin the Plugin
     * @return The site which contains the data of the site matching the strLastUpdate 
     */
	Collection<Site> selectSitesListByLastUpdate( String strLastUpdate, Plugin plugin );

	int newPrimaryKey(Plugin plugin);
	
	Site selectSitesByFilter( String strArtifactId, String strLastUpDate, Plugin plugin);
	
	Site selectSitesByArtifactId( String strArtifactId, Plugin plugin);
	
}

