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
/**
 *  Description of the Class
 *
 * @author     yoda
 * @created    13. Februar 2005
 */

import java.awt.event.*;

import javax.swing.JLabel;
import javax.swing.JTable;

import org.emile.cirilo.ServiceNames;
import org.emile.cirilo.business.Session;
import org.emile.cirilo.ecm.repository.Repository;

import voodoosoft.jroots.core.CServiceProvider;
import voodoosoft.jroots.core.gui.CEventListener;
import voodoosoft.jroots.dialog.*;

import org.apache.log4j.Logger;


public class LocationDialog extends CDialog {

	private static Logger log = Logger.getLogger(LocationDialog.class);
	/**
	 *  Constructor for the LoginDialog object
	 */
	public LocationDialog() { }


	//
	/**
	 *  Sets the dirty attribute of the LoginDialog object
	 *
	 * @param  ab_IsDirty  The new dirty value
	 */
	public void setDirty(boolean ab_IsDirty) {
		super.setDirty(ab_IsDirty);

		try {
			getGuiComposite().getWidget("jbOK").setEnabled(ab_IsDirty);
		} catch (Exception ex) {
		}
	}


	/**
	 *  Description of the Method
	 *
	 * @param  e  Description of the Parameter
	 */
	public void handleCancelButton(ActionEvent e) {
		org.emile.cirilo.dialog.CBoundSerializer.save(this.getCoreDialog(), se.getLocationDialogProperties(), (JTable) null);   
		location = "";
		close();
	}


	/**
	 *  Description of the Method
	 *
	 * @param  e  Description of the Parameter
	 */
	public void handleOKButton(ActionEvent e) {
		try {
			org.emile.cirilo.dialog.CBoundSerializer.save(this.getCoreDialog(), se.getLocationDialogProperties(), (JTable) null);   
				location = (String) moGA.getInput("jtfLocation");
				if (!location.isEmpty()) {
					Repository.modifyDatastream (pid, dsid, null, "R", location);
				}

		} catch (Exception ex) {}
		finally {
			close();
		}
	}


	/**
	 *  Description of the Method
	 *
	 * @param  aoHandler  Description of the Parameter
	 */
	public void handlerRemoved(CEventListener aoHandler) {
	}

	public void show()
	 throws CShowFailedException {
		try {
			se = (Session) CServiceProvider.getService( ServiceNames.SESSIONCLASS );						
			org.emile.cirilo.dialog.CBoundSerializer.load(this.getCoreDialog(), se.getLocationDialogProperties(), (JTable) null);
		} catch (Exception e){}
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
			moGA = (IGuiAdapter) getGuiAdapter();
            
			// map buttons
			CDialogTools.createButtonListener(this, "jbOK", "handleOKButton");
			CDialogTools.createButtonListener(this, "jbCancel", "handleCancelButton");
			((JLabel) getGuiComposite().getWidget("jtlLocation")).setText(dsid+": ");
			moGA.setData("jtfLocation", location);
	        
			this.getCoreDialog().setSize(400, 100);
	           
			setDirty(false);

		} catch (Exception ex) {
			throw new COpenFailedException(ex);
		}
	}



	/**
	 *  Gets the name attribute of the handleAuth object
	 *
	 * @param  service  Description of the Parameter
	 * @return          The name value
	 */
	public void set(String pid, String dsid, String location) {
		this.pid = pid;
		this.dsid = dsid;
		this.location = location;
	}
  
	public String get() {
		return location;
	}


	private IGuiAdapter moGA;
	private Session se;
	private String pid;
	private String dsid;
	private String location;

}

