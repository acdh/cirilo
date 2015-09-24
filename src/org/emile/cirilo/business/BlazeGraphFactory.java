package org.emile.cirilo.business;

import java.io.File;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

import com.bigdata.rdf.sail.webapp.SD;
import com.bigdata.rdf.sail.webapp.client.ConnectOptions;
import com.bigdata.rdf.sail.webapp.client.JettyResponseListener;
import com.bigdata.rdf.sail.webapp.client.RemoteRepository;
import com.bigdata.rdf.sail.webapp.client.RemoteRepository.AddOp;
import com.bigdata.rdf.sail.webapp.client.RemoteRepository.RemoveOp;
import com.bigdata.rdf.sail.webapp.client.RemoteRepositoryManager;

import org.eclipse.jetty.client.*;
import org.eclipse.jetty.client.util.BasicAuthentication;
import org.eclipse.jetty.client.api.AuthenticationStore;

import org.emile.cirilo.Common;
import org.emile.cirilo.ServiceNames;
import org.emile.cirilo.User;
import org.openrdf.model.Statement;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.rio.RDFFormat;

import voodoosoft.jroots.core.CPropertyService;
import voodoosoft.jroots.core.CServiceProvider;

public class BlazeGraphFactory {
	
	final private String BLAZEGRAPH = "Blazegraph";
	
	private String sparqlEndPoint;
	private RemoteRepositoryManager repository;
	private HttpClient client;
	private User user;

	public BlazeGraphFactory () {
	  try {	
		CPropertyService props=(CPropertyService) CServiceProvider.getService(ServiceNames.PROPERTIES);
		User user = (User) CServiceProvider.getService(ServiceNames.CURRENT_USER);
		String host = user.getUrl();						
		sparqlEndPoint = host.substring(0,host.lastIndexOf("/")+1)+"bigdata";
		client = new HttpClient();
		AuthenticationStore auth = client.getAuthenticationStore();
		auth.addAuthentication(new BasicAuthentication(new java.net.URI(sparqlEndPoint), BLAZEGRAPH, user.getUser(), user.getPasswd()));		    		   			
		this.client.start();
		this.repository = new RemoteRepositoryManager(sparqlEndPoint, false, client, null);
	  } catch (Exception e) {		  
	  }	
	}

	public void close() {
		try {
			this.client.stop();
			this.repository.close();
		} catch (Exception e) {}	
	}
	
	public boolean getStatus() {
		
			   boolean ret = true;
			   try {
			      final ConnectOptions opts = new ConnectOptions(sparqlEndPoint + "/status");
			      opts.method = "GET";
			      final JettyResponseListener response = this.repository.doConnect(opts);
			      this.repository.checkResponseCode(response); // can throw HttpException
			   } catch (Exception e) {
                  ret = false;
			   } finally {
				  if (!ret) show();
			   }

			   return ret;
	}	

	public String getInfo() {
		return this.sparqlEndPoint;
	}
	
    private void show()  {	
    	try {
    		ResourceBundle  res = (ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);
    		MessageFormat msgFmt = new MessageFormat(res.getString("triplestoreerror"));
    		Object[] args = {BLAZEGRAPH}; 		    		
    		JOptionPane.showMessageDialog(null, msgFmt.format(args), Common.WINDOW_HEADER, JOptionPane.ERROR_MESSAGE);
    	} catch (Exception e) {}
    }

	public boolean insert(File fp, String context) {
		try {
			RemoteRepository.AddOp ao= new RemoteRepository.AddOp(fp, RDFFormat.RDFXML);
			ao.setContext(new org.openrdf.model.impl.URIImpl(context));		
			this.repository.getRepositoryForDefaultNamespace().add(ao);
		} catch (Exception e) {
            return  false;
		}    
		return true;
	}

	public boolean removeAll() {
		try {
			RemoveOp ro = new RemoveOp(null,null,null);			
			this.repository.getRepositoryForDefaultNamespace().remove(ro);		
		} catch (Exception e) {
            return  false;
		}    
		return true;
	}
	
	public boolean remove(String context)  {
		try {
			RemoveOp ro = new RemoveOp(null,null,null,new org.openrdf.model.impl.URIImpl(context));			
			this.repository.getRepositoryForDefaultNamespace().remove(ro);		
		} catch (Exception e) {
		   return false;
		}    
		return true;
	}

	public boolean update(File fp, String context) {
		try {
			RemoveOp ro = new RemoveOp(null,null,null,new org.openrdf.model.impl.URIImpl(context));
			AddOp ao= new AddOp(fp, RDFFormat.RDFXML);
			ao.setContext(new org.openrdf.model.impl.URIImpl(context));			
			this.repository.getRepositoryForDefaultNamespace().remove(ro);		
			this.repository.getRepositoryForDefaultNamespace().add(ao);
		} catch (Exception e) {
            return  false;
		}    
		return true;
	}
}
