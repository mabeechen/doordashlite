package com.mabeechen.doordashlite.servicetasks;

import java.util.Date;

/**
 * Controls refresh state for the main list of results
 *
 * NOTE ABOUT THIS APPROACH: Typically I'd do this via a database table values for each item I want to control
 * refresh behavior for.  Since this project only requires that I manage that for a single list of search
 * results I decided to keep my life simple and manage it through a singleton class.
 * A little ghetto?  Totally.
 * But does it work?  Of course.
 *
 * @author mabeechen
 * @since 7/19/17
 */
public class RefreshState {
    private static final RefreshState ourInstance = new RefreshState();
    private State mState = State.OutOfDate;
    private Date mDate = new Date();
    private long timeOutInMillis = 60000;

    public static RefreshState getInstance() {
        return ourInstance;
    }

    /**
     * Constructor
     */
    private RefreshState() {
    }

    /**
     * Indicates whether a refresh is required.
     *
     * @return true is a refresh is needed, false otherwise
     */
    public boolean needsRefresh () {
        synchronized (this) {
            Date currentDateTime = new Date();
            Date expiredDateTime = new Date(mDate.getTime() + timeOutInMillis);
            if((mState != State.RefreshComplete || expiredDateTime.before(currentDateTime)) && mState != State.Refreshing) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Get current state
     *
     * @return The current state
     */
    public State getCurrentState() {
        synchronized (this) {
            return mState;
        }
    }

    /**
     * Updates the state
     *
     * @param newState The new state
     */
    public void setCurrentState(State newState) {
        synchronized (this) {
            mState = newState;
            mDate = new Date();
        }
    }
}

