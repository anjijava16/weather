package com.crossover.trial.weather;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.crossover.trial.weather.controller.collector.WeatherCollectorEndpoint;
import com.crossover.trial.weather.controller.collector.impl.WeatherCollectorEndpointImpl;
import com.crossover.trial.weather.controller.query.WeatherQueryEndpoint;
import com.crossover.trial.weather.controller.query.impl.WeatherQueryEndpointImpl;
import com.crossover.trial.weather.domain.AtmosphericInformation;
import com.crossover.trial.weather.domain.DataPoint;
import com.crossover.trial.weather.service.impl.AirportServiceImpl;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class WeatherEndpointTest {

    private WeatherQueryEndpoint _query = new WeatherQueryEndpointImpl();

    private WeatherCollectorEndpoint _update = new WeatherCollectorEndpointImpl();

    private Gson _gson = new Gson();

    private DataPoint _dp;
    @Before
    public void setUp() throws Exception {
    	AirportServiceImpl.airportData.clear();
    	AirportServiceImpl.atmosphericInformationMap.clear();
    	AirportServiceImpl.requestFrequency.clear();
    	
        _update.addAirport("BOS", "42.364347", "-71.005181");
        _update.addAirport("EWR", "40.6925", "-74.168667");
        _update.addAirport("JFK", "40.639751", "-73.778925");
        _update.addAirport("LGA", "40.777245", "-73.872608");
        _update.addAirport("MMU", "40.79935", "-74.4148747");
       // _update.addAirport("MMU", "40.79935", "-74.4148747");
    	
        _dp = new DataPoint.Builder()
                .withCount(10).withFirst(10).withMedian(20).withLast(30).withMean(22).build();
        _update.updateWeather("BOS", "wind", _gson.toJson(_dp));
        
        DataPoint _dp2 = new DataPoint.Builder()
        .withCount(10).withFirst(19).withMedian(25).withLast(35).withMean(28).build();
        _update.updateWeather("BOS","TEMPERATURE", _gson.toJson(_dp2));
//        
        DataPoint _dp3 = new DataPoint.Builder()
        .withCount(20).withFirst(40).withMedian(30).withLast(35).withMean(12).build();
        _update.updateWeather("JFK", "TEMPERATURE",  _gson.toJson(_dp3));
//        
        Object o=_query.weather("BOS", "0").getEntity();
        Object o6=_query.weather("BOS", "0.5").getEntity();
        Object o7=_query.weather("BOS", "0.9").getEntity();
        
        Object o2=_query.weather("BOS", "10").getEntity();
        
        Object o3=_query.weather("JFK", "10").getEntity();
        Object o4=_query.weather("JFK", "10").getEntity();
        
        Object o5=_query.weather("LGA", "10").getEntity();
        
        System.out.println(o);
        System.out.println(o2);
        
//        Object o2=_query.weather("BOS", "0").getEntity();
//        Object o3=_query.weather("BOS", "0").getEntity();
//        System.out.println("query : " +o);
    }

    @Test
    public void testPing() throws Exception {
        String ping = _query.ping();
        
        System.out.println("ping: " + ping);
        
        JsonElement pingResult = new JsonParser().parse(ping);
        assertEquals(3, pingResult.getAsJsonObject().get("datasize").getAsInt());
        assertEquals(5, pingResult.getAsJsonObject().get("iata_freq").getAsJsonObject().entrySet().size());
    }

    @Test
    public void testGet() throws Exception {
        List<AtmosphericInformation> ais = (List<AtmosphericInformation>) _query.weather("BOS", "0").getEntity();
        assertEquals(ais.get(0).getWind(), _dp);
    }

    @Test
    public void testGetNearby() throws Exception {
        // check datasize response
        _update.updateWeather("JFK", "wind", _gson.toJson(_dp));
        _dp.setMean(40);
        _update.updateWeather("EWR", "wind", _gson.toJson(_dp));
        _dp.setMean(30);
        _update.updateWeather("LGA", "wind", _gson.toJson(_dp));
        
        List<AtmosphericInformation> ais = (List<AtmosphericInformation>) _query.weather("JFK", "200").getEntity();
        for(AtmosphericInformation as : ais){
        	System.out.println("as: " + as);
        }
        assertEquals(3, ais.size());
    }

    @Test
    public void testUpdate() throws Exception {

        DataPoint windDp = new DataPoint.Builder()
                .withCount(10).withFirst(10).withMedian(20).withLast(30).withMean(22).build();
        _update.updateWeather("BOS", "wind", _gson.toJson(windDp));
        _query.weather("BOS", "0").getEntity();

        String ping = _query.ping();
        JsonElement pingResult = new JsonParser().parse(ping);
        assertEquals(3, pingResult.getAsJsonObject().get("datasize").getAsInt());

        DataPoint cloudCoverDp = new DataPoint.Builder()
                .withCount(4).withFirst(10).withMedian(60).withLast(100).withMean(50).build();
        _update.updateWeather("BOS", "cloudcover", _gson.toJson(cloudCoverDp));

        List<AtmosphericInformation> ais = (List<AtmosphericInformation>) _query.weather("BOS", "0").getEntity();
        assertEquals(ais.get(0).getWind(), windDp);
        assertEquals(ais.get(0).getCloudCover(), cloudCoverDp);
    }

}