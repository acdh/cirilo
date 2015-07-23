package org.emile.cirilo.gui.jtable;

import java.util.*;

import javax.swing.*;
import javax.swing.table.*;
 
public class HarvesterTableModel  extends AbstractTableModel  {

     private ArrayList<String[]> data;
	 private String[] header;
	
	
	 public HarvesterTableModel (String[] header) 
	 {
	   this.header = header;  
	   data = new ArrayList<String[]>();
     }
	
	
	  public int getRowCount()
	  {
		 return data.size();
	  }
	  
      public int getColumnCount() 
      {
	     return header.length;
	  }
      
      public Object getValueAt(int rowIndex, int columnIndex) 
      {
    	  String[] row = data.get(rowIndex);
	      return row[columnIndex];
	  }

      public String[] getRow(int rowIndex) 
      {
    	  return(data.get(rowIndex));
	  }
      
      public String getColumnName(int index) {
          return header[index];
      }
      
      public void add(String[] data) {
          this.data.add(data);
          fireTableDataChanged();

      }
      
      public void removeRow(int row) {
          data.remove(row);
          fireTableDataChanged();
      }
      
  	  public boolean isCellEditable(int row, int column) 
  	  {
		  return false;
	  }
}
