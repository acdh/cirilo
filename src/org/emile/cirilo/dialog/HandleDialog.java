package org.emile.cirilo.dialog;

/*
 *  -----------------------------------------------------------------------------
 *
 *  <p><b>License and Copyright: </b>The contents of this file are subject to the
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

import voodoosoft.jroots.core.CServiceProvider;
import voodoosoft.jroots.core.CPropertyService;
import voodoosoft.jroots.core.gui.CEventListener;
import voodoosoft.jroots.dialog.*;

import org.emile.cirilo.business.*;
import org.emile.cirilo.*;
import org.emile.cirilo.Common;

import java.awt.event.*;
import java.io.File;

import javax.swing.*;

import net.handle.hdllib.*;

import java.io.*;



/**
 *  Description of the Class
 *
 * @author     Johannes Stigler
 * @created    10.3.2011
 */
public class HandleDialog extends CDialog {
 
	/**
	 *  Constructor for the LoginDialog object
	 */

	public HandleDialog() {}

	public void setParent(EditObjectDialog dlg) {
		this.oParent = dlg;
	}

	
	
	/**
	 *  Description of the Method
	 *
	 * @param  e  Description of the Parameter
	 */
	public void handleCancelButton(ActionEvent e) {
		close();
	}

	public void handleShowButton(ActionEvent e) 
	throws Exception {
		TextEditor dlg = (TextEditor) CServiceProvider.getService(DialogNames.TEXTEDITOR);
		dlg.set(new File(System.getProperty("user.home")).getAbsolutePath()+System.getProperty( "file.separator" )+"handles.log",null, "text/log", "R", null, null);
		dlg.open();
	}	

	
	
	public void handleGetButton(ActionEvent e) {
		
		try {
			  CPropertyService props = (CPropertyService) CServiceProvider.getService(ServiceNames.PROPERTIES);			
			 
			  JFileChooser chooser = new JFileChooser(props.getProperty("user", "export.path"));
			  
			  chooser.setDialogTitle("Schlüsseldatei wählen");
			  if (chooser.showDialog(getCoreDialog(), "Auswählen") != JFileChooser.APPROVE_OPTION) {
				  return;
			  }
			  File fp = chooser.getSelectedFile();
			    
	          ByteArrayOutputStream stream = new ByteArrayOutputStream();
			  FileInputStream fin = new FileInputStream(fp);

			  byte buf[] = new byte[256];
			  int r = 0;
			  while((r=fin.read(buf))>=0) stream.write(buf, 0, r);
			  buf = stream.toByteArray();
			    
			  
 			  try {	
 				  byte passphrase[] = null;
 				  buf = Util.decrypt(buf, passphrase);
 	 			  Resolver resolver = new Resolver();
 	 			  AuthenticationInfo   auth = new PublicKeyAuthenticationInfo(Util.encodeString(Common.HANDLE_PREFIX), 300, Util.getPrivateKeyFromBytes(buf, 0));					
 				  if (resolver.checkAuthentication(auth)) {
 					  Handles hdl = (Handles) CServiceProvider.getService( ServiceNames.HANDLESCLASS );
 					  hdl.setHandleKey(buf);
 					  JOptionPane.showMessageDialog (getCoreDialog(),"Schüssel aus Datei <"+fp.getAbsolutePath().trim()+"> steht für Authentifizierung am Handle Server dauerhaft zur Verfügung");			  
 				  } 
 			  } catch (Exception q)	  {
 				  JOptionPane.showMessageDialog (getCoreDialog(),"Datei <"+fp.getAbsolutePath().trim()+"> enthält keinen gültigen Handle-Schlüssel.");			  
			  }
				  
			
		} catch (Exception ex) {
		}
	}
		

	public void handleDeleteButton(ActionEvent e) {
		
		try {
						 
			((JButton) getGuiComposite().getWidget("jbShow")).setEnabled(true);

			String handle = ((JTextField) getGuiComposite().getWidget("jtfHandlePrefix")).getText();
			String project = ((JTextField) getGuiComposite().getWidget("jtfProjectPrefix")).getText();
			String start = ((JTextField) getGuiComposite().getWidget("jtfBegin")).getText();
			boolean mode = ((JCheckBox) getGuiComposite().getWidget("jcbNumber")).isEnabled();
			 
			oParent.handleManageHandle(false, handle, project, start, mode);

		} catch (Exception ex) {
		}
	}

	
	public void handleGenerateButton(ActionEvent e) {
		
		try {
			((JButton) getGuiComposite().getWidget("jbShow")).setEnabled(true);

			String handle = ((JTextField) getGuiComposite().getWidget("jtfHandlePrefix")).getText();
			String project = ((JTextField) getGuiComposite().getWidget("jtfProjectPrefix")).getText();
			String start = ((JTextField) getGuiComposite().getWidget("jtfBegin")).getText();
			boolean mode = ((JCheckBox) getGuiComposite().getWidget("jcbNumber")).isSelected();
			 
			oParent.handleManageHandle(true, handle, project, start, mode);
			
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

			CDialogTools.createButtonListener(this, "jbGet", "handleGetButton");			
			CDialogTools.createButtonListener(this, "jbCancel", "handleCancelButton");			
			CDialogTools.createButtonListener(this, "jbDelete", "handleDeleteButton");			
			CDialogTools.createButtonListener(this, "jbShow", "handleShowButton");			
			CDialogTools.createButtonListener(this, "jbGenerate", "handleGenerateButton");			
 			((JButton) getGuiComposite().getWidget("jbShow")).setEnabled(false);
	
			
		} catch (Exception ex) {
			throw new COpenFailedException(ex);
		}
	}

	
	private CDefaultGuiAdapter moGA;
	private EditObjectDialog oParent;
	
}

