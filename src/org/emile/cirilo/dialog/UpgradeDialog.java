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
import org.emile.cirilo.ServiceNames;
import org.emile.cirilo.User;
import org.emile.cirilo.business.TEI;
import org.emile.cirilo.business.LIDO;
import org.emile.cirilo.ecm.repository.Repository;
import org.emile.cirilo.ecm.templates.TemplateSubsystem;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.DOMBuilder;
import org.jdom.input.SAXBuilder;
import org.jdom.output.DOMOutputter;
import org.jdom.output.XMLOutputter;
import org.jdom.output.Format;

import org.jdom.xpath.*;
import org.xml.sax.InputSource;






import org.xml.sax.InputSource;

import java.awt.Cursor;
import java.awt.event.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.StringReader;
import java.io.FilenameFilter;
import java.io.StringWriter;
import java.util.List;
import java.util.Iterator;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.asprise.util.ui.progress.ProgressDialog;

import fedora.client.FedoraClient;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.ResourceBundle;

import voodoosoft.jroots.core.CPropertyService;
import voodoosoft.jroots.core.CServiceProvider;
import voodoosoft.jroots.core.gui.CEventListener;
import voodoosoft.jroots.dialog.*;


/**
 *  Description of the Class
 *
 * @author     Johannes Stigler
 * @created    13. Februar 2005
 * @version    1.1
 */
public class UpgradeDialog extends CDialog {
    private static final Log LOG = LogFactory.getLog(UpgradeDialog.class);
    
    Document properties; 
    Format format;
    XMLOutputter outputter;
    
	/**
	 *  Constructor for the LoginDialog object
	 */
	public UpgradeDialog() { }




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
			User user = (User) CServiceProvider.getService(ServiceNames.CURRENT_USER);
			ArrayList groups = (ArrayList) CServiceProvider.getService(ServiceNames.MEMBER_LIST);

			format = Format.getRawFormat();
			format.setEncoding("UTF-8");
			outputter = new XMLOutputter(format);

			res =(ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);
			moGA = (IGuiAdapter) getGuiAdapter();

			CDialogTools.createButtonListener(this, "jbOK", "handleOKButton");
			CDialogTools.createButtonListener(this, "jbCancel", "handleCancelButton");
			JLabel jl = (JLabel) getGuiComposite().getWidget("jlText");
			
			try {
				DOMBuilder db = new DOMBuilder();	
				properties = db.build (Repository.getDatastream("cirilo:Backbone", "PROPERTIES"));
			
				int installed = new Integer(properties.getRootElement().getChild("ContentModels").getText());
				int current = new Integer(Common.CM_VERSION);
				
				if (current >= installed) {
					jl.setText(hint);
					JButton jb = (JButton) getGuiComposite().getWidget("jbOK");
					jb.setEnabled(false);
				}
				
				if (!groups.contains("administrator")) {
					jl.setText(credentials);
					JButton jb = (JButton) getGuiComposite().getWidget("jbOK");
					jb.setEnabled(false);
				}

			} catch (Exception e) {				
			}	

		} catch (Exception ex) {
			throw new COpenFailedException(ex);
		}
	}

	public void handleOKButton(ActionEvent e) 
			throws Exception {
			new Thread() {
				public void run() {
					ResourceBundle res = null;
					Boolean fin = true;
					try {
						CPropertyService props = (CPropertyService) CServiceProvider.getService(ServiceNames.PROPERTIES);
						res =(ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);
						TemplateSubsystem temps = (TemplateSubsystem) CServiceProvider.getService(ServiceNames.TEMPLATESUBSYSTEM);
						User user = (User) CServiceProvider.getService(ServiceNames.CURRENT_USER);

						String fedora = user.getUrl();						
						String host = fedora.substring(0,fedora.lastIndexOf("/"));
						String cocoon = host+"/cocoon";
						String format = FedoraClient.FOXML1_1.uri;
						
				    	SAXBuilder builder = new SAXBuilder();
				    	XMLOutputter outputter = new XMLOutputter();
				    	DOMOutputter domoutputter = new DOMOutputter();


						int liChoice = JOptionPane.showConfirmDialog(getCoreDialog(), "Upgrading: Are you really sure? " ,
								Common.WINDOW_HEADER, JOptionPane.YES_NO_OPTION,
								JOptionPane.QUESTION_MESSAGE);

						if (liChoice == 0) {

							getCoreDialog().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
							
							JFileChooser chooser = new JFileChooser(props.getProperty("user", "ingest.import.path"));
							chooser.setDialogTitle(res.getString("chooseimdir"));
							chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
							if (chooser.showDialog(null, res.getString("choose")) == JFileChooser.APPROVE_OPTION) {
								entries = new ArrayList<String>();
								File fp = chooser.getSelectedFile();
						    	treeWalk(fp);						
								ProgressDialog progressDialog = new ProgressDialog( getCoreDialog(), Common.WINDOW_HEADER);
								progressDialog.displayPercentageInProgressBar = true;
								progressDialog.millisToDecideToPopup = 1;
								progressDialog.millisToPopup = 1;

					   		    progressDialog.beginTask("Upgrading content models ...", entries.size(), true);
																
								progressDialog.worked(1);
	                 
								for (String s: entries) {

									if(progressDialog.isCanceled()) {
										fin = false;
										break;
									}				

									progressDialog.worked(1);
		 										
									try {
								    	org.jdom.Document doc = builder.build(new File(s));
				  	    				XPath xpath = XPath.newInstance("/foxml:digitalObject");
				  	    				xpath.addNamespace( Common.xmlns_foxml );
				  	    				org.jdom.Element object = (org.jdom.Element) xpath.selectSingleNode( doc );
								    	
								    	String pid = object.getAttributeValue("PID");
								    	
								    	if (Repository.exist(pid)) {
								    		Repository.purgeObject(pid);
								    	}	
								    	
										String foxml = outputter.outputString(doc)
												.replaceAll("http://fedora.host/fedora", fedora)
												.replaceAll("http://fedora.host/cocoon", cocoon)
												.replaceAll("http://fedora.host", host)
												.replaceAll(host+"#", "http://gams.uni-graz.at#")
												.replaceAll(host+"/ontology#","http://gams.uni-graz.at/ontology#");
	
										doc = builder.build(new StringReader(foxml));
										Repository.ingestDocument(domoutputter.output(doc),  format, "Object ingested by Cirilo");
										
									  } catch (Exception e) {
											fin = false;									
									  }	
									  
									  try {
											Thread.sleep(50); 
									  } catch (InterruptedException e) {
									  }

									}																				
					
								}

	
								entries = Repository.getPidList("");
							
								ProgressDialog progressDialog = new ProgressDialog( getCoreDialog(), Common.WINDOW_HEADER);
								progressDialog.displayPercentageInProgressBar = true;
								progressDialog.millisToDecideToPopup = 1;
								progressDialog.millisToPopup = 1;

								progressDialog.beginTask("Upgrading data objects ...", entries.size(), true);
							
							
								progressDialog.worked(1);
													
                 
								for (String s: entries) {

									if(progressDialog.isCanceled()) {
										fin = false;									
										break;
									}				

									progressDialog.worked(1);
									try {

										if (Repository.hasContentModel(s,"info:fedora/cm:TEI")) {	
											if(!Repository.exists(s, "SOURCE_REF")) {										
												Repository.addDatastream(s, "SOURCE_REF",  "Reference to source stream", "text/xml", fedora+"/get/"+s+"/TEI_SOURCE");
											}	
											if(!Repository.exists(s, "HSSF_STYLESHEET")) {										
												Repository.addDatastream(s, "HSSF_STYLESHEET",  "Stylesheet to generate HSSF stream", "text/xml", fedora+"/get/cirilo:Backbone/TEItoHSSF");
											}	
										}
	 							
										if (Repository.hasContentModel(s,"info:fedora/cm:dfgMETS")) {																			
											if(!Repository.exists(s, "METS_REF")) {
												Repository.addDatastream(s, "SOURCE_REF",  "Reference to source stream", "text/xml", fedora+"/get/"+s+"/METS_SOURCE");
											}	
										}

										if (Repository.hasContentModel(s,"info:fedora/cm:LIDO")) {																			
											if(!Repository.exists(s, "SOURCE_REF")) {
												Repository.addDatastream(s, "SOURCE_REF",  "Reference to source stream", "text/xml", fedora+"/get/"+s+"/LIDO_SOURCE");
											}	
										}
								
										if (Repository.hasContentModel(s,"info:fedora/cm:Context")) {	
											if(!Repository.exists(s, "HSSF_STYLESHEET")) {										
												Repository.addDatastream(s, "HSSF_STYLESHEET",  "Stylesheet to generate HSSF stream", "text/xml", fedora+"/get/cirilo:Backbone/HSSF_STYLESHEET");
											}	
										}
								
									} catch (Exception e) {
										fin = false;									
									}

									try {
										Thread.sleep(50); 
									} catch (InterruptedException e) {
									}

				
								} 
							
						} else {
							close();
						}
						} catch (Exception ex) {
						}
						finally {
							try {	
								if (fin) {
									JLabel jl = (JLabel) getGuiComposite().getWidget("jlText");
									jl.setText(hint);
									JButton jb = (JButton) getGuiComposite().getWidget("jbOK");
									jb.setEnabled(false);
									Element cm = properties.getRootElement().getChild("ContentModels");
									cm.setText(Common.CM_VERSION);
									Repository.modifyDatastreamByValue("cirilo:Backbone", "PROPERTIES", "text/xml", new String(outputter.outputString(properties).getBytes("UTF-8"),"UTF-8"));
								}	
							} catch (Exception e) {}
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
	     		entries.add(file.getAbsolutePath());
	     	}
		  } catch (Exception e) {e.printStackTrace();}	
	  	}	

	
		private CPropertyService props;
		private IGuiAdapter moGA;
		private ResourceBundle res;
		private ArrayList<String> entries;
		
		private static String hint = "Note: The system is up to date";
		private static String credentials = "Note: Upgrading needs admin credentials";
}

