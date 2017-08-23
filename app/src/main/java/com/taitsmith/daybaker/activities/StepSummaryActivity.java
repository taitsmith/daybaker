package com.taitsmith.daybaker.activities;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.taitsmith.daybaker.R;
import com.taitsmith.daybaker.data.Recipe;
import com.taitsmith.daybaker.data.StepWidget;
import com.taitsmith.daybaker.fragments.StepDetailFragment;
import com.taitsmith.daybaker.fragments.StepListFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

import static com.taitsmith.daybaker.activities.BaseActivity.realmConfiguration;

public class StepSummaryActivity extends AppCompatActivity implements StepListFragment.OnStepClickListener {
    private String recipeName;
    private Realm realm;
    private Recipe recipe;
    private RealmResults<Recipe> results;
    private FragmentManager manager;
    private StepListFragment stepListFragment;
    private StepDetailFragment stepDetailFragment;
    private JsonObject stepObject;
    public static JsonArray stepArray;
    private boolean isTwoPane;

    @BindView(R.id.stepSummaryDescription)
    TextView summaryDescription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_summary);
        ButterKnife.bind(this);

        stepDetailFragment = new StepDetailFragment();
        realm = Realm.getInstance(realmConfiguration);
        manager = getSupportFragmentManager();
        JsonParser parser = new JsonParser();
        stepListFragment = new StepListFragment();

        if (getIntent().hasExtra("RECIPE_NAME")){
            recipeName = getIntent().getStringExtra("RECIPE_NAME");
        }

        isTwoPane = findViewById(R.id.stepDetailFragment) != null;

        summaryDescription.setText(getString(R.string.step_summary_description, recipeName));

        results = realm.where(Recipe.class)
            .equalTo("name", recipeName)
            .findAll();
        recipe = results.first();

        String steps = recipe.getSteps();
        stepObject = parser.parse(steps).getAsJsonObject();
        stepArray = stepObject.get("values").getAsJsonArray();


        manager.beginTransaction()
               .add(R.id.stepListFragment, stepListFragment)
               .commit();

        if (isTwoPane) {
            if (getIntent().hasExtra("DESCRIPTION") && getIntent().hasExtra("VIDEO_URL")) {
                stepDetailFragment.setDescription(getIntent().getStringExtra("DESCRIPTION"));
                stepDetailFragment.setVideoUri(getIntent().getStringExtra("VIDEO_URL"));
            } else if (savedInstanceState != null) {
                stepDetailFragment.setDescription(savedInstanceState.getString("DESCRIPTION"));
                stepDetailFragment.setVideoUri(savedInstanceState.getString("VIDEO_URL"));
                stepObject = parser.parse(savedInstanceState.getString("STEP_OBJECT")).getAsJsonObject();
            } else {
                stepObject = stepArray.get(0).getAsJsonObject();
                JsonElement element = stepObject.get("nameValuePairs");
                stepObject = element.getAsJsonObject();
                stepDetailFragment.setVideoUri(stepObject.get("videoURL").getAsString());
                stepDetailFragment.setDescription(stepObject.get("description").getAsString());
            }
            manager.beginTransaction()
                    .replace(R.id.stepDetailFragment, stepDetailFragment)
                    .commit();
        }

    }

    @Override
    public void onStepSelected(int position) {
        stepObject = stepArray.get(position).getAsJsonObject();
        JsonElement element = stepObject.get("nameValuePairs");
        stepObject = element.getAsJsonObject();

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, StepWidget.class));

        String description = stepObject.get("description").getAsString();
        String videoUrl = stepObject.get("videoURL").getAsString();

        StepWidget.stepDescription = description;
        StepWidget.videoUrl = videoUrl;
        StepWidget.step = recipeName;

        StepWidget.updateWidgetText(this, appWidgetManager, appWidgetIds, description);

        if (isTwoPane){
            stepDetailFragment = new StepDetailFragment();
            stepDetailFragment.setDescription(description);
            stepDetailFragment.setVideoUri(videoUrl);

            manager.beginTransaction()
                    .replace(R.id.stepDetailFragment, stepDetailFragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, StepDetailActivity.class);
            intent.putExtra("step", stepObject.toString());
            startActivity(intent);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (isTwoPane) {
            outState.putString("STEP_OBJECT", stepObject.toString());
            outState.putString("DESCRIPTION", stepObject.get("description").getAsString());
            outState.putString("VIDEO_URL", stepObject.get("videoURL").getAsString());
            super.onSaveInstanceState(outState);
        }
    }
}
