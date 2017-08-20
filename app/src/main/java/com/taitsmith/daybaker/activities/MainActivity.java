package com.taitsmith.daybaker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.taitsmith.daybaker.R;
import com.taitsmith.daybaker.data.Recipe;
import com.taitsmith.daybaker.data.RecipeAdapter;
import com.taitsmith.daybaker.data.RecipeRealmCreator;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity
    implements RecipeAdapter.ListItemClickListener {
    private Realm recipeRealm;
    public static RealmConfiguration realmConfiguration;
    private RecipeAdapter recipeAdapter;
    RealmResults<Recipe> recipes;
    private Recipe recipe;

    @BindView(R.id.rv_recipes)
    RecyclerView recipeRecycler;
    @BindString(R.string.recipe_url)
    String recipeListString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Realm.init(this);

        realmConfiguration = new RealmConfiguration.Builder()
                .name("recipeRealm.recipeRealm")
                .build();

        recipeRealm = Realm.getInstance(realmConfiguration);

        if (recipeRealm.isEmpty()) {
            RecipeRealmCreator creator = new RecipeRealmCreator();
            creator.downloadRecipeData(this);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recipeRecycler.setLayoutManager(layoutManager);

        recipeRecycler.setHasFixedSize(true);

        fillRecycler();
    }

    public void fillRecycler(){
        recipes = recipeRealm.where(Recipe.class)
                .findAll();

        recipeAdapter = new RecipeAdapter(recipes.size(), recipes, this);
        recipeRecycler.setAdapter(recipeAdapter);
    }

    //get the clicked recipeName and start the detail activity
    //include the name of the recipeName selected.
    @Override
    public void onListItemClick(int itemIndex) {
        recipe = recipes.get(itemIndex);
        Intent intent = new Intent(this, IngredientSummaryActivity.class);
        intent.putExtra("recipe_name", recipe.getName());
        startActivity(intent);

    }
}
