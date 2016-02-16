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

import org.emile.cirilo.Common;
import org.emile.cirilo.ServiceNames;
import org.emile.cirilo.User;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Database;

import java.awt.event.*;

import javax.swing.*;

import org.apache.log4j.Logger;



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
public class LoginExistDialog extends CDialog {
    
	private static Logger log = Logger.getLogger(LoginExistDialog.class);
	/**
	 *  Constructor for the LoginDialog object
	 */
	public LoginExistDialog() { }

	public boolean isConnected() {
		return bAuthentication;
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
		final String driver = "org.exist.xmldb.DatabaseImpl";

		try {
					
			User us = (User) CServiceProvider.getService(ServiceNames.CURRENT_USER);
            us.setExistUrl((String) moGA.getInput("jtfServer"));
            us.setExistUser((String) moGA.getInput("jtfUserID"));
            us.setExistPasswd((String) moGA.getInput("jpfPassword"));
            us.setExistHome((String) moGA.getInput("jtfHome"));
            
		    props.setProperty("user", "exist.server", us.getExistUrl());
		    props.setProperty("user", "exist.home", us.getExistHome());
			props.saveProperties("user");

			Class cl = Class.forName( driver );
			Database database = (Database) cl.newInstance();
			DatabaseManager.registerDatabase( database );
			
			org.xmldb.api.base.Collection collection = DatabaseManager.getCollection( us.getExistUrl()+us.getExistHome() , us.getExistUser(), us.getExistPasswd());
			collection.getName();
            collection.close();
            
			bAuthentication = true;
			close();
			
		} catch (Exception ex) {
			log.error(ex.getLocalizedMessage(),ex);	
			
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
			
			props = (CPropertyService) CServiceProvider.getService(ServiceNames.PROPERTIES);

			res =(ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);
			moGA = (IGuiAdapter) getGuiAdapter();
			
			bAuthentication = false;

			// map buttons
			CDialogTools.createButtonListener(this, "jbLogin", "handleLoginButton");
			CDialogTools.createButtonListener(this, "jbCancel", "handleCancelButton");


			User us = (User) CServiceProvider.getService(ServiceNames.CURRENT_USER);

			server = props.getProperty("user", "exist.server");
			home = props.getProperty("user", "exist.home");

			
			moGA.setData("jtfUserID", us.getExistUser());
			moGA.setData("jtfServer", server != null ? server : us.getExistUrl());
			moGA.setData("jtfHome", home != null ? home : us.getExistHome());
			moGA.setData("jpfPassword", "");
			setDirty(false);

			moGA.requestFocus(user != null ?  "jpfPassword" : "jtfUserID");

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



	private CPropertyService props;
	private IGuiAdapter moGA;
	private ResourceBundle res;
	private boolean bAuthentication;
	private String user;
	private String passwd;
	private String home;
	private String server;
}

