package io.telenor.bustripper;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Callback from Jersey when bustrips are there.
 */
public class BusTripsCallBack implements InvocationCallback<Response> {
    ObjectMapper mapper = new ObjectMapper();
    String url;
    private TripsCallback listener;
    private int totalStops;

    public BusTripsCallBack(String url, TripsCallback callback, int totalStops) {
        this.url = url;
        this.listener = callback;
        this.totalStops = totalStops;
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public void completed(Response response) {
        ObjectMapper mapper = new ObjectMapper();
        String content = response.readEntity(String.class);
        if (content == null || content.isEmpty()) {
            // Bubble up the empty content so that the aggregator can keep track
            // since this was expected to be a valid response
            listener.gotTrips(null, totalStops);
        } else {
            try {
                BusTrip[] trips = mapper.readValue(content, BusTrip[].class);
                HashSet set = new HashSet(Arrays.asList(trips));
                listener.gotTrips(set, totalStops);

            } catch (IOException e) {
                listener.failedGettingTrips(e);
            }
        }
    }

    public void failed(Throwable throwable) {
        listener.failedGettingTrips((IOException) throwable);
    }
}
