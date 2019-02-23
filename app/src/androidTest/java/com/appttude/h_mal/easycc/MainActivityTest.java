package com.appttude.h_mal.easycc;

import android.support.test.rule.ActivityTestRule;
import android.view.View;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> activityActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    private MainActivity mainActivity = null;

    @Before
    public void setUp() throws Exception {
        mainActivity = activityActivityTestRule.getActivity();
    }

    @Test
    public void testViews(){
        View view = mainActivity.findViewById(R.id.editTextSomething);

        assertNotNull(view);
    }

    @After
    public void TearDown() throws Exception{
        mainActivity = null;
    }
}
