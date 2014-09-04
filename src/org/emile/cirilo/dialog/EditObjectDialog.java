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

import voodoosoft.jroots.core.CServiceProvider;
import voodoosoft.jroots.core.CPropertyService;
import voodoosoft.jroots.core.gui.CEventListener;
import voodoosoft.jroots.core.gui.CItemListener;
import voodoosoft.jroots.core.gui.CMouseListener;
import voodoosoft.jroots.dialog.*;
import voodoosoft.jroots.exception.CException;




import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.emile.cirilo.Common;
import org.emile.cirilo.ServiceNames;
import org.emile.cirilo.User;
import org.emile.cirilo.dialog.ObjectEditorDialog.PopupoListener;
import org.emile.cirilo.ecm.templates.*;
import org.emile.cirilo.ecm.repository.*;
import org.emile.cirilo.business.*;
import org.emile.cirilo.*;
import org.emile.cirilo.gui.jtable.DefaultSortTableModel;
import org.emile.cirilo.ecm.repository.FedoraConnector.Relation;
import org.emile.cirilo.utils.ImageTools;
import org.emile.cirilo.utils.Split;
import org.jdom.input.DOMBuilder;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;
import org.jdom.filter.ElementFilter;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Attribute;
import org.jdom.input.*;
import org.openrdf.repository.manager.RemoteRepositoryManager;

import com.asprise.util.ui.progress.ProgressDialog;

import fedora.server.access.FedoraAPIA;
import fedora.server.management.FedoraAPIM;
import fedora.server.utilities.StreamUtility;

import org.emile.cirilo.ServiceNames;
import org.emile.cirilo.business.MDMapper;
import org.emile.cirilo.ecm.repository.Repository;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;
import java.net.URL;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.Transformer;



/**
 *  Description of the Class
 *
 * @author     Johannes Stigler
 * @created    10.3.2011
 */
public class EditObjectDialog extends CDialog {
    private static final Log LOG = LogFactory.getLog(EditObjectDialog.class);

	/**
	 *  Constructor for the LoginDialog object
	 */

	public EditObjectDialog() {}
	
	public void handleObjectReplace(ArrayList sb)
	throws Exception {
		
	   final ArrayList substitutions = sb;
	    new Thread() {
			public void run() {

				FileWriter	logger = null;
				try {
					  JTable loTable = (JTable) getGuiComposite().getWidget(jtData);
					  Boolean hasRelations = false;
					  
					  Format format = Format.getRawFormat();
					  format.setEncoding("UTF-8");
 					  XMLOutputter outputter = new XMLOutputter(format);		   			   														

  					  SAXBuilder builder = new SAXBuilder(); 						 						
					  builder = new SAXBuilder();				    						

					  int[] selected = loTable.getSelectedRows();
					  ProgressDialog pd = new ProgressDialog(getCoreDialog(), Common.WINDOW_HEADER);
					  pd.displayPercentageInProgressBar = true;
					  pd.beginTask(res.getString("replace"), selected.length, true);
					  
					  logger = new FileWriter( System.getProperty( "user.home" )+System.getProperty( "file.separator" )+"replace.log" );
					  
					  int deleted = 0;
					  
					  MessageFormat msgFmt = new MessageFormat(res.getString("objmod"));
	 					Object[] args = {new Integer(selected.length).toString()};
	 					
				      int liChoice = JOptionPane.showConfirmDialog(null, msgFmt.format(args), res.getString("replaceobjc"), JOptionPane.YES_NO_OPTION,
				    		  						JOptionPane.QUESTION_MESSAGE);
					 
//					  System.setProperty("javax.xml.transform.TransformerFactory",  "net.sf.saxon.TransformerFactoryImpl");			
                      TransformerFactory transFact = TransformerFactory.newInstance();
                      ByteArrayOutputStream os;
				      Transformer trans = null;
				      Source xsltSource = null;
				      
				      byte[] url = null;
				      org.jdom.Document mapping = null;;
				      MDMapper m = null;;
				      
			    	  int fi=0;
				      if (liChoice == 0) {
				    	  for (int i=0; i<selected.length; i++) {
				    		  if(pd.isCanceled()) {break;}		
				    		  String pid =(String)loTable.getValueAt(selected[i],0);
				    		  String model =(String)loTable.getValueAt(selected[i],2);

							  logger.write( new java.util.Date()  +" " + pid + " ");									                                    	
				    		  
				    		  DOMBuilder db = new DOMBuilder();
				    		  try {

				    		  for (int j = 0; j < substitutions.size(); j++) {
				    			  String p = (String) substitutions.get(j);
				    			  if (p.substring(0,1).equals(Common.DCMAPPING)) {
				    				  if (p.substring(1,2).equals(Common.REPLACE)) {
                                         try {
  				    					   org.jdom.Document tei = db.build (Repository.getDatastream(pid, "TEI_SOURCE"));
			    	            		   TEI t = new TEI(null,false,true);
			    	            		   t.set(outputter.outputString(tei));
			    	            		   t.setPID(pid);
			    	            		   t.validate(null, null);
 	    				  		   		   Repository.modifyDatastreamByValue(pid, "TEI_SOURCE", "text/xml", new String(t.toString().getBytes("UTF-8"),"UTF-8"));
				    					 } catch (Exception q) {}  
				    				  }	  
				    			  }
				    		  }

				    		  Document doc = db.build (Repository.getDatastream(pid, "DC"));
				    		  Element root = doc.getRootElement();
				    			  				    			  
				    			  
				    		  for (int j = 0; j < substitutions.size(); j++) {
				    			  String p = (String) substitutions.get(j);
				    			  if (p.substring(0,1).equals(Common.DUBLIN_CORE)) {
				    				  if (p.substring(1,2).equals(Common.REPLACE)) {
				    					  root.removeContent(new ElementFilter(Common.DCMI[j].toLowerCase()));
				    				  }
				    			  }
				    		  }
				    		  
				    		  for (int j = 0; j < substitutions.size(); j++) {
				    			  String p = (String) substitutions.get(j);
				    			  if (p.substring(0,1).equals(Common.DUBLIN_CORE)) {
				    				  if (!p.substring(1,2).equals(Common.UNALTERED) && !p.substring(2).isEmpty()) {
				    					  java.util.List children = root.getChildren(Common.DCMI[j].toLowerCase(), Common.xmlns_dc);
				    					  boolean found = false;
				    					  for ( Object e : children) {				    						  
				    						  if (((Element) e).getText().equals(p.substring(2))) found=true;;
				    					  }
				    					  if (!found) {
				    						  Element dc = new Element(Common.DCMI[j].toLowerCase(), Common.xmlns_dc);
				    						  dc.addContent(p.substring(2));
				    						  root.addContent(dc);
				    					  }
				    				  }
				    			  }
				    		  }
				    		  
				    		  Repository.modifyDatastreamByValue(pid, "DC", "text/xml", outputter.outputString(doc));
				    		  
				    		  
				    		  for (int j = 0; j < substitutions.size(); j++) {
				    			  String p = (String) substitutions.get(j);
				    			  if (p.substring(0,1).equals(Common.OAIPROVIDER)) {
				    				  if (p.substring(1,2).equals(Common.REPLACE)) {
							    		  doc = db.build (Repository.getDatastream(pid, "RELS-EXT"));
			                              Element rdf = doc.getRootElement().getChild("Description", Common.xmlns_rdf);
				    					  rdf.removeContent(new ElementFilter("itemID"));			                              
				    					  if(p.substring(2).equals("true")) {
				    						  Element oai = new Element("itemID", Common.xmlns_oai);
				    						  oai.addContent(Common.URN+":"+pid);
				    						  rdf.addContent(oai);
				    					  }
							    		  Repository.modifyDatastreamByValue(pid, "RELS-EXT", "text/xml", outputter.outputString(doc));
				    				  }	  
				    			  }
				    		  }
				    		  
				    		  for (int j = 0; j < substitutions.size(); j++) {
				    			  String p = (String) substitutions.get(j);
				    			  if (p.substring(0,1).equals(Common.HTML_LAYOUT)) {
				    				  if (p.substring(1,2).equals(Common.REPLACE)) {
				    					  try {
				    					  Repository.modifyDatastream (pid, "STYLESHEET", null, "R", p.substring(2));
				    					 } catch (Exception q) {}  

				    				  }	  
				    			  }
				    		  }
 
				    		  for (int j = 0; j < substitutions.size(); j++) {
				    			  String p = (String) substitutions.get(j);
				    			  if (p.substring(0,1).equals(Common.DC_MAPPING)) {
				    				  if (p.substring(1,2).equals(Common.REPLACE)) {
				    					  try {
				    					  Repository.modifyDatastream (pid, "DC_MAPPING", null, "R", p.substring(2));
				    					 } catch (Exception q) {}  
				    				  }	  
				    			  }
				    		  }
 
				    		  for (int j = 0; j < substitutions.size(); j++) {
				    			  String p = (String) substitutions.get(j);
				    			  if (p.substring(0,1).equals(Common.RDF_MAPPING)) {
				    				  if (p.substring(1,2).equals(Common.REPLACE)) {
				    					  try {
				    					  Repository.modifyDatastream (pid, "RDF_MAPPING", null, "R", p.substring(2));
				    					 } catch (Exception q) {}  
				    				  }	  
				    			  }
				    		  }
				    		  for (int j = 0; j < substitutions.size(); j++) {
				    			  String p = (String) substitutions.get(j);
				    			  if (p.substring(0,1).equals(Common.BIBTEX_MAPPING)) {
				    				  if (p.substring(1,2).equals(Common.REPLACE)) {
				    					  try {
				    					  Repository.modifyDatastream (pid, "BIBTEX_MAPPING", null, "R", p.substring(2));
				    					 } catch (Exception q) {}  
				    				  }	  
				    			  }
				    		  }
				    		  for (int j = 0; j < substitutions.size(); j++) {
				    			  String p = (String) substitutions.get(j);
				    			  if (p.substring(0,1).equals(Common.KML_TEMPLATE)) {
				    				  if (p.substring(1,2).equals(Common.REPLACE)) {
				    					  try {
				    					  Repository.modifyDatastream (pid, "KML_TEMPLATE", null, "R", p.substring(2));
				    					 } catch (Exception q) {}  
				    				  }	  
				    			  }
				    		  }
				    		  for (int j = 0; j < substitutions.size(); j++) {
				    			  String p = (String) substitutions.get(j);
				    			  if (p.substring(0,1).equals(Common.REPLACEMENT_RULESET)) {
				    				  if (p.substring(1,2).equals(Common.REPLACE)) {
				    					  try {
				    					  Repository.modifyDatastream (pid, "REPLACEMENT_RULESET", null, "R", p.substring(2));
				    					 } catch (Exception q) {}  
				    				  }	  
				    			  }
				    		  }
				    		  for (int j = 0; j < substitutions.size(); j++) {
				    			  String p = (String) substitutions.get(j);
				    			  if (p.substring(0,1).equals(Common.TORDF)) {
				    				  if (p.substring(1,2).equals(Common.REPLACE)) {
                                          try {
				    					  Repository.modifyDatastream (pid, "TORDF", null, "R", p.substring(2));
 				    					 } catch (Exception q) {}  
				    				  }	  
				    			  }
				    		  }
				    		  for (int j = 0; j < substitutions.size(); j++) {
				    			  String p = (String) substitutions.get(j);
				    			  if (p.substring(0,1).equals(Common.TOMETS)) {
				    				  if (p.substring(1,2).equals(Common.REPLACE)) {
				    					  try {
				    					  Repository.modifyDatastream (pid, "TOMETS", null, "R", p.substring(2));
				    											    					 } catch (Exception q) {}  
				    				  }	  
				    			  }
				    		  }
 				    		  
				    		  				    		  
				    		  
				    		  try {
    						  
				    		  for (int j = 0; j < substitutions.size(); j++) {
				    			  String p = (String) substitutions.get(j);
				    			  if (p.substring(0,1).equals(Common.XSLT)) {
				    				 
				    				  if (!p.substring(0,1).equals("0")){
				    					  try {
				    					  if (! p.substring(1,2).equals(Common.VOYANT)) { 
				    						  Split param = new Split(p.substring(2));
				    					  	os = new ByteArrayOutputStream();
				    					  	if (i == 0 ) {
				    						  	xsltSource = new StreamSource(new File(param.get(0)));
				    						  	trans = transFact.newTransformer(xsltSource);
				    		                    trans.setParameter("pid", pid);
				    					  	}					    				  
			    						  	DOMSource domSource=new DOMSource(Repository.getDatastream(pid, param.get(1)));
			    						  
			    						  	trans.transform(domSource, new StreamResult(os));
				    					  	if (p.substring(1,2).equals(Common.SIMULATE)) {
				    						  	logger.write("\n"+new String(os.toByteArray(),"UTF-8")+"\n" );
				    					  	}	  
				    					  	else if (p.substring(1,2).equals(Common.ADD)) {
				    						  	Repository.modifyDatastreamByValue(pid, param.get(1), "text/xml", new String(os.toByteArray(),"UTF-8") );
				    					  	}	  
				    					  }
				    					  if ( p.substring(1,2).equals(Common.VOYANT)) {
				    						  Repository.addDatastream(pid, "VOYANT",  "Reference to Voyant Tools", "text/xml", "http://voyant-tools.org?input=http://gams.uni-graz.at/archive/objects/"+pid+"/datastreams/TEI_SOURCE/content");
				    					  }
				    					 } catch (Exception q) {}  
				    				  }
				    			  }
				    		  }
				    		  } catch (Exception eq) {}
				    		  
				    		  for (int j = 0; j < substitutions.size(); j++) {
				    			  String p = (String) substitutions.get(j);
				    			  if (p.substring(0,1).equals(Common.FO_LAYOUT)) {
				    				  if (p.substring(1,2).equals(Common.REPLACE)) {
				    					  try  {
				    					  Repository.modifyDatastream (pid, "FO_STYLESHEET", null, "R", p.substring(2));
				    					 } catch (Exception q) {}  
				    				  }	  
				    			  }				    		  
				    		  }
				    		  
				    		  for (int j = 0; j < substitutions.size(); j++) {
				    			  String p = (String) substitutions.get(j);
				    			  if (p.substring(0,1).equals(Common.QUERY)) {
				    				  if (p.substring(1,2).equals(Common.REPLACE)) {
				    					 try { 
				    				      File query = File.createTempFile( "tmp", "~" );		
				    				      BufferedWriter out = new BufferedWriter(new FileWriter(query));
				    				      out.write(p.substring(2).replaceAll("[$]self","fedora:"+pid));
				    				      out.close();
			    				    	  Repository.modifyDatastream(pid, "QUERY", "text/plain", "M", query);
				    				      query.delete();
				    					 } catch (Exception q) {}  
				    				      
				    				  }	  
				    			  }
				    		  }

				    		  for (int j = 0; j < substitutions.size(); j++) {
				    			  String p = (String) substitutions.get(j);
				    			  if (p.substring(0,1).equals(Common.OWNER)) {
				    				  if (p.substring(1,2).equals(Common.REPLACE)) {
				    					  try {
				    					  Repository.modifyObject( pid, null, null, p.substring(2));
				    					 } catch (Exception q) {}  
				    				  }	  
				    			  }				    		  
				    		  }
				    		  
                              boolean first=true;
				    		  for (int j = 0; j < substitutions.size(); j++) {
				    			  String p = (String) substitutions.get(j);
		    					  try {
				    			  if (p.substring(0,1).equals(Common.RELATIONS)) {
				    				  if (p.substring(1,2).equals(Common.REPLACE) && first) {
			    						  first=false;
							  			  java.util.List  <org.emile.cirilo.ecm.repository.FedoraConnector.Relation>relations = Repository.getRelations(pid,Common.isMemberOf);
							  			  for (Relation r: relations) {
							  				 Repository.purgeRelation("info:fedora/"+pid,Common.isMemberOf, r.getTo());
							  			  }
				    				  }	  
				    				  Split id = new Split(p.substring(2));				    				  
		    						  Repository.addRelation("info:fedora/"+pid,Common.isMemberOf,"info:fedora/"+id.get());
		    						  hasRelations = true;
				    			  }
		    					 } catch (Exception q) {}  
				    		  }

				    		  if (hasRelations) {
	    	            		   TEI t = new TEI(null,false,true);
	    	            		   if (t.get(pid)) {
	    	            			   t.createRELS_INT(null);
	    	            		   }	   
				    		  }
				    		  
			    			  fi++;
							  logger.write( "... ok\n");									                                    	

				    		  try {Thread.sleep(5);} catch (Exception e) {}	
				    		  
				    		  } catch (Exception eq) {
				    		  }
				    		  finally {
								  logger.write( "... error\n");									                                    	
					    		  pd.worked(1);
				    		  }
				    	}	
				    	   msgFmt = new MessageFormat(res.getString("objmodsuc"));
		 				Object[] args0 = {new Integer(fi).toString()}; 		    		
		 					JOptionPane.showMessageDialog(  getCoreDialog(), msgFmt.format(args0), Common.WINDOW_HEADER, JOptionPane.INFORMATION_MESSAGE);
						
					}

 					
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				finally {
					getCoreDialog().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					try {
						logger.close();
					} catch (Exception fe) {
					}
				}
		
			}
		}.start();
		
		}
	
	/**
	 *  Description of the Method
	 *
	 * @param  e  Description of the Parameter
	 */
	public void handleCancelButton(ActionEvent e) {
		try {
			org.emile.cirilo.dialog.CBoundSerializer.save(this.getCoreDialog(), se.getEditDialogProperties(),(JTable) getGuiComposite().getWidget(jtData));
		} catch (Exception ex) {}		
		close();
	}

	/**
	 *  Description of the Method
	 *
	 * @param  e  Description of the Parameter
	 */
	public void handleDelButton(ActionEvent e) {
			new Thread() {
				public void run() {
				  String last;	
				  try {

					  JTable loTable = (JTable) getGuiComposite().getWidget(jtData);
					  int[] selected = loTable.getSelectedRows();
					  ProgressDialog pd = new ProgressDialog(getCoreDialog(), Common.WINDOW_HEADER);
					  pd.displayPercentageInProgressBar = true;
					  pd.beginTask(res.getString("delete"), selected.length, true);
					  int deleted = 0;
					  
					  MessageFormat msgFmt = new MessageFormat(res.getString("objdel"));
		 				Object[] args = {new Integer(selected.length).toString()};
				      int liChoice = JOptionPane.showConfirmDialog(null, msgFmt.format(args) ,
				    		  						Common.WINDOW_HEADER, JOptionPane.YES_NO_OPTION,
				    		  						JOptionPane.QUESTION_MESSAGE);
					 
				      if (liChoice == 0) {
				    	  for (int i=0; i<selected.length; i++) {

				    		  if(pd.isCanceled()) {break;}		
				    		  String pid =(String)loTable.getValueAt(selected[i],0);
					    	  if ( pid.startsWith("cirilo:") && !groups.contains("administrator") ) {
					    		  	msgFmt = new MessageFormat(res.getString("nonsysadm"));
					    		  	Object[] argo = {pid};
									JOptionPane.showMessageDialog(  getCoreDialog(),msgFmt.format(argo), Common.WINDOW_HEADER, JOptionPane.INFORMATION_MESSAGE);
						    		continue;
						      }
				    		  String model =(String)loTable.getValueAt(selected[i],2);				    		  
				  			  if (model.contains("Ontology") || model.contains("SKOS") || model.contains("TEI")) {	
				  			    try {
				  			    	String ses = (String) props.getProperty("user", "sesame.server");
				  			    	RemoteRepositoryManager repositoryManager = new RemoteRepositoryManager(ses == null ? Common.SESAME_SERVER : ses);
					           		repositoryManager.setUsernameAndPassword(user.getUser(), user.getPasswd());
					           		repositoryManager.initialize();	 				           	
					           		org.openrdf.repository.Repository repo = repositoryManager.getRepository(user.getUrl().substring(7).replace("/",".")); 	
					           		repo.initialize(); 				    			
					           		org.openrdf.repository.RepositoryConnection con = repo.getConnection();	 				    			
					           		con.clear(new org.openrdf.model.impl.URIImpl(pid)); 							 							  				
			  				     } catch (Exception u) {}

			  				  }	
				  			  if ( ( model.contains("TEI") || model.contains("METS") ) && !user.getIIPSUser().isEmpty()) {
				  				  try {				  			  				  					  
				  					  Scp s = new Scp();
				  					  if (s.connect()) {
				  						  s.rm(pid);
				  						  s.disconnect();
				  					  }
				  				  } catch (Exception u) {u.printStackTrace();}
			  				  }						  			  				  			  
				    		  if (Repository.purgeObject(pid)) deleted++;
				    		  pd.worked(1);
				    		  try {Thread.sleep(5);} catch (Exception e) {}						    				    
				      		}
				    	  
				    	  msgFmt = new MessageFormat(res.getString("objdelsuc"));
			 				Object[] args0 = {new Integer(deleted).toString()};
					       	JOptionPane.showMessageDialog( null, msgFmt.format(args0), Common.WINDOW_HEADER, JOptionPane.INFORMATION_MESSAGE);
				   		}
				    } catch (Exception ex) {ex.printStackTrace();}					  
				}
			}.start();	 
	}

	public void handleExportButton(ActionEvent e) {
		new Thread() {
			public void run() {
			  try {
				  JTable loTable = (JTable) getGuiComposite().getWidget(jtData);
				  int[] selected = loTable.getSelectedRows();
				  ProgressDialog pd = new ProgressDialog(getCoreDialog(), Common.WINDOW_HEADER);
				  pd.displayPercentageInProgressBar = true;
				  pd.beginTask(res.getString("export"), selected.length, true);
				  int exported = 0;
				  MessageFormat msgFmt = new MessageFormat(res.getString("objex"));
	 			  Object[] args = {new Integer(selected.length).toString()};
				  
			      int liChoice = JOptionPane.showConfirmDialog(null, msgFmt.format(args) ,
			    		  res.getString("file.export"), JOptionPane.YES_NO_OPTION,
			    		  						JOptionPane.QUESTION_MESSAGE);
				 
			      if (liChoice == 0) {
					  CPropertyService props = (CPropertyService) CServiceProvider.getService(ServiceNames.PROPERTIES);
					  JFileChooser chooser = new JFileChooser(props.getProperty("user", "export.path"));
					  chooser.setDialogTitle(res.getString("chooseedir"));
					  chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

					  if (chooser.showDialog(getCoreDialog(), res.getString("choose")) != JFileChooser.APPROVE_OPTION) {
						  return;
					  }
					  props.setProperty("user", "export.path", chooser.getSelectedFile().getAbsolutePath());
					  props.saveProperties("user");
					  File fp = chooser.getSelectedFile();
	    	  
			    	  for (int i=0; i<selected.length; i++) {
			    		  if(pd.isCanceled()) {break;}		
			    		  String pid =(String)loTable.getValueAt(selected[i],0);	
			    		  try {
			    	          FileOutputStream fos = new FileOutputStream(fp.getAbsolutePath()+System.getProperty( "file.separator" )+pid.replace(":",".")+".xml" );
							  BufferedWriter out = new BufferedWriter(new OutputStreamWriter( fos, "UTF-8" ) );

							  out.write(Repository.get2ObjectXml(pid));
							  out.close();
			    			  
			    			  exported++;
			    		  } catch (Exception ex) {
					       	  JOptionPane.showMessageDialog( null,  ex.getLocalizedMessage(), Common.WINDOW_HEADER, JOptionPane.INFORMATION_MESSAGE);
			    		  }	  
			    		  pd.worked(1);
			    		  try {Thread.sleep(5);} catch (Exception eq) {			    			 
			    		  }						    				    
			      		}
			    	   msgFmt = new MessageFormat(res.getString("objexsuc"));
	 					Object[] args0 = {new Integer(exported).toString()}; 		    		
	 					JOptionPane.showMessageDialog( getCoreDialog(), msgFmt.format(args0), Common.WINDOW_HEADER, JOptionPane.INFORMATION_MESSAGE);
				       	
			   		}
			    } catch (Exception ex) {
			    	ex.printStackTrace();
			    }					  
			}
		}.start();	 
	}

	public void handleEditButton(ActionEvent e) {
		  try {
				
			  getCoreDialog().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));


			  JTable loTable = (JTable) getGuiComposite().getWidget(jtData);
			  int[] selected = loTable.getSelectedRows();
			  String pid =(String)loTable.getValueAt(selected[0],0);	
			  String label =(String)loTable.getValueAt(selected[0],1);	
			  String model =(String)loTable.getValueAt(selected[0],2);	
			  String owner =(String)loTable.getValueAt(selected[0],4);	
      	  
			  ObjectEditorDialog dlg = (ObjectEditorDialog) CServiceProvider.getService(DialogNames.OBJECTEDITOR_DIALOG);
			  dlg.set(pid, label, model, owner);
			  dlg.open();
			  
		    } catch (Exception ex) {}					  
			finally {
				getCoreDialog().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
	}
	
	public void handleMouseDoubleClick(MouseEvent e, int type) {

		try {

			CEventListener.setBlocked(true);
			// no events while handling

			if (type == MouseEvent.MOUSE_CLICKED) {
				if (e.getClickCount() >= 2) {
					handleEditButton(null);
				}
			}
		} catch (Exception ex) {
		} finally {
			CEventListener.setBlocked(false);
		}

	}	
	
	public void handleNewButton(ActionEvent e) {
		
		try {
			NewObjectDialog dlg;

			dlg = (NewObjectDialog) CServiceProvider.getService(DialogNames.NEWOBJECT_DIALOG);
			dlg.set(this,null,user.getUser());
			dlg.open();
		} catch (Exception ex) {
		}

	}
	
	public void handleReplaceButton(ActionEvent e) {
		
		try {
			ReplaceDialog dlg;

			dlg = (ReplaceDialog) CServiceProvider.getService(DialogNames.REPLACE_DIALOG);
			dlg.setParent(this);
			dlg.open();
		} catch (Exception ex) {
		}

	}			

	public void handleRefreshButton(ActionEvent e) {
		
		try {
			refresh();
		} catch (Exception ex) {
		}

	}			
	

	public void handleModelButton(ActionEvent e) {
		
		try {	
		  JTable loTable = (JTable) getGuiComposite().getWidget(jtData);
		  int[] selected = loTable.getSelectedRows();
		  String pid =(String)loTable.getValueAt(selected[0],0);	
		  Repository.addRelation("info:fedora/"+pid,"http://ecm.sourceforge.net/relations/0/2/#isTemplateFor","info:fedora/cm:DefaultContentModel");
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}			
	
	
	public void handleValidateButton(ActionEvent e) 
			throws Exception {
			new Thread() {
				public void run() {

					int fi = 0;

					try {
						JTable loTable = (JTable) getGuiComposite().getWidget(jtData);
						int[] selected = loTable.getSelectedRows();
						ProgressDialog progressDialog = new ProgressDialog(getCoreDialog(), Common.WINDOW_HEADER);
						progressDialog.displayPercentageInProgressBar = true;
						progressDialog.beginTask(res.getString("addcont"), selected.length, true);
						
						MessageFormat msgFmt = new MessageFormat(res.getString("objmod"));
	 					Object[] args = {new Integer(selected.length).toString()};
	 					
  				        int liChoice = JOptionPane.showConfirmDialog(null, msgFmt.format(args) ,
  				        		res.getString("geo"), JOptionPane.YES_NO_OPTION,
					    		  						JOptionPane.QUESTION_MESSAGE);

						if (liChoice == 0) {

							getCoreDialog().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

							progressDialog.worked(1);
							
							ContextRefresher cr = new ContextRefresher();
							
 				    	    for (int i=0; i<selected.length; i++) 
 				    	    {
					    		String pid =(String)loTable.getValueAt(selected[i],0);
					    		String title =(String)loTable.getValueAt(selected[i],1);
					    		String cm =(String)loTable.getValueAt(selected[i],2);
					    		
					    		try {
					    		
					    			if (cm.equals("cm:Context")) {
					    				if (cr.refreshKML(pid, title))
					    					fi++;
					    			}
					    		} catch (Exception r) {}

								if(progressDialog.isCanceled()) {
									// break;
								}				

								progressDialog.worked(1);
	 															
								try {
									Thread.sleep(50); 
								} catch (InterruptedException e) {
								}
									
 				    	    }
 				    	   msgFmt = new MessageFormat(res.getString("geosuc"));
 		 					Object[] args1 = {new Integer(fi).toString()}; 		    		
 		 					JOptionPane.showMessageDialog(  getCoreDialog(), msgFmt.format(args1), Common.WINDOW_HEADER, JOptionPane.INFORMATION_MESSAGE);
 							
						}
						
					} catch (Exception ex) {
						try {
						} catch (Exception ez) {}
					}
					finally {
						getCoreDialog().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					}
			
				}
			}.start();
			
		}
	
		
	
	public void handleSeekButton(ActionEvent e) {

		try {
			    seekString =  (String) ((JTextField) getGuiComposite().getWidget("jtfSeek")).getText().trim();
			    props.setProperty("user", "objects.seekterm", seekString);
				props.saveProperties("user");
			    refresh();		   
			   
		    } catch (Exception ex) {}					  
	}			
	
	public void handleMakeEnvironmentButton(ActionEvent e) {

		try {
			MakeEnvironmentDialog loDlg;


			loDlg = (MakeEnvironmentDialog) CServiceProvider.getService(DialogNames.MAKEENVIRONMENT_DIALOG);
			loDlg.open();
			
			if (loDlg.isOK()) {
				try {
						getCoreDialog().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					
						TemplateSubsystem temps = (TemplateSubsystem) CServiceProvider.getService(ServiceNames.TEMPLATESUBSYSTEM);
						temps.makeTemplate("cirilo:TEI", loDlg.getUser(), "$cirilo:TEI."+loDlg.getUser(), "No Title", "info:fedora/cm:TEI");
						temps.makeTemplate("cirilo:Context", loDlg.getUser(), "$cirilo:Context."+loDlg.getUser(), "No Title", "info:fedora/cm:Content");
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				finally {
					getCoreDialog().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				    MessageFormat msgFmt = new MessageFormat(res.getString("envok"));
				    Object[] args = {loDlg.getUser()};
					JOptionPane.showMessageDialog( getCoreDialog(), msgFmt.format(args) , Common.WINDOW_HEADER, JOptionPane.INFORMATION_MESSAGE );

				}

			}

		}
		catch (Exception ex) {
			CException.record(ex, this);
		}
	}

	
	public void handleAddModel() throws Exception  {

		new Thread() {
			public void run() {

				try {
						
						 String qu ="select distinct ?pid ?title ?model ?lastModifiedDate ?user "+
							       "where { ?object  <dc:title> ?title ."+
							                  " ?object  <dc:identifier> ?pid ."+
							                   "?object <fedora-view:lastModifiedDate> ?lastModifiedDate ."+
							                   "?object <info:fedora/fedora-system:def/model#ownerId> ?user ."+
							                   "optional {?object <info:fedora/fedora-system:def/model#hasModel> ?model .  filter (!regex(str(?model), '^info:fedora/fedora-system:') && regex(str(?model), '^null') && !regex(str(?pid), '^fedora-system:') ) } . " +
							         "} order by ?pid";

			 			 String[] columnNames = {res.getString("pid"),
				                   res.getString("title"),
				                   res.getString("contentmodel"),
				                   res.getString("lastupdate"),
				                   res.getString("owner") };
			 			 
				         DefaultSortTableModel dm= Repository.getObjects(qu,columnNames);

				         DOMBuilder db = new DOMBuilder();
						 Format	format = Format.getRawFormat();
						 format.setEncoding("UTF-8");
						 XMLOutputter 	outputter = new XMLOutputter(format);

				         int objects = 0;
				         
						ProgressDialog progressDialog = new ProgressDialog( getCoreDialog(), Common.WINDOW_HEADER);
						progressDialog.displayPercentageInProgressBar = true;
						progressDialog.millisToDecideToPopup = 1;
						progressDialog.millisToPopup = 1;
						progressDialog.beginTask(res.getString("addrel"),dm.getRowCount(), true);
											
						getCoreDialog().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		         
				         Element rdf = new Element("RDF",Common.xmlns_rdf);
				         Element description = new Element("Description",Common.xmlns_rdf);
				         Attribute about = new Attribute("about", "", Attribute.CDATA_TYPE, Common.xmlns_rdf);
				         Element hasmodel = new Element("hasModel", Common.xmlns_model);
				         Attribute resource = new Attribute("resource", "info:fedora/cm:DefaultContentModel", Attribute.CDATA_TYPE, Common.xmlns_rdf);
				         hasmodel.setAttribute(resource);
				         description.setAttribute(about);
				         description.addContent(hasmodel);
				         rdf.addContent(description);
				    	 
			       	     File tmp = File.createTempFile("temp", ".tmp");	    					
						 
 		       	         progressDialog.worked(1);
				        			       	     			       	     
				         for (int i=0; i<dm.getRowCount(); i++) {
				 				String pid = (String) dm.getValueAt(i,0);

				 				if(progressDialog.isCanceled()) {
									break;
								}
								
				       	       progressDialog.worked(1);
								
								try {
									Thread.sleep(50); 
								} catch (InterruptedException e) {
								}
								
				 				try {
				 					Document doc = db.build (Repository.getDatastream(pid, "RELS-EXT"));
				 				} catch (Exception ex) {
				 					about.setValue("info:fedora/"+pid);
							    	try {				    		
				    					byte[] data = (byte[]) new String (outputter.outputString(rdf)).getBytes("UTF-8");   					
				    					FileOutputStream fos = new FileOutputStream(tmp);
				    					fos.write(data);
				    					fos.flush();
				    					fos.close();	    					
							    		Repository.addDatastream(pid, "RELS-EXT", "", "X", "text/xml", tmp);
							    	} catch (Exception q ){
							    	}	
							    	objects++;	 					
				 				}
				 			}
						    tmp.delete();
						    
						    MessageFormat msgFmt = new MessageFormat(res.getString("relsuc"));
						    Object[] args = {new Integer(objects).toString() };
							JOptionPane.showMessageDialog( getCoreDialog(), msgFmt.format(args) , Common.WINDOW_HEADER, JOptionPane.INFORMATION_MESSAGE );
				         
				} catch (Exception ex) {
				}
				finally {
					getCoreDialog().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
			}
		}.start();
	}


	
	
	public void refresh() {
		try {			
			getCoreDialog().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));


			user = (User) CServiceProvider.getService(ServiceNames.CURRENT_USER);
			groups = (ArrayList) CServiceProvider.getService(ServiceNames.MEMBER_LIST);
            if (seekString.startsWith("add:RELS-EXT")) {
            	handleAddModel();
            } else {	             
            	 String seek = seekString.replace("*","");
 			     String qu ="select distinct ?pid ?title ?model ?lastModifiedDate ?user "+
        				       "where { ?object  <dc:title> ?title ."+
          				                  " ?object  <dc:identifier> ?pid ."+
          				                   "?object <fedora-view:lastModifiedDate> ?lastModifiedDate ."+
          				                   "?object <info:fedora/fedora-system:def/model#ownerId> ?user ."+
          				                   "optional {?object <info:fedora/fedora-system:def/model#hasModel> ?model } . " +
          				                 "filter (" +
          				                 (!groups.contains("administrator") ? " (?user = '"+user.getUser()+"' || ?user='public')  && "  : "") +	
          				                 "(!regex(str(?model), '^info:fedora/fedora-system:') )" +
        				                   (!seek.isEmpty() ?
        		 	          				       "&& ( regex(?title,'"+seek+"','i')  || regex(?pid,'"+seek+"','i')  || regex(?user,'"+seek+"','i') )" : "")+
        		 	          				       (!groups.contains("administrator") ?  "&&  (!regex(str(?pid), '^cirilo:') || ?user = '"+user.getUser()+"')  && !regex(str(?pid), '^ini:') && !regex(str(?pid), '^tcm:')"  : "")+
          				                 "  )"+
		                "} order by ?pid";
		

			
			JTable tb = (JTable) getGuiComposite().getWidget(jtData);
			int row = tb.getSelectedRow();
 	 	    String[] columnNames = {res.getString("pid"),
	                   res.getString("title"),
	                   res.getString("contentmodel"),
	                   res.getString("lastupdate"),
	                   res.getString("owner") };
			
			DefaultSortTableModel dm= Repository.getObjects(qu, columnNames);
			tb.setModel(dm);
			tb. setShowHorizontalLines(false);
			if (dm.getRowCount() > 0) {
				tb.setRowSelectionInterval(0,0);
			}
            }
		} catch (Exception e) {			
		}
		finally {
			getCoreDialog().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}
	
	
	public void handlerRemoved(CEventListener aoHandler) {
	}

	/**
	 *  Description of the Method
	 */
	protected void cleaningUp() {
	}

	public void show()
	 throws CShowFailedException {
		try {
			se = (Session) CServiceProvider.getService( ServiceNames.SESSIONCLASS );						
			org.emile.cirilo.dialog.CBoundSerializer.load(this.getCoreDialog(), se.getEditDialogProperties(),(JTable) getGuiComposite().getWidget(jtData));
			seekString = props.getProperty("user", "objects.seekterm");
			seekString = seekString == null ? "*" : seekString;
			moGA.setData("jtfSeek", seekString);
			refresh();

		} catch (Exception e){}
	}		
	/**
	 *  Description of the Method
	 *
	 * @exception  COpenFailedException  Description of the Exception
	 */
	protected void opened() throws COpenFailedException {

		try {
			 res =(ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);
   			 props = (CPropertyService) CServiceProvider.getService(ServiceNames.PROPERTIES);			
		
			 moGA = (CDefaultGuiAdapter)getGuiAdapter();						
			 CDialogTools.createButtonListener(this, "jbClose", "handleCancelButton");
			 CDialogTools.createButtonListener(this, "jbDel", "handleDelButton");
			 CDialogTools.createButtonListener(this, "jbExport", "handleExportButton");
			 CDialogTools.createButtonListener(this, "jbReplace", "handleReplaceButton");
			 CDialogTools.createButtonListener(this, "jbAddGeo", "handleValidateButton");
			 CDialogTools.createButtonListener(this, "jbRefresh", "handleRefreshButton");
			 CDialogTools.createButtonListener(this, "jbEdit", "handleEditButton");
			 CDialogTools.createButtonListener(this, "jbNew", "handleNewButton");
			 CDialogTools.createButtonListener(this, "jbSeek", "handleSeekButton");
			 
			 user = (User) CServiceProvider.getService(ServiceNames.CURRENT_USER);
			 groups = (ArrayList) CServiceProvider.getService(ServiceNames.MEMBER_LIST);
		            
 			 popup = new JPopupMenu();
		     JMenuItem mi;
			 mi = new JMenuItem(res.getString("refresh"));
		     mi.addActionListener(new ActionListener() {
		    	  public void actionPerformed(ActionEvent e) {
		    		  handleRefreshButton(null);
		    	  }
		      });	      
		     popup.add(mi);
		     popup.add(new JSeparator());
 			 mi = new JMenuItem(res.getString("edit"));
		     mi.addActionListener(new ActionListener() {
		    	  public void actionPerformed(ActionEvent e) {
		    		  handleEditButton(null);
		    	  }
		     });	      
		     
		     popup.add(mi);
			 mi = new JMenuItem(res.getString("delete"));
		     mi.addActionListener(new ActionListener() {
		    	  public void actionPerformed(ActionEvent e) {
		    		  handleDelButton(null);
		    	  }
		     });	      
		     popup.add(mi);
		     popup.add(new JSeparator());
			 mi = new JMenuItem(res.getString("new"));
		     mi.addActionListener(new ActionListener() {
		    	  public void actionPerformed(ActionEvent e) {
		    		  handleNewButton(null);
		    	  }
		      });	      
		     popup.add(mi);
		     popup.add(new JSeparator());
			 mi = new JMenuItem(res.getString("replace"));
		     mi.addActionListener(new ActionListener() {
		    	  public void actionPerformed(ActionEvent e) {
		    		  handleReplaceButton(null);
		    	  }
		      });	      
		     popup.add(mi);
			 mi = new JMenuItem(res.getString("export"));
		     mi.addActionListener(new ActionListener() {
		    	  public void actionPerformed(ActionEvent e) {
		    		  handleExportButton(null);
		    	  }
		      });	      
		     
		     popup.add(mi);
/*		     
		     popup.add(new JSeparator());
			 mi = new JMenuItem(res.getString("maketemplate"));
		     mi.addActionListener(new ActionListener() {
		    	  public void actionPerformed(ActionEvent e) {
		    		  handleModelButton(null);
		    	  }
		      });	      
		     popup.add(mi);
*/		     
/*		     
 			 mi = new JMenuItem(res.getString("makeenvironment"));
			    mi.addActionListener(new ActionListener() {
			    	  public void actionPerformed(ActionEvent e) {
			    		  handleMakeEnvironmentButton(null);
			    	  }
			      });	      
			    popup.add(mi);
*/
		    
		     PopupListener popupListener = new PopupListener();
 			 JTable tb = (JTable) getGuiComposite().getWidget(jtData);
	         new CMouseListener(tb, this, "handleMouseDoubleClick");
		     tb.addMouseListener(popupListener);
		     
			tb.addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
			         if (e.getKeyCode() == 82 &&  KeyEvent.getKeyModifiersText(e.getModifiers()).equals("Strg")) refresh();
			    }
			 });

			JTextField tf = (JTextField) getGuiComposite().getWidget("jtfSeek");
			tf.addKeyListener(
				new KeyAdapter() {
					public void keyPressed(KeyEvent ev) {
						if (ev.getKeyCode() == KeyEvent.VK_ENTER) {
							ev.consume();
							handleSeekButton(null);
						}
					}
				});

             
		} catch (Exception ex) {
			throw new COpenFailedException(ex);
		}
	}
	
	  
	  class PopupListener extends MouseAdapter {
		    public void mousePressed(MouseEvent e) {
		      showPopup(e);
		    }
		    public void mouseReleased(MouseEvent e) {
		      showPopup(e);
		    }
		    private void showPopup(MouseEvent e) {
		      if (e.isPopupTrigger()) {
		        popup.show(e.getComponent(), e.getX(), e.getY());
		      }
		    }
		  }

	  
	private JPopupMenu popup;	
	private CDefaultGuiAdapter moGA;
	private CPropertyService props;
	private User user;
	private ArrayList<String> groups;
	private Session se;
	private final String jtData ="jtData";
	private ResourceBundle res;
	private String seekString;

	
}

