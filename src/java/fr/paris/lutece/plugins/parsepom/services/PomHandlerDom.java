package fr.paris.lutece.plugins.parsepom.services;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import fr.paris.lutece.plugins.parsepom.business.Dependency;
import fr.paris.lutece.plugins.parsepom.business.Site;
import fr.paris.lutece.portal.service.util.AppLogService;

public class PomHandlerDom
{
	// Tags
	private static final String TAG_DEPENDENCY = "dependency";
	private static final String TAG_GROUP_ID = "groupId";
	private static final String TAG_ARTIFACT_ID = "artifactId";
	private static final String TAG_NAME = "name";
	private static final String TAG_VERSION = "version";
	private static final String TAG_TYPE = "type";
	private static final String TAG_EXCLUSIONS = "exclusions";
	private static final String TAG_MAIN_NODE = "project";
		
	private   ArrayList<Dependency> _listDependencies = new ArrayList<Dependency>( );
	private   Site _site = new Site( );
	
	public  List<Dependency> getDependencies(  )
    {
        return _listDependencies;
    }
    
    public  Site getSite( )
    {
    	return _site;
    }
    
	public void parse( File pom )
	{
		try 
		{
			DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance( ).newDocumentBuilder( );
			Document doc = dBuilder.parse( pom );
		
			doc.getDocumentElement( ).normalize( );
			
			NodeList nlSite = doc.getChildNodes( ).item(0).getChildNodes( );
			filledSite( nlSite );
			NodeList nlDenpendency = doc.getElementsByTagName( TAG_DEPENDENCY );

			for ( int i = 0; i < nlDenpendency.getLength( ); i++ )
			{
				Node n = nlDenpendency.item( i );
				// test if node dependency is not in configuration node
				if ( n.getNodeType( ) == Node.ELEMENT_NODE && n.getParentNode( ).getParentNode( ).getNodeName( ).equals( TAG_MAIN_NODE ) )
				{
					filledDependency( n );
				}
			}
		}
	    catch ( ParserConfigurationException | SAXException | IOException e )
        {
        	 AppLogService.error( e.getMessage( ) , e );
        }
	}
	
	private void filledSite( NodeList nlSite )
	{
		for ( int i = 0; i < nlSite.getLength( ); i++ )
		{
			Node p = nlSite.item( i );
			if ( p.getNodeType( ) == Node.ELEMENT_NODE )
			{
				if (p.getNodeName( ).equals( TAG_ARTIFACT_ID ) )
				{
					_site.setArtifactId( p.getTextContent( ).trim( ) );
				}
				if (p.getNodeName( ).equals( TAG_VERSION ) )
				{
					_site.setVersion( p.getTextContent( ).trim( ) );
				}
				if (p.getNodeName( ).equals( TAG_NAME ) )
				{
					_site.setName( p.getTextContent( ).trim( ) );
				}
			}
		}
	}
	
	private  void filledDependency( Node n )
	{
		Dependency dep = new Dependency( );
		NodeList nl = n.getChildNodes( );
		
		for ( int i = 0; i < nl.getLength( ); i++ )
		{
			Node t = n.getChildNodes().item(i);
			if ( t.getNodeType( ) == Node.ELEMENT_NODE)
			{
				if ( t.getNodeName( ).equals( TAG_ARTIFACT_ID ) )
				{
					dep.setArtifactId(t.getTextContent( ).trim( ) );
				}
				if ( t.getNodeName( ).equals( TAG_VERSION ) )
				{
					dep.setVersion( t.getTextContent( ).trim( ) );
				}
				if ( t.getNodeName( ).equals( TAG_GROUP_ID ) )
				{
					dep.setGroupId( t.getTextContent( ).trim( ) );
				}
				if ( t.getNodeName( ).equals( TAG_TYPE ) )
				{
					dep.setType( t.getTextContent( ).trim( ) );
				}
				if ( t.getNodeName( ).equals( TAG_EXCLUSIONS ) )
				{
					//TODO add Object exclusion
				}
			}
		}
		_listDependencies.add( dep );
	}
}