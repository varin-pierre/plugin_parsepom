package fr.paris.lutece.plugins.parsepom.services;

import javax.swing.*;

public class FileChooser 
{
	public static String chooserDir( )
	{
		String path = ""; 

		JFileChooser f = new JFileChooser( );
		f.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY ); 
		int result = f.showSaveDialog( null );
		
		if ( f.getSelectedFile() != null && result == JFileChooser.APPROVE_OPTION )
			path = f.getSelectedFile( ).toString( );

		return path ;
	}
}
