package com.crossover.trial.weather.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.crossover.trial.weather.controller.collector.WeatherCollectorEndpoint;
import com.crossover.trial.weather.controller.collector.impl.WeatherCollectorEndpointImpl;
import com.crossover.trial.weather.domain.AirportData;
import com.crossover.trial.weather.domain.AtmosphericInformation;
import com.crossover.trial.weather.domain.DataPoint;
import com.crossover.trial.weather.domain.DataPointType;
import com.crossover.trial.weather.exception.WeatherException;
import com.crossover.trial.weather.service.AirportService;
import com.google.gson.Gson;

public class AirportServiceImpl implements AirportService {

	private static final Logger LOGGER = Logger.getLogger(AirportServiceImpl.class.getName());

	/** earth radius in KM */
	public static final double R = 6372.8;

	/** shared gson json to object factory */
	public static final Gson gson = new Gson();

	/** all known airports */
	public static List<AirportData> airportData = new ArrayList<>();

	/**
	 * atmospheric information for each airport, iataCode corresponds with
	 * airportData
	 */
	public static Map<String, AtmosphericInformation> atmosphericInformationMap = new HashMap<>();

	/**
	 * Internal performance counter to better understand most requested
	 * information, this map can be improved but for now provides the basis for
	 * future performance optimizations. Due to the stateless deployment
	 * architecture we don't want to write this to disk, but will pull it off
	 * using a REST request and aggregate with other performance metrics
	 * {@link #ping()}
	 */
	public static Map<AirportData, Integer> requestFrequency = new HashMap<AirportData, Integer>();

	public static Map<Double, Integer> radiusFreq = new HashMap<Double, Integer>();

	/**
	 * Update the airports weather data with the collected data.
	 *
	 * @param iataCode
	 *            the 3 letter IATA code
	 * @param pointType
	 *            the point type {@link DataPointType}
	 * @param dp
	 *            a datapoint object holding pointType data
	 *
	 * @throws WeatherException
	 *             if the update can not be completed
	 */

	@Override
	public void addDataPoint(String iataCode, String pointType, DataPoint dp)
			throws WeatherException {
		AtmosphericInformation ai = atmosphericInformationMap.get(iataCode);		
		updateAtmosphericInformation(ai, pointType, dp);
	}

	/**
	 * update atmospheric information with the given data point for the given
	 * point type
	 *
	 * @param ai
	 *            the atmospheric information object to update
	 * @param pointType
	 *            the data point type as a string
	 * @param dp
	 *            the actual data point
	 */

	@Override
	public void updateAtmosphericInformation(AtmosphericInformation ai, String pointType,
			DataPoint dp) throws WeatherException {
		if(ai==null) {
			throw new WeatherException();
		}
		DataPointType dptype = null;
		try {
			dptype = DataPointType.valueOf(pointType.toUpperCase());
		} catch (IllegalArgumentException ex) {
			throw new WeatherException();
		}

		switch (dptype) {
		case WIND:
			if (dp.getMean() >= 0) {
				ai.setWind(dp);
			}
			break;

		case TEMPERATURE:
			if (dp.getMean() >= -50 && dp.getMean() < 100) {
				ai.setTemperature(dp);
			}
			break;

		case HUMIDTY:
			if (dp.getMean() >= 0 && dp.getMean() < 100) {
				ai.setHumidity(dp);
			}
			break;

		case PRESSURE:
			if (dp.getMean() >= 650 && dp.getMean() < 800) {
				ai.setPressure(dp);
			}
			break;

		case CLOUDCOVER:
			if (dp.getMean() >= 0 && dp.getMean() < 100) {
				ai.setCloudCover(dp);
			}
			break;

		case PRECIPITATION:
			if (dp.getMean() >= 0 && dp.getMean() < 100) {
				ai.setPrecipitation(dp);
			}
			break;
		}
	}

	/**
	 * Records information about how often requests are made
	 *
	 * @param iata
	 *            an iata code
	 * @param radius
	 *            query radius
	 */
	public void updateRequestFrequency(String iata, Double radius) {
		AirportData airportData = findAirportData(iata);
		requestFrequency.put(airportData, requestFrequency.getOrDefault(airportData, 0) + 1);
		radiusFreq.put(radius, radiusFreq.getOrDefault(radius, 0));
	}

	/**
	 * Add a new known airport to our list.
	 *
	 * @param iataCode
	 *            3 letter code
	 * @param latitude
	 *            in degrees
	 * @param longitude
	 *            in degrees
	 *
	 * @return the added airport
	 */


	@Override
	public AirportData addAirport(String iataCode, double latitude, double longitude) {
		AirportData airportData = new AirportData.Builder().withIata(iataCode).withLatitude(latitude)
				.withLongitude(longitude).build();
		//check already added
		if(findAirportData(iataCode)==null){
			AirportServiceImpl.airportData.add(airportData);			
			AtmosphericInformation ai = new AtmosphericInformation();
			atmosphericInformationMap.put(iataCode, ai);
		}
		return airportData;

	}

	/**
	 * Given an iataCode find the airport data
	 *
	 * @param iataCode
	 *            as a string
	 * @return airport data or null if not found
	 */

	@Override
	public AirportData findAirportData(String iataCode) {
		return airportData.stream().filter(ap -> ap.getIata().equals(iataCode)).findFirst()
				.orElse(null);
	}

	/**
	 * Given an iataCode find the airport data
	 *
	 * @param iataCode
	 *            as a string
	 * @return airport data or null if not found
	 */

	@Override
	public Set<String> getAllAirportIataCodes() {

		Set<String> iataCodes = new HashSet<>();
		for (AirportData ad : airportData) {
			iataCodes.add(ad.getIata());
		}
		return iataCodes;
	}

	@Override
	public void deleteAirport(String iataCode) {
		// if (iataCode == null) {
		// LOGGER.severe("Cannot delete airport");
		// return;
		// }
		// deleteAtmosphericInformation(iataCode);
		// getAirportDataStorage().remove(iataCode);

		System.out.println("deleting....");
		atmosphericInformationMap.remove(iataCode);
		AirportData ad = findAirportData(iataCode);
		airportData.remove(ad);
	}

	/**
	 * Haversine distance between two airports.
	 *
	 * @param ad1
	 *            airport 1
	 * @param ad2
	 *            airport 2
	 * @return the distance in KM
	 */

	@Override
	public double calculateDistance(AirportData ad1, AirportData ad2) {
		double latPointOne= ad1.getLatitude();
		double latPointTwo= ad2.getLatitude();
		double longPointOne= ad1.getLongitude();
		double longPointTwo=  ad2.getLongitude();
		
		double deltaLat = Math.toRadians(latPointTwo - latPointOne);
		double deltaLon = Math.toRadians(longPointTwo - longPointOne);
		
	    double a = Math.pow(Math.sin(deltaLat / 2),2)
	  	      + Math.cos(Math.toRadians(latPointOne)) * Math.cos(Math.toRadians(latPointTwo))
	  	      * Math.pow(Math.sin(deltaLon / 2),2);

	  	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
	  	    return R * c;
	//
	//		Haversine
	//		formula:	a = sin²(Δφ/2) + cos φ1 ⋅ cos φ2 ⋅ sin²(Δλ/2)
	//		c = 2 ⋅ atan2( √a, √(1−a) )
	//		d = R ⋅ c
	// 		http://www.movable-type.co.uk/scripts/latlong.html
	}

//	/**
//	 * A dummy init method that loads hard coded data
//	 */
//	{
//		// airportData.clear();
//		// atmosphericInformation.clear();
//		// requestFrequency.clear();
//		if (airportData.size() == 0) {
//			addAirport("BOS", 42.364347, -71.005181);
//			addAirport("EWR", 40.6925, -74.168667);
//			addAirport("JFK", 40.639751, -73.778925);
//			addAirport("LGA", 40.777245, -73.872608);
//			addAirport("MMU", 40.79935, -74.4148747);
//		}
//
//		AtmosphericInformation ai = new AtmosphericInformation();
//
//		DataPoint _dp = new DataPoint.Builder().withCount(10).withFirst(10).withMedian(20)
//				.withLast(30).withMean(22).build();
//		ai.setWind(_dp);
//
//		atmosphericInformation.put("JFK", ai);
//
//		System.out.println(atmosphericInformation);
//
//	}

}
