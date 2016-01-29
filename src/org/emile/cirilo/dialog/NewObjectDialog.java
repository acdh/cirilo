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

import org.emile.cirilo.Common;
import org.emile.cirilo.ServiceNames;
import org.emile.cirilo.ecm.templates.*;
import org.emile.cirilo.ecm.repository.*;
import org.emile.cirilo.business.*;
import org.emile.cirilo.utils.*;
import org.emile.cirilo.*;


import java.awt.Cursor;
import java.awt.event.*;

import javax.swing.*;

import java.text.MessageFormat;
import java.util.*;
import java.awt.Color;



/**
 *  Description of the Class
 *
 * @author     Johannes Stigler
 * @created    10.3.2011
 */
public class NewObjectDialog extends CDialog {

	/**
	 *  Constructor for the LoginDialog object
	 */

	public NewObjectDialog() {}

	
	/**
	 *  Description of the Method
	 *
	 * @param  e  Description of the Parameter
	 */
	public void handleCancelButton(ActionEvent e) {
		org.emile.cirilo.dialog.CBoundSerializer.save(this.getCoreDialog(), se.getNewDialogProperties(), (JTable) null);   
		close();
	}
		    
	public void set(EditObjectDialog dlg, String pid, String owner) {
		this.dlg = dlg;
		this.pid = pid;
		this.owner = owner;
	}
	
	/**
	 *  Description of the Method
	 *
	 * @param  e  Description of the Parameter
	 */
	public void handleResetButton(ActionEvent e) {
		try {
			JCheckBox jcbGenerated = ((JCheckBox) getGuiComposite().getWidget("jcbGenerated"));
			jcbGenerated.setSelected(false);
			JComboBox jcbContentModel = ((JComboBox) getGuiComposite().getWidget("jcbContentModel"));
			jcbContentModel.setSelectedIndex(0);
			reset();
			
			new DCMI().reset(moGA);
		} catch (Exception ex) {			
		}
			
	}

	/**
	 *  Description of the Method
	 *
	 * @param  e  Description of the Parameter
	 */
	 public void handleCBGenerated(ActionEvent e) {
		try {
			set();
		} catch (Exception ex) {			
		}
	}

	 
	 
	private void reset() {
		try {
			JTextField jtfPID = ((JTextField) getGuiComposite().getWidget("jtfPID"));
			JTextField jtfTitle = ((JTextField) getGuiComposite().getWidget("jtfTitle"));
			JComboBox jcbNamespace = ((JComboBox) getGuiComposite().getWidget("jcbNamespace"));
			JComboBox jcbUser = ((JComboBox) getGuiComposite().getWidget("jcbUser"));
			JComboBox jcbContentModel = ((JComboBox) getGuiComposite().getWidget("jcbContentModel"));			
			jcbNamespace.setEnabled(false);			
			
			jcbUser.setSelectedItem(user.getUser());
		
            String cm = jcbContentModel.getSelectedItem().toString().toLowerCase();
			if (cm.contains("context") || cm.contains("query")) {
				jcbNamespace.setSelectedIndex(cm.contains("context") ? 1 : 2);
				jcbNamespace.setEnabled(true);
				jtfPID.setBackground( Color.YELLOW );
				jtfPID.setEnabled(true);
				jtfPID.requestFocus();
			} else  {
				jcbNamespace.setSelectedIndex(0);
				if (!groups.contains("administrator")) {
					jtfPID.setBackground( new Color (238,238,238) );
					jtfPID.setText("");
					jcbNamespace.setEnabled(false);
					jtfPID.setEnabled(false);
					jtfTitle.requestFocus();
				}
			}
			
		} catch (Exception ex) {		
		}
		
	}

	private void set() {
		try {
			JCheckBox jcbGenerated = ((JCheckBox) getGuiComposite().getWidget("jcbGenerated"));
			JTextField jtfPID = ((JTextField) getGuiComposite().getWidget("jtfPID"));
			JTextField jtfTitle = ((JTextField) getGuiComposite().getWidget("jtfTitle"));
			JComboBox jcbNamespace = ((JComboBox) getGuiComposite().getWidget("jcbNamespace"));
			JComboBox jcbUser = ((JComboBox) getGuiComposite().getWidget("jcbUser"));
			JComboBox jcbContentModel = ((JComboBox) getGuiComposite().getWidget("jcbContentModel"));			
			
			jcbUser.setSelectedItem(user.getUser());
			jcbNamespace.setEnabled(false);			
		
            String cm = jcbContentModel.getSelectedItem().toString().toLowerCase();
			if (cm.contains("context")) {
				jcbNamespace.setSelectedIndex(1);
			} else {
				jcbNamespace.setSelectedIndex(0);				
			}
			
			if (!jcbGenerated.isSelected()) {
				jtfPID.setBackground( new Color (238,238,238) );
				jtfPID.setText("");
				jtfPID.setEnabled(false);
				jtfTitle.requestFocus();
			} else {
				jtfPID.setBackground( Color.YELLOW );
				jtfPID.setEnabled(true);
				jtfPID.requestFocus();
			}
		} catch (Exception ex) {		
		}
		
	}	
	
	
	/**
	 *  Description of the Method
	 *
	 * @param  e  Description of the Parameter
	 */
	public void handleCreateButton(ActionEvent e) {
		
		String model = "";
		String x = "";
		try {
			TemplateSubsystem temps = (TemplateSubsystem) CServiceProvider.getService(ServiceNames.TEMPLATESUBSYSTEM);
			JComboBox jcbContentModel = ((JComboBox) getGuiComposite().getWidget("jcbContentModel"));            
			JTextField jtfPID = ((JTextField) getGuiComposite().getWidget("jtfPID"));
			JCheckBox jcbGenerated = ((JCheckBox) getGuiComposite().getWidget("jcbGenerated"));
			JComboBox jcbNamespace = ((JComboBox) getGuiComposite().getWidget("jcbNamespace"));
			JComboBox jcbUser = ((JComboBox) getGuiComposite().getWidget("jcbUser"));
			model = (String)jcbContentModel.getSelectedItem();

			
			String pid = ""; 
					
			String tid = (String)jtfPID.getText().trim();
			
			 if (!tid.isEmpty() && (((String)jcbNamespace.getSelectedItem()).contains("context")||((String)jcbNamespace.getSelectedItem()).contains("container")||((String)jcbNamespace.getSelectedItem()).contains("query"))) {
					pid= (String)jcbNamespace.getSelectedItem()+tid; 					
			} else {			
				if (jcbGenerated.isSelected() && !jtfPID.getText().isEmpty()) {
					   pid =(String)jcbNamespace.getSelectedItem()+jtfPID.getText();
					   if (Repository.exist(pid)) {
							MessageFormat msgFmt = new MessageFormat(res.getString("double"));
							Object[] args = {pid};
							JOptionPane.showMessageDialog(  getCoreDialog(), msgFmt.format(args) , Common.WINDOW_HEADER, JOptionPane.INFORMATION_MESSAGE);
							return;
					   }
					   pid = "$"+pid;
					} else {
						pid = ((String)jcbNamespace.getSelectedItem()+(String)jcbUser.getSelectedItem()).trim();
					}	
			}
			 
			if (Repository.exist(pid.substring(1))) {
				
				MessageFormat msgFmt = new MessageFormat(res.getString("double"));
				Object[] args = {pid.substring(1)}; 
				JOptionPane.showMessageDialog (getCoreDialog(), msgFmt.format(args), Common.WINDOW_HEADER, JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			getCoreDialog().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			
			Split pcm = new Split(model);
			pid = temps.cloneTemplate("info:fedora/"+pcm.get(),(String)jcbUser.getSelectedItem(), pid, (String) null);
			
			DCMI dc = new DCMI();
			dc.save(moGA);
			while (!Repository.exist(pid));
			
			Common.genQR(user, pid);
			dc.write(pid, moGA, ((JCheckBox) getGuiComposite().getWidget("jcbOAIProvider")).isSelected());

			if (dlg != null) dlg.refresh();
			
			MessageFormat msgFmt = new MessageFormat(res.getString("objowner"));
			Object[] args = {model, (String)jcbUser.getSelectedItem(), pid}; 
					  			
			JOptionPane.showMessageDialog (getCoreDialog(), msgFmt.format(args), Common.WINDOW_HEADER, JOptionPane.INFORMATION_MESSAGE);

			
		} catch (Exception ex) {
			if (model.contains("Context") || model.contains("Query")) {
				
				MessageFormat msgFmt = new MessageFormat(res.getString("errcrea"));
			    Object[] args = {model};
				
				
				JOptionPane.showMessageDialog (getCoreDialog(), msgFmt.format(args)+res.getString("nopid"), Common.WINDOW_HEADER, JOptionPane.INFORMATION_MESSAGE); }
			
				else {
					MessageFormat msgFmt = new MessageFormat(res.getString("errcrea"));
				    Object[] args = {model};
					
					JOptionPane.showMessageDialog (getCoreDialog(),msgFmt.format(args), Common.WINDOW_HEADER, JOptionPane.INFORMATION_MESSAGE); /*   !"+x+pid+ex.getMessage());	*/		
		}}
		finally {
			getCoreDialog().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}

	public void handleCMComboBox(ItemEvent e)
	throws Exception {

		if (e.getStateChange() == 1) {
			reset(); 
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
			org.emile.cirilo.dialog.CBoundSerializer.load(this.getCoreDialog(), se.getNewDialogProperties(), (JTable) null);
//          DCMI x = new DCMI("/Users/yoda/xo/quetzal/src/org/emile/quetzal/ruleset.xml");
//			SAXBuilder builder = new SAXBuilder();
//			x.map(builder.build("/Users/yoda/xo/quetzal/src/org/emile/quetzal/tei.xml"));
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
			res =(ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);
			moGA = (CDefaultGuiAdapter)getGuiAdapter();		
			props = (CPropertyService) CServiceProvider.getService( ServiceNames.PROPERTIES );
	
			if (pid==null) new DCMI().preallocate(moGA);
			
			CDialogTools.createButtonListener(this, "jbClose", "handleCancelButton");
			CDialogTools.createButtonListener(this, "jbReset", "handleResetButton");
			CDialogTools.createButtonListener(this, "jbSave", "handleCreateButton");			
			CDialogTools.createButtonListener(this, "jcbGenerated", "handleCBGenerated");
			
			JComboBox jcbContentModel = ((JComboBox) getGuiComposite().getWidget("jcbContentModel"));			
			JComboBox jcbUser = ((JComboBox) getGuiComposite().getWidget("jcbUser"));
			JTextField jtfPID = ((JTextField) getGuiComposite().getWidget("jtfPID"));
			JCheckBox jcbGenerated = ((JCheckBox) getGuiComposite().getWidget("jcbGenerated"));
			
			groups = (ArrayList) CServiceProvider.getService(ServiceNames.MEMBER_LIST);
			user = (User) CServiceProvider.getService(ServiceNames.CURRENT_USER);
			
            List<String> ds = Repository.getTemplates(user.getUser(),groups.contains("administrator"));                
            for (String s: ds) {
                if (!s.isEmpty()) jcbContentModel.addItem(s);            	
            }
            boolean contains = false;
	        List<String> users = Repository.getUsers();
	        for (String s : users) {
	        	    if (!s.isEmpty()) {
	        	    	jcbUser.addItem(s);
		        	    if (!contains) if (s.equals(user.getUser())) contains = true;
	        	    }
	         }
                   
	        if (!contains) jcbUser.addItem(user.getUser());
	        jcbUser.setSelectedItem(user.getUser());
	        
	        
            String cm =  props.getProperty("user", "General.DefaultContentModel");
            jcbContentModel.setSelectedItem(cm); 
            if (jcbContentModel.getSelectedIndex() == -1) jcbContentModel.setSelectedIndex(0); 
            
			jcbUser.setEnabled(groups.contains("administrator"));
			jtfPID.setEnabled(groups.contains("administrator"));
			jcbGenerated.setEnabled(groups.contains("administrator"));

			new CItemListener((JComboBox) getGuiComposite().getWidget("jcbContentModel"), this, "handleCMComboBox");
			reset();
			
			jcbUser.setSelectedItem(user);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new COpenFailedException(ex);
		}
	}

	
	private CDefaultGuiAdapter moGA;
	private User user;
	private EditObjectDialog dlg;
	private ArrayList<String> groups;
	private Session se;
    private String pid;	
    private String owner;	
    private ResourceBundle res;
    private CPropertyService props;
	
}

