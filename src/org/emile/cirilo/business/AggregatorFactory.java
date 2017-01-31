package org.emile.cirilo.business;

import java.io.StringReader;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.io.InputStream;
import java.net.URLConnection;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.emile.cirilo.Common;
import org.emile.cirilo.ecm.repository.Repository;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.DOMBuilder;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.transform.JDOMResult;
import org.jdom.transform.JDOMSource;
import org.jdom.xpath.XPath;

public class AggregatorFactory {

    private static Logger log = Logger.getLogger(AggregatorFactory.class);

	public AggregatorFactory() {};
	
	public boolean aggregateKML(String pid, String tit, Document kml, Transformer transformer) {
		try {
	        DOMBuilder db = new DOMBuilder();
	        org.jdom.Document doc = db.build (Repository.getDatastream(pid, "METADATA", new Integer(0)));

			XPath xpath = XPath.newInstance("/s:sparql/s:results/s:result[contains(s:model/@uri,'cm:TEI') or contains(s:model/@uri,'cm:LIDO') or contains(s:model/@uri,'cm:OAIRecord')]");
			xpath.addNamespace( Common.xmlns_sparql );

			List nodes = (List) xpath.selectNodes( doc );
			
		   	if (nodes.size() > 0) {

		   		System.setProperty("javax.xml.transform.TransformerFactory",  "net.sf.saxon.TransformerFactoryImpl");  
				
				Format format = Format.getRawFormat();
				format.setEncoding("UTF-8");
				XMLOutputter outputter = new XMLOutputter(format);
				
				XPath qPath =  XPath.newInstance("//k:Folder");
				qPath.addNamespace(Common.xmlns_kml);
				Element Folder = (Element) qPath.selectSingleNode( kml );

				try {
					Folder.getChild("name", Common.xmlns_kml).setText(tit);
				} catch (Exception e) {
					Element name = new Element("name", Common.xmlns_kml);
					name.setText(tit);
					Folder.addContent(name);
				}
				
				String oid = null;
				
				for (Iterator iter = nodes.iterator(); iter.hasNext();) 
	    		{	        	
					Element el = (Element) iter.next();
					String model = el.getChild("model", Common.xmlns_sparql).getAttributeValue("uri"); 

					oid = el.getChild("pid", Common.xmlns_sparql).getAttributeValue("uri").substring(Common.INFO_FEDORA.length()); 	
					
					String dsid;
					if (model.contains("cm:TEI")) dsid = "TEI_SOURCE";
					else if (model.contains("cm:LIDO")) dsid = "LIDO_SOURCE";
					else dsid = "EDM_STREAM";
					
					Document data = db.build (Repository.getDatastream(oid, dsid));
					
					transformer.setParameter("pid", oid);
					transformer.setParameter("model", model);
					
	        		JDOMSource in = new JDOMSource(data);
	        		JDOMResult out = new JDOMResult();
	        		transformer.transform(in, out);
	        		
	        		try {
	        			List children =  out.getDocument().getRootElement().getChildren();
	                
	        			for (Iterator jter = children.iterator(); jter.hasNext();) 
	        			{
	        				Element le = (Element) jter.next();
	        				Folder.addContent((Element)le.clone());
	        			}
	        		} catch (Exception ch) {}	

	    		}
		   		Repository.modifyDatastreamByValue(pid, "KML", "text/xml",outputter.outputString(kml).replaceAll("<k:","<").replaceAll("</k:","</"));
	        	
        		System.setProperty("javax.xml.transform.TransformerFactory",  "org.apache.xalan.processor.TransformerFactoryImpl");

		        return true;
		   	}
		   	
		   	return false;
		   	
		} catch (Exception e) {
		  	log.error(e.getLocalizedMessage(),e);		  		
		} finally {        	
    		System.setProperty("javax.xml.transform.TransformerFactory",  "org.apache.xalan.processor.TransformerFactoryImpl");
		}
		return false;
		
	}

	
	
	public boolean aggregatePELAGIOS(String pid, String tit, Document pelagios, Transformer transformer) {
		try {
	        DOMBuilder db = new DOMBuilder();
	        org.jdom.Document doc = db.build (Repository.getDatastream(pid, "METADATA", new Integer(0)));

			XPath xpath = XPath.newInstance("/s:sparql/s:results/s:result[contains(s:model/@uri,'cm:TEI') or contains(s:model/@uri,'cm:LIDO') or contains(s:model/@uri,'cm:OAIRecord')]");
			xpath.addNamespace( Common.xmlns_sparql );

			List nodes = (List) xpath.selectNodes( doc );
			
		   	if (nodes.size() > 0) {

		   		System.setProperty("javax.xml.transform.TransformerFactory",  "net.sf.saxon.TransformerFactoryImpl");  
				
				Format format = Format.getRawFormat();
				format.setEncoding("UTF-8");
				XMLOutputter outputter = new XMLOutputter(format);
				
		   		Element root = pelagios.getRootElement();
		   		
				for (Iterator iter = nodes.iterator(); iter.hasNext();) 
	    		{	        	
					Element el = (Element) iter.next();
					String oid = el.getChild("pid", Common.xmlns_sparql).getAttributeValue("uri").substring(Common.INFO_FEDORA.length()); 	
					String model = el.getChild("model", Common.xmlns_sparql).getAttributeValue("uri"); 
					
					String dsid;
					if (model.contains("cm:TEI")) dsid = "TEI_SOURCE";
					else if (model.contains("cm:LIDO")) dsid = "LIDO_SOURCE";
					else dsid = "EDM_STREAM";
					
					Document data = db.build (Repository.getDatastream(oid, dsid));
						
					transformer.setParameter("pid", oid);
					transformer.setParameter("model", model);
					
	        		JDOMSource in = new JDOMSource(data);
	        		JDOMResult out = new JDOMResult();
	        		transformer.transform(in, out);
	        		
	        		try {
	        			List children =  out.getDocument().getRootElement().getChildren();
	                
	        			for (Iterator jter = children.iterator(); jter.hasNext();) 
	        			{
	        				Element le = (Element) jter.next();
	        				root.addContent((Element)le.clone());
	        			}
	        		} catch (Exception ch) {}	

	    		}
		   		Repository.modifyDatastreamByValue(pid, "PELAGIOS", "text/xml",outputter.outputString(pelagios));
	        	
        		System.setProperty("javax.xml.transform.TransformerFactory",  "org.apache.xalan.processor.TransformerFactoryImpl");

		        return true;
		   	}
		   	
		   	return false;
		   	
		} catch (Exception e) {
		  	log.error(e.getLocalizedMessage(),e);		  		
		} finally {        	
    		System.setProperty("javax.xml.transform.TransformerFactory",  "org.apache.xalan.processor.TransformerFactoryImpl");
		}
		return false;
		
	}

	
	public boolean aggregateCMIF(String pid, String tit, Document cmif, Transformer transformer) {
		try {
	        DOMBuilder db = new DOMBuilder();
	        org.jdom.Document doc = db.build (Repository.getDatastream(pid, "METADATA", new Integer(0)));

			XPath xpath = XPath.newInstance("/s:sparql/s:results/s:result[contains(s:model/@uri,'cm:TEI')]");
			xpath.addNamespace( Common.xmlns_sparql );

			List nodes = (List) xpath.selectNodes( doc );
			
		   	if (nodes.size() > 0) {

		   		System.setProperty("javax.xml.transform.TransformerFactory",  "net.sf.saxon.TransformerFactoryImpl");  
				
				Format format = Format.getRawFormat();
				format.setEncoding("UTF-8");
				XMLOutputter outputter = new XMLOutputter(format);

				
				XPath qpath = XPath.newInstance("/t:TEI/t:teiHeader/t:profileDesc");
				qpath.addNamespace( Common.xmlns_tei_p5);								
		   		Element root = (Element) qpath.selectSingleNode( cmif );
		   		
				for (Iterator iter = nodes.iterator(); iter.hasNext();) 
	    		{	        	
					Element el = (Element) iter.next();
					String oid = el.getChild("pid", Common.xmlns_sparql).getAttributeValue("uri").substring(Common.INFO_FEDORA.length()); 	
					
					Document data = db.build (Repository.getDatastream(oid, "TEI_SOURCE" ));
					
					transformer.setParameter("pid", oid);
					
	        		JDOMSource in = new JDOMSource(data);
	        		JDOMResult out = new JDOMResult();
	        		transformer.transform(in, out);
	        		
	        		try {
	        			List children =  out.getDocument().getRootElement().getChildren();
	                
	        			for (Iterator jter = children.iterator(); jter.hasNext();) 
	        			{
	        				Element le = (Element) jter.next();
	        				root.addContent((Element)le.clone());
	        			}
	        		} catch (Exception ch) {}	

	    		}
		   		Repository.modifyDatastreamByValue(pid, "CMIF", "text/xml",outputter.outputString(cmif));
	        	
        		System.setProperty("javax.xml.transform.TransformerFactory",  "org.apache.xalan.processor.TransformerFactoryImpl");

		        return true;
		   	}
		   	
		   	return false;
		   	
		} catch (Exception e) {
		  	log.error(e.getLocalizedMessage(),e);		  		
		} finally {        	
    		System.setProperty("javax.xml.transform.TransformerFactory",  "org.apache.xalan.processor.TransformerFactoryImpl");
		}
		return false;
		
	}

	
}
