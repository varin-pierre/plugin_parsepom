package fr.paris.lutece.plugins.parsepom.services;

import java.util.Date;

import fr.paris.lutece.portal.service.daemon.Daemon;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

public class CacheUpdaterDaemon extends Daemon
{
	private static final String PROPERTY_UPDATE_DELAY = "parsepom.update.delay";
    private static final long DEFAULT_UPDATE_DELAY = 60000L; // 2 hours
    private static final long UPDATE_DELAY = AppPropertiesService.getPropertyLong( PROPERTY_UPDATE_DELAY, DEFAULT_UPDATE_DELAY );
    
    @Override
    public void run(  )
    {
    	System.out.println("======================");
        System.out.println("YES");
        System.out.println("======================");
    }
    
    
    private boolean shouldBeUpdated( String strLastUpdate )
    {
        // The last update is too far
        long lNow = new Date(  ).getTime(  );

        return ( lNow - 5 ) > UPDATE_DELAY;
    }
}
