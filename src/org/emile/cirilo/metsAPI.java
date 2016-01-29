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
/*
import java.io.FileInputStream;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import edu.harvard.hul.ois.mets.DmdSec;
import edu.harvard.hul.ois.mets.Mets;
import edu.harvard.hul.ois.mets.helper.MetsReader;
import edu.harvard.hul.ois.mets.helper.MetsWriter;
*/
public class metsAPI {
	/*
	try {
	    FileInputStream in = new FileInputStream ("c:\\temp\\brentano.xml");
	    Mets mets = Mets.reader (new MetsReader (in, false));					
	    in.close ();

	
        for (Object o : mets.getContent())
        {
            if (o instanceof DmdSec)
            {
            	DmdSec s = (DmdSec) o;
            	s.setID("deleted");
            }
            if (o instanceof AmdSec)
            {
            	AmdSec s = (AmdSec) o;
            	s.setID("sys:delete");
            }
            if (o instanceof FileSec)
            {
            	FileSec s = (FileSec) o;
            	s.init();
            	FileGrp fg = new FileGrp();
            	s.getContent().add(fg);
            }
            if (o instanceof StructMap)
            {
            	StructMap s = (StructMap) o;
            	s.init();
            	Div div = new Div();
            	s.getContent().add(div);
            }
            if (o instanceof StructLink)
            {
            	StructLink s = (StructLink) o;
            	s.init();
            	SmLink sm = new SmLink();
            	sm.setFrom(""); 
            	sm.setTo("");
            	s.getContent().add(sm);
            }

            if (sm.getTYPE().equals("PHYSICAL")) {
			        for (Object q :  sm.getContent()) {
			        	Div d = (Div) q;
			        	for (Object s : d.getContent()) {
			        		Div dv = (Div)s;
                            Div n = new Div();
                            n.setID("DIV.8");
                            n.setORDER(8);
                            n.setTYPE("page");
                            Fptr f = new Fptr();
                            f.setFILEID("IMG.1");
                            n.getContent().add(f);
                            d.getContent().add(n);
                            d.init();
                            break;
			        	}
			        }
            		
            	}
            }

        java.io.ByteArrayOutputStream os = new java.io.ByteArrayOutputStream();
        mets.write (new MetsWriter (os));
       	XPath xPath = XPath.newInstance("//*[@ID='deleted']");
		xPath.addNamespace( Common.xmlns_nmets);
		SAXBuilder builder = new SAXBuilder();
		org.jdom.Document doc =  builder.build( new StringReader(os.toString()) );
  	    List deleted = (List) xPath.selectNodes( doc );
  	    
  	    
//		    mets.validate (new MetsValidator ());
  	    
  	    if (deleted != null) {
  	    	for (Iterator iter = deleted.iterator(); iter.hasNext();) {
  	    		try {
  	    			Element e = (Element) iter.next();
  	    		    e.getParent().removeContent(e);
  	    		} catch (Exception ex) {
  	    			
  	    		}
  	    	}
  	    }				    				

	    XMLOutputter op = new XMLOutputter(); 
    System.out.println(op.outputString(doc));
	}
	
	catch (Exception e) {
	    e.printStackTrace ();	    
	}

	*/
	

}
