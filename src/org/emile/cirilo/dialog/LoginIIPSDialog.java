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

import java.awt.event.*;
import javax.swing.*;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
public class LoginIIPSDialog extends CDialog {
    private static final Log LOG = LogFactory.getLog(LoginIIPSDialog.class);
    private boolean mode;
    
	/**
	 *  Constructor for the LoginDialog object
	 */
	public LoginIIPSDialog() { }

	public boolean isCanceled() {
		return bCancel;
	}

	//
	/**
	 *  Sets the dirty attribute of the LoginDialog object
	 *
	 * @param  ab_IsDirty  The new dirty value
	 */
	public void setDirty(boolean ab_IsDirty) {
		super.setDirty(ab_IsDirty);

		try {
			getGuiComposite().getWidget("jbLogin").setEnabled(ab_IsDirty);
		} catch (Exception ex) {
		}
	}


	/**
	 *  Description of the Method
	 *
	 * @param  e  Description of the Parameter
	 */
	public void handleCancelButton(ActionEvent e) {
		bCancel = true;
		close();
	}


	/**
	 *  Description of the Method
	 *
	 * @param  e  Description of the Parameter
	 */
	public void handleLoginButton(ActionEvent e) {
		handleLoginButton();
	}


	/**
	 *  Description of the Method
	 */
	public void handleLoginButton() {

		try {
			props = (CPropertyService) CServiceProvider.getService(ServiceNames.PROPERTIES);
			
            us.setIIPSUrl((String) moGA.getInput("jtfServer"));
            us.setIIPSUser((String) moGA.getInput("jtfUserID"));
            us.setIIPSPasswd((String) moGA.getInput("jpfPassword"));
			
			bCancel = false;
            close();
            
		} catch (Exception ex) {
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
			us = (User) CServiceProvider.getService(ServiceNames.CURRENT_USER);

			// map buttons
			CDialogTools.createButtonListener(this, "jbLogin", "handleLoginButton");
			CDialogTools.createButtonListener(this, "jbCancel", "handleCancelButton");
					
			setDirty(false);
			moGA.requestFocus("jtfUserID");
            
			moGA.setData("jtfServer", us.getIIPSUrl());
            
            if (us.getIIPSUser() != null) moGA.setData("jtfUserID", us.getIIPSUser());
			
			JPasswordField pf = (JPasswordField) getGuiComposite().getWidget("jpfPassword");
			pf.addKeyListener(
				new KeyAdapter() {
					public void keyPressed(KeyEvent ev) {
						if (ev.getKeyCode() == KeyEvent.VK_ENTER) {
							ev.consume();
							handleLoginButton();
						}
					}
				});

		} catch (Exception ex) {
			throw new COpenFailedException(ex);
		}
	}



	private IGuiAdapter moGA;
	private CPropertyService props;
	private ResourceBundle res;
	private boolean bCancel;
	private User us;
	private String user;
	private String passwd;
	private String server;
}

