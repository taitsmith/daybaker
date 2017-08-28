package com.taitsmith.daybaker.activities;

import android.app.Application;

import com.taitsmith.daybaker.data.RecipeRealmCreator;
import com.taitsmith.daybaker.data.RecipeRealmMigration;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.exceptions.RealmMigrationNeededException;

/**
 * Sets up Realm etc.
 */

public class BaseActivity extends Application {
    private Realm recipeRealm;
    public static RealmConfiguration realmConfiguration;

    @Override
    public void onCreate() {
        RecipeRealmCreator creator = new RecipeRealmCreator();

        super.onCreate();
        Realm.init(this);

        try {
            realmConfiguration = new RealmConfiguration.Builder()
                    .name("recipeRealm.recipeRealm")
                    .build();

            recipeRealm = Realm.getInstance(realmConfiguration);
        } catch (RealmMigrationNeededException e) { // updated realm schema
            realmConfiguration = new RealmConfiguration.Builder()
                    .schemaVersion(2)
                    .migration(new RecipeRealmMigration())
                    .build();
            recipeRealm = Realm.getInstance(realmConfiguration);
            creator.downloadRecipeData(this);
        }

        if (recipeRealm.isEmpty()) {
            creator.downloadRecipeData(this);
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        recipeRealm.close();
    }
}
