package com.taitsmith.daybaker.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.taitsmith.daybaker.R;
import com.taitsmith.daybaker.fragments.StepDetailFragment;

public class StepDetailActivity extends AppCompatActivity {
    private Gson gson;
    private JsonObject step;
    private JsonParser parser;
    private SimpleExoPlayerView playerView;
    private SimpleExoPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_detail);

        gson = new Gson();
        parser = new JsonParser();

        if (getIntent().hasExtra("step")){
            String stepString = getIntent().getStringExtra("step");
            step = parser.parse(stepString).getAsJsonObject();
        }

        StepDetailFragment detailFragment = new StepDetailFragment();
        detailFragment.setText(step.get("description").getAsString());

        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction()
                .add(R.id.stepDetailFragmentPortrait, detailFragment)
                .commit();
    }
}
