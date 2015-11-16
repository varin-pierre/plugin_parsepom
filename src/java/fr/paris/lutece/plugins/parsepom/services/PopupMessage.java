package fr.paris.lutece.plugins.parsepom.services;

import javax.swing.JOptionPane;

public class PopupMessage
{
    public static void infoBox(String infoMessage)
    {
        JOptionPane.showMessageDialog(null, infoMessage, null, JOptionPane.INFORMATION_MESSAGE);
    }
}
