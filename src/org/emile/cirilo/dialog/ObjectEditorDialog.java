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

import voodoosoft.jroots.core.CServiceProvider;
import voodoosoft.jroots.core.CPropertyService;
import voodoosoft.jroots.core.gui.CEventListener;
import voodoosoft.jroots.core.gui.CMouseListener;
import voodoosoft.jroots.dialog.*;

import org.openrdf.rio.RDFParser;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;
import org.emile.cirilo.utils.ImagePreviewPanel;
import org.emile.cirilo.utils.ImageTools;
import org.emile.cirilo.Common;
import org.emile.cirilo.ServiceNames;
import org.emile.cirilo.User;
import org.emile.cirilo.ecm.repository.*;
import org.emile.cirilo.ecm.repository.FedoraConnector.Relation;
import org.emile.cirilo.business.*;
import org.emile.cirilo.utils.*;
import org.emile.cirilo.business.IIIFFactory;
import org.apache.log4j.Logger;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import java.util.*;
import java.io.*;
import java.net.URL;
import java.text.MessageFormat;



/**
 *  Description of the Class
 *
 * @author     Johannes Stigler
 * @created    10.3.2011
 */
public class ObjectEditorDialog extends CDialog {

	private static Logger log = Logger.getLogger(ObjectEditorDialog.class);
	/**
	 *  Constructor for the LoginDialog object
	 */

	public ObjectEditorDialog() {}

	
	/**
	 *  Description of the Method
	 *
	 * @param  e  Description of the Parameter
	 */
	public void handleCancelButton(ActionEvent e) {
		try {
			if ( op[2].contains("Inactive") && statusChanged) Repository.modifyObject(pid, "I", null, null);
		} catch (Exception ex) {}	
		try {
		    JTable loMD = (JTable) getGuiComposite().getWidget(jtMetadata);	
		    JTable loDS = (JTable) getGuiComposite().getWidget(jtDatastreams);
		    JTable[] loTable = {loMD, loDS}; 
			org.emile.cirilo.dialog.CBoundSerializer.save(this.getCoreDialog(), se.getObjectDialogProperties(), loTable);   		    
		} catch (Exception ex) {}	
		finally {
			close();
		}		
	}

   public void set (String pid, String label, String model, String owner) {
	   this.pid = pid;
	   this.model = model;
	   this.label = label;
	   this.owner = owner;
   }
	
	public void handleSaveButton(ActionEvent e) {
		  try {
				
			  getCoreDialog().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			  statusChanged= false;
			  Repository.modifyObject( pid, "A",
			    		this.label,
						(String)((JComboBox) getGuiComposite().getWidget("jcbUser")).getSelectedItem()); 
		  }  catch (Exception ex) {
			  log.error(ex.getLocalizedMessage(),ex);	
		   }					  
			finally {
				getCoreDialog().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
	}

	public void handleSaveRelationsButton(ActionEvent e) {
		  try {
				
			  getCoreDialog().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			  JList ltRels = (JList) getGuiComposite().getWidget( "jtRelations" );
			  DefaultListModel lm = (DefaultListModel) ltRels.getModel();
			  for (  Map.Entry<String, String> r : coll.entrySet()) {
				  Repository.purgeRelation("info:fedora/"+pid,Common.isMemberOf, r.getValue());
			   }
			  for (Enumeration el = lm.elements() ; el.hasMoreElements() ;) {
				  String s= el.nextElement().toString();
				  Split id = new Split(s);
				  Repository.addRelation("info:fedora/"+pid,Common.isMemberOf,id.get());
			   }

			  TEI t = new TEI(null,false,true);
			  t.setUser(this.owner);
       		  if (t.get(pid)) {
       			   t.createRELS_INT(null);
       		  }	   

  				
		   }  catch (Exception ex) {
		   }					  
			finally {
				getCoreDialog().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
	}
	
	public void handleRelsMouseDoubleClick(MouseEvent e, int type) {

		try {

			CEventListener.setBlocked(true);
			// no events while handling

			if (type == MouseEvent.MOUSE_CLICKED) {
				if (e.getClickCount() >= 2) {
					handleRemoveRelationButton(null);	
				}
			}
		} catch (Exception ex) {
		} finally {
			CEventListener.setBlocked(false);
		}

	}	

	public void handleNonRelsMouseDoubleClick(MouseEvent e, int type) {

		try {

			CEventListener.setBlocked(true);
			// no events while handling

			if (type == MouseEvent.MOUSE_CLICKED) {
				if (e.getClickCount() >= 2) {
					handleAddRelationButton(null);	
				}
			}
		} catch (Exception ex) {
		} finally {
			CEventListener.setBlocked(false);
		}

	}	

	public void handleAddRelationButton( ActionEvent ev ) {
		try {
			JList ltRels = (JList) getGuiComposite().getWidget( "jtRelations" );
			DefaultListModel lma = (DefaultListModel) ltRels.getModel();
			JList ltNonRels = (JList) getGuiComposite().getWidget( "jtNonRelations" );
			DefaultListModel lmb = (DefaultListModel) ltNonRels.getModel();

			int[] sel = ltNonRels.getSelectedIndices();
			ArrayList rm = new ArrayList( 16 );

			for ( int i = 0; i < sel.length; i++ ) {
				lma.addElement( lmb.getElementAt( sel[i] ) );
				rm.add( lmb.getElementAt( sel[i] ) );
			}
			for ( int i = 0; i < rm.size(); i++ ) {
				lmb.removeElement( rm.get( i ) );
			}
			ltRels.setSelectedIndex( ltRels.getLastVisibleIndex() );
			ltNonRels.setSelectedIndex( ltNonRels.getLastVisibleIndex() );
			getGuiComposite().getWidget( "jbRemoveRelation" ).setEnabled( !lma.isEmpty() );
			getGuiComposite().getWidget( "jbAddRelation" ).setEnabled( !lmb.isEmpty() );
			getGuiComposite().getWidget( "jbSaveRelations" ).setEnabled(true);
		}
		catch ( Exception e ) {
		}
	}


	/**
	 *  Description of the Method
	 *
	 * @param  ev  Description of the Parameter
	 */
	public void handleRemoveRelationButton( ActionEvent ev ) {
		try {
			JList ltRels = (JList) getGuiComposite().getWidget( "jtRelations" );
			DefaultListModel lma = (DefaultListModel) ltRels.getModel();
			JList ltNonRels = (JList) getGuiComposite().getWidget( "jtNonRelations" );
			DefaultListModel lmb = (DefaultListModel) ltNonRels.getModel();

			int[] sel = ltRels.getSelectedIndices();
			ArrayList rm = new ArrayList( 16 );

			for ( int i = 0; i < sel.length; i++ ) {
				lmb.addElement( lma.getElementAt( sel[i] ) );
				rm.add( lma.getElementAt( sel[i] ) );
			}
			for ( int i = 0; i < rm.size(); i++ ) {
				lma.removeElement( rm.get( i ) );
			}
			ltRels.setSelectedIndex( ltRels.getLastVisibleIndex() );
			ltNonRels.setSelectedIndex( ltNonRels.getLastVisibleIndex() );
			getGuiComposite().getWidget( "jbRemoveRelation" ).setEnabled( !lma.isEmpty() );
			getGuiComposite().getWidget( "jbAddRelation" ).setEnabled( !lmb.isEmpty() );
			getGuiComposite().getWidget( "jbSaveRelations" ).setEnabled( true );
		}
		catch ( Exception e ) {
		}
	}
	

	
	public void handleNewButton(ActionEvent e) {
		try {
			CreateDatastreamDialog loD;
			
			loD = (CreateDatastreamDialog) CServiceProvider.getService(DialogNames.CREATEDATASTREAM_DIALOG);
			loD.open();

			if (!loD.getID().isEmpty()) {
				File fp =  File.createTempFile( "temp", ".tmp");
				if (loD.getMimetype().equals("text/xml")) {
		            FileOutputStream fop = new FileOutputStream(fp);  
		            byte[] contentInBytes = "<content/>".getBytes("UTF-8");  
		            fop.write(contentInBytes);  
		            fop.flush();  
		            fop.close();  
				} 
				Repository.addDatastream(pid, loD.getID(), loD.getLabel(), loD.getMimetype().equals("text/xml") ? "X" : "M", loD.getMimetype(), fp );
				fp.delete();
		    	JTable ds = (JTable) getGuiComposite().getWidget(jtDatastreams);
		    	ds.setModel(Repository.listDatastreams(pid,false));
		    	ds.setRowSelectionInterval(0,0);		
			}

		} catch (Exception ex) {
			log.error(ex.getLocalizedMessage(),ex);	
		}
	}

	public void handleDelButton(ActionEvent e) {
		try {
	    	JTable tb = (JTable) getGuiComposite().getWidget("jtDatastreams");
  		  	int[] selected = tb.getSelectedRows();
  		  	
    		MessageFormat msgFmt = new MessageFormat(res.getString("delstream"));
    		Object[] args = {selected.length};
    		int liChoice = JOptionPane.showConfirmDialog(null, msgFmt.format(args) ,
	    		  						Common.WINDOW_HEADER, JOptionPane.YES_NO_OPTION,
	    		  						JOptionPane.QUESTION_MESSAGE);

    		if (liChoice == 0) {
      	    for (int i=selected.length-1; i>-1; i--) {
    	    	String dsid = (String) tb.getValueAt(selected[i], 0);

    	    	if (Common.SYSTEM_DATASTREAMS.contains("|"+dsid+"|")) {
    	    		msgFmt = new MessageFormat(res.getString("nonvaliddel"));
    	    		Object[] args0 = {dsid};
    	    		JOptionPane.showMessageDialog( null, msgFmt.format(args0), Common.WINDOW_HEADER, JOptionPane.INFORMATION_MESSAGE);
    	    	} else {		
				  			 
    	    		if (liChoice == 0) {
			    	  	Repository.purgeDatastream(pid, dsid);
			    	  	JTable ds = (JTable) getGuiComposite().getWidget(jtDatastreams);
			    	  	ds.setModel(Repository.listDatastreams(pid,false));
			    	  	ds.setRowSelectionInterval(0,0);
    	    		}
    	    	}
      	    }
			}		

		} catch (Exception ex) {
			log.error(ex.getLocalizedMessage(),ex);	
		}
	}

	
	public void handleoUploadButton(ActionEvent e) {
	      handleUpload(false);	  
	}

	public void handlemUploadButton(ActionEvent e) {
	       handleUpload(true);	  		  
	}
	
	public void handlemEditButton(ActionEvent e) {
		try {
		    getCoreDialog().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));				

		    JTable tb = (JTable) getGuiComposite().getWidget("jtMetadata");
	    	int rs = tb.getSelectedRow();

	    	String dsid = (String) tb.getValueAt(rs, 0);
	    	String mimetype = (String) tb.getValueAt(rs, 2);
	    	String group = (String) tb.getValueAt(rs, 3);
	    	String location = (String) tb.getValueAt(rs, 5);
	
			if (dsid.equals("DC")) {
				NewObjectDialog dlg;
				dlg = (NewObjectDialog) CServiceProvider.getService(DialogNames.NEWOBJECT_DIALOG);
				dlg.set((EditObjectDialog) null,pid,owner);
				dlg.open();
			}  else  if (isText(mimetype)) {
	        	TextEditor dlg = (TextEditor) CServiceProvider.getService(DialogNames.TEXTEDITOR);
	        	dlg.set(pid, dsid, mimetype, group, location, null, this.owner);
	        	dlg.open();
			}  else if (isImage(mimetype)) {
		        try {
		        	byte[] stream = null; 
					ByteArrayInputStream byteArrayInputStream = null;
					java.awt.image.BufferedImage image = null;
					if (group.equals("R")) {
						URL url = new URL(location);
						image = javax.imageio.ImageIO.read(url);
					} else {
						stream =  Repository.getDatastream(pid, dsid, "");
						byteArrayInputStream = new ByteArrayInputStream(stream);
						image = javax.imageio.ImageIO.read(byteArrayInputStream);
						byteArrayInputStream.close();
					}
	  			  	ij.ImagePlus ip = new ij.ImagePlus(dsid, image);
					ip.show("Statuszeile");
		        } catch (IOException q) {
		        	log.error(q.getLocalizedMessage(),q);	
		        }
		    } else {
				JOptionPane.showMessageDialog(  getCoreDialog(), res.getString("noedit"), Common.WINDOW_HEADER, JOptionPane.INFORMATION_MESSAGE );	        	
	        }
		} catch (Exception ex) {
			log.error(ex.getLocalizedMessage(),ex);	
		}
		finally {
		    getCoreDialog().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));				
		}
	}


	
	public void handleoEditButton(ActionEvent e) {
		try {
		    getCoreDialog().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));				

		    JTable tb = (JTable) getGuiComposite().getWidget("jtDatastreams");
	    	int rs = tb.getSelectedRow();

	    	String dsid = (String) tb.getValueAt(rs, 0);
	    	String title = (String) tb.getValueAt(rs, 1);
	    	String mimetype = (String) tb.getValueAt(rs, 2);
	    	String group = (String) tb.getValueAt(rs, 3);
	    	String location = (String) tb.getValueAt(rs, 5);
	    	
			if (dsid.equals("DC")) {
				EditDCDialog dlg;
				dlg = (EditDCDialog) CServiceProvider.getService(DialogNames.EDITDC_DIALOG);
				dlg.set(pid,owner);
				dlg.open();			
			}  else if  (isText(mimetype)) {
	        	TextEditor dlg = (TextEditor) CServiceProvider.getService(DialogNames.TEXTEDITOR);
	        	dlg.set(pid, dsid, mimetype, group, location, model, this.owner);
	        	dlg.open();
			}  else if (isImage(mimetype)) {
		        try {
		        	byte[] stream = null; 
					ByteArrayInputStream byteArrayInputStream = null;
					java.awt.image.BufferedImage image = null;
					if (group.equals("R")) {
						URL url = new URL(location);
						image = javax.imageio.ImageIO.read(url);
					} else {
						stream =  Repository.getDatastream(pid, dsid, "");
						byteArrayInputStream = new ByteArrayInputStream(stream);
						image = javax.imageio.ImageIO.read(byteArrayInputStream);
						byteArrayInputStream.close();
					}
	  			  	ij.ImagePlus ip = new ij.ImagePlus(dsid, image);
					ip.show("Statuszeile");
		        } catch (IOException q) {
		        	log.error(q.getLocalizedMessage(),q);	
		        }
	        } else {
				JOptionPane.showMessageDialog(  getCoreDialog(), res.getString("noedit"), Common.WINDOW_HEADER, JOptionPane.INFORMATION_MESSAGE );	        	
	        }
		} catch (Exception ex) {
			log.error(ex.getLocalizedMessage(),ex);	
		}
		finally {
			    getCoreDialog().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));				
		}
	}

	public void handlemMouseDoubleClick(MouseEvent e, int type) {

		try {

			CEventListener.setBlocked(true);
			// no events while handling

			if (type == MouseEvent.MOUSE_CLICKED) {
				if (e.getClickCount() >= 2) {
					handleUpload(true);	
				}
			}
		} catch (Exception ex) {
		} finally {
			CEventListener.setBlocked(false);
		}

	}	

	public void handleoMouseDoubleClick(MouseEvent e, int type) {

		try {

			CEventListener.setBlocked(true);
			// no events while handling

			if (type == MouseEvent.MOUSE_CLICKED) {
				if (e.getClickCount() >= 2) {
					handleUpload(false);	
				}
			}
		} catch (Exception ex) {
		} finally {
			CEventListener.setBlocked(false);
		}

	}	
	
	public void handleUpload(boolean mode) {
		JFileChooser chooser = null;
		Boolean ret;
	    try {
	    	ret = true; 
			getCoreDialog().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		    JTable tb = (JTable) getGuiComposite().getWidget(mode? "jtMetadata" : "jtDatastreams");
	    	int rs = tb.getSelectedRow();

	    	String dsid = (String) tb.getValueAt(rs, 0);
	    	String mimetype = (String) tb.getValueAt(rs, 2);
	    	String controlgroup = (String) tb.getValueAt(rs, 3);
	    	String location = (String) tb.getValueAt(rs, 5);
	    	
 		    CPropertyService props = (CPropertyService) CServiceProvider.getService(ServiceNames.PROPERTIES);
			
 		    if ( controlgroup.contains("X") || controlgroup.contains("M") || mimetype.toLowerCase().equals("image/tiff") ) {
				chooser = new JFileChooser(props.getProperty("user", "import.path"));
				chooser.setDialogTitle(res.getString("choosefile"));
			
				if (mimetype.toLowerCase().contains("xml")) chooser.addChoosableFileFilter(new FileFilter(new String[]{".xml"}));
				if (dsid.toLowerCase().contains("ontology")) chooser.addChoosableFileFilter(new FileFilter(new String[]{".rdf",".ttl"}));
				if (mimetype.toLowerCase().contains("jpeg") || mimetype.toLowerCase().contains("tiff")) {
			   		IIIFFactory i3f = (IIIFFactory) CServiceProvider.getService(ServiceNames.I3F_SERVICE);
					i3f.delete(pid,dsid);
					ImagePreviewPanel preview = new ImagePreviewPanel();
					chooser.setAccessory(preview);
					chooser.addPropertyChangeListener(preview);
					chooser.addChoosableFileFilter(new FileFilter(mimetype.toLowerCase().contains("tiff") ?new String[]{".tif"}:new String[]{".jpg"}));
				}

				if (mimetype.toLowerCase().contains("plain")) chooser.addChoosableFileFilter(new FileFilter(new String[]{".txt"}));
				if (mimetype.toLowerCase().contains("pdf")) chooser.addChoosableFileFilter(new FileFilter(new String[]{".pdf"}));

				if (chooser.showDialog(getCoreDialog(), res.getString("choose")) != JFileChooser.APPROVE_OPTION) {
					return;
				} 
				props.setProperty("user", "import.path", chooser.getCurrentDirectory().getAbsolutePath());
				props.saveProperties("user");
			    getCoreDialog().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			    
			    if (dsid.equals("THUMBNAIL")) {
			    	File thumbnail = File.createTempFile( "temp", ".tmp" );			    
			    	ImageTools.createThumbnail( chooser.getSelectedFile(), thumbnail, 100, 80, Color.lightGray );
			    	Repository.modifyDatastream(pid, dsid, mimetype, controlgroup, thumbnail);
			    	thumbnail.delete();
 		    	} else {
 		    		if (mimetype.toLowerCase().contains("pdf")) {
/* 		    			try {
 		    				 byte [] passwd = null;
 		    				 PdfReader reader = new PdfReader(chooser.getSelectedFile().getAbsolutePath(), passwd);
 		    			} catch(Exception ex) {
 	    					 JOptionPane.showMessageDialog(getCoreDialog(), "Hinzufügen von Datei "+chooser.getSelectedFile()+" ist nicht möglich. Validierung des Dateinhaltes ist fehlgeschlagen.\nDie Datei enthält kein Dokument in einem gültigen PDF-Format. ");
 		    				 return;
 		    			} */
 		    		}
 		    		if (Repository.exists(pid, "THUMBNAIL") && (mimetype.toLowerCase().equals("image/jpeg") || mimetype.toLowerCase().equals("image/tiff"))) {
						File thumb = File.createTempFile( "temp", ".tmp" );
						ImageTools.createThumbnail( chooser.getSelectedFile(), thumb, 100, 80, Color.lightGray );
				    	Repository.modifyDatastream(pid, "THUMBNAIL", "image/jpeg", "M", thumb);
				    	thumb.delete();
 		    		} 	
 		    		String path = chooser.getSelectedFile().getAbsolutePath();
 		    		File selected = new File(path);

 		    		
 		    		if (dsid.equals("TEI_SOURCE")) {
						TEI t = new TEI(null,false,true);
						t.setUser(this.owner);
						t.set(chooser.getSelectedFile().getAbsolutePath(), false);
						if (t.isValid()) {
						    t.setPID(pid);
						    t.validate(null, null);
							Repository.modifyDatastreamByValue(pid, "TEI_SOURCE", "text/xml", t.toString());
						    refresh(true);
						} else { ret = false; }
 		    		} else if (dsid.equals("MEI_SOURCE")) {
		    			MEI m = new MEI(null,false,true);
						m.setUser(this.owner);
						m.set(chooser.getSelectedFile().getAbsolutePath(), false);
						if (m.isValid()) {
						    m.setPID(pid);
						    m.validate(null, null);
							Repository.modifyDatastreamByValue(pid, "MEI_SOURCE", "text/xml", m.toString());
						    refresh(true);
						} else { ret = false; }
 		    		} else if (dsid.equals("LIDO_SOURCE")) {
		    				LIDO l = new LIDO(null,false,true);
						l.setUser(this.owner);
						l.set(chooser.getSelectedFile().getAbsolutePath(), false);
						if (l.isValid()) {
						    l.setPID(pid);
						    l.validate(null, null);
							Repository.modifyDatastreamByValue(pid, "LIDO_SOURCE", "text/xml", l.toString());
						    refresh(true);
						} else { ret = false; }
 		    		} else if (dsid.equals("STORY")) {
		    				STORY s = new STORY(null,false,true);
		    				s.setUser(this.owner);
		    				s.set(chooser.getSelectedFile().getAbsolutePath(), false);
		    				if (s.isValid()) {
		    					s.setPID(pid);
		    					s.validate(null, null);
		    					Repository.modifyDatastreamByValue(pid, "STORY", "text/xml", s.toString());
		    					refresh(true);
		    				} else { ret = false; }
 				    } else if (dsid.equals("METS_SOURCE")) {
						METS m = new METS(null,false,true);
						m.setUser(this.owner);
						m.set(selected.getAbsolutePath(), false);
						if (m.isValid()) {
						    m.setPID(pid);
						    m.ingestImages();
						    m.write();
						    m.createMapping(null, null);
							Repository.modifyDatastreamByValue(pid, "METS_SOURCE", "text/xml", m.toString());
						    refresh(true);
						} else { ret = false; }
						
 				    } else if (dsid.equals("BIBTEX") && !selected.getAbsolutePath().contains(".xml")) { 				    	
 				    	File bibtex = File.createTempFile( "temp", ".tmp" );			    
						net.sourceforge.bibtexml.BibTeXConverter bc = new net.sourceforge.bibtexml.BibTeXConverter();
						bc.bibTexToXml( selected, bibtex );
 				    	Repository.modifyDatastream(pid, dsid,"text/xml", controlgroup, bibtex);
 				    	bibtex.delete();

 				    } else {
 				    	 				    	
 				    	if (dsid.equals("ONTOLOGY")) {
                          
 				    		File temp = selected;

 				    		String pr = props.getProperty("user", "SKOS.IFY"); 
                            if (model.contains("SKOS") && pr != null && pr.equals("1") ) {
                            	SkosifyFactory skosify = (SkosifyFactory) CServiceProvider.getService(ServiceNames.SKOSIFY_SERVICE);
				    			temp = skosify.skosify(temp);
				    			if (temp == null) {
	     				    		temp = selected;
				    			}	
                            } else {
                            	if (selected.getCanonicalPath().contains(".ttl")) {
                            	  try {	
                            		temp = File.createTempFile("temp", "tmp"); 
                    			    InputStream is = new java.io.FileInputStream(selected.getCanonicalPath());
                    				FileOutputStream os = new FileOutputStream(temp);
                    			    RDFParser parser = Rio.createParser(org.openrdf.rio.RDFFormat.TURTLE);
                    			    RDFWriter writer = Rio.createWriter(org.openrdf.rio.RDFFormat.RDFXML, os);
                    			    parser.setRDFHandler(writer);
                    			    parser.parse(is,  "http://gams.uni-graz.at");
                    			    os.close();
                    			    is.close();
                            	  } catch (Exception tz) {}  
                    			    
                            	}
                            }

                            Repository.modifyDatastream(pid, dsid, mimetype, controlgroup, temp);

 				    		try {
								TripleStoreFactory tf = new TripleStoreFactory();
								if (tf.getStatus()) {
									tf.update(temp, pid);
								}	
								tf.close();	
								
 				    		} catch (Exception e) {
 								JOptionPane.showMessageDialog(  getCoreDialog(), e.getMessage(), Common.WINDOW_HEADER, JOptionPane.INFORMATION_MESSAGE); 				    			
 				    		}
 				    		
                            if (temp.getAbsolutePath().contains("tmp")) temp.delete();
 				    	} else {
 	 				    	Repository.modifyDatastream(pid, dsid, mimetype, controlgroup, selected);
 				    	}
 				    	
 				    	
 				    }	

 		    		if (ret) 
 		    		{
 		    			MessageFormat msgFmt = new MessageFormat(res.getString("update"));
 		    			Object[] args = {dsid, pid,chooser.getSelectedFile()}; 		    		
 		    			JOptionPane.showMessageDialog(  getCoreDialog(), msgFmt.format(args), Common.WINDOW_HEADER, JOptionPane.INFORMATION_MESSAGE);
 		    		} else {
 		    			MessageFormat msgFmt = new MessageFormat(res.getString("parsererror"));
		    			Object[] args = {chooser.getSelectedFile()}; 		    		
		    			JOptionPane.showMessageDialog(  getCoreDialog(), msgFmt.format(args), Common.WINDOW_HEADER, JOptionPane.INFORMATION_MESSAGE);
 		    		}	
		    	}
				if ( mode) {
					JTable md = (JTable) getGuiComposite().getWidget(jtMetadata);
					md.setModel(Repository.listDatastreams(pid,true));
					md.setRowSelectionInterval(rs,rs);			
					md. setShowHorizontalLines(false);
				} else {
					JTable ds = (JTable) getGuiComposite().getWidget(jtDatastreams);
					ds.setModel(Repository.listDatastreams(pid,false));
					ds.setRowSelectionInterval(rs,rs);		
					ds. setShowHorizontalLines(false);
				}
			}
 		    if ( controlgroup.contains("R") && !mimetype.toLowerCase().contains("tiff") ) {
 		    	if (dsid.contains("STYLESHEET")) {
 		    		SelectLayoutDialog dlg = (SelectLayoutDialog) CServiceProvider.getService(DialogNames.SELECTLAYOUT_DIALOG);
 		    		dlg.set(pid, dsid, model, owner);
 		    		dlg.open();
 					if (!dlg.getStylesheet().isEmpty()) tb.setValueAt(dlg.getStylesheet(), rs, 5 );
 		    	} else {
 		    		LocationDialog dlg = (LocationDialog) CServiceProvider.getService(DialogNames.LOCATION_DIALOG);
 		    		dlg.set(pid, dsid, location);
 					dlg.open();
 					if (!dlg.get().isEmpty()) tb.setValueAt(dlg.get(), rs, 5 );
 		    	}	
 		    }
	    	
	    } catch (Exception e) {
			 
				MessageFormat msgFmt = new MessageFormat(res.getString("errimport"));
				Object[] args = {chooser.getSelectedFile()}; 		    		
				JOptionPane.showMessageDialog(  getCoreDialog(), msgFmt.format(args), Common.WINDOW_HEADER, JOptionPane.INFORMATION_MESSAGE);
					
	    }	
		finally {
			getCoreDialog().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}

	}
	
	class FileFilter extends javax.swing.filechooser.FileFilter {
		  private String[] filter;
		  public FileFilter(String[] f) {
			  this.filter = f;
		  }
		  public boolean accept(File file) {
			    if (file.isDirectory()) {
			    	return true;
			        }
		    String filename = file.getName();
		    boolean found=false;
		    for (int i = 0; i<this.filter.length;i++) {
		    	if (filename.endsWith(this.filter[i])) {
		    		found=true;
		    		break;
		    	};
		    }
		    return found;
		  }
		  public String getDescription() {
			    String s="";
			    for (int i = 0; i<this.filter.length;i++) {
			    	s="*"+this.filter[i]+";";
			    };
			    return s;
		  }
	}      
	
	public void setMetadata() {
		
	}

	public void setDatastreams() {
		
	}
	
	public void handleEditButton(ActionEvent e) {
		  try {
		  
		    } catch (Exception ex) {}					  
	}
	
	
	private void handleDownload( String pid, String dsid, String mimetype, String group) {
		  try {		
			  getCoreDialog().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

			  int i = mimetype.indexOf('/');
			  String ext = mimetype.substring(i+1); 
			  ext = ext.equals("plain") ? "txt"  : ext;
			  String fn = (pid +"_"+dsid+"."+ext).replaceAll(":", "_").toLowerCase();
			  CPropertyService props = (CPropertyService) CServiceProvider.getService(ServiceNames.PROPERTIES);
			  JFileChooser fc = new JFileChooser(props.getProperty("user", "import.path"));
			  fc.setSelectedFile(new File(fn));

			  fc.setDialogTitle(res.getString("saveds"));  
		      byte[] stream = null; 
		      int sel = fc.showSaveDialog(null);
		      if (sel == fc.APPROVE_OPTION) {
		        		File fp = fc.getSelectedFile();
		        		try {
		        			stream =  Repository.getDatastream(pid, dsid, "");
							if (group.equals("R")) {
								URL url = new URL(new String(stream));
								InputStream is = new URL(new String(stream)).openStream();
								ByteArrayOutputStream baos = new ByteArrayOutputStream();
							    int reads = is.read();					       
							    while(reads != -1){
							        baos.write(reads);
							        reads = is.read();
							     }						      
							    stream = baos.toByteArray();
							}	
		        			FileOutputStream fos = new FileOutputStream(fp);
		        			fos.write(stream);
		        			fos.close();
		        			MessageFormat msgFmt = new MessageFormat(res.getString("saveok"));
		    	    		Object[] args0 = {dsid, pid, fp.getAbsolutePath()};		        			
		        			JOptionPane.showMessageDialog(null,msgFmt.format(args0));
		        			props.setProperty("user", "import.path", fc.getCurrentDirectory().getAbsolutePath());
		        			props.saveProperties("user");
		        		} catch (Exception e) {	
		        			JOptionPane.showMessageDialog(null, res.getString("errsave")+": "+fp.getAbsolutePath());
		        		}	
		       }
		    } catch (Exception ex) {}
		    finally {
				  getCoreDialog().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));		    	
		    }
	}
	
	public void handleoDownloadButton(ActionEvent e) {
		  try {
			  JTable loDS = (JTable) getGuiComposite().getWidget(jtDatastreams);
			  int sel = loDS.getSelectedRow();
			  handleDownload (pid, (String) loDS.getValueAt(sel, 0), (String) loDS.getValueAt(sel, 2), (String) loDS.getValueAt(sel, 3)); 
		    } catch (Exception ex) {}					  
	}
	public void handlemDownloadButton(ActionEvent e) {
		  try {
		      JTable loMD = (JTable) getGuiComposite().getWidget(jtMetadata);	
			  int sel = loMD.getSelectedRow();
			  handleDownload (pid, (String) loMD.getValueAt(sel, 0), (String) loMD.getValueAt(sel, 2), (String) loMD.getValueAt(sel, 3)); 
		    } catch (Exception ex) {}					  
	}
	
	public void handlerRemoved(CEventListener aoHandler) {
	}

	public void handleSeekButton(ActionEvent e) {
		  try {
			    seekString =  (String) ((JTextField) getGuiComposite().getWidget("jtfSeek")).getText().trim();
			    props.setProperty("user", "relations.seekterm", seekString);
				props.saveProperties("user");

			   refresh(true);
		    } catch (Exception ex) {}					  
	}

	public void refresh(boolean mode) {
		  try {
			   getCoreDialog().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

			    JList ltRels = (JList) getGuiComposite().getWidget( "jtRelations" );
				DefaultListModel lma = (DefaultListModel) ltRels.getModel();
				lma.removeAllElements();
				JList ltNonRels = (JList) getGuiComposite().getWidget( "jtNonRelations" );
				DefaultListModel lmb = (DefaultListModel) ltNonRels.getModel();
				lmb.removeAllElements();
				java.util.List  <org.emile.cirilo.ecm.repository.FedoraConnector.Relation>relations = Repository.getRelations(pid,Common.isMemberOf);	
				coll = new HashMap();
		        for (Relation r : relations) {
		        	   String s=r.getTo().substring(12); 
		        	   coll.put(s, s);
		        }
		        java.util.List<String> containers = Repository.getContainers(user.getUser(), groups.contains( "administrator") );
             			        
		        SortedSet<String> sortedset= new TreeSet<String>(containers);
		        
				HashMap<String,String> hm = new HashMap();
		        SortedSet<String> hma = new TreeSet<String>();
			
				for (String s: sortedset) {
					  Split id = new Split(s);
	                  hm.put(id.get(), s);
	                  hma.add(id.get());
				}
				
				for (  String s: hma) {
					 if (coll.get(s) != null) lma.addElement(hm.get(s));
					 else if (mode) {
						 if (hm.get(s).contains(seekString.replace("*", ""))) {
							 lmb.addElement(hm.get(s));
						 }						 
					 } else {
						 lmb.addElement(hm.get(s));
					 }	 
				}
	
			    JTable loMD = (JTable) getGuiComposite().getWidget(jtMetadata);
				loMD.setModel(Repository.listDatastreams(pid,true));
				loMD.setRowSelectionInterval(0,0);			

			    JTable loDS = (JTable) getGuiComposite().getWidget(jtDatastreams);
		    	loDS.setModel(Repository.listDatastreams(pid,false));
		    	loDS.setRowSelectionInterval(0,0);		

				((JTextField) getGuiComposite().getWidget("jtfIdentifier")).setText(pid);
				((JTextField) getGuiComposite().getWidget("jtfIdentifier")).setEnabled(false);
				JComboBox jcbUser = ((JComboBox) getGuiComposite().getWidget("jcbUser"));
		        jcbUser.setSelectedItem(owner);

				ltRels.setSelectedIndex( 0 );
	            ltNonRels.setSelectedIndex( 0 );
	            
				new CMouseListener(ltRels, this, "handleRelsMouseDoubleClick");
				new CMouseListener(ltNonRels, this, "handleNonRelsMouseDoubleClick");
		  
		    } catch (Exception ex) {}		
			finally {
				getCoreDialog().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}

	}

	
	
	
	/**
	 *  Description of the Method
	 */
	protected void cleaningUp() {
	}

	public void show()
	 throws CShowFailedException {
		try {
			
			seekString = props.getProperty("user", "relations.seekterm");
			moGA.setData("jtfSeek", seekString);
		    refresh(true);			

		    JTable loMD = (JTable) getGuiComposite().getWidget(jtMetadata);	
		    JTable loDS = (JTable) getGuiComposite().getWidget(jtDatastreams);
		    JTable[] loTable = {loMD, loDS}; 
		    se = (Session) CServiceProvider.getService( ServiceNames.SESSIONCLASS );						
			org.emile.cirilo.dialog.CBoundSerializer.load(this.getCoreDialog(), se.getObjectDialogProperties(), loTable);
			
			this.setTitle(res.getString("editobjsing")+" - "+pid);
			
		} catch (Exception e) {		
		}
	}
	/**
	 *  Description of the Method
	 *
	 * @exception  COpenFailedException  Description of the Exception
	 */
	protected void opened() throws COpenFailedException {

		try {

			res =(ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);

			moGA = (CDefaultGuiAdapter)getGuiAdapter();						
			CDialogTools.createButtonListener(this, "jbClose", "handleCancelButton");
			CDialogTools.createButtonListener(this, "jbSave", "handleSaveButton");
			CDialogTools.createButtonListener(this, "jbSaveRelations", "handleSaveRelationsButton");
			CDialogTools.createButtonListener(this, "jbSeek", "handleSeekButton");
			CDialogTools.createButtonListener(this, "jboUpload", "handleoUploadButton");
			CDialogTools.createButtonListener(this, "jbmUpload", "handlemUploadButton");
			CDialogTools.createButtonListener(this, "jboDownload", "handleoDownloadButton");
			CDialogTools.createButtonListener(this, "jbmDownload", "handlemDownloadButton");
			CDialogTools.createButtonListener(this, "jboEdit", "handleoEditButton");
			CDialogTools.createButtonListener(this, "jbmEdit", "handlemEditButton");
			CDialogTools.createButtonListener(this, "jbAddRelation", "handleAddRelationButton");
			CDialogTools.createButtonListener(this, "jbRemoveRelation", "handleRemoveRelationButton");

			CDialogTools.createButtonListener(this, "jbNew", "handleNewButton");
			CDialogTools.createButtonListener(this, "jbDel", "handleDelButton");
			
			
			String[] op = Repository.getObjectProfile(pid);
			((JTextField) getGuiComposite().getWidget("jtfIdentifier")).setText(pid);
			((JTextField) getGuiComposite().getWidget("jtfIdentifier")).setEnabled(false);
//			((JComboBox) getGuiComposite().getWidget("jcbState")).setSelectedIndex(op[2].contains("Inactive")? 1: 0);
			
			popupMetadata = new JPopupMenu();
 		    JMenuItem mi;
			mi = new JMenuItem(res.getString("import"));
		    mi.addActionListener(new ActionListener() {
		    	  public void actionPerformed(ActionEvent e) {
		    		  handlemUploadButton(null);
		    	  }
		      });	      
		    popupMetadata.add(mi);
		    popupMetadata.add(new JSeparator());
			mi = new JMenuItem(res.getString("edit"));
		    mi.addActionListener(new ActionListener() {
		    	  public void actionPerformed(ActionEvent e) {
		    		  handlemEditButton(null);
		    	  }
		      });	      
		    popupMetadata.add(mi);

		    popupDatastreams = new JPopupMenu();
			mi = new JMenuItem(res.getString("import"));
		    mi.addActionListener(new ActionListener() {
		    	  public void actionPerformed(ActionEvent e) {
		    		  handleoUploadButton(null);
		    	  }
		      });	      
		    popupDatastreams.add(mi);			
		    popupDatastreams.add(new JSeparator());
			mi = new JMenuItem(res.getString("edit"));
		    mi.addActionListener(new ActionListener() {
		    	  public void actionPerformed(ActionEvent e) {
		    		  handleoEditButton(null);
		    	  }
		      });	      
		    popupDatastreams.add(mi);
			
			user = (User) CServiceProvider.getService(ServiceNames.CURRENT_USER);
			groups = (ArrayList) CServiceProvider.getService( ServiceNames.MEMBER_LIST );

			JTable md = (JTable) getGuiComposite().getWidget(jtMetadata);
			md.getSelectionModel().addListSelectionListener(new MySelectionListener(md,(JButton) getGuiComposite().getWidget("jbmEdit")));			
			
/*
 			statusChanged = false;
			if ( op[2].contains("Inactive") ) {
				statusChanged = true;
				Repository.modifyObject(pid, "A", null, null);
			}
*/
			
			md.setModel(Repository.listDatastreams(pid,true));
			md.setRowSelectionInterval(0,0);			
			md. setShowHorizontalLines(false);
			new CMouseListener(md, this, "handlemMouseDoubleClick");			
		    MouseListener popupListener = new PopupmListener();
		    md.addMouseListener(popupListener);

		    	JTable ds = (JTable) getGuiComposite().getWidget(jtDatastreams);
		    	ds.getSelectionModel().addListSelectionListener(new MySelectionListener(ds,(JButton) getGuiComposite().getWidget("jboEdit")));			

		    	ds.setModel(Repository.listDatastreams(pid,false));
		    	ds.setRowSelectionInterval(0,0);		
		    	ds. setShowHorizontalLines(false);
		    	new CMouseListener(ds, this, "handleoMouseDoubleClick");
		    	popupListener = new PopupoListener();
		    	ds.addMouseListener(popupListener);
		    
			JComboBox jcbUser = ((JComboBox) getGuiComposite().getWidget("jcbUser"));
	        java.util.List<String> users = Repository.getUsers();
	        for (String s : users) {
	        	    if (!s.isEmpty()) jcbUser.addItem(s);
	        	    
	         }
	        
	        jcbUser.setSelectedItem(owner);
			((JComboBox) getGuiComposite().getWidget("jcbUser")).setSelectedItem(op[0].replace("\"",""));
			((JComboBox) getGuiComposite().getWidget("jcbUser")).setEnabled(groups.contains("administrator"));
			getGuiComposite().getWidget( "jbSaveRelations" ).setEnabled( false );

			props = (CPropertyService) CServiceProvider.getService(ServiceNames.PROPERTIES);

			JTextField tf = (JTextField) getGuiComposite().getWidget("jtfSeek");
			tf.addKeyListener(
				new KeyAdapter() {
					public void keyPressed(KeyEvent ev) {
						if (ev.getKeyCode() == KeyEvent.VK_ENTER) {
							ev.consume();
							handleSeekButton(null);
						}
					}
				});

			JTabbedPane tpPane = ((JTabbedPane) getGuiComposite().getWidget("tpPane"));
			tpPane.setSelectedIndex(2);
			
		} catch (Exception ex) {
			log.error(ex.getLocalizedMessage(),ex);	
			throw new COpenFailedException(ex);
		}
	}

	
    private boolean isText(String mimetype) {
    	if (mimetype.contains("xml") || Common.TEXT_MIMETYPES.contains(mimetype)) return true;
    	return false;
    }

    private boolean isImage(String mimetype) {
    	if (mimetype.contains("jpeg") || mimetype.contains("gif") | mimetype.contains("tif") | mimetype.contains("png")) return true;
    	return false;
    }

    private boolean isRelation(String group) {
    	if (group.contains("R")) return true;
    	return false;
    }
	
	 class MySelectionListener implements ListSelectionListener{

		JTable table;
		JButton button;
		
		public MySelectionListener(JTable table, JButton button) {
			this.table = table;
			this.button = button;
		}
		@Override
		public void valueChanged(ListSelectionEvent e) {
			 int rs = this.table.getSelectedRow() > -1 ? this.table.getSelectedRow() : 0;
			 String mimetype = (String) this.table.getValueAt(rs, 2);
			 String group = (String) this.table.getValueAt(rs, 3);
			 String id = (String) this.table.getValueAt(rs, 0);
			 button.setText(isImage(mimetype) || isRelation(group) ? res.getString("show") : res.getString("edit"));
		}

	}	
	 
	  class PopupmListener extends MouseAdapter {
		    public void mousePressed(MouseEvent e) {
		      showPopup(e);
		    }
		    public void mouseReleased(MouseEvent e) {
		      showPopup(e);
		    }
		    private void showPopup(MouseEvent e) {
		      if (e.isPopupTrigger()) {
		    	  popupMetadata.show(e.getComponent(), e.getX(), e.getY());
		      }
		    }
		  }
	  
	  class PopupoListener extends MouseAdapter {
		    public void mousePressed(MouseEvent e) {
		      showPopup(e);
		    }
		    public void mouseReleased(MouseEvent e) {
		      showPopup(e);
		    }
		    private void showPopup(MouseEvent e) {
		      if (e.isPopupTrigger()) {
		        popupDatastreams.show(e.getComponent(), e.getX(), e.getY());
		      }
		    }
		  }
	
	private CDefaultGuiAdapter moGA;
	private User user;
	private ArrayList<String> groups;
	private Session se;
	private String pid;
	private String  model;
	private String  label;
	private String owner;
	private HashMap<String,String> coll;
	private JPopupMenu popupMetadata;
	private JPopupMenu popupDatastreams;
	private ResourceBundle res;
	private CPropertyService props;
	private String seekString;
	private String[] op;
	private final String jtMetadata ="jtMetadata";
	private final String jtDatastreams ="jtDatastreams";
	private Boolean statusChanged;


}

