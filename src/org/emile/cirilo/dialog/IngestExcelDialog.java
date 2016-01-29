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

import org.emile.cirilo.ServiceNames;
import org.emile.cirilo.User;

import java.awt.event.*;
import java.io.File;
import java.util.ResourceBundle;

import javax.swing.*;

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
public class IngestExcelDialog extends CDialog {
    
	/**
	 *  Constructor for the LoginDialog object
	 */
	public IngestExcelDialog() { }

	public boolean isSubmit() {
		return bSubmit;
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

	   public String getTemplate() { return template ;}
	   public String getTable() { return table ;}
	

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
	public void handleSubmitButton(ActionEvent e) {

		try {
					
            
			template = (String) moGA.getInput("jtfTEITemplate");
			table = (String) moGA.getInput("jtfEXCELTable");
			
		    props.setProperty("user", "excel.template", template);
		    props.setProperty("user", "excel.table", table);
			props.saveProperties("user");
            bSubmit = true;
			
			close();
			
		} catch (Exception ex) {
		}

	}

	public void handleTemplateButton(ActionEvent e) {

		try {

			JFileChooser chooser = new JFileChooser(props.getProperty("user", "ingest.template.path"));

			chooser.setDialogTitle( res.getString("choosetemp"));
			chooser.addChoosableFileFilter(new FileFilter(".xml"));
			if (chooser.showDialog(getCoreDialog(), res.getString("choose")) != JFileChooser.APPROVE_OPTION) {
				return;
			}

			props.setProperty("user", "ingest.template.path", chooser.getCurrentDirectory().getAbsolutePath());
			props.saveProperties("user");
			moGA.setData("jtfTEITemplate", chooser.getSelectedFile());
			

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public void handleExcelButton(ActionEvent e) {

		try {
			JFileChooser chooser = new JFileChooser(props.getProperty("user", "ingest.excel.path"));

			chooser.setDialogTitle( res.getString("choosesource"));
			chooser.addChoosableFileFilter(new FileFilter(".xlsx"));
			if (chooser.showDialog(getCoreDialog(),  res.getString("choose")) != JFileChooser.APPROVE_OPTION) {
				return;
			}

			props.setProperty("user", "ingest.excel.path", chooser.getCurrentDirectory().getAbsolutePath());
			props.saveProperties("user");
			moGA.setData("jtfEXCELTable", chooser.getSelectedFile());
			
			
		} catch (Exception ex) {
			ex.printStackTrace();
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
		       bSubmit = false;
				try {
					template = props.getProperty("user", "excel.template");
					table = props.getProperty("user", "excel.table");
					
					moGA.setData("jtfTEITemplate", template != null ? template : "");
					moGA.setData("jtfEXCELTable", table != null ? table : "");
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
			
			props = (CPropertyService) CServiceProvider.getService(ServiceNames.PROPERTIES);
			res =(ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);

			moGA = (IGuiAdapter) getGuiAdapter();

			// map buttons
			CDialogTools.createButtonListener(this, "jbSubmit", "handleSubmitButton");
			CDialogTools.createButtonListener(this, "jbCancel", "handleCancelButton");
			CDialogTools.createButtonListener(this, "jbTEITemplate", "handleTemplateButton");
			CDialogTools.createButtonListener(this, "jbEXCELTable", "handleExcelButton");


			User us = (User) CServiceProvider.getService(ServiceNames.CURRENT_USER);

			setDirty(false);

		} catch (Exception ex) {
			throw new COpenFailedException(ex);
		}
	}

	class FileFilter extends javax.swing.filechooser.FileFilter {
		  private String filter;
		  public FileFilter(String f) {
			  this.filter = f;
		  }
		  public boolean accept(File file) {
			    if (file.isDirectory()) {
			    	return true;
			        }
		    String filename = file.getName();
		    return filename.endsWith(this.filter);
		  }
		  public String getDescription() {
			    return "*"+this.filter;
		  }
	}      

	private CPropertyService props;
	private IGuiAdapter moGA;
	private String template;
	private String table;
	private boolean bSubmit;
	private ResourceBundle res;
}

