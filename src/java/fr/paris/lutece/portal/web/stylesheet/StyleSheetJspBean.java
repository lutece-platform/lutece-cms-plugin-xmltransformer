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

import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.lang3.StringUtils;
import org.xml.sax.InputSource;

import fr.paris.lutece.portal.business.portalcomponent.PortalComponentHome;
import fr.paris.lutece.portal.business.portlet.PortletType;
import fr.paris.lutece.portal.business.portlet.PortletTypeHome;
import fr.paris.lutece.portal.business.style.ModeHome;
import fr.paris.lutece.portal.business.style.Style;
import fr.paris.lutece.portal.business.style.StyleHome;
import fr.paris.lutece.portal.business.stylesheet.StyleSheet;
import fr.paris.lutece.portal.business.stylesheet.StyleSheetHome;
import fr.paris.lutece.portal.service.fileupload.FileUploadService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.upload.MultipartItem;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.xsl.XslSecurityService;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.RequestParam;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.web.cdi.mvc.Models;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.portal.web.constants.Parameters;
import fr.paris.lutece.portal.web.util.IPager;
import fr.paris.lutece.portal.web.util.Pager;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.sort.AttributeComparator;
import fr.paris.lutece.util.url.UrlItem;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Manages the XSL stylesheets of the styles.
 */
@RequestScoped
@Named
@Controller( controllerJsp = "ManageStyleSheets.jsp", controllerPath = "jsp/admin/style/", right = StyleSheetJspBean.RIGHT_MANAGE_STYLESHEET, securityTokenEnabled = true )
public class StyleSheetJspBean extends MVCAdminJspBean
{
    /** Right to manage the stylesheets */
    public static final String RIGHT_MANAGE_STYLESHEET = "XMLTRANSFORMER_STYLESHEET_MANAGEMENT";

    private static final long serialVersionUID = 8176263113722225633L;

    private static final String VIEW_MANAGE_STYLESHEETS = "manageStyleSheets";
    private static final String VIEW_CREATE_STYLESHEET = "createStyleSheet";
    private static final String VIEW_MODIFY_STYLESHEET = "modifyStyleSheet";
    private static final String VIEW_CONFIRM_REMOVE_STYLESHEET = "confirmRemoveStyleSheet";
    private static final String ACTION_CREATE_STYLESHEET = VIEW_CREATE_STYLESHEET;
    private static final String ACTION_MODIFY_STYLESHEET = VIEW_MODIFY_STYLESHEET;
    private static final String ACTION_REMOVE_STYLESHEET = "removeStyleSheet";

    private static final String MARK_MODE_ID = "mode_id";
    private static final String MARK_MODE_LIST = "mode_list";
    private static final String MARK_STYLESHEET_LIST = "stylesheet_list";
    private static final String MARK_STYLE_LIST = "style_list";
    private static final String MARK_STYLESHEET = "stylesheet";
    private static final String MARK_PORTAL_COMPONENT_NAME = "portal_component_name";
    private static final String MARK_PORTLET_TYPE_NAME = "portlet_type_name";
    private static final String MARK_STYLE_DESCRIPTION = "style_description";

    private static final String TEMPLATE_MANAGE_STYLESHEETS = "admin/stylesheet/manage_stylesheets.html";
    private static final String TEMPLATE_CREATE_STYLESHEET = "admin/stylesheet/create_stylesheet.html";
    private static final String TEMPLATE_MODIFY_STYLESHEET = "admin/stylesheet/modify_stylesheet.html";
    private static final String TEMPLATE_STYLE_SELECT_OPTION = "admin/stylesheet/style_select_option.html";

    private static final String PROPERTY_STYLESHEETS_PER_PAGE = "paginator.stylesheet.itemsPerPage";
    private static final String MESSAGE_STYLESHEET_ALREADY_EXISTS = "xmltransformer.message.stylesheetAlreadyExists";
    private static final String MESSAGE_STYLESHEET_NOT_VALID = "xmltransformer.message.stylesheetNotValid";
    private static final String MESSAGE_STYLESHEET_SECURITY_VIOLATION = "xmltransformer.message.stylesheetSecurityViolation";
    private static final String MESSAGE_CONFIRM_DELETE_STYLESHEET = "xmltransformer.message.stylesheetConfirmDelete";
    private static final String MESSAGE_CONFIRM_REMOVE_STYLESHEET = "xmltransformer.message.stylesheetConfirmRemove";
    private static final String MESSAGE_STYLESHEET_NOT_FOUND = "xmltransformer.message.stylesheetNotFound";
    private static final String LABEL_ALL = "portal.util.labelAll";
    private static final String JSP_CONFIRM_REMOVE_STYLE = "ManageStyles.jsp?view=getConfirmRemoveStyle&id=";
    private static final String NO_MODE = "-1";

    @Inject
    @Pager( listBookmark = MARK_STYLESHEET_LIST, defaultItemsPerPage = PROPERTY_STYLESHEETS_PER_PAGE )
    private IPager<StyleSheet, Void> _pager;

    /**
     * Displays the stylesheets, filtered by mode and sorted on request.
     *
     * @param model
     *            the model
     * @param request
     *            the request
     * @return the page
     */
    @View( value = VIEW_MANAGE_STYLESHEETS, defaultView = true )
    public String getManageStyleSheets( Models model, HttpServletRequest request )
    {
        String strModeId = StringUtils.isNumeric( request.getParameter( Parameters.MODE_ID ) ) ? request.getParameter( Parameters.MODE_ID ) : NO_MODE;
        ReferenceList listModes = ModeHome.getModes( );
        listModes.addItem( -1, I18nService.getLocalizedString( LABEL_ALL, getLocale( ) ) );

        List<StyleSheet> listStyleSheets = (List<StyleSheet>) StyleSheetHome.getStyleSheetList( Integer.parseInt( strModeId ) );
        UrlItem url = new UrlItem( getControllerPath( ) + getControllerJsp( ) );
        url.addParameter( Parameters.MODE_ID, strModeId );
        String strSortedAttributeName = request.getParameter( Parameters.SORTED_ATTRIBUTE_NAME );

        if ( strSortedAttributeName != null )
        {
            String strAscSort = request.getParameter( Parameters.SORTED_ASC );
            Collections.sort( listStyleSheets, new AttributeComparator( strSortedAttributeName, Boolean.parseBoolean( strAscSort ) ) );
            url.addParameter( Parameters.SORTED_ATTRIBUTE_NAME, strSortedAttributeName );
            url.addParameter( Parameters.SORTED_ASC, String.valueOf( strAscSort ) );
        }

        _pager.withBaseUrl( url.getUrl( ) ).withListItem( listStyleSheets ).populateModels( request, model, getLocale( ) );
        model.put( MARK_MODE_ID, strModeId );
        model.put( MARK_MODE_LIST, listModes );

        return getAdminPage( AppTemplateService.getTemplate( TEMPLATE_MANAGE_STYLESHEETS, getLocale( ), model ).getHtml( ) );
    }

    /**
     * Displays the creation form.
     *
     * @param model
     *            the model
     * @param request
     *            the request
     * @return the page
     */
    @View( value = VIEW_CREATE_STYLESHEET )
    public String getCreateStyleSheet( Models model, HttpServletRequest request )
    {
        model.put( MARK_STYLE_LIST, getStyleList( ) );
        model.put( MARK_MODE_LIST, ModeHome.getModes( ) );
        model.put( MARK_MODE_ID, StringUtils.defaultIfBlank( request.getParameter( Parameters.MODE_ID ), NO_MODE ) );

        return getAdminPage( AppTemplateService.getTemplate( TEMPLATE_CREATE_STYLESHEET, getLocale( ), model ).getHtml( ) );
    }

    /**
     * Creates a stylesheet from the uploaded XSL file.
     *
     * @param source
     *            the uploaded XSL file
     * @param request
     *            the request
     * @return the redirection
     */
    @Action( value = ACTION_CREATE_STYLESHEET )
    public String doCreateStyleSheet( @RequestParam( value = Parameters.STYLESHEET_SOURCE, required = false ) MultipartItem source, HttpServletRequest request )
    {
        StyleSheet stylesheet = new StyleSheet( );
        String strErrorUrl = getData( request, source, stylesheet );

        if ( strErrorUrl != null )
        {
            return redirect( request, strErrorUrl );
        }

        StyleSheetHome.create( stylesheet );

        return redirectView( request, VIEW_MANAGE_STYLESHEETS );
    }

    /**
     * Displays the modification form.
     *
     * @param model
     *            the model
     * @param request
     *            the request
     * @return the page
     */
    @View( value = VIEW_MODIFY_STYLESHEET )
    public String getModifyStyleSheet( Models model, HttpServletRequest request )
    {
        StyleSheet stylesheet = findStyleSheet( request );

        if ( stylesheet == null )
        {
            return redirect( request, AdminMessageService.getMessageUrl( request, MESSAGE_STYLESHEET_NOT_FOUND, AdminMessage.TYPE_STOP ) );
        }

        model.put( MARK_STYLE_LIST, getStyleList( ) );
        model.put( MARK_MODE_LIST, ModeHome.getModes( ) );
        model.put( MARK_STYLESHEET, stylesheet );

        return getAdminPage( AppTemplateService.getTemplate( TEMPLATE_MODIFY_STYLESHEET, getLocale( ), model ).getHtml( ) );
    }

    /**
     * Updates a stylesheet from the uploaded XSL file.
     *
     * @param source
     *            the uploaded XSL file
     * @param request
     *            the request
     * @return the redirection
     */
    @Action( value = ACTION_MODIFY_STYLESHEET )
    public String doModifyStyleSheet( @RequestParam( value = Parameters.STYLESHEET_SOURCE, required = false ) MultipartItem source, HttpServletRequest request )
    {
        StyleSheet stylesheet = findStyleSheet( request );

        if ( stylesheet == null )
        {
            return redirect( request, AdminMessageService.getMessageUrl( request, MESSAGE_STYLESHEET_NOT_FOUND, AdminMessage.TYPE_STOP ) );
        }

        String strErrorUrl = getData( request, source, stylesheet );

        if ( strErrorUrl != null )
        {
            return redirect( request, strErrorUrl );
        }

        StyleSheetHome.update( stylesheet );

        return redirectView( request, VIEW_MANAGE_STYLESHEETS );
    }

    /**
     * Asks for the confirmation of a stylesheet removal. A removal started from the removal of a style carries the style id.
     *
     * @param request
     *            the request
     * @return the redirection to the confirmation message
     */
    @View( value = VIEW_CONFIRM_REMOVE_STYLESHEET, securityTokenAction = ACTION_REMOVE_STYLESHEET )
    public String getConfirmRemoveStyleSheet( HttpServletRequest request )
    {
        StyleSheet stylesheet = findStyleSheet( request );

        if ( stylesheet == null )
        {
            return redirect( request, AdminMessageService.getMessageUrl( request, MESSAGE_STYLESHEET_NOT_FOUND, AdminMessage.TYPE_STOP ) );
        }

        Map<String, Object> parameters = new HashMap<>( );
        parameters.put( Parameters.STYLESHEET_ID, Integer.toString( stylesheet.getId( ) ) );
        String strStyleId = request.getParameter( Parameters.STYLE_ID );
        String strMessage = MESSAGE_CONFIRM_REMOVE_STYLESHEET;

        if ( StringUtils.isNumeric( strStyleId ) )
        {
            parameters.put( Parameters.STYLE_ID, strStyleId );
            strMessage = MESSAGE_CONFIRM_DELETE_STYLESHEET;
        }

        Object [ ] args = {
                stylesheet.getDescription( )
        };

        return redirect( request, AdminMessageService.getMessageUrl( request, strMessage, args, null,
                getActionUrl( ACTION_REMOVE_STYLESHEET ), null, AdminMessage.TYPE_CONFIRMATION, parameters ) );
    }

    /**
     * Removes a stylesheet, then resumes the removal of its style when it started there.
     *
     * @param request
     *            the request
     * @return the redirection
     */
    @Action( value = ACTION_REMOVE_STYLESHEET )
    public String doRemoveStyleSheet( HttpServletRequest request )
    {
        StyleSheet stylesheet = findStyleSheet( request );

        if ( stylesheet != null )
        {
            StyleSheetHome.remove( stylesheet.getId( ) );
        }

        String strStyleId = request.getParameter( Parameters.STYLE_ID );

        if ( StringUtils.isNumeric( strStyleId ) )
        {
            return redirect( request, JSP_CONFIRM_REMOVE_STYLE + strStyleId );
        }

        return redirectView( request, VIEW_MANAGE_STYLESHEETS );
    }

    /**
     * Builds the labelled list of the styles, as options of the style select.
     *
     * @return the styles
     */
    public ReferenceList getStyleList( )
    {
        ReferenceList listStyles = new ReferenceList( );

        for ( Style style : StyleHome.getStylesList( ) )
        {
            Map<String, Object> model = new HashMap<>( );
            model.put( MARK_PORTAL_COMPONENT_NAME, PortalComponentHome.findByPrimaryKey( style.getPortalComponentId( ) ).getName( ) );
            PortletType portletType = PortletTypeHome.findByPrimaryKey( style.getPortletTypeId( ) );
            model.put( MARK_PORTLET_TYPE_NAME, ( portletType != null ) ? I18nService.getLocalizedString( portletType.getNameKey( ), getLocale( ) ) : "" );
            model.put( MARK_STYLE_DESCRIPTION, style.getDescription( ) );
            HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_STYLE_SELECT_OPTION, getLocale( ), model );
            listStyles.addItem( style.getId( ), template.getHtml( ) );
        }

        return listStyles;
    }

    /**
     * Loads the stylesheet named by the request.
     *
     * @param request
     *            the request
     * @return the stylesheet, or null when the id is missing or unknown
     */
    private StyleSheet findStyleSheet( HttpServletRequest request )
    {
        String strId = request.getParameter( Parameters.STYLESHEET_ID );

        return StringUtils.isNumeric( strId ) ? StyleSheetHome.findByPrimaryKey( Integer.parseInt( strId ) ) : null;
    }

    /**
     * Checks the form and fills the stylesheet.
     *
     * @param request
     *            the request
     * @param source
     *            the uploaded XSL file
     * @param stylesheet
     *            the stylesheet to fill
     * @return the url of the error message, or null when the data is valid
     */
    private String getData( HttpServletRequest request, MultipartItem source, StyleSheet stylesheet )
    {
        String strDescription = request.getParameter( Parameters.STYLESHEET_NAME );
        String strStyleId = request.getParameter( Parameters.STYLES );
        String strModeId = request.getParameter( Parameters.MODE_STYLESHEET );
        String strFilename = ( source != null ) ? FileUploadService.getFileNameOnly( source ) : null;

        if ( StringUtils.isAnyBlank( strDescription, strFilename ) || !StringUtils.isNumeric( strStyleId ) || !StringUtils.isNumeric( strModeId ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        int nStyleId = Integer.parseInt( strStyleId );
        int nModeId = Integer.parseInt( strModeId );

        if ( ( stylesheet.getId( ) == 0 ) && ( StyleSheetHome.getStyleSheetNbPerStyleMode( nStyleId, nModeId ) >= 1 ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_STYLESHEET_ALREADY_EXISTS, AdminMessage.TYPE_STOP );
        }

        byte [ ] baXslSource = source.get( );

        if ( !isValid( baXslSource ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_STYLESHEET_NOT_VALID, AdminMessage.TYPE_STOP );
        }

        if ( !XslSecurityService.validateXslSecurity( baXslSource ).isEmpty( ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_STYLESHEET_SECURITY_VIOLATION, AdminMessage.TYPE_STOP );
        }

        stylesheet.setDescription( strDescription );
        stylesheet.setStyleId( nStyleId );
        stylesheet.setModeId( nModeId );
        stylesheet.setSource( baXslSource );
        stylesheet.setFile( strFilename );

        return null;
    }

    /**
     * Tells whether the XSL source is well-formed XML, with the external entities disabled.
     *
     * @param baXslSource
     *            the XSL source
     * @return true when the source parses
     */
    private boolean isValid( byte [ ] baXslSource )
    {
        try
        {
            SAXParserFactory factory = SAXParserFactory.newInstance( );
            factory.setFeature( "http://apache.org/xml/features/disallow-doctype-decl", true );
            factory.setFeature( "http://xml.org/sax/features/external-general-entities", false );
            factory.setFeature( "http://xml.org/sax/features/external-parameter-entities", false );
            SAXParser analyzer = factory.newSAXParser( );
            analyzer.getXMLReader( ).parse( new InputSource( new ByteArrayInputStream( baXslSource ) ) );

            return true;
        }
        catch( Exception e )
        {
            AppLogService.debug( "Invalid XSL stylesheet: {}", e.getMessage( ), e );

            return false;
        }
    }
}
