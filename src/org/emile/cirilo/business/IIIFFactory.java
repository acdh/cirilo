package org.emile.cirilo.business;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.AuthenticationStore;
import org.eclipse.jetty.client.util.BasicAuthentication;
import org.eclipse.jetty.client.api.ContentResponse;

import voodoosoft.jroots.core.CServiceProvider;

import org.apache.log4j.Logger;
import org.emile.cirilo.*;

public class IIIFFactory {

    private static Logger log = Logger.getLogger(IIIFFactory.class);

    private HttpClient client;
	private User user;
	private String iiif;
	private boolean connection;

	public IIIFFactory() {
	 	  try {	
			    connection = false;
	 			user = (User) CServiceProvider.getService(ServiceNames.CURRENT_USER);
	 			String host = user.getUrl();						
	 			iiif = host.substring(0,host.lastIndexOf("/")+1)+"iiif/";
	 			client = new HttpClient();
	 			AuthenticationStore auth = client.getAuthenticationStore();
	 			auth.addAuthentication(new BasicAuthentication(new java.net.URI(iiif), "IIIF", user.getUser(), user.getPasswd()));		
	 			this.client.start();
		    	ContentResponse response = client.newRequest(iiif+"clear/test")
			    	        .timeout(5, java.util.concurrent.TimeUnit.SECONDS)
			    	        .send();
		    	if (response.getStatus() == 200) connection = true;
	 		 } catch (Exception e) {	
	 		  	log.error(e.getLocalizedMessage(),e);		  	 			 
	 		 }	
	}
	
	 
	
	public boolean delete(String pid) {
		   boolean ret = false;
		   if (connection) {
		      try {	    	
		    	 ContentResponse response = client.newRequest(iiif+"clear/"+pid)
		    	        .timeout(5, java.util.concurrent.TimeUnit.SECONDS)
		    	        .send();
		    	 ret = true;
		       } catch (Exception e) {
		   	  	log.error(e.getLocalizedMessage(),e);		  		    	   
		       }  
		   }
		   return ret;			
	}
	
	public boolean delete(String pid, String image) {
		   boolean ret = false;
		   if (connection) {
		      try {	    	
		    	 ContentResponse response = client.newRequest(iiif+"clear/"+pid+"/"+image)
		    	        .timeout(5, java.util.concurrent.TimeUnit.SECONDS)
		    	        .send();
		    	 ret = true;
		       } catch (Exception e) {
		   	  	 log.error(e.getLocalizedMessage(),e);		  		    	   
		       }  
		   }
		   return ret;			
	}
	
	public void close() {
		try {
			if (this.client != null) this.client.stop();
		} catch (Exception e) {
		  	log.error(e.getLocalizedMessage(),e);		  			
		}	
	}
	
	
}
