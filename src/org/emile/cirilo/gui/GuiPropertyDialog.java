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

import org.emile.cirilo.*;
import java.util.ResourceBundle;
import java.awt.Container;

import voodoosoft.jroots.core.CPropertyService;
import voodoosoft.jroots.core.CServiceProvider;
import voodoosoft.jroots.gui.CGuiComposite;

import javax.swing.*;

import org.apache.log4j.Logger;

/**
 *  Description of the Class
 *
 * @author     yoda
 * @created    07. September 2006
 */
public class GuiPropertyDialog extends CGuiComposite {

	private static Logger log = Logger.getLogger(GuiPropertyDialog.class);
	
	protected Container container;	
	JTable jtProperties;
	JButton jbCancel;
     
	/**
	 *Constructor for the GuiEditObject object
	 */
	public GuiPropertyDialog() {
		super("GuiPropertyDialog");

		try {
			jbInit();
			setRootComponent(container);
			setup();
		} catch (Exception e) {
			log.error(e.getLocalizedMessage(),e);	
		}
	}


	/**
	 *  Description of the Method
	 */
	protected void setup() {
		setWidgetName(jtProperties, "jtProperties");
		setWidgetName(jbCancel, "jbCancel");
	}


	/**
	 *  Description of the Method
	 *
	 * @exception  Exception  Description of the Exception
	 */
	private void jbInit() throws Exception {
		ResourceBundle res=(ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);

		container = new Container();
		container.setLayout(new net.miginfocom.swing.MigLayout("","[grow]","[grow]"));


		jbCancel = new JButton(res.getString("close"));
		jtProperties = new JTable();
  		
    	container.add( new JScrollPane(jtProperties), "grow, wrap 10");
		container.add( jbCancel, "gapleft push, wrap" );
		
	}

}

