package com.mabeechen.doordashlite.dbhelpers;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import static com.mabeechen.doordashlite.database.DoorDashDatabase.*;

/**
 *  Provides create, read, update, and delete functionality related to the search results table
 *
 *  @author mabeechen
 *  @since 7/16/17
 */
public class SearchResultsDBHelper {

    public static final String SEARCH_RESULTS_RESTAURANT_LEFT_OUTER_JOIN = SEARCH_RESULTS_TABLE_NAME + " LEFT OUTER JOIN "
            + RESTAURANT_TABLE_NAME + " ON "
            + SEARCH_RESULTS_TABLE_NAME + "." + SearchResultsTableColumns.RESTAURANT_ID
            + " = " + RESTAURANT_TABLE_NAME + "." + RestaurantTableColumns._ID;

    /**
     * Deletes contents of search results table.  Not sure I'll need this.
     *
     * @param db The db to use
     *
     * @return The number of deleted rows
     */
    public static int deleteAllResults(SQLiteDatabase db) {
        return db.delete(SEARCH_RESULTS_TABLE_NAME, null, null);
    }

    /**
     * Deletes search results in a dirty state.
     *
     * @param db The db to use
     *
     * @return The number of deleted rows
     */
    public static int deleteDirtyResults(SQLiteDatabase db) {
        String where = SearchResultsTableColumns.IS_DIRTY + "=1";
        return db.delete(SEARCH_RESULTS_TABLE_NAME, where, null);
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
        updateValues.put(SearchResultsTableColumns.IS_DIRTY, true);

        return db.update(SEARCH_RESULTS_TABLE_NAME, updateValues, null, null);
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
                                          StatusType statusType,
                                          String statusText,
                                          int asapTime) {
        ContentValues values = new ContentValues();
        values.put(SearchResultsTableColumns.RESTAURANT_ID, restaurantId);
        values.put(SearchResultsTableColumns.STATUS_TYPE, statusType.toInt());
        values.put(SearchResultsTableColumns.STATUS_TEXT, statusText);
        values.put(SearchResultsTableColumns.ASAP_TIME, asapTime);
        values.put(SearchResultsTableColumns.IS_DIRTY, false);

        return db.insert(SEARCH_RESULTS_TABLE_NAME, null, values);
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
                                         StatusType statusType,
                                         String statusText,
                                         int asapTime) {
        ContentValues values = new ContentValues();
        values.put(SearchResultsTableColumns.STATUS_TYPE, statusType.toInt());
        values.put(SearchResultsTableColumns.STATUS_TEXT, statusText);
        values.put(SearchResultsTableColumns.ASAP_TIME, asapTime);
        values.put(SearchResultsTableColumns.IS_DIRTY, false);

        String where = SearchResultsTableColumns.RESTAURANT_ID + "=?";
        String[] whereArgs = {Long.toString(restaurantId)};
        return db.update(SEARCH_RESULTS_TABLE_NAME, values, where, whereArgs);
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
        String selection = SearchResultsTableColumns.RESTAURANT_ID + "=?";
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
        String select = SearchResultsTableColumns.STATUS_TYPE + "!= ? AND "
                + SearchResultsTableColumns.STATUS_TYPE + "!=? ";
        String[] selectArgs = {Integer.toString(StatusType.UNAVAILABLE.toInt()),
                Integer.toString(StatusType.UNKNOWN.toInt())};

        String orderBy = RestaurantTableColumns.IS_FAVORITE + " DESC, "
                + SearchResultsTableColumns.STATUS_TYPE + " DESC, "
                + SearchResultsTableColumns.ASAP_TIME + " ASC";

        return db.query(SEARCH_RESULTS_RESTAURANT_LEFT_OUTER_JOIN, null, select, selectArgs, null, null, orderBy, null);
    }
}
