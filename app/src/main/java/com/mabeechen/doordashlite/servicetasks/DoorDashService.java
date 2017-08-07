package com.mabeechen.doordashlite.servicetasks;

import com.mabeechen.doordashlite.servicetasks.models.SearchResult;
import com.mabeechen.doordashlite.servicetasks.models.Token;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
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

    /**
     * Creates a post to get
     * @return The search results
     */
    @POST("/v2/auth/token/")
    Call<Token> getAuthToken(@Body Map<String, String> params);

}
