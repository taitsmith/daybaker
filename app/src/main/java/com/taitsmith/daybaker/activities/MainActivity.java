package com.taitsmith.daybaker.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.taitsmith.daybaker.R;
import com.taitsmith.daybaker.data.RecipeRealmCreator;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MainActivity extends AppCompatActivity {
    private Realm recipeRealm;
    public static RealmConfiguration realmConfiguration;

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

        fillRecycler();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recipeRecycler.setLayoutManager(layoutManager);

        recipeRecycler.setHasFixedSize(true);
    }

    public void fillRecycler(){
    }
}
