package com.mabeechen.doordashlite.servicetasks;

import org.junit.Before;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.test.InstrumentationRegistry;

import com.mabeechen.doordashlite.R;
import com.mabeechen.doordashlite.database.DoorDashDatabase;

/**
 * Created by Martin on 8/4/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class RefreshTaskTest {

    private static final String FAKE_STRING = "MOCKS WORK";

    @InjectMocks
    private Context mMockContext;

    /**
     * Deletes the database before running tests
     */
    @Before
    public void setup() {
        //mMockContext = InstrumentationRegistry.getTargetContext();
        //MockitoAnnotations.initMocks(this);
    }
    /**
     * Stupid simple test to verify that mocks are working
     */
    @Test
    public void readStringFromContext_LocalizedString() {
        // Given a mocked Context injected into the object under test...
        when(mMockContext.getString(R.string.discover))
                .thenReturn(FAKE_STRING);

        Context context = mMockContext;
        String returnedString = mMockContext.getString(R.string.discover);

        // ...then the result should be the expected one.
        assertThat(returnedString, is(FAKE_STRING));
    }
}
