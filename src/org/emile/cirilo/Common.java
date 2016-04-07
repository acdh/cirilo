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
import java.io.FileWriter;
import java.io.OutputStream;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.List;

import javax.swing.UIManager;

import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;

import org.apache.log4j.Logger;
import org.emile.cirilo.User;
import org.emile.cirilo.business.MDMapper;
import org.emile.cirilo.ecm.repository.Repository;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.xpath.XPath;

import voodoosoft.jroots.core.CPropertyService;
import voodoosoft.jroots.core.CServiceProvider;

public class Common {

	private static Logger log = Logger.getLogger(Common.class);

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
    
    public final static Namespace xmlns_xmp= Namespace.getNamespace( "x", "adobe:ns:meta/");
    
    public final static Namespace xmlns_exif= Namespace.getNamespace( "exif", "http://ns.adobe.com/exif/1.0/");
  
    public final static Namespace xmlns_xinc= Namespace.getNamespace( "x", "http://www.w3.org/2001/XInclude");
    
    public final static Namespace xmlns_lido= Namespace.getNamespace( "lido", "http://www.lido-schema.org");

	public final static Namespace xmlns_cantus = Namespace.getNamespace( "l", "http://cantus.oeaw.ac.at");

	public final static Namespace xmlns_dcterms = Namespace.getNamespace( "dcterms", "http://purl.org/dc/terms/");
		
	public final static Namespace xmlns_edm = Namespace.getNamespace( "edm", "http://www.europeana.eu/schemas/edm/");

	public final static Namespace xmlns_wgs84_pos = Namespace.getNamespace( "wgs84_pos", "http://www.w3.org/2003/01/geo/wgs84_pos#");
	
	public final static Namespace xmlns_ns0 = Namespace.getNamespace( "ns0", "http://phaidra.univie.ac.at/XML/metadata/V1.0");

	public final static Namespace xmlns_ns1 = Namespace.getNamespace( "ns1", "http://phaidra.univie.ac.at/XML/metadata/lom/V1.0");
	
	public final static Namespace xmlns_ns2 = Namespace.getNamespace( "ns2", "http://phaidra.univie.ac.at/XML/metadata/extended/V1.0");
		    	
	public final static Namespace xmlns_ns3 = Namespace.getNamespace( "ns3", "http://phaidra.univie.ac.at/XML/metadata/lom/V1.0/entity");
	
	public final static Namespace xmlns_ns4 = Namespace.getNamespace( "ns4", "http://phaidra.univie.ac.at/XML/metadata/lom/V1.0/requirement");
	
	public final static Namespace xmlns_ns5 = Namespace.getNamespace( "ns5", "http://phaidra.univie.ac.at/XML/metadata/lom/V1.0/educational");
	
	public final static Namespace xmlns_ns6 = Namespace.getNamespace( "ns6", "http://phaidra.univie.ac.at/XML/metadata/lom/V1.0/annotation");
	
	public final static Namespace xmlns_ns7 = Namespace.getNamespace( "ns7", "http://phaidra.univie.ac.at/XML/metadata/lom/V1.0/classification");
	
	public final static Namespace xmlns_ns8 = Namespace.getNamespace( "ns8", "http://phaidra.univie.ac.at/XML/metadata/lom/V1.0/organization");
	
	public final static Namespace xmlns_ns9 = Namespace.getNamespace( "ns9", "http://phaidra.univie.ac.at/XML/metadata/histkult/V1.0");
	
	public final static Namespace xmlns_ns10 = Namespace.getNamespace( "ns10", "http://phaidra.univie.ac.at/XML/metadata/provenience/V1.0");
	
	public final static Namespace xmlns_ns11 = Namespace.getNamespace( "ns11", "http://phaidra.univie.ac.at/XML/metadata/provenience/V1.0/entity");
	
	public final static Namespace xmlns_ns12 = Namespace.getNamespace( "ns12", "http://phaidra.univie.ac.at/XML/metadata/digitalbook/V1.0");
	
	public final static Namespace xmlns_ns13 = Namespace.getNamespace( "ns13", "http://phaidra.univie.ac.at/XML/metadata/etheses/V1.0");
	
	public final static Namespace xmlns_ore = Namespace.getNamespace( "ore", "http://www.openarchives.org/ore/terms/");
	
	public final static Namespace xmlns_owl = Namespace.getNamespace( "owl", "http://www.w3.org/2002/07/owl#");
	
	public final static Namespace xmlns_rdaGr2 = Namespace.getNamespace( "rdaGr2", "http://rdvocab.info/ElementsGr2/");
	
	public final static Namespace xmlns_gn = Namespace.getNamespace( "gn", "http://www.geonames.org/ontology#");
	
	public final static Namespace xmlns_mei = Namespace.getNamespace( "m", "http://www.music-encoding.org/ns/mei");
	
	public final static Namespace xmlns_nmei = Namespace.getNamespace( "", "http://www.music-encoding.org/ns/mei");
	
	public final static String TEIP5SCHEMA ="/tei/schema/P5/tei.xsd";  

	public final static String MEISCHEMA ="/mei/schema/2015/mei.xsd";  
      	
    public final static String[] LANGUAGES ={"en", "de"};
    
    public final static String SEPERATOR = "|";
    
    public final static String WINDOW_HEADER = "Cirilo 2.4.0";

    public final static String CM_VERSION = "15";
    
    public final static String DUBLIN_CORE = "0";
    
    public final static String HTML_LAYOUT = "1";
    
    public final static String FO_LAYOUT = "2";
    
    public final static String QUERY = "3";
    
    public final static String RELATIONS = "4";
    
    public final static String OAIPROVIDER = "5";    
    
    public final static String OWNER = "6";    
    
    public final static String XSLT = "X";

    public final static String DCMAPPING = "8";

    public final static String DC_MAPPING = "A";

    public final static String RDF_MAPPING = "B";
    
    public final static String BIBTEX_MAPPING = "C";
    
    public final static String KML_TEMPLATE = "D";
    
    public final static String REPLACEMENT_RULESET = "E";
    
    public final static String TORDF = "F";
    
    public final static String TOMETS = "G";

    public final static String HSSF_LAYOUT = "H";
   
    public final static String REPLACE = "1";

    public final static String ADD = "2";

    public final static String VOYANT = "3";

    public final static String SIMULATE = "1";
    
    public final static String UNALTERED = "0";
    
    public final static String isMemberOf = "info:fedora/fedora-system:def/relations-external#isMemberOf";

    public final static String hasModel ="info:fedora/fedora-system:def/model#";
   
    public final static String itemID = "http://www.openarchives.org/OAI/2.0#itemID";
   
    public final static String INFO_FEDORA ="info:fedora/";
    
    public final static String UNTITLED = "Untitled";
    
    public final static String LOCAL_FEDORA_HOST = "glossa.uni-graz.at:80";

    public final static String LOCAL_FEDORA_CONTEXT = "fedora";
    
    public final static int LOCAL_FEDORA_PROTOCOL = 0; // 0 =http; 1 = https

    public final static String LOCAL_SUBNET_IDENTIFIER = "143.50.";
    
    public final static String JSERROR =  "A BIRT exception occurred in evaluating Javascript expression";

    public final static String ONTOLOGYOBJECTS = "info:fedora/cm:SKOS;info:fedora/cm:Ontology";

    public final static String TEIOBJECTS = "info:fedora/cm:TEI";

    public final static String LIDOOBJECTS = "info:fedora/cm:LIDO";
    
	public final static String SYSTEM_DATASTREAMS ="|STYLESHEET|FO_STYLESHEET|QUERY|KML_TEMPLATE|DC_MAPPING|RDF_MAPPING|BIBTEX_MAPPING|RELS-EXT|RELS-INT|REPLACEMENT_RULESET|VOYANT|PID|METADATA|METHODS"+
												   "|THUMBNAIL|TEI_SOURCE|PDF_STREAM|URL|DC|BIBTEX|HTML_STREAM|ONTOLOGY|KML|KML_TEMPLATE|METS_SOURCE|TOMETS|";

    public final static String HANDLE_PREFIX = "0.NA/";
    
    public final static String UTF8 = "UTF-8";
        
    public final static String TEXT_MIMETYPES = "text/plain|text/css|application/javascript|application/sparql-query";
    
	 public final static String stylesheet = "<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns=\"http://www.tei-c.org/ns/1.0\" "+
	         "xmlns:t=\"http://www.tei-c.org/ns/1.0\" xmlns:l=\"http://cantus.oeaw.ac.at\" exclude-result-prefixes=\"xs l\" version=\"2.0\">"+
	         "<xsl:template match=\"*|@*|text()\"> <xsl:copy><xsl:apply-templates select=\"*|@*|text()\"/></xsl:copy></xsl:template>"+
	         "<xsl:template match=\"t:seg[@ana='#strikethrough']\"><xsl:element name=\"del\" namespace=\"http://www.tei-c.org/ns/1.0\"><xsl:apply-templates select=\"*|text()\"/></xsl:element></xsl:template>"+                       
	         "<xsl:template match=\"l:*\">"+
	         "<xsl:element name=\"seg\"><xsl:if test=\"not(@type)\"><xsl:attribute name=\"type\"><xsl:value-of select=\"'incipit'\"/></xsl:attribute></xsl:if>"+
	         "<xsl:if test=\"string-length(name())=6\"><xsl:attribute name=\"rend\"><xsl:value-of select=\"concat('#', substring(name(), 3, 2))\"/></xsl:attribute></xsl:if>"+
	         "<xsl:if test=\"string-length(name())=7\"><xsl:attribute name=\"rend\"><xsl:value-of select=\"concat('#', substring(name(), 3, 2))\"/></xsl:attribute></xsl:if>"+
	         "<xsl:if test=\"string-length(name())=8\"><xsl:attribute name=\"rend\"><xsl:value-of select=\"concat('#', substring(name(), 3, 3))\"/></xsl:attribute></xsl:if>"+                    
	         "<xsl:attribute name=\"ana\"><xsl:choose><xsl:when test=\"contains(name(),'_')\"><xsl:value-of select=\"concat('#',substring(name(),4))\"></xsl:value-of></xsl:when>"+
			 "<xsl:when test=\"string-length(name())=6\"><xsl:value-of select=\"concat('#', substring(name(), 5))\"/></xsl:when>"+
			 "<xsl:when test=\"string-length(name())=7\"><xsl:value-of select=\"concat('#', substring(name(), 5))\"/></xsl:when>"+  
	 		 "<xsl:when test=\"string-length(name())=8\"><xsl:value-of select=\"concat('#', substring(name(), 6))\"/></xsl:when>"+               
	         "<xsl:otherwise><xsl:value-of select=\"concat('#',substring(name(),3))\"/></xsl:otherwise></xsl:choose></xsl:attribute>"+
	     	 "<xsl:if test=\"contains(name(),'_')\"><xsl:attribute name=\"corresp\"><xsl:value-of select=\"concat('#./preceding-sibling::',substring(name(),4),'[1]')\"/></xsl:attribute></xsl:if>"+   
	         "<xsl:apply-templates select=\"*|@*|text()\"/>"+            
	         "</xsl:element></xsl:template>"+
	         "<xsl:template match=\"t:publicationStmt\">"+
	         "<xsl:copy><authority>Cantus</authority><xsl:apply-templates select=\".//t:idno\"/>"+
	         "</xsl:copy></xsl:template></xsl:stylesheet>";

    public final static int INFO = 0;
    public final static int WARN = 1;
    public final static int DEBUG = 2;
    public final static int LOGLEVEL = INFO;
  
    public static void log(FileWriter logger, Exception e) {
 	   try {
 		   if (logger != null) logger.write(new java.util.Date()  +" "+e.getLocalizedMessage()+"\n");
 	   } catch (Exception eq) {}	   
    }
      
    public static void log(FileWriter logger, String s) {
  	   try {
  		   if (logger != null) logger.write(new java.util.Date()  +" "+s+"\n");
  	   } catch (Exception eq) {}	   
     }    
    
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

	public static String OAIPHM() {
		String urn = "";
		try {
			CPropertyService props = (CPropertyService) CServiceProvider.getService( ServiceNames.PROPERTIES );
			urn = props.getProperty("user", "OAI.Prefix");
			if (urn != null && !urn.equals("null")) {
				if (!urn.endsWith(":") && urn.trim().length() > 0) urn+=":";
			} else {
				urn = "";
			}
		} catch (Exception e) {}
		return urn;
	}

	public static Document validate( Document dc) {
		try {			
			XPath xpath = XPath.newInstance("//dc:title");
			xpath.addNamespace( Common.xmlns_dc );
			Element title = (Element) xpath.selectSingleNode( dc );			
			if (title == null) {
				title = new Element ("title", Common.xmlns_dc);
				title.setText("Untitled");
				dc.getRootElement().addContent(title);
			} else {
				if (title.getTextTrim().isEmpty()) title.setText("Untitled");
			}	
		} catch (Exception e) {
			log.error(e.getLocalizedMessage(),e);
		}
		return dc;
	}
	
	
	   public static void genQR (User user, String pid) {	
	    	try { 
	    		List<String> cm = Repository.getContentModels(pid);
	    		if (cm.contains("cm:OAIRecord")) return;
	    		String toEncode = user.getUrl().substring(0,user.getUrl().lastIndexOf("/")+1)+pid;
	    		ByteArrayOutputStream stream = QRCode.from(toEncode).to(ImageType.JPG).withSize(125, 125).stream();
	    		File temp = File.createTempFile("tmp","xml");
	    		OutputStream out = new FileOutputStream (temp);
	    		stream.writeTo(out);
	    		if (!Repository.exists(pid, "QR")) {
	    			Repository.addDatastream(pid, "QR", "QR Code", "M", "image/jpeg", temp);
	    		} else {
	    			Repository.modifyDatastream(pid, "QR", "image/jpeg", "M", temp);
	    		}
	    		temp.delete();
	    	} catch (Exception e) {    		
	    	}
	    }    
}
