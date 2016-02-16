package org.emile.cirilo.business;

/*
 * -----------------------------------------------------------------------------
 *
 * <p><b>License and Copyright: </b>The contents of this file are subject to the
 * Educational Community License (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License
 * at <a href="http://www.opensource.org/licenses/ecl1.txt">
 * http://www.opensource.org/licenses/ecl1.txt.</a></p>
 *
 * <p>Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.</p>
 *
 * <p>The entire file consists of original code.  Copyright &copy; 2005-2008 by 
 * Department of Information Processing in the Humanities, University of Graz.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 */


import org.emile.cirilo.*;

import voodoosoft.jroots.core.CServiceProvider;

import java.io.*;

import com.jcraft.jsch.*;

import org.apache.log4j.Logger;
import org.emile.cirilo.ServiceNames;



/**
 *  Description of the Class
 *
 * @author     yoda
 * @created    9. Mai 2006
 */
public class Scp {

	   private static Logger log = Logger.getLogger(Scp.class);
	   
	   private String host;
	   private String passwd;
	   private User user;
	   private com.jcraft.jsch.Session session;
	   
	   public Scp () {}
	   
	   public boolean connect() {

		   JSch jsch=new JSch();

		   try {

			   user    = (User) CServiceProvider.getService(ServiceNames.CURRENT_USER);
			   
			   
/*
			   LDAPLoginDialog loLoginIIPS;
 
			   loLoginIIPS = (LDAPLoginDialog) CServiceProvider.getService(DialogNames.LOGINIIPS_DIALOG);

			   while (true) {
				   
                        try {			   
                        	session = jsch.getSession(user.getIIPSUser(), user.getIIPSUrl(), 22);
                        	session.setPassword(user.getIIPSPasswd());						
			   
                        	java.util.Properties config=new java.util.Properties();
                        	config.put("StrictHostKeyChecking", "no");
                        	session.setConfig(config);		
                        	session.connect();
			   
                        	return true;
                        } catch (Exception q) {}
                        
    				    loLoginIIPS.open();
                      if (loLoginIIPS.isCanceled()) break;

			   }	   
			  */ 
                        
		   } catch (Exception e){
	      	   log.error(e.getLocalizedMessage(),e);				      					   
			   return false;
		   }
		   return false;

		   
	   }
	
	   public void disconnect() {
		   try {
			   session.disconnect();
		   } catch (Exception e) {
	      		log.error(e.getLocalizedMessage(),e);				      					   			   
		   }	   
			   
	   }

		public boolean rm (String pid)
		{
			Channel channel      = null;
		    String dir           = null;
			
			try {
				
				dir    = new Integer(pid.hashCode()).toString().replace("-","");
				pid    = (pid.replace(":","_")+"_*.tif").toLowerCase();
				dir    = dir.substring(0,4)+"/"+dir.substring(4,7)+"/"+dir.substring(7)+"/";
				
		
				channel = session.openChannel("sftp");
				channel.connect();
				
				ChannelSftp sftp = (ChannelSftp)channel;	
				sftp.cd("imageStore");
				String[] folders = dir.split( "/" );
				for ( String folder : folders ) {
				    if ( folder.length() > 0 ) {
				        try {
				            sftp.cd( folder );
				        }
				        catch ( SftpException e ) {
				            sftp.mkdir( folder );
				            sftp.cd( folder );
				        }
				    }
				}
				
				sftp.rm(pid);
				
			} catch (Exception e) {
				log.error(e.getLocalizedMessage(),e);	
				return false;
			}
			finally {
				try {
					channel.disconnect();  
				} catch(Exception q) {
		      		log.error(q.getLocalizedMessage(),q);				      					   
				}
			}		
			return true;
			
		}
		
		public String put(File file, String pid, String dsn) 
		{
			Channel channel      = null;
		    String dir           = null;
			
			try {
				
				dir    = new Integer(pid.hashCode()).toString().replace("-","");
				pid    = (pid.replace(":","_")+"_"+dsn+".tif").toLowerCase();
				dir    = dir.substring(0,4)+"/"+dir.substring(4,7)+"/"+dir.substring(7)+"/";
				
				String filename   = file.getAbsolutePath();
				
				channel = session.openChannel("sftp");
				channel.connect();
				
				ChannelSftp sftp = (ChannelSftp)channel;	
				sftp.cd("imageStore");
				String[] folders = dir.split( "/" );
				for ( String folder : folders ) {
				    if ( folder.length() > 0 ) {
				        try {
				            sftp.cd( folder );
				        }
				        catch ( SftpException e ) {
				            sftp.mkdir( folder );
				            sftp.cd( folder );
				        }
				    }
				}
				
				sftp.put(filename, pid);
				
			} catch (Exception e) {
				log.error(e.getLocalizedMessage(),e);	
			}
			finally {
				try {
					channel.disconnect();  
				} catch(Exception q) {
		      		log.error(q.getLocalizedMessage(),q);				      					   		
				}
			}		
			return dir+pid;
			
	}

}

