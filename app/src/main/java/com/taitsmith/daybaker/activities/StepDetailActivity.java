package com.taitsmith.daybaker.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.taitsmith.daybaker.R;

public class StepDetailActivity extends AppCompatActivity {
    private Gson gson;
    private JsonObject step;
    private JsonParser parser;

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

        String s = step.get("description").getAsString();
        Log.d("DETAIL STEP ", s);
    }
}
