package org.emile.cirilo.business;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

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
import org.emile.cirilo.business.Unzipper;
import org.emile.cirilo.business.Topos;
import org.emile.cirilo.business.CantusConverter;
import org.emile.cirilo.business.IIIFFactory;

import voodoosoft.jroots.core.CPropertyService;
import voodoosoft.jroots.core.CServiceProvider;
import voodoosoft.jroots.dialog.CDefaultGuiAdapter;

import org.geonames.*;

public class TEI {

    private static int cc = 0;
	private Document tei;
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
	private boolean isCustomized; 	
	private Element tmptree;
	private CPropertyService props;
	private String xuser;
	private Document intermedidate;
	
	public TEI(FileWriter logger, boolean validate, boolean mode) {		
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
			this.raw = null;
			this.xuser = user.getUser();
			this.intermedidate = null;
	} catch (Exception e) {}
	}

	public void setUser (String u) {this.xuser = u;}

    public boolean set (String file, boolean eXist) {
    	try {
			this.PID = "";
			this.intermedidate = null;
			if (!eXist) {
				this.file = new File (file);
    			
    			if (file.toLowerCase().contains(".docx")) {
                    try {
                    	String stylesheet = "tei/docx/from/docxtotei.xsl";
	    				Unzipper unzipper = new Unzipper();

	    				String tmpDir = new File(System.getProperty("java.io.tmpdir")).getAbsolutePath();
	    				String homeDir = new File(System.getProperty("user.home")).getAbsolutePath();
	    				
			            unzipper.unzip(file, tmpDir, "");
	                    SAXBuilder builder = new SAXBuilder();   
	                    File doc = new File(tmpDir+System.getProperty("file.separator")+"word"+System.getProperty("file.separator")+"document.xml"); 
	                    
	                    org.jdom.Document docx = builder.build (doc);		                    
                    
			            System.setProperty("javax.xml.transform.TransformerFactory",  "net.sf.saxon.TransformerFactoryImpl");  
			            TransformerFactory transformerFactory = TransformerFactory.newInstance();

				        StreamSource is = null; 
				        Transformer transformer = null;
		        		JDOMSource in = new JDOMSource(docx);
		        		JDOMResult out = new JDOMResult();

				        try {
				        	is =  new StreamSource(user.getUrl().substring(0,user.getUrl().lastIndexOf("/")+1)+stylesheet); 				            
				        	transformer = transformerFactory.newTransformer(is);
				            transformer.setParameter("word-directory", tmpDir);				            
			        		transformer.transform(in, out); 			        	
				        	} catch (Exception q) {
				        	try {
				        		String fp = tmpDir+System.getProperty("file.separator")+stylesheet;
				        		if (!(new File(fp)).exists()) fp = homeDir+System.getProperty("file.separator")+stylesheet;
				        		is =  new StreamSource(fp); 
				        		transformer = transformerFactory.newTransformer(is);
					            transformer.setParameter("word-directory", tmpDir);					            
				        		transformer.transform(in, out); 			        	
				        	} catch (Exception s) {
                                Common.log(logger,s);
				        		return false;
				        	}					        	
				        }
			            			            
		        		System.setProperty("javax.xml.transform.TransformerFactory",  "org.apache.xalan.processor.TransformerFactoryImpl");
		        		
		        		XMLOutputter outputter = new XMLOutputter();
	    				set(outputter.outputString(out.getResult()));
	    				
	    				XPath xpath = XPath.newInstance("//t:fileDesc/t:titleStmt/t:title");
	    				xpath.addNamespace( Common.xmlns_tei_p5 );
	    				Element title = (Element) xpath.selectSingleNode( tei );
	    				if (title.getTextTrim().isEmpty()) title.setText("Untitled");

	    				xpath = XPath.newInstance("//t:revisionDesc/t:listChange/t:change/t:name");
	    				xpath.addNamespace( Common.xmlns_tei_p5 );
	    				Element name = (Element) xpath.selectSingleNode( tei );
                        
	    				
	    				if (name != null && name.getText().toLowerCase().contains("cantus")) {
	    				   String variant = name.getText();
	    				   if (variant.contains(":")) variant = variant.substring(variant.indexOf(":")+1); else variant ="";
	    				   CantusConverter cc = new CantusConverter(variant);
	    				   this.intermedidate = cc.transform(this.tei);
	    				   if (this.intermedidate != null) {
	    					  this.intermedidate.getRootElement().addNamespaceDeclaration(Common.xmlns_cantus);  					  	    					  
    			              System.setProperty("javax.xml.transform.TransformerFactory",  "net.sf.saxon.TransformerFactoryImpl");  
	    			            
	    			          transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(new StringReader(Common.stylesheet)));
	    				      in = new JDOMSource(this.intermedidate);
	    				      out = new JDOMResult();
	    				      transformer.transform(in, out);	    				      
	    				      this.tei = builder.build( new StringReader(outputter.outputString(out.getResult())
		    				    		.replaceAll(" </seg>","</seg> ").replaceAll("</seg> ,","</seg>,").replaceAll("</seg> \\.","</seg>.")
		    			           		.replaceAll("</seg>([a-z])", "</seg> $1")));
	    				        		        
	    		        	  System.setProperty("javax.xml.transform.TransformerFactory",  "org.apache.xalan.processor.TransformerFactoryImpl");	    					  
	    				   }	  
	    				}
	    					    				
                        return true;		    				
                    } catch (Exception q){
                    	q.printStackTrace();
                        Common.log(logger,q);
                    	return false;
                    }   
    			} else if (file.toLowerCase().contains(".odt")) {
                    try {
	    				String tmpDir = new File(System.getProperty("java.io.tmpdir")).getAbsolutePath();
	    				Unzipper unzipper = new Unzipper();

	    			
			            unzipper.unzip(file, tmpDir, "");
	                    SAXBuilder builder = new SAXBuilder();                
	                    org.jdom.Document docx = builder.build (new File(tmpDir+System.getProperty("file.separator")+"content.xml"));		                    
                    
			            System.setProperty("javax.xml.transform.TransformerFactory",  "net.sf.saxon.TransformerFactoryImpl");  
			            TransformerFactory transformerFactory = TransformerFactory.newInstance();

			            StreamSource is =  new StreamSource(user.getUrl().substring(0,user.getUrl().lastIndexOf("/")+1)+"tei/odt/odttotei.xsl"); 				            
			            Transformer transformer = transformerFactory.newTransformer(is);
			            
		        		JDOMSource in = new JDOMSource(docx);
		        		JDOMResult out = new JDOMResult();
		        		transformer.transform(in, out); 			        	
		        		System.setProperty("javax.xml.transform.TransformerFactory",  "org.apache.xalan.processor.TransformerFactoryImpl");
		        		
		        		XMLOutputter outputter = new XMLOutputter();
	    				set(outputter.outputString(out.getResult()));
	    				
	    				XPath xpath = XPath.newInstance("//t:fileDesc/t:titleStmt/t:title");
	    				xpath.addNamespace( Common.xmlns_tei_p5 );
	    				Element title = (Element) xpath.selectSingleNode( tei );
	    				if (title.getTextTrim().isEmpty()) title.setText("Untitled");

                        return true;		    				
                    } catch (Exception q){
                        Common.log(logger,q);
                    	return false;
                    }                                            
    			} else {    		
    				this.collection="";
    				if (this.file.exists()) { 
    					this.tei = builder.build( this.file );
    				}   
    				return this.file.exists();
    			}
    		} else {
    			eXist eX = new eXist(file);
    			org.xmldb.api.base.Collection collection = DatabaseManager.getCollection( URI + eX.getCollection(), user.getExistUser(), user.getExistPasswd() );
    			XMLResource res = (XMLResource) collection.getResource(eX.getStream());
    	        this.tei = builder.build( new StringReader( (String) res.getContent()));
    	        collection.close();
    	        this.collection = file;
    	        return true;
    		}
		} catch (Exception e) { 
			return false;}
	}
  
  
    
    
    public boolean get (String pid) {
    	try {
    		SAXBuilder builder = new SAXBuilder();
    		this.tei =builder.build(new StringReader(new String(Repository.getDatastream(pid, "TEI_SOURCE",""))));
			this.PID = pid;
   	        return true;
		} catch (Exception e) { 
            Common.log(logger,e);
			return false;
		}
	}
    
    
    public boolean set (String stream) {
    	try {
    		SAXBuilder builder = new SAXBuilder();
			this.collection="";
			this.tei =builder.build(new StringReader(stream));
			this.PID = "";
   	        return true;
		} catch (Exception e) { 
            Common.log(logger,e);
			return false;
		}
	}
    
 	public String toString() {
 		   // removeEmpty();
	    	return this.outputter.outputString(this.tei); 
	}
	
	public String getName() { 
		try {
			return !this.collection.isEmpty() ? this.collection : this.file.getCanonicalPath();
		} catch (Exception e) { return ""; }
	}
	
	public String getPID() { 
		try {
			XPath xpath = XPath.newInstance("//t:fileDesc[1]/t:publicationStmt//t:idno[@type='PID']");
			xpath.addNamespace( Common.xmlns_tei_p5 );
			Element idno = (Element) xpath.selectSingleNode( tei );
			if (idno != null) {
				String s = idno.getTextNormalize();
	            this.PID = s.startsWith(Common.INFO_FEDORA) ? s.substring(Common.INFO_FEDORA.length()) : s;
			} else {
	   			String pid = tei.getRootElement().getAttributeValue("id", Common.xmlns_xml).toLowerCase();
                if (pid != null) {				
                	if (!pid.startsWith("o:")) pid= "o:"+pid;
                	xpath = XPath.newInstance("//t:fileDesc/t:publicationStmt");
                	xpath.addNamespace( Common.xmlns_tei_p5 );
                	Element anchor = (Element) xpath.selectSingleNode( tei );
                	Element child = new Element ("idno", Common.xmlns_ntei_p5 );
                	child.setText(Common.INFO_FEDORA+pid);
                	child.setAttribute("type", "PID");
                	anchor.addContent(child);
                	this.PID =pid;
              }   
			}
		} catch (Exception e) {}
		return this.PID;
	}

	public void setPID(String pid) { 
		try {
			XPath xpath = XPath.newInstance("//t:fileDesc[1]/t:publicationStmt//t:idno[@type='PID']");
			xpath.addNamespace( Common.xmlns_tei_p5 );
			Element idno = (Element) xpath.selectSingleNode( tei );
			if (idno == null) {
				try {
					String oid = tei.getRootElement().getAttributeValue("id", Common.xmlns_xml).toLowerCase();
					if (oid != null) pid = oid;				
					if (!pid.startsWith("o:")) pid= "o:"+pid;
				} catch (Exception u) {}
				xpath = XPath.newInstance("//t:fileDesc/t:publicationStmt");
				xpath.addNamespace( Common.xmlns_tei_p5 );
				Element anchor = (Element) xpath.selectSingleNode( tei );
				Element child = new Element ("idno", Common.xmlns_ntei_p5 );
				child.setText(pid);
				child.setAttribute("type", "PID");
				anchor.addContent(child);								
				this.raw = outputter.outputString(this.tei);
			} else {
				if (this.mode) { 
					idno.setText(pid);
				}
			}
			this.PID = pid;

		} catch (Exception e) {}
	}
		
	public boolean isValid() {
		try  {
			isCustomized = false;
			this.raw = null;
			
			XPath xpath = XPath.newInstance( "/t:TEI" );
			xpath.addNamespace( Common.xmlns_tei_p5 );
			if ( xpath.selectSingleNode( this.tei ) == null  ) { 
				xpath = XPath.newInstance( "/t:teiCorpus" );
				xpath.addNamespace( Common.xmlns_tei_p5 );
				if ( xpath.selectSingleNode( this.tei ) == null ) return false;
			}

	    	String p = props.getProperty("user", "TEI.Customization"); 
			if (p != null && p.equals("1")) { 
                transform();				
				isCustomized = true;
				SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
				Schema schema = factory.newSchema(new URL(Common.TEIP5SCHEMA));
				Validator validator = schema.newValidator();
				try {
					validator.validate(new JDOMSource(this.tei));			        
				} catch (Exception q) {
	       			Common.log(logger, "-----------------------\n"+outputter.outputString(this.tei)+"-----------------------\n");
	       			Common.log(logger, q);
					isCustomized = true;
					return false;
				}
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
   			Common.log(logger, e);   		 
			return false;}
	}
	

	public boolean transform() {
		byte[] stylesheet = null;
        try {
        	try {
        		stylesheet =  Repository.getDatastream("cirilo:"+xuser, "TOTEI" , "");
        	} catch (Exception ex) {
           		try { 
        		stylesheet =  Repository.getDatastream("cirilo:Backbone", "TOTEI" , "");
        		} catch (Exception q) {
           			Common.log(logger, q);   		 
        			return false;
        		}
          	}
        	
        	
        	if (new String(stylesheet).contains(":template")) {
        		System.setProperty("javax.xml.transform.TransformerFactory",  "net.sf.saxon.TransformerFactoryImpl");  

        		Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(new StringReader(new String(stylesheet, "UTF-8"))));
        	
        		JDOMSource in = new JDOMSource(this.tei);
        		JDOMResult out = new JDOMResult();
        		transformer.transform(in, out);
        	
        		System.setProperty("javax.xml.transform.TransformerFactory",  "org.apache.xalan.processor.TransformerFactoryImpl");
        		SAXBuilder builder = new SAXBuilder();

        		this.tei = builder.build( new StringReader( outputter.outputString(out.getResult())) );	

        		XPath xpath = XPath.newInstance("//x:include");
  				xpath.addNamespace( Common.xmlns_xinc);
         		
  				List includes = (List) xpath.selectNodes( this.tei );

  	    		for (Iterator iter = includes.iterator(); iter.hasNext();) {
  	    			Element parent = null;
  	    			try {
  	    				List a = null;
  	    				Element e = (Element) iter.next();
  	    				InputStream is = new URL(e.getAttributeValue("href")).openStream();
  	    				Document include = builder.build(is);
  	    				if (e.getAttributeValue("xpointer") != null) {
  	    		       		XPath qpath = XPath.newInstance("//*[@xml:id='"+e.getAttributeValue("xpointer")+"']");
  	    	  				Element xpointer = (Element) qpath.selectSingleNode(include);	
  	    	  				a = xpointer.getParentElement().cloneContent();
  	    				} else {
  	    					a = include.cloneContent();
  	    				} 	    				
  	    				parent = e.getParentElement();
  	    				int index = parent.indexOf(e);
  	    				parent.setContent(index, a);
  	    			} catch (Exception e) {  	    				
 	    				parent.removeChild("include", Common.xmlns_xinc);
 	    			}
  	    		}	
          		
        	}	

        } catch (Exception e) {
        	e.printStackTrace();
        	Common.log(logger, e);
        }
        	
       	return true;
	}
		
	
	public List getChildren(String path) {
		try {		
			XPath xpath = XPath.newInstance(path);
			xpath.addNamespace( Common.xmlns_tei_p5 );
			List nodes = (List) xpath.selectNodes( this.tei );
			return nodes;
		} catch (Exception e) { return null;}
	}
 	

	public boolean addChild(String path, String name, String text) {
		try {		
			XPath xpath = XPath.newInstance(path);
			xpath.addNamespace( Common.xmlns_tei_p5 );
			Element anchor = (Element) xpath.selectSingleNode( tei );
			Element child = new Element (name, Common.xmlns_ntei_p5 );
			child.setText(text);
			anchor.addContent(child);
			return true;			
		} catch (Exception e) {return false;}
	}

	public boolean write(boolean mode) {
		try {

			if (!onlyValidate)  {
				if (this.collection.isEmpty()) {
					if  (mode || (!isCustomized && this.raw !=null && this.file.toString().contains(".xml"))) {
						FileOutputStream fos = new FileOutputStream( this.file.toString() );
						BufferedWriter out = new BufferedWriter(new OutputStreamWriter( fos, "UTF-8" ) );
						if (mode) out.write(new String(outputter.outputString(this.tei).getBytes("UTF-8"),"UTF-8")); 
						else if (!isCustomized && this.raw !=null) out.write(this.raw);	
						out.close();
					}
				} else {
					if  (mode || (!isCustomized && this.raw !=null && this.file.toString().contains(".xml"))) {
						eXist eX = new eXist (this.collection);
						org.xmldb.api.base.Collection coll = DatabaseManager.getCollection( URI + eX.getCollection(), user.getExistUser(), user.getExistPasswd() );
						XMLResource res = (XMLResource) coll.getResource(eX.getStream());
						if (mode) res.setContent(new String(outputter.outputString(this.tei).getBytes("UTF-8"),"UTF-8"));
						else if (!isCustomized && this.raw !=null) res.setContent(this.raw);
						coll.storeResource(res);
						coll.close();
					}		
				}
			}
			return true;			
		} catch (Exception e) {return false;}
	}	

	public boolean regexer() {

        try {
        	SAXBuilder db = new SAXBuilder();	
		  	byte[] buf = Repository.getDatastream(this.PID, "REPLACEMENT_RULESET", "");
			URLConnection con = new URL (new String(buf)).openConnection();
			con.setUseCaches(false);
	    	Document rs = builder.build(con.getInputStream());
        	
    		XPath xpath = XPath.newInstance("//rs:regex");
			xpath.addNamespace( Common.xmlns_rs );
			List rules = (List) xpath.selectNodes( rs );
	        String t = outputter.outputString(this.tei);
			
		    if (rules.size() > 0 ) {
		    	int i = 0;
		        for (Iterator iter = rules.iterator(); iter.hasNext();) {
		        	i++;
		        	try {
		        		
		        		Element e = (Element) iter.next();
		        		String pattern = e.getAttributeValue("pattern");
		        		String s = "";
		        		List children = e.getChildren();
				        for (Iterator jter = children.iterator(); jter.hasNext();) {
			        		Element ch = (Element) jter.next();
			        		s = getRepl(s, ch);
				        }			        
                       
				        
				        Pattern p = Pattern.compile(pattern);				        
			        	Matcher m = p.matcher(t);
				        while (m.find()) {
						        String r = s;
				        		for (int j=1; j<=m.groupCount();j++) {
				        			r = r.replaceAll("$"+new Integer(j).toString().trim(), m.group(j));
			        			
				        		}
				        		t=m.replaceFirst(r);	
					        	m = p.matcher(t);
				        }
				        
		        			        		
		            } catch (Exception ex) { 
		        		continue;	
		        	}
		    		SAXBuilder builder = new SAXBuilder();
		        	this.tei = builder.build(new StringReader(t));
		        }
		    }
           			
        } catch (Exception ex) {       	
        }
		return true;
		

		
	}
	
	private String getRepl(String s, Element r) {
		
		List nodes = r.getChildren();
		s+="<"+r.getName();
		
		List attributes = r.getAttributes();
        for (Iterator iter = attributes.iterator(); iter.hasNext();) {
    		Attribute a = (Attribute) iter.next();
    		s+=" "+a.getName()+"=\""+a.getValue()+"\"";        	
        }
		
        s+=">";
        
	    if (nodes.size() > 0 ) {
	        for (Iterator iter = nodes.iterator(); iter.hasNext();) {
	        	try {	        		
	        		Element e = (Element) iter.next();
	        		s=getRepl(s, e);
	            } catch (Exception ex) {       	
	            }
            }
	    } else {
	    	s+=r.getText();
	    }
		s+="</"+r.getName()+">";
		return s;
	}
	

	public boolean removeEmpty() {
		try {
			List ee = getChildren("/t:TEI//t:*[not(@*) and not(*) and string-length(.) = 0 and not(name() = 'lb' or name() = 'pb')]");
			if (ee.size() > 0) {
				for (Iterator iter = ee.iterator(); iter.hasNext();) {
					try {
						Element e = (Element) iter.next();
						e.getParent().removeContent(e); 	
					} catch (Exception e) {}
				}
			}			
			return true;			
		} catch (Exception e) {return false;}
	}	
	
	
   public void ingestImages() {	
	   
     try { 	
    	ResourceBundle	resb =(ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);
	    	     	 
   	    List images = getChildren("//t:graphic");
    	if (images.size() > 0) {
    		int i = 1;
    		IIIFFactory i3f = (IIIFFactory) CServiceProvider.getService(ServiceNames.I3F_SERVICE);
    		i3f.delete(this.PID);  		
    		for (Iterator iter = images.iterator(); iter.hasNext();) {
    			try {
    				Element e = (Element) iter.next();
			      	String url = e.getAttributeValue("url");
			      	if (!url.startsWith(Common.INFO_FEDORA) && !url.startsWith("http://")) {
			      		String id = e.getAttributeValue("id", Common.xmlns_xml);
			      		id = id == null ? "IMAGE."+new Integer(i).toString() : id;
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
		      					while(!Repository.exist(this.PID)) {}
		      					if (Repository.exist(this.PID)) {
									if (!Repository.exists(this.PID, id)) {				      								
   										Repository.addDatastream(this.PID, id,  "Facsimile", "M", mimetype, f);
   									} else {
										Repository.modifyDatastream(this.PID, id, mimetype, "M", f);
   									}
		      						e.setAttribute("url", Common.INFO_FEDORA+this.PID);
		      						e.setAttribute("id", id, Common.xmlns_xml);		      						
		      						
	      						    if ( i == 1 ) {
		      							File thumb = File.createTempFile( "temp", ".tmp" );
		      							if (this.PID.contains("numis")) {
			      							ImageTools.createThumbnail( f, thumb, 150, 150, Color.lightGray );
		      							} else {		      							
		      								ImageTools.createThumbnail( f, thumb, 100, 80, Color.lightGray );
		      							}
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
   	    List attachments = getChildren("//t:list[@type='attachments']/t:item");
    	if (attachments.size() > 0) {
    		int i = 1;
    		for (Iterator iter = attachments.iterator(); iter.hasNext();) {
    			try {
    				Element e = (Element) iter.next();
			      	String url = e.getChild("ref", Common.xmlns_tei_p5).getAttributeValue("target");
			      	if (!url.startsWith(Common.INFO_FEDORA) && !url.startsWith("http://")) {
			      		String id = e.getChild("ref", Common.xmlns_tei_p5).getAttributeValue("id", Common.xmlns_xml);
			      		id = id == null ? "STREAM."+new Integer(i).toString() : id;
			      		String mimetype = e.getChild("ref", Common.xmlns_tei_p5).getAttributeValue("mimeType");
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
		      					while(!Repository.exist(this.PID)) {}
		      					if (Repository.exist(this.PID)) {
										if (!Repository.exists(this.PID, id)) {			      								
      										Repository.addDatastream(this.PID, id, e.getChildText("name", Common.xmlns_tei_p5), "M", mimetype, f);
      									} else {
   											Repository.modifyDatastream(this.PID, id, mimetype, "M", f);
      									}
		      						e.setAttribute("url", Common.INFO_FEDORA+this.PID);
		      						e.setAttribute("id", id, Common.xmlns_xml);		      						
		      						
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
	      				MessageFormat msgFmt = new MessageFormat(resb.getString("streamnotfound"));
						Object[] args0 = {url, this.PID}; 
						Common.log(logger,  msgFmt.format(args0)  ); 
		           }
			     }
    			} catch (Exception eq) {
    				Common.log(logger, eq);
    				return;
    			}
    		}
    	}	

	  } catch (Exception e) {
          Common.log(logger,e);
	  }	     
   }

   public void createContexts(String account) {	   
   	try { 									
 	    List contexts = getChildren("//t:ref[@type='context' or @type='container']"); 	    
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
    	     } catch (Exception e0){} 	        		  	        					        				
             try {
	            Document doc = parser.build(user.getUrl()+"/objects/cirilo%3ABackbone/datastreams/"+xuser.toUpperCase()+"/content");
 	            XPath xPath = XPath.newInstance( "/stylesheets/stylesheet[@type='STYLESHEET' and @model='cm:Context' and @state='default']" );
 	            List stylesheets = (List) xPath.selectNodes( doc );	        		 	     
           	    if (stylesheets != null) {	        			    		 
 	            	Iterator jter = stylesheets.iterator();
 	            	Element el = (Element) jter.next();
 	       			href = el.getAttributeValue("href");
 	            }
		    } catch (Exception e0){} 	        		  	        					        				
	        for (Iterator iter = contexts.iterator(); iter.hasNext();) {
	        	try {
	        		Element e = (Element) iter.next();
	        		String target = e.getAttributeValue("target");
	        		target = target.startsWith(Common.INFO_FEDORA) ? target.substring(12) : target; 
	        		target = target.replaceAll("[,;]","");
	        		if (!Repository.exist(target)) {  				
	        			String title = Common.itrim(e.getText()); 	
	        			if (Repository.exist("cirilo:Context."+xuser)) {
	        				temps.cloneTemplate("info:fedora/cirilo:Context."+xuser, account, target, title);	        				
	        			} else {	        			
	        				temps.cloneTemplate("info:fedora/cirilo:Context", account, target, title);
           				    if (href != null) Repository.modifyDatastream (target, "STYLESHEET", null, "R", href);
	        			}	
	        			Common.log(logger, "Context-Objekt '"+target+ "' wurde erstellt\n");
	        		}
	        		e.setAttribute("target", Common.INFO_FEDORA+target);
	        		IsMemberOf.put(target,target);
				    Common.genQR(user, target);
	        	} catch (Exception e) { 
	        		continue;	
	        	}
	        }
            if (IsMemberOf.size() > 0) {
    			java.util.List  <org.emile.cirilo.ecm.repository.FedoraConnector.Relation>relations = Repository.getRelations(this.PID,Common.isMemberOf);						
    	        for (Relation r : relations) {
  				    Repository.purgeRelation(Common.INFO_FEDORA+this.PID,Common.isMemberOf, r.getTo());
    	        }	   
            	for ( String ct : IsMemberOf.values() ) {						
            		Repository.addRelation(Common.INFO_FEDORA+this.PID, Common.isMemberOf, Common.INFO_FEDORA+ct);
               	}
            }
 	    }
   	 } catch (Exception e) {
	   try {
		   Common.log(logger, e);
  	   } catch (Exception eq) {
  	   }            
     }
   
   }

   
   public void resolveGeoNameID(String account) {	   
	   
	   	try { 			
		    String s = props.getProperty("user", "TEI.OnlyGeonameID"); 
 		    s = (s != null && s.equals("1")) ? "[(contains(@ref,'geonameID') or contains(@ref,'geonames.org')) and (not(@type) or @type != 'cirilo:ignore')]" : "";
            s = s.isEmpty() ? "[not(@type) or @type != 'cirilo:ignore']" : s;
 		    
 		    Element place;
	 	    List places = getChildren("//t:text//t:placeName"+s+"|//t:sourceDesc//t:placeName"+s);
	 	    WebService.setUserName(account);
	 	    cc = 0;
	 	    ToponymSearchCriteria searchCriteria = new ToponymSearchCriteria();
			List nodes = getChildren("//@key[contains(.,'GID')]");
			for (Iterator jter = nodes.iterator(); jter.hasNext();) {
				try {
					Attribute at = (Attribute) jter.next();
					at.getParent().removeAttribute("key");
				} catch (Exception eq) {
				}
			}
	 	    
	 	    if (places.size() > 0 && !onlyValidate) {
	 	    	int i =0;
		        HashMap<Integer,Topos> normdata = new HashMap<Integer,Topos> ();  
		        for (Iterator iter = places.iterator(); iter.hasNext();) 
		        {
		        		place = (Element) iter.next();
		        		Attribute key = place.getAttribute("ref"); 		        		
		        		if (key == null)
		        		{
		        			Element settlement = place.getChild("settlement", Common.xmlns_tei_p5);
		        			Element country = place.getChild("country", Common.xmlns_tei_p5);
		        			Element reg = place.getChild("reg", Common.xmlns_tei_p5);
		        			Element ref = place.getChild("ref", Common.xmlns_tei_p5);

		        			String placeName;
		        			placeName = (reg != null ? reg.getText() : place.getText() );
		        			placeName = (ref != null ? ref.getText() : placeName );
		        			if (settlement != null) placeName += "  "+ settlement.getText();
		        			if (country != null) placeName += " "+ country.getText();
			        		if (!placeName.isEmpty()) {		        			
			        			searchCriteria.setQ(placeName);
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
			        				place.setAttribute("key", "#"+t.getXMLID());
			        				break;
			        			}		
		        			}
		        		} else {
		        			String[] geoNameID = {"",""};
		        			
		        			if (key.getValue().startsWith("geonameID")) {
		        				geoNameID = key.getValue().split("[:]");
		        			}		        					
		        			if (key.getValue().contains("geonames.org")) {
		        				geoNameID[0] = key.getValue();
		        				int pos = geoNameID[0].indexOf("org/");
		        				geoNameID[1] = geoNameID[0].substring(pos+4);
		        			}
		        		    if (geoNameID.length>1 && !geoNameID[1].isEmpty()) {
		        				Topos t =  normdata.get(new Integer(geoNameID[1]));
		        				 if ( t == null) {
		        					Toponym toponym = WebService.get(new Integer(geoNameID[1]).intValue(), null, null);
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
	  		        			place.setAttribute("key", "#"+t.getXMLID());	        			
//	  			        		place.setAttribute("key", "geo:"+t.getLongitude()+" "+t.getLatitude());
		        		    }
		        		} 		        	
		        }
		        
		        
				XPath xpath = XPath.newInstance("//t:sourceDesc[t:ab[@type='thesaurus']]");
				xpath.addNamespace( Common.xmlns_tei_p5 );
				Element sourceDesc = (Element) xpath.selectSingleNode( this.tei );
				if (sourceDesc != null) sourceDesc.removeChildren("ab", Common.xmlns_tei_p5 );
				
				xpath = XPath.newInstance("//t:profileDesc");
				xpath.addNamespace( Common.xmlns_tei_p5 );
				Element profileDesc = (Element) xpath.selectSingleNode( this.tei );
				Element normalizedList = null;
				if (profileDesc == null) { 
					profileDesc = new Element("profileDesc", Common.xmlns_ntei_p5 );
				    this.tei.getRootElement().getChild("teiHeader", Common.xmlns_ntei_p5).addContent(profileDesc);
				}
				XPath qpath = XPath.newInstance("//t:profileDesc/t:textClass/t:keywords[@scheme='cirilo:normalizedPlaceNames']");
				qpath.addNamespace( Common.xmlns_tei_p5 );
				normalizedList = (Element) qpath.selectSingleNode( this.tei );				
				if (normalizedList != null) {
					Element list = normalizedList.getChild("list", Common.xmlns_ntei_p5);
					if (list == null) {
						list = new Element("list", Common.xmlns_ntei_p5 );
						normalizedList.addContent(list);
					} 
					qpath = XPath.newInstance("//t:profileDesc/t:textClass/t:keywords[@scheme='cirilo:normalizedPlaceNames']/t:list");
					qpath.addNamespace( Common.xmlns_tei_p5 );
					normalizedList = (Element) qpath.selectSingleNode( this.tei );				
					normalizedList.removeChildren("item", Common.xmlns_ntei_p5);
				} else {
					Element textClass = new Element("textClass", Common.xmlns_ntei_p5 );
					Element keywords = new Element("keywords", Common.xmlns_ntei_p5 );
					keywords.setAttribute("scheme","cirilo:normalizedPlaceNames");
					normalizedList = new Element("list", Common.xmlns_ntei_p5 );
					keywords.addContent(normalizedList);
					textClass.addContent(keywords);
					profileDesc.addContent(textClass);
				}

				for (Topos t : normdata.values()) 
		        {
 					Element item = new Element("item", Common.xmlns_ntei_p5);
 					Element placeName = new Element("placeName", Common.xmlns_ntei_p5);
 					Element name = new Element("name", Common.xmlns_ntei_p5);
 					Element location = new Element("location", Common.xmlns_ntei_p5);
 					Element geo = new Element("geo", Common.xmlns_ntei_p5);
 					geo.setText(t.getLongitude()+","+t.getLatitude());
 					location.addContent(geo);
	                name.setText(t.getName());				 						

	                String q = t.getFeature(); 					
 					if (q.startsWith("PPL")) {
 	 					Element settlement = new Element("settlement", Common.xmlns_ntei_p5);
 	 					Element country = new Element("country", Common.xmlns_ntei_p5);
 	 					placeName.addContent(country.setText(t.getCountry()));
 	 					placeName.addContent(settlement.setText(t.getName()));                       						
 					} else if (q.startsWith("PCL")) {
 	 					Element country = new Element("country", Common.xmlns_ntei_p5);
 					} else if (q.startsWith("ADM") || q.startsWith("AREA") ) {
 	 					Element country = new Element("country", Common.xmlns_ntei_p5);
 	 					Element region = new Element("region", Common.xmlns_ntei_p5); 						
 	 					placeName.addContent(country.setText(t.getCountry()));
 	 					placeName.addContent(region.setText(t.getName()));                       						
 					}
 					name.setAttribute("type","fcode:"+t.getFeature());
					name.setAttribute("ref","http://geonames.org/"+t.getID());
 					placeName.addContent(name); 					
 					placeName.addContent(location);
 					placeName.setAttribute("id", t.getXMLID(), Common.xmlns_xml);
 					item.addContent(placeName);
 					normalizedList.addContent(item);
 		        }
  
	 	    }
	   	 } catch (Exception e) {
		   try {
			   Common.log(logger, e); 
	  	   } catch (Exception eq) {
	  	   }            
	   	 }	   
	   }
 

   
   public void createRELS_INT(String pid) 
 	{
	    int id = 0;
	    String stream = "";
	    String rdf ="";
	    HashMap <String,String>fragments = new HashMap<String,String>();
		
	    String currPID = this.PID;    
	    this.PID = pid != null ? pid :this.PID;
	    
 		try {
 			if (!onlyValidate) {
 				byte[] url =  Repository.getDatastream(this.PID, "RDF_MAPPING" , "");
 			
 				SAXBuilder builder = new SAXBuilder(); 			
				URLConnection con = new URL (new String(url)).openConnection();
				con.setUseCaches(false);
				Document mapping = builder.build(con.getInputStream());
 				
 				List ns = mapping.getRootElement().getAdditionalNamespaces();

            				 				
 				XPath xpath = XPath.newInstance("//rdf:Description");
 				xpath.addNamespace( Common.xmlns_rdf);
 				
 				List refs = (List) xpath.selectNodes( mapping );
 				
 			    if (refs.size() > 0 ) {
					List nodes = getChildren("//@xml:id[contains(.,'SID')]");
					for (Iterator jter = nodes.iterator(); jter.hasNext();) {
						try {
							Attribute at = (Attribute) jter.next();
							at.getParent().removeAttribute("id", Common. xmlns_xml );
						} catch (Exception eq) {
						}
					}
 			    	
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
			        		Attribute type = e.getAttribute("type");
			                e.removeAttribute("type");
			                
 			        		List elems =  getChildren("//"+about);
         			        for (Iterator jter = elems.iterator(); jter.hasNext();) {
         			        	
           			        	try {
         			        		Element q = (Element) jter.next();
         			        		String oid = q.getAttributeValue("id",Common. xmlns_xml );
         			        		if (oid == null) {
         			        			oid = "SID."+new Integer(++id).toString();
         			        			q.setAttribute("id", oid, Common.xmlns_xml);
         			        		}
        			        		String ref  = "//"+  about+"[@xml:id='"+oid+"']";

         			        		fragments.put(oid,oid);
         			        		Element map = new Element("metadata-mapping", Common.xmlns_mm);
         			        		e.removeAttribute("type");
        			        	    String mm = "<mm:metadata-mapping xmlns:mm= \""+Common.xmlns_mm.getURI()+"\" xmlns:t= \""+Common.xmlns_tei_p5.getURI()+"\">"+outputter.outputString(e).replaceAll("[.]/", ref+"/")+"</mm:metadata-mapping>"; 			        		
        			        	    MDMapper m = new MDMapper(this.PID, mm, true);
         							org.jdom.Document r = builder.build( new StringReader (m.transform(this.tei) ) );							
         			        		Element root = r.getRootElement();         	
         			        		if (type != null && type.getValue().equals("document")) {
         			        			root.setAttribute("about", "info:fedora/"+this.PID+"/TEI_SOURCE", Common.xmlns_rdf);         			        			
         			        		} else {
         			        			root.setAttribute("about", "info:fedora/"+this.PID+"/TEI_SOURCE#"+oid, Common.xmlns_rdf);
         			        		}         			        		
         			        		root.removeNamespaceDeclaration(Common.xmlns_tei_p5);
         			        		stream +=outputter.outputString(r).substring(39);
         			            } catch (Exception ex) { 
         			        		continue;	
         			        	}
         			        }                                                        
 			        			        		
 			            } catch (Exception ex) { 
 			        		continue;	
 			        	}
 			        }
 			         			
 			        
 			       for ( String elem : fragments.keySet() )
 			    	   rdf+= "<rel:hasPart rdf:resource=\"info:fedora/"+this.PID+"/TEI_SOURCE#"+elem+"\"/>";
 			       }
		    	   java.util.List  <org.emile.cirilo.ecm.repository.FedoraConnector.Relation>relations = Repository.getRelations(this.PID,Common.isMemberOf);	
			        for (Relation r : relations) {
			        	   String s=r.getTo(); 
				    	   rdf+= "<rel:isPartOf rdf:resource=\""+s+"\"/>";				        	  
			        }
			        
					rdf = "<rdf:RDF xmlns:rdf=\""+Common.xmlns_rdf.getURI()+"\"  xmlns:rel=\""+Common.xmlns_gams.getURI()+"\">"+ 
	 			      "<rdf:Description rdf:about=\"info:fedora/"+this.PID+"/TEI_SOURCE\">" +
	 			      "<rel:isDatastreamOf rdf:resource=\"info:fedora/"+this.PID+"\"/>"+
						rdf +	
	 			     "</rdf:Description>"+
	 			      stream+
	 			     "</rdf:RDF>";
 
				if (Repository.exists(this.PID, "RELS-INT")) {
 					Repository.modifyDatastreamByValue(this.PID, "RELS-INT", "text/xml", new String(rdf.getBytes("UTF-8"),"UTF-8"));
 				} else {
					File f= File.createTempFile("temp", ".tmp");	    					
     	            FileOutputStream fos = new FileOutputStream(f.getAbsolutePath());
					BufferedWriter out = new BufferedWriter(new OutputStreamWriter( fos, "UTF-8" ) );
					out.write(rdf);
					out.close();
 					Repository.addDatastream(this.PID, "RELS-INT",  "RDF Statements about the TEI_SOURCE stream", "X", "text/xml", f);
 					f.delete(); 					 					
 		 		}
 			    
				String rdfs = null;

				try {
					byte[] url0 =  Repository.getDatastream(pid != null ? pid : this.PID, "TORDF" , "");
					con = new URL (new String(url0)).openConnection();
					con.setUseCaches(false);
					Document doc = builder.build(con.getInputStream());
                    rdfs = outputter.outputString(doc);					
				} catch (Exception ex0) {
					try {
						Document doc  = builder.build(new StringReader(new String(Repository.getDatastream("cirilo:"+xuser, "TORDF",""))));
                        rdfs = outputter.outputString(doc);
					} catch (Exception ex1) {
						Document doc  = builder.build(new StringReader(new String(Repository.getDatastream("cirilo:Backbone", "TORDF",""))));
                        rdfs = outputter.outputString(doc);
					}
				}		

				try {
					
					
					if (rdfs!= null && rdfs.contains("xsl:template")) {
						System.setProperty("javax.xml.transform.TransformerFactory",  "net.sf.saxon.TransformerFactoryImpl");  

						Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(new StringReader(rdfs)));
						JDOMSource in = new JDOMSource(this.tei);
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
			     } catch (Exception ex) {
			    	 Common.log(logger, ex);
			     }
			      
		    }					 				
 					
 		} catch (Exception e) {
 			try {
 				Common.log(logger, e);
 			} catch (Exception q) {} 	 
 		} finally {
 			this.PID =currPID;
 		}

 	}
   
   
	
	public void createContextualizations() {
		try {
      	    List categories = getChildren("/t:TEI/t:teiHeader[@type='contextualizations']/t:encodingDesc/t:classDecl/t:taxonomy/t:category");
			
      	    if (categories != null) {
      	    	for (Iterator iter = categories.iterator(); iter.hasNext();) {
      	    		try {
      	    			get ((Element) iter.next(), null);
      	    		} catch (Exception ex) {
      	    			continue;	
      	    		}
      	    	}
      	    }			
			} catch (Exception e) {
		}
	}

	
	public void interfereTerms() {
		
		URL url; 
		try {
						
			
			XPath xpath = XPath.newInstance("/t:TEI//t:textClass/t:keywords");
			xpath.addNamespace( Common.xmlns_tei_p5 );
			List keywords = (List) xpath.selectNodes( this.tei );			
						
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
					List attr = getChildren("//t:body//@*[contains(.,'"+pre.toUpperCase()+".')]");
					for (Iterator jter = attr.iterator(); jter.hasNext();) {
						try {
							Attribute at = (Attribute) jter.next();
							at.getParent().removeAttribute("corresp");
						} catch (Exception eq) {
						}
					}
					attr = getChildren("//t:body//@*[contains(.,'"+pre+":')]");
	      	    	o.removeChildren("term", Common.xmlns_ntei_p5);

	      	        for (Iterator iter = attr.iterator(); iter.hasNext();) {
	      	    	    Object p = iter.next();
	      	    	    if (p instanceof Attribute) {
	      	    	    	Attribute a = (Attribute) p;
	      	    	    	String[] x = a.getValue().split("[:]");
	      	    	    	if (CONCEPTS.get(uri+x[1]) == null) CONCEPTS.put(uri+x[1],pre.toUpperCase()+"."+new Integer(++cc).toString());
	      	    	    	String id = CONCEPTS.get(uri+x[1]);
	      	    	    	a.getParent().setAttribute("corresp", "#"+id);
	      	    	    }	      	    	    
	      	        }    
				}


				
	      	    for ( String el : CONCEPTS.keySet() ) {
			      		  Element term = new Element("term",Common.xmlns_ntei_p5);
			      		  term.setAttribute("ref",el);
			      		  term.setAttribute("id", CONCEPTS.get(el), Common.xmlns_xml);
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
	      	    
				} catch (Exception e) {}
		   }	    

		} catch (Exception eq) {
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
                			
			} catch (Exception ex) {
			}								
	}
		
	
	private void get(Element leaf, String parent) {
		
		String pid = null;
		String name = null;
		
        try {
        	pid = leaf.getAttributeValue("id", Common.xmlns_xml).replaceFirst("_", ":");
        	name = leaf.getChildText("catDesc", Common.xmlns_tei_p5);
        	if (!Repository.exist("info:fedora/"+pid)) {
        		temps.cloneTemplate("info:fedora/cirilo:Context", xuser, pid, name);
        	} 
        } catch (Exception e) {
        }
        		
  	    List categories = (List) leaf.getChildren("category", Common.xmlns_tei_p5);

    	if (categories != null) {
    		for (Iterator iter = categories.iterator(); iter.hasNext();) {
    			try {
    				Element e = (Element) iter.next();
    				get(e, pid);
    			} catch (Exception ex) {
    				continue;	
    			}
    		}
    	}
    	
    	if (parent != null) {
        	while(true) {
        		try {
          			    Repository.addRelation("info:fedora/"+pid, Common.isMemberOf, "info:fedora/"+parent.replaceFirst("_", ":"));
        				break;
        		} catch (org.emile.cirilo.ecm.exceptions.ObjectNotFoundException e) {
         				continue;
        		} catch (Exception e) {
        				break;
        		}
        	}  
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
					Document mapping = builder.build(con.getInputStream());
					MDMapper m = new MDMapper(this.PID,outputter.outputString(mapping));
					
					org.jdom.Document dc = builder.build( new StringReader (m.transform(this.tei) ) );							
				
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
                Common.log(logger,e);
			}
	       finally {
	        }
		}

		public void expanNamespaces() {
			
				List ns = this.tei.getRootElement().getAdditionalNamespaces();
				for (Iterator iter = ns.iterator(); iter.hasNext();) {
							try {
								Namespace n = (Namespace) iter.next();
								String prefix = n.getPrefix();
								if (!prefix.matches("t|tei")) {
									List nodes = getChildren("//@ref[contains(.,'"+prefix+":')]");
									for (Iterator jter = nodes.iterator(); jter.hasNext();) {
										try {
											Attribute at = (Attribute) jter.next();
											String[] val = at.getValue().split("[:]");
											at.setValue(n.getURI()+val[1]);
										} catch (Exception eq) {
										}
									}
								}
							} catch (Exception ex) {}		
				}			
		} 
	
	public void validate(String pid, CDefaultGuiAdapter moGA) 
	{
		try {
	    String account;
		String p;  
		if(this.PID != null && this.PID.startsWith("cirilo:")) return;
		expanNamespaces();
		p = props.getProperty("user", "TEI.CreateContexts"); 
		if (p == null || p.equals("1")) createContexts(xuser);
		p = props.getProperty("user", "TEI.IngestImages"); 
		if (p == null || p.equals("1"))  ingestImages();
		p = props.getProperty("user", "TEI.RemoveEmpties"); 
		if (p != null && p.equals("1")) removeEmpty();
		p = props.getProperty("user", "TEI.ResolveRegex"); 
		if (p == null || p.equals("1"))  regexer();
		p = props.getProperty("user", "TEI.DCMapping"); 
		if (p == null || p.equals("1"))  createMapping(pid, moGA);
		p = props.getProperty("user", "TEI.ResolveGeoIDs"); 
		account = props.getProperty("user","TEI.LoginName");
		if (p == null || p.equals("1"))  resolveGeoNameID(account);
		createContextualizations();
		p = props.getProperty("user", "TEI.ResolveSKOS"); 
		if (p == null || p.equals("1"))  interfereTerms();
		p = props.getProperty("user", "TEI.SEMExtraction"); 
		if (p == null || p.equals("1")) createRELS_INT(null);
		p = props.getProperty("user", "TEI.RefreshSource"); 
		write(p != null && p.equals("1"));
		} catch (Exception e) {
		}

		if (this.intermedidate != null) {
 			try {
 				File temp = File.createTempFile("tmp","xml");
 				String s = this.outputter.outputString(this.intermedidate);
				if (!Repository.exists(this.PID, "INTERMEDIATE_CODE")) {				      								
      				FileOutputStream fos = new FileOutputStream(temp);
      				fos.write(s.getBytes("UTF-8"));
      				fos.close();
      				Repository.addDatastream(this.PID, "INTERMEDIATE_CODE","Intermediate TEI Code",  "X", "text/xml", temp);
 				} else {
		    		Repository.modifyDatastreamByValue(this.PID, "INTERMEDIATE_CODE", "text/xml", s);                 							            	
 				}
				temp.delete();
 				
 			} catch (Exception e) {}
 		}
		
		try {
			String p = props.getProperty("user", "TEI.toMETS"); 
			if (p != null && p.equals("1")) createMETS();
		} catch (Exception e) {}	

	}
	
	
	public void createMETS() {
	  try {	
		String xsl;
		try {
			Document doc  = builder.build(new StringReader(new String(Repository.getDatastream("cirilo:"+xuser, "TEITOMETS",""))));
            xsl = outputter.outputString(doc);
		} catch (Exception ex1) {
			Document doc  = builder.build(new StringReader(new String(Repository.getDatastream("cirilo:Backbone", "TEITOMETS",""))));
            xsl = outputter.outputString(doc);
		}

        System.setProperty("javax.xml.transform.TransformerFactory",  "net.sf.saxon.TransformerFactoryImpl");  
        
        Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(new StringReader(xsl)));
        JDOMSource in = new JDOMSource(this.tei);
	    JDOMResult out = new JDOMResult();
	    
	    transformer.transform(in, out);	    				        		        
	        		        
  	  	System.setProperty("javax.xml.transform.TransformerFactory",  "org.apache.xalan.processor.TransformerFactoryImpl");	    					  

  	    Document mets = builder.build(new StringReader(outputter.outputString(out.getResult())));
		XPath xpath = XPath.newInstance("//mets:fileGrp[@USE='DEFAULT']/mets:file");
		xpath.addNamespace( Common.xmlns_mets);

   	    List images = xpath.selectNodes(mets);
   	    
		for (Iterator iter = images.iterator(); iter.hasNext();) {
			Element e = (Element) iter.next();
      		String id = e.getAttributeValue("ID");
      		InputStream bin = new ByteArrayInputStream(Repository.getDatastream(this.PID, id, ""));
						
   			BufferedImage img = ImageIO.read(bin);			      				                		
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
	    			e.addContent(fcontent);
	    		} catch (Exception q) {}
		}
  	  	
  	  	
		File file= File.createTempFile("temp", ".tmp");	    					
		FileOutputStream fos = new FileOutputStream(file);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter( fos, "UTF-8" ) );
		bw.write(new String(outputter.outputString(mets).getBytes("UTF-8"),"UTF-8"));
		bw.flush();
		bw.close();
		Repository.addDatastream(this.PID, "METS_SOURCE","METS Source",  "X", "text/xml", file);  	  		
        file.delete();  		    
  	  	  	  	
	  } catch (Exception ex0) {
		  ex0.printStackTrace();
	  }

	}
	
	
	public void refresh() 
	{
		try {
		String account;
		String p;  
		if(this.PID.startsWith("cirilo:")) return;
		p = props.getProperty("user", "TEI.CreateContexts"); 
		if (p == null || p.equals("1")) createContexts(xuser);
		p = props.getProperty("user", "TEI.SEMExtraction"); 
		if (p == null || p.equals("1")) createRELS_INT(null);
		} catch (Exception e) {
		}	    
	 }	

	 	 
}
