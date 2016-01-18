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

package org.emile.cirilo.ecm.repository;

import fedora.client.FedoraClient;
import fedora.server.access.FedoraAPIA;
import fedora.server.management.FedoraAPIM;
import fedora.server.types.gen.DatastreamDef;
import fedora.server.types.gen.MIMETypedStream;
import fedora.server.types.gen.ObjectProfile;
import fedora.server.types.gen.RelationshipTuple;
import fedora.server.utilities.StreamUtility;

import org.emile.cirilo.ecm.exceptions.DatastreamNotFoundException;
import org.emile.cirilo.ecm.exceptions.FedoraConnectionException;
import org.emile.cirilo.ecm.exceptions.FedoraIllegalContentException;
import org.emile.cirilo.ecm.exceptions.ObjectIsWrongTypeException;
import org.emile.cirilo.ecm.exceptions.ObjectNotFoundException;
import org.emile.cirilo.ecm.utils.Constants;
import org.emile.cirilo.ecm.utils.DocumentUtils;
import org.emile.cirilo.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jrdf.graph.Node;
import org.trippi.TrippiException;
import org.trippi.TupleIterator;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.rpc.ServiceException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.*;
import java.io.*;

import org.emile.cirilo.gui.jtable.DefaultSortTableModel;





/**
 * This is a implementation of the Fedora connector, based on the old
 * FedoraClient This FedoraClient is really a wrapping of the soap api.
 */
public class FedoraSoapImpl
        implements FedoraConnector {

	private final String SYSTEM_DATASTREAMS ="|STYLESHEET|FO_STYLESHEET|QUERY|RDF|KML_TEMPLATE|DC_MAPPING|RDF_MAPPING|"+
	                                      "BIBTEX_MAPPING|RELS-EXT|RELS-INT|REPLACEMENT_RULESET|VOYANT|CONTEXTtoHTML|"+
										  "CONTEXTtoFO|TOMETS|TORDF|METS2JSON|XML2JSON|TOTEI|QR|HSSF_STYLESHEET|QUERYtoHSSF|QUERYtoHTML|TEItoHSSF|"+
										  "TEItoHTML|TEItoFO|BIBTEXtoHTML|BIBTEXtoFO|PAGE-1|PAGE-2|TEItoDC_MAPPING|RDF_MAPPING|SKOStoHTML|SKOStoFO|"+
										  "TEITOMETS|STYLESHEETS|BIBTEXtoHTML|BIBTEXtoFO|PAGE-1|PAGE-2|TEItoDC_MAPPING|RDF_MAPPING|SKOStoHTML|SKOStoFO|"+
										  "LIDOtoDC_MAPPING|OAItoDC_MAPPING|LIDOtoHTML|LIDOtoFO|LIDOtoRDF|DATAPROVIDERS|MODStoDC_MAPPING|MODStoBIBTEX_MAPPING|PROPERTIES|"+
										  "EDMtoHTML|EDMtoDC_MAPPING|RECORDtoEDM|STORYtoHTML|STORY2JSON|STORYtoDC_MAPPING|";
    private final String DISSEMINATOR = "|PID|METADATA|METHODS|";
	

    private static final Log LOG = LogFactory.getLog(FedoraSoapImpl.class);
    private FedoraUserToken token;

    //Do not get this directly, use the accessor
    private FedoraClient client;

    //noargs constructor, as required
    public FedoraSoapImpl() {
    }


    public void initialise(FedoraUserToken token) {
        this.token = token;
    }


    public boolean addRelation(String from, String relation, String to)
            throws ObjectNotFoundException, FedoraConnectionException,
                   FedoraIllegalContentException {
        from = Repository.ensurePID(from);
        to = Repository.ensureURI(to);
        if (!exists(from)) {
            throw new ObjectNotFoundException("The object '" + from + "' was not found in the repository");
        }
        try {
            return getAPIM().addRelationship(from, relation, to, false, null);
        } catch (RemoteException e) {

            throw new FedoraConnectionException(
                    "Something went wrong in the connection with fedora",
                    e);
        }
    }

    public boolean purgeRelation(String from, String relation, String to)
    throws ObjectNotFoundException, FedoraConnectionException,
           FedoraIllegalContentException {
    		from = Repository.ensurePID(from);
    		to = Repository.ensureURI(to);
    		if (!exists(from)) {
    			throw new ObjectNotFoundException("The object '" + from + "' was not found in the repository");
    		}
    		try {
    			return getAPIM().purgeRelationship(from, relation, to, false, null);
    		} catch (RemoteException e) {
    			throw new FedoraConnectionException(
    					"Something went wrong in the connection with fedora",
    					e);
    		}
    }
    
    public boolean addLiteralRelation(String from,
                                      String relation,
                                      String value,
                                      String datatype)
            throws ObjectNotFoundException, FedoraConnectionException,
                   FedoraIllegalContentException {
        from = Repository.ensurePID(from);

        if (!exists(from)) {
            throw new ObjectNotFoundException("The object '" + from + "' was not found in the repository");
        }
        try {
            return getAPIM().addRelationship(from,
                                             relation,
                                             value,
                                             true,
                                             datatype);
        } catch (RemoteException e) {
            throw new FedoraConnectionException(
                    "Something went wrong in the connection with fedora",
                    e);
        }
    }

    public List<Relation> getRelations(String pid)
            throws ObjectNotFoundException, FedoraConnectionException,
                   FedoraIllegalContentException {
        return getRelations(pid, null);
    }

    public List<Relation> getRelations(String pid, String relation)
            throws ObjectNotFoundException, FedoraConnectionException,
                   FedoraIllegalContentException {
        pid = Repository.ensurePID(pid);
        if (!exists(pid)) {
            throw new ObjectNotFoundException("The object '" + pid + "' was not found in the repository");
        }
        try {
            RelationshipTuple[] relations = getAPIM().getRelationships(pid,
                                                                       relation);
            List<Relation> result = new ArrayList<Relation>();
            if (relations != null) {

                for (RelationshipTuple rel : relations) {
                    result.add(toRelation(rel));
                }
                
            }
            return result;
        } catch (RemoteException e) {
            throw new FedoraConnectionException(
                    "Something failed in the communication with Fedora",
                    e);
        }

    }
    
    private Relation toRelation(RelationshipTuple rel) {
        return new Relation(rel.getSubject(),
                            rel.getObject(),
                            rel.getPredicate());
    }

    public PidList getContentModels(String pid)
            throws ObjectNotFoundException, FedoraConnectionException,
                   FedoraIllegalContentException {

        pid = Repository.ensurePID(pid);
        if (!exists(pid)) {
            throw new ObjectNotFoundException("The object '" + pid + "' was not found in the repository");
        }

        ObjectProfile profile;
        try {
            profile = getAPIA().getObjectProfile(pid, null);
        } catch (RemoteException e) {
            throw new FedoraConnectionException(
                    "Failed in communication with Fedora",
                    e);
        }

        String[] models = profile.getObjModels();
        PidList localmodels = new PidList(Arrays.asList(models));
        return getInheritedContentModelsBreadthFirst(localmodels);
    }


    private PidList getInheritedContentModelsBreadthFirst(PidList contentmodels)
            throws ObjectNotFoundException, FedoraIllegalContentException,
                   FedoraConnectionException {


        /*
        bfs (Graph G) {
	        all vertices of G are first painted white

	        the graph root is painted gray and put in a queue

	        while the queue is not empty {
	            a vertex u is removed from the queue

	            for all white successors v of u {
	                v is painted gray
		            v is added to the queue
	            }

	            u is painted black
	        }
        }
        */

        //all vertices of G are first painted white
        //all content models are white if not in one of the sets grey or black
        Set<String> grey = new HashSet<String>();
        PidList black = new PidList();
        Queue<String> queue = new LinkedList<String>();

        //the graph root is painted gray and put in a queue
        for (String startingcontentmodel : contentmodels) {
            queue.add(startingcontentmodel);
            grey.add(startingcontentmodel);
        }

        //while the queue is not empty {
        while (queue.size() > 0) {
            //a vertex u is removed from the queue
            String u = queue.poll();

            //    for all white successors v of u {
            List<String> successor_of_u = getAncestors(u);
            for (String v : successor_of_u) {
                if (grey.contains(v) || black.contains(v)) {
                    continue;
                }

                //v is painted gray
                grey.add(v);
                //v is added to the queue
                queue.add(v);
            }
            //u is painted black
            black.add(u);
        }
        return black;
    }

    private List<String> getAncestors(String s)
            throws FedoraIllegalContentException, FedoraConnectionException {
        PidList temp = new PidList();

        List<Relation> ancestors = null;
        try {
            ancestors = getRelations(s, Constants.RELATION_EXTENDS_MODEL);
        } catch (ObjectNotFoundException e) {
            //Content model does not exist, but that is not a problem. It just
            //does not have ancestors
            return temp;
        }
        for (Relation ancestor : ancestors) {
            temp.add(ancestor.getTo());
        }
         return temp;
     }

   
    
    /**
     * @param cmpid the content model pid
     * @return an empty list
     */
    public PidList getInheritingContentModels(String cmpid)
            throws FedoraConnectionException, ObjectNotFoundException,
                   ObjectIsWrongTypeException, FedoraIllegalContentException {
        cmpid = Repository.ensureURI(cmpid);
        if (!exists(cmpid)) {
            throw new ObjectNotFoundException("Object '" + cmpid + "' does not exist in the repository");
        }
        if (!isContentModel(cmpid)) {
            throw new ObjectIsWrongTypeException("Object '" + cmpid + "' is not a content model");
        }

        PidList descendants = query("select $object \n" + "from <#ri>\n" + "where \n" + "walk(\n" + "$object <" + Constants.RELATION_EXTENDS_MODEL + "> <" + cmpid + ">\n" + "and\n" + "$object <" + Constants.RELATION_EXTENDS_MODEL + "> $temp\n" + ");");
        return descendants;

    }

    public PidList getTemplates(String ownerid, boolean sysop)
    throws FedoraConnectionException, FedoraIllegalContentException {

    	PidList descendants = query ("select $object $title \n" + "from <#ri>\n" + "where \n" + " $object <http://ecm.sourceforge.net/relations/0/2/#isTemplateFor> $dummy \n" +"and $object <dc:title> $title \n"+
    			"and $object <info:fedora/fedora-system:def/model#ownerId> $user \n"+
    			(!sysop ? "and ($user <mulgara:is> '"+ownerid +"' or $user <mulgara:is> 'public') \n" : "")+		
    	        "order by $title\n");    	
    	return descendants;
//    	"and $object <info:fedora/fedora-system:def/model#hasModel> $model \n"+

}

    
    public PidList getContainers(String ownerid, boolean sysop)
    throws FedoraConnectionException, FedoraIllegalContentException {

    	PidList descendants = query ("select $object $title \n" + "from <#ri>\n" + "where \n" + " $object <dc:title> $title \n"+
    			"and $object <info:fedora/fedora-system:def/model#ownerId> $user \n"+
    			"and ( $object <fedora-model:hasModel> <info:fedora/cirilo:Context.ContentModel> or $object <fedora-model:hasModel> <info:fedora/cm:Context>)\n  "+
    			"minus  $object <http://ecm.sourceforge.net/relations/0/2/#isTemplateFor> $d\n "+
    			(!sysop ? "and ($user <mulgara:is> '"+ownerid +"' or $user <mulgara:is> 'public') \n" : "")+		
    	        "order by $title\n");
    	
    	return descendants;

}
    
    public PidList getUsers()
    throws FedoraConnectionException, FedoraIllegalContentException {

    	PidList descendants = query ("select $user \n" + "from <#ri>\n" + "where \n" + " $object <info:fedora/fedora-system:def/model#ownerId> $user order by $user");
    	return descendants;
    }

    public String[] getObjectProfile(String pid)
    throws FedoraConnectionException, FedoraIllegalContentException {

    	String s= null;;
    	
        List<String> us = query ("select $owner $label $state from <#ri> where  <info:fedora/"+pid+"> <info:fedora/fedora-system:def/model#ownerId> $owner  and  <info:fedora/"+pid+"> <http://purl.org/dc/elements/1.1/title> $label  and <info:fedora/"+pid+"> <info:fedora/fedora-system:def/model#state> $state");             
        Iterator jt = us.iterator();
        if (jt.hasNext()) {
       	   s = (String)jt.next();
        }
 	
    	return s.split(Common.SEPERATOR);
    }

    public PidList getInheritedContentModels(String cmpid)
            throws FedoraConnectionException, ObjectNotFoundException,
                   ObjectIsWrongTypeException, FedoraIllegalContentException {
        cmpid = Repository.ensurePID(cmpid);
        return getInheritedContentModelsBreadthFirst(new PidList(cmpid));
    }


    public DefaultSortTableModel listDatastreams(String pid, boolean metadata)
            throws FedoraConnectionException, ObjectNotFoundException,
                   FedoraIllegalContentException {
        pid = Repository.ensurePID(pid);
        if (!exists(pid)) {
            throw new ObjectNotFoundException("The object '" + pid + "' was not found in the repository");
        }
        
        try {
            DatastreamDef[] datastreams = getAPIA().listDatastreams(pid, null);            
		   	Vector data = new Vector();
            for (DatastreamDef def : datastreams) {
    			Vector row = new Vector();
    			String id =def.getID();
    			fedora.server.types.gen.Datastream ds = getAPIM().getDatastream(pid, id, "");
    			if (  ((metadata && SYSTEM_DATASTREAMS.contains("|"+id+"|")) || !metadata && !SYSTEM_DATASTREAMS.contains("|"+id+"|")) && !DISSEMINATOR.contains("|"+id+"|") ) {
    				row.add(def.getID());
    				row.add((String)ds.getLabel());
    				row.add((String)ds.getMIMEType());
       				row.add(ds.getControlGroup().toString());
       			 	row.add((String)ds.getCreateDate());
    				row.add((String)ds.getLocation());
    				data.add(row);
    			}
            }
            Vector names = new Vector();
		    names.add("ID");
		    names.add("Label");
		    names.add("Mimetype");		    
		    names.add("Group");
		    names.add("Last update");		    
		    names.add("Location");		    
		    DefaultSortTableModel dm = new DefaultSortTableModel(data,names);
            return dm;
        } catch (RemoteException e) {
            throw new FedoraConnectionException("Something failed in the " + "communication with Fedora",
                                                e);
        }
    }

    public String getUsername() {
        return token.getUsername();
     }

    public String ingestDocument(Document document, String logmessage)
            throws FedoraConnectionException, FedoraIllegalContentException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            DocumentUtils.DOCUMENT_TRANSFORMER.transform(new DOMSource(document),
                                                         new StreamResult(
                                                                 byteArrayOutputStream));
        } catch (TransformerException e) {
            throw new FedoraIllegalContentException("The new object could not" + "be transformed to a stream",
                                                    e);
        }

        //TODO check the document for all things that make the Fedora system fail
        try {
            return getAPIM().ingest(byteArrayOutputStream.toByteArray(),
                                    FedoraClient.FOXML1_1.uri,
                                    logmessage);
        } catch (RemoteException e) {
            throw new FedoraConnectionException(
                    "The object could not be ingested",
                    e);
        }
    }

    public String ingestDocument(String document, String logmessage)
            throws FedoraConnectionException, FedoraIllegalContentException {

       try {    	
        byte[]  byteArray = new String(document).getBytes("UTF8"); 
        //TODO check the document for all things that make the Fedora system fail
        
        try {
            return getAPIM().ingest(byteArray,            		             
                                    FedoraClient.FOXML1_1.uri,
                                    logmessage);
        } catch (RemoteException e) {
            throw new FedoraConnectionException(
                    "The object could not be ingested",
                    e);
        }
       } catch (Exception q) {
    	   return null;
       }
    }

    public String ingestDocument(Document document, String format, String logmessage)
            throws FedoraConnectionException, FedoraIllegalContentException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            DocumentUtils.DOCUMENT_TRANSFORMER.transform(new DOMSource(document),
                                                         new StreamResult(
                                                                 byteArrayOutputStream));
        } catch (TransformerException e) {
            throw new FedoraIllegalContentException("The new object could not" + "be transformed to a stream",
                                                    e);
        }

        //TODO check the document for all things that make the Fedora system fail
        try {
            return getAPIM().ingest(byteArrayOutputStream.toByteArray(),
                                   	format,
                                    logmessage);
        } catch (RemoteException e) {
            throw new FedoraConnectionException(
                    "The object could not be ingested",
                    e);
        }
    }

    
    /**
     * Get object XML from Fedora, and return it as a DOM document
     *
     * @param pid The PID of the document to retrieve. May be represented as a
     *            PID, or as a Fedora URI.
     * @return The object parsed in a DOM.
     */
    public Document getObjectXml(String pid)
            throws FedoraConnectionException, FedoraIllegalContentException,
                   ObjectNotFoundException {
        pid = Repository.ensurePID(pid);
        if (!exists(pid)) {
            throw new ObjectNotFoundException("The object '" + pid + "' was not found in the repository");
        }


        byte[] objectXML;
        try {
        	objectXML = getAPIM().export(pid, "info:fedora/fedora-system:FOXML-1.1","archive");

        } catch (RemoteException e) {
            throw new FedoraConnectionException("Error getting XML for '" + pid + "' from Fedora",
                                                e);
        }


        try {
           return DocumentUtils.DOCUMENT_BUILDER.parse(new ByteArrayInputStream(objectXML));
        } catch (Exception e) {
            throw new FedoraIllegalContentException("Error parsing XML for '" + pid + "' from Fedora",
                                                    e);
        }
    }

    public String get2ObjectXml(String pid)
            throws FedoraConnectionException, FedoraIllegalContentException,
                   ObjectNotFoundException {
        pid = Repository.ensurePID(pid);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
    	try {
        	fedora.client.utility.export.AutoExporter.export(getAPIA(),
        			getAPIM(),
                    pid,
                    fedora.common.Constants.FOXML1_1.uri,
                    "archive", os
                   );
        	            

        } catch (Exception e) {
            throw new FedoraConnectionException("Error getting XML for '" + pid + "' from Fedora",
                                                e);
        }

        try {
        	byte [] s = os.toByteArray();
        	os.close();
        	return new String (s, "UTF-8");
        } catch (Exception e) {
            throw new FedoraIllegalContentException("Error parsing XML for '" + pid + "' from Fedora",
                                                    e);
        }
    }

   
    /**
     * Retrieve a datastream from Fedora, and parse it as document.
     *
     * @param pid        The ID of the object to get the datastream from.
     * @param datastream The ID of the datastream.
     * @return The datastream parsed as a DOM document.
     */
    public Document getDatastream(String pid, String datastream)
            throws DatastreamNotFoundException, FedoraConnectionException,
                   FedoraIllegalContentException, ObjectNotFoundException {

        pid = Repository.ensurePID(pid);
        if (!exists(pid)) {
            throw new ObjectNotFoundException("The object '" + pid + "' was not found in the repository");
        }

        MIMETypedStream dsCompositeDatastream;
        byte[] buf;
        try {
            dsCompositeDatastream = getAPIA()

                    .getDatastreamDissemination(pid, datastream, null);
            buf = dsCompositeDatastream.getStream();
        } catch (RemoteException e) {
            if (e.getMessage().contains(
                    "fedora.server.errors.DatastreamNotFoundException")) {

                throw new DatastreamNotFoundException(
                        "Error getting datastream'" + datastream + "' from '" + pid + "'",
                        e);

            }
            throw new FedoraConnectionException("Error getting datastream'" + datastream + "' from '" + pid + "'",
                                                e);
        }

        Document dsCompositeXml;
        try {
            dsCompositeXml = DocumentUtils.DOCUMENT_BUILDER.parse(new ByteArrayInputStream(
                    buf));
        } catch (SAXException e) {
            throw new FedoraIllegalContentException("Error parsing datastream '" + datastream + "'  from '" + pid + "' as XML",
                                                    e);
        } catch (IOException e) {
            throw new Error("IOTrouble reading from byte array stream, " + "this should never happen",
                            e);
        }
        return dsCompositeXml;
    }

    public byte[] getDatastream(String pid, String datastream, String dummy)
    throws DatastreamNotFoundException, FedoraConnectionException,
           FedoraIllegalContentException, ObjectNotFoundException {

    	pid = Repository.ensurePID(pid);
    	if (!exists(pid)) {
    		throw new ObjectNotFoundException("The object '" + pid + "' was not found in the repository");
    	}

    	MIMETypedStream dsCompositeDatastream;
    	byte[] buf;
    	try {
    		dsCompositeDatastream = getAPIA().getDatastreamDissemination(pid, datastream, null);
    		buf = dsCompositeDatastream.getStream();
    	} catch (RemoteException e) {
    		if (e.getMessage().contains(
            "fedora.server.errors.DatastreamNotFoundException")) {

    			throw new DatastreamNotFoundException(
    					"Error getting datastream'" + datastream + "' from '" + pid + "'",
    					e);

    		}
    		throw new FedoraConnectionException("Error getting datastream'" + datastream + "' from '" + pid + "'",
                                        e);
    	}

    	return buf;
    }

    public Document getDatastream(String pid, String datastream, Integer dummy)
            throws DatastreamNotFoundException, FedoraConnectionException,
                   FedoraIllegalContentException, ObjectNotFoundException, Exception {

        pid = Repository.ensurePID(pid);
        if (!exists(pid)) {
            throw new ObjectNotFoundException("The object '" + pid + "' was not found in the repository");
        }

        MIMETypedStream dsCompositeDatastream;
        byte[] buf;
        try {
            dsCompositeDatastream = getAPIA().getDatastreamDissemination(pid, datastream, null);
            buf = dsCompositeDatastream.getStream();
        } catch (RemoteException e) {
            if (e.getMessage().contains(
                    "fedora.server.errors.DatastreamNotFoundException")) {

                throw new DatastreamNotFoundException(
                        "Error getting datastream'" + datastream + "' from '" + pid + "'",
                        e);

            }
            throw new FedoraConnectionException("Error getting datastream'" + datastream + "' from '" + pid + "'",
                                                e);
        }

        Document dsCompositeXml;
        try {
        	URL url = new URL(new String(buf));
            dsCompositeXml = DocumentUtils.DOCUMENT_BUILDER.parse(url.openStream());
        } catch (SAXException e) {
            throw new FedoraIllegalContentException("Error parsing datastream '" + datastream + "'  from '" + pid + "' as XML",
                                                    e);
        } catch (IOException e) {
            throw new Error("IOTrouble reading from byte array stream, " + "this should never happen",
                            e);
        }
        return dsCompositeXml;
    }

    
    
    public String modifyDatastream(String pid, String datastream, byte[] stream)
    throws DatastreamNotFoundException, FedoraConnectionException,
           FedoraIllegalContentException, ObjectNotFoundException {

    	pid = Repository.ensurePID(pid);

    	try {
    		File temp = File.createTempFile("tmp","bak");
    		FileOutputStream fos = new FileOutputStream(temp);
    		fos.write(stream);
    		fos.close();
    		String uploadURL = getFedoraClient().uploadFile(temp);
    		temp.delete();
    		return getAPIM().modifyDatastreamByReference(pid, datastream, null, null, "text/plain", null, uploadURL, "DISABLED", "none", null, false);
    	} catch (RemoteException e) {
    		e.printStackTrace();
            throw new FedoraConnectionException("Error ingesting datastream'" + datastream + "' from '" + pid + "'",
                                        e);
    	} catch (IOException e) {
    		e.printStackTrace();
            throw new FedoraConnectionException("Error ingesting datastream'" + datastream + "' from '" + pid + "'",
                    e);

    	}
    }

    public String modifyDatastream(String pid, String datastream, String mimetype, byte[] stream)
    	    throws DatastreamNotFoundException, FedoraConnectionException,
    	           FedoraIllegalContentException, ObjectNotFoundException {

    	    	pid = Repository.ensurePID(pid);

    	    	try {
    	    		File temp = File.createTempFile("tmp","bak");
    	    		FileOutputStream fos = new FileOutputStream(temp);
    	    		fos.write(stream);
    	    		fos.close();
    	    		String uploadURL = getFedoraClient().uploadFile(temp);
    	    		temp.delete();
    	    		return getAPIM().modifyDatastreamByReference(pid, datastream, null, null, mimetype, null, uploadURL, "DISABLED", "none", null, false);
    	    	} catch (RemoteException e) {
    	    		e.printStackTrace();
    	            throw new FedoraConnectionException("Error ingesting datastream'" + datastream + "' from '" + pid + "'",
    	                                        e);
    	    	} catch (IOException e) {
    	    		e.printStackTrace();
    	            throw new FedoraConnectionException("Error ingesting datastream'" + datastream + "' from '" + pid + "'",
    	                    e);

    	    	}
    	    }
    
    public String modifyObject(String pid, String state, String label, String owner)
    throws DatastreamNotFoundException, FedoraConnectionException,
           FedoraIllegalContentException, ObjectNotFoundException {

    	pid = Repository.ensurePID(pid);
    	
    	try {
    		return  getAPIM().modifyObject(pid, state, label, owner, "");

  	} catch (RemoteException e) {
            throw new FedoraConnectionException("Error modifying object '" + pid + "'",
                                        e);
    	} catch (IOException e) {
            throw new FedoraConnectionException("Error modifying object '" + pid + "'",
                    e);

    	}
    }

    

    public String addDatastream(String pid, String datastream, String title, String type, String mimetype, File stream )
    throws DatastreamNotFoundException, FedoraConnectionException,
           FedoraIllegalContentException, ObjectNotFoundException {

    	pid = Repository.ensurePID(pid);
    	
    	try {
    		String uploadURL = getFedoraClient().uploadFile(stream);
    		return getAPIM().addDatastream(pid, datastream, new String[0], title, false, mimetype, null, uploadURL, type , "A", "DISABLED", "none", null);
  	} catch (RemoteException e) {
            throw new FedoraConnectionException("Error ingesting datastream'" + datastream + "' from '" + pid + "'",
                                        e);
    	} catch (IOException e) {
            throw new FedoraConnectionException("Error ingesting datastream'" + datastream + "' from '" + pid + "'",
                    e);

    	}
    }


    
    public String addDatastream(String pid, String datastream, String title, String mimetype, String location )
    throws DatastreamNotFoundException, FedoraConnectionException,
           FedoraIllegalContentException, ObjectNotFoundException {

    	pid = Repository.ensurePID(pid);
    	
    	try {
    		return getAPIM().addDatastream(pid, datastream, new String[0], title, false, mimetype, null, location, "R", "A", "DISABLED", "none",  null);
  	} catch (RemoteException e) {
            throw new FedoraConnectionException("Error ingesting datastream'" + datastream + "' from '" + pid + "'",
                                        e);
    	} catch (IOException e) {
            throw new FedoraConnectionException("Error ingesting datastream'" + datastream + "' from '" + pid + "'",
                    e);

    	}
    }
    
    
    public String modifyDatastream(String pid, String datastream, String mimetype, String controlgroup, File fp )
    throws DatastreamNotFoundException, FedoraConnectionException,
           FedoraIllegalContentException, ObjectNotFoundException {

    	try {
    		if (controlgroup.equals("X") ) {
    			pid = Repository.ensurePID(pid);
    			InputStream in = (InputStream) new FileInputStream( fp );
    			ByteArrayOutputStream out = new ByteArrayOutputStream();
    			StreamUtility.pipeStream( in, out, 4096 );
    			return getAPIM().modifyDatastreamByValue(pid, datastream, null, null, mimetype, null, out.toByteArray() , "DISABLED", "none", null, false);
    		}	
       		if (controlgroup.equals("M") ) {
    	  		String uploadURL = getFedoraClient().uploadFile(fp);
    	  	    return getAPIM().modifyDatastreamByReference(pid, datastream, null, null, mimetype, null, uploadURL , "DISABLED", "none", null, false);
    		}
       		return "Error ingesting datastream'" + datastream + "' from '" + pid + "'";     		
    	} catch (RemoteException e) {
    		e.printStackTrace();
            throw new FedoraConnectionException("Error ingesting datastream'" + datastream + "' from '" + pid + "'",
                                        e);
    	} catch (IOException e) {
    		e.printStackTrace();
            throw new FedoraConnectionException("Error ingesting datastream'" + datastream + "' from '" + pid + "'",
                    e);

    	}
   		
    }

    public String modifyDatastream(String pid, String datastream, String mimetype, String controlgroup, String location )
    throws DatastreamNotFoundException, FedoraConnectionException,
           FedoraIllegalContentException, ObjectNotFoundException {

    	try {
     	  	  return getAPIM().modifyDatastreamByReference(pid, datastream, null, null, mimetype, null, location , "DISABLED", "none", null, false);
    	} catch (RemoteException e) {
    		e.printStackTrace();
            throw new FedoraConnectionException("Error ingesting datastream'" + datastream + "' from '" + pid + "'",
                                        e);
    	} catch (IOException e) {
    		e.printStackTrace();
            throw new FedoraConnectionException("Error ingesting datastream'" + datastream + "' from '" + pid + "'",
                    e);
    	}   		
    }
    
    public String modifyDatastreamByValue(String pid, String datastream, String mimetype, String stream)
    throws DatastreamNotFoundException, FedoraConnectionException,
           FedoraIllegalContentException, ObjectNotFoundException {

    	pid = Repository.ensurePID(pid);

    	try {
    		return getAPIM().modifyDatastreamByValue(pid, datastream, null, null, mimetype, null, stream.getBytes("UTF-8"), "DISABLED", "none", null, false);
    	} catch (RemoteException e) {
    		e.printStackTrace();
            throw new FedoraConnectionException("Error ingesting datastream'" + datastream + "' from '" + pid + "'",
                                        e);
    	} catch (IOException e) {
    		e.printStackTrace();
            throw new FedoraConnectionException("Error ingesting datastream'" + datastream + "' from '" + pid + "'",
                    e);

    	}
    }

    
    

    public boolean exists(String pid)
    throws FedoraIllegalContentException, FedoraConnectionException {
    	return hasContentModel(pid, Constants.FEDORA_OBJECT_3_0);
    }

    public boolean exists(String pid, String dsid)
    	    throws IllegalStateException, FedoraIllegalContentException, FedoraConnectionException {
    	    	try {    	    	
    		    	PidList list = query ("select $p from <#ri> where <info:fedora/"+pid+"> $p <info:fedora/"+pid+"/"+dsid+">");	  
    		    	return (list.size() > 0);
   	    	} catch (Exception e) {}
    	   		return false;
    	    }
   

    
    public boolean isDataObject(String pid)
            throws FedoraIllegalContentException, FedoraConnectionException {
        boolean cm = hasContentModel(pid, Constants.CONTENT_MODEL_3_0);
        boolean sdef = hasContentModel(pid, Constants.SERVICE_DEFINITION_3_0);
        boolean sdep = hasContentModel(pid, Constants.SERVICE_DEPLOYMENT_3_0);
        return !cm && !sdef && !sdep;
    }


    public boolean isTemplate(String pid)
            throws ObjectNotFoundException, FedoraConnectionException,
                   FedoraIllegalContentException {

        List<Relation> templaterels = getRelations(pid, Constants.TEMPLATE_REL);
        return templaterels.size() > 0;
    }

    public boolean isContentModel(String pid)
            throws FedoraIllegalContentException, FedoraConnectionException {
        return hasContentModel(pid, Constants.CONTENT_MODEL_3_0);
    }

    public boolean hasContentModel(String pid, String cmpid)
            throws FedoraIllegalContentException, FedoraConnectionException {
        PidList contentmodels = query("select $object\n" + "from <#ri>\n" + "where\n <" + Repository.ensureURI(
                pid) + "> <" + Constants.HAS_MODEL + "> " + "$object\n");
        return contentmodels.contains(Repository.ensurePID(cmpid));

    }


    public boolean purgeObject(String pid)
        	throws FedoraConnectionException, FedoraIllegalContentException {
            try {
            	 return !getAPIM().purgeObject(pid ,"", true).isEmpty();
            } catch (Exception e) {
            	 return false;
            }
     }

    public boolean purgeDatastream(String pid, String dsid)
        	throws FedoraConnectionException, FedoraIllegalContentException {
            try {
            	 getAPIM().purgeDatastream(pid, dsid, null, null, null, true);
            	 return true;
            } catch (Exception e) {
            	 return false;
            }
     }
    
    public PidList query(String query)
            throws FedoraConnectionException, FedoraIllegalContentException {


        PidList pidlist = new PidList();

        LOG.trace("Entering query with this string \n'" + query + "'\n");
        Map<String, String> map = new HashMap<String, String>();
        map.put("lang", "itql");
        map.put("query", query);

        final TupleIterator tupleIterator;
        try {
            tupleIterator = getFedoraClient().getTuples(map);
        } catch (IOException e) {
        	e.printStackTrace();
            throw new FedoraConnectionException(            	
                    "IO exception when communication with fedora",
                    e);
        }

        try {
            while (tupleIterator.hasNext()) {
                final Map<String, Node> tuple = tupleIterator.next();
                String subject = "";
                try {
                	subject = Repository.ensurePID(tuple.get("object").toString());
                } catch (Exception e) {}                	
                try {
                    String s = new String(tuple.get("title").toString().getBytes("UTF-8"),"UTF-8");
                	subject = s.replaceAll("\"", "") +" "+Common.SEPERATOR+" "+subject;
                } catch (Exception e) {}
                try {
                    String s = tuple.get("user").toString();
                	subject = s.replaceAll("\"", "");
                } catch (Exception e) {}
                try {
                    String s = tuple.get("owner").toString();
                	subject = s;
                } catch (Exception e) {}
                try {
                    String s = new String(tuple.get("label").toString().getBytes("UTF-8"),"UTF-8");
                	subject += Common.SEPERATOR+s;
                } catch (Exception e) {}
                try {
                    String s = tuple.get("state").toString();
                	subject += Common.SEPERATOR+s;
                } catch (Exception e) {}
       			try {
       				String s = tuple.get("hdl").toString();       				
       				subject = (s.startsWith("\"hdl:")? s :"").replaceAll("\"", "").substring(4);
    	         } catch (Exception e) {}    
               
                pidlist.add(subject);
            }
        } catch (TrippiException e) {
            throw new FedoraIllegalContentException(
                    "Incorrect data was returned",
                    e);
        }

        return pidlist;
    }
    
    public DefaultSortTableModel getObjects(String query, String[]columnNames)
    throws FedoraConnectionException, FedoraIllegalContentException {

 //   	LOG.trace("Entering query with this string \n'" + query + "'\n");
    	Map<String, String> map = new HashMap<String, String>();
    	map.put("lang", "sparql");
    	map.put("query", query);

    	final TupleIterator tupleIterator;
    	try {
    		tupleIterator = getFedoraClient().getTuples(map);
    	} catch (IOException e) {
    		e.printStackTrace();
    		throw new FedoraConnectionException(            	
    				"IO exception when communication with fedora",
    				e);
    	}

    	Vector data = new Vector();

    	try {
    		while (tupleIterator.hasNext()) {
    			final Map<String, Node> tuple = tupleIterator.next();
    			Vector row = new Vector();
    			try {
    				row.add(Repository.ensurePID(tuple.get("pid").toString().replace("\"", "")));
    	         } catch (Exception e) {}                	
      			try {
      				row.add(new String(tuple.get("title").stringValue().replace("\"", "").getBytes("UTF-8"),"UTF-8"));
    	         } catch (Exception e) {}                	
       			try {
                    row.add(new String(tuple.get("model").toString().getBytes("UTF-8"),"UTF-8").substring(12));
    	         } catch (Exception e) {}                	
       			try {
       				String s =tuple.get("lastModifiedDate").toString().replace("\"", "");
       				int i = s.indexOf("^");
       				row.add(s.substring(0,i-1));    				
    	         } catch (Exception e) {}    
       			try {
       				row.add(tuple.get("user").toString().replace("\"", ""));    				
    	         } catch (Exception e) {}
       			try {
       				String s = tuple.get("hdl").toString();
       				row.add((s.startsWith("\"hdl:") ? s :"").replaceAll("\"", "").substring(4));    				
    	         } catch (Exception e) {}    
       			
    	         data.add(row);
    		}
    	} catch (TrippiException e) {
    		throw new FedoraIllegalContentException(
    				"Incorrect data was returned",
    				e);
    	}
        Vector names = new Vector();
        for (int i=0;i<columnNames.length;i++) {
        	names.add(columnNames[i]);
        }
    	DefaultSortTableModel dm = new DefaultSortTableModel(data,names);

    	return dm;
    }


    public java.util.ArrayList<String> getPidList(String query)
    throws FedoraConnectionException, FedoraIllegalContentException {

    	LOG.trace("Entering query with this string \n'" + query + "'\n");
    	Map<String, String> map = new HashMap<String, String>();
    	map.put("lang", "itql");
    	map.put("query", "select $pid from <#ri> where $object <dc:identifier> $pid");

    	final TupleIterator tupleIterator;
    	try {
    		tupleIterator = getFedoraClient().getTuples(map);
    	} catch (IOException e) {
    		e.printStackTrace();
    		throw new FedoraConnectionException(            	
    				"IO exception when communication with fedora",
    				e);
    	}

    	java.util.ArrayList<String> data = new java.util.ArrayList();

    	try {
    		while (tupleIterator.hasNext()) {
    			final Map<String, Node> tuple = tupleIterator.next();
    			try {
    				String p = (String) Repository.ensurePID(tuple.get("pid").toString().replace("\"", ""));
    				if (p.startsWith(query)) data.add(p);
    	         } catch (Exception e) {}                	
    		}
    	} catch (TrippiException e) {
    		throw new FedoraIllegalContentException(
    				"Incorrect data was returned",
    				e);
    	}

    	return data;
    }


    public java.util.ArrayList<String> getTriples(String query)
    throws FedoraConnectionException, FedoraIllegalContentException {

    	LOG.trace("Entering query with this string \n'" + query + "'\n");
    	Map<String, String> map = new HashMap<String, String>();
    	map.put("lang", "sparql");
    	map.put("query", query);

    	final TupleIterator tupleIterator;
    	try {
    		tupleIterator = getFedoraClient().getTuples(map);
    	} catch (IOException e) {
    		e.printStackTrace();
    		throw new FedoraConnectionException(            	
    				"IO exception when communication with fedora",
    				e);
    	}

    	java.util.ArrayList<String> data = new java.util.ArrayList();

    	try {
    		while (tupleIterator.hasNext()) {
    			final Map<String, Node> tuple = tupleIterator.next();
    			try {
    				String p = (String) Repository.ensurePID(tuple.get("subject").toString()+","+tuple.get("predicate").toString()+","+tuple.get("object").toString());
    				data.add(p);
    	         } catch (Exception e) {}                	
    		}
    	} catch (TrippiException e) {
    		throw new FedoraIllegalContentException(
    				"Incorrect data was returned",
    				e);
    	}

    	return data;
    }


    
    public String getNextPid () 
    	throws FedoraConnectionException {
    	String pid = "o:11.1";  
    	try {
    		pid = this.client.getAPIM().getNextPID(null, null)[0]; 
    	} catch (Exception e) {
    		throw new FedoraConnectionException("Error connecting to Fedora",
    	e);
    	}
    	return pid;

    }

    /**
     * Gets a Fedora client. If this is the first connect, or if the client has
     * been reset, the client is initialised, and connection to Fedora
     * initialised. Otherwise, the existing client is reused.
     *
     * @return The fedora client instance.
     * @throws net.sourceforge.ecm.exceptions.FedoraConnectionException
     *          on trouble connectng to Fedora.
     */
    private synchronized FedoraClient getFedoraClient()
            throws FedoraConnectionException {

        if (token == null) {
            throw new IllegalStateException("Connector not initialised");
        }
        try {
            FedoraClient client = this.client;
            if (client == null) {

                client = new FedoraClient(token.getServerurl(),
                                          token.getUsername(),
                                          token.getPassword());
                this.client = client;
            }           
           return client;
        } catch (MalformedURLException e) {
            throw new FedoraConnectionException("Error connecting to Fedora",
                                                e);
       }
    }

    /**
     * Get the API-M interface to Fedora.
     *
     * @return The API-M interface to Fedora.
     * @throws net.sourceforge.ecm.exceptions.FedoraConnectionException
     *          on trouble connecting to Fedora.
     */
    private FedoraAPIM getAPIM() throws FedoraConnectionException {
        try {
            return getFedoraClient().getAPIM();
        } catch (ServiceException e) {
            throw new FedoraConnectionException("Error connecting to Fedora",
                                                e);
        } catch (IOException e) {
            throw new FedoraConnectionException("Error connecting to Fedora",
                                                e);
        }
    }

    /**
     * Get the API-A interface to Fedora.
     *
     * @return The API-A interface to Fedora.
     * @throws net.sourceforge.ecm.exceptions.FedoraConnectionException
     *          on trouble connecting to Fedora.
     */
    private FedoraAPIA getAPIA() throws FedoraConnectionException {
        FedoraAPIA fedoraAPIA;
        try {
            fedoraAPIA = getFedoraClient().getAPIA();
        } catch (IOException e) {
            throw new FedoraConnectionException("Error connecting to Fedora",
                                                e);
        } catch (ServiceException e) {
            throw new FedoraConnectionException("Error connecting to Fedora",
                                                e);
        }
        return fedoraAPIA;
    }
}
