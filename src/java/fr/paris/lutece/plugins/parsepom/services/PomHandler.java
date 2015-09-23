package fr.paris.lutece.plugins.parsepom.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import fr.paris.lutece.plugins.parsepom.business.Dependency;

public class PomHandler extends DefaultHandler
{
    private static SAXParserFactory _saxFactory;
    private ArrayList<Dependency> _listDependencies = new ArrayList<Dependency>(  );
    private Dependency _currentDependency;
    private boolean _nInsideDependency;
    private StringBuffer _sbBodyText = new StringBuffer(  );
    private String _strVersion;

    /**
     * @return the dependencies attribute of the Bootstrap object
     */
    public List<Dependency> getDependencies(  )
    {
        return _listDependencies;
    }
    
    public String getVersion(  ) 
    {
        return _strVersion;
    }

    /**
     * Parse a POM
     * @param project the project file to parse
     */
    public void parse( File project )
    {
        try
        {
            _saxFactory = SAXParserFactory.newInstance(  );

            SAXParser parser = _saxFactory.newSAXParser(  );
            InputSource is = new InputSource( new FileInputStream( project ) );
            parser.parse( is, this );
        }
        // TODO warning cath error log ?
        catch ( ParserConfigurationException e )
        {
            System.err.println( e.getMessage(  ) );
        }
        catch ( SAXException e )
        {
            System.err.println( e.getMessage(  ) );
        }
        catch ( IOException e )
        {
            System.err.println( e.getMessage(  ) );
        }
    }

    /**
     * Handles opening elements of the xml file.
     * @param uri the uri being parsed
     * @param localName element without namespace
     * @param rawName element name
     * @param attributes element attributes
     */
    // TODO import attribute ??? check if is good one
    @Override
    public void startElement( String uri, String localName, String rawName, Attributes attributes )
    {
        if ( "dependency".equals( rawName ) )
        {
            _currentDependency = new Dependency(  );
            _nInsideDependency = true;
        }
    }

    /**
     * some xml element text
     * @param buffer the text found
     * @param start the start position of text in the buffer
     * @param length the length of the text in the buffer
     */
    @Override
    public void characters( char[] buffer, int start, int length )
    {
        _sbBodyText.append( buffer, start, length );
    }

    /**
     * @return the current text from the characters method
     */
    private String getBodyText(  )
    {
        return _sbBodyText.toString(  ).trim(  );
    }

    /**
     * Handles closing elements of the xml file.
     * @param uri the uri being parsed
     * @param localName element without namespace
     * @param rawName element name
     */
    @Override
    public void endElement( String uri, String localName, String rawName )
    {
        if ( "dependency".equals( rawName ) )
        {
            _listDependencies.add( _currentDependency );
            _nInsideDependency = false;
        }
        else if ( "id".equals( rawName ) && _nInsideDependency )
        {
            _currentDependency.setId( Integer.parseInt( getBodyText(  ) ) );
        }
        else if ( "version".equals( rawName ) )
        {
            if( _nInsideDependency )
            {
                _currentDependency.setVersion( getBodyText(  ) );
            }
            else
            {
                _strVersion = getBodyText(  );
            }
        }
    }
}