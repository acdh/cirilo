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

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactoryConfigurationException;
import javax.xml.xpath.XPathFactory;
import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Collections;

import org.apache.log4j.Logger;

/**
 * Utilities for performing Xpath operations in the ECM context. Already defines
 * all the relevant namespaces, so proper shorthands can be used.
 */
public class XpathUtils {

	private static Logger log = Logger.getLogger(XpathUtils.class);

    private static final String[][] NAMESPACE_TABLE
            = {{XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI},
            {XMLConstants.XMLNS_ATTRIBUTE, XMLConstants.XMLNS_ATTRIBUTE_NS_URI},
            {XMLConstants.DEFAULT_NS_PREFIX, XMLConstants.NULL_NS_URI},
            {"rdf", Constants.NAMESPACE_RDF},
            {"rdfs", Constants.NAMESPACE_RDFS},
            {"owl", Constants.NAMESPACE_OWL},
            {"xsi", Constants.NAMESPACE_XML_SCHEMA_INSTANCE},
            {"dc", Constants.NAMESPACE_DC},
            {"dcterms", Constants.NAMESPACE_DCTERMS},
            {"oai", Constants.NAMESPACE_OAI},
            {"oai_dc", Constants.NAMESPACE_OAIDC},
            {"view", Constants.NAMESPACE_VIEW},
            {"fedora-model", Constants.NAMESPACE_FEDORA_MODEL},
            {"foxml", Constants.NAMESPACE_FOXML},
            {"ds", Constants.NAMESPACE_DS_COMPOSITE},
            {"doms", Constants.NAMESPACE_RELATIONS},//TODO, this should be deprecated
            {"ecm", Constants.NAMESPACE_RELATIONS},//To shorthands for same namespace
            {"schema", Constants.NAMESPACE_SCHEMA},
            {"dobundle", Constants.NAMESPACE_DIGITAL_OBJECT_BUNDLE}
    };

    private static final NamespaceContext ECM_NAMESPACE_CONTEXT
            = new NamespaceContext() {
        Map<String, String> nsPrefixMap = new HashMap<String, String>(NAMESPACE_TABLE.length);
        Map<String, String> inverseNsPrefixMap = new HashMap<String, String>(NAMESPACE_TABLE.length);
        {
            for (String[] pair : NAMESPACE_TABLE) {
                nsPrefixMap.put(pair[0], pair[1]);
                inverseNsPrefixMap.put(pair[0], pair[1]);
            }
        }

        public String getNamespaceURI(String prefix) {
            if (prefix == null) {
                throw new IllegalArgumentException("Prefix is null");
            }
            String uri = nsPrefixMap.get(prefix);
            if (uri != null) {
                return uri;
            } else {
                return XMLConstants.NULL_NS_URI;
            }
        }

        public String getPrefix(String namespaceURI) {
            if (namespaceURI == null) {
                throw new IllegalArgumentException(
                        "namespaceURI is null");
            }
            return inverseNsPrefixMap.get(namespaceURI);
        }

        public Iterator getPrefixes(String namespaceURI) {
            if (namespaceURI == null) {
                throw new IllegalArgumentException(
                        "namespaceURI is null");
            }
            String prefix = getPrefix(namespaceURI);
            if (prefix == null) {
                return Collections.emptyList().iterator();
            } else {
                return Collections.singletonList(prefix)
                        .iterator();
            }
        }
    };

    /**
     * Helper method for doing an XPath query using ECM namespaces.
     *
     * @param node            The node to start XPath query on.
     * @param xpathExpression The XPath expression, using default DOMS
     *                        namespace prefixes.
     * @return The result, as a node list.
     *
     * @throws XPathExpressionException On trouble parsing or evaluating the
     *                                  expression.
     */
    public static NodeList xpathQuery(Node node, String xpathExpression)
            throws XPathExpressionException, XPathFactoryConfigurationException{
    	
    	
        XPath xPath = XPathFactory.newInstance(
      		  XPathFactory.DEFAULT_OBJECT_MODEL_URI,
      		  "com.sun.org.apache.xpath.internal.jaxp.XPathFactoryImpl",
      		  ClassLoader.getSystemClassLoader()).newXPath();
        xPath.setNamespaceContext(ECM_NAMESPACE_CONTEXT);

        
        return (NodeList) xPath
                .evaluate(xpathExpression, node, XPathConstants.NODESET);
    }
}
