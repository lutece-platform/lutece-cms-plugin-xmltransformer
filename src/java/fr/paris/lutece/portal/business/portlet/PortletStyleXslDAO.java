/*
 * Copyright (c) 2002-2025, City of Paris
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
package fr.paris.lutece.portal.business.portlet;

import fr.paris.lutece.portal.business.stylesheet.StyleSheet;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.sql.DAOUtil;

public class PortletStyleXslDAO implements IPortletStyleDAO
{

    private static final String SQL_QUERY_SELECT_XSL_FILE = " SELECT a.id_stylesheet , a.description , a.file_name, a.source "
            + " FROM core_stylesheet a, core_portlet b, core_style_mode_stylesheet c " + " WHERE a.id_stylesheet = c.id_stylesheet "
            + " AND b.id_style = c.id_style AND b.id_portlet = ? AND c.id_mode = ? ";
    private static final String SQL_QUERY_SELECT_STYLE_LIST = " SELECT distinct a.id_style , a.description_style "
            + " FROM core_style a , core_style_mode_stylesheet b " + " WHERE  a.id_style = b.id_style "
            + " AND a.id_portlet_type = ? ORDER BY a.description_style";

    @Override
    public String selectXslFile( int nIdPortlet, int nIdMode )
    {
        return selectXslStyleSheet( nIdPortlet, nIdMode ).getFile( );
    }

    @Override
    public byte [ ] selectXslSource( int nIdPortlet, int nIdMode )
    {
        return selectXslStyleSheet( nIdPortlet, nIdMode ).getSource( );
    }

    /**
     * {@inheritDoc}
     */
    public ReferenceList selectStylesList( String strPortletTypeId )
    {
        ReferenceList list = new ReferenceList( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_STYLE_LIST ) )
        {
            daoUtil.setString( 1, strPortletTypeId );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                list.addItem( daoUtil.getInt( 1 ), daoUtil.getString( 2 ) );
            }

        }

        return list;
    }

    /**
     * Returns the stylesheet of the portlet according to the mode
     *
     * @param nPortletId
     *            the identifier of the portlet
     * @param nIdMode
     *            the selected mode
     * @return the stylesheet
     */
    private StyleSheet selectXslStyleSheet( int nPortletId, int nIdMode )
    {
        StyleSheet stylesheet = new StyleSheet( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_XSL_FILE ) )
        {
            daoUtil.setInt( 1, nPortletId );
            daoUtil.setInt( 2, nIdMode );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                stylesheet.setId( daoUtil.getInt( 1 ) );
                stylesheet.setDescription( daoUtil.getString( 2 ) );
                stylesheet.setFile( daoUtil.getString( 3 ) );
                stylesheet.setSource( daoUtil.getBytes( 4 ) );
            }

        }

        return stylesheet;
    }
}
