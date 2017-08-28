package com.taitsmith.daybaker.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.taitsmith.daybaker.R;
import com.taitsmith.daybaker.fragments.StepDetailFragment;

import static com.taitsmith.daybaker.R.id.stepDetailFragment;
import static com.taitsmith.daybaker.activities.StepSummaryActivity.SHARED_PREFS;

public class StepDetailActivity extends AppCompatActivity {
    private JsonObject step;
    private JsonParser parser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_detail);
        parser = new JsonParser();
        StepDetailFragment detailFragment = (StepDetailFragment)
                getSupportFragmentManager().findFragmentByTag("STEP_DETAIL_FRAGMENT");
        FragmentManager manager = getSupportFragmentManager();
        SharedPreferences preferences = getSharedPreferences(SHARED_PREFS, 0);

        if (detailFragment == null) {
            detailFragment = new StepDetailFragment();
        }

        if (getIntent().hasExtra("VIDEO_URL")){
            detailFragment.setDescription(preferences.getString("DESCRIPTION", null));
            detailFragment.setVideoUri(preferences.getString("VIDEO_URL", null));
        } else if (getIntent().hasExtra("RECIPE_NAME")){
            String stepString = getIntent().getStringExtra("RECIPE_NAME");
            step = parser.parse(stepString).getAsJsonObject();
            detailFragment.setVideoUri(step.get(getString(R.string.videoURL)).getAsString());
            detailFragment.setDescription(step.get(getString(R.string.description)).getAsString());
        }
        manager.beginTransaction()
                .replace(stepDetailFragment, detailFragment, "STEP_DETAIL_FRAGMENT")
                .commit();
    }
}
