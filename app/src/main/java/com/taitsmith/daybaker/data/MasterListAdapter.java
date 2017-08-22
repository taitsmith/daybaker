package com.taitsmith.daybaker.data;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.taitsmith.daybaker.R;


/**
 * Step list beep boop
 */

public class MasterListAdapter extends BaseAdapter {
    private Context context;
    private JsonArray stepArray;


    public MasterListAdapter(Context context, JsonArray steps){
        this.context = context;
        stepArray = steps;
    }

    @Override
    public int getCount() {
        return stepArray.size();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        TextView textView;
        if (view == null) {
            textView = new TextView(context);
            textView.setPadding(8, 16, 8, 16);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            textView.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryDark));
        } else {
            textView = (TextView) view;
        }

        JsonObject object = stepArray.get(i).getAsJsonObject();
        object = object.get("nameValuePairs").getAsJsonObject();
        String step = object.get("shortDescription").getAsString();

        textView.setText(step);

        return textView;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


}