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


package org.emile.cirilo.dialog;

import org.emile.cirilo.*;
import org.emile.cirilo.business.MDMapper;
import org.emile.cirilo.business.Session;
import org.emile.cirilo.ecm.repository.*;
import org.emile.cirilo.ecm.repository.FedoraConnector.Relation;

import voodoosoft.jroots.application.*;
import voodoosoft.jroots.core.CServiceProvider;
import voodoosoft.jroots.dialog.*;
import voodoosoft.jroots.exception.CException;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.File;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import javax.swing.JEditorPane;
import javax.swing.JTable;

import org.jdom.input.DOMBuilder;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;

public class TemplaterDialog extends CDefaultDialog {

	private static Logger log = Logger.getLogger(TemplaterDialog.class);

	/**
	 *  Constructor for the SelectLayoutDialog object
	 */
	public TemplaterDialog() {
		format = Format.getRawFormat();
		format.setEncoding("UTF-8");
		outputter = new XMLOutputter(format);						
	}
	
	/**
	 *  Gets the accessContext attribute of the SelectLayoutDialog object
	 *
	 * @return    The accessContext value
	 */
	public IAccessContext getAccessContext() {
		CDefaultAccessContext loCxt = null;

		try {
		} catch (Exception ex) {
			CException.record(ex, this);
		}
		return loCxt;
	}


	/**
	 *  Description of the Method
	 *
	 * @param  e  Description of the Parameter
	 */
	public void handleCloseButton(ActionEvent e)
			throws Exception {
			org.emile.cirilo.dialog.CBoundSerializer.save(this.getCoreDialog(), se.getTemplaterDialogProperties(), (JTable) null);   
			close();
		}

	public void handleShowButton(ActionEvent e)
		   throws Exception {
		   String pid = (String) moGA.getInput("jtfPID");
		   ArrayList triples = Repository.getTriples("select * where { <info:fedora/"+pid+"/TEI_SOURCE> <http://gams.uni-graz.at#hasPart> ?subject .  ?subject ?predicate ?object   } order by ?subject");
		   logm="";
		   for(int i=0; i<triples.size();i++) {
			   logm+=(String)triples.get(i)+"\n";
		   }
		   jep.setText(logm);
		}

	/**
	 *  Description of the Method
	 *
	 * @param  e              Description of the Parameter
	 * @exception  Exception  Description of the Exception
	 */
	public void handleStartButton(ActionEvent ea) {
            try {
            	getCoreDialog().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		        
            	int id = 0;
			    String stream = "";
			    String rdf ="";
			    HashMap <String,String>fragments = new HashMap<String,String>();

			    String pid = (String) moGA.getInput("jtfPID");
                results.setText("");
                jep.setText("");
			    
			    logm = "";
		 		try {
		 			
		 			 
		 				SAXBuilder builder = new SAXBuilder(); 						 						
		 				org.jdom.Document mapping = builder.build( new StringReader( template.getText()) );			
				    	DOMBuilder db = new DOMBuilder();	
				    	Document doc = db.build (Repository.getDatastream(pid, "TEI_SOURCE"));
				    			
				    	
		 				List ns = mapping.getRootElement().getAdditionalNamespaces();

		            				 				
		 				XPath xpath = XPath.newInstance("//rdf:Description");
		 				xpath.addNamespace( Common.xmlns_rdf);
		 				
		 				List refs = (List) xpath.selectNodes( mapping );
		 				
		 			    if (refs.size() > 0 ) {
		 			        for (Iterator iter = refs.iterator(); iter.hasNext();) {
		 			        	try {
		 			        		
		 			        		Element e = (Element) iter.next();
		 			 				for (Iterator jter = ns.iterator(); jter.hasNext();) {
		 		 						try {
		 		 							Namespace node = (Namespace) jter.next();
		 		 							e.addNamespaceDeclaration(node);	   				
		 		 						} catch (Exception ex) {}		
		 		 				    }										

		 			        		String about = e.getAttributeValue("about",Common.xmlns_rdf);
		 							
		 			        		XPath qpath = XPath.newInstance("//"+about);
		 			        		qpath.addNamespace( Common.xmlns_tei_p5 );
		 			        		List elems = (List) qpath.selectNodes( doc);

		 			        		for (Iterator jter = elems.iterator(); jter.hasNext();) {
		 			        			MDMapper m =null;;
		           			        	try {
		         			        		Element q = (Element) jter.next();
		         			        		String p = q.getAttributeValue("id",Common. xmlns_xml );
		         			        		if (p == null) {
		         			        			p = "ID."+new Integer(++id).toString();
		         			        			q.setAttribute("id", p, Common.xmlns_xml);
		         			        		}
//		         			        		String ref  = "//"+ (about.contains("]") ? about.replaceFirst("(.*)\\[(.*)", "$1[($2").replaceFirst( "(.*)\\]", "$1) and @xml:id='"+p+"']") : about+"[@xml:id='"+p+"']");
		        			        		String ref  = "//"+  about+"[@xml:id='"+p+"']";

		         			        		fragments.put(p,p);
		         			        		Element map = new Element("metadata-mapping", Common.xmlns_mm);
		        			        	    String mm = "<mm:metadata-mapping xmlns:mm= \""+Common.xmlns_mm.getURI()+"\" xmlns:t= \""+Common.xmlns_tei_p5.getURI()+"\">"+outputter.outputString(e).replaceAll("[.]/", ref+"/")+"</mm:metadata-mapping>"; 			        		
		        			        	    m = new MDMapper(pid, mm, true);
	        			        	    	org.jdom.Document r = builder.build( new StringReader (m.transform(doc) ));							
	        			        	    	Element root = r.getRootElement();         			        	
	        			        	    	root.setAttribute("about", "info:fedora/"+pid+"/TEI_SOURCE#"+p, Common.xmlns_rdf);         			        	
	        			        	    	root.removeNamespaceDeclaration(Common.xmlns_tei_p5);
	             			        		stream +=outputter.outputString(r).substring(39);         			        		
		         			            } catch (Exception ex) { 
                                            Logger(ex);
		         			        		continue;	
		         			        	}
		         			        }                                                        
		 			        			        		
		 			            } catch (Exception eq) {
		 			            	Logger(eq);
		 			        		continue;	
		 			        	}
		 			        }
		 			         			
		 			        
		 			       for ( String elem : fragments.keySet() )
		 			    	   rdf+= "<rel:hasPart rdf:resource=\"info:fedora/"+pid+"/TEI_SOURCE#"+elem+"\"/>";
		 			       }
				    	   rdf+= "<rel:isPartOf rdf:resource=\"info:fedora/context:"+this.user.getUser()+"\"/>";

				    	   java.util.List  <org.emile.cirilo.ecm.repository.FedoraConnector.Relation>relations = Repository.getRelations(pid,Common.isMemberOf);	
					        for (Relation r : relations) {
					        	   String s=r.getTo(); 
						    	   rdf+= "<rel:isPartOf rdf:resource=\""+s+"\"/>";				        	  
					        }
					        
							rdf = "<rdf:RDF xmlns:rdf=\""+Common.xmlns_rdf.getURI()+"\"  xmlns:rel=\""+Common.xmlns_gams.getURI()+"\">"+ 
			 			      "<rdf:Description rdf:about=\"info:fedora/"+pid+"/TEI_SOURCE\">" +
								rdf +	
			 			     "</rdf:Description>"+
			 			      stream+
			 			     "</rdf:RDF>";
			 			      
 					    	results.setText(new String(outputter.outputString(builder.build( new StringReader( rdf.replaceAll("[>]",">\n")))).getBytes("UTF-8"),"UTF-8"));
		 					File f = null;

		 					/*String success =  "Datastream 'RELS-INT' from '"+pid+"' successfully updated";*/
			 					Object[] args = {pid};
			 					MessageFormat msgFmt = new MessageFormat(res.getString("relsintsuc"));
			 					String success = msgFmt.format(args);

			 							 					
		 							
 						    try {
 			 					Repository.modifyDatastreamByValue(pid, "RELS-INT", "text/xml", rdf);
		 						logm +=success;
 			 				} catch (Exception ex) {
 			 					try {
 			 						f= File.createTempFile("temp", ".tmp");	    					
 			 						FileOutputStream fos = new FileOutputStream(f.getAbsolutePath());
 			 						BufferedWriter out = new BufferedWriter(new OutputStreamWriter( fos, "UTF-8" ) );
 			 						out.write(rdf);
 			 						out.close();
 			 						Repository.addDatastream(pid, "RELS-INT",  "RDF Statements about the TEI_SOURCE stream", "X", "text/xml", f);
 			 						logm +=success;
 	 			 		 		} catch (Exception e) {
 	 			 		 			logm+=res.getString("valerror")+": "+e.getMessage()+"\n";
 	 			 		 		}
 			 					finally {
 			 						f.delete(); 					 					 			 						
 			 					}
 			 		 		}
 			  						    		 					
		 		} catch (Exception et) {
		            	Logger(et);
		 		}
		 		
	 		} catch (Exception es) {
	            	Logger(es);
	 		}	
            finally {
            	jep.setText(logm);
				getCoreDialog().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
           }
	}

		
	private void Logger (Exception e) {
		logm+= e.getMessage()+"\n";
	}
	
	public void show()  throws CShowFailedException {
	  try {	
			se = (Session) CServiceProvider.getService( ServiceNames.SESSIONCLASS );						
			org.emile.cirilo.dialog.CBoundSerializer.load(this.getCoreDialog(), se.getTemplaterDialogProperties(), (JTable) null);
   	 
	  } catch (Exception e) {
	  }
	}


	/**
	 *  Description of the Method
	 */
	protected void cleaningUp() {
	}


	/**
	 *  Description of the Method
	 *
	 * @return    Description of the Return Value
	 */
	protected boolean closing() {
		try {
		} catch (Exception e) {
			CException.record(e, this, false);
		}

		return true;
	}
    

	/**
	 *  Description of the Method
	 *
	 * @exception  COpenFailedException  Description of the Exception
	 */
	protected void opened() throws COpenFailedException {

		try {
 			 moGA = (IGuiAdapter) getGuiAdapter();
			 res =(ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);
			 user = (User) CServiceProvider.getService(ServiceNames.CURRENT_USER);

			 se = (Session) CServiceProvider.getService( ServiceNames.SESSIONCLASS );						
			 org.emile.cirilo.dialog.CBoundSerializer.load(this.getCoreDialog(), se.getSelectDialogProperties(), (JTable) null);

			 template = (JEditorPane) getGuiComposite().getWidget("jtaTemplate");
			 template.setContentType("text/xml");
			 results = (JEditorPane) getGuiComposite().getWidget("jtaResult");
			 results.setContentType("text/xml");
			 jep = (JEditorPane) getGuiComposite().getWidget("jtaLog");
			 jep.setContentType("text/plain");

			
			CDialogTools.createButtonListener(this, "jbClose", "handleCloseButton");
			CDialogTools.createButtonListener(this, "jbStart", "handleStartButton");
			CDialogTools.createButtonListener(this, "jbShow", "handleShowButton");
			
		} catch (Exception ex) {
			throw new COpenFailedException(ex);
		} finally {
		}
	}


	private IGuiAdapter moGA;
	private ResourceBundle res; 
	private Session se;
	private User user;
	private String filename;
	private XMLOutputter outputter;
	private Format format;
	private JEditorPane template;
	private JEditorPane results;
	private JEditorPane jep;
	private String logm;
}


