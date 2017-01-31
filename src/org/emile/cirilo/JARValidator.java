package org.emile.cirilo;

import java.util.*;
import java.util.zip.*;
import java.io.*;

public class JARValidator {

	  static ArrayList<String> files = new ArrayList<String>();
	  private static JARValidator instance;
	  private static FileWriter out;
	
	  public static JARValidator getInstance ()
	  {
	    if (instance == null) {
	      instance = new JARValidator ();
	    }
	    return instance;
	  }

	
	  public static void main (String args[])
	  {
		  JARValidator q = JARValidator.getInstance();
	      System.out.println("Validator 1.0 (C) 2016 by JS");
	      if (args.length != 1) {	
	    	 System.out.println("usage: Validator <dir>");
	     } else {
	    	 q.validate(args[0]);	
	    	 System.out.println("Terminated normally");
	      }
	  }
	  
	  private void validate(String dir) {
	
		  
		  
	    treewalk(new File(dir));
        try {
        	out = new FileWriter(new File("/Users/yoda/listing.txt"));
        } catch (Exception e) {}
        
        ZipEntry entry = null;
        
	    for (int i = 0;  i< files.size(); i++) {
	    	try {
	    		out.write("\n\n------ "+files.get(i));
	    		ZipFile file = new ZipFile(new File(files.get(i)));
	    		Enumeration<? extends ZipEntry> e = file.entries();
	    		while(e.hasMoreElements()) {
	    	    	try {
	    	    		entry = e.nextElement();
	    	    		out.write("OK             "+entry.getName()+"\n");
	    	    	} catch(Exception q) {
	        			out.write("CORRUPT  "+entry.getName()+"\n");
	    	    	}		
	    		}
	    		file.close();
	    	} catch(Exception ex) {
	    		try {
	    			out.write(ex.getMessage());
	    		} catch (Exception x) {}
	    	}
	    }	
	}
	
	private static void treewalk(File file) {
		   
		  try {
	 		if (file.isDirectory()) {
		 		File[] children = file.listFiles();
		 		for (int i = 0; i < children.length; i++) {
		 			treewalk(children[i]);
		 		}
	     	} else if (file.getAbsolutePath().toLowerCase().endsWith(".jar")) {
	     		files.add(file.getAbsolutePath());	     		
	     	}
		  } catch (Exception e) {}	
	}	

}
