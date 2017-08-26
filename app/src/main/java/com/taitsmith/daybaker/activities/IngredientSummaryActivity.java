package com.taitsmith.daybaker.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.taitsmith.daybaker.R;
import com.taitsmith.daybaker.data.IngredientAdapter;
import com.taitsmith.daybaker.data.IngredientWidgetService;
import com.taitsmith.daybaker.data.Recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;

import static com.taitsmith.daybaker.activities.BaseActivity.realmConfiguration;
import static com.taitsmith.daybaker.activities.StepSummaryActivity.SHARED_PREFS;

/**
 * Just displays a recycler view full of ingredients and their amounts. When the continue button is
 * clicked the widget text is set and a list of strings is stored in shared prefs to be used with the
 * widget. It's probably more complicated than it needs to be.
 */
public class IngredientSummaryActivity extends AppCompatActivity {
    @BindView(R.id.rv_ingredients)
    RecyclerView ingredientRecycler;
    @BindView(R.id.continueButton)
    Button continueButton;
    @VisibleForTesting
    String recipeName = "Cheesecake";

    private Realm realm;
    private Recipe recipe;
    private IngredientAdapter adapter;
    private List<String> ingredientList;
    private SharedPreferences preferences;
    private JsonParser parser;
    private JsonArray ingredientsArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_summary);
        ButterKnife.bind(this);

        preferences = getSharedPreferences(SHARED_PREFS, 0);
        ingredientList = new ArrayList<>();
        realm = Realm.getInstance(realmConfiguration);
        parser = new JsonParser();


        if (getIntent().hasExtra("recipe_name")) {
            recipeName = getIntent().getStringExtra("recipe_name");
        }

        RealmResults<Recipe> results = realm.where(Recipe.class)
                .equalTo("name", recipeName)
                .findAll();

        recipe = results.first();

        ingredientsArray = parser.parse(recipe.getIngredients()).getAsJsonArray();

        LinearLayoutManager manager = new LinearLayoutManager(this);
        ingredientRecycler.setLayoutManager(manager);

        adapter = new IngredientAdapter(ingredientsArray.size(), ingredientsArray);
        ingredientRecycler.setAdapter(adapter);


    }

    @OnClick(R.id.continueButton)
    public void seeSteps(){
        Intent intent = new Intent(this, StepSummaryActivity.class);
        intent.putExtra("RECIPE_NAME", recipeName);
        startActivity(intent);
    }

    public void getIngredientArray(JsonArray ingredientsArray) {
        ListIterator<String> iterator = ingredientList.listIterator();

        for (JsonElement jsonElement : ingredientsArray) {
            JsonObject object = jsonElement.getAsJsonObject();
            object = object.get("nameValuePairs").getAsJsonObject();
            String s = object.get("ingredient").getAsString();
            iterator.add(s);
        }
        updateSharedPrefs((ArrayList<String>) ingredientList);
    }

    public void updateSharedPrefs(ArrayList<String> list) {
        StringBuilder builder = new StringBuilder();
        for (String s : list){
            builder.append(s).append("-");
        }

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("INGREDIENTS", builder.toString());
        editor.putInt("CURRENT_INGREDIENT", -1);
        Log.d("LOG ", builder.toString());
        editor.apply();
        
        IngredientWidgetService.startActionNextIngredient(this);
    }
}

