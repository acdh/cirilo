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
import org.emile.cirilo.business.Handles;
import org.emile.cirilo.ecm.repository.FedoraSoapImpl;
import org.emile.cirilo.ecm.repository.FedoraUserToken;
import org.emile.cirilo.ecm.repository.Repository;
import org.emile.cirilo.ecm.templates.TemplateSubsystem;

import java.awt.event.*;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.swing.*;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.ResourceBundle;
import java.util.Properties;

import fedora.client.FedoraClient;
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
public class LDAPLoginDialog extends CDialog {
    private boolean mode;
    
	private static Logger log = Logger.getLogger(LDAPLoginDialog.class);
	/**
	 *  Constructor for the LoginDialog object
	 */
	public LDAPLoginDialog() { }

	public boolean isCanceled() {
		return bCancel;
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
	 * @param  e  Description of the Parameter
	 */
	public void handleCancelButton(ActionEvent e) {
		bCancel = true;
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
		String user;
		String passwd;
		String group;
		SearchControls constraints;
		NamingEnumeration results;
		Hashtable env;

		String[] sattrs = {"cn"};

		ArrayList groups = new ArrayList(16);
		ArrayList member = new ArrayList(16);

		try {
			repository = (String) ((JComboBox) getGuiComposite().getWidget("jcbRepository")).getSelectedItem();

			passwd = (String) moGA.getInput("jpfPassword");
			user = (String) moGA.getInput("jtfUserID");
			group = "";
			bAuthentication = false;
			bAuthorisation = false;
           
			if (repository.contains("gams.") && user.equals("guest")) {
				handleNonValidLogin();
				return;
			}
			
			
			if (!bAuthentication) {
				try {
					env = new Hashtable();
					env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
					env.put(Context.PROVIDER_URL, props.getProperty(repository + ".ldap.providerURL"));
					env.put(Context.SECURITY_PRINCIPAL, "cn=" + user + "," + props.getProperty(repository + ".ldap.userDN") + "," + props.getProperty(repository + ".ldap.baseDN"));
					env.put(Context.SECURITY_CREDENTIALS, passwd);
					if (props.getProperty(repository + ".ldap.providerURL").startsWith("ldaps://")) {
						env.put("java.naming.ldap.factory.socket", "org.emile.cirilo.utils.CiriloSocketFactory");
					} else {
						env.put(Context.SECURITY_AUTHENTICATION, "simple");
					}
					ctx = new InitialDirContext(env);
					bAuthentication = true;

					if (bAuthentication) {
						try {
							constraints = new SearchControls(
									SearchControls.SUBTREE_SCOPE,
							// search entire subtree
									0,
							// no result count limit
									0,
							// no time limit
									sattrs,
							// return all attributes
									true,
							// return objects
									true);
							// dereference links

							try {
								results = ctx.search("cn=editor," + props.getProperty(repository + ".ldap.groupDN") + "," + props.getProperty(repository + ".ldap.baseDN"),
										"(uniqueMember=cn=" + user + "," + props.getProperty(repository + ".ldap.userDN") + "," + props.getProperty(repository + ".ldap.baseDN") + ")",
										constraints);
								if (results != null && results.hasMore()) {
									bAuthorisation = true;
								}
							} catch (Exception e0) {}

							if (!bAuthorisation) {
								results = ctx.search("cn=administrator," + props.getProperty(repository + ".ldap.groupDN") + "," + props.getProperty(repository + ".ldap.baseDN"),
										"(uniqueMember=cn=" + user + "," + props.getProperty(repository + ".ldap.userDN") + "," + props.getProperty(repository + ".ldap.baseDN") + ")",
										constraints);
								if (results != null && results.hasMore()) {
									bAuthorisation = true;
								}
							}

							
							if (bAuthorisation) {

								constraints = new SearchControls(
										SearchControls.SUBTREE_SCOPE,
								// search entire subtree
										0,
								// no result count limit
										0,
								// no time limit
										sattrs,
								// return all attributes
										true,
								// return objects
										true);
								// dereference links

								results = ctx.search(props.getProperty( repository + ".ldap.groupDN") + "," + props.getProperty(repository + ".ldap.baseDN"), "objectClass=*", constraints);
								
								while (results != null && results.hasMore()) {
									SearchResult si = (SearchResult) results.next();
									Attributes attrs = si.getAttributes();
									for (NamingEnumeration ne = attrs.getAll(); ne.hasMoreElements(); ) {
										Attribute attr = (Attribute) ne.next();
										for (Enumeration vals = attr.getAll(); vals.hasMoreElements(); ) {
											Object x = vals.nextElement();
											groups.add(x);
										}
									}
								}

								try {
									CServiceProvider.removeService(ServiceNames.GROUP_LIST);
								} catch (Exception x) {}
								CServiceProvider.addService(groups, ServiceNames.GROUP_LIST);

								
								constraints = new SearchControls(
										SearchControls.SUBTREE_SCOPE,
								// search entire subtree
										0,
								// no result count limit
										0,
								// no time limit
										sattrs,
								// return all attributes
										true,
								// return objects
										true);
								// dereference links

								for (int i = 0; i < groups.size(); i++) {
									group = (String) groups.get(i);
									try {
										results = ctx.search("cn=" + group + "," + props.getProperty(repository + ".ldap.groupDN") + "," + props.getProperty( repository + ".ldap.baseDN"),
												"(uniqueMember=cn=" + user + "," + props.getProperty(repository + ".ldap.userDN") + "," + props.getProperty(repository + ".ldap.baseDN") + ")",
												constraints);
										if (results != null && results.hasMore()) {
											member.add(group);
										}
									} catch (Exception e) {}
								}

								try {
									CServiceProvider.removeService(ServiceNames.MEMBER_LIST);
								} catch (Exception x) {}
								CServiceProvider.addService(member, ServiceNames.MEMBER_LIST);
								
								String url = "http://"+ props.getProperty(repository + ".fedora.host") + "/"+props.getProperty(repository + ".fedora.instance");
								
								User us = new User(user, passwd, getName("fedora"), getPasswd("fedora"), url, repository);
								us.setSesameAuth(user, passwd, props.getProperty(repository + ".sesame.host"));							
								
								try {
									CServiceProvider.removeService(ServiceNames.CURRENT_USER);
								} catch (Exception x) {}								
								CServiceProvider.addService(us, ServiceNames.CURRENT_USER);
								
								String dns = "cn=handles.cirilo," + props.getProperty(repository + ".ldap.objectDN") + "," + props.getProperty(repository + ".ldap.baseDN");
													
								try {
									Handles hdl = (Handles) ctx.lookup(dns);
									CServiceProvider.addService(hdl, ServiceNames.HANDLESCLASS);
									log.debug("Reading handle key from " + dns);
								} catch (Exception ex) {
									Handles hdl = new Handles();
									CServiceProvider.addService(hdl, ServiceNames.HANDLESCLASS);
									ctx.rebind(dns, hdl);
									log.debug("Creating new handle key object" + dns);
								}
								
								
								
								FedoraUserToken token = new FedoraUserToken(us.getUrl(), us.getRootUser(), us.getRootPasswd());
					            FedoraClient client = new FedoraClient(us.getUrl(), us.getRootUser(), us.getRootPasswd());
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
					            Repository.getRelations("cirilo:Backbone");
					            }
					            
								try {
									cprops.setProperty("user", "last.login", user);
									cprops.setProperty("user", "last.repository", repository);
								    cprops.setProperty("user", "fedora.server", "http://"+ props.getProperty(repository + ".fedora.host"));
								    cprops.setProperty("user", "fedora.context", props.getProperty(repository + ".fedora.instance"));
									
									
									cprops.saveProperties("user");
								} catch (Exception e) {}

							}   

						} catch (Exception ex) {
							log.error(ex.getLocalizedMessage(),ex);	
						} finally {
							if (ctx != null) {
								ctx.close();
							}
						}
					}

					if (couldConnect()) {
						close();
					} else {
						handleNonValidLogin();
					}
				} catch (Exception e) {
					handleNonValidLogin();
				}

			}
		} catch (Exception ex) {
			handleNonValidLogin();
		}


	}

	private void handleNonValidLogin() {
		JOptionPane.showMessageDialog(this.getCoreDialog(), res.getString("invalauthent"), "", JOptionPane.WARNING_MESSAGE);
		try {
			if (moGA != null) {
				moGA.setData("jpfPassword", "");
				setDirty(false);
				moGA.requestFocus(moGA.getInput("jtfUserID") != null ? "jpfPassword" : "jtfUserID");
			}
		} catch (Exception e) {
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
			moGA = (IGuiAdapter) getGuiAdapter();
			res = (ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);

			// map buttons
			CDialogTools.createButtonListener(this, "jbLogin", "handleLoginButton");
			CDialogTools.createButtonListener(this, "jbCancel", "handleCancelButton");

			String user = "";
			
			props = new Properties();
			props.load(Cirilo.class.getResourceAsStream("cirilo.properties"));

			cprops = (CPropertyService) CServiceProvider.getService(ServiceNames.PROPERTIES);
			try {
				user = cprops.getProperty("user", "last.login");
			} catch (Exception e) {}
			

			moGA.setData("jtfUserID", user);
			moGA.setData("jpfPassword", "");

			JComboBox jcbRepository = (JComboBox) getGuiComposite().getWidget("jcbRepository");
			String[] rep = ((String) props.getProperty("fedora.repositories")).split(";");

			jcbRepository.setEnabled(this.mode);
			
			for (int i = 0; i < rep.length; i++) {
				jcbRepository.addItem(rep[i]);
			}
			try {
				repository = cprops.getProperty("user", "last.repository");
				if (repository != null) {
					jcbRepository.setSelectedItem(repository);
				}
			} catch (Exception e) {}

			try {
				String repository = props.getProperty("last.repository");
				if (repository != null) {
					jcbRepository.setSelectedItem(repository);
				}
			} catch (Exception e) {}

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

	public boolean couldConnect() {
		return bAuthentication && bAuthorisation;
	}


	/**
	 *  Gets the name attribute of the handleAuth object
	 *
	 * @param  service  Description of the Parameter
	 * @return          The name value
	 */
	private String getName(String service) {
		String s = getAttr(service, "uid");
		return s;
	}


	/**
	 *  Gets the passwd attribute of the handleAuth object
	 *
	 * @param  service  Description of the Parameter
	 * @return          The passwd value
	 */
	private String getPasswd(String service) {
		String s = getAttr(service, "userPassword");
		return s;
	}


	/**
	 *  Gets the passwd attribute of the handleAuth object
	 *
	 * @param  service  Description of the Parameter
	 * @return          The passwd value
	 */
	private String getCertificate(String service) {
		String s = getAttr(service, "userSMIMECertificate");
		return s;
	}


	/**
	 *  Description of the Method
	 *
	 * @param  lsService  Description of the Parameter
	 * @param  lsAttr     Description of the Parameter
	 * @return            Description of the Return Value
	 */
	private String getAttr(String lsService, String lsAttr) {
		String s = "";
		try {
			String[] sattrs = {"uid", "userPassword", "userSMIMECertificate"};

			SearchControls constraints = new SearchControls(
					SearchControls.SUBTREE_SCOPE,
			// search entire subtree
					0,
			// no result count limit
					0,
			// no time limit
					sattrs,
			// return all attributes
					true,
			// return objects
					true);

			NamingEnumeration r = ctx.search("cn=" + lsService + "," + props.getProperty(repository + ".ldap.serviceDN") + "," + props.getProperty(repository + ".ldap.baseDN"), "objectclass=*", constraints);

			while (r != null && r.hasMore()) {
				SearchResult i = (SearchResult) r.next();
				Attributes attrs = i.getAttributes();
				for (NamingEnumeration n = attrs.getAll(); n.hasMoreElements(); ) {
					Attribute attr = (Attribute) n.next();
					String id = attr.getID();
					if (id.equals(lsAttr)) {
						Enumeration v = attr.getAll();
						Object o = v.nextElement();
						if (lsAttr.equals(sattrs[1])) {
							byte[] encPasswd = (byte[]) o;
							s = new String(encPasswd);
						} else {
							s = (String) o;
						}
					}
				}
			}
		} catch (Exception e) {			
		}
			
		return s;
	}

	private boolean bAuthentication;
	private boolean bAuthorisation;

    private Properties props;
    private CPropertyService cprops;
	private IGuiAdapter moGA;
	private ResourceBundle res;
	private boolean bCancel;
	private String repository;
	private DirContext ctx;

}

