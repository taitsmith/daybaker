package com.taitsmith.daybaker.data;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.taitsmith.daybaker.R;
import com.taitsmith.daybaker.activities.StepSummaryActivity;

/**
 * Implementation of App Widget functionality.
 */
public class StepWidget extends AppWidgetProvider {
    public static String step;
    public static String videoUrl;
    public static String stepDescription;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, String stepString) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.step_widget);
        views.setTextViewText(R.id.step_text, stepString);

        Intent intent = new Intent(context, StepSummaryActivity.class);
        intent.putExtra("DESCRIPTION", stepDescription);
        intent.putExtra("VIDEO_URL", videoUrl);
        intent.putExtra("RECIPE_NAME", step);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.step_text, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them

    }

    @Override
    public void onEnabled(Context context) {
        Toast.makeText(context, context.getString(R.string.widget_toast_hello), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    public static void updateWidgetText(Context context, AppWidgetManager manager, int[] appWidgetIds,
                                        String step) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, manager, appWidgetId, step);
        }
    }
}

