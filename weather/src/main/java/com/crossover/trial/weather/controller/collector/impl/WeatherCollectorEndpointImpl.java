package com.crossover.trial.weather.controller.collector.impl;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.crossover.trial.weather.controller.collector.WeatherCollectorEndpoint;
import com.crossover.trial.weather.domain.AirportData;
import com.crossover.trial.weather.domain.DataPoint;
import com.crossover.trial.weather.exception.WeatherException;
import com.crossover.trial.weather.service.impl.AirportServiceImpl;
import com.google.gson.Gson;

/**
 * A REST implementation of the WeatherCollector API. Accessible only to airport
 * weather collection sites via secure VPN.
 *
 * @author code test administrator
 */

@Path("/collect")
public class WeatherCollectorEndpointImpl implements WeatherCollectorEndpoint {
	public final static Logger LOGGER = Logger.getLogger(WeatherCollectorEndpointImpl.class
			.getName());

	/** shared gson json to object factory */
	public final static Gson gson = new Gson();

	@Override
	public Response ping() {
		return Response.status(Response.Status.OK).entity("ready").build();
	}

	@Override
	public Response updateWeather(@PathParam("iata") String iataCode,
			@PathParam("pointType") String pointType, String datapointJson) {

		AirportServiceImpl service = new AirportServiceImpl();

		try {
			service.addDataPoint(iataCode, pointType, gson.fromJson(datapointJson, DataPoint.class));
		} catch (WeatherException e) {
			e.printStackTrace();
		}
		return Response.status(Response.Status.OK).build();
	}

	//http://localhost:9090/collect/airports
	@GET
	@Path("/airports")
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public Response getAirports() {

		System.out.println("getAirports...");
		Set<String> retval = new HashSet<>();
		
		AirportServiceImpl service = new AirportServiceImpl();
		retval=service.getAllAirportIataCodes();
		
		return Response.status(Response.Status.OK).entity(retval).build();
	}

	//http://localhost:9090/collect/airport/EWR
	@GET
	@Path("/airport/{iata}")
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public Response getAirport(@PathParam("iata") String iata) {
		System.out.println("getAirport....");
		AirportServiceImpl service = new AirportServiceImpl();
		AirportData ad = service.findAirportData(iata);
		return Response.status(Response.Status.OK).entity(ad).build();
	}

	//http://localhost:9090/collect/airport/IST/40.9829928/28.8082538
	@POST
	@Path("/airport/{iata}/{lat}/{long}")
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public Response addAirport(@PathParam("iata") String iata, @PathParam("lat") String latString,
			@PathParam("long") String longString) {
		System.out.println("addAirport...");
		

        if (iata == null || iata.length() != 3 || latString == null || longString == null) {
            LOGGER.severe( "Bad parameters: iata = " + iata + ", latString = " + latString + ", longString = " + longString);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        
        Double latitude;
        Double longitude;
        try {
            latitude = Double.valueOf(latString);
            longitude = Double.valueOf(longString);
        } catch (NumberFormatException ex) {
            LOGGER.severe("Wrong airport coordinates latString = " + latString + ", longString = " + longString);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        if (latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180) {
            LOGGER.severe("Wrong airport coordinates latString = " + latString + ", longString = " + longString);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

		AirportServiceImpl service = new AirportServiceImpl();
		service.addAirport(iata, latitude, longitude);

		return Response.status(Response.Status.OK).build();
	}

	@DELETE
	@Path("/airport/{iata}")
	@Override
	public Response deleteAirport(@PathParam("iata") String iata) {
		
		System.out.println("delete....");
		AirportServiceImpl service = new AirportServiceImpl();
		service.deleteAirport(iata);
		return Response.status(Response.Status.OK).build();
	}

	@Override
	public Response exit() {
		System.exit(0);
		return Response.noContent().build();
	}

}
