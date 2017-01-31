package org.emile.cirilo.business;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.log4j.Logger;
import org.emile.cirilo.Common;
import org.emile.cirilo.ServiceNames;
import org.emile.cirilo.User;
import org.emile.cirilo.ecm.repository.Repository;
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
import org.xmldb.api.modules.XMLResource;

import voodoosoft.jroots.core.CPropertyService;
import voodoosoft.jroots.core.CServiceProvider;
import voodoosoft.jroots.dialog.CDefaultGuiAdapter;

public class MEI {

	private static Logger log = Logger.getLogger(MEI.class);

	private Document mei;
	private File file;
	private String raw;
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
	private CPropertyService props;
	private String xuser;
	
	public MEI(FileWriter logger, boolean validate, boolean mode) {		
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
			this.raw = null;
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
    				this.mei = builder.build( this.file );
    				validate();
    			}   
    			return this.file.exists();
    		} else {
    			eXist eX = new eXist(file);
    			org.xmldb.api.base.Collection collection = DatabaseManager.getCollection( URI + eX.getCollection(), user.getExistUser(), user.getExistPasswd() );
    			XMLResource res = (XMLResource) collection.getResource(eX.getStream());
    	        this.mei = builder.build( new StringReader( (String) res.getContent()));
    	        validate();
    	        collection.close();
    	        this.collection = file;
    	        return true;
    		}
		} catch (Exception e) { 
      		log.error(e.getLocalizedMessage(),e);				      					   			
			return false;
		}
	}
  
   public void validate() {
	   try {
            System.setProperty("javax.xml.transform.TransformerFactory",  "net.sf.saxon.TransformerFactoryImpl");  
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
	        StreamSource is = null; 
   			JDOMResult out = new JDOMResult();
            Transformer transformer;

			XPath xpath = XPath.newInstance("/score-partwise");
			Element score_partwise = (Element) xpath.selectSingleNode( this.mei );
			if (score_partwise != null) {
		        try {
			        JDOMSource in = new JDOMSource(this.mei);	        
		        	is =  new StreamSource(user.getUrl().substring(0,user.getUrl().lastIndexOf("/")+1)+"mei/musicxml2parttime.xsl"); 				            
		        	transformer = transformerFactory.newTransformer(is);
	        		transformer.transform(in, out);
	        		this.mei = out.getDocument();
	        	} catch (Exception q) {
		        }
			}
			xpath = XPath.newInstance("/score-timewise");
			Element score_timewise = (Element) xpath.selectSingleNode( this.mei );
			if (score_timewise != null) {
		        try {
			        JDOMSource in = new JDOMSource(this.mei);	        
		        	is =  new StreamSource(user.getUrl().substring(0,user.getUrl().lastIndexOf("/")+1)+"mei/musicxml2mei.xsl"); 				            
		        	transformer = transformerFactory.newTransformer(is);
	        		transformer.transform(in, out);
	        		this.mei = out.getDocument();
	        	} catch (Exception q) {
	        		log.debug(q.getLocalizedMessage(),q);
		        }
			}
	   } catch (Exception e) {
		   log.debug(e.getLocalizedMessage(),e);
	   }
	   
	   finally {
   		System.setProperty("javax.xml.transform.TransformerFactory",  "org.apache.xalan.processor.TransformerFactoryImpl");
	   }
	   
   }
    
    
    public boolean get (String pid) {
    	try {
    		SAXBuilder builder = new SAXBuilder();
    		this.mei =builder.build(new StringReader(new String(Repository.getDatastream(pid, "MEI_SOURCE",""))));
			this.PID = pid;
   	        return true;
		} catch (Exception e) { 
      		log.error(e.getLocalizedMessage(),e);				      					   
            Common.log(logger,e);
			return false;
		}
	}
    
    
    public boolean set (String stream) {
    	try {
    		SAXBuilder builder = new SAXBuilder();
			this.collection="";
			this.mei =builder.build(new StringReader(stream));
			validate();
			this.PID = "";
   	        return true;
		} catch (Exception e) { 
      		log.error(e.getLocalizedMessage(),e);				      					   
            Common.log(logger,e);
			return false;
		}
	}
    
 	public String toString() {
 		   // removeEmpty();
	    	return this.outputter.outputString(this.mei); 
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
			XPath xpath = XPath.newInstance("//m:fileDesc/m:pubStmt//m:identifier[@type='PID']");
			xpath.addNamespace( Common.xmlns_mei );
			Element idno = (Element) xpath.selectSingleNode( mei );
			if (idno != null) {
				String s = idno.getTextNormalize();
	            this.PID = s.startsWith(Common.INFO_FEDORA) ? s.substring(Common.INFO_FEDORA.length()) : s;
			} else {
	   			String pid = mei.getRootElement().getAttributeValue("id", Common.xmlns_xml).toLowerCase();
                if (pid != null) {				
                	if (!pid.startsWith("o:")) pid= "o:"+pid;
                	xpath = XPath.newInstance("//m:fileDesc/m:pubStmt");
                	xpath.addNamespace( Common.xmlns_mei );
                	Element anchor = (Element) xpath.selectSingleNode( mei );
                	Element child = new Element ("identifier", Common.xmlns_nmei );
                	child.setText(Common.INFO_FEDORA+pid);
                	child.setAttribute("type", "PID");
                	anchor.addContent(child);
                	this.PID =pid;
              }   
			}
		} catch (Exception e) {
      		log.error(e.getLocalizedMessage(),e);				      					   			
		}
		return this.PID;
	}

	public void setPID(String pid) { 
		try {
			XPath xpath = XPath.newInstance("//m:fileDesc/m:pubStmt//m:identifier[@type='PID']");
			xpath.addNamespace( Common.xmlns_mei );
			Element idno = (Element) xpath.selectSingleNode( mei );
			if (idno == null) {
				try {
					String oid = mei.getRootElement().getAttributeValue("id", Common.xmlns_xml).toLowerCase();
					if (oid != null) pid = oid;				
					if (!pid.startsWith("o:")) pid= "o:"+pid;
				} catch (Exception u) {}
				xpath = XPath.newInstance("//m:fileDesc/m:pubStmt");
				xpath.addNamespace( Common.xmlns_mei );
				Element anchor = (Element) xpath.selectSingleNode( mei );
				Element child = new Element ("identifier", Common.xmlns_nmei );
				child.setText(pid);
				child.setAttribute("type", "PID");
				anchor.addContent(child);								
				this.raw = outputter.outputString(this.mei);
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
		try  {
			isCustomized = false;
			this.raw = null;
			
			XPath xpath = XPath.newInstance( "/m:mei" );
			xpath.addNamespace( Common.xmlns_mei );
			if ( xpath.selectSingleNode( this.mei ) == null  ) { 
				xpath = XPath.newInstance( "/m:meiCorpus" );
				xpath.addNamespace( Common.xmlns_mei );
				if ( xpath.selectSingleNode( this.mei ) == null ) return false;
			}

	    	String p = props.getProperty("user", "MEI.Customization"); 
			if (p != null && p.equals("1")) { 
                transform();				
				isCustomized = true;
				SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
				Schema schema = factory.newSchema(new URL(user.getUrl().substring(0,user.getUrl().lastIndexOf("/"))+Common.MEISCHEMA));
				Validator validator = schema.newValidator();
				try {
					validator.validate(new JDOMSource(this.mei));			        
				} catch (Exception q) {
	       			Common.log(logger, "-----------------------\n"+outputter.outputString(this.mei)+"-----------------------\n");
	       			Common.log(logger, q);
					isCustomized = true;
					return false;
				}
			}
			return true;
		} catch (Exception e) {
			log.error(e.getLocalizedMessage(),e);	
   			Common.log(logger, e);   		 
			return false;}
	}
	

	public boolean transform() {
		byte[] stylesheet = null;
        try {
        	try {
        		stylesheet =  Repository.getDatastream("cirilo:"+xuser, "TOMEI" , "");
        	} catch (Exception ex) {
           		try { 
        		stylesheet =  Repository.getDatastream("cirilo:Backbone", "TOMEI" , "");
        		} catch (Exception q) {
    	      		log.error(q.getLocalizedMessage(),q);				      					   
         			Common.log(logger, q);   		 
        			return false;
        		}
          	}
        	
        	
        	if (new String(stylesheet).contains(":template")) {
        		System.setProperty("javax.xml.transform.TransformerFactory",  "net.sf.saxon.TransformerFactoryImpl");  

        		Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(new StringReader(new String(stylesheet, "UTF-8"))));
        	
        		JDOMSource in = new JDOMSource(this.mei);
        		JDOMResult out = new JDOMResult();
        		transformer.transform(in, out);
        	
        		System.setProperty("javax.xml.transform.TransformerFactory",  "org.apache.xalan.processor.TransformerFactoryImpl");
        		SAXBuilder builder = new SAXBuilder();

        		this.mei = builder.build( new StringReader( outputter.outputString(out.getResult())) );	
     		
        	}	

        } catch (Exception e) {
        	log.error(e.getLocalizedMessage(),e);	
        	Common.log(logger, e);
        }
        	
       	return true;
	}
		

	public boolean write(boolean mode) {
		try {

			if (!onlyValidate)  {
				if (this.collection.isEmpty()) {
					if  (mode || (!isCustomized && this.raw !=null && this.file.toString().contains(".xml"))) {
						FileOutputStream fos = new FileOutputStream( this.file.toString() );
						BufferedWriter out = new BufferedWriter(new OutputStreamWriter( fos, "UTF-8" ) );
						if (mode) out.write(new String(outputter.outputString(this.mei).getBytes("UTF-8"),"UTF-8")); 
						else if (!isCustomized && this.raw !=null) out.write(this.raw);	
						out.close();
					}
				} else {
					if  (mode || (!isCustomized && this.raw !=null && this.file.toString().contains(".xml"))) {
						eXist eX = new eXist (this.collection);
						org.xmldb.api.base.Collection coll = DatabaseManager.getCollection( URI + eX.getCollection(), user.getExistUser(), user.getExistPasswd() );
						XMLResource res = (XMLResource) coll.getResource(eX.getStream());
						if (mode) res.setContent(new String(outputter.outputString(this.mei).getBytes("UTF-8"),"UTF-8"));
						else if (!isCustomized && this.raw !=null) res.setContent(this.raw);
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

	
	
   
   public void createRELS_INT(String pid) 
 	{
	    String currPID = this.PID;    
	    this.PID = pid != null ? pid :this.PID;

 			    
     	String rdfs = null;

     	try {
				try {
					byte[] url0 =  Repository.getDatastream(pid != null ? pid : this.PID, "TORDF" , "");
					URLConnection con = new URL (new String(url0)).openConnection();
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
						JDOMSource in = new JDOMSource(this.mei);
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
			     } catch (Exception q) {
		        	 log.error(q.getLocalizedMessage(),q);				      					             								
			    	 Common.log(logger, q);
			     }	 
			} catch (Exception e) {
				try {
			   		log.error(e.getLocalizedMessage(),e);				      					             								
					Common.log(logger, e);
				} catch (Exception q) {} 	 
			} finally {
				this.PID =currPID;
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
					
					org.jdom.Document dc = builder.build( new StringReader (m.transform(this.mei) ) );							
				
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
                Common.log(logger,e);
			}
	       finally {
	        }
		}

	
	public void validate(String pid, CDefaultGuiAdapter moGA) 
	{
		try {
			String p;  
			if(this.PID != null && this.PID.startsWith("cirilo:")) return;
			p = props.getProperty("user", "MEI.DCMapping"); 
			if (p == null || p.equals("1"))  createMapping(pid, moGA);
			p = props.getProperty("user", "MEI.SEMExtraction"); 
			if (p == null || p.equals("1")) createRELS_INT(null);
			p = props.getProperty("user", "MEI.RefreshSource"); 
			write(p != null && p.equals("1"));
		} catch (Exception e) {
    		log.error(e.getLocalizedMessage(),e);				      					             											
		}


	}
	
	 	 
}
