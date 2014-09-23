/*
 *  -----------------------------------------------------------------------------
 *
 *  <p><b>License and Copyright: </b>The contents of this file are subject to the
 *  Educational Community License (the "License"); you may not use this file
 *  except in compliance with the License. You may obtain a copy of the License
 *  at <a href="http://www.opensource.org/licenses/ecl1.txt">
 *  http://www.opensource.org/licenses/ecl1.txt.</a></p>
 *
 *  <p>Software distributed under the License is distributed on an "AS IS" basis,
 *  WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 *  the specific language governing rights and limitations under the License.</p>
 *
 *  <p>The entire file consists of original code.  Copyright &copy; 2005-2008 by
 *  Department of Information Processing in the Humanities, University of Graz.
 *  All rights reserved.</p>
 *
 *  -----------------------------------------------------------------------------
 */
package org.emile.cirilo.business;

import org.jdom.*;
import org.jdom.output.*;
import org.jdom.input.*;
import org.jdom.transform.JDOMResult;
import org.jdom.transform.JDOMSource;
import org.jdom.xpath.XPath;

import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.regex.*;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;



import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.emile.cirilo.Common;

/**
 * Description of the Class
 * 
 * @author Johannes Stigler
 * @created 10.3.2011
 */

public class MDMapper 
{
	private static final Log LOG = LogFactory.getLog(MDMapper.class);
    private static String stylesheet = null;
	
    private XMLOutputter outputter;
    private Format format;
    
    private String PID;
    private org.jdom.Document xslt;

    
	public MDMapper (String p, String s) {
		
		try
		{

			PID = p;
			format = Format.getRawFormat();
			format.setEncoding("UTF-8");
			outputter = new XMLOutputter(format);				

			initialize(s, false);
			
		} catch( Exception e) {
		}
		
	}

	public MDMapper (String p, String s, boolean mode) {
		
		try
		{

			PID = p;
			format = Format.getRawFormat();
			format.setEncoding("UTF-8");
			outputter = new XMLOutputter(format);				

			initialize(s ,true);
			
		} catch( Exception e) {
		}
		
	}
	
	public MDMapper (String p, File s) {
		
		try 
		{
			PID = p;
			format = Format.getRawFormat();
			format.setEncoding("UTF-8");
			outputter = new XMLOutputter(format);				
			SAXBuilder builder = new SAXBuilder();

			org.jdom.Document mapping = builder.build( s );								
			initialize(outputter.outputString(mapping), false);
			
		} catch( Exception e) {
		}
		
	}

	public String getXSLT() {
		return stylesheet;
	}
	
	private void initialize(String s, boolean mode) {
		
		try {
			SAXBuilder builder = new SAXBuilder();

			org.jdom.Document mapping = builder.build( new StringReader(s) );		
					
			XPath xPath = XPath.newInstance( "/mm:metadata-mapping");
			xPath.addNamespace( Common.xmlns_mm );

			Element  schema = (Element) xPath.selectSingleNode( mapping );		
			List nodes = schema.getChildren();
			Element root = (Element) nodes.get(0);
			
			stylesheet="<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" version=\"2.0\"><xsl:strip-space  elements=\"*\"/><xsl:template match=\"/\">"+outputter.outputString(root)+"</xsl:template>";
			if (!mode) {
				stylesheet += "<xsl:template name=\"transform\"><xsl:param name=\"expr\"/><xsl:param name=\"tag\"/><xsl:param name=\"delimiter\"/><xsl:choose><xsl:when test=\"$delimiter != 'null'\"><xsl:choose><xsl:when test=\"count($expr)=1\"><xsl:element name=\"{$tag}\"><xsl:value-of select=\"$expr\"/></xsl:element></xsl:when><xsl:otherwise><xsl:element name=\"{$tag}\"><xsl:for-each select=\"$expr\"><xsl:if test=\"position() &gt; 1\"><xsl:value-of select=\"$delimiter\"/></xsl:if><xsl:value-of select=\".\"/></xsl:for-each></xsl:element>    </xsl:otherwise></xsl:choose>                </xsl:when><xsl:otherwise><xsl:choose><xsl:when test=\"count($expr)=1\"><xsl:element name=\"{$tag}\"><xsl:value-of select=\"$expr\"/></xsl:element></xsl:when><xsl:otherwise><xsl:for-each select=\"$expr\"><xsl:element name=\"{$tag}\"><xsl:value-of select=\".\"/></xsl:element></xsl:for-each></xsl:otherwise></xsl:choose></xsl:otherwise></xsl:choose></xsl:template>";
			} else {	
				stylesheet += "<xsl:template name=\"transform\"><xsl:param name=\"expr\"/><xsl:param name=\"tag\"/><xsl:param name=\"delimiter\"/><xsl:choose><xsl:when test=\"count($expr)=1\"><xsl:element name=\"{$tag}\"><xsl:value-of select=\"$expr\"/></xsl:element></xsl:when><xsl:otherwise><xsl:for-each select=\"$expr\"><xsl:element name=\"{$tag}\"><xsl:value-of select=\".\"/></xsl:element></xsl:for-each></xsl:otherwise></xsl:choose></xsl:template>";
				stylesheet += "<xsl:template name=\"transform-uri\"><xsl:param name=\"expr\"/><xsl:param name=\"tag\"/><xsl:param name=\"delimiter\"/><xsl:choose><xsl:when test=\"count($expr)=1\"><xsl:element name=\"{$tag}\"><xsl:attribute name=\"rdf:resource\"><xsl:value-of select=\"$expr\"/></xsl:attribute></xsl:element></xsl:when><xsl:otherwise><xsl:for-each select=\"$expr\"><xsl:element name=\"{$tag}\"><xsl:attribute name=\"rdf:resource\"><xsl:value-of select=\".\"/></xsl:attribute></xsl:element></xsl:for-each></xsl:otherwise></xsl:choose></xsl:template>";
			}
			stylesheet +="</xsl:stylesheet>";
			stylesheet = stylesheet.replaceAll("this:PID", PID); 
			stylesheet = stylesheet.replaceAll("this:URN", Common.OAIPHM()+PID); 
						
			xslt = builder.build(new java.io.StringReader(stylesheet));		
			
			List ns = root.getAdditionalNamespaces();
			for (Iterator iter = ns.iterator(); iter.hasNext();) {
					try {
						Namespace node = (Namespace) iter.next();
						xslt.getRootElement().addNamespaceDeclaration(node);	   				
					} catch (Exception ex) {
					}		
			}						
			
	   	    
	   	    Namespace n = root.getNamespace();
	   	    if (n != null) xslt.getRootElement().addNamespaceDeclaration(n);
	   	    
			treeWalk(xslt.getRootElement(), xslt.getRootElement());			
			stylesheet = outputter.outputString(xslt);
			
		} catch( Exception e) {
		}
		
	}
	

	public String transform(Document doc) {
		try {
			String xalan = System.getProperty("javax.xml.transform.TransformerFactory");
	        System.setProperty("javax.xml.transform.TransformerFactory",  "net.sf.saxon.TransformerFactoryImpl");  

	        Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(new StringReader(stylesheet)));
	        JDOMSource in = new JDOMSource(doc);
	        JDOMResult out = new JDOMResult();
	        transformer.transform(in, out);

	        System.setProperty("javax.xml.transform.TransformerFactory",  "org.apache.xalan.processor.TransformerFactoryImpl");  
	        
			String s = outputter.outputString(out.getResult());			
			return s;
	     }
	     catch (Exception e) {
	        return "";
		 }
	}	

	public String transform(Element el) {
		
		try {
			String xalan = System.getProperty("javax.xml.transform.TransformerFactory");
	        System.setProperty("javax.xml.transform.TransformerFactory",  "net.sf.saxon.TransformerFactoryImpl");  

	        Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(new StringReader(stylesheet)));
	        JDOMSource in = new JDOMSource(el);
	        JDOMResult out = new JDOMResult();
	        transformer.transform(in, out);

	        System.setProperty("javax.xml.transform.TransformerFactory",  "org.apache.xalan.processor.TransformerFactoryImpl");  
				        
			String s = outputter.outputString(out.getResult());			
			return s;
	     }
	     catch (Exception e) {
		        return "";
		 }
	}	
	
	
    private void treeWalk(Element e, Element parent) 
    {
   	    List nodes = (List) e.getChildren();

            	       	      	    
   	    for (Iterator iter = nodes.iterator(); iter.hasNext();) {
   	    	try {
   				Element node = (Element) iter.next();
   				if (node.getName().equals("map")) {
   					String name = parent.getNamespacePrefix()+(parent.getNamespacePrefix().isEmpty() ? "" : ":") + parent.getName();
   					String select = node.getAttributeValue("select");
   					String delimiter = node.getAttributeValue("delimiter");
					XPath qPath = XPath.newInstance(select);
					qPath.addNamespace( Common.xmlns_mods );
   					Element p1 = new Element ("with-param", Common.xmlns_xsl);
   					try {
   						p1.setAttribute("name", "expr");
   						p1.setAttribute("select", select);
   					} catch (Exception ex) {}
   					Element p2 = new Element ("with-param", Common.xmlns_xsl);
   					try {
   						p2.setAttribute("name", "tag");
   						p2.setAttribute("select", "'"+name+"'" );
   					} catch (Exception ex) {}
   					Element p3 = new Element ("with-param", Common.xmlns_xsl);
   					try {
   						p3.setAttribute("name", "delimiter");
   						p3.setAttribute("select", "'"+delimiter+"'" );
   					} catch (Exception ex) {}
   					
   					parent.addContent(p1);
   					parent.addContent(p2); 				
   					parent.addContent(p3); 				
   					parent.setName("call-template");
 					parent.setAttribute("name","transform");
   					parent.setNamespace(Common.xmlns_xsl);
   					parent.removeChild("map", Common.xmlns_mm);
   				}
   				if (node.getName().equals("copy")) {
   					
   					String select = node.getAttributeValue("select");
   					Element p1 =   new Element ("copy-of", Common.xmlns_xsl);
                    p1.setAttribute("select", select);
                    parent.addContent(p1);
   					parent.removeChild("copy", Common.xmlns_mm);   					
   				}
   				if (node.getAttribute("resource", Common.xmlns_rdf) != null) {
   					String name = node.getNamespacePrefix()+(node.getNamespacePrefix().isEmpty() ? "" : ":") + node.getName();
   					String select = node.getAttribute("resource", Common.xmlns_rdf).getValue();
   					String delimiter = "null";
   					Element p1 = new Element ("with-param", Common.xmlns_xsl);
   					try {
   						p1.setAttribute("name", "expr");
   						p1.setAttribute("select", select);
   					} catch (Exception ex) {}
   					Element p2 = new Element ("with-param", Common.xmlns_xsl);
   					try {
   						p2.setAttribute("name", "tag");
   						p2.setAttribute("select", "'"+name+"'" );
   					} catch (Exception ex) {}
   					Element p3 = new Element ("with-param", Common.xmlns_xsl);
   					try {
   						p3.setAttribute("name", "delimiter");
   						p3.setAttribute("select", "'"+delimiter+"'" );
   					} catch (Exception ex) {}

   					node.addContent(p1);
   					node.addContent(p2); 				
   					node.addContent(p3); 				
   					node.setName("call-template");
 					node.setAttribute("name","transform-uri");
   					node.setNamespace(Common.xmlns_xsl);
   				}
                try { 				
                	xslt.getRootElement().addNamespaceDeclaration(node.getNamespace());
                } catch (Exception q) {
                }	
   				if (node.getChildren() != null) treeWalk(node, node);
   	    	} catch (Exception ex) {
   	    	}
   	    	
   	    }
    }    


    
}
