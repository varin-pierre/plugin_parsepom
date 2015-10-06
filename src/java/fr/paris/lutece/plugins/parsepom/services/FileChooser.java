package fr.paris.lutece.plugins.parsepom.services;

import javax.swing.*;

public class FileChooser 
{
	public static String chooserDir()
	{
		JFileChooser f = new JFileChooser();
		f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); 
		f.showSaveDialog(null);
		
		String path = f.getSelectedFile( ).toString( );

		return path ;
	}
}
