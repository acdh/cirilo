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

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;

import java.security.KeyManagementException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;

 
public class CiriloSocketFactory extends SocketFactory {
	private static Logger log = Logger.getLogger(CiriloSocketFactory.class);

	private static CiriloSocketFactory factory;
    private SSLSocketFactory sf=null;
 
    private CiriloSocketFactory() {
            createFactory();
    }
 
    public static synchronized SocketFactory getDefault() {
        if(factory == null){
            factory = new CiriloSocketFactory();
        }
        return factory;
    }
 
    public void createFactory() {
        try {
            TrustManager[] tm = new TrustManager[] {new CiriloX509TrustManager()};
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, tm,  new java.security.SecureRandom());
            sf = sc.getSocketFactory();
        }
        catch (KeyManagementException e) {log.error(e.getLocalizedMessage(),e);	}
        catch (NoSuchAlgorithmException e) {log.error(e.getLocalizedMessage(),e);	}
    }
 
    public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
        return sf.createSocket(host,port);
    }
 
    public Socket createSocket(String host, int port, InetAddress localHost, int localPort)
            throws IOException, UnknownHostException {
        return sf.createSocket(host, port, localHost, localPort);
    }
 
    public Socket createSocket(InetAddress host, int port) throws IOException {
        return sf.createSocket(host,port);
    }
 
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort)
            throws IOException {
        return sf.createSocket(address, port, localAddress, localPort);
    }
}
