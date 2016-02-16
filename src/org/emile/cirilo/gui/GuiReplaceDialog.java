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

import java.util.ResourceBundle;

import voodoosoft.jroots.core.CServiceProvider;
import voodoosoft.jroots.gui.CGuiComposite;

import java.awt.*;

import javax.swing.*;

import org.apache.log4j.Logger;

/**
 * Description of the Class
 * 
 * @author hstigler
 * @created 28. Jan 2009
 */
public class GuiReplaceDialog extends CGuiComposite {

	private static Logger log = Logger.getLogger(GuiReplaceDialog.class);
	
	protected Container container;
	protected JTextField jtfTitle;
	protected JTextField jtfSubject;
	protected JTextField jtfDescription;
	protected JTextField jtfCreator;
	protected JTextField jtfPublisher;
	protected JTextField jtfContributor;
	protected JTextField jtfDate;
	protected JTextField jtfType;
	protected JTextField jtfFormat;
	protected JTextField jtfSource;
	protected JTextField jtfLanguage;
	protected JTextField jtfRelation;
	protected JTextField jtfCoverage;
	protected JTextField jtfRights;
	protected JTextField jtfReferences;
	protected JComboBox jcbTitle;
	protected JComboBox jcbSubject;
	protected JComboBox jcbDescription;
	protected JComboBox jcbCreator;
	protected JComboBox jcbPublisher;
	protected JComboBox jcbContributor;
	protected JComboBox jcbDate;
	protected JComboBox jcbType;
	protected JComboBox jcbFormat;
	protected JComboBox jcbSource;
	protected JComboBox jcbLanguage;
	protected JComboBox jcbRelation;
	protected JComboBox jcbCoverage;
	protected JComboBox jcbRights;
	protected JComboBox jcbQueries;
	protected JCheckBox jcbOAIProvider;
	protected JCheckBox jcbDCMIMapping;
	protected JComboBox jcbOAI;
	protected JComboBox jcbRels;
	protected JComboBox jcbOwner;
	protected JComboBox jcbUser;
	protected JComboBox jcbDCMapping;
	protected JComboBox jcbReferences;
	protected JComboBox jcbMReferences;

	
	protected JList jtRelations;
	protected JList jtNonRelations;
	protected JTextField jtfXSLStylesheet;
	protected JComboBox jcbXSLStylesheet;
	protected JComboBox jcbDatastreams;
	
	
	protected JButton jbXSLStylesheet; 
	protected JButton jbAddRelation;
	protected JButton jbRemoveRelation;
	protected JTextArea jtaQueries;
	protected JButton jbReferences;

	protected JButton jbReplace;
	protected JButton jbClose;
	protected JButton jbSeek;
	protected JTextField jtfSeek;

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
			log.error(e.getLocalizedMessage(),e);	
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
		setWidgetName(jtfReferences, "jtfReferences");
		setWidgetName(jcbReferences, "jcbReferences");
		setWidgetName(jcbMReferences, "jcbMReferences");
		setWidgetName(jbReferences, "jbReferences");

		setWidgetName(jcbOwner, "jcbOwner");
		setWidgetName(jcbUser, "jcbUser");
		setWidgetName(jbSeek, "jbSeek");
		setWidgetName(jtfSeek, "jtfSeek");
		
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
		Object[] references = { "STYLESHEET", "FO_STYLESHEET", "HSSF_STYLESHEET", "DC_MAPPING", "RDF_MAPPING", "BIBTEX_MAPPING", "KML_TEMPLATE", "REPLACEMENT_RULESET", "TORDF", "TOMETS"};
		
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

		jtfReferences = new JTextField();
		jtfReferences.setPreferredSize(new Dimension(900, jtfReferences.getPreferredSize().height));
		jcbMReferences = new JComboBox(states);
		jcbReferences = new JComboBox(references);
		jbReferences = new JButton(new ImageIcon(Cirilo.class.getResource("seek.gif")));
		jcbMReferences.setPreferredSize(new Dimension(88, jcbMReferences.getPreferredSize().height));

		
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
//		t1.setLayout(new net.miginfocom.swing.MigLayout("fillx"));
		t1.setLayout(new net.miginfocom.swing.MigLayout("","[][][grow][]",""));
        t1.add(jcbReferences);
		t1.add(jcbMReferences);
	    t1.add(jtfReferences);
		t1.add(jbReferences);

		
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