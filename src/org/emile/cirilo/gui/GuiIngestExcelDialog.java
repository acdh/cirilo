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
import java.util.ResourceBundle;

import voodoosoft.jroots.core.CServiceProvider;
import voodoosoft.jroots.gui.CGuiComposite;

import javax.swing.*;

import org.emile.cirilo.ServiceNames;



/**
 *  Description of the Class
 *
 * @author     yoda
 * @created    07. September 2006
 */
public class GuiIngestExcelDialog extends CGuiComposite {

	Container container;
	/**
	 *  Description of the Field
	 */
	protected JTextField jtfTEITemplate;

	protected JButton jbTEITemplate;

	protected JTextField jtfEXCELTable;

	protected JButton jbEXCELTable;
		
	protected JButton jbSubmit;

	protected JButton jbCancel;


	/**
	 *Constructor for the GuiLoginDialog object
	 */
	public GuiIngestExcelDialog() {
		super("GuiIngestExcelDialog");

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
		setWidgetName(jtfTEITemplate, "jtfTEITemplate");
		setWidgetName(jbTEITemplate, "jbTEITemplate");
		setWidgetName(jtfEXCELTable, "jtfEXCELTable");
		setWidgetName(jbEXCELTable, "jbEXCELTable");
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
		container.setLayout(new net.miginfocom.swing.MigLayout("","[][grow][]",""));

	
		jtfTEITemplate = new JTextField();
		jtfTEITemplate.setPreferredSize(new Dimension(300, jtfTEITemplate.getPreferredSize().height));

		jtfEXCELTable = new JTextField();
		jtfEXCELTable.setPreferredSize(new Dimension(300, jtfEXCELTable.getPreferredSize().height));

		jbTEITemplate = new JButton("...");
		jbTEITemplate.setPreferredSize(new Dimension(88, jbTEITemplate.getPreferredSize().height));

		jbEXCELTable = new JButton("...");
		jbEXCELTable.setPreferredSize(new Dimension(88, jbEXCELTable.getPreferredSize().height));

		jbSubmit = new JButton(res.getString("ingest"));

		jbCancel = new JButton(res.getString("cancel"));
	    
		container.add( new JLabel(res.getString("teitemp")+ ":") );
		container.add( jtfTEITemplate );
		container.add( jbTEITemplate, "gapleft push, wrap 10" );
		container.add( new JLabel(res.getString("exceltable")+ ":") );
		container.add( jtfEXCELTable );
		container.add( jbEXCELTable, "gapleft push, wrap 10" );
		container.add( new JLabel(""));
		Box c0  = Box.createHorizontalBox();
		c0.add( jbSubmit );
		c0.add( new JLabel (" "));
		c0.add( jbCancel );
        container.add(c0);
		container.add( new JLabel(""), "gapleft push, wrap 10" );
	}	
}

