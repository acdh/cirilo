package org.emile.cirilo.gui;

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
public class GuiUpgradeDialog extends CGuiComposite {

	
	private static Logger log = Logger.getLogger(GuiUpgradeDialog.class);
	
	protected Container container;
		
	/**
	 *  Description of the Field
	 */
	protected JPanel radioPanel;
	/** 
	 *  Description of the Field
	 */
	protected JButton jbOK;
	/** 
	 *  Description of the Field
	 */
	protected JLabel jlText;
	/**
	 *  Description of the Field
	 */
	protected JButton jbCancel;
		
	/**
	 *Constructor for the GuiLoginDialog object
	 */
	public GuiUpgradeDialog() {
		super("GuiUpgradeDialog");

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
		jbOK.setDefaultCapable(true);
		jbCancel.setDefaultCapable(false);

		setWidgetName(jbOK, "jbOK");
		setWidgetName(jlText, "jlText");
		setWidgetName(jbCancel, "jbCancel");
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

				
		jbOK = new JButton("OK");
		jlText = new JLabel(" ");
		jbCancel = new JButton(res.getString("cancel"));
		        
		container.add(new JLabel("Upgrading the system environment ..."), "wrap 1");
		container.add(jlText, "wrap 12");
		Box c0  = Box.createHorizontalBox();
		c0.add( jbOK );
		c0.add( new JLabel( " "));
		c0.add( jbCancel );
		container.add( c0, "gapleft push, wrap 10" );
		
	}
}

