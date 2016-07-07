package com.crossover.trial.weather.service;

import java.util.Set;

import com.crossover.trial.weather.domain.AirportData;
import com.crossover.trial.weather.domain.AtmosphericInformation;
import com.crossover.trial.weather.domain.DataPoint;
import com.crossover.trial.weather.exception.WeatherException;

public interface AirportService {
	
	public AirportData addAirport(String iataCode, double latitude, double longitude);

	public AirportData findAirportData(String iataCode);

	public void deleteAirport(String iataCode);
	
	public Set<String> getAllAirportIataCodes();

	public void addDataPoint(String iataCode, String pointType, DataPoint dp)
			throws WeatherException;

	public void updateAtmosphericInformation(AtmosphericInformation ai, String pointType,
			DataPoint dp) throws WeatherException;
}
