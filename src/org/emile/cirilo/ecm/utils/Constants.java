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

package org.emile.cirilo.ecm.utils;
/**
 * TODO abr forgot to document this class
 */
public class Constants {

    /**
     * The major ecm version
     */
    public static final String MAJORVERSION = "0";

    /**
     * The minor ecm version
     */
    public static final String MINORVERSION = "2";//This is wrong for some of the namespaces

    /**
     * The ECM version
     *
     * @see #MAJORVERSION
     * @see #MINORVERSION
     */
    private static final String VERSION = MAJORVERSION +"/"+ MINORVERSION;

    // Our namespace
    /**
     * The ECM namespace
     */
    public static final String NAMESPACE_ECM
            = "http://ecm.sourceforge.net/";

    /** The view namespace
     * @see #NAMESPACE_ECM
     * @see #VERSION
     */
    public static final String NAMESPACE_VIEW
            = NAMESPACE_ECM+"types/view/"+ VERSION +"/#";

    /**
     * The schema DSCOMPOSITEMODEL extension namespace
     * @see #NAMESPACE_ECM
     * @see #VERSION
     */
    public static final String NAMESPACE_SCHEMA
            = NAMESPACE_ECM+"types/dscompositeschema/"+ VERSION +"/#";

    /**
     * The digital object bundle namespace
     * @see #NAMESPACE_ECM
     * @see #VERSION
     */
    public static final String NAMESPACE_DIGITAL_OBJECT_BUNDLE
            = NAMESPACE_ECM+"types/digitalobjectbundle/"+ VERSION +"/#";//Our relation namespace


    //our relations
    public static final String NAMESPACE_RELATIONS
            = NAMESPACE_ECM+"relations/"+ VERSION +"/#";

    public static final String TEMPLATE_REL
            = NAMESPACE_RELATIONS +"isTemplateFor";

    public static final String ENTRY_RELATION =
            NAMESPACE_RELATIONS+"isEntryForViewAngle";

    public static final String RELATION_EXTENDS_MODEL
            = NAMESPACE_RELATIONS + "extendsModel";

    //Fedora
    public static final String FEDORA_SYSTEM_DEF
            = "info:fedora/fedora-system:def/";
    public static final String NAMESPACE_FOXML
            = FEDORA_SYSTEM_DEF+"foxml#";
    public static final String NAMESPACE_DS_COMPOSITE
            = FEDORA_SYSTEM_DEF+"dsCompositeModel#";
    public static final String NAMESPACE_FEDORA_MODEL
            = FEDORA_SYSTEM_DEF + "model#";
    public static final String STATEREL
            = NAMESPACE_FEDORA_MODEL + "state";
    public static final String HAS_MODEL
            = NAMESPACE_FEDORA_MODEL + "hasModel";

    //Fedora objects
    public static final String SERVICE_DEFINITION_3_0
            = "info:fedora/fedora-system:ServiceDefinition-3.0";
    public static final String SERVICE_DEPLOYMENT_3_0
            = "info:fedora/fedora-system:ServiceDeployment-3.0";
    public static final String CONTENT_MODEL_3_0
            = "info:fedora/fedora-system:ContentModel-3.0";
    public static final String FEDORA_OBJECT_3_0
            = "info:fedora/fedora-system:FedoraObject-3.0";

    //ONTOLOGIES
    public static final String NAMESPACE_RDF
            = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    public static final String NAMESPACE_RDFS
            = "http://www.w3.org/2000/01/rdf-schema#";
    public static final String NAMESPACE_OWL
            = "http://www.w3.org/2002/07/owl#";//SCHEMA

    /**
     * The owl property defining a class
     */
    public static final String RDF_PROPERTY_OWL_CLASS
            = "owl:Class";
    /**
     * The rdf property defining rdf properties.
     */
    public static final String RDF_PROPERTY_RDF_ABOUT
            = "rdf:about";
    /**
     * The RDFS property defining that one class is a subclass of another.
     */
    public static final String RDF_PROPERTY_RDFS_SUB_CLASS_OF
            = "rdfs:subClassOf";
    /**
     * The RDF property defining the object of a property.
     */
    public static final String RDF_PROPERTY_RDF_RESOURCE
            = "rdf:resource";


    //SCHEMA
    public static final String NAMESPACE_XML_SCHEMA_INSTANCE
            = "http://www.w3.org/2001/XMLSchema-instance";//DC

    //DC
    public static final String NAMESPACE_DC
            = "http://purl.org/dc/elements/1.1/";
    public static final String NAMESPACE_DCTERMS
            = "http://purl.org/dc/terms/";
    public static final String NAMESPACE_OAI
            = "http://www.openarchives.org/OAI/2.0/";
    public static final String NAMESPACE_OAIDC
            = "http://www.openarchives.org/OAI/2.0/oai_dc/";

    //DATASTREAM NAMES
    /**
     * The name of the datastream containing ontology information.
     */
    public static final String ONTOLOGY_DATASTREAM
            = "ONTOLOGY";
    /**
     * The name of the datastream containing datastream information.
     */
    public static final String DS_COMPOSITE_MODEL_DATASTREAM
            = "DS-COMPOSITE-MODEL";
    /**
     * The name of the datastream containing view information
     */
    public static final String VIEW_DATASTREAM
            = "VIEW";


    public static final String ONTOLOGY_URI = "http://ecm.sourceforge.net/" +
    "relations/0/2/#";
}
