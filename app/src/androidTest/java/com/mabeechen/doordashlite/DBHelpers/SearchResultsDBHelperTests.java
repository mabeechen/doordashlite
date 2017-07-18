package com.mabeechen.doordashlite.DBHelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.mabeechen.doordashlite.database.DoorDashDatabase;
import com.mabeechen.doordashlite.dbhelpers.RestaurantsDBHelper;
import com.mabeechen.doordashlite.dbhelpers.SearchResultsDBHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Tests SearchResultsDBHelper class
 *
 * @author mabeechen
 * @since 7/16/17
 */
@RunWith(AndroidJUnit4.class)
public class SearchResultsDBHelperTests {
    private SQLiteDatabase mDatabase;
    /**
     * Deletes the database before running tests
     */
    @Before
    public void wipeDB() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        appContext.deleteDatabase(DoorDashDatabase.DB_NAME);

        DoorDashDatabase database = new DoorDashDatabase(appContext);
        mDatabase = database.getWritableDatabase();
        insert10Restaurants();
    }

    /**
     * Tests inserting search results
     */
    @Test
    public void testInsertSearchResults() {
        Cursor c = SearchResultsDBHelper.querySearchResults(mDatabase);
        assert (c.getCount() == 0);
        c.close();

        for(int i = 1; i <= 5 ; i++) {
            SearchResultsDBHelper.insertSearchResult(mDatabase, i, DoorDashDatabase.StatusType.OPEN, "Open", 30);
        }
        c = SearchResultsDBHelper.querySearchResults(mDatabase);
        assertEquals(5, c.getCount());
    }

    @Test
    public void testDeleteAll() {
        SearchResultsDBHelper.insertSearchResult(mDatabase, 6, DoorDashDatabase.StatusType.OPEN, "Open", 30);
        SearchResultsDBHelper.deleteAllResults(mDatabase);
        Cursor c = SearchResultsDBHelper.querySearchResults(mDatabase);
        assert (c.getCount() == 0);
        c.close();
    }

    @Test
    public void testMarkDirty() {
        SearchResultsDBHelper.deleteAllResults(mDatabase);
        insert10Results();
        Cursor c = SearchResultsDBHelper.querySearchResults(mDatabase);
        assert (c.getCount() == 10);
        c.close();

        SearchResultsDBHelper.markCurrentResultsDirty(mDatabase);
        c = SearchResultsDBHelper.querySearchResults(mDatabase);
        if(c.moveToFirst()) {
            do {
                boolean isDirty = c.getInt(c.getColumnIndex(DoorDashDatabase.SearchResultsTableColumns.IS_DIRTY)) == 1 ? true : false;
                assert(isDirty);
            } while(c.moveToNext());
        }
        c.close();
    }

    @Test
    public void testDeleteDirtyResults() {
        SearchResultsDBHelper.deleteAllResults(mDatabase);
        insert10Results();
        Cursor c = SearchResultsDBHelper.querySearchResults(mDatabase);
        assert (c.getCount() == 10);
        c.close();

        SearchResultsDBHelper.markCurrentResultsDirty(mDatabase);
        SearchResultsDBHelper.deleteDirtyResults(mDatabase);

        c = SearchResultsDBHelper.querySearchResults(mDatabase);
        assert (c.getCount() == 0);
        c.close();
    }

    @Test
    public void testUpdateResult() {
        SearchResultsDBHelper.deleteAllResults(mDatabase);
        insert10Results();

        SearchResultsDBHelper.updateSearchResultOnRestaurantId(mDatabase, 5, DoorDashDatabase.StatusType.UNAVAILABLE, "Blah", 40);
    }

    @Test
    public void testQuerySingleResult() {
        SearchResultsDBHelper.deleteAllResults(mDatabase);
        insert10Results();

        Cursor c = SearchResultsDBHelper.queryResultOnRestaurantId(mDatabase, 5);
        if(c.moveToFirst()) {
            String name = c.getString(c.getColumnIndex(DoorDashDatabase.RestaurantTableColumns.NAME));
            assertEquals("Name5", name);
        } else {
            assert(false);
        }
    }

    /**
     * Inserts 10 dummy restaurants into the DB
     */
    private void insert10Restaurants() {
        ContentValues restaurantValues = new ContentValues();
        for(int i = 1; i <= 10; i++) {
            restaurantValues.put(DoorDashDatabase.RestaurantTableColumns.NAME, new String("Name" + Integer.toString(i)));
            restaurantValues.put(DoorDashDatabase.RestaurantTableColumns.BUSINESS_ID, i);
            RestaurantsDBHelper.insertRestaurant(mDatabase, restaurantValues);
        }
    }

    /**
     * Inserts 10 dummy restaurants into the DB
     */
    private void insert10Results() {
        ContentValues restaurantValues = new ContentValues();
        for(int i = 1; i <= 10; i++) {
            SearchResultsDBHelper.insertSearchResult(mDatabase, i, DoorDashDatabase.StatusType.OPEN, "Some Text", 30);
        }
    }
}
