package com.taitsmith.daybaker.data;

import android.content.Context;

import com.google.gson.Gson;
import com.taitsmith.daybaker.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    private Gson gson;
    private OkHttpClient client;
    private HttpUrl recipeUrl;
    private Realm realm;

    public void downloadRecipeData(final Context context) {
        recipeUrl = HttpUrl.parse(context.getString(R.string.recipe_url));

        client = new OkHttpClient();
        Realm.init(context);

        gson = new Gson();

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

                realm = Realm.getInstance(realmConfiguration);
                realm.beginTransaction();

                try {
                    final String responseData = response.body().string();
                    final JSONArray recipeList = new JSONArray(responseData);

                    for (int i = 0; i < recipeList.length(); i++){
                        Recipe recipe = realm.createObject(Recipe.class);

                        JSONObject object = recipeList.getJSONObject(i);

                        String name = object.getString(context.getString(R.string.name));
                        recipe.setName(name);

                        String ingredients = gson.toJson(object.getJSONArray(context.getString(
                                R.string.ingredients)));
                        recipe.setIngredients(ingredients);

                        String steps = gson.toJson(object.getJSONArray(context.getString(
                                R.string.steps)));
                        recipe.setSteps(steps);

                        int servings = object.getInt(context.getString(R.string.servings));
                        recipe.setServings(servings);
                    }
                    realm.commitTransaction();
                    realm.close();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
