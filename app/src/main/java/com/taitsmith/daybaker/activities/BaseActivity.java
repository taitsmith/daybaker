package com.taitsmith.daybaker.activities;

import android.app.Application;

import com.taitsmith.daybaker.data.RecipeRealmCreator;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Sets up Realm etc.
 */

public class BaseActivity extends Application {
    private Realm recipeRealm;
    public static RealmConfiguration realmConfiguration;

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);

        realmConfiguration = new RealmConfiguration.Builder()
                .name("recipeRealm.recipeRealm")
                .build();

        recipeRealm = Realm.getInstance(realmConfiguration);

        if (recipeRealm.isEmpty()) {
            RecipeRealmCreator creator = new RecipeRealmCreator();
            creator.downloadRecipeData(this);
        }
    }
}
