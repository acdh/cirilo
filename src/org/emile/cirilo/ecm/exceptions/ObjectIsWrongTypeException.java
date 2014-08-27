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
 * Exception thrown when an operation attempts to use a object as something it
 * is not.
 * <br/>
 * Examples include:
 * <ul>
 * <li>Using a content model as a data object
 * <li>Using a data object as a content model
 * <li>Using a normal data object as a template
 * <li>Using a content model as a template
 * </ul>
 *
 * This exception is, of course, only thrown if the object is found, and it's
 * type can be determined 
 * @see net.sourceforge.ecm.exceptions.ObjectNotFoundException
 */
public class ObjectIsWrongTypeException extends EcmException{
    public ObjectIsWrongTypeException() {
    }

    public ObjectIsWrongTypeException(String s) {
        super(s);
    }

    public ObjectIsWrongTypeException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public ObjectIsWrongTypeException(Throwable throwable) {
        super(throwable);
    }
}
