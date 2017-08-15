package com.taitsmith.daybaker.data;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.taitsmith.daybaker.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 */

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    private int numberItems;
    private JSONArray resultsArray;

    public interface ListItemClickListener{
        void onClick(int itemIndex);
    }

    public RecipeAdapter(int items, JSONArray recipeArray){
        numberItems = items;
        resultsArray = recipeArray;
    }

    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForRecipe = R.layout.recipe_layout_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForRecipe, parent, false);

        return new RecipeViewHolder(view);
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {

        final TextView nameTv;

        @Override
        public void onClick(View view) {

        }

        RecipeViewHolder(View recipeView) {
            super(recipeView);
            nameTv = recipeView.findViewById(R.id.recipe_name_tv);
        }

        void bind(int position) {
            try {
                JSONObject recipe = resultsArray.getJSONObject(position);
                String name = recipe.getString("name");
                nameTv.setText(name);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBindViewHolder(RecipeViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return resultsArray.length();
    }
}
