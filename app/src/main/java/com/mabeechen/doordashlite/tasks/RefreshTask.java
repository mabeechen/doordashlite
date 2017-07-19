package com.mabeechen.doordashlite.tasks;

import android.content.ContentValues;

import java.util.List;

/**
 * Creates a refresh task to retrieve data and write it to the database
 *
 * @author mabeechen
 * @since 7/18/17
 */
public class RefreshTask implements Runnable {

    Fetcher mfetcher;
    DataWriter mWriter;
    List<ContentValues> mData;

    public RefreshTask(Fetcher fetcher, DataWriter writer) {
        mfetcher = fetcher;
        mWriter = writer;
    }

    @Override
    public void run() {
        boolean fetchedSuccessful = mfetcher.fetchData();
        if(!fetchedSuccessful) {
            // TODO: deal with failure, return early
        }
        mData = mfetcher.getData();

        mWriter.priorToUpdate();
        mWriter.update(mData);
        mWriter.afterUpdate();
        //TODO: notify Uri
    }
}
