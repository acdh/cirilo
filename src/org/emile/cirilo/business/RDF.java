package org.emile.cirilo.business;


import java.io.File;
import java.util.*;

import org.apache.log4j.Logger;
import org.emile.cirilo.Common;
import org.emile.cirilo.ecm.repository.Repository;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.DOMBuilder;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

public class RDF {

	private static Logger log = Logger.getLogger(RDF.class);

	private Document rdf;
	private File file;
	private Format format;
	private XMLOutputter outputter;
	private SAXBuilder builder;
	private String PID;
	private HashMap<String,ArrayList<String>> HPID;

	
	public RDF() {		
		try {
			format = Format.getRawFormat();
			format.setEncoding("UTF-8");
			outputter = new XMLOutputter(format);
			this.builder = new SAXBuilder();
			HPID = new HashMap<String,ArrayList<String>>();
			
		} catch (Exception e) {
			log.error(e.getLocalizedMessage(),e);				      					   		
		}
	}


    public boolean set (String file) {
    	try {
			this.PID = "";
			this.file = new File (file);    			
   			if (this.file.exists()) { 
   				this.rdf = builder.build( this.file );
   				XPath xpath =  XPath.newInstance("//void:Dataset");
   				xpath.addNamespace(Common.xmlns_void);
   				Element dataset = (Element) xpath.selectSingleNode( this.rdf );
                if (dataset != null) {
                	this.PID = dataset.getAttributeValue("about",Common.xmlns_rdf).replace("fedora:", "");
                	if (!this.PID.startsWith("o:")){
                	    String[] a = this.PID.split("[/#]");
                	    for (int i = 0; i<a.length;i++) {
                	    	if (a[i].startsWith("o:")) {
                	    		this.PID = a[i];
                	    		break;
                	    	}
                	    }
                	}                	
                	if (HPID.get(this.PID) == null) {
                	    ArrayList<String> al = new ArrayList<String>();
                		al.add(file);
                	    HPID.put(this.PID,al);
                	} else {
                	    ArrayList<String> al = HPID.get(this.PID);
                		al.add(file);
                	    HPID.put(this.PID,al);                		
                	}
                }                   				
   			}   
   			return this.file.exists();
		} catch (Exception e) { 
      		log.error(e.getLocalizedMessage(),e);				      					   			
			return false;
		}
	}

    public ArrayList<String> getPidList() {
    	ArrayList<String> hm = new ArrayList<String>();
    	Iterator<String> keySetIterator = HPID.keySet().iterator();
    	while(keySetIterator.hasNext()){
    		String key = keySetIterator.next();
    		hm.add(key);
    	}
    	return hm;
    }

    public String get(String pid) {
    	String s = "";
    	try {
    		ArrayList<String> entries = HPID.get(pid);
    		
    		Element r = new Element ("RDF",Common.xmlns_rdf);
		
    		for (int j=0; j <entries.size();j++) {									
				Document file = builder.build( entries.get(j) );
				XPath  xpath = null;
				if (j==0) {
					List ns = file.getRootElement().getAdditionalNamespaces();
					for (int i=0; i<ns.size();i++) {
					   r.addNamespaceDeclaration((Namespace)ns.get(i));	
						xpath =  XPath.newInstance("/rdf:RDF/*");
						xpath.addNamespace(Common.xmlns_rdf);
					}
				}  else {
					xpath =  XPath.newInstance("/rdf:RDF/*[not(contains(name(),'Dataset'))]");
					xpath.addNamespace(Common.xmlns_rdf);
					xpath.addNamespace(Common.xmlns_void);
				}	

				List nodes = (List) xpath.selectNodes(file);
				for (Iterator iter = nodes.iterator(); iter.hasNext();) {
					try {
						Element o = (Element)iter.next();
						r.addContent((Element)o.clone());
					} catch (Exception q) {
						log.error(q.getLocalizedMessage(),q);				      					             		
					}
				}
    		}
    		this.rdf = new Document(r);  
    		s = outputter.outputString(this.rdf);
    	} catch (Exception e) { }
    	
    	return s;

    }
    
    
    public void mapDC( String pid) {  
      try {	
    	  
    	  DOMBuilder db = new DOMBuilder();
    	  Document dc = db.build (Repository.getDatastream(pid, "DC"));
    	  Element root = dc.getRootElement();
		  				    			  
    	  XPath xpath =  XPath.newInstance("/rdf:RDF/void:Dataset/dcterms:*");
    	  xpath.addNamespace(Common.xmlns_rdf);
    	  xpath.addNamespace(Common.xmlns_void);
    	  xpath.addNamespace(Common.xmlns_dcterms);

    	  List nodes = (List) xpath.selectNodes(this.rdf);
    	  if (nodes.size() > 0) {
    		  for (Iterator iter = nodes.iterator(); iter.hasNext();) {
    			  try {
    				  Element o = (Element)iter.next();
    				  if (!o.getName().equals("identifier")) {
    					  root.removeChildren(o.getName(),Common.xmlns_dc);
    					  Element dcterms = new Element(o.getName(), Common.xmlns_dc);
    					  dcterms.setText(o.getText());
    					  root.addContent(dcterms);
    				  }	  
    			  } catch (Exception q) {
    			  }
    		  }
    	  	  
    		  Repository.modifyDatastreamByValue(pid, "DC", "text/xml", outputter.outputString(dc));
    	  }	  
    	  
      } catch (Exception e) {}	  
    }
    
	public String toString() {
		  	this.rdf.getRootElement().removeChildren("Dataset", Common.xmlns_void);
	    	return this.outputter.outputString(this.rdf); 
	}

}
	
	 	 
