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

import voodoosoft.jroots.core.CPropertyService;
import voodoosoft.jroots.core.CServiceProvider;
import voodoosoft.jroots.core.gui.CEventListener;
import voodoosoft.jroots.dialog.*;
import voodoosoft.jroots.exception.CException;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;

import org.emile.cirilo.ServiceNames;
import org.emile.cirilo.User;
import org.emile.cirilo.business.Session;
import org.emile.cirilo.ecm.repository.Repository;



/**
 * Description of the Class
 *
 * @author yoda
 * @created 07. September 2006
 */
public class OptionsDialog extends CDialog {
	/**
	 *Constructor for the PropertyTableDialog object
	 */
	public OptionsDialog() {
	}


	/**
	 * Description of the Method
	 *
	 * @param e Description of the Parameter
	 */
	public void handleCancelButton(ActionEvent e) {
		org.emile.cirilo.dialog.CBoundSerializer.save(this.getCoreDialog(), se.getOptionsDialogProperties(), (JTable) null);   
		close();
	}

	public void handleSaveButton(ActionEvent e) {
		try {
			
			set("DCMapping","TEI");
			set("SEMExtraction","TEI");
			set("RemoveEmpties","TEI");
			set("CreateContexts","TEI");
			set("ResolveRegex","TEI");
			set("ResolveGeoIDs","TEI");
			set("ResolveSKOS","TEI");
			set("IngestImages","TEI");
			set("RefreshSource","TEI");
			set("OnlyGeonameID","TEI");

			set("DCMapping","LIDO");
			set("SEMExtraction","LIDO");
			set("CreateContexts","LIDO");
			set("ResolveGeoIDs","LIDO");
			set("IngestImages","LIDO");
			set("RefreshSource","LIDO");

			set("RefreshSource","METS");

		   	
			try {
	    		JCheckBox cb = (JCheckBox) getGuiComposite().getWidget("jcbTEICustomization");
	    		props.setProperty("user", "TEI.Customization", cb.isSelected() ? "1" : "0");
	    	} catch (Exception ex) {
	    	}
				   		
			try {
				JTextField tf = (JTextField) getGuiComposite().getWidget("jtfTEILoginName");
		        props.setProperty("user", "TEI.LoginName", tf.getText());
			} catch (Exception ex) {}

			try {
				JTextField tf = (JTextField) getGuiComposite().getWidget("jtfOAIPrefix");
		        props.setProperty("user", "OAI.Prefix", tf.getText());
			} catch (Exception ex) {}

			try {
				JComboBox jcbCM = ((JComboBox) getGuiComposite().getWidget("jcbGeneralDefaultCM"));
				props.setProperty("user", "General.DefaultContentModel", (String) jcbCM.getSelectedItem());
			} catch (Exception ex) {}
	        
	        props.saveProperties("user");
			
	        org.emile.cirilo.dialog.CBoundSerializer.save(this.getCoreDialog(), se.getOptionsDialogProperties(), (JTable) null);   
			close();
		} catch (Exception ex) {	
		}
	}

    private void set(String widget, String type) {
    	try {
    		JCheckBox cb = (JCheckBox) getGuiComposite().getWidget("jcb"+type+widget);
    		props.setProperty("user", type+"."+widget, cb.isSelected() ? "1" : "0");
    	} catch (Exception e) {
    	}
    }

    
    private void get(String widget, String type) {
    	try {
    		JCheckBox cb = (JCheckBox) getGuiComposite().getWidget("jcb"+type+widget);
    		cb.setSelected(props.getProperty("user", type+"."+widget).equals("1"));
    	} catch (Exception e) {
    	}
    }

	/**
	 * Description of the Method
	 *
	 * @param aoHandler Description of the Parameter
	 */
	public void handlerRemoved(CEventListener aoHandler) {
	}




	/**
	 * Description of the Method
	 *
	 * @exception CShowFailedException Description of the Exception
	 */
	public void show()
		throws CShowFailedException {

		try {
			se = (Session) CServiceProvider.getService( ServiceNames.SESSIONCLASS );						
			org.emile.cirilo.dialog.CBoundSerializer.load(this.getCoreDialog(), se.getOptionsDialogProperties(), (JTable) null);
			
			
			get("DCMapping","TEI");
			get("SEMExtraction","TEI");
			get("RemoveEmpties","TEI");
			get("CreateContexts","TEI");
			get("ResolveRegex","TEI");
			get("ResolveGeoIDs","TEI");
			get("ResolveSKOS","TEI");
			get("IngestImages","TEI");
			get("RefreshSource","TEI");
			get("OnlyGeonameID","TEI");

			get("DCMapping","LIDO");
			get("SEMExtraction","LIDO");
			get("CreateContexts","LIDO");
			get("ResolveGeoIDs","LIDO");
			get("IngestImages","LIDO");
			get("RefreshSource","LIDO");

			get("RefreshSource","METS");

			
			try {
	    		JCheckBox cb = (JCheckBox) getGuiComposite().getWidget("jcbTEICustomization");
	    		cb.setSelected(props.getProperty("user", "TEI.Customization").equals("1"));
	    	} catch (Exception e) {
	    	}
			
			
			try {
				JTextField tf = (JTextField) getGuiComposite().getWidget("jtfTEILoginName");
				tf.setText("");
				tf.setText(props.getProperty("user", "TEI.LoginName"));
			} catch (Exception e) {}

			try {
				JTextField tf = (JTextField) getGuiComposite().getWidget("jtfOAIPrefix");
				tf.setText("");
				tf.setText(props.getProperty("user", "OAI.Prefix"));
			} catch (Exception e) {}
			
			try {
				JComboBox jcbCM = ((JComboBox) getGuiComposite().getWidget("jcbGeneralDefaultCM"));

				List<String> ds = Repository.getTemplates(user.getUser(),groups.contains("administrator"));                
				for (String s: ds) {
	                if (!s.isEmpty()) jcbCM.addItem(s);            	
				}
	 
				String cm =  props.getProperty("user", "General.DefaultContentModel");
				if (cm != null) jcbCM.setSelectedItem(cm); 
			} catch (Exception e) {}
		}
		catch (Exception ex) {
			throw new CShowFailedException(ex);
		}
	}


	/**
	 * Description of the Method
	 */
	protected void cleaningUp() {
		moGA = null;
	}


	/**
	 * Description of the Method
	 *
	 * @exception COpenFailedException Description of the Exception
	 */
	protected void opened()
		throws COpenFailedException {
		try {
			res =(ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);

			moGA = (IGuiAdapter) getGuiAdapter();
			
			props = (CPropertyService) CServiceProvider.getService( ServiceNames.PROPERTIES );
			groups = (ArrayList) CServiceProvider.getService(ServiceNames.MEMBER_LIST);
			user = (User) CServiceProvider.getService(ServiceNames.CURRENT_USER);
						
			
			CDialogTools.createButtonListener(this, "jbClose", "handleCancelButton");
			CDialogTools.createButtonListener(this, "jbSave", "handleSaveButton");

			// focus comments
			moGA.requestFocus("jbClose");
		}
		catch (Exception ex) {
			throw new COpenFailedException(ex);
		}
		finally {
		}
	}


	/**
	 * Description of the Method
	 *
	 * @return Description of the Return Value
	 */
	protected boolean closing() {

		try {


		}
		catch (Exception e) {
			CException.record(e, this, false);
		}

		return true;
	}


	private IGuiAdapter moGA;
	private Session se;
	private CPropertyService props;
	private User user;
	private ArrayList<String> groups;
	private ResourceBundle res;


}

