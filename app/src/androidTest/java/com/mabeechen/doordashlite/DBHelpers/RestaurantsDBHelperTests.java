package com.mabeechen.doordashlite.DBHelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.mabeechen.doordashlite.database.DoorDashDatabase;
import com.mabeechen.doordashlite.dbhelpers.RestaurantsDBHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Tests the RestaurantDBHelper class
 *
 * @author mabeechen
 * @since 7/16/17
 */
@RunWith(AndroidJUnit4.class)
public class RestaurantsDBHelperTests {
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
    }

    @Test
    public void testInsert() throws Exception {
        ContentValues values = new ContentValues();
        values.put(DoorDashDatabase.RestaurantTableColumns.BUSINESS_ID, "12345");
        values.put(DoorDashDatabase.RestaurantTableColumns.NAME, "Marty-Pizza");

        long newId = RestaurantsDBHelper.insertRestaurant(mDatabase, values);
        assert(newId > -1);

        Cursor c = RestaurantsDBHelper.queryAllRestaurants(mDatabase);

        assertEquals(1, c.getCount());
    }

    @Test
    public void testUpdate() throws Exception {
        ContentValues values = new ContentValues();
        values.put(DoorDashDatabase.RestaurantTableColumns.BUSINESS_ID, "123456");
        values.put(DoorDashDatabase.RestaurantTableColumns.NAME, "Marty-Pizza2");

        long newId = RestaurantsDBHelper.insertRestaurant(mDatabase, values);

        ContentValues update = new ContentValues();
        update.put(DoorDashDatabase.RestaurantTableColumns.NAME, "WRONGNAME");
        RestaurantsDBHelper.updateRestaurantOnId(mDatabase, newId, update);

        Cursor results = RestaurantsDBHelper.queryRestaurantOnId(mDatabase, newId);
        if(results != null && results.moveToFirst()) {
            int i = results.getColumnIndex(DoorDashDatabase.RestaurantTableColumns.NAME);
            String returnedName = results.getString(i);
            assertEquals("WRONGNAME", returnedName);
        } else {
            assert(false);
        }
    }

    @Test
    public void testDelete() throws Exception {
        ContentValues values1 = new ContentValues();
        values1.put(DoorDashDatabase.RestaurantTableColumns.BUSINESS_ID, "1234567");
        values1.put(DoorDashDatabase.RestaurantTableColumns.NAME, "Marty-Pizza3");

        ContentValues values2 = new ContentValues();
        values2.put(DoorDashDatabase.RestaurantTableColumns.BUSINESS_ID, "12345678");
        values2.put(DoorDashDatabase.RestaurantTableColumns.NAME, "Marty-Pizza4");

        long newId1 = RestaurantsDBHelper.insertRestaurant(mDatabase, values1);
        assert(newId1 > -1);
        long newId2 = RestaurantsDBHelper.insertRestaurant(mDatabase, values2);
        assert(newId2 > -1);

        int delete1count = RestaurantsDBHelper.deleteRestaurantOnId(mDatabase, newId1);
        assertEquals(1, delete1count);

        int delete2count = RestaurantsDBHelper.deleteRestaurantOnBusinessId(mDatabase, "12345678");
        assertEquals(1, delete2count);
    }
}
