package fr.paris.lutece.portal.service.html;

import fr.paris.lutece.portal.service.init.StartUpService;

public class XmlTransformerCacheManager implements StartUpService
{

    @Override
    public String getName( )
    {
        return XmlTransformerCacheManager.class.getName( );
    }

    @Override
    public void process( )
    {
        XmlTransformerCacheService.init( );
    }

}
