package com.taitsmith.daybaker.data;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.taitsmith.daybaker.R;

import io.realm.RealmResults;

/**
 * Your standard recycler view adapter to show the list of recipes.
 */

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    private int numberItems;
    private RealmResults<Recipe> recipes;
    final private ListItemClickListener listener;

    public interface ListItemClickListener{
        void onListItemClick(int itemIndex);
    }

    public RecipeAdapter(int items, RealmResults<Recipe> realmResults, ListItemClickListener listener){
        numberItems = items;
        recipes = realmResults;
        this.listener = listener;
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
        final TextView servingsTv;

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            listener.onListItemClick(clickedPosition);
        }

        RecipeViewHolder(View recipeView) {
            super(recipeView);
            nameTv = recipeView.findViewById(R.id.recipe_name_tv);
            servingsTv = recipeView.findViewById(R.id.recipe_servings_tv);
            itemView.setOnClickListener(this);
        }

        void bind(int position) {
            Recipe recipe = recipes.get(position);

            int servings = recipe.getServings();
            String servingString = itemView.getResources().getString(R.string.serves, servings);

            nameTv.setText(recipe.getName());
            servingsTv.setText(servingString);
        }
    }

    @Override
    public void onBindViewHolder(RecipeViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return numberItems;
    }
}
