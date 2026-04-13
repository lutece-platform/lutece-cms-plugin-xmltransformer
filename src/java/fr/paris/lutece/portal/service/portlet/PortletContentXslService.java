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
package fr.paris.lutece.portal.service.portlet;

import java.util.Map;
import java.util.Properties;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Specializes;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;

import fr.paris.lutece.portal.business.portlet.Portlet;
import fr.paris.lutece.portal.business.style.ModeHome;
import fr.paris.lutece.portal.service.cache.ICacheKeyService;
import fr.paris.lutece.portal.service.html.XmlTransformerService;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.page.PortletCacheService;

@ApplicationScoped
@Specializes
public class PortletContentXslService extends PortletContentService
{

//    @Inject
//    public PortletContentXslService( PortletCacheService portletCacheService, ICacheKeyService portletCacheKeyService )
//    {
//        super( portletCacheService, portletCacheKeyService );
//    }

    private static final String XSL_UNIQUE_PREFIX = "page-";
    
    @Override
    protected String getXslContent(HttpServletRequest request, Portlet portlet, Map<String, String> mapParams, int nMode) throws SiteMessageException
    {
        Properties outputProperties = ModeHome.getOuputXslProperties( nMode );
        String strXslUniqueId = XSL_UNIQUE_PREFIX + String.valueOf( portlet.getStyleId( ) );
        XmlTransformerService xmlTransformerService = new XmlTransformerService( );
        String strPortletXmlContent = portlet.getXml( request );
        return xmlTransformerService.transformBySourceWithXslCache( strPortletXmlContent, portlet.getXslSource( nMode ), strXslUniqueId, mapParams, outputProperties );
    }
    
}
