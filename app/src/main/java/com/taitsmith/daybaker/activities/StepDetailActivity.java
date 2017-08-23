package com.taitsmith.daybaker.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.taitsmith.daybaker.R;
import com.taitsmith.daybaker.data.StepWidget;
import com.taitsmith.daybaker.fragments.StepDetailFragment;

public class StepDetailActivity extends AppCompatActivity {
    private JsonObject step;
    private JsonParser parser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_detail);
        parser = new JsonParser();
        StepDetailFragment detailFragment = new StepDetailFragment();
        FragmentManager manager = getSupportFragmentManager();




        if (getIntent().hasExtra("VIDEO_URL")){
            detailFragment.setDescription(StepWidget.stepDescription);
            detailFragment.setVideoUri(StepWidget.videoUrl);
        } else if (getIntent().hasExtra("RECIPE_NAME")){
            String stepString = getIntent().getStringExtra("RECIPE_NAME");
            step = parser.parse(stepString).getAsJsonObject();
            detailFragment.setVideoUri(step.get(getString(R.string.videoURL)).getAsString());
            detailFragment.setDescription(step.get(getString(R.string.description)).getAsString());
        }
        manager.beginTransaction()
                .replace(R.id.stepDetailFragment, detailFragment)
                .commit();
    }
}
