package com.taitsmith.daybaker.activities;

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
import com.taitsmith.daybaker.fragments.StepDetailFragment;
import com.taitsmith.daybaker.fragments.StepListFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

import static com.taitsmith.daybaker.activities.MainActivity.realmConfiguration;

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
        realm = Realm.getInstance(realmConfiguration);
        manager = getSupportFragmentManager();
        JsonParser parser = new JsonParser();
        stepListFragment = new StepListFragment();

        if (getIntent().hasExtra("recipe_name")){
            recipeName = getIntent().getStringExtra("recipe_name");
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
           if (savedInstanceState != null) {
                  stepDetailFragment = (StepDetailFragment) getSupportFragmentManager()
                          .getFragment(savedInstanceState, "DETAIL_FRAGMENT");
           } else {
               stepDetailFragment = new StepDetailFragment();
               stepDetailFragment.setVideoUri("");
               stepDetailFragment.setShortDescription(getString(R.string.step_detail_fragment_default));
               manager.beginTransaction()
                       .add(R.id.stepDetailFragment, stepDetailFragment)
                       .commit();
           }
        }
    }

    @Override
    public void onStepSelected(int position) {
        JsonElement element = stepArray.get(position);
        JsonObject object = element.getAsJsonObject();
        JsonElement element1 = object.get("nameValuePairs");
        stepObject = element1.getAsJsonObject();

        if (isTwoPane){
            stepDetailFragment = new StepDetailFragment();
            FragmentManager manager = getSupportFragmentManager();

            stepDetailFragment.setShortDescription(stepObject.get("description").getAsString());
            stepDetailFragment.setVideoUri(stepObject.get("videoURL").getAsString());

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
            getSupportFragmentManager().putFragment(outState, "DETAIL_FRAGMENT", stepDetailFragment);
            super.onSaveInstanceState(outState);
        }
    }
}
