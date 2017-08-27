package com.taitsmith.daybaker.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

import static com.taitsmith.daybaker.activities.BaseActivity.realmConfiguration;

/**
 * Get rid of those pesky " " around all of the strings from the JSON and
 * also swippy swap between recipes in the ingredient master list. Also gets
 * a list of ingrediends for the widget.
 */

public class HelpfulUtils {
    //for swapping between recipes in the stepsummaryactivity
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

    //for swapping between recipes in the stepsummaryactivity
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

    //all of the json strings have "" around them.
    static String removeQuotes(String input) {
        return input.replace("\"", "");
    }

    //takes the ingredient array and makes a List for use in the widget.
    //pretty goofy so it'll be changed to a listview at some point
    public static List<String> getStringsForIngredientWidget(String recipeName) {
        List<String> ingredientList = new ArrayList<>();
        Realm realm = Realm.getInstance(realmConfiguration);
        JsonParser jsonParser = new JsonParser();
        RealmResults<Recipe> realmResults = realm.where(Recipe.class)
                .equalTo("name", recipeName)
                .findAll();

        Recipe recipe = realmResults.first();
        JsonArray ingredientArray = jsonParser.parse(recipe.getIngredients()).getAsJsonArray();

        for (JsonElement element : ingredientArray) {
            String ingredient = removeQuotes(element.getAsJsonObject().get("ingredient").getAsString());
            String measure = element.getAsJsonObject().get("measure").getAsString().concat(")");
            String amount = (" " +
                    "(").concat(element.getAsJsonObject().get("quantity").getAsString());

            if (measure.equals("UNIT)")) {
                ingredient = ingredient.concat(amount).concat(")");
            } else {
                ingredient = ingredient.concat(amount).concat(measure);
            }
            ingredientList.add(ingredient.concat("\n"));
        }
        return ingredientList;
    }
}
