/*
 * Copyright (c) 2002-2022, City of Paris
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
package fr.paris.lutece.portal.web.stylesheet;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.web.constants.Parameters;
import fr.paris.lutece.test.AdminUserUtils;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;
import fr.paris.lutece.test.mocks.MockHttpServletResponse;
import jakarta.servlet.http.HttpServletResponse;

/**
 * The stylesheet download servlet answers « not found » to a malformed or unknown id
 */
public class StyleSheetFileServletTest extends LuteceTestCase
{
    /**
     * Records the status of an error answer.
     */
    private static final class ErrorResponse extends MockHttpServletResponse
    {
        private int _nError;

        /**
         * {@inheritDoc}
         */
        @Override
        public void sendError( int nStatus ) throws IOException
        {
            _nError = nStatus;
        }
    }

    @Test
    public void testBadIdAnswersNotFound( ) throws Exception
    {
        for ( String strId : new String [ ] { "abc", "999999", "", "-1", "99999999999" } )
        {
            MockHttpServletRequest request = new MockHttpServletRequest( );
            AdminUserUtils.registerAdminUserWithRight( request, new AdminUser( ), StyleSheetJspBean.RIGHT_MANAGE_STYLESHEET );
            request.addParameter( Parameters.STYLESHEET_ID, strId );
            ErrorResponse response = new ErrorResponse( );

            new StyleSheetFileServlet( ).processRequest( request, response );

            assertEquals( HttpServletResponse.SC_NOT_FOUND, response._nError, strId );
        }
    }
}
