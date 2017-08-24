package com.taitsmith.daybaker.activities;

import android.support.test.rule.ActivityTestRule;

import com.taitsmith.daybaker.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.IsAnything.anything;

/**
 * Test for tablet + xl size devices to ensure fragments are displayed properly
 * when selected. Won't work on anything smaller since layouts are different.
 */

@RunWith(JUnit4.class)
public class TabletRecipeSummaryTest {
    private static final String RECIPE_SUMMARY_INTRODUCTION = "Recipe Introduction";

    @Rule
    public ActivityTestRule<StepSummaryActivity> stepSummaryActivityActivityTestRule =
            new ActivityTestRule<>(StepSummaryActivity.class);

    @Test
    public void StepClick_DisplaysProperStep(){
        onData(anything()).inAdapterView(withId(R.id.master_recycler_view))
                .atPosition(0).perform(click());

        onView(withId(R.id.step_fragment_name_view)).check(matches(withText(RECIPE_SUMMARY_INTRODUCTION)));

        onData(anything()).inAdapterView(withId(R.id.master_recycler_view))
                .atPosition(4).perform(click());

        onView(withId(R.id.noVideoImageView)).check(matches(isDisplayed()));
    }
}
