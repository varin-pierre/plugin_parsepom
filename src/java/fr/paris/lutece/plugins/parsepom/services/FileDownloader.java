package fr.paris.lutece.plugins.parsepom.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileDownloader 
{
	public static Integer download( String fileInputPath )
	{
		FileInputStream in = null;
    	FileOutputStream out = null;
    	
    	try 
    	{
    		File fin = new File( fileInputPath );
    		if ( !fin.exists( ) )
			{
				return -1;
			}
    		
    		String filename = fileInputPath.substring( fileInputPath.lastIndexOf( "/" ) + 1 );
    		String outputPath = FileChooser.chooserDir( );
    		if ( outputPath.isEmpty( ) )
    			return 2;
			String fileOutputPath = outputPath.concat( "/" ).concat( filename );
			
			File fout = new File( fileOutputPath );
			if ( fout.exists( ) )
			{
				return 0;
			}

			System.out.println("------------------------");
			System.out.println(fileOutputPath);
			System.out.println("------------------------");
			
			in = new FileInputStream( fileInputPath );
			out = new FileOutputStream( fileOutputPath );
			
			int read = 0;
			byte[] bytes = new byte[1024];
	
			while ( ( read = in.read( bytes ) ) != -1)
			{
				out.write( bytes, 0, read );
			}
    	} 
    	catch ( IOException e )
    	{
    		e.printStackTrace( );
    	} 
    	finally
    	{
    		if ( in != null )
    		{
    			try
    			{
    				in.close( );
    			}
    			catch ( IOException e )
    			{
    				e.printStackTrace( );
    			}
    		}
    		if ( out != null )
    		{
    			try
    			{
    				out.close( );
    			}
    			catch ( IOException e )
    			{
    				e.printStackTrace( );
    			}
    		}
    	}
    	return 1;
	}
}
