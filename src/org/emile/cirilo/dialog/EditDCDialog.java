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


package org.emile.cirilo.dialog;

import voodoosoft.jroots.core.CServiceProvider;
import voodoosoft.jroots.core.CPropertyService;
import voodoosoft.jroots.core.gui.CEventListener;
import voodoosoft.jroots.core.gui.CItemListener;
import voodoosoft.jroots.dialog.*;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.emile.cirilo.Common;
import org.emile.cirilo.ServiceNames;
import org.emile.cirilo.ecm.templates.*;
import org.emile.cirilo.ecm.repository.*;
import org.emile.cirilo.business.*;
import org.emile.cirilo.utils.*;
import org.emile.cirilo.*;

import com.asprise.util.ui.progress.ProgressDialog;

import org.jdom.input.DOMBuilder;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;
import org.jdom.output.*;
import org.jdom.input.*;
import org.jdom.*;

import java.awt.Cursor;
import java.awt.event.*;
import javax.swing.*;

import java.util.*;
import java.io.*;
import java.awt.Color;



/**
 *  Description of the Class
 *
 * @author     Johannes Stigler
 * @created    10.3.2011
 */
public class EditDCDialog extends CDialog {
    private static final Log LOG = LogFactory.getLog(EditDCDialog.class);

	/**
	 *  Constructor for the LoginDialog object
	 */

	public EditDCDialog() {}

	
	/**
	 *  Description of the Method
	 *
	 * @param  e  Description of the Parameter
	 */
	public void handleCancelButton(ActionEvent e) {
		org.emile.cirilo.dialog.CBoundSerializer.save(this.getCoreDialog(), se.getEditDCDialogProperties(), (JTable) null);   
		close();
	}
		    
	public void set( String pid, String owner) {
		this.pid = pid;
		this.owner = owner;
	}
	
	/**
	 *  Description of the Method
	 *
	 * @param  e  Description of the Parameter
	 */
	public void handleEditButton(ActionEvent e) {
		try {
	        TextEditor dlg = (TextEditor) CServiceProvider.getService(DialogNames.TEXTEDITOR);
	        dlg.set(pid, "DC", "text/xml", "X", null);
	        dlg.open();
		} catch (Exception ex) {			
		}
			
	}

	 
		/**
	 *  Description of the Method
	 *
	 * @param  e  Description of the Parameter
	 */
	public void handleSaveButton(ActionEvent e) {
		
		try {
			new DCMI().write(pid, moGA,  ((JCheckBox) getGuiComposite().getWidget("jcbOAIProvider")).isSelected());
//			handleCancelButton(null);
		} catch (Exception ex) {
		}
 
	}
	
	
	/**
	 *  Description of the Method
	 *
	 * @param  aoHandler  Description of the Parameter
	 */
	public void handlerRemoved(CEventListener aoHandler) {
	}

	/**
	 *  Description of the Method
	 */
	protected void cleaningUp() {
	}

	public void show()
	 throws CShowFailedException {
		try {
			se = (Session) CServiceProvider.getService( ServiceNames.SESSIONCLASS );						
			org.emile.cirilo.dialog.CBoundSerializer.load(this.getCoreDialog(), se.getEditDCDialogProperties(), (JTable) null);
		} catch (Exception e) {		
		}
	}
	/**
	 *  Description of the Method
	 *
	 * @exception  COpenFailedException  Description of the Exception
	 */
	protected void opened() throws COpenFailedException {

		try {

			moGA = (CDefaultGuiAdapter)getGuiAdapter();		
			
			CDialogTools.createButtonListener(this, "jbClose", "handleCancelButton");
			CDialogTools.createButtonListener(this, "jbEdit", "handleEditButton");
			CDialogTools.createButtonListener(this, "jbSave", "handleSaveButton");			
			
			JComboBox jcbUser = ((JComboBox) getGuiComposite().getWidget("jcbUser"));
			
			groups = (ArrayList) CServiceProvider.getService(ServiceNames.MEMBER_LIST);
			user = (User) CServiceProvider.getService(ServiceNames.CURRENT_USER);
			
            
	        List<String> users = Repository.getUsers();
	        for (String s : users) {
	        	    if (!s.isEmpty()) jcbUser.addItem(s);
	         }
                               
			jcbUser.setEnabled(groups.contains("administrator"));
	
			
		    String[] PID = pid.split("[:]"); 
		    ((JTextField) getGuiComposite().getWidget("jtfPID")).setText(PID[1]);
		    ((JComboBox) getGuiComposite().getWidget("jcbNamespace")).setSelectedItem(PID[0]+":");
		    ((JTextField) getGuiComposite().getWidget("jtfTitle")).setText("");
	        DOMBuilder db = new DOMBuilder();
	        Document doc = db.build (Repository.getDatastream(pid, "DC"));
		    List children = doc.getRootElement().getChildren();
		    for ( Object o : children) {
				  Element e = (Element) o;
				  String name = "jtf"+e.getName().substring(0,1).toUpperCase() + e.getName().substring(1);
				  if (!name.equals("jtfIdentifier")) {
					  String field = ((JTextField) getGuiComposite().getWidget(name)).getText();
					  String dc = e.getText();
					  if (!field.isEmpty()) field += "~"+dc; else field = dc;				  
					  ((JTextField) getGuiComposite().getWidget(name)).setText(field);
				  }
			}
	    	doc = db.build (Repository.getDatastream(pid, "RELS-EXT"));
	        XPath xpath = XPath.newInstance( "//oai:itemID" );
		    xpath.addNamespace( Common.xmlns_oai  );
			Element oai = (Element) xpath.selectSingleNode( doc );
      		((JCheckBox) getGuiComposite().getWidget("jcbOAIProvider")).setSelected(oai != null);                             	

			jcbUser.setSelectedItem(user);
			jcbUser.setEnabled(false);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new COpenFailedException(ex);
		}
	}

	
	private CDefaultGuiAdapter moGA;
	private User user;
	private ArrayList<String> groups;
	private Session se;
    private String pid;	
    private String owner;	
}

