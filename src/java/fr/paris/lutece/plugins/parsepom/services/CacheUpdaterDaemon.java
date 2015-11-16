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
        long t1 = new Date(  ).getTime(  );
        //MavenRepoService.instance(  ).updateCache(  );
        instance( );
        long t2 = new Date(  ).getTime(  );
        AppLogService.debug( "LOLOLOLOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO : " + UPDATE_DELAY );
        setLastRunLogs( "Lutece Tools - Cache for Maven info updated : duration = " + ( t2 - t1 ) + "ms" );
        System.out.println("======================");
        System.out.println(UPDATE_DELAY);
        System.out.println("======================");
    }
    
    
    private boolean shouldBeUpdated( String strLastUpdate )
    {
        // The last update is too far
        long lNow = new Date(  ).getTime(  );

        return ( lNow - 5 ) > UPDATE_DELAY;
    }
    
    public static synchronized void instance( )
    {
    	System.out.println("======================");
        System.out.println("YES");
        System.out.println("======================");
    }
}
