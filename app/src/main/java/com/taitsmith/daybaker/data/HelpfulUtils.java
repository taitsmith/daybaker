package com.taitsmith.daybaker.data;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

import static com.taitsmith.daybaker.activities.BaseActivity.realmConfiguration;

/**
 * Get rid of those pesky " " around all of the strings from the JSON and
 * also swippy swap between recipes in the ingredient master list.
 */

public class HelpfulUtils {

    public static Recipe getNextRecipe(String currentRecipe) {
        Realm realm = Realm.getInstance(realmConfiguration);
        Recipe nextRecipe;
        RealmResults<Recipe> realmResults = realm.where(Recipe.class)
                .findAll();

        Recipe recipe = realmResults.where()
                .equalTo("name", currentRecipe)
                .findFirst();

        int position = recipe.getPosition();

        if (position < realmResults.size() - 1) {
            nextRecipe = realmResults.get(position + 1);
        } else {
            nextRecipe = realmResults.get(0);
        }
        return nextRecipe;
    }

    public static Recipe getPreviousRecipe(String currentRecipe) {
        Realm realm = Realm.getInstance(realmConfiguration);
        Recipe previousRecipe;
        RealmResults<Recipe> realmResults = realm.where(Recipe.class)
                .findAll();

        Recipe recipe = realmResults.where()
                .equalTo("name", currentRecipe)
                .findFirst();

        int position = recipe.getPosition();

        if (position == 0) {
            previousRecipe = realmResults.get(3);
        } else {
            previousRecipe = realmResults.get(position - 1);
        }

        return previousRecipe;
    }

    static String removeQuotes(String input) {
        return input.replace("\"", "");
    }

    public static List<String> getStringsForIngredientWidget(String recipeName) {
        Realm realm = Realm.getInstance(realmConfiguration);
        JsonParser jsonParser = new JsonParser();
        RealmResults<Recipe> realmResults = realm.where(Recipe.class)
                .equalTo("name", recipeName)
                .findAll();

        Recipe recipe = realmResults.first();
        JsonArray stepArray = jsonParser.parse(recipe.getIngredients()).getAsJsonArray();

        for (JsonElement element : stepArray) {
            String ingredient = removeQuotes(element.getAsJsonObject().get("ingredient").getAsString());
            Log.d("INGREDIENT: ", ingredient);
        }
        return  null;
    }
}
