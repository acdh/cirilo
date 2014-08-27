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
 * This is the mother class of all exceptions for ECM. All exceptions will be
 * regarded as EcmExceptions for purposes of formatting them for user
 * comsumption.
 * <br/>
 * The class is abstract, as no code should ever throw just an EcmException.
 * If something fails, and you really do not know what to throw, throw an
 * UnknownException
 *
 * @see net.sourceforge.ecm.exceptions.UnknownException
 */
public abstract class EcmException extends Exception {

    public EcmException() {
    }

    public EcmException(String s) {
        super(s);
    }

    public EcmException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public EcmException(Throwable throwable) {
        super(throwable);
    }


}
