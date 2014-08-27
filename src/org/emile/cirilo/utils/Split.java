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

import org.emile.cirilo.*;

public class Split {
	
	private String[] AS;
	
	public Split(String s) {
		AS = s.split("["+Common.SEPERATOR+"]");
		if (AS.length != 2) {
			AS = new String[2];
			AS[0] = s;
			AS[1] = "undefined";
		}
	 }		
	 public String get() {
		 return AS[1].trim();
	 }
	 
	 public String get(int i) {
		 return AS[i].trim();
	 }
	

}
