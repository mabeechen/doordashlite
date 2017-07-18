package com.mabeechen.doordashlite.dbhelpers;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mabeechen.doordashlite.database.DoorDashDatabase;

/**
 *  Provides create, read, update, and delete functionality related to the search results table
 *
 *  @author mabeechen
 *  @since 7/16/17
 */
public class SearchResultsDBHelper {

    public static final String SEARCH_RESULTS_RESTAURANT_LEFT_OUTER_JOIN = DoorDashDatabase.SEARCH_RESULTS_TABLE_NAME + " LEFT OUTER JOIN "
            + DoorDashDatabase.RESTAURANT_TABLE_NAME + " ON "
            + DoorDashDatabase.SEARCH_RESULTS_TABLE_NAME + "." + DoorDashDatabase.SearchResultsTableColumns.RESTAURANT_ID
            + " = " + DoorDashDatabase.RESTAURANT_TABLE_NAME + "." + DoorDashDatabase.RestaurantTableColumns._ID;

    /**
     * Deletes contents of search results table.  Not sure I'll need this.
     *
     * @param db The db to use
     *
     * @return The number of deleted rows
     */
    public static int deleteAllResults(SQLiteDatabase db) {
        return db.delete(DoorDashDatabase.SEARCH_RESULTS_TABLE_NAME, null, null);
    }

    /**
     * Deletes search results in a dirty state.
     *
     * @param db The db to use
     *
     * @return The number of deleted rows
     */
    public static int deleteDirtyResults(SQLiteDatabase db) {
        String where = DoorDashDatabase.SearchResultsTableColumns.IS_DIRTY + "=1";
        return db.delete(DoorDashDatabase.SEARCH_RESULTS_TABLE_NAME, where, null);
    }

    /**
     * Marks all current results as dirty
     *
     * @param db The database to use
     *
     * @return The number of updated rows
     */
    public static int markCurrentResultsDirty(SQLiteDatabase db) {
        ContentValues updateValues = new ContentValues();
        updateValues.put(DoorDashDatabase.SearchResultsTableColumns.IS_DIRTY, true);

        return db.update(DoorDashDatabase.SEARCH_RESULTS_TABLE_NAME, updateValues, null, null);
    }

    /**
     * Inserts a new record into search results
     *
     * @param db the database to use
     * @param restaurantId The restaurant Id to update a record for
     * @param statusType The current status of the restaurant
     * @param statusText The status text
     * @param asapTime The time to delivery
     *
     * @return the newly inserted record
     */
    public static long insertSearchResult(SQLiteDatabase db,
                                          long restaurantId,
                                          DoorDashDatabase.StatusType statusType,
                                          String statusText,
                                          int asapTime) {
        ContentValues values = new ContentValues();
        values.put(DoorDashDatabase.SearchResultsTableColumns.RESTAURANT_ID, restaurantId);
        values.put(DoorDashDatabase.SearchResultsTableColumns.STATUS_TYPE, statusType.toInt());
        values.put(DoorDashDatabase.SearchResultsTableColumns.STATUS_TEXT, statusText);
        values.put(DoorDashDatabase.SearchResultsTableColumns.ASAP_TIME, asapTime);
        values.put(DoorDashDatabase.SearchResultsTableColumns.IS_DIRTY, false);

        return db.insert(DoorDashDatabase.SEARCH_RESULTS_TABLE_NAME, null, values);
    }

    /**
     * Updates a record in the search results based on restaurantId
     *
     * @param db The database to use
     * @param restaurantId The restaurant Id to look up
     * @param statusType The current status of the restaurant
     * @param statusText The status text
     * @param asapTime The time to delivery
     *
     * @return The number of updated rows.
     */
    public static int updateSearchResultOnRestaurantId(SQLiteDatabase db,
                                         long restaurantId,
                                         DoorDashDatabase.StatusType statusType,
                                         String statusText,
                                         int asapTime) {
        ContentValues values = new ContentValues();
        values.put(DoorDashDatabase.SearchResultsTableColumns.STATUS_TYPE, statusType.toInt());
        values.put(DoorDashDatabase.SearchResultsTableColumns.STATUS_TEXT, statusText);
        values.put(DoorDashDatabase.SearchResultsTableColumns.ASAP_TIME, asapTime);
        values.put(DoorDashDatabase.SearchResultsTableColumns.IS_DIRTY, false);

        String where = DoorDashDatabase.SearchResultsTableColumns.RESTAURANT_ID + "=?";
        String[] whereArgs = {Long.toString(restaurantId)};
        return db.update(DoorDashDatabase.SEARCH_RESULTS_TABLE_NAME, values, where, whereArgs);
    }

    /**
     * Returns values for a single search result associated with the passed in restaurantId
     *
     * @param db The database
     * @param restaurantId The restaurant ID to look up the value for
     *
     * @return A cursor with the values
     */
    public static Cursor queryResultOnRestaurantId(SQLiteDatabase db, long restaurantId) {
        String selection = DoorDashDatabase.SearchResultsTableColumns.RESTAURANT_ID + "=?";
        String[] selectArgs = {Long.toString(restaurantId)};

        return db.query(SEARCH_RESULTS_RESTAURANT_LEFT_OUTER_JOIN, null, selection, selectArgs, null, null, null, "1");
    }

    /**
     * Queries for all search results in the db
     *
     * @param db The database to use
     *
     * @return A Cursor containing all the search results
     */
    public static Cursor querySearchResults(SQLiteDatabase db) {
        // don't return restaurants with unknown status or that are unavailable
        String select = DoorDashDatabase.SearchResultsTableColumns.STATUS_TYPE + "!= ? AND "
                + DoorDashDatabase.SearchResultsTableColumns.STATUS_TYPE + "!=? ";
        String[] selectArgs = {Integer.toString(DoorDashDatabase.StatusType.UNAVAILABLE.toInt()),
                Integer.toString(DoorDashDatabase.StatusType.UNKNOWN.toInt())};

        String orderBy = DoorDashDatabase.SearchResultsTableColumns.ASAP_TIME + " ASC";

        return db.query(SEARCH_RESULTS_RESTAURANT_LEFT_OUTER_JOIN, null, select, selectArgs, null, null, orderBy, null);
    }
}
