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

package org.emile.cirilo.ecm.repository;

/**
 * A Fedora user token. This class encapsulates the fedora server url and the
 * user credentials.
 *
 * @see net.sourceforge.ecm.repository.FedoraConnector
 */
public class FedoraUserToken {
    private final String serverurl;
    private final String username;
    private final String password;

    /**
     * Constructor. Creates a new user token.
     * @param serverurl the location of the fedora server. In the form
     * http://localhost:8080/fedora
     * @param username The username to connect with
     * @param password The password to connect with
     */
    public FedoraUserToken(String serverurl, String username, String password) {
        this.serverurl = serverurl;
        this.username = username;
        this.password = password;
    }

    /**
     * Get the server url.
     * @return the server url
     */
    public String getServerurl() {
        return serverurl;
    }

    /**
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

}
