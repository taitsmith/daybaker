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
 * It's a recycler, but it shows you steps. Steps can be selected and expanded to show a
 * video (if it exists) and more detail on each step.
 */

public class StepAdapter extends RecyclerView.Adapter<StepAdapter.StepHolder> {
    private final int numberItems;
    private JsonArray stepArray;
    final private ListItemClickListener listener;


    public StepAdapter(int items, JsonArray steps, ListItemClickListener listener){
        numberItems = items;
        stepArray = steps;
        this.listener = listener;
    }

    //so they can click on a step for more detail.
    public interface ListItemClickListener{
        void onListItemClick(int itemIndex, JsonObject step);
    }

    @Override
    public int getItemCount() {
        return numberItems;
    }

    @Override
    public StepHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForStep = R.layout.step_summary_layout_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForStep, parent, false);

        return  new StepHolder(view);
    }

    @Override
    public void onBindViewHolder(StepAdapter.StepHolder holder, int position) {
        holder.bind(position);
    }

    class StepHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.stepSummaryTv)
                TextView summaryTv;
        private JsonObject stepObject;
        private String step;

        StepHolder(View view){
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        void bind(int position) {
            JsonElement element = stepArray.get(position);
            JsonObject object = element.getAsJsonObject();
            JsonElement element1 = object.get("nameValuePairs");
            stepObject = element1.getAsJsonObject();
            step = stepObject.get("shortDescription").getAsString();

            summaryTv.setText(step);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            listener.onListItemClick(clickedPosition, stepObject);
        }
    }
}
