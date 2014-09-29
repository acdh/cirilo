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

import org.emile.cirilo.*;
import org.emile.cirilo.business.*;
import org.emile.cirilo.ecm.repository.FedoraSoapImpl;
import org.emile.cirilo.ecm.repository.FedoraUserToken;
import org.emile.cirilo.ecm.repository.Repository;
import org.emile.cirilo.ecm.templates.TemplateSubsystem;
import org.emile.cirilo.ServiceNames;
import org.emile.cirilo.User;

import java.awt.event.*;
import javax.swing.*;

import java.net.*;
import java.io.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fedora.client.FedoraClient;

import java.util.Hashtable;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.naming.*;
import javax.naming.directory.*;

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
public class LoginDialog extends CDialog {
    private static final Log LOG = LogFactory.getLog(LoginDialog.class);
    private boolean mode;
    
	/**
	 *  Constructor for the LoginDialog object
	 */
	public LoginDialog() { }

	public boolean isConnected() {
		return bAuthentication;
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
			getGuiComposite().getWidget("jbLogin").setEnabled(ab_IsDirty);
		} catch (Exception ex) {
		}
	}


	public void set(boolean mode) { this.mode = mode;}
	
	/**
	 *  Description of the Method
	 *
	 * @return    Description of the Return Value
	 */


	/**
	 *  Description of the Method
	 *
	 * @param  e  Description of the Parameter
	 */
	public void handleCancelButton(ActionEvent e) {
		close();
	}


	/**
	 *  Description of the Method
	 *
	 * @param  e  Description of the Parameter
	 */
	public void handleLoginButton(ActionEvent e) {
		handleLoginButton();
	}


	/**
	 *  Description of the Method
	 */
	public void handleLoginButton() {

		ArrayList groups = new ArrayList(16);
		ArrayList member = new ArrayList(16);

		try {
			props = (CPropertyService) CServiceProvider.getService(ServiceNames.PROPERTIES);
			
			protocol = (String) ((JComboBox) getGuiComposite().getWidget("jcbProtocol")).getSelectedItem();
			passwd = (String) moGA.getInput("jpfPassword");
			user = (String) moGA.getInput("jtfUserID");
			server = (String) moGA.getInput("jtfServer");
			context =(String) moGA.getInput("jtfContext");
			group = "";
								
  		    try {
			    props.setProperty("user", "current.owner", user);
			    props.setProperty("user", "fedora.server", server);
			    props.setProperty("user", "fedora.protocol", protocol);			    
			    props.setProperty("user", "fedora.context", context);
				props.setProperty("user", "sesame.server", "http://"+server+"/openrdf-sesame");
				props.saveProperties("user");

  		    } catch (Exception e) {}
			
            server = protocol+"://"+server+"/"+context;
  		    
			FedoraUserToken token = new FedoraUserToken(server, user, passwd);
            FedoraClient client = new FedoraClient(server, user, passwd);
            String [] rep=client.getAPIA().describeRepository().getRepositoryVersion().split("\\D");
                        
            int version = new Integer(rep[0]).intValue();
            
            if (version > 2) {
            
	        Repository.initialise(token,new FedoraSoapImpl());
            TemplateSubsystem temps = new TemplateSubsystem();

			try {
				CServiceProvider.removeService(ServiceNames.TEMPLATESUBSYSTEM);
			} catch (Exception x) {}								
            CServiceProvider.addService(temps, ServiceNames.TEMPLATESUBSYSTEM);

            //check Authentication
            Repository.listDatastreams("fedora-system:ContentModel-3.0", true);
            
            groups.add("administrator");           
            groups.add("editor");
            
            if (user.equals("fedoraAdmin") || user.equals("yoda") || user.equals("admin")) {
                member.add("administrator");			            	
            } else {
                member.add("editor");			            	
            }
            
			User us = new User(user, passwd, server);
			
			String iipus = props.getProperty("user", "iipsrv.user");
			String iipho = props.getProperty("user", "iipsrv.host");
			us.setIIPSAuth(iipus == null ? "" : iipus, "", iipho == null ? "" : iipho);

			try {
				CServiceProvider.removeService(ServiceNames.CURRENT_USER);
			} catch (Exception x) {}								
			CServiceProvider.addService(us, ServiceNames.CURRENT_USER);
			
 			try {
				CServiceProvider.removeService(ServiceNames.GROUP_LIST);
			} catch (Exception x) {}
			CServiceProvider.addService(groups, ServiceNames.GROUP_LIST);

			try {
				CServiceProvider.removeService(ServiceNames.MEMBER_LIST);
			} catch (Exception x) {}
			CServiceProvider.addService(member, ServiceNames.MEMBER_LIST);
			bAuthentication = true;
		
			close();
			
            } else {
            
            	JOptionPane.showMessageDialog(null,  client.getAPIA().describeRepository().getRepositoryVersion(), Common.WINDOW_HEADER, JOptionPane.WARNING_MESSAGE);
            }

		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, res.getString("invalauthent"), Common.WINDOW_HEADER, JOptionPane.WARNING_MESSAGE);
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
			
			
			bAuthentication = false;
			res =(ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);
			moGA = (IGuiAdapter) getGuiAdapter();

			// map buttons
			CDialogTools.createButtonListener(this, "jbLogin", "handleLoginButton");
			CDialogTools.createButtonListener(this, "jbCancel", "handleCancelButton");

			String user = "";
			// setup widgets
			CPropertyService props = (CPropertyService) CServiceProvider.getService(ServiceNames.PROPERTIES);
			try {
				user = props.getProperty("user", "current.owner");
				server = props.getProperty("user", "fedora.server");
				protocol = props.getProperty("user", "fedora.protocol");
				context = props.getProperty("user", "fedora.context");
				context = context == null ? "fedora" : context;
			} catch (Exception e) {}

			moGA.setData("jtfUserID", user);
			moGA.setData("jtfServer", server);
			moGA.setData("jtfContext", context);
			moGA.setData("jpfPassword", "");
		    ((JComboBox) getGuiComposite().getWidget("jcbProtocol")).setSelectedItem(protocol);

		    String localHost = InetAddress.getLocalHost().getHostName();
		    boolean localIP = false;
		    for (InetAddress ip : InetAddress.getAllByName(localHost)) {
		 //   	  if (ip.toString().contains(Common.LOCAL_SUBNET_IDENTIFIER)) localIP=true; 
		    }

		    if (localIP) {
				JTextField tf = (JTextField) getGuiComposite().getWidget("jtfServer");
				tf.setText(Common.LOCAL_FEDORA_HOST);
				tf.setEditable(false);
				tf = (JTextField) getGuiComposite().getWidget("jtfContext");
				tf.setText(Common.LOCAL_FEDORA_CONTEXT);
				tf.setEditable(false);
				JComboBox cb = (JComboBox) getGuiComposite().getWidget("jcbProtocol");
                cb.setSelectedIndex(Common.LOCAL_FEDORA_PROTOCOL);
                cb.setEnabled(false);
				tf = (JTextField) getGuiComposite().getWidget("jtfUserID");
				tf.setText("guest");
				tf.setEditable(false);
			}
					
			setDirty(false);
			moGA.requestFocus(user != null ? "jpfPassword" : "jtfUserID");

			JPasswordField pf = (JPasswordField) getGuiComposite().getWidget("jpfPassword");
			pf.addKeyListener(
				new KeyAdapter() {
					public void keyPressed(KeyEvent ev) {
						if (ev.getKeyCode() == KeyEvent.VK_ENTER) {
							ev.consume();
							handleLoginButton();
						}
					}
				});

		} catch (Exception ex) {
			throw new COpenFailedException(ex);
		}
	}



	private IGuiAdapter moGA;
	private CPropertyService props;
	private ResourceBundle res;
	private boolean bAuthentication;
	private String user;
	private String passwd;
	private String group;
	private String server;
	private String protocol;
	private String context;
	private String language;
}

