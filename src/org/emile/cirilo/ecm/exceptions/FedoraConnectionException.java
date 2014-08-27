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

package org.emile.cirilo.ecm.exceptions;

/**
 * Thrown when unable to complete a fedora operation due to some connection
 * problem. This is like an IOException for speaking with the Fedora server
 * over whatever connector is chosen
 */
public class FedoraConnectionException extends EcmException{
    public FedoraConnectionException() {
    }

    public FedoraConnectionException(String s) {
        super(s);
    }

    public FedoraConnectionException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public FedoraConnectionException(Throwable throwable) {
        super(throwable);
    }
}
