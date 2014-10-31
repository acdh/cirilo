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
import javax.swing.table.*;

public class CBoundSerializer {
	
	public static void load (Container container, CWindowsProperties prop, Object table) {
		Dimension screensize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();		

		if ((prop.getX()+prop.getWidth() <= screensize.getWidth() &&  prop.getWidth() > 300)
				&& (prop.getY()+prop.getHeight() <= screensize.getHeight() &&  prop.getHeight() > 300 ) ) {
			container.setSize(new Dimension(prop.getWidth(), prop.getHeight()));
			container.setLocation(prop.getX(), prop.getY());
		} else {
			container.setSize(new Dimension(800,550));
			container.setLocation(100,100);			
		} 
       try {
	          if (table != null) {
	              if (table instanceof JTable) {
	            	  JTable t = (JTable) table;
	            	  for (int i=0; i < t.getColumnCount(); i++) {
	            		  TableColumn col = t.getColumn(t.getColumnName(i));
	            		  col.setPreferredWidth(prop.getWidth(i)); 		
	            	  }
	               }
	              
	               if (table instanceof JTable[]) {
		               JTable[] t = (JTable[]) table;
		               int dx = 0;
	            	   for (int j=0; j<t.length;j++) {
		            	  for (int i=0; i < t[j].getColumnCount(); i++) {
		            		  TableColumn col = t[j].getColumn(t[j].getColumnName(i));
		            		  col.setPreferredWidth(prop.getWidth(dx+i)); 		
		            	  }
	            	      dx = t[j].getColumnCount();
	            	   }  
	               }
	        	} 
        } catch (Exception e) {
	        	e.printStackTrace();
        }
	}
	       
	public static void save (Container container, CWindowsProperties prop, Object table)
	{			
			prop.setX(container.getX());
			prop.setY(container.getY());
			prop.setWidth(container.getWidth());
			prop.setHeight(container.getHeight());

	        try {
	        	if (table != null) {
	        	  if (table instanceof JTable) {
	        		  JTable t = (JTable) table;
	        		  for (int i=0; i < t.getColumnCount(); i++) {
	        			  TableColumn col = t.getColumn(t.getColumnName(i));
	        			  prop.setWidth((Integer)col.getWidth(),i);
   	 			  	}
	        	  } 
	               if (table instanceof JTable[]) {
		               JTable[] t = (JTable[]) table;
		               int dx = 0;
	            	   for (int j=0; j<t.length;j++) {
	 	        		  for (int i=0; i < t[j].getColumnCount(); i++) {
		        			  TableColumn col = t[j].getColumn(t[j].getColumnName(i));
		        			  prop.setWidth((Integer)col.getWidth(),dx+i);
	   	 			  	  }
	            	      dx = t[j].getColumnCount();
	            	  }  
	               }
	        	  
	        	  
	        	}	  
	        } catch (Exception e) {
	        	e.printStackTrace();
	        }
	}

}
