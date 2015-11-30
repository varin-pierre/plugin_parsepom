package fr.paris.lutece.plugins.parsepom.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.validator.constraints.Length;
import org.springframework.aop.aspectj.annotation.LazySingletonAspectInstanceFactoryDecorator;

import fr.paris.lutece.plugins.parsepom.business.Dependency;
import fr.paris.lutece.plugins.parsepom.business.DependencyHome;
import fr.paris.lutece.plugins.parsepom.business.Site;
import fr.paris.lutece.plugins.parsepom.business.Tools;
import fr.paris.lutece.plugins.parsepom.business.ToolsHome;

public class VersionNote
{
	public List<Integer> listNote( Collection<Site> lSite )
	{
		List<Integer> lnote = new ArrayList<Integer>( );
		
		for ( Site s : lSite )
		{
			int note = 0;
			
			Collection<Dependency> lDep = DependencyHome.getDependencysListBySiteId( s.getId( ) );
			for ( Dependency d : lDep )
			{
				String lastVersion = "";
				Tools tools = ToolsHome.findByArtifactId( d.getArtifactId( ) );
				if ( tools != null )
					lastVersion = tools.getLastRelease( );
				String version = d.getVersion( );
				note += compareVersion( version,lastVersion );
//				note++;
//				if ( version.indexOf( "SNAPSHOT" ) != -1 )
//					note -= 100000;
				
			}
			
//			int nbDep = 0;
//			nbDep = lDep.size();
//			System.out.println("==> nbDep = " + nbDep );
//			if (  nbDep > 0 ) 
//				lnote.add( note / nbDep );
//			else
				lnote.add( note );
		}
		return ( lnote );
	}
	
	private int compareVersion( String version, String lastVersion )
	{
		List<String> ls = new ArrayList<String>();
		Pattern p = Pattern.compile("[0-9]+.[0-9]+.[0-9]+");
		Matcher m = p.matcher( version );
		while( m.find( ) ) 
		{
			String r = m.group();
			ls.add(r);
		}
		List<Integer> lnote = new ArrayList<Integer>(); 
		for ( String l : ls )
		{
			int note = 0;
			note += score( l, lastVersion );
			lnote.add(note);
		}
		int note = chooseNote(lnote);
		
		return ( note ); 
	}
	
	
	private int[] splitString( String str )
	{
		String[] strSplit = str.split( "\\." );
		int convert[] = new int[3];
		
		if (strSplit.length == 3 )
		{
			convert[0] = Integer.parseInt(strSplit[0]);
			convert[1] = Integer.parseInt(strSplit[1]);
			convert[2] = Integer.parseInt(strSplit[2]);
			return convert;
		}
		
		return null;
	}
	
	private int score( String v, String lv )
	{
		int note = 0;
		
		int SplitV[] = splitString(v);
		int SplitLv[] = splitString(lv);
		
		if ( SplitV == null || SplitLv == null )
			return -1;
		note = ( ( SplitLv[0] - SplitV[0] ) * 100  + ( SplitLv[1] -  SplitV[1] ) * 10 +  ( SplitLv[2] - SplitV[2] ) );
		return note;
	}
	
	private int chooseNote( List<Integer> lnote )
	{
		int note = 0;
		System.out.println(" len of lnote = " + lnote.size());
		for ( Integer n  : lnote )
		{
			if ( n > 0 )
				note = n;
		}
		return note;
	}
	
	
	public int note(Site site)
	{
		int note = 0;

		Collection<Dependency> listDep = DependencyHome.getDependencysListBySiteId( site.getId( ) );
		
		for ( Dependency d : listDep )
		{
			String v = d.getVersion();
			if ( v.indexOf("SNAPSHOT") != -1 )
				note += 10000;
			note++;
		}
		return note;
	}
	
}
