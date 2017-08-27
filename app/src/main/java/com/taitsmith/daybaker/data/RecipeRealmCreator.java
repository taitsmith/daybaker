package com.taitsmith.daybaker.data;

import android.content.Context;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.taitsmith.daybaker.R;

import java.io.IOException;

import io.realm.Realm;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.taitsmith.daybaker.activities.BaseActivity.realmConfiguration;
import static com.taitsmith.daybaker.activities.MainActivity.showError;

/**
 * Downloads recipe JSON from web, parses it and creates realm objects to be used offline.
 * Only runs if the app doesn't detect a filled Realm when it starts.
 */

public class RecipeRealmCreator {
    private OkHttpClient client;
    private HttpUrl recipeUrl;
    private Realm realm;
    private JsonParser parser;
    private String responseData;
    private JsonArray recipeResponseList;

    public void downloadRecipeData(final Context context) {
        recipeUrl = HttpUrl.parse(context.getString(R.string.recipe_url));

        client = new OkHttpClient();
        Realm.init(context);

        Request request = new Request.Builder()
                .url(recipeUrl)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                showError = true;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                showError = false;
                int i = 0;

                realm = Realm.getInstance(realmConfiguration);
                realm.beginTransaction();
                parser = new JsonParser();
                responseData = response.body().string();
                recipeResponseList = parser.parse(responseData).getAsJsonArray();

                //The json that comes back is pretty messy so you have
                //to get rid of a lot of stuff.
                for (JsonElement element : recipeResponseList) {
                    Recipe recipe = realm.createObject(Recipe.class);
                    recipe.setIngredients(element.getAsJsonObject().get("ingredients").toString());
                    recipe.setSteps(element.getAsJsonObject().get("steps").toString());
                    recipe.setName(HelpfulUtils.removeQuotes(element.getAsJsonObject().
                            get("name").toString()));
                    recipe.setServings(element.getAsJsonObject().get("servings").getAsInt());
                    recipe.setPosition(i);
                    i++;
                }
                realm.commitTransaction();
                realm.close();
            }
        });
    }
}
