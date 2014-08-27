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

import java.awt.Container;
import java.awt.Dimension;

import voodoosoft.jroots.core.CServiceProvider;
import voodoosoft.jroots.gui.CGuiComposite;
import javax.swing.*;

import org.emile.cirilo.ServiceNames;
import java.util.ResourceBundle;


/**
*  Description of the Class
*
* @author     yoda
* @created    07. September 2006
*/
public class GuiMakeEnvironmentDialog extends CGuiComposite {

	
	Container container;

	protected JLabel user;
	/**
	 *  Description of the Field
	 */
	protected JTextField jtfUserID;
	/**
	 *  Description of the Field
	 */
	protected JButton jbSubmit;
	/**
	 *  Description of the Field
	 */
	protected JButton jbCancel;


	/**
	 *Constructor for the GuiLoginDialog object
	 */
	public GuiMakeEnvironmentDialog() {
		super("GuiMakeEnvironmentDialog");

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
		jbSubmit.setDefaultCapable(true);
		jbCancel.setDefaultCapable(false);

		setWidgetName(jbSubmit, "jbSubmit");
		setWidgetName(jbCancel, "jbCancel");
		setWidgetName(jtfUserID, "jtfUserID");
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

		user = new JLabel(res.getString("user")+": ");
		user.setHorizontalTextPosition(SwingConstants.LEADING);

		jtfUserID = new JTextField();
		jtfUserID.setPreferredSize(new Dimension(150, jtfUserID.getPreferredSize().height));

		jbSubmit = new JButton(res.getString("submit"));
		jbCancel = new JButton(res.getString("cancel"));
		
		container.add(user);
		container.add(jtfUserID, "span, grow");	
		container.add(jbSubmit);	
		container.add(jbCancel);	
	}

}

