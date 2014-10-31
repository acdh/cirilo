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


import java.io.*;

/**
 *  Description of the Class
 *
 * @author     hstigler
 * @created    14. Juni 2006
 */
public class Handles implements java.io.Serializable {
   
    private byte[] HandleKey = new byte[256];
       
    public byte[] getHandleKey() { return HandleKey; }
    public void setHandleKey(byte[] key) { HandleKey = key; }

}

