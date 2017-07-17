package com.mabeechen.doordashlite.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.action.MotionEvents;
import android.support.test.runner.AndroidJUnit4;

import com.mabeechen.doordashlite.database.DoorDashDatabase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Tests database creation
 *
 */
@RunWith(AndroidJUnit4.class)
public class DoorDashDatabaseTests {

    /**
     * Deletes the database before running tests
     */
    @Before
    public void wipeDB() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        appContext.deleteDatabase(DoorDashDatabase.DB_NAME);
    }

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.mabeechen.doordashlite", appContext.getPackageName());
    }

    @Test
    public void TestDatabaseCreation() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        DoorDashDatabase db = new DoorDashDatabase(appContext);

        String queryTableSql = "SELECT name FROM sqlite_master WHERE type='table' AND name=?";
        String[] selectArgs1 = {DoorDashDatabase.RESTAURANT_TABLE_NAME};
        //Cursor c = db.getReadableDatabase().query(DoorDashDatabase.RESTAURANT_TABLE_NAME, null, null, null, null, null, null);
        Cursor c = db.getReadableDatabase().rawQuery(queryTableSql, selectArgs1);
        assertEquals(c.getCount(), 1);

        String[] selectArgs2 = {DoorDashDatabase.SEARCH_RESULTS_TABLE_NAME};
        //Cursor c = db.getReadableDatabase().query(DoorDashDatabase.RESTAURANT_TABLE_NAME, null, null, null, null, null, null);
        c = db.getReadableDatabase().rawQuery(queryTableSql, selectArgs2);
        assertEquals(c.getCount(), 1);
    }
}
