package org.emile.cirilo.business;

public class HashEntry {
	  
	  private String key;
	  private String value;
	  
	  public HashEntry (String key, String value) {
		  this.key = key;
		  this.value = value;
	  }
	  public String getKey() {
		  return this.key;
	  }
	  public String getValue() {
		  return this.value;
	  }
}