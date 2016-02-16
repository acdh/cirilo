package org.emile.cirilo.business;

import java.io.File;

import org.emile.cirilo.ecm.repository.Repository;
import org.jdom.input.DOMBuilder;
import org.jdom.Document;
import org.apache.log4j.Logger;

public class TripleStoreFactory {

	private static Logger log = Logger.getLogger(TripleStoreFactory.class);

	private Object triplestore;
	private String TTType;
	
	public TripleStoreFactory() {
		try {
			DOMBuilder db = new DOMBuilder();	
			Document properties = db.build (Repository.getDatastream("cirilo:Backbone", "PROPERTIES"));	
			this.TTType = properties.getRootElement().getChild("TripleStoreTechnology").getText();
			
			if (TTType.equals("Blazegraph"))  triplestore = new BlazeGraphFactory();
			else if (TTType.equals("Sesame"))  triplestore = new SesameFactory();
			else triplestore = null;
			
		} catch (Exception e) {
      		log.error(e.getLocalizedMessage(),e);				      					   
			triplestore = null;
		}
	}
	
	
	public void close () {
		 if (triplestore  instanceof BlazeGraphFactory) ((BlazeGraphFactory)triplestore).close();
		 else if (triplestore  instanceof SesameFactory ) ((SesameFactory)triplestore).close();
	}

	
	public boolean getStatus() {
		 boolean ret = false;
		 if (triplestore  instanceof BlazeGraphFactory) ret = ((BlazeGraphFactory)triplestore).getStatus();
		 else if (triplestore  instanceof SesameFactory ) ret = ((SesameFactory)triplestore).getStatus();
		 return ret;		
	}

	public String getInfo() {
		 if (triplestore  instanceof BlazeGraphFactory) return ((BlazeGraphFactory)triplestore).getInfo();
		 else if (triplestore  instanceof SesameFactory ) return ((SesameFactory)triplestore).getInfo();
		 return this.TTType;
	}

	public void removeAll() {
		 if (triplestore  instanceof BlazeGraphFactory) ((BlazeGraphFactory)triplestore).removeAll();
		 else if (triplestore  instanceof SesameFactory ) ((SesameFactory)triplestore).removeAll();
	}
	
	public boolean insert(File fp, String context) {
		 if (triplestore  instanceof BlazeGraphFactory ) return (((BlazeGraphFactory)triplestore).insert(fp, context));
		 else if (triplestore  instanceof SesameFactory ) return (((SesameFactory)triplestore).insert(fp, context));
		 else return false;
	}
	
	public boolean remove(String context) {
		 if (triplestore  instanceof BlazeGraphFactory ) return (((BlazeGraphFactory)triplestore).remove(context));
		 else if (triplestore  instanceof SesameFactory ) return (((SesameFactory)triplestore).remove(context));
		 else return false;
	}

	public boolean update(File fp, String context) {
		 if (triplestore  instanceof BlazeGraphFactory ) return (((BlazeGraphFactory)triplestore).update(fp, context));
		 else if (triplestore  instanceof SesameFactory ) return (((SesameFactory)triplestore).update(fp, context));
		 else return false;
	}
}
