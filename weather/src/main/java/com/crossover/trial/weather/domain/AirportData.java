package com.crossover.trial.weather.domain;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Basic airport information.
 *
 * @author code test administrator
 */
public class AirportData {
	// committed ;
	// 0 - fixed BuilderPattern bug
	// 1 - public properties are restricted to private for better encapsulation.
	// 2 - added all properties

	/** Name of the airport */
	private String name;

	/** Main city served by airport. May be spelled differently from name. */
	private String city;

	/** Country or territory where airport is located. */
	private String country;

	/** the three letter IATA code */
	private String iata;

	/** 4-letter ICAO code */
	private String icao;

	/** latitude value in degrees */
	private double latitude;

	/** longitude value in degrees */
	private double longitude;

	/** altitude value in feets */
	private long altitude;

	/** Hours offset from UTC. */
	private double timezone;

	/** Day light saving time of airport */
	private DST zone = DST.U;

	@SuppressWarnings("unused")
	private AirportData() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getIata() {
		return iata;
	}

	public void setIata(String iata) {
		this.iata = iata;
	}

	public String getIcao() {
		return icao;
	}

	public void setIcao(String icao) {
		this.icao = icao;
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

	public long getAltitude() {
		return altitude;
	}

	public void setAltitude(long altitude) {
		this.altitude = altitude;
	}

	public double getTimezone() {
		return timezone;
	}

	public void setTimezone(double timezone) {
		this.timezone = timezone;
	}

	public DST getZone() {
		return zone;
	}

	public void setZone(DST zone) {
		this.zone = zone;
	}

	public AirportData(Builder builder) {
		this.name = builder.name;
		this.city = builder.city;
		this.country = builder.country;
		this.iata = builder.iata;
		this.icao = builder.icao;
		this.latitude = builder.latitude;
		this.longitude = builder.longitude;
		this.altitude = builder.altitude;
		this.timezone = builder.timezone;
		this.zone = builder.zone;
	}

	public static class Builder {

		private String name;
		private String city;
		private String country;
		private String iata;
		private String icao;
		private double latitude;
		private double longitude;
		private long altitude;
		private double timezone;
		private DST zone;

		// default constructor
		public Builder() {
		}

		public Builder withName(String name) {
			this.name = name;
			return this;
		}

		public Builder withCity(String city) {
			this.city = city;
			return this;
		}

		public Builder withCountry(String country) {
			this.country = country;
			return this;
		}

		public Builder withIata(String iata) {
			this.iata = iata;
			return this;
		}

		public Builder withICAO(String icao) {
			this.icao = icao;
			return this;
		}

		public Builder withLatitude(double latitude) {
			this.latitude = latitude;
			return this;
		}

		public Builder withLongitude(double longitude) {
			this.longitude = longitude;
			return this;
		}

		public Builder withAltitude(long altitude) {
			this.altitude = altitude;
			return this;
		}

		public Builder withTimezone(double timezone) {
			this.timezone = timezone;
			return this;
		}

		public Builder withDSTZone(DST zone) {
			this.zone = zone;
			return this;
		}

		public AirportData build() {
			return new AirportData(this);
		}

	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.NO_CLASS_NAME_STYLE);
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof AirportData) {
			return ((AirportData) other).getIata().equals(this.getIata());
		}

		return false;
	}
}
