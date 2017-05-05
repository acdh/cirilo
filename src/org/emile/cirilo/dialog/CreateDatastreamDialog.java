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

/*
 *  -----------------------------------------------------------------------------
 *
 *  Educational Community License (the "License"); you may not use this file
 *  except in compliance with the License. You may obtain a copy of the License
 *  at <a href="http://www.opensource.org/licenses/ecl1.txt">
 *  http://www.opensource.org/licenses/ecl1.txt.</a></p>
 *
 *  <p>Software distributed under the License is distributed on an "AS IS" basis,
 *  WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 *  the specific language governing rights and limitations under the License.</p>
 *
 *  <p>The entire file consists of original code.  Copyright &copy; 2005-2008 by
 *  Department of Information Processing in the Humanities, University of Graz.
 *  All rights reserved.</p>
 *
 *  -----------------------------------------------------------------------------
 */

import org.emile.cirilo.Common;
import org.emile.cirilo.ServiceNames;

import java.awt.event.*;

import javax.swing.*;

import org.apache.log4j.Logger;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import voodoosoft.jroots.core.CPropertyService;
import voodoosoft.jroots.core.CServiceProvider;
import voodoosoft.jroots.core.gui.CEventListener;
import voodoosoft.jroots.dialog.*;

/**
 *  Description of the Class
 *
 * @author     Johannes Stigler
 * @created    13. Februar 2005
 * @version    1.1
 */
public class CreateDatastreamDialog extends CDialog {   
   
    
	private static Logger log = Logger.getLogger(CreateDatastreamDialog.class);
	/**
	 *  Constructor for the LoginDialog object
	 */
	public CreateDatastreamDialog() { }


	public String getID() {
		return this.ID;
	}
	public String getLabel() {
		return this.Label;
	}
	public String getVersionable() {
		return this.Versionable;
	}
	public String getMimetype() {
		return this.Mimetype;
	}
	public String getGroup() {
		return this.Group;
	}
	public String getLocation() {
		return this.Location;
	}

	//
	/**
	 *  Sets the dirty attribute of the LoginDialog object
	 *
	 * @param  ab_IsDirty  The new dirty value
	 */
	public void setDirty(boolean ab_IsDirty) {
		super.setDirty(ab_IsDirty);

		try {
			getGuiComposite().getWidget("jbCreate").setEnabled(ab_IsDirty);
		} catch (Exception ex) {
		}
	}



	/**
	 *  Description of the Method
	 *
	 * @param  e  Description of the Parameter
	 */
	public void handleCancelButton(ActionEvent e) {
		this.ID ="";
		close();
	}


	/**
	 *  Description of the Method
	 *
	 * @param  e  Description of the Parameter
	 */
	public void handleCreateButton(ActionEvent e) {
		handleCreateButton();
	}


	/**
	 *  Description of the Method
	 */
	public void handleCreateButton() {

		try {		
			ID =  (String) moGA.getInput("jtfID");
			Label =  (String) moGA.getInput("jtfLabel");
			Location =  (String) moGA.getInput("jtfLocation");
			Versionable = (String) ((JComboBox) getGuiComposite().getWidget("jcbVersionable")).getSelectedItem();
			Mimetype = (String) ((JComboBox) getGuiComposite().getWidget("jcbMimetype")).getSelectedItem();
			Group = (String) ((JComboBox) getGuiComposite().getWidget("jcbGroup")).getSelectedItem();

			if (Common.SYSTEM_DATASTREAMS.contains("|"+ID+"|")) {
				MessageFormat msgFmt = new MessageFormat(res.getString("nonvalidid"));
 				Object[] args0 = {ID};
		       	JOptionPane.showMessageDialog( null, msgFmt.format(args0), Common.WINDOW_HEADER, JOptionPane.INFORMATION_MESSAGE);
			} else {			
			  close();
			}		
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


	/**
	 *  Description of the Method
	 *
	 * @exception  COpenFailedException  Description of the Exception
	 */
	protected void opened() throws COpenFailedException {

		try {
			
			props = (CPropertyService) CServiceProvider.getService(ServiceNames.PROPERTIES);

			res =(ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);
			moGA = (IGuiAdapter) getGuiAdapter();
			
			JComboBox versionable = (JComboBox) moGA.getWidget("jcbVersionable");
			versionable.setSelectedIndex(1);
			JComboBox mimetype = (JComboBox) moGA.getWidget("jcbMimetype");
			mimetype.setSelectedIndex(0);
			
            ID ="";

			// map buttons
			CDialogTools.createButtonListener(this, "jbCreate", "handleCreateButton");
			CDialogTools.createButtonListener(this, "jbCancel", "handleCancelButton");

			moGA.requestFocus("jtfID");


		} catch (Exception ex) {
			throw new COpenFailedException(ex);
		}
	}



	private CPropertyService props;
	private IGuiAdapter moGA;
	private ResourceBundle res;
	private String ID;
	private String Label;
	private String Versionable;
	private String Group;
	private String Location;
	private String Mimetype;
}

