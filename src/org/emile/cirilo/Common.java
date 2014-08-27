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

package org.emile.cirilo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.UIManager;

import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;

import org.emile.cirilo.User;
import org.emile.cirilo.ecm.repository.Repository;

import org.jdom.Namespace;

import voodoosoft.jroots.core.CServiceProvider;

public class Common {
	
	public final static String[] DCMI = {"Title", "Description", "Subject", "Creator", "Publisher", "Contributor", "Language", "Date", "Type", "Format", "Source", "Relation", "Coverage", "Rights"};

	public final static Namespace xmlns_dc = Namespace.getNamespace("dc", "http://purl.org/dc/elements/1.1/");

	public final static Namespace xmlns_oai_dc = Namespace.getNamespace("oai_dc", "http://www.openarchives.org/OAI/2.0/oai_dc/");

	public final static Namespace xmlns_oai = Namespace.getNamespace("oai", "http://www.openarchives.org/OAI/2.0/");
	
	public final static Namespace xmlns_rdf = Namespace.getNamespace("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");

	public final static Namespace xmlns_gams = Namespace.getNamespace("gams", "http://gams.uni-graz.at#");

	public final static Namespace xmlns_tei_p5 = Namespace.getNamespace( "t", "http://www.tei-c.org/ns/1.0" );

	public final static Namespace xmlns_ntei_p5 = Namespace.getNamespace( "", "http://www.tei-c.org/ns/1.0" );
	
	public final static Namespace xmlns_xml = Namespace.getNamespace( "xml", "http://www.w3.org/XML/1998/namespace" );
    
	public final static Namespace xmlns_mets = Namespace.getNamespace( "mets", "http://www.loc.gov/METS/" );
	
	public final static Namespace xmlns_nmets = Namespace.getNamespace( "", "http://www.loc.gov/METS/" );
	
	public final static Namespace xmlns_mods = Namespace.getNamespace( "mods", "http://www.loc.gov/mods/v3");
	    
	public final static Namespace xmlns_xlink = Namespace.getNamespace( "xlink", "http://www.w3.org/1999/xlink" );
    
    public final static Namespace xmlns_ecm = Namespace.getNamespace( "", "http://ecm.sourceforge.net/relations/0/2/#");
   
    public final static Namespace xmlns_fedora_model = Namespace.getNamespace( "", "info:fedora/fedora-system:def/model#");
	 
    public final static Namespace xmlns_rel = Namespace.getNamespace( "rel", "info:fedora/fedora-system:def/relations-external#");

    public final static Namespace xmlns_mm = Namespace.getNamespace( "mm", "http://mml.uni-graz.at/v1.0");

    public final static Namespace xmlns_xsl = Namespace.getNamespace( "xsl", "http://www.w3.org/1999/XSL/Transform");

    public final static Namespace xmlns_tex = Namespace.getNamespace( "tex", "http://bibtexml.sf.net/");
    
    public final static Namespace xmlns_rs = Namespace.getNamespace( "rs", "http://rsml.uni-graz.at/v1.0");

    public final static Namespace xmlns_sparql = Namespace.getNamespace( "s", "http://www.w3.org/2001/sw/DataAccess/rf1/result");

    public final static Namespace xmlns_kml = Namespace.getNamespace( "kml", "http://earth.google.com/kml/2.0");

    public final static Namespace xmlns_nkml = Namespace.getNamespace( "", "http://earth.google.com/kml/2.0");

    public final static Namespace xmlns_foxml = Namespace.getNamespace( "foxml", "info:fedora/fedora-system:def/foxml#");
 
    public final static Namespace xmlns_model = Namespace.getNamespace( "", "info:fedora/fedora-system:def/model#");

    public final static Namespace xmlns_europeana = Namespace.getNamespace( "europeana", "http://www.europeana.eu/schemas/ese/");
    
    public final static Namespace xmlns_skos = Namespace.getNamespace( "skos", "http://www.w3.org/2004/02/skos/core#");

    public final static Namespace xmlns_grel= Namespace.getNamespace( "rel", "http://gams.uni-graz.at#");

    public final static Namespace xmlns_viewer= Namespace.getNamespace( "", "http://gams.uni-graz.at/viewer");
    	
    public final static String SESAME_SERVER = "http://gams.uni-graz.at/openrdf-sesame";
    
    public final static String TEIP5SCHEMA ="http://gams.uni-graz.at/tei/schema/P5/tei.xsd";  
  	
    public final static String[] LANGUAGES ={"en", "de"};
    
    public final static String SEPERATOR = "|";
    
    public final static String WINDOW_HEADER = "Cirilo 2.4.0";
    
    public final static String DUBLIN_CORE = "0";
    
    public final static String HTML_LAYOUT = "1";
    
    public final static String FO_LAYOUT = "2";
    
    public final static String QUERY = "3";
    
    public final static String RELATIONS = "4";
    
    public final static String OAIPROVIDER = "5";    
    
    public final static String OWNER = "6";    
    
    public final static String XSLT = "7";

    public final static String DCMAPPING = "8";

    public final static String DC_MAPPING = "A";

    public final static String RDF_MAPPING = "B";
    
    public final static String BIBTEX_MAPPING = "C";
    
    public final static String KML_TEMPLATE = "D";
    
    public final static String REPLACEMENT_RULESET = "E";
    
    public final static String TORDF = "F";
    
    public final static String TOMETS = "G";
   
    public final static String REPLACE = "1";

    public final static String ADD = "2";

    public final static String VOYANT = "3";

    public final static String SIMULATE = "1";
    
    public final static String UNALTERED = "0";
    
    public final static String isMemberOf = "info:fedora/fedora-system:def/relations-external#isMemberOf";

    public final static String hasModel ="info:fedora/fedora-system:def/model#";
   
    public final static String itemID = "http://www.openarchives.org/OAI/2.0#itemID";

    public final static String URN = "urn:kfug:at:gams";
    
    public final static String INFO_FEDORA ="info:fedora/";
    
    public final static String UNTITLED = "Ohne Titel";
    
    public final static String LOCAL_FEDORA_HOST = "glossa.uni-graz.at:80";

    public final static String LOCAL_FEDORA_CONTEXT = "fedora";
    
    public final static int LOCAL_FEDORA_PROTOCOL = 0; // 0 =http; 1 = https

    public final static String LOCAL_SUBNET_IDENTIFIER = "143.50.";
    
    public final static String JSERROR =  "A BIRT exception occurred in evaluating Javascript expression";

    public final static String ONTOLOGYOBJECTS = "info:fedora/cm:SKOS;info:fedora/cm:Ontology";

    public final static String TEIOBJECTS = "info:fedora/cm:TEI";
    
	public final static String SYSTEM_DATASTREAMS ="|STYLESHEET|FO_STYLESHEET|QUERY|KML_TEMPLATE|DC_MAPPING|RDF_MAPPING|BIBTEX_MAPPING|RELS-EXT|RELS-INT|REPLACEMENT_RULESET|VOYANT|PID|METADATA|METHODS"+
												   "|THUMBNAIL|TEI_SOURCE|PDF_STREAM|URL|DC|BIBTEX|HTML_STREAM|ONTOLOGY|KML|KML_TEMPLATE|METS_SOURCE|TOMETS|";

    
	public static String itrim(String s) {
        return s.replaceAll("\\b\\s{2,}\\b", " ");
	}

	public static ResourceBundle getResourceBundle(String locale) {
		
		ResourceBundle  res; 
		
		if (locale.contains("de")) {
			Locale.setDefault(Locale.GERMAN);
			res = ResourceBundle.getBundle("org.emile.cirilo.CiriloResources", Locale.GERMAN);
		} else {
			Locale.setDefault(Locale.ENGLISH);
			res = ResourceBundle.getBundle("org.emile.cirilo.CiriloResources", Locale.ENGLISH);
		}
	
		UIManager.put("OptionPane.yesButtonText", res.getString("yes"));
		UIManager.put("OptionPane.noButtonText", res.getString("no"));
		UIManager.put("OptionPane.cancelButtonText", res.getString("cancel"));
		UIManager.put("FileChooser.cancelButtonText", res.getString("cancel"));
	   
		return res;
	}
	
	   public static void genQR (User user, String pid) {	
	    	try {
	    		String toEncode = user.getUrl().substring(0,user.getUrl().lastIndexOf("/")+1)+pid;
	    		ByteArrayOutputStream stream = QRCode.from(toEncode).to(ImageType.JPG).withSize(125, 125).stream();
	    		File temp = File.createTempFile("tmp","xml");
	    		OutputStream out = new FileOutputStream (temp);
	    		stream.writeTo(out);
	    		try {
	    			Repository.addDatastream(pid, "QR", "QR Code", "M", "image/jpeg", temp);
	    		} catch (Exception q) {
	    			Repository.modifyDatastream(pid, "QR", "image/jpeg", "M", temp);
	    		}
	    		temp.delete();
	    	} catch (Exception e) {    		
	    	}
	    }    
}
