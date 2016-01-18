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
import voodoosoft.jroots.core.gui.CMouseListener;
import voodoosoft.jroots.dialog.*;
import voodoosoft.jroots.exception.CException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.emile.cirilo.Common;
import org.emile.cirilo.ServiceNames;
import org.emile.cirilo.User;
import org.emile.cirilo.ecm.templates.*;
import org.emile.cirilo.ecm.repository.*;
import org.emile.cirilo.business.*;
import org.emile.cirilo.*;
import org.emile.cirilo.gui.jtable.DefaultSortTableModel;
import org.emile.cirilo.ecm.repository.FedoraConnector.Relation;
import org.emile.cirilo.utils.ImageTools;
import org.emile.cirilo.utils.Split;
import org.jdom.input.DOMBuilder;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;
import org.jdom.filter.ElementFilter;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.*;

import com.asprise.util.ui.progress.ProgressDialog;

import jsyntaxpane.DefaultSyntaxKit;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.*;

import javax.swing.*;

import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;
import java.net.URL;

import javax.xml.parsers.DocumentBuilderFactory;


/**
 *  Description of the Class
 *
 * @author     Johannes Stigler
 * @created    10.3.2011
 */
public class TextEditor extends CDialog {
    private static final Log LOG = LogFactory.getLog(TextEditor.class);
 
	/**
	 *  Constructor for the TextEditor object
	 */

	public TextEditor() {}

	public void set (String pid, String dsid, String mimetype, String group, String location, String model) {
		this.pid = pid;
		this.dsid = dsid;
		this.mimetype = mimetype;
		this.group = group;
		this.location = location;
		this.model = model;
	}

	/**
	 *  Description of the Method
	 *
	 * @param  e  Description of the Parameter
	 */
	public void handleCancelButton(ActionEvent e) {
		org.emile.cirilo.dialog.CBoundSerializer.save(this.getCoreDialog(), se.getTextEditorProperties(), (JTable) null);   
		close();
	}


	
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
		  org.emile.cirilo.dialog.CBoundSerializer.load(this.getCoreDialog(), se.getTextEditorProperties(), (JTable) null);
          
	      JScrollPane scrPane = (JScrollPane) getGuiComposite().getWidget("scrPane");
		  scrPane.setVisible(false);

		  JEditorPane jebEditorPane = (JEditorPane) getGuiComposite().getWidget("jebEditorPane");
		  if (mimetype.contains("xml")) {
			  	jebEditorPane.setContentType("text/xml");
			    if (group.equals("R")) {
				  	URL url = new URL (location);
			    	jebEditorPane.setPage(url);
			    } else {
			    	DOMBuilder db = new DOMBuilder();	
			    	Document doc = db.build (Repository.getDatastream(pid, dsid));
			    	XMLOutputter outputter = new XMLOutputter();
			    	jebEditorPane.setText(new String(outputter.outputString(doc).getBytes("UTF-8"),"UTF-8"));
			    }
		   } else if (Common.TEXT_MIMETYPES.contains(mimetype)) {
			  	byte[] buf = Repository.getDatastream(pid, dsid, "");
			  	jebEditorPane.setContentType(mimetype);
			  	jebEditorPane.setText(new String(buf));
	      } else if (mimetype.contains("text/log")) {
		  	   jebEditorPane.setContentType("text/plain");			  	
			   jebEditorPane.setText(new String(readFile(pid)));      	  
         }
  	     scrPane.setVisible(true);
 	     
		 } catch (Exception e){
			 e.printStackTrace();
		 }
		 finally {
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
			  
		  se = (Session) CServiceProvider.getService( ServiceNames.SESSIONCLASS );						
	      user = (User) CServiceProvider.getService(ServiceNames.CURRENT_USER);
	      
	      props = (CPropertyService) CServiceProvider.getService(ServiceNames.PROPERTIES);

	      
		  CDialogTools.createButtonListener(this, "jbClose", "handleCancelButton");
		  	  
		  JMenuItem jmiSave = (JMenuItem) getGuiComposite().getWidget("jmiSave");
		  jmiSave.setEnabled(!group.equals("R"));
	      jmiSave.addActionListener(new ActionListener() {
	    	  public void actionPerformed(ActionEvent e) {
    			  JMenuItem jmiSave = null; 
	    		  try {
	    			  jmiSave = (JMenuItem) getGuiComposite().getWidget("jmiSave");
	    			  jmiSave.setEnabled(false);
	    			  getCoreDialog().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	    			  if (mimetype.contains("xml")) {
	    				  JEditorPane jebEditorPane = (JEditorPane) getGuiComposite().getWidget("jebEditorPane");
	    	              try  {		
	    	            	   if (dsid.equals("TEI_SOURCE") && !pid.startsWith("cirilo:")) {
	    	            		   TEI t = new TEI(null,false,false);
	    	            		   t.set(new String(jebEditorPane.getText().getBytes("UTF-8"),"UTF-8"));
	    	            		   t.setPID(pid);
	    	            		   
	    	            		   if (t.isValid()) {
	    	            			   t.validate(null, null);
  	    				  		       jebEditorPane.setText( t.toString());

  	    				  		   	   SAXBuilder builder = new SAXBuilder();
  	    				  		   	   try {
  	    				  		   		   Document doc = builder.build(new StringReader(t.toString()));
  	    				  		   		   Repository.modifyDatastreamByValue(pid, dsid, mimetype, new String(t.toString().getBytes("UTF-8"),"UTF-8"));
  	    				  		   	   } catch (Exception ex) {
  		  	 								JOptionPane.showMessageDialog(  getCoreDialog(),  res.getString("xmlformat") , Common.WINDOW_HEADER, JOptionPane.INFORMATION_MESSAGE); 				    				    	            			   
  	    				  		   	   }
	    	            		   } else {
	    	    		    			MessageFormat msgFmt = new MessageFormat(res.getString("parsererror"));
	    	    		    			Object[] args = {"TEI_SOURCE"}; 		    		
	    	    		    			JOptionPane.showMessageDialog(  getCoreDialog(), msgFmt.format(args), Common.WINDOW_HEADER, JOptionPane.INFORMATION_MESSAGE);
	    	            		   }		
	    	            	   } else if (dsid.equals("STORY") && !pid.startsWith("cirilo:")) {
		    	            		   STORY s = new STORY(null,false,false);
		    	            		   s.set(new String(jebEditorPane.getText().getBytes("UTF-8"),"UTF-8"));    	            		   
		    	            		   if (s.isValid()) {
		    	            			   s.validate(null, null);
		    	            			   s.setPID(pid);
	  	    				  		       jebEditorPane.setText(s.toString());	  	    				  		       
	  	    				  		   	   try {
	  	    				  		   		   Repository.modifyDatastreamByValue(pid, dsid, mimetype, new String(s.toString().getBytes("UTF-8"),"UTF-8"));
	  	    				  		   	   } catch (Exception ex) {
	  		  	 								JOptionPane.showMessageDialog(  getCoreDialog(),  res.getString("xmlformat") , Common.WINDOW_HEADER, JOptionPane.INFORMATION_MESSAGE); 				    				    	            			   
	  	    				  		   	   }
		    	            		   } else {
		    	    		    			MessageFormat msgFmt = new MessageFormat(res.getString("parsererror"));
		    	    		    			Object[] args = {"STORY"}; 		    		
		    	    		    			JOptionPane.showMessageDialog(  getCoreDialog(), msgFmt.format(args), Common.WINDOW_HEADER, JOptionPane.INFORMATION_MESSAGE);
		    	            		   }
	    	            		   
	    	            	   } else if (dsid.equals("LIDO_SOURCE") && !pid.startsWith("cirilo:")) {
		    	            		   LIDO l = new LIDO(null,false,false);
		    	            		   l.set(new String(jebEditorPane.getText().getBytes("UTF-8"),"UTF-8"));
		    	            		   l.setPID(pid);
		    	            		   
		    	            		   if (l.isValid()) {
		    	            			   l.validate(null, null);
	  	    				  		       jebEditorPane.setText( l.toString());

	  	    				  		   	   SAXBuilder builder = new SAXBuilder();
	  	    				  		   	   try {
	  	    				  		   		   Document doc = builder.build(new StringReader(l.toString()));
	  	    				  		   		   Repository.modifyDatastreamByValue(pid, dsid, mimetype, new String(l.toString().getBytes("UTF-8"),"UTF-8"));
	  	    				  		   	   } catch (Exception ex) {
	  		  	 								JOptionPane.showMessageDialog(  getCoreDialog(),  res.getString("xmlformat") , Common.WINDOW_HEADER, JOptionPane.INFORMATION_MESSAGE); 				    				    	            			   
	  	    				  		   	   }
		    	            		   } else {
		    	    		    			MessageFormat msgFmt = new MessageFormat(res.getString("parsererror"));
		    	    		    			Object[] args = {"LIDO_SOURCE"}; 		    		
		    	    		    			JOptionPane.showMessageDialog(  getCoreDialog(), msgFmt.format(args), Common.WINDOW_HEADER, JOptionPane.INFORMATION_MESSAGE);
		    	            		   }
	    	            	   } else if (dsid.equals("DC") && !pid.startsWith("cirilo:")) {
	    	            		   DC d = new DC(null,false,true);
	    	            		   d.set(new String(jebEditorPane.getText().getBytes("UTF-8"),"UTF-8"));
  				  		   		   Repository.modifyDatastreamByValue(pid, dsid, mimetype, new String(d.toString().getBytes("UTF-8"),"UTF-8"));	    	            		   
	    	            	   } else {   
	    	            		   SAXBuilder builder = new SAXBuilder();
	    	            		   try {
	    	            			   Document doc = builder.build(new StringReader(jebEditorPane.getText()));
		  	    				  		    	            		   	    	            		   		  	    				  	
		  	    				  	   if (dsid.equals("METS_SOURCE") && !pid.startsWith("cirilo:")) {
		  	    				  		   METS m = new METS (null, false, true);
		  	    				  		   m.set(new String(jebEditorPane.getText().getBytes("UTF-8"),"UTF-8"));
		  	    				  		   m.setPID(pid);
		  	    				  		   m.ingestImages();
		  	    				  		   m.createMapping(null,null);
		  	    				  		   Repository.modifyDatastreamByValue(pid, dsid, mimetype, m.toString());	
		  	    				  		   jebEditorPane.setText( m.toString());
		  	    				  	   } else {
		  	    				  		   Repository.modifyDatastreamByValue(pid, dsid, mimetype, new String(jebEditorPane.getText().getBytes("UTF-8"),"UTF-8"));
		  	    				  	   }
		  	    				  	   
		  	    				  	   if (dsid.equals("ONTOLOGY")) {

		  	 	 				    		try {
		  	 	 				    			
		  	 	 				    			String pr = props.getProperty("user", "SKOS.IFY"); 
			  	 				    			if (model != null && model.contains("SKOS") && pr != null && pr.equals("1") ) {
			  	 				    				SkosifyFactory skosify = (SkosifyFactory) CServiceProvider.getService(ServiceNames.SKOSIFY_SERVICE);
			  	 				    				String skos = skosify.skosify(new String(jebEditorPane.getText().getBytes("UTF-8")));
			  	 				    				if (!skos.isEmpty()) jebEditorPane.setText(skos);
			  	 				    			} 
			  	 				    			

		  	 	 				    			File temp = File.createTempFile("tmp","xml");
		  	 					           		FileOutputStream fos = new FileOutputStream(temp);
		  	 					           		fos.write(jebEditorPane.getText().getBytes("UTF-8"));
		  	 					           		fos.close();

		  	 									TripleStoreFactory tf = new TripleStoreFactory();
		  	 									if (tf.getStatus()) {
		  	 										tf.update(temp, pid);
		  	 									}	
		  	 									tf.close();
		  	 					           		temp.delete();
		  	 				    		} catch (Exception ex) {
		  	 				    			ex.printStackTrace();
		  	 								JOptionPane.showMessageDialog(  getCoreDialog(), ex.getMessage(), Common.WINDOW_HEADER, JOptionPane.INFORMATION_MESSAGE); 				    			
		  	 				    		}
		  	    				  	  }		
	    	            		   } catch (Exception ex) {
	  	 								JOptionPane.showMessageDialog(  getCoreDialog(),  res.getString("xmlformat") , Common.WINDOW_HEADER, JOptionPane.INFORMATION_MESSAGE); 				    				    	            			   
	    	            		   }
		  	    				  	
	    	            	   }
	    	                } catch (Exception eq) {
	    	                	eq.printStackTrace();
	    	                }

	    			  }
	    			  if ((Common.TEXT_MIMETYPES.contains(mimetype))) {
	    				  JEditorPane jebEditorPane = (JEditorPane) getGuiComposite().getWidget("jebEditorPane");
	    				  Repository.modifyDatastream(pid, dsid, mimetype, jebEditorPane.getText().getBytes("UTF-8"));
	    				  
	    			  }
	    	     } catch (Exception ex) {
	    	    	 ex.printStackTrace();
	    	     }
	 	  		 finally {
	 	  			if (jmiSave!= null) jmiSave.setEnabled(!group.equals("R"));
	 				getCoreDialog().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	 			}
	    	  }
	      });	      
	      JMenuItem jmiQuit = (JMenuItem) getGuiComposite().getWidget("jmiQuit");
	      jmiQuit.addActionListener(new ActionListener() {
	    	  public void actionPerformed(ActionEvent e) {
	    	     handleCancelButton(null);
	    	  }
	      });
		} catch (Exception ex) {
			throw new COpenFailedException(ex);
		}
	}

	private String readFile( String file ) throws IOException {
	    BufferedReader reader = new BufferedReader( new FileReader (file));
	    String line  = null;
	    StringBuilder stringBuilder = new StringBuilder();
	    String ls = System.getProperty("line.separator");
	    while( ( line = reader.readLine() ) != null ) {
	        stringBuilder.append( line );
	        stringBuilder.append( ls );
	    }
	    return stringBuilder.toString();
	 }

	private User user;
	private CPropertyService props;
	private ArrayList<String> groups;
	private ResourceBundle res;
	private Session se;
	private String pid;
	private String dsid;
	private String mimetype;
	private String group;
	private String location;
	private String model;
}

