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

import voodoosoft.jroots.core.CServiceProvider;
import voodoosoft.jroots.gui.CGuiComposite;

import javax.swing.*;

import jsyntaxpane.DefaultSyntaxKit;

import org.apache.log4j.Logger;

/**
 *  Description of the Class
 *
 * @author     yoda
 * @created    07. September 2006
 */
public class GuiTextEditor extends CGuiComposite {

	private static Logger log = Logger.getLogger(GuiTextEditor.class);
	
	protected Container container;	
	protected JEditorPane jebEditorPane;
	protected JButton jbClose;
	protected JMenuBar jmbMenu;
	protected JMenu jmFile;
	protected JMenuItem jmiSave;
	protected  JMenuItem jmiQuit;
	protected JScrollPane scrPane;
     
	/**
	 *Constructor for the GuiEditObject object
	 */
	public GuiTextEditor() {
		super("GuiTextEditor");

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
		setWidgetName(jebEditorPane, "jebEditorPane");
		setWidgetName(jmbMenu, "jmbMenu");
		setWidgetName(jmFile, "jmFile");
		setWidgetName(jbClose, "jbClose");
		setWidgetName(jmiSave, "jmiSave");
		setWidgetName(jmiQuit, "jmiQuit");
		setWidgetName(scrPane, "scrPane");
//		setWidgetName(jmiCut, "jmiCut");
//		setWidgetName(jmiPaste, "jmiPaste");
	}


	/**
	 *  Description of the Method
	 *
	 * @exception  Exception  Description of the Exception
	 */
	private void jbInit() throws Exception {
		ResourceBundle res=(ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);

		container = new Container();
		container.setLayout(new net.miginfocom.swing.MigLayout("fillx"));

        jmFile = new JMenu(res.getString("file"));
        jmFile.setMnemonic(java.awt.event.KeyEvent.VK_D);
        jmiSave = new JMenuItem(res.getString("save"));
        jmiSave.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.ActionEvent.CTRL_MASK));
        jmFile.add(jmiSave);
        jmFile.addSeparator();
        jmiQuit = new JMenuItem(res.getString("close"));
        jmiQuit.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.ActionEvent.CTRL_MASK));
        jmFile.add(jmiQuit);
        
        jmbMenu = new JMenuBar();	
		jmbMenu.add(jmFile);

		jbClose = new JButton(res.getString("close"));

		DefaultSyntaxKit.initKit();
		jebEditorPane = new JEditorPane();
        scrPane = new JScrollPane(jebEditorPane);
  
    	container.add( jmbMenu, "height 20:20:20, wrap 5");
    	container.add( scrPane, "height 100:1000:3000, growx, wrap 10");
		container.add( jbClose, "gapleft push, wrap" );
		
	}

}

