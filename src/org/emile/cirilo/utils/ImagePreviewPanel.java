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

package org.emile.cirilo.utils;

import javax.swing.*;
import javax.swing.filechooser.*;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.File;

/**
 *  Description of the Class
 *
 * @author     yoda
 * @created    21. Oktober 2008
 */
public class ImagePreviewPanel extends JPanel
		 implements PropertyChangeListener {

	static final long serialVersionUID = 0L;
	private int width, height;
	private ImageIcon icon;
	private Image image;
	private final static int ACCSIZE = 155;
	private Color bg;


	/**
	 *Constructor for the ImagePreviewPanel object
	 */
	public ImagePreviewPanel() {
		setPreferredSize(new Dimension(ACCSIZE, -1));
		bg = getBackground();
	}


	/**
	 *  Description of the Method
	 *
	 * @param  e  Description of the Parameter
	 */
	public void propertyChange(PropertyChangeEvent e) {
		String propertyName = e.getPropertyName();

		// Make sure we are responding to the right event.
		if (propertyName.equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)) {
			File selection = (File) e.getNewValue();
			String name;

			if (selection == null) {
				return;
			} else {
				name = selection.getAbsolutePath();
			}

			/*
			 *  Make reasonably sure we have an image format that AWT can
			 *  handle so we don't try to draw something silly.
			 */
			if ((name != null) && name.toLowerCase().endsWith(".jpg") ||
				name.toLowerCase().endsWith(".jpeg") || name.toLowerCase().endsWith(".gif") ||
				name.toLowerCase().endsWith(".png")) {
				icon = new ImageIcon(name);
				image = icon.getImage();
				scaleImage();
				repaint();
			}
		}
	}


	/**
	 *  Description of the Method
	 */
	private void scaleImage() {
		width = image.getWidth(this);
		height = image.getHeight(this);
		double ratio = 1.0;

		/*
		 *  Determine how to scale the image. Since the accessory can expand
		 *  vertically make sure we don't go larger than 150 when scaling
		 *  vertically.
		 */
		if (width >= height) {
			ratio = (double) (ACCSIZE - 5) / width;
			width = ACCSIZE - 5;
			height = (int) (height * ratio);
		} else {
			if (getHeight() > 150) {
				ratio = (double) (ACCSIZE - 5) / height;
				height = ACCSIZE - 5;
				width = (int) (width * ratio);
			} else {
				ratio = (double) getHeight() / height;
				height = getHeight();
				width = (int) (width * ratio);
			}
		}

		image = image.getScaledInstance(width, height, Image.SCALE_DEFAULT);
	}


	/**
	 *  Description of the Method
	 *
	 * @param  g  Description of the Parameter
	 */
	public void paintComponent(Graphics g) {
		g.setColor(bg);

		/*
		 *  If we don't do this, we will end up with garbage from previous
		 *  images if they have larger sizes than the one we are currently
		 *  drawing. Also, it seems that the file list can paint outside
		 *  of its rectangle, and will cause odd behavior if we don't clear
		 *  or fill the rectangle for the accessory before drawing. This might
		 *  be a bug in JFileChooser.
		 */
		g.fillRect(0, 0, ACCSIZE, getHeight());
		g.drawImage(image, 5, 0, this);
	}


}

