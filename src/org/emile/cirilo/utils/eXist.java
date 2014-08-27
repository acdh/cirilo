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

package org.emile.cirilo.utils;

public class eXist
{
	private String collection;
	private String stream;
	
	public eXist(String p) 
	{ 
		int k = p.lastIndexOf("/");
	
		if (k > -1) {
			collection=p.substring(0,k);
			stream=p.substring(k+1);
		} else {
			collection = "";
			stream = p;
		}
	}
	
	public String getCollection() {  		
		return this.collection;
	}

	public String getStream() {
		return this.stream;    		
	}
}

