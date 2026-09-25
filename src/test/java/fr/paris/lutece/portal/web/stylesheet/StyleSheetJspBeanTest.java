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
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.portal.business.style.Style;
import fr.paris.lutece.portal.business.style.StyleHome;
import fr.paris.lutece.portal.business.stylesheet.StyleSheet;
import fr.paris.lutece.portal.business.stylesheet.StyleSheetHome;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.upload.MultipartItem;
import fr.paris.lutece.portal.web.cdi.mvc.Models;
import fr.paris.lutece.portal.web.constants.Parameters;
import fr.paris.lutece.portal.web.upload.MultipartHttpServletRequest;
import fr.paris.lutece.test.AdminUserUtils;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;
import fr.paris.lutece.test.mocks.MockHttpServletResponse;
import fr.paris.lutece.test.mocks.MockMultipartItem;
import fr.paris.lutece.test.mocks.TemporaryMultipartItemFactory;
import jakarta.inject.Inject;

/**
 * Tests the stylesheet controller through its views and actions.
 */
public class StyleSheetJspBeanTest extends LuteceTestCase
{
    private static final String VALID_XSL = "<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\"/>";

    @Inject
    private StyleSheetJspBean _instance;
    @Inject
    private Models _models;
    private Style _style;
    private StyleSheet _stylesheet;

    /**
     * Records the target of a redirection.
     */
    private static final class RedirectResponse extends MockHttpServletResponse
    {
        private String _strLocation;

        /**
         * {@inheritDoc}
         */
        @Override
        public void sendRedirect( String strLocation ) throws IOException
        {
            _strLocation = strLocation;
        }

        /**
         * Returns the target of the last redirection.
         *
         * @return the location
         */
        String getLocation( )
        {
            return _strLocation;
        }
    }

    /**
     * Creates a style and one of its stylesheets.
     */
    @BeforeEach
    protected void setUp( )
    {
        _style = new Style( );
        _style.setId( StyleHome.getStylesList( ).stream( ).map( Style::getId ).max( Integer::compare ).orElse( 0 ) + 1 );
        _style.setDescription( getRandomName( ) );
        _style.setPortalComponentId( 2 );
        StyleHome.create( _style );
        _stylesheet = new StyleSheet( );
        _stylesheet.setDescription( getRandomName( ) );
        _stylesheet.setModeId( 1 );
        _stylesheet.setStyleId( _style.getId( ) );
        _stylesheet.setFile( "file.xsl" );
        _stylesheet.setSource( VALID_XSL.getBytes( ) );
        StyleSheetHome.create( _stylesheet );
    }

    /**
     * Removes what the test created.
     */
    @AfterEach
    protected void tearDown( )
    {
        StyleSheetHome.getStyleSheetList( -1 ).stream( ).filter( s -> s.getStyleId( ) == _style.getId( ) ).forEach( s -> StyleSheetHome.remove( s.getId( ) ) );
        StyleHome.remove( _style.getId( ) );
    }

    /**
     * Builds a random name.
     *
     * @return the name
     */
    private static String getRandomName( )
    {
        Random rand = new SecureRandom( );

        return "junit" + new BigInteger( 128, rand ).toString( 36 );
    }

    /**
     * Builds a request of an administrator holding the stylesheet right.
     *
     * @return the request
     * @throws AccessDeniedException
     *             never with the right registered
     */
    private MockHttpServletRequest adminRequest( ) throws AccessDeniedException
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        AdminUserUtils.registerAdminUserWithRight( request, new AdminUser( ), StyleSheetJspBean.RIGHT_MANAGE_STYLESHEET );
        _instance.init( request, StyleSheetJspBean.RIGHT_MANAGE_STYLESHEET );

        return request;
    }

    /**
     * Builds a multipart request posting a stylesheet form.
     *
     * @param request
     *            the base request
     * @param strAction
     *            the action
     * @param strName
     *            the stylesheet name
     * @param strSource
     *            the XSL source
     * @return the multipart request
     * @throws IOException
     *             if the temporary file cannot be written
     */
    private MultipartHttpServletRequest stylesheetForm( MockHttpServletRequest request, String strAction, String strName, String strSource ) throws IOException
    {
        Map<String, String [ ]> parameters = new HashMap<>( );
        parameters.put( "action", new String [ ] { strAction } );
        parameters.put( Parameters.STYLESHEET_NAME, new String [ ] { strName } );
        parameters.put( Parameters.STYLES, new String [ ] { Integer.toString( _style.getId( ) ) } );
        parameters.put( Parameters.MODE_STYLESHEET, new String [ ] { "0" } );
        parameters.put( Parameters.STYLESHEET_ID, new String [ ] { Integer.toString( _stylesheet.getId( ) ) } );
        MockMultipartItem source = TemporaryMultipartItemFactory.create( Parameters.STYLESHEET_SOURCE, "application/xml", strName + ".xsl" );
        source.getOutputStream( ).write( strSource.getBytes( ) );
        Map<String, List<MultipartItem>> files = new HashMap<>( );
        files.put( Parameters.STYLESHEET_SOURCE, List.of( source ) );

        return new MultipartHttpServletRequest( request, files, parameters );
    }

    /**
     * The list view renders the stylesheet of the test.
     *
     * @throws AccessDeniedException
     *             never with the right registered
     */
    @Test
    public void testGetManageStyleSheets( ) throws AccessDeniedException
    {
        assertTrue( _instance.getManageStyleSheets( _models, adminRequest( ) ).contains( _stylesheet.getDescription( ) ) );
    }

    /**
     * The creation form renders its upload field.
     *
     * @throws AccessDeniedException
     *             never with the right registered
     */
    @Test
    public void testGetCreateStyleSheet( ) throws AccessDeniedException
    {
        assertTrue( _instance.getCreateStyleSheet( _models, adminRequest( ) ).contains( "stylesheet_source" ) );
    }

    /**
     * The modification form renders the stylesheet name.
     *
     * @throws AccessDeniedException
     *             never with the right registered
     */
    @Test
    public void testGetModifyStyleSheet( ) throws AccessDeniedException
    {
        MockHttpServletRequest request = adminRequest( );
        request.addParameter( Parameters.STYLESHEET_ID, Integer.toString( _stylesheet.getId( ) ) );

        assertTrue( _instance.getModifyStyleSheet( _models, request ).contains( _stylesheet.getDescription( ) ) );
    }

    /**
     * A valid XSL file creates a stylesheet.
     *
     * @throws Exception
     *             on a test failure
     */
    @Test
    public void testDoCreateStyleSheet( ) throws Exception
    {
        String strName = getRandomName( );
        _instance.processController( stylesheetForm( adminRequest( ), "createStyleSheet", strName, VALID_XSL ), new RedirectResponse( ) );

        assertTrue( StyleSheetHome.getStyleSheetList( 0 ).stream( ).anyMatch( s -> strName.equals( s.getDescription( ) ) ) );
    }

    /**
     * A file that is not XML is refused.
     *
     * @throws Exception
     *             on a test failure
     */
    @Test
    public void testDoCreateStyleSheetRefusesInvalidXml( ) throws Exception
    {
        String strName = getRandomName( );
        MultipartHttpServletRequest request = stylesheetForm( adminRequest( ), "createStyleSheet", strName, "not xml" );
        _instance.processController( request, new RedirectResponse( ) );

        assertNotNull( AdminMessageService.getMessage( request ) );
        assertTrue( StyleSheetHome.getStyleSheetList( 0 ).stream( ).noneMatch( s -> strName.equals( s.getDescription( ) ) ) );
    }

    /**
     * An unknown stylesheet answers a message.
     *
     * @throws AccessDeniedException
     *             never with the right registered
     */
    @Test
    public void testGetModifyStyleSheetUnknownId( ) throws AccessDeniedException
    {
        MockHttpServletRequest request = adminRequest( );
        request.addParameter( Parameters.STYLESHEET_ID, "999999" );
        _instance.getModifyStyleSheet( _models, request );

        assertNotNull( AdminMessageService.getMessage( request ) );
    }

    /**
     * A modification stores the new name.
     *
     * @throws Exception
     *             on a test failure
     */
    @Test
    public void testDoModifyStyleSheet( ) throws Exception
    {
        String strName = getRandomName( );
        _instance.processController( stylesheetForm( adminRequest( ), "modifyStyleSheet", strName, VALID_XSL ), new RedirectResponse( ) );

        assertEquals( strName, StyleSheetHome.findByPrimaryKey( _stylesheet.getId( ) ).getDescription( ) );
    }

    /**
     * The removal asks for a confirmation.
     *
     * @throws AccessDeniedException
     *             never with the right registered
     */
    @Test
    public void testGetConfirmRemoveStyleSheet( ) throws AccessDeniedException
    {
        MockHttpServletRequest request = adminRequest( );
        request.addParameter( Parameters.STYLESHEET_ID, Integer.toString( _stylesheet.getId( ) ) );
        _instance.getConfirmRemoveStyleSheet( request );

        AdminMessage message = AdminMessageService.getMessage( request );
        assertNotNull( message );
        assertEquals( Integer.toString( _stylesheet.getId( ) ), message.getRequestParameters( ).get( Parameters.STYLESHEET_ID ) );
    }

    /**
     * The removal deletes the stylesheet.
     *
     * @throws Exception
     *             on a test failure
     */
    @Test
    public void testDoRemoveStyleSheet( ) throws Exception
    {
        MockHttpServletRequest request = adminRequest( );
        request.addParameter( "action", "removeStyleSheet" );
        request.addParameter( Parameters.STYLESHEET_ID, Integer.toString( _stylesheet.getId( ) ) );
        _instance.processController( request, new RedirectResponse( ) );

        assertNull( StyleSheetHome.findByPrimaryKey( _stylesheet.getId( ) ) );
    }

    /**
     * A removal started from the removal of a style goes back to it.
     *
     * @throws Exception
     *             on a test failure
     */
    @Test
    public void testDoRemoveStyleSheetResumesStyleRemoval( ) throws Exception
    {
        MockHttpServletRequest request = adminRequest( );
        request.addParameter( "action", "removeStyleSheet" );
        request.addParameter( Parameters.STYLESHEET_ID, Integer.toString( _stylesheet.getId( ) ) );
        request.addParameter( Parameters.STYLE_ID, Integer.toString( _style.getId( ) ) );
        RedirectResponse response = new RedirectResponse( );
        _instance.processController( request, response );

        assertNull( StyleSheetHome.findByPrimaryKey( _stylesheet.getId( ) ) );
        assertTrue( response.getLocation( ).contains( "view=getConfirmRemoveStyle&id=" + _style.getId( ) ), response.getLocation( ) );
    }
}
