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

import org.emile.cirilo.ServiceNames;

import java.awt.Container;
import java.util.ResourceBundle;

import javax.swing.*;

/**
 *  Description of the Class
 *
 * @author     yoda
 * @created    07. September 2006
 */
public class GuiHarvesterDialog extends CGuiComposite {
	Container container;
	JTable jtRepositories;
	JButton jbStart;
	JButton jbClose;
	JButton jbShowLogfile;


	/**
	 *Constructor for the GuiSelectLayoutDialog object
	 */
	public GuiHarvesterDialog() {
		super("GuiHarvesterDialog");

		try {
			jbInit();
			setRootComponent(container);
			setup();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 *  Description of the Method
	 */
	protected void setup() {

		setWidgetName(jtRepositories, "jtRepositories");
		setWidgetName(jbStart, "jbStart");
		setWidgetName(jbClose, "jbClose");
		setWidgetName( jbShowLogfile, "jbShowLogfile" );

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
		container.setLayout(new net.miginfocom.swing.MigLayout("insets 10, fillx"));

		jtRepositories = new JTable();

		jbStart = new JButton(res.getString("harvest"));

		jbClose = new JButton(res.getString("close"));

		jbShowLogfile = new JButton( res.getString("showlog") );

		container.add( jtRepositories, "height 100:500:1500, growx, wrap 5" );
		Box c0  = Box.createHorizontalBox();
		c0.add( jbStart );
		c0.add( new JLabel (" "));
		c0.add( jbShowLogfile );
		c0.add( new JLabel (" "));
		c0.add( jbClose );
 		container.add( c0, "gapleft push, wrap 10" );
	}

}

