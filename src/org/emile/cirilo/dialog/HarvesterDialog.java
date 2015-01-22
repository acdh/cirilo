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

import org.apache.poi.util.SystemOutLogger;
import org.emile.cirilo.*;
import org.emile.cirilo.utils.*;
import org.emile.cirilo.business.Session;
import org.emile.cirilo.ecm.templates.*;
import org.emile.cirilo.ecm.repository.*;
import org.emile.cirilo.gui.jtable.DefaultSortTableModel;

import voodoosoft.jroots.application.*;
import voodoosoft.jroots.core.CServiceProvider;
import voodoosoft.jroots.core.gui.CEventListener;
import voodoosoft.jroots.core.gui.CMouseListener;
import voodoosoft.jroots.dialog.*;
import voodoosoft.jroots.exception.CException;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.FileWriter;
import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;
import java.net.URL;
import java.io.*;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import java.util.regex.*;

import javax.swing.*;

import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;
import org.jdom.Document;
import org.jdom.Element;

import com.asprise.util.ui.progress.ProgressDialog;

public class HarvesterDialog extends CDefaultDialog {
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
		org.emile.cirilo.dialog.CBoundSerializer.save(this.getCoreDialog(), se.getHarvesterDialogProperties(), (JTable) null);   
		close();
	}

	/**
	 *  Description of the Method
	 *
	 * @param  e              Description of the Parameter
	 * @exception  Exception  Description of the Exception
	 */
	public void handleStartButton(ActionEvent e)
			throws Exception {
		new Thread() {
			public void run() {

					try {

						JTable tb = (JTable) getGuiComposite().getWidget("jtRepositories");

						MessageFormat msgFmt = new MessageFormat(res.getString("askharv"));
						Object[] args = {new Integer(tb.getRowCount()).toString()};
						String time = new java.sql.Timestamp(System.currentTimeMillis()).toString();
			   		    logfile =saveto+System.getProperty( "file.separator" )+java.net.URLEncoder.encode("harvest-"+time,"US-ASCII")+".log"; 
						logger = new FileWriter( logfile );

						int liChoice = JOptionPane.showConfirmDialog(null, msgFmt.format(args) ,
								Common.WINDOW_HEADER, JOptionPane.YES_NO_OPTION,
								JOptionPane.QUESTION_MESSAGE);

						if (liChoice == 0) {

							logger.write( new java.util.Date()  +res.getString("start")+" harvest"+"\n----");	

	    			    	ProgressDialog progressDialog = new ProgressDialog( getCoreDialog(), Common.WINDOW_HEADER);
	    			    	progressDialog.displayPercentageInProgressBar = true;
	    			    	progressDialog.millisToDecideToPopup = 1;
	    			    	progressDialog.millisToPopup = 1;

				   		    getCoreDialog().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));						
	    			    	progressDialog.beginTask(res.getString("harvcont"),tb.getRowCount(), true);
	    			    	progressDialog.worked(1);
				   		    
				   		    for (int i=0; i<tb.getRowCount(); i++) {	 

	    			    		if(progressDialog.isCanceled()) {
	    			    			break;
	    			    		}				

	    			    		progressDialog.worked(1);
 															
	    			    		try {
	    			    			Thread.sleep(50); 
	    			    		} catch (InterruptedException e) {
	    			    		}
			   		    		 																		   		    	
				   		    	String baseURL =(String) tb.getValueAt(i,1);
				   		    	String metadataPrefix =(String) tb.getValueAt(i,2);
				   		    	try {
				   		    		String from = (String) tb.getValueAt(i,3);
				   		    		int j = from.indexOf(" ");
				   		    		if ( j > 0) {
				   		    			from = " -from "+from.substring(0,j+1);
				   		    		} else {
				   		    			from ="";
				   		    		}
				   		    //		String filename = saveto+System.getProperty( "file.separator" )+java.net.URLEncoder.encode(baseURL+time,"US-ASCII")+".xml ";
				   		    		
				   		    		String filename = saveto+System.getProperty( "file.separator" )+"test.xml";
				   		    		org.emile.cirilo.oai.RawWrite.harvest(logger, "-metadataPrefix " +metadataPrefix+from+" -out "+filename, baseURL);
				   		    		addItems(filename);
				   		    		
				   		    	} catch (Exception ex) {				   		    		
				   		    	}
				   		    	XPath xPath = XPath.newInstance( "/dataproviders/repository[@url='"+baseURL+"']" );
				   		    	Element curr = (Element) xPath.selectSingleNode( doc );
				   		    	if (curr != null) curr.setAttribute("updated", time);		     
				   		    	logger.write("\n----");
				   		    }
				   		    Format format = Format.getRawFormat();
				   		    format.setEncoding("UTF-8");
				   		    XMLOutputter outputter = new XMLOutputter(format);

//				   		    Repository.modifyDatastreamByValue("ini:Datastreams", "DATAPROVIDERS", "text/xml", outputter.outputString(doc));

				   		    msgFmt = new MessageFormat(res.getString("harvested"));
				   		    Object[] argu = {};

				   		    JOptionPane.showMessageDialog(  getCoreDialog(), msgFmt.format(argu) + res.getString("details")+logfile , Common.WINDOW_HEADER, JOptionPane.INFORMATION_MESSAGE);
				   		    
				   		    logger.write("\n"+ new java.util.Date()  +res.getString("end")+" harvest");									
							logger.close();

							getGuiComposite().getWidget("jbShowLogfile").setEnabled(true);

				   		    
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

	
	public void addItems(String filename)
			throws Exception {
					try {

							SAXBuilder builder = new SAXBuilder();
		    				Document metadata = builder.build(new File(filename) );		    				
		    				XPath xpath = XPath.newInstance("//oai:record ");
		    				xpath.addNamespace( Common.xmlns_oai  );

		    				List records = (List) xpath.selectNodes( metadata );

		    			    if (records.size() > 0 ) {
		    			    	int i = 0;
			    														   		    
		    			    	for (Iterator iter = records.iterator(); iter.hasNext();) {
		    			    		Element em = (Element) iter.next();
		    			    						   		    	
		    			    		try {
	    			    				String pid = "o:"+em.getChild("header", Common.xmlns_oai  ).getChild("identifier", Common.xmlns_oai  ).getText().replaceAll("o:","").replace(":","_");
		    			    	        System.out.println(pid);
	    			    				if (!Repository.exist(pid)) {
		    			    				pid = temps.cloneTemplate("info:fedora/cirilo:OAIItem", user.getUser(), "$"+pid, (String) null);
			    			    			logger.write("\n"+ new java.util.Date() + res.getString("creatingobject")+pid);		    			    				
		    			    			} else {
			    			    			logger.write("\n"+ new java.util.Date() + res.getString("updatingobject")+pid);		    			    				
		    			    			}		    	
		    			    			
		    			    			XPath qpath = XPath.newInstance("oai:metadata/europeana:record");
		    		    				qpath.addNamespace( Common.xmlns_dc  );
		    		    				qpath.addNamespace( Common.xmlns_oai  );
		    			    		    qpath.addNamespace(Common.xmlns_europeana);		    			    		    
		    		    				Element root  = (Element) qpath.selectSingleNode( em );
		    		    				
		    		    				if (root != null) {
		    		    					
		    		    					try {
		    		    						Element isShownAt = root.getChild("isShownAt", Common.xmlns_europeana);
		    		    						Repository.modifyDatastream (pid, "PID", null, "R", isShownAt.getText());
		    		    					} catch (Exception eq) {}

		    		    					try {
		    		    						Element object = root.getChild("object", Common.xmlns_europeana);
		    		    						File thumbnail = File.createTempFile( "temp", ".tmp" );		
		    		    						URL url = new URL(object.getText());

		    		    						InputStream is = url.openStream();
		    		    						OutputStream os = new FileOutputStream(thumbnail.getAbsoluteFile());

		    		    						byte[] b = new byte[2048];
		    		    						int length;
		    		    						while ((length = is.read(b)) != -1) {
		    		    							os.write(b, 0, length);
		    		    						}
		    		    						is.close();
		    		    						os.close();
		    		    					
		    		    						ImageTools.createThumbnail( thumbnail, thumbnail, 100, 80, Color.lightGray );
		    		    						Repository.modifyDatastream(pid, "THUMBNAIL", "image/jpeg", "M", thumbnail);
		    		    						thumbnail.delete();
		    		    					} catch (Exception eq) {}
		    		    				}

		    			    		} catch (Exception e) {
		    			    			e.printStackTrace();
		    			    		}
		    			    	}
				   		    
		    			    }
				   		    	         
					} catch (Exception ex) {
						ex.printStackTrace();
					}
 
	}

	
	public void handleShowLogfileButton(ActionEvent e) 
	throws Exception {
		TextEditor dlg = (TextEditor) CServiceProvider.getService(DialogNames.TEXTEDITOR);
		dlg.set(logfile, null, "text/log", "R", null);
		dlg.open();
	}	

	/**
	 *  Description of the Method
	 *
	 * @exception  CShowFailedException  Description of the Exception
	 */
	public void show()  throws CShowFailedException {
	  try {	
		 SAXBuilder parser = new SAXBuilder();
	
			se = (Session) CServiceProvider.getService( ServiceNames.SESSIONCLASS );						
			org.emile.cirilo.dialog.CBoundSerializer.load(this.getCoreDialog(), se.getHarvesterDialogProperties(), (JTable) null);
			

	     JTable tb = (JTable) getGuiComposite().getWidget("jtRepositories");
         tb.setShowHorizontalLines(false);
         Vector data = new Vector();

         List repositories = null;
	     XPath xPath;
         
	     try {

	    	 Vector names = new Vector();
			 names.addElement(res.getString("provider"));
			 names.addElement(res.getString("baseurl"));
			 names.addElement(res.getString("prefix"));
			 names.addElement(res.getString("updated"));

	    	 doc = parser.build(user.getUrl()+"/objects/cirilo%3ABackbone/datastreams/DATAPROVIDERS/content");
   	         saveto =doc.getRootElement().getAttributeValue("objectstore");
   	         
		     xPath = XPath.newInstance( "/dataproviders/repository[@state='active']" );
		     repositories = (List) xPath.selectNodes( doc );
	     
	    	 if (repositories != null) {
    		
	    		 for (Iterator iter = repositories.iterator(); iter.hasNext();) {
	    			 try {
	    				 Element e = (Element) iter.next();
	    				 Vector row = new Vector();
	    				 JCheckBox x = new JCheckBox();
	    				 row.addElement( e.getAttributeValue("name"));
	    				 row.addElement( e.getAttributeValue("url"));
	    				 row.addElement( e.getAttributeValue("metadataprefix"));
	    				 row.addElement( e.getAttributeValue("updated"));
	    				 data.addElement(row);
	    			 } catch (Exception ex) {}
	    		 }	  
	    	 }
	 	
	    	 DefaultSortTableModel dm = new DefaultSortTableModel(data, names);
	         tb.setModel(dm);
	         tb.setRowSelectionInterval(0,0);
	         
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
			 temps = (TemplateSubsystem) CServiceProvider.getService(ServiceNames.TEMPLATESUBSYSTEM);
			 res =(ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);
			 user = (User) CServiceProvider.getService(ServiceNames.CURRENT_USER);

			CDialogTools.createButtonListener(this, "jbClose", "handleCloseButton");
			CDialogTools.createButtonListener(this, "jbStart", "handleStartButton");
			CDialogTools.createButtonListener(this, "jbShowLogfile", "handleShowLogfileButton");			
  		    getGuiComposite().getWidget("jbShowLogfile").setEnabled(false);

		    JTable tb = (JTable) getGuiComposite().getWidget("jtRepositories");

		} catch (Exception ex) {
			throw new COpenFailedException(ex);
		} finally {
		}
	}


	private ResourceBundle res; 
	private TemplateSubsystem  temps;
	private Document doc;
	private Session se;
	private User user;
	private String logfile;
	private String saveto;
	private FileWriter logger;
}


