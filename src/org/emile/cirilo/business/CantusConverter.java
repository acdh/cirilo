package org.emile.cirilo.business;

import org.emile.cirilo.*;
import org.emile.cirilo.business.HashEntry;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.net.*;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

public class CantusConverter {
	 
	 public enum Types { EOT, TYPUS, TIMETERM_1, TIMETERM_2, TEXT, CANTO, NEUME, PAGE, WHITESPACE, DOT, COMMA, PERSON, PLACE, FUNCTION};
	 EnumSet<Types> Typus = EnumSet.of(Types.CANTO, Types.NEUME, Types.TYPUS, Types.WHITESPACE);
	 EnumSet<Types> Entities = EnumSet.of(Types.PERSON, Types.PLACE, Types.FUNCTION);
	 EnumSet<Types> Description = EnumSet.of(Types.TEXT, Types.DOT, Types.COMMA, Types.WHITESPACE, Types.PAGE);

	 private HashMap<String,String> CANTOS;
	 private ArrayList<HashEntry> TIMETERMS_1;
	 private ArrayList<HashEntry> TIMETERMS_2;
	 private ArrayList<String> TYPES;

	 private Pattern pattern;
	 private SAXBuilder builder;
	 private org.jdom.Document target;
	 private XMLOutputter op;
	 
     public CantusConverter() {			
	  	this.readConfig();
     }
	  
	 public org.jdom.Document transform(org.jdom.Document tei) {
	        try {
	        	
	        	target = tei; 
	        	
				builder = new SAXBuilder();

				Format format = Format.getRawFormat();
				format.setEncoding("UTF-8");
				op = new XMLOutputter(format);
				
				XPath xPath = XPath.newInstance("//t:body/t:div");								
				xPath.addNamespace(Common.xmlns_tei_p5);
	  	        List <Element> divs = xPath.selectNodes(tei);

	  	        XPath qPath = XPath.newInstance("//t:body");								
				qPath.addNamespace(Common.xmlns_tei_p5);
	  	        Element body = (Element) qPath.selectSingleNode(target);
                body.removeChildren("div", Common.xmlns_tei_p5);
	  	        
	  	        for (Element div: divs) {
		  	        List<Element> ps = div.getChildren("p",  Common.xmlns_tei_p5);
		  	        String shead = op.outputString( div.getChild("head", Common.xmlns_tei_p5))
		  	        		.replaceAll("<.*?>","")
  	        				.replaceAll("[\n\r]","")
  	        				.replaceAll("\\[","| ")
  	        				.replaceAll("\\]","")
  	        		        .replaceAll("      "," ")	  	        		
  	        		        .replaceAll("     "," ")	  	        		
  	        		        .replaceAll("    "," ")	  	        		
  	        		        .replaceAll("   "," ")	  	        		
  	        				.replaceAll("  "," ")
  	        				.trim();	  	        		
                    
                    Element feast = null;
                      	feast = new Element("div",Common.xmlns_ntei_p5);
                      	Element head = new Element("head",Common.xmlns_ntei_p5);
                    	Element label = new Element("label",Common.xmlns_ntei_p5);
                       	String[] Head = shead.split("[|]");
                        if (Head.length > 1) {
                        		Element note = new Element("note",Common.xmlns_ntei_p5);
                        		note.setAttribute("style","Konjektur");
                        		label.setText(Head[0].trim());
                        		note.setText(Head[1].trim());
                        		head.addContent(label);
                        		head.addContent(note);                   	
                        } else {
                        		label.setText(shead.trim());                    	
                        		head.addContent(label);
                        }
                        feast.addContent(head);
        		  	    body.addContent(feast);
 		  	        
		  	        ArrayList<Element> segments_1 = new ArrayList<Element>();
  	        		segments_1.add(new Element("div",Common.xmlns_ntei_p5));
  	        		Element currSegment_1 = segments_1.get(segments_1.size()-1);
  	        		
		  	        ArrayList<Element> segments_2 = new ArrayList<Element>();
  	        		Element currSegment_2 = null;
  	        		
	  	        	for (Element p: ps) {
	  	        		String buf = op.outputString(p)
	  	        				.replaceAll("[\n\r]","")
	  	        				.replaceAll("[{]","!")
	  	        				.replaceAll("[}]","|")
	  	        				.replaceAll("<hi rend=\"italic strikethrough.*?>.+?</hi>","")
	  	        				.replaceAll("<hi rend=\"strikethrough.*?>.+?</hi>","")
	  	        				.replaceAll("<hi rend=\"Person.*?>","Ö")
	  	        				.replaceAll("<hi rend=\"Ort.*?>","Ü")
	  	        				.replaceAll("<hi rend=\"Funktion.*?>","Ä")
	  	        				.replaceAll("<hi rend=\"Neume.*?>","#")
	  	        				.replaceAll("<hi rend=\"Incipit.*?>","{")
		  	        			.replaceAll("<hi.*?>","")
	  	        				.replaceAll("</hi>","}")
	  	        				.replaceAll("\\[RASUR\\]","")
	  	        				.replaceAll("<.*?>","")  	        
	  	        				.replaceAll("(\\w)\\{","$1 {")
	  	        				.replaceAll("\\{ ","{")
	  	        				.replaceAll(" ([.,])","$1")
	  	        				.replaceAll(" \\}","}")
	  	        				.replaceAll("\\]\\{","] {")
	  	        				.replaceAll("\\[[ ]*\\]","")
	  	        				.replaceAll("[.][ ]*[.]",".")
	  	        				.replaceAll("[.]\\}","}.")
	  	        				.replaceAll("\\}(\\W)","} $1")
	  	        				.replaceAll("\\} ([.,])","}$1")
	  	        				.replaceAll("[.](\\w)",". $1")
	  	        		        .replaceAll("      "," ")	  	        		
	  	        		        .replaceAll("     "," ")	  	        		
	  	        		        .replaceAll("    "," ")	  	        		
	  	        		        .replaceAll("   "," ")	  	        		
	  	        				.replaceAll("  "," ");	  	        		

	  	        		ArrayList<String> segs = new ArrayList<String>();
	  	        		String line = "";
	  	        		boolean inline = false;
	  	        		for (int i=0;i<buf.length();i++) {
	  	        			if (buf.charAt(i) == '!') {
	  	        				if (!line.trim().isEmpty()) { segs.add(line); line=""; }
	  	        				line = line + buf.charAt(i);
	  	        				inline = true;
	  	        			} else if  (buf.charAt(i) == '|') {
	  	        				if (!line.trim().isEmpty()) { segs.add(line); line=""; }
	  	        				inline = false;
	  	        			} else if  (buf.charAt(i) == '.' && !inline) {
	  	        				line = line + buf.charAt(i);
	  	        				if (!line.trim().isEmpty()) { segs.add(line); line=""; }
	  	        			} else {
	  	        				line = line + buf.charAt(i);
	  	        			}		  	        			
	  	        		}
        				if (!line.trim().isEmpty()) {
        					segs.add(line);
        				}

	                    Element stream = new Element("quote",Common.xmlns_ntei_p5);
	  	        		for (String s: segs) {
		                    Element l = new Element("l",Common.xmlns_ntei_p5);
                            l.setText(s);
                            stream.addContent(l);
	  	        		}   	  	        			
	                    currSegment_1.addContent(stream);
                    
	          			boolean mode = false;        				
		  	        	Parser parser = new Parser();	
	  	        		String lastdiv = null;
		  	        	
	  	        		for (String s: segs) {
                            	  
	  	        			if (s.contains("!")) {
	  	        				s = s.replaceAll("!","");
	  	        			}
	  	        			
	  	        			parser.set(s);
	  	        			Types q;
	  	        			while ((q = parser.next()) != parser.types.EOT)
	  	        			{
	  	        				if(q == parser.types.TIMETERM_1) {
	  	  	  	        			segments_1.add(new Element("div",Common.xmlns_ntei_p5));
                                    currSegment_1 = segments_1.get(segments_1.size()-1);
                                    div.setAttribute("type","Zeit:1");
                                    Element ab = new Element("ab",Common.xmlns_ntei_p5);
                                    ab.setAttribute("ana","#head");
                                    Element hi = new Element("hi",Common.xmlns_ntei_p5);
       	        					if (parser.getConjecture()) ab.setAttribute("style","Konjektur");
       	        					String h = parser.getEntity().trim();
       	        					if (h.contains("|")) {
       	        						String[] a = h.split("[|]");
                                        Element seg = new Element("seg",Common.xmlns_ntei_p5);
       	        						seg.setText(a[0]);  
       	        						hi.setText(a[1]);
       	        					    ab.addContent(seg);
           	        				    ab.addContent(hi);
       	        						
       	        					} else {
       	        						hi.setText(h);
           	        				    ab.addContent(hi);       	        						
       	        					}
       	        					lastdiv = hi.getText();
                                    currSegment_1.addContent(ab);
                		  	        segments_2 = new ArrayList<Element>();
                  	        		currSegment_2 = null;
                  	        		mode = false;
                                    continue;
	  	        				}
	  	        				
	  	        				if (q == parser.types.TIMETERM_2) {	  	        					
	  	  	  	        			segments_2.add(new Element("ab",Common.xmlns_ntei_p5));
                                    currSegment_2 = segments_2.get(segments_2.size()-1);
                                    currSegment_2.setAttribute("type","Zeit:2");
                                    Element hd = new Element("label",Common.xmlns_ntei_p5);
                                    hd.setText(parser.getEntity().trim());
      	        					if (parser.getConjecture()) hd.setAttribute("style","Konjektur");
      	        					currSegment_2.addContent(hd);
                                    currSegment_1.addContent(currSegment_2);
	  	        				    mode = true;
                                    continue;
	  	        				}
	  	        				if (!mode) {
	  	  	  	        			segments_2.add(new Element("ab",Common.xmlns_ntei_p5));
                                    currSegment_2 = segments_2.get(segments_2.size()-1);
                                    currSegment_1.addContent(currSegment_2);
                                    mode = true;
	  	        				}
	  	        				
	  	        				if (q == parser.types.TYPUS) {
	  	        					Element seg = new Element(parser.getEntity(),Common.xmlns_cantus);
	  	        					if (parser.getConjecture()) seg.setAttribute("style","Konjektur");
	  	        					while(Types.WHITESPACE == parser.foresee() || Types.PAGE == parser.foresee()) parser.next();
	  	        					while (Typus.contains(parser.foresee())) {
	  	        						q= parser.next();
		        						if (q == Types.NEUME) {
	  		  	        					Element phr = new Element("phr",Common.xmlns_ntei_p5);
	  	        							phr.setText(parser.getEntity().trim());
	  	        							phr.setAttribute("type","Neume"); 
		  	        						seg.addContent(phr);  	  	        							
	  	        						} else {
	  	        							parseIncipit(seg, parser.getEntity());
	  	        						}
	  						        }	  	        						  	        				
	  	        					if (currSegment_2 == null) currSegment_1.addContent(seg); else currSegment_2.addContent(seg);
	  	        					continue;
	  	        				}	
	  	        				
	  	        				if (q == parser.types.CANTO || q == Types.NEUME) {
	  	        					String typ = "NO";
	  	        					if (lastdiv != null && lastdiv.equals("Officium")) {
	  	        						typ = "IN";
	  	        						lastdiv = null;
	  	        					}
	  	        					Element seg = new Element(typ,Common.xmlns_cantus);
	  	        					if (parser.getConjecture()) seg.setAttribute("style","Konjektur");
	        						if (q == Types.NEUME) {
  		  	        					Element phr = new Element("phr",Common.xmlns_ntei_p5);
  	        							phr.setText(parser.getEntity().trim());
  	        							phr.setAttribute("type","Neume"); 
	  	        						seg.addContent(phr);  	  	        							
  	        						} else {
  	        							parseIncipit(seg, parser.getEntity());
  	        						}
	  	        					if (currSegment_2 == null) currSegment_1.addContent(seg); else currSegment_2.addContent(seg);
	  	        					continue;
	  	        				}	

	  	        				if (Entities.contains(q)) {	  	        					
  				  	        		Element seg = new Element("seg",Common.xmlns_ntei_p5);	  	        							
                                    seg.setText(parser.getEntity());
                                    if (q == Types.PERSON) seg.setAttribute("ana", "#Person");
                                    if (q == Types.PLACE) seg.setAttribute("ana", "#Ort");
                                    if (q == Types.FUNCTION) seg.setAttribute("ana", "#Funktion");
  				  	        		if (currSegment_2 == null) currSegment_1.addContent(seg); else currSegment_2.addContent(seg);
	  	        				}	
	  	        					
	  	        				if (Description.contains(q)) {	  	        					
	  	    						    if (currSegment_2 == null) currSegment_1.addContent(parser.getEntity()); else currSegment_2.addContent(parser.getEntity());
	  	    				  	        while (Description.contains(parser.foresee())) {	
	  	    				  	        	q= parser.next();	
	  	    				  	        	if (q == Types.PAGE) {
	  	    				  	        		Element pb = new Element("pb",Common.xmlns_ntei_p5);	  	        							
	  	    				  	        		pb.setAttribute("n",parser.getEntity());
	  	    				  	        		if (currSegment_2 == null) currSegment_1.addContent(pb); else currSegment_2.addContent(pb);
	  	    				  	        	} else {
	  	    				  	        		if (currSegment_2 == null) currSegment_1.addContent(parser.getEntity()); else currSegment_2.addContent(parser.getEntity());
	  	    				  	        	}	
	  	    				  	        }	  	        				
	  	         		  	     }	  	        				
	  	        			}
	  	        		}	
	  	        	}
	  	        	for (Element e: segments_1) {
	  	        		if (e.getChildren().size() > 0) feast.addContent(e);  
	  	        	}
	  	        }
	  		
	  	        target = builder.build( new StringReader(op.outputString(target).replaceAll("[\n\r]","").replaceAll("°",".")) );	
	        } catch (Exception e) {
	        	e.printStackTrace();	       	
	        }
	        return target;
	         
	  }      
	  
	 
	  private void parseIncipit(Element seg, String s) {
			
		    if (s.contains("(")) {
		       int ipos = s.indexOf("(");	
		       int jpos = s.indexOf(")");
        	   Element pb = new Element("pb",Common.xmlns_ntei_p5);	  	        										       
	  	       pb.setAttribute("n",s.substring(ipos+1,jpos));		  	       
	  	       seg.addContent(s.substring(0,ipos-1));
	  	       seg.addContent(pb);
	  	       seg.addContent(s.substring(jpos+1));
		    } else {
		       seg.addContent(!s.equals(" ") ? s.trim() : s);
		    }	
		  
	  }
      
	  private void readConfig() {
			boolean times_1 = false, times_2 = false, types = false, phrases = false, abbreviations = false;
			CANTOS = new HashMap<String,String>();
			TIMETERMS_1 = new ArrayList<HashEntry>();
			TIMETERMS_2 = new ArrayList<HashEntry>();
			TYPES = new ArrayList<String>();
				
	   		try {	   			
	   			
				String homeDir = new File(System.getProperty("user.home")).getAbsolutePath();
                File config = new File(homeDir+System.getProperty("file.separator")+"cantus.conf");
                InputStream is = null;
                
                if (config.exists()) {
                	is = new FileInputStream(config);
                } else {	   			
                	is = Cirilo.class.getResourceAsStream("cantus.conf");
                }
	   			BufferedReader in = new BufferedReader(new InputStreamReader(is));
	   			String line;
	   			
	   	        while ((line = in.readLine()) != null) {      
	   	        	if (line.equals("%TimeTermsOfLevelOne")) {times_1=true; times_2=false; types=false; continue;} 
	   	        	if (line.equals("%TimeTermsOfLevelTwo")) {times_1=false; times_2=true; types=false; continue;} 
	   	        	if (line.equals("%Types")) {times_1=false; times_2=false; types=true; continue;}
	   	        	if (times_1) { String[] a = line.split(";"); TIMETERMS_1.add(new HashEntry(a[0],a[1])); } 
	   	        	else if (times_2) { String[] a = line.split(";"); TIMETERMS_2.add(new HashEntry(a[0],a[1])); }
	   	        	else if (types) { TYPES.add(line.trim()); TYPES.add("["+line.trim()+"]"); } 
	   	         }
	   	         in.close();
	   	         	   	         
	   	         ArrayList<HashEntry> temp = new ArrayList<HashEntry>();
	   	         for (HashEntry s: TIMETERMS_1) {temp.add(new HashEntry("["+s.getKey()+"]", s.getValue()));} 
	   	         for (HashEntry s: temp) TIMETERMS_1.add(new HashEntry(s.getKey(), s.getValue()));	   	         
	   	         temp = new ArrayList<HashEntry>();
	   	         for (HashEntry s: TIMETERMS_2) {temp.add(new HashEntry("["+s.getKey()+"]", s.getValue()));} 
	   	         for (HashEntry s: temp) TIMETERMS_2.add(new HashEntry(s.getKey(), s.getValue()));	   	         
	   		} catch (Exception e) {
	   			e.printStackTrace();
	   		}
		  
	  }
	   
	  private String getCanto(String s) {
		 		  
	 	 String id = "";

 /*	     id = "|cao:nul";
		  
		 try {
			 
		   if (s.contains(" ")) s+="*";
		    
		   String canto = CANTOS.get(s);
		   
		   if (canto == null) {
			   URL url = new URL("http://glyph.uni-graz.at/cocoon/cantus/get?incipit="+s);
			   InputStream in = getInputStream(url);
			   Document cao = builder.build(in);
			   XPath xpath = XPath.newInstance("//c:cantus/c:id");
			   xpath.addNamespace( Common.xmlns_cantus );
			   List ids = (List) xpath.selectNodes(cao);
		   
			   if (ids.size() > 0) {
				   id ="|cao:";
				   for (Iterator iter = ids.iterator(); iter.hasNext();) {
					   try {
						   Element oid = (Element) iter.next();
						   id+=oid.getText()+";"; 
 	    			  	} catch (Exception q) {}
				   }
				   CANTOS.put(s,id);
			   }
			   
		   } else {
			   id = canto;
		   }
		   
		  } catch (Exception e) {
		        e.printStackTrace();
		  }  */
		  		  
		  return id;
		  
	  }
	  
	  private static InputStream getInputStream(URL url) {
		    InputStream in = null;
		    try {
		        HttpURLConnection con = (HttpURLConnection) url.openConnection();
		        con.setRequestMethod("GET");
		        con.connect();
		        in = con.getInputStream();

		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		    return in;
		}


	  
	  private String trunc(Types t) {
		  
		  String s = t.toString();
		  return "|"+s.substring(0,2);
	  }
	  private class Parser {
		
		  public Types types;
		  
		  public static final String SEPARATOR = " ,.";
		  
		  private String buf;
		  private String entity;
		  private int bp;
		  private boolean conjecture;
		  
		  public Parser() {}
		  
		  public void set( String s) {
			  buf = s;
			  bp = 0;
		  }
		  
		  public Types next() {
			  
			  String ch;
			  entity = "";
			  if (bp > buf.length()-1) return types.EOT;
			  
			  ch = String.valueOf(buf.charAt(bp++));
			  conjecture = false;
			  
			  if (ch.equals(".")) {
				  entity = ".";
				  return log(types.DOT);
			  } else if (ch.equals(",")) { 
				  entity = ",";
				  return log(types.COMMA);
			  } else if (ch.equals(" ")) {
				  entity = " ";
				  return log(types.WHITESPACE);
			  } else if (ch.equals("{")) {
				  while (true) {
					  ch = String.valueOf(buf.charAt(bp++));
					  if (ch.equals("}") || bp > buf.length()-1) break;
					  entity+=ch;
				  }
				  return log(types.CANTO);
			  } else if (ch.equals("(")) {
				  while (true) {
					  ch = String.valueOf(buf.charAt(bp++));
					  if (ch.equals(")") || bp > buf.length()-1) break;
					  entity+=ch;
				  }
				  return log(types.PAGE);
			  } else if (ch.equals("#")) {
				  while (true) {
					  ch = String.valueOf(buf.charAt(bp++));
					  if (ch.equals("}") || bp > buf.length()-1) break;
					  entity+=ch;
				  }
				  return log(types.NEUME);
			  } else if (ch.equals("Ö")) {
				  while (true) {
					  ch = String.valueOf(buf.charAt(bp++));
					  if (ch.equals("}") || bp > buf.length()-1) break;
					  entity+=ch;
				  }
				  return log(types.PERSON);
			  } else if (ch.equals("Ä")) {
				  while (true) {
					  ch = String.valueOf(buf.charAt(bp++));
					  if (ch.equals("}") || bp > buf.length()-1) break;
					  entity+=ch;
				  }
				  return log(types.FUNCTION);
			  } else if (ch.equals("Ü")) {
				  while (true) {
					  ch = String.valueOf(buf.charAt(bp++));
					  if (ch.equals("}") || bp > buf.length()-1) break;
					  entity+=ch;
				  }
				  return log(types.PLACE);				  
			  } else {	  
				  int cp = bp;
				  while (true) {
					  if (SEPARATOR.contains(ch) || bp > buf.length()-1) { break; };
					  entity+=ch;
					  if (ch.equals("[")) conjecture = true;
					  ch = String.valueOf(buf.charAt(bp++));
				  }
				  String p = buf.substring(cp-1);
				  for (HashEntry t: TIMETERMS_1) {
					  String q = t.getKey();
					  if (p.startsWith(q)) {
						 entity = t.getValue();
						 bp = cp+q.length()-1;
						 return log(types.TIMETERM_1);
					  }
				  }
				  for (HashEntry t: TIMETERMS_2) {
					  String q = t.getKey();
					  if (p.startsWith(q)) {
						 entity = t.getValue();
						 bp = cp+q.length()-1;
						 return log(types.TIMETERM_2);
					  }
				  }
				  
				  if( (SEPARATOR.contains(ch) && bp <= buf.length()) || bp <= buf.length()-1 ) { bp--;} else {entity+=ch;}
				  
				  if (TYPES.contains(entity)) {
						 return log(types.TYPUS);				  
				  }
				  return log(types.TEXT);
			  }
		  	
		  }
		  
		  private Types log (Types q) {
	    	  return q;
		  }
		  
		  public Types foresee() {
			  int tbp = bp;
			  Types result = next();
			  bp = tbp;
			  return result;	
		  }
		  
		  public boolean getConjecture() {
			  return conjecture;
		  }
		  
		  public String getEntity() {
			  return entity.replaceAll("\\[","").replaceAll("\\]","");
		  }
		  
	  
	  }
	  

	  
}
