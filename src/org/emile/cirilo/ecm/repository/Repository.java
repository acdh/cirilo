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

import org.emile.cirilo.ecm.exceptions.DatastreamNotFoundException;
import org.emile.cirilo.ecm.exceptions.FedoraConnectionException;
import org.emile.cirilo.ecm.exceptions.FedoraIllegalContentException;
import org.emile.cirilo.ecm.exceptions.ObjectIsWrongTypeException;
import org.emile.cirilo.ecm.exceptions.ObjectNotFoundException;
import org.w3c.dom.Document;
import org.emile.cirilo.gui.jtable.DefaultSortTableModel;

import fedora.server.types.gen.ObjectProfile;

import java.util.List;
import java.io.File;
/**
 * This is the connection independent way for the webservice to talk to Fedora.
 *
 * This class is "static", ie it is never supposed to be instantiated. Instead,
 * before the class is used it must be "initialised" by a call to the initialise
 * method. In this call, you provide the fedora connector object, which this
 * class will use to delegate all fedora communication.
 *
 * @see #initialise(FedoraUserToken, FedoraConnector)
 */
public class Repository {


    /**
     * The connector object through which all communication will be made
     */
    private static FedoraConnector connector;

    private static String pidGenerator = "net.sourceforge.ecm.repository.PidGeneratorImpl";


    /**
     * The static fedora uri prefix, to convert between pids and uris
     */
    private static final String FEDORA_URI_PREFIX = "info:fedora/";


    //TODO javadoc or remove
    public static String getUser(){
        return connector.getUsername();
    }

    /**
     * Initialise this static class. This method must be called before any other
     * or IllegalStateExceptions will be thrown.
     * If any of these arguments are null, you risk getting nullpointerexceptions
     * later on.
     * @param usertoken The usertoken, containing the username, password and
     * server url
     * @param connectorObject the Connector object which will handle the
     * communication with fedora
     */
    public static void initialise(
            FedoraUserToken usertoken,
            FedoraConnector connectorObject) {
        connector = connectorObject;
        connector.initialise(usertoken);
    }

    public static PidList query(String query)
            throws IllegalStateException,
                   FedoraConnectionException,
                   FedoraIllegalContentException {
        return connector.query(query);
    }

    public static DefaultSortTableModel getObjects(String query, String[] columnNames)
    throws IllegalStateException,
           FedoraConnectionException,
           FedoraIllegalContentException {
    	return connector.getObjects(query,columnNames);
}

    
    public static java.util.ArrayList<String> getPidList(String query)
    throws IllegalStateException,
           FedoraConnectionException,
           FedoraIllegalContentException {
    	return connector.getPidList(query);
}
    
    public static java.util.ArrayList<String> getTriples(String query)
    throws IllegalStateException,
           FedoraConnectionException,
           FedoraIllegalContentException {
    	return connector.getTriples(query);
}
    
    

    public static boolean addRelation(String from, String relation, String to)
            throws IllegalStateException, ObjectNotFoundException,
                   FedoraConnectionException, FedoraIllegalContentException {
        return connector.addRelation(from, relation, to);
    }

    public static boolean addLiteralRelation(
            String from,
            String relation,
            String value,
            String datatype)
            throws ObjectNotFoundException, FedoraConnectionException,
                   FedoraIllegalContentException {
        return connector.addLiteralRelation(from, relation, value, datatype);
    }

    public static Document getObjectXml(String pid)
            throws FedoraConnectionException,
                   FedoraIllegalContentException,
                   ObjectNotFoundException {
        return connector.getObjectXml(pid);
    }

    public static String get2ObjectXml(String pid)
            throws FedoraConnectionException,
                   FedoraIllegalContentException,
                   ObjectNotFoundException {
        return connector.get2ObjectXml(pid);
    }

    public static String ingestDocument(
            Document newobject,
            String logmessage)
            throws FedoraConnectionException,
                   FedoraIllegalContentException {
        return connector.ingestDocument(newobject, logmessage);
    }

    
    public static String ingestDocument(
            String newobject,
            String logmessage)
            throws FedoraConnectionException,
                   FedoraIllegalContentException {
        return connector.ingestDocument(newobject, logmessage);
    }

    public static String ingestDocument(
    		Document newobject,
            String format,
            String logmessage)
            throws FedoraConnectionException,
                   FedoraIllegalContentException {
        return connector.ingestDocument(newobject, format, logmessage);
    }

    public static List<FedoraConnector.Relation> getRelations(String pid)
            throws ObjectNotFoundException, FedoraConnectionException,
                   FedoraIllegalContentException {
        return connector.getRelations(pid);
    }

    public static List<FedoraConnector.Relation> getRelations(
            String pid,
            String relation)
            throws ObjectNotFoundException, FedoraConnectionException,
                   FedoraIllegalContentException {
        return connector.getRelations(pid, relation);
    }

    public static Document getDatastream(String pid, String dsid)
    throws DatastreamNotFoundException,
           FedoraConnectionException,
           FedoraIllegalContentException,
           ObjectNotFoundException {
    	return connector.getDatastream(pid, dsid);
    }
    
    public static byte[] getDatastream(String pid, String datastream, String dummy)
    throws DatastreamNotFoundException,
           FedoraConnectionException,
           FedoraIllegalContentException,
           ObjectNotFoundException {
    	return connector.getDatastream(pid, datastream,  (String) null);
    }

    public static Document getDatastream(String pid, String datastream, Integer dummy)
    throws DatastreamNotFoundException,
           FedoraConnectionException,
           FedoraIllegalContentException,
           ObjectNotFoundException,
           Exception {
    	return connector.getDatastream(pid, datastream, new Integer(0));
    }
    
    public static String modifyDatastream(String pid, String datastream, byte[] stream)
    throws DatastreamNotFoundException, FedoraConnectionException,
           FedoraIllegalContentException, ObjectNotFoundException {
       	return connector.modifyDatastream(pid, datastream, stream);
    }

    public static String modifyDatastream(String pid, String datastream, String mimetype, byte[] stream)
    throws DatastreamNotFoundException, FedoraConnectionException,
           FedoraIllegalContentException, ObjectNotFoundException {
       	return connector.modifyDatastream(pid, datastream, stream);
    }

    public static String modifyDatastream(String pid, String datastream, String mimetype, String controlgroup, File fp )
    throws DatastreamNotFoundException, FedoraConnectionException,
           FedoraIllegalContentException, ObjectNotFoundException {    
          	return connector.modifyDatastream(pid, datastream, mimetype, controlgroup, fp);
    }
    
    public static String modifyDatastream(String pid, String datastream, String mimetype, String controlgroup, String location )
    throws DatastreamNotFoundException, FedoraConnectionException,
           FedoraIllegalContentException, ObjectNotFoundException {    
          	return connector.modifyDatastream(pid, datastream, mimetype, controlgroup, location);
    }
    
    public static String modifyObject(String pid, String state, String label, String owner)
    throws DatastreamNotFoundException, FedoraConnectionException,
           FedoraIllegalContentException, ObjectNotFoundException {
           return connector.modifyObject(pid, state, label, owner);	
    }
    public static String modifyDatastreamByValue(String pid, String datastream, String mimetype, String stream)
    throws DatastreamNotFoundException, FedoraConnectionException,
           FedoraIllegalContentException, ObjectNotFoundException {
       	return connector.modifyDatastreamByValue(pid, datastream, mimetype, stream);
    }
    public static boolean purgeRelation(String from, String relation, String to)
    	    throws ObjectNotFoundException, FedoraConnectionException,
    	           FedoraIllegalContentException {
    	    	return connector.purgeRelation(from, relation, to);
    	    }
    public static boolean purgeDatastream(String id, String dsid)
    	    throws ObjectNotFoundException, FedoraConnectionException,
    	           FedoraIllegalContentException {
    	    	return connector.purgeDatastream(id, dsid);
    	    }

    public static String addDatastream(String pid, String datastream, String title, String type, String mimetype, File stream )
    throws DatastreamNotFoundException, FedoraConnectionException,
           FedoraIllegalContentException, ObjectNotFoundException {
       	return connector.addDatastream(pid, datastream, title, type, mimetype, stream);
    }
 
    public static String addDatastream(String pid, String datastream, String title, String mimetype, String location)
    throws DatastreamNotFoundException, FedoraConnectionException,
           FedoraIllegalContentException, ObjectNotFoundException {
       	return connector.addDatastream(pid, datastream, title, mimetype, location);
    }
 
    
    public static String getNextPid()
    throws  FedoraConnectionException {
    	return connector.getNextPid();
    }

    
    public static List<String> getContentModels(String pid)
            throws ObjectNotFoundException, FedoraConnectionException,
                   FedoraIllegalContentException {
        return connector.getContentModels(pid);
    }

    public static List<String> getInheritingContentModels(String cmpid)
            throws FedoraConnectionException, ObjectNotFoundException,
                   FedoraIllegalContentException, ObjectIsWrongTypeException {
        return connector.getInheritingContentModels(cmpid);
    }


    public static List<String> getInheritedContentModels(String cmpid)
            throws FedoraConnectionException, ObjectNotFoundException,
                   FedoraIllegalContentException, ObjectIsWrongTypeException {
        return connector.getInheritedContentModels(cmpid);
    }

    public static List<String> getTemplates(String ownerid, boolean sysop)
    	throws FedoraConnectionException, FedoraIllegalContentException {
    		return connector.getTemplates(ownerid, sysop);
    }

    public static List<String> getContainers(String ownerid, boolean sysop)
	throws FedoraConnectionException, FedoraIllegalContentException {
		return connector.getContainers(ownerid, sysop);
}

    
    public static List<String> getUsers()
	throws FedoraConnectionException, FedoraIllegalContentException {
		return connector.getUsers();
    }
    
    public static DefaultSortTableModel listDatastreams(String pid, boolean metadata)
            throws ObjectNotFoundException, FedoraConnectionException,
                   FedoraIllegalContentException {
        return connector.listDatastreams(pid, metadata);
    }

    public static boolean exist(String pid) throws FedoraConnectionException,
                                                   FedoraIllegalContentException {
        return connector.exists(pid);
    }

    public static boolean exists(String pid, String dsid) throws IllegalStateException, FedoraIllegalContentException,
    FedoraConnectionException {
    	return connector.exists(pid, dsid);
    }

    
    public static String[] getObjectProfile(String pid)
    throws FedoraConnectionException, FedoraIllegalContentException {
        return connector.getObjectProfile(pid);
    }
    
    public static boolean isDataObject(String pid) throws
                                                   FedoraConnectionException,
                                                   FedoraIllegalContentException {
        return connector.isDataObject(pid);
    }

    public static boolean isTemplate(String pid) throws ObjectNotFoundException,
                                                        FedoraIllegalContentException,
                                                        FedoraConnectionException {
        return connector.isTemplate(pid);
    }

    public static boolean isContentModel(String pid) throws
                                                     FedoraConnectionException,
                                                     FedoraIllegalContentException {
        return connector.isContentModel(pid);
    }

    
    public static boolean hasContentModel(String pid, String cmpid)
            throws FedoraIllegalContentException, FedoraConnectionException {
    		return connector.hasContentModel(pid, cmpid);
    }		
    
    public static boolean purgeObject(String pid) throws
    	FedoraConnectionException,
    	FedoraIllegalContentException {
    	return connector.purgeObject(pid);
}

    /**
     * If the given string starts with "info:fedora/", remove it.
     *
     * @param pid A pid, possibly as a URI
     * @return The pid, with the possible URI prefix removed.
     */
    public static String ensurePID(String pid) {
        if (pid.startsWith(FEDORA_URI_PREFIX)) {
            pid = pid.substring(FEDORA_URI_PREFIX.length());
        }
        return pid;
    }

    /**
     * If the given string does not start with "info:fedora/", add it.
     *
     * @param uri An URI, possibly as a PID
     * @return The uri, with the possible URI prefix prepended.
     */
    public static String ensureURI(String uri) {
        if (!uri.startsWith(FEDORA_URI_PREFIX)) {
            uri = FEDORA_URI_PREFIX + uri;
        }
        return uri;
    }

    public static String getPidGenerator() {
        return pidGenerator;
    }

    public static void setPidGenerator(String pidGenerator) {
        Repository.pidGenerator = pidGenerator;
    }
}
