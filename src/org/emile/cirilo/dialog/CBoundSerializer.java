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

package org.emile.cirilo.dialog;

import java.awt.Container;
import java.awt.Dimension;

import javax.swing.JTable;
import javax.swing.table.TableColumn;

public class CBoundSerializer {
	
	public static void load (Container container, CWindowsProperties prop, JTable table) {
		Dimension screensize = java.awt.Toolkit.getDefaultToolkit().getScreenSize ();
		if ((prop.getX()+prop.getWidth() <= screensize.getWidth() &&  prop.getWidth() > 300)
				&& (prop.getY()+prop.getHeight() <= screensize.getHeight() &&  prop.getHeight() > 300 ) ) {
			container.setSize(new Dimension(prop.getWidth(), prop.getHeight()));
			container.setLocation(prop.getX(), prop.getY());
		       try {
		        	if (table != null)
		        	  for (int i=0; i < table.getColumnCount(); i++) {
		        		TableColumn col = table.getColumn(table.getColumnName(i));
		        		col.setPreferredWidth(prop.getWidth(i)); 		
		          	 }
		        } catch (Exception e) {
		        	e.printStackTrace();
		        }
		} else {
			container.setSize(new Dimension(800,550));
			container.setLocation(100,100);			
		} 
	}
	       
	public static void save (Container container, CWindowsProperties prop, JTable table)
	{			
			prop.setX(container.getX());
			prop.setY(container.getY());
			prop.setWidth(container.getWidth());
			prop.setHeight(container.getHeight());

	        try {
	        	if (table != null)
	        	  for (int i=0; i < table.getColumnCount(); i++) {
   	 				TableColumn col = table.getColumn(table.getColumnName(i));
   	 				prop.setWidth((Integer)col.getWidth(),i);
   	 			 }
	        } catch (Exception e) {
	        	e.printStackTrace();
	        }
	}

}
