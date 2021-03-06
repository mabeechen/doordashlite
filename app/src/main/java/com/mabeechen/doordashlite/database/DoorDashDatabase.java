package com.mabeechen.doordashlite.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

/**
 * Manages the DoorDashLite database
 *
 * @author mabeechen
 * @since 7/15/2017
 */
public class DoorDashDatabase extends SQLiteOpenHelper {
    // DB Info
    public static final String DB_NAME = "doordashdata.db";
    public static final int DB_VERSION = 1;

    // SQL constants
    public static final String SQL_TYPE_INTEGER = "INTEGER";
    public static final String SQL_TYPE_BOOLEAN = "BOOLEAN";
    public static final String SQL_TYPE_TEXT = "TEXT";
    public static final String SQL_TYPE_REAL = "REAL";

    // Table names
    public static final String RESTAURANT_TABLE_NAME = "restaurants";
    public static final String SEARCH_RESULTS_TABLE_NAME = "search_results";

    /**
     * Status for restaurant search results
     */
    public enum StatusType {
        UNKNOWN(0),
        UNAVAILABLE(1),
        PRE_ORDER (2),
        OPEN(3);
        private int mValue;

        private StatusType(int value) {
            this.mValue = value;
        }

        public int toInt() {
            return mValue;
        }
    }
    /**
     * Constructor
     *
     * @param context The context to use for the db
     */
    public DoorDashDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys = ON;");

        createTable(db, RESTAURANT_TABLE_NAME, RESTAURANTS_COLUMN_SQL);
        createTable(db, SEARCH_RESULTS_TABLE_NAME, SEARCH_RESULTS_COLUMN_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            // upgrade the database
        }
    }

    /**
     * Creates a table in the database
     *
     * @param db The database to use
     * @param tableName The table name to create
     * @param additionalColumns Additional columns beyond the _id column to add to the table in proper form
     */
    public static void createTable(SQLiteDatabase db, String tableName, String additionalColumns) {
        String SqlToExecute = "CREATE TABLE IF NOT EXISTS "
                + tableName + " ( " + CommonTableColumns._ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT "
                + (TextUtils.isEmpty(additionalColumns) ? "" : ", " + additionalColumns) + " );";
        db.execSQL(SqlToExecute);
    }

    /**
     * Common columns for tables
     */
    public static class CommonTableColumns {
        public static final String _ID = "_id";
    }

    /**
     * Columns to hold restaurant data
     */
    public static final class RestaurantTableColumns extends CommonTableColumns {
        public static final String BUSINESS_ID = "business_id";
        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
        public static final String AVG_RATING = "avg_rating";
        public static final String RATING_COUNT = "rating_count";
        public static final String DD_PARTIAL_URL = "url";
        public static final String DELIVERY_FEE = "delivery_fee";
        public static final String IMAGE_URL = "image_url";
        public static final String STREET = "street";
        public static final String CITY = "city";
        public static final String STATE = "state";
        public static final String PRINT_ADDRESS = "print_address";
        public static final String IS_FAVORITE = "is_favorite";
    }

    /**
     * Columns to hold values for a given search
     */
    public static final class SearchResultsTableColumns extends CommonTableColumns {
        public static final String RESTAURANT_ID = "restaurant_id";
        public static final String STATUS_TYPE = "status_type";
        public static final String STATUS_TEXT = "status_text";
        public static final String IS_DIRTY = "is_dirty";
        public static final String ASAP_TIME = "asap_time";
    }

    /**
     * SQL columns definition for restaurants table.
     */
    private static final String RESTAURANTS_COLUMN_SQL = RestaurantTableColumns.BUSINESS_ID + " " + SQL_TYPE_INTEGER + ", "
            + RestaurantTableColumns.NAME + " " + SQL_TYPE_TEXT + ", "
            + RestaurantTableColumns.DESCRIPTION + " " + SQL_TYPE_TEXT + ","
            + RestaurantTableColumns.AVG_RATING + " " + SQL_TYPE_REAL + ", "
            + RestaurantTableColumns.RATING_COUNT + " " + SQL_TYPE_TEXT + ", "
            + RestaurantTableColumns.DD_PARTIAL_URL + " " + SQL_TYPE_TEXT + ","
            + RestaurantTableColumns.DELIVERY_FEE + " " + SQL_TYPE_INTEGER + ", "
            + RestaurantTableColumns.IMAGE_URL + " " + SQL_TYPE_TEXT + ", "
            + RestaurantTableColumns.STREET + " " + SQL_TYPE_TEXT + ","
            + RestaurantTableColumns.CITY + " " + SQL_TYPE_INTEGER + ", "
            + RestaurantTableColumns.STATE + " " + SQL_TYPE_TEXT + ", "
            + RestaurantTableColumns.PRINT_ADDRESS + " " + SQL_TYPE_TEXT + ", "
            + RestaurantTableColumns.IS_FAVORITE + " " + SQL_TYPE_BOOLEAN;

    /**
     * SQL columns definition for search result table.
     */
    private static final String SEARCH_RESULTS_COLUMN_SQL = SearchResultsTableColumns.RESTAURANT_ID + " " + SQL_TYPE_INTEGER + ", "
            + SearchResultsTableColumns.STATUS_TYPE + " " + SQL_TYPE_TEXT + ", "
            + SearchResultsTableColumns.STATUS_TEXT + " " + SQL_TYPE_INTEGER + ", "
            + SearchResultsTableColumns.IS_DIRTY + " " + SQL_TYPE_BOOLEAN + ", "
            + SearchResultsTableColumns.ASAP_TIME + " " + SQL_TYPE_INTEGER + ", "
            + "FOREIGN KEY(" + SearchResultsTableColumns.RESTAURANT_ID + ") REFERENCES "
            + RESTAURANT_TABLE_NAME + "(" + RestaurantTableColumns._ID + ") ON DELETE CASCADE";
}
