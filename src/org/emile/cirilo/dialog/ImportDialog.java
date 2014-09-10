package org.emile.cirilo.dialog;

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


import org.emile.cirilo.Common;
import org.emile.cirilo.business.TEI;

import org.emile.cirilo.ServiceNames;
import org.emile.cirilo.User;
import org.emile.cirilo.ecm.repository.Repository;
import org.emile.cirilo.ecm.templates.TemplateSubsystem;
import org.jdom.output.XMLOutputter;
import org.openrdf.repository.manager.RemoteRepositoryManager;

import javax.xml.transform.stream.StreamResult;

import org.xml.sax.InputSource;

import java.awt.Cursor;
import java.awt.event.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.StringWriter;
import java.io.StringReader;
import java.io.FilenameFilter;

import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.*;
import javax.swing.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.asprise.util.ui.progress.ProgressDialog;

import fedora.client.FedoraClient;




import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import voodoosoft.jroots.core.CPropertyService;
import voodoosoft.jroots.core.CServiceProvider;
import voodoosoft.jroots.core.gui.CEventListener;
import voodoosoft.jroots.dialog.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;


/**
 *  Description of the Class
 *
 * @author     Johannes Stigler
 * @created    13. Februar 2005
 * @version    1.1
 */
public class ImportDialog extends CDialog {
    private static final Log LOG = LogFactory.getLog(ImportDialog.class);
    
	/**
	 *  Constructor for the LoginDialog object
	 */
	public ImportDialog() { }




	/**
	 *  Description of the Method
	 *
	 * @param  e  Description of the Parameter
	 */
	public void handleCancelButton(ActionEvent e) {
		close();
	}

	/**
	 *  Description of the Method
	 *
	 * @param  e  Description of the Parameter
	 */
	public void handleShowlogButton(ActionEvent e)
		throws Exception {
			TextEditor dlg = (TextEditor) CServiceProvider.getService(DialogNames.TEXTEDITOR);
			dlg.set(logfile, null, "text/log", "R", null);
			dlg.open();
	}	


	/**
	 *  Description of the Method
	 *
	 * @param  aoHandler  Description of the Parameter
	 */
	public void handlerRemoved(CEventListener aoHandler) {
	}


	/**
	 *  Description of the Method
	 */
	protected void cleaningUp() {
	}


	/**
	 *  Description of the Method
	 *
	 * @exception  COpenFailedException  Description of the Exception
	 */
	protected void opened() throws COpenFailedException {

		try {
			
			props = (CPropertyService) CServiceProvider.getService(ServiceNames.PROPERTIES);

			res =(ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);
			moGA = (IGuiAdapter) getGuiAdapter();
			

			// map buttons
			CDialogTools.createButtonListener(this, "jbOK", "handleOKButton");
			CDialogTools.createButtonListener(this, "jbCancel", "handleCancelButton");
			CDialogTools.createButtonListener(this, "jbShow", "handleShowlogButton");

			User us = (User) CServiceProvider.getService(ServiceNames.CURRENT_USER);

 		
			getGuiComposite().getWidget("jbShow").setEnabled(false);

		} catch (Exception ex) {
			ex.printStackTrace();
			throw new COpenFailedException(ex);
		}
	}

	public void handleOKButton(ActionEvent e) 
			throws Exception {
			new Thread() {
				public void run() {
					ResourceBundle res = null;
					FileWriter logger = null;
					String model = "";
					int fi = 0;
					int fe = 0;
					int ff = 0;
					String cm = null;
					try {
						CPropertyService props = (CPropertyService) CServiceProvider.getService(ServiceNames.PROPERTIES);
						res =(ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);
						TemplateSubsystem temps = (TemplateSubsystem) CServiceProvider.getService(ServiceNames.TEMPLATESUBSYSTEM);
						User user = (User) CServiceProvider.getService(ServiceNames.CURRENT_USER);
					
						String fedora = user.getUrl();						
						String host = fedora.substring(0,fedora.lastIndexOf("/"));
						String cocoon = host+"/cocoon";
						
						JFileChooser chooser = new JFileChooser(props.getProperty("user", "ingest.import.path"));
						
						DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				    	DocumentBuilder builder = factory.newDocumentBuilder();

				    	files = new ArrayList<String>();

						chooser.setDialogTitle(res.getString("chooseimdir"));
						chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				
						if (chooser.showDialog(null, res.getString("choose")) != JFileChooser.APPROVE_OPTION) {
							return;
						}

						props.setProperty("user", "ingest.import.path", chooser.getCurrentDirectory().getAbsolutePath());
						props.saveProperties("user");
						
						File fp = chooser.getSelectedFile();
						logger = new FileWriter( fp.getAbsolutePath()+System.getProperty( "file.separator" )+"import.log" );
						
						
						getCoreDialog().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

						
						files = new ArrayList<String>();
						treeWalk(fp);
						
						MessageFormat msgFmt = new MessageFormat(res.getString("objimp"));
						Object[] args = {new Integer(files.size()).toString(), fp.getAbsolutePath()};
			            
						int liChoice = JOptionPane.showConfirmDialog(getCoreDialog(), msgFmt.format(args) ,
								Common.WINDOW_HEADER, JOptionPane.YES_NO_OPTION,
								JOptionPane.QUESTION_MESSAGE);

						if (liChoice == 0) {

							ProgressDialog progressDialog = new ProgressDialog( getCoreDialog(), Common.WINDOW_HEADER);
							progressDialog.displayPercentageInProgressBar = true;
							progressDialog.millisToDecideToPopup = 1;
							progressDialog.millisToPopup = 1;

				   		    progressDialog.beginTask(res.getString("ingestcont"), files.size(), true);
							
							
							progressDialog.worked(1);
													

							for (int i = 0; i<files.size(); i++) {

								if(progressDialog.isCanceled()) {
									break;
								}				

								progressDialog.worked(1);
	 															
								try {
									Thread.sleep(50); 
								} catch (InterruptedException e) {
								}
								
								try {
							    	Document doc = builder.parse((String) files.get(i));
									String pid = doc.getDocumentElement().getAttribute("PID");
									String objid = doc.getDocumentElement().getAttribute("OBJID");
									String version = doc.getDocumentElement().getAttribute("VERSION");
									String ext_version = doc.getDocumentElement().getAttribute("EXT_VERSION");
									
									String format = null;

									if ( !pid.isEmpty()  && version.equals("1.1") ) format = FedoraClient.FOXML1_1.uri;
									if ( !pid.isEmpty()  && version.isEmpty() ) format = FedoraClient.FOXML1_0.uri;
									if ( !objid.isEmpty() && ext_version.equals("1.1") ) { format = FedoraClient.METS_EXT1_1.uri; pid = objid; }
									if ( !objid.isEmpty() && ext_version.isEmpty() ) { format = FedoraClient.METS_EXT1_0.uri; pid = objid; }
									
									if (format != null) {
										MessageFormat msgFmt0 = new MessageFormat(res.getString("attingest"));
										Object[] arg0 = {files.get(i), format, pid};
										
										DOMSource domSource = new DOMSource(doc);
										StringWriter writer = new StringWriter();
										StreamResult result = new StreamResult(writer);
										TransformerFactory tf = TransformerFactory.newInstance();
										Transformer transformer = tf.newTransformer();
										transformer.transform(domSource, result);
										InputSource is = new InputSource();
										is.setCharacterStream(new StringReader(writer.toString()
									    .replaceAll("http://fedora.host/fedora", fedora)
										.replaceAll("http://fedora.host/cocoon", cocoon)
									    .replaceAll("http://fedora.host", host)
										.replaceAll(host+"#", "http://gams.uni-graz.at#")
										.replaceAll(host+"/ontology#","http://gams.uni-graz.at/ontology#")));
										doc = builder.parse(is);
										
										logger.write("\n " + new java.util.Date() + ". "+ msgFmt0.format(arg0));
										if (!Repository.exist(pid)) {	
											try {
												Repository.ingestDocument(doc,  format, "Object ingested by Cirilo");
												try {
													while (!Repository.exist(pid));
													Repository.getDatastream(pid, "RELS-EXT");
													
													List <org.emile.cirilo.ecm.repository.FedoraConnector.Relation> relations = Repository.getRelations(pid, Common.hasModel);		
								 	  			    for (org.emile.cirilo.ecm.repository.FedoraConnector.Relation r: relations) {								 	  			
								 	  			    	String p = r.getTo();
								 	  			    	if (p.startsWith("info:fedora/cm:")) {
								 	  			    		cm =  p;
								 	  			    	}
								 	  			    }
								 	  			    Common.genQR(user, pid);
								 	  			    if (Common.ONTOLOGYOBJECTS.contains(cm)) {
							 				    		try {
							 				    			

					  	 	 				    			String ses = (String) props.getProperty("user", "sesame.server");
					  	 						        	RemoteRepositoryManager repositoryManager = new RemoteRepositoryManager(ses == null ? Common.SESAME_SERVER : ses);
					  	 						        	repositoryManager.setUsernameAndPassword(user.getUser(), user.getPasswd());
					  	 						        	repositoryManager.initialize();	 				           	
					  	 						        	org.openrdf.repository.Repository repo = repositoryManager.getRepository(user.getUrl().substring(7).replace("/",".")); 	
					  	 						        	repo.initialize(); 				    			
					  	 						        	org.openrdf.repository.RepositoryConnection con = repo.getConnection();	 				    			
					  	 						        	con.clear(new org.openrdf.model.impl.URIImpl(pid)); 							 							  				
							 				    			
					  	 					           		File temp = File.createTempFile("tmp","xml");
					  	 					           		FileOutputStream fos = new FileOutputStream(temp);
					  	 								  	byte[] buf = Repository.getDatastream(pid,"TEI_SOURCE", "");
					  	 					           		fos.write(buf);
					  	 					           		fos.close();
					  	 					           		con.add(temp.getAbsoluteFile(), null, org.openrdf.rio.RDFFormat.RDFXML, new org.openrdf.model.impl.URIImpl(pid));
					  	 					           		temp.delete();
							 				    		} catch (Exception e) {
							 				    		}	
								 	  			    } else if  (Common.TEIOBJECTS.contains(cm)) {
								 	  					try {
							    	            		   TEI t = new TEI(null,false,true);
							    	            		   byte[] buf = Repository.getDatastream(pid,"TEI_SOURCE", "");
							    	            		   t.set(new String(buf,"UTF-8"));
							    	            		   t.setPID(pid);
							    	            		   t.refresh();
							 	  						} catch (Exception e) {
							 	  						}
								 	  					
								 	  			    }

								 	  			    
												} catch (Exception u) {
                                                    try {                                                              		  	 					           		
                                                    	String rdf = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">"+
                                                    				 "<rdf:Description rdf:about=\"info:fedora/"+pid+"\">"+
                                                    			     "<hasModel xmlns=\"info:fedora/fedora-system:def/model#\" rdf:resource=\"info:fedora/cm:DefaultContentModel-1.0\" />"+
                                                    			     "</rdf:Description>"+
                                                    			     "</rdf:RDF>";
                                                    	
            	  	 					           		File temp = File.createTempFile("tmp","xml");
            	  	 					           		FileOutputStream fos = new FileOutputStream(temp);
            	  	 					           		fos.write(rdf.getBytes("UTF-8"));
            	  	 					           		fos.close();
                                                    	Repository.addDatastream(pid, "RELS-EXT","RDF Statements about this object",  "X", "text/xml", temp);
            	  	 					           		temp.delete();
                                                    	
                                                    } catch (Exception o) {}	
									
												}
												fi++;
												logger.write(" ... Ok");
											} catch (Exception q) {
												ff++;
												logger.write(" ... "+res.getString("ingfail"));										
											}
										} else {
											fe++;
											logger.write(" ... "+res.getString("alrexist"));
										}
									} else {
										MessageFormat msgFmt1 = new MessageFormat(res.getString("novalidobj"));
										Object[] arg1 = {files.get(i)};
										logger.write("\n " + new java.util.Date() + ". "+ msgFmt1.format(arg1));
										ff++;
									}	
								} catch (Exception w) {
								}
				
							}
							String m = new Integer(fi).toString().trim() + res.getString("imported")+ new Integer(fe).toString().trim() + res.getString("existed")+ new Integer(ff).toString().trim() + res.getString("failed");
							logger.write("\n" +res.getString("end")+res.getString("ofimport") + new java.util.Date() + ". " + m);									
							logger.close();
																		        	
							logfile = fp.getAbsolutePath()+System.getProperty( "file.separator" )+"import.log"; 
							getGuiComposite().getWidget("jbShow").setEnabled(true);
							
							JOptionPane.showMessageDialog( getCoreDialog(), m, Common.WINDOW_HEADER, JOptionPane.INFORMATION_MESSAGE );					
							getCoreDialog().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

						}		
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					finally {
						getCoreDialog().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					}
			
				}
			}.start();
			
		}

		private void treeWalk(File file) {
			  try {
		 		if (file.isDirectory()) {
			 		File[] children = file.listFiles();
			 		for (int i = 0; i < children.length; i++) {
			 			treeWalk(children[i]);
			 		}
		     	} else if (file.getAbsolutePath().toLowerCase().endsWith(".xml")) {
		     		files.add(file.getAbsolutePath());
		     	}
			  } catch (Exception e) {e.printStackTrace();}	
		}	
		
		class XMLFilter implements FilenameFilter
		{
			public boolean accept( File f, String s )
			{
				return s.toLowerCase().endsWith( ".xml" );
			}
		}



		private CPropertyService props;
		private IGuiAdapter moGA;
		private ResourceBundle res;
		private static ArrayList<String> files;
		private String logfile;

}

