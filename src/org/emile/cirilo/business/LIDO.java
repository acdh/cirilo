package org.emile.cirilo.business;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.emile.cirilo.business.MDMapper;
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
import org.openrdf.repository.manager.RemoteRepositoryManager;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Resource;
import org.xmldb.api.modules.XMLResource;
import org.emile.cirilo.utils.ImageTools;
import org.geonames.Toponym;
import org.geonames.ToponymSearchCriteria;
import org.geonames.ToponymSearchResult;
import org.geonames.WebService;

import com.sun.media.imageioimpl.plugins.tiff.TIFFImageWriter;
import com.sun.media.imageioimpl.plugins.tiff.TIFFImageWriterSpi;

import voodoosoft.jroots.core.CPropertyService;
import voodoosoft.jroots.core.CServiceProvider;
import voodoosoft.jroots.dialog.CDefaultGuiAdapter;

import java.io.*;

import javax.xml.transform.Source;
import javax.xml.validation.*;

import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;

public class LIDO {

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
		} catch (Exception e) {}
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
			e.printStackTrace();
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
			e.printStackTrace();
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
			e.printStackTrace();
			return false;
		}
	}
    
 	public String toString() {
	    	return this.outputter.outputString(this.lido); 
	}
	
	public String getName() { 
		try {
			return !this.collection.isEmpty() ? this.collection : this.file.getCanonicalPath();
		} catch (Exception e) { return ""; }
	}
	
	public String getPID() { 
		try {
		} catch (Exception e) {}
		return this.PID;
	}

	public void setPID(String pid) {
		try {
			this.PID = pid;
		} catch (Exception e) {}
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
			return false; 
		}
	}

	public boolean write() {
		try {
			if (!onlyValidate)  {
				if (this.collection.isEmpty()) {
					FileOutputStream fos = new FileOutputStream( this.file.toString() );
					BufferedWriter out = new BufferedWriter(new OutputStreamWriter( fos, "UTF-8" ) );
					out.write(outputter.outputString(this.lido));
					out.close();
				} else {
					eXist eX = new eXist (this.collection);
					org.xmldb.api.base.Collection coll = DatabaseManager.getCollection( URI + eX.getCollection(), user.getExistUser(), user.getExistPasswd() );
					XMLResource res = (XMLResource) coll.getResource(eX.getStream());
					res.setContent(new String(outputter.outputString(this.lido).getBytes("UTF-8"),"UTF-8"));
					coll.storeResource(res);
					coll.close();
				}
			}
			return true;			
		} catch (Exception e) {return false;}
	}	

	
	
   public void ingestImages() {	
	   
     try { 									
	  } catch (Exception e) {
	  }	     
   }

   
   public void createContexts(String account) {	   
	   try { 									
   		
   		} catch (Exception e) {}
   }
   
   public void resolveGeoNameID(String account) {	   
	   
 	   	try { 		
 	   	 } catch (Exception e) {
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
								
				Repository.modifyDatastreamByValue(this.PID, "DC", "text/xml", outputter.outputString(dc));
				
			}
					
		} catch (Exception e) {
			e.printStackTrace();
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
				} catch (Exception ex0) {
					try {
						Document doc  =builder.build(new StringReader(new String(Repository.getDatastream("cirilo:"+this.xuser, "LIDOtoRDF",""))));
                        rdfs = outputter.outputString(doc);
					} catch (Exception ex1) {
						try {
							Document doc  =builder.build(new StringReader(new String(Repository.getDatastream("cirilo:Backbone", "LIDOtoRDF",""))));
							rdfs = outputter.outputString(doc);
						} catch (Exception ex2) {}
					}	
				}		

  				
				try {
									
		    		String ses = (String) props.getProperty("user", "sesame.server");
			        RemoteRepositoryManager repositoryManager = new RemoteRepositoryManager(ses == null ? Common.SESAME_SERVER : ses);
					repositoryManager.setUsernameAndPassword(xuser, user.getPasswd());
					repositoryManager.initialize();	 
					org.openrdf.repository.Repository repo = repositoryManager.getRepository("FEDORA"); 	
					repo.initialize(); 				    			
					org.openrdf.repository.RepositoryConnection scon = repo.getConnection();	 				    			
					scon.clear(new org.openrdf.model.impl.URIImpl(this.PID)); 							 							  				
					
					if (rdfs.contains(":template")) {
					
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
						scon.add(temp.getAbsoluteFile(), null, org.openrdf.rio.RDFFormat.RDFXML, new org.openrdf.model.impl.URIImpl(this.PID));
												
  						try {
  							Repository.addDatastream(this.PID, "RDF", "RDF Stream created by TORDF", "M", "text/xml", temp);
  						} catch (Exception eq) {
  							try {
  								Repository.modifyDatastream(this.PID, "RDF", "text/xml", "M", temp);
  							} catch (Exception ep) {}
  						}

						temp.delete();
					}
			     } catch (Exception ex) {
			    	 ex.printStackTrace();
			     }		
  	}
 





	public void validate(String pid, CDefaultGuiAdapter moGA) 
	{
		try {
			String account;
			if(this.PID != null && this.PID.startsWith("cirilo:")) return;
			createContexts(this.xuser);
			ingestImages(); 
			createMapping(pid, moGA);
			account = props.getProperty("user", "TEI.LoginName"); 
			resolveGeoNameID(account);
			createRELS_INT(null);
			write();
		} catch (Exception e) {
		}
	    
	 }
	
	public void refresh() 
	{
		try {
			if(this.PID.startsWith("cirilo:")) return;		 
			createContexts(this.xuser);
			createRELS_INT(null);
		} catch (Exception e) {
		}	    
	 }	
	

	
}
