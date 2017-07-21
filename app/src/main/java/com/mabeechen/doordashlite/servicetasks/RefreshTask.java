package com.mabeechen.doordashlite.servicetasks;

import android.content.ContentValues;
import android.content.Context;

import com.mabeechen.doordashlite.providers.RestaurantsContentProvider;

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
    Context mContext;

    public RefreshTask(Context context, Fetcher fetcher, DataWriter writer) {
        mfetcher = fetcher;
        mWriter = writer;
        mContext = context;
    }

    @Override
    public void run() {
        RefreshState.getInstance().setCurrentState(State.Refreshing);
        boolean fetchedSuccessful = mfetcher.fetchData();
        if(!fetchedSuccessful) {
            // TODO: deal with failure, return early
            RefreshState.getInstance().setCurrentState(State.RefreshFailed);
        }
        mData = mfetcher.getData();

        mWriter.priorToUpdate();
        mWriter.update(mData);
        mWriter.afterUpdate();
        RefreshState.getInstance().setCurrentState(State.RefreshComplete);
        //TODO: notify Uri
        mContext.getContentResolver().notifyChange(RestaurantsContentProvider.getSearchListUri(), null);

    }
}
