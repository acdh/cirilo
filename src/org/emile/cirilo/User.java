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

/**
 *  Description of the Class
 *
 *@author     Johannes Stigler
 *@created    13. Februar 2005
 */
public class User {

	private static String FedoraUser;
	private static String FedoraPasswd;
	private static String FedoraUrl;
	private static String ExistUser ="admin";
	private static String ExistPasswd ="kefalos";
	private static String ExistUrl = "xmldb:exist://localhost:8080/exist/xmlrpc";
    private static String ExistHome ="";
	private static String IIPSUser = null;
	private static String IIPSUrl = "glossa.uni-graz.at";
	private static String IIPSPasswd = null;

	

	/**
	 *  Constructor for the User object
	 *
	 *@param  u  Description of the Parameter
	 *@param  p  Description of the Parameter
	 *@param  r  Description of the Parameter
	 */
	public User(String user, String passwd, String url ) {
		FedoraUser = user;
		FedoraPasswd = passwd;
		FedoraUrl = url;
	}


	/**
	 *  Gets the name attribute of the User object
	 *
	 *@return    The name value
	 */
	public String getUser() {
		return FedoraUser.trim();
	}

	/**
	 *  Gets the passwd attribute of the User object
	 *
	 *@return    The passwd value
	 */
	public String getPasswd() {
		return FedoraPasswd.trim();
	}

	/**
	 *  Gets the repository attribute of the User object
	 *
	 *@return    The name value
	 */
	public String getUrl() {
		return FedoraUrl.trim();
	}
	
	public String getExistUrl() {
		return ExistUrl.trim();
	}
	public String getExistHome() {
		return ExistHome.trim();
	}
	public String getExistUser() {
		return ExistUser.trim();
	}
	public String getExistPasswd() {
		return ExistPasswd.trim();
	}
	public void setIIPSAuth(String u, String p, String url) {
		IIPSUser = u;
		IIPSPasswd = p;
		IIPSUrl = url;
	}
	public String getIIPSUrl() {
		return IIPSUrl.trim();
	}
	public String getIIPSUser() {
		return IIPSUser.trim();
	}
	public String getIIPSPasswd() {
		return IIPSPasswd.trim();
	}	
	public void setIIPSUrl(String s) {
		IIPSUrl =s;
	}
	public void setIIPSUser(String s) {
		IIPSUser=s;
	}
	public void setIIPSPasswd(String s) {
		IIPSPasswd=s;
	}
	public void setExistUrl(String s) {
		ExistUrl =s;
	}
	public void setExistHome(String s) {
		ExistHome=s;
	}
	public void setExistUser(String s) {
		ExistUser=s;
	}
	public void setExistPasswd(String s) {
		ExistPasswd=s;
	}


}

