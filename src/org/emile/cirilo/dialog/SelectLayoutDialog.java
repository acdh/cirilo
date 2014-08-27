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

import org.apache.poi.util.SystemOutLogger;
import org.emile.cirilo.*;
import org.emile.cirilo.utils.*;
import org.emile.cirilo.business.Session;
import org.emile.cirilo.ecm.repository.Repository;
import org.emile.cirilo.gui.jtable.DefaultSortTableModel;

import voodoosoft.jroots.application.*;
import voodoosoft.jroots.core.CServiceProvider;
import voodoosoft.jroots.core.gui.CEventListener;
import voodoosoft.jroots.core.gui.CMouseListener;
import voodoosoft.jroots.dialog.*;
import voodoosoft.jroots.exception.CException;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.*;

import javax.swing.JTable;
import java.util.regex.*;

import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;
import org.jdom.Document;
import org.jdom.Element;

public class SelectLayoutDialog extends CDefaultDialog {
	/**
	 *  Constructor for the SelectLayoutDialog object
	 */
	public SelectLayoutDialog() { }

	public void set (String pid, String dsid, String model, String owner) {
		this.pid = pid;
		this.dsid = dsid;
		this.model = model;
		this.owner = owner;
	}
	
	/**
	 *  Gets the accessContext attribute of the SelectLayoutDialog object
	 *
	 * @return    The accessContext value
	 */
	public IAccessContext getAccessContext() {
		CDefaultAccessContext loCxt = null;

		try {
		} catch (Exception ex) {
			CException.record(ex, this);
		}
		return loCxt;
	}


	/**
	 *  Description of the Method
	 *
	 * @param  e  Description of the Parameter
	 */
	public void handleCloseButton(ActionEvent e)
		throws Exception {
		org.emile.cirilo.dialog.CBoundSerializer.save(this.getCoreDialog(), se.getSelectDialogProperties(), (JTable) null);   
		close();
	}

	public void handleMouseDoubleClick(MouseEvent e, int type) {

		try {

			CEventListener.setBlocked(true);
			// no events while handling

			if (type == MouseEvent.MOUSE_CLICKED) {
				if (e.getClickCount() >= 2) {
					handleSelectButton(null);	
				}
			}
		} catch (Exception ex) {
		} finally {
			CEventListener.setBlocked(false);
		}

	}	

	/**
	 *  Description of the Method
	 *
	 * @param  e              Description of the Parameter
	 * @exception  Exception  Description of the Exception
	 */
	public void handleSelectButton(ActionEvent e) {
		try {
			 org.emile.cirilo.dialog.CBoundSerializer.save(this.getCoreDialog(), se.getSelectDialogProperties(), (JTable) null);   
			 JTable tb = (JTable) getGuiComposite().getWidget("jtLayouts");
			 int[] selected = tb.getSelectedRows();
			 String sel = (String)tb.getValueAt(selected[0],0);
			 if (sel.equals(res.getString("userdef"))) {
	    		LocationDialog dlg = (LocationDialog) CServiceProvider.getService(DialogNames.LOCATION_DIALOG);
	    		dlg.set(pid, dsid, location);
				dlg.open();
				if (!dlg.get().isEmpty()) {
					stylesheet= dlg.get();
					Repository.modifyDatastream (pid, dsid, null, "R", stylesheet);
				}		
			 } else {
			 
			    Split url = new Split(sel);
			    if (pid != null) {
				    Repository.modifyDatastream (pid, dsid, null, "R", url.get());
			    } else {
				    location = url.get(); 
			    }
			    stylesheet = url.get();
			 }   
		} catch (Exception ex) {
		}
		finally {
		     close();
		}
		 
	}


	/**
	 *  Description of the Method
	 *
	 * @exception  CShowFailedException  Description of the Exception
	 */
	public void show()  throws CShowFailedException {
	  try {	
		 SAXBuilder parser = new SAXBuilder();
		
		 org.emile.cirilo.dialog.CBoundSerializer.load(this.getCoreDialog(), se.getSelectDialogProperties(), (JTable) null);
			
		 
	     JTable tb = (JTable) getGuiComposite().getWidget("jtLayouts");
         tb.setShowHorizontalLines(false);

         Vector data = new Vector();         
         HashMap<String,Vector> ss = new HashMap<String,Vector> ();
        	
         stylesheet = "";
         List stylesheets = null;
	     XPath xPath;
    	 Document doc = null;
         
	     try {

	         doc = parser.build(user.getUrl()+"/objects/cirilo%3ABackbone/datastreams/STYLESHEETS/content");
	         if (groups.contains( "administrator") ) {
		     	 xPath = XPath.newInstance( "//stylesheets/stylesheet[(@type='"+dsid+"'"+ (model!=null ? "and @model='"+model+"'":"")+")]" );
	         } else { 
		     	 xPath = XPath.newInstance( "//stylesheets/stylesheet[(@owner='public' or @owner='"+user.getUser()+"') and (@type='"+dsid+"'"+ (model!=null ? "and @model='"+model+"'":"")+")]" );
	         }	 
	     	 stylesheets = (List) xPath.selectNodes( doc );
	       
	   	      
		    if (stylesheets != null) {

		       for (Iterator iter = stylesheets.iterator(); iter.hasNext();) {
		    			 try {
		    				 Element e = (Element) iter.next();
		    				 String label = e.getAttributeValue("label");
		    				 String href = e.getAttributeValue("href");
		    				 Vector row = new Vector();
		    				 row.addElement(label+" "+Common.SEPERATOR+" "+href);
		    				 ss.put(label+" "+Common.SEPERATOR+" "+href, row);
		    			 } catch (Exception ex) {}
		    	
		       }	  
		     }
	     
	     } catch (Exception e){} 

	     try{    	 
	    	 doc = parser.build(user.getUrl()+"/objects/cirilo%3ABackbone/datastreams/"+owner.toUpperCase()+"/content");
	         if (groups.contains( "administrator") ) {
		     	 xPath = XPath.newInstance( "//stylesheets/stylesheet[(@type='"+dsid+"'"+ (model!=null ? "and @model='"+model+"'":"")+")]" );
	         } else { 
			     xPath = XPath.newInstance( "//stylesheets/stylesheet[@type='"+dsid+"'"+ (model!=null ? "and @model='"+model+"'":"")+"]" );
	         }	 
		     stylesheets = (List) xPath.selectNodes( doc );
	     
	    	 if (stylesheets != null) {
    		
	    		 for (Iterator iter = stylesheets.iterator(); iter.hasNext();) {
	    			 try {
	    				 Element e = (Element) iter.next();
	    				 String label = e.getAttributeValue("label");
	    				 String href = e.getAttributeValue("href");
	    				 Vector row = new Vector();
	    				 row.addElement(label+" "+Common.SEPERATOR+" "+href);
	    				 ss.put(label+" "+Common.SEPERATOR+" "+href, row);
	    			 } catch (Exception ex) {}
	    		 }	  
	    	 }      		
	     } catch (Exception e){} 
         	     
		 Vector row = new Vector();
         row.addElement(res.getString("userdef"));
		 ss.put(res.getString("userdef"), row);
	     
	     for (  Map.Entry<String, Vector> e : ss.entrySet() ) data.addElement(e.getValue());	 
	    	 
		 Vector names = new Vector();
		 names.addElement("Stylesheet");
         DefaultSortTableModel dm = new DefaultSortTableModel(data, names);
         tb.setModel(dm);
         tb.setRowSelectionInterval(0,0);
	     
	  } catch (Exception e) {
	  }
	}


	/**
	 *  Description of the Method
	 */
	protected void cleaningUp() {
	}


	/**
	 *  Description of the Method
	 *
	 * @return    Description of the Return Value
	 */
	protected boolean closing() {
		try {
		} catch (Exception e) {
			CException.record(e, this, false);
		}

		return true;
	}

    public String getLocation() {
    	return location;
    }
    public String getStylesheet() {
    	return stylesheet;
    }
    

	/**
	 *  Description of the Method
	 *
	 * @exception  COpenFailedException  Description of the Exception
	 */
	protected void opened() throws COpenFailedException {

		try {
			res =(ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);
			user = (User) CServiceProvider.getService(ServiceNames.CURRENT_USER);

			se = (Session) CServiceProvider.getService( ServiceNames.SESSIONCLASS );						
			CDialogTools.createButtonListener(this, "jbClose", "handleCloseButton");
			CDialogTools.createButtonListener(this, "jbSelect", "handleSelectButton");

			location = ""; stylesheet  ="";
		    JTable tb = (JTable) getGuiComposite().getWidget("jtLayouts");
			new CMouseListener(tb, this, "handleMouseDoubleClick");
  		    groups = (ArrayList) CServiceProvider.getService(ServiceNames.MEMBER_LIST);

		} catch (Exception ex) {
			throw new COpenFailedException(ex);
		} finally {
		}
	}


	private ResourceBundle res;
	private Session se;
	private User user;
	private String pid;
	private String dsid;
    private String model;
    private String owner;
    private String location;
    private String stylesheet;
	private ArrayList<String> groups;

}


