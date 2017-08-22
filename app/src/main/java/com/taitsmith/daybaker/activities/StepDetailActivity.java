package com.taitsmith.daybaker.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.taitsmith.daybaker.R;
import com.taitsmith.daybaker.fragments.StepDetailFragment;

public class StepDetailActivity extends AppCompatActivity {
    private JsonObject step;
    private JsonParser parser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_detail);
        parser = new JsonParser();

        if (getIntent().hasExtra("step")){
            String stepString = getIntent().getStringExtra("step");
            step = parser.parse(stepString).getAsJsonObject();
        }

        StepDetailFragment detailFragment = new StepDetailFragment();
        detailFragment.setVideoUri(step.get("videoURL").getAsString());
        detailFragment.setDescription(step.get("description").getAsString());

        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction()
                .replace(R.id.stepDetailFragment, detailFragment)
                .commit();
    }
}
