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

import java.awt.event.*;

/**
 *  Description of the Class
 *
 * @author     hstigler
 * @created    11.03 2011
 */
public class GuiEditDCDialog extends CGuiComposite
{

	Container container;
	JTextField jtfIdentifier;
	JTextField jtfPID;
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
	JComboBox jcbStatus;
	JComboBox jcbNamespace;
	JComboBox jcbUser;
	JCheckBox jcbOAIProvider;

	JButton jbSave;
	JButton jbClose;
	JButton jbEdit;
	JButton jbIngest;

	Object[] namespaces = { "o:", "context:", "container:"};


	/**
	 *  Constructor for the GuiNewObjectDialog object
	 */
	public GuiEditDCDialog() {
		super( "GuiEditDCDialog" );

		try {
			jbInit();
			setRootComponent( container );
			setup();
		}
		catch ( Exception e ) {
			e.printStackTrace();
		}
	}


	/**
	 *  Description of the Method
	 */
	protected void setup() {
		setWidgetName( jbSave, "jbSave" );
		setWidgetName( jbClose, "jbClose" );
		setWidgetName( jbEdit, "jbEdit" );
		setWidgetName( jcbStatus, "jcbStatus" );
		setWidgetName( jcbNamespace, "jcbNamespace" );
		setWidgetName( jcbUser, "jcbUser" );
		setWidgetName( jtfPID, "jtfPID" );
		setWidgetName( jtfTitle, "jtfTitle" );
		setWidgetName( jtfSubject, "jtfSubject" );
		setWidgetName( jtfDescription, "jtfDescription" );
		setWidgetName( jtfCreator, "jtfCreator" );
		setWidgetName( jtfPublisher, "jtfPublisher" );
		setWidgetName( jtfContributor, "jtfContributor" );
		setWidgetName( jtfDate, "jtfDate" );
		setWidgetName( jtfType, "jtfType" );
		setWidgetName( jtfFormat, "jtfFormat" );
		setWidgetName( jtfSource, "jtfSource" );
		setWidgetName( jtfLanguage, "jtfLanguage" );
		setWidgetName( jtfRelation, "jtfRelation" );
		setWidgetName( jtfCoverage, "jtfCoverage" );
		setWidgetName( jtfRights, "jtfRights" );
		setWidgetName( jcbOAIProvider, "jcbOAIProvider" );

	}


	/**
	 *  Description of the Method
	 *
	 * @exception  Exception  Description of the Exception
	 */
	private void jbInit()
		throws Exception {
			
		ResourceBundle res=(ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);
		
		container = new Container();
		container.setLayout(new net.miginfocom.swing.MigLayout("insets 10","[][grow]",""));

		jcbOAIProvider = new JCheckBox(res.getString("checkoai"));

		jtfPID = new JTextField();
		jtfPID.setBackground( new Color (238,238,238)  );
		jtfPID.setPreferredSize(new Dimension(400, jtfPID.getPreferredSize().height));
		jtfPID.setEnabled(false);

		String[] objectStatus = {res.getString("active"), res.getString("inactive")};
		jcbStatus = new JComboBox( objectStatus );
		jcbStatus.setBackground( Color.yellow );

		jcbUser = new JComboBox();
		jcbUser.setBackground( Color.YELLOW );
		jcbUser.setEnabled(false);
		
		jcbNamespace = new JComboBox(namespaces);
		jcbNamespace.setBackground( Color.YELLOW );
		jcbNamespace.setEnabled(false);

		jtfTitle = new JTextField();
		jtfTitle.setBackground( Color.YELLOW );
		jtfTitle.addKeyListener( new TextKeyListener() );

		jtfSubject = new JTextField();
		jtfSubject.addKeyListener( new TextKeyListener() );

		jtfDescription = new JTextField();
		jtfDescription.addKeyListener( new TextKeyListener() );

		jtfCreator = new JTextField();
		jtfCreator.addKeyListener( new TextKeyListener() );

		jtfPublisher = new JTextField();
		jtfPublisher.addKeyListener( new TextKeyListener() );

		jtfContributor = new JTextField();
		jtfContributor.addKeyListener( new TextKeyListener() );

		jtfDate = new JTextField();
		jtfDate.addKeyListener( new TextKeyListener() );

		jtfType = new JTextField();
		jtfType.addKeyListener( new TextKeyListener() );

		jtfFormat = new JTextField();
		jtfFormat.addKeyListener( new TextKeyListener() );

		jtfSource = new JTextField();
		jtfSource.addKeyListener( new TextKeyListener() );

		jtfLanguage = new JTextField();
		jtfLanguage.addKeyListener( new TextKeyListener() );

		jtfRelation = new JTextField();
		jtfRelation.addKeyListener( new TextKeyListener() );

		jtfCoverage = new JTextField();
		jtfCoverage.addKeyListener( new TextKeyListener() );

		jtfRights = new JTextField();
		jtfRights.addKeyListener( new TextKeyListener() );

		jbSave = new JButton( res.getString("save"));

		jbClose = new JButton( res.getString("close") );
		
		jbEdit = new JButton(res.getString("edit"));
		
		container.add( new JLabel("PID:"));
		Box c0  = Box.createHorizontalBox();
		c0.add( jcbNamespace );
		c0.add( new JLabel (" "));
		c0.add( jtfPID );
		c0.add( new JLabel ("  Owner: "));
	    c0.add( jcbUser );
        container.add(c0,"wrap 5");

        container.add( new JLabel( "dc:Title" ) );
	    container.add( jtfTitle, "grow, wrap 5" );
        container.add( new JLabel( "dc:Description" ) );
	    container.add( jtfDescription, "grow, wrap 5" );
	    container.add( new JLabel( "dc:Subject" ) );
	    container.add( jtfSubject, "grow, wrap 5" );
	    container.add( new JLabel( "dc:Creator" ) );
		container.add( jtfCreator, "grow, wrap 5" );
	    container.add( new JLabel( "dc:Publisher" ) );
		container.add( jtfPublisher, "grow, wrap 5" );
	    container.add( new JLabel( "dc:Contributor" ) );
		container.add( jtfContributor, "grow, wrap 5" );
	    
	    container.add( new JLabel( "dc:Language" ) );
		Box c1  = Box.createHorizontalBox();
		c1.add( jtfLanguage );
		c1.add( new JLabel( "  dc:Date " ) );
		c1.add( jtfDate );
        container.add(c1,"grow, wrap 5");
		
	    container.add( new JLabel( "dc:Type" ) );
		Box c2  = Box.createHorizontalBox();
		c2.add( jtfType );
		c2.add( new JLabel( "  dc:Format " ) );
		c2.add( jtfFormat );
        container.add(c2,"grow, wrap 5");
    
	    container.add( new JLabel( "dc:Source" ) );
		container.add( jtfSource, "grow, wrap 5" );
	    container.add( new JLabel( "dc:Relation" ) );
		container.add( jtfRelation, "grow, wrap 5" );
	    container.add( new JLabel( "dc:Coverage" ) );
		container.add( jtfCoverage, "grow, wrap 5" );
	    container.add( new JLabel( "dc:Rights" ) );
		container.add( jtfRights, "grow, wrap 5" );
	    container.add( new JLabel( "" ) );
		container.add( jcbOAIProvider, "wrap 5" );
		
	    container.add( new JLabel( "" ) );
		Box c3  = Box.createHorizontalBox();
		c3.add( jbSave );
		c3.add( new JLabel( " " ) );
		c3.add( jbEdit );
		c3.add( new JLabel( " " ) );
		c3.add( jbClose );
		container.add (c3, "gapleft push, wrap 10");

	
	}


	/**
	 *  Description of the Class
	 *
	 * @author     yoda
	 * @created    07. September 2006
	 */
	static class PidKeyListener implements KeyListener
	{
		/**
		 *  Description of the Method
		 *
		 * @param  e  Description of the Parameter
		 */
		public void keyTyped( KeyEvent e ) {
			char ch = e.getKeyChar();

			if ( ch >= 'A' && ch <= 'Z' ) {
				e.setKeyChar( (char) ( ch + 32 ) );
				return;
			}
			if ( ch >= 'a' && ch <= 'z' ) {
				return;
			}
			if ( ch >= '0' && ch <= '9' ) {
				return;
			}
			if ( ch == (char) 8 ) {
				return;
			}
			if ( ch == '-' ) {
				return;
			}
			if ( ch == '.' ) {
				return;
			}
			Toolkit.getDefaultToolkit().beep();
			e.consume();

		}


		/**
		 *  Description of the Method
		 *
		 * @param  e  Description of the Parameter
		 */
		public void keyPressed( KeyEvent e ) {
		}


		/**
		 *  Description of the Method
		 *
		 * @param  e  Description of the Parameter
		 */
		public void keyReleased( KeyEvent e ) {
		}
	}

	static class TextKeyListener implements KeyListener
	{
		/**
		 *  Description of the Method
		 *
		 * @param  e  Description of the Parameter
		 */
		public void keyTyped( KeyEvent e ) {
			char ch = e.getKeyChar();

			if (ch == '&' || ch == '<' || ch == '>') {
				Toolkit.getDefaultToolkit().beep();
				e.consume();
			} else {
				return;
			}

		}


		/**
		 *  Description of the Method
		 *
		 * @param  e  Description of the Parameter
		 */
		public void keyPressed( KeyEvent e ) {
		}


		/**
		 *  Description of the Method
		 *
		 * @param  e  Description of the Parameter
		 */
		public void keyReleased( KeyEvent e ) {
		}
	}

}

