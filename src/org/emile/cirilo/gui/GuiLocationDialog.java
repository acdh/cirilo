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
public class GuiLocationDialog extends CGuiComposite {

	Container container;
	/**
	 *  Description of the Field
	 */
	protected JLabel jtlLocation;
	/**
	 *  Description of the Field
	 */
	protected JTextField jtfLocation;
	/**
	 *  Description of the Field
	 */
	protected JButton jbOK;
	/**
	 *  Description of the Field
	 */
	protected JButton jbCancel;


	/**
	 *Constructor for the GuiLoginDialog object
	 */
	public GuiLocationDialog() {
		super("GuiLocationDialog");

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

		setWidgetName(jbOK, "jbOK");
		setWidgetName(jbCancel, "jbCancel");
		setWidgetName(jtfLocation, "jtfLocation");
		setWidgetName(jtlLocation, "jtlLocation");
	}


	/**
	 *  Description of the Method
	 *
	 * @exception  Exception  Description of the Exception
	 */
	private void jbInit()
		throws Exception {
		ResourceBundle res=(ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);
		
		container = new Container();
		container.setLayout(new net.miginfocom.swing.MigLayout("insets 10","[grow]",""));

		jtfLocation = new JTextField();
		jtlLocation = new JLabel();

		jbOK = new JButton(res.getString("apply"));
		jbCancel = new JButton(res.getString("cancel"));
		
		container.add(jtlLocation);
		container.add( jtfLocation, "height 100:500:1000, wrap 10");
		Box c0  = Box.createHorizontalBox();
		c0.add( jbOK );
		c0.add( new JLabel (" "));
		c0.add( jbCancel );
		container.add( c0, "span 2, gapleft push, wrap 10" );
	}

}

