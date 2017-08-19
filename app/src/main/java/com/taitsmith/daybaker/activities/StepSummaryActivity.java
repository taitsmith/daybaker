package com.taitsmith.daybaker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.taitsmith.daybaker.R;
import com.taitsmith.daybaker.data.Recipe;
import com.taitsmith.daybaker.data.StepAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

import static com.taitsmith.daybaker.activities.MainActivity.realmConfiguration;

public class StepSummaryActivity extends AppCompatActivity implements StepAdapter.ListItemClickListener {
    String name;
    Realm realm;
    Recipe recipe;
    StepAdapter adapter;
    RealmResults<Recipe> results;
    public JsonArray stepArray;

    @BindView(R.id.stepSummaryRecycler)
    RecyclerView stepRecycler;
    @BindView(R.id.stepSummaryDescription)
    TextView summaryDescription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_summary);
        ButterKnife.bind(this);
        realm = Realm.getInstance(realmConfiguration);

        if (getIntent().hasExtra("recipe_name")){
            name = getIntent().getStringExtra("recipe_name");
        }

        summaryDescription.setText(getString(R.string.step_summary_description, name));

        results = realm.where(Recipe.class)
                .equalTo("name", name)
                .findAll();

        recipe = results.first();

        String steps = recipe.getSteps();

        JsonParser parser = new JsonParser();

        JsonObject stepObject = parser.parse(steps).getAsJsonObject();

        stepArray = stepObject.get("values").getAsJsonArray();

        LinearLayoutManager manager = new LinearLayoutManager(this);
        stepRecycler.setLayoutManager(manager);

        adapter = new StepAdapter(stepArray.size(), stepArray, this);
        stepRecycler.setAdapter(adapter);
    }

    @Override
    public void onListItemClick(int itemIndex, JsonObject stepObject) {
        Intent intent = new Intent(this, StepDetailActivity.class);
        intent.putExtra("step", stepObject.toString());
        startActivity(intent);
    }
}
