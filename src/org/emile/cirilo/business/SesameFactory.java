package org.emile.cirilo.business;

import java.io.File;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

import org.openrdf.repository.manager.RemoteRepositoryManager;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.model.impl.URIImpl;
import org.emile.cirilo.Common;
import org.emile.cirilo.ServiceNames;
import org.emile.cirilo.User;
import org.apache.log4j.Logger;

import voodoosoft.jroots.core.CServiceProvider;

public class SesameFactory {
	
	private static Logger log = Logger.getLogger(SesameFactory.class);
	   
	private Repository repository;
	private RemoteRepositoryManager manager;
	private RepositoryConnection connection;
	private String sparqlEndPoint;

	public SesameFactory () {
		try {
			User user = (User) CServiceProvider.getService(ServiceNames.CURRENT_USER);
			String host = user.getUrl();						
			sparqlEndPoint = host.substring(0,host.lastIndexOf("/")+1)+"openrdf-sesame";
			this.manager = new RemoteRepositoryManager(sparqlEndPoint);
			this.manager.setUsernameAndPassword(user.getSesameUser(), user.getSesamePasswd());
			this.manager.initialize();	 				           	
			this.repository = this.manager.getRepository("FEDORA"); 	
			this.repository.initialize(); 	       	
			this.connection = this.repository.getConnection();
		} catch (Exception e) {
			log.error(e.getLocalizedMessage(),e);	
			this.connection = null;
		}
		
	}
	public void close() {
		try {
			this.connection.close();
		} catch (Exception e) {
      		log.error(e.getLocalizedMessage(),e);				      					   			
		}	
	}
	
	public boolean getStatus() {
		return this.connection != null;
	}

	public String getInfo() {
		return this.sparqlEndPoint;
	}

    private void show()  {	
    	try {
    		ResourceBundle  res = (ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);
    		MessageFormat msgFmt = new MessageFormat(res.getString("triplestoreerror"));
    		Object[] args = {"Sesame"}; 		    		
    		JOptionPane.showMessageDialog(null, msgFmt.format(args), Common.WINDOW_HEADER, JOptionPane.ERROR_MESSAGE);
    	} catch (Exception e) {
      		log.error(e.getLocalizedMessage(),e);				      					    		
    	}
    }

	public boolean removeAll() {
		try {
			this.connection.remove(new URIImpl(null),new URIImpl(null),new URIImpl(null)); 							 							  				
		} catch (Exception e) {
      		log.error(e.getLocalizedMessage(),e);				      					   		
            return  false;
		}    
		return true;
	}

	public boolean insert(File fp, String context) {
		try {
			this.connection.add(fp, null, org.openrdf.rio.RDFFormat.RDFXML, new URIImpl(context));
		} catch (Exception e) {
      		log.error(e.getLocalizedMessage(),e);				      					   		
			show();
            return  false;
		}    
		return true;
	}
	
	public boolean remove(String context) {
		try {
			this.connection.clear(new URIImpl(context)); 							 							  				
		} catch (Exception e) {
      		log.error(e.getLocalizedMessage(),e);				      					   		
			show();
            return  false;
		}    
		return true;
	}

	public boolean update(File fp, String context) {
		try {

			this.connection.clear(new URIImpl(context)); 							 							  				
			this.connection.add(fp, null, org.openrdf.rio.RDFFormat.RDFXML, new URIImpl(context));
		} catch (Exception e) {
      		log.error(e.getLocalizedMessage(),e);				      					   		
			show();
            return  false;
		}    
		return true;
	}
}
