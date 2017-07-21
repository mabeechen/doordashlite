package com.mabeechen.doordashlite.tasks;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import  com.mabeechen.doordashlite.database.DoorDashDatabase;
import com.mabeechen.doordashlite.dbhelpers.RestaurantsDBHelper;
import com.mabeechen.doordashlite.dbhelpers.SearchResultsDBHelper;

import java.util.List;

import static com.mabeechen.doordashlite.database.DoorDashDatabase.*;
/**
 * Writes restaurant search result data to the database
 *
 * @author mabeechen
 * @since 7/18/17
 */
public class SearchResultsDataWriter implements DataWriter {
    private SQLiteDatabase mDatabase;
    private long mInsertCount, mUpdateCount;

    /**
     * Constructor
     * @param context The context used to create the writer
     */
    public SearchResultsDataWriter(Context context) {
        DoorDashDatabase db = new DoorDashDatabase(context);
        mDatabase = db.getWritableDatabase();
    }

    @Override
    public void priorToUpdate() {
        // Set current results as dirty
        SearchResultsDBHelper.markCurrentResultsDirty(mDatabase);
        mInsertCount = 0;
        mUpdateCount = 0;
    }

    @Override
    public void update(List<ContentValues> values) {
        mDatabase.beginTransaction();
        for (ContentValues searchResult : values) {

            //prep restaurant values
            ContentValues restaurantValues = new ContentValues();
            restaurantValues.put(RestaurantTableColumns.DELIVERY_FEE,
                    searchResult.getAsString(RestaurantTableColumns.DELIVERY_FEE));
            String businessId = searchResult.getAsString(RestaurantTableColumns.BUSINESS_ID);
            restaurantValues.put(RestaurantTableColumns.BUSINESS_ID, businessId);
            restaurantValues.put(RestaurantTableColumns.NAME, searchResult.getAsString(RestaurantTableColumns.NAME));
            restaurantValues.put(RestaurantTableColumns.DESCRIPTION, searchResult.getAsString(RestaurantTableColumns.DESCRIPTION));
            restaurantValues.put(RestaurantTableColumns.AVG_RATING, searchResult.getAsDouble(RestaurantTableColumns.AVG_RATING));
            restaurantValues.put(RestaurantTableColumns.RATING_COUNT, searchResult.getAsInteger(RestaurantTableColumns.RATING_COUNT));
            restaurantValues.put(RestaurantTableColumns.DD_PARTIAL_URL, searchResult.getAsString(RestaurantTableColumns.DD_PARTIAL_URL));
            restaurantValues.put(RestaurantTableColumns.IMAGE_URL, searchResult.getAsString(RestaurantTableColumns.IMAGE_URL));
            restaurantValues.put(RestaurantTableColumns.STREET, searchResult.getAsString(RestaurantTableColumns.STREET));
            restaurantValues.put(RestaurantTableColumns.CITY, searchResult.getAsString(RestaurantTableColumns.CITY));
            restaurantValues.put(RestaurantTableColumns.STATE, searchResult.getAsString(RestaurantTableColumns.STATE));
            restaurantValues.put(RestaurantTableColumns.PRINT_ADDRESS, searchResult.getAsString(RestaurantTableColumns.PRINT_ADDRESS));

            // Update or insert restaurant data
            int updateCount = RestaurantsDBHelper.updateRestaurantOnBusinessId(mDatabase, businessId, restaurantValues);
            mUpdateCount += updateCount;
            long restaurantId;
            if(updateCount == 0) {
                restaurantId = RestaurantsDBHelper.insertRestaurant(mDatabase, restaurantValues);
                mInsertCount++;
            } else {
                restaurantId = RestaurantsDBHelper.findRestaurantId(mDatabase, businessId);
            }

            //prep search result data
            StatusType statusType = StatusType.values()[searchResult.getAsInteger(SearchResultsTableColumns.STATUS_TYPE)];
            String statusText = searchResult.getAsString(SearchResultsTableColumns.STATUS_TEXT);
            Integer asapTime = searchResult.getAsInteger(SearchResultsTableColumns.ASAP_TIME);
            int time = asapTime == null? 0 : asapTime.intValue();

            // Update or insert search result
            int updatedSearchResultCount = SearchResultsDBHelper.updateSearchResultOnRestaurantId(mDatabase, restaurantId, statusType, statusText, time);
            mUpdateCount += updatedSearchResultCount;
            if(updatedSearchResultCount == 0) {
                SearchResultsDBHelper.insertSearchResult(mDatabase,restaurantId, statusType, statusText, time);
                mInsertCount++;
            }
        }
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();
    }

    @Override
    public void afterUpdate() {
        // delete dirty results from db
        SearchResultsDBHelper.deleteDirtyResults(mDatabase);
        Log.d("Data Writer Complete", "Inserted: " + Long.toString(mInsertCount) + ", Updated: " + Long.toString(mUpdateCount));
    }
}
