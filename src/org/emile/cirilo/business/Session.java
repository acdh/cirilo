/*
 * -----------------------------------------------------------------------------
 *
 * <p><b>License and Copyright: </b>The contents of this file are subject to the
 * Educational Community License (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License
 * at <a href="http://www.opensource.org/licenses/ecl1.txt">
 * http://www.opensource.org/licenses/ecl1.txt.</a></p>
 *
 * <p>Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.</p>
 *
 * <p>The entire file consists of original code.  Copyright &copy; 2005-2008 by 
 * Department of Information Processing in the Humanities, University of Graz.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 */


package org.emile.cirilo.business;

import org.emile.cirilo.ServiceNames;
import org.emile.cirilo.dialog.CWindowsProperties;

import voodoosoft.jroots.core.CPropertyService;
import voodoosoft.jroots.core.CServiceProvider;

import java.io.*;

/**
 *  Description of the Class
 *
 * @author     hstigler
 * @created    14. Juni 2006
 */
public class Session  {

    private CWindowsProperties EditDialogProperties = new CWindowsProperties();
    private CWindowsProperties IngestDialogProperties = new CWindowsProperties();
    private CWindowsProperties LocationDialogProperties = new CWindowsProperties();
    private CWindowsProperties NewDialogProperties = new CWindowsProperties();
    private CWindowsProperties ObjectDialogProperties = new CWindowsProperties();
    private CWindowsProperties ReplaceDialogProperties = new CWindowsProperties();
    private CWindowsProperties SelectDialogProperties = new CWindowsProperties();
    private CWindowsProperties EditDCDialogProperties = new CWindowsProperties();
    private CWindowsProperties TextEditorProperties = new CWindowsProperties();
    private CWindowsProperties HarvesterDialogProperties = new CWindowsProperties();
    private CWindowsProperties TemplaterDialogProperties = new CWindowsProperties();
    private CWindowsProperties OptionsDialogProperties = new CWindowsProperties();
          
    public CWindowsProperties loadEditDialogProperties() {
    	try {
    		CPropertyService props = (CPropertyService) CServiceProvider.getService( ServiceNames.PROPERTIES );
    		EditDialogProperties.setWidth(new Integer(props.getProperty("user", "edit.dialog.width")).intValue());
    		EditDialogProperties.setHeight(new Integer(props.getProperty("user", "edit.dialog.height")).intValue());
    		EditDialogProperties.setX(new Integer(props.getProperty("user", "edit.dialog.x")).intValue());
    		EditDialogProperties.setY(new Integer(props.getProperty("user", "edit.dialog.y")).intValue());
    	} catch (Exception e) {}	
    	return EditDialogProperties; 
    }    
    public CWindowsProperties getEditDialogProperties() { return EditDialogProperties; }
    public void setEditDialogProperties(CWindowsProperties prop ) { EditDialogProperties = prop; }

    public CWindowsProperties loadIngestDialogProperties() {
    	try {
    		CPropertyService props = (CPropertyService) CServiceProvider.getService( ServiceNames.PROPERTIES );
    		IngestDialogProperties.setWidth(new Integer(props.getProperty("user", "ingest.dialog.width")).intValue());
    		IngestDialogProperties.setHeight(new Integer(props.getProperty("user", "ingest.dialog.height")).intValue());
    		IngestDialogProperties.setX(new Integer(props.getProperty("user", "ingest.dialog.x")).intValue());
    		IngestDialogProperties.setY(new Integer(props.getProperty("user", "ingest.dialog.y")).intValue());
    	} catch (Exception e) {e.printStackTrace();}	
    	return IngestDialogProperties; 
    }
    public CWindowsProperties getIngestDialogProperties() { return IngestDialogProperties; }
    public void setIngestDialogProperties(CWindowsProperties prop ) { IngestDialogProperties = prop; }

    public CWindowsProperties loadLocationDialogProperties() {
    	try {
    		CPropertyService props = (CPropertyService) CServiceProvider.getService( ServiceNames.PROPERTIES );
    		LocationDialogProperties.setWidth(new Integer(props.getProperty("user", "location.dialog.width")).intValue());
    		LocationDialogProperties.setHeight(new Integer(props.getProperty("user", "location.dialog.height")).intValue());
    		LocationDialogProperties.setX(new Integer(props.getProperty("user", "location.dialog.x")).intValue());
    		LocationDialogProperties.setY(new Integer(props.getProperty("user", "location.dialog.y")).intValue());
    	} catch (Exception e) {e.printStackTrace();}	
    	return LocationDialogProperties; 
    }
    public CWindowsProperties getLocationDialogProperties() { return LocationDialogProperties; }
    public void setLocationDialogProperties(CWindowsProperties prop ) { LocationDialogProperties = prop; }

    public CWindowsProperties loadNewDialogProperties() {
    	try {
    		CPropertyService props = (CPropertyService) CServiceProvider.getService( ServiceNames.PROPERTIES );
    		NewDialogProperties.setWidth(new Integer(props.getProperty("user", "new.dialog.width")).intValue());
    		NewDialogProperties.setHeight(new Integer(props.getProperty("user", "new.dialog.height")).intValue());
    		NewDialogProperties.setX(new Integer(props.getProperty("user", "new.dialog.x")).intValue());
    		NewDialogProperties.setY(new Integer(props.getProperty("user", "new.dialog.y")).intValue());
    	} catch (Exception e) {e.printStackTrace();}	
    	return NewDialogProperties; 
    }
    public CWindowsProperties getNewDialogProperties() { return NewDialogProperties; }
    public void setNewDialogProperties(CWindowsProperties prop ) { NewDialogProperties = prop; }

    public CWindowsProperties loadObjectDialogProperties() {
    	try {
    		CPropertyService props = (CPropertyService) CServiceProvider.getService( ServiceNames.PROPERTIES );
    		ObjectDialogProperties.setWidth(new Integer(props.getProperty("user", "object.dialog.width")).intValue());
    		ObjectDialogProperties.setHeight(new Integer(props.getProperty("user", "object.dialog.height")).intValue());
    		ObjectDialogProperties.setX(new Integer(props.getProperty("user", "object.dialog.x")).intValue());
    		ObjectDialogProperties.setY(new Integer(props.getProperty("user", "object.dialog.y")).intValue());
    	} catch (Exception e) {e.printStackTrace();}	
    	return ObjectDialogProperties; 
    }
    public CWindowsProperties getObjectDialogProperties() { return ObjectDialogProperties; }
    public void setObjectDialogProperties(CWindowsProperties prop ) { ObjectDialogProperties = prop; }
    
    public CWindowsProperties loadReplaceDialogProperties() {
    	try {
    		CPropertyService props = (CPropertyService) CServiceProvider.getService( ServiceNames.PROPERTIES );
    		ReplaceDialogProperties.setWidth(new Integer(props.getProperty("user", "replace.dialog.width")).intValue());
    		ReplaceDialogProperties.setHeight(new Integer(props.getProperty("user", "replace.dialog.height")).intValue());
    		ReplaceDialogProperties.setX(new Integer(props.getProperty("user", "replace.dialog.x")).intValue());
    		ReplaceDialogProperties.setY(new Integer(props.getProperty("user", "replace.dialog.y")).intValue());
    	} catch (Exception e) {e.printStackTrace();}	
    	return ReplaceDialogProperties; 
    }
    public CWindowsProperties getReplaceDialogProperties() { return ReplaceDialogProperties; }
    public void setReplaceDialogProperties(CWindowsProperties prop ) { ReplaceDialogProperties = prop; }

  
    public CWindowsProperties loadSelectDialogProperties() {
    	try {
    		CPropertyService props = (CPropertyService) CServiceProvider.getService( ServiceNames.PROPERTIES );
    		SelectDialogProperties.setWidth(new Integer(props.getProperty("user", "select.dialog.width")).intValue());
    		SelectDialogProperties.setHeight(new Integer(props.getProperty("user", "select.dialog.height")).intValue());
    		SelectDialogProperties.setX(new Integer(props.getProperty("user", "select.dialog.x")).intValue());
    		SelectDialogProperties.setY(new Integer(props.getProperty("user", "select.dialog.y")).intValue());
    	} catch (Exception e) {e.printStackTrace();}	
    	return SelectDialogProperties; 
    }
    public CWindowsProperties getSelectDialogProperties() { return SelectDialogProperties; }
    public void setSelectDialogProperties(CWindowsProperties prop ) { SelectDialogProperties = prop; }

    public CWindowsProperties loadEditDCDialogProperties() {
    	try {
    		CPropertyService props = (CPropertyService) CServiceProvider.getService( ServiceNames.PROPERTIES );
    		EditDCDialogProperties.setWidth(new Integer(props.getProperty("user", "dc.dialog.width")).intValue());
    		EditDCDialogProperties.setHeight(new Integer(props.getProperty("user", "dc.dialog.height")).intValue());
    		EditDCDialogProperties.setX(new Integer(props.getProperty("user", "dc.dialog.x")).intValue());
    		EditDCDialogProperties.setY(new Integer(props.getProperty("user", "dc.dialog.y")).intValue());
    	} catch (Exception e) {e.printStackTrace();}	
    	return EditDCDialogProperties; 
    }
    public CWindowsProperties getEditDCDialogProperties() { return EditDCDialogProperties; }
    public void setEditDCDialogProperties(CWindowsProperties prop ) { EditDCDialogProperties = prop; }

    public CWindowsProperties loadTextEditorProperties() {
    	try {
    		CPropertyService props = (CPropertyService) CServiceProvider.getService( ServiceNames.PROPERTIES );
    		TextEditorProperties.setWidth(new Integer(props.getProperty("user", "editor.dialog.width")).intValue());
    		TextEditorProperties.setHeight(new Integer(props.getProperty("user", "editor.dialog.height")).intValue());
    		TextEditorProperties.setX(new Integer(props.getProperty("user", "editor.dialog.x")).intValue());
    		TextEditorProperties.setY(new Integer(props.getProperty("user", "editor.dialog.y")).intValue());
    	} catch (Exception e) {e.printStackTrace();}	
    	return TextEditorProperties; 
    }
    public CWindowsProperties getTextEditorProperties() { return TextEditorProperties; }
    public void setTextEditorProperties(CWindowsProperties prop ) { TextEditorProperties = prop; }

    public CWindowsProperties loadHarvesterDialogProperties() {
    	try {
    		CPropertyService props = (CPropertyService) CServiceProvider.getService( ServiceNames.PROPERTIES );
    		HarvesterDialogProperties.setWidth(new Integer(props.getProperty("user", "harvester.dialog.width")).intValue());
    		HarvesterDialogProperties.setHeight(new Integer(props.getProperty("user", "harvester.dialog.height")).intValue());
    		HarvesterDialogProperties.setX(new Integer(props.getProperty("user", "harvester.dialog.x")).intValue());
    		HarvesterDialogProperties.setY(new Integer(props.getProperty("user", "harvester.dialog.y")).intValue());
    	} catch (Exception e) {e.printStackTrace();}	
    	return HarvesterDialogProperties; 
    }
    public CWindowsProperties getHarvesterDialogProperties() { return HarvesterDialogProperties; }
    public void setHarvesterDialogProperties(CWindowsProperties prop ) { HarvesterDialogProperties = prop; }

    public CWindowsProperties loadTemplaterDialogProperties() {
    	try {
    		CPropertyService props = (CPropertyService) CServiceProvider.getService( ServiceNames.PROPERTIES );
    		TemplaterDialogProperties.setWidth(new Integer(props.getProperty("user", "templater.dialog.width")).intValue());
    		TemplaterDialogProperties.setHeight(new Integer(props.getProperty("user", "templater.dialog.height")).intValue());
    		TemplaterDialogProperties.setX(new Integer(props.getProperty("user", "templater.dialog.x")).intValue());
    		TemplaterDialogProperties.setY(new Integer(props.getProperty("user", "templater.dialog.y")).intValue());
    	} catch (Exception e) {e.printStackTrace();}	
    	return TemplaterDialogProperties; 
    }
    public CWindowsProperties getTemplaterDialogProperties() { return TemplaterDialogProperties; }
    public void setTemplaterDialogProperties(CWindowsProperties prop ) { TemplaterDialogProperties = prop; }

    public CWindowsProperties loadOptionsDialogProperties() {
    	try {
    		CPropertyService props = (CPropertyService) CServiceProvider.getService( ServiceNames.PROPERTIES );
    		OptionsDialogProperties.setWidth(new Integer(props.getProperty("user", "options.dialog.width")).intValue());
    		OptionsDialogProperties.setHeight(new Integer(props.getProperty("user", "options.dialog.height")).intValue());
    		OptionsDialogProperties.setX(new Integer(props.getProperty("user", "options.dialog.x")).intValue());
    		OptionsDialogProperties.setY(new Integer(props.getProperty("user", "options.dialog.y")).intValue());
    	} catch (Exception e) {e.printStackTrace();}	
    	return OptionsDialogProperties; 
    }
    public CWindowsProperties getOptionsDialogProperties() { return OptionsDialogProperties; }
    public void setOptionsDialogProperties(CWindowsProperties prop ) { OptionsDialogProperties = prop; }

}

