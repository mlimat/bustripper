package io.telenor.bustripper;

import java.io.IOException;
import java.util.Set;

/**
 * Callback class to client app requesting some bustrips.
 */
public interface TripsCallback {

    /**
     * Got a list of trips
     * @param trips the set of bus trips found
     * @param totalStops the number of expected calls
     */
    public void gotTrips(Set<BusTrip> trips, int totalStops);

    /**
     * Faild getting the list of trips.
     * @param io trouble found
     */
    public void failedGettingTrips(IOException io);
}
