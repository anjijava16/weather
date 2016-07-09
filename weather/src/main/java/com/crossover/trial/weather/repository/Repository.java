package com.crossover.trial.weather.repository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.crossover.trial.weather.domain.AirportData;
import com.crossover.trial.weather.domain.AtmosphericInformation;

public interface Repository {

	Map<AirportData, AtomicInteger> getRequestFrequency();

	Map<Double, AtomicInteger> getRadiusFrequency();

	Map<String, AtmosphericInformation> getAtmosphericInformation();
	
	List<AirportData> getAirportData();

}
