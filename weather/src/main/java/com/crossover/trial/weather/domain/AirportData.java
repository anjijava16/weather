package com.crossover.trial.weather.domain;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Basic airport information.
 *
 * @author code test administrator
 */
public class AirportData {
	
	//for better encapsulation , make private the properties...
	
//	City		| Main city served by airport. May be spelled differently from name.
//	Country		| Country or territory where airport is located.
//	IATA/FAA 	| 3-letter FAA code or IATA code (blank or "" if not assigned)
//	ICAO		| 4-letter ICAO code (blank or "" if not assigned)
//	Latitude 	| Decimal degrees, up to 6 significant digits. Negative is South, positive is North.
//	Longitude	| Decimal degrees, up to 6 significant digits. Negative is West, positive is East.
//	Altitude	| In feet
//	Timezone	| Hours offset from UTC. Fractional hours are expressed as decimals. (e.g. India is 5.5)
//	DST			| One of E (Europe), A (US/Canada), S (South America), O (Australia), Z (New Zealand), N (None) or U (Unknown) 
	
	
//	private String city;
//	private String country;
//	private String icao;
//	private int altitude;
//	private float timezone;
//	private char dst='U';
	
    /** the three letter IATA code */
    private String iata;

    /** latitude value in degrees */
    private double latitude;

    /** longitude value in degrees */
    private double longitude;

    public AirportData() { }

    public String getIata() {
        return iata;
    }

    public void setIata(String iata) {
        this.iata = iata;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.NO_CLASS_NAME_STYLE);
    }

    public boolean equals(Object other) {
        if (other instanceof AirportData) {
            return ((AirportData)other).getIata().equals(this.getIata());
        }

        return false;
    }
}
