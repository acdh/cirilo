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
 *  Description of the Class
 *
 * @author     hstigler
 * @created    28. Juni 2005
 */
public class OutOfMemoryException extends Exception {
	/**
	 *  Constructor for the OutOfMemoryException object
	 */
	public OutOfMemoryException() { }


	/**
	 *  Constructor for the OutOfMemoryException object
	 *
	 * @param  p0  Description of the Parameter
	 */
	public OutOfMemoryException(String p0) {
		super(p0);
	}


	/**
	 *  Constructor for the OutOfMemoryException object
	 *
	 * @param  p0  Description of the Parameter
	 */
	public OutOfMemoryException(Throwable p0) {
		super(p0);
	}


	/**
	 *  Constructor for the OutOfMemoryException object
	 *
	 * @param  p0  Description of the Parameter
	 * @param  p1  Description of the Parameter
	 */
	public OutOfMemoryException(String p0, Throwable p1) {
		super(p0, p1);
	}
}

