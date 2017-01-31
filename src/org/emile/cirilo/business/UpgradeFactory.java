package org.emile.cirilo.business;

import java.io.File;
import java.io.FileOutputStream;

import org.emile.cirilo.ecm.repository.Repository;

public class UpgradeFactory {
	
	private static final String CIRILO_BACKBONE = "cirilo:Backbone";
    private static final String CIRILO_ENVIRONMENT = "cirilo:Environment";

    private String fedora;
    private String host;
    
    public UpgradeFactory (String fedora, String host) {
    	this.fedora = fedora;
    	this.host = host;
    }
    
	public void addDefaultDatastreams() {

		File temp = null;
		
		try {
			 temp = File.createTempFile("tmp","xml");

		    try {
	   		      if (!Repository.exists(CIRILO_BACKBONE, "OAItoHTML")) {	
	   		    	  FileOutputStream fos = new FileOutputStream(temp);
		           	  fos.write("<xsl:stylesheet version=\"1.0\" xmlns:oai_dc=\"http://www.openarchives.org/OAI/2.0/oai_dc/\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:dcterms=\"http://purl.org/dc/terms/\"  xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\"/>".getBytes("UTF-8"));
		           	  fos.close();
		           	  Repository.addDatastream(CIRILO_BACKBONE, "OAItoHTML","",  "X", "text/xml", temp);
		           	  Repository.addDatastream(CIRILO_ENVIRONMENT, "OAItoHTML","",  "X", "text/xml", temp);
	   		      }		
	   		    }
	   		finally {}
 		    try {
	   		      if (!Repository.exists(CIRILO_BACKBONE, "PELAGIOS_TEMPLATE")) {	
	   		    	  FileOutputStream fos = new FileOutputStream(temp);
		           	  fos.write("<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:dcmitype=\"http://purl.org/dc/dcmitype/\" xmlns:dcterms=\"http://purl.org/dc/terms/\" xmlns:ecrm=\"http://erlangen-crm.org/current/\" xmlns:europeana=\"http://www.europeana.eu/schemas/ese/\" xmlns:foaf=\"http://xmlns.com/foaf/0.1/\" xmlns:geo=\"http://www.w3.org/2003/01/geo/wgs84_pos#\" xmlns:nm=\"http://nomisma.org/id/\" xmlns:nmo=\"http://nomisma.org/ontology#\" xmlns:oa=\"http://www.w3.org/ns/oa#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:pelagios=\"http://pelagios.github.io/vocab/terms#\" xmlns:relations=\"http://pelagios.github.io/vocab/relations#\" xmlns:skos=\"http://www.w3.org/2004/02/skos/core#\" xmlns:t=\"http://www.tei-c.org/ns/1.0\" xmlns:void=\"http://rdfs.org/ns/void#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" />".getBytes("UTF-8"));
		           	  fos.close();
		           	  Repository.addDatastream(CIRILO_BACKBONE, "PELAGIOS_TEMPLATE","",  "X", "text/xml", temp);
	   		      }		
	   		    }
	   		finally {}
 		    try {
	   		      if (!Repository.exists(CIRILO_BACKBONE, "PELAGIOS_STYLESHEET")) {	
	   		    	  FileOutputStream fos = new FileOutputStream(temp);
		           	  fos.write(("<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:dcmitype=\"http://purl.org/dc/dcmitype/\" xmlns:dcterms=\"http://purl.org/dc/terms/\" xmlns:ecrm=\"http://erlangen-crm.org/current/\" xmlns:europeana=\"http://www.europeana.eu/schemas/ese/\" xmlns:foaf=\"http://xmlns.com/foaf/0.1/\" xmlns:geo=\"http://www.w3.org/2003/01/geo/wgs84_pos#\" xmlns:nm=\"http://nomisma.org/id/\" xmlns:nmo=\"http://nomisma.org/ontology#\" xmlns:oa=\"http://www.w3.org/ns/oa#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:pelagios=\"http://pelagios.github.io/vocab/terms#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:relations=\"http://pelagios.github.io/vocab/relations#\" xmlns:skos=\"http://www.w3.org/2004/02/skos/core#\" xmlns:t=\"http://www.tei-c.org/ns/1.0\" xmlns:void=\"http://rdfs.org/ns/void#\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" exclude-result-prefixes=\"xs\" version=\"2.0\">"+
					            "<xsl:param name=\"context\" /><xsl:param name=\"pid\" /><xsl:param name=\"model\" />"+
					            "<xsl:template match=\"/\"><entry>"+
					            "<pelagios:AnnotatedThing rdf:about=\""+host+"/{$context}/PELAGIOS#{$pid}\">"+
					            "<foaf:homepage rdf:resource=\""+host+"/{$pid}\" />"+
					            "</pelagios:AnnotatedThing>"+
					            "<oa:Annotation rdf:about=\""+host+"/{$context}/PELAGIOS#{$pid}/annotations/01\">"+
					            "<oa:hasTarget rdf:resource=\""+host+"/{$context}/PELAGIOS#{$pid}\"/>"+
					            "<oa:hasBody rdf:resource=\"http://pleiades.stoa.org/places/\"/>"+
					            "</oa:Annotation></entry></xsl:template></xsl:stylesheet>").getBytes("UTF-8"));
		           	  fos.close();
	   		      }		
	   		    }
	   		finally {}					   		    
 		    try {
	   		      if (!Repository.exists(CIRILO_BACKBONE, "CMIF_TEMPLATE")) {	
	   		    	  FileOutputStream fos = new FileOutputStream(temp);
		           	  fos.write(("<TEI xmlns=\"http://www.tei-c.org/ns/1.0\">"+
		           		"<teiHeader><fileDesc><titleStmt><title>Title</title></titleStmt>"+
		                "<publicationStmt><p>Publication Information</p></publicationStmt>"+
		                "<sourceDesc><p>Information about the source</p></sourceDesc>"+
		                "</fileDesc></teiHeader>"+
		                "<text><body><p>Some text here.</p></body></text>"+
		                "</TEI>").getBytes("UTF-8"));
		           	  fos.close();
		           	  Repository.addDatastream(CIRILO_BACKBONE, "CMIF_TEMPLATE","",  "X", "text/xml", temp);
	   		      }		
	   		    }
	   		finally {}
 		    try {
	   		      if (!Repository.exists(CIRILO_BACKBONE, "CMIF_STYLESHEET")) {	
	   		    	  FileOutputStream fos = new FileOutputStream(temp);
		           	  fos.write(("<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" xmlns=\"http://www.tei-c.org/ns/1.0\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" version=\"2.0\">"+
					            "<xsl:param name=\"context\" /><xsl:param name=\"pid\" />"+
					            "<xsl:template match=\"/\"><entry>"+
					            "<correspDesc ref=\""+host+"/{$pid}\">"+
		           	  			"<correspAction type=\"sent\"><persName ref=\"http://d-nb.info/gnd/\"/>"+
		           	  			"<date when=\"1881-12\"/><placeName ref=\"http://www.geonames.org/\"/></correspAction>"+
		           	  			"<correspAction type=\"received\"><persName ref=\"http://d-nb.info/gnd/\"/></correspAction></correspDesc>"+
		           			    "</entry></xsl:template></xsl:stylesheet>").getBytes("UTF-8"));
		           	  fos.close();
		           	  Repository.addDatastream(CIRILO_BACKBONE, "CMIF_STYLESHEET","",  "X", "text/xml", temp);
	   		      }		
	   		    }
	   		finally {} 		    
 		    try {
	   		      if (!Repository.exists(CIRILO_BACKBONE, "KML_TEMPLATE")) {	
	   		    	  FileOutputStream fos = new FileOutputStream(temp);
		           	  fos.write((" <k:kml xmlns:k=\"http://earth.google.com/kml/2.0\">"+
		           			  "<k:Document>"+
		           			  "<k:Folder>"+
		           			  "<k:name/>"+
		           			  "</k:Folder>"+
		           			  "</k:Document>"+
		           			  "</k:kml>").getBytes("UTF-8"));
		           	  fos.close();
		           	  Repository.addDatastream(CIRILO_BACKBONE, "KML_TEMPLATE","",  "X", "text/xml", temp);
	   		      }		
	   		    }
	   		finally {}			
 		    try {
	   		      if (!Repository.exists(CIRILO_BACKBONE, "KML_STYLESHEET")) {	
	   		    	  FileOutputStream fos = new FileOutputStream(temp);
		           	  fos.write(("<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" xmlns:k=\"http://earth.google.com/kml/2.0\" xmlns:t=\"http://www.tei-c.org/ns/1.0\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" exclude-result-prefixes=\"xs\" version=\"2.0\">"+
					            "<xsl:param name=\"context\" />"+
					            "<xsl:param name=\"pid\" />"+
					            "<xsl:param name=\"model\" />"+
					            "<xsl:template match=\"/\">"+
					            "<entry>"+
					            "<xsl:for-each select=\"//t:placeName[contains(@ref, 'http://www.geonames.org/')]\">"+
					            "<k:Placemark>"+
			                    "<k:name>"+
			                    "<xsl:value-of select=\"//t:titleStmt/t:title\" />"+
			                    "</k:name>"+
			                    "<k:address>"+
			                    "<xsl:value-of select=\".\" />"+
			                    "</k:address>"+
			                    "<k:description>"+
			                    "<xsl:text>&lt;a target=\"_blank\" href=\"http://fedora.host/</xsl:text>"+
			                    "<xsl:value-of select=\"$pid\" />"+
			                    "<xsl:text>\"&gt;&lt;img src=\"http://fedora.host/fedora/objects/</xsl:text>"+
			                    "<xsl:value-of select=\"$pid\" />"+
			                    "<xsl:text>/datastreams/THUMBNAIL/content\"/&gt;&lt;/a&gt;</xsl:text>"+
			                    "</k:description>"+
			                    "<k:Point>"+
			                    "<k:coordinates>"+
			                    "<xsl:variable name=\"key\" select=\"substring-after(@key,'#')\" />"+
			                    "<xsl:value-of select=\"//t:placeName[@xml:id = $key]/t:location/t:geo\" />"+
			                    "</k:coordinates>"+
			                    "</k:Point>"+
			                    "</k:Placemark>"+
			                    "</xsl:for-each>"+
			                    "</entry>"+
			                    "</xsl:template>"+
			                    "</xsl:stylesheet>").getBytes("UTF-8"));
		           	  fos.close();
		           	  Repository.addDatastream(CIRILO_BACKBONE, "KML_STYLESHEET","",  "X", "text/xml", temp);
	   		      }		
	   		    }
	   		finally {}								   		   					   		    
 		    try {
	   		      if (!Repository.exists(CIRILO_BACKBONE, "MEItoRDF")) {	
	   		    	  FileOutputStream fos = new FileOutputStream(temp);
		           	  fos.write(("<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" xmlns=\"http://www.tei-c.org/ns/1.0\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" version=\"2.0\"/>").getBytes("UTF-8"));
		           	  fos.close();
		           	  Repository.addDatastream(CIRILO_BACKBONE, "MEItoRDF","",  "X", "text/xml", temp);
		           	  Repository.addDatastream(CIRILO_ENVIRONMENT, "MEItoRDF","",  "X", "text/xml", temp);
	   		      }		
	   		    }
	   		finally {}
 		    try {
	   		      if (!Repository.exists(CIRILO_BACKBONE, "MEItoHTML")) {	
	   		    	  FileOutputStream fos = new FileOutputStream(temp);
		           	  fos.write(("<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" xmlns=\"http://www.tei-c.org/ns/1.0\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" version=\"2.0\"/>").getBytes("UTF-8"));
		           	  fos.close();
		           	  Repository.addDatastream(CIRILO_BACKBONE, "MEItoHMTL","",  "X", "text/xml", temp);
		           	  Repository.addDatastream(CIRILO_ENVIRONMENT, "MEItoHMTL","",  "X", "text/xml", temp);
	   		      }		
	   		    }
	   		finally {}
 		    try {
	   		      if (!Repository.exists(CIRILO_BACKBONE, "OAItoDC_MAPPING")) {	
	   		    	  FileOutputStream fos = new FileOutputStream(temp);
		           	  fos.write("<mm:metadata-mapping xmlns:mm=\"http://mml.uni-graz.at/v1.0\"><oai_dc:dc xmlns:oai=\"http://www.openarchives.org/OAI/2.0/\" xmlns:oai_dc=\"http://www.openarchives.org/OAI/2.0/oai_dc/\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:dcterms=\"http://purl.org/dc/terms/\" xmlns:europeana=\"http://www.europeana.eu/schemas/ese/\" /></mm:metadata-mapping>".getBytes("UTF-8"));
		           	  fos.close();
		           	  Repository.addDatastream(CIRILO_BACKBONE, "OAItoDC_MAPPING","",  "X", "text/xml", temp);
		           	  Repository.addDatastream(CIRILO_ENVIRONMENT, "OAItoDC_MAPPING","",  "X", "text/xml", temp);
	   		      }		
	   		    }
	   		finally {}
 		    try {
	   		      if (!Repository.exists(CIRILO_BACKBONE, "LIDOtoHTML")) {	
	   		    	  FileOutputStream fos = new FileOutputStream(temp);
		           	  fos.write("<xsl:stylesheet version=\"1.0\" xmlns:lido=\"http://www.lido-schema.org\" xmlns:bibtex=\"http://bibtexml.sf.net/\" xmlns:t=\"http://www.tei-c.org/ns/1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\"/>".getBytes("UTF-8"));
		           	  fos.close();
		           	  Repository.addDatastream(CIRILO_BACKBONE, "LIDOtoHTML","",  "X", "text/xml", temp);
		           	  Repository.addDatastream(CIRILO_ENVIRONMENT, "LIDOtoHTML","",  "X", "text/xml", temp);
	   		      }		
	   		    }
	   		finally {}
 		    try {
	   		      if (!Repository.exists(CIRILO_BACKBONE, "LIDOtoFO")) {	
	   		    	  FileOutputStream fos = new FileOutputStream(temp);
		           	  fos.write("<xsl:stylesheet version=\"1.0\" xmlns:fo=\"http://www.w3.org/1999/XSL/Format\" xmlns:lido=\"http://www.lido-schema.org\" xmlns:bibtex=\"http://bibtexml.sf.net/\" xmlns:t=\"http://www.tei-c.org/ns/1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\"/>".getBytes("UTF-8"));
		           	  fos.close();
		           	  Repository.addDatastream(CIRILO_BACKBONE, "LIDOtoFO","",  "X", "text/xml", temp);
		           	  Repository.addDatastream(CIRILO_ENVIRONMENT, "LIDOtoFO","",  "X", "text/xml", temp);
	   		      }		
	   		    }
	   		finally {}
 		    try {
	   		      if (!Repository.exists(CIRILO_BACKBONE, "LIDOtoDC_MAPPING")) {	
	   		    	  FileOutputStream fos = new FileOutputStream(temp);
	   	          	  fos.write("<mm:metadata-mapping xmlns:mm=\"http://mml.uni-graz.at/v1.0\"><oai_dc:dc xmlns:lido=\"http://www.lido-schema.org\" xmlns:oai_dc=\"http://www.openarchives.org/OAI/2.0/oai_dc/\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:dcterms=\"http://purl.org/dc/terms/\" xmlns:europeana=\"http://www.europeana.eu/schemas/ese/\"/></mm:mapping>".getBytes("UTF-8"));
	 				  fos.close();
		           	  Repository.addDatastream(CIRILO_BACKBONE, "LIDOtoDC_MAPPING","",  "X", "text/xml", temp);
		           	  Repository.addDatastream(CIRILO_ENVIRONMENT, "LIDOtoDC_MAPPING","",  "X", "text/xml", temp);
	   		      }		
	   		    }
	   		finally {}
		} catch (Exception e) {}    
		finally {   
 		    temp.delete();
		}    

		
	}
	
	
	public void updateDatastreams(String s) {
	
		try {
			if (Repository.hasContentModel(s,"info:fedora/cm:TEI")) {	
				if(!Repository.exists(s, "HSSF_STYLESHEET")) {										
					Repository.addDatastream(s, "HSSF_STYLESHEET",  "Stylesheet to generate HSSF stream", "text/xml", fedora+"/get/cirilo:Backbone/TEItoHSSF");
				}	
				if(!Repository.exists(s, "R_STYLESHEET")) {										
					Repository.addDatastream(s, "HSSF_STYLESHEET",  "Stylesheet to generate a data matrix", "text/xml", fedora+"/get/cirilo:Backbone/R_STYLESHEET");
				}	
				if(!Repository.exists(s, "LATEX_STYLESHEET")) {										
					Repository.addDatastream(s, "LATEX_STYLESHEET",  "Stylesheet to generate LaTeX PDF", "text/xml", host+"/tei/latex/latex.xsl");
				}	
			}
	

			if (Repository.hasContentModel(s,"info:fedora/cm:dfgMETS")) {																			
				if(!Repository.exists(s, "METS_REF")) {
					Repository.addDatastream(s, "METS_REF",  "Reference to source stream", "text/xml", fedora+"/get/"+s+"/METS_SOURCE");
				}	
			}

			if (Repository.hasContentModel(s,"info:fedora/cm:LIDO")) {																			
				if(!Repository.exists(s, "SOURCE_REF")) {
					Repository.addDatastream(s, "SOURCE_REF",  "Reference to source stream", "text/xml", fedora+"/get/"+s+"/LIDO_SOURCE");
				}	
			}

			if (Repository.hasContentModel(s,"info:fedora/cm:Context")) {	
				if(!Repository.exists(s, "HSSF_STYLESHEET")) {										
					Repository.addDatastream(s, "HSSF_STYLESHEET",  "Stylesheet to generate HSSF stream", "text/xml", fedora+"/get/cirilo:Backbone/HSSF_STYLESHEET");
				}	
				if(Repository.exists(s, "KML_TEMPLATE")) {										
					Repository.purgeDatastream(s, "KML_TEMPLATE");
				}	
			}
		} catch (Exception e) {}
	}

}
