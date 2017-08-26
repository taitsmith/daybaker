package com.taitsmith.daybaker.activities;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.taitsmith.daybaker.R;
import com.taitsmith.daybaker.data.Recipe;
import com.taitsmith.daybaker.data.StepWidget;
import com.taitsmith.daybaker.fragments.StepDetailFragment;
import com.taitsmith.daybaker.fragments.StepListFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;

import static com.taitsmith.daybaker.activities.BaseActivity.realmConfiguration;

public class StepSummaryActivity extends AppCompatActivity implements StepListFragment.OnStepClickListener {
    private Realm realm;
    private Recipe recipe;
    private FragmentManager fragmentManager;
    private StepListFragment stepListFragment;
    private StepDetailFragment stepDetailFragment;
    private JsonObject stepObject;
    private SharedPreferences preferences;
    private boolean isTwoPane;
    private JsonParser jsonParser;

    public String videoUrl, stepDescription, stepString;

    public static JsonArray stepArray;
    public static final String SHARED_PREFS = "sharedPrefs";

    @BindView(R.id.stepSummaryDescription)
    TextView summaryDescription;
    @BindView(R.id.returnHomeFab)
    FloatingActionButton returnHome;

    @VisibleForTesting
    String recipeName = "Cheesecake";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_summary);
        ButterKnife.bind(this);

        stepListFragment = new StepListFragment();
        stepDetailFragment = new StepDetailFragment();
        realm = Realm.getInstance(realmConfiguration);
        fragmentManager = getSupportFragmentManager();
        jsonParser = new JsonParser();
        preferences = getSharedPreferences(SHARED_PREFS, 0);

        //assuming we got here somehow and that the name of the
        //selected recipe was sent with us;
        if (getIntent().hasExtra("RECIPE_NAME")) {
            recipeName = getIntent().getStringExtra("RECIPE_NAME");
        } else {
            recipeName = preferences.getString("RECIPE_NAME", null);
        }

        //set the basic description text to include the recipe name.
        summaryDescription.setText(getString(R.string.step_summary_description, recipeName));

        //do a real query and get the recipe object.
        RealmResults<Recipe> results = realm.where(Recipe.class)
                .equalTo("name", recipeName)
                .findAll();
        recipe = results.first();

        setUi();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getStepData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveStepData(videoUrl, stepDescription, stepObject.toString());
    }

    public void setUi() {
        stepArray = jsonParser.parse(recipe.getSteps()).getAsJsonArray();
        Log.d("LOG ", stepArray.toString());

        //if the user has selected a new recipe show the first step
        if (preferences.getBoolean("NEW_RECIPE", false)){
            stepObject = stepArray.get(0).getAsJsonObject();
            videoUrl = stepObject.get(getString(R.string.videoURL)).getAsString();
            stepDescription = stepObject.get(getString(R.string.description)).getAsString();
        } else {
            stepObject = stepArray.get(0).getAsJsonObject();
            getStepData();
        }

        isTwoPane = findViewById(R.id.stepDetailFragment) != null;

        //if it's not two pane and it's coming from the widget
        //start the StepDetailActivity with the relevant info
        if(!isTwoPane && getIntent().getBooleanExtra("FROM_HOMESCREEN", false)){
            Intent intent = new Intent(this, StepDetailActivity.class);
            intent.putExtra("DESCRIPTION", stepDescription);
            intent.putExtra("VIDEO_URL", videoUrl);
            startActivity(intent);
        }
        
        //do some stuff if it's two-pane
        if (isTwoPane) {
            stepDetailFragment.setVideoUri(videoUrl);
            stepDetailFragment.setDescription(stepDescription);
            fragmentManager.beginTransaction()
                    .replace(R.id.stepListFragment, stepListFragment)
                    .replace(R.id.stepDetailFragment, stepDetailFragment)
                    .commit();
        } else {
            //do some different stuff if it's single pane
            fragmentManager.beginTransaction()
                    .replace(R.id.stepListFragment, stepListFragment)
                    .commit();
        }
    }

    @Override
    public void onStepSelected(int position) {
        stepObject = stepArray.get(position).getAsJsonObject();

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, StepWidget.class));

        stepDescription = stepObject.get(getString(R.string.description)).getAsString();
        videoUrl = stepObject.get(getString(R.string.videoURL)).getAsString();

        //update the widget.
        StepWidget.updateWidgetText(this, appWidgetManager, appWidgetIds, stepDescription);

        //if it's two pane change the detail fragment
        if (isTwoPane){
            stepDetailFragment = new StepDetailFragment();
            stepDetailFragment.setDescription(stepDescription);
            stepDetailFragment.setVideoUri(videoUrl);

            fragmentManager.beginTransaction()
                    .replace(R.id.stepDetailFragment, stepDetailFragment)
                    .commit();
        } else { //otherwise start a new activity.
            saveStepData(videoUrl, stepDescription, stepString);
            Intent intent = new Intent(this, StepDetailActivity.class);
            intent.putExtra("RECIPE_NAME", stepObject.toString());
            startActivity(intent);
        }
    }

    public void saveStepData(String videoUrl, String description, String step) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("VIDEO_URL", videoUrl);
        editor.putString("DESCRIPTION", description);
        editor.putString("STEP_OBJECT", step);
        editor.putString("RECIPE_NAME", recipe.getName());
        editor.putBoolean("NEW_RECIPE", false);
        editor.apply();
    }
    public void getStepData() {
        stepObject = jsonParser.parse(preferences.getString("STEP_OBJECT", null))
                    .getAsJsonObject();
        videoUrl = preferences.getString("VIDEO_URL", null);
        stepDescription = preferences.getString("DESCRIPTION", null);
        recipeName = preferences.getString("RECIPE_NAME", null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @OnClick(R.id.returnHomeFab)
    public void returnHome(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
