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

import org.apache.log4j.Logger;

/**
 *  Description of the Class
 *
 * @author     yoda
 * @created    07. September 2006
 */
public class GuiLoginDialog extends CGuiComposite {

	
	private static Logger log = Logger.getLogger(GuiLoginDialog.class);
	
	protected Container container;
		
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
	protected JLabel protocol;
	/**
	 *  Description of the Field
	 */
	protected JComboBox jcbProtocol;
	/**
	 *  Description of the Field
	 */
	protected JLabel context;
	/**
	 *  Description of the Field
	 */
	protected JTextField jtfContext;
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
	public GuiLoginDialog() {
		super("GuiLoginDialog");

		try {
			
			jbInit();						
			setRootComponent(container);
			setup();
		}
		catch (Exception e) {
			log.error(e.getLocalizedMessage(),e);	
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
		setWidgetName(jcbProtocol, "jcbProtocol");
		setWidgetName(jtfUserID, "jtfUserID");
		setWidgetName(jpfPassword, "jpfPassword");
		setWidgetName(jtfServer, "jtfServer");
		setWidgetName(jtfContext, "jtfContext");
	}


	/**
	 *  Description of the Method
	 *
	 * @exception  Exception  Description of the Exception
	 */
	private void jbInit()
		throws Exception {
		
		
		Object[] protocols = { "http", "https"};

		container = new Container();
		container.setLayout(new net.miginfocom.swing.MigLayout("","[][grow]",""));
		
		ResourceBundle res=(ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);

		Object[] languages = { res.getString("english"), res.getString("german")};
		
		server = new JLabel(res.getString("server")+": ");
		server.setHorizontalTextPosition(SwingConstants.LEADING);

		jtfServer= new JTextField();
		jtfServer.setPreferredSize(new Dimension(200, jtfServer.getPreferredSize().height));

		context = new JLabel(res.getString("context")+": ");
		context.setHorizontalTextPosition(SwingConstants.LEADING);

		jtfContext= new JTextField();
		jtfContext.setPreferredSize(new Dimension(200, jtfContext.getPreferredSize().height));

		user = new JLabel(res.getString("user")+": ");
		user.setHorizontalTextPosition(SwingConstants.LEADING);

		jtfUserID = new JTextField();
		jtfUserID.setPreferredSize(new Dimension(200, jtfUserID.getPreferredSize().height));

		passwd = new JLabel(res.getString("passwd")+": ");
		passwd.setHorizontalTextPosition(SwingConstants.LEADING);

		jpfPassword = new JPasswordField();
		jpfPassword.setPreferredSize(new Dimension(200, jpfPassword.getPreferredSize().height));

		protocol = new JLabel(res.getString("protocol")+": ");
		protocol.setHorizontalTextPosition(SwingConstants.LEADING);

		jcbProtocol = new JComboBox(protocols);

		jbLogin = new JButton(res.getString("login"));

		jbCancel = new JButton(res.getString("cancel"));
		
		container.add(server);
		container.add(jtfServer, "span, grow");	
		container.add(protocol);
		container.add(jcbProtocol, "span, grow");	
		container.add(context);
		container.add(jtfContext, "span, grow");				
		container.add(user);
		container.add(jtfUserID, "wrap");								
		container.add(passwd);
		container.add(jpfPassword , "wrap, grow");
		container.add(jbLogin);	
		container.add(jbCancel);	

		
	}
}

