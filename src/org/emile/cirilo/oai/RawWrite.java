/*
 *  -------------------------------------------------------------------------
 *  Copyright 2006 OCLC, Online Computer Library Center
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

package org.emile.cirilo.oai;

import java.io.*;
import java.lang.NoSuchFieldException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.HashMap;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class RawWrite {
    public static void harvest(FileWriter logger, String arg) {
	try {
	    
	       String[] args = arg.split(" ");
	    
            HashMap options = getOptions(args);
            List rootArgs = (List)options.get("rootArgs");
	        String baseURL = null;
            if (rootArgs.size() > 0) {
                baseURL = (String)rootArgs.get(0);
            } else {
                throw new IllegalArgumentException();
            }
            
            OutputStream out = System.out;
            String outFileName = (String)options.get("-out");
            logger.write("\n"+new java.util.Date() +" Data providers BaseURL : "+ baseURL);
            logger.write("\n"+new java.util.Date() +" "+ arg);
            String from = (String)options.get("-from");
            String until = (String)options.get("-until");
            String metadataPrefix = (String)options.get("-metadataPrefix");

            if (metadataPrefix == null) metadataPrefix = "oai_dc";
            String resumptionToken = (String)options.get("-resumptionToken");
            String setSpec = (String)options.get("-setSpec");

            if (resumptionToken != null) {
                if (outFileName != null)
                    out = new FileOutputStream(outFileName, true);
                run(logger, baseURL, resumptionToken, out);
            } else {
                if (outFileName != null)
                    out = new FileOutputStream(outFileName);
                run(logger, baseURL, from, until, metadataPrefix, setSpec, out);
            }
            
            out.close();
        } catch (IllegalArgumentException e) {
//        	logger.write("RawWrite <-from date> <-until date> <-metadataPrefix prefix> <-setSpec setName> <-resumptionToken token> <-out fileName> baseURL");
	} catch (Exception e) {
		try {
			logger.write("\n"+new java.util.Date() +" "+e.getMessage());
		} catch (Exception ex) {}
	}
    }

    
    public static void run(FileWriter logger, String baseURL, String resumptionToken,
                           OutputStream out)
        throws IOException, ParserConfigurationException, SAXException, TransformerException,
               NoSuchFieldException {
        ListRecords listRecords = new ListRecords(baseURL, resumptionToken);
        while (listRecords != null) {
            NodeList errors = listRecords.getErrors();
            if (errors != null && errors.getLength() > 0) {
                int length = errors.getLength();
                for (int i=0; i<length; ++i) {
                    Node item = errors.item(i);
                    logger.write("\n"+new java.util.Date() +" "+item.getTextContent());
                }
                logger.write("\n"+new java.util.Date() +" "+ listRecords.toString());
                break;
            }
//            logger.write("\n"+listRecords);
            out.write(listRecords.toString().getBytes("UTF-8"));
            out.write("\n".getBytes("UTF-8"));
            resumptionToken = listRecords.getResumptionToken();
            if (resumptionToken == null || resumptionToken.length() == 0) {
                listRecords = null;
            } else {
                listRecords = new ListRecords(baseURL, resumptionToken);
            }
        }
    }

    public static void run(FileWriter logger, String baseURL, String from, String until,
                           String metadataPrefix, String setSpec,
                           OutputStream out)
        throws IOException, ParserConfigurationException, SAXException, TransformerException,
               NoSuchFieldException {
        out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n".getBytes("UTF-8"));
//        out.write(new Identify(baseURL).toString().getBytes("UTF-8"));
//        out.write("\n".getBytes("UTF-8"));
//        out.write(new ListMetadataFormats(baseURL).toString().getBytes("UTF-8"));
//        out.write("\n".getBytes("UTF-8"));
//        out.write(new ListSets(baseURL).toString().getBytes("UTF-8"));
//        out.write("\n".getBytes("UTF-8"));
        ListRecords listRecords = new ListRecords(baseURL, from, until, setSpec,
                                                  metadataPrefix);
        while (listRecords != null) {
            NodeList errors = listRecords.getErrors();
            if (errors != null && errors.getLength() > 0) {
                int length = errors.getLength();
                for (int i=0; i<length; ++i) {
                    Node item = errors.item(i);
                    logger.write("\n"+new java.util.Date() +" "+item.getTextContent());
                }
//             logger.write("\n " + listRecords.toString());
                break;
            }
//         logger.write("\n"+listRecords);
            out.write(listRecords.toString().getBytes("UTF-8"));
            logger.write("\n"+new java.util.Date() +" Harvesting process successfully terminated");
            out.write("\n".getBytes("UTF-8"));
            String resumptionToken = listRecords.getResumptionToken();
            if (resumptionToken == null || resumptionToken.length() == 0) {
                listRecords = null;
            } else {
                listRecords = new ListRecords(baseURL, resumptionToken);
            }
        }
    }

    private static HashMap getOptions(String[] args) {
        HashMap options = new HashMap();
        ArrayList rootArgs = new ArrayList();
        options.put("rootArgs", rootArgs);
        
        for (int i=0; i<args.length; ++i) {
            if (args[i].charAt(0) != '-') {
                rootArgs.add(args[i]);
            } else if (i+1 < args.length) {
                options.put(args[i], args[++i]);
            } else {
                throw new IllegalArgumentException();
            }
        }
        return options;
    }
}
