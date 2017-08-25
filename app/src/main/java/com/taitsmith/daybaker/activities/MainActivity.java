package com.taitsmith.daybaker.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.taitsmith.daybaker.R;
import com.taitsmith.daybaker.data.Recipe;
import com.taitsmith.daybaker.data.RecipeAdapter;
import com.taitsmith.daybaker.data.RecipeRealmCreator;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;

import static com.taitsmith.daybaker.activities.BaseActivity.realmConfiguration;
import static com.taitsmith.daybaker.activities.StepSummaryActivity.SHARED_PREFS;

public class MainActivity extends AppCompatActivity
    implements RecipeAdapter.ListItemClickListener {
    private Realm recipeRealm;
    private RecipeAdapter recipeAdapter;
    RealmResults<Recipe> recipes;
    private Recipe recipe;
    public static boolean showError;
    private int numColumns;

    @BindView(R.id.rv_recipes)
    RecyclerView recipeRecycler;
    @BindString(R.string.recipe_url)
    String recipeListString;
    @BindView(R.id.errorTV)
    TextView errorTv;
    @BindView(R.id.reloadFromNetworkFab)
    FloatingActionButton reloadFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        recipeRealm = Realm.getInstance(realmConfiguration);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);

        recipeRecycler.setLayoutManager(layoutManager);

        recipeRecycler.setHasFixedSize(true);

        if (showError) {
            hideUi(true);
        }

        fillRecycler();
    }

    //if there's an error
    public void hideUi(boolean hide) {
        if (hide) {
            errorTv.setVisibility(View.INVISIBLE);
            reloadFab.setVisibility(View.INVISIBLE);
        } else {
            errorTv.setVisibility(View.INVISIBLE);
            reloadFab.setVisibility(View.INVISIBLE);
            recipeRecycler.setVisibility(View.VISIBLE);
        }
    }

    //ui stuff
    public void fillRecycler() {
        try {
            recipes = recipeRealm.where(Recipe.class)
                    .findAll();
        } catch (NullPointerException e) {
            e.printStackTrace();
            hideUi(true);
        }
        hideUi(false);

        recipeAdapter = new RecipeAdapter(recipes.size(), recipes, this);
        recipeRecycler.setAdapter(recipeAdapter);
    }

    //get the clicked recipeName and start the detail activity
    //include the recipeName of the recipeName selected.
    @Override
    public void onListItemClick(int itemIndex) {
        //so we know to default to the recipe intro step
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("NEW_RECIPE", true);
        editor.apply();

        recipe = recipes.get(itemIndex);
        Intent intent = new Intent(this, IngredientSummaryActivity.class);
        intent.putExtra("recipe_name", recipe.getName());
        startActivity(intent);
    }

    //button
    @OnClick(R.id.reloadFromNetworkFab)
    public void reloadFromNetwork() {
        if (recipeRealm.isEmpty()) {
            RecipeRealmCreator creator = new RecipeRealmCreator();
            creator.downloadRecipeData(this);
        }
        if (!showError) {
            hideUi(false);
        }
    }
}
