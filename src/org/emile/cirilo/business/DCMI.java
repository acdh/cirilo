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

import voodoosoft.jroots.core.CServiceProvider;
import voodoosoft.jroots.dialog.*;

import org.emile.cirilo.Common;
import org.emile.cirilo.ServiceNames;
import org.emile.cirilo.ecm.repository.*;
import org.emile.cirilo.ecm.exceptions.FedoraConnectionException;

import java.util.*;

import org.apache.log4j.Logger;

import javax.swing.JTextField;

import org.jdom.*;
import org.jdom.output.*;
import org.jdom.filter.ElementFilter;
import org.jdom.input.*;
import org.jdom.xpath.XPath;

/**
 * Description of the Class
 * 
 * @author Johannes Stigler
 * @created 10.3.2011
 */
public class DCMI {
	
    private static Logger log = Logger.getLogger(DCMI.class);

	private Document rules;
	private Document dcms;

	public DCMI(String file) {
		try {
			SAXBuilder builder = new SAXBuilder();
			this.rules = builder.build(file);
		} catch (Exception e) {
			this.rules = null;
		}
		init();
	}

	/**
	 * Constructor for the LoginDialog object
	 */

	public DCMI() {
		this.rules = null;
		init();
	}

	private void init() {
		dcms = new Document();
		dcms.addContent(new Element("dc", org.emile.cirilo.Common.xmlns_oai_dc));
		dcms.getRootElement().addNamespaceDeclaration(org.emile.cirilo.Common.xmlns_dc);
	}

	public void preallocate(CDefaultGuiAdapter moGA) {
		try {
			ArrayList preallocations = (ArrayList) CServiceProvider
					.getService(ServiceNames.DCMI_PREALLOCATIONS);

			for (int i = 0; i < org.emile.cirilo.Common.DCMI.length; i++) {
				JTextField jtfDCMI = ((JTextField) moGA.getWidget("jtf"
						+ org.emile.cirilo.Common.DCMI[i]));
				jtfDCMI.setText((String) preallocations.get(i));
			}
		} catch (Exception e) {
		  	log.error(e.getLocalizedMessage(),e);		  			
		}

	}

	public void reset(CDefaultGuiAdapter moGA) {
		try {
			ArrayList preallocations = (ArrayList) CServiceProvider
					.getService(ServiceNames.DCMI_PREALLOCATIONS);

			for (int i = 0; i < org.emile.cirilo.Common.DCMI.length; i++) {
				JTextField jtfDCMI = ((JTextField) moGA.getWidget("jtf"
						+ org.emile.cirilo.Common.DCMI[i]));
				jtfDCMI.setText(i > 0 ? "" : org.emile.cirilo.Common.UNTITLED);
				preallocations.set(i, "");
			}

		} catch (Exception e) {
		  	log.error(e.getLocalizedMessage(),e);		  			
		}

	}

	public void save(CDefaultGuiAdapter moGA) {
		try {
			ArrayList preallocations = (ArrayList) CServiceProvider
					.getService(ServiceNames.DCMI_PREALLOCATIONS);

			for (int i = 0; i < org.emile.cirilo.Common.DCMI.length; i++) {
				JTextField jtfDCMI = ((JTextField) moGA.getWidget("jtf"
						+ org.emile.cirilo.Common.DCMI[i]));
					preallocations.set(i, jtfDCMI.getText());
			}
			CServiceProvider.addService(preallocations,
					ServiceNames.DCMI_PREALLOCATIONS);

		} catch (Exception e) {
		  	log.error(e.getLocalizedMessage(),e);		  
		}

	}

	public void write(String pid, CDefaultGuiAdapter moGA, boolean isOAI_PMH)
			throws FedoraConnectionException {

		String ds = "RELS-EXT";

		try {
			Element oai = new Element("dc",
					org.emile.cirilo.Common.xmlns_oai_dc);
			oai.addNamespaceDeclaration(org.emile.cirilo.Common.xmlns_dc);

			Element dc = new Element("identifier",
					org.emile.cirilo.Common.xmlns_dc);
			dc.addContent(pid);
			oai.addContent(dc);

			ArrayList preallocations = (ArrayList) CServiceProvider
					.getService(ServiceNames.DCMI_PREALLOCATIONS);
			for (int i = 0; i < org.emile.cirilo.Common.DCMI.length; i++) {

				if (i==0) {
					JTextField jtfDCMI = ((JTextField) moGA.getWidget("jtf"+ org.emile.cirilo.Common.DCMI[i]));
					jtfDCMI.setText( jtfDCMI.getText().trim().length() == 0 ? org.emile.cirilo.Common.UNTITLED : jtfDCMI.getText());
				}

				preallocations.set(
						i,
						(String) moGA.getText("jtf"
								+ org.emile.cirilo.Common.DCMI[i]));
				StringTokenizer st = new StringTokenizer(
						(String) preallocations.get(i), "~");
				if (st.hasMoreTokens()) {
					while (st.hasMoreTokens()) {
						String s = st.nextToken();
						dc = new Element(
								org.emile.cirilo.Common.DCMI[i].toLowerCase(),
								org.emile.cirilo.Common.xmlns_dc);
						dc.addContent(s);
						oai.addContent(dc);
					}
				}
			}

			org.jdom.Document doc = new Document(oai);
			Format format = Format.getRawFormat();
			format.setOmitEncoding(true);
			XMLOutputter outputter = new XMLOutputter(format);

			
			doc = Common.validate(doc);
			Repository.modifyDatastreamByValue(pid, "DC", "text/xml", outputter.outputString(doc));
			
			
			DOMBuilder builder = new DOMBuilder();
			doc = builder.build (Repository.getDatastream(pid, "RELS-EXT"));
	        Element rdf = doc.getRootElement().getChild("Description", Common.xmlns_rdf);
	        rdf.removeContent(new ElementFilter("itemID"));			                              
			if (isOAI_PMH) {
	        	oai = new Element("itemID", Common.xmlns_oai);
	        	oai.addContent(Common.OAIPHM()+pid);
	        	rdf.addContent(oai);
		     }
		     Repository.modifyDatastreamByValue(pid, "RELS-EXT", "text/xml", outputter.outputString(doc));

/*
 *
			if (isOAI_PMH) {
				DOMBuilder builder = new DOMBuilder();
				doc = builder.build(Repository.getDatastream(
						Repository.ensurePID(pid), ds));

				Element item = new Element("itemID",
						org.emile.cirilo.Common.xmlns_oai);
				item.addContent("oai:kfug:" + pid);
				doc.getRootElement()
						.getChild("Description",
								org.emile.cirilo.Common.xmlns_rdf)
						.addContent(item);

				outputter = new XMLOutputter(format);
				LOG.info(outputter.outputString(doc));
				Repository.modifyDatastreamByValue(pid, ds, "text/xml", outputter.outputString(doc));
			}
*/
		} catch (Exception e) {
		  	log.error(e.getLocalizedMessage(),e);		  
			throw new FedoraConnectionException(
					"Something went wrong in the connection with fedora", e);
		}

	}

	public Document map(Document doc) {

		try {

			List rule = rules.getRootElement().getChildren();
			Iterator it = rule.iterator();

			init();

			while (it.hasNext()) {
				Element e = (Element) it.next();
				String delimiter = (e.getAttributeValue("delimiter") == null ? new String(
						" ") : e.getAttributeValue("delimiter"));
				Element dc = new Element(e.getAttributeValue("to"),
						org.emile.cirilo.Common.xmlns_dc);

				try {
					String s = new String();
					if (e.getAttributeValue("type") == null
							|| !e.getAttributeValue("type").equals("fixed")) {
						XPath path = XPath.newInstance(e.getAttributeValue("from"));
						path.addNamespace(org.emile.cirilo.Common.xmlns_tei_p5);
						List nodes = (List) path.selectNodes(doc);
						Iterator jt = nodes.iterator();
						while (jt.hasNext()) {
							Element t = (Element) jt.next();
							s = s + t.getTextTrim();
							if (jt.hasNext())
								s += delimiter;
						}
					} else {
						s = e.getAttributeValue("from");
					}
					dc.addContent(s);
					dcms.getRootElement().addContent(dc);

				} catch (Exception et) {
				}
			}
		} catch (Exception e) {
		  	log.error(e.getLocalizedMessage(),e);		  
		}

		return dcms;
	}

	public String toString() {
		Format format = Format.getRawFormat();
		format.setOmitEncoding(true);
		XMLOutputter outputter = new XMLOutputter(format);
		outputter = new XMLOutputter(format);
		return outputter.outputString(dcms);
	}

}
