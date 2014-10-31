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

import org.emile.cirilo.*;

import org.emile.cirilo.ServiceNames;
import java.util.ResourceBundle;

import voodoosoft.jroots.core.CServiceProvider;
import voodoosoft.jroots.gui.CGuiComposite;
import java.awt.*;

import javax.swing.*;

import java.awt.event.*;

/**
 * Description of the Class
 * 
 * @author hstigler
 * @created 28. Jan 2009
 */
public class GuiReplaceDialog extends CGuiComposite {

	Container container;
	JTextField jtfTitle;
	JTextField jtfSubject;
	JTextField jtfDescription;
	JTextField jtfCreator;
	JTextField jtfPublisher;
	JTextField jtfContributor;
	JTextField jtfDate;
	JTextField jtfType;
	JTextField jtfFormat;
	JTextField jtfSource;
	JTextField jtfLanguage;
	JTextField jtfRelation;
	JTextField jtfCoverage;
	JTextField jtfRights;
	JTextField jtfStylesheet;
	JTextField jtfFO_Stylesheet;
	JComboBox jcbTitle;
	JComboBox jcbSubject;
	JComboBox jcbDescription;
	JComboBox jcbCreator;
	JComboBox jcbPublisher;
	JComboBox jcbContributor;
	JComboBox jcbDate;
	JComboBox jcbType;
	JComboBox jcbFormat;
	JComboBox jcbSource;
	JComboBox jcbLanguage;
	JComboBox jcbRelation;
	JComboBox jcbCoverage;
	JComboBox jcbRights;
	JComboBox jcbStylesheet;
	JComboBox jcbFO_Stylesheet;
	JComboBox jcbQueries;
	JCheckBox jcbOAIProvider;
	JCheckBox jcbDCMIMapping;
	JComboBox jcbOAI;
	JComboBox jcbRels;
	JComboBox jcbOwner;
	JComboBox jcbUser;
	JComboBox jcbDCMapping;
	JList jtRelations;
	JList jtNonRelations;
	JTextField jtfXSLStylesheet;
	JComboBox jcbXSLStylesheet;
	JComboBox jcbDatastreams;
	
	JComboBox jcbDC_MAPPING;
	JComboBox jcbRDF_MAPPING;
	JComboBox jcbBIBTEX_MAPPING;
	JComboBox jcbKML_TEMPLATE;
	JComboBox jcbREPLACEMENT_RULESET;
	JComboBox jcbTORDF;
	JComboBox jcbTOMETS;

	JTextField jtfDC_MAPPING;
	JTextField jtfRDF_MAPPING;
	JTextField jtfBIBTEX_MAPPING;
	JTextField jtfKML_TEMPLATE;
	JTextField jtfREPLACEMENT_RULESET;
	JTextField jtfTORDF;
	JTextField jtfTOMETS;
	
	JButton jbXSLStylesheet; 
	JButton jbStylesheet;
	JButton jbFO_Stylesheet;
	JButton jbAddRelation;
	JButton jbRemoveRelation;
	JTextArea jtaQueries;

	JButton jbReplace;
	JButton jbClose;
	JButton jbSeek;
    JTextField jtfSeek;

	/**
	 * Constructor for the GuiNewObjectDialog object
	 */
	public GuiReplaceDialog() {
		super( "GuiReplaceDialog" );
		try {
			jbInit();
			setRootComponent(container);
			setup();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Description of the Method
	 */
	protected void setup() {
		setWidgetName(jbReplace, "jbReplace");
		setWidgetName(jbClose, "jbClose");
		setWidgetName(jtfTitle, "jtfTitle");
		setWidgetName(jtfSubject, "jtfSubject");
		setWidgetName(jtfDescription, "jtfDescription");
		setWidgetName(jtfCreator, "jtfCreator");
		setWidgetName(jtfPublisher, "jtfPublisher");
		setWidgetName(jtfContributor, "jtfContributor");
		setWidgetName(jtfDate, "jtfDate");
		setWidgetName(jtfType, "jtfType");
		setWidgetName(jtfFormat, "jtfFormat");
		setWidgetName(jtfSource, "jtfSource");
		setWidgetName(jtfLanguage, "jtfLanguage");
		setWidgetName(jtfRelation, "jtfRelation");
		setWidgetName(jtfCoverage, "jtfCoverage");
		setWidgetName(jtfRights, "jtfRights");
		setWidgetName(jcbOAIProvider, "jcbOAIProvider");
		setWidgetName(jcbDCMapping, "jcbDCMapping");
		setWidgetName(jcbDCMIMapping, "jcbDCMIMapping");
		setWidgetName(jcbTitle, "jcbTitle");
		setWidgetName(jcbSubject, "jcbSubject");
		setWidgetName(jcbDescription, "jcbDescription");
		setWidgetName(jcbCreator, "jcbCreator");
		setWidgetName(jcbPublisher, "jcbPublisher");
		setWidgetName(jcbContributor, "jcbContributor");
		setWidgetName(jcbDate, "jcbDate");
		setWidgetName(jcbType, "jcbType");
		setWidgetName(jcbFormat, "jcbFormat");
		setWidgetName(jcbSource, "jcbSource");
		setWidgetName(jcbLanguage, "jcbLanguage");
		setWidgetName(jcbRelation, "jcbRelation");
		setWidgetName(jcbCoverage, "jcbCoverage");
		setWidgetName(jcbRights, "jcbRights");
		setWidgetName(jtfStylesheet, "jtfStylesheet");
		setWidgetName(jtfFO_Stylesheet, "jtfFO_Stylesheet");
		setWidgetName(jcbStylesheet, "jcbStylesheet");
		setWidgetName(jcbFO_Stylesheet, "jcbFO_Stylesheet");
		setWidgetName(jbStylesheet, "jbStylesheet");
		setWidgetName(jbFO_Stylesheet, "jbFO_Stylesheet");
		setWidgetName(jcbOwner, "jcbOwner");
		setWidgetName(jcbUser, "jcbUser");
		setWidgetName(jbSeek, "jbSeek");
		setWidgetName(jtfSeek, "jtfSeek");
		
		setWidgetName(jcbDC_MAPPING, "jcbDC_MAPPING");
		setWidgetName(jcbRDF_MAPPING, "jcbRDF_MAPPING");
		setWidgetName(jcbBIBTEX_MAPPING, "jcbBIBTEX_MAPPING");
		setWidgetName(jcbKML_TEMPLATE, "jcbKML_TEMPLATE");
		setWidgetName(jcbREPLACEMENT_RULESET, "jcbREPLACEMENT_RULESET");
		setWidgetName(jcbTORDF, "jcbTORDF");
		setWidgetName(jcbTOMETS, "jcbTOMETS");
		setWidgetName(jtfDC_MAPPING, "jtfDC_MAPPING");
		setWidgetName(jtfRDF_MAPPING, "jtfRDF_MAPPING");
		setWidgetName(jtfBIBTEX_MAPPING, "jtfBIBTEX_MAPPING");
		setWidgetName(jtfKML_TEMPLATE, "jtfKML_TEMPLATE");
		setWidgetName(jtfREPLACEMENT_RULESET, "jtfREPLACEMENT_RULESET");
		setWidgetName(jtfTORDF, "jtfTORDF");
		setWidgetName(jtfTOMETS, "jtfTOMETS");

		setWidgetName(jtaQueries, "jtaQueries");
		setWidgetName(jcbQueries, "jcbQueries");

		
		setWidgetName(jcbOAI, "jcbOAI");
		setWidgetName(jtRelations, "jtRelations");
		setWidgetName(jtNonRelations, "jtNonRelations");
		setWidgetName(jbAddRelation, "jbAddRelation");
		setWidgetName(jbRemoveRelation, "jbRemoveRelation");
		setWidgetName(jcbRels, "jcbRels");

		setWidgetName(jtfXSLStylesheet, "jtfXSLStylesheet");
		setWidgetName(jcbXSLStylesheet, "jcbXSLStylesheet");
		setWidgetName(jcbDatastreams, "jcbDatastreams");
		setWidgetName(jbXSLStylesheet, "jbXSLStylesheet");

	}

	/**
	 * Description of the Method
	 * 
	 * @exception Exception
	 *                Description of the Exception
	 */
	private void jbInit() throws Exception {
		ResourceBundle res=(ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);
		Object[] states = { res.getString("unmod"), res.getString("replace"), res.getString("add")};
		Object[] modes = { res.getString("unmod"), res.getString("replace") };
		Object[] cmodes = { res.getString("unmod"), res.getString("simulate"), res.getString("apply")};
		Object[] datastreams = { "BIBTEX", "DC", "METS_SOURCE", "RELS-EXT", "TEI_SOURCE" };
		
		container = new Container();
		container.setLayout(new net.miginfocom.swing.MigLayout("","[grow]",""));

		jcbOAIProvider = new JCheckBox(res.getString("checkoai"));
		jcbDCMIMapping = new JCheckBox(res.getString("dcfromtei"));

		jtfXSLStylesheet = new JTextField();
		jtfXSLStylesheet.setPreferredSize(new Dimension(1500, jtfXSLStylesheet.getPreferredSize().height));

		jcbXSLStylesheet = new JComboBox(cmodes);
		jcbXSLStylesheet.setPreferredSize(new Dimension(1500, jcbXSLStylesheet.getPreferredSize().height));
		jcbDatastreams = new JComboBox(datastreams);
		jcbDatastreams.setPreferredSize(new Dimension(1500, jcbDatastreams.getPreferredSize().height));
		jbXSLStylesheet = new JButton("...");

		jtfTitle = new JTextField();
		jcbTitle = new JComboBox(states);
		jcbTitle.setPreferredSize(new Dimension(88, jcbTitle.getPreferredSize().height));
		jtfTitle.setPreferredSize(new Dimension(1000, jtfTitle.getPreferredSize().height));

		jtfSubject = new JTextField();
		jcbSubject = new JComboBox(states);
		jcbSubject.setPreferredSize(new Dimension(88, jcbSubject.getPreferredSize().height));

		jtfDescription = new JTextField();
		jcbDescription = new JComboBox(states);
		jcbDescription.setPreferredSize(new Dimension(88, jcbDescription.getPreferredSize().height));

		jtfCreator = new JTextField();
		jcbCreator = new JComboBox(states);
		jcbCreator.setPreferredSize(new Dimension(88, jcbCreator.getPreferredSize().height));

		jtfPublisher = new JTextField();
		jcbPublisher = new JComboBox(states);
		jcbPublisher.setPreferredSize(new Dimension(88, jcbPublisher.getPreferredSize().height));

		jtfContributor = new JTextField();
		jcbContributor = new JComboBox(states);
		jcbContributor.setPreferredSize(new Dimension(88, jcbContributor.getPreferredSize().height));

		jtfDate = new JTextField();
		jcbDate = new JComboBox(states);
		jcbDate.setPreferredSize(new Dimension(88, jcbDate.getPreferredSize().height));

		jtfType = new JTextField();
		jcbType = new JComboBox(states);
		jcbType.setPreferredSize(new Dimension(88, jcbType.getPreferredSize().height));

		jtfFormat = new JTextField();
		jcbFormat = new JComboBox(states);
		jcbFormat.setPreferredSize(new Dimension(88, jcbFormat.getPreferredSize().height));

		jtfSource = new JTextField();
		jcbSource = new JComboBox(states);
		jcbSource.setPreferredSize(new Dimension(88, jcbSource.getPreferredSize().height));

		jtfLanguage = new JTextField();
		jcbLanguage = new JComboBox(states);
		jcbLanguage.setPreferredSize(new Dimension(88, jcbLanguage.getPreferredSize().height));

		jtfRelation = new JTextField();
		jcbRelation = new JComboBox(states);
		jcbRelation.setPreferredSize(new Dimension(88, jcbRelation.getPreferredSize().height));

		jtfCoverage = new JTextField();
		jcbCoverage = new JComboBox(states);
		jcbCoverage.setPreferredSize(new Dimension(88, jcbCoverage.getPreferredSize().height));

		jtfRights = new JTextField();
		jcbRights = new JComboBox(states);
		jcbRights.setPreferredSize(new Dimension(88, jcbRights.getPreferredSize().height));

		jtfStylesheet = new JTextField();
		jcbStylesheet = new JComboBox(modes);
		jbStylesheet = new JButton(new ImageIcon(Cirilo.class.getResource("seek.gif")));
		jcbStylesheet.setPreferredSize(new Dimension(88, jcbStylesheet.getPreferredSize().height));
		jtfStylesheet.setPreferredSize(new Dimension(1500, jtfStylesheet.getPreferredSize().height));

		jtfFO_Stylesheet = new JTextField();
		jcbFO_Stylesheet = new JComboBox(modes);
		jbFO_Stylesheet = new JButton(new ImageIcon(Cirilo.class.getResource("seek.gif")));
		jcbFO_Stylesheet.setPreferredSize(new Dimension(88, jcbFO_Stylesheet.getPreferredSize().height));
		jtfFO_Stylesheet.setPreferredSize(new Dimension(1500, jtfFO_Stylesheet.getPreferredSize().height));

		jtfDC_MAPPING = new JTextField();
		jcbDC_MAPPING = new JComboBox(modes);
		jcbDC_MAPPING.setPreferredSize(new Dimension(88, jcbDC_MAPPING.getPreferredSize().height));
		jtfDC_MAPPING.setPreferredSize(new Dimension(1500, jtfDC_MAPPING.getPreferredSize().height));
		
		jtfRDF_MAPPING = new JTextField();
		jcbRDF_MAPPING = new JComboBox(modes);
		jcbRDF_MAPPING.setPreferredSize(new Dimension(88, jcbRDF_MAPPING.getPreferredSize().height));
		jtfRDF_MAPPING.setPreferredSize(new Dimension(1500, jtfRDF_MAPPING.getPreferredSize().height));
	
		jtfBIBTEX_MAPPING = new JTextField();
		jcbBIBTEX_MAPPING = new JComboBox(modes);
		jcbBIBTEX_MAPPING.setPreferredSize(new Dimension(88, jcbBIBTEX_MAPPING.getPreferredSize().height));
		jtfBIBTEX_MAPPING.setPreferredSize(new Dimension(1500, jtfBIBTEX_MAPPING.getPreferredSize().height));
		
		jtfKML_TEMPLATE = new JTextField();
		jcbKML_TEMPLATE = new JComboBox(modes);
		jcbKML_TEMPLATE.setPreferredSize(new Dimension(88, jcbKML_TEMPLATE.getPreferredSize().height));
		jtfKML_TEMPLATE.setPreferredSize(new Dimension(1500, jtfKML_TEMPLATE.getPreferredSize().height));
		
		jtfREPLACEMENT_RULESET = new JTextField();
		jcbREPLACEMENT_RULESET = new JComboBox(modes);
		jcbREPLACEMENT_RULESET.setPreferredSize(new Dimension(88, jcbREPLACEMENT_RULESET.getPreferredSize().height));
		jtfREPLACEMENT_RULESET.setPreferredSize(new Dimension(1500, jtfREPLACEMENT_RULESET.getPreferredSize().height));
		
		jtfTORDF = new JTextField();
		jcbTORDF = new JComboBox(modes);
		jcbTORDF.setPreferredSize(new Dimension(88, jcbTORDF.getPreferredSize().height));
		jtfTORDF.setPreferredSize(new Dimension(1500, jtfTORDF.getPreferredSize().height));
		
		jtfTOMETS = new JTextField();
		jcbTOMETS = new JComboBox(modes);
		jcbTOMETS.setPreferredSize(new Dimension(88, jcbTOMETS.getPreferredSize().height));
		jtfTOMETS.setPreferredSize(new Dimension(1500, jtfTOMETS.getPreferredSize().height));

		
		jcbOwner = new JComboBox(modes);
		jcbUser = new JComboBox();
		jcbOwner.setPreferredSize(new Dimension(88, jcbOwner.getPreferredSize().height));
		
		jcbOAI = new JComboBox(modes);
		jcbOAI.setPreferredSize(new Dimension(88, jcbOAI.getPreferredSize().height));

		jbAddRelation = new JButton("+");

		jbRemoveRelation = new JButton(res.getString("delete"));

		jtRelations = new JList(new DefaultListModel());
		jtRelations.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		jtNonRelations = new JList(new DefaultListModel());
		jtNonRelations.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		jtaQueries = new JTextArea();
		jcbQueries = new JComboBox(modes);
		
		jcbQueries.setMinimumSize(new Dimension(88, jcbQueries.getMinimumSize().height));
		jcbQueries.setPreferredSize(new Dimension(88, jcbQueries.getPreferredSize().height));
		jcbQueries.setMaximumSize(new Dimension(89, jcbQueries.getMaximumSize().height));
		
        jtfSeek = new JTextField();  
		jbSeek = new JButton(res.getString("search"));
        jtfSeek.setPreferredSize(new Dimension(1500, jtfSeek.getPreferredSize().height));
					
		jcbRels = new JComboBox(states);

		jcbDCMapping = new JComboBox(modes);
		jcbDCMapping.setPreferredSize(new Dimension(88, jcbDCMapping.getPreferredSize().height));

		
		jbReplace = new JButton(res.getString("replace"));

		jbClose = new JButton(res.getString("close"));

		JTabbedPane tp = new JTabbedPane();
		
		Container t0 = new Container();
		t0.setLayout(new net.miginfocom.swing.MigLayout("fillx"));
		t0.add( new JLabel( "dc:Title" ) );
		t0.add(jcbTitle);
		t0.add( jtfTitle, "grow, wrap 5" );
		t0.add( new JLabel( "dc:Description" ) );
		t0.add(jcbDescription);
		t0.add( jtfDescription, "grow, wrap 5" );
		t0.add( new JLabel( "dc:Subject" ) );
		t0.add(jcbSubject);
		t0.add( jtfSubject, "grow, wrap 5" );
		t0.add( new JLabel( "dc:Creator" ) );
		t0.add(jcbCreator);
		t0.add( jtfCreator, "grow, wrap 5" );
		t0.add( new JLabel( "dc:Publisher" ) );
		t0.add(jcbPublisher);
		t0.add( jtfPublisher, "grow, wrap 5" );
		t0.add( new JLabel( "dc:Contributor" ) );
		t0.add(jcbContributor);
		t0.add( jtfContributor, "grow, wrap 5" );
		    
		t0.add( new JLabel( "dc:Language" ) );
		t0.add(jcbLanguage);
		Box d1  = Box.createHorizontalBox();
		d1.add( jtfLanguage );
		d1.add( new JLabel( "  dc:Date " ) );
		d1.add(jcbDate);
		d1.add( new JLabel( " " ) );
		d1.add( jtfDate );
		t0.add(d1,"grow, wrap 5");
			
		t0.add( new JLabel( "dc:Type" ) );
		t0.add(jcbType);
		Box d2  = Box.createHorizontalBox();
		d2.add( jtfType );
		d2.add( new JLabel( "  dc:Format " ) );
		d2.add(jcbFormat);
		d2.add( new JLabel( " " ) );
		d2.add( jtfFormat );
		t0.add(d2,"grow, wrap 5");
	    
		t0.add( new JLabel( "dc:Source" ) );
		t0.add(jcbSource);
		t0.add( jtfSource, "grow, wrap 5" );
		t0.add( new JLabel( "dc:Relation" ) );
		t0.add(jcbRelation);
		t0.add( jtfRelation, "grow, wrap 5" );
		t0.add( new JLabel( "dc:Coverage" ) );
		t0.add(jcbCoverage);
		t0.add( jtfCoverage, "grow, wrap 5" );
		t0.add( new JLabel( "dc:Rights" ) );
		t0.add(jcbRights);
		t0.add( jtfRights, "grow, wrap 5" );

		t0.add( new JLabel( "Owner" ) );
		t0.add(jcbOwner);
		t0.add( jcbUser, "grow, wrap 5" );

		t0.add( new JLabel( "" ) );
		t0.add(jcbOAI);
		t0.add( jcbOAIProvider, "grow, wrap 5" );

		t0.add( new JLabel( "" ) );
		t0.add(jcbDCMapping);
		t0.add( jcbDCMIMapping, "grow, wrap 5" );
				
		
		Container t1 = new Container();
		t1.setLayout(new net.miginfocom.swing.MigLayout("","[grow]",""));
        t1.add(new JLabel("STYLESHEET"));
		t1.add(jcbStylesheet);
		Box c1  = Box.createHorizontalBox();
		c1.add(jtfStylesheet);
		c1.add(jbStylesheet);
		t1.add( c1, "wrap 5" );
        t1.add(new JLabel("FO_STYLESHEET"));
		t1.add(jcbFO_Stylesheet);
		Box c2  = Box.createHorizontalBox();
		c2.add(jtfFO_Stylesheet);
		c2.add(jbFO_Stylesheet);
		t1.add( c2, "wrap 5" );
        t1.add(new JLabel("DC_MAPPING"));
		t1.add(jcbDC_MAPPING);
		t1.add( jtfDC_MAPPING, "wrap 5" );
        t1.add(new JLabel("RDF_MAPPING"));
		t1.add(jcbRDF_MAPPING);
		t1.add( jtfRDF_MAPPING, "wrap 5" );
        t1.add(new JLabel("BIBTEX_MAPPING"));
		t1.add(jcbBIBTEX_MAPPING);
		t1.add( jtfBIBTEX_MAPPING, "wrap 5" );
        t1.add(new JLabel("KML_TEMPLATE"));
		t1.add(jcbKML_TEMPLATE);
		t1.add( jtfKML_TEMPLATE, "wrap 5" );
        t1.add(new JLabel("REPLACEMENT_RULESET"));
		t1.add(jcbREPLACEMENT_RULESET);
		t1.add( jtfREPLACEMENT_RULESET, "wrap 5" );
        t1.add(new JLabel("TORDF"));
		t1.add(jcbTORDF);
		t1.add( jtfTORDF, "wrap 5" );
        t1.add(new JLabel("TOMETS"));
		t1.add(jcbTOMETS);
		t1.add( jtfTOMETS, "wrap 5" );

		
		Container t2 = new Container();
		t2.setLayout(new net.miginfocom.swing.MigLayout("fillx",""));
		t2.add(new JScrollPane(jtaQueries), "height 100:500:1500, growx, wrap 5");
		t2.add(jcbQueries);

		Container t3 = new Container();
		t3.setLayout(new net.miginfocom.swing.MigLayout("fillx"));
		t3.add(new JLabel(res.getString("appear")), "wrap 2");
		t3.add(new JScrollPane(jtRelations), "height 100:500:1500, growx, wrap 5");
		Box c3  = Box.createHorizontalBox();
		c3.add(jbAddRelation);
		c3.add(new JLabel(" "));
		c3.add(jbRemoveRelation);
		t3.add( c3, "gapleft push, wrap 10" );
		t3.add(new JScrollPane(jtNonRelations), "height 100:500:1500, growx, wrap 5" );
		Box c4  = Box.createHorizontalBox();
		c4.add(jtfSeek);
		c4.add(new JLabel(" "));
		c4.add(jbSeek);
		t3.add( c4, "wrap 10" );
		t3.add(jcbRels);
		
		Container t4 = new Container();
		t4.setLayout(new net.miginfocom.swing.MigLayout("fillx"));
		t4.add(new JLabel("XSLT-Stylesheet: "));
		Box c5  = Box.createHorizontalBox();
		c5.add(jtfXSLStylesheet);
		c5.add( new JLabel (" "));
		c5.add(jbXSLStylesheet);
		t4.add( c5, "wrap 5" );
		t4.add(new JLabel(res.getString("streamid")+": "));
		Box c6  = Box.createHorizontalBox();
		c6.add(jcbDatastreams);
		t4.add( c6, "wrap 5" );
		t4.add(new JLabel(" "));
		Box c7  = Box.createHorizontalBox();		
		c7.add(jcbXSLStylesheet);
		t4.add( c7, "wrap 10" );
		
			
		tp.addTab("Dublin Core", t0);
		tp.addTab(res.getString("datalocations"), t1);
		tp.addTab("Queries", t2);
		tp.addTab(res.getString("rels"), t3);
		tp.addTab(res.getString("transfs"), t4);
		
		container.add(tp, "grow, wrap 10");		
		Box c8  = Box.createHorizontalBox();
		c8.add( jbReplace );
		c8.add( new JLabel (" "));
		c8.add( jbClose );
		container.add( c8, "gapleft push, wrap 10" );
		
	}

}