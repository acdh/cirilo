package org.emile.cirilo.business;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.emile.cirilo.Common;
import org.emile.cirilo.ecm.repository.Repository;
import org.emile.cirilo.ecm.repository.FedoraConnector.Relation;

import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.DOMBuilder;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

public class SKOS {

	  public SKOS() {};
	  
	  public void createRELS_INT(String pid, String user, String dsid) 
	 	{
		  
			Format format = Format.getRawFormat();
			format.setEncoding("UTF-8");
			XMLOutputter outputter = new XMLOutputter(format);

		    int id = 0;
		    String stream = "";
		    String rdf ="";
		    HashMap <String,String>fragments = new HashMap<String,String>();
			
		    
	 		try {
	 			
	 				byte[] url =  Repository.getDatastream(pid, "RDF_MAPPING" , "");
	 			
	 				SAXBuilder builder = new SAXBuilder(); 	
					URLConnection con = new URL (new String(url)).openConnection();
					con.setUseCaches(false);
					org.jdom.Document mapping = builder.build(con.getInputStream());

	 				DOMBuilder db = new DOMBuilder();	
	 				org.jdom.Document skos = db.build( Repository.getDatastream(pid,dsid) );			
	 				
	 				List ns = mapping.getRootElement().getAdditionalNamespaces();
	            				 				
	 				XPath xpath = XPath.newInstance("//rdf:Description");
	 				xpath.addNamespace( Common.xmlns_rdf);
	 				
	 				List refs = (List) xpath.selectNodes( mapping );
	 				
	 			    if (refs.size() > 0 ) {
	 			        for (Iterator iter = refs.iterator(); iter.hasNext();) {
	 			        	try {
	 			        		
	 			        		Element e = (Element) iter.next();
	 			 				for (Iterator jter = ns.iterator(); jter.hasNext();) {
	 		 						try {
	 		 							Namespace node = (Namespace) jter.next();
	 		 							e.addNamespaceDeclaration(node);	   				
	 		 						} catch (Exception ex) {}		
	 		 				    }										

	 			        		String about = e.getAttributeValue("about",Common.xmlns_rdf);
	 			        		XPath path = XPath.newInstance("//"+about);	 			        		
	 			        		if (about.contains("rdf:Description")) path.addNamespace( Common.xmlns_rdf); else path.addNamespace( Common.xmlns_skos );
	 			        		List elems = (List) path.selectNodes( skos );

	 			        		for (Iterator jter = elems.iterator(); jter.hasNext();) {
	           			        	try {
	         			        	    Element q = (Element) jter.next();
		         			        	
                                        if (!about.contains("rdf:Description")) {  		         			        	
                                        	String[] ab = q.getAttributeValue("about",Common.xmlns_rdf).split("[#]");	        			        		
                                        	String p = q.getAttributeValue("id",Common. xmlns_xml );
                                        	if (p == null) {
                                        		q.setAttribute("id", ab[1], Common.xmlns_xml);
                                        	}
                                        	String ref  = "//"+  about+"[@xml:id='"+ab[1]+"']";
                                        	fragments.put(ab[1],ab[1]);
	         			        		
                                        	Element map = new Element("metadata-mapping", Common.xmlns_mm);
                                        	String mm = "<mm:metadata-mapping xmlns:mm= \""+Common.xmlns_mm.getURI()+"\" xmlns:skos= \""+Common.xmlns_skos.getURI()+"\">"+outputter.outputString(e).replaceAll("[.]/", ref+"/")+"</mm:metadata-mapping>"; 			        		
                                        	MDMapper m = new MDMapper(pid, mm, true);
                                        	org.jdom.Document r = builder.build( new StringReader (m.transform(skos) ) );							
                                        	Element root = r.getRootElement();         			        	
                                        	root.setAttribute("about", "info:fedora/"+pid+"/"+dsid+"#"+ab[1], Common.xmlns_rdf);         			        	
                                        	root.removeNamespaceDeclaration(Common.xmlns_tei_p5);
                                        	stream +=outputter.outputString(r).substring(39);
                                        } else {
                                        	String ab = q.getAttributeValue("about",Common.xmlns_rdf);
                 			        		String oid = q.getAttributeValue("id",Common. xmlns_xml );
                 			        		if (oid == null) {
                 			        			oid = "ID."+new Integer(++id).toString();
                 			        			q.setAttribute("id", oid, Common.xmlns_xml);
                 			        		}
                			        		String ref  = "//"+  about+"[@xml:id='"+oid+"']";
                 			        		fragments.put(oid,oid);
                                        	Element map = new Element("metadata-mapping", Common.xmlns_mm);
                                        	String mm = "<mm:metadata-mapping xmlns:mm= \""+Common.xmlns_mm.getURI()+"\" xmlns:skos= \""+Common.xmlns_skos.getURI()+"\">"+outputter.outputString(e).replaceAll("[.]/", ref+"/")+"</mm:metadata-mapping>"; 			        		
                                        	MDMapper m = new MDMapper(pid, mm, true);
                                        	org.jdom.Document r = builder.build( new StringReader (m.transform(skos) ) );							
                                        	Element root = r.getRootElement();         			        	
                                        	root.setAttribute("about", "info:fedora/"+pid+"/"+dsid+"#"+oid, Common.xmlns_rdf);         			        	
                                        	root.removeNamespaceDeclaration(Common.xmlns_tei_p5);                              
                                        	stream +=outputter.outputString(r).substring(39);    
                                        }
	         			        		         			        		
	         			            } catch (Exception ex) { 
	         			        		continue;	
	         			        	}
	         			        }                                                        
	 			        			        		
	 			            } catch (Exception ex) { 
	 			        		continue;	
	 			        	}
	 			        }
	 			         			
	 			        
	 			       for ( String elem : fragments.keySet() )
	 			    	   rdf+= "<rel:hasPart rdf:resource=\"info:fedora/"+pid+"/"+dsid+"#"+elem+"\"/>";
	 			       }
//			    	   rdf+= "<rel:isPartOf rdf:resource=\"info:fedora/context:"+user+"\"/>";

			    	   java.util.List  <org.emile.cirilo.ecm.repository.FedoraConnector.Relation>relations = Repository.getRelations(pid,Common.isMemberOf);	
				        for (Relation r : relations) {
				        	   String s=r.getTo(); 
					    	   rdf+= "<rel:isPartOf rdf:resource=\""+s+"\"/>";				        	  
				        }
				        
						rdf = "<rdf:RDF xmlns:rdf=\""+Common.xmlns_rdf.getURI()+"\"  xmlns:rel=\""+Common.xmlns_gams.getURI()+"\">"+ 
		 			      "<rdf:Description rdf:about=\"info:fedora/"+pid+"/"+dsid+"\">" +
							rdf +	
		 			     "</rdf:Description>"+
		 			      stream+
		 			     "</rdf:RDF>";
	 			    try {
	 					Repository.modifyDatastreamByValue(pid, "RELS-INT", "text/xml", new String(rdf.getBytes("UTF-8"),"UTF-8"));
	 				} catch (Exception ex) {
						File f= File.createTempFile("temp", ".tmp");	    					
	     	            FileOutputStream fos = new FileOutputStream(f.getAbsolutePath());
						BufferedWriter out = new BufferedWriter(new OutputStreamWriter( fos, "UTF-8" ) );
						out.write(rdf);
						out.close();
	 					Repository.addDatastream(pid, "RELS-INT",  "RDF Statements about the "+dsid+" stream", "X", "text/xml", f);
	 					f.delete(); 					 					
	 		 		}
	 					 				
	 					
	 		} catch (Exception e) {
	 			e.printStackTrace();
	 		} finally {
	 		}

	 	}
	   
}
