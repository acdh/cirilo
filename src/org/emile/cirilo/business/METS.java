package org.emile.cirilo.business;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.URL;
import java.util.*;
import java.net.URLConnection;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.emile.cirilo.Common;
import org.emile.cirilo.ServiceNames;
import org.emile.cirilo.User;
import org.emile.cirilo.ecm.repository.Repository;
import org.emile.cirilo.ecm.repository.FedoraConnector.Relation;
import org.emile.cirilo.ecm.templates.TemplateSubsystem;
import org.emile.cirilo.utils.ImageTools;
import org.emile.cirilo.utils.Split;
import org.emile.cirilo.utils.eXist;
import org.emile.cirilo.business.Scp;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.transform.JDOMResult;
import org.jdom.transform.JDOMSource;
import org.jdom.xpath.XPath;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Resource;
import org.xmldb.api.modules.XMLResource;
import org.xmldb.api.modules.BinaryResource;

import com.sun.media.imageioimpl.plugins.tiff.TIFFImageWriter;
import com.sun.media.imageioimpl.plugins.tiff.TIFFImageWriterSpi;

import voodoosoft.jroots.core.CPropertyService;
import voodoosoft.jroots.core.CServiceProvider;
import voodoosoft.jroots.dialog.CDefaultGuiAdapter;

public class METS {

	private Document mets;
	private Document viewer;
	private File file;
	private String collection;
	private Format format;
	private XMLOutputter outputter;
	private FileWriter logger;
	private String PID;
	private TemplateSubsystem temps;
	private String URI;
	private User user;
	private boolean onlyValidate;
	private boolean mode;
	private CPropertyService props;
	
	
	public METS(FileWriter logger, boolean validate, boolean mode) {		
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
		} catch (Exception e) {}
	}
	
    public boolean set (String file, boolean eXist) {
    	try {
    		SAXBuilder builder = new SAXBuilder();
			this.PID = "";
    		this.viewer = null;
 		if (!eXist) {
    			this.file = new File (file);
    			this.collection="";
    			if (this.file.exists()) { 
    				this.mets = builder.build( this.file );
    			}   
    			return this.file.exists();
    		} else {
    			eXist eX = new eXist(file);
    			org.xmldb.api.base.Collection collection = DatabaseManager.getCollection( URI + eX.getCollection(), user.getExistUser(), user.getExistPasswd() );
    			XMLResource res = (XMLResource) collection.getResource(eX.getStream());
    	        this.mets = builder.build( new StringReader( (String) res.getContent()));
    	        collection.close();
    	        this.collection = file;
    	        return true;
    		}
		} catch (Exception e) { return false;}
	}
  
    public boolean set (String stream) {
    	try {
    		SAXBuilder builder = new SAXBuilder();
    		this.viewer = null;
			this.collection="";
    		this.mets =builder.build(new StringReader(stream));
			this.PID = "";
   	        return true;
		} catch (Exception e) { 
			e.printStackTrace();
			return false;
		}
	}
    
 	public String toString() { 		
		return (this.outputter.outputString(this.mets).replaceAll("this:PID", this.PID).replaceAll("this:FEDORA", user.getUrl())); 
	}
	
	public String getName() { 
		try {
			return !this.collection.isEmpty() ? this.collection : this.file.getCanonicalPath();
		} catch (Exception e) { return ""; }
	}
	
	public String getPID() { 
		try {
			XPath xpath = XPath.newInstance( "//mods:mods/mods:identifier[@type='urn']" );
			xpath.addNamespace( Common.xmlns_mods );  		
			Element idno = (Element) xpath.selectSingleNode( mets );
			if (idno != null) {
				String s = idno.getTextNormalize();
	            this.PID = s.startsWith(Common.INFO_FEDORA) ? s.substring(Common.INFO_FEDORA.length()) : s;
			}
		} catch (Exception e) {}
		return this.PID;
	}

	public void setPID(String pid) { 
		try {
			XPath xpath = XPath.newInstance( "//mods:mods/mods:identifier[@type='urn']" );
			xpath.addNamespace( Common.xmlns_mods );
			Element idno = (Element) xpath.selectSingleNode( mets );
			if (idno == null) {
				idno = new Element ("identifier", Common.xmlns_mods );
				idno.setAttribute("type", "urn");
				idno.setText(Common.INFO_FEDORA+pid);
				xpath = XPath.newInstance("//mods:mods");
				xpath.addNamespace( Common.xmlns_mods );
				Element anchor = (Element) xpath.selectSingleNode( mets );
				anchor.addContent(idno);								
				this.PID = pid;
			} else {
				if (this.mode) { 
					idno.setText(pid);
					this.PID = pid;
				}
			}
		} catch (Exception e) {}
	}
		
	public boolean isValid() {
		try  {
			XPath xpath = XPath.newInstance( "/");		
			xpath.addNamespace( Common.xmlns_viewer );
		    if ( this.mets.getRootElement().getNamespace() == Common.xmlns_viewer)
			{
		    	Element structure = this.mets.getRootElement().getChild("structure", Common.xmlns_viewer );
		    	if (structure.getChild("div", Common.xmlns_viewer) == null) {
		    		Element div = new Element("div",Common.xmlns_viewer);
		    		File[] images = this.file.getParentFile().listFiles(new JPGFilter());
					for (int i = 0; i < images.length; i++) {
						Element page = new Element("page",Common.xmlns_viewer);
						page.setAttribute("href","file:///"+images[i].getAbsolutePath(), Common.xmlns_xlink);
						div.addContent (page);
					}
		    		structure.addContent(div);
		    	}
	    		this.viewer = this.mets;
		    	
				byte[] stylesheet = null;
				try {
					stylesheet =  Repository.getDatastream("cirilo:"+user.getUser(), "TOMETS" , "");
				} catch (Exception ex) {
					stylesheet =  Repository.getDatastream("cirilo:Backbone", "TOMETS" , "");					
				}
				
		        System.setProperty("javax.xml.transform.TransformerFactory",  "net.sf.saxon.TransformerFactoryImpl");  

		        Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(new StringReader(new String(stylesheet))));
		  	    JDOMSource in = new JDOMSource(this.mets);
		        JDOMResult out = new JDOMResult();
		        transformer.transform(in, out);

		        System.setProperty("javax.xml.transform.TransformerFactory",  "org.apache.xalan.processor.TransformerFactoryImpl");
		  		SAXBuilder builder = new SAXBuilder();
	
    	        this.mets = builder.build( new StringReader( outputter.outputString(out.getResult())) );	

    	        
			}
			
			xpath = XPath.newInstance( "/mets:mets" );
			xpath.addNamespace( Common.xmlns_mets );
			if ( xpath.selectSingleNode( this.mets ) == null) return false;
			return true;
		} catch (Exception e) {
			return false;}
	}

	public List getChildren(String path) {
		try {		
			XPath xpath = XPath.newInstance(path);
			xpath.addNamespace( Common.xmlns_mets );
			List nodes = (List) xpath.selectNodes( this.mets );
			return nodes;
		} catch (Exception e) { return null;}
	}
 	

	public boolean addChild(String path, String name, String text) {
		try {		
			XPath xpath = XPath.newInstance(path);
			xpath.addNamespace( Common.xmlns_mets );
			Element anchor = (Element) xpath.selectSingleNode( mets );
			Element child = new Element (name, Common.xmlns_mets );
			child.setText(text);
			anchor.addContent(child);
			return true;			
		} catch (Exception e) {return false;}
	}

	public boolean write() {
		try {
		    String p = props.getProperty("user", "METS.RefreshSource"); 
		    if (p != null && p.equals("1")) {
		    	if (!onlyValidate)  {
		    		if (this.collection.isEmpty()) {
		    			FileOutputStream fos = new FileOutputStream( this.file.toString() );
		    			BufferedWriter out = new BufferedWriter(new OutputStreamWriter( fos, "UTF-8" ) );
		    			out.write(outputter.outputString(this.mets).replaceAll("o:self", this.PID));		    			
		    			out.close();
		    		} else {
		    			eXist eX = new eXist (this.collection);
		    			org.xmldb.api.base.Collection coll = DatabaseManager.getCollection( URI + eX.getCollection(), user.getExistUser(), user.getExistPasswd() );
		    			XMLResource res = (XMLResource) coll.getResource(eX.getStream());
		    			res.setContent(new String(outputter.outputString(this.mets).replaceAll("o:self", this.PID).getBytes("UTF-8")));
		    			coll.storeResource(res);
		    			coll.close();
		    		}
		    	}
		    }
		    if (this.viewer != null) {
		    	Element idno = this.viewer.getRootElement().getChild("idno", Common.xmlns_viewer );
		    	if (idno!= null) {
		    		idno.setText(this.PID);	
		    	} else {
		    		idno = new Element("idno", Common.xmlns_viewer );
		    		idno.setText(this.PID);
		    		this.viewer.getRootElement().addContent(2,idno);
		    	}
    	        FileOutputStream fos = new FileOutputStream( this.file.toString() );
    			BufferedWriter os = new BufferedWriter(new OutputStreamWriter( fos, "UTF-8" ) );
    			os.write(outputter.outputString(this.viewer));
    			os.close();		    		
		    }		    
			return true;
		} catch (Exception e) {return false;}
	}	


	   public void ingestImages() {	
		     try { 				
		    	Element fileGrp = null; 
		   	    List images = getChildren("//mets:fileGrp[@USE='DEFAULT']/mets:file");
		   	    List thumbs = getChildren("//mets:fileGrp[@USE='THUMBS']/mets:file");

		   	    if (images.size() > 0) {
		    		int i = 0;		    		
		    		if (thumbs.size() == 0) {
		    			fileGrp = new Element("fileGrp", Common.xmlns_mets);
		    			fileGrp.setAttribute("USE", "THUMBS");
		    		}
		    		for (Iterator iter = images.iterator(); iter.hasNext();) {
		    			try {
		    				Element e = (Element) iter.next();
				      		String id = e.getAttributeValue("ID");
				      		i++;
				      		id = id == null ? "IMG."+new Integer(i).toString() : id;
				      		String mimetype = e.getAttributeValue("MIMETYPE");
				      	    Element ch = e.getChild("FLocat", Common.xmlns_mets);
				      		if (ch.getAttributeValue("LOCTYPE").equals("URL")) {
				      			File f = null;
				      			String url = ch.getAttributeValue("href", Common.xmlns_xlink);
				      			if (mimetype == null) {
				      				if ( url.indexOf(".tif") > -1 )
				      					mimetype ="image/tiff"; 
				      				else
				      					mimetype ="image/jpeg"; 
				      			}      											      			
				      			if (url.indexOf("http://") == -1) {				      				
				      				if (url.startsWith(Common.INFO_FEDORA)) {
				      					String s = url.substring(Common.INFO_FEDORA.length());
				      					String[] AS;
                                        AS = s.split("/");
                                        if (AS.length == 2) {
                                        	ch.setAttribute("href", user.getUrl()+"/objects/"+AS[0]+"/datastreams/"+AS[1]+"/content", Common.xmlns_xlink);
                                        }	
				      				} else {
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
				      						f= f.createTempFile("temp", ".tmp");	    					
				      						byte[] data = (byte[]) res.getContent();		    					
				      						FileOutputStream fos = new FileOutputStream(f);
				      						fos.write(data);
				      						fos.flush();
				      						fos.close();
				      					}
				      					coll.close();
				      				}		      			
				      				if (f != null && f.exists()){
      	 				    			BufferedImage img = ImageIO.read(f);			      				                		
	      	 				    		try {
	      	 				    			Element fcontent= new Element("FContent",Common.xmlns_mets);
	      	 				    			Element xmldata= new Element("xmlData",Common.xmlns_mets);
	      	 				    			Element x= new Element("xmpmeta",Common.xmlns_xmp);
	      	 				    			Element pixelx= new Element("PixelXDimension",Common.xmlns_exif);
	      	 				    			Element pixely= new Element("PixelYDimension",Common.xmlns_exif);
	      	 				    			pixelx.setText(new Integer(img.getWidth()).toString());
	      	 				    			pixely.setText(new Integer(img.getHeight()).toString());
	      	 				    			x.addContent(pixelx);
	      	 				    			x.addContent(pixely);
	      	 				    			xmldata.addContent(x);
	      	 				    			fcontent.addContent(xmldata);  									
	      	 				    			ch.getParentElement().addContent(fcontent);
	      	 				    		} catch (Exception q) {}
				      					if (!onlyValidate) {
				      						while(!Repository.exist(this.PID)) {}
				      						if (Repository.exist(this.PID)) {
			      								if (mimetype.equals("image/tiff") && !user.getIIPSUser().isEmpty()) {
			      									TIFFImageWriterSpi imageWriterSpi = new TIFFImageWriterSpi();
			      									TIFFImageWriter imageWriter = (TIFFImageWriter)imageWriterSpi.createWriterInstance();

			      									File tp = File.createTempFile("temp", ".tmp");
			      									ImageOutputStream out = new FileImageOutputStream(tp);
			      									imageWriter.setOutput(out);
			      									
			      									org.emile.cirilo.business.PTIFConverter.pyramidGenerator(imageWriter, img, 256, 256);
			      									out.close();
			      									String ref = null;
			      									Scp s = new Scp();
			      									if (s.connect()) {
			      										ref = s.put(tp, this.PID, id);
			      										s.disconnect();
			      										ref = "http://"+user.getIIPSUrl()+"/iipsrv?FIF="+ref+"&hei=900&cvt=jpeg";
			      										try {
			      											Repository.addDatastream(this.PID, id,  "Facsimile", mimetype, ref);
			      										} catch (Exception eq) {
			         										try {
			          											Repository.modifyDatastream(this.PID, id, mimetype, "M", ref);
			          										} catch (Exception ep) {}          											
			      										}
				      									ch.setAttribute("href", ref, Common.xmlns_xlink);			      	
			      									}
			      									tp.delete();
			      								 } else {
			      									try {				      								
			      										Repository.addDatastream(this.PID, id,  "Facsimile", "M", mimetype, f);
			      									} catch (Exception eq) {
			      										try {
			      											Repository.modifyDatastream(this.PID, id, mimetype, "M", f);
			      										} catch (Exception ep) {}
			      									}
			      									ch.setAttribute("href", user.getUrl()+"/objects/"+this.PID+"/datastreams/"+id+"/content", Common.xmlns_xlink);			      									
			      								}
					      						
				      							
				      							if ( i == 1 ) {
					      							File thumb = File.createTempFile( "temp", ".tmp" );
				      								ImageTools.createThumbnail( f, thumb, 100, 80, Color.lightGray );
					      	 				    	Repository.modifyDatastream(this.PID, "THUMBNAIL", "image/jpeg", "M", thumb);
					      	 				    	thumb.delete();
					      						}
				      						}
				      					} else {
				      						if (logger != null && this.PID != null) logger.write( new java.util.Date()  +" Objekt '"+this.PID+"' konnte nicht gefunden werden\n");
				      						return;
				      					}
				      					try {
				      						if (!this.collection.isEmpty()) f.delete();
				      					} catch (Exception q) {}
				      				} else {
				      					if (logger != null && this.PID != null) logger.write( new java.util.Date()  +" Bilddatei  '"+url+"' für Objekt '"+this.PID+"' konnte nicht gefunden werden\n");
				      				}
				      			}
				      			}	
	      						if (thumbs.size() == 0) {
	      							boolean temp = false;
	      							File thumb = File.createTempFile( "temp", ".tmp" );
	      							String tid = "TBN."+new Integer(i).toString();
	      							if (f == null) {
	      								f = File.createTempFile( "temp", ".tmp" );
	      								temp = true;
	      								byte [] image = Repository.getDatastream(this.PID, id, (String) null);
	      								FileOutputStream fos = new FileOutputStream(f.getAbsolutePath());
	      								fos.write(image);
	      								fos.close();
	      							}	
	      							try {
	      								ImageTools.createThumbnail( f, thumb, 150, 150, Color.lightGray );
	      								Repository.addDatastream(this.PID, tid, "Thumbnail", "M", mimetype, thumb);
	      							} catch (Exception eq) {
	      								try {
	      									Repository.modifyDatastream(this.PID, tid, mimetype, "M", thumb);
	      								} catch (Exception ep) {}
	      							}
      	 				    		
      	 				    		Element file = new Element("file",Common.xmlns_mets);
      	 				    		file.setAttribute("ID", tid);
      	 				    		file.setAttribute("MIMETYPE","image/jpeg");
      	 				    		Element flocat = new Element("FLocat",Common.xmlns_mets);
      	 				    		flocat.setAttribute("LOCTYPE", "URL");
      	 				    		flocat.setAttribute("href", user.getUrl()+"/objects/"+this.PID+"/datastreams/"+tid+"/content", Common.xmlns_xlink);
      	 				    		file.addContent(flocat);
      	 				    		fileGrp.addContent(file);
      	 				    		
      	 			    			XPath xpath = XPath.newInstance( "//mets:structMap[@TYPE='PHYSICAL']/mets:div[@ID='PHY.1']/mets:div[mets:fptr/@FILEID='"+id+"']" );
      	 			    			xpath.addNamespace( Common.xmlns_mets );
      	 			    			Element div = (Element) xpath.selectSingleNode( mets );
      	 			    			Element fptr = new Element("fptr", Common.xmlns_mets);
      	 			    			fptr.setAttribute("FILEID", tid);
      	 			    			div.addContent(fptr);

      	 			    			if (temp) f.delete();
      	 				    		thumb.delete();      	 				    		

	      						
	      						}					      											      						
				      		}				      		
				      	} catch (Exception eq) {
				      		if (logger != null) logger.write(new java.util.Date()  +" "+eq.getLocalizedMessage()+"\n");
				      	}
		    		}
		    		if (thumbs.size() == 0) {
		    			XPath xpath = XPath.newInstance( "//mets:fileSec" );
		    			xpath.addNamespace( Common.xmlns_mets );
		    			Element fileSec = (Element) xpath.selectSingleNode( mets );
	    				fileSec.addContent(fileGrp);
		    		}

		    	}
		   	    
		   	    if (thumbs.size() > 0) {
		    		int i = 0;
		    		for (Iterator iter = thumbs.iterator(); iter.hasNext();) {
		    			try {
		    				Element e = (Element) iter.next();
				      		String id = e.getAttributeValue("ID");
				      		id = id == null ? "TBN."+new Integer(++i).toString() : id;
				      		String mimetype = e.getAttributeValue("MIMETYPE");
				      		mimetype = mimetype == null ? "image/jpeg" : mimetype;
				      	    Element ch = e.getChild("FLocat", Common.xmlns_mets);
				      		if (ch.getAttributeValue("LOCTYPE").equals("URL")) {
				      			String url = ch.getAttributeValue("href", Common.xmlns_xlink);
				      			if (url.indexOf("http://") == -1) {
				      				File f = null;
				      				if (url.startsWith(Common.INFO_FEDORA)) {
				      					String s = url.substring(Common.INFO_FEDORA.length());
				      					String[] AS;
                                        AS = s.split("/");
                                        if (AS.length == 2) {
                                        	ch.setAttribute("href", user.getUrl()+"/objects/"+AS[0]+"/datastreams/"+AS[1]+"/content", Common.xmlns_xlink);
                                        }	
				      				} else {
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
				      						f= f.createTempFile("temp", ".tmp");	    					
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
				      						while(!Repository.exist(this.PID)) {}
				      						if (Repository.exist(this.PID)) {
				      							i++;
				      							File thumb = File.createTempFile( "temp", ".tmp" );
				      							try {
				      								if( e.getAttribute("resize") != null) {
				      									ImageTools.createThumbnail( f, thumb, 150, 150, Color.lightGray );
				      								    Repository.addDatastream(this.PID, id, "Thumbnail", "M", mimetype, thumb);
				      								} else {
				      								    Repository.addDatastream(this.PID, id, "Thumbnail", "M", mimetype, f);
				      								}    
								      			} catch (Exception eq) {
				      								try {
				      									Repository.modifyDatastream(this.PID, id, mimetype, "M", f);
				      								} catch (Exception ep) {}
				      							}
				      	 				    	thumb.delete();
				      							ch.setAttribute("href", user.getUrl()+"/objects/"+this.PID+"/datastreams/"+id+"/content", Common.xmlns_xlink);
				      						}
				      					} else {
				      						if (logger != null && this.PID != null) logger.write( new java.util.Date()  +" Objekt '"+this.PID+"' konnte nicht gefunden werden\n");
				      						return;
				      					}
				      					try {
				      						if (!this.collection.isEmpty()) f.delete();
				      					} catch (Exception q) {}
				      					continue;
				      				} else {
				      					if (logger != null && this.PID != null) logger.write( new java.util.Date()  +" Thumbnail  '"+url+"' für Objekt '"+this.PID+"' konnte nicht gefunden werden\n");
				      				}
				      			}
				      			}	
				      		}				      		
				      	} catch (Exception eq) {
				      		if (logger != null) logger.write(new java.util.Date()  +" "+eq.getLocalizedMessage()+"\n");
				      	}
		    		}
		    	}

		   	    
		   	    List divs = getChildren("//mets:div[@TYPE='page']");
		    	if (divs.size() > 0) {
		    		for (Iterator iter = divs.iterator(); iter.hasNext();) {
		    			try {
		    				Element e = (Element) iter.next();
		    				Element fptr = e.getChild("fptr", Common.xmlns_mets);
		    				String ID = fptr.getAttributeValue("FILEID");
		    				XPath xpath = XPath.newInstance("//mets:file[@ID='"+ID+"']");
		    				xpath.addNamespace( Common.xmlns_mets );
                            Element file = (Element) xpath.selectSingleNode(this.mets);
                            Element flocat = file.getChild("FLocat", Common.xmlns_mets);
		    				String href = flocat.getAttributeValue("href",Common.xmlns_xlink);
		    				if (e.getAttributeValue("CONTENTIDS") == null) e.setAttribute("CONTENTIDS", href);
		    			} catch (Exception eq) {}
		    		}	
		    	}

		    } catch (Exception e) {}    
   }	
	
   public void createMapping(String pid, CDefaultGuiAdapter moGA) 
	{
		try {
			if (!onlyValidate){
				byte[] url =  Repository.getDatastream(pid != null ? pid : this.PID, "DC_MAPPING" , "");

				SAXBuilder builder = new SAXBuilder(); 						 
				URLConnection con = new URL (new String(url)).openConnection();
				con.setUseCaches(false);
		    	Document dc =builder.build(con.getInputStream());
				MDMapper m = new MDMapper(this.PID,outputter.outputString(dc));
				
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

								
				Repository.modifyDatastreamByValue(this.PID, "DC", "text/xml", m.transform( this.mets));
				
				url =  Repository.getDatastream(pid != null ? pid : this.PID, "BIBTEX_MAPPING" , "");
				con = new URL (new String(url)).openConnection();
				con.setUseCaches(false);
		    	Document mapping = builder.build(con.getInputStream());
				m = new MDMapper(this.PID,outputter.outputString(mapping));
				Repository.modifyDatastreamByValue(this.PID, "BIBTEX", "text/xml", m.transform( this.mets));

			}
					
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
   
	 class JPGFilter implements FilenameFilter
	 {
	  public boolean accept( File f, String s )
	  {
	    return s.toLowerCase().endsWith( ".jpg" );
	  }
	}
	   
}
