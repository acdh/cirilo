/*
 *  -------------------------------------------------------------------------
 *  Copyright 2014 
 *  Centre for Information Modeling - Austrian Centre for Digital Humanities
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 *  -------------------------------------------------------------------------
 */

package org.emile.cirilo.ecm.templates;

import org.emile.cirilo.Common;
import org.emile.cirilo.ecm.exceptions.FedoraConnectionException;
import org.emile.cirilo.ecm.exceptions.FedoraIllegalContentException;
import org.emile.cirilo.ecm.exceptions.ObjectIsWrongTypeException;
import org.emile.cirilo.ecm.exceptions.ObjectNotFoundException;
import org.emile.cirilo.ecm.exceptions.PIDGeneratorException;
import org.emile.cirilo.ecm.repository.PidList;
import org.emile.cirilo.ecm.repository.Repository;
import org.emile.cirilo.ecm.utils.Constants;
import org.emile.cirilo.ecm.utils.XpathUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import voodoosoft.jroots.dialog.CDefaultGuiAdapter;

import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactoryConfigurationException;

import org.apache.log4j.Logger;

import java.util.List;
import java.util.StringTokenizer;

import javax.swing.*;

import org.jdom.input.DOMBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;




/**
 * This class deals with the template subsystem.
 */
public class TemplateSubsystem {

	private static Logger log = Logger.getLogger(TemplateSubsystem.class);

	
    private static final String FOXML_DIGITAL_OBJECT_PID = "/foxml:digitalObject/@PID";
    private static final String RELSEXT_ABOUT = "/foxml:digitalObject/foxml:datastream[@ID='RELS-EXT']/"
                             + "foxml:datastreamVersion[position()=last()]/"
                         + "foxml:xmlContent/rdf:RDF/"
                         + "rdf:Description/@rdf:about";
    private static final String DCIDENTIFIER = "/foxml:digitalObject/foxml:datastream[@ID='DC']/"
    					+ "foxml:datastreamVersion[position()=last()]/"
    					+ "foxml:xmlContent/oai_dc:dc/dc:identifier";
    private static final String DCTITLE = "/foxml:digitalObject/foxml:datastream[@ID='DC']/"
        				+ "foxml:datastreamVersion[position()=last()]/"
        				+ "foxml:xmlContent/oai_dc:dc/dc:title";
    private static final String ISTEMPLATEFOR = "/foxml:digitalObject/foxml:datastream[@ID='RELS-EXT']/"
                         + "foxml:datastreamVersion[position()=last()]/"
    + "foxml:xmlContent/rdf:RDF/"
    + "rdf:Description/doms:isTemplateFor";
    private static final String DATASTREAM_AUDIT = "/foxml:digitalObject/foxml:datastream[@ID='AUDIT']";
    private static final String DATASTREAM_NEWEST = "/foxml:digitalObject/foxml:datastream/"
                + "foxml:datastreamVersion[position()=last()]";
    private static final String DATASTREAM_CREATED = "/foxml:digitalObject/foxml:datastream/foxml:datastreamVersion";
    private static final String OBJECTPROPERTY_CREATED = "/foxml:digitalObject/foxml:objectProperties/foxml:property[@NAME='info:fedora/fedora-system:def/model#createdDate']";
    private static final String OBJECTPROPERTIES_LSTMODIFIED = "/foxml:digitalObject/foxml:objectProperties/foxml:property[@NAME='info:fedora/fedora-system:def/view#lastModifiedDate']";
    /** AAR Ext 1.0 **/
    private static final String DATASTREAM_CONTENTLOCATION = "/foxml:digitalObject/foxml:datastream/foxml:datastreamVersion/foxml:contentLocation/@REF";
    private static final String DATASTREAM_GROUPR = "/foxml:digitalObject/foxml:datastream[@CONTROL_GROUP='R']/foxml:datastreamVersion/foxml:contentLocation/@REF";
    private static final String OBJECTPROPERTIES_OWNERID = "/foxml:digitalObject/foxml:objectProperties/foxml:property[@NAME='info:fedora/fedora-system:def/model#ownerId']/@VALUE";
    private static final String RELSEXT_DESCRIPTION = "/foxml:digitalObject/foxml:datastream[@ID='RELS-EXT']/"
    	+ "foxml:datastreamVersion[position()=last()]/foxml:xmlContent/rdf:RDF/rdf:Description[@rdf:about]";
    private static final String DC_DATASTREAM = "/foxml:digitalObject/foxml:datastream[@ID='DC']/"
        + "foxml:datastreamVersion/"
        + "foxml:xmlContent/oai_dc:dc";

    
    
    /**
     * Mark the objpid object as a template for the cmpid object
     * @param objpid the object to mark
     * @param cmpid the content model to make objpid a template for
     * @throws ObjectNotFoundException if either of the objects do not exist
     * @throws FedoraConnectionException if anything went wrong in the communication
     * @throws ObjectIsWrongTypeException if the object is not a data object or the content model is not a content model
     */
    public void markObjectAsTemplate(
            String objpid,
            String cmpid)
            throws ObjectNotFoundException, FedoraConnectionException,
                   ObjectIsWrongTypeException, FedoraIllegalContentException {
        log.trace("Entering markObjectAsTemplate with params: " + objpid + " and "+cmpid );
        //Working

        if (!Repository.exist(cmpid)){
            throw new ObjectNotFoundException("The content model '"+cmpid+
                                              "' does not exist");
        }
        if (!Repository.exist(objpid)){
            throw new ObjectNotFoundException("The data object '"+objpid+
                                              "' does not exist");
        }


        boolean added = Repository.addRelation(objpid, Constants.TEMPLATE_REL, cmpid);
        log.info("Marked object '"+objpid+"' as template for '"+cmpid+"'");
        if (!added){
            //The object is already a template. Note this in the log, and do no more
            log.info("Object '"+objpid+"' was already a template for '"+cmpid+"' so no change was performed");
        }
    }



    public PidList findTemplatesFor(String cmpid)
            throws ObjectNotFoundException,
                   FedoraConnectionException,
                   FedoraIllegalContentException,
                   ObjectIsWrongTypeException {
        //Working
       log.trace("Entering findTemplatesFor with param '"+cmpid+"'");

        if (Repository.exist(cmpid)){
            if (Repository.isContentModel(cmpid)){

                List<String> childcms
                        = Repository.getInheritingContentModels(cmpid);

                String contentModel
                        = "<"+
                          Repository.ensureURI(cmpid)+
                          ">\n";

                String query = "select $object\n" +
                               "from <#ri>\n" +
                               "where\n" +
                               " $object <" + Constants.TEMPLATE_REL + "> " +
                               contentModel;

                for (String childcm: childcms){
                    String cm = "<" +
                                Repository.ensureURI(childcm) +
                                ">\n";

                    query = query +
                            "or $object <" +
                            Constants.TEMPLATE_REL +
                            "> " + cm;
                }
               return Repository.query(query);
            } else {
                throw new ObjectIsWrongTypeException("The pid '" +
                                                     cmpid +
                                                     "' is not a content model");
            }
        } else {
            throw new ObjectNotFoundException("The pid '" +
                                              cmpid +
                                              "' is not in the repository");
        }

    }


    public void makeTemplate(String templatepid, String ownerid, String newPid, String dctitle, String cm) {
    	try {
    		cloneInternalTemplate(templatepid, ownerid, newPid, (String) null, false);
			if (!Common.exist(newPid.substring(1))) {
				 throw new Exception();
			}
    		markObjectAsTemplate(newPid.substring(1), cm);    		
    	} catch (Exception e) {log.error(e.getLocalizedMessage(),e);	}
    	
    	return;    	    	
    }
        


    public String cloneTemplate(String templatepid, String ownerid, String newPid, String dctitle)
        throws FedoraIllegalContentException,
        FedoraConnectionException, PIDGeneratorException,
        ObjectNotFoundException,
        ObjectIsWrongTypeException, XPathFactoryConfigurationException {
    	return cloneInternalTemplate( templatepid,  ownerid,  newPid,  dctitle, true);
    }
    
    public String cloneInternalTemplate(String templatepid, String ownerid, String newPid, String dctitle, boolean mode)
            throws FedoraIllegalContentException,
                   FedoraConnectionException, PIDGeneratorException,
                   ObjectNotFoundException,
                   ObjectIsWrongTypeException, XPathFactoryConfigurationException {

        //working
        templatepid = Repository.ensurePID(templatepid);
       log.debug("Entering cloneTemplate with param '" + templatepid + "'");

        if (!Repository.exist(templatepid)){
            throw new ObjectNotFoundException("The object (" + templatepid +
                                              " does not exists");
        }
        if (!Repository.isTemplate(templatepid)){
            throw new ObjectIsWrongTypeException("The pid (" + templatepid +
                                                 ") is not a pid of a template");
        }

        // Get the document
        Document document = Repository.getObjectXml(templatepid);
		
		newPid = newPid + (!newPid.contains("context:") && !newPid.contains("query:") && !newPid.startsWith("container:")  && !newPid.startsWith("$") ? "." + Repository.getNextPid().replaceAll("(.*):(.*)","$2") : "");
        newPid = (newPid.startsWith("$")  ? newPid.substring(1) : newPid);
		        
       log.debug("Generated new pid '" + newPid + "'");

        try {
        	removeOlderVersions(document);
        	
            removeAudit(document);
           log.debug("Audit removed");
            removeDatastreamVersions(document);
           log.debug("Datastreamsversions removed");

            // Replace PID
            replacePid(document, templatepid, newPid);
           log.debug("Pids replaced");

            /** AAR Ext 1.0 **/
            replaceOwner(document, ownerid);
           log.debug("Ownerid replaced");
            
            removeDCidentifier(document);
           log.debug("DC identifier removed");

            if (dctitle != null) setExpathList(document, DCTITLE, dctitle );
            
            removeCreated(document);
           log.debug("CREATED removed");

            removeLastModified(document);
           log.debug("Last Modified removed");

            removeTemplateRelation(document);
          log.debug("Template relation removed");
                       
        } catch (XPathExpressionException e){
            throw new FedoraIllegalContentException(
                    "Template object did not contain the correct structure",e);
        }
        
        
        //fix a fedora-bug 
        String xmlSource = "";
        try {    		
        	DOMBuilder domBuilder = new DOMBuilder();
        	org.jdom.Document doc = domBuilder.build(document);
        	Format format = Format.getRawFormat();
        	format.setEncoding("UTF-8");
        	XMLOutputter outputter = new XMLOutputter(format);
        	xmlSource = outputter.outputString(doc).replace("<oai_dc:dc ", "<oai_dc:dc xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ");
        	
    	} catch (Exception q) {}

        	//reingest the object
        String stream = Repository.ingestDocument(
                xmlSource,
                "Cloned from template '" +
                templatepid +
                "' by user '" +
                Repository.getUser() +
                "'");

	
        try {
        	if(!Common.exist(newPid)) {
			 throw new Exception();
			}
        	if (newPid.contains("TEI")) { 
        		setLocation(newPid, "STYLESHEET", "STYLESHEET", ownerid);
        		setLocation(newPid, "FO_STYLESHEET", "FO_STYLESHEET", ownerid);
        		setLocation(newPid, "DC_MAPPING", "DC_MAPPING", ownerid);
        		setLocation(newPid, "RDF_MAPPING", "RDF_MAPPING", ownerid);
        		setLocation(newPid, "REPLACEMENT_RULESET", "REPLACEMENT_RULESET", ownerid);
        		setLocation(newPid, "HSSF_STYLESHEET", "HSSF_STYLESHEET", ownerid);
            }
        	if (newPid.contains("Context")) { 
        		setLocation(newPid, "STYLESHEET", "CONTEXTtoHTML", ownerid);
        		setLocation(newPid, "FO_STYLESHEET", "CONTEXTtoFO", ownerid);
        		setLocation(newPid, "KML_TEMPLATE", "KML_TEMPLATE", ownerid);
        		setLocation(newPid, "HSSF_STYLESHEET", "HSSF_STYLESHEET", ownerid);
            }
        	if (mode) {
        		byte[] buf = Repository.getDatastream(templatepid, "QUERY", (String) null);
        		Repository.modifyDatastream(newPid, "QUERY", "application/sparql-query", new String(buf).replaceAll("obj:self",newPid).getBytes("UTF-8"));        	
        	}
        	

        } catch (Exception e) {
        	log.error(e.getLocalizedMessage(),e);	
        }
        return stream;
    }

    private void setLocation( String pid, String dsid, String ndsid, String ownerid) {
    	try {
        	byte[] buf = Repository.getDatastream(pid, dsid, (String) null);
        	String s = new String(buf);
    	    int i = s.indexOf("cirilo:");
    	    if (i > -1) {
			  Repository.modifyDatastream (pid, dsid, null, "R", s.substring(0,i)+"cirilo:"+ownerid+"/datastreams/"+ndsid+"/content");
    	    }
        	
    	} catch (Exception e) {
           	log.error(e.getLocalizedMessage(),e);	               		
    	}
    }
    
    public String cloneTemplate(String templatepid, String ownerid, String newPid, CDefaultGuiAdapter moGA)
    throws FedoraIllegalContentException,
           FedoraConnectionException, PIDGeneratorException,
           ObjectNotFoundException,
           ObjectIsWrongTypeException, XPathFactoryConfigurationException {

    	//working
    	templatepid = Repository.ensurePID(templatepid);
    	log.trace("Entering cloneTemplate with param '" + templatepid + "'");

    	if (!Repository.exist(templatepid)){
    		throw new ObjectNotFoundException("The object (" + templatepid +
    								" does not exists");
    	}
    	if (!Repository.isTemplate(templatepid)){
    		throw new ObjectIsWrongTypeException("The pid (" + templatepid +
                                         ") is not a pid of a template");
    	}

    	//Get the document
    	Document document = Repository.getObjectXml(templatepid);

		try {
			newPid = newPid + (!newPid.contains("context:") && !newPid.contains("query:") && !newPid.startsWith("container:") ? "." + Repository.getNextPid().replaceAll("(.*):(.*)","$2")  : "");
		} catch (Exception ex) {	
			log.error(ex.getLocalizedMessage(),ex);
		}
     		
    	log.debug("Generated new pid '" + newPid + "'");

    	try {
        	removeOlderVersions(document);

        	removeAudit(document);
    		log.debug("Audit removed");
    		removeDatastreamVersions(document);
    		log.debug("Datastreamsversions removed");

    		// Replace PID
    		replacePid(document, templatepid, newPid);
    		log.debug("Pids replaced");

    		/** AAR Ext 1.0 **/
    		replaceOwner(document, ownerid);
    		log.debug("Ownerid replaced");
    
    		removeDCidentifier(document);
    		log.debug("DC identifier removed");

    		removeCreated(document);
    		log.debug("CREATED removed");

    		removeLastModified(document);
    		log.debug("Last Modified removed");

    		removeTemplateRelation(document);
    		log.debug("Template relation removed");
    		
    		try {
    			JCheckBox jcbOAIProvider = (JCheckBox) moGA.getWidget("jcbOAIProvider");
    			if (jcbOAIProvider.isSelected()) {
    			   addOAIItemID(document, newPid, RELSEXT_DESCRIPTION);
    			}
    		} catch (Exception ex) {	
    		}

    		removeDCtitle(document);
           log.debug("DC title removed");
    		
    		addDCMetadata(document, DC_DATASTREAM, moGA);
    		
    	} catch (XPathExpressionException e){
    		throw new FedoraIllegalContentException(
    				"Template object did not contain the correct structure",e);
    	}

        //fix a fedora-bug 
        String xmlSource = "";
        try {    		
        	DOMBuilder domBuilder = new DOMBuilder();
        	org.jdom.Document doc = domBuilder.build(document);
        	Format format = Format.getRawFormat();
        	format.setEncoding("UTF-8");
        	XMLOutputter outputter = new XMLOutputter(format);
        	xmlSource = outputter.outputString(doc).replace("<oai_dc:dc ", "<oai_dc:dc xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ");
    	} catch (Exception q) {
			log.error(q.getLocalizedMessage(),q);
			
    	}

        	//reingest the object
        String stream = Repository.ingestDocument(
                xmlSource,
                "Cloned from template '" +
                templatepid +
                "' by user '" +
                Repository.getUser() +
                "'");

    	try {
    		
    		byte[] buf = Repository.getDatastream(templatepid, "QUERY", (String) null);
        	Repository.modifyDatastream(newPid, "QUERY", "application/sparql-query", new String(buf).replaceAll("obj:self",newPid).getBytes("UTF-8"));
    	} catch (Exception e) {    
			log.error(e.getLocalizedMessage(),e);
    	}
    	return stream;
    }
    
    
    /** Private helper method for cloneTemplate. In a document, replaces the
     * mention of oldpid with newpid
     * @param doc the document to work on
     * @param oldpid the old pid
     * @param newpid the new pid
     * @throws FedoraIllegalContentException If there is a problem understanding
     * the document
     * @throws javax.xml.xpath.XPathExpressionException if there was 
     */
    private void replacePid(Document doc, String oldpid, String newpid)
            throws FedoraIllegalContentException, XPathExpressionException, XPathFactoryConfigurationException {

       log.trace("Entering replacepid");
        substituteAttribute(doc, FOXML_DIGITAL_OBJECT_PID,
                Repository.ensurePID(newpid));
        
        //** AAR Ext 1.0 **/
        replaceAttribute(doc,
                oldpid, Repository.ensurePID(newpid));
     
        substituteAttribute(doc, RELSEXT_ABOUT,Repository.ensureURI(newpid));

    }

    //** AAR Ext 1.0 **/
    private void replaceOwner (Document doc, String ownerid)
    throws FedoraIllegalContentException, XPathExpressionException, XPathFactoryConfigurationException {

    	substituteAttribute(doc, OBJECTPROPERTIES_OWNERID, ownerid);
    }

    
    /**
     * Utility method for removing all nodes from a query. Does not work
     * for attributes
     * @param doc the object
     * @param query the adress of the nodes
     * @throws XPathExpressionException if a xpath expression did not evaluate
     */
    private void removeExpathList(Document doc, String query)
    throws XPathExpressionException, XPathFactoryConfigurationException {
NodeList nodes = XpathUtils.
        xpathQuery(doc,
                   query);

for (int i=0;i<nodes.getLength();i++){
    Node node = nodes.item(i);
    node.getParentNode().removeChild(node);

}
}

    private void removeOlderVersions(Document doc)
    throws XPathExpressionException, XPathFactoryConfigurationException {
    		NodeList nodes = XpathUtils.
    					xpathQuery(doc,"/foxml:digitalObject/foxml:datastream");

    		for (int i=0;i<nodes.getLength();i++){
    			Node node = nodes.item(i);
    			NodeList children = XpathUtils.xpathQuery(node,"foxml:datastreamVersion");    			
        		for (int j=0;j<children.getLength();j++){    		
        			if (j+1<children.getLength()) {
            			Node child = children.item(j);
        				child.getParentNode().removeChild(child);
        			}
        		}    			
    		}
    }
    
    private void setExpathList(Document doc, String query, String text)
    throws XPathExpressionException, XPathFactoryConfigurationException {
NodeList nodes = XpathUtils.
        xpathQuery(doc,
                   query);

	for (int i=0;i<nodes.getLength();i++){
		Node node = nodes.item(i);
		node.setTextContent(text);
	}
}

    
    /**
     * Utility method for changing the value of an attribute
     * @param doc the object
     * @param query the location of the Attribute
     * @param value the new value
     * @throws XPathExpressionException if a xpath expression did not evaluate
     */
    private void substituteAttribute(Document doc, String query, String value)
            throws XPathExpressionException, XPathFactoryConfigurationException {
        NodeList nodes = XpathUtils.
                xpathQuery(doc,
                           query);
        for (int i=0;i<nodes.getLength();i++){
            Node node = nodes.item(i);
            node.setNodeValue(value);
        }
    }
    
    //** AAR Ext .0 **/
    private void replaceAttribute(Document doc, String oldpid, String newpid)
    	throws XPathExpressionException, XPathFactoryConfigurationException {
		NodeList nodes = XpathUtils.xpathQuery(doc, DATASTREAM_CONTENTLOCATION);
		for (int i=0;i<nodes.getLength();i++){
			Node node = nodes.item(i);
			String s = node.getTextContent();
			node.setNodeValue(s.replace(oldpid+"/",newpid+"/").replace("/repositories/"+oldpid,"/repositories/"+newpid));
		}
					
		NodeList nodeq = XpathUtils.xpathQuery(doc, DATASTREAM_GROUPR);
		for (int i=0;i<nodeq.getLength();i++){
			Node node = nodeq.item(i);
			String s = node.getTextContent();
			String[] p = oldpid.split(":");
			if (p.length > 1) oldpid=p[0]+"%3A"+p[1];
			String[] q = newpid.split(":");
			if (q.length > 1) newpid=q[0]+"%3A"+q[1];		
			node.setNodeValue(s.replace(oldpid,newpid));

//			node.setNodeValue(s.replace(oldpid+"/",newpid+"/").replace("/repositories/"+oldpid,"/repositories/"+newpid));
		}
}
   
    
    /**
     * Utility method for removing an attribute
     * @param doc the object
     * @param query the adress of the node element
     * @param attribute the name of the attribute
     * @throws XPathExpressionException if a xpath expression did not evaluate
     */
    private void removeAttribute(Document doc, String query, String attribute)
            throws XPathExpressionException, XPathFactoryConfigurationException {
        NodeList nodes;

        nodes = XpathUtils.xpathQuery(
                doc,
                query);

        for (int i=0;i<nodes.getLength();i++){
            Node node = nodes.item(i);

            NamedNodeMap attrs = node.getAttributes();

            if (attrs.getNamedItem(attribute) != null){
                attrs.removeNamedItem(attribute);
            }

        }
    }

    /**
     * Removes the DC identifier from the DC datastream
     * @param doc the object
     * @throws XPathExpressionException if a xpath expression did not evaluate
     */
    private void removeDCidentifier(Document doc)
    	throws  XPathExpressionException, XPathFactoryConfigurationException {
    	//Then remove the pid in dc identifier
    	removeExpathList(doc, DCIDENTIFIER);
    }
    private void removeDCtitle(Document doc)
    	throws  XPathExpressionException, XPathFactoryConfigurationException {
    	//Then remove the pid in dc identifier
    	removeExpathList(doc, DCTITLE);
    }



    /**
     * Removes all template relations
     * @param doc the object
     * @throws XPathExpressionException if a xpath expression did not evaluate
     */
    private void removeTemplateRelation(Document doc) throws
                                                      XPathExpressionException, XPathFactoryConfigurationException {
        // Remove template relation

        //TODO Constant for template relation
        removeExpathList(doc, ISTEMPLATEFOR);
         
    }

    /**
     * Removes the AUDIT datastream
     * @param doc the object
     * @throws XPathExpressionException if a xpath expression did not evaluate
     */
    private void removeAudit(Document doc) throws
                                           XPathExpressionException, XPathFactoryConfigurationException {

        removeExpathList(doc, DATASTREAM_AUDIT);

    }

    /**
     * Removes all datastream versions, except the newest
     * @param doc the object
     * @throws XPathExpressionException if a xpath expression did not evaluate
     */
    private void removeDatastreamVersions(Document doc) throws
                                                        XPathExpressionException, XPathFactoryConfigurationException {
        NodeList relationNodes;

        relationNodes = XpathUtils.xpathQuery(
                doc, DATASTREAM_NEWEST);

        Node node = relationNodes.item(0);
        Node datastreamnode = node.getParentNode();

        //Remove all of the datastream node children
        while (datastreamnode.getFirstChild() != null) {
            datastreamnode.removeChild(
                    datastreamnode.getFirstChild());
        }

        datastreamnode.appendChild(node);


    }

    /**
     * Removes the CREATED attribute on datastreamVersion and the createdDate objectProperty
     * @param doc the object
     * @throws XPathExpressionException if a xpath expression did not evaluate
     */
    private void removeCreated(Document doc) throws XPathExpressionException, XPathFactoryConfigurationException {
       log.trace("Entering removeCreated");
        removeAttribute(doc, DATASTREAM_CREATED,"CREATED");

        removeExpathList(doc, OBJECTPROPERTY_CREATED);


    }

    /**
     * Removes the lastModifiedDate objectDate
     * @param doc the object
     * @throws XPathExpressionException if a xpath expression did not evaluate
     */
    private void removeLastModified(Document doc) throws
                                                  XPathExpressionException, XPathFactoryConfigurationException {

        removeExpathList(doc, OBJECTPROPERTIES_LSTMODIFIED);

    }
    
    private void addOAIItemID(Document doc, String pid, String query)
    	throws XPathExpressionException, XPathFactoryConfigurationException {
    		NodeList nodes = XpathUtils.
    		xpathQuery(doc,
    				query);
    		for (int i=0;i<nodes.getLength();i++){
    			Node node = nodes.item(i);    			
    			Element item = doc.createElement("oai:itemID");
    			item.setAttribute("xmlns:oai", "http://www.openarchives.org/OAI/2.0/");
    			item.setTextContent(pid);
    			node.appendChild(item);
    		}
    }
    
    private void addDCMetadata( Document doc, String query, CDefaultGuiAdapter moGA )
   		throws XPathExpressionException , XPathFactoryConfigurationException{    	
    		NodeList nodes = XpathUtils.
    			xpathQuery(doc,
    				query);
		Node node = nodes.item(0);
    	try {
    		for (int i = 0; i <  org.emile.cirilo.Common.DCMI.length; i++)
    		{
    			StringTokenizer st = new StringTokenizer((String) moGA.getText("jtf" +  org.emile.cirilo.Common.DCMI[i]), "~");
    			if (st.hasMoreTokens()) {
    				while (st.hasMoreTokens()) {
    					String s = st.nextToken();
    	    			Element item = doc.createElement("dc:"+ org.emile.cirilo.Common.DCMI[i].toLowerCase());
    	    			item.setTextContent(s);
     	    			node.appendChild(item);
    				}
    			}
    		}
		} catch (Exception e) {	
		}
   	
    }

}
