package fr.paris.lutece.plugins.parsepom.services;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.joda.time.DateTime;
import org.joda.time.Months;

public class TimeInterval
{
	private static final String FORMAT_DATE = "yyyy-MM-dd";
	
	public static int getMonthDiff( String strLastUpdate )
	{
		String timeStamp = new SimpleDateFormat( FORMAT_DATE ).format( Calendar.getInstance( ).getTime( ) );
        DateTime start = new DateTime( strLastUpdate.split(" ")[0] );
        DateTime end = new DateTime( timeStamp );

        Months months = Months.monthsBetween( start, end );
        
        int nMonths = months.getMonths( );
        
        return ( nMonths );
	}
}
