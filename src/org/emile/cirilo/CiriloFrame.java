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

package org.emile.cirilo;

import org.emile.cirilo.dialog.*;
import org.emile.cirilo.ecm.templates.TemplateSubsystem;
import org.emile.cirilo.gui.*;
import org.emile.cirilo.business.*;
import org.emile.cirilo.ServiceNames;
import org.emile.cirilo.dialog.DialogNames;
import org.emile.cirilo.dialog.LDAPLoginDialog;
import org.emile.cirilo.dialog.LoginDialog;
import org.emile.cirilo.dialog.MakeEnvironmentDialog;
import org.emile.cirilo.dialog.OptionsDialog;
import org.emile.cirilo.business.IIIFFactory;
import org.emile.cirilo.User;
import org.emile.cirilo.business.Handles;
import org.emile.cirilo.business.Session;

import voodoosoft.jroots.application.*;
import voodoosoft.jroots.core.CServiceProvider;
import voodoosoft.jroots.gui.*;
import voodoosoft.jroots.core.gui.*;
import voodoosoft.jroots.dialog.*;
import voodoosoft.jroots.exception.CException;
import voodoosoft.jroots.core.CPropertyService;

import org.apache.log4j.Logger;

import java.util.Hashtable;
import java.awt.*;
import java.awt.event.*;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.swing.*;
import javax.naming.*;
import javax.naming.directory.*;


/**
 * Description of the Class
 *
 * @author yoda
 * @created 15.März 2011
 */
public class CiriloFrame extends JFrame implements IEventHandler {

	private static Logger log = Logger.getLogger(CiriloFrame.class);

    
	/**
	 *Constructor for the CiriloFrame object
	 *
	 * @param asTitle Description of the Parameter
	 * @param aoGuiManager Description of the Parameter
	 * @exception Exception Description of the Exception
	 */
	public CiriloFrame(String asTitle, CGuiManager aoGuiManager)
		throws Exception {

		super(asTitle);

		JMenuItem loItem;
		IGuiComposite loMenu;

		try {
			ResourceBundle res =(ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);

			// the garbage timer will ask the business factory every 10 minutes
			// to free unused objects
			moGarbageTimer = new Timer(600000, null);
			moGarbageTimer.setRepeats(true);
			new CActionListener(moGarbageTimer, this, "handleGarbageTimer");
			moGarbageTimer.start();

			loMenu = aoGuiManager.getGuiComposite("FrameMenu");
			
			
			
			moAccMan = (CDefaultAccessManager) CServiceProvider.getService(ServiceNames.ACCESS_MANAGER);
			moAccMan.setGuiAdapter(aoGuiManager.getAdapter(loMenu));
			CDefaultGuiAdapter adapter = (CDefaultGuiAdapter) aoGuiManager.getAdapter(loMenu);
			adapter.setAccessManager(moAccMan);

			loItem = (JMenuItem) loMenu.getWidget("File");
			loItem.setText(res.getString("File"));
			
			loItem = (JMenuItem) loMenu.getWidget("Extras");
			loItem.setText(res.getString("Extras"));

			loItem = (JMenuItem) loMenu.getWidget("Infos");
			loItem.setText(res.getString("Infos"));
			
			loItem = (JMenuItem) loMenu.getWidget("File.Exit");			
			loItem.setText(res.getString(loItem.getText()));
			loItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_MASK));
			new CActionListener(loItem, this, "handleExit");

			loItem = (JMenuItem) loMenu.getWidget("File.Edit");
			loItem.setText(res.getString(loItem.getText()));
			new CActionListener(loItem, this, "handleEdit");

			loItem = (JMenuItem) loMenu.getWidget("File.Ingest");
			loItem.setText(res.getString(loItem.getText()));
			new CActionListener(loItem, this, "handleIngest");

			loItem = (JMenuItem) loMenu.getWidget("File.Import");
			loItem.setText(res.getString(loItem.getText()));
			new CActionListener(loItem, this, "handleImport");

			loItem = (JMenuItem) loMenu.getWidget("File.Login");
			loItem.setText(res.getString(loItem.getText()));
			new CActionListener(loItem, this, "handleLogin");

			loItem = (JMenuItem) loMenu.getWidget("Extras.Reset");
			loItem.setText(res.getString(loItem.getText()));
			new CActionListener(loItem, this, "handleReset");
			
			loItem = (JMenuItem) loMenu.getWidget("Infos.About");
			loItem.setText(res.getString(loItem.getText()));
			new CActionListener(loItem, this, "handleAbout");

			loItem = (JMenuItem) loMenu.getWidget("Extras.Harvest");
			loItem.setText(res.getString(loItem.getText()));
			new CActionListener(loItem, this, "handleHarvest");

			loItem = (JMenuItem) loMenu.getWidget("Extras.Templater");
			loItem.setText(res.getString(loItem.getText()));
			new CActionListener(loItem, this, "handleTemplater");

			loItem = (JMenuItem) loMenu.getWidget("Extras.Create");
			loItem.setText(res.getString(loItem.getText()));
			new CActionListener(loItem, this, "handleCreate");

			loItem = (JMenuItem) loMenu.getWidget("Extras.Upgrade");
			loItem.setText(res.getString(loItem.getText()));
			new CActionListener(loItem, this, "handleUpgrade");
			
			loItem = (JMenuItem) loMenu.getWidget("Extras.Reorganize");
			loItem.setText(res.getString(loItem.getText()));
			new CActionListener(loItem, this, "handleReorganize");

			loItem = (JMenuItem) loMenu.getWidget("Extras.Options");
			loItem.setText(res.getString(loItem.getText()));
			new CActionListener(loItem, this, "handleOptionsDialog");
			
			setJMenuBar((JMenuBar) loMenu.getRootComponent());
			moPane = new JDesktopPane();
			
			this.getContentPane().add(moPane);
			this.pack();
			this.setExtendedState(Frame.MAXIMIZED_BOTH);
			this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			
		} 
		catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}

		addWindowListener(
			new WindowAdapter() {
				public void windowClosed(WindowEvent e) {
				}


				public void windowClosing(WindowEvent e) {
					try {
						close();
					}
					catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			});
	}


 	
	/**
	 * Description of the Method
	 */
	public void setVisible(boolean mode) {
		moAccMan.execRules(getAccessContext());
		super.setVisible(true);

	}


	/**
	 * Description of the Method
	 */
	public void close() {


			try {
				
				Session se = (Session) CServiceProvider.getService( ServiceNames.SESSIONCLASS );
				
				saveProperties("edit",se.getEditDialogProperties());
				saveProperties("ingest",se.getIngestDialogProperties());
				saveProperties("location", se.getLocationDialogProperties());
				saveProperties("new",se.getNewDialogProperties());
				saveProperties("object",se.getObjectDialogProperties());
				saveProperties("replace",se.getReplaceDialogProperties());
				saveProperties("select",se.getSelectDialogProperties());
				saveProperties("dc",se.getEditDCDialogProperties());
				saveProperties("editor",se.getTextEditorProperties());
				saveProperties("harvester",se.getHarvesterDialogProperties());
				saveProperties("templater",se.getTemplaterDialogProperties());
				saveProperties("options",se.getOptionsDialogProperties());
    
				IIIFFactory i3f = (IIIFFactory) CServiceProvider.getService(ServiceNames.I3F_SERVICE);
			    i3f.close();
  
			    SkosifyFactory skosify = (SkosifyFactory) CServiceProvider.getService(ServiceNames.SKOSIFY_SERVICE);
    			skosify.close();

    			CPropertyService props = (CPropertyService) CServiceProvider.getService( ServiceNames.PROPERTIES );
    			
    			if (props.getProperty("user", "authentication.method").equals("ldap")) {
    				User user = (User) CServiceProvider.getService( ServiceNames.CURRENT_USER );

    				String repository = user.getRepository();
    				Hashtable env = new Hashtable();
    				env.put( Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory" );
    				env.put( Context.PROVIDER_URL, props.getProperty( "system", repository + ".ldap.providerURL" ) );
    				env.put( Context.SECURITY_PRINCIPAL, "cn=" + user.getUser() + "," + props.getProperty( "system", repository + ".ldap.userDN" ) + "," + props.getProperty( "system", repository + ".ldap.baseDN" ) );
    				env.put( Context.SECURITY_CREDENTIALS, user.getPasswd() );
    				if ( props.getProperty( "system", repository + ".ldap.providerURL" ).startsWith( "ldaps://" ) ) {
    					env.put( "java.naming.ldap.factory.socket", "org.emile.cirilo.utils.CiriloSocketFactory" );
    				}
    				else {
    					env.put( Context.SECURITY_AUTHENTICATION, "simple" );
    				}

    				DirContext ctx = new InitialDirContext( env );
    				
    				String dns = "cn=handles," + props.getProperty( "system", repository + ".ldap.objectDN" ) + "," + props.getProperty( "system", repository + ".ldap.baseDN" );

    				Handles hdl = (Handles) CServiceProvider.getService( ServiceNames.HANDLESCLASS );
    				ctx.rebind( dns, hdl );
    				ctx.close();
    			}

    			log.info("Program terminated normally");

			}
			catch ( Exception ex ) {
				ex.printStackTrace();
			}

			try {
				this.dispose();
				CApplication.getApp().end();

			}
			catch ( Exception ex ) {
			}
	}

	private void saveProperties(String dialog, CWindowsProperties q) {
		
		try {
			
			CPropertyService props = (CPropertyService) CServiceProvider.getService( ServiceNames.PROPERTIES );
						
			props.setProperty("user", dialog+".dialog.width", new Integer(q.getWidth()).toString());
			props.setProperty("user", dialog+".dialog.height", new Integer(q.getHeight()).toString());
			props.setProperty("user", dialog+".dialog.x", new Integer(q.getX()).toString());
			props.setProperty("user", dialog+".dialog.y", new Integer(q.getY()).toString());
			
			for (int i=0; i<24; i++) {
				int w = q.getWidth(i);
				if (w == 0) break;
				props.setProperty("user", dialog+".dialog.column."+new Integer(i+1).toString(), new Integer(w).toString());
			}				
			props.saveProperties("user");
            
		}
		catch ( Exception ex ) {
			ex.printStackTrace();
		}
		
	}
	
	/**
	 * Gets the accessContext attribute of the CiriloFrame object
	 *
	 * @return The accessContext value
	 */
	public IAccessContext getAccessContext() {
		CDefaultAccessContext loCxt = null;

		try {
		}
		catch (Exception ex) {
			CException.record(ex, this);
		}
		return loCxt;
	}


	/**
	 * Gets the desktopPane attribute of the CiriloFrame object
	 *
	 * @return The desktopPane value
	 */
	public JDesktopPane getDesktopPane() {
		return moPane;
	}


	public void handleReset(ActionEvent e) {

		try {
			Session se = (Session) CServiceProvider.getService( ServiceNames.SESSIONCLASS );						
            se.getEditDialogProperties().setWidth(0);
            se.getIngestDialogProperties().setWidth(0);
            se.getLocationDialogProperties().setWidth(0);
            se.getNewDialogProperties().setWidth(0);
            se.getObjectDialogProperties().setWidth(0);
            se.getReplaceDialogProperties().setWidth(0);
            se.getSelectDialogProperties().setWidth(0);
            se.getEditDCDialogProperties().setWidth(0);
            se.getTextEditorProperties().setWidth(0);
            se.getHarvesterDialogProperties().setWidth(0);
            se.getTemplaterDialogProperties().setWidth(0);
            se.getOptionsDialogProperties().setWidth(0);
		
		}
		catch (Exception ex) {
			CException.record(ex, this);
		}
	}

	public void handleOptionsDialog(ActionEvent e) {

		try {
			CDialogManager dm    = (CDialogManager) CServiceProvider.getService(ServiceNames.DIALOG_MANAGER);
			OptionsDialog dlg  = (OptionsDialog) dm.getDialog(DialogNames.OPTIONS_DIALOG);
			dlg.open();
		}
		catch (Exception ex) {
			CException.record(ex, this);
		}
	}
	
	
	public void handleLogin(ActionEvent e) {

		try {
			
			User user = (User) CServiceProvider.getService( ServiceNames.CURRENT_USER );
			
			if (!user.viaLDAP()) {
				LoginDialog loLogin;				
				loLogin = (LoginDialog) CServiceProvider.getService(DialogNames.LOGIN_DIALOG);
				loLogin.set(false);
				loLogin.open();
				if (!loLogin.isConnected()) {
					System.exit(-1);
				}
			} else {					
				LDAPLoginDialog loLDAPLogin;
				loLDAPLogin = (LDAPLoginDialog) CServiceProvider.getService(DialogNames.LDAPLOGIN_DIALOG);
				loLDAPLogin.set(false);
				loLDAPLogin.open();
				if (loLDAPLogin.isCanceled()) {
					System.exit(-1);
				}
			}	
						
			EditObjectDialog dlg = (EditObjectDialog) CServiceProvider.getService(DialogNames.EDITOBJECT_DIALOG);
			dlg.refresh();

		}
		catch (Exception ex) {
			CException.record(ex, this);
		}
	}

	
	
	/**
	 * Description of the Method
	 *
	 * @param e Description of the Parameter
	 */
	public void handleExit(ActionEvent e) {
		close();
	}



	/**
	 * Description of the Method
	 *
	 * @param e Description of the Parameter
	 */
	public void handleSearch(ActionEvent e) {
	}

	
	public void handleEdit(ActionEvent e) {
		
		try {
			EditObjectDialog dlg;

			dlg = (EditObjectDialog) CServiceProvider.getService(DialogNames.EDITOBJECT_DIALOG);
			dlg.open();
		} catch (Exception ex) {
		}
	}		
	
	public void handleHarvest(ActionEvent e) {
		
		try {
			HarvesterDialog dlg;

			dlg = (HarvesterDialog) CServiceProvider.getService(DialogNames.HARVESTER_DIALOG);
			dlg.open();
		} catch (Exception ex) {
		}
	}		
	
	public void handleTemplater(ActionEvent e) {
		
		try {
			TemplaterDialog dlg;

			dlg = (TemplaterDialog) CServiceProvider.getService(DialogNames.TEMPLATER_DIALOG);
			dlg.open();
		} catch (Exception ex) {
		}
	}		

	public void handleCreate(ActionEvent e) {
		
		try {
			MakeEnvironmentDialog loDlg;
			ResourceBundle res =(ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);


			loDlg = (MakeEnvironmentDialog) CServiceProvider.getService(DialogNames.MAKEENVIRONMENT_DIALOG);
			loDlg.open();
			
			if (loDlg.isOK()) {
				try {
						this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));					
						TemplateSubsystem temps = (TemplateSubsystem) CServiceProvider.getService(ServiceNames.TEMPLATESUBSYSTEM);
						temps.makeTemplate("cirilo:TEI", loDlg.getUser(), "$cirilo:TEI."+loDlg.getUser(), "Untitled", "info:fedora/cm:TEI");
						temps.makeTemplate("cirilo:LIDO", loDlg.getUser(), "$cirilo:LIDO."+loDlg.getUser(), "Untitled", "info:fedora/cm:LIDO");
						temps.makeTemplate("cirilo:Context", loDlg.getUser(), "$cirilo:Context."+loDlg.getUser(), "Untitled", "info:fedora/cm:Context");
						temps.makeTemplate("cirilo:OAIRecord", loDlg.getUser(), "$cirilo:OAIRecord."+loDlg.getUser(), "Untitled", "info:fedora/cm:OAIRecord");
						temps.makeTemplate("cirilo:Environment", loDlg.getUser(), "$cirilo:"+loDlg.getUser(), "Untitled", "");
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				finally {
					this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				    MessageFormat msgFmt = new MessageFormat(res.getString("envok"));
				    Object[] args = {loDlg.getUser()};
					JOptionPane.showMessageDialog(this, msgFmt.format(args) , Common.WINDOW_HEADER, JOptionPane.INFORMATION_MESSAGE );
				}

			}

		}
		catch (Exception ex) {
			CException.record(ex, this);
		}
    }		

	
	public void handleIngest(ActionEvent e) {
		
		try {
			IngestObjectDialog dlg;

			dlg = (IngestObjectDialog) CServiceProvider.getService(DialogNames.INGESTOBJECT_DIALOG);
			dlg.open();
		} catch (Exception ex) {
		}
	}	
	

	public void handleImport(ActionEvent e) {
		
		try {
			ImportDialog dlg;

			dlg = (ImportDialog) CServiceProvider.getService(DialogNames.IMPORT_DIALOG);
			dlg.open();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}	

	public void handleUpgrade(ActionEvent e) {
		
		try {
			UpgradeDialog dlg;

			dlg = (UpgradeDialog) CServiceProvider.getService(DialogNames.UPGRADE_DIALOG);
			dlg.open();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}	
	
	public void handleReorganize(ActionEvent e) {
		
		try {
			ReorganizeDialog dlg;

			dlg = (ReorganizeDialog) CServiceProvider.getService(DialogNames.REORGANIZE_DIALOG);
			dlg.open();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}	
	
	/**
	 * Description of the Method
	 *
	 * @param e Description of the Parameter
	 */
	public void handleAbout(ActionEvent e) {

		try {
			HelpFrame loHelp = new HelpFrame();
			loHelp.setVisible(true);

		}
		catch (Exception ex) {
			CException.record(ex, this);
		}
	}


	/**
	 * Asks business factory to free objects that have been unused for more than
	 * 300 seconds.
	 *
	 * @param e
	 */
	public void handleGarbageTimer(ActionEvent e) {

		try {
		}
		catch (Exception ex) {
			CException.record(ex, this);
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
	 * @param e Description of the Parameter
	 */
	public void handleCloseQuestion(ActionEvent e) {
		moGarbageTimer.stop();
		moGarbageTimer = null;
		this.dispose();
		CApplication.getApp().end();
	}


	private JDesktopPane moPane;
	private Timer moGarbageTimer;
	private CDefaultAccessManager moAccMan;
}

