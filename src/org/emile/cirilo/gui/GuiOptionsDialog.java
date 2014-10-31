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

package org.emile.cirilo.gui;


import java.awt.Container;
import java.awt.Dimension;
import java.util.ResourceBundle;

import voodoosoft.jroots.core.CServiceProvider;
import voodoosoft.jroots.gui.CGuiComposite; 

import javax.swing.*;

import org.emile.cirilo.ServiceNames;

/**
*  Description of the Class
*
* @author     postgres
* @created    18. November 2005
*/
public class GuiOptionsDialog extends CGuiComposite {

	
	ResourceBundle res;
	Container container;

	JButton jbSave;
	JButton jbClose;
	JCheckBox jcbTEIDCMapping;
	JCheckBox jcbTEISEMExtraction;
	JCheckBox jcbTEIRemoveEmpties;
	JCheckBox jcbTEICreateContexts;
	JCheckBox jcbTEIResolveRegex;
	JCheckBox jcbTEIResolveGeoIDs;
	JTextField jtfTEILoginName;
	JTextField jtfOAIPrefix;
	JCheckBox jcbTEIResolveSKOS;
	JCheckBox jcbTEIIngestImages;
	JCheckBox jcbTEIRefreshSource;
	JCheckBox jcbTEIOnlyGeonameID;
	JCheckBox jcbTEICustomization;
	JCheckBox jcbMETSRefreshSource;
	JCheckBox jcbMETSCreateFromJPEG;
	JComboBox jcbGeneralDefaultCM;


	/**
	 *  Constructor for the GuiObjectEditorDialog  object
	 */
	public GuiOptionsDialog () {
		super("GuiOptionsDialog ");

		try {
			jbInit();
			setRootComponent(container);
			setup();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 *  Description of the Method
	 */
	protected void setup() {

		setWidgetName(jbSave, "jbSave");
		setWidgetName(jbClose, "jbClose");
		setWidgetName(jcbTEIDCMapping, "jcbTEIDCMapping");
		setWidgetName(jcbTEISEMExtraction, "jcbTEISEMExtraction");
		setWidgetName(jcbTEIRemoveEmpties, "jcbTEIRemoveEmpties");
		setWidgetName(jcbTEICreateContexts, "jcbTEICreateContexts");
		setWidgetName(jcbTEIResolveRegex, "jcbTEIResolveRegex");
		setWidgetName(jcbTEIResolveGeoIDs, "jcbTEIResolveGeoIDs");
		setWidgetName(jtfTEILoginName, "jtfTEILoginName");
		setWidgetName(jcbTEIIngestImages, "jcbTEIIngestImages");
		setWidgetName(jcbTEIResolveSKOS, "jcbTEIResolveSKOS");
		setWidgetName(jcbTEIRefreshSource, "jcbTEIRefreshSource");
		setWidgetName(jcbMETSRefreshSource, "jcbMETSRefreshSource");
		setWidgetName(jcbGeneralDefaultCM, "jcbGeneralDefaultCM");
		setWidgetName(jcbMETSCreateFromJPEG, "jcbMETSCreateFromJPEG" );
		setWidgetName(jcbTEIOnlyGeonameID, "jcbTEIOnlyGeonameID" );
		setWidgetName(jcbTEICustomization, "jcbTEICustomization" );
		setWidgetName(jtfOAIPrefix, "jtfOAIPrefix" );
			
	}


	/**
	 *  Description of the Method
	 *
	 * @exception  Exception  Description of the Exception
	 */
	private void jbInit() throws Exception {
		
		res =(ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);

		container = new Container();
		container.setLayout(new net.miginfocom.swing.MigLayout("","[grow]",""));

		jbSave = new JButton(res.getString("apply"));
		jbClose = new JButton(res.getString("close"));
		
		jcbTEIDCMapping = new JCheckBox(res.getString("dcmapping"), true);
		jcbTEISEMExtraction = new JCheckBox(res.getString("semextraction"), true);
		jcbTEIRemoveEmpties = new JCheckBox(res.getString("removeempties"), false);
		jcbTEICreateContexts = new JCheckBox(res.getString("createcontexts"), true);
		jcbTEIResolveRegex = new JCheckBox(res.getString("resolveregex"), true);
		jcbTEIResolveGeoIDs = new JCheckBox(res.getString("resolvegeoids"), true);
		jcbTEIIngestImages = new JCheckBox(res.getString("ingestimages"), true);
		jcbTEIResolveSKOS = new JCheckBox(res.getString("resolveskos"), true);
		jcbTEIRefreshSource = new JCheckBox(res.getString("refreshsource"), false);
		jcbTEICustomization = new JCheckBox(res.getString("customization"), false);
		jcbGeneralDefaultCM = new JComboBox();
		jcbMETSRefreshSource = new JCheckBox(res.getString("refreshsource"), false);
		jcbMETSCreateFromJPEG = new JCheckBox(res.getString("createfromjpeg"), false);
		jcbTEIOnlyGeonameID = new JCheckBox(res.getString("onlygeonameids"), false);
		
		jtfTEILoginName = new JTextField();
		jtfTEILoginName.setPreferredSize(new Dimension(100, jtfTEILoginName.getPreferredSize().height));
		jtfOAIPrefix = new JTextField();
		jtfOAIPrefix.setPreferredSize(new Dimension(150, jtfOAIPrefix.getPreferredSize().height));

		JTabbedPane tp = new JTabbedPane();
		
		Container t0 = new Container();
		t0.setLayout(new net.miginfocom.swing.MigLayout("","[grow]",""));
		Box c0  = Box.createHorizontalBox();
		c0.add( new JLabel(res.getString("defaultcm")+": ") );
		c0.add( jcbGeneralDefaultCM );
		t0.add( c0, "wrap 5" );
		Box c1  = Box.createHorizontalBox();
		c1.add( new JLabel("Handle Prefix: ") );
		c1.add( jtfOAIPrefix );
		t0.add( c1, "wrap 5" );

		Container t1 = new Container();
		t1.setLayout(new net.miginfocom.swing.MigLayout("","[grow]",""));
		t1.add(jcbTEIRefreshSource, "wrap 5");
		t1.add(jcbTEIDCMapping, "wrap 5"); 
		t1.add(jcbTEISEMExtraction, "wrap 5"); 
		t1.add(jcbTEICreateContexts, "wrap 5"); 
		t1.add(jcbTEICustomization, "wrap 5"); 
		t1.add(jcbTEIResolveRegex, "wrap 5"); 
		t1.add(jcbTEIIngestImages, "wrap 5"); 
		t1.add(jcbTEIResolveSKOS, "wrap 5"); 														
		t1.add(jcbTEIResolveGeoIDs, "wrap 5");	
		t1.add(jcbTEIOnlyGeonameID, "gapbefore 18px, wrap 5");
		Box c2  = Box.createHorizontalBox();
		c2.add( new JLabel(res.getString("geonameslogin")+": "));
		c2.add( jtfTEILoginName );
		t1.add( c2, "gapbefore 18px , wrap 5" );
		t1.add(jcbTEIRemoveEmpties); 
		
		Container t2 = new Container();
		t2.setLayout(new net.miginfocom.swing.MigLayout("","[grow]",""));
		t2.add(jcbMETSRefreshSource, "wrap 5");
		t2.add( jcbMETSCreateFromJPEG, "wrap 5");
		
		tp.addTab(res.getString("general"), t0);
		tp.addTab(res.getString("teiupload"), t1);
		tp.addTab(res.getString("metsupload"), t2);
		
		container.add(tp, "grow, wrap 10");
		Box c3  = Box.createHorizontalBox();
		c3.add( jbSave );
		c3.add( new JLabel (" "));
		c3.add( jbClose );
		container.add( c3, "gapleft push, wrap 10" );
	}

}
