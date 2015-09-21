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
package fr.paris.lutece.plugins.parsepom.business.portlet;

import fr.paris.lutece.portal.business.portlet.Portlet;
import fr.paris.lutece.util.sql.DAOUtil;


/**
 * this class provides Data Access methods for ParsepomPortlet objects
 */
public final class ParsepomPortletDAO implements IParsepomPortletDAO
{
    ////////////////////////////////////////////////////////////////////////////
    // Constants
    private static final String SQL_QUERY_SELECT = "SELECT id_portlet FROM parsepom_portlet WHERE id_portlet = ? ";
    private static final String SQL_QUERY_INSERT = "INSERT INTO parsepom_portlet ( id_portlet ) VALUES ( ? )";
    private static final String SQL_QUERY_DELETE = "DELETE FROM parsepom_portlet WHERE id_portlet = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE parsepom_portlet SET id_portlet = ? WHERE id_portlet = ? ";

    ///////////////////////////////////////////////////////////////////////////////////////
    // Access methods to data

    /**
     * Insert a new record in the table.
     *
     * @param portlet The Instance of the Portlet
     */
    @Override
    public void insert( Portlet portlet )
    {
        ParsepomPortlet p = (ParsepomPortlet) portlet;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT );
        daoUtil.setInt( 1, p.getId(  ) );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Delete record from table
     *
     * @param nPortletId The indentifier of the Portlet
     */
    @Override
    public void delete( int nPortletId )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE );
        daoUtil.setInt( 1, nPortletId );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Update the record in the table
     *
     * @param portlet The reference of the portlet
     */
    @Override
    public void store( Portlet portlet )
    {
        ParsepomPortlet p = (ParsepomPortlet) portlet;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE );
        daoUtil.setInt( 1, p.getId(  ) );
        daoUtil.setInt( 2, p.getId(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * load the data of dbpagePortlet from the table
     * @return portlet The instance of the object portlet
     * @param nIdPortlet The identifier of the portlet
     */
    @Override
    public Portlet load( int nIdPortlet )
    {
        ParsepomPortlet portlet = new ParsepomPortlet(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT );
        daoUtil.setInt( 1, nIdPortlet );
        daoUtil.executeQuery(  );

        if ( daoUtil.next(  ) )
        {
            portlet.setId( daoUtil.getInt( 1 ) );
        }

        daoUtil.free(  );

        return portlet;
    }

}
