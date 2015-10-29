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
package fr.paris.lutece.plugins.parsepom.business;

import javax.validation.constraints.*;
import org.hibernate.validator.constraints.*;


/**
 * This is the business class for the object Site
 */ 
public class Site
{
    // Variables declarations 
    private int _nId;
    
    @NotEmpty( message = "#i18n{parsepom.validation.site.ArtifactId.notEmpty}" )
    @Size( max = 255 , message = "#i18n{parsepom.validation.site.ArtifactId.size}" ) 
    private String _strArtifactId;
    
    @NotEmpty( message = "#i18n{parsepom.validation.site.Name.notEmpty}" )
    @Size( max = 255 , message = "#i18n{parsepom.validation.site.Name.size}" ) 
    private String _strName;
    
    @NotEmpty( message = "#i18n{parsepom.validation.site.Version.notEmpty}" )
    @Size( max = 50 , message = "#i18n{parsepom.validation.site.Version.size}" )
    private String _strVersion;
 
    @NotEmpty( message = "#i18n{parsepom.validation.site.IdPlugins.notEmpty}" )
    @Size( max = 60000 , message = "#i18n{parsepom.validation.site.IdPlugins.size}" )
    private String _strIdPlugins;
    
    @NotEmpty( message = "#i18n{parsepom.validation.site.LastUpdate.notEmpty}" )
    @Size( max = 50 , message = "#i18n{parsepom.validation.site.LastUpdate.size}" )
    private String _strLastUpdate;
    
    @NotEmpty( message = "#i18n{parsepom.validation.site.Path.notEmpty}" )
    @Size( max = 255 , message = "#i18n{parsepom.validation.site.Path.size}" )
    private String _strPath;

    /**
     * Returns the Id
     * @return The Id
     */
    public int getId( )
    {
        return _nId;
    }

    /**
     * Sets the Id
     * @param nId The Id
     */ 
    public void setId( int nId )
    {
        _nId = nId;
    }

    /**
     * Returns the ArtifactId
     * @return The ArtifactId
     */
    public String getArtifactId( )
    {
        return _strArtifactId;
    }

    /**
     * Sets the ArtifactId
     * @param strArtifactId The ArtifactId
     */ 
    public void setArtifactId( String strArtifactId )
    {
        _strArtifactId = strArtifactId;
    }
    
    /**
     * Returns the Name
     * @return The Name
     */
    public String getName( )
    {
        return _strName;
    }

    /**
     * Sets the Name
     * @param strName The Name
     */ 
    public void setName( String strName )
    {
        _strName = strName;
    }
    
    /**
     * Returns the Version
     * @return The Version
     */
    public String getVersion( )
    {
        return _strVersion;
    }

    /**
     * Sets the Version
     * @param strVersion The Version
     */ 
    public void setVersion( String strVersion )
    {
        _strVersion = strVersion;
    }
    
    /**
     * Returns the IdPLugins
     * @return The IdPlugins
     */
    public String getIdPlugins( )
    {
        return _strIdPlugins;
    }

    /**
     * Sets the IdPlugins
     * @param strName The IdPlugins
     */ 
    public void setIdPlugins( String strIdPlugins )
    {
        _strIdPlugins = strIdPlugins;
    }
    
    /**
     * Returns the LastUpdate
     * @return The LastUpdate
     */
    public String getLastUpdate( )
    {
        return _strLastUpdate;
    }

    /**
     * Sets the LastUpdate
     * @param strLastUpdate The LastUpdate
     */ 
    public void setLastUpdate( String strLastUpdate )
    {
        _strLastUpdate = strLastUpdate;
    }
    
    /**
     * Returns the Path
     * @return The Path
     */
    public String getPath( )
    {
        return _strPath;
    }

    /**
     * Sets the Path
     * @param strPath The Path
     */ 
    public void setPath( String strPath )
    {
        _strPath = strPath;
    }
}
