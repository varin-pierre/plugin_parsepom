package fr.paris.lutece.plugins.parsepom.services;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;

public class FileChooser extends JPanel implements ActionListener 
{
	   JButton go;
	   
	   JFileChooser chooser;
	   String choosertitle;
	   String path;
	   
	  public String getPath()
	  {
		  return path;
	  }
	  
	  public FileChooser( )
	  {
	    go = new JButton("Do it");
	    go.addActionListener(this);
	    add(go);
	   }

	  public void actionPerformed(ActionEvent e) 
	  {
	    int result;
	        
	    chooser = new JFileChooser(); 
	    chooser.setCurrentDirectory(new java.io.File("."));
	    chooser.setDialogTitle(choosertitle);
	    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	    //
	    // disable the "All files" option.
	    //
	    chooser.setAcceptAllFileFilterUsed(false);
	    //    
	    if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
	    { 
	      System.out.println("getCurrentDirectory(): " 
	         +  chooser.getCurrentDirectory());
	      System.out.println("getSelectedFile() : " 
	         +  chooser.getSelectedFile());
	      path = chooser.getSelectedFile().toString();
	      
	    }
	    else 
	    {
	      System.out.println("No Selection ");
	    }
	    path = paramString();
	  }
	   
	  public Dimension getPreferredSize()
	  {
	    return new Dimension(300, 300);
	  }

}
