package com.mabeechen.doordashlite.tasks;

import android.content.ContentValues;
import android.util.Log;

import com.mabeechen.doordashlite.database.DoorDashDatabase;
import com.mabeechen.doordashlite.tasks.models.SearchResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by marbe on 7/18/2017.
 */
public class SearchResultFetcher implements Fetcher {
    private List<ContentValues> mData = new ArrayList<>();

    @Override
    public boolean fetchData() {
        boolean fetchWorked = false;

        try {
            String API_BASE_URL = "https://api.doordash.com/";

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

            Retrofit.Builder builder =
                    new Retrofit.Builder()
                            .baseUrl(API_BASE_URL)
                            .addConverterFactory(
                                    GsonConverterFactory.create()
                            );

            Retrofit retrofit =
                    builder
                            .client(
                                    httpClient.build()
                            )
                            .build();

            DoorDashService client = retrofit.create(DoorDashService.class);
            Response<List<SearchResult>> call = client.getSearchResults().execute();
            if(call.isSuccessful()) {
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
            Log.d("Failed Message", ex.getMessage());
            fetchWorked = false;
        } catch (IOException ex) {
            Log.d("FetcherFailed", "IOException");
            fetchWorked = false;
        } finally {

        }

        return fetchWorked;
    }

    @Override
    public List<ContentValues> getData() {
        return mData;
    }

    private void parseData(List<SearchResult> results) {
        // TODO: parse the data
        for(SearchResult result : results) {
            ContentValues values = new ContentValues();
            values.put(DoorDashDatabase.RestaurantTableColumns.DELIVERY_FEE, result.getDeliveryFee());
            values.put(DoorDashDatabase.RestaurantTableColumns.BUSINESS_ID, result.getBusinessId());
            values.put(DoorDashDatabase.RestaurantTableColumns.NAME, result.getName());
            mData.add(values);
        }
    }
}
