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
 * Exception thrown if the ontology in the content model cannot be parsed as
 * such. A subclass of FedoraIllegalContentException, as it is a more specific
 * demand on the contents of a datastream.
 * <br/>
 * This exception will only be thrown if the contents of the datastream was
 * found to be valid xml. If not, it will never be attempted to be parsed as
 * a ontology.
 *
 * @see net.sourceforge.ecm.exceptions.FedoraIllegalContentException
 */
public class InvalidOntologyException extends FedoraIllegalContentException {
    public InvalidOntologyException() {
    }

    public InvalidOntologyException(String s) {
        super(s);
    }

    public InvalidOntologyException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public InvalidOntologyException(Throwable throwable) {
        super(throwable);
    }
}
