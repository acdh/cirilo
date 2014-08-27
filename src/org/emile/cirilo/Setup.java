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

import org.emile.cirilo.business.Session;
import org.emile.cirilo.dialog.*;

import voodoosoft.jroots.application.*;
import voodoosoft.jroots.business.*;
import voodoosoft.jroots.core.*;
import voodoosoft.jroots.data.*;
import voodoosoft.jroots.dialog.*;
import voodoosoft.jroots.gui.*;
import org.emile.cirilo.ServiceNames;
import org.emile.cirilo.dialog.DialogNames;
import org.emile.cirilo.dialog.OptionsDialog;

import java.util.ResourceBundle;
import java.text.DateFormat;
import javax.swing.*;

import java.util.*;

/**
 * Description of the Class
 *
 * @author yoda
 * @created 07. September 2006
 */
public class Setup {
	/**
	 *Constructor for the Setup object
	 */
	private Setup() { }


	/**
	 * Description of the Method
	 *
	 * @exception Exception Description of the Exception
	 */
	public static void AccessManager()
		throws Exception {

		CDefaultAccessManager loAccMan;
	
		loAccMan = new CDefaultAccessManager(null);
		CServiceProvider.addService(loAccMan, ServiceNames.ACCESS_MANAGER);
	}


	/**
	 * Description of the Method
	 *
	 * @exception Exception Description of the Exception
	 */
	public static void BusinessObjects()
		throws Exception {

	}


	/**
	 * Description of the Method
	 *
	 * @param aoGuiMan Description of the Parameter
	 * @exception Exception Description of the Exception
	 */
	public static void Dialogs(CGuiManager aoGuiMan)
		throws Exception {
		
		ResourceBundle res =(ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);

		NewObjectDialog loNewObjectDialog;
		IngestObjectDialog loIngestObjectDialog;
		EditObjectDialog loEditObjectDialog;
		EditDCDialog loEditDCDialog;
		ObjectEditorDialog loObjectEditorDialog;
		ReplaceDialog loReplaceDialog;
		SelectLayoutDialog loSelectLayoutDialog;;
		LoginDialog loLoginDialog;
		LoginIIPSDialog loLoginIIPSDialog;
		CreateDatastreamDialog loCreateDatastreamDialog;
		LoginExistDialog loLoginExistDialog;
		IngestExcelDialog loIngestExcelDialog;
		ImportDialog loImportDialog;
		MakeEnvironmentDialog loMakeEnvironmentDialog;
		LocationDialog loLocationDialog;
		HarvesterDialog loHarvesterDialog;
		TemplaterDialog loTemplaterDialog;
		OptionsDialog loOptionsDialog;
		TextEditor loTextEditor;
		CiriloFrame loFrame;
		CDialogManager loDialogManager;
		IDialogCreator loCreator;
		CDialogCreator loDialogCreator;

		// dialog date format
		CDefaultGuiAdapter.setDateFormat(DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.GERMAN));
		CFormatTransformer.registerTransformer("DefaultDialogDate", DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.GERMAN));

		// dialog manager
		loDialogManager = new CDialogManager();
		CServiceProvider.addService(loDialogManager, ServiceNames.DIALOG_MANAGER);
		
		// parent for all dialogs
		loFrame = (CiriloFrame) CServiceProvider.getService(ServiceNames.FRAME_WINDOW);

		// creator for internal frames
		loCreator = new CInternalFrameCreator(loFrame.getDesktopPane());
		// creator for modal dialogs
		loDialogCreator = new CDialogCreator(loFrame, true);
		
		// NewObjectDialog
		loNewObjectDialog = (NewObjectDialog) loCreator.createDialog(NewObjectDialog.class, "GuiNewObjectDialog",res.getString("createobj"), DialogNames.NEWOBJECT_DIALOG);
		loNewObjectDialog.setGuiManager(aoGuiMan);
		loDialogManager.registerPrototype(loNewObjectDialog, true);
		CServiceProvider.addService(loNewObjectDialog, DialogNames.NEWOBJECT_DIALOG);

		// IngestObjectDialog
		loIngestObjectDialog = (IngestObjectDialog) loCreator.createDialog(IngestObjectDialog.class, "GuiIngestObjectDialog", res.getString("file.ingest"), DialogNames.INGESTOBJECT_DIALOG);
		loIngestObjectDialog.setGuiManager(aoGuiMan);
		loDialogManager.registerPrototype(loIngestObjectDialog, true);
		CServiceProvider.addService(loIngestObjectDialog, DialogNames.INGESTOBJECT_DIALOG);

		// EditObjectDialog
		loEditObjectDialog = (EditObjectDialog) loCreator.createDialog(EditObjectDialog.class, "GuiEditObjectDialog",res.getString("file.edit"), DialogNames.EDITOBJECT_DIALOG);
		loEditObjectDialog.setGuiManager(aoGuiMan);
		loDialogManager.registerPrototype(loEditObjectDialog, true);
		CServiceProvider.addService(loEditObjectDialog, DialogNames.EDITOBJECT_DIALOG);

		// EditDCDialog
		loEditDCDialog = (EditDCDialog) loCreator.createDialog(EditDCDialog.class, "GuiEditDCDialog", res.getString("editdc"), DialogNames.EDITDC_DIALOG);
		loEditDCDialog.setGuiManager(aoGuiMan);
		loDialogManager.registerPrototype(loEditDCDialog, true);
		CServiceProvider.addService(loEditDCDialog, DialogNames.EDITDC_DIALOG);

		// ObjectEditorDialog
		loObjectEditorDialog = (ObjectEditorDialog) loCreator.createDialog(ObjectEditorDialog.class, "GuiObjectEditorDialog", res.getString("editobjsing"), DialogNames.OBJECTEDITOR_DIALOG);
		loObjectEditorDialog.setGuiManager(aoGuiMan);
		loDialogManager.registerPrototype(loObjectEditorDialog, true);
		CServiceProvider.addService(loObjectEditorDialog, DialogNames.OBJECTEDITOR_DIALOG);

		// ReplaceDialog
		loReplaceDialog = (ReplaceDialog) loCreator.createDialog(ReplaceDialog.class, "GuiReplaceDialog", res.getString("replaceobjc"), DialogNames.REPLACE_DIALOG);
		loReplaceDialog.setGuiManager(aoGuiMan);
		loDialogManager.registerPrototype(loReplaceDialog, true);
		CServiceProvider.addService(loReplaceDialog, DialogNames.REPLACE_DIALOG);


		// SelectLayoutDialog
		loSelectLayoutDialog = (SelectLayoutDialog) loDialogCreator.createDialog(SelectLayoutDialog.class, "GuiSelectLayoutDialog", res.getString("choosestyle"), DialogNames.SELECTLAYOUT_DIALOG);
		loSelectLayoutDialog.setGuiManager(aoGuiMan);
		loDialogManager.registerPrototype(loSelectLayoutDialog, true);
		CServiceProvider.addService(loSelectLayoutDialog, DialogNames.SELECTLAYOUT_DIALOG);

		// TextEditor
		loTextEditor = (TextEditor) loDialogCreator.createDialog(TextEditor.class, "GuiTextEditor", res.getString("texteditor"), DialogNames.TEXTEDITOR);
		loTextEditor.setGuiManager(aoGuiMan);
		loDialogManager.registerPrototype(loTextEditor, true);
		CServiceProvider.addService(loTextEditor, DialogNames.TEXTEDITOR);
		
		// CreateDatastreamDialog
		loCreateDatastreamDialog = (CreateDatastreamDialog) loDialogCreator.createDialog(CreateDatastreamDialog.class, "GuiCreateDatastreamDialog", res.getString("createstream"), DialogNames.CREATEDATASTREAM_DIALOG);
		loCreateDatastreamDialog.setGuiManager(aoGuiMan);
		CServiceProvider.addService(loCreateDatastreamDialog, DialogNames.CREATEDATASTREAM_DIALOG);

		// LoginDialog
		loLoginDialog = (LoginDialog) loDialogCreator.createDialog(LoginDialog.class, "GuiLoginDialog", res.getString("login"), DialogNames.LOGIN_DIALOG);
		loLoginDialog.setGuiManager(aoGuiMan);
		CServiceProvider.addService(loLoginDialog, DialogNames.LOGIN_DIALOG);

		// LoginIIPSDialog
		loLoginIIPSDialog = (LoginIIPSDialog) loDialogCreator.createDialog(LoginIIPSDialog.class, "GuiLoginIIPSDialog", res.getString("loginiips"), DialogNames.LOGINIIPS_DIALOG);
		loLoginIIPSDialog.setGuiManager(aoGuiMan);
		CServiceProvider.addService(loLoginIIPSDialog, DialogNames.LOGINIIPS_DIALOG);
		
		// OptionsDialog
		loOptionsDialog = (OptionsDialog) loDialogCreator.createDialog(OptionsDialog.class, "GuiOptionsDialog", res.getString("preferences"), DialogNames.OPTIONS_DIALOG);
		loOptionsDialog.setGuiManager(aoGuiMan);
		loDialogManager.registerPrototype(loOptionsDialog, true);
		CServiceProvider.addService(loOptionsDialog, DialogNames.OPTIONS_DIALOG);

		
		// LoginExistDialog
		loLoginExistDialog = (LoginExistDialog) loDialogCreator.createDialog(LoginExistDialog.class, "GuiLoginExistDialog", res.getString("existlogin"), DialogNames.LOGIN_EXISTDIALOG);
		loLoginExistDialog.setGuiManager(aoGuiMan);
		CServiceProvider.addService(loLoginExistDialog, DialogNames.LOGIN_EXISTDIALOG);

		// IngestExcelDialog
		loIngestExcelDialog = (IngestExcelDialog) loDialogCreator.createDialog(IngestExcelDialog.class, "GuiIngestExcelDialog", res.getString("ingestexcel"), DialogNames.INGESTEXCEL_DIALOG);
		loIngestExcelDialog.setGuiManager(aoGuiMan);
		CServiceProvider.addService(loIngestExcelDialog, DialogNames.INGESTEXCEL_DIALOG);


		// ImportDialog
		loImportDialog = (ImportDialog) loDialogCreator.createDialog(ImportDialog.class, "GuiImportDialog", res.getString("import"), DialogNames.IMPORT_DIALOG);
		loImportDialog.setGuiManager(aoGuiMan);
		CServiceProvider.addService(loImportDialog, DialogNames.IMPORT_DIALOG);

		// MakeEnvironmentDialog
		loMakeEnvironmentDialog = (MakeEnvironmentDialog) loDialogCreator.createDialog(MakeEnvironmentDialog.class, "GuiMakeEnvironmentDialog", res.getString("extras.createenvironment"), DialogNames.MAKEENVIRONMENT_DIALOG);
		loMakeEnvironmentDialog.setGuiManager(aoGuiMan);
		CServiceProvider.addService(loMakeEnvironmentDialog, DialogNames.MAKEENVIRONMENT_DIALOG);

		// LocationDialog
		loLocationDialog = (LocationDialog) loDialogCreator.createDialog(LocationDialog.class, "GuiLocationDialog", Common.WINDOW_HEADER, DialogNames.LOCATION_DIALOG);
		loLocationDialog.setGuiManager(aoGuiMan);
		CServiceProvider.addService(loLocationDialog, DialogNames.LOCATION_DIALOG);

		// HarvesterDialog
		loHarvesterDialog = (HarvesterDialog) loDialogCreator.createDialog(HarvesterDialog.class, "GuiHarvesterDialog",res.getString("extras.harvest"), DialogNames.HARVESTER_DIALOG);
		loHarvesterDialog.setGuiManager(aoGuiMan);
		CServiceProvider.addService(loHarvesterDialog, DialogNames.HARVESTER_DIALOG);

		// TemplaterDialog
		loTemplaterDialog = (TemplaterDialog) loDialogCreator.createDialog(TemplaterDialog.class, "GuiTemplaterDialog",res.getString("extras.templater"), DialogNames.TEMPLATER_DIALOG);
		loTemplaterDialog.setGuiManager(aoGuiMan);
		CServiceProvider.addService(loTemplaterDialog, DialogNames.TEMPLATER_DIALOG);

	}



	/**
	 * Description of the Method
	 *
	 * @return Description of the Return Value
	 * @exception Exception Description of the Exception
	 */
	public static CGuiManager GUI()
		throws Exception {

		CGuiFactory loFactory;
		JFrame loFrame;
		CGuiManager loGuiMan;

		loGuiMan = new CGuiManager();

		// create gui factory
		loFactory = new CGuiFactory("javax.swing");
		loFactory.addDefaultBindings();

		// build dialog gui
		loGuiMan.addGuiComposite("org.emile.cirilo.gui.GuiNewObjectDialog", "GuiNewObjectDialog");
		loGuiMan.addGuiComposite("org.emile.cirilo.gui.GuiIngestObjectDialog", "GuiIngestObjectDialog");
		loGuiMan.addGuiComposite("org.emile.cirilo.gui.GuiEditObjectDialog", "GuiEditObjectDialog");
		loGuiMan.addGuiComposite("org.emile.cirilo.gui.GuiEditDCDialog", "GuiEditDCDialog");
		loGuiMan.addGuiComposite("org.emile.cirilo.gui.GuiObjectEditorDialog", "GuiObjectEditorDialog");
		loGuiMan.addGuiComposite("org.emile.cirilo.gui.GuiReplaceDialog", "GuiReplaceDialog");
		loGuiMan.addGuiComposite("org.emile.cirilo.gui.GuiSelectLayoutDialog", "GuiSelectLayoutDialog");
		loGuiMan.addGuiComposite("org.emile.cirilo.gui.GuiLocationDialog", "GuiLocationDialog");
		loGuiMan.addGuiComposite("org.emile.cirilo.gui.GuiIngestExcelDialog", "GuiIngestExcelDialog");
		loGuiMan.addGuiComposite("org.emile.cirilo.gui.GuiTextEditor", "GuiTextEditor");
		loGuiMan.addGuiComposite("org.emile.cirilo.gui.GuiLoginDialog", "GuiLoginDialog");
		loGuiMan.addGuiComposite("org.emile.cirilo.gui.GuiLoginIIPSDialog", "GuiLoginIIPSDialog");
		loGuiMan.addGuiComposite("org.emile.cirilo.gui.GuiCreateDatastreamDialog", "GuiCreateDatastreamDialog");
		loGuiMan.addGuiComposite("org.emile.cirilo.gui.GuiLoginExistDialog", "GuiLoginExistDialog");
		loGuiMan.addGuiComposite("org.emile.cirilo.gui.GuiMakeEnvironmentDialog", "GuiMakeEnvironmentDialog");
		loGuiMan.addGuiComposite("org.emile.cirilo.gui.GuiHarvesterDialog", "GuiHarvesterDialog");
		loGuiMan.addGuiComposite("org.emile.cirilo.gui.GuiImportDialog", "GuiImportDialog");
		loGuiMan.addGuiComposite("org.emile.cirilo.gui.GuiTemplaterDialog", "GuiTemplaterDialog");
		loGuiMan.addGuiComposite("org.emile.cirilo.gui.GuiOptionsDialog", "GuiOptionsDialog");

		// build menu gui
		loGuiMan.addGuiComposite(loFactory.createGuiFromXML(Cirilo.class.getResourceAsStream("menu.xml"), true), "FrameMenu");
		loGuiMan.addWidgetTree("FrameMenu", true, true);

		// frame window
		loFrame = new CiriloFrame(Common.WINDOW_HEADER, loGuiMan);
		CServiceProvider.addService(loFrame, ServiceNames.FRAME_WINDOW);

		return loGuiMan;
	}
	private ResourceBundle res;
}

