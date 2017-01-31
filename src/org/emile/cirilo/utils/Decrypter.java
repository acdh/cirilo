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

package org.emile.cirilo.utils;

import javax.crypto.*;
import javax.crypto.spec.*;
import javax.xml.bind.*;

import org.apache.log4j.Logger;


public class Decrypter {
	
  private static Logger log = Logger.getLogger(Decrypter.class);

	
  final private transient String fgs = "5Hb6t90x17Fe85-12VAlq55.MaUT1956";
  final private byte [] salt = { (byte) 0xc9, (byte) 0xc9,(byte) 0xc9,(byte) 0xc9,(byte) 0xc9,(byte) 0xc9,(byte) 0xc9,(byte) 0xc9};

  private Cipher decryptCipher;

//  private sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();

  private String charset = "UTF16";


  public Decrypter()
  {
	  String hc = (new Integer(fgs.hashCode())).toString();
	  init((hc+fgs+hc).toCharArray(), salt);
  }

  private void init (char[] pass, byte[] salt) throws SecurityException
  {  
    try {
      final PBEParameterSpec ps = new PBEParameterSpec(salt, 20);
      final SecretKeyFactory kf = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
      final SecretKey k = kf.generateSecret(new PBEKeySpec(pass));
      decryptCipher = Cipher.getInstance("PBEWithMD5AndDES/CBC/PKCS5Padding");
      decryptCipher.init (Cipher.DECRYPT_MODE, k, ps);
    }
    catch (Exception e) {
		log.error(e.getLocalizedMessage(),e);	    	
    }
  }

  public String decrypt(String str) throws SecurityException
  {
	String s = "";
    try {
      byte[] dec = DatatypeConverter.parseBase64Binary(str);
//      byte[] dec = decoder.decodeBuffer(str);
      byte[] b = decryptCipher.doFinal(dec);
      s = new String(b, this.charset);
    }
    catch (Exception e) {
		log.error(e.getLocalizedMessage(),e);	   	
    }
    finally {
    }
	return s;
   
  }

}