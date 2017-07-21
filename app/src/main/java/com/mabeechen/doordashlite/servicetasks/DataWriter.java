package com.mabeechen.doordashlite.servicetasks;

import android.content.ContentValues;

import java.util.List;

/**
 * Interface for writing data to the db
 *
 * @author mabeechen
 * @since 7/17/17
 */

public interface DataWriter {

    /**
     * Executed prior to update
     */
    void priorToUpdate();

    /**
     * Updates data
     */
    void update(List<ContentValues> values);

    /**
     * Runs after update
     */
    void afterUpdate();
}
