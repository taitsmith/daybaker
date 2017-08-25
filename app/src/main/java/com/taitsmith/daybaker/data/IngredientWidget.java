package com.taitsmith.daybaker.data;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.taitsmith.daybaker.R;

import static com.taitsmith.daybaker.data.IngredientWidgetService.ACTION_UPDATE_NEXT_INGREDIENT;
import static com.taitsmith.daybaker.data.IngredientWidgetService.ACTION_UPDATE_PREVIOUS_INGREDIENT;

/**
 * Widget to display and shuffle through recipe ingredients.
 */
public class IngredientWidget extends AppWidgetProvider {


    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, String widgetText) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ingredient_widget);
        views.setTextViewText(R.id.ingredient_widget_text, widgetText);

        //for the next button
        Intent nextIntent = new Intent(context, IngredientWidgetService.class);
        nextIntent.setAction(ACTION_UPDATE_NEXT_INGREDIENT);
        PendingIntent nextPendingIntent = PendingIntent.getService(context, 0, nextIntent, 0);
        views.setOnClickPendingIntent(R.id.ingredient_widget_next_button, nextPendingIntent);

        //for the previous button
        Intent previousIntent = new Intent(context, IngredientWidgetService.class);
        previousIntent.setAction(ACTION_UPDATE_PREVIOUS_INGREDIENT);
        PendingIntent previousPendingIntent = PendingIntent.getService(context, 0, previousIntent, 0);
        views.setOnClickPendingIntent(R.id.ingredient_widget_previous_button, previousPendingIntent);
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
                                 int[] appWidgetIds, String currentStep) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, currentStep);
        }
    }
}

