package com.taitsmith.daybaker.activities;

import android.support.test.rule.ActivityTestRule;

import com.taitsmith.daybaker.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.IsAnything.anything;

/**
 * Check that button displays summary for proper recipe and everything is still
 * there when we go back. Size independent.
 */

@RunWith(JUnit4.class)
public class IngredientListTest {
    private static final String RECIPE_INTRODUCTION = "Recipe Introduction";
    private static final String FIRST_INGREDIENT = "Graham Cracker crumbs";
    private static final String LAST_INGREDIENT = "heavy cream";
    private static final String SUMMARY_TEXT = "Here\'s a brief summary of the necessary steps to make " +
            "Cheesecake. Select a step to see it in more detail, or use the arrows to navigate between" +
            " recipes. Some steps will include a video, others will include a very sad looking Android " +
            "instead. Add a widget to your home screen to keep you updated on your current step and " +
            "return to this page.";

    @Rule
    public ActivityTestRule<IngredientSummaryActivity> activityTestRule =
            new ActivityTestRule<>(IngredientSummaryActivity.class);

    @Test
    public void ButtonPress_ShowsStepSummary(){
        onView(withId(R.id.continueButton)).perform(click());

        onData(anything()).inAdapterView(withId(R.id.master_recycler_view))
                .atPosition(0).check(matches(withText(RECIPE_INTRODUCTION)));

        onView(withId(R.id.stepSummaryDescription)).check(matches(withText(SUMMARY_TEXT)));

        pressBack();
    }

    @Test
    public void RecyclerView_ContainsIngredients(){
        onView(allOf(withText(FIRST_INGREDIENT),
                withParent(withId(R.id.list_ingredient_name_tv))));

        onView(allOf(withText(LAST_INGREDIENT),
                withParent(withId(R.id.list_ingredient_name_tv))));
    }
}
