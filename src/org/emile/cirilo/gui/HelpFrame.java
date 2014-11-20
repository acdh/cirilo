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

import com.digitprop.tonic.*;

import voodoosoft.jroots.core.CServiceProvider;
import voodoosoft.jroots.core.gui.CGuiTools;
import org.emile.cirilo.ServiceNames;
import java.util.ResourceBundle;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.URL;

import org.emile.cirilo.Common;

/**
 *  Description of the Class
 *
 * @author     yoda
 * @created    07. September 2006
 */
public class HelpFrame extends JWindow {
	
	JPanel panel             = new JPanel();
	JLabel logo              = new JLabel();
	JTextArea log            = new JTextArea();
	JScrollPane scrollPanel  = new JScrollPane();
	JButton jbCancel;


	/**
	 *Constructor for the HelpFrame object
	 */
	public HelpFrame() {
		
		try {
			Init();

			URL url  = SplashFrame.class.getResource("quetzal.jpg");
			logo.setOpaque(false); 
			logo.setIcon(new ImageIcon(url));

			log.setFont(new Font("SansSerif", java.awt.Font.PLAIN, 9));

			log.append("Cirilo\nAn application for data curation and content preservation\nin FEDORA based repositories\n");
			log.append("\nAuthor: Johannes H. Stigler, (c)  2011 - 2014\n");
			log.append("Documentation: Elisabeth Steiner\n");
			log.append("Version 2.4.0.1 (build 2014-11-20)\n");
			log.append("Used third-party libraries:\n");
			log.append("JRoots 1.5.0 (c) 2002-2003, by Stefan Wischnewski\n");
			log.append("This product includes software developed by the\n");
			log.append("Apache Software Foundation (http://www.apache.org/).");

			jbCancel.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});

			pack();
			CGuiTools.center(this);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 *  Description of the Method
	 */
	public void setVisible(boolean mode) {
		super.setVisible(true);
	}


	/**
	 *  Description of the Method
	 */
	public void dispose() {
		super.dispose();
	}


	/**
	 *  Description of the Method
	 *
	 * @exception  Exception  Description of the Exception
	 */
	private void Init()
		throws Exception {
		
		ResourceBundle res =(ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);
		jbCancel=new JButton(res.getString("close"));
		
		javax.swing.UIManager.setLookAndFeel(new TonicLookAndFeel());

		this.getContentPane().setLayout(new GridBagLayout());

		panel.setBorder(BorderFactory.createEtchedBorder());
		panel.setPreferredSize(new Dimension(400, 450));
		panel.setLayout(new GridBagLayout());
		log.setBorder(null);
		log.setEditable(false);

		logo.setPreferredSize(new Dimension(350, 200));
		logo.setHorizontalAlignment(SwingConstants.CENTER);

		scrollPanel.setAutoscrolls(true);
		scrollPanel.setPreferredSize(new Dimension(280, 160));

		Box box  = Box.createHorizontalBox();
		box.add(Box.createGlue());
		box.add(jbCancel);

		panel.add(logo, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
			GridBagConstraints.BOTH, new Insets(10, 10, 10, 10), 0, 0));
		panel.add(scrollPanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
			GridBagConstraints.BOTH, new Insets(10, 10, 10, 10), 0, 0));

		panel.add(box, new GridBagConstraints(0, 2, 1, 1, 1.0, 0, GridBagConstraints.CENTER,
			GridBagConstraints.HORIZONTAL, new Insets(10, 10, 10, 10), 0, 0));

		scrollPanel.getViewport().add(log, null);

		this.getContentPane().add(panel, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
			GridBagConstraints.CENTER,
			GridBagConstraints.BOTH, new Insets(9, 9, 6, 7), 0, 0));

	}

}

