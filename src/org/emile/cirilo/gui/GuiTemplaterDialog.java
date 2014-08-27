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

import org.emile.cirilo.ServiceNames;

import java.util.ResourceBundle;

import javax.swing.*;

import jsyntaxpane.DefaultSyntaxKit;

/**
 *  Description of the Class
 *
 * @author     yoda
 * @created    07. September 2006
 */
public class GuiTemplaterDialog extends CGuiComposite {
	Container container;
    JTextField jtfPID;
    JEditorPane jtaTemplate;
    JEditorPane jtaLog;
    JEditorPane jtaResult;
	JButton jbStart;
	JButton jbClose;
	JButton jbShow;
    JScrollPane scrPaneTemplate;
    JScrollPane scrPaneResult;
    JScrollPane scrPaneLog;


	/**
	 *Constructor for the GuiSelectLayoutDialog object
	 */
	public GuiTemplaterDialog() {
		super("TemplaterDialog");

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

		setWidgetName(jtaTemplate, "jtaTemplate");
		setWidgetName(jtaResult, "jtaResult");
		setWidgetName(jtaLog, "jtaLog");
		setWidgetName(scrPaneTemplate, "scrPaneTemplate");
		setWidgetName(scrPaneResult, "scrPaneResult");
		setWidgetName(scrPaneLog, "scrPaneLog");
		setWidgetName(jbStart, "jbStart");
		setWidgetName(jbClose, "jbClose");
		setWidgetName(jbShow, "jbShow");
		setWidgetName(jtfPID, "jtfPID");

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
		container.setLayout(new net.miginfocom.swing.MigLayout("","[grow]",""));
		
		DefaultSyntaxKit.initKit();		
		
		jtaTemplate = new JEditorPane();
        scrPaneTemplate = new JScrollPane(jtaTemplate);      
		
        jtaResult = new JEditorPane();
        scrPaneResult = new JScrollPane(jtaResult);
		
        jtaLog = new JEditorPane();
        scrPaneLog = new JScrollPane(jtaLog);

		jbStart = new JButton(res.getString("evaluate"));

		jbClose = new JButton(res.getString("close"));

		jbShow = new JButton(res.getString("showtrip"));

		jtfPID = new JTextField();

		container.add( new JLabel("PID:"), "wrap 10");
		container.add( jtfPID, "grow, wrap 10");
		container.add( new JLabel("Template:"), "wrap 5");
		container.add( scrPaneTemplate, "height 100:500:1000, growx, wrap 10");
		container.add( new JLabel(res.getString("results")+ ":"), "wrap 5");
		container.add( scrPaneResult, "height 100:500:1000, growx, wrap 10");
		container.add( new JLabel("Log:"), "wrap 5");
		container.add( scrPaneLog, "height 100:500:1000, growx, wrap 10");
		Box c0  = Box.createHorizontalBox();
		c0.add( jbStart );
		c0.add( new JLabel (" "));
		c0.add( jbShow );
		c0.add( new JLabel (" "));
		c0.add( jbClose );
		container.add( c0, "gapleft push, wrap 10" );
	}
}

