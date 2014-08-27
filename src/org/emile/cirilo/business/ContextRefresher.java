package org.emile.cirilo.business;

import java.io.StringReader;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.net.URLConnection;

import org.emile.cirilo.Common;
import org.emile.cirilo.ecm.repository.Repository;
import org.jdom.Element;
import org.jdom.input.DOMBuilder;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

public class ContextRefresher {

	
	public ContextRefresher() {};
	
	
	public boolean refreshKML(String pid, String tit) {
		try {
	        DOMBuilder db = new DOMBuilder();
	        org.jdom.Document doc = db.build (Repository.getDatastream(pid, "METADATA", new Integer(0)));

			XPath xpath = XPath.newInstance("/s:sparql/s:results/s:result[contains(s:model/@uri,'cm:TEI')]/s:pid");
			xpath.addNamespace( Common.xmlns_sparql );

									
			List nodes = (List) xpath.selectNodes( doc );
		   	if (nodes.size() > 0) {

				Format format = Format.getRawFormat();
				format.setEncoding("UTF-8");
				XMLOutputter outputter = new XMLOutputter(format);
		   		
				byte[] url =  Repository.getDatastream(pid, "KML_TEMPLATE" , "");

				SAXBuilder builder = new SAXBuilder(); 				
				URLConnection con = new URL (new String(url)).openConnection();
				con.setUseCaches(false);
		    	org.jdom.Document kml_template =builder.build(con.getInputStream());
		   														
				XPath qPath =  XPath.newInstance("//mm:metadata-mapping");
				qPath.addNamespace(Common.xmlns_mm);
				Element Placemark_template = (Element) qPath.selectSingleNode( kml_template );
				
				qPath =  XPath.newInstance("//k:Folder");
				qPath.addNamespace(Common.xmlns_kml);
				Element Folder = (Element) qPath.selectSingleNode( kml_template );
    												
				Folder.getChild("name", Common.xmlns_kml).setText(tit);

				for (Iterator iter = nodes.iterator(); iter.hasNext();) 
	    		{
	    			try {	
	    				Element e = (Element) iter.next();

	    				String uri = e.getAttributeValue("uri").substring(Common.INFO_FEDORA.length());
	    				org.jdom.Document tei = db.build (Repository.getDatastream(uri, "TEI_SOURCE"));
	    				
	    				XPath oPath = XPath.newInstance("//t:placeName[contains(@key,'geo:')]");
	    				oPath.addNamespace( Common.xmlns_tei_p5 );
	    				List placeNames = (List) oPath.selectNodes( tei );

	    				if (placeNames.size() > 0) {
	    					int i=0;
	    					for (Iterator jter = placeNames.iterator(); jter.hasNext();) 
	    		    		{
	    		    			try {	
	    		    				Element place = (Element) jter.next();
	    		    				Element name = place.getChild("ref", Common.xmlns_tei_p5 );
	    		    				
	    		    				if (name.getText().trim().length() > 0) {
	    		    					String p =  outputter.outputString(Placemark_template);
	    		    					i++;
	    		    					String pos = new Integer(i).toString();
	    		    				
	    		    					p = p.replace("t:placeName[", "t:placeName[position() = "+pos+" and ").replace("t:placeName/", "t:placeName[position() = "+pos+"]/");	    		    				
	    		    					MDMapper m = new MDMapper (uri, p);	
	    		    					String s = m.transform(tei).replace(">geo:",">");
	    		    					org.jdom.Document Placemark = builder.build(new StringReader(s));
	    		    					Folder.addContent((Element)Placemark.getRootElement().clone());
	    		    				}		
	    		    			} catch (Exception r) {}
	    		    		}	
	    				}
	 
	    				oPath = XPath.newInstance("//t:placeName/t:location/t:geo");
	    				oPath.addNamespace( Common.xmlns_tei_p5 );
	    				placeNames = (List) oPath.selectNodes( tei );

	    				if (placeNames.size() > 0) {
	    					int i=0;
	    					for (Iterator jter = placeNames.iterator(); jter.hasNext();) 
	    		    		{
    		    				Element place = (Element) jter.next();
	    		    			try {	
    		    					String p =  outputter.outputString(Placemark_template);
    		    					i++;
    		    					MDMapper m = new MDMapper (uri, p);	
    		    					String s = m.transform(tei);
    		    					org.jdom.Document Placemark = builder.build(new StringReader(s));
    		    					Folder.addContent((Element)Placemark.getRootElement().clone());
	    		    			} catch (Exception r) {}
	    		    		}	
	    				}
	 
	    				
					} catch (Exception q) {
					}
	                
	    		}		
								
				Placemark_template.getParent().removeContent(Placemark_template);
		   		Repository.modifyDatastreamByValue(pid, "KML", "text/xml",outputter.outputString(kml_template).replace("<k:", "<").replace("</k:", "</").replace("xmlns:k", "xmlns"));
		        return true;
		   	}
		   	
		   	return false;
		   	
		} catch (Exception e) {
			return false;
		}
		
		
	}


}
