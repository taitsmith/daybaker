package com.taitsmith.daybaker.activities;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
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

    @VisibleForTesting
    String recipeName = "Cheesecake";

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
        JsonObject object = parser.parse(steps).getAsJsonObject();
        stepArray = object.get(getString(R.string.values)).getAsJsonArray();
        manager.beginTransaction()
                .replace(R.id.stepListFragment, stepListFragment)
                .commit();

        stepDetailFragment = new StepDetailFragment();

        if(!isTwoPane && getIntent().hasExtra("DESCRIPTION")){
            Intent intent = new Intent(this, StepDetailActivity.class);
            intent.putExtra("DESCRIPTION", getIntent().getStringExtra("DESCRIPTION"));
            intent.putExtra("VIDEO_URL", getIntent().getStringExtra("VIDEO_URL"));
            startActivity(intent);
        }

        if (isTwoPane) {
            if (savedInstanceState != null) {
                stepDetailFragment.setDescription(savedInstanceState.getString("DESCRIPTION"));
                stepDetailFragment.setVideoUri(savedInstanceState.getString("VIDEO_URL"));
                stepObject = parser.parse(savedInstanceState.getString("STEP_OBJECT")).getAsJsonObject();
            }else if (getIntent().hasExtra("DESCRIPTION")) {
                stepDetailFragment.setDescription(getIntent().getStringExtra("DESCRIPTION"));
                stepDetailFragment.setVideoUri(getIntent().getStringExtra("VIDEO_URL"));
                stepObject = parser.parse(getIntent().getStringExtra("STEP_STRING")).getAsJsonObject();
            } else {
                stepObject = stepArray.get(0).getAsJsonObject();
                JsonElement element = stepObject.get(getString(R.string.nameValuePairs));
                stepObject = element.getAsJsonObject();
                stepDetailFragment.setVideoUri(stepObject.get(getString(R.string.videoURL)).getAsString());
                stepDetailFragment.setDescription(stepObject.get(getString(R.string.description)).getAsString());
            }
            manager.beginTransaction()
                .replace(R.id.stepDetailFragment, stepDetailFragment)
                .commit();
        }
    }

    @Override
    public void onStepSelected(int position) {
        stepObject = stepArray.get(position).getAsJsonObject();
        JsonElement element = stepObject.get(getString(R.string.nameValuePairs));
        stepObject = element.getAsJsonObject();

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, StepWidget.class));

        String description = stepObject.get(getString(R.string.description)).getAsString();
        String videoUrl = stepObject.get(getString(R.string.videoURL)).getAsString();

        StepWidget.stepDescription = description;
        StepWidget.videoUrl = videoUrl;
        StepWidget.recipeName = recipeName;
        StepWidget.stepObject = stepObject.toString();

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
            intent.putExtra("RECIPE_NAME", stepObject.toString());
            startActivity(intent);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (isTwoPane) {
            outState.putString("STEP_OBJECT", stepObject.toString());
            outState.putString("DESCRIPTION", stepObject.get(getString(R.string.description)).getAsString());
            outState.putString("VIDEO_URL", stepObject.get(getString(R.string.videoURL)).getAsString());
            super.onSaveInstanceState(outState);
        }
    }
}
