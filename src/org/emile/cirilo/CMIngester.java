package org.emile.cirilo;

import java.io.*;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.emile.cirilo.ecm.repository.FedoraSoapImpl;
import org.emile.cirilo.ecm.repository.FedoraUserToken;
import org.emile.cirilo.ecm.repository.Repository;
import org.emile.cirilo.business.UpgradeFactory;
import org.jdom.Element;
import org.jdom.input.DOMBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import fedora.client.FedoraClient;


public class CMIngester {

	  private static Logger log = Logger.getLogger(CMIngester.class);	
	  private ArrayList<String> files;
      private Format format;
      private XMLOutputter outputter;

	  protected CMIngester()
	  {
	  }

	  private static CMIngester instance;

	  /** Singleton Factory
	   * @return instance
	   */
	  public static CMIngester getInstance ()
	  {
	    if (instance == null) {
	      instance = new CMIngester();
	    }
	    return instance;
	  }

	
	public void ingest(String dir, String server, String user, String passwd) {

        try {

        	FedoraUserToken token = new FedoraUserToken("http://"+server+"/fedora", user, passwd);
        	Repository.initialise(token,new FedoraSoapImpl());

        	format = Format.getRawFormat();
    		format.setEncoding("UTF-8");
    		outputter = new XMLOutputter(format);
        	
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			String foxml = FedoraClient.FOXML1_1.uri;
			String fedora = "http://"+server+"/fedora";					
			String host = fedora.substring(0,fedora.lastIndexOf("/"));
			String cocoon = host+"/cocoon";
			
	    	files = new ArrayList<String>();
            File fp = new File (dir);
	    	treeWalk(fp);
			for (int i = 0; i<files.size(); i++) {
				  try {
			    	Document doc = builder.parse((String) files.get(i));
					DOMSource domSource = new DOMSource(doc);
					StringWriter writer = new StringWriter();
					StreamResult result = new StreamResult(writer);
					TransformerFactory tf = TransformerFactory.newInstance();
					Transformer transformer = tf.newTransformer();
					transformer.transform(domSource, result);
					InputSource is = new InputSource();
					is.setCharacterStream(new StringReader(writer.toString()
				    .replaceAll("http://fedora.host/fedora", fedora)
					.replaceAll("http://fedora.host/cocoon", cocoon)
					.replaceAll("http://fedora.host", host)
					.replaceAll(host+"#", "http://gams.uni-graz.at#")
					.replaceAll(host+"/ontology#","http://gams.uni-graz.at/ontology#")));
					doc = builder.parse(is);
					Repository.ingestDocument(doc,  foxml, "Object ingested by Cirilo");
					System.out.print(".");
				  } catch (Exception e) {
					 e.printStackTrace();
				  }	
			}
			
			Thread.sleep(10000);
			
			DOMBuilder db = new DOMBuilder();	
			org.jdom.Document properties = db.build (Repository.getDatastream("cirilo:Backbone", "PROPERTIES"));
		
			int installed = new Integer(properties.getRootElement().getChild("ContentModels").getText());
			int current = new Integer(Common.CM_VERSION);
			
			if (current != installed) {
				UpgradeFactory uf = new UpgradeFactory(fedora, host);
				uf.addDefaultDatastreams();
				
				ArrayList<String> entries = Repository.getPidList("");			
				for (String s: entries) {

					try {
					    uf.updateDatastreams(s);	
						System.out.print(".");
					} catch (Exception e) {
						e.printStackTrace();
					}

					try {
						Thread.sleep(50); 
					} catch (InterruptedException e) {
					}
				} 
				
				Element cm = properties.getRootElement().getChild("ContentModels");
				cm.setText(Common.CM_VERSION);
				Repository.modifyDatastreamByValue("cirilo:Backbone", "PROPERTIES", "text/xml", new String(outputter.outputString(properties).getBytes("UTF-8"),"UTF-8"));

			}

				
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
     }

	  private void treeWalk(File file) {
		  try {
	 		if (file.isDirectory()) {
		 		File[] children = file.listFiles();
		 		for (int i = 0; i < children.length; i++) {
		 			treeWalk(children[i]);
		 		}
	     	} else if (file.getAbsolutePath().toLowerCase().endsWith(".xml")) {
	     		files.add(file.getAbsolutePath());
	     	}
		  } catch (Exception e) {log.error(e.getLocalizedMessage(),e);	}	
	  }	

	  public static void main (String args[])
	  {
		CMIngester q = CMIngester.getInstance();
	    System.out.println("CMIngester 1.0 (C) 2014 by JS");
    	q.ingest(args[0], args[1], args[2], args[3]);
    	System.out.println(" Ok");
	  }
	
}
