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
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;
import com.taitsmith.daybaker.R;
import com.taitsmith.daybaker.data.HelpfulUtils;
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
    private FragmentManager fragmentManager;
    private StepDetailFragment stepDetailFragment;
    private JsonObject stepObject;
    private SharedPreferences preferences;
    private boolean isTwoPane;
    private JsonParser jsonParser;
    private FloatingActionMenu actionMenu;
    private SharedPreferences.Editor editor;

    public String videoUrl, stepDescription, stepString, imageUrl;

    public static JsonArray stepArray;
    public static final String SHARED_PREFS = "sharedPrefs";

    StepListFragment stepListFragment;
    SubActionButton.Builder itemBuilder;
    ImageView nextRecipe, previousRecipe, homeView;

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

        realm = Realm.getInstance(realmConfiguration);
        fragmentManager = getSupportFragmentManager();
        jsonParser = new JsonParser();
        preferences = getSharedPreferences(SHARED_PREFS, 0);

        //assuming we got here somehow and that the name of the
        //selected recipe was sent with us;
        if (getIntent().hasExtra("RECIPE_NAME")) {
            recipeName = getIntent().getStringExtra("RECIPE_NAME");
        } else if (preferences.getBoolean("NEW_RECIPE", true)){
            recipeName = preferences.getString("RECIPE_NAME", null);
        }

        //set the basic description text to include the recipe name.
        RealmResults<Recipe> results = realm.where(Recipe.class)
                .equalTo("name", recipeName)
                .findAll();

        setUi(results.first());
    }

    @Override
    protected void onResume() {
        super.onResume();
        getStepData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveStepData(imageUrl, videoUrl, stepDescription, stepObject.toString());
    }

    public void setUi(Recipe recipe) {
        this.recipe = recipe;
        stepListFragment = new StepListFragment();
        stepDetailFragment = new StepDetailFragment();

        stepArray = jsonParser.parse(recipe.getSteps()).getAsJsonArray();
        summaryDescription.setText(getString(R.string.step_summary_description, recipe.getName()));

        if (preferences.getBoolean("NEW_RECIPE", false)) {
            stepObject = stepArray.get(0).getAsJsonObject();
            videoUrl = stepObject.get(getString(R.string.videoURL)).getAsString();
            stepDescription = stepObject.get(getString(R.string.description)).getAsString();
            saveStepData(imageUrl, videoUrl, stepDescription, stepObject.toString());
        } else {
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
                    .replace(R.id.stepListFragment, stepListFragment, "STEP_LIST_FRAGMENT")
                    .replace(R.id.stepDetailFragment, stepDetailFragment)
                    .commit();
        } else {
            //do some different stuff if it's single pane
            fragmentManager.beginTransaction()
                    .replace(R.id.stepListFragment, stepListFragment)
                    .commit();
        }
        setFab();
    }

    @Override
    public void onStepSelected(int position) {
        stepObject = stepArray.get(position).getAsJsonObject();


        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, StepWidget.class));

        stepDescription = stepObject.get(getString(R.string.description)).getAsString();
        videoUrl = stepObject.get(getString(R.string.videoURL)).getAsString();
        imageUrl = stepObject.get("thumbnailURL").getAsString();


        //update the widget.
        StepWidget.updateWidgetText(this, appWidgetManager, appWidgetIds, stepDescription);

        saveStepData(imageUrl, videoUrl, stepDescription, stepObject.toString());

        //if it's two pane change the detail fragment
        if (isTwoPane){
            stepDetailFragment = new StepDetailFragment();
            stepDetailFragment.setDescription(stepDescription);
            stepDetailFragment.setVideoUri(videoUrl);

            fragmentManager.beginTransaction()
                    .replace(R.id.stepDetailFragment, stepDetailFragment)
                    .commit();
        } else { //otherwise start a new activity.
            saveStepData(imageUrl, videoUrl, stepDescription, stepString);
            Intent intent = new Intent(this, StepDetailActivity.class);
            intent.putExtra("RECIPE_NAME", stepObject.toString());
            startActivity(intent);
        }
    }

    //for easy access.
    public void saveStepData(String imageUrl, String videoUrl, String description, String step) {
        editor = preferences.edit();
        editor.putString("IMAGE_URL", imageUrl);
        editor.putString("VIDEO_URL", videoUrl);
        editor.putString("DESCRIPTION", description);
        editor.putString("STEP_OBJECT", step);
        editor.putString("RECIPE_NAME", recipe.getName());
        editor.putBoolean("NEW_RECIPE", false);
        editor.apply();
    }
    public void getStepData() {
        stepObject = jsonParser.parse(preferences.getString("STEP_OBJECT", null)).getAsJsonObject();
        videoUrl = preferences.getString("VIDEO_URL", null);
        stepDescription = preferences.getString("DESCRIPTION", null);
        recipeName = preferences.getString("RECIPE_NAME", null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    private void setFab(){
        final SubActionButton nextButton, previousButton, homeButton;
        itemBuilder = new SubActionButton.Builder(this);
        nextRecipe = new ImageView(this);
        previousRecipe = new ImageView(this);
        homeView = new ImageView(this);
        homeView.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_upward_black_36dp));
        nextRecipe.setImageDrawable(getResources().getDrawable(R.drawable.ic_skip_next_black_36dp));
        previousRecipe.setImageDrawable(getResources().getDrawable(R.drawable.ic_skip_previous_black_36dp));
        nextButton = itemBuilder.setContentView(nextRecipe).build();
        previousButton = itemBuilder.setContentView(previousRecipe).build();
        homeButton = itemBuilder.setContentView(homeView).build();

        actionMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(nextButton)
                .addSubActionView(homeButton)
                .addSubActionView(previousButton)
                .attachTo(returnHome)
                .build();

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor = preferences.edit();
                editor.putBoolean("NEW_RECIPE", true);
                editor.apply();

                actionMenu.close(true);
                setUi(HelpfulUtils.getNextRecipe(recipe.getName()));
            }
        });

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor = preferences.edit();
                editor.putBoolean("NEW_RECIPE", true);
                editor.apply();
                actionMenu.close(true);
                setUi(HelpfulUtils.getPreviousRecipe(recipe.getName()));
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
