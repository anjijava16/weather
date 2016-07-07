package com.crossover.trial.weather.service;

import java.util.Set;

import com.crossover.trial.weather.domain.AirportData;

public interface AirportService {
	public AirportData addAirport(String iataCode, double latitude, double longitude);

	public AirportData findAirportData(String iataCode);

	public int getAirportDataIdx(String iataCode);

	public Set<String> getAllAirportIataCodes();
	
	public void deleteAirport(String iataCode);
}
