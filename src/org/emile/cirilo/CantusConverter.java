package org.emile.cirilo;

import org.emile.cirilo.business.TEI;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.File;

import org.apache.commons.io.FileUtils;

public class CantusConverter {

	  private static Logger log = Logger.getLogger(CantusConverter.class);	

	  protected CantusConverter() {}

	  private static CantusConverter instance;

	  /** Singleton Factory
	   * @return instance
	   */
	  public static CantusConverter getInstance ()
	  {
	    if (instance == null) {
	      instance = new CantusConverter();
	    }
	    return instance;
	  }

	
	public void transform(String file) {

        try {

        	PropertyConfigurator.configure(Cirilo.class.getResource("log4jcc.properties"));

        	TEI t = new TEI();
        	t.set(file, false);
        	file = file.replaceAll("\\..*", ".xml");
        	FileUtils.writeStringToFile(new File(file),t.toString());
 				
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
     }


	  public static void main (String args[])
	  {
	    CantusConverter q = CantusConverter.getInstance();
	    System.out.println("CantusConverter 1.0 (C) 2016 by JS");
    	q.transform(args[0]);
	  }
	
}
