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

import voodoosoft.jroots.core.CServiceProvider;
import voodoosoft.jroots.gui.CGuiComposite; 

import java.awt.*;

import org.emile.cirilo.ServiceNames;

import java.util.ResourceBundle;

import javax.swing.*;
import javax.swing.border.*;

import org.apache.log4j.Logger;

/**
 *  Description of the Class
 *
 * @author     postgres
 * @created    18. November 2005
 */
public class GuiObjectEditorDialog extends CGuiComposite {

	
	private static Logger log = Logger.getLogger(GuiObjectEditorDialog.class);
	
	protected Container container;

	protected JTabbedPane tpPane;
	
	protected JTable jtDatastreams;
	protected JTable jtMetadata;
	protected JList jtRelations;
	protected JList jtNonRelations;
	protected JButton jbClose;
	protected JButton jbSave;
	protected JButton jbAddRelation;
	protected JButton jbRemoveRelation;
	protected JButton jbSaveRelations;
	protected JTextField jtfIdentifier;
	protected JComboBox jcbState;
	protected JTextField jtfLabel;
	protected JComboBox jcbUser;
	protected JButton jboUpload;
	protected JButton jbmUpload;
	protected JButton jboDownload;
	protected JButton jbmDownload;
	protected JButton jbNew;
	protected JButton jbDel;
	protected JButton jboEdit;
	protected JButton jbmEdit;
	protected JButton jbSeek;
	protected JTextField jtfSeek;

	/**
	 *  Constructor for the GuiObjectEditorDialog  object
	 */
	public GuiObjectEditorDialog () {
		super("GuiObjectEditorDialog ");

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

		setWidgetName(jtDatastreams, "jtDatastreams");
		setWidgetName(jtMetadata, "jtMetadata");
		setWidgetName(jtRelations, "jtRelations");
		setWidgetName(jtNonRelations, "jtNonRelations");
		setWidgetName(jbClose, "jbClose");
		setWidgetName(jtfIdentifier, "jtfIdentifier");
		setWidgetName(jtfLabel, "jtfLabel");
		setWidgetName(jcbUser, "jcbUser");
		setWidgetName(jcbState, "jcbState");
		setWidgetName(jbSave, "jbSave");
		setWidgetName(jbAddRelation, "jbAddRelation");
		setWidgetName(jbRemoveRelation, "jbRemoveRelation");
		setWidgetName(jbSaveRelations, "jbSaveRelations");
		setWidgetName(jboUpload, "jboUpload");
		setWidgetName(jbmUpload, "jbmUpload");
		setWidgetName(jboDownload, "jboDownload");
		setWidgetName(jbmDownload, "jbmDownload");
		setWidgetName(jboEdit, "jboEdit");
		setWidgetName(jbmEdit, "jbmEdit");
		setWidgetName(jbNew, "jbNew");
		setWidgetName(jbDel, "jbDel");
		setWidgetName(jbSeek, "jbSeek");
		setWidgetName(jtfSeek, "jtfSeek");
		setWidgetName(jtfSeek, "jtfSeek");
		setWidgetName(tpPane, "tpPane");
		
	}


	/**
	 *  Description of the Method
	 *
	 * @exception  Exception  Description of the Exception
	 */
	private void jbInit() throws Exception {
		ResourceBundle res=(ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);
		
		container = new Container();
		container.setLayout(new net.miginfocom.swing.MigLayout("fill"));

		String[] states = new String[] {res.getString("active"), res.getString("inactive")}; 
		String[] groups = new String[] {"Internal XML Metadata", "Managed Content", "External Referenced Content", "Redirect"};

	    String[] MIMETYPES = new String[] {"text/xml", "text/plain", "text/html",
                    "text/html+xml", "text/svg+xml", "text/rtf", "image/jpeg",
                    "image/gif", "image/bmp", "image/png",
                    "image/tiff", "audio/mpeg", "audio/x-aiff", "audio/x-wav",
                    "audio/x-pn-realaudio", "video/mpeg", "video/quicktime",
                    "application/postscript", "application/pdf",
                    "application/rdf+xml", "application/ms-word",
                    "application/ms-excel", "application/ms-powerpoint",
                    "application/smil", "application/octet-stream",
                    "application/x-tar", "application/zip",
                    "application/xhtml+xml",
                    "application/xslt+xml", "application/xml-dtd"};
	
	    tpPane = new JTabbedPane();
	    
		jtDatastreams = new JTable();

		jtMetadata = new JTable();
		
		jtRelations = new JList(new DefaultListModel());
		jtRelations.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		jtNonRelations = new JList(new DefaultListModel());
		jtNonRelations.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		jbAddRelation = new JButton("+");
		jbRemoveRelation = new JButton(res.getString("delete"));
		
		jbSaveRelations = new JButton(res.getString("apply"));

		jbSave = new JButton(res.getString("apply"));
		jbClose = new JButton(res.getString("close"));
		jbmEdit = new JButton(res.getString("edit"));
		jboEdit = new JButton(res.getString("edit"));

		jbNew = new JButton(res.getString("new"));
		jbDel = new JButton(res.getString("delete"));

        jtfSeek = new JTextField();  
		jbSeek = new JButton(res.getString("search"));
        jtfSeek.setPreferredSize(new Dimension(1500, jtfSeek.getPreferredSize().height));
		
		jtfIdentifier = new JTextField();
		jtfIdentifier.setBackground(Color.yellow);
		jtfIdentifier.setPreferredSize(new Dimension(200, jtfIdentifier.getPreferredSize().height));

		jtfLabel = new JTextField();
		jtfLabel.setBackground(Color.yellow);
		
		jcbUser = new JComboBox();
		jcbUser.setBackground( Color.YELLOW );
		
		jcbState = new JComboBox(states);
		jcbState.setBackground(Color.yellow);
						

		jboUpload = new JButton(res.getString("add"));
		jbmUpload = new JButton(res.getString("add"));
		jboDownload = new JButton(res.getString("saveas"));
		jbmDownload = new JButton(res.getString("saveas"));

		jbClose = new JButton(res.getString("close"));
			
		Container t0 = new Container();
		t0.setLayout(new net.miginfocom.swing.MigLayout());
		Box c0  = Box.createHorizontalBox();
		c0.add(new JLabel("PID: "));
		c0.add(jtfIdentifier);
//		c0.add(new JLabel(" "));
//		c0.add(jcbState);
		c0.add(new JLabel("  Owner: "));
		c0.add(jcbUser);					
		c0.add(new JLabel(" "));
		c0.add(jbSave);
		t0.add( c0, "wrap 10" );


		Container t1 = new Container();
		t1.setLayout(new net.miginfocom.swing.MigLayout("fillx"));
		t1.add(new JScrollPane(jtMetadata), "growx, wrap 5");
		Box c1  = Box.createHorizontalBox();
		c1.add(jbmUpload);
		c1.add(new JLabel(" "));
		c1.add(jbmEdit);
		c1.add(new JLabel(" "));
		c1.add(jbmDownload);
		t1.add( c1, "wrap 10" );
		
		Container t2 = new Container();
		t2.setLayout(new net.miginfocom.swing.MigLayout("fillx"));
		t2.add(new JScrollPane(jtDatastreams), "growx, wrap 5");
		Box c2  = Box.createHorizontalBox();
		c2.add(jboUpload);
		c2.add(new JLabel(" "));
		c2.add(jboEdit);
		c2.add(new JLabel(" "));
		c2.add(jbNew);
		c2.add(new JLabel(" "));
		c2.add(jbDel);
		c2.add(new JLabel(" "));
		c2.add(jboDownload);
		t2.add( c2, "wrap 10" );

		Container t3 = new Container();
		t3.setLayout(new net.miginfocom.swing.MigLayout("fillx"));
		t3.add(new JLabel(res.getString("appear")), "wrap 2");
		t3.add(new JScrollPane(jtRelations), "height 100:500:1000, growx, wrap 5");
		Box c3  = Box.createHorizontalBox();
		c3.add(jbAddRelation);
		c3.add(new JLabel(" "));
		c3.add(jbRemoveRelation);
		t3.add( c3, "gapleft push, wrap 10" );
		t3.add(new JScrollPane(jtNonRelations), "height 100:500:1000, growx, wrap 5" );
		Box c4  = Box.createHorizontalBox();
		c4.add(jtfSeek);
		c4.add(new JLabel(" "));
		c4.add(jbSeek);
		c4.add(new JLabel(" "));
		c4.add(jbSaveRelations);
		t3.add( c4, "wrap 10" );
		
		tpPane.addTab(res.getString("prop"), t0);
		tpPane.addTab(res.getString("sysdata"), t1);
		tpPane.addTab(res.getString("streams"), t2);
		tpPane.addTab(res.getString("rels"), t3);
		
		container.add(tpPane, "grow, wrap 10");		
		container.add( jbClose, "gapleft push, wrap 10" );

	}

}

