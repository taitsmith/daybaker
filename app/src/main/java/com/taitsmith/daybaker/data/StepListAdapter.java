package com.taitsmith.daybaker.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;
import com.taitsmith.daybaker.R;


/**
 * Step list for the steplist fragment.
 */

public class StepListAdapter extends BaseAdapter {
    private Context context;
    private JsonArray stepArray;
    private LayoutInflater inflater;


    public StepListAdapter(Context context, JsonArray steps){
        this.context = context;
        stepArray = steps;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return stepArray.size();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        String imageUrl;
        if (view == null) {
            view = inflater.inflate(R.layout.step_list_item, null);
            holder = new ViewHolder();

            holder.description = view.findViewById(R.id.step_list_item_text);
            holder.thumbnail = view.findViewById(R.id.step_list_item_thumbnail);
            view.setTag(holder);
        } else {
            holder = (ViewHolder)view.getTag();
        }

        JsonObject object = stepArray.get(i).getAsJsonObject();

        holder.description.setText(HelpfulUtils.removeQuotes(object.get("shortDescription")
                .getAsString()));
        try {
            if (!object.get("imageURL").isJsonNull()) {
                imageUrl = object.get("imageURL").getAsString();
                Picasso.with(context).load(imageUrl).into(holder.thumbnail);
                holder.thumbnail.setVisibility(View.VISIBLE);
            }
        } catch (NullPointerException e) {
            holder.thumbnail.setVisibility(View.GONE);
        }

        return view;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public static class ViewHolder {
        TextView description;
        ImageView thumbnail;
    }
}
