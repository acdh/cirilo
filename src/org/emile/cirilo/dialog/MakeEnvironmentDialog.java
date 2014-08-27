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
import org.emile.cirilo.business.*;
import org.emile.cirilo.ecm.repository.FedoraSoapImpl;
import org.emile.cirilo.ecm.repository.FedoraUserToken;
import org.emile.cirilo.ecm.repository.Repository;
import org.emile.cirilo.ecm.templates.TemplateSubsystem;
import org.emile.cirilo.ServiceNames;
import org.emile.cirilo.User;

import java.awt.event.*;
import javax.swing.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fedora.client.FedoraClient;

import java.util.Hashtable;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.naming.*;
import javax.naming.directory.*;

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
public class MakeEnvironmentDialog extends CDialog {
    private static final Log LOG = LogFactory.getLog(MakeEnvironmentDialog.class);
    private boolean mode;
	/**
	 *  Constructor for the LoginDialog object
	 */
	public MakeEnvironmentDialog() { }

	//
	/**
	 *  Sets the dirty attribute of the LoginDialog object
	 *
	 * @param  ab_IsDirty  The new dirty value
	 */
	public void setDirty(boolean ab_IsDirty) {
		super.setDirty(ab_IsDirty);

		try {
			getGuiComposite().getWidget("jbSubmit").setEnabled(ab_IsDirty);
		} catch (Exception ex) {
		}
	}


	public boolean isOK() { return submit;}
	
	public String getUser() { return user;}
	
	/**
	 *  Description of the Method
	 *
	 * @return    Description of the Return Value
	 */


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
	 */
	public void handleSubmitButton(ActionEvent e) {

		try {
			props = (CPropertyService) CServiceProvider.getService(ServiceNames.PROPERTIES);			
			user = (String) moGA.getInput("jtfUserID");
		
			close();
			submit = true;

		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, res.getString("invalauthent"), Common.WINDOW_HEADER, JOptionPane.WARNING_MESSAGE);
		}

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
			res =(ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);
			moGA = (IGuiAdapter) getGuiAdapter();

			// map buttons
			CDialogTools.createButtonListener(this, "jbSubmit", "handleSubmitButton");
			CDialogTools.createButtonListener(this, "jbCancel", "handleCancelButton");

			submit=false;
			
			setDirty(false);

			JTextField pf = (JTextField) getGuiComposite().getWidget("jtfUserID");
			pf.addKeyListener(
				new KeyAdapter() {
					public void keyPressed(KeyEvent ev) {
						if (ev.getKeyCode() == KeyEvent.VK_ENTER) {
							ev.consume();
							handleSubmitButton(null);
						}
					}
				});

		} catch (Exception ex) {
			throw new COpenFailedException(ex);
		}
	}

	private IGuiAdapter moGA;
	private CPropertyService props;
    private boolean submit;
	private ResourceBundle res;
	private String user;
	
}

