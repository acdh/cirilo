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
public class GuiLDAPLoginDialog extends CGuiComposite {

	
	private static Logger log = Logger.getLogger(GuiLDAPLoginDialog.class);

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
	protected JLabel pool;
	/**
	 *  Description of the Field
	 */
	protected JComboBox jcbRepository;
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
	public GuiLDAPLoginDialog() {
		super("GuiLDAPLoginDialog");

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
		setWidgetName(jtfUserID, "jtfUserID");
		setWidgetName(jpfPassword, "jpfPassword");
		setWidgetName(jcbRepository, "jcbRepository");
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

		
		pool = new JLabel(res.getString("pool")+": ");
		pool.setHorizontalTextPosition(SwingConstants.LEADING);

		jcbRepository = new JComboBox();

		user = new JLabel(res.getString("user")+": ");
		user.setHorizontalTextPosition(SwingConstants.LEADING);

		jtfUserID = new JTextField();
		jtfUserID.setPreferredSize(new Dimension(200, jtfUserID.getPreferredSize().height));

		passwd = new JLabel(res.getString("passwd")+": ");
		passwd.setHorizontalTextPosition(SwingConstants.LEADING);

		jpfPassword = new JPasswordField();
		jpfPassword.setPreferredSize(new Dimension(200, jpfPassword.getPreferredSize().height));

		jbLogin = new JButton(res.getString("login"));

		jbCancel = new JButton(res.getString("cancel"));

		container.add(user);
		container.add(jtfUserID, "wrap");								
		container.add(passwd);
		container.add(jpfPassword , "wrap");
		container.add(pool);
		container.add(jcbRepository, "wrap, grow");	
		container.add(jbLogin);	
		container.add(jbCancel);	
		
	}
}

