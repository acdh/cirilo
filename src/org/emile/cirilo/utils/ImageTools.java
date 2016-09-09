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

import ij.*;
import ij.gui.*;
import ij.io.*;
import ij.process.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

import javax.imageio.*;

import org.apache.log4j.Logger;
import org.emile.cirilo.ecm.exceptions.*;

/**
 * The ImageTools class creates JPEG thumbnails of GIF and JPEG files.
 *
 * @author     yoda
 * @created    28. Juni 2005
 */
public class ImageTools {

	private static Logger log = Logger.getLogger(ImageTools.class);
	/**
	 * Description of the Method
	 *
	 * @param  in                        Description of the Parameter
	 * @param  out                       Description of the Parameter
	 * @param  bgWidth                   Description of the Parameter
	 * @param  bgHeight                  Description of the Parameter
	 * @param  bgColor                   Description of the Parameter
	 * @exception  OutOfMemoryException  Description of the Exception
	 */
	public static void createThumbnail(File in, File out, int bgWidth, int bgHeight, Color bgColor)
			 throws OutOfMemoryException {
		try {
			ImagePlus imp0;
			
			imp0 = new Opener().openImage(in.getCanonicalPath());
			if (imp0 == null) {
				return;
			}

			ImagePlus imp1 = makeThumbnail(imp0, bgWidth, bgHeight, bgColor);

			if (imp1 != null) {
				
				
			    try {
			    	ImageIO.write(imp1.getBufferedImage(), "jpeg", out);
			    } catch (Exception q) { 	
			    	new FileSaver(imp1).saveAsJpeg(out.getCanonicalPath());
			    }	

				imp0 = null;
				imp1 = null;
				return;
			}
			return;
		} catch (Exception e) {
			log.error(e.getLocalizedMessage(),e);				
			throw new OutOfMemoryException();
		}
	}

	public static void createPreview(File in, File out, int bgWidth) throws OutOfMemoryException {
		try {
			ImagePlus imp0;

			imp0 = new Opener().openImage(in.getCanonicalPath());
			if (imp0 == null) {
				return;
			}

			ImagePlus imp1 = makePreview(imp0, bgWidth);

			if (imp1 != null) {
			    try {
			    	ImageIO.write(imp1.getBufferedImage(), "jpeg", out);
			    } catch (Exception q) { 	
			    	new FileSaver(imp1).saveAsJpeg(out.getCanonicalPath());
			    }	

				imp0 = null;
				imp1 = null;
				return;
			}
			return;
		} catch (Exception e) {
			log.error(e.getLocalizedMessage(),e);				
			throw new OutOfMemoryException();
		}
	}

	
	
	/**
	 * Description of the Method
	 *
	 * @param  in                        Description of the Parameter
	 * @param  out                       Description of the Parameter
	 * @exception  OutOfMemoryException  Description of the Exception
	 */
	public static void toJPEG(File in, File out)
			 throws OutOfMemoryException {
		try {
			ImagePlus imp0;

			imp0 = new Opener().openImage(in.getCanonicalPath());
			if (imp0 != null) {
				new FileSaver(imp0).saveAsJpeg(out.getCanonicalPath());
				return;
			}
			return;
		} catch (Exception e) {
			log.error(e.getLocalizedMessage(),e);				
			throw new OutOfMemoryException();
		}
	}


	/**
	 * Description of the Method
	 *
	 * @param  imp       Description of the Parameter
	 * @param  bgWidth   Description of the Parameter
	 * @param  bgHeight  Description of the Parameter
	 * @param  bgColor   Description of the Parameter
	 * @return           Description of the Return Value
	 */
	static ImagePlus makePreview(ImagePlus imp, int bgWidth) {

		ImageProcessor ip = imp.getProcessor();
		
		if (imp.getType() == ImagePlus.COLOR_256) {
			ip = ip.convertToRGB();
		}

		ImagePlus imp1 = new ImagePlus("", ip.resize(bgWidth));  

		return imp1;
	}

	static ImagePlus makeThumbnail(ImagePlus imp, int bgWidth, int bgHeight, Color bgColor) {

		ImageProcessor ip = imp.getProcessor();

		int scaledWidth = ip.getWidth();
		int scaledHeight = ip.getHeight();

		if (imp.getType() == ImagePlus.COLOR_256) {
			ip = ip.convertToRGB();
		}

		if (ip.getWidth() > bgWidth || ip.getHeight() > bgHeight) {
			double ratio = (double) ip.getWidth() / (double) ip.getHeight();

			scaledWidth = bgWidth;
			scaledHeight = (int) ((double) scaledWidth / ratio);
			if (scaledHeight > bgHeight) {
				scaledHeight = bgWidth;
				scaledWidth = (int) ((double) scaledHeight * ratio);
			}
		}

		ImagePlus imp1 = NewImage.createRGBImage("Thumbnail", bgWidth, bgHeight, 1, 0);

		imp1.setColor(bgColor);
		imp1.draw(0, 0, bgWidth, bgHeight);

		ImageProcessor ip1 = imp1.getProcessor();

		ip1.setColor(bgColor);
		ip1.fill();
		ip1.insert(ip.resize(scaledWidth, scaledHeight), (int) ((bgWidth - scaledWidth) / 2.0), (int) ((bgHeight - scaledHeight) / 2.0));

		if (ip1 instanceof ShortProcessor || ip1 instanceof FloatProcessor) {
			ip1 = ip1.convertToByte(true);
		}

		return imp1;
	}
	

}

