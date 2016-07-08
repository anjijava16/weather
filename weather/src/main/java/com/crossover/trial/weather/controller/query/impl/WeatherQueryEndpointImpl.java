package com.crossover.trial.weather.controller.query.impl;

import com.crossover.trial.weather.controller.query.WeatherQueryEndpoint;
import com.crossover.trial.weather.domain.AirportData;
import com.crossover.trial.weather.domain.AtmosphericInformation;
import com.crossover.trial.weather.service.impl.AirportServiceImpl;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.*;
import java.util.logging.Logger;

import static com.crossover.trial.weather.service.impl.AirportServiceImpl.*;
/**
 * The Weather App REST endpoint allows clients to query, update and check health stats. Currently, all data is
 * held in memory. The end point deploys to a single container
 *
 * @author code test administrator
 */
@Path("/query")
public class WeatherQueryEndpointImpl implements WeatherQueryEndpoint {

    public final static Logger LOGGER = Logger.getLogger("WeatherQueryEndpointImpl");
    
    /**
     * Retrieve service health including total size of valid data points and request frequency information.
     *
     * @return health stats for the service as a string
     */
	@Override
	public String ping() {
		Map<String, Object> retval = new HashMap<>();

		// System.out.println(atmosphericInformationMap);

		int datasize = 0;
		for (String key : atmosphericInformationMap.keySet()) {

			AtmosphericInformation ai = atmosphericInformationMap.get(key);
			// we only count recent readings

			long lastUpdateTime = ai.getLastUpdateTime();
			if (ai.getCloudCover() != null) {
				datasize = count(lastUpdateTime, datasize);
			}

			if (ai.getHumidity() != null) {
				datasize = count(lastUpdateTime, datasize);
			}

			if (ai.getPressure() != null) {
				datasize = count(lastUpdateTime, datasize);
			}

			if (ai.getPrecipitation() != null) {
				datasize = count(lastUpdateTime, datasize);
			}

			if (ai.getTemperature() != null) {
				datasize = count(lastUpdateTime, datasize);
			}

			if (ai.getWind() != null) {
				datasize = count(lastUpdateTime, datasize);
			}

		}
		// System.out.println("datasize: " +datasize);
		retval.put("datasize", datasize);

		Map<String, Double> freq = new HashMap<>();
		// fraction of queries

		int totalRequest = 0;
		for (int requestCount : requestFrequency.values()) {
			totalRequest = totalRequest + requestCount;
		}
		// fixed
		for (AirportData data : airportData) {
			double frac = (double) requestFrequency.getOrDefault(data, 0) / totalRequest;
			freq.put(data.getIata(), frac);
		}
		retval.put("iata_freq", freq);

		// find maximum radis which is requested.
		// [0-maximum radis]
		int m = radiusFrequency.keySet().stream().max(Double::compare).orElse(1000.0).intValue() + 1;

		int[] hist = new int[m];
		for (Map.Entry<Double, Integer> e : radiusFrequency.entrySet()) {
			int i = e.getKey().intValue(); // round lower
			// sum for radius same double ( 0.0 --- 0.9 radiuses are all 0)
			hist[i] = hist[i] + e.getValue();
		}
		retval.put("radius_freq", hist);

		return gson.toJson(retval);
	}
	
    
	private int count(long lastUpdateTime, int datasize) {
		if (lastUpdateTime > System.currentTimeMillis() - 86400000) {
			return datasize + 1;
		}
		return datasize;

	}
    
    public static void main(String[] args) {
    	ArrayList<Double> a = new ArrayList<Double>();
    	
//    	a.add(10.2);
//    	a.add(20.5);
//    	a.add(20.5);
//    	a.add(1071.2);
//    	a.add(20.5);
    	
    	  int m = a.stream()
                  .max(Double::compare)
                  .orElse(1000.0).intValue() + 1;
    	  
    	  System.out.println(m);
	}

    /**
     * Given a query in json format {'iata': CODE, 'radius': km} extracts the requested airport information and
     * return a list of matching atmosphere information.
     *
     * @param iata the iataCode
     * @param radiusString the radius in km
     *
     * @return a list of atmospheric information
     */
    @GET
    @Path("/weather/{iata}/{radius}")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    //ilgili radius capinda herhangi bir atmosphreic data varsa listeye ekle.
    //current datayi da ekle.
    public Response weather(@PathParam("iata") String iata, @PathParam("radius") String radiusString) {
    	
        double radius = radiusString == null || radiusString.trim().isEmpty() ? 0 : Double.valueOf(radiusString);
        
        AirportServiceImpl service = new AirportServiceImpl();
        //updateRequestFrequency
        service.updateRequestFrequency(iata, radius);

        List<AtmosphericInformation> retval = new ArrayList<>();
        if (radius == 0) {                                  
            retval.add(atmosphericInformationMap.get(iata));
        } else {
            AirportData ad = service.findAirportData(iata);
            //System.out.println("found : " + ad);
            //System.out.println("airport data : " + airportData);
            for (int i=0;i< airportData.size(); i++){
            	double distance=service.calculateDistance(ad, airportData.get(i));
             	//System.out.println();
            	//System.out.println(distance + " " + airportData.get(i));
                if (distance<= radius){               
                    AtmosphericInformation ai = atmosphericInformationMap.get(airportData.get(i).getIata());
                    //System.out.println("ai before : "+ ai);
                    if (ai.getCloudCover() != null || ai.getHumidity() != null || ai.getPrecipitation() != null
                            || ai.getPressure() != null || ai.getTemperature() != null || ai.getWind() != null){
                             retval.add(ai);
                        //System.out.println("ai next : " + iata + " | " + ai);
                    }
                }
            }
        }
        return Response.status(Response.Status.OK).entity(retval).build();
    }


}
