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

package org.emile.cirilo.gui;


import voodoosoft.jroots.core.CServiceProvider;
import voodoosoft.jroots.gui.CGuiComposite;
import javax.swing.*;


import org.emile.cirilo.ServiceNames;

import java.util.ResourceBundle;

import java.awt.*;
import java.awt.Dimension;

/**
 *  Description of the Class
 *
 * @author     yoda
 * @created    07. September 2006
 */
public class GuiLoginExistDialog extends CGuiComposite {

	
	Container container;
		
	/**
	 *  Description of the Field
	 */
	protected JLabel user;
	/**
	 *  Description of the Field
	 */
	protected JTextField jtfUserID;
	/**
	 *  Description of the Field
	 */
	protected JLabel passwd;
	/**
	 *  Description of the Field
	 */
	protected JPasswordField jpfPassword;
	/**
	 *  Description of the Field
	 */
	protected JLabel server;
	/**
	 *  Description of the Field
	 */
	protected JTextField jtfServer;
	/**
	 *  Description of the Field
	 */
	protected JLabel home;
	/**
	 *  Description of the Field
	 */
	protected JTextField jtfHome;
	/**
	 *  Description of the Field
	 */
	protected JButton jbLogin;
	/**
	 *  Description of the Field
	 */
	protected JButton jbCancel;


	/**
	 *Constructor for the GuiLoginDialog object
	 */
	public GuiLoginExistDialog() {
		super("GuiLoginExistDialog");

		try {
			
			jbInit();						
			setRootComponent(container);
			setup(); 
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 *  Description of the Method
	 */
	protected void setup() {
		jbLogin.setDefaultCapable(true);
		jbCancel.setDefaultCapable(false);

		setWidgetName(jbLogin, "jbLogin");
		setWidgetName(jbCancel, "jbCancel");
		setWidgetName(jtfUserID, "jtfUserID");
		setWidgetName(jpfPassword, "jpfPassword");
		setWidgetName(jtfServer, "jtfServer");
		setWidgetName(jtfHome, "jtfHome");
	}


	/**
	 *  Description of the Method
	 *
	 * @exception  Exception  Description of the Exception
	 */
	private void jbInit()
		throws Exception {
		
		
		container = new Container();
		container.setLayout(new net.miginfocom.swing.MigLayout());
		
		ResourceBundle res=(ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);

	
		server = new JLabel(res.getString("url")+": ");
		server.setHorizontalTextPosition(SwingConstants.LEADING);

		jtfServer= new JTextField();
		jtfServer.setPreferredSize(new Dimension(300, jtfServer.getPreferredSize().height));

		home = new JLabel(res.getString("home")+": ");
		home.setHorizontalTextPosition(SwingConstants.LEADING);

		jtfHome= new JTextField();
		jtfHome.setPreferredSize(new Dimension(300, jtfHome.getPreferredSize().height));

		user = new JLabel(res.getString("user")+": ");
		user.setHorizontalTextPosition(SwingConstants.LEADING);

		jtfUserID = new JTextField();
		jtfUserID.setPreferredSize(new Dimension(300, jtfUserID.getPreferredSize().height));

		passwd = new JLabel(res.getString("passwd")+": ");
		passwd.setHorizontalTextPosition(SwingConstants.LEADING);

		jpfPassword = new JPasswordField();
		jpfPassword.setPreferredSize(new Dimension(300, jpfPassword.getPreferredSize().height));



		jbLogin = new JButton(res.getString("login"));

		jbCancel = new JButton(res.getString("cancel"));

		container.add(server);
		container.add(jtfServer, "span, grow");	
		container.add(home);
		container.add(jtfHome, "span, grow");	
		container.add(user);
		container.add(jtfUserID, "wrap");								
		container.add(passwd);
		container.add(jpfPassword , "wrap, grow");
		container.add(jbLogin);	
		container.add(jbCancel);	
		
	}
}

