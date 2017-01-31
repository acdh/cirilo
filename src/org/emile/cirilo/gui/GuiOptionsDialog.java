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
import org.apache.log4j.Logger;

/**
*  Description of the Class
*
* @author     postgres
* @created    18. November 2005
*/
public class GuiOptionsDialog extends CGuiComposite {

	private static Logger log = Logger.getLogger(GuiOptionsDialog.class);
		
	protected ResourceBundle res;
	protected Container container;

	protected JButton jbSave;
	protected JButton jbClose;
	protected JCheckBox jcbTEIDCMapping;
	protected JCheckBox jcbTEISEMExtraction;
	protected JCheckBox jcbTEIRemoveEmpties;
	protected JCheckBox jcbTEICreateContexts;
	protected JCheckBox jcbTEIResolveRegex;
	protected JCheckBox jcbTEIResolveGeoIDs;
	protected JTextField jtfTEILoginName;
	protected JTextField jtfOAIPrefix;
	protected JCheckBox jcbTEIResolveSKOS;
	protected JCheckBox jcbTEIIngestImages;
	protected JCheckBox jcbTEIRefreshSource;
	protected JCheckBox jcbTEIOnlyGeonameID;
	protected JCheckBox jcbTEICustomization;
	protected JCheckBox jcbTEItoMETS;
	protected JCheckBox jcbMETSRefreshSource;
	protected JCheckBox jcbSKOSIFY;
	protected JComboBox jcbGeneralDefaultCM;
	protected JComboBox jcbExportContext;

	protected JCheckBox jcbMEIDCMapping;
	protected JCheckBox jcbMEISEMExtraction;
	protected JCheckBox jcbMEIRefreshSource;
	protected JCheckBox jcbMEICustomization;

	
	protected JCheckBox jcbLIDODCMapping;
	protected JCheckBox jcbLIDOSEMExtraction;
	protected JCheckBox jcbLIDOCreateContexts;
	protected JCheckBox jcbLIDOResolveGeoIDs;
	protected JCheckBox jcbLIDOIngestImages;
	protected JCheckBox jcbLIDORefreshSource;
	protected JCheckBox jcbLIDOOnlyGeonameID;
	protected JCheckBox jcbLIDOResolveSKOS;


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
			log.error(e.getLocalizedMessage(),e);	
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
		setWidgetName(jcbTEIOnlyGeonameID, "jcbTEIOnlyGeonameID" );
		setWidgetName(jcbTEICustomization, "jcbTEICustomization" );
		setWidgetName(jcbTEItoMETS, "jcbTEItoMETS" );
		setWidgetName(jtfOAIPrefix, "jtfOAIPrefix" );
		setWidgetName(jcbLIDODCMapping, "jcbLIDODCMapping" );
		setWidgetName(jcbLIDOSEMExtraction, "jcbLIDOSEMExtraction" );
		setWidgetName(jcbLIDOCreateContexts, "jcbLIDOCreateContexts" );
		setWidgetName(jcbLIDOResolveGeoIDs, "jcbLIDOResolveGeoIDs" );
		setWidgetName(jcbLIDOIngestImages, "jcbLIDOIngestImages" );
		setWidgetName(jcbLIDOResolveSKOS, "jcbLIDOResolveSKOS" );
		setWidgetName(jcbLIDORefreshSource, "jcbLIDORefreshSource" );
		setWidgetName(jcbLIDOOnlyGeonameID, "jcbLIDOOnlyGeonameID" );
		setWidgetName(jcbSKOSIFY, "jcbSKOSIFY" );
		setWidgetName(jcbExportContext, "jcbExportContext" );

		setWidgetName(jcbMEIDCMapping, "jcbMEIDCMapping");
		setWidgetName(jcbMEISEMExtraction, "jcbMEISEMExtraction");
		setWidgetName(jcbMEIRefreshSource, "jcbMEIRefreshSource");
		setWidgetName(jcbMEICustomization, "jcbMEICustomization" );
		
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
		jcbTEItoMETS = new JCheckBox(res.getString("tei2mets"), false);
		jcbGeneralDefaultCM = new JComboBox();
		jcbExportContext = new JComboBox();
		jcbMETSRefreshSource = new JCheckBox(res.getString("refreshsource"), false);
		jcbTEIOnlyGeonameID = new JCheckBox(res.getString("onlygeonameids"), false);
		jcbLIDOOnlyGeonameID = new JCheckBox(res.getString("onlygeonameids"), false);

		jcbLIDODCMapping = new JCheckBox(res.getString("dcmapping"), true);
		jcbLIDOSEMExtraction = new JCheckBox(res.getString("semextraction"), true);
		jcbLIDOCreateContexts = new JCheckBox(res.getString("createcontexts"), true);
		jcbLIDOResolveGeoIDs = new JCheckBox(res.getString("resolvegeoids"), true);
		jcbLIDOIngestImages = new JCheckBox(res.getString("ingestimages"), true);
		jcbLIDORefreshSource = new JCheckBox(res.getString("refreshsource"), false);
		jcbLIDOResolveSKOS = new JCheckBox(res.getString("resolveskos"), true);
		jcbSKOSIFY = new JCheckBox(res.getString("skosify"), false);

		jcbMEIDCMapping = new JCheckBox(res.getString("dcmapping"), true);
		jcbMEISEMExtraction = new JCheckBox(res.getString("semextraction"), true);
		jcbMEIRefreshSource = new JCheckBox(res.getString("refreshsource"), false);
		jcbMEICustomization = new JCheckBox(res.getString("customization").replaceAll("TEI","MEI"), false);

		
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
		Box c2  = Box.createHorizontalBox();
		c2.add( new JLabel(res.getString("geonameslogin")+": "));
		c2.add( jtfTEILoginName );
		t0.add( c2, "wrap 5" );
		Box c3  = Box.createHorizontalBox();
		c3.add( new JLabel(res.getString("exportcontext")+": "));
		c3.add( jcbExportContext );
		t0.add( c3, "wrap 5" );

		Container t1 = new Container();
		t1.setLayout(new net.miginfocom.swing.MigLayout("","[grow]",""));
		t1.add(jcbTEIRefreshSource, "wrap 5");
		t1.add(jcbTEIDCMapping, "wrap 5"); 
		t1.add(jcbTEISEMExtraction, "wrap 5"); 
		t1.add(jcbTEICreateContexts, "wrap 5"); 
		t1.add(jcbTEICustomization, "wrap 5"); 
		t1.add(jcbTEItoMETS, "wrap 5"); 
		t1.add(jcbTEIResolveRegex, "wrap 5"); 
		t1.add(jcbTEIIngestImages, "wrap 5"); 
		t1.add(jcbTEIResolveSKOS, "wrap 5"); 														
		t1.add(jcbTEIResolveGeoIDs, "wrap 5");	
		t1.add(jcbTEIOnlyGeonameID, "gapbefore 18px, wrap 5");
		t1.add(jcbTEIRemoveEmpties); 
		
		Container t5 = new Container();
		t5.setLayout(new net.miginfocom.swing.MigLayout("","[grow]",""));
		t5.add(jcbMEIRefreshSource, "wrap 5");
		t5.add(jcbMEIDCMapping, "wrap 5"); 
		t5.add(jcbMEISEMExtraction, "wrap 5"); 
		t5.add(jcbMEICustomization, "wrap 5"); 

		Container t3 = new Container();
		t3.setLayout(new net.miginfocom.swing.MigLayout("","[grow]",""));
		t3.add(jcbLIDORefreshSource, "wrap 5");
		t3.add(jcbLIDODCMapping, "wrap 5"); 
		t3.add(jcbLIDOSEMExtraction, "wrap 5"); 
		t3.add(jcbLIDOCreateContexts, "wrap 5"); 
		t3.add(jcbLIDOIngestImages, "wrap 5"); 
		t3.add(jcbLIDOResolveSKOS, "wrap 5"); 														
		t3.add(jcbLIDOResolveGeoIDs, "wrap 5");	
		t3.add(jcbLIDOOnlyGeonameID, "gapbefore 18px, wrap 5");

		Container t2 = new Container();
		t2.setLayout(new net.miginfocom.swing.MigLayout("","[grow]",""));
		t2.add(jcbMETSRefreshSource, "wrap 5");

		Container t4 = new Container();
		t4.setLayout(new net.miginfocom.swing.MigLayout("","[grow]",""));
		t4.add(jcbSKOSIFY, "wrap 5");
		
		tp.addTab(res.getString("general"), t0);
		tp.addTab(res.getString("teiupload"), t1);
		tp.addTab(res.getString("meiupload"), t5);
		tp.addTab(res.getString("lidoupload"), t3);
		tp.addTab(res.getString("metsupload"), t2);
		tp.addTab(res.getString("skosupload"), t4);
		
		container.add(tp, "grow, wrap 10");
		Box c4  = Box.createHorizontalBox();
		c4.add( jbSave );
		c4.add( new JLabel (" "));
		c4.add( jbClose );
		container.add( c4, "gapleft push, wrap 10" );
	}

}
