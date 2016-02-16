package org.emile.cirilo.gui;


/*
 * -----------------------------------------------------------------------------
 *
 * <p><b>License and Copyright: </b>The contents of this file are subject to the
 * Educational Community License (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License
 * at <a href="http://www.opensource.org/licenses/ecl1.txt">
 * http://www.opensource.org/licenses/ecl1.txt.</a></p>
 *
 * <p>Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.</p>
 *
 * <p>The entire file consists of original code.  Copyright &copy; 2005-2008 by 
 * Department of Information Processing in the Humanities, University of Graz.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 */


import java.awt.Container;
import java.util.ResourceBundle;

import voodoosoft.jroots.core.CServiceProvider;
import voodoosoft.jroots.gui.CGuiComposite;

import javax.swing.*;

import org.emile.cirilo.ServiceNames;
import org.apache.log4j.Logger;


/**
 *  Description of the Class
 *
 * @author     yoda
 * @created    07. September 2006
 */
public class GuiHandleDialog extends CGuiComposite {

	private static Logger log = Logger.getLogger(GuiHandleDialog.class);

	protected Container container;
	
	protected JTextField jtfHandlePrefix;
	protected JTextField jtfProjectPrefix;
	protected JTextField jtfBegin;
	protected JCheckBox jcbNumber;
	protected JButton jbCancel; 
	protected JButton jbGet; 
	protected JButton jbShow; 
	protected JButton jbDelete; 
	protected JButton jbGenerate; 

	/**
	 *Constructor for the GuiLoginDialog object
	 */
	public GuiHandleDialog() {
		super("GuiHandleDialog");

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
		jbCancel.setDefaultCapable(false);

		setWidgetName(jtfHandlePrefix, "jtfHandlePrefix");
		setWidgetName(jtfProjectPrefix, "jtfProjectPrefix");
		setWidgetName(jtfBegin, "jtfBegin");
		setWidgetName(jcbNumber, "jcbNumber");
		setWidgetName(jbCancel, "jbCancel");
		setWidgetName(jbGet, "jbGet");
		setWidgetName(jbDelete, "jbDelete");
		setWidgetName(jbShow, "jbShow");
		setWidgetName(jbGenerate, "jbGenerate");
	}


	/**
	 *  Description of the Method
	 *
	 * @exception  Exception  Description of the Exception
	 */
	private void jbInit()
		throws Exception {
		
 		container = new Container();
		container.setLayout(new net.miginfocom.swing.MigLayout("","[][grow]",""));

		ResourceBundle res=(ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);



		jtfHandlePrefix = new JTextField();
		jtfHandlePrefix.setText("");
		jtfProjectPrefix = new JTextField();
		jtfProjectPrefix.setText("812.10");
		jcbNumber = new JCheckBox(); 
		jtfBegin = new JTextField();
		jtfBegin.setText("1");


		jbGet = new JButton( res.getString("getkey"));
		jbDelete = new JButton( res.getString("delete"));
		jbGenerate = new JButton( res.getString("create"));
		jbCancel = new JButton(res.getString("close"));
		jbShow = new JButton( res.getString("showlog"));
		
		
		container.add(new JLabel( res.getString("hdlprefix")));
		container.add(jtfHandlePrefix, "span, grow");	
		container.add(new JLabel( res.getString("proprefix")));
		container.add(jtfProjectPrefix, "span, grow");	
		container.add(new JLabel( res.getString("numcons")));
		container.add(jcbNumber);
		container.add(new JLabel( res.getString("startw")));
		container.add(jtfBegin, "span, grow");				
		
		container.add(jbGet);
		container.add(jbGenerate);
		container.add(jbDelete);
		container.add(jbShow);
		container.add(jbCancel);


		
	}

}

