package com.crossover.trial.weather.repository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import com.crossover.trial.weather.domain.AirportData;
import com.crossover.trial.weather.domain.AtmosphericInformation;

public class MemoryRepository implements Repository {

	private final Map<String, AtmosphericInformation> atmosphericInformation;

	private final Map<AirportData, AtomicInteger> requestFrequency;

	private final Map<Double, AtomicInteger> radiusFrequency;

	private final List<AirportData> airportData;

	private MemoryRepository() {
		atmosphericInformation = new ConcurrentHashMap<>(1000);
		requestFrequency = new ConcurrentHashMap<>();
		radiusFrequency = new ConcurrentHashMap<>();
		airportData = new CopyOnWriteArrayList<>();
	}
	
	private static final Repository INSTANCE = new MemoryRepository();
	
	public static Repository getInstance() {
		return INSTANCE;
	}

	@Override
	public Map<AirportData, AtomicInteger> getRequestFrequency() {
		return requestFrequency;
	}

	@Override
	public Map<Double, AtomicInteger> getRadiusFrequency() {
		return radiusFrequency;
	}

	@Override
	public Map<String, AtmosphericInformation> getAtmosphericInformation() {
		return atmosphericInformation;
	}

	@Override
	public List<AirportData> getAirportData() {
		// TODO Auto-generated method stub
		return airportData;
	}

}
