package com.taitsmith.daybaker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.taitsmith.daybaker.R;
import com.taitsmith.daybaker.data.Recipe;
import com.taitsmith.daybaker.data.RecipeAdapter;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

import static com.taitsmith.daybaker.activities.BaseActivity.realmConfiguration;

public class MainActivity extends AppCompatActivity
    implements RecipeAdapter.ListItemClickListener {
    private Realm recipeRealm;
    private RecipeAdapter recipeAdapter;
    RealmResults<Recipe> recipes;
    private Recipe recipe;
    public static boolean showError;

    @BindView(R.id.rv_recipes)
    RecyclerView recipeRecycler;
    @BindString(R.string.recipe_url)
    String recipeListString;
    @BindView(R.id.errorTV)
    TextView errorTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        recipeRealm = Realm.getInstance(realmConfiguration);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);

        recipeRecycler.setLayoutManager(layoutManager);

        recipeRecycler.setHasFixedSize(true);

        errorTv.setVisibility(View.INVISIBLE);

        fillRecycler();
    }

    public void fillRecycler(){
        recipes = recipeRealm.where(Recipe.class)
                .findAll();

        recipeAdapter = new RecipeAdapter(recipes.size(), recipes, this);
        recipeRecycler.setAdapter(recipeAdapter);
    }

    //get the clicked recipeName and start the detail activity
    //include the recipeName of the recipeName selected.
    @Override
    public void onListItemClick(int itemIndex) {
        recipe = recipes.get(itemIndex);
        Intent intent = new Intent(this, IngredientSummaryActivity.class);
        intent.putExtra("recipe_name", recipe.getName());
        startActivity(intent);
    }

    public void showError(boolean show){
        if (show) {
            recipeRecycler.setVisibility(View.INVISIBLE);
        }
    }
}
