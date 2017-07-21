package com.mabeechen.doordashlite.servicetasks;

import com.mabeechen.doordashlite.servicetasks.models.SearchResult;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Holds calls to the DoorDash interface
 *
 * @author marbe
 * @since 7/18/17
 */
public interface DoorDashService {
    /**
     * Creates a call to get restaurant search results
     * @return The search results
     */
    @GET("/v2/restaurant/?lat=37.422740&lng=-122.139956")
    Call<List<SearchResult>> getSearchResults();
}
