package io.telenor.bustripper;

import org.glassfish.jersey.client.ClientConfig;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Searches for bus stops in area provided.
 */
public class FindBusStop implements Runnable {


    private static final String SEARCH_URL = "reisapi.ruter.no";
    private static final String SEARCH_PATH = "/Place/GetPlaces/";

    private URI searchTarget;

    private Client client;

    private TripsCallback listener;

    public FindBusStop(TripsCallback callback, String searchTerm) {
        this.listener = callback;
        try {
            // The API does not support any form of punctuation
            // Replacements such as %2E for dots are invalid
            // All should be simply removed
            searchTerm = searchTerm.replaceAll("\\p{P}", "").trim();
            this.searchTarget = new URI("http", SEARCH_URL, SEARCH_PATH + searchTerm, null);
        } catch (URISyntaxException ue) {
            // The URI encoder could not handle the search term, making it an illegal argument to this constructor
            // Bubbling up caller as such
            throw new IllegalArgumentException("Could not encode the search term", ue);
        }
    }

    public void run() {
        ClientConfig configuration = new ClientConfig();

        client = ClientBuilder.newClient(configuration);

        Invocation.Builder invocationBuilder = client
                .target(searchTarget)
                .request(MediaType.APPLICATION_JSON);

        final AsyncInvoker asyncInvoker = invocationBuilder.async();
        BusStopsCallBack callback = new BusStopsCallBack(listener);
        asyncInvoker.get(callback);
    }
}
