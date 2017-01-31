package org.emile.cirilo.business;

import java.io.FileWriter;
import java.io.StringReader;

import org.emile.cirilo.Common;
import org.emile.cirilo.ServiceNames;
import org.emile.cirilo.User;
import org.emile.cirilo.ecm.templates.TemplateSubsystem;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;
import org.apache.log4j.Logger;

import voodoosoft.jroots.core.CPropertyService;
import voodoosoft.jroots.core.CServiceProvider;

public class DC {

    private static Logger log = Logger.getLogger(DC.class);

	private Document dc;
	private Format format;
	private XMLOutputter outputter;
	private String PID;
	private User user;
	private boolean mode;
	private CPropertyService props;
	
	
	public DC (FileWriter logger, boolean validate, boolean mode) {		
		try {
			props = (CPropertyService) CServiceProvider.getService(ServiceNames.PROPERTIES);
			user = (User) CServiceProvider.getService(ServiceNames.CURRENT_USER);
			format = Format.getRawFormat();
			format.setEncoding("UTF-8");
			outputter = new XMLOutputter(format);
			this.mode = mode;
		} catch (Exception e) {
		  	log.error(e.getLocalizedMessage(),e);		  			
		}
	}
	
    public boolean set (String stream) {
    	try {
    		SAXBuilder builder = new SAXBuilder();
    		this.dc = builder.build(new StringReader(stream));
			this.PID = "";
   	        return true;
		} catch (Exception e) { 
		  	log.error(e.getLocalizedMessage(),e);		  			
			return false;
		}
	}
  
 	public String toString() { 		
 		this.dc = Common.validate(this.dc);
		return (this.outputter.outputString(this.dc).replaceAll("this:PID", this.PID).replaceAll("this:FEDORA", user.getUrl())); 
	}
	
 	public boolean isValid() {
 		return true;
 	}
 	 	
	public void setPID(String pid) { 
		try {
			XPath xpath = XPath.newInstance( "//dc:identifier[contains(.'o:')]" );
			xpath.addNamespace( Common.xmlns_dc);
			Element idno = (Element) xpath.selectSingleNode( dc );
			if (idno == null) {
				idno = new Element ("identifier", Common.xmlns_dc );
				idno.setText(Common.INFO_FEDORA+pid);
				xpath = XPath.newInstance("//oai_dc:dc");
				xpath.addNamespace( Common.xmlns_oai_dc );
				Element anchor = (Element) xpath.selectSingleNode( dc );
				anchor.addContent(idno);								
				this.PID = pid;
			} else {
				if (this.mode) { 
					idno.setText(pid);
					this.PID = pid;
				}
			}
		} catch (Exception e) {
		  	log.error(e.getLocalizedMessage(),e);		  			
		}
	}
	
	public String getPID() { 
		try {
			XPath xpath = XPath.newInstance( "//dc:identifier[contains(.,'o:')]" );
			xpath.addNamespace( Common.xmlns_dc );  		
			Element idno = (Element) xpath.selectSingleNode( dc );
			if (idno != null) {
				String s = idno.getTextNormalize();
	            this.PID = s.startsWith(Common.INFO_FEDORA) ? s.substring(Common.INFO_FEDORA.length()) : s;
			}
		} catch (Exception e) {
		  	log.error(e.getLocalizedMessage(),e);		  		
		}
		return this.PID;
	}
	
	public String getURL() { 
		try {
			XPath xpath = XPath.newInstance( "//dc:source[contains(.,'http://') or contains(.,'https://')]" );
			xpath.addNamespace( Common.xmlns_dc );  		
			Element url = (Element) xpath.selectSingleNode( dc );
			if (url != null) {
				return url.getText();
			}
		} catch (Exception e) {			
		  	log.error(e.getLocalizedMessage(),e);		  			
		} 
		return null;
	   		
	}
		   
}
