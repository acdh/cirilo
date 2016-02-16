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

import voodoosoft.jroots.core.gui.CGuiTools;

import java.awt.*;

import javax.swing.*;

import org.emile.cirilo.Common;

import java.net.URL;

import org.apache.log4j.Logger;

/**
 *  Description of the Class
 *
 * @author     yoda
 * @created    07. September 2006
 */
public class SplashFrame extends JWindow {

	private static Logger log = Logger.getLogger(SplashFrame.class);
	
	private JPanel panel              = new JPanel();
	private JLabel logo               = new JLabel();
	private JTextArea jta             = new JTextArea();
	private JScrollPane scrollPanel   = new JScrollPane();
	private JProgressBar progressBar;
	private int miMaxSteps; 


	/**
	 *Constructor for the SplashFrame object
	 *
	 * @param  maxSteps  Description of the Parameter
	 */
	public SplashFrame(int maxSteps) {
		Image loIcon;

		try {
			miMaxSteps = maxSteps;
			Init();

			URL url  = SplashFrame.class.getResource("quetzal.jpg");
			logo.setOpaque(false);
			logo.setIcon(new ImageIcon(url));

			pack();
			CGuiTools.center(this);
		}
		catch (Exception e) {
			log.error(e.getLocalizedMessage(),e);	
		}
	}


	/**
	 *  Adds a feature to the Log attribute of the SplashFrame object
	 *
	 * @param  asLog  The feature to be added to the Log attribute
	 */
	public void addLog(String asLog) {
		if (progressBar.getValue() > 0) {
			jta.append("ok\n");
		} else {
			jta.append(Common.WINDOW_HEADER+"\n");
		}
		jta.append(asLog + " ... ");
		jta.setCaretPosition(jta.getDocument().getLength());
		progressBar.setValue(progressBar.getValue() + 1);
	}
	

	/**
	 *  Description of the Method
	 */
	public void dispose() {

		addLog("");
		super.dispose();

	}


	/**
	 *  Description of the Method
	 *
	 * @exception  Exception  Description of the Exception
	 */
	private void Init()
		throws Exception {

		progressBar = new JProgressBar(0, miMaxSteps);
		progressBar.setPreferredSize(new Dimension(300, 10));

		this.getContentPane().setLayout(new GridBagLayout());

		panel.setBorder(BorderFactory.createEtchedBorder());
		panel.setPreferredSize(new Dimension(400, 450));
		panel.setLayout(new GridBagLayout());
		jta.setBorder(null);
		jta.setEditable(false);

		logo.setPreferredSize(new Dimension(300, 200));
		logo.setHorizontalAlignment(SwingConstants.CENTER);

		scrollPanel.setAutoscrolls(true);
		scrollPanel.setPreferredSize(new Dimension(280, 160));

		panel.add(logo, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
			GridBagConstraints.BOTH, new Insets(10, 10, 10, 10), 0, 0));
		panel.add(scrollPanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
			GridBagConstraints.BOTH, new Insets(10, 10, 10, 10), 0, 0));
		panel.add(progressBar, new GridBagConstraints(0, 2, 1, 1, 1.0, 0, GridBagConstraints.CENTER,
			GridBagConstraints.HORIZONTAL, new Insets(10, 10, 10, 10), 0, 0));

		scrollPanel.getViewport().add(jta, null);

		this.getContentPane().add(panel, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
			GridBagConstraints.CENTER,
			GridBagConstraints.BOTH, new Insets(9, 9, 6, 7), 0, 0));
	}

}

