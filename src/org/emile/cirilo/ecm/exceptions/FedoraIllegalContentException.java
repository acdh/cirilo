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
 * This exception is a wrapper for all xml parsing exceptions. If an operation
 * requires that a datastream is parsed as xml, and the datastream does not
 * contain valid xml, this exception is thrown.
 * <br/>
 * Is also thrown when Trippi fails to parse the return from a call to the
 * Resource index
 *
 */
public class FedoraIllegalContentException extends EcmException {
    public FedoraIllegalContentException() {
    }

    public FedoraIllegalContentException(String s) {
        super(s);
    }

    public FedoraIllegalContentException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public FedoraIllegalContentException(Throwable throwable) {
        super(throwable);
    }
}
