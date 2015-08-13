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
import javax.swing.*;



import org.emile.cirilo.ServiceNames;
import java.util.ResourceBundle;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import net.miginfocom.swing.*;

/**
 *  Description of the Class
 *
 * @author     yoda
 * @created    07. September 2006
 */
public class GuiCreateDatastreamDialog extends CGuiComposite {

	
	Container container;
		
	/**
	 *  Description of the Field
	 */
	protected JLabel id;
	/**
	 *  Description of the Field
	 */
	protected JTextField jtfID;
	/**
	 *  Description of the Field
	 */
	protected JLabel label;
	/**
	 *  Description of the Field
	 */
	protected JTextField jtfLabel;
	/**
	 *  Description of the Field
	 */
	protected JLabel versionable;
	/**
	 *  Description of the Field
	 */
	protected JComboBox jcbVersionable;
	/**
	 *  Description of the Field
	 */
	protected JLabel mimetype;
	/**
	 *  Description of the Field
	 */
	protected JComboBox jcbMimetype;
	/**
	 *  Description of the Field
	 */
	/**
	 *  Description of the Field
	 */
	protected JButton jbCreate;
	/**
	 *  Description of the Field
	 */
	protected JButton jbCancel;


	/**
	 *Constructor for the GuiLoginDialog object
	 */
	public GuiCreateDatastreamDialog() {
		super("GuiCreateDatastreamDialog");

		try {
			
			jbInit();						
			setRootComponent(container);
			setup();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 *  Description of the Method
	 */
	protected void setup() {
		jbCreate.setDefaultCapable(true);
		jbCancel.setDefaultCapable(false);

		setWidgetName(jbCreate, "jbCreate");
		setWidgetName(jbCancel, "jbCancel");
		setWidgetName(jcbVersionable, "jcbVersionable");
		setWidgetName(jtfID, "jtfID");
		setWidgetName(jtfLabel, "jtfLabel");
		setWidgetName(jcbMimetype, "jcbMimetype");
	}


	/**
	 *  Description of the Method
	 *
	 * @exception  Exception  Description of the Exception
	 */
	private void jbInit()
		throws Exception {
		
		ResourceBundle res=(ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);
		
		Object[] versions = { res.getString("createversion"), res.getString("replaceversion")};

		container = new Container(); 
		container.setLayout(new net.miginfocom.swing.MigLayout());
		

		Object[] mimetypes = { 
				"text/xml",
				"application/javascript",
				"application/mp4",
				"application/msword",
				"application/pdf",
				"application/sparql-query",
				"application/vnd.ms-excel",
				"application/vnd.ms-powerpoint",
				"application/vnd.ms-powerpoint",
				"application/vnd.oais.opendocument.text",
				"application/vnd.oais.opendocument.presentation",
				"application/vnd.oais.opendocument.spreadsheet",
				"application/vnd.sun.xml.calc",
				"application/vnd.sun.xml.impress",
				"application/vnd.sun.xml.writer",
				"application/xhtml+xml",
				"application/x-shockwave-flash",
				"audio/mp3",
				"audio/x-wave",
				"image/tiff",
				"image/jpeg",
				"text/css",
				"text/html",
				"text/plain",
				"video/mpeg",
				"video/quicktime"								
		};
		
		id = new JLabel(res.getString("id")+": ");
		id.setHorizontalTextPosition(SwingConstants.LEADING);

		label = new JLabel(res.getString("label")+": ");
		label.setHorizontalTextPosition(SwingConstants.LEADING);

		versionable = new JLabel(res.getString("versionable")+": ");
		versionable.setHorizontalTextPosition(SwingConstants.LEADING);

		jtfID = new JTextField();
		jtfID.setPreferredSize(new Dimension(200, jtfID.getPreferredSize().height));
		jtfID.addKeyListener( new IDKeyListener() );

		jtfLabel = new JTextField();
		jtfLabel.setPreferredSize(new Dimension(200, jtfLabel.getPreferredSize().height));
		
		jcbVersionable = new JComboBox(versions); 
		jcbVersionable.setPreferredSize(new Dimension(200, jcbVersionable.getPreferredSize().height));
 
		mimetype = new JLabel(res.getString("mimetype")+": ");
		mimetype.setHorizontalTextPosition(SwingConstants.LEADING);

		jcbMimetype = new JComboBox(mimetypes); 
		jcbMimetype.setPreferredSize(new Dimension(200, jcbMimetype.getPreferredSize().height));

		jbCreate = new JButton(res.getString("create"));

		jbCancel = new JButton(res.getString("cancel"));

		container.add(id);
		container.add(jtfID, "span, grow");	
		container.add(label);
		container.add(jtfLabel, "span, grow");	
		container.add(versionable);
		container.add(jcbVersionable, "span, grow");				
		container.add(mimetype);
		container.add(jcbMimetype , "wrap, grow");
		container.add(jbCreate);	
		container.add(jbCancel);	
		
	}
	
	static class IDKeyListener implements KeyListener
	{
		/**
		 *  Description of the Method
		 *
		 * @param  e  Description of the Parameter
		 */
		public void keyTyped( KeyEvent e ) {
			char ch = e.getKeyChar();

			if ( ch >= 'A' && ch <= 'Z' ) {
				return;
			}
			if ( ch >= 'a' && ch <= 'z' ) {
				e.setKeyChar( (char) ( ch - 32 ) );
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
			if ( ch == '_' ) {
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

}

