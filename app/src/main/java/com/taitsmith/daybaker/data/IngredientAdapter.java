package com.taitsmith.daybaker.data;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.taitsmith.daybaker.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * For displaying list of ingredients.
 */

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder> {
    private int numberItems;
    private JsonArray ingredientList;


    public IngredientAdapter(int items, JsonArray array){
        numberItems = items;
        ingredientList = array;
    }

    @Override
    public IngredientAdapter.IngredientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForIngredient = R.layout.ingredient_layout_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForIngredient, parent, false);

        return new IngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(IngredientViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return numberItems;
    }

    public class IngredientViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.list_ingredient_name_tv)
        TextView ingredientName;
        @BindView(R.id.list_ingredient_measurement_tv)
        TextView ingredientMeasure;

        IngredientViewHolder(View ingredientView){
            super(ingredientView);
            ButterKnife.bind(this, ingredientView);
        }

        void bind(int position){
            JsonElement element = ingredientList.get(position);
            JsonObject object = element.getAsJsonObject();
            JsonElement element1 = object.get("nameValuePairs");
            JsonObject array = element1.getAsJsonObject();
            String ingredient = array.get("ingredient").getAsString();
            String measure = array.get("measure").getAsString();
            String amount = array.get("quantity").getAsString();
            String measuredAmount;


            //because 5 UNIT looks weird.
            if (measure.equals("UNIT")) {
                measuredAmount = amount;
            } else {
                measuredAmount = amount.concat(measure);
            }
            ingredientName.setText(ingredient);
            ingredientMeasure.setText(measuredAmount);
        }
    }
}
