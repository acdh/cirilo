package org.emile.cirilo.business;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;
import java.text.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.ss.usermodel.*;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;
import org.emile.cirilo.*;

public class EXCEL {
	
	private final int MAXIT = 10000; 
	
	private  HashMap <Integer,String> fields = new HashMap <Integer,String>();
	private  HashMap <String,String> data; 
	private  File fp;	
	private  File tp;
	private  XSSFSheet sheet;
	private  Document template;
	private  Format format;
	private  XMLOutputter outputter;
	private  ScriptEngineManager manager;
	private  ScriptEngine engine;
	private InputStream input;
	private FormulaEvaluator evaluator;
	
	private int currentRow;
	
	public EXCEL(String s, String t) {
		fp = new File(s);
		tp = new File(t);
		currentRow = 0;
	};

    public boolean init(int tab)
    {
    	try {
			format = Format.getRawFormat();
			format.setEncoding("UTF-8");
			outputter = new XMLOutputter(format);

			input = new FileInputStream(fp);
    		
    		XSSFWorkbook wb = new XSSFWorkbook(input);
    		evaluator = wb.getCreationHelper().createFormulaEvaluator();
    		
    		sheet = wb.getSheetAt(tab);

    		manager = new ScriptEngineManager();  
			engine = manager.getEngineByName("javascript");

    		getFieldNames();

    		return true;
    	} catch (Exception e) {
    		e.printStackTrace();
    		return false;
    	}    	
    }

    public void destroy() {
    	try {
    		input.close();
    	} catch (Exception e) {}
    }
    
 	public String toString() {
    	return this.outputter.outputString(this.template); 
 	}

    
    public int getRowCount() {
       	try {
    		return sheet.getLastRowNum();    		
    	} catch (Exception e) {
    		return 0;
    	}    	    	
    }
    
    public String get(String field) {
       	try {
    		return (String) data.get(field);    		
    	} catch (Exception e) {
    		return "null";
    	}    	    	
    }
        
    public ArrayList <String> getFieldNames() {

    	ArrayList <String> f = new ArrayList();
    	
    	try {
    		XSSFRow fd = (XSSFRow) sheet.getRow(0);
    		Iterator fiter = fd.cellIterator();
    		while( fiter.hasNext() ) {
    			XSSFCell cell = (XSSFCell) fiter.next();
    			switch ( cell.getCellType() ) {
					case XSSFCell.CELL_TYPE_STRING:
						fields.put(new Integer (cell.getColumnIndex()), cell.getStringCellValue().trim());
						f.add(cell.getStringCellValue().trim());
						break;
					default:
						break;
    			}
    		}    		
    	} catch (Exception e) {}
    	finally {
    		return f;
    	}
	}
    
    
    public boolean hasNext()
    {    	
    	try {
    		if (currentRow < sheet.getLastRowNum())
    		{    		
    			currentRow++;
    			
    			getRow(currentRow);
    			return evaluate (data);
    		} 
    	} catch (Exception e) {}
    	return false;
    }
    
    public boolean getNext()
    {    	
    	try {
    		if (currentRow <= sheet.getLastRowNum())
    		{    		
    			getRow(currentRow);
    			currentRow++;
    			return true;
    		} 
    	} catch (Exception e) {}
    	return false;
    }
    
    
    public boolean getRow(int r) {
    	
       	
    	try {
    		data = new HashMap <String,String> ();
    		
    		XSSFRow row = (XSSFRow) sheet.getRow(r);
			Iterator cells = row.cellIterator();
			SimpleDateFormat df = new SimpleDateFormat("dd.MM.YYYY");
			
			while( cells.hasNext() ) {
				XSSFCell cell = (XSSFCell) cells.next();
				switch ( evaluator.evaluateInCell(cell).getCellType()) {
					case XSSFCell.CELL_TYPE_BOOLEAN:
						data.put(fields.get(new Integer(cell.getColumnIndex())), new Boolean(cell.getBooleanCellValue()).toString());
						break;
					case XSSFCell.CELL_TYPE_NUMERIC:
					    if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
							data.put(fields.get(new Integer(cell.getColumnIndex())), df.format(cell.getDateCellValue()));
					    } else {	
							data.put(fields.get(new Integer(cell.getColumnIndex())), new Double(cell.getNumericCellValue()).toString());
					    }						
						break;
					case XSSFCell.CELL_TYPE_STRING: 
						data.put(fields.get(new Integer(cell.getColumnIndex())), cell.getStringCellValue() );
						break;
					default: 
						break;
				}
			}
			return true;
    	} catch (Exception e) {
    		return false;
    	}
    }
    
    
    public boolean evaluate(HashMap <String,String> data) 
    {
		try {
    		SAXBuilder builder = new SAXBuilder();
			template =builder.build(tp);
		
			XPath xpath = XPath.newInstance("//mm:while");
			xpath.addNamespace( Common.xmlns_mm);
			java.util.List <org.jdom.Element> iterates = (List) xpath.selectNodes( this.template);
			
			int currentPos = currentRow;
			int delta = 1;

			
			for (org.jdom.Element iterate : iterates) {			
			    
				String fore = (String) iterate.getAttributeValue("field").substring(1);
		        delta = 0;
				
				java.util.List <org.jdom.Element> children = iterate.getChildren();
				org.jdom.Document n = new Document();
                n.addContent(new Element("root"));
                
                String id = data.get(fore);
                
                while (getNext())
                {
               	
                	if (!get(fore).equals(id)) break;
                	               	
                	delta++;
                	
                	for (org.jdom.Element o: children) {
					    n.getRootElement().addContent((Element)o.clone());
                	}
                	
        			xpath = XPath.newInstance("//@*[contains(.,'{')]");
        			java.util.List <org.jdom.Attribute>attributes = (List) xpath.selectNodes( n);
        			for (org.jdom.Attribute at : attributes) {
        				String expr = at.getValue().replace("{", "").replace("}", "");						
        				Pattern pn = Pattern.compile("position\\(\\)");
        				Matcher m = pn.matcher(expr);
        				if (m.find()) {				    	 	 
        					StringBuffer sb = new StringBuffer();
        					do {
        						try {
        							m.appendReplacement(sb,"'"+new Integer(delta).toString()+"'");
        						} catch (Exception e) {	
        	        			}
        					} while (m.find());		
            				m.appendTail(sb);
            				expr = sb.toString();
        				}
        				pn = Pattern.compile("\\$([a-zA-Z0-9_-]*)([\\[][\\]]){0,1}");
        				m = pn.matcher(expr);
        				if (m.find()) {				    	 	 
        					StringBuffer sb = new StringBuffer();
        					do {
        						try {
        							m.appendReplacement(sb,"'"+(get(m.group(1))).trim()+"'");
        						} catch (Exception e) {	
        	        			}
        					} while (m.find());		
            				m.appendTail(sb);
            				expr = sb.toString();
        				}
           				String result = Common.JSERROR; 
           				try {
           					result = (String) engine.eval( expr );
           				} catch (Exception e) {
           				}	
           				at.setValue(result);					    
        			}
        			
        			xpath = XPath.newInstance("//mm:expr");
        			xpath.addNamespace( Common.xmlns_mm);
        			java.util.List <org.jdom.Element> elements = (List) xpath.selectNodes(n);

        			for (org.jdom.Element el : elements) {        				
        				String expr = el.getAttributeValue("value").replace("{", "").replace("}", "");						
        				Pattern pn = Pattern.compile("position\\(\\)");
        				Matcher m = pn.matcher(expr);
        				if (m.find()) {				    	 	 
        					StringBuffer sb = new StringBuffer();
        					do {
        						try {
        							m.appendReplacement(sb,"'"+new Integer(delta).toString()+"'");
        						} catch (Exception e) {	
        	        			}
        					} while (m.find());		
            				m.appendTail(sb);
            				expr = sb.toString();
        				}
        				pn = Pattern.compile("\\$([a-zA-Z0-9_-]*)([\\[][\\]]){0,1}");
        				m = pn.matcher(expr);
        				if (m.find()) {				    	 	 
        					StringBuffer sb = new StringBuffer();
        					do {
        						try {
        							m.appendReplacement(sb,"'"+(get(m.group(1))).trim()+"'");
        						} catch (Exception e) {	
        	        			}
        					} while (m.find());		
            				m.appendTail(sb);
            				expr = sb.toString();
        				}
           				String result = Common.JSERROR; 
           				try {
           					result = (String) engine.eval( expr );
           				} catch (Exception e) {
           				}	

           				Element pr = el.getParentElement();
           				pr.removeChild("expr", Common.xmlns_mm);
           				pr.setText(result);
        			}
                 }
				
                children =  n.getRootElement().getChildren();
            	for (org.jdom.Element o: children) {
            		iterate.getParentElement().addContent((Element)o.clone());
            	}					                
            	iterate.getParentElement().removeChild("while", Common.xmlns_mm);
    			currentRow = currentPos;
    			getRow(currentRow);
			}			
						
			xpath = XPath.newInstance("//mm:repeat");
			xpath.addNamespace( Common.xmlns_mm);
			java.util.List <org.jdom.Element> repeats = (List) xpath.selectNodes( this.template);
			
			for (org.jdom.Element re : repeats) {			
				java.util.List <org.jdom.Element> children = re.getChildren();
				org.jdom.Document n = new Document();
                n.addContent(new Element("root"));
              
                boolean last = false;
                
                for (int i=1; i < MAXIT; i++) 
                {                	
    				org.jdom.Document q = new Document();
                    q.addContent(new Element("root"));

                	
                	for (org.jdom.Element o: children) {
					    q.getRootElement().addContent((Element)o.clone());
                	}
                	
        			xpath = XPath.newInstance("//@*[contains(.,'{')]");
        			java.util.List <org.jdom.Attribute>attributes = (List) xpath.selectNodes(q);
        			for (org.jdom.Attribute at : attributes) {
        				
        				String expr = at.getValue().replace("{", "").replace("}", "");						
        				Pattern pn = Pattern.compile("position\\(\\)");
        				Matcher m = pn.matcher(expr);
        				if (m.find()) {				    	 	 
        					StringBuffer sb = new StringBuffer();
        					do {
        						try {
        							m.appendReplacement(sb,"'"+new Integer(i).toString()+"'");
        						} catch (Exception e) {	
        	        			}
        					} while (m.find());		
            				m.appendTail(sb);
            				expr = sb.toString();
        				}
        				pn = Pattern.compile("\\$([a-zA-Z0-9_-]*)([\\[][\\]]){0,1}");
        				m = pn.matcher(expr);
        				if (m.find()) {				    	 	 
        					StringBuffer sb = new StringBuffer();
        					do {
        						try {
        							m.appendReplacement(sb,"'"+(get(m.group(1)+(m.group(2) != null  ? ":"+new Integer(i).toString() : ""))).trim()+"'");
        						} catch (Exception e) {
        							last = true;
        	        			}
        					} while (m.find());		
            				m.appendTail(sb);
            				expr = sb.toString();
        				}
           				String result = Common.JSERROR; 
           				try {
           					result = (String) engine.eval( expr );
           				} catch (Exception e) {
           				}	
           				at.setValue(result);					    
        			}
        			
        			xpath = XPath.newInstance("//mm:expr");
        			xpath.addNamespace( Common.xmlns_mm);
        			java.util.List <org.jdom.Element> elements = (List) xpath.selectNodes(q);

        			for (org.jdom.Element el : elements) {
         				String expr = el.getAttributeValue("value").replace("{", "").replace("}", "");						
        				Pattern pn = Pattern.compile("position\\(\\)");
        				Matcher m = pn.matcher(expr);
        				if (m.find()) {				    	 	 
        					StringBuffer sb = new StringBuffer();
        					do {
        						try {
        							m.appendReplacement(sb,"'"+new Integer(i).toString()+"'");
        						} catch (Exception e) {	
        	        			}
        					} while (m.find());		
            				m.appendTail(sb);
            				expr = sb.toString();
        				}
        				pn = Pattern.compile("\\$([a-zA-Z0-9_-]*)([\\[][\\]]){0,1}");
        				m = pn.matcher(expr);
        				if (m.find()) {				    	 	 
        					StringBuffer sb = new StringBuffer();
        					do {
        						try {
           							m.appendReplacement(sb,"'"+(get(m.group(1)+(m.group(2) != null  ? ":"+new Integer(i).toString() : ""))).trim()+"'");
           						} catch (Exception e) {
           							last =true;
        	        			}
        					} while (m.find());		
            				m.appendTail(sb);
            				expr = sb.toString();
        				}
           				String result = Common.JSERROR; 
           				try {
           					result = (String) engine.eval( expr );
           				} catch (Exception e) {
           				}	

           				Element pr = el.getParentElement();
           				pr.removeChild("expr", Common.xmlns_mm);
           				pr.setText(result);
        			}
        			
        			if (last) break;
        			
        			java.util.List <org.jdom.Element> childs =  q.getRootElement().getChildren();
                    for (org.jdom.Element o: childs) {
              		  n.getRootElement().addContent((Element)o.clone());
              		  
              	  	}
                  }

                 children =  n.getRootElement().getChildren();
           	  	 for (org.jdom.Element o: children) {
            		  re.getParentElement().addContent((Element)o.clone());
            	 }		                              	
                 re.getParentElement().removeChild("repeat", Common.xmlns_mm);
			}
			
									
			xpath = XPath.newInstance("//@*[contains(.,'{')]");
			java.util.List <org.jdom.Attribute>attributes = (List) xpath.selectNodes( this.template);

			for (org.jdom.Attribute at : attributes) {
				String expr = at.getValue().replace("{", "").replace("}", "");						
				Pattern pn = Pattern.compile("\\$([a-zA-Z0-9_-]*)");
				Matcher m = pn.matcher(expr);
				StringBuffer sb = new StringBuffer();
				if (m.find()) {				    	 	 
					do {
						try {
							m.appendReplacement(sb,"'"+(get(m.group(1))).trim()+"'");
						} catch (Exception e) {	
						}
					} while (m.find());		
					m.appendTail(sb);			
       				String result = Common.JSERROR; 
    				try {
    					result = (String) engine.eval( sb.toString() );
    				} catch (Exception e) {
    				}	
					at.setValue(result);
				}
			}
			
			xpath = XPath.newInstance("//mm:expr");
			xpath.addNamespace( Common.xmlns_mm);
			java.util.List <org.jdom.Element> elements = (List) xpath.selectNodes(this.template);
			for (org.jdom.Element el : elements) {
				String expr = el.getAttributeValue("value").replace("{", "").replace("}", "");						
				StringBuffer sb = new StringBuffer();
				Pattern pn = Pattern.compile("\\$([a-zA-Z0-9_-]*)");
				Matcher m = pn.matcher(expr);
				if (m.find()) {				    	 	 
					do {
						try {
							m.appendReplacement(sb,"'"+(get(m.group(1))).trim()+"'");
						} catch (Exception e) {	
	        			}
					} while (m.find());		
					m.appendTail(sb);			
				}
   				String result = Common.JSERROR; 
   				try {
   					result = (String) engine.eval( sb.toString() );
   				} catch (Exception e) {
   				}	
				Element pr = el.getParentElement();
				pr.removeChild("expr", Common.xmlns_mm);
				pr.setText(result);
			}

			currentRow += delta-1;
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
    }
    
}