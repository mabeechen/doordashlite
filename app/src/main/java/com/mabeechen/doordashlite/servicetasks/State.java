package com.mabeechen.doordashlite.servicetasks;

/**
 * Used to indicate the refresh state of something we need to retrieve service-based values for.
 */
public enum State {
    OutOfDate(0),
    Refreshing(1),
    RefreshFailed(2),
    RefreshComplete(3);

    private int mValue;
    private State(int val) {
        mValue = val;
    }

    public int toInt() {
        return mValue;
    }
}
