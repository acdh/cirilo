package org.emile.cirilo.business;

public class Topos {
		private String ID;
		private String Name;
		private String Country;
		private String Latitude;
		private String Longitude;
		private String XMLID;
		private String Feature;
		
		public Topos( String id, String name, String country, String latitude, String longitude, String feature, Integer cc  ) {
			this.ID = id;
			this.Name = name;
			this.Country = country;
			this.Latitude = latitude;
			this.Longitude = longitude;
			this.Feature = feature;
			this.XMLID = new Integer (cc).toString();
            			
		}
		
		public String getID() {return this.ID;};
		public String getName() {return this.Name;};
		public String getCountry() {return this.Country;};
		public String getLatitude() {return this.Latitude;};
		public String getLongitude() {return this.Longitude;};
		public String getFeature() {return this.Feature;};
		public String getXMLID() {return "GID."+this.XMLID;};
	
}
