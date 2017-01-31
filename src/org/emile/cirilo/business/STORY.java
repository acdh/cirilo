package org.emile.cirilo.business;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.*;

import org.apache.log4j.Logger;
import org.emile.cirilo.business.MDMapper;
import org.emile.cirilo.Common;
import org.emile.cirilo.ServiceNames;
import org.emile.cirilo.User;
import org.emile.cirilo.ecm.repository.Repository;
import org.emile.cirilo.utils.eXist;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Resource;
import org.xmldb.api.modules.XMLResource;
import org.emile.cirilo.utils.ImageTools;
import org.emile.cirilo.business.IIIFFactory;
import org.json.JSONObject;
import org.json.XML;

import voodoosoft.jroots.core.CPropertyService;
import voodoosoft.jroots.core.CServiceProvider;
import voodoosoft.jroots.dialog.CDefaultGuiAdapter;


public class STORY {

	private static Logger log = Logger.getLogger(STORY.class);

	private Document story;
	private File file;
	private String collection;
	private Format format;
	private XMLOutputter outputter;
	private FileWriter logger;
	private String PID;
	private String URI;
	private String raw;
	private User user;
	private SAXBuilder builder;
	private boolean onlyValidate;
	private boolean mode;
	private CPropertyService props;

	public STORY(FileWriter logger, boolean validate, boolean mode) {		
		try {
			props = (CPropertyService) CServiceProvider.getService(ServiceNames.PROPERTIES);
			user = (User) CServiceProvider.getService(ServiceNames.CURRENT_USER);
			URI = user.getExistUrl();
			format = Format.getRawFormat();
			format.setEncoding("UTF-8");
			outputter = new XMLOutputter(format);
			this.logger = logger;
			this.onlyValidate = validate;
			this.mode = mode;	
			this.raw = null;
			this.builder = new SAXBuilder();
		} catch (Exception e) {
      		log.error(e.getLocalizedMessage(),e);				      					   			
		}
	}


	public void setUser (String u) {}
	
    public boolean set (String file, boolean eXist) {
    	String stream = null;
		int n;
    	try {
 			this.PID = "";
 			if (!eXist) {
		    	this.file = new File (file);
		    	this.collection="";
		    	if (this.file.exists()) {
		    		char[] buff = new char[1024];
		    		StringWriter sw = new StringWriter();
	    			FileInputStream is = new FileInputStream(file);
	    			BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		    		try {
		                while ((n = br.read(buff)) != -1) { sw.write(buff, 0, n); }
		         		stream = sw.toString();
		    		} catch (Exception io) {
		    			sw.close();
		    			br.close();
		    		}
		    	}
    		} else {
    			eXist eX = new eXist(file);
    			org.xmldb.api.base.Collection collection = DatabaseManager.getCollection( URI + eX.getCollection(), user.getExistUser(), user.getExistPasswd() );
    			XMLResource res = (XMLResource) collection.getResource(eX.getStream());
    			stream = (String) res.getContent();
    	        collection.close();
    	        this.collection = file;
    		}
		} catch (Exception e) { 
      		log.error(e.getLocalizedMessage(),e);				      					   
			return false;
		}
    	try {
    		this.raw = stream;
    		if (stream.contains("\"storymap\":")) {
    			JSONObject json = new JSONObject(stream);
    			stream = XML.toString(json);
    	    	
    		}    
    		this.story = builder.build( new StringReader( stream ));
    		return true;
    	} catch (Exception e) {
      		log.error(e.getLocalizedMessage(),e);				      					   
    		return false;
    	}

	}
  
    public boolean set (String stream) {
    	try {
    		SAXBuilder builder = new SAXBuilder();
    		if (stream.contains("\"storymap\":")) {
    			JSONObject json = new JSONObject(stream);
    			stream = XML.toString(json);
    		}
			this.collection="";
    		this.story = builder.build(new StringReader(stream));
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
    		this.story = builder.build(new StringReader(new String(Repository.getDatastream(pid, "STORY",""))));
			this.PID = pid;
   	        return true;
		} catch (Exception e) { 
			log.error(e.getLocalizedMessage(),e);	
			return false;
		}
	}
    
 	public String toString() {
	    	return this.outputter.outputString(this.story); 
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
			XPath xpath = XPath.newInstance("/storymap/pid");
			Element idno = (Element) xpath.selectSingleNode( this.story );
			if (idno != null) {
				String s = idno.getTextNormalize();
	            this.PID = s.startsWith(Common.INFO_FEDORA) ? s.substring(Common.INFO_FEDORA.length()) : s;
			}
		} catch (Exception e) {
      		log.error(e.getLocalizedMessage(),e);				      					   		
		}
		return this.PID;
	}
	
	public void setPID(String pid) { 
		try {

			XPath xpath = XPath.newInstance("/storymap/pid");
			Element idno = (Element) xpath.selectSingleNode( this.story );			
			if (idno == null) {
				Element root = this.story.getRootElement();
				if (!pid.startsWith("o:")) pid= "o:"+pid;
				Element child = new Element ("pid");
				child.setText(pid);
				root.addContent(0,child);								
			} else {
				if (this.mode) { 
					idno.setText(pid);
				}
			}
			this.PID = pid;
			
		} catch (Exception e) {
      		log.error(e.getLocalizedMessage(),e);				      					   			
		}
	}

		
	public boolean isValid() {
		try {				
			return true;
		} catch (Exception e) {
      		log.error(e.getLocalizedMessage(),e);				      					   
			return false; 
		}
	}


	public boolean write(boolean mode) {
		try {

			if (!onlyValidate)  {
				if (mode) {
					if (this.raw.contains("\"storymap\":")) {
						JSONObject obj = XML.toJSONObject(outputter.outputString(this.story));
						this.raw = obj.toString(4);
					} else {    
						this.raw = outputter.outputString(this.story);
					}
				}	
				if (this.collection.isEmpty()) {
					if  (mode) {
						FileOutputStream fos = new FileOutputStream( this.file.toString() );
						BufferedWriter out = new BufferedWriter(new OutputStreamWriter( fos, "UTF-8" ) );
						out.write(new String(this.raw.getBytes("UTF-8"),"UTF-8")); 
						out.close();
					}
				} else {
					if  (mode) {
						eXist eX = new eXist (this.collection);
						org.xmldb.api.base.Collection coll = DatabaseManager.getCollection( URI + eX.getCollection(), user.getExistUser(), user.getExistPasswd() );
						XMLResource res = (XMLResource) coll.getResource(eX.getStream());
						res.setContent(new String(this.raw.getBytes("UTF-8"),"UTF-8"));
						coll.storeResource(res);
						coll.close();
					}		
				}
			}
			return true;			
		} catch (Exception e) {
      		log.error(e.getLocalizedMessage(),e);				      					   
      		return false;
      }
	}	
	
	public void ingestImages() {	
		   
		     try { 			
		    	ResourceBundle	resb =(ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);
 
		   	    List images = getChildren("//*[contains(.'file:///')]");
		    	if (images != null && images.size() > 0) {
		    		int i = 1;
		    		IIIFFactory i3f = (IIIFFactory) CServiceProvider.getService(ServiceNames.I3F_SERVICE);
		    		i3f.delete(this.PID);
		    		for (Iterator iter = images.iterator(); iter.hasNext();) {
		    			try {
		    				Element el = (Element) iter.next();
					      	String url = el.getText();
					      	if (!url.startsWith(Common.INFO_FEDORA) && !url.startsWith("http://")) {
					      		String id = el.getAttributeValue("id", Common.xmlns_xml);
					      		id = id == null ? "IMAGE."+new Integer(i).toString() : id;
				      			String mimetype = el.getAttributeValue("mimeType");
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
		    				Common.log(logger, eq);
		    				return;
		    			}
		    		}
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
				
				org.jdom.Document dc = builder.build( new StringReader (m.transform(this.story) ) );							
			
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
			List nodes = (List) xpath.selectNodes( this.story );
			return nodes;
		} catch (Exception e) { 
      		log.error(e.getLocalizedMessage(),e);				      					   
			return null;
		}
	}


	public void validate(String pid, CDefaultGuiAdapter moGA) 
	{
		try {
			if(this.PID != null && this.PID.startsWith("cirilo:")) return;
			ingestImages(); 
			createMapping(pid, moGA);
		    write(true);

		} catch (Exception e) {
      		log.error(e.getLocalizedMessage(),e);				      					   
		}
	  		
		
	 }
	
	public void refresh() 
	{
		try {
		} catch (Exception e) {
      		log.error(e.getLocalizedMessage(),e);				      					   
		}	    
	 }	
		
}
