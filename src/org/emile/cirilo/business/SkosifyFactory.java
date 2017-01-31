package org.emile.cirilo.business;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.AuthenticationStore;
import org.eclipse.jetty.client.util.BasicAuthentication;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.util.Fields;
import org.eclipse.jetty.util.Fields.Field;
import org.apache.log4j.Logger;

import voodoosoft.jroots.core.CPropertyService;
import voodoosoft.jroots.core.CServiceProvider;

import org.emile.cirilo.*;

public class SkosifyFactory {

	private static Logger log = Logger.getLogger(SkosifyFactory.class);

	private HttpClient client;
	private User user;
	private String skosify;
	private boolean connection;

	public SkosifyFactory() {
	 	  try {	
			    connection = false;
	 			CPropertyService props=(CPropertyService) CServiceProvider.getService(ServiceNames.PROPERTIES);
	 			user = (User) CServiceProvider.getService(ServiceNames.CURRENT_USER);
	 			String host = user.getUrl();						
	 			skosify = host.substring(0,host.lastIndexOf("/")+1)+"skosify/";
	 			client = new HttpClient();
	 			AuthenticationStore auth = client.getAuthenticationStore();
	 			auth.addAuthentication(new BasicAuthentication(new java.net.URI(skosify), "skosify", user.getUser(), user.getPasswd()));		
	 			this.client.start();
		    	ContentResponse response = client.newRequest(skosify+"status")
		    	        .timeout(5, java.util.concurrent.TimeUnit.SECONDS)
		    	        .send();
		    	if (response.getStatus() == 200) connection = true;
	 		 } catch (Exception e) {	
		      log.error(e.getLocalizedMessage(),e);				      					    			 
	 		 }	
	}
	
	 
	
	public String skosify(String skos) {
		  String content = "";
		   if (connection) {
		      try {	
		 		 Fields fields = new Fields();
		 		 fields.put(new Field("skos",skos));
		    	 ContentResponse response = client.FORM(skosify, fields);
		    	 content = new String (response.getContent());
		       } catch (Exception e) {
		      	 log.error(e.getLocalizedMessage(),e);				      					   		    	   
		       }
		   }
		   return content;			
	}
	

	
	public File skosify(File skos) {
		   File out = null;
	       if (connection) {
		      try {	
		         out = File.createTempFile("temp", ".tmp");
		         BufferedReader reader = new BufferedReader (new FileReader(skos));
		    	 StringBuffer content = new StringBuffer();		
		    	 String line;
		    	 while((line=reader.readLine()) != null) {
		    	   content.append(line+"\n");    	 
		    	 }		    	
		    	 reader.close();
		    	 
		 		 Fields fields = new Fields();
		 		 fields.put(new Field("skos",content.toString()));
		    	 ContentResponse response = client.FORM(skosify, fields);
		    	 
                 FileWriter writer = new FileWriter(out);
                 writer.write(new String(response.getContent()));
                 writer.close();
                 
		       } catch (Exception e) {
		      	  log.error(e.getLocalizedMessage(),e);				      					   		    	   
		    	  try { 
		    		  out.delete();
		    	  } catch (Exception q) {	  
		    		  out = null;
		    	  }	  
		       }  
		   }
		   return out; 			
	}

	
	public void close() {
		try {
			if (this.client != null) this.client.stop();
		} catch (Exception e) {
      		log.error(e.getLocalizedMessage(),e);				      					   			
		}	
	}
	
	
}
