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
import org.emile.cirilo.business.TripleStoreFactory;
import org.emile.cirilo.ecm.repository.Repository;
import org.emile.cirilo.ecm.templates.TemplateSubsystem;






import org.xml.sax.InputSource;

import java.awt.Cursor;
import java.awt.event.*;
import java.io.File;
import java.io.FileOutputStream;

import javax.swing.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.asprise.util.ui.progress.ProgressDialog;

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
public class ReorganizeDialog extends CDialog {
    private static final Log LOG = LogFactory.getLog(ReorganizeDialog.class);
        
	/**
	 *  Constructor for the LoginDialog object
	 */
	public ReorganizeDialog() { }


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
			ArrayList groups = (ArrayList) CServiceProvider.getService(ServiceNames.MEMBER_LIST);

			res =(ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);

			CDialogTools.createButtonListener(this, "jbOK", "handleOKButton");
			CDialogTools.createButtonListener(this, "jbCancel", "handleCancelButton");
			
			try {
								
				if (!groups.contains("administrator")) {
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
					res = null;
					try {
						res =(ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);

						int liChoice = JOptionPane.showConfirmDialog(getCoreDialog(), "Start with the reorganization. Are you really sure? " ,
								Common.WINDOW_HEADER, JOptionPane.YES_NO_OPTION,
								JOptionPane.QUESTION_MESSAGE);

						TripleStoreFactory tf = new TripleStoreFactory();
						
						if (liChoice == 0 && tf.getStatus()) {

							    tf.removeAll();
								getCoreDialog().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	
								entries = Repository.getPidList("o:");
							
								ProgressDialog progressDialog = new ProgressDialog( getCoreDialog(), Common.WINDOW_HEADER);
								progressDialog.displayPercentageInProgressBar = true;
								progressDialog.millisToDecideToPopup = 1;
								progressDialog.millisToPopup = 1;

								progressDialog.beginTask("Reorganization ...", entries.size(), true);
							
							
								progressDialog.worked(1);
													
								File temp = File.createTempFile("tmp","xml");

								boolean ret = true;
								
								
								for (String s: entries) {

									if(progressDialog.isCanceled()) {
										break;
									}				

									progressDialog.worked(1);
									progressDialog.setSubTaskName(s);
										
									try {
										byte[] buf = null;
										if (Repository.exists(s, "RDF")) {
										  	buf = Repository.getDatastream(s, "RDF", "");											
										}
										if (Repository.exists(s, "ONTOLOGY")) {
										  	buf = Repository.getDatastream(s, "ONTOLOGY", "");
										}
										if (buf != null) {
											FileOutputStream fos = new FileOutputStream(temp);
											fos.write(buf);
											fos.close();
											if (tf.getStatus()) {
												if (!tf.update(temp, s)) {
													ret = false;
													break;
												}
											}
										}	


									} catch (Exception e) {
									}

									try {
										Thread.sleep(50); 
									} catch (InterruptedException e) {
									}
				
								}
								JOptionPane.showMessageDialog(null, ret ? "Reorganization of Cirilo's triplestore terminated normally." : "Couldn't connect to triplestore at "+tf.getInfo(), Common.WINDOW_HEADER, JOptionPane.INFORMATION_MESSAGE);
								tf.close();
							    close();
						} else {
							close();
						}
					    
						} catch (Exception ex) {
							ex.printStackTrace();
						}
						finally {
						}
			
					}
			}.start();
			
		}
	
		private ArrayList<String> entries;
		private CPropertyService props;
		private IGuiAdapter moGA;
		private ResourceBundle res;
		
}

