package com.taitsmith.daybaker.data;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.taitsmith.daybaker.R;

import java.util.List;

/**
 * Widget to display and shuffle through recipe ingredients.
 */
public class IngredientWidget extends AppWidgetProvider {


    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, List<String> widgetText) {
        StringBuilder sb = new StringBuilder();

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ingredient_widget);
        for (String s : widgetText) {
            sb.append(s);
        }

        String text = sb.toString();
        views.setTextViewText(R.id.ingredient_widget_text, text);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
    }

    @Override
    public void onEnabled(Context context) {
        Toast.makeText(context, "Select a recpie to view ingredients", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    public static void updateWidgetText(Context context, AppWidgetManager appWidgetManager,
                                 int[] appWidgetIds, List<String> ingredientList) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, ingredientList);
        }
    }
}

