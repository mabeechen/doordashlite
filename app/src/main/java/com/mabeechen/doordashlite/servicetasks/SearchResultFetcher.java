package com.mabeechen.doordashlite.servicetasks;

import android.content.ContentValues;
import android.util.Log;

import com.mabeechen.doordashlite.servicetasks.models.Address;
import com.mabeechen.doordashlite.servicetasks.models.Business;
import com.mabeechen.doordashlite.servicetasks.models.SearchResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.mabeechen.doordashlite.database.DoorDashDatabase.*;

/**
 * Fetches restaurant search result data from doordash service
 *
 * @author mabeechen
 * @since 7/20/17
 */
public class SearchResultFetcher implements Fetcher {
    private List<ContentValues> mData = new ArrayList<>();

    @Override
    public boolean fetchData() {
        boolean fetchWorked = false;

        try {
            String API_BASE_URL = "https://api.doordash.com/";
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            Retrofit.Builder builder = new Retrofit.Builder().baseUrl(API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());
            Retrofit retrofit = builder.client(httpClient.build()).build();

            DoorDashService client = retrofit.create(DoorDashService.class);
            Response<List<SearchResult>> call = client.getSearchResults().execute();
            if (call.isSuccessful()) {
                Log.d("FetcherWorked", Integer.toString(call.body().size()));
                fetchWorked = true;
                List<SearchResult> results = call.body();
                parseData(results);
            } else {
                //TODO: handle failed state
                fetchWorked = false;
            }
        } catch (RuntimeException ex) {
            Log.d("FetcherFailed", "RuntimeException");
            fetchWorked = false;
        } catch (IOException ex) {
            Log.d("FetcherFailed", "IOException");
            fetchWorked = false;
        }

        return fetchWorked;
    }

    @Override
    public List<ContentValues> getData() {
        return mData;
    }

    /**
     * Parses the data retrieved from the service into an understandable list
     *
     * @param results The parsed list of json values from the service.
     */
    private void parseData(List<SearchResult> results) {
        for (SearchResult result : results) {
            ContentValues values = new ContentValues();
            values.put(RestaurantTableColumns.DELIVERY_FEE, result.getDeliveryFee());
            values.put(RestaurantTableColumns.BUSINESS_ID, result.getBusinessId());
            if (result.getBusiness() != null) {
                Business business = result.getBusiness();
                values.put(RestaurantTableColumns.NAME, business.getName());
            }
            values.put(RestaurantTableColumns.DESCRIPTION, result.getDescription());
            values.put(RestaurantTableColumns.AVG_RATING, result.getYelpRating());
            values.put(RestaurantTableColumns.RATING_COUNT, result.getYelpReviewCount());
            values.put(RestaurantTableColumns.DD_PARTIAL_URL, result.getUrl());
            values.put(RestaurantTableColumns.IMAGE_URL, result.getCoverImgUrl());
            if (result.getAddress() != null) {
                Address address = result.getAddress();
                values.put(RestaurantTableColumns.STREET, address.getStreet());
                values.put(RestaurantTableColumns.CITY, address.getCity());
                values.put(RestaurantTableColumns.STATE, address.getState());
                values.put(RestaurantTableColumns.PRINT_ADDRESS, address.getPrintableAddress());
            }
            values.put(SearchResultsTableColumns.STATUS_TEXT, result.getStatus());
            values.put(SearchResultsTableColumns.ASAP_TIME, result.getAsapTime());
            values.put(SearchResultsTableColumns.STATUS_TYPE, parseStatusType(result.getStatusType()).toInt());

            mData.add(values);
        }
    }

    /**
     * Creates a status type based on the json string value passed in
     *
     * @param statusTypeString The json string representation of statusType
     * @return The status type, unknown if it's not recognized
     */
    private StatusType parseStatusType(String statusTypeString) {
        StatusType retVal = StatusType.UNKNOWN;
        switch (statusTypeString) {
            case "open":
                retVal = StatusType.OPEN;
                break;
            case "pre-order":
                retVal = StatusType.PRE_ORDER;
                break;
            case "unavailable":
                retVal = StatusType.UNAVAILABLE;
                break;
        }
        return retVal;
    }
}
