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

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import org.apache.log4j.Logger;


/**
 * Utility methods for working with w3c DOM Documents.
 */
public class DocumentUtils {

	private static Logger log = Logger.getLogger(DocumentUtils.class);
	   
    /** A default document builder, namespace aware. */
    public static final DocumentBuilder DOCUMENT_BUILDER;
    static {
        try {
            DocumentBuilderFactory documentBuilderFactory
                    = DocumentBuilderFactory
                    .newInstance();
            documentBuilderFactory.setNamespaceAware(true);
            DOCUMENT_BUILDER = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new Error("Error initialising default document builder", e);
        }
    }

    /** A default document transformer. */
    public static final Transformer DOCUMENT_TRANSFORMER;
    static {
        try {
            DOCUMENT_TRANSFORMER
                    = TransformerFactory.newInstance().newTransformer();
        } catch (TransformerConfigurationException e) {
            throw new Error("Error initialising default document transformer",
                            e);
        }
    }

    /**
     *  Simple method to dump a Document object as a xml string. Be aware that the
     *  string starts with <?xml ...
     * @param doc the document to convert
     * @return The document as a xml
     * @throws TransformerException If the document could not be transformed to
     * xml
     */
    public static String documentToString(Document doc)
            throws TransformerException {

        StringWriter writer = new StringWriter();
        DOCUMENT_TRANSFORMER.transform(new DOMSource(doc), new StreamResult(writer) );
        return writer.toString();
    }

    /**
     * Simple method to parse a string into a w3c Document
     * @param doc the string to parse
     * @return the document
     * @throws SAXException if the string did not contain valid html.
     */
    public static Document stringToDocument(String doc)
            throws SAXException {
        try {
            InputStream in = new ByteArrayInputStream(doc.getBytes("UTF-8"));
            return DOCUMENT_BUILDER.parse(in);
        } catch (IOException e) {
            throw new Error("Problem reading a string, should never happen",e);
        }
    }

}
