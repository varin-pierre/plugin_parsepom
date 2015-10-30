package fr.paris.lutece.plugins.parsepom.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileDownloader 
{
	// Errors
	private static final int VALUE_FILECHOOSER_CANCELLED = -2;
	private static final int VALUE_INPUT_FILE_NOT_FOUND = -1;
    private static final int VALUE_OUTPUT_FILE_EXISTS = 0;
    private static final int VALUE_SUCCESS = 1;
    
	public static Integer download( String fileInputPath )
	{
		FileInputStream in = null;
    	FileOutputStream out = null;
    		
    	try 
    	{
    		File fin = new File( fileInputPath );
    		if ( !fin.exists( ) )
			{
				return VALUE_INPUT_FILE_NOT_FOUND;
			}
    		
    		String filename = fileInputPath.substring( fileInputPath.lastIndexOf( File.separator ) + 1 );
    		String outputPath = FileChooser.chooserDir( );
    		if ( outputPath.isEmpty( ) )
    		{
    			return VALUE_FILECHOOSER_CANCELLED;
    		}
    		
			String fileOutputPath = outputPath.concat( File.separator ).concat( filename );
			File fout = new File( fileOutputPath );
			if ( fout.exists( ) )
			{
				return VALUE_OUTPUT_FILE_EXISTS;
			}
			
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
    	return VALUE_SUCCESS;
	}
}
