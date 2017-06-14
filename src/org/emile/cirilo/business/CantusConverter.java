package org.emile.cirilo.business;

import org.emile.cirilo.*;
import org.emile.cirilo.business.HashEntry;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.net.*;

import org.apache.log4j.Logger;
 
import org.jdom.Element;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

public class CantusConverter {
	 
     private static Logger log = Logger.getLogger(CantusConverter.class);

	 public static enum Types { EOT, TYPUS, TIMETERM_1, TIMETERM_2, TEXT, CANTO, NEUME, PAGE, WHITESPACE, DOT, COMMA, PERSON, PLACE, FUNCTION, RASUR, XASUR, RUSUR, MASUR, EOA, EOI, CHOICE, DEL, INS, MARGINAL, COMMENT}; 
	 EnumSet<Types> Typus = EnumSet.of(Types.CANTO, Types.NEUME, Types.TYPUS, Types.WHITESPACE);
	 EnumSet<Types> Emendations = EnumSet.of(Types.RASUR, Types.RUSUR, Types.MASUR, Types.XASUR);
	 EnumSet<Types> Entities = EnumSet.of(Types.PERSON, Types.PLACE, Types.FUNCTION, Types.DEL);
	 EnumSet<Types> Description = EnumSet.of(Types.TEXT, Types.DOT, Types.COMMA, Types.WHITESPACE, Types.PAGE);
     EnumSet<Types> Text = EnumSet.of(Types.PERSON, Types.PLACE, Types.FUNCTION, Types.TEXT, Types.WHITESPACE, Types.TYPUS, Types.DOT);
     EnumSet<Types> All = EnumSet.of(Types.PERSON, Types.PLACE, Types.FUNCTION, Types.DEL,Types.TEXT, Types.DOT, Types.COMMA, Types.WHITESPACE, Types.PAGE, Types.COMMA, Types.DOT, Types.CANTO, Types.NEUME, Types.TYPUS, Types.CHOICE);

	 private ArrayList<String> TYPES;

	 private org.jdom.Document target;
	
	 private SAXBuilder builder;
	 private XMLOutputter op;
     private String lastdiv;
     
     private Element currSegment_1;
     private Element currSegment_2;
     private Element ins;
  
	 private String VARIANT = "";
	 private boolean urtext;
     
     public CantusConverter(String VARIANT) {
     	this.VARIANT = VARIANT;
     	this.urtext = this.VARIANT.contains(":"); 
         if (this.urtext) {
             String v[] = this.VARIANT.split(":");
             this.VARIANT = v[1];
         }	
 	  	this.readConfig();
     }
	  
	 public org.jdom.Document transform(org.jdom.Document tei) {
	        try {
	        	
	        	target = tei;	        	
				builder = new SAXBuilder();
				target.getRootElement().addNamespaceDeclaration(Common.xmlns_cantus);

				Format format = Format.getRawFormat();
				format.setEncoding("UTF-8");
				op = new XMLOutputter(format);
				
				XPath xPath = XPath.newInstance("//t:editionStmt/t:edition"); 							
				xPath.addNamespace(Common.xmlns_tei_p5);
				if (!VARIANT.isEmpty()) {
					Element edition = (Element) xPath.selectSingleNode(target);
					edition.removeChildren("date",Common.xmlns_tei_p5);
					Element witDetail = new Element("witDetail",Common.xmlns_tei_p5);
					witDetail.setAttribute("wit",VARIANT);
					edition.addContent(witDetail);
				}	
				xPath = XPath.newInstance("//t:titleStmt"); 							
				xPath.addNamespace(Common.xmlns_tei_p5);
				Element ts = (Element) xPath.selectSingleNode(target);
				ts.removeChildren("author",Common.xmlns_tei_p5);

						
                xPath = XPath.newInstance("//t:body/t:div");								
				xPath.addNamespace(Common.xmlns_tei_p5);
	  	        List <Element> divs = xPath.selectNodes(tei);

	  	        XPath qPath = XPath.newInstance("//t:body");								
				qPath.addNamespace(Common.xmlns_tei_p5);
	  	        Element body = (Element) qPath.selectSingleNode(target);
                body.removeChildren("div", Common.xmlns_tei_p5);
                body.setAttribute("space","preserve",Common.xmlns_xml);
	  	        
	  	        for (Element div: divs) {
		  	        List<Element> ps = div.getChildren("p",  Common.xmlns_tei_p5);
		  	        String shead = op.outputString( div.getChild("head", Common.xmlns_tei_p5))
	        				.replaceAll("%D::(.*?)%", "ß+$1+ß")  	        				
	        				 .replaceAll("[?]","+")	       		
  	        				.replaceAll("/!", "/µ")
  	        				.replaceAll("#", "~") 
 	        			  	.replaceAll("\\$", "¥")
 	        			  	.replaceAll("[|]","§")	  	        		
		  	        		.replaceAll("<.*?>","")
  	        				.replaceAll("[\n\r]","")
  	        				.replaceAll("\\[","{")
  	        				.replaceAll("\\]","}")
  	        		        .replaceAll("      "," ")	  	        		
  	        		        .replaceAll("     "," ")	  	        		
  	        		        .replaceAll("    "," ")	  	        		
  	        		        .replaceAll("   "," ")	  	        		
  	        				.replaceAll("  "," ")
  	        				.trim();	  	        		
                    
                   Element feast = new Element("div",Common.xmlns_ntei_p5);
                   Element head = new Element("head",Common.xmlns_ntei_p5);
                   
                   
                   shead = parseVariant(shead);
                   
                  // head.setText(shead);
                   
                   parseEmendations(head, shead);
                   feast.addContent(head);
       		  	   body.addContent(feast);
 		  	        
		  	        ArrayList<Element> segments_1 = new ArrayList<Element>();
  	        		segments_1.add(new Element("div",Common.xmlns_ntei_p5));
  	        		currSegment_1 = segments_1.get(segments_1.size()-1);
  	        		
		  	        ArrayList<Element> segments_2 = new ArrayList<Element>();
  	        		currSegment_2 = null;
	        		ins = null;
	        		  	
	  	        	for (Element p: ps) {
	  	        		String buf = op.outputString(p)
	  	        			    .replaceAll("[|]G[|]","Γ")	  	        		
	  	        			    .replaceAll("/!", "/µ")
	  	        			    .replaceAll("[?]","+")	       		
	  	        			    .replaceAll("[|]","§")	  	        		
	  	  	  		        	.replaceAll("[\n\r]","")
	  	        				.replaceAll("[{]","!")
	  	        				.replaceAll("[}]","|")
	  	        				.replaceAll("%D::(.*?)%", "ß+$1+ß") 
	  	        				.replaceAll("#", "~") 
	  	        				.replaceAll("<hi rend=\"Person.*?>","Ö")
	  	        				.replaceAll("<hi rend=\"Ort.*?>","Ü")
	  	        				.replaceAll("<hi rend=\"Funktion.*?>","Ä")
	  	        				.replaceAll("<hi rend=\"Neume.*?>","#")
	  	        				.replaceAll("<hi rend=\"Incipit.*?>","{")
	  	        				.replaceAll("<hi rend=\"Time:1\">","Δ")
	  	        				.replaceAll("<hi rend=\"Time:2\">","Θ")
	  	        				.replaceAll(" </hi>","</hi> ")
	  	        				.replaceAll("<hi.*?>","")
	  	        				.replaceAll("</hi>","}")
	  	        				.replaceAll("<.*?>","")  	        
	  	        				.replaceAll("(\\w)\\{","$1 {")
	  	        				.replaceAll("\\{ ","{")
	  	        				.replaceAll(" ([.,])","$1")
	  	        				.replaceAll(" \\}","}")
	  	        				.replaceAll("\\]\\{","] {")
	  	        				.replaceAll("\\[[ ]*\\]","")
	  	        				.replaceAll("[.][ ]*[.]",".")
	  	        				.replaceAll("[.]\\}","}.")
	  	        			//	.replaceAll("\\}(\\W)","} $1")
	  	        				.replaceAll("\\} ([.,§$])","}$1")
	  	        				.replaceAll("[.](\\w)",". $1")
	  	        		        .replaceAll("      "," ")	  	        		
	  	        		        .replaceAll("     "," ")	  	        		
	  	        		        .replaceAll("    "," ")	  	        		
	  	        		        .replaceAll("   "," ")	  	        		
	  	        				.replaceAll("  "," ")
	  	        		        .replaceAll("§<", "§ <")
	  	        				.replaceAll("\\$", "¥")
	  	        				.replaceAll("\\} #", "\\}#")
	  	        				.replaceAll("\\*", "")
	        					.replaceAll("\\[KOMMENTAR\\]", "KOMMENTAR");
	  	        		
	  	        		
	  	        		
	  	        		log.debug(buf);
	  	        		
	  	        		if (!VARIANT.isEmpty()) {
	  	        			Pattern p0 = Pattern.compile("(//)(.*?)(//)");
	  	        			Matcher m0 = p0.matcher(buf);
	  	        			StringBuffer sb = new StringBuffer();
  	  						  	  				
	  	        			while (m0.find())  {
	  	        				String variant = "";
	  	        				String s = m0.group();	  	        				
	  	        				s = s.substring(2,s.length()-1);
	  	        				log.info(s);
	  	        				boolean test = this.urtext && s.contains("µ");
	  	        				Pattern p1 = Pattern.compile("("+(test ? "µ" : "")+".*?)~(.*?)/");
  		  	  					Matcher m1 = p1.matcher(s);
  		  	  					while (m1.find()) {
  		  	  						String[] a = m1.group().substring(0, m1.group().length()-1).split("~");
  		  	  						if (test || a[0].contains(VARIANT)) {
  		  	  							variant = a[1];
  		  	  							break;
  		  	  						}
  		  	  					}  	  		
  		  	  					try {
  		  	  						m0.appendReplacement(sb," φ "+ variant+" ƕ ");
  		  	  					} catch (Exception qe) {
  		  	  						qe.printStackTrace();
  		  	  					}
	  	        			}
	  	        			m0.appendTail(sb);
	  	        			buf = sb.toString();
	  	        		}
  	  					 
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
/*	  	        			} else if  (buf.charAt(i) == '.' && !inline) {
	  	        				line = line + buf.charAt(i);
	  	        				if (!line.trim().isEmpty()) { segs.add(line); line=""; } */
	  	        			} else {
	  	        				line = line + buf.charAt(i);
	  	        			}		  	        			
	  	        		}
        				if (!line.trim().isEmpty()) {
        					segs.add(line);
        				}

        				/*
	                    Element stream = new Element("quote",Common.xmlns_ntei_p5);
	  	        		for (String s: segs) {
		                    Element l = new Element("l",Common.xmlns_ntei_p5);
                            l.setText(s);
                            stream.addContent(l);
	  	        		}   	  	        			
	                    currSegment_1.addContent(stream);
                        */
        				
	          			boolean mode = false;        				
		  	        	Parser parser = new Parser();	
	  	        		lastdiv = null;
		  	        	
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
                                    currSegment_1.setAttribute("type","time:1");
                                    if (parser.getInsertMode()) currSegment_1.setAttribute("subtype","addition");
                                    if (parser.getRasurMode()) currSegment_1.setAttribute("subtype","rasur"); 
                                    if (parser.getMarginalMode()) currSegment_1.setAttribute("subtype","marginal");
                                    Element ab = new Element("ab",Common.xmlns_ntei_p5);
                                    ab.setAttribute("ana","#head");
                                    String bp =parser.getEntity().trim(); 
                                    if (bp.contains("€")) {
                                    	String[] pre = bp.split("[€]");
                                    	Element seg = new Element("seg",Common.xmlns_ntei_p5);
                                    	seg.setText(pre[0]);
                                    	ab.addContent(seg);;
                                    	Element label = addPage("label",pre[1]);
                                    	ab.addContent(label);                        	
                                    } else {
                                    	Element label = addPage("label",bp);
                                    	ab.addContent(label);
                                    }                                  
       	        					lastdiv = ab.getText();
                                    currSegment_1.addContent(ab);
                		  	        segments_2 = new ArrayList<Element>();
                  	        		currSegment_2 = null;
                  	        		mode = false;
                                    continue;
	  	        				}
 
	  	        				
	  	        				if (q == parser.types.TIMETERM_2) {	  	        					
	  	  	  	        			segments_2.add(new Element("ab",Common.xmlns_ntei_p5));
                                    currSegment_2 = segments_2.get(segments_2.size()-1);
                                    currSegment_2.setAttribute("type","time:2");
                                    if (parser.getInsertMode()) currSegment_2.setAttribute("subtype","addition");
                                    if (parser.getRasurMode()) currSegment_2.setAttribute("subtype","rasur"); 
                                    if (parser.getMarginalMode()) currSegment_2.setAttribute("subtype","marginal");
                                    Element hd = addPage("label",parser.getEntity().trim());
      	        					currSegment_2.addContent(hd);
                                    currSegment_1.addContent(currSegment_2);
	  	        				    mode = true;
                                    continue;
	  	        				}
	  	        				if (!mode) {
	  	  	  	        			segments_2.add(new Element("ab",Common.xmlns_ntei_p5));
                                    currSegment_2 = segments_2.get(segments_2.size()-1);
                                    if (parser.getInsertMode())  currSegment_2.setAttribute("subtype","addition"); 
                                    if (parser.getRasurMode()) currSegment_2.setAttribute("subtype","rasur"); 
                                    if (parser.getMarginalMode()) currSegment_2.setAttribute("subtype","marginal");                                
                                    currSegment_1.addContent(currSegment_2);
                                    mode = true;
	  	        				}
	  	        				 	        				
	  	        				if (q == parser.types.INS) {
                                    ins = new Element("add",Common.xmlns_ntei_p5);	 
                                    if (currSegment_2 == null) currSegment_1.addContent(ins); else currSegment_2.addContent(ins);
                                    continue;
	  	        				}
	  	        				
	  	        				if (q == parser.types.EOI) {
                                    ins = null;
                                    continue;
	  	        				}
	  	        				
	  	        				if (q == parser.types.RUSUR) {
                                    Element del = new Element("del",Common.xmlns_ntei_p5);	 
                                    del.setAttribute("type","rasur");
	  	        					if (ins != null) { ins.addContent(del); } else { if (currSegment_2 == null) currSegment_1.addContent(del); else currSegment_2.addContent(del); }
	  	        				    continue;
	  	        				}

	  	        				if (q == parser.types.RASUR || q == parser.types.MASUR | q == parser.types.XASUR) {
                                    Element del = new Element("del",Common.xmlns_ntei_p5);	 
                                    del.setAttribute("type","rasur");
                                    if (q == parser.types.RASUR) del.setAttribute("subtype","signingover");
                                    if (q == parser.types.XASUR) del.setAttribute("subtype","deciphered");
                                    if (q == parser.types.MASUR) del.setAttribute("subtype","marginal");
	  	        					
                                    Element add = new Element("add",Common.xmlns_ntei_p5);
                                    del.addContent(add);

	  	        					while (All.contains(parser.foresee())) {	  	        						
	  	        						q= parser.next();
	  		  	        				if (Entities.contains(q)) {	 
	  		  	        					add = Entities (q, parser, add);
	 		  	        				} else if (Description.contains(q)) {
	  		  	        					add = Description(q, parser, add);
	  		  	        				} else if (q == parser.types.TYPUS) {
	  		  	        					add = Cantus(q, parser, add);
	 		  	        				} else if (q == parser.types.CANTO || q == Types.NEUME) {
	 		  	        					add = Neumes(q, parser, add);	
	 		  	        				} else if (q == parser.types.CHOICE) {
	 			 	        				add = Choice(q, parser, add);  	        					
	 		  	        				} else if (q == parser.types.PAGE) {
	 		  	        					add = Page(q, parser, add);  	        					
		  	        					}
	  	        					}	
	  	                  			if (ins != null) { ins.addContent(del); } else { if (currSegment_2 == null) currSegment_1.addContent(del); else currSegment_2.addContent(del); }
	  	                  			continue;               
	  	        				}
	  	        				if (q == parser.types.MARGINAL ) {
                                    Element add = new Element("add",Common.xmlns_ntei_p5);	 
                                    add.setAttribute("type","marginal");
	  	        					while (All.contains(parser.foresee())) {	  	        						
	  	        						q= parser.next();
	  		  	        				if (Entities.contains(q)) {	 
	  		  	        					add = Entities (q, parser, add);
	 		  	        				} else if (Description.contains(q)) {
	  		  	        					add = Description(q, parser, add);
	 		  	        				} else if (q == parser.types.TYPUS) {
	  		  	        					add = Cantus(q, parser, add);
	 		  	        				} else if (q == parser.types.CANTO || q == Types.NEUME) {
	 		  	        					add = Neumes(q, parser, add);
	 		  	        				} else if (q == parser.types.CHOICE) {
	 			 	        				add = Choice(q, parser, add);  	        					
		  	        					}
	  	        					}	
	  	                  			if (ins != null) { ins.addContent(add); } else { if (currSegment_2 == null) currSegment_1.addContent(add); else currSegment_2.addContent(add); }
	  	                  			continue;               
	  	        				}

	  	        				if (q == parser.types.COMMENT ) {
                                    Element note = new Element("note",Common.xmlns_ntei_p5);	 
	 	        					note = Description(q, parser, note);	  	        					
	  	                  			if (ins != null) { ins.addContent(note); } else { if (currSegment_2 == null) currSegment_1.addContent(note); else currSegment_2.addContent(note); }
	  	        				    continue;
	  	        				}
	  	        				
	  	        				if (q == parser.types.TYPUS) {
	  	        					ins = Cantus(q, parser, ins);
	  	        					continue;
	  	        				}	
	  	        				
	  	        				if (q == parser.types.CANTO || q == Types.NEUME) {
	  	        					ins = Neumes(q, parser, ins);
	  	        					continue;
	  	        				}	

	  	        				if (Entities.contains(q)) {	 
	  	        					ins = Entities(q, parser, ins);
	  	        					continue;
  		  	        			}	
	  	        					
	  	        				if (Description.contains(q)) {	  	        		
	 	        					ins = Description(q, parser, ins);
	 	        					continue;
	  	         		  	    }

	  	        				if (q == parser.types.CHOICE) {
	 	        					ins = Choice(q, parser, ins);
                                    continue;
	  	        				}    
	  	        				
	  	        				if (q == parser.types.PAGE) {
	 	        					ins = Page(q, parser, ins);
                                    continue;
	  	        				}    
	  	        				
	  	        			}
	  	        		}	
	  	        	}
	  	        	for (Element e: segments_1) {
	  	        		if (e.getChildren().size() > 0) feast.addContent(e);  
	  	        	}
	  	        }
	  		
	  	        StringBuffer sb = new StringBuffer();
    			Pattern p0 = Pattern.compile("φ(.*?)ƕ");
      	        Matcher m0 = p0.matcher(op.outputString(target).replaceAll(" φ ", "φ").replaceAll(" ƕ ", "ƕ"));	  	        
      	  	  	        
	  	        Document snippet = new Document();
       			int i = 0;
       			
	  	        while (m0.find()) {
	  	        	i++;
	  	        	String s = m0.group().replaceAll("φ", "<metamark>").replaceAll("ƕ", "</metamark>");
	  	        	
	  	        	try {
	  	        		if (m0.group().equals("φƕ")) {
	  	        			s=m0.group().replaceAll("φ", "<metamark function=\"variant\" xml:id=\"V\\."+new Integer(i).toString()+"\" />").replaceAll("ƕ","");
	  	        		} else {	
	 	        			String p =m0.group().replaceAll("φ", "<metamark function=\"variant\" xml:id=\"V\\."+new Integer(i).toString()+"\">").replaceAll("ƕ", "</metamark>").replaceAll("l:","");
	 	        			snippet = builder.build(new StringReader(p));
	 	        			s =m0.group().replaceAll("φ", "<metamark function=\"variant\" xml:id=\"V\\."+new Integer(i).toString()+"\">").replaceAll("ƕ", "</metamark>");
	  	        		}	
	  	        	} catch (Exception q) {
	  	        		s=m0.group().replaceAll("φ", "<metamark function=\"variant\" xml:id=\"V\\."+new Integer(i).toString()+"\" />").replaceAll("ƕ", "<metamark corresp=\"#V\\."+new Integer(i).toString()+"\" />");
	  	        	};	
	  	        	m0.appendReplacement(sb,s);
	  	        }
	  	        m0.appendTail(sb);      

	    	 	String xml = sb.toString()   	 			
	    	 			.replaceAll("¬", "").replaceAll("</ab><ab","</ab> <ab")
		     			.replaceAll("ö\\+","<note type=\"supplied\">")
		     			.replaceAll("\\+ö","</note>")
		     			.replaceAll("ä\\+","<note type=\"supplied\">")
		     			.replaceAll("\\+ä","</note>")
				     	.replaceAll("<metamark function=\"variant\" xml:id=\"(V.[0-9]+)\"> </metamark>", "<metamark  function=\"variant\" xml:id=\"$1\"/>")     	
				     	.replaceAll("<metamark function=\"variant\" xml:id=\"(V.[0-9]+)\"></metamark>", "<metamark  function=\"variant\" xml:id=\"$1\"/>")     	
		     			.replaceAll(" <metamark","<metamark")
				     	.replaceAll("\\s+"," ")
				     	.replaceAll("°",".")
			     		.replaceAll("ß\\+(.*?)\\+ß", "<del>$1</del>")
				     	.replaceAll("<ab> </ab>","")
    	 			 	.replaceAll("Γ","<gap/>")
		     			.replaceAll("§(.*?)::(.*?)§","<choice><sic>$1</sic><corr>$2</corr></choice>")
		        		.replaceAll(" ,",",")
		        		.replaceAll(" \\.","\\.")
		        		.replaceAll("<del></del>","")
		        		.replaceAll("<del/>","")
				     	.replaceAll(" </metamark><metamark function=\"variant\" xml.id=\"(V.[0-9]+)\">(\\.|,)", "</metamark><metamark  function=\"variant\" xml:id=\"$1\">$2")     	
				     	.replaceAll(" </metamark><metamark function=\"variant\" xml.id=\"(V.[0-9]+)\"[ ]*/>(\\.|,)", "</metamark><metamark  function=\"variant\" xml:id=\"$1\"/>$2")     	
				     	.replaceAll(" </metamark><metamark function=\"variant\" xml.id=\"(V.[0-9]+)\"[ ]*/><metamark function=\"variant\" xml.id=\"(V.[0-9]+)\"[ ]*/>(\\.|,)", "</metamark><metamark  function=\"variant\" xml:id=\"$1\"/><metamark  function=\"variant\" xml:id=\"$2\"/>$3")     	
				     	.replaceAll(" </metamark><metamark function=\"variant\" xml.id=\"(V.[0-9]+)\"[ ]*/><metamark function=\"variant\" xml.id=\"(V.[0-9]+)\">(\\.|,)", "</metamark><metamark  function=\"variant\" xml:id=\"$1\"/><metamark  function=\"variant\" xml:id=\"$2\">$3")     	
					     .replaceAll(" </metamark></l:([A-Z]{2,6})>(\\.|,)","</metamark></l:$1>$2")
					     .replaceAll(" </metamark></l:([A-Z]{2,6})><metamark function=\"variant\" xml.id=\"(V.[0-9]+)\"[ ]*/>(\\.|,)","</metamark></l:$1><metamark  function=\"variant\" xml:id=\"$2\"/>$3")
					     .replaceAll(" </metamark></l:([A-Z]{2,6})><metamark function=\"variant\" xml.id=\"(V.[0-9]+)\">(\\.|,)","</metamark></l:$1><metamark  function=\"variant\" xml:id=\"$2\">$3")
					     .replaceAll(" </metamark></l:([A-Z]{2,6})><metamark function=\"variant\" xml.id=\"(V.[0-9]+)\"[ ]*/><metamark function=\"variant\" xml.id=\"(V.[0-9]+)\"[ ]*/>(\\.|,)","</metamark></l:$1><metamark  function=\"variant\" xml:id=\"$2\"/><metamark  function=\"variant\" xml:id=\"$3\"/>$4")
						 .replaceAll(" <note>(.*?)</note> ", "<note>$1</note>")			     	
				     	.replaceAll("\\.<", ". <")
				     	.replaceAll("(\\.|,)<","$1 <" )
				     	.replaceAll("@", " ")
				     	.replaceAll(" </metamark>(\\.|,)","</metamark>$1") 
	    	 	        .replaceAll("Ä\\+","<note type=\"supplied\">").replaceAll("\\+Ä","</note>")
	    	 			.replaceAll("[+]","<unclear />")
	    	 			.replaceAll("¥E::(.*?)¥","<add>$1</add>");

	    	
    	 		try {
    	 			
    	 			target = builder.build(new org.xml.sax.InputSource(new ByteArrayInputStream(xml.getBytes("utf-8"))));

  	 		} catch (Exception x) {
    	 			log.info(xml);
    	 		}
    	 		
	            xPath = XPath.newInstance("//t:div[preceding-sibling::t:div[1][t:ab/t:add[@type='marginal' and .='']]]");								
	 			xPath.addNamespace(Common.xmlns_tei_p5);
	 	  	    List<Element> list = xPath.selectNodes(target);
	 	  	        
	 	  	    for (Element m: list) {
	 	  	      	m.setAttribute("subtype", "marginal");
	 	  	    }

	 	  	    
	            xPath = XPath.newInstance("//t:div[preceding-sibling::t:div[1][t:ab/t:del[@type='rasur' and t:add ='']]]");								
	 			xPath.addNamespace(Common.xmlns_tei_p5);
	 	  	    list = xPath.selectNodes(target);
	 	  	        
	 	  	    for (Element m: list) {
	 	  	      	m.setAttribute("subtype", "rasur");
	 	  	    }


	            xPath = XPath.newInstance("//t:ab/t:add[@type='marginal' and .='']");								
	 			xPath.addNamespace(Common.xmlns_tei_p5);
	 	  	    list = xPath.selectNodes(target);
	 	  	        
	 	  	    for (Element m: list) {
	 	  	    	m.getParent().removeContent(m);
	 	  	    }

	            xPath = XPath.newInstance("//t:del[@type='rasur' and t:add ='']");								
	 			xPath.addNamespace(Common.xmlns_tei_p5);
	 	  	    list = xPath.selectNodes(target);
	 	  	        
	 	  	    for (Element m: list) {
	 	  	    	m.getParent().removeContent(m);
	 	  	    }

	 	  	    
	            xPath = XPath.newInstance("//t:add[@type='marginal' and .='']");								
	 			xPath.addNamespace(Common.xmlns_tei_p5);
	 	  	    list = xPath.selectNodes(target);
	 	  	        
	 	  	    for (Element m: list) {
	 	  	    	m.getParent().removeContent(m);
	 	  	    }
  	 		
	 	  	    
	
 	  	        
   	 		
	        } catch (Exception e) {
	        	log.error(e.getLocalizedMessage(),e);	
	        }
	        return target;	

	  }      

	  private Element Choice(Types q, Parser parser, Element elem) {        
    	
		  String sp = parser.getEntity();
		  int ipos = sp.indexOf("::"); 
		  Element choice = new Element("choice",Common.xmlns_ntei_p5);	  	        										       
		  Element orig = new Element("sic",Common.xmlns_ntei_p5);	  	        										       
		  Element corr = new Element("corr",Common.xmlns_ntei_p5);	  	        										       
		  orig.setText(sp.substring(0,ipos));
		  corr.setText(sp.substring(ipos+2	));
		  choice.addContent(orig);
		  choice.addContent(corr);
		  if (elem != null) { elem.addContent(choice); } else { if (currSegment_2 == null) currSegment_1.addContent(choice); else currSegment_2.addContent(choice); }
		  return elem;
	  }         
	        
	  private Element Cantus(Types q, Parser parser, Element elem) {
		  
		    boolean conj = parser.getConjecture();
			Element seg = new Element(parser.getEntity(),Common.xmlns_cantus);
			if (conj) seg.setAttribute("subtype","supplied");
			while(true) {
				Types x = parser.foresee();				
				if (Types.WHITESPACE == x || Types.PAGE == x) parser.next();
				else break;
			}
			while (Typus.contains(parser.foresee())) {
				q= parser.next();
				if (q == Types.NEUME) {
        			Element phr = new Element("phr",Common.xmlns_ntei_p5);
        			phr.setAttribute("type","neume"); 
					parseEmendations(phr, parser.getEntity().trim());  //
  					seg.addContent(phr);  	  	        							
				} else {
					parseEmendations(seg, parser.getEntity().trim());
				}
			}	  	  
			if (elem != null) { elem.addContent(seg); } else { if (currSegment_2 == null) currSegment_1.addContent(seg); else currSegment_2.addContent(seg); }
	        return elem;
	  }
	
	  
	  private Element Entities (Types q, Parser parser, Element elem) {
	        Element seg = addPage("seg",parser.getEntity());	  	        	
	        if (q == Types.PERSON) seg.setAttribute("ana", "#person");
	        else if (q == Types.PLACE) seg.setAttribute("ana", "#place");
	        else if (q == Types.FUNCTION) seg.setAttribute("ana", "#function");
	        else if (q == Types.DEL) seg.setAttribute("ana", "#strikethrough");
            
            if (elem != null) { elem.addContent(seg); } else { if (currSegment_2 == null) currSegment_1.addContent(seg); else currSegment_2.addContent(seg); }
            return elem;
	  }
	  
	  private Element addPage (String name, String content) {
		  Element elem = new Element(name,Common.xmlns_ntei_p5);
	        if (content.contains("(")) {
	        	String a[] = content.split("[()]");
	        	elem.addContent(a[0]);
	        	Element pb = new Element("pb",Common.xmlns_ntei_p5);
	        	pb.setAttribute("n", a[1]);
	        	elem.addContent(pb);
	        	elem.addContent(a[2]);
	        } else {
	        	elem.setText(content);
	        }			  
		  return elem;
	  }
	  
	  private Element Neumes (Types q, Parser parser, Element elem) {
			String typ = "NO";
			if (lastdiv != null && lastdiv.equals("Officium")) {
				typ = "IN";
				
				lastdiv = null;
			}
			Element seg = new Element(typ,Common.xmlns_cantus);
			if (parser.getConjecture()) seg.setAttribute("subtype","supplied");
			if (q == Types.NEUME) {
					Element phr = new Element("phr",Common.xmlns_ntei_p5);
					phr.setAttribute("type","neume"); 
					parseEmendations(phr, parser.getEntity().trim());
					seg.addContent(phr);  	  	        							
			} else {
				parseEmendations(seg, parser.getEntity().trim());
			}
			if (elem != null) { elem.addContent(seg); } else { if (currSegment_2 == null) currSegment_1.addContent(seg); else currSegment_2.addContent(seg); }
	        return elem;
	  }	

	  private Element Page(Types q, Parser parser, Element elem) {        
	    	
		  Element pb = new Element("pb",Common.xmlns_ntei_p5);
		  pb.setAttribute("n", parser.getEntity());
		  if (elem != null) { elem.addContent(pb); } else { if (currSegment_2 == null) currSegment_1.addContent(pb); else currSegment_2.addContent(pb); }
		  return elem;
	  }         

	  
	  private Element Description (Types q, Parser parser, Element elem) {
		  	while (true) {	
		  	    if (q == Types.PAGE) {		  	    	
		  	        Element pb = new Element("pb",Common.xmlns_ntei_p5);	  	        							
		  	        pb.setAttribute("n",parser.getEntity());
		  	        if (elem != null) { elem.addContent(pb); } else { if (currSegment_2 == null) currSegment_1.addContent(pb); else currSegment_2.addContent(pb); }
		  	        if (parser.foresee() == Types.WHITESPACE) parser.next();
		  	     } else {
  			    if (elem != null) { elem.addContent(parser.getEntity()); } else { if (currSegment_2 == null) currSegment_1.addContent(parser.getEntity()); else currSegment_2.addContent(parser.getEntity()); }
		  	     }	
		  	    if (!Description.contains(parser.foresee())) break;
		  	    q= parser.next();	
		  	 }	 
		  	 return elem;
	  }
	  
	  private void parseEmendations(Element seg, String s) {
			
		  int bp = 0;
		  String ch;		
		  String buf ="";
		  
		  while (bp < s.length()) {
			  ch = String.valueOf(s.charAt(bp++));
			  if (ch.equals("§")) {
			     if (!buf.isEmpty()) {
			    	seg.addContent(buf);
			    	buf = "";
			     }
			     String sp = "";
				 if (s.substring(bp).startsWith("R§")) {
					 bp+=2;
			       	 Element del = new Element("del",Common.xmlns_ntei_p5);	  	        										       
			  	     del.setAttribute("type","rasur");		  	       		  	 			       			       					 
					 seg.addContent(del);
				 } else if (s.substring(bp).startsWith("R::")) {
					 bp+=3;
					 while (true) {
						 ch = String.valueOf(s.charAt(bp++));
						 if (ch.equals("§") || bp > s.length()-1) { break; };
						 sp+=ch;
					 }					 
					 if (!sp.isEmpty()) {
				       	 Element del = new Element("del",Common.xmlns_ntei_p5);	  	        										       
				       	 Element add = new Element("add",Common.xmlns_ntei_p5);	  	        										       
				  	     del.setAttribute("type","rasur");		  	       		  	 			       			       					 
				  	     del.setAttribute("subtype","signingover");		  	       		  	 			       			       					 
						 add.setText(sp.trim());
						 del.addContent(add);
						 seg.addContent(del);
					 }
				 } else if (s.substring(bp).startsWith("RM::")) {
					 bp+=4;
					 while (true) {
						 ch = String.valueOf(s.charAt(bp++));
						 if (ch.equals("§") || bp > s.length()-1) { break; };
						 sp+=ch;
					 }					 
					 if (!sp.isEmpty()) {
				       	 Element del = new Element("del",Common.xmlns_ntei_p5);	  	        										       
				       	 Element add = new Element("add",Common.xmlns_ntei_p5);	  	        										       
				  	     del.setAttribute("type","rasur");		  	       		  	 			       			       					 
				  	     add.setAttribute("subtype","marginal");		  	       		  	 			       			       					 
						 add.setText(sp.trim());
						 del.addContent(add);
						 seg.addContent(del);
					 }
				 } else if (s.substring(bp).startsWith("RE::")) {
					 bp+=4;
					 while (true) {
						 ch = String.valueOf(s.charAt(bp++));
						 if (ch.equals("§") || bp > s.length()-1) { break; };
						 sp+=ch;
					 }					 
					 if (!sp.isEmpty()) {
				       	 Element del = new Element("del",Common.xmlns_ntei_p5);	  	        										       
				       	 Element add = new Element("add",Common.xmlns_ntei_p5);	  	        										       
				  	     del.setAttribute("type","rasur");		  	       		  	 			       			       					 
				  	     add.setAttribute("subtype","deciphered");		  	       		  	 			       			       					 
						 add.setText(sp.trim());
						 del.addContent(add);
						 seg.addContent(del);
					 }
				 } else if (s.substring(bp).startsWith("M::")) {
					 bp+=3;
					 while (true) {
						 ch = String.valueOf(s.charAt(bp++));
						 if (ch.equals("§") || bp > s.length()-1) { break; };
						 sp+=ch;
					 }					 
					 if (!sp.isEmpty()) {
				       	 Element add = new Element("add",Common.xmlns_ntei_p5);	  	        										       
				  	     add.setAttribute("type","marginal");		  	       		  	 			       			       					 
				  	     add.setText(sp.trim());
						 seg.addContent(add);
					 }
				 } else if (s.substring(bp).startsWith("I::")) {
					 bp+=3;
					 while (true) {
						 ch = String.valueOf(s.charAt(bp++));
						 if (ch.equals("§") || bp > s.length()-1) { break; };
						 sp+=ch;
					 }					 
					 if (!sp.isEmpty()) {
				       	 Element note = new Element("note",Common.xmlns_ntei_p5);	  	        										       
				       	 note.setText(sp.trim());
						 seg.addContent(note);
					 }
				 } else {
					 while (true) {
						 ch = String.valueOf(s.charAt(bp++));
						 if (ch.equals("§") || bp > s.length()-1) { break; };
						 sp+=ch;
					 }			
					 if (sp.contains("::")) {
						int ipos = sp.indexOf("::"); 
				       	Element choice = new Element("choice",Common.xmlns_ntei_p5);	  	        										       
				       	Element orig = new Element("sic",Common.xmlns_ntei_p5);	  	        										       
				       	Element corr = new Element("corr",Common.xmlns_ntei_p5);	  	        										       
						orig.setText(sp.substring(0,ipos));
						corr.setText(sp.substring(ipos+2	));
						choice.addContent(orig);
						choice.addContent(corr);
						seg.addContent(choice);
					 }
				 }
			  } else if (ch.equals("(")) {
				  if (!buf.isEmpty()) {
					   seg.addContent(buf);
					   buf = "";
				  }
			      String sp = "";
				  while (true) {
					  ch = String.valueOf(s.charAt(bp++));
					  if (ch.equals(")") || bp > s.length()-1) break;	
					  sp+=ch;
				   }
	        	   Element pb = new Element("pb",Common.xmlns_ntei_p5);	  	        										       
		  	       pb.setAttribute("n",sp);		  	       
		  	       seg.addContent(pb);
			  } else if (ch.equals("{")) {
				  if (!buf.isEmpty()) {
					   seg.addContent(buf);
					   buf = "";
				  }
			      String sp = "";
				  while (true) {
					  ch = String.valueOf(s.charAt(bp++));
					  if (ch.equals("}") || bp > s.length()-1) break;	
					  sp+=ch;
				   }
	        	   Element note = new Element("note",Common.xmlns_ntei_p5);	  	        										       
	        	   note.setText(sp);
	        	   note.setAttribute("type","supplied");
		  	       seg.addContent(note);		  	       
			  } else if (ch.equals("[")) {
				  if (!buf.isEmpty()) {
					   seg.addContent(buf);
					   buf = "";
				  }
			      String sp = "";
				  while (true) {
					  ch = String.valueOf(s.charAt(bp++));
					  if (ch.equals("]") || bp > s.length()-1) break;	
					  sp+=ch;
				   }
	        	   Element ins = new Element("ins",Common.xmlns_ntei_p5);	  	        										       
	        	   ins.setText(sp);
		  	       seg.addContent(ins);		  	       
			  } else if (ch.equals("¥")) {
				  if (!buf.isEmpty()) {
					   seg.addContent(buf);
					   buf = "";
				  }
			      String sp = "";
			      bp+=3;
				  while (true) {
					  ch = String.valueOf(s.charAt(bp++));
					  if (ch.equals("¥") || bp > s.length()-1) break;	
					  sp+=ch;
				   }
	        	   Element add = new Element("add",Common.xmlns_ntei_p5);	  	        										       
	        	   add.setText(sp);
	        	   seg.addContent(add);			  
			  } else {
      				buf+=ch;
 			  }
		  }
		  if (!buf.isEmpty()) {
		      seg.addContent(buf);
		      buf = "";
		  }
	}
      
	  private void readConfig() {
			boolean times_1 = false, times_2 = false, types = false;
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
	   	        	if (line.equals("%Types")) {times_1=false; times_2=false; types=true; continue;}
	   	        	if (types) { TYPES.add(line.trim()); TYPES.add("["+line.trim()+"]"); TYPES.add("_"+line.trim()); TYPES.add("[_"+line.trim()+"]"); } 
	   	         }
	   	         in.close();
	   	         	   	         
	   		} catch (Exception e) {
	   			log.error(e.getLocalizedMessage(),e);	
	   		}
		  
	  }
	   
	  
	     private String parseVariant(String buf) {	  
	    	 
       	 
		 		if (!VARIANT.isEmpty()) {
		   			Pattern p0 = Pattern.compile("(//)(.*?)(//)");
		   			Matcher m0 = p0.matcher(buf);
		   			StringBuffer sb = new StringBuffer();
		 				  	  				
		   			while (m0.find())  {
		   				String variant = "";
		   				String s = m0.group();	  	        				
		   				s = s.substring(2,s.length()-1);
		   				log.info(s);
		   				boolean test = this.urtext && s.contains("µ");
		   				Pattern p1 = Pattern.compile("("+(test ? "µ" : "")+".*?)~(.*?)/");
		   					Matcher m1 = p1.matcher(s);
		   					while (m1.find()) {
		   						String[] a = m1.group().substring(0, m1.group().length()-1).split("~");
		   						if (test || a[0].contains(VARIANT)) {
		   							variant = a[1];
		   							break;
		   						}
		   					}  	  		
		   					try {
		  	  					m0.appendReplacement(sb," φ "+ variant+ " ƕ ");		  	  					 
		  	  			    } catch (Exception e) {
		   						e.printStackTrace();
		   					}
		   			}
		   			m0.appendTail(sb);
		   			buf = sb.toString();
		   		}
		         return buf;
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
		        log.error(e.getLocalizedMessage(),e);	
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
		        log.error(e.getLocalizedMessage(),e);	
		    }
		    return in;
		}


	  
	  private String trunc(Types t) {
		  
		  String s = t.toString();
		  return "|"+s.substring(0,2);
	  }

	  private class Parser {
			
		  public Types types;
		  
		  public static final String SEPARATOR = " ,.$¥§(%~¦¬";
		  		  
		  private String buf;
		  private String entity;
		  private int bp;
		  private boolean conjecture;
		  private boolean addition;
		  private boolean rasur; 
		  private boolean marginal; 
			  
		  public Parser() {
		  }
		  
		  public void set( String s) {
			  
			  addition = false;
			  rasur = false;
			  marginal = false;
			  
			  if (s.startsWith("¥E¥")) {
				  addition = true;
				  s = s.substring(3);
			  }
			  
			  if (s.startsWith("§M::") && s.trim().endsWith("§")) {
				  marginal = true;
				  s = s.substring(4,s.length()-1);
			  }
			  if (s.startsWith("§R::") && s.trim().endsWith("§")) {
				  rasur = true;
				  s = s.substring(4,s.length()-1);
			  }
			  
			  buf = s;
			  bp = 0;
		  }
		  
		  
		  public Types next() {
			  
			  String ch;
			  entity = "";
			  if (bp > buf.length()-1) return types.EOT;
			  
			  ch = String.valueOf(buf.charAt(bp++));
			  conjecture = false;
			  
			  if (ch.equals(".") || ch.equals("¬")) {
				  entity = ch;
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
			  } else if (ch.equals("¥")) {
				  if (buf.substring(bp).startsWith("E::")) {
					bp+=3;
				    return log(Types.INS);	 
				  }	    
				  return log(Types.EOI);
			  } else if (ch.equals("§")) {
				  if (buf.substring(bp).startsWith("R§")) {
					 bp+=2;
					 return log(types.RUSUR);
				  } else if (buf.substring(bp).startsWith("R::")) {
					 bp+=3;
					 return log(types.RASUR);					 
				  } else if (buf.substring(bp).startsWith("RM::")) {
					 bp+=4;
					 return log(types.MASUR);
				  } else if (buf.substring(bp).startsWith("RE::")) {
					 bp+=4;
					 return log(types.XASUR);
				  } else if (buf.substring(bp).startsWith("M::")) {
					 bp+=3;
					 return log(types.MARGINAL);					 
				  } else if (buf.substring(bp).startsWith("I::")) {
					 bp+=3;
					 return log(types.COMMENT);
				  } else {
					 int cp = bp; 
					 while (true) {
						 if (bp > buf.length()-1) break;
						 ch = String.valueOf(buf.charAt(bp++));
						 if (ch.equals("§")) break;
						 if (ch.equals("¥")) {
		                	 bp = cp; // +1;
	                    	 return log(types.EOA);						 
						 }
						 entity+=ch;
					 }		
                     if (entity.contains("::")) {
                    	 return log(types.CHOICE);
                     } else {
                    	 bp = cp; // +1;
                    	 return log(types.EOA);
                     }	 
				  }
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
			  } else if (ch.equals("ü")) {
				  while (true) {
					  ch = String.valueOf(buf.charAt(bp++));
					  if (ch.equals("}") || bp > buf.length()-1) break;
					  entity+=ch;
				  }
				  return log(types.DEL);
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
					  if (ch.equals("}") || bp > buf.length()) break;
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
			  } else if (ch.equals("Ü")) {
				  while (true) {
					  ch = String.valueOf(buf.charAt(bp++));
					  if (ch.equals("}") || bp > buf.length()-1) break;
					  entity+=ch;
				  }
				  return log(types.PLACE);				  
			  } else if (ch.equals("Δ")) {
				  while (true) {
					  ch = String.valueOf(buf.charAt(bp++));
					  if (ch.equals("[")) conjecture = true;
					  if (ch.equals("}") || bp > buf.length()-1) break;
					  entity+=ch;
				  }
				  entity = entity.replaceAll("\\[","ä+").replaceAll("\\]","+ä");
				  return log(types.TIMETERM_1);				  
			  } else if (ch.equals("Θ")) {
				  while (true) {
					  ch = String.valueOf(buf.charAt(bp++));
					  if (ch.equals("}") || bp > buf.length()-1) break;
					  entity+=ch;
				  }
				  entity = entity.replaceAll("\\[","ä+").replaceAll("\\]","+ä");
				  return log(types.TIMETERM_2);				  
			  } else {	  
				  int cp = bp;
				  while (true) {
					  if (SEPARATOR.contains(ch) || bp > buf.length()-1) { break; };
					  if (ch.equals("[")) conjecture = true;
					  entity+=ch;
					  ch = String.valueOf(buf.charAt(bp++));
				  }
				  
				  if( (SEPARATOR.contains(ch) && bp <= buf.length()) || bp <= buf.length()-1 ) { bp--;} else {entity+=ch;}
				  
				  if (TYPES.contains(entity)) {
						 return log(types.TYPUS);				  
				  }
				  
				  entity = entity.replaceAll("\\[","ö+").replaceAll("\\]","+ö");
				  return log(types.TEXT);
			  }
		  	
		  }
		  
		  
		  private Types log (Types q) {
	//		  System.out.println(q+"!"+entity);
	    	  return q;
		  }
		  
		  public Types foresee() {
			  int tbp = bp;
			  Types result = next();
			  bp = tbp;
			  return result;	
		  }
		  
		  public boolean getInsertMode() { 
			  boolean mode = addition;
			  addition = false;
			  return mode; //
		  } //
		  
		  public boolean getRasurMode() { 
			  boolean mode = rasur;
			  return mode; //
		  } //
		 
		  public boolean getMarginalMode() {
			  boolean mode = marginal;
			  return mode; //
		  } //
		  
		  public boolean getConjecture() {
			  return conjecture;
		  }
		  
		  public String getEntity() {
			  return entity.replaceAll("\\[","").replaceAll("\\]","");
		  }
	  
	  }
	  

	  
}
