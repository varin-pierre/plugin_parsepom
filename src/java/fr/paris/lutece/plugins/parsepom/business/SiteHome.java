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
import java.util.Collection;
import java.util.List;

/**
 * This class provides instances management methods (create, find, ...) for Site objects
 */

public final class SiteHome
{
    // Static variable pointed at the DAO instance

    private static ISiteDAO _dao = SpringContextService.getBean( "parsepom.siteDAO" );
    private static Plugin _plugin = PluginService.getPlugin( "parsepom" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private SiteHome(  )
    {
    }

    /**
     * Create an instance of the site class
     * @param site The instance of the Site which contains the informations to store
     * @return The  instance of site which has been created with its primary key.
     */
    public static Site create( Site site )
    {
        _dao.insert( site, _plugin );

        return site;
    }

    /**
     * Update of the site which is specified in parameter
     * @param site The instance of the Site which contains the data to store
     * @return The instance of the  site which has been updated
     */
    public static Site update( Site site )
    {
        _dao.store( site, _plugin );

        return site;
    }

    /**
     * Remove the site whose identifier is specified in parameter
     * @param nKey The site Id
     */
    public static void remove( int nKey )
    {
        _dao.delete( nKey, _plugin );
    }

    /**
     * Update of the site which the Id is specified in second parameter
     * @param nDId The id of the dependency to add
     * @param nDSiteId The id of the Site to update, registered in the dependency
     */
    public static void updateDependencyInSite( int nDId, int nDSiteId )
    {
    	_dao.addDependencyIdToSite( nDId, nDSiteId, _plugin );
    }
    
    /**
     * Update of the site which the Id is specified in second parameter
     * @param nDId The id of the dependency to delete
     * @param nDSiteId The id of the Site to update, registered in the dependency
     */
    public static void removeDependencyFromSite( int nDId, int nDSiteId )
    {
    	_dao.deleteDependencyIdFromSite( nDId, nDSiteId, _plugin );
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Returns an instance of a site whose identifier is specified in parameter
     * @param nKey The site primary key
     * @return an instance of Site
     */
    public static Site findByPrimaryKey( int nKey )
    {
        return _dao.load( nKey, _plugin);
    }

    /**
     * Load the data of all the site objects and returns them in form of a collection
     * @return the collection which contains the data of all the site objects
     */
    public static Collection<Site> getSitesList( )
    {
        return _dao.selectSitesList( _plugin );
    }
    
    /**
     * Load the id of all the site objects and returns them in form of a collection
     * @return the collection which contains the id of all the site objects
     */
    public static Collection<Integer> getIdSitesList( )
    {
        return _dao.selectIdSitesList( _plugin );
    }
    
    /**
     * Load the id of all the dependency objects and returns them in form of a double list of integers
     * @return the bidimensional list which contains the id of all the dependency objects by site
     */
   public static List<List<Integer>> getIdSitesListByDependency(  )
   {
	   return _dao.selectIdSitesListByDependency( _plugin );
   }
   
   /**
    * Load sites matching strArtifactId and return site
    * @return collection site
    */
   public static Collection<Site> getSitesListByArtifactId( String strArtifactId )
   {
	   return _dao.selectSitesListByArtifactId( strArtifactId, _plugin );
   }
   
   /**
    * Load sites matching strName and return a sites list
    * @return collection site
    */
   public static Collection<Site> getSitesListByName( String strName )
   {
	   return _dao.selectSitesListByName( strName, _plugin );
   }
   
   /**
    * Load sites matching strVersion and return a sites list
    * @return collection site
    */
   public static Collection<Site> getSitesListByVersion( String strVersion )
   {
	   return _dao.selectSitesListByVersion( strVersion, _plugin );
   }
   
   /**
    * Load sites matching strLastUpdate and return a sites list
    * @return collection site
    */
   public static Collection<Site> getSitesListByLastUpdate( String strLastUpdate )
   {
	   return _dao.selectSitesListByLastUpdate( strLastUpdate, _plugin );
   }
   
   /**
    * Load the max id_site in table
    * @return max index + 1  
    */
   public static int getMaxId( )
   {
	   return _dao.newPrimaryKey( _plugin );
   }

   /**
    * Load sites who match with strArtifactId and return site
    * @return site
    */
   public static Site getOneSite( String strArtifactId, String strLastUpDate)
   {
	   return _dao.selectSitesByFilter( strArtifactId, strLastUpDate, _plugin );
   }
   
   public static Site   getSiteByArtifactId( String strArtifactId )
   {
	   return _dao.selectSitesByArtifactId( strArtifactId, _plugin );
   }
   
}

