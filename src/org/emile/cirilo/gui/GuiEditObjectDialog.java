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

import org.emile.cirilo.ServiceNames;

import java.util.ResourceBundle;

import voodoosoft.jroots.core.CServiceProvider;
import voodoosoft.jroots.gui.CGuiComposite;

import java.awt.*;

import javax.swing.*;

import org.apache.log4j.Logger;

/**
 *  Description of the Class
 *
 * @author     yoda
 * @created    07. September 2006
 */
public class GuiEditObjectDialog extends CGuiComposite {

	private static Logger log = Logger.getLogger(GuiEditObjectDialog.class);

	protected Container container;
	protected JTable jtData;
	protected JButton jbEdit;
	protected JButton jbSeek;
	protected JButton jbNew;
	protected JButton jbDel;
	protected JButton jbReplace;
	protected JButton jbRefresh;
	protected JButton jbAddGeo;
	protected JButton jbExport;
	protected JButton jbManage;
	protected JButton jbClose;
	protected JTextField jtfSeek;

	/**
	 *Constructor for the GuiEditObject object
	 */
	public GuiEditObjectDialog() {
		super("GuiEditObjectDialog");

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
		setWidgetName(jtData, "jtData");
		setWidgetName(jbClose, "jbClose");
		setWidgetName(jbEdit, "jbEdit");
		setWidgetName(jbReplace, "jbReplace");
		setWidgetName(jbRefresh, "jbRefresh");
		setWidgetName(jbAddGeo, "jbAddGeo");
		setWidgetName(jbExport, "jbExport");
		setWidgetName(jbSeek, "jbSeek");
		setWidgetName(jbNew, "jbNew");
		setWidgetName(jbDel, "jbDel");
		setWidgetName(jtfSeek, "jtfSeek");
		setWidgetName(jbManage, "jbManage");
	}


	/**
	 *  Description of the Method
	 *
	 * @exception  Exception  Description of the Exception
	 */
	private void jbInit() throws Exception {
		ResourceBundle res=(ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);
		
		container = new Container(); 
		container.setLayout(new net.miginfocom.swing.MigLayout("","[grow,fill]","[grow,fill][]"));

		jtData = new JTable();
		jbClose = new JButton(res.getString("close"));
		jbEdit = new JButton(res.getString("edit")); 
		jbReplace = new JButton(res.getString("replace"));
		jbRefresh = new JButton(res.getString("refresh"));
		jbAddGeo = new JButton(res.getString("geo"));
		jbExport = new JButton(res.getString("export"));
		jbDel = new JButton(res.getString("delete"));
		jbNew = new JButton(res.getString("new"));
        jtfSeek = new JTextField();  
		jbSeek = new JButton( res.getString("search") );
		jbManage = new JButton( res.getString("hdlmanage")  );

        jtfSeek.setPreferredSize(new Dimension(100, jbSeek.getPreferredSize().height));
		
		container.add(new JScrollPane(jtData),"span 11, wrap 10");
 	    container.add(jtfSeek);
		container.add(jbSeek);
		container.add(jbEdit);
		container.add(jbNew);
		container.add(jbDel);
	    container.add(jbReplace);
		container.add(jbExport);
		container.add(jbAddGeo);
		container.add(jbRefresh);
		container.add(jbRefresh);
		container.add(jbManage);
		container.add(jbClose);

	}

} 

