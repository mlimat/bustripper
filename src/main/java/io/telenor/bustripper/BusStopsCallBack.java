package io.telenor.bustripper;

import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.core.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 */
public class BusStopsCallBack implements InvocationCallback<Response> {

    private ObjectMapper mapper = new ObjectMapper();

    private TripsCallback listener;

    public BusStopsCallBack(TripsCallback callback) {
        this.listener = callback;
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public void completed(Response response) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            BusStop[] stops = mapper.readValue(response.readEntity(String.class), BusStop[].class);

            // Consider only bus stops
            ArrayList<BusStop> validStops = new ArrayList<>();
            for(BusStop stop : stops) {
                if (stop.getPlaceType().equals("Stop")) {
                    validStops.add(stop);
                }
            }

            System.out.println(String.format("Got %d busstops nearby", validStops.size()));

            // The API does not return stops by proximity if a location is not given
            // Therefore it is not wise to limit the stops to 10 here
            for (BusStop stop : validStops) {
                new Thread(new FindBusLinesForStop(stop.getId(), listener, validStops.size())).start();
            }
        } catch (IOException e) {
            listener.failedGettingTrips(e);
        }

    }

    public void failed(Throwable throwable) {
        listener.failedGettingTrips((IOException) throwable);
    }
}
