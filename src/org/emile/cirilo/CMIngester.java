package org.emile.cirilo;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.emile.cirilo.ecm.repository.FedoraSoapImpl;
import org.emile.cirilo.ecm.repository.FedoraUserToken;
import org.emile.cirilo.ecm.repository.Repository;
import org.emile.cirilo.ecm.templates.TemplateSubsystem;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;
import org.openrdf.repository.manager.RemoteRepositoryManager;
import org.openrdf.repository.sail.config.SailRepositoryConfig;
import org.openrdf.sail.config.SailImplConfig;
import org.openrdf.sail.inferencer.fc.config.ForwardChainingRDFSInferencerConfig;
import org.openrdf.sail.memory.config.MemoryStoreConfig;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import fedora.client.FedoraClient;


public class CMIngester {
	
	private ArrayList<String> files;

	  
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
        	FedoraClient client = new FedoraClient("http://"+server+"/fedora", user, passwd);
        	Repository.initialise(token,new FedoraSoapImpl());
        	TemplateSubsystem temps = new TemplateSubsystem();

       		RemoteRepositoryManager repositoryManager = new RemoteRepositoryManager("http://"+server+":8080/openrdf-sesame");
            repositoryManager.setUsernameAndPassword(user, passwd);

       		repositoryManager.initialize();
       		SailImplConfig backendConfig = new MemoryStoreConfig(true);				 
			backendConfig = new ForwardChainingRDFSInferencerConfig(backendConfig);
			SailRepositoryConfig repositoryTypeSpec = new SailRepositoryConfig(backendConfig);
			org.openrdf.repository.config.RepositoryConfig repConfig = new org.openrdf.repository.config.RepositoryConfig(server+".fedora", repositoryTypeSpec);
			repositoryManager.addRepositoryConfig(repConfig);					

			if (!Repository.exist("sdef:TEI")) {
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				String format = FedoraClient.FOXML1_1.uri;
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
					Repository.ingestDocument(doc,  format, "Object ingested by Cirilo");
				  } catch (Exception e) {
					  e.printStackTrace();
				  }	
				}		
			}
		} catch (Exception s) {
			s.printStackTrace();
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
		  } catch (Exception e) {e.printStackTrace();}	
	  }	

	  public static void main (String args[])
	  {
		CMIngester q = CMIngester.getInstance();
	    System.out.println("CMIngester 1.0 (C) 2014 by JS");
    	q.ingest(args[0], args[1], args[2], args[3]);
    	System.out.println("Ok");
	  }
	
}
