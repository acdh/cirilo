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
 * Exception thrown when an operation is performed on a datastream, that do
 * not exist. If the object itself does not exist, a ObjectNotFoundException
 * is thrown instead.
 *
 * @see net.sourceforge.ecm.exceptions.ObjectNotFoundException
 */
public class DatastreamNotFoundException extends EcmException{
	
	static final long serialVersionUID = 1L;

    public DatastreamNotFoundException() {
    }

    public DatastreamNotFoundException(String s) {
        super(s);
    }

    public DatastreamNotFoundException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public DatastreamNotFoundException(Throwable throwable) {
        super(throwable);
    }
}
