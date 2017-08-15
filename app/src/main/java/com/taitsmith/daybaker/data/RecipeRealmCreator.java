package com.taitsmith.daybaker.data;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.taitsmith.daybaker.R;
import com.taitsmith.daybaker.activities.MainActivity;

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

/**
 * Downloads recipe JSON from web, parses it and creates realm objects to be used offline.
 */

public class RecipeRealmCreator {
    private Gson gson;
    private OkHttpClient client;
    private HttpUrl recipeUrl;
    private Realm realm;


    public void downloadRecipeData(Context context) {
        recipeUrl = HttpUrl.parse(context.getString(R.string.recipe_url));

        client = new OkHttpClient();
        realm.init(context);

        realm = Realm.getInstance(MainActivity.realmConfiguration);

        realm.beginTransaction();

        gson = new Gson();

        Request request = new Request.Builder()
                .url(recipeUrl)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("OKHTTP ", "FAILURE");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final String responseData = response.body().string();
                    final JSONArray recipeList = new JSONArray(responseData);

                    for (int i = 0; i < recipeList.length(); i++){
                        Recipe recipe = new Recipe();

                        JSONObject object = recipeList.getJSONObject(i);

                        String name = object.getString("name");
                        recipe.setName(name);

                        String ingredients = gson.toJson(object.getJSONArray("ingredients"));
                        recipe.setIngredients(ingredients);

                        String steps = gson.toJson(object.getJSONArray("steps"));
                        recipe.setSteps(steps);

                        Log.d("RESULTS NAME ", name);
                        Log.d("RESULTS STEPS ", steps);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        realm.close();
    }
}
