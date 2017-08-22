package com.taitsmith.daybaker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.taitsmith.daybaker.R;
import com.taitsmith.daybaker.data.IngredientAdapter;
import com.taitsmith.daybaker.data.Recipe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;

import static com.taitsmith.daybaker.activities.BaseActivity.realmConfiguration;

public class IngredientSummaryActivity extends AppCompatActivity {
    @BindView(R.id.rv_ingredients)
    RecyclerView ingredientRecycler;
    @BindView(R.id.continueButton)
    Button continueButton;

    Realm realm;
    String recipeName;
    Recipe recipe;
    IngredientAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_summary);
        ButterKnife.bind(this);

        realm = Realm.getInstance(realmConfiguration);

        if (getIntent().hasExtra("recipe_name")) {
            recipeName = getIntent().getStringExtra("recipe_name");
        }

        RealmResults<Recipe> results = realm.where(Recipe.class)
                .equalTo("name", recipeName)
                .findAll();

        recipe = results.first();

        String ingredients = recipe.getIngredients();

        JsonParser parser = new JsonParser();

        JsonObject ingredientObject = parser.parse(ingredients).getAsJsonObject();

        JsonArray ingredientsArray = ingredientObject.get("values").getAsJsonArray();

        LinearLayoutManager manager = new LinearLayoutManager(this);
        ingredientRecycler.setLayoutManager(manager);

        adapter = new IngredientAdapter(ingredientsArray.size(), ingredientsArray);
        ingredientRecycler.setAdapter(adapter);
    }

    @OnClick(R.id.continueButton)
    public void seeSteps(){
        Intent intent = new Intent(this, StepSummaryActivity.class);
        intent.putExtra("recipe_name", recipeName);
        startActivity(intent);
    }
}

