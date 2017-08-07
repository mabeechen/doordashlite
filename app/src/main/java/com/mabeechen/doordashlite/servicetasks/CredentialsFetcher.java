package com.mabeechen.doordashlite.servicetasks;

import android.content.ContentValues;
import android.util.Log;

import com.mabeechen.doordashlite.servicetasks.models.SearchResult;
import com.mabeechen.doordashlite.servicetasks.models.Token;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Fetches credentials for current user
 *
 * @author mabeechen
 * @since 8-4-17
 */
public class CredentialsFetcher implements Fetcher {

    private String mUserName, mPassword;
    private String returnedToken;

    public CredentialsFetcher(String username, String password) {
        mUserName = username;
        mPassword = password;
    }

    @Override
    public boolean fetchData() {
        String API_BASE_URL = "https://api.doordash.com/";
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        Retrofit.Builder builder = new Retrofit.Builder().baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.client(httpClient.build()).build();

        DoorDashService client = retrofit.create(DoorDashService.class);
        HashMap<String, String> queryMap = new HashMap<String, String>();
        queryMap.put("email", mUserName);
        queryMap.put("password", mPassword);

        boolean fetchWorked = false;
        try {
            Response<Token> call = client.getAuthToken(queryMap).execute();
            Token token = call.body();
            returnedToken = token.getToken();
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
        ContentValues tokenValues = new ContentValues();
        tokenValues.put("token", returnedToken);
        List<ContentValues> list = new ArrayList<>();
        list.add(tokenValues);

        return list;
    }
}
