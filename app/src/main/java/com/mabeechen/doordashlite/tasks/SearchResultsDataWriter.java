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
    SQLiteDatabase mDatabase;

    public SearchResultsDataWriter(Context context) {
        DoorDashDatabase db = new DoorDashDatabase(context);
        mDatabase = db.getWritableDatabase();
    }

    @Override
    public void priorToUpdate() {
        SearchResultsDBHelper.markCurrentResultsDirty(mDatabase);
    }

    @Override
    public void update(List<ContentValues> values) {
        Log.d("DataWriter update", Integer.toString(values.size()));
        mDatabase.beginTransaction();
        for (ContentValues searchResult : values) {
            ContentValues restaurantValues = new ContentValues();
            restaurantValues.put(RestaurantTableColumns.DELIVERY_FEE,
                    searchResult.getAsString(RestaurantTableColumns.DELIVERY_FEE));
            String businessId = searchResult.getAsString(RestaurantTableColumns.BUSINESS_ID);
            restaurantValues.put(RestaurantTableColumns.BUSINESS_ID, businessId);
            int updateCount = RestaurantsDBHelper.updateRestaurantOnBusinessId(mDatabase, businessId, restaurantValues);
            long restaurantId;
            if(updateCount == 0) {
                restaurantId = RestaurantsDBHelper.insertRestaurant(mDatabase, restaurantValues);
            } else {
                restaurantId = RestaurantsDBHelper.findRestaurantId(mDatabase, businessId);
            }
            Log.d("AfterInsert", Long.toString(restaurantId));
        }
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();
    }

    @Override
    public void afterUpdate() {
        SearchResultsDBHelper.deleteDirtyResults(mDatabase);
    }
}
