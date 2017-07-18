package com.mabeechen.doordashlite.providers;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by marbe on 7/16/2017.
 */

public class RestaurantsContentProvider extends ContentProvider {

    public static final String AUTHORITY = "com.mabeechen.doordashlite.restaurants";
    public static Uri BASE_URI = Uri.parse(AUTHORITY);
    public static final String PROPERTIES_PATH = "PROPERTY";
    public static final String LIST_PATH = "SEARCHLIST";
    private static final Uri LIST_URI = Uri.parse("content://" + AUTHORITY + "/"
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
        return null;
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
}
