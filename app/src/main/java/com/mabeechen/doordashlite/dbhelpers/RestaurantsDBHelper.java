package com.mabeechen.doordashlite.dbhelpers;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mabeechen.doordashlite.database.DoorDashDatabase;

/**
 *  Provides create, read, update, and delete functionality related to the restaurants table
 *
 *  @author mabeechen
 *  @since 7/16/17
 */
public class RestaurantsDBHelper {

    /**
     * Inserts a restaurant into the database
     *
     * @param db The database to use
     * @param values The values to insert
     *
     * @return The ID of the inserted row, -1 if failed.
     */
    public static long insertRestaurant(SQLiteDatabase db, ContentValues values) {
        return db.insert(DoorDashDatabase.RESTAURANT_TABLE_NAME, null, values);
    }

    /**
     * Queries for the restaurant values based on a given business ID
     *
     * @param db The database to use
     * @param businessId The business ID to look up
     *
     * @return The values for the given restaurant
     */
    public static Cursor queryRestaurantonBusinessId(SQLiteDatabase db, String businessId) {
        String selection = DoorDashDatabase.RestaurantTableColumns.BUSINESS_ID + "=?";
        String[] selectArgs = {businessId};

        return db.query(DoorDashDatabase.RESTAURANT_TABLE_NAME, null, selection, selectArgs, null, null, null);
    }

    /**
     * Queries for the restaurant values based on a given database ID
     *
     * @param db The database to use
     * @param restaurantId The restaurant ID to look up
     *
     * @return The values for the given restaurant
     */
    public static Cursor queryRestaurantOnId(SQLiteDatabase db, long restaurantId) {
        String selection = DoorDashDatabase.RestaurantTableColumns._ID + "=?";
        String[] selectArgs = {Long.toString(restaurantId)};

        return db.query(DoorDashDatabase.RESTAURANT_TABLE_NAME, null, selection, selectArgs, null, null, null);
    }

    /**
     * Deletes the restaurant values for the given business ID
     *
     * @param db The database to use
     * @param businessId The business ID to delete values for
     *
     * @return The number of deleted rows
     */
    public static int deleteRestaurantOnBusinessId(SQLiteDatabase db, String businessId) {
        String where = DoorDashDatabase.RestaurantTableColumns.BUSINESS_ID + "=?";
        String[] whereArgs = {businessId};

        return db.delete(DoorDashDatabase.RESTAURANT_TABLE_NAME, where, whereArgs);
    }

    /**
     * Deletes the restaurant values for the given restaurant db ID
     *
     * @param db The database to use
     * @param restuarantId The db ID to delete values for
     *
     * @return The number of deleted rows
     */
    public static int deleteRestaurantOnId(SQLiteDatabase db, long restuarantId) {
        String where = DoorDashDatabase.RestaurantTableColumns._ID + "=?";
        String[] whereArgs = {Long.toString(restuarantId)};

        return db.delete(DoorDashDatabase.RESTAURANT_TABLE_NAME, where, whereArgs);
    }

    /**
     * Updates the values for a restaurant
     *
     * @param db The database to use
     * @param restaurantId The DB Id of the restaurant to update
     * @param values The values to update
     *
     * @return The number of updated rows
     */
    public static int updateRestaurantOnId(SQLiteDatabase db, long restaurantId, ContentValues values) {
        String selection = DoorDashDatabase.RestaurantTableColumns._ID + "=?";
        String[] selectArgs = {Long.toString(restaurantId)};

        return db.update(DoorDashDatabase.RESTAURANT_TABLE_NAME, values, selection, selectArgs);
    }

    /**
     * Returns a cursor with data for all restaurants in the db
     *
     * @param db The database to use
     *
     * @return A cursor with all restaurant data
     */
    public static Cursor queryAllRestaurants(SQLiteDatabase db) {
        return db.query(DoorDashDatabase.RESTAURANT_TABLE_NAME, null, null, null, null, null, null);
    }
}
