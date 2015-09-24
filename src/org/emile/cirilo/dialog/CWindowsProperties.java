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

package org.emile.cirilo.dialog;

public class CWindowsProperties  {
	
	private  int x = 0;
	private  int y = 0;
	private  int width = 0; 
	private  int height = 0;
	private  Integer[] columns = new Integer[24];
	
	public int getX() { return this.x; };
	public int getY() { return this.y; };
	public int getWidth() { return this.width; };
	public int getHeight() { return this.height; };
	
	public int getWidth(int i) { 
		int r = 0;
		try {
			r = ((Integer) this.columns[i]).intValue(); 		
		} catch (Exception e) {					
		}
		return r;
	}

	public void setX(int x) { this.x = x; };
	public void setY(int y) { this.y = y; };
	public void setWidth(int width) { this.width = width; };
	public void setHeight(int height) { this.height = height; };
	public void setWidth (Integer width, int i) { this.columns[i] = width ; }

}
