package com.mabeechen.doordashlite.servicetasks;

import android.content.ContentValues;

import java.util.List;

/**
 * Base class for fetching data
 *
 * @author mabeechen
 * @since 7/17/17
 */
public interface Fetcher {
    /**
     * Fetches data from the service
     *
     * @return true if call was successful, false if not
     */
    boolean fetchData();

    /**
     * Gets the data retrived from the service
     *
     * @return A list of values retrieved from the service
     */
    List<ContentValues> getData();
}
