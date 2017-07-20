package com.mabeechen.doordashlite.providers;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.mabeechen.doordashlite.dbhelpers.SearchResultsDBHelper;
import com.mabeechen.doordashlite.database.DoorDashDatabase;
import com.mabeechen.doordashlite.tasks.RefreshState;
import com.mabeechen.doordashlite.tasks.RefreshTask;
import com.mabeechen.doordashlite.tasks.SearchResultFetcher;
import com.mabeechen.doordashlite.tasks.SearchResultsDataWriter;
import com.mabeechen.doordashlite.tasks.State;

/**
 * Created by marbe on 7/16/2017.
 */

public class RestaurantsContentProvider extends ContentProvider {

    public static final String AUTHORITY = "com.mabeechen.doordashlite.restaurants";
    public static Uri BASE_URI = Uri.parse(AUTHORITY);
    public static final String PROPERTIES_PATH = "PROPERTY";
    public static final String LIST_PATH = "SEARCHLIST";
    public static final Uri LIST_URI = Uri.parse("content://" + AUTHORITY + "/"
            + LIST_PATH);

    /**
     * Gets a restaurant search results list uri
     *
     * @return The uri
     */
    public static Uri getSearchListUri() {
        return LIST_URI;
    }

    /**
     * Gets a uri for a single restaurant
     *
     * @param id The id of the restaurant
     *
     * @return The restaurant uri
     */
    public static Uri getSingleRestaurantUri(long id) {
        return BASE_URI.buildUpon().appendEncodedPath(Long.toString(id)).appendEncodedPath(PROPERTIES_PATH).build();
    }

    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        // Check if we need a refresh
        if(RefreshState.getInstance().needsRefresh()) {
            // schedule if so
            Log.d("Refresh State", "Refresh Required");
            scheduleRefreshTask();
        }
        DoorDashDatabase data = new DoorDashDatabase(getContext());
        Cursor c = SearchResultsDBHelper.querySearchResults(data.getWritableDatabase());
        //c.setNotificationUri(getContext().getContentResolver(), LIST_URI);
        return c;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    private void scheduleRefreshTask() {
        SearchResultsDataWriter writer = new SearchResultsDataWriter(getContext());
        SearchResultFetcher fetcher = new SearchResultFetcher();
        RefreshTask task = new RefreshTask(getContext(), fetcher, writer);
        new Thread(task).start();
    }
}
