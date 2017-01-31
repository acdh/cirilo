package org.emile.cirilo.business;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

import org.emile.cirilo.Common;
import org.emile.cirilo.ServiceNames;
import org.emile.cirilo.User;
import org.emile.cirilo.ecm.repository.Repository;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;
import org.json.JSONObject;
import org.geonames.Toponym;
import org.geonames.WebService;
import org.apache.log4j.Logger;

import voodoosoft.jroots.core.CPropertyService;
import voodoosoft.jroots.core.CServiceProvider;
import voodoosoft.jroots.dialog.CDefaultGuiAdapter;

public class EDM {

	private static Logger log = Logger.getLogger(EDM.class);

	private Document edm;
	private Format format;
	private XMLOutputter outputter;
	private SAXBuilder builder;
	private CPropertyService props;
	private String account;
	private Document persons;
	private Document places;

	public void init(User user) {		
		try {
			props = (CPropertyService) CServiceProvider.getService(ServiceNames.PROPERTIES);
	 		account = props.getProperty("user","TEI.LoginName");

			format = Format.getRawFormat();
			format.setEncoding("UTF-8");
			outputter = new XMLOutputter(format);
			this.builder = new SAXBuilder();
			
      	    try {persons = builder.build(user.getUrl()+"/objects/cirilo%3ABackbone/datastreams/PERSONS/content");} catch (Exception q) {persons = null;}
	    	try {places = builder.build(user.getUrl()+"/objects/cirilo%3ABackbone/datastreams/PLACES/content");} catch (Exception q) {places = null;}

			WebService.setUserName(account);

		} catch (Exception e) {
		  	log.error(e.getLocalizedMessage(),e);		  			
		}
	}

	public EDM(User user, String stream) {		
		try {
			init(user);
			set(stream);
		} catch (Exception e) {
		  	log.error(e.getLocalizedMessage(),e);		  			
		}
	}

	public EDM(User user) {		
		try {
			init(user);
		} catch (Exception e) {
		  	log.error(e.getLocalizedMessage(),e);		  			
		}
	}

  
    public boolean set(String stream) {
    	try {
    		this.edm = builder.build(new StringReader(stream));
   	        return true;
		} catch (Exception e) { 
		  	log.error(e.getLocalizedMessage(),e);		  
			return false;
		}
	}

    public boolean set(Document edm) {
    	try {
    		this.edm = edm;
   	        return true;
		} catch (Exception e) { 
		  	log.error(e.getLocalizedMessage(),e);		  
			return false;
		}
	}
    
 	public String toString() {
 		normalizePersons();
 		normalizePlaces();
	    return this.outputter.outputString(this.edm); 
	}
	
 	public Document get() {
 		return this.edm;
 	}

    public void save() {  
    	try {
    		if (persons != null) Repository.modifyDatastreamByValue("cirilo:Backbone", "PERSONS", "text/xml", outputter.outputString(persons));
    		if (places != null) Repository.modifyDatastreamByValue("cirilo:Backbone", "PLACES", "text/xml", outputter.outputString(places));
    	} catch (Exception e) {}	
    }	

	public void normalizePersons() {
		
		String stream = null;
		
		if (persons == null) return;
		
		try {
			XPath xpath = XPath.newInstance("//edm:Agent[contains(@rdf:about,'/gnd/')]");
			xpath.addNamespace(Common.xmlns_edm);
			xpath.addNamespace(Common.xmlns_skos);
			xpath.addNamespace(Common.xmlns_rdf);
			xpath.addNamespace(Common.xmlns_rdaGr2);
			
			List nodes = (List) xpath.selectNodes( this.edm );

			if (nodes.size() > 0) {
				for (Iterator iter = nodes.iterator(); iter.hasNext();) {
					try {
						Element e = (Element) iter.next();
						String preferredName = null;
						String id = e.getAttributeValue("about",Common.xmlns_rdf);
						XPath qpath = XPath.newInstance("//person[@xml:id='"+id+"']");
						qpath.addNamespace(Common.xmlns_xml);
	    				Element person = (Element) qpath.selectSingleNode( persons );
                        if (person == null) {
        		    		char[] buff = new char[1024];
                        	int n;
        		    		StringWriter sw = new StringWriter();
        		    		
        		    		URL url = new URL("http://hub.culturegraph.org/entityfacts/"+id.substring(id.indexOf("gnd/")+4));
        		    		try {
        		    			BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
        		    			try {
        		    				while ((n = br.read(buff)) != -1) { sw.write(buff, 0, n); }
        		    				stream = sw.toString();
        		    			} catch (Exception io) {}
        		    			finally {
        		    				sw.close();
        		    				br.close();
        		    			}
                                JSONObject json = new JSONObject(stream);
                                preferredName = json.getString("preferredName");
                                                         
        		    		} catch (Exception p) {}        		    		
                        	person = new Element ("person");                            
                        	person.setAttribute("id", id, Common.xmlns_xml);
                        	Element name = new Element ("name");
                        	name.setAttribute("lang","de",Common.xmlns_xml);
                        	name.setText(preferredName);
                        	person.addContent(name);
                        	persons.getRootElement().addContent(person);                        	
                        } else {
                        	preferredName = person.getChildText("name");	
                        }
                        e.getChild("prefLabel", Common.xmlns_skos).setText(preferredName);
                        
					} catch (Exception q) {log.debug(q.getLocalizedMessage(),q);}
				}
			}	
			
		} catch (Exception e) {}	
		
	}
    
	public void normalizePlaces() {

		if (places == null) return;
		
		try {
			XPath xpath = XPath.newInstance("//edm:Place[contains(@rdf:about,'geonames.org')]");
			xpath.addNamespace(Common.xmlns_edm);
			xpath.addNamespace(Common.xmlns_skos);
			xpath.addNamespace(Common.xmlns_rdf);
			xpath.addNamespace(Common.xmlns_wgs84_pos);

			List nodes = (List) xpath.selectNodes( this.edm );

			if (nodes.size() > 0) {
				for (Iterator iter = nodes.iterator(); iter.hasNext();) {
					try {
						Element e = (Element) iter.next();
						String id = e.getAttributeValue("about",Common.xmlns_rdf).replaceAll("www\\.","") ;
						log.debug("Resolving "+id+" against geonames.org"); 
						XPath qpath = XPath.newInstance("//place[@xml:id='"+id+"']");
						qpath.addNamespace(Common.xmlns_xml);
						Element place = (Element) qpath.selectSingleNode( places );
						if (place == null) {
                        	place = new Element ("place");
                        	place.setAttribute("id", id, Common.xmlns_xml);
                        	Element name = new Element ("name");
        					Document toponym  = builder.build(id+"/about.rdf");
    						XPath tpath = XPath.newInstance("//gn:*[@xml:lang='de']");
    						tpath.addNamespace(Common.xmlns_gn);
    						Element alt = (Element) tpath.selectSingleNode( toponym );
    						if (alt == null) {
    							tpath = XPath.newInstance("//gn:name");
        						tpath.addNamespace(Common.xmlns_gn);
        						alt = (Element) tpath.selectSingleNode( toponym );
    						} else {
    							name.setAttribute("lang","de",Common.xmlns_xml);
    						}
    						if (alt != null) {
    							name.setText(alt.getTextNormalize());    							
    						} else {
    							name.setText(e.getChildText("prefLabel",Common.xmlns_skos));
    						}    						
                        	place.addContent(name);
                        	places.getRootElement().addContent(place);
						} 
                    	e.getChild("prefLabel", Common.xmlns_skos).setText(place.getChild("name").getText());
                    	
    					Toponym toponym = WebService.get(new Integer(id.substring(id.indexOf("org/") + 4)).intValue(), null, null);
    					e.getChild("lat",Common.xmlns_wgs84_pos).setText(new Double(toponym.getLatitude()).toString());
    					e.getChild("long",Common.xmlns_wgs84_pos).setText(new Double(toponym.getLongitude()).toString());
    					log.debug("lat: "+new Double(toponym.getLatitude()).toString()+" long: "+new Double(toponym.getLongitude()).toString());
 
                    	
					} catch (Exception q) {
						log.error(q.getMessage());
					}
				}
			}	

		
		} catch (Exception e) {}	
		
	 }
	
	 public void refresh(String pid) 
	 {
			try {
					byte[] url =  Repository.getDatastream( pid , "DC_MAPPING" , "");
				
					SAXBuilder builder = new SAXBuilder(); 		
					URLConnection con = new URL (new String(url)).openConnection();
					con.setUseCaches(false);
					Document mapping = builder.build(con.getInputStream());
					MDMapper m = new MDMapper(pid,outputter.outputString(mapping));
					
					org.jdom.Document dc = builder.build( new StringReader (m.transform(this.edm) ) );							
										
					dc = Common.validate(dc);
					Repository.modifyDatastreamByValue(pid, "DC", "text/xml", outputter.outputString(dc));
					
						
			} catch (Exception e) {
				log.error(e.getLocalizedMessage(),e);				      					             								
			}
		}

 }
