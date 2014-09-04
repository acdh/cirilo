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

import org.emile.cirilo.gui.*;
import org.emile.cirilo.dialog.*;
import org.emile.cirilo.business.Session;
import org.emile.cirilo.ecm.repository.FedoraSoapImpl;
import org.emile.cirilo.ecm.repository.FedoraUserToken;
import org.emile.cirilo.ecm.repository.Repository;
import org.emile.cirilo.ecm.templates.TemplateSubsystem;
import org.emile.cirilo.ServiceNames;
import org.emile.cirilo.business.*;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import voodoosoft.jroots.application.*;
import voodoosoft.jroots.core.*;
import voodoosoft.jroots.core.gui.*;
import voodoosoft.jroots.exception.*;
import voodoosoft.jroots.gui.CGuiManager;

import com.digitprop.tonic.*;

import fedora.client.FedoraClient;

import javax.swing.JFrame;
import javax.swing.UIManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.applet.*;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.net.URL;
import java.awt.*;
import java.io.*;
import java.util.*;

import org.apache.sanselan.*;
import org.apache.sanselan.common.*;
import org.geonames.*;
import org.openrdf.repository.http.HTTPRepository;
import org.openrdf.repository.manager.RemoteRepositoryManager;
import org.openrdf.sail.config.SailImplConfig;
import org.openrdf.sail.memory.config.MemoryStoreConfig;
import org.openrdf.repository.config.RepositoryImplConfig;
import org.openrdf.repository.sail.config.SailRepositoryConfig;
import org.openrdf.sail.inferencer.fc.config.ForwardChainingRDFSInferencerConfig;
import org.openrdf.sail.nativerdf.*;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
;


/**
 * Description of the Class
 *
 * @author yoda
 * @created 1. März 2011
 */
public class Cirilo extends CiriloApplet {

	/**
	 * The main program for the Cirilo class
	 *
	 * @param args The command line arguments
	 */
	public static void main(String[] args) {
		AppletFrame frame = new AppletFrame(new CiriloApplet());
		frame.setSize(0, 0);
		frame.setVisible(true);
	}
}

/**
 * Description of the Class
 *
 * @author yoda
 * @created 07. September 2006
 */
class CiriloApplet extends JApplet {

	private CiriloWindow oWnd;
	
	/**
	 * Description of the Method
	 */
	public void init() {

		Container contentPane = getContentPane();
		oWnd = new CiriloWindow(getParameter("Applet"));
		contentPane.add(oWnd);
	}


	/**
	 * Description of the Method
	 */
	public void stop() {
	}

}

/**
 * Description of the Class
 *
 * @author yoda
 * @created 07. September 2006
 */
class CiriloWindow extends JPanel {
    private static final Log LOG = LogFactory.getLog(CiriloWindow.class);
	private Format format;
	private XMLOutputter outputter;


	/**
	 *Constructor for the CiriloWindow object
	 *
	 * @param applet Description of the Parameter
	 */
	public CiriloWindow(String applet) {

		CiriloApp loCirilo;
		try {
			javax.swing.UIManager.setLookAndFeel(new TonicLookAndFeel());
		}
		catch (Exception e) {}

		loCirilo = new CiriloApp();
		loCirilo.begin();

	}


	/**
	 * Description of the Method
	 */
	public void stop() {
	}


	/**
	 * Description of the Class
	 *
	 * @author yoda
	 * @created 07. September 2006
	 */
	public class CiriloApp extends CApplication {

		private ArrayList<String> files;

		/**
		 *Constructor for the CiriloApp object
		 */
		public CiriloApp() {
			setApp(this);
		}


		/**
		 * Description of the Method
		 */
		public void begin() {

			try {
													

/*				
				ImagePHash ph = new ImagePHash();

				String image1 = ph.getHash(new FileInputStream(new File("c:/temp/1.jpg")));
				String image2 = ph.getHash(new FileInputStream(new File("c:/temp/2.jpg")));

				System.out.println("Score is " + ph.distance(image1, image2));
*/	
			
//				String xmpXml = Sanselan.getXmpXml(new File("c:/temp/image.jpg"));
				
				
/*				
				IImageMetadata metadata = Sanselan.getMetadata(new File("c:/temp/image.tif"));
				System.out.println(metadata);
				String xmpXml = Sanselan.getXmpXml(new File("c:/temp/image.tif"));
				System.out.println(xmpXml);
*/				
		 		   								
				               
                
				Properties p = new Properties();
				p.load(Cirilo.class.getResourceAsStream("cirilo.ini"));
				CPropertyService props = new CPropertyService();
				props.cacheProperties(p, "system");
				String home = System.getenv("USERPROFILE");	
				
				System.setProperty("file.encoding", "UTF-8");

				
				if (System.getProperty("os.name").toLowerCase().indexOf("windows") >- 1) {
					File file = new File ( home );
					if (!file.canWrite()) { 
						home = System.getenv("TEMP");
						File temp = new File ( home );
						if (!temp.canWrite()) { home = System.getProperty("user.home"); }
					}
				} else {
			    	 home = System.getProperty("user.home");
				}
								
				System.setProperty("user.home",home);
				home = home + System.getProperty("file.separator")+ "cirilo.ini";
				try {
					File fp = new File(home );
					fp.createNewFile();
					props.cacheProperties(home, "user");
				} catch (Exception e) {}
				
				CServiceProvider.addService(props, ServiceNames.PROPERTIES);
				
				String lang =props.getProperty("user","interface.language");
				if (lang == null) lang = "en";

				ResourceBundle  res = Common.getResourceBundle(lang);				
				CServiceProvider.addService(res,ServiceNames.RESOURCES);
					
				String title = Common.WINDOW_HEADER;
				// show splash screen
				SplashFrame loSplash = new SplashFrame(11);
				loSplash.setVisible(true);

				// load application properties
				loSplash.addLog(res.getString("loadprop"));				

																							
				CEventListener.setBlocked(true);

				// Setup access manager
				loSplash.addLog(res.getString("sam"));
				Setup.AccessManager();

				// setup gui
				loSplash.addLog(res.getString("dam"));
				CGuiManager loGuiMan = Setup.GUI();
				Setup.Dialogs(loGuiMan);

				ArrayList preallocations = new ArrayList();
				preallocations.add(res.getString("notitle"));
				for (int i=1;i<14;i++) preallocations.add("");
				
				CServiceProvider.addService(preallocations, ServiceNames.DCMI_PREALLOCATIONS);

				
				LoginDialog loLogin;
				
				loLogin = (LoginDialog) CServiceProvider.getService(DialogNames.LOGIN_DIALOG);
				loLogin.set(true);
				loLogin.open();

				
				loSplash.dispose();
				if (!loLogin.isConnected()) {
					System.exit(-1);
				}
				
				// finish

				User user = (User) CServiceProvider.getService(ServiceNames.CURRENT_USER);

				JFrame loFrame = (JFrame) CServiceProvider.getService(ServiceNames.FRAME_WINDOW);
				loFrame.setVisible(true);
				loFrame.toFront();				
				loFrame.setTitle(title);

				CEventListener.setBlocked(false);

				try {
		 			String ses = (String) props.getProperty("user", "sesame.server");
		        	RemoteRepositoryManager repositoryManager = new RemoteRepositoryManager(ses == null ? Common.SESAME_SERVER : ses);
		        	repositoryManager.initialize();
					SailImplConfig backendConfig = new MemoryStoreConfig(true);				 
					backendConfig = new ForwardChainingRDFSInferencerConfig(backendConfig);
					SailRepositoryConfig repositoryTypeSpec = new SailRepositoryConfig(backendConfig);
					org.openrdf.repository.config.RepositoryConfig repConfig = new org.openrdf.repository.config.RepositoryConfig(user.getUrl().substring(7).replace("/","."), repositoryTypeSpec);
					repositoryManager.addRepositoryConfig(repConfig);					
				} catch (Exception s) {
				}

				try {
					if (!Repository.exist("sdef:TEI")) {
						DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
						DocumentBuilder builder = factory.newDocumentBuilder();
						String format = FedoraClient.FOXML1_1.uri;
						String fedora = user.getUrl();						
						String host = fedora.substring(0,fedora.lastIndexOf("/"));
						String cocoon = host+"/cocoon";
				    	files = new ArrayList<String>();
	                    File fp = new File ("bundle/cm");
				    	treeWalk(fp);
						for (int i = 0; i<files.size(); i++) {
						  try {
					    	Document doc = builder.parse((String) files.get(i));
							DOMSource domSource = new DOMSource(doc);
							StringWriter writer = new StringWriter();
							StreamResult result = new StreamResult(writer);
							TransformerFactory tf = TransformerFactory.newInstance();
							Transformer transformer = tf.newTransformer();
							transformer.transform(domSource, result);
							InputSource is = new InputSource();
							is.setCharacterStream(new StringReader(writer.toString()
							.replaceAll("http://gams.uni-graz.at/archive", fedora)
							.replaceAll("http://gams.uni-graz.at/cocoon", cocoon)
							.replaceAll("http://gams.uni-graz.at", host)
							.replaceAll(host+"#", "http://gams.uni-graz.at#")
							.replaceAll(host+"/ontology#","http://gams.uni-graz.at/ontology#")));
							doc = builder.parse(is);
							Repository.ingestDocument(doc,  format, "Object ingested by Cirilo");
						  } catch (Exception eq) {
							  eq.printStackTrace();
						  }	
						}		
					}
				} catch (Exception s) {
				}
				
				CServiceProvider.addService(new Session(), ServiceNames.SESSIONCLASS);				
				Session se = (Session) CServiceProvider.getService( ServiceNames.SESSIONCLASS );						

				se.loadEditDialogProperties();
				se.loadIngestDialogProperties();
				se.loadLocationDialogProperties();
				se.loadNewDialogProperties();
				se.loadObjectDialogProperties();
				se.loadReplaceDialogProperties();
				se.loadSelectDialogProperties();
				se.loadEditDCDialogProperties();
				se.loadTextEditorProperties();
				se.loadHarvesterDialogProperties();
				se.loadTemplaterDialogProperties();
				se.loadOptionsDialogProperties();
				
				
			}
			catch (Exception ex) {
				ex.printStackTrace();
				exit();
			}

		}

		private void treeWalk(File file) {
			  try {
		 		if (file.isDirectory()) {
			 		File[] children = file.listFiles();
			 		for (int i = 0; i < children.length; i++) {
			 			treeWalk(children[i]);
			 		}
		     	} else if (file.getAbsolutePath().toLowerCase().endsWith(".xml")) {
		     		files.add(file.getAbsolutePath());
		     	}
			  } catch (Exception e) {e.printStackTrace();}	
		}	

		/**
		 * Description of the Method
		 */
		public void end() {

			JFrame loFrame;
			int liChoice = 0;

			try {

				loFrame = (JFrame) CServiceProvider.getService(ServiceNames.FRAME_WINDOW);
				if (loFrame != null) CEventListener.removeListener(loFrame);
			}
			catch (Exception ex) {
				CException.record(ex, this, false);
			}
			finally {
				CEventListener.printLog();
				CServiceProvider.removeAllServices();
			}
		}


		/**
		 * Description of the Method
		 */
		private void exit() {

			JFrame loFrame;

			try {
				loFrame = (JFrame) CServiceProvider.getService(ServiceNames.FRAME_WINDOW);
				if (loFrame != null) {
					loFrame.dispose();
				}

			}
			catch (Exception ex) {
			}
			finally {
				System.exit(-1);
			}
		}

	}
	
}

/**
 * Description of the Class
 *
 * @author yoda
 * @created 07. September 2006
 */
class AppletFrame extends JFrame implements AppletStub, AppletContext {


	/**
	 *Constructor for the AppletFrame object
	 *
	 * @param applet Description of the Parameter
	 */
	public AppletFrame(Applet applet) {
		this.applet = applet;
		Container contentPane = getContentPane();
		contentPane.add(this.applet);
		this.applet.setStub(this);
	}


	/**
	 * Description of the Method
	 */
	public void setVisible(boolean mode) {
		applet.init();
		super.setVisible(true);
		applet.start();
		this.dispose();
	}


	// AppletStub methods
	/**
	 * Gets the active attribute of the AppletFrame object
	 *
	 * @return The active value
	 */
	public boolean isActive() {
		return true;
	}


	/**
	 * Gets the documentBase attribute of the AppletFrame object
	 *
	 * @return The documentBase value
	 */
	public URL getDocumentBase() {
		return null;
	}


	/**
	 * Gets the codeBase attribute of the AppletFrame object
	 *
	 * @return The codeBase value
	 */
	public URL getCodeBase() {
		return null;
	}


	/**
	 * Gets the parameter attribute of the AppletFrame object
	 *
	 * @param name Description of the Parameter
	 * @return The parameter value
	 */
	public String getParameter(String name) {
		return "";
	}


	/**
	 * Gets the appletContext attribute of the AppletFrame object
	 *
	 * @return The appletContext value
	 */
	public AppletContext getAppletContext() {
		return this;
	}


	/**
	 * Description of the Method
	 *
	 * @param width Description of the Parameter
	 * @param height Description of the Parameter
	 */
	public void appletResize(int width, int height) { }


	// AppletContext methods
	/**
	 * Gets the audioClip attribute of the AppletFrame object
	 *
	 * @param url Description of the Parameter
	 * @return The audioClip value
	 */
	public AudioClip getAudioClip(URL url) {
		return null;
	}


	/**
	 * Gets the image attribute of the AppletFrame object
	 *
	 * @param url Description of the Parameter
	 * @return The image value
	 */
	public Image getImage(URL url) {
		return null;
	}


	/**
	 * Gets the applet attribute of the AppletFrame object
	 *
	 * @param name Description of the Parameter
	 * @return The applet value
	 */
	public Applet getApplet(String name) {
		return null;
	}


	/**
	 * Gets the applets attribute of the AppletFrame object
	 *
	 * @return The applets value
	 */
	public Enumeration getApplets() {
		return null;
	}


	/**
	 * Description of the Method
	 *
	 * @param url Description of the Parameter
	 */
	public void showDocument(URL url) { }


	/**
	 * Description of the Method
	 *
	 * @param url Description of the Parameter
	 * @param target Description of the Parameter
	 */
	public void showDocument(URL url, String target) { }


	/**
	 * Description of the Method
	 *
	 * @param status Description of the Parameter
	 */
	public void showStatus(String status) { }


	/**
	 * Sets the stream attribute of the AppletFrame object
	 *
	 * @param key The new stream value
	 * @param stream The new stream value
	 */
	public void setStream(String key, InputStream stream) { }


	/**
	 * Gets the stream attribute of the AppletFrame object
	 *
	 * @param key Description of the Parameter
	 * @return The stream value
	 */
	public InputStream getStream(String key) {
		return null;
	}


	/**
	 * Gets the streamKeys attribute of the AppletFrame object
	 *
	 * @return The streamKeys value
	 */
	public Iterator getStreamKeys() {
		return null;
	}


	private Applet applet;

}
