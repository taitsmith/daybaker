package com.taitsmith.daybaker.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.taitsmith.daybaker.R;
import com.taitsmith.daybaker.data.Recipe;
import com.taitsmith.daybaker.data.RecipeAdapter;
import com.taitsmith.daybaker.data.RecipeRealmCreator;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

import static com.taitsmith.daybaker.activities.MainActivity.realmConfiguration;

/**
 * Recycler view fragment for tablet activities.
 */

public class MasterListFragment extends Fragment implements RecipeAdapter.ListItemClickListener {
    @BindView(R.id.rv_recipes)
    RecyclerView recipeRecycler;
    Context context;
    Realm recipeRealm;
    RealmResults<Recipe> recipes;
    RecipeAdapter recipeAdapter;

    public MasterListFragment(){}


    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        ButterKnife.bind(this, container);
        context = getContext();
        View rootView = inflater.inflate(R.layout.w600_recipe_recycler_fragment, container, false);

        Realm.init(context);

        realmConfiguration = new RealmConfiguration.Builder()
                .name("recipeRealm.recipeRealm")
                .build();

        recipeRealm = Realm.getInstance(realmConfiguration);

        if (recipeRealm.isEmpty()) {
            RecipeRealmCreator creator = new RecipeRealmCreator();
            creator.downloadRecipeData(context);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recipeRecycler.setLayoutManager(layoutManager);

        recipeRecycler.setHasFixedSize(true);

        recipes = recipeRealm.where(Recipe.class)
                .findAll();

        recipeAdapter = new RecipeAdapter(recipes.size(), recipes, null);
        recipeRecycler.setAdapter(recipeAdapter);

        return rootView;
    }

    @Override
    public void onListItemClick(int itemIndex) {

    }
}
