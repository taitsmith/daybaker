package com.taitsmith.daybaker.data;

import android.content.Context;
import android.util.Log;

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

/**
 * Downloads recipe JSON from web, parses it and creates realm objects to be used offline.
 * Only runs if the app doesn't detect a filled Realm when it starts.
 */

public class RecipeRealmCreator {
    private Gson gson;
    private OkHttpClient client;
    private HttpUrl recipeUrl;
    private Realm realm;
    public static boolean networkError;

    public void downloadRecipeData(Context context) {
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
                Log.d("OKHTTP ERROR", e.toString());
                networkError = true;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                realm = Realm.getInstance(realmConfiguration);

                realm.beginTransaction();

                try {
                    final String responseData = response.body().string();
                    final JSONArray recipeList = new JSONArray(responseData);

                    for (int i = 0; i < recipeList.length(); i++){
                        Recipe recipe = realm.createObject(Recipe.class);

                        JSONObject object = recipeList.getJSONObject(i);

                        String name = object.getString("name");
                        recipe.setName(name);

                        String ingredients = gson.toJson(object.getJSONArray("ingredients"));
                        recipe.setIngredients(ingredients);

                        String steps = gson.toJson(object.getJSONArray("steps"));
                        recipe.setSteps(steps);

                        int servings = object.getInt("servings");
                        recipe.setServings(servings);

                        Log.d("RESULTS NAME ", name);
                        Log.d("RESULTS STEPS ", steps);
                    }
                    realm.commitTransaction();
                    realm.close();
                    networkError = false;

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
