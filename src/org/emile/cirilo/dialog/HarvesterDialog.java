/*
 *  -------------------------------------------------------------------------
 *  Copyright 2014 
 *  Centre for Information Modeling - Austrian Centre for Digital Humanities
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 *  -------------------------------------------------------------------------
 */


package org.emile.cirilo.dialog;

import org.emile.cirilo.*;
import org.emile.cirilo.business.MDMapper;
import org.emile.cirilo.business.Session;
import org.emile.cirilo.business.EDM;
import org.emile.cirilo.ecm.templates.*;
import org.emile.cirilo.ecm.repository.*;
import org.emile.cirilo.gui.jtable.HarvesterTableModel;
import org.emile.cirilo.oai.*;
import org.emile.cirilo.utils.ImageTools;

import voodoosoft.jroots.application.*;
import voodoosoft.jroots.core.CServiceProvider;
import voodoosoft.jroots.dialog.*;
import voodoosoft.jroots.exception.CException;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.io.FileWriter;
import java.io.File;
import java.io.StringReader;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.net.URL;
import java.net.URLConnection;
import java.io.*;

import javax.swing.JOptionPane;
import javax.swing.JTable;

import org.apache.log4j.Logger;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.Transformer;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.transform.JDOMResult;
import org.jdom.transform.JDOMSource;
import org.jdom.xpath.XPath;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Attribute;

import com.asprise.util.ui.progress.ProgressDialog;

public class HarvesterDialog extends CDefaultDialog {

	private static Logger log = Logger.getLogger(HarvesterDialog.class);

	/**
	 *  Constructor for the SelectLayoutDialog object
	 */
	public HarvesterDialog() { }
	
	/**
	 *  Gets the accessContext attribute of the SelectLayoutDialog object
	 *
	 * @return    The accessContext value
	 */
	public IAccessContext getAccessContext() {
		CDefaultAccessContext loCxt = null;

		try {
		} catch (Exception ex) {
			CException.record(ex, this);
		}
		return loCxt;
	}


	/**
	 *  Description of the Method
	 *
	 * @param  e  Description of the Parameter
	 */
	public void handleCloseButton(ActionEvent e)
		throws Exception {
	    JTable tb = (JTable) getGuiComposite().getWidget("jtRepositories");
		org.emile.cirilo.dialog.CBoundSerializer.save(this.getCoreDialog(), se.getHarvesterDialogProperties(), tb);   
		close();
	}

	/**
	 *  Description of the Method
	 *
	 * @param  e              Description of the Parameter
	 * @exception  Exception  Description of the Exception
	 */
	public void handleStartButton(ActionEvent e) throws Exception {
		new Thread() {
			public void run() {

					try {

						JTable tb = (JTable) getGuiComposite().getWidget("jtRepositories");
			  		  	int[] selected = tb.getSelectedRows();

			  		  	edm = new EDM();
			  		  	
						MessageFormat msgFmt = new MessageFormat(res.getString("askharv"));
						Object[] args = {new Integer(selected.length).toString()};
						String time = new java.sql.Timestamp(System.currentTimeMillis()).toString();
			   		    logfile = logdir + System.getProperty( "file.separator" )+"harvest-"+time.replaceAll("[ ]", "_").replaceAll("[:]", ".")+".log";
			   		    if (!new File(logdir).exists()) {
			   		    	Object[] arg  = {logdir};
			   		    	msgFmt = new MessageFormat(res.getString("nologdir"));
			   		    	JOptionPane.showMessageDialog(null, msgFmt.format(arg),Common.WINDOW_HEADER,JOptionPane.ERROR_MESSAGE);
			   		    	return;
			   		    }
			   		    
						logger = new FileWriter( logfile );

						int liChoice = JOptionPane.showConfirmDialog(null, msgFmt.format(args), Common.WINDOW_HEADER, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

						if (liChoice == 0) {

							logger.write( new java.util.Date()  +res.getString("start")+" harvesting"+"\n");	


				   		    getCoreDialog().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));						

				   		    boolean exit = false;				   		    
				   		    for (int i=0; i<selected.length; i++) {	 
 																    			    		
			   		    		int x = selected[i];
			   		    		
				   		    	String baseURL =(String) tb.getValueAt(x,2);
				   		    	String metadataPrefix =(String) tb.getValueAt(x,3);
				   		    	
				   		    	try {
				   		    		
				   		    		if (baseURL.startsWith("http")) {
				   		    			if (harvest (metadataPrefix, baseURL, ((HarvesterTableModel) tb.getModel()).getRow(x)[6], null, null)) {
				   		    				if(!addItems( ((HarvesterTableModel) tb.getModel()).getRow(x))) {
				   		    					exit = true;
				   		    					break;
				   		    				}
				   		    			}	
				   		    			if (exit) break;
				   		    		}	
				   		    		if (baseURL.startsWith("file:///")) {
				   		    			if (collect (metadataPrefix, baseURL, null, null)) {
				   		    				if(!addItems( ((HarvesterTableModel) tb.getModel()).getRow(x))) {
				   		    					exit = true;
				   		    					break;
				   		    				}
				   		    			}	
				   		    			if (exit) break;
				   		    		}	
				   		    		
				   		    	} catch (Exception ex) {	
									log.error(ex.getLocalizedMessage(),ex);					   		    		
				   		    	}
				   		    	
				   		    	XPath xPath = XPath.newInstance( "/dataproviders/repository[serviceprovider='"+baseURL+"']" );
				   		    	Element rep = (Element) xPath.selectSingleNode( doc );
				   		    	if (rep != null) rep.getChild("updated").setText(time);		     
				   		    	logger.write("\n");
				   		    }

				   		    Repository.modifyDatastreamByValue("cirilo:Backbone", "DATAPROVIDERS", "text/xml", outputter.outputString(doc));


				   		    JOptionPane.showMessageDialog(  getCoreDialog(), res.getString("details")+logfile , Common.WINDOW_HEADER, JOptionPane.INFORMATION_MESSAGE);
				   		    
				   		    logger.write("\n"+ new java.util.Date()  +res.getString("end")+" harvesting");									
							logger.close();

							getGuiComposite().getWidget("jbShowLogfile").setEnabled(true);

				   		    
						}
	         
					} catch (Exception ex) {
						log.error(ex.getLocalizedMessage(),ex);	
					}
					finally {
						getCoreDialog().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					}
			}	
		}.start();

	}

	public boolean collect(String metadataPrefix, String baseURL, String from, String until)  {
		try {
			metadata = parser.build(new File(baseURL.substring(8)));	
			return true;
		} catch (Exception e) {
			try {
				logger.write("\n" + new java.util.Date() + e.getLocalizedMessage() );
			} catch (Exception q) {} 	
			return false;
		}	
	}
	
	public boolean harvest(String metadataPrefix, String baseURL, String constraints, String from, String until) {
		try {	
			metadata = new Document();
			root = new Element("OAI-PMH");
			metadata.addContent(root);
				
			ListRecords listRecords = new ListRecords(baseURL, from, until, null, metadataPrefix);
			String resumptionToken = null;
            int i = 0;
			
			log.debug("REST request to "+baseURL+" with metadataPrefix "+metadataPrefix+" "+constraints);
			
	        do {
				NodeList errors = listRecords.getErrors();
				if (errors != null && errors.getLength() > 0) {
				   for (int j = 0; i< errors.getLength(); j++) {
					   Node item = errors.item(j);
					   logger.write("\n"+ new java.util.Date()  +" "+item.getTextContent());	
				   }
				   return false;
				}
				
				Document pass = parser.build(new StringReader(new String(listRecords.toString().getBytes(),"UTF-8")));
				XPath xpath = XPath.newInstance("//oai:record"+(!constraints.isEmpty() ? "["+constraints+"]" : ""));
				xpath.addNamespace(Common.xmlns_dc);
				xpath.addNamespace(Common.xmlns_oai);
				xpath.addNamespace(Common.xmlns_edm);
				xpath.addNamespace(Common.xmlns_europeana);
				xpath.addNamespace(Common.xmlns_tei_p5);
				xpath.addNamespace(Common.xmlns_dcterms);
				xpath.addNamespace(Common.xmlns_lido);
				xpath.addNamespace(Common.xmlns_skos);
				xpath.addNamespace(Common.xmlns_rdf);
				xpath.addNamespace(Common.xmlns_ore);
				xpath.addNamespace(Common.xmlns_owl);
				xpath.addNamespace(Common.xmlns_rdaGr2);
				xpath.addNamespace(Common.xmlns_wgs84_pos);

				List records = (List) xpath.selectNodes(pass);
							
				if (records.size() > 0) {
					for (Iterator iter = records.iterator(); iter.hasNext();) {
						Element em = (Element) iter.next();
						root.addContent((Element) em.clone());
					}			
				}

        		resumptionToken = listRecords.getResumptionToken();
        		
	        	if (!resumptionToken.isEmpty()) {
	        		listRecords = new ListRecords(baseURL, resumptionToken);
		        	i++;		        	
					log.debug("Pass "+i+" on "+baseURL+ " with resumptionToken "+resumptionToken);
	        	} else {
	        		break;
	        	}
	      
	        } while (true);
	        
			log.debug("Building JDOM Document from harvested metadata was successful");
			
			return true;
			
		} catch (Exception e) {
			try {
				log.error(e.getLocalizedMessage(),e);
				if (!e.getLocalizedMessage().contains("bad syntax")) logger.write("\n" + new java.util.Date() + e.getLocalizedMessage() );
			} catch (Exception q) {} 	
		}
  	    return false;
	}
	
	public boolean addItems(String[] par) throws Exception {
		try {
			
			 
			String name = par[0];
			String updated = par[1];
			String serviceprovider = par[2];
			String metadataprefix = par[3];
			String url = par[4];
			String model = par[5];
			String constraints = par[6];
			String icon = par[7];
			String owner = par[8];
			
			XPath xpath = XPath.newInstance("//oai:record");
			xpath.addNamespace(Common.xmlns_dc);
			xpath.addNamespace(Common.xmlns_oai);
			xpath.addNamespace(Common.xmlns_edm);
			xpath.addNamespace(Common.xmlns_europeana);
			xpath.addNamespace(Common.xmlns_tei_p5);
			xpath.addNamespace(Common.xmlns_dcterms);
			xpath.addNamespace(Common.xmlns_lido);
			xpath.addNamespace(Common.xmlns_skos);
			xpath.addNamespace(Common.xmlns_rdf);
			xpath.addNamespace(Common.xmlns_ore);
			xpath.addNamespace(Common.xmlns_owl);
			xpath.addNamespace(Common.xmlns_rdaGr2);
			xpath.addNamespace(Common.xmlns_wgs84_pos);
								
			byte[] stylesheet = null;
        	try {
	        	stylesheet =  Repository.getDatastream("cirilo:"+owner, "RECORDtoEDM" , "");
	        } catch (Exception ex) {
	           		try { 
	        		stylesheet =  Repository.getDatastream("cirilo:Backbone", "RECORDtoEDM" , "");
	        		} catch (Exception q) {
	           			Common.log(logger, q);   		 
	        			return false;
	        		}
          	}
        	
    		System.setProperty("javax.xml.transform.TransformerFactory",  "net.sf.saxon.TransformerFactoryImpl");  
    		Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(new StringReader(new String(stylesheet))));

        	
			URLConnection con = new URL (new String(Repository.getDatastream(model , "DC_MAPPING" , ""))).openConnection();
			con.setUseCaches(false);
			Document mapping = parser.build(con.getInputStream());			
			MDMapper m = new MDMapper(model, outputter.outputString(mapping));
			con = null;

			
            log.debug("Reading stylesheet RECORDtoEDM was successful");
			
			List records = (List) xpath.selectNodes(metadata);
			
			if (records.size() > 0) {
				
		    	ProgressDialog progressDialog = new ProgressDialog( getCoreDialog(), Common.WINDOW_HEADER);
		    	progressDialog.displayPercentageInProgressBar = true;
		    	progressDialog.millisToDecideToPopup = 1;
		    	progressDialog.millisToPopup = 1;

		    	progressDialog.beginTask(name+": "+res.getString("harvcont"),records.size(), true);
		    	progressDialog.worked(1);

				
				int i = 0;

				for (Iterator iter = records.iterator(); iter.hasNext();) {
					Element em = (Element) iter.next();
					
					i++;
					
		    		if(progressDialog.isCanceled()) {
		    			return false;
		    		}				

		    		progressDialog.worked(1);

		    		try {
		    			Thread.sleep(50); 
		    		} catch (InterruptedException e) {
		    		}

					try {
						String pid = "o:oai." + em.getChild("header", Common.xmlns_oai).getChild("identifier",	Common.xmlns_oai).getText()
								.replaceAll("info:fedora/oai:", "")
								.replaceAll("o:", "")
								.replaceAll("hdl:", "")
								.replaceAll("[\\[({<>)}\\];#+~]", "")
								.replaceAll("[:/]", ".");
						
						log.debug("Starting ingest of object "+i+" with PID "+pid);
						
						if (!Repository.exist(pid)) {
							pid = temps.cloneTemplate("info:fedora/"+model,	owner, "$" + pid, (String) null);
							log.debug("Creating the object "+pid+ " was successful" );
							logger.write("\n" + new java.util.Date() + res.getString("creatingobject") + pid);
						} else {
							logger.write("\n" + new java.util.Date() + res.getString("updatingobject") + pid);
							log.debug("Updating the object "+pid+ " was successful" );
						}

						xpath = XPath.newInstance(url);
						xpath.addNamespace(Common.xmlns_dc);
						xpath.addNamespace(Common.xmlns_oai);
						xpath.addNamespace(Common.xmlns_edm);
						xpath.addNamespace(Common.xmlns_europeana);
						xpath.addNamespace(Common.xmlns_tei_p5);
						xpath.addNamespace(Common.xmlns_dcterms);
						xpath.addNamespace(Common.xmlns_lido);
						xpath.addNamespace(Common.xmlns_skos);
						xpath.addNamespace(Common.xmlns_rdf);
						xpath.addNamespace(Common.xmlns_ore);
						xpath.addNamespace(Common.xmlns_owl);
						xpath.addNamespace(Common.xmlns_rdaGr2);
						xpath.addNamespace(Common.xmlns_wgs84_pos);
						
						Object path =  xpath.selectSingleNode(em);
						
						if (path != null) {							
							
							XPath vpath = XPath.newInstance(icon);
							vpath.addNamespace(Common.xmlns_dc);
							vpath.addNamespace(Common.xmlns_oai);
							vpath.addNamespace(Common.xmlns_edm);
							vpath.addNamespace(Common.xmlns_europeana);
							vpath.addNamespace(Common.xmlns_tei_p5);
							vpath.addNamespace(Common.xmlns_dcterms);
							vpath.addNamespace(Common.xmlns_lido);
							vpath.addNamespace(Common.xmlns_skos);
							vpath.addNamespace(Common.xmlns_rdf);
							vpath.addNamespace(Common.xmlns_ore);
							vpath.addNamespace(Common.xmlns_owl);
							vpath.addNamespace(Common.xmlns_rdaGr2);
							vpath.addNamespace(Common.xmlns_wgs84_pos);
							
							Object object =  vpath.selectSingleNode(em);

							if (object != null) {

								String iconref = null;
								if (object instanceof Element) {
									iconref = ((Element) object).getText();
								}
								if (object instanceof Attribute) {
									iconref = ((Attribute) object).getValue();
								}
								
								String uwmetadata = null;
								
                                String server;
                                String oid;
                                
								if(iconref.contains("phaidra")) {
								   int ipos = iconref.indexOf("/o:");	
								   int jpos = iconref.indexOf("//");
								   server =  "https://"+iconref.substring(jpos+2,ipos).replaceAll("phaidra", "fedora");
								   oid = iconref.substring(ipos+1);
								   iconref = server+"/fedora/objects/"+oid+"/methods/bdef:Document/preview?box=520";
								   uwmetadata =  (server+"/fedora/get/"+oid+"/bdef:Asset/getUWMETADATA");
								}
								
								try {
									if(uwmetadata != null) {
								        InputStream is = new URL(uwmetadata).openStream();
								        BufferedReader br = new BufferedReader(new InputStreamReader(is));
								        
								        StringBuilder response = new StringBuilder();								        
								        String line;
								        
								        while ( (line = br.readLine()) != null) {
								        	response.append(line);
								        }
										Document uwm = parser.build(new StringReader(response.toString()));									

										XPath upath = XPath.newInstance("./oai:metadata");
										upath.addNamespace(Common.xmlns_oai);
										em.addNamespaceDeclaration(Common.xmlns_ns0);
										Element metadata = (Element) upath.selectSingleNode(em);																				
								        metadata.removeChild("dc", Common.xmlns_oai_dc);
										metadata.addContent(uwm.cloneContent());																				
									}
									String buf =  outputter.outputString(em);
									
									String objref = null;
									if (path instanceof Element) {
										objref = ((Element) path).getText();
									}
									if (path instanceof Attribute) {
										objref = ((Attribute) path).getValue();
									}									
									Repository.modifyDatastream(pid, "URL", null, "R", objref);							
									
									JDOMSource in = new JDOMSource(em);
		    		        		JDOMResult out = new JDOMResult();
									
			    		        	try {
			    		        		transformer.transform(in, out);
										log.debug("Mapping metadata of object "+pid+ " was successful" );
			    		        	} catch (Exception e) {
										log.error(e.getLocalizedMessage(),e);
			    		        	}
			    		        	try {
			    		        		Repository.modifyDatastreamByValue(pid, "RECORD", "text/xml", buf);
			    		        		edm.set(out.getDocument());
			    		    			Repository.modifyDatastreamByValue(pid, "EDM_STREAM", "text/xml", edm.toString());
										log.debug("Updating metadata of object "+pid+ " was successful" );
			    		        	} catch (Exception e) {
										log.error(e.getLocalizedMessage(),e);
			    		        	}	
			    		        	finally {
			    		        		in = null;
			    		        		out = null;
			    		        		buf = null;
			    		        	}
			    					org.jdom.Document dc = parser.build( new StringReader (m.transform(parser.build(new StringReader(edm.toString())))));					    					
			    					dc = Common.validate(dc);
			    					Repository.modifyDatastreamByValue(pid, "DC", "text/xml", outputter.outputString(dc));
								} catch (Exception eq) {
									log.error(eq.getLocalizedMessage(),eq);
								}

															
								try {
									File image = File.createTempFile("temp",".tmp");
							    	File thumbnail = File.createTempFile( "temp", ".tmp" );			    

							    	URL ref = new URL(iconref);

									InputStream is = ref.openStream();
									OutputStream os = new FileOutputStream(image.getAbsoluteFile());
									
									byte[] b = new byte[2048];
									int length;
									while ((length = is.read(b)) != -1) {
										os.write(b, 0, length);
									}
									is.close();
									os.close();									
									
							    	ImageTools.createThumbnail( image, thumbnail, 300, 240, Color.lightGray );
									
									Repository.modifyDatastream(pid, "THUMBNAIL","image/jpeg", "M", thumbnail);

									thumbnail.delete();	
									image.delete();
									
									log.debug("Updating thumbnail of object "+pid+ " was successful" );
								} catch (Exception eq) {
									log.error(eq.getLocalizedMessage(),eq);
								}
							}
							

						}
						
	
					} catch (Exception e) {
						log.error(e.getLocalizedMessage(),e);	
					}
					finally {
					}
				}

				MessageFormat msgFmt = new MessageFormat(res.getString("oaiok"));
	 			Object[] args = {i, records.size(), serviceprovider};
	 			JOptionPane.showMessageDialog (getCoreDialog(),msgFmt.format(args));			  
			}

		} catch (Exception ex) {
			log.error(ex.getLocalizedMessage(),ex);	
		}
		finally {
    		System.setProperty("javax.xml.transform.TransformerFactory",  "org.apache.xalan.processor.TransformerFactoryImpl");
		}

		return true;
	}

	
	public void handleShowLogfileButton(ActionEvent e) 
	throws Exception {
		TextEditor dlg = (TextEditor) CServiceProvider.getService(DialogNames.TEXTEDITOR);
		dlg.set(logfile, null, "text/log", "R", null, null);
		dlg.open();
	}	

	/**
	 *  Description of the Method
	 *
	 * @exception  CShowFailedException  Description of the Exception
	 */
	public void show()  throws CShowFailedException {
	  try {

		 String[] names ={res.getString("provider"),res.getString("updated"),res.getString("baseurl"),res.getString("prefix"),res.getString("shownat"), res.getString("cmodel"),"Constraints","Thumbnail", res.getString("owner")};
		  
		 se = (Session) CServiceProvider.getService( ServiceNames.SESSIONCLASS );						
	     JTable tb = (JTable) getGuiComposite().getWidget("jtRepositories");
         List repositories = null;
         
	     try {

	    	 
	    	 doc = parser.build(user.getUrl()+"/objects/cirilo%3ABackbone/datastreams/DATAPROVIDERS/content");
   	         logdir = doc.getRootElement().getAttributeValue("logdir");
   	         
		     XPath xPath = XPath.newInstance( "/dataproviders/repository[@state='active']" );
		     repositories = (List) xPath.selectNodes( doc );

		     HarvesterTableModel dm = new HarvesterTableModel(names);
	     
	    	 if (repositories != null) {
    		
	    		 for (Iterator iter = repositories.iterator(); iter.hasNext();) {
	    			 try {
	    				 Element e = (Element) iter.next();
	    				 String[] row = new String[9]; 
	    				 row[0] = e.getAttributeValue("name");
	    				 row[1] = e.getChild("updated").getText();
	    				 row[2] = e.getChild("serviceprovider").getText();
	    				 row[3] = e.getChild("metadataprefix").getText();
	    				 row[4] = e.getChild("url").getText();
	    				 row[5] = e.getChild("model").getText();
	    				 row[6] = e.getChild("constraints").getText();
	    				 row[7] = e.getChild("thumbnail").getText();
	    				 row[8] = e.getChild("owner").getText();
	    				 dm.add(row);
	    				 
	    			 } catch (Exception ex) {}
	    		 }	  
	    	 }
	 	    	 
	         tb.setModel(dm);
	         tb.setRowSelectionInterval(0,0);
			 org.emile.cirilo.dialog.CBoundSerializer.load(this.getCoreDialog(), se.getHarvesterDialogProperties(), tb);			
	         
	         
	     } catch (Exception e){} 
    	 
	  } catch (Exception e) {
	  }
	}


	/**
	 *  Description of the Method
	 */
	protected void cleaningUp() {
	}


	/**
	 *  Description of the Method
	 *
	 * @return    Description of the Return Value
	 */
	protected boolean closing() {
		try {
		} catch (Exception e) {
			CException.record(e, this, false);
		}

		return true;
	}
    

	/**
	 *  Description of the Method
	 *
	 * @exception  COpenFailedException  Description of the Exception
	 */
	protected void opened() throws COpenFailedException {

		try {
			parser = new SAXBuilder();

			temps = (TemplateSubsystem) CServiceProvider.getService(ServiceNames.TEMPLATESUBSYSTEM);
			res =(ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);
			user = (User) CServiceProvider.getService(ServiceNames.CURRENT_USER);
			
   		    Format format = Format.getRawFormat();
   		    format.setEncoding("UTF-8");
   		    outputter = new XMLOutputter(format);

			CDialogTools.createButtonListener(this, "jbClose", "handleCloseButton");
			CDialogTools.createButtonListener(this, "jbStart", "handleStartButton");
			CDialogTools.createButtonListener(this, "jbShowLogfile", "handleShowLogfileButton");			
  		    getGuiComposite().getWidget("jbShowLogfile").setEnabled(false);

		} catch (Exception ex) {
			throw new COpenFailedException(ex);
		} finally {
		}
	}


	private ResourceBundle res; 
	private TemplateSubsystem  temps;
	private Document doc;
	private Document metadata;
	private Element root;
	private SAXBuilder parser;
	private XMLOutputter outputter;
	private Session se;
	private User user;
	private String logfile;
	private String logdir;
	private FileWriter logger;
	private EDM edm;
}


