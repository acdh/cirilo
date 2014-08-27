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
 * A development class. Methods not implemented should throw this exception. As
 * such, they will be easy to find via a global search.
 *
 *<br/>
 * This class should not be used in a released version
 */
public class NotImplementedYetException extends RuntimeException {

    public NotImplementedYetException() {
    }

    public NotImplementedYetException(String s) {
        super(s);
    }

    public NotImplementedYetException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public NotImplementedYetException(Throwable throwable) {
        super(throwable);
    }
}
