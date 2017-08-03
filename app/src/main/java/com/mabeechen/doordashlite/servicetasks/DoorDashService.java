package com.mabeechen.doordashlite.servicetasks;

import com.mabeechen.doordashlite.servicetasks.models.SearchResult;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

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
    @GET("/v2/restaurant/")
    Call<List<SearchResult>> getSearchResults(@QueryMap Map<String, String> options );
}
