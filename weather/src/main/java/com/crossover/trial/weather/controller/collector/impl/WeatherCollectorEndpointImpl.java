package com.crossover.trial.weather.controller.collector.impl;

import com.crossover.trial.weather.controller.collector.WeatherCollectorEndpoint;
import com.crossover.trial.weather.domain.AirportData;
import com.crossover.trial.weather.domain.AtmosphericInformation;
import com.crossover.trial.weather.domain.DataPoint;
import com.crossover.trial.weather.domain.DataPointType;
import com.crossover.trial.weather.exception.WeatherException;
import com.crossover.trial.weather.service.collector.impl.WeatherCollectorEndpointServiceImpl;
import com.google.gson.Gson;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import static com.crossover.trial.weather.controller.query.impl.WeatherQueryEndpointImpl.*;

/**
 * A REST implementation of the WeatherCollector API. Accessible only to airport weather collection
 * sites via secure VPN.
 *
 * @author code test administrator
 */

@Path("/collect")
public class WeatherCollectorEndpointImpl implements WeatherCollectorEndpoint {
    public final static Logger LOGGER = Logger.getLogger(WeatherCollectorEndpointImpl.class.getName());

    /** shared gson json to object factory */
    public final static Gson gson = new Gson();

    @Override
    public Response ping() {
        return Response.status(Response.Status.OK).entity("ready").build();
    }

    @Override
    public Response updateWeather(@PathParam("iata") String iataCode,
                                  @PathParam("pointType") String pointType,
                                  String datapointJson) {
    	
    	WeatherCollectorEndpointServiceImpl service = new WeatherCollectorEndpointServiceImpl();
    	
        try {
        	service.addDataPoint(iataCode, pointType, gson.fromJson(datapointJson, DataPoint.class));
        } catch (WeatherException e) {
            e.printStackTrace();
        }
        return Response.status(Response.Status.OK).build();
    }


    @GET
    @Path("/airports")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response getAirports() {
    	System.out.println("getAirports...");
        Set<String> retval = new HashSet<>();
        for (AirportData ad : airportData) {
            retval.add(ad.getIata());
        }
        return Response.status(Response.Status.OK).entity(retval).build();
    }

    @GET
    @Path("/airport/{iata}")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response getAirport(@PathParam("iata") String iata) {
    	System.out.println("getAirport....");
        AirportData ad = findAirportData(iata);
        return Response.status(Response.Status.OK).entity(ad).build();
    }


    @POST
    @Path("/airport/{iata}/{lat}/{long}")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response addAirport(@PathParam("iata") String iata,
                               @PathParam("lat") String latString,
                               @PathParam("long") String longString) {
    	System.out.println("addAirport...");
    	
    	WeatherCollectorEndpointServiceImpl service = new WeatherCollectorEndpointServiceImpl();  	
    	service.addAirport(iata, Double.valueOf(latString), Double.valueOf(longString));
    	
        return Response.status(Response.Status.OK).build();
    }


    @DELETE
    @Path("/airport/{iata}")
    @Override
    public Response deleteAirport(@PathParam("iata") String iata) {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @Override
    public Response exit() {
        System.exit(0);
        return Response.noContent().build();
    }
    //
    // Internal support methods
    //

}
