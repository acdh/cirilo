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
 * Exception thrown when a fedora operation fails in ways not properly
 * documented in the fedora api.
 *
 * This class have been marked as deprecated, as it is really only a placeholder
 * untill the fedora workings can be properly explored.
 */
@Deprecated
public class UnknownException extends EcmException{
    public UnknownException() {
    }

    public UnknownException(String s) {
        super(s);
    }

    public UnknownException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public UnknownException(Throwable throwable) {
        super(throwable);
    }
}
