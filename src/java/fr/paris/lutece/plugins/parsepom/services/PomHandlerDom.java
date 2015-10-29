package fr.paris.lutece.plugins.parsepom.services;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.sun.xml.bind.v2.model.core.Element;

import fr.paris.lutece.plugins.parsepom.business.Dependency;
import fr.paris.lutece.plugins.parsepom.business.Site;
import fr.paris.lutece.portal.service.util.AppLogService;

public class PomHandlerDom
{
	private   ArrayList<Dependency> _listDependencies = new ArrayList<Dependency>(  );
	private   Site _site = new Site( );
	
	public  List<Dependency> getDependencies(  )
    {
        return _listDependencies;
    }
    
    public  Site getSite( )
    {
    	return _site;
    }
    
	public void parse( File pom ) throws SAXException, IOException, ParserConfigurationException
	{
		try 
		{
			DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance( ).newDocumentBuilder( );
			Document doc = dBuilder.parse( pom );
		
			doc.getDocumentElement( ).normalize( );
			
			NodeList nlSite = doc.getChildNodes( ).item(0).getChildNodes( );

			filledSite(nlSite);
			
			NodeList nlDenpendency = doc.getElementsByTagName( "dependency" );
			
			for ( int i = 0; i < nlDenpendency.getLength(); i++ )
			{
				Node n = nlDenpendency.item(i);

				if ( n.getNodeType() == Node.ELEMENT_NODE )
				{
					filledDependency( n );
				}
			}
			
		}
	    catch ( ParserConfigurationException e )
        {
        	 AppLogService.error( e.getMessage( ) , e );
        }
        catch ( SAXException e )
        {
        	 AppLogService.error( e.getMessage( ) , e );
        }
        catch ( IOException e )
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
				if (p.getNodeName( ).equals( "artifactId" ) )
				{
					_site.setArtifactId( p.getTextContent( ) );
				}
				if (p.getNodeName( ).equals( "version" ) )
				{
					_site.setVersion( p.getTextContent( ) );
				}
				if (p.getNodeName( ).equals( "name" ) )
				{
					_site.setName( p.getTextContent( ) );
				}
			}
		}
	}
	
	private  void filledDependency( Node n )
	{
		Dependency dep = new Dependency();
		NodeList nl = n.getChildNodes();
		
		for ( int i = 0; i < nl.getLength(); i++ )
		{
			Node t = n.getChildNodes().item(i);
			if ( t.getNodeType( ) == Node.ELEMENT_NODE)
			{
				if ( t.getNodeName( ).equals( "artifactId" ) )
				{
					dep.setArtifactId(t.getTextContent( ) );
				}
				if ( t.getNodeName( ).equals( "version" ) )
				{
					dep.setVersion( t.getTextContent( ) );
				}
				if ( t.getNodeName( ).equals( "groupId" ) )
				{
					dep.setGroupId( t.getTextContent( ) );
				}
				if ( t.getNodeName( ).equals( "type" ) )
				{
					dep.setType( t.getTextContent( ) );
				}
				if ( t.getNodeName( ).equals( "exclusions" ) )
				{
					//TODO add Object exclusion
				}
			}
		}
		_listDependencies.add(dep);
	}
}
