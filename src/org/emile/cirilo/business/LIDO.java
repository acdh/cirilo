package org.emile.cirilo.business;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.*;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.emile.cirilo.business.MDMapper;
import org.emile.cirilo.business.Topos;
import org.emile.cirilo.Common;
import org.emile.cirilo.ServiceNames;
import org.emile.cirilo.User;
import org.emile.cirilo.ecm.repository.Repository;
import org.emile.cirilo.ecm.repository.FedoraConnector.Relation;
import org.emile.cirilo.ecm.templates.TemplateSubsystem;
import org.emile.cirilo.utils.eXist;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Attribute; 
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.transform.JDOMResult;
import org.jdom.transform.JDOMSource;
import org.jdom.xpath.XPath;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Resource;
import org.xmldb.api.modules.XMLResource;
import org.emile.cirilo.utils.ImageTools;
import org.emile.cirilo.business.IIIFFactory;
import org.geonames.Toponym;
import org.geonames.ToponymSearchCriteria;
import org.geonames.ToponymSearchResult;
import org.geonames.WebService;

import voodoosoft.jroots.core.CPropertyService;
import voodoosoft.jroots.core.CServiceProvider;
import voodoosoft.jroots.dialog.CDefaultGuiAdapter;

public class LIDO {

    private static Logger log = Logger.getLogger(LIDO.class);

    private static int cc = 0;
	private Document lido;
	private String raw;
	private File file;
	private String collection;
	private Format format;
	private XMLOutputter outputter;
	private FileWriter logger;
	private String PID;
	private TemplateSubsystem temps;
	private String URI;
	private User user;
	private SAXBuilder builder;
	private boolean onlyValidate;
	private boolean mode;
	private CPropertyService props;
	private String xuser;

	public LIDO(FileWriter logger, boolean validate, boolean mode) {		
		try {
			temps = (TemplateSubsystem) CServiceProvider.getService(ServiceNames.TEMPLATESUBSYSTEM);
			props = (CPropertyService) CServiceProvider.getService(ServiceNames.PROPERTIES);
			user = (User) CServiceProvider.getService(ServiceNames.CURRENT_USER);
			URI = user.getExistUrl();
			format = Format.getRawFormat();
			format.setEncoding("UTF-8");
			outputter = new XMLOutputter(format);
			this.logger = logger;
			this.onlyValidate = validate;
			this.mode = mode;	
			this.builder = new SAXBuilder();
			this.xuser = user.getUser();
		} catch (Exception e) {
		  	log.error(e.getLocalizedMessage(),e);		  
		}
	}


	public void setUser (String u) {this.xuser = u;}
	
    public boolean set (String file, boolean eXist) {
    	try {
 			this.PID = "";
 			if (!eXist) {
		    	this.file = new File (file);
		    	this.collection="";
		    	if (this.file.exists()) { 
		    		this.lido = builder.build( this.file );
		    	}
    			return this.file.exists();
    		} else {
    			eXist eX = new eXist(file);
    			org.xmldb.api.base.Collection collection = DatabaseManager.getCollection( URI + eX.getCollection(), user.getExistUser(), user.getExistPasswd() );
    			XMLResource res = (XMLResource) collection.getResource(eX.getStream());
    	        this.lido = builder.build( new StringReader( (String) res.getContent()));
    	        collection.close();
    	        this.collection = file;
    	        return true;
    		}
		} catch (Exception e) { 
		  	log.error(e.getLocalizedMessage(),e);		  
			return false;
		}
	}
  
    public boolean set (String stream) {
    	try {
    		SAXBuilder builder = new SAXBuilder();
			this.collection="";
    		this.lido =builder.build(new StringReader(stream));
			this.PID = "";
   	        return true;
		} catch (Exception e) { 
		  	log.error(e.getLocalizedMessage(),e);		  
			return false;
		}
	}

    public boolean get (String pid) {
    	try {
    		SAXBuilder builder = new SAXBuilder();
    		this.lido =builder.build(new StringReader(new String(Repository.getDatastream(pid, "LIDO_SOURCE",""))));
			this.PID = pid;
   	        return true;
		} catch (Exception e) { 
		  	log.error(e.getLocalizedMessage(),e);		  
			return false;
		}
	}
    
 	public String toString() {
	    	return this.outputter.outputString(this.lido); 
	}
	
	public String getName() { 
		try {
			return !this.collection.isEmpty() ? this.collection : this.file.getCanonicalPath();
		} catch (Exception e) {
		  	log.error(e.getLocalizedMessage(),e);		  
		  	return "";
		}
	}
	
	public String getPID() { 
		try {
			XPath xpath = XPath.newInstance("//lido:lidoRecID[@lido:type='PID']");
			xpath.addNamespace( Common.xmlns_lido );
			Element idno = (Element) xpath.selectSingleNode( this.lido );
			if (idno != null) {
				String s = idno.getTextNormalize();
	            this.PID = s.startsWith(Common.INFO_FEDORA) ? s.substring(Common.INFO_FEDORA.length()) : s;
			}
		} catch (Exception e) {
		  	log.error(e.getLocalizedMessage(),e);		  
		  	return "";
		}
		this.PID =  Common.normalize(this.PID);
		return this.PID;
	}
	
	public void setPID(String pid) { 
		try {

			XPath xpath = XPath.newInstance("//lido:lidoRecID[@lido:type='PID']");
			xpath.addNamespace( Common.xmlns_lido );
			Element idno = (Element) xpath.selectSingleNode( this.lido );			
			if (idno == null) {
				Element root = this.lido.getRootElement();
				if (!pid.startsWith("o:")) pid= "o:"+pid;
				Element child = new Element ("lidoRecID", Common.xmlns_lido);
				child.setText(Common.normalize(pid));
				child.setAttribute("type", "PID");
				root.addContent(0,child);								
			} else {
				if (this.mode) { 
					idno.setText(Common.normalize(pid));
				}
			}
			this.raw = outputter.outputString(this.lido);
			this.PID = Common.normalize(pid);
			
		} catch (Exception e) {
		  	log.error(e.getLocalizedMessage(),e);		  
		}
	}

		
	public boolean isValid() {
		try {
				
				XPath xpath = XPath.newInstance( "/lido:lido" );
				xpath.addNamespace( Common.xmlns_lido );
				if ( xpath.selectSingleNode( this.lido ) == null ) { 
					xpath = XPath.newInstance( "/lido:lidoWrap" );
					xpath.addNamespace( Common.xmlns_lido );
					if ( xpath.selectSingleNode( this.lido ) == null ) return false;
				}

				return true;
		
		} catch (Exception e) {
		  	log.error(e.getLocalizedMessage(),e);		  		
			return false; 
		}
	}


	public boolean write(boolean mode) {
		try {

			if (!onlyValidate)  {
				if (this.collection.isEmpty()) {
					if  (mode && this.raw !=null) {
						FileOutputStream fos = new FileOutputStream( this.file.toString() );
						BufferedWriter out = new BufferedWriter(new OutputStreamWriter( fos, "UTF-8" ) );
						if (mode) out.write(new String(outputter.outputString(this.lido).getBytes("UTF-8"),"UTF-8")); 
						else if (this.raw !=null) out.write(this.raw);	
						out.close();
					}
				} else {
					if  (mode && this.raw !=null) {
						eXist eX = new eXist (this.collection);
						org.xmldb.api.base.Collection coll = DatabaseManager.getCollection( URI + eX.getCollection(), user.getExistUser(), user.getExistPasswd() );
						XMLResource res = (XMLResource) coll.getResource(eX.getStream());
						if (mode) res.setContent(new String(outputter.outputString(this.lido).getBytes("UTF-8"),"UTF-8"));
						else if (this.raw !=null) res.setContent(this.raw);
						coll.storeResource(res);
						coll.close();
					}		
				}
			}
			return true;			
		} catch (Exception e) { 
			return false;
		}
	}	
	
	public void ingestImages() {	
		   
		     try { 			
		    	ResourceBundle	resb =(ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);
 
		   	    List images = getChildren("//lido:resourceWrap/lido:resourceSet[contains(lido:resourceRepresentation/lido:linkResource/@lido:formatResource,'image/')]");
		    	if (images.size() > 0) {
		    		int i = 1;
		    		IIIFFactory i3f = (IIIFFactory) CServiceProvider.getService(ServiceNames.I3F_SERVICE);
		    		i3f.delete(this.PID);
		    		for (Iterator iter = images.iterator(); iter.hasNext();) {
		    			try {
		    				Element parent = (Element) iter.next();
		    				Element e = parent.getChild("resourceRepresentation", Common.xmlns_lido).getChild("linkResource", Common.xmlns_lido);
					      	String url = e.getText();
					      	if (!url.startsWith(Common.INFO_FEDORA) && !url.startsWith("http://")) {
					      		Element resourceID =  parent.getChild("resourceID", Common.xmlns_lido);
					      		String id = null;
					      		if ( resourceID == null) {
					      			resourceID = new Element("resourceID", Common.xmlns_lido);
					      			resourceID.setAttribute("type", "IMAGE", Common.xmlns_lido);
					      			parent.addContent(0,resourceID);
					      		} else {
					      			id = resourceID.getText(); 					      		
					      		}	
					      		id = id == null ? "IMAGE."+new Integer(i).toString() : id;
				      			resourceID.setText(id);
				      			e.setText(user.getUrl()+"/"+this.PID+"/"+id);
					      		String mimetype = e.getAttributeValue("mimeType");
			      				File f = null;
			      				if (this.collection.isEmpty()) {
				      				url = url.startsWith("file:///") ? url.substring(8) : url;
			      					f = new File (url);
			      					if (!f.exists()) {
			      						f = new File (file.getParent()+System.getProperty( "file.separator" )+url); 		      			   
			      					}
			      				} else {	
				      				url = url.startsWith("exist:///") ? url.substring(9) : url;
				    	    		eXist eX = new eXist(this.collection);
				      				eXist eP = new eXist(url);
				    	    		String suffix = "";
				    	    		if (!eP.getCollection().isEmpty()) {
				    	    			suffix = "/"+eP.getCollection();
				    	    			url = eP.getStream();
				    	    		}
				    				org.xmldb.api.base.Collection coll = DatabaseManager.getCollection( URI + eX.getCollection()+suffix, user.getExistUser(), user.getExistPasswd() );
			    					Resource res = (Resource) coll.getResource(url);
				    				if (res != null) {		    				
				    					f= File.createTempFile("temp", ".tmp");	    					
				    					byte[] data = (byte[]) res.getContent();		    					
				    					FileOutputStream fos = new FileOutputStream(f);
				    					fos.write(data);
				    					fos.flush();
				    					fos.close();
				    				}
				    				coll.close();
				      			}		      			
				      			if (f != null && f.exists()){
				      				if (!onlyValidate) {
					      				if (mimetype == null)  {
					      					if ( url.indexOf(".tif") > -1 )
					      						mimetype ="image/tiff"; 
					      					else
					      						mimetype ="image/jpeg"; 
					      				}      								      							      						      					
				      					if (Common.exist(this.PID)) {
      										if (!Repository.exists(this.PID, id)) {				      								
	      										Repository.addDatastream(this.PID, id,  "Facsimile", "M", mimetype, f);
	      									} else {
      											Repository.modifyDatastream(this.PID, id, mimetype, "M", f);
	      									}				      						
			      						    if ( i == 1 ) {
				      							File thumb = File.createTempFile( "temp", ".tmp" );
			      								ImageTools.createThumbnail( f, thumb, 100, 80, Color.lightGray );
				      	 				    	Repository.modifyDatastream(this.PID, "THUMBNAIL", "image/jpeg", "M", thumb);
				      	 				    	thumb.delete();
				      						}
				      						i++;		      						
				      					}
					      			} else {
					      				MessageFormat msgFmt = new MessageFormat(resb.getString("objectnotfound"));
										Object[] args0 = {this.PID}; 
						  	  		    Common.log(logger, msgFmt.format(args0)+"\n");
										return;
					      			}
					      			try {
					      				if (!this.collection.isEmpty()) f.delete();
					      			} catch (Exception q) {}
					      			continue;
				      	    } else {
			      				MessageFormat msgFmt = new MessageFormat(resb.getString("imagenotfound"));
								Object[] args0 = {url, this.PID}; 
								Common.log(logger, msgFmt.format(args0)+"\n");

				           }
					     }
		    			} catch (Exception eq) {
		    			  	log.error(eq.getLocalizedMessage(),eq);		  
		    				Common.log(logger, eq);
		    				return;
		    			}
		    		}
		    	}
			  } catch (Exception e) {
				log.error(e.getLocalizedMessage(),e);		  
			  }	     
		   }

   
   public void createContexts(String account) {	   
  	try { 									
 	    List contexts = getChildren("//lido:*[contains(@lido:label,'info:fedora/context:')]"); 	    
 	    String href = null;
		 	    
 	    if (contexts.size() > 0 && !onlyValidate) {
 	    	int i =0;
	        HashMap<String,String> IsMemberOf = new HashMap<String,String> ();  
		    SAXBuilder parser = new SAXBuilder();
		    try {
		 	     Document doc = parser.build(user.getUrl()+"/objects/cirilo%3ABackbone/datastreams/STYLESHEETS/content");
 		    	 XPath xPath = XPath.newInstance( "/stylesheets/stylesheet[@type='STYLESHEET' and @model='cm:Context' and @state='default' and @owner='"+xuser+"']" );
 		   	     List stylesheets = (List) xPath.selectNodes( doc );	        		 	     
 		    	 if (stylesheets != null) {	        			    		 
 	 	    		 Iterator jter = stylesheets.iterator();
 	   				 Element el = (Element) jter.next();
 	   				 href = el.getAttributeValue("href");
 		    	 }    
    	     } catch (Exception q){
    			 log.error(q.getLocalizedMessage(),q);		  
    	     } 	        		  	        					        				
             try {
             	Document doc = parser.build(user.getUrl()+"/objects/cirilo%3ABackbone/datastreams/"+xuser.toUpperCase()+"/content");
 	            XPath xPath = XPath.newInstance( "/stylesheets/stylesheet[@type='STYLESHEET' and @model='cm:Context' and @state='default']" );
 	            List stylesheets = (List) xPath.selectNodes( doc );	        		 	     
           	    if (stylesheets != null) {	        			    		 
 	            	Iterator jter = stylesheets.iterator();
 	            	Element el = (Element) jter.next();
 	       			href = el.getAttributeValue("href");
 	            }
		    } catch (Exception q){
			  	log.error(q.getLocalizedMessage(),q);		     	
		    } 
             
            String context =  "info:fedora/cirilo:Context" + (Repository.exist("cirilo:Context."+xuser) ? "."+xuser : "");
	        for (Iterator iter = contexts.iterator(); iter.hasNext();) {
	        	try {
	        		Element e = (Element) iter.next();
	        		String target = e.getAttributeValue("label",Common.xmlns_lido);
	        		target = target.startsWith(Common.INFO_FEDORA) ? target.substring(12) : target; 
        			log.debug("Creating "+target);
        			if (!Repository.exist(target)) {  				
	        			String title = Common.itrim(e.getText()); 	
	        			temps.cloneTemplate(context, account, target, title);	        	
	    			    Common.genQR(user, target);
	    			} else {
	        			log.debug("Context already exists");
	        		}
	        		IsMemberOf.put(target,target);
	        	} catch (Exception q) { 
	    		  	log.error(q.getLocalizedMessage(),q);		  
	        		continue;	
	        	}
	        }
            if (IsMemberOf.size() > 0) {
    			java.util.List  <org.emile.cirilo.ecm.repository.FedoraConnector.Relation>relations = Repository.getRelations(this.PID,Common.isMemberOf);						
                log.debug("Deleting object relations");
    			for (Relation r : relations) {
  				    Repository.purgeRelation(Common.INFO_FEDORA+this.PID,Common.isMemberOf, r.getTo());
    	        }	   
                log.debug("Inserting object relations");
            	for ( String ct : IsMemberOf.values() ) {						
            		Repository.addRelation(Common.INFO_FEDORA+this.PID, Common.isMemberOf, Common.INFO_FEDORA+ct);
               	}
            }
 	    }
   	 } catch (Exception e) {
	   try {
		   log.error(e.getLocalizedMessage(),e);		  		   
		   Common.log(logger, e);
  	   } catch (Exception eq) {
  	   }            
     }
   
   }
   
   public void resolveGeoNameID(String account) {	   
	   
    WebService.setUserName(account);
    ToponymSearchCriteria searchCriteria = new ToponymSearchCriteria();
    HashMap<Integer,Topos> normdata = new HashMap<Integer,Topos> ();  

    try {

    	
	    String s = props.getProperty("user", "LIDO.OnlyGeonameID"); 	    
		s = (s != null && s.equals("1")) ? "[contains(lido:placeID/@lido:source,'geonames.org') or contains(lido:placeID/@lido:source,'geonameID')]" : "[not(@lido:geographicalEntity) or (@lido:geographicalEntity != 'cirilo:ignore')]";

    	
	    List places = getChildren("//lido:place"+s);
	    cc = 0;
		List nodes = getChildren("//@lido:corresp[contains(.,'GID')]");
		for (Iterator jter = nodes.iterator(); jter.hasNext();) {
			try {
				Attribute at = (Attribute) jter.next();
				at.getParent().removeAttribute("corresp", Common.xmlns_lido);
			} catch (Exception q) {
			  	log.error(q.getLocalizedMessage(),q);		  
			}
		}
	 	    
	    if (places.size() > 0 && !onlyValidate) {
		        for (Iterator iter = places.iterator(); iter.hasNext();) 
		        {
				        	
				         try {	
				        	Element place = (Element) iter.next();
				        	Element placeID = place.getChild("placeID", Common.xmlns_lido);
				        	Element namePlaceSet = place.getChild("namePlaceSet", Common.xmlns_lido);
				        	String appellationValue = namePlaceSet.getChildText("appellationValue", Common.xmlns_lido);

				        	if (placeID == null )
				        	{
				        		if (!appellationValue.isEmpty()) {		        			
				        			searchCriteria.setQ(appellationValue);
				        			ToponymSearchResult searchResult = WebService.search(searchCriteria);
				        			for (Toponym toponym : searchResult.getToponyms()) {
				        				Topos t =  normdata.get(new Integer(toponym.getGeoNameId()));
				        				if ( t == null) {
				        					t = new Topos(new Integer(toponym.getGeoNameId()).toString(),
				        							toponym.getName(),
				        							toponym.getCountryName(),
				        							new Double(toponym.getLatitude()).toString(),
				        							new Double(toponym.getLongitude()).toString(),
				        							toponym.getFeatureCode(),
				        							++cc
				        							);
				        					normdata.put(new Integer(toponym.getGeoNameId()), t);
				        				}
				        				namePlaceSet.setAttribute("corresp","#"+t.getXMLID(), Common.xmlns_lido);
				        				break;
				        			}		
				        		}
				        	} else {
				        		String ns = placeID.getAttributeValue("source", Common.xmlns_lido);
				        		if (ns.contains("geonameID") || ns.contains("geonames.org")) {
					        		String geoNameID = placeID.getText();
				        			Topos t =  normdata.get(new Integer(geoNameID));
				        			if ( t == null) {
				        				Toponym toponym = WebService.get(new Integer(geoNameID).intValue(), null, null);
		    		        					t = new Topos(new Integer(toponym.getGeoNameId()).toString(),
		    		        							toponym.getName(),
		    		        							toponym.getCountryName(),
		    		        							new Double(toponym.getLatitude()).toString(),
		    		        							new Double(toponym.getLongitude()).toString(),
		    		        		    				toponym.getFeatureCode(),
		    		        		    				++cc
		    		        							);
		    		        					normdata.put(new Integer(toponym.getGeoNameId()), t);
				        			}  
				        			namePlaceSet.setAttribute("corresp", "#"+t.getXMLID(), Common.xmlns_lido);
				        		}
				        	} 
					       
				         } catch (Exception o) {
				 		  	log.error(o.getLocalizedMessage(),o);		  
				         }   
				        	
				      }
		        		      
	 	    } 
    		    	

	
	    	XPath xpath = XPath.newInstance( "//lido:objectDescriptionWrap/lido:objectDescriptionSet[@lido:type='cirilo:normalizedPlaceNames']" );
			xpath.addNamespace( Common.xmlns_lido );
			Element e = (Element) xpath.selectSingleNode( this.lido ); 

			if ( e != null ) e.getParent().removeContent(e);

			if (normdata.size() > 0) {
			            
				Element objectDescriptionSet = new Element("objectDescriptionSet", Common.xmlns_lido);
				objectDescriptionSet.setAttribute("type", "cirilo:normalizedPlaceNames", Common.xmlns_lido);
				Element descriptiveNoteValue = new Element("descriptiveNoteValue", Common.xmlns_lido);
				objectDescriptionSet.addContent(descriptiveNoteValue);
				
				Element textClass = new Element("textClass", Common.xmlns_tei_p5);
				Element keywords = new Element("keywords", Common.xmlns_tei_p5);
				Element list = new Element("list", Common.xmlns_tei_p5);
				
				keywords.addContent(list);
				textClass.addContent(keywords);
				descriptiveNoteValue.addContent(textClass);
			
				
				for (Topos t : normdata.values()) 
				{
					Element item = new Element("item", Common.xmlns_tei_p5);
					item.setAttribute("ana",t.getFeature());
					item.setAttribute("id",t.getXMLID());
				
					Element name = new Element("name", Common.xmlns_tei_p5);
					name.setAttribute("type","placeName");
					name.setAttribute("ref","http://geonames.org/"+t.getID());
					name.setText(t.getName());
					
 					Element location = new Element("seg", Common.xmlns_tei_p5);
 					location.setAttribute("type","location");
 					location.setText(t.getLongitude()+","+t.getLatitude());
					item.addContent(name);

	                String q = t.getFeature(); 					
 					if (q.startsWith("PPL") || q.startsWith("ADM") || q.startsWith("AREA")) {
 						Element country = new Element("seg", Common.xmlns_tei_p5);
 						country.setAttribute("type","country");
 						country.setText(t.getCountry());
 						item.addContent(country);
 					}
			
					item.addContent(location);
					list.addContent(item);						
				}

				xpath = XPath.newInstance( "//lido:objectDescriptionWrap");
				e = (Element) xpath.selectSingleNode( this.lido );
				e.addContent(0, objectDescriptionSet);
								
			}

    	} catch (Exception e) {
		  	log.error(e.getLocalizedMessage(),e);		  
	   	 }	   
    
   }

	public void interfereTerms() {
		
		URL url; 
		try {
									
			XPath xpath = XPath.newInstance("//t:textClass/t:keywords[@scheme]");
			xpath.addNamespace( Common.xmlns_tei_p5 );
			List keywords = (List) xpath.selectNodes( this.lido );			
						
			for (Iterator thes = keywords.iterator(); thes.hasNext();) {
				try
				{
				HashMap<String,String> CONCEPTS = new HashMap<String,String>();
				Element o = (Element) thes.next();
				String scheme = o.getAttributeValue("scheme");
				scheme+=scheme.endsWith("/") ? "" : "/";
				List namespaces = o.getAdditionalNamespaces();
				
				int cc = 0;
				
				Iterator ins = namespaces.iterator();
				if (ins.hasNext()) 
				{
					Namespace n = (Namespace) ins.next();
					String pre = n.getPrefix();
					String uri = n.getURI();
					
					List elem = getChildren("//lido:conceptID[@lido:source='"+pre+"']");
					for (Iterator jter = elem.iterator(); jter.hasNext();) {
						try {
							Element e = (Element) jter.next();
							e.removeAttribute("type", Common.xmlns_lido);
						} catch (Exception q) {
						  	log.error(q.getLocalizedMessage(),q);		  
						}
					}
					elem = getChildren("//lido:conceptID[@lido:source='"+pre+"']");
	      	    	o.removeChildren("term", Common.xmlns_ntei_p5);

	      	        for (Iterator iter = elem.iterator(); iter.hasNext();) {
	      	    	    Element e = (Element) iter.next();
	      	    	    if (e instanceof Element) {
	      	    	    	if (CONCEPTS.get(uri+e.getText()) == null) CONCEPTS.put(uri+e.getText(),pre.toUpperCase()+"."+new Integer(++cc).toString());
	      	    	    	String id = CONCEPTS.get(uri+e.getText());
	      	    	    	e.setAttribute("type", "#"+id, Common.xmlns_lido);
	      	    	    }	      	    	    
	      	        }    
				}

				
	      	    for ( String el : CONCEPTS.keySet() ) {
			      		  Element term = new Element("term",Common.xmlns_ntei_p5);
			      		  term.setAttribute("ref",el);
			      		  term.setAttribute("id", CONCEPTS.get(el));
			      		  o.addContent(term);
	      	    }
			      	
				XPath qpath = XPath.newInstance("t:term");
				qpath.addNamespace(Common.xmlns_tei_p5);
				List terms = (List) qpath.selectNodes( o );	      	    

				if (terms != null) {
	      	                 	
		      	    	for (Iterator iter = terms.iterator(); iter.hasNext();) {
		      	    		try {
		      	    			Element e = (Element) iter.next();
		      	    			getBroader(scheme, e.getAttributeValue("ref"), e);	
		      	    		} catch (Exception ex) {
		      	    			continue;	
		      	    		}
		      	    	}
	      	    }	
	      	    
				} catch (Exception q) {
				  	log.error(q.getLocalizedMessage(),q);		  
			
				}
		   }	    

		} catch (Exception e) {
		  	log.error(e.getLocalizedMessage(),e);		  			
		}
	}
	

	private void getBroader (String scheme, String ref, Element root) {
		String context = "";
		String type;
		try { 

				XPath xpath = XPath.newInstance("//skos:Concept|//skos:TopConcept");
				xpath.addNamespace( Common.xmlns_skos );

				URL url = new URL(scheme + "methods/sdef:SKOS/getConceptByURI?uri=" + URLEncoder.encode(ref, "UTF-8"));

				URLConnection con = url.openConnection();
				con.setUseCaches(false);				
				Document skos = builder.build (con.getInputStream()); 
				Element concept = (Element) xpath.selectSingleNode(skos);
				type = concept.getName();
				xpath = XPath.newInstance("//skos:externalID");
				concept = (Element) xpath.selectSingleNode(skos);
                if (concept != null) {
                	context = concept.getText(); 
                }				
                xpath = XPath.newInstance("//skos:prefLabel");
				List prefLabels = (List) xpath.selectNodes(skos);
				root.setAttribute("type","skos:"+type);
				Boolean first = true;
    	    	for (Iterator iter = prefLabels.iterator(); iter.hasNext();) {
	    			Element s = (Element) iter.next();
	    			Element prefLabel = new Element ("term", Common.xmlns_ntei_p5);
	    			prefLabel.setText(s.getText());
	    			prefLabel.setAttribute("type","skos:prefLabel");
	    			prefLabel.setAttribute("lang",s.getAttributeValue("lang",Common.xmlns_xml), Common.xmlns_xml);
	    			root.addContent(prefLabel);
    	    	}
				xpath = XPath.newInstance("//skos:broader|//skos:broaderGeneric|//skos:broaderInstantive|//skos:broaderPartitive");
                List relations = (List) xpath.selectNodes(skos);
    	    	for (Iterator iter = relations.iterator(); iter.hasNext();) {
	    			Element s = (Element) iter.next();
	    			Element term = new Element ("term", Common.xmlns_ntei_p5);
	    			term.setAttribute("subtype","skos:"+s.getName());
	    			term.setAttribute("type","skos:Concept");
	    			term.setAttribute("ref", s.getAttributeValue("resource", Common.xmlns_rdf));
	    			getBroader(scheme, s.getAttributeValue("resource", Common.xmlns_rdf), term );
	    			root.addContent(term);
    	    	}
                			
			} catch (Exception e) {
			  	log.error(e.getLocalizedMessage(),e);		  			
			}								
	}
		

   public void createMapping(String pid, CDefaultGuiAdapter moGA) 
	{
		try {
			if (!onlyValidate) {
				byte[] url =  Repository.getDatastream(pid != null ? pid : this.PID, "DC_MAPPING" , "");
			
				SAXBuilder builder = new SAXBuilder(); 			
				URLConnection con = new URL (new String(url)).openConnection();
				con.setUseCaches(false);
				org.jdom.Document mapping = builder.build( con.getInputStream());			
				MDMapper m = new MDMapper(this.PID,outputter.outputString(mapping));
				builder = new SAXBuilder();
				
				org.jdom.Document dc = builder.build( new StringReader (m.transform(this.lido) ) );							
			
				if (moGA != null)  {
					Element root = dc.getRootElement();
					for (int i = 1; i < org.emile.cirilo.Common.DCMI.length; i++) {
						String s = (String) moGA.getText("jtf"+ org.emile.cirilo.Common.DCMI[i]);
						if (s.length() > 0) {
							StringTokenizer st = new StringTokenizer(s, "~");
							if (st.hasMoreTokens()) {
								while (st.hasMoreTokens()) {
									String p = Common.itrim(st.nextToken());
									Element e = new Element(
											org.emile.cirilo.Common.DCMI[i].toLowerCase(),
											org.emile.cirilo.Common.xmlns_dc);
									e.addContent(p);
									root.addContent(e);
								}
							}

						}
					}
				}
				
				dc = Common.validate(dc);				
				Repository.modifyDatastreamByValue(this.PID, "DC", "text/xml", outputter.outputString(dc));
				
			}
					
		} catch (Exception e) {
		  	log.error(e.getLocalizedMessage(),e);		  
		}
       finally {
        }
	}

	public List getChildren(String path) {
		try {		
			XPath xpath = XPath.newInstance(path);
			xpath.addNamespace( Common.xmlns_lido );
			List nodes = (List) xpath.selectNodes( this.lido );
			return nodes;
		} catch (Exception e) { return null;}
	}

   public void createRELS_INT(String pid) 
  	{

				String rdfs = null;
				SAXBuilder builder = new SAXBuilder();

				try {
					byte[] url =  Repository.getDatastream(pid != null ? pid : this.PID, "TORDF" , "");
					URLConnection con = new URL (new String(url)).openConnection();
					con.setUseCaches(false);
		    		Document doc =builder.build(con.getInputStream());
                    rdfs = outputter.outputString(doc);					
				} catch (Exception e) {
					try {
						Document doc  =builder.build(new StringReader(new String(Repository.getDatastream("cirilo:"+this.xuser, "LIDOtoRDF",""))));
                        rdfs = outputter.outputString(doc);
					} catch (Exception ex1) {
						try {
							Document doc  =builder.build(new StringReader(new String(Repository.getDatastream("cirilo:Backbone", "LIDOtoRDF",""))));
							rdfs = outputter.outputString(doc);
						} catch (Exception ex2) {
						  	log.error(ex2.getLocalizedMessage(),ex2);		  
						}
					}	
				}		

  				
				try {
									
					
					if (rdfs != null && rdfs.contains(":template")) {
					
						System.setProperty("javax.xml.transform.TransformerFactory",  "net.sf.saxon.TransformerFactoryImpl");  
						Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(new StringReader(rdfs)));
						JDOMSource in = new JDOMSource(this.lido);
						JDOMResult out = new JDOMResult();
						transformer.transform(in, out);
						System.setProperty("javax.xml.transform.TransformerFactory",  "org.apache.xalan.processor.TransformerFactoryImpl");
						
						File temp = File.createTempFile("tmp","xml");
						FileOutputStream fos = new FileOutputStream(temp);
						fos.write( outputter.outputString(out.getResult()).getBytes("UTF-8") );
						fos.close();
						TripleStoreFactory tf = new TripleStoreFactory();
						if (tf.getStatus()) {
							tf.update(temp, this.PID);
						}	
						tf.close();
																		
						if (!Repository.exists(this.PID, "RDF")) {				      								
  							Repository.addDatastream(this.PID, "RDF", "RDF Stream created by TORDF", "M", "text/xml", temp);
  						} else {
							Repository.modifyDatastream(this.PID, "RDF", "text/xml", "M", temp);
  						}

						temp.delete();
					}
			     } catch (Exception e) {
					 log.error(e.getLocalizedMessage(),e);		  
			     }		
  	}
 





	public void validate(String pid, CDefaultGuiAdapter moGA) 
	{
		try {
			String p;  
			String account;
			
			log.debug("Enter saving LIDO Object");
			if(this.PID != null && this.PID.startsWith("cirilo:")) return;
			log.debug("Creating contexts");
			p = props.getProperty("user", "LIDO.CreateContexts"); 
			if (p == null || p.equals("1")) createContexts(this.xuser);
			log.debug("Ingesting images");
			p = props.getProperty("user", "LIDO.IngestImages"); 
			if (p == null || p.equals("1")) ingestImages(); 
			log.debug("Creating DC Mapping");
			p = props.getProperty("user", "LIDO.DCMapping"); 
			if (p == null || p.equals("1")) createMapping(pid, moGA);
			log.debug("Resolving geonames.org ids");
			p = props.getProperty("user", "LIDO.ResolveGeoIDs"); 
			account = props.getProperty("user", "TEI.LoginName"); 
			if (p == null || p.equals("1")) resolveGeoNameID(account);
			log.debug("Extracting RDF triples");
			p = props.getProperty("user", "LIDO.SEMExtraction"); 
			if (p == null || p.equals("1")) createRELS_INT(null);
		    write(p == null || p.equals("1"));
		    log.debug("Resolving SKOS concepts");
			p = props.getProperty("user", "LIDO.ResolveSKOS"); 
			if (p == null || p.equals("1")) interfereTerms();
			log.debug("Finish saving LIDO Object");
			    
		} catch (Exception e) {
		  	log.error(e.getLocalizedMessage(),e);		  			
		}
	  		
		
	 }
	
	public void refresh() 
	{
		try {
			if(this.PID.startsWith("cirilo:")) return;		 
			createContexts(this.xuser);
			createRELS_INT(null);
		} catch (Exception e) {
		  	log.error(e.getLocalizedMessage(),e);		  			
		}	    
	 }	
		
}
